package org.mifos.customers.office.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.ExpectedException;

@RunWith(MockitoJUnitRunner.class)
public class OfficeBusinessServiceTest {
    final OfficePersistence officePersistence = mock(OfficePersistence.class);

    OfficeBusinessService service = new OfficeBusinessService() {
        @Override
        protected OfficePersistence getOfficePersistence() {
            return officePersistence;
        }
    };
    Short id = new Short("1");

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetBranchOffices() throws PersistenceException {
        try {
            when(officePersistence.getBranchOffices()).
                    thenThrow(new PersistenceException("some exception"));
            service.getBranchOffices();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetBranchOfficesUnderPersonnel() throws PersistenceException {
        try {
            PersonnelBO personnel = mock(PersonnelBO.class);
            String searchId = "somestr";
            when(personnel.getOfficeSearchId()).thenReturn(searchId);
            when(officePersistence.getActiveBranchesUnderUser(searchId)).
                    thenThrow(new PersistenceException("some exception"));
            service.getActiveBranchesUnderUser(personnel);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetChildOffice() throws PersistenceException {
        try {
            String searchId = "somestr";
            when(officePersistence.getChildOffices(searchId)).
                    thenThrow(new PersistenceException("some exception"));
            service.getChildOffices(searchId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetOffice() throws PersistenceException {
        try {
            when(officePersistence.getOffice(id)).
                    thenThrow(new PersistenceException("some exception"));
            service.getOffice(id);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInGetOfficesTillBranchOffice() throws PersistenceException {
        try {
            when(officePersistence.getOfficesTillBranchOffice()).
                    thenThrow(new PersistenceException("some exception"));
            service.getOfficesTillBranchOffice();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

}
