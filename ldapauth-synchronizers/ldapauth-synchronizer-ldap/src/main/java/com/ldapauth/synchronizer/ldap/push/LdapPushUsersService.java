package com.ldapauth.synchronizer.ldap.push;

import cn.hutool.core.collection.CollectionUtil;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.constants.ldap.InetOrgPerson;
import com.ldapauth.crypto.password.PasswordReciprocal;
import com.ldapauth.persistence.service.OrganizationService;
import com.ldapauth.persistence.service.UserInfoService;
import com.ldapauth.pojo.entity.Organization;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.synchronizer.abstracts.AbstractPushSynchronizer;
import com.ldapauth.synchronizer.ISynchronizerPushService;
import com.ldapauth.util.LdapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;

@Slf4j
@Service
public class LdapPushUsersService extends AbstractPushSynchronizer implements ISynchronizerPushService {


    @Autowired
    OrganizationService organizationService;

    @Autowired
    UserInfoService userInfoService;


    @Override
    public boolean add(Synchronizers synchronizer, Object data) {
        log.info("user insert:{}",data);
        DirContext dirContext = getDirContext(synchronizer);
        try {
            if (Objects.nonNull(dirContext)) {
                UserInfo userInfo = (UserInfo) data;
                String peopleDn = synchronizer.getUserDn();
                // 创建组条目的DN
                String userDn = getLdapUserDN(userInfo.getObjectFrom(),"UID",userInfo.getUsername(),synchronizer);
                String userPassword = decoderPassword(userInfo.getDecipherable());
                // 创建用户的Attributes
                Attributes attributes = new BasicAttributes();
                Attribute  objectClassAttribute = new BasicAttribute("objectClass");
                objectClassAttribute.add("top");
                objectClassAttribute.add("person");
                objectClassAttribute.add("inetOrgPerson");
                attributes.put(objectClassAttribute);

                String displayName = userInfo.getDisplayName();
                String nickName = userInfo.getNickName();
                String mobile = userInfo.getMobile();
                String email = userInfo.getEmail();
                if(StringUtils.isNotBlank(displayName)) {
                    attributes.put(new BasicAttribute(InetOrgPerson.CN,userInfo.getDisplayName()));
                    attributes.put(new BasicAttribute(InetOrgPerson.SN,userInfo.getDisplayName()));
                    attributes.put(new BasicAttribute(InetOrgPerson.DISPLAYNAME,userInfo.getDisplayName()));
                }
                if(StringUtils.isNotBlank(nickName)) {
                    attributes.put(new BasicAttribute(InetOrgPerson.INITIALS,userInfo.getNickName()));
                }

                if(StringUtils.isNotBlank(mobile)) {
                    attributes.put(new BasicAttribute(InetOrgPerson.MOBILE,userInfo.getMobile()));
                }
                if(StringUtils.isNotBlank(email)) {
                    attributes.put(new BasicAttribute(InetOrgPerson.MAIL,userInfo.getEmail()));
                }
                attributes.put(new BasicAttribute(InetOrgPerson.UID,userInfo.getUsername()));
                attributes.put(new BasicAttribute(InetOrgPerson.USERPASSWORD,userPassword));
                if(Objects.nonNull(userInfo.getDepartmentId())) {
                    Organization organization = organizationService.getById(userInfo.getDepartmentId());
                    if(Objects.nonNull(organization)) {
                        if(StringUtils.isNotEmpty(organization.getLdapId())) {
                            attributes.put(new BasicAttribute(InetOrgPerson.DEPARTMENTNUMBER,organization.getLdapId()));
                        } else {
                            attributes.put(new BasicAttribute(InetOrgPerson.DEPARTMENTNUMBER,String.valueOf(organization.getId())));
                        }
                    }
                }
                // 添加用户
                dirContext.createSubcontext(userDn, attributes);
                //反向查entryUUID
                //查询所有属性
                String[] attrts = new String[]{"*","+"};
                SearchControls constraints = new SearchControls();
                constraints.setSearchScope(1);
                constraints.setReturningAttributes(attrts);
                String entryUUID = null;
                NamingEnumeration<SearchResult> results = dirContext.search(peopleDn, "(&(objectClass=inetOrgPerson)(uid="+userInfo.getUsername()+"))", constraints);
                while (null != results && results.hasMoreElements()) {
                    Object obj = results.nextElement();
                    if (obj instanceof SearchResult) {
                        SearchResult sr = (SearchResult) obj;
                        if (StringUtils.isEmpty(sr.getName())) {
                            log.info("Skip '' or 'OU=Domain Controllers' .");
                            continue;
                        }
                        HashMap<String, Attribute> attributeMap = new HashMap<>();
                        NamingEnumeration<? extends Attribute> attrss = sr.getAttributes().getAll();
                        while (null != attrss && attrss.hasMoreElements()) {
                            Attribute objAttrs = attrss.nextElement();
                            log.trace("attribute {} : {}",
                                    objAttrs.getID(),
                                    LdapUtils.getAttrStringValue(objAttrs)
                            );
                            attributeMap.put(objAttrs.getID().toLowerCase(), objAttrs);
                        }
                        entryUUID = LdapUtils.getAttributeStringValue("entryUUID", attributeMap);
                    }
                }
                //成功后修改组织ldapDn
                userInfoService.updateLdapDnAndLdapIdById(userInfo.getId(),entryUUID,userDn);
            }
        }catch (Exception e){
            log.error("error",e);
        } finally {
            if (Objects.nonNull(dirContext)) {
                try {
                    dirContext.close();
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }

    @Override
    public boolean update(Synchronizers synchronizer, Object data) {
        log.info("user update:{}",data);
        DirContext dirContext = getDirContext(synchronizer);
        try {
            if (Objects.nonNull(dirContext)) {
                UserInfo userInfo = (UserInfo) data;
                String peopleDn = "ou=people,"+synchronizer.getBaseDn();
                // 创建组条目的DN
                String userDn = getLdapUserDN(userInfo.getObjectFrom(),"UID",userInfo.getUsername(),synchronizer);
                //如果两个一致，则不需要更新
                if (!userDn.equalsIgnoreCase(userInfo.getLdapDn())) {
                    dirContext.rename(userInfo.getLdapDn(),userDn);
                    //成功后修改组织ldapDn
                    userInfoService.updateLdapDnAndLdapIdById(userInfo.getId(),null,userDn);
                }
                List<ModificationItem> modificationItems = new ArrayList<>();
                String displayName = userInfo.getDisplayName();
                String nickName = userInfo.getNickName();
                String mobile = userInfo.getMobile();
                String email = userInfo.getEmail();
                String deptmentId = "";
                if(StringUtils.isNotBlank(displayName)) {
                    modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(InetOrgPerson.CN,displayName)));
                    modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(InetOrgPerson.DISPLAYNAME,displayName)));
                    modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(InetOrgPerson.SN,displayName)));
                }
                if(StringUtils.isNotBlank(nickName)) {
                    modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(InetOrgPerson.INITIALS,nickName)));
                }
                if(StringUtils.isNotBlank(mobile)) {
                    modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(InetOrgPerson.MOBILE,mobile)));
                }
                if(StringUtils.isNotBlank(email)) {
                    modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute(InetOrgPerson.MAIL,email)));
                }
                if(Objects.nonNull(userInfo.getDepartmentId())) {
                    Organization organization = organizationService.getById(userInfo.getDepartmentId());
                    if(Objects.nonNull(organization)) {
                        if(StringUtils.isNotEmpty(organization.getLdapId())) {
                            modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(InetOrgPerson.DEPARTMENTNUMBER, organization.getLdapId())));
                        } else {
                            modificationItems.add(new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(InetOrgPerson.DEPARTMENTNUMBER,String.valueOf(organization.getId()))));
                        }
                    }
                }

                if (CollectionUtil.isNotEmpty(modificationItems)) {
                    ModificationItem[] up = modificationItems.toArray(new ModificationItem[modificationItems.size()]);
                    dirContext.modifyAttributes(userDn, up);
                }
            }
        }catch (Exception e){
            log.error("error",e);
        } finally {
            if (Objects.nonNull(dirContext)) {
                try {
                    dirContext.close();
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }

    @Override
    public boolean delete(Synchronizers synchronizer, Object data) {
        log.info("user delete:{}",data);
        DirContext dirContext = getDirContext(synchronizer);
        try {
            if (Objects.nonNull(dirContext)) {
                UserInfo userInfo = (UserInfo) data;
                if (Objects.nonNull(dirContext)) {
                    if(StringUtils.isNotEmpty(userInfo.getLdapDn())) {
                        dirContext.destroySubcontext(userInfo.getLdapDn());
                    }
                }
            }
        }catch (Exception e){
            log.error("error",e);
        } finally {
            if (Objects.nonNull(dirContext)) {
                try {
                    dirContext.close();
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }


    @Override
    public boolean password(Synchronizers synchronizer, Object data) {
        log.info("password:{}",data);
        DirContext dirContext = getDirContext(synchronizer);
        try {
            if (Objects.nonNull(dirContext)) {
                UserInfo userInfo = (UserInfo) data;
                //如果LDAPDN不为空，和来源不是系统，则进行删除
                if (Objects.nonNull(userInfo.getLdapDn()) && !userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
                    // 创建组条目的DN
                    String userDn = userInfo.getLdapDn();
                    // 解密密码
                    String userPassword = decoderPassword(userInfo.getDecipherable());
                    ModificationItem[] modificationItems = new ModificationItem[1];
                    modificationItems[0] = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, new BasicAttribute(InetOrgPerson.USERPASSWORD, userPassword));
                    dirContext.modifyAttributes(userDn, modificationItems);
                }
            }
        }catch (Exception e){
            log.error("error",e);
        } finally {
            if (Objects.nonNull(dirContext)) {
                try {
                    dirContext.close();
                } catch (NamingException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return false;
    }


    public static void main(String[] args) {
        String ldapUserDN = "ldapauth\\administrator";
        String ldapPassword = "Shibl@123456";
        String baseDn = "ldaps://192.168.0.104";
        // 初始化上下文环境
        String trustStore= "/app/ldapauth/trustStoreFile.jks";
        String trustStorePassword = "ldapauth";
        DirContext ctx = null;
        try {
            LdapUtils utils = new LdapUtils(
                    baseDn,
                    ldapUserDN,
                    ldapPassword,
                    "OU=软件安全科技有限公司,DC=ldapauth,DC=com");
            utils.setSsl(true);
            utils.setTrustStore(trustStore);
            utils.setTrustStorePassword(trustStorePassword);
            ctx = utils.openConnection();
            if(null != ctx) {
                System.out.println("链接成功");
            } else {
                System.err.println("链接失败");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void initData() {
        // OpenLDAP服务器信息
        String ldapHost = "192.168.31.222";
        int ldapPort = 389;
        String ldapUserDN = "cn=Manager,dc=ldapauth,dc=com";
        String ldapPassword = "X9hht7Kv";
        // 新用户信息
        String newUserDN = "uid=test,ou=people,dc=ldapauth,dc=com";
        String newUserPassword = "password123";
        String baseDn = "dc=ldapauth,dc=com";
        // 初始化上下文环境
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + ldapHost + ":" + ldapPort);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUserDN);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);

        try {
            // 创建初始上下文
            DirContext ctx = new InitialDirContext(env);

            Attributes attributes = new BasicAttributes();
            Attribute objectClassAttribute = new BasicAttribute("objectClass");
            objectClassAttribute.add("top");
            objectClassAttribute.add("organizationalUnit");

            attributes.put(objectClassAttribute);
            attributes.put(new BasicAttribute("ou","groups"));

            String groups="ou=groups,"+baseDn;
            ctx.createSubcontext(groups, attributes);


            attributes = new BasicAttributes();
            attributes.put(objectClassAttribute);
            attributes.put(new BasicAttribute("ou","people"));

            String people="ou=people,"+baseDn;
            ctx.createSubcontext(people, attributes);


            // 创建新用户的属性
            BasicAttributes entry = new BasicAttributes();
            objectClassAttribute = new BasicAttribute("objectClass");
            objectClassAttribute.add("top");
            objectClassAttribute.add("person");
            objectClassAttribute.add("inetOrgPerson");
            entry.put(objectClassAttribute);
            entry.put(new BasicAttribute("uid", "test"));
            entry.put(new BasicAttribute("cn", "UserOne"));
            entry.put(new BasicAttribute("sn", "One"));
            entry.put(new BasicAttribute("userPassword", newUserPassword));

            // 添加新用户
            ctx.createSubcontext(newUserDN, entry);

            System.out.println("User added successfully.");

            // 关闭上下文
            ctx.close();
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    public static void clean() {
        // OpenLDAP服务器信息
        String ldapHost = "192.168.31.222";
        int ldapPort = 389;
        String ldapUserDN = "cn=Manager,dc=ldapauth,dc=com";
        String ldapPassword = "X9hht7Kv";

        String baseDn = "dc=ldapauth,dc=com";
        // 初始化上下文环境
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + ldapHost + ":" + ldapPort);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUserDN);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        DirContext ctx = null;
        try {
            // 创建初始上下文
            ctx = new InitialDirContext(env);
            //查询所有属性
            String[] attrts = new String[]{"*","+"};
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(2);
            constraints.setReturningAttributes(attrts);
            NamingEnumeration<SearchResult> results = ctx.search(baseDn, "(&(objectClass=organizationalUnit))", constraints);
            List<String> deleteDn = new ArrayList<>();
            while (null != results && results.hasMoreElements()) {
                Object obj = results.nextElement();
                SearchResult sr = (SearchResult) obj;
                if (StringUtils.isEmpty(sr.getName())) {
                    log.info("Skip '' or 'OU=Domain Controllers' .");
                    continue;
                }

                String dn = sr.getNameInNamespace();

                if(dn.endsWith("ou=江苏麦克思软件有限公司,dc=ldapauth,dc=com")) {
                    continue;
                }
                if(dn.endsWith("ou=people,dc=ldapauth,dc=com")) {
                    continue;
                }
                if(dn.endsWith("ou=groups,dc=ldapauth,dc=com")) {
                    continue;
                }
                deleteDn.add(dn);
                System.out.println("dn:"+dn);
            }

            for(int i=deleteDn.size()-1;i>=0;i--) {
                ctx.destroySubcontext(deleteDn.get(i));
                System.out.println("delete:"+deleteDn.get(i)+" ，success");
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != ctx) {
                    ctx.close();
                }
            }catch (NamingException e){

            }
        }

    }

    public static void cleanUser() {
        // OpenLDAP服务器信息
        String ldapHost = "192.168.31.222";
        int ldapPort = 389;
        String ldapUserDN = "cn=Manager,dc=ldapauth,dc=com";
        String ldapPassword = "X9hht7Kv";

        String baseDn = "dc=ldapauth,dc=com";
        // 初始化上下文环境
        Hashtable<String, String> env = new Hashtable<>();
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://" + ldapHost + ":" + ldapPort);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL, ldapUserDN);
        env.put(Context.SECURITY_CREDENTIALS, ldapPassword);
        DirContext ctx = null;
        try {
            // 创建初始上下文
            ctx = new InitialDirContext(env);
            //查询所有属性
            String[] attrts = new String[]{"*","+"};
            SearchControls constraints = new SearchControls();
            constraints.setSearchScope(2);
            constraints.setReturningAttributes(attrts);
            NamingEnumeration<SearchResult> results = ctx.search(baseDn, "(&(objectClass=inetOrgPerson))", constraints);
            List<String> deleteDn = new ArrayList<>();
            while (null != results && results.hasMoreElements()) {
                Object obj = results.nextElement();
                SearchResult sr = (SearchResult) obj;
                if (StringUtils.isEmpty(sr.getName())) {
                    log.info("Skip '' or 'OU=Domain Controllers' .");
                    continue;
                }
                String dn = sr.getNameInNamespace();
                if(dn.endsWith("uid=dummy,ou=people,dc=ldapauth,dc=com")) {
                    continue;
                }
                deleteDn.add(dn);
                System.out.println("dn:"+dn);
            }

            for(int i=deleteDn.size()-1;i>=0;i--) {
                ctx.destroySubcontext(deleteDn.get(i));
                System.out.println("delete:"+deleteDn.get(i)+" ，success");
            }
        } catch (NamingException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != ctx) {
                    ctx.close();
                }
            }catch (NamingException e){

            }
        }

    }
    /**
     * 密码解密
     * @param decoderPassword
     */
    private String decoderPassword(String decoderPassword){
        try {
            if (StringUtils.isNotBlank(decoderPassword)) {
                return PasswordReciprocal.getInstance().decoder(decoderPassword);
            }
        }catch (Exception e){
            log.error("error");
        }
        return decoderPassword;
    }
}
