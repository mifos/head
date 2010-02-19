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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsPaymentStrategy;
import org.mifos.accounts.savings.business.SavingsPaymentStrategyImpl;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.savings.business.SavingsTransactionActivityHelper;
import org.mifos.accounts.savings.business.SavingsTransactionActivityHelperImpl;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class SavingsAccountBuilder {

    private final MeetingBO scheduleForInterestCalculation = new MeetingBuilder().savingsInterestCalulationSchedule()
            .monthly().every(1).build();
    private SavingsOfferingBO savingsProduct = new SavingsProductBuilder().buildForUnitTests();
    private final Short createdByUserId = TestUtils.makeUserWithLocales().getId();
    private final Date createdDate = new DateTime().minusDays(14).toDate();

    private final AccountTypes accountType = AccountTypes.SAVINGS_ACCOUNT;
    private AccountState accountState = AccountState.SAVINGS_ACTIVE;
    private CustomerBO customer;
    private final Integer offsettingAllowable = Integer.valueOf(1);

    private final Double interestRate = Double.valueOf("4.0");
    private final InterestCalcType interestCalcType = InterestCalcType.MINIMUM_BALANCE;

    private SavingsType savingsType = SavingsType.VOLUNTARY;
    private RecommendedAmountUnit recommendedAmountUnit = RecommendedAmountUnit.COMPLETE_GROUP;
    private Money recommendedAmount = new Money(TestUtils.RUPEE, "13.0");
    private CustomerPersistence customerDao;
    private Money savingsBalanceAmount = new Money(TestUtils.RUPEE, "0.0");
    private SavingsTransactionActivityHelper savingsTransactionActivityHelper = new SavingsTransactionActivityHelperImpl();
    private SavingsPaymentStrategy savingsPaymentStrategy = new SavingsPaymentStrategyImpl(
            savingsTransactionActivityHelper);
    private Set<AccountActionDateEntity> scheduledPayments = new LinkedHashSet<AccountActionDateEntity>();
    private PersonnelBO savingsOfficer;

    public SavingsBO build() {

        final SavingsBO savingsBO = new SavingsBO(savingsProduct, savingsType, savingsBalanceAmount,
                savingsPaymentStrategy, savingsTransactionActivityHelper, scheduledPayments, interestRate,
                interestCalcType, accountType, accountState, customer, offsettingAllowable,
                scheduleForInterestCalculation, recommendedAmountUnit, recommendedAmount, savingsOfficer, createdDate,
                createdByUserId);
        savingsBO.setCustomerPersistence(customerDao);

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

    public SavingsAccountBuilder mandatory() {
        this.savingsType = SavingsType.MANDATORY;
        return this;
    }

    public SavingsAccountBuilder voluntary() {
        this.savingsType = SavingsType.VOLUNTARY;
        return this;
    }

    public SavingsAccountBuilder completeGroup() {
        this.recommendedAmountUnit = RecommendedAmountUnit.COMPLETE_GROUP;
        return this;
    }

    public SavingsAccountBuilder perIndividual() {
        this.recommendedAmountUnit = RecommendedAmountUnit.PER_INDIVIDUAL;
        return this;
    }

    public SavingsAccountBuilder withCustomerDao(final CustomerPersistence withCustomerDao) {
        this.customerDao = withCustomerDao;
        return this;
    }

    public SavingsAccountBuilder withBalanceOf(final Money withBalanceAmount) {
        this.savingsBalanceAmount = withBalanceAmount;
        return this;
    }

    public SavingsAccountBuilder withPaymentStrategy(final SavingsPaymentStrategy withPaymentStrategy) {
        this.savingsPaymentStrategy = withPaymentStrategy;
        return this;
    }

    public SavingsAccountBuilder asInActive() {
        this.accountState = AccountState.SAVINGS_INACTIVE;
        return this;
    }

    public SavingsAccountBuilder withPayments(final List<SavingsScheduleEntity> withPayments) {
        this.scheduledPayments = new LinkedHashSet<AccountActionDateEntity>(withPayments);
        return this;
    }

    public SavingsAccountBuilder withTransactionHelper(
            final SavingsTransactionActivityHelper withTransactionActivityHelper) {
        this.savingsTransactionActivityHelper = withTransactionActivityHelper;
        return this;
    }

    public SavingsAccountBuilder withSavingsOfficer(final PersonnelBO withSavingsOfficer) {
        this.savingsOfficer = withSavingsOfficer;
        return this;
    }

    public SavingsAccountBuilder withRecommendedAmount(final Money withRecommendedAmount) {
        this.recommendedAmount = withRecommendedAmount;
        return this;
    }
}
