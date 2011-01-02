package org.mifos.customers.checklist.business;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.checklist.exceptions.CheckListException;
import org.mifos.customers.checklist.persistence.CheckListPersistence;
import org.mifos.customers.checklist.util.helpers.CheckListType;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.framework.exceptions.PersistenceException;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.annotation.ExpectedException;

@RunWith(MockitoJUnitRunner.class)
public class CheckListBOTest {
    final CheckListPersistence checkListPersistence = mock(CheckListPersistence.class);

    @Test
    @ExpectedException(value = CustomerException.class)
    public void testInvalidConnectionInSave() throws PersistenceException {
        try {
            Short id = new Short("1");
            OfficeBO office = mock(OfficeBO.class);
            when(office.getOfficeId()).thenReturn(id);
            CheckListBO checkListBO = new CheckListBO() {
                @Override
                public CheckListType getCheckListType() {
                    return null;
                }

                @Override
                protected CheckListPersistence getCheckListPersistence() {
                    return checkListPersistence;
                }
            };
            when(checkListPersistence.createOrUpdate(checkListBO)).thenThrow(new PersistenceException("some exception"));
            checkListBO.save();
            junit.framework.Assert.fail("should fail because of invalid session");
        } catch (CheckListException e) {
        }
    }


}
