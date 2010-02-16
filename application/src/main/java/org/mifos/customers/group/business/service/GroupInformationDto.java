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

package org.mifos.customers.group.business.service;

import org.mifos.framework.business.service.DataTransferObject;

/**
 * Initial data transfer object to hold data for display 
 * on the viewgroupdetails.jsp page.
 */
public class GroupInformationDto implements DataTransferObject {
    private final String avgLoanAmountForMember;
    private final String totalOutStandingLoanAmount;
    private final String portfolioAtRisk;
    private final String totalSavingsAmount;       
    private final String displayName;
    
    public GroupInformationDto(String avgLoanAmountForMember, String totalOutStandingLoanAmount,
            String portfolioAtRisk, String totalSavingsAmount, String displayName) {
        this.displayName = displayName;
        this.avgLoanAmountForMember = avgLoanAmountForMember;
        this.totalOutStandingLoanAmount = totalOutStandingLoanAmount;
        this.portfolioAtRisk = portfolioAtRisk;
        this.totalSavingsAmount = totalSavingsAmount;
    }

    public String getAvgLoanAmountForMember() {
        return this.avgLoanAmountForMember;
    }

    public String getTotalOutStandingLoanAmount() {
        return this.totalOutStandingLoanAmount;
    }

    public String getPortfolioAtRisk() {
        return this.portfolioAtRisk;
    }

    public String getTotalSavingsAmount() {
        return this.totalSavingsAmount;
    }

    public String getDisplayName() {
        return displayName;
    }    
    
}
