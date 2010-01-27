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

package org.mifos.application.customer.center.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.exceptions.ServiceException;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class WebTierClientDetailsServiceFacadeTest {
    // class under test
    private WebTierCenterDetailsServiceFacade centerDetailsServiceFacade;

    @Mock
    private CustomerBusinessService customerBusinessService;
    
    @Mock
    private CenterBO center;
    
    @Mock
    private OfficeBO office;
    
    @Before
    public void setupAndInjectDependencies() {
        centerDetailsServiceFacade = new WebTierCenterDetailsServiceFacade();
        centerDetailsServiceFacade.setCustomerBusinessService(customerBusinessService);
    }

    @Test
    public void shouldLoadTotalOutstandingPortfolioForCenter() throws ServiceException {
        String globalCustNum = "123";
        Short levelId = 1;
        String searchId = "1.1";
        Short officeId = 1;
        Integer numberOfGroups = 2;
        Integer numberOfClients = 8;
        String totalOutstandingPortfolio = "33";
        String totalSavings = "44";
        String portfolioAtRisk = "2";
        CenterPerformanceHistory centerPerformanceHistory = new CenterPerformanceHistory(numberOfGroups, 
                numberOfClients, totalOutstandingPortfolio, totalSavings, portfolioAtRisk);
        
        when(customerBusinessService.findBySystemId(globalCustNum, levelId)).thenReturn(center);
        when(center.getSearchId()).thenReturn(searchId);
        when(center.getOffice()).thenReturn(office);
        when(office.getOfficeId()).thenReturn(officeId);
        when(customerBusinessService.getCenterPerformanceHistory(searchId, officeId)).thenReturn(centerPerformanceHistory);
        
        CenterInformationDto centerInformationDto = centerDetailsServiceFacade.getCenterInformationDto(globalCustNum, levelId);
        assertThat(centerInformationDto.getTotalOutstandingPortfolio(), is(totalOutstandingPortfolio));
    }
}
