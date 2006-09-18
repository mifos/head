package org.mifos.application.rolesandpermission.business.service;

import java.util.List;

import javax.xml.rpc.ServiceException;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.rolesandpermission.business.RoleBO;
import org.mifos.application.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;


public class RolesPermissionsBusinessService extends BusinessService {
	
	private RolesPermissionsPersistence rolesPermissionsPersistence=new RolesPermissionsPersistence();

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}
	
	public List<RoleBO> getRoles() throws ServiceException {
		try {
			return rolesPermissionsPersistence.getRoles();
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

}
