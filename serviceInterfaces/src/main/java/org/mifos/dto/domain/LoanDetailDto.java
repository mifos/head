/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
package org.mifos.dto.domain;

import java.io.Serializable;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class LoanDetailDto implements Serializable {

    private final String globalAccountNum;
    private final String prdOfferingName;
    private final Short accountStateId;
    private final String accountStateName;
    private final String outstandingBalance;
    private final String totalAmountDue;
    //account type for displaying new type of GLIM acc
    private final Short accountTypeId;

    public LoanDetailDto(final String globalAccountNum, final String prdOfferingName, final Short accountStateId,
            final String accountStateName, final String outstandingBalance, final String totalAmountDue) {
        this.globalAccountNum = globalAccountNum;
        this.prdOfferingName = prdOfferingName;
        this.accountStateId = accountStateId;
        this.accountStateName = accountStateName;
        this.outstandingBalance = outstandingBalance;
        this.totalAmountDue = totalAmountDue;
        this.accountTypeId = 0;
    }
    
    public LoanDetailDto(final String globalAccountNum, final String prdOfferingName, final Short accountStateId,
            final String accountStateName, final String outstandingBalance, final String totalAmountDue, final Short accountTypeId) {
        this.globalAccountNum = globalAccountNum;
        this.prdOfferingName = prdOfferingName;
        this.accountStateId = accountStateId;
        this.accountStateName = accountStateName;
        this.outstandingBalance = outstandingBalance;
        this.totalAmountDue = totalAmountDue;
        this.accountTypeId = accountTypeId;
    }

    public String getGlobalAccountNum() {
        return this.globalAccountNum;
    }

    public String getPrdOfferingName() {
        return this.prdOfferingName;
    }

    public Short getAccountStateId() {
        return this.accountStateId;
    }

    public String getAccountStateName() {
        return this.accountStateName;
    }

    public String getOutstandingBalance() {
        return this.outstandingBalance;
    }

    public String getTotalAmountDue() {
        return this.totalAmountDue;
    }

    public Short getAccountTypeId() {
        return accountTypeId;
    }
}