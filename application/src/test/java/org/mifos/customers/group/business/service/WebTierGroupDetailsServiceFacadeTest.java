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

package org.mifos.customers.group.business.service;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;
import static org.hamcrest.CoreMatchers.is;
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
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientPerformanceHistoryEntity;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.GroupPerformanceHistoryEntity;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.application.servicefacade.LoanServiceFacade;
import org.mifos.application.servicefacade.LoanServiceFacadeWebTier;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WebTierGroupDetailsServiceFacadeTest {
    // class under test
    private WebTierGroupDetailsServiceFacade groupDetailsServiceFacade;

    @Mock
    private CustomerBusinessService customerBusinessService;
    
    @Mock
    private GroupBO group;
    
    @Mock
    private GroupPerformanceHistoryEntity groupPerformanceHistoryEntity;
    
    @Before
    public void setupAndInjectDependencies() {
        groupDetailsServiceFacade = new WebTierGroupDetailsServiceFacade();
        groupDetailsServiceFacade.setCustomerBusinessService(customerBusinessService);
    }

    @Test
    public void shouldLoadAverageLoanAmountForMemberForGroup() throws ServiceException, CustomerException {
        String globalCustNum = "123";
        String amount = "10.0";
        Money moneyAmount = new Money(TestUtils.RUPEE,amount);
        when(customerBusinessService.findBySystemId(globalCustNum)).thenReturn(group);
        when(group.getGroupPerformanceHistory()).thenReturn(groupPerformanceHistoryEntity);
        when(groupPerformanceHistoryEntity.getAvgLoanAmountForMember()).thenReturn(moneyAmount);
        when(groupPerformanceHistoryEntity.getPortfolioAtRisk()).thenReturn(moneyAmount);
        when(groupPerformanceHistoryEntity.getTotalOutStandingLoanAmount()).thenReturn(moneyAmount);
        when(groupPerformanceHistoryEntity.getTotalSavingsAmount()).thenReturn(moneyAmount);
        GroupInformationDto groupInformationDto = groupDetailsServiceFacade.getGroupInformationDto(globalCustNum);
        assertThat(groupInformationDto.getAvgLoanAmountForMember(), is(amount));
    }
}
