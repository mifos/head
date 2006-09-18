package org.mifos.application.rolesandpermission.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class RolesPermissionsPersistence extends Persistence {

	public RoleBO getRole(String roleName) throws PersistenceException{
		Map<String,Object> queryParameters=new HashMap<String,Object>();
		queryParameters.put("RoleName",roleName);
		return (RoleBO)execUniqueResultNamedQuery(NamedQueryConstants.GET_ROLE_FOR_GIVEN_NAME,queryParameters);
	}
	
	public List<ActivityEntity> getActivities() throws PersistenceException{
		return (List<ActivityEntity>)executeNamedQuery(NamedQueryConstants.GET_ALL_ACTIVITIES,null);
	}
	
	public List<RoleBO> getRoles() throws PersistenceException{
		return (List<RoleBO>)executeNamedQuery(NamedQueryConstants.GET_ALL_ROLES,null);
	}
}
