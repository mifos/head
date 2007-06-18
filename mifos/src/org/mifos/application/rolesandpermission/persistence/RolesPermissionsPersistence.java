package org.mifos.application.rolesandpermission.persistence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class RolesPermissionsPersistence extends Persistence {

	public RoleBO getRole(String roleName) throws PersistenceException{
		Map<String,Object> queryParameters=new HashMap<String,Object>();
		queryParameters.put("RoleName",roleName);
		return (RoleBO)execUniqueResultNamedQuery(
			NamedQueryConstants.GET_ROLE_FOR_GIVEN_NAME,queryParameters);
	}
	
	public List<ActivityEntity> getActivities() throws PersistenceException{
		try {
			return getActivities(HibernateUtil.getSessionTL());
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
	}

	public List<ActivityEntity> getActivities(Session session) {
		Query query = session.getNamedQuery(
			NamedQueryConstants.GET_ALL_ACTIVITIES);
		return query.list();
	}
	
	@SuppressWarnings("cast")
	public List<RoleBO> getRoles() throws PersistenceException{
		return (List<RoleBO>)executeNamedQuery(
			NamedQueryConstants.GET_ALL_ROLES,null);
	}
	
	public RoleBO getRole(Short roleId) throws PersistenceException{
		return (RoleBO)getPersistentObject(RoleBO.class,roleId);
	}

}
