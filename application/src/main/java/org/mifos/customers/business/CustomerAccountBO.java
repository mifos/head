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

package org.mifos.customers.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.business.FeesTrxnDetailEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.persistence.FeePersistence;
import org.mifos.accounts.fees.util.helpers.FeeChangeType;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.FeeInstallment;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.components.batchjobs.exceptions.BatchJobException;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.security.util.UserContext;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysScheduledDateGeneration;

/**
 * Clients, groups, and centers are stored in the db as customer accounts.
 */
public class CustomerAccountBO extends AccountBO {

    private List<CustomerActivityEntity> customerActivitDetails = new ArrayList<CustomerActivityEntity>();

    private FeePersistence feePersistence;

    @Override
    public FeePersistence getFeePersistence() {
        if (feePersistence == null) {
            feePersistence = new FeePersistence();
        }
        return feePersistence;
    }

    @Override
    public void setFeePersistence(final FeePersistence feePersistence) {
        this.feePersistence = feePersistence;
    }

    /**
     * default constructor for hibernate usage
     */
    protected CustomerAccountBO() {
        super();
    }

    /**
     * TODO - keithw - work in progress
     * 
     * minimal legal constructor
     */
    public CustomerAccountBO(final CustomerBO customer, final Set<AmountFeeBO> accountFees, final OfficeBO office,
            final PersonnelBO loanOfficer, final Date createdDate, final Short createdByUserId,
            final boolean buildForIntegrationTests) {
        super(AccountTypes.CUSTOMER_ACCOUNT, AccountState.CUSTOMER_ACCOUNT_ACTIVE, customer, Integer.valueOf(1),
                new LinkedHashSet<AccountActionDateEntity>(), new HashSet<AccountFeesEntity>(), office, loanOfficer,
                createdDate, createdByUserId);
        this.customer.addCustomerAccount(this);

        // FIXME - keithw - userContext feels redundant
        this.userContext = customer.getUserContext();

        // FIXME - keithw - ideally this setup is moved from constructor into
        // factory method
        for (AmountFeeBO amountFee : accountFees) {
            AccountFeesEntity accountFeesEntity = new AccountFeesEntity(this, amountFee, amountFee.getFeeAmount()
                    .getAmountDoubleValue());
            this.addAccountFees(accountFeesEntity);
        }

        // FIXME - keithw - generate schedule uses dao/persistence
        if (buildForIntegrationTests) {
            try {
                generateCustomerFeeSchedule(customer);
            } catch (AccountException e) {
                throw new IllegalStateException("Unable to setup customer fee schedule", e);
            }
        }
    }

    public CustomerAccountBO(final UserContext userContext, final CustomerBO customer, final List<FeeView> fees)
            throws AccountException {
        super(userContext, customer, AccountTypes.CUSTOMER_ACCOUNT, AccountState.CUSTOMER_ACCOUNT_ACTIVE);
        if (fees != null) {
            for (FeeView feeView : fees) {
                FeeBO fee = getFeePersistence().getFee(feeView.getFeeIdValue());

                this.addAccountFees(new AccountFeesEntity(this, fee, new LocalizationConverter()
                        .getDoubleValueForCurrentLocale(feeView.getAmount())));

            }
            generateCustomerFeeSchedule(customer);
        }
    }

    @Override
    public AccountTypes getType() {
        return AccountTypes.CUSTOMER_ACCOUNT;
    }

    public void generateCustomerFeeSchedule() throws AccountException {
        generateCustomerFeeSchedule(getCustomer());
    }

    public List<CustomerActivityEntity> getCustomerActivitDetails() {
        return customerActivitDetails;
    }

    @SuppressWarnings("unused")
    // see .hbm.xml file
    private void setCustomerActivitDetails(final List<CustomerActivityEntity> customerActivitDetails) {
        this.customerActivitDetails = customerActivitDetails;
    }

    public void addCustomerActivity(final CustomerActivityEntity customerActivityEntity) {
        customerActivitDetails.add(customerActivityEntity);
    }

