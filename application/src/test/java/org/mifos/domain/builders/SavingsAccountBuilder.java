/*
 * Copyright Grameen Foundation USA
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
package org.mifos.domain.builders;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.savings.business.SavingsAccountActivationDetail;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsPaymentStrategy;
import org.mifos.accounts.savings.business.SavingsPaymentStrategyImpl;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.savings.business.SavingsTransactionActivityHelper;
import org.mifos.accounts.savings.business.SavingsTransactionActivityHelperImpl;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mifos.calendar.CalendarEvent;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;

/**
 *
 */
public class SavingsAccountBuilder {

    private AccountState accountState = AccountState.SAVINGS_ACTIVE;
    private CustomerBO customer;
    List<CustomerBO> jointAccountMembers = new ArrayList<CustomerBO>();
    private SavingsAccountActivationDetail activationDetails;
    private DateTime activationDate = new DateTime();
    private LocalDate nextInterestPostingDate = new LocalDate();
    private Set<AccountActionDateEntity> scheduledPayments = null;

    private final Date createdDate = new DateTime().minusDays(14).toDate();
    private final Short createdByUserId = TestUtils.makeUserWithLocales().getId();
    private SavingsOfferingBO savingsProduct = new SavingsProductBuilder().voluntary().minimumBalance().withInterestRate(Double.valueOf("4.0")).buildForUnitTests();

    private RecommendedAmountUnit recommendedAmountUnit = RecommendedAmountUnit.COMPLETE_GROUP;
    private Money recommendedAmount = new Money(TestUtils.RUPEE, "13.0");
    private PersonnelBO createdBy = new PersonnelBuilder().asLoanOfficer().build();

    private Money savingsBalanceAmount = new Money(TestUtils.RUPEE, "0.0");

    private CustomerPersistence customerDao;
    private SavingsTransactionActivityHelper savingsTransactionActivityHelper = new SavingsTransactionActivityHelperImpl();
    private SavingsPaymentStrategy savingsPaymentStrategy = new SavingsPaymentStrategyImpl(savingsTransactionActivityHelper);

    private List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
    private List<Holiday> holidays = new ArrayList<Holiday>();

    private List<AccountPaymentEntity> deposits = new ArrayList<AccountPaymentEntity>();
    private List<AccountPaymentEntity> withdrawals = new ArrayList<AccountPaymentEntity>();

    public SavingsBO buildJointSavingsAccount() {
        CalendarEvent calendarEvents = new CalendarEvent(workingDays, holidays);
        SavingsAccountActivationDetail derivedActivationDetails = SavingsBO.determineAccountActivationDetails(customer, savingsProduct, recommendedAmount,
                accountState, calendarEvents, jointAccountMembers);
        return buildAccount(derivedActivationDetails);
    }

    // build individual savings account
    public SavingsBO build() {

        CalendarEvent calendarEvents = new CalendarEvent(workingDays, holidays);
        SavingsAccountActivationDetail derivedActivationDetails = SavingsBO.determineAccountActivationDetails(customer, savingsProduct, recommendedAmount, accountState, calendarEvents);
        return buildAccount(derivedActivationDetails);
    }

    public SavingsBO buildForUnitTests() {
        List<AccountActionDateEntity> listOfScheduledPayments = new ArrayList<AccountActionDateEntity>();
        if (scheduledPayments != null) {
            listOfScheduledPayments = new ArrayList<AccountActionDateEntity>(scheduledPayments);
        }
        activationDetails = new SavingsAccountActivationDetail(new LocalDate(activationDate), nextInterestPostingDate, listOfScheduledPayments);
        return buildAccount(activationDetails);
    }

    private SavingsBO buildAccount(SavingsAccountActivationDetail derivedActivationDetails) {

        List<AccountActionDateEntity> listOfScheduledPayments = new ArrayList<AccountActionDateEntity>();
        if (scheduledPayments == null) {
            listOfScheduledPayments = derivedActivationDetails.getScheduledPayments();
        } else {
            listOfScheduledPayments = new ArrayList<AccountActionDateEntity>(scheduledPayments);
        }
        activationDetails = new SavingsAccountActivationDetail(new LocalDate(activationDate), nextInterestPostingDate, listOfScheduledPayments);

        SavingsBO savingsAccount = new SavingsBO(accountState, customer, activationDetails,
                new LocalDate(createdDate), createdByUserId.intValue(), savingsProduct, recommendedAmountUnit,
                recommendedAmount, createdBy, savingsBalanceAmount);
        savingsAccount.setCustomerPersistence(customerDao);
        savingsAccount.setSavingsPaymentStrategy(savingsPaymentStrategy);
        savingsAccount.setSavingsTransactionActivityHelper(savingsTransactionActivityHelper);
        savingsAccount.updateDetails(TestUtils.makeUserWithLocales());

//        savingsAccount = new SavingsBO(savingsProduct, savingsBalanceAmount,
//                savingsPaymentStrategy, savingsTransactionActivityHelper, scheduledPayments, accountState, customer, offsettingAllowable,
//                scheduleForInterestCalculation, recommendedAmountUnit, recommendedAmount, createdDate,
//                createdByUserId, holidays, activationDate);

        for (AccountPaymentEntity depositPayment : deposits) {
            try {
                depositPayment.setAccount(savingsAccount);
                savingsAccount.deposit(depositPayment, customer);
            } catch (AccountException e) {
                throw new MifosRuntimeException("builder failed to apply deposits.", e);
            }
        }

        for (AccountPaymentEntity withdrawal : withdrawals) {
            try {
                withdrawal.setAccount(savingsAccount);
                savingsAccount.withdraw(withdrawal, customer);
            } catch (AccountException e) {
                throw new MifosRuntimeException("builder failed to apply withdrawals.", e);
            }
        }

        return savingsAccount;
    }

