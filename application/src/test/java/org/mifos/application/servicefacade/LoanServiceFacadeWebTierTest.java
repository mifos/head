/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
package org.mifos.application.servicefacade;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.struts.action.LoanCreationGlimDto;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class LoanServiceFacadeWebTierTest {

    // class under test
    private LoanServiceFacade loanServiceFacade;

    // collaborators
    @Mock
    private LoanProductDao loanProductDao;

    @Mock
    private CustomerDao customerDao;

    // test data
    @Mock
    private CustomerBO customer;

    @Mock
    private ClientBO client;

    @Mock
    private LoanOfferingBO activeLoanProduct;

    private MeetingBO meeting;

    @Before
    public void setupAndInjectDependencies() {
        loanServiceFacade = new LoanServiceFacadeWebTier(loanProductDao, customerDao);
    }

    @Test
    public void shouldFindAllActiveLoanProductsWithMeetingThatMatchCustomerMeeting() {

        // setup
        meeting = new MeetingBuilder().build();
        CustomerLevelEntity customerLevelEntity = new CustomerLevelEntity(CustomerLevel.GROUP);

        List<LoanOfferingBO> activeLoanProducts = Arrays.asList(activeLoanProduct);

        // stubbing
        when(customer.getCustomerLevel()).thenReturn(customerLevelEntity);
        when(loanProductDao.findActiveLoanProductsApplicableToCustomerLevel(customerLevelEntity)).thenReturn(
                activeLoanProducts);
        when(customer.getCustomerMeetingValue()).thenReturn(meeting);
        when(activeLoanProduct.getLoanOfferingMeetingValue()).thenReturn(meeting);

        // exercise test
        List<LoanOfferingBO> activeLoanProductsForCustomer = loanServiceFacade
                .loadActiveProductsApplicableForCustomer(customer);

        // verification
        assertThat(activeLoanProductsForCustomer, hasItem(activeLoanProduct));
    }

    @Test
    public void shouldFindGlimDataForGroupCustomer() {

        // setup
        ValueListElement valueListElement = new BusinessActivityEntity(Integer.valueOf(1), "", "");
        List<ValueListElement> loanPurposes = Arrays.asList(valueListElement);

        List<ClientBO> clients = Arrays.asList(client);

        // stubbing
        when(loanProductDao.findAllLoanPurposes()).thenReturn(loanPurposes);
        when(customerDao.findActiveClientsUnderGroup(customer)).thenReturn(clients);

        // exercise test
        LoanCreationGlimDto glimData = loanServiceFacade.retrieveGlimSpecificDataForGroup(customer);

        // verification
        assertThat(glimData.getLoanPurposes(), hasItem(valueListElement));
        assertThat(glimData.getActiveClientsOfGroup(), hasItem(client));
    }

}
