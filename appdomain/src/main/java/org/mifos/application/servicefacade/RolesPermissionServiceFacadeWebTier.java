package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.servicefacade.UserContextFactory;
import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.dto.screen.ListElement;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.security.MifosUser;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.security.rolesandpermission.exceptions.RolesPermissionException;
import org.mifos.security.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.security.rolesandpermission.util.helpers.RolesAndPermissionConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

public class RolesPermissionServiceFacadeWebTier implements RolesPermissionServiceFacade {

    private static final Logger logger = LoggerFactory.getLogger(RoleBO.class);

    @Override
    public List<ListElement> retrieveAllRoles() {
        try {
            List<RoleBO> roles = new RolesPermissionsBusinessService().getRoles();
            if(!roles.isEmpty()) {
                List<ListElement> roleList = new ArrayList<ListElement>();

                for (RoleBO role: roles) {
                    ListElement element = new ListElement(new Integer(role.getId()), role.getName());
                    roleList.add(element);
                }
                return roleList;
            }
        } catch (ServiceException e) {
            throw new MifosRuntimeException(e);
        }
        return null;
    }

    @Override
    public void createRole(Short userId, String name, List<Short> ActivityIds) throws RolesPermissionException {
        MifosUser user = (MifosUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        UserContext userContext = new UserContextFactory().create(user);
        List<ActivityEntity> activityEntities = getActivityEntities(ActivityIds);

        RoleBO roleBO = new RoleBO(userContext, name, activityEntities);

        RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();

        roleBO.validateRoleName(name);

        try {
            if (roleBO.getName() == null || !roleBO.getName().trim().equalsIgnoreCase(name.trim())) {
                if (rolesPermissionsPersistence.getRole(name.trim()) != null) {
                    throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLEALREADYEXIST);
                }
            }

            StaticHibernateUtil.startTransaction();
            rolesPermissionsPersistence.save(roleBO);
            StaticHibernateUtil.commitTransaction();
            AuthorizationManager.getInstance().addRole(roleBO);
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    @Override
    public void updateRole(Short roleId, Short userId, String name, List<Short> ActivityIds) throws Exception {
        RolesPermissionsBusinessService rolesPermissionsBusinessService = new RolesPermissionsBusinessService();
        RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
        RoleBO role = rolesPermissionsBusinessService.getRole(roleId);

        if (role.getName() == null || !role.getName().trim().equalsIgnoreCase(name.trim())) {
            if (rolesPermissionsPersistence.getRole(name.trim()) != null) {
                throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLEALREADYEXIST);
            }
        }

        try {
            StaticHibernateUtil.startTransaction();
            role.update(userId, name, getActivityEntities(ActivityIds));
            rolesPermissionsPersistence.save(role);
            StaticHibernateUtil.commitTransaction();
            AuthorizationManager.getInstance().updateRole(role);
        } catch (RolesPermissionException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new BusinessRuleException(e.getKey(), e);
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private List<ActivityEntity> getActivityEntities(List<Short> ActivityIds) {
        List<ActivityEntity> activityEntities = new ArrayList<ActivityEntity>();
        RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();
        for (Short id: ActivityIds) {
            try {
                ActivityEntity activityEntity = rolesPermissionsPersistence.getActivityById(id);
                activityEntities.add(activityEntity);
            } catch (PersistenceException e) {
                throw new MifosRuntimeException(e);
            }
        }
        return activityEntities;
    }

    @Override
    public void deleteRole(Integer versionNo, Short roleId) throws Exception {
        RolesPermissionsBusinessService rolesPermissionsBusinessService = new RolesPermissionsBusinessService();
        RolesPermissionsPersistence rolesPermissionsPersistence = new RolesPermissionsPersistence();

        RoleBO role = rolesPermissionsBusinessService.getRole(roleId);
        role.setVersionNo(versionNo);

        validateIfRoleAssignedToPersonnel(role);

        try {
            StaticHibernateUtil.startTransaction();

            rolesPermissionsPersistence.delete(role);
            StaticHibernateUtil.commitTransaction();
            AuthorizationManager.getInstance().deleteRole(role);
        } catch (PersistenceException e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    private void validateIfRoleAssignedToPersonnel(RoleBO role) throws RolesPermissionException {
        logger.info("Validating if role is assigned to personnel");
        if (isRoleAssignedToPersonnel(role)) {
            throw new RolesPermissionException(RolesAndPermissionConstants.KEYROLEASSIGNEDTOPERSONNEL);
        }
        logger.info("Validation done for role assigned to personnel");
    }

    private boolean isRoleAssignedToPersonnel(RoleBO role) throws RolesPermissionException {
        Integer count;
        try {
            count = new PersonnelPersistence().getPersonnelRoleCount(role.getId());
        } catch (PersistenceException e) {
            throw new RolesPermissionException(e);
        }
        return (null != count && count > 0) ? true : false;
    }

}