    @Override
    protected AccountPaymentEntity makePayment(final PaymentData paymentData) throws AccountException {

        Money totalPaid = paymentData.getTotalAmount();

        if (totalPaid.isZero()) {
            throw new AccountException("errors.update",
                    new String[] { "Attempting to pay a customer account balance of zero for customer: "
                            + paymentData.getCustomer().getGlobalCustNum() });
        }

        final List<CustomerScheduleEntity> customerAccountPayments = findAllUnpaidInstallmentsUpTo(paymentData
                .getTransactionDate());

        if (customerAccountPayments.isEmpty()) {
            throw new AccountException(AccountConstants.NO_TRANSACTION_POSSIBLE, new String[] {"Trying to pay account charges before the due date."});
        }

        Money totalAllUnpaidInstallments = new Money(totalPaid.getCurrency(), "0.0");
        for (CustomerScheduleEntity customerSchedule : customerAccountPayments) {
            totalAllUnpaidInstallments = totalAllUnpaidInstallments.add(customerSchedule.getMiscFee());
            totalAllUnpaidInstallments = totalAllUnpaidInstallments.add(customerSchedule.getMiscPenalty());
            for (AccountFeesActionDetailEntity accountFeesActionDetail : customerSchedule.getAccountFeesActionDetails()) {
                CustomerFeeScheduleEntity customerFeeSchedule = (CustomerFeeScheduleEntity) accountFeesActionDetail;
                totalAllUnpaidInstallments = totalAllUnpaidInstallments.add(customerFeeSchedule.getFeeAmount());
            }
        }
        if (!totalPaid.equals(totalAllUnpaidInstallments)) {
            throw new AccountException(
                    "errors.paymentmismatch",
                    new String[] { totalPaid.toString(), totalAllUnpaidInstallments.toString() });
        }

        final AccountPaymentEntity accountPayment = new AccountPaymentEntity(this, paymentData.getTotalAmount(),
                paymentData.getReceiptNum(), paymentData.getReceiptDate(), getPaymentTypeEntity(paymentData
                        .getPaymentTypeId()), new DateTimeService().getCurrentJavaDateTime());

        for (CustomerScheduleEntity customerSchedule : customerAccountPayments) {

            customerSchedule.setPaymentStatus(PaymentStatus.PAID);
            customerSchedule.setPaymentDate(new java.sql.Date(paymentData.getTransactionDate().getTime()));
            customerSchedule.setMiscFeePaid(customerSchedule.getMiscFee());
            customerSchedule.setMiscPenaltyPaid(customerSchedule.getMiscPenalty());

            final List<FeesTrxnDetailEntity> feeTrxns = new ArrayList<FeesTrxnDetailEntity>();
            for (AccountFeesActionDetailEntity accountFeesActionDetail : customerSchedule.getAccountFeesActionDetails()) {
                CustomerFeeScheduleEntity customerFeeSchedule = (CustomerFeeScheduleEntity) accountFeesActionDetail;

                customerFeeSchedule.makePayment(customerFeeSchedule.getFeeDue());
                final FeesTrxnDetailEntity feesTrxnDetailBO = new FeesTrxnDetailEntity(null, customerFeeSchedule
                        .getAccountFee(), customerFeeSchedule.getFeeAmountPaid());
                feeTrxns.add(feesTrxnDetailBO);
            }

            Money customerScheduleAmountPaid = new Money(totalPaid.getCurrency(), "0.0");
            customerScheduleAmountPaid = customerScheduleAmountPaid.add(customerSchedule.getMiscFeePaid());
            customerScheduleAmountPaid = customerScheduleAmountPaid.add(customerSchedule.getMiscPenaltyPaid());
            
            final CustomerTrxnDetailEntity accountTrxn = new CustomerTrxnDetailEntity(accountPayment,
                    AccountActionTypes.CUSTOMER_ACCOUNT_REPAYMENT, customerSchedule.getInstallmentId(),
                    customerSchedule.getActionDate(), paymentData.getPersonnel(), paymentData.getTransactionDate(),
                    customerScheduleAmountPaid, AccountConstants.PAYMENT_RCVD, null, customerSchedule
                            .getMiscFeePaid(), customerSchedule.getMiscPenaltyPaid(), getFeePersistence());

            for (FeesTrxnDetailEntity feesTrxnDetailEntity : feeTrxns) {
                accountTrxn.addFeesTrxnDetail(feesTrxnDetailEntity);
                feesTrxnDetailEntity.setAccountTrxn(accountTrxn);
            }

            accountPayment.addAccountTrxn(accountTrxn);
        }

        addCustomerActivity(new CustomerActivityEntity(this, paymentData.getPersonnel(), paymentData.getTotalAmount(),
                AccountConstants.PAYMENT_RCVD, paymentData.getTransactionDate()));

        return accountPayment;
    }

