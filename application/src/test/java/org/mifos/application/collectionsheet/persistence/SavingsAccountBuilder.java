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
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.InterestCalcType;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.TestUtils;

/**
 *
 */
public class SavingsAccountBuilder {
    
    private SavingsOfferingBO savingsProduct = new SavingsProductBuilder().buildForUnitTests();
    
    private final Short createdByUserId = TestUtils.makeUserWithLocales().getId();
    private final Date createdDate = new DateTime().minusDays(14).toDate();

    private final AccountTypes accountType = AccountTypes.SAVINGS_ACCOUNT;
    private final AccountState accountState = AccountState.SAVINGS_ACTIVE;
    private CustomerBO customer;
    private final Integer offsettingAllowable = Integer.valueOf(1);
    
    private final Double interestRate = Double.valueOf("4.0");
    private final InterestCalcType interestCalcType = InterestCalcType.MINIMUM_BALANCE;
    private final SavingsType savingsType = SavingsType.VOLUNTARY;
    
    public SavingsBO build() {
        
        final SavingsBO savingsBO = new SavingsBO(savingsProduct, savingsType, interestRate, interestCalcType,
                accountType, accountState, customer, offsettingAllowable, createdDate, createdByUserId);
        
        return savingsBO;
    }

    public SavingsAccountBuilder withSavingsProduct(final SavingsOfferingBO withSavingsProduct) {
        this.savingsProduct = withSavingsProduct;
        return this;
    }
    
    public SavingsAccountBuilder withCustomer(final CustomerBO withCustomer) {
        this.customer = withCustomer;
        return this;
    }
}
