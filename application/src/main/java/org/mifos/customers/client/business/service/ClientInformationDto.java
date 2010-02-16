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

package org.mifos.customers.client.business.service;

import java.util.List;

import org.mifos.framework.business.service.DataTransferObject;

/**
 * Initial data transfer object to hold data for display 
 * on the viewClientDetails page.
 *
 */
public class ClientInformationDto implements DataTransferObject {
    private final Short customerStatusId;
    private final String customerStatusName;
    private final String delinquentPortfolioAmount;
    private final String displayName;
    private final String globalCustNum;
    private final List<ClientLoanInformationDto> clientLoans;
    

    public ClientInformationDto(Short customerStatusId, String customerStatusName, String delinquentPortfolioAmount,
            String displayName, String globalCustNum, List<ClientLoanInformationDto> clientLoans) {
        super();
        this.customerStatusId = customerStatusId;
        this.customerStatusName = customerStatusName;
        this.delinquentPortfolioAmount = delinquentPortfolioAmount;
        this.displayName = displayName;
        this.globalCustNum = globalCustNum;
        this.clientLoans = clientLoans;
    }
    
    public Short getCustomerStatusId() {
        return this.customerStatusId;
    }

    public String getCustomerStatusName() {
        return this.customerStatusName;
    }

    public List<ClientLoanInformationDto> getClientLoans() {
        return this.clientLoans;
    }

    public String getGlobalCustNum() {
        return this.globalCustNum;
    }

    public String getDelinquentPortfolioAmount() {
        return this.delinquentPortfolioAmount;
    }

    public String getDisplayName() {
        return this.displayName;
    }        
        
}
