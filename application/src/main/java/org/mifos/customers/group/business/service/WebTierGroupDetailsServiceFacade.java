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

import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.GroupPerformanceHistoryEntity;
import org.mifos.application.master.MessageLookup;
import org.mifos.core.CurrencyMismatchException;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class WebTierGroupDetailsServiceFacade implements GroupDetailsServiceFacade {

    CustomerBusinessService customerBusinessService;
    /* (non-Javadoc)
     * @see org.mifos.customers.client.business.service.ClientDetailsServiceFacade#getClientInformationDto(java.lang.String, java.lang.Short)
     */
    @Override
    public GroupInformationDto getGroupInformationDto(String globalCustNum) throws ServiceException, CustomerException {
        GroupBO group = (GroupBO)getCustomerBusinessService().findBySystemId(globalCustNum);
        if (group == null) {
            throw new MifosRuntimeException("Group not found for globalCustNum: " + globalCustNum);
        }
        GroupPerformanceHistoryEntity performancHistory = group.getGroupPerformanceHistory();

        String avgLoanAmountForMember;
        String totalOutStandingLoanAmount;
        String portfolioAtRisk;
        String totalSavingsAmount;
        try {
            avgLoanAmountForMember = performancHistory.getAvgLoanAmountForMember().toString();
        } catch(CurrencyMismatchException e) {
            avgLoanAmountForMember = localizedMessageLookup("errors.multipleCurrencies");
        }
        try {
            totalOutStandingLoanAmount = performancHistory.getTotalOutStandingLoanAmount().toString();
        } catch(CurrencyMismatchException e) {
            totalOutStandingLoanAmount = localizedMessageLookup("errors.multipleCurrencies");
        }
        try {
            if (performancHistory.getPortfolioAtRisk() == null) {
                portfolioAtRisk = "0";
            } else {
                portfolioAtRisk = performancHistory.getPortfolioAtRisk().toString();
            }
        } catch(CurrencyMismatchException e) {
            portfolioAtRisk = localizedMessageLookup("errors.multipleCurrencies");
        }
        try {
            totalSavingsAmount = performancHistory.getTotalSavingsAmount().toString();
        } catch(CurrencyMismatchException e) {
            totalSavingsAmount = localizedMessageLookup("errors.multipleCurrencies");
        }

        return new GroupInformationDto(avgLoanAmountForMember, totalOutStandingLoanAmount, 
                portfolioAtRisk, totalSavingsAmount, group.getDisplayName());   
    }
    
    public CustomerBusinessService getCustomerBusinessService() {
        if (customerBusinessService == null) {
            customerBusinessService = new CustomerBusinessService();
        }
        return this.customerBusinessService;
    }
    
    // allow for service injection
    public void setCustomerBusinessService(CustomerBusinessService customerBusinessService) {
        this.customerBusinessService = customerBusinessService;
    }

    protected String localizedMessageLookup(String key) {
        return MessageLookup.getInstance().lookup(key);
    }
    
    
}
