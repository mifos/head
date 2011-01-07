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

package org.mifos.accounts.loan.business;

import org.mifos.accounts.productdefinition.business.AmountRange;

/**
 * this class is used for persisting the max min interest rate with account
 * creation so that it will refer to these values when editing
 */

public class MaxMinInterestRate extends AmountRange {

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private Integer accountId;
    @SuppressWarnings("unused")
    // see .hbm.xml file
    private LoanBO loan;

    public MaxMinInterestRate() {
        this(null, null, null);
    }

    public MaxMinInterestRate(Double maxInterestRate, Double minInterestRate, LoanBO loanBO) {
        super(minInterestRate, maxInterestRate);
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