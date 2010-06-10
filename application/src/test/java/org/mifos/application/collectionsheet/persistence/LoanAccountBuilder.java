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
package org.mifos.application.collectionsheet.persistence;

import java.math.BigDecimal;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanProductBuilder;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.customers.business.CustomerBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class LoanAccountBuilder {

    private LoanOfferingBO loanProduct = new LoanProductBuilder().buildForUnitTests();
    private final Short numOfInstallments = Short.valueOf("5");
    private final GraceType gracePeriodType = GraceType.NONE;
    private final AccountTypes accountType = AccountTypes.LOAN_ACCOUNT;
    private AccountState accountState = AccountState.LOAN_ACTIVE_IN_GOOD_STANDING;
    private CustomerBO customer;
    private final Integer offsettingAllowable = Integer.valueOf(1);

    private double orgininalLoanAmount = Double.valueOf("300.0");
    private double remainingLoanBalance = Double.valueOf("300.0");

    public LoanBO build() {

        BigDecimal amount = BigDecimal.valueOf(orgininalLoanAmount);
        Money loanAmount = new Money(Money.getDefaultCurrency(), amount);

        BigDecimal balance = BigDecimal.valueOf(remainingLoanBalance);
        Money loanBalance = new Money(Money.getDefaultCurrency(), balance);

        final LoanBO loanAccount = new LoanBO(loanProduct, numOfInstallments, gracePeriodType, accountType,
                accountState, customer, offsettingAllowable, loanAmount, loanBalance);
        loanAccount.setUserContext(TestUtils.makeUser());
        loanAccount.setCreateDetails();

        return loanAccount;
    }

    public LoanAccountBuilder withLoanProduct(final LoanOfferingBO withLoanProduct) {
        this.loanProduct = withLoanProduct;
        return this;
    }

    public LoanAccountBuilder withCustomer(final CustomerBO withCustomer) {
        this.customer = withCustomer;
        return this;
    }

    public LoanAccountBuilder withOriginalLoanAmount(double withLoanAmount) {
        this.orgininalLoanAmount = withLoanAmount;
        this.remainingLoanBalance = withLoanAmount;
        return this;
    }

    public LoanAccountBuilder remainingLoanBalance(double withRemainingLoanBalance) {
        this.remainingLoanBalance = withRemainingLoanBalance;
        return this;
    }

    public LoanAccountBuilder activeInGoodStanding() {
        this.accountState = AccountState.LOAN_ACTIVE_IN_GOOD_STANDING;
        return this;
    }
}