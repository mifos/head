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

package org.mifos.accounts.loan.business;

import org.mifos.application.productdefinition.business.InstallmentRange;

/**
 * this class is used for saving max min no of installmment with the loan
 * account creation so that it will refer to these values when editing the
 * account.
 */

public class MaxMinNoOfInstall extends InstallmentRange {

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private Integer accountId;
    @SuppressWarnings("unused")
    // see .hbm.xml file
    private LoanBO loan;

    public MaxMinNoOfInstall() {
        this(null, null, null);
    }

    public MaxMinNoOfInstall(Short maxLoanAmount, Short minLoanAmount, LoanBO loanBO) {
        super(minLoanAmount, maxLoanAmount);
        this.loan = loanBO;
        this.accountId = null;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public LoanBO getLoanBO() {
        return loan;
    }

    public void setLoanBO(LoanBO loanBO) {
        this.loan = loanBO;
    }
}
