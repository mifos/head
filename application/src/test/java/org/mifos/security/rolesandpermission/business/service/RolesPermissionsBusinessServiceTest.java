package org.mifos.security.rolesandpermission.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.ExpectedException;

@RunWith(MockitoJUnitRunner.class)
public class RolesPermissionsBusinessServiceTest {
    final RolesPermissionsPersistence rolesPermissionsPersistence = mock(RolesPermissionsPersistence.class);

    RolesPermissionsBusinessService service = new RolesPermissionsBusinessService() {
        @Override
        protected RolesPermissionsPersistence getRolesPermissionsPersistence() {
            return rolesPermissionsPersistence;
        }
    };
    Short id = new Short("1");

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetActivities() throws PersistenceException {
        try {
            when(rolesPermissionsPersistence.getActivities()).thenThrow(new PersistenceException("some exception"));
            service.getActivities();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetRole() throws PersistenceException {
        try {
            when(rolesPermissionsPersistence.getRole(id)).thenThrow(new PersistenceException("some exception"));
            service.getRole(id);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetRoles() throws PersistenceException {
        try {
            when(rolesPermissionsPersistence.getRoles()).thenThrow(new PersistenceException("some exception"));
            service.getRoles();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }


}
