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

import org.mifos.framework.business.service.DataTransferObject;

public class ClientLoanInformationDto implements DataTransferObject {
    private final String totalAmountDue;
    private final String globalAccountNum;
    private final String loanProductName;
    private final String accountStateId;
    private final String accountStateName;
    private final String outstandingBalance;    

    public ClientLoanInformationDto(String totalAmountDue, String globalAccountNum, String loanProductName,
            String accountStateId, String accountStateName, String outstandingBalance) {
        super();
        this.totalAmountDue = totalAmountDue;
        this.globalAccountNum = globalAccountNum;
        this.loanProductName = loanProductName;
        this.accountStateId = accountStateId;
        this.accountStateName = accountStateName;
        this.outstandingBalance = outstandingBalance;
    }

    public String getOutstandingBalance() {
        return this.outstandingBalance;
    }

    public String getTotalAmountDue() {
        return this.totalAmountDue;
    }

    public String getGlobalAccountNum() {
        return this.globalAccountNum;
    }

    public String getLoanProductName() {
        return this.loanProductName;
    }

    public String getAccountStateId() {
        return this.accountStateId;
    }

    public String getAccountStateName() {
        return this.accountStateName;
    }               
    
}