    public SavingsAccountBuilder withSavingsProduct(final SavingsOfferingBO withSavingsProduct) {
        this.savingsProduct = withSavingsProduct;
        if (withSavingsProduct.getRecommendedAmntUnit() != null) {
            this.recommendedAmountUnit = RecommendedAmountUnit.fromInt(withSavingsProduct.getRecommendedAmntUnit().getId());
        }
        this.recommendedAmount = withSavingsProduct.getRecommendedAmount();
        return this;
    }

    public SavingsAccountBuilder withCustomer(final CustomerBO withCustomer) {
        this.customer = withCustomer;
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

    public SavingsAccountBuilder active() {
        this.accountState = AccountState.SAVINGS_ACTIVE;
        return this;
    }

    public SavingsAccountBuilder asInActive() {
        this.accountState = AccountState.SAVINGS_INACTIVE;
        return this;
    }

    public SavingsAccountBuilder asPendingApproval() {
        this.accountState = AccountState.SAVINGS_PENDING_APPROVAL;
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
        customer.setPersonnel(withSavingsOfficer);
        return this;
    }

    public SavingsAccountBuilder withRecommendedAmount(final Money withRecommendedAmount) {
        this.recommendedAmount = withRecommendedAmount;
        return this;
    }

    public SavingsAccountBuilder with(List<Holiday> withHolidays) {
        this.holidays = withHolidays;
        return this;
    }

    public SavingsAccountBuilder with(Holiday...holidays) {
        for (Holiday holiday : holidays) {
            this.holidays.add(holiday);
        }
        return this;
    }

    public SavingsAccountBuilder withActivationDate(DateTime withActivationDate) {
        this.activationDate = withActivationDate;
        return this;
    }

    public SavingsAccountBuilder withDepositOf(String depositAmount) {
        Money amount = TestUtils.createMoney(depositAmount);
        String receiptNumber = null;
        Date receiptDate = null;
        PaymentTypeEntity paymentType = new PaymentTypeEntity(PaymentTypes.CASH.getValue());
        Date paymentDate = new DateTime().toDate();
        AccountPaymentEntity deposit = new AccountPaymentEntity(null, amount, receiptNumber, receiptDate, paymentType, paymentDate);
        this.deposits.add(deposit);
        return this;
    }

    public SavingsAccountBuilder withDepositOn(String depositAmount, DateTime paymentDateTime) {
        Money amount = TestUtils.createMoney(depositAmount);
        String receiptNumber = null;
        Date receiptDate = null;
        PaymentTypeEntity paymentType = new PaymentTypeEntity(PaymentTypes.CASH.getValue());
        Date paymentDate = paymentDateTime.toDate();
        AccountPaymentEntity deposit = new AccountPaymentEntity(null, amount, receiptNumber, receiptDate, paymentType, paymentDate);
        this.deposits.add(deposit);
        return this;
    }

    public SavingsAccountBuilder withWithdrawalOf(String withdrawalAmount) {
        Money amount = TestUtils.createMoney(withdrawalAmount);
        String receiptNumber = null;
        Date receiptDate = null;
        PaymentTypeEntity paymentType = new PaymentTypeEntity(PaymentTypes.CASH.getValue());
        Date paymentDate = new DateTime().toDate();
        AccountPaymentEntity deposit = new AccountPaymentEntity(null, amount, receiptNumber, receiptDate, paymentType, paymentDate);
        this.withdrawals.add(deposit);
        return this;
    }

    public SavingsAccountBuilder withNextInterestPostingDateOf(DateTime nextInterestPostingDate) {
        this.nextInterestPostingDate = new LocalDate(nextInterestPostingDate);
        return this;
    }

    public SavingsAccountBuilder withCreatedBy(PersonnelBO testUser) {
        this.createdBy = testUser;
        return this;
    }

    public SavingsAccountBuilder withMember(CustomerBO withMember) {
        this.jointAccountMembers.add(withMember);
        return this;
    }
}
