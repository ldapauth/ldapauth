package com.ldapauth.synchronizer.ldap.push;

import cn.hutool.core.collection.CollectionUtil;
import com.ldapauth.constants.ConstsSynchronizers;
import com.ldapauth.persistence.service.GroupMemberService;
import com.ldapauth.persistence.service.GroupService;
import com.ldapauth.pojo.entity.Group;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.pojo.entity.UserInfo;
import com.ldapauth.provision.abstracts.AbstractBaseHandle;
import com.ldapauth.provision.interfaces.BaseHandle;
import com.ldapauth.synchronizer.ldap.utils.FormatUtil;
import com.ldapauth.util.LdapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.*;


@Slf4j
@Service
public class LdapPushGroupService extends AbstractBaseHandle implements BaseHandle {

    @Autowired
    GroupService groupService;

    @Autowired
    GroupMemberService groupMemberService;

    @Override
    public boolean add(Synchronizers synchronizer, Object data) {
        log.info("gruop insert:{}",data);
        DirContext dirContext = getDirContext(synchronizer);
        try {
            if (Objects.nonNull(dirContext)) {
                Group group = (Group) data;
                //查询分组成员列表
                List<UserInfo> userInfoList = groupMemberService.getUserListByGroupId(group.getId());

                String groupsDn = synchronizer.getGroupDn();
                String peopleDn = synchronizer.getUserDn();
                // 创建组条目的DN
                String dn = "cn="+group.getName()+","+groupsDn;
                // 设置组条目的属性
                Attributes attrs = new BasicAttributes();
                Attribute objectClassAttribute = new BasicAttribute("objectClass");
                objectClassAttribute.add("top");
                objectClassAttribute.add("groupOfNames");
                attrs.put(objectClassAttribute);

                attrs.put("cn", group.getName()); //组名
                //默认用户
                attrs.put("member", "uid=dummy," + peopleDn); // 组成员DN列表
                if (CollectionUtil.isNotEmpty(userInfoList)) {
                    for (UserInfo userInfo : userInfoList) {
                        //判断来源和DN
                        if (StringUtils.isNotEmpty(userInfo.getLdapDn()) && !userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
                            attrs.put("member", userInfo.getLdapDn());
                        }
                    }
                }
                // 创建组条目
                dirContext.createSubcontext(dn, attrs);
                //反向查entryUUID
                //查询所有属性
                String[] attrts = new String[]{"*","+"};
                SearchControls constraints = new SearchControls();
                constraints.setSearchScope(1);
                constraints.setReturningAttributes(attrts);
                String entryUUID = null;
                NamingEnumeration<SearchResult> results = dirContext.search(groupsDn, "(&(objectClass=groupOfNames)(cn="+group.getName()+"))", constraints);
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
                groupService.updateLdapDnAndLdapIdById(group.getId(),entryUUID,dn);
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
        log.info("gruop update:{}",data);
        DirContext dirContext = getDirContext(synchronizer);
        try {
            if (Objects.nonNull(dirContext)) {
                Group group = (Group) data;
                //查询分组成员列表
                List<UserInfo> userInfoList = groupMemberService.getUserListByGroupId(group.getId());

                String groupsDn = "ou=groups,"+synchronizer.getBaseDn();
                String peopleDn = "ou=people,"+synchronizer.getBaseDn();
                // 创建组条目的DN
                String dn = "cn="+group.getName()+","+groupsDn;
                //如果两个一致，则不需要更新
                if (!dn.equalsIgnoreCase(group.getLdapDn())) {
                    dirContext.rename(group.getLdapDn(), dn);
                    //成功后修改组织ldapDn
                    groupService.updateLdapDnAndLdapIdById(group.getId(),null,dn);
                }
                //获取组的历史成员集合
                List<String> oidGroupMember = FormatUtil.getMembers(dirContext,groupsDn,group.getName());
                //定义需要添加的成员集合
                List<String> newMember = new ArrayList<>();
                //构建新增成员集合
                if (CollectionUtil.isNotEmpty(userInfoList)) {
                    for (UserInfo userInfo : userInfoList) {
                        //判断来源和DN
                        if (StringUtils.isNotEmpty(userInfo.getLdapDn()) && !userInfo.getObjectFrom().equalsIgnoreCase(ConstsSynchronizers.SYSTEM)) {
                            String uid = userInfo.getLdapDn();
                            newMember.add(uid);
                        }
                    }
                }
                //删除历史成员
                if (CollectionUtil.isNotEmpty(oidGroupMember)) {
                    for (String uid : oidGroupMember) {
                        //如果包含则跳过，否则直接删除
                        if (newMember.contains(uid)) {
                            continue;
                        }
                        //忽略默认用户
                        String deft = "uid=dummy," + peopleDn;
                        if(uid.equalsIgnoreCase(deft)){
                            continue;
                        }
                        //删除后再授权
                        ModificationItem member = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
                                new BasicAttribute("member",uid));
                        dirContext.modifyAttributes(dn,new ModificationItem[] { member });
                    }
                }

                //执行新增组成员
                if (CollectionUtil.isNotEmpty(newMember)) {
                    for (String uid : newMember) {
                        //如果历史成员存在，则跳过，否则新增
                        if (oidGroupMember.contains(uid)) {
                            continue;
                        }
                        ModificationItem member = new ModificationItem(DirContext.ADD_ATTRIBUTE,
                                new BasicAttribute("member", uid));
                        dirContext.modifyAttributes(dn,new ModificationItem[] { member });
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
    public boolean delete(Synchronizers synchronizer, Object data) {
        log.info("gruop delete:{}",data);
        DirContext dirContext = getDirContext(synchronizer);
        try {
            Group group = (Group) data;
            if (Objects.nonNull(dirContext)) {
                if(StringUtils.isNotEmpty(group.getLdapDn())) {
                    dirContext.destroySubcontext(group.getLdapDn());
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
        return false;
    }
}
