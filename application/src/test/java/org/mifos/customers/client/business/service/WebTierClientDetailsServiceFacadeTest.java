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

package org.mifos.customers.client.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.customers.business.CustomerStatusEntity;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.ClientPerformanceHistoryEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.util.helpers.ClientDisplayDto;
import org.mifos.customers.util.helpers.ClientPerformanceHistoryDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WebTierClientDetailsServiceFacadeTest {
    
    // class under test
    private WebTierClientDetailsServiceFacade clientDetailsServiceFacade;

    @Mock
    private CustomerDao customerDao;
    
    @Mock
    private CustomerBusinessService customerBusinessService;
    
    @Mock
    private ClientBO client;
    
    @Mock
    private ClientPerformanceHistoryEntity clientPerformanceHistoryEntity;
    
    @Mock
    private CustomerStatusEntity customerStatusEntity;
    
    @Mock
    private OfficeBO office;
    
    @Before
    public void setupAndInjectDependencies() {
        clientDetailsServiceFacade = new WebTierClientDetailsServiceFacade(customerDao, customerBusinessService);
    }

    @Ignore
    @Test
    public void shouldLoadDelinquentPortfolioAmountForClient() throws Exception {
        String globalCustNum = "123";
        String amount = "10.0";
        String clientName = "client name";
        String globalCusttomerNumber = "1234";
        String customerStatusEntityName = "status name";
        Short customerStatusId = 1;
        Short branchId = Short.valueOf("1");
        
        UserContext userContext = new UserContext();
        
        when(customerDao.findClientBySystemId(globalCustNum)).thenReturn(client);
        
        when(client.getClientPerformanceHistory()).thenReturn(clientPerformanceHistoryEntity);
        when(client.getCustomerStatus()).thenReturn(customerStatusEntity);
        when(client.getDisplayName()).thenReturn(clientName);
        when(client.getGlobalCustNum()).thenReturn(globalCusttomerNumber);
        when(client.getOffice()).thenReturn(office);
        when(office.getOfficeId()).thenReturn(branchId);
        
        when(customerStatusEntity.getId()).thenReturn(customerStatusId);
        when(customerStatusEntity.getName()).thenReturn(customerStatusEntityName);
        when(clientPerformanceHistoryEntity.getDelinquentPortfolioAmount()).thenReturn(new Money(TestUtils.RUPEE,amount));
        
        ClientInformationDto clientInformationDto = clientDetailsServiceFacade.getClientInformationDto(globalCustNum, userContext);
        
        ClientDisplayDto clientDisplayDto = clientInformationDto.getClientDisplay();
        ClientPerformanceHistoryDto performanceHistory = clientInformationDto.getClientPerformanceHistory();
        
        assertThat(performanceHistory.getDelinquentPortfolioAmount(), is(amount));
        assertThat(clientDisplayDto.getCustomerStatusId(), is(customerStatusId));
        assertThat(clientDisplayDto.getCustomerStatusName(), is(customerStatusEntityName));
        assertThat(clientDisplayDto.getDisplayName(), is(clientName));
        assertThat(clientDisplayDto.getGlobalCustNum(), is(globalCusttomerNumber));
    }
}
