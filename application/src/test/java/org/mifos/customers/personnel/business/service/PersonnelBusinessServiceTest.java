package org.mifos.customers.personnel.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.rolesandpermission.persistence.RolesPermissionsPersistence;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.ExpectedException;

@RunWith(MockitoJUnitRunner.class)
public class PersonnelBusinessServiceTest {
    final PersonnelPersistence personnelPersistence = mock(PersonnelPersistence.class);
    final RolesPermissionsPersistence rolesPermissionsPersistence = mock(RolesPermissionsPersistence.class);
    final OfficePersistence officePersistence = mock(OfficePersistence.class);

    PersonnelBusinessService service = new PersonnelBusinessService(personnelPersistence, rolesPermissionsPersistence) {
        @Override
        protected OfficePersistence getOfficePersistence() {
            return officePersistence;
        }
    };
    Short id = new Short("1");

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetOffice() throws PersistenceException {
        try {
            when(officePersistence.getOffice(id)).thenThrow(new PersistenceException("some exception"));
            service.getOffice(id);
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

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetPersonnelByUsername() throws PersistenceException {
        try {
            String shortName = "shortname";
            when(personnelPersistence.getPersonnelByUserName(shortName)).thenThrow(new PersistenceException("some exception"));
            service.getPersonnel(shortName);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInSearch() throws PersistenceException {
        try {
            String searchStr = "shortname";
            when(personnelPersistence.search(searchStr, id)).thenThrow(new PersistenceException("some exception"));
            service.search(searchStr, id);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetActiveLoanOfficersUnderOffice() throws PersistenceException {
        try {
            when(personnelPersistence.getActiveLoanOfficersUnderOffice(id)).thenThrow(new PersistenceException("some exception"));
            service.getActiveLoanOfficersUnderOffice(id);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

}
