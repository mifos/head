package org.mifos.application.servicefacade;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;
import org.mifos.core.MifosRuntimeException;
import org.mifos.dto.screen.ListElement;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.authorization.AuthorizationManager;
import org.mifos.security.rolesandpermission.business.ActivityEntity;
import org.mifos.security.rolesandpermission.business.RoleBO;
import org.mifos.security.rolesandpermission.business.service.RolesPermissionsBusinessService;
import org.mifos.security.rolesandpermission.exceptions.RolesPermissionException;
import org.mifos.security.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.security.util.UserContext;

public class RolesPermissionServiceFacadeWebTier implements RolesPermissionServiceFacade {

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
        UserContext userContext = new UserContext();
        userContext.setId(userId);
        List<ActivityEntity> activityEntities = getActivityEntities(ActivityIds);

        RoleBO role = new RoleBO(userContext, name, activityEntities);
        role.save();
        AuthorizationManager.getInstance().addRole(role);
    }

    @Override
    public void updateRole(Short roleId, Short userId, String name, List<Short> ActivityIds) throws Exception {
        RolesPermissionsBusinessService rolesPermissionsBusinessService = new RolesPermissionsBusinessService();

        RoleBO role = rolesPermissionsBusinessService.getRole(roleId);
        role.update(userId, name, getActivityEntities(ActivityIds));
        AuthorizationManager.getInstance().updateRole(role);
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

        RoleBO role = rolesPermissionsBusinessService.getRole(roleId);
        role.setVersionNo(versionNo);
        role.delete();
        AuthorizationManager.getInstance().deleteRole(role);
    }
}