    @Override
    public boolean isAdjustPossibleOnLastTrxn() {
        if (!getCustomer().isActive()) {
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                    "State is not active hence adjustment is not possible");
            return false;
        }
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                "Total payments on this account is  " + getAccountPayments().size());
        if (null == getLastPmnt() && getLastPmntAmnt() == 0) {

            return false;
        }

        for (AccountTrxnEntity accntTrxn : getLastPmnt().getAccountTrxns()) {
            if (accntTrxn.getAccountActionEntity().getId().equals(AccountActionTypes.CUSTOMER_ADJUSTMENT.getValue())) {
                return false;
            }
        }

        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Adjustment is not possible ");
        return true;
    }

    @Override
    protected void updateInstallmentAfterAdjustment(final List<AccountTrxnEntity> reversedTrxns)
            throws AccountException {
        if (null != reversedTrxns && reversedTrxns.size() > 0) {
            Money totalAmountAdj = new Money(getCurrency());
            for (AccountTrxnEntity accntTrxn : reversedTrxns) {
                CustomerTrxnDetailEntity custTrxn = (CustomerTrxnDetailEntity) accntTrxn;
                CustomerScheduleEntity accntActionDate = (CustomerScheduleEntity) getAccountActionDate(custTrxn
                        .getInstallmentId());
                accntActionDate.setPaymentStatus(PaymentStatus.UNPAID);
                accntActionDate.setPaymentDate(null);
                accntActionDate.setMiscFeePaid(accntActionDate.getMiscFeePaid().add(custTrxn.getMiscFeeAmount()));
                totalAmountAdj = totalAmountAdj.add(removeSign(custTrxn.getMiscFeeAmount()));
                accntActionDate.setMiscPenaltyPaid(accntActionDate.getMiscPenaltyPaid().add(
                        custTrxn.getMiscPenaltyAmount()));
                totalAmountAdj = totalAmountAdj.add(removeSign(custTrxn.getMiscPenaltyAmount()));
                if (null != accntActionDate.getAccountFeesActionDetails()
                        && accntActionDate.getAccountFeesActionDetails().size() > 0) {
                    for (AccountFeesActionDetailEntity accntFeesAction : accntActionDate.getAccountFeesActionDetails()) {
                        Money feeAmntAdjusted = custTrxn.getFeesTrxn(accntFeesAction.getAccountFee().getAccountFeeId())
                                .getFeeAmount();
                        ((CustomerFeeScheduleEntity) accntFeesAction).setFeeAmountPaid(accntFeesAction
                                .getFeeAmountPaid().add(feeAmntAdjusted));
                        totalAmountAdj = totalAmountAdj.add(removeSign(feeAmntAdjusted));
                    }
                }
            }
            addCustomerActivity(buildCustomerActivity(totalAmountAdj, AccountConstants.AMNT_ADJUSTED, userContext
                    .getId()));
        }
    }

    @Override
    public void waiveAmountDue(@SuppressWarnings("unused") final WaiveEnum chargeType) throws AccountException {
        AccountActionDateEntity accountActionDateEntity = getUpcomingInstallment();
        Money chargeWaived = ((CustomerScheduleEntity) accountActionDateEntity).waiveCharges();
        if (chargeWaived != null && chargeWaived.isGreaterThanZero()) {
            addCustomerActivity(buildCustomerActivity(chargeWaived, AccountConstants.AMNT_WAIVED, userContext.getId()));
        }
        try {
            new AccountPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    @Override
    public void waiveAmountOverDue(@SuppressWarnings("unused") final WaiveEnum chargeType) throws AccountException {
        Money chargeWaived = new Money(getCurrency());
        List<AccountActionDateEntity> accountActionDateList = getApplicableIdsForDueInstallments();
        accountActionDateList.remove(accountActionDateList.size() - 1);
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
            chargeWaived = chargeWaived.add(((CustomerScheduleEntity) accountActionDateEntity).waiveCharges());
        }
        if (chargeWaived != null && chargeWaived.isGreaterThanZero()) {
            addCustomerActivity(buildCustomerActivity(chargeWaived, AccountConstants.AMNT_WAIVED, userContext.getId()));
        }
        try {
            new AccountPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    public void applyPeriodicFees() throws AccountException {
        if (isUpcomingInstallmentUnpaid()) {
            AccountActionDateEntity accountActionDate = getDetailsOfUpcomigInstallment();
            List<AccountFeesEntity> periodicFeeList = getPeriodicFeeList();
            for (AccountFeesEntity accountFeesEntity : periodicFeeList) {
                Integer applicableDatesCount = accountFeesEntity.getApplicableDatesCount(accountActionDate
                        .getActionDate());
                if (applicableDatesCount > 0) {
                    new AccountPersistence().initialize(accountFeesEntity.getFees());
                    accountFeesEntity.setLastAppliedDate(accountActionDate.getActionDate());
                    FeeBO feesBO = getAccountFeesObject(accountFeesEntity.getFees().getFeeId());
                    Money totalAmount = ((AmountFeeBO) feesBO).getFeeAmount().multiply(
                            new Double(Integer.toString(applicableDatesCount)));
                    ((CustomerScheduleEntity) accountActionDate).applyPeriodicFees(accountFeesEntity.getFees()
                            .getFeeId(), totalAmount);
                    String description = feesBO.getFeeName() + " " + AccountConstants.FEES_APPLIED;
                    addCustomerActivity(buildCustomerActivity(totalAmount, description, null));
                    try {
                        new AccountPersistence().createOrUpdate(this);
                    } catch (PersistenceException e) {
                        throw new AccountException(e);
                    }
                }
            }
        }
    }

    private CustomerActivityEntity buildCustomerActivity(final Money amount, final String description,
            final Short personnelId) throws AccountException {
        try {
            PersonnelBO personnel = null;
            if (personnelId != null) {
                personnel = new PersonnelPersistence().getPersonnel(personnelId);
            }
            return new CustomerActivityEntity(this, personnel, amount, description, new DateTimeService()
                    .getCurrentJavaDateTime());
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    @Override
    public void updateAccountActivity(@SuppressWarnings("unused") final Money principal,
            @SuppressWarnings("unused") final Money interest, final Money fee,
            @SuppressWarnings("unused") final Money penalty, final Short personnelId, final String description)
            throws AccountException {
        addCustomerActivity(buildCustomerActivity(fee, description, personnelId));
    }

    @Override
    public final void removeFees(final Short feeId, final Short personnelId) throws AccountException {
        List<Short> installmentIds = getApplicableInstallmentIdsForRemoveFees();
        if (installmentIds != null && installmentIds.size() != 0 && isFeeActive(feeId)) {
            updateAccountActionDateEntity(installmentIds, feeId);
        }
        updateAccountFeesEntity(feeId);
        FeeBO feesBO = getAccountFeesObject(feeId);
        String description = feesBO.getFeeName() + " " + AccountConstants.FEES_REMOVED;
        updateAccountActivity(null, null, null, null, personnelId, description);
        try {
            new AccountPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }

    }

    @Override
    protected Money getDueAmount(final AccountActionDateEntity installment) {
        return ((CustomerScheduleEntity) installment).getTotalDueWithFees();
    }

    @Override
    protected void regenerateFutureInstallments(final AccountActionDateEntity nextInstallment, final List<Days> workingDays, final List<Holiday> holidays) throws AccountException {
        if (!this.getCustomer().getCustomerStatus().getId().equals(CustomerStatus.CLIENT_CLOSED.getValue())
                && !this.getCustomer().getCustomerStatus().getId().equals(GroupConstants.CLOSED)) {

            try {
                getCustomer().getCustomerMeeting().setUpdatedFlag(YesNoFlag.NO.getValue());
                getCustomer().changeUpdatedMeeting();
            } catch (CustomerException ce) {
                throw new AccountException(ce);
            }
            
            int numberOfInstallmentsToGenerate = getLastInstallmentId();

            MeetingBO meeting = getMeetingForAccount();
            DateTime startFromMeetingDate = meeting.startDateForMeetingInterval(new DateTime(nextInstallment.getActionDate()));
                        
            ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);
            ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays,
                    holidays);

            List<DateTime> meetingDates = dateGeneration.generateScheduledDates(numberOfInstallmentsToGenerate,
                    startFromMeetingDate, scheduledEvent);
            
            updateCustomerSchedule(nextInstallment.getInstallmentId(), meetingDates);
        }
    }

    private List<CustomerScheduleEntity> findAllUnpaidInstallmentsUpTo(final Date transactionDate) {

        final List<CustomerScheduleEntity> customerSchedulePayments = new ArrayList<CustomerScheduleEntity>();
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            if (accountActionDateEntity != null && !accountActionDateEntity.isPaid()
                    && !accountActionDateEntity.getActionDate().after(transactionDate)) {

                customerSchedulePayments.add((CustomerScheduleEntity) accountActionDateEntity);
            }
        }

        return customerSchedulePayments;
    }

    private void updateCustomerSchedule(final Short nextInstallmentId, final List<DateTime> meetingDates) {
        short installmentId = nextInstallmentId;
        for (int count = 0; count < meetingDates.size(); count++) {

            AccountActionDateEntity accountActionDate = getAccountActionDate(installmentId);
            if (accountActionDate != null) {
                DateTime meetingDate = meetingDates.get(count); 
                ((CustomerScheduleEntity) accountActionDate).setActionDate(new java.sql.Date(meetingDate.toDate().getTime()));
            }
            installmentId++;
        }
    }

    @Override
    protected void resetUpdatedFlag() throws AccountException {
        try {
            getCustomer().getCustomerMeeting().setUpdatedFlag(YesNoFlag.NO.getValue());
            getCustomer().changeUpdatedMeeting();
        } catch (CustomerException ce) {
            throw new AccountException(ce);
        }
    }

    public void generateNextSetOfMeetingDates(final List<Days> workingDays, final List<Holiday> orderedUpcomingHolidays) {
        
        Short lastInstallmentId = Short.valueOf("0"); 
        if (getLastInstallmentId() != null) {
            lastInstallmentId = getLastInstallmentId();
        }
            
        AccountActionDateEntity lastInstallment = getAccountActionDate(lastInstallmentId);
        MeetingBO meeting = getCustomer().getCustomerMeetingValue();
        
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);
        Date lastInstallmentDate = new Date();
        if (lastInstallment != null) {
            lastInstallmentDate = lastInstallment.getActionDate();
        }
        
        DateTime startFromDayAfterLastKnownSchedule = new DateTime(lastInstallmentDate).toDateMidnight().toDateTime().plusDays(1);
        ScheduledDateGeneration scheduleGenerationStrategy = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays, orderedUpcomingHolidays);
        List<DateTime> scheduledDates = scheduleGenerationStrategy.generateScheduledDates(10, startFromDayAfterLastKnownSchedule, scheduledEvent);
        
        int count = 1;
        for (DateTime installmentDate : scheduledDates) {
            CustomerScheduleEntity customerScheduleEntity = new CustomerScheduleEntity(this, getCustomer(), Short
                    .valueOf(String.valueOf(count + lastInstallmentId)), new java.sql.Date(installmentDate.toDate().getTime()),
                    PaymentStatus.UNPAID);
            count++;
            addAccountActionDate(customerScheduleEntity);
        }
    }

    @Override
    public Money updateAccountActionDateEntity(final List<Short> intallmentIdList, final Short feeId) {
        Money totalFeeAmount = new Money(getCurrency());
        Set<AccountActionDateEntity> accountActionDateEntitySet = this.getAccountActionDates();
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateEntitySet) {
            if (intallmentIdList.contains(accountActionDateEntity.getInstallmentId())) {
                totalFeeAmount = totalFeeAmount.add(((CustomerScheduleEntity) accountActionDateEntity)
                        .removeFees(feeId));
            }
        }
        return totalFeeAmount;
    }

    @Override
    public void applyCharge(final Short feeId, final Double charge) throws AccountException {
        if (!isCustomerValid()) {
            if (feeId.equals(Short.valueOf(AccountConstants.MISC_FEES))
                    || feeId.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
                throw new AccountException(AccountConstants.MISC_CHARGE_NOT_APPLICABLE);
            }
            addFeeToAccountFee(feeId, charge);
            FeeBO fee = getFeePersistence().getFee(feeId);
            updateCustomerActivity(feeId,
                    new Money(((AmountFeeBO) fee).getFeeAmount().getCurrency(), charge.toString()), fee.getFeeName()
                            + AccountConstants.APPLIED);
        } else {
            Money chargeAmount = new Money(getCurrency(), String.valueOf(charge));
            List<AccountActionDateEntity> dueInstallments = null;
            if (feeId.equals(Short.valueOf(AccountConstants.MISC_FEES))
                    || feeId.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
                dueInstallments = getTotalDueInstallments();
                if (dueInstallments.isEmpty()) {
                    throw new AccountException(AccountConstants.NOMOREINSTALLMENTS);
                }
                applyMiscCharge(feeId, chargeAmount, dueInstallments.get(0));
            } else {
                dueInstallments = getTotalDueInstallments();
                if (dueInstallments.isEmpty()) {
                    throw new AccountException(AccountConstants.NOMOREINSTALLMENTS);
                }
                FeeBO fee = getFeePersistence().getFee(feeId);
                if (fee.getFeeFrequency().getFeePayment() != null) {
                    applyOneTimeFee(fee, chargeAmount, dueInstallments.get(0));
                } else {
                    applyPeriodicFee(fee, chargeAmount, dueInstallments);
                }
            }
        }
    }

    public Date getUpcomingChargesDate() {
        AccountActionDateEntity nextAccountAction = getNextUnpaidDueInstallment();
        return nextAccountAction != null ? nextAccountAction.getActionDate() : new DateTimeService()
                .getCurrentJavaSqlDate();
    }

    @Override
    public Money getTotalAmountDue() {
        Money totalAmt = getTotalAmountInArrears();
        List<AccountActionDateEntity> dueActionDateList = getTotalDueInstallments();
        if (dueActionDateList.size() > 0) {
            AccountActionDateEntity nextInstallment = dueActionDateList.get(0);
            totalAmt = totalAmt.add(getDueAmount(nextInstallment));
        }
        return totalAmt;
    }

    public AccountActionDateEntity getUpcomingInstallment() {
        List<AccountActionDateEntity> dueActionDateList = getTotalDueInstallments();
        if (dueActionDateList.size() > 0) {
            return dueActionDateList.get(0);
        }
        return null;
    }

    private void addFeeToAccountFee(final Short feeId, final Double charge) {
        FeeBO fee = getFeePersistence().getFee(feeId);
        AccountFeesEntity accountFee = null;
        if (fee.isPeriodic() && !isFeeAlreadyApplied(fee) || !fee.isPeriodic()) {
            accountFee = new AccountFeesEntity(this, fee, charge, FeeStatus.ACTIVE.getValue(), new DateTimeService()
                    .getCurrentJavaDateTime(), null);
            addAccountFees(accountFee);
        } else {
            accountFee = getAccountFees(fee.getFeeId());
            accountFee.setFeeAmount(charge);
            accountFee.setFeeStatus(FeeStatus.ACTIVE);
            accountFee.setStatusChangeDate(new DateTimeService().getCurrentJavaDateTime());
        }
    }

    private void applyPeriodicFee(final FeeBO fee, final Money charge,
            final List<AccountActionDateEntity> dueInstallments) throws AccountException {
        AccountFeesEntity accountFee = getAccountFee(fee, charge.getAmountDoubleValue());
        accountFee.setAccountFeeAmount(charge);
        List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
        for (AccountActionDateEntity accountActionDateEntity : dueInstallments) {
            installmentDates.add(new InstallmentDate(accountActionDateEntity.getInstallmentId(),
                    accountActionDateEntity.getActionDate()));
        }
        List<FeeInstallment> feeInstallmentList = mergeFeeInstallments(handlePeriodic(accountFee, installmentDates));
        Money totalFeeAmountApplied = applyFeeToInstallments(feeInstallmentList, dueInstallments);
        updateCustomerActivity(fee.getFeeId(), totalFeeAmountApplied, fee.getFeeName() + AccountConstants.APPLIED);
        accountFee.setFeeStatus(FeeStatus.ACTIVE);
    }

    private void applyOneTimeFee(final FeeBO fee, final Money charge,
            final AccountActionDateEntity accountActionDateEntity) throws AccountException {
        CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
        AccountFeesEntity accountFee = new AccountFeesEntity(this, fee, charge.getAmountDoubleValue(), FeeStatus.ACTIVE
                .getValue(), new DateTimeService().getCurrentJavaDateTime(), null);
        List<AccountActionDateEntity> customerScheduleList = new ArrayList<AccountActionDateEntity>();
        customerScheduleList.add(customerScheduleEntity);
        List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
        installmentDates.add(new InstallmentDate(accountActionDateEntity.getInstallmentId(), accountActionDateEntity
                .getActionDate()));
        List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
        feeInstallmentList.add(handleOneTime(accountFee, installmentDates));
        Money totalFeeAmountApplied = applyFeeToInstallments(feeInstallmentList, customerScheduleList);
        updateCustomerActivity(fee.getFeeId(), totalFeeAmountApplied, fee.getFeeName() + AccountConstants.APPLIED);
        accountFee.setFeeStatus(FeeStatus.ACTIVE);
    }

    private void applyMiscCharge(final Short chargeType, final Money charge,
            final AccountActionDateEntity accountActionDateEntity) throws AccountException {
        CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
        customerScheduleEntity.applyMiscCharge(chargeType, charge);
        updateCustomerActivity(chargeType, charge, "");
    }

    private void updateCustomerActivity(final Short chargeType, final Money charge, final String comments)
            throws AccountException {
        try {
            PersonnelBO personnel = new PersonnelPersistence().getPersonnel(getUserContext().getId());
            CustomerActivityEntity customerActivityEntity = null;
            if (chargeType != null && chargeType.equals(Short.valueOf(AccountConstants.MISC_PENALTY))) {
                customerActivityEntity = new CustomerActivityEntity(this, personnel, charge,
                        AccountConstants.MISC_PENALTY_APPLIED, new DateTimeService().getCurrentJavaDateTime());
            } else if (chargeType != null && chargeType.equals(Short.valueOf(AccountConstants.MISC_FEES))) {
                customerActivityEntity = new CustomerActivityEntity(this, personnel, charge,
                        AccountConstants.MISC_FEES_APPLIED, new DateTimeService().getCurrentJavaDateTime());
            } else {
                customerActivityEntity = new CustomerActivityEntity(this, personnel, charge, comments,
                        new DateTimeService().getCurrentJavaDateTime());
            }
            addCustomerActivity(customerActivityEntity);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private Money applyFeeToInstallments(final List<FeeInstallment> feeInstallmentList,
            final List<AccountActionDateEntity> accountActionDateList) {
        Date lastAppliedDate = null;
        Money totalFeeAmountApplied = new Money(getCurrency());
        AccountFeesEntity accountFeesEntity = null;
        for (AccountActionDateEntity accountActionDateEntity : accountActionDateList) {
            CustomerScheduleEntity customerScheduleEntity = (CustomerScheduleEntity) accountActionDateEntity;
            for (FeeInstallment feeInstallment : feeInstallmentList) {
                if (feeInstallment.getInstallmentId().equals(customerScheduleEntity.getInstallmentId())) {
                    lastAppliedDate = customerScheduleEntity.getActionDate();
                    totalFeeAmountApplied = totalFeeAmountApplied.add(feeInstallment.getAccountFee());
                    AccountFeesActionDetailEntity accountFeesActionDetailEntity = new CustomerFeeScheduleEntity(
                            customerScheduleEntity, feeInstallment.getAccountFeesEntity().getFees(), feeInstallment
                                    .getAccountFeesEntity(), feeInstallment.getAccountFee());
                    customerScheduleEntity.addAccountFeesAction(accountFeesActionDetailEntity);
                    accountFeesEntity = feeInstallment.getAccountFeesEntity();
                }
            }
        }
        accountFeesEntity.setLastAppliedDate(lastAppliedDate);
        addAccountFees(accountFeesEntity);
        return totalFeeAmountApplied;
    }

    private boolean isCustomerValid() {
        if (getCustomer().getCustomerStatus().getId().equals(CustomerStatus.CENTER_ACTIVE.getValue())
                || getCustomer().getCustomerStatus().getId().equals(CustomerConstants.GROUP_ACTIVE_STATE)
                || getCustomer().getCustomerStatus().getId().equals(GroupConstants.HOLD)
                || getCustomer().getCustomerStatus().getId().equals(CustomerConstants.CLIENT_APPROVED)
                || getCustomer().getCustomerStatus().getId().equals(CustomerConstants.CLIENT_ONHOLD)) {
            return true;
        }
        return false;
    }

    private AccountActionDateEntity getNextUnpaidDueInstallment() {
        AccountActionDateEntity accountAction = null;
        for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
            if (!accountActionDate.isPaid()) {
                if (accountActionDate.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) >= 0) {
                    if (accountAction == null) {
                        accountAction = accountActionDate;
                    } else {
                        if (accountAction.getInstallmentId() > accountActionDate.getInstallmentId()) {
                            accountAction = accountActionDate;
                        }
                    }
                }
            }
        }
        return accountAction;
    }

    /*
     * This is currently (Jan 2010) used by jsp pages.
     */
    public Money getNextDueAmount() {
        AccountActionDateEntity accountAction = getNextUnpaidDueInstallment();
        if (accountAction != null) {
            return getDueAmount(accountAction);
        }

        return new Money(getCurrency(), "0.0");
    }

    public void generateCustomerAccountSystemId() throws CustomerException {
        try {
            if (getGlobalAccountNum() == null) {
                // FIXME - keithw - Question - why get the branchGlobalNum from
                // usercontext? why not the office globalbranchNum which is set
                // in the userContext anyway...
                this.setGlobalAccountNum(generateId(userContext.getBranchGlobalNum()));
            } else {
                throw new CustomerException(AccountExceptionConstants.IDGenerationException);
            }
        } catch (AccountException e) {
            throw new CustomerException(e);
        }
    }

    @Override
    protected final List<FeeInstallment> handlePeriodic(final AccountFeesEntity accountFees,
            final List<InstallmentDate> installmentDates) throws AccountException {
        Money accountFeeAmount = accountFees.getAccountFeeAmount();
        MeetingBO feeMeetingFrequency = accountFees.getFees().getFeeFrequency().getFeeMeetingFrequency();
        List<Date> feeDates = getFeeDates(feeMeetingFrequency, installmentDates);
        ListIterator<Date> feeDatesIterator = feeDates.listIterator();
        List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
        while (feeDatesIterator.hasNext()) {
            Date feeDate = feeDatesIterator.next();
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                    "FeeInstallmentGenerator:handlePeriodic date considered after removal.." + feeDate);
            Short installmentId = getMatchingInstallmentId(installmentDates, feeDate);
            feeInstallmentList.add(buildFeeInstallment(installmentId, accountFeeAmount, accountFees));
            break;
        }
        return feeInstallmentList;
    }

    @Override
    protected final List<FeeInstallment> handlePeriodic(final AccountFeesEntity accountFees,
            final List<InstallmentDate> installmentDates, final List<InstallmentDate> nonAdjustedInstallmentDates)
            throws AccountException {
        Money accountFeeAmount = accountFees.getAccountFeeAmount();
        MeetingBO feeMeetingFrequency = accountFees.getFees().getFeeFrequency().getFeeMeetingFrequency();
        List<Date> feeDates = getFeeDates(feeMeetingFrequency, nonAdjustedInstallmentDates);
        ListIterator<Date> feeDatesIterator = feeDates.listIterator();
        List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
        while (feeDatesIterator.hasNext()) {
            Date feeDate = feeDatesIterator.next();
            MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Handling periodic fee.." + feeDate);

            Short installmentId = getMatchingInstallmentId(installmentDates, feeDate);
            feeInstallmentList.add(buildFeeInstallment(installmentId, accountFeeAmount, accountFees));

        }
        return feeInstallmentList;
    }

    private void generateMeetingSchedule() throws AccountException {
        // generate dates that adjust for holidays
        List<InstallmentDate> installmentDates = getInstallmentDates(getCustomer().getCustomerMeeting().getMeeting(),
                (short) 10, (short) 0);
        // generate dates without adjusting for holidays
        List<InstallmentDate> nonAdjustedInstallmentDates = getInstallmentDates(getCustomer().getCustomerMeeting()
                .getMeeting(), (short) 10, (short) 0, false, true);
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                "RepamentSchedular:getRepaymentSchedule , installment dates obtained ");
        List<FeeInstallment> feeInstallmentList = mergeFeeInstallments(getFeeInstallments(installmentDates,
                nonAdjustedInstallmentDates));
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                "RepamentSchedular:getRepaymentSchedule , fee installment obtained ");
        for (InstallmentDate installmentDate : installmentDates) {
            CustomerScheduleEntity customerScheduleEntity = new CustomerScheduleEntity(this, getCustomer(),
                    installmentDate.getInstallmentId(), new java.sql.Date(installmentDate.getInstallmentDueDate()
                            .getTime()), PaymentStatus.UNPAID);
            addAccountActionDate(customerScheduleEntity);
            for (FeeInstallment feeInstallment : feeInstallmentList) {
                if (feeInstallment.getInstallmentId().equals(installmentDate.getInstallmentId())) {
                    CustomerFeeScheduleEntity customerFeeScheduleEntity = new CustomerFeeScheduleEntity(
                            customerScheduleEntity, feeInstallment.getAccountFeesEntity().getFees(), feeInstallment
                                    .getAccountFeesEntity(), feeInstallment.getAccountFee());
                    customerScheduleEntity.addAccountFeesAction(customerFeeScheduleEntity);
                }
            }
        }
        MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
                "RepamentSchedular:getRepaymentSchedule , repayment schedule generated  ");
    }

    public void updateFee(final AccountFeesEntity fee, final FeeBO feeBO) throws BatchJobException {
        boolean feeApplied = isFeeAlreadyApplied(fee, feeBO);
        if (!feeApplied) {
            // update this account fee
            try {
                if (feeBO.getFeeChangeType().equals(FeeChangeType.AMOUNT_AND_STATUS_UPDATED)) {
                    if (!feeBO.isActive()) {
                        removeFees(feeBO.getFeeId(), Short.valueOf("1"));
                        updateAccountFee(fee, feeBO);
                    } else {
                        // generate repayment schedule and enable fee
                        updateAccountFee(fee, feeBO);
                        fee.changeFeesStatus(FeeStatus.ACTIVE, new DateTimeService().getCurrentJavaDateTime());
                        addTonextInstallment(fee);
                    }

                } else if (feeBO.getFeeChangeType().equals(FeeChangeType.STATUS_UPDATED)) {
                    if (!feeBO.isActive()) {
                        removeFees(feeBO.getFeeId(), Short.valueOf("1"));

                    } else {
                        fee.changeFeesStatus(FeeStatus.ACTIVE, new DateTimeService().getCurrentJavaDateTime());
                        addTonextInstallment(fee);
                    }

                } else if (feeBO.getFeeChangeType().equals(FeeChangeType.AMOUNT_UPDATED)) {
                    updateAccountFee(fee, feeBO);
                    updateNextInstallment(fee);
                }
            } catch (PropertyNotFoundException e) {
                throw new BatchJobException(e);
            } catch (AccountException e) {
                throw new BatchJobException(e);
            }
        }
    }

    private void generateCustomerFeeSchedule(final CustomerBO customer) throws AccountException {
        if (customer.getCustomerMeeting() != null && customer.isActiveViaLevel()) {
            Date meetingStartDate = customer.getCustomerMeeting().getMeeting().getMeetingStartDate();
            if (customer.getParentCustomer() != null) {
                Date nextMeetingDate = customer.getParentCustomer().getCustomerAccount().getNextMeetingDate();
                customer.getCustomerMeeting().getMeeting().setMeetingStartDate(nextMeetingDate);
            }
            generateMeetingSchedule();
            customer.getCustomerMeeting().getMeeting().setMeetingStartDate(meetingStartDate);
        }
    }

    private boolean isFeeAlreadyApplied(final AccountFeesEntity fee, final FeeBO feeBO) {
        boolean feeApplied = false;
        if (feeBO.isOneTime()) {
            for (AccountActionDateEntity accountActionDateEntity : getPastInstallments()) {
                CustomerScheduleEntity installment = (CustomerScheduleEntity) accountActionDateEntity;
                if (installment.getAccountFeesAction(fee.getAccountFeeId()) != null) {
                    feeApplied = true;
                    break;
                }
            }
        }
        return feeApplied;

    }

    private void updateAccountFee(final AccountFeesEntity fee, final FeeBO feeBO) {
        fee.changeFeesStatus(FeeStatus.INACTIVE, new DateTimeService().getCurrentJavaDateTime());
        fee.setFeeAmount(((AmountFeeBO) feeBO).getFeeAmount().getAmountDoubleValue());
        fee.setAccountFeeAmount(((AmountFeeBO) feeBO).getFeeAmount());
    }

    private void updateNextInstallment(final AccountFeesEntity fee) {
        CustomerScheduleEntity installment = (CustomerScheduleEntity) getDetailsOfNextInstallment();
        AccountFeesActionDetailEntity accountFeesActionDetail = installment.getAccountFeesAction(fee.getAccountFeeId());
        if (accountFeesActionDetail != null) {
            ((CustomerFeeScheduleEntity) accountFeesActionDetail).setFeeAmount(fee.getAccountFeeAmount());
            accountFeesActionDetail.setUpdatedBy(Short.valueOf("1"));
            accountFeesActionDetail.setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
        }
    }

    private void addTonextInstallment(final AccountFeesEntity fee) throws AccountException {
        CustomerScheduleEntity nextInstallment = (CustomerScheduleEntity) getDetailsOfNextInstallment();
        CustomerFeeScheduleEntity accountFeesaction = new CustomerFeeScheduleEntity(nextInstallment, fee.getFees(),
                fee, fee.getAccountFeeAmount());
        accountFeesaction.setFeeAmountPaid(new Money(fee.getAccountFeeAmount().getCurrency(),"0.0"));
        nextInstallment.addAccountFeesAction(accountFeesaction);
        String description = fee.getFees().getFeeName() + " " + AccountConstants.FEES_APPLIED;
        try {
            addCustomerActivity(new CustomerActivityEntity(this, new PersonnelPersistence().getPersonnel(Short
                    .valueOf("1")), fee.getAccountFeeAmount(), description, new DateTimeService()
                    .getCurrentJavaDateTime()));
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }
    /*
     * In order to do audit logging, we need to get the name of the PaymentTypeEntity.
     * A new instance constructed with the paymentTypeId is not good enough for this,
     * we need to get the lookup value loaded so that we can resolve the name of the
     * PaymentTypeEntity.
     */
    private PaymentTypeEntity getPaymentTypeEntity(final short paymentTypeId) {
        return (PaymentTypeEntity)getFeePersistence().loadPersistentObject(PaymentTypeEntity.class, paymentTypeId);
    }    
    
    @Override
    public MeetingBO getMeetingForAccount() {
        return getCustomer().getCustomerMeetingValue();
    }
}
