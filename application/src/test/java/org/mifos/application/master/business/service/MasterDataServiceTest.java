package org.mifos.application.master.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.springframework.test.annotation.ExpectedException;

public class MasterDataServiceTest {
    final PersonnelPersistence personnelPersistence = mock(PersonnelPersistence.class);
    final OfficePersistence officePersistence = mock(OfficePersistence.class);
    final CustomerPersistence customerPersistence = mock(CustomerPersistence.class);
    final MasterPersistence masterPersistence = mock(MasterPersistence.class);

    MasterDataService service = new MasterDataService() {
        @Override
        protected PersonnelPersistence getPersonnelPersistence() {
            return personnelPersistence;
        }

        @Override
        protected OfficePersistence getOfficePersistence() {
            return officePersistence;
        }

        @Override
        protected CustomerPersistence getCustomerPersistence() {
            return customerPersistence;
        }

        @Override
        MasterPersistence getMasterPersistence() {
            return masterPersistence;
        }
    };

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForFindById() throws PersistenceException {
        try {
            Short accountId = new Short("1");
            when(officePersistence.getActiveOffices(accountId)).
                    thenThrow(new PersistenceException("some exception"));
            service.getActiveBranches(accountId);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForGetListOfActiveLoanOfficers() throws PersistenceException {
        try {
            Short id = Short.valueOf("3");
            when(personnelPersistence.getActiveLoanOfficersInBranch(PersonnelConstants.LOAN_OFFICER, id, id, PersonnelConstants.LOAN_OFFICER)).
                    thenThrow(new PersistenceException("some exception"));
            service.getListOfActiveLoanOfficers(PersonnelConstants.LOAN_OFFICER, id, id, PersonnelConstants.LOAN_OFFICER);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForGetListOfActiveParentsUnderLoanOfficer() throws PersistenceException {
        try {
            Short id = Short.valueOf("3");
            Short value = CustomerLevel.CENTER.getValue();
            when(customerPersistence.getActiveParentList(id, value, id)).
                    thenThrow(new PersistenceException("some exception"));
            service.getListOfActiveParentsUnderLoanOfficer(id, value, id);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForGetMasterDataEntity() throws PersistenceException {
        try {
            when(masterPersistence.getMasterDataEntity(AccountStateEntity.class, AccountState.LOAN_PARTIAL_APPLICATION
                    .getValue())).
                    thenThrow(new PersistenceException("some exception"));
            service.getMasterDataEntity(AccountStateEntity.class, AccountState.LOAN_PARTIAL_APPLICATION
                    .getValue());
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }

    @Test
    @ExpectedException(value = ServiceException.class)
    public void testInvalidConnectionForRetrieveMasterEntities() throws PersistenceException {
        try {
            Short id = Short.valueOf("1");
            when(masterPersistence.retrieveMasterEntities(1, id)).
                    thenThrow(new PersistenceException("some exception"));
            service.retrieveMasterEntities(1, id);
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (ServiceException e) {
        }
    }
}