package com.ldapauth.synchronizer.ldap.push;

import com.ldapauth.persistence.service.OrganizationService;
import com.ldapauth.pojo.entity.Organization;
import com.ldapauth.pojo.entity.Synchronizers;
import com.ldapauth.provision.abstracts.AbstractBaseHandle;
import com.ldapauth.provision.interfaces.BaseHandle;
import com.ldapauth.util.LdapUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.*;
import java.util.HashMap;
import java.util.Objects;

@Slf4j
@Service
public class LdapPushOrgsService extends AbstractBaseHandle implements BaseHandle {

    @Autowired
    OrganizationService organizationService;

    @Override
    public boolean add(Synchronizers synchronizer, Object data) {
        log.info("orgs add:{}",data);
        DirContext dirContext = getDirContext(synchronizer);
        try {
            Organization organization = (Organization) data;
            if (Objects.nonNull(dirContext)) {
                String rdn = "";
                //如果是根，推送也是根目录
                if (Objects.isNull(organization) ) {
                    rdn = synchronizer.getBaseDn();
                } else if (Objects.isNull(organization.getParentId())) {
                    rdn = synchronizer.getBaseDn();
                } else if (organization.getParentId().intValue() == 0) {
                    rdn = synchronizer.getBaseDn();
                } else {
                    Organization parent = organizationService.getById(organization.getParentId());
                    if (Objects.nonNull(parent) && StringUtils.isNotEmpty(parent.getLdapDn())) {
                        rdn = parent.getLdapDn();
                    } else {
                        rdn = synchronizer.getBaseDn();
                    }
                }

                Attributes attributes = new BasicAttributes();
                Attribute objectClassAttribute = new BasicAttribute("objectClass");
                objectClassAttribute.add("top");
                objectClassAttribute.add("organizationalUnit");
                attributes.put(objectClassAttribute);

                attributes.put(new BasicAttribute("ou",organization.getOrgName()));
                String dn="ou="+organization.getOrgName()+","+rdn;
                dirContext.createSubcontext(dn, attributes);
                //反向查entryUUID
                //查询所有属性
                String[] attrts = new String[]{"*","+"};
                SearchControls constraints = new SearchControls();
                constraints.setSearchScope(1);
                constraints.setReturningAttributes(attrts);
                String entryUUID = null;
                NamingEnumeration<SearchResult> results = dirContext.search(rdn, "(&(objectClass=organizationalUnit)(ou="+organization.getOrgName()+"))", constraints);
                while (null != results && results.hasMoreElements()) {
                    Object obj = results.nextElement();
                    if (obj instanceof SearchResult) {
                        SearchResult sr = (SearchResult) obj;
                        if (StringUtils.isEmpty(sr.getName())) {
                            log.info("Skip '' or 'OU=Domain Controllers' .");
                            continue;
                        }
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
                        entryUUID = LdapUtils.getAttributeStringValue("entryUUID", attributeMap);
                    }
                }

                //成功后修改组织ldapDn
                organizationService.updateLdapDnAndLdapIdById(organization.getId(),entryUUID,dn);
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
        log.info("orgs update:{}",data);
        DirContext dirContext = getDirContext(synchronizer);
        try {
            Organization organization = (Organization) data;
            if (Objects.nonNull(dirContext)) {
                String rdn = "";
                //如果是根，推送也是根目录
                if (Objects.isNull(organization) ) {
                    rdn = synchronizer.getBaseDn();
                } else if (Objects.isNull(organization.getParentId())) {
                    rdn = synchronizer.getBaseDn();
                } else if (organization.getParentId().intValue() == 0) {
                    rdn = synchronizer.getBaseDn();
                } else {
                    Organization parent = organizationService.getById(organization.getParentId());
                    if (Objects.nonNull(parent) && StringUtils.isNotEmpty(parent.getLdapDn())) {
                        rdn = parent.getLdapDn();
                    } else {
                        rdn = synchronizer.getBaseDn();
                    }
                }
                String dn = "ou="+organization.getOrgName()+","+rdn;
                //如果两个一致，则不需要更新
                if (dn.equalsIgnoreCase(organization.getLdapDn())) {
                    return false;
                }
                dirContext.rename(organization.getLdapDn(), dn);
                ModificationItem[] modificationItems = new ModificationItem[1];
                modificationItems[0]=new ModificationItem(DirContext.REPLACE_ATTRIBUTE,new BasicAttribute("ou",organization.getOrgName()));
                dirContext.modifyAttributes(dn, modificationItems);
                //成功后修改组织ldapDn
                organizationService.updateLdapDnAndLdapIdById(organization.getId(),null,dn);
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
        log.info("orgs delete:{}",data);
        DirContext dirContext = getDirContext(synchronizer);
        try {
            Organization organization = (Organization) data;
            if (Objects.nonNull(dirContext)) {
                if(StringUtils.isNotEmpty(organization.getLdapDn())) {
                    dirContext.destroySubcontext(organization.getLdapDn());
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
