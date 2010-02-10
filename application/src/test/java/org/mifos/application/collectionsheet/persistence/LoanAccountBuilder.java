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
package org.mifos.application.collectionsheet.persistence;

import java.util.Date;

import org.joda.time.DateTime;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.LoanProductBuilder;
import org.mifos.accounts.productdefinition.util.helpers.GraceType;
import org.mifos.framework.TestUtils;

/**
 *
 */
public class LoanAccountBuilder {
    
    private LoanOfferingBO loanProduct = new LoanProductBuilder().buildForUnitTests();
    private final Short numOfInstallments = Short.valueOf("5");
    private final GraceType gracePeriodType = GraceType.NONE;
    private final AccountTypes accountType = AccountTypes.LOAN_ACCOUNT;
    private final AccountState accountState = AccountState.LOAN_ACTIVE_IN_GOOD_STANDING;
    private CustomerBO customer;
    private final Integer offsettingAllowable = Integer.valueOf(1);
    
    private final Short createdByUserId = TestUtils.makeUserWithLocales().getId();
    private final Date createdDate = new DateTime().minusDays(14).toDate();
    
    public LoanBO build() {
        final LoanBO loanAccount = new LoanBO(loanProduct, numOfInstallments, gracePeriodType, accountType,
                accountState, customer, offsettingAllowable);
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
}
