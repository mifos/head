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

package org.mifos.clientportfolio.newloan.domain;

import org.mifos.accounts.fund.business.FundBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.util.helpers.Money;

public class LoanAccountDetail {

    private final CustomerBO customer;
    private final LoanOfferingBO loanProduct;
    private final Money loanAmount;
    private final AccountState accountState;
    private final FundBO fund;

    public LoanAccountDetail(CustomerBO customer, LoanOfferingBO loanProduct, Money loanAmount,
            AccountState accountState, FundBO fund) {
        this.customer = customer;
        this.loanProduct = loanProduct;
        this.loanAmount = loanAmount;
        this.accountState = accountState;
        this.fund = fund;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public LoanOfferingBO getLoanProduct() {
        return loanProduct;
    }

    public Money getLoanAmount() {
        return loanAmount;
    }

    public AccountState getAccountState() {
        return accountState;
    }

    public FundBO getFund() {
        return fund;
    }
}