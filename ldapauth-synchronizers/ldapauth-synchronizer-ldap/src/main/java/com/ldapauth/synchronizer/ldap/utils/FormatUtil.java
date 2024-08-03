package com.ldapauth.synchronizer.ldap.utils;

import com.ldapauth.constants.ldap.InetOrgPerson;
import com.ldapauth.constants.ldap.OrganizationalUnit;
import com.ldapauth.synchronizer.ldap.domain.LdapGroup;
import com.ldapauth.synchronizer.ldap.domain.LdapOrg;
import com.ldapauth.synchronizer.ldap.domain.LdapUser;
import com.ldapauth.util.LdapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import java.util.*;

@Slf4j
public class FormatUtil {


    /**
     * 格式化LDAP用户
     * @param attributeMap
     * @param name
     * @param nameInNamespace
     * @return
     */
    public static LdapUser formatUser(Map<String, String> entryUUIDMap,HashMap<String, Attribute> attributeMap, String name, String nameInNamespace) {
        try {
            LdapUser user = new LdapUser();
            user.setNameInNamespace(nameInNamespace);
            String cn  = LdapUtils.getAttributeStringValue(InetOrgPerson.CN,attributeMap);
            String uid = LdapUtils.getAttributeStringValue(InetOrgPerson.UID,attributeMap);
            String sn  = LdapUtils.getAttributeStringValue(InetOrgPerson.SN,attributeMap);
            String initials  = LdapUtils.getAttributeStringValue(InetOrgPerson.INITIALS,attributeMap);
            String givenName = LdapUtils.getAttributeStringValue(InetOrgPerson.GIVENNAME,attributeMap);
            String displayName = LdapUtils.getAttributeStringValue(InetOrgPerson.DISPLAYNAME,attributeMap);
            if(StringUtils.isBlank(displayName)) {
                user.setDisplayName(sn + givenName);
            }else {
                user.setDisplayName(displayName);
            }
            if(StringUtils.isBlank(initials)) {
                user.setInitials(sn + givenName);
            }else {
                user.setInitials(initials);
            }
            if(StringUtils.isBlank(uid)) {
                user.setUid(cn);
            } else {
                user.setUid(uid);
            }
            if (StringUtils.isNotEmpty(LdapUtils.getAttributeStringValue(InetOrgPerson.CN,attributeMap))) {
                user.setCn(LdapUtils.getAttributeStringValue(InetOrgPerson.CN,attributeMap));
            }
            if (StringUtils.isNotEmpty(LdapUtils.getAttributeStringValue(InetOrgPerson.SN,attributeMap))) {
                user.setSn(LdapUtils.getAttributeStringValue(InetOrgPerson.SN,attributeMap));
            }
            if (StringUtils.isNotEmpty(LdapUtils.getAttributeStringValue(InetOrgPerson.MOBILE,attributeMap))) {
                user.setMobile(LdapUtils.getAttributeStringValue(InetOrgPerson.MOBILE,attributeMap));
            }
            if (StringUtils.isNotEmpty(LdapUtils.getAttributeStringValue(InetOrgPerson.MAIL,attributeMap))) {
                user.setEmail(LdapUtils.getAttributeStringValue(InetOrgPerson.MAIL,attributeMap));
            }
            String[] namePaths = name.replaceAll(",OU=", "/").replaceAll("OU=", "/")
                    .replaceAll(",ou=", "/").replaceAll("ou=", "/")
                    .split("/");
            String entryUUID = LdapUtils.getAttributeStringValue("entryUUID", attributeMap);
            user.setEntryUUID(entryUUID);
            //根据nameInNamespace 组装parentId
            List<String> nameInNamespaceArray = Arrays.asList(StringUtils.split(nameInNamespace, ","));
            if (Objects.nonNull(nameInNamespaceArray) && nameInNamespaceArray.size()>0) {
                StringBuffer parentIdStringBuffer = new StringBuffer();
                for (int i= 1;i<nameInNamespaceArray.size();i++) {
                    if (i+1 == nameInNamespaceArray.size() ) {
                        parentIdStringBuffer.append(nameInNamespaceArray.get(i));
                    } else {
                        parentIdStringBuffer.append(nameInNamespaceArray.get(i)+",");
                    }
                }
                if (entryUUIDMap.containsKey(parentIdStringBuffer.toString())) {
                    user.setParentEntryUUID(entryUUIDMap.get(parentIdStringBuffer.toString()));
                }
                user.setParentNameInNamespace(parentIdStringBuffer.toString());
            }
            return user;
        }catch (Exception e){
            log.error("构建ldap用户对象异常");
        }
        return null;
    }


    /**
     * 格式化LDAP组织对象
     * @param attributeMap
     * @param name
     * @param nameInNamespace
     * @return
     */
    public static LdapOrg formatOrg(Map<String, String> entryUUIDMap,HashMap<String,Attribute> attributeMap, String name, String nameInNamespace) {
        LdapOrg ldapOrg = new LdapOrg();
        ldapOrg.setNameInNamespace(nameInNamespace);
        try {
            String ou = LdapUtils.getAttributeStringValue(OrganizationalUnit.OU, attributeMap);
            String entryUUID = LdapUtils.getAttributeStringValue("entryUUID", attributeMap);
            ldapOrg.setOu(ou);
            ldapOrg.setEntryUUID(entryUUID);
            String[] namePaths = name.replaceAll(",OU=", "/").replaceAll("OU=", "/")
                    .replaceAll(",ou=", "/").replaceAll("ou=", "/")
                    .split("/");

            ldapOrg.setOrder(namePaths.length);
            //根据nameInNamespace 组装parentId
            List<String> nameInNamespaceArray = Arrays.asList(StringUtils.split(nameInNamespace, ","));
            if (Objects.nonNull(nameInNamespaceArray) && nameInNamespaceArray.size() > 0) {
                StringBuffer parentIdStringBuffer = new StringBuffer();
                for (int i = 1; i < nameInNamespaceArray.size(); i++) {
                    if (i + 1 == nameInNamespaceArray.size()) {
                        parentIdStringBuffer.append(nameInNamespaceArray.get(i));
                    } else {
                        parentIdStringBuffer.append(nameInNamespaceArray.get(i) + ",");
                    }
                }
                ldapOrg.setParentNameInNamespace(parentIdStringBuffer.toString());
            }
        } catch (Exception e){

        }
        return ldapOrg;
    }



    /**
     * 格式化LDAP组
     * @param attributeMap
     * @param name
     * @param nameInNamespace
     * @return
     */
    public static LdapGroup formatGroup(HashMap<String,Attribute> attributeMap, String name, String nameInNamespace) {
        LdapGroup ldapGroup = new LdapGroup();
        ldapGroup.setNameInNamespace(nameInNamespace);
        try {
            String entryUUID = LdapUtils.getAttributeStringValue("entryUUID", attributeMap);
            String cn = LdapUtils.getAttributeStringValue(OrganizationalUnit.CN,attributeMap);
            ldapGroup.setEntryUUID(entryUUID);
            ldapGroup.setCn(cn);




        } catch (Exception e){

        }
        return ldapGroup;
    }



    /**
     * 获取当前组的成员列表
     * @param dirContext
     * @param groupDn 组基本DN
     * @param cn 组名称
     * @return
     */
    public static List<String> getMembers(DirContext dirContext, String groupDn, String cn){
        SearchControls constraints = new SearchControls();
        constraints.setSearchScope(SearchControls.SUBTREE_SCOPE);
        //查询所有属性
        String[] attrts = new String[]{"*","+"};
        String filter = "(cn="+cn+")";
        constraints.setReturningAttributes(attrts);
        List<String> members = new ArrayList<>();
        try {
            NamingEnumeration<SearchResult> results = dirContext.search(groupDn, filter, constraints);

            long recordCount = 0;
            while (null != results && results.hasMoreElements()) {
                Object obj = results.nextElement();
                if (obj instanceof SearchResult) {
                    SearchResult sr = (SearchResult) obj;
                    if (StringUtils.isEmpty(sr.getName())) {
                        log.info("Skip '' or 'OU=Domain Controllers' .");
                        continue;
                    }
                    log.debug("Sync OrganizationalUnit {} , name [{}] , NameInNamespace [{}]",
                            (++recordCount), sr.getName(), sr.getNameInNamespace());
                    HashMap<String, Attribute> attributeMap = new HashMap<>();
                    NamingEnumeration<? extends Attribute> attrs = sr.getAttributes().getAll();
                    while (null != attrs && attrs.hasMoreElements()) {
                        Attribute objAttrs = attrs.nextElement();
                        log.trace("attribute {} : {}",
                                objAttrs.getID(),
                                LdapUtils.getAttrStringValue(objAttrs)
                        );
                        attributeMap.put(objAttrs.getID().toLowerCase(), objAttrs);
                    }
                    Attribute attr = attributeMap.get("member");
                    if (Objects.nonNull(attr)) {
                        for (Enumeration vals = attr.getAll(); vals.hasMoreElements();) {
                            String uid = (String) vals.nextElement();
                            members.add(uid);
                        }
                    }

                }
            }
        } catch (Exception e){
            log.error("error");
        }
        return members;
    }


}
