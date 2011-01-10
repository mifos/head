package org.mifos.application.master.business.service;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
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
        MasterPersistence getMasterPersistence() {
            return masterPersistence;
        }
    };

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

}