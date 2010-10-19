package org.mifos.customers.center.business;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.security.util.UserContext;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.ExpectedException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CenterBOTest {
    final CustomerPersistence customerPersistence = mock(CustomerPersistence.class);

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInConstructor() throws PersistenceException {
        try {
            Short id = new Short("1");
            OfficeBO office = mock(OfficeBO.class);
            when(office.getOfficeId()).thenReturn(id);
            when(customerPersistence.getCustomerCountForOffice(CustomerLevel.CENTER, id)).thenThrow(new PersistenceException("some exception"));
            new CenterBO(mock(UserContext.class), "name", null, null, null, "externalId", null, office, mock(MeetingBO.class),
                    mock(PersonnelBO.class), customerPersistence);

            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (CustomerException e) {
        }
    }

}
