/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.customers;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.servicefacade.CenterUpdate;
import org.mifos.customers.business.service.CustomerAccountFactory;
import org.mifos.customers.business.service.CustomerService;
import org.mifos.customers.business.service.CustomerServiceImpl;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.domain.builders.CenterUpdateBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateTransactionHelper;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test implementation of {@link CustomerService} for update of status of {@link CenterBO}'s.
 */
@RunWith(MockitoJUnitRunner.class)
public class CenterStatusUpdateTest {

    // class under test
    private CustomerService customerService;

    // collaborators
    @Mock
    private CustomerDao customerDao;

    @Mock
    private PersonnelDao personnelDao;

    @Mock
    private OfficeDao officeDao;

    @Mock
    private HolidayDao holidayDao;

    @Mock
    private HibernateTransactionHelper hibernateTransaction;

    @Mock
    private CustomerAccountFactory customerAccountFactory;

    @Mock
    private CenterBO mockedCenter;

    @Before
    public void setupAndInjectDependencies() {
        customerService = new CustomerServiceImpl(customerDao, personnelDao, officeDao, holidayDao, hibernateTransaction);
        ((CustomerServiceImpl)customerService).setCustomerAccountFactory(customerAccountFactory);
    }

    @Test
    public void throwsCheckedExceptionWhenVersionOfCenterForUpdateIsDifferentToPersistedCenterVersion() throws Exception {

        // setup
//        UserContext userContext = TestUtils.makeUser();
//        CenterUpdate centerUpdate = new CenterUpdateBuilder().build();
//
//        // stubbing
//        when(customerDao.findCustomerById(centerUpdate.getCustomerId())).thenReturn(mockedCenter);
//        doThrow(new CustomerException(Constants.ERROR_VERSION_MISMATCH)).when(mockedCenter).validateVersion(anyInt());

        // exercise test
//        customerService.updateCenterStatus(center, newStatus, customerStatusFlag, customerNote);
    }
}