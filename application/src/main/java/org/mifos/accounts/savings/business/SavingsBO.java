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

package org.mifos.accounts.savings.business;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountActionEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsTypeEntity;
import org.mifos.accounts.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.accounts.util.helpers.AccountPaymentData;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.config.AccountingRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.PersonnelPersistence;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysScheduledDateGeneration;
import org.mifos.security.util.UserContext;

public class SavingsBO extends AccountBO {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

    private Money recommendedAmount;
    private Money savingsBalance;
    private Date activationDate;
    private Money interestToBePosted;
    private Date lastIntCalcDate;
    private Date lastIntPostDate;
    private Date nextIntCalcDate;
    private Date nextIntPostDate;
    private Date interIntCalcDate;
    private Money minAmntForInt;
    private Double interestRate;
    private SavingsOfferingBO savingsOffering;
    private SavingsPerformanceEntity savingsPerformance;
    private RecommendedAmntUnitEntity recommendedAmntUnit;
    private SavingsTypeEntity savingsType;
    private InterestCalcTypeEntity interestCalcType;
    private MeetingBO timePerForInstcalc;

    private Set<SavingsActivityEntity> savingsActivityDetails = new LinkedHashSet<SavingsActivityEntity>();

    private final SavingsHelper helper = new SavingsHelper();
    private SavingsTransactionActivityHelper savingsTransactionActivityHelper = new SavingsTransactionActivityHelperImpl();
    private SavingsPaymentStrategy savingsPaymentStrategy = new SavingsPaymentStrategyImpl(
            savingsTransactionActivityHelper);
    private SavingsPersistence savingsPersistence = null;

    public SavingsPersistence getSavingsPersistence() {
        if (null == savingsPersistence) {
            savingsPersistence = new SavingsPersistence();
        }
        return savingsPersistence;
    }

    public void setSavingsPersistence(final SavingsPersistence savingsPersistence) {
        this.savingsPersistence = savingsPersistence;
    }

    private PersonnelPersistence personnelPersistence = null;

    @Override
    public PersonnelPersistence getPersonnelPersistence() {
        if (null == personnelPersistence) {
            personnelPersistence = new PersonnelPersistence();
        }
        return personnelPersistence;
    }

    @Override
    public void setPersonnelPersistence(final PersonnelPersistence personnelPersistence) {
        this.personnelPersistence = personnelPersistence;
    }

    private CustomerPersistence customerPersistence;

    @Override
    public CustomerPersistence getCustomerPersistence() {
        if (null == customerPersistence) {
            customerPersistence = new CustomerPersistence();
        }
        return customerPersistence;
    }

    @Override
    public void setCustomerPersistence(final CustomerPersistence customerPersistence) {
        this.customerPersistence = customerPersistence;
    }

    /**
     * default constructor for hibernate usage
     */
    public SavingsBO() {
        // default constructor for hibernate
    }

    /**
     * TODO - keithw - work in progress
     *
     * minimal constructor for builder
     */
    public SavingsBO(final SavingsOfferingBO savingsProduct, final SavingsType savingsType,
            final Money savingsBalanceAmount, final SavingsPaymentStrategy savingsPaymentStrategy,
            final SavingsTransactionActivityHelper savingsTransactionActivityHelper,
            final Set<AccountActionDateEntity> scheduledPayments, final Double interestRate,
            final InterestCalcType interestCalcType, final AccountTypes accountType, final AccountState accountState,
            final CustomerBO customer, final Integer offsettingAllowable,
            final MeetingBO scheduleForInterestCalculation, final RecommendedAmountUnit recommendedAmountUnit,
            final Money recommendedAmount, final PersonnelBO savingsOfficer, final Date createdDate,
            final Short createdByUserId) {
        super(accountType, accountState, customer, offsettingAllowable, scheduledPayments,
                new HashSet<AccountFeesEntity>(), null, savingsOfficer, createdDate, createdByUserId);
        this.savingsOffering = savingsProduct;
        this.savingsPaymentStrategy = savingsPaymentStrategy;
        this.savingsTransactionActivityHelper = savingsTransactionActivityHelper;
        this.savingsType = new SavingsTypeEntity(savingsType);
        this.savingsBalance = savingsBalanceAmount;
        this.interestRate = interestRate;
        this.interestCalcType = new InterestCalcTypeEntity(interestCalcType);
        this.timePerForInstcalc = scheduleForInterestCalculation;
        this.recommendedAmntUnit = new RecommendedAmntUnitEntity(recommendedAmountUnit);
        this.recommendedAmount = recommendedAmount;

        // FIXME - keithw - usercontext seems redundant
        this.userContext = new UserContext();
        this.userContext.setId(createdByUserId);

        setSavingsPerformance(createSavingsPerformance());
        try {
            List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
            List<Holiday> holidays = new ArrayList<Holiday>();
            setValuesForActiveState(workingDays, holidays);
        } catch (AccountException e) {
            throw new IllegalStateException("Unable to create savings schedules", e);
        }
    }

    public SavingsBO(final UserContext userContext, final SavingsOfferingBO savingsOffering, final CustomerBO customer,
            final AccountState accountState, final Money recommendedAmount, final List<CustomFieldView> customFields)
            throws AccountException {
        super(userContext, customer, AccountTypes.SAVINGS_ACCOUNT, accountState);
        this.setSavingsOffering(savingsOffering);

        setSavingsPerformance(createSavingsPerformance());
        setSavingsBalance(new Money(getCurrency()));
        this.setSavingsType(savingsOffering.getSavingsType());
        this.setRecommendedAmntUnit(savingsOffering.getRecommendedAmntUnit());
        addcustomFields(customFields);
        this.recommendedAmount = recommendedAmount;
        this.setSavingsOfferingDetails();
        // generated the deposit action dates only if savings account is being
        // saved in approved state
        if (isActive()) {
            // FIXME - keithw - pass in this info to method
            List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
            List<Holiday> holidays = new ArrayList<Holiday>();
            setValuesForActiveState(workingDays, holidays);
        }
    }

    public void populateInstanceForTest(final SavingsOfferingBO savingsOffering) {
        this.savingsOffering = savingsOffering;
    }

    public Money getRecommendedAmount() {
        return recommendedAmount;
    }

    public void setRecommendedAmount(final Money recommendedAmount) {
        this.recommendedAmount = recommendedAmount;
    }

    public Money getSavingsBalance() {
        return savingsBalance;
    }

    void setSavingsBalance(final Money savingsBalance) {
        this.savingsBalance = savingsBalance;
    }

    public SavingsOfferingBO getSavingsOffering() {
        return savingsOffering;
    }

    void setSavingsOffering(final SavingsOfferingBO savingsOffering) {
        this.savingsOffering = savingsOffering;
    }

    public SavingsPerformanceEntity getSavingsPerformance() {
        return savingsPerformance;
    }

    public Date getActivationDate() {
        return activationDate;
    }

    void setActivationDate(final Date activationDate) {
        this.activationDate = activationDate;
    }

    /**
     * Most callers will want to call {@link #getRecommendedAmountUnit()}.
     */
    public RecommendedAmntUnitEntity getRecommendedAmntUnit() {
        return recommendedAmntUnit;
    }

    void setRecommendedAmntUnit(final RecommendedAmntUnitEntity recommendedAmntUnit) {
        this.recommendedAmntUnit = recommendedAmntUnit;
    }

    public RecommendedAmountUnit getRecommendedAmountUnit() {
        return RecommendedAmountUnit.fromInt(recommendedAmntUnit.getId());
    }

    public void setRecommendedAmountUnit(final RecommendedAmountUnit unit) {
        this.recommendedAmntUnit = new RecommendedAmntUnitEntity(unit);
    }

    public SavingsTypeEntity getSavingsType() {
        return savingsType;
    }

    void setSavingsType(final SavingsTypeEntity savingsType) {
        this.savingsType = savingsType;
    }

    public Money getInterestToBePosted() {
        return interestToBePosted;
    }

    void setInterestToBePosted(final Money interestToBePosted) {
        this.interestToBePosted = interestToBePosted;
    }

    public Date getInterIntCalcDate() {
        return interIntCalcDate;
    }

    void setInterIntCalcDate(final Date interIntCalcDate) {
        this.interIntCalcDate = interIntCalcDate;
    }

    public Date getLastIntCalcDate() {
        return lastIntCalcDate;
    }

    void setLastIntCalcDate(final Date lastIntCalcDate) {
        this.lastIntCalcDate = lastIntCalcDate;
    }

    public Date getLastIntPostDate() {
        return lastIntPostDate;
    }

    void setLastIntPostDate(final Date lastIntPostDate) {
        this.lastIntPostDate = lastIntPostDate;
    }

    public Date getNextIntCalcDate() {
        return nextIntCalcDate;
    }

    void setNextIntCalcDate(final Date nextIntCalcDate) {
        this.nextIntCalcDate = nextIntCalcDate;
    }

    public Date getNextIntPostDate() {
        return nextIntPostDate;
    }

    void setNextIntPostDate(final Date nextIntPostDate) {
        this.nextIntPostDate = nextIntPostDate;
    }

    public InterestCalcTypeEntity getInterestCalcType() {
        return interestCalcType;
    }

    public Double getInterestRate() {
        return interestRate;
    }

    public Money getMinAmntForInt() {
        return minAmntForInt;
    }

    public MeetingBO getTimePerForInstcalc() {
        return timePerForInstcalc;
    }

    private void setInterestCalcType(final InterestCalcTypeEntity interestCalcType) {
        this.interestCalcType = interestCalcType;
    }

    private void setInterestRate(final Double interestRate) {
        this.interestRate = interestRate;
    }

    private void setMinAmntForInt(final Money minAmntForInt) {
        this.minAmntForInt = minAmntForInt;
    }

    private void setTimePerForInstcalc(final MeetingBO timePerForInstcalc) {
        this.timePerForInstcalc = timePerForInstcalc;
    }

    public Set<SavingsActivityEntity> getSavingsActivityDetails() {
        return savingsActivityDetails;
    }

    public void setSavingsActivityDetails(final Set<SavingsActivityEntity> savingsActivityDetails) {
        this.savingsActivityDetails = savingsActivityDetails;
    }

    public void addSavingsActivityDetails(final SavingsActivityEntity savingsActivity) {
        savingsActivityDetails.add(savingsActivity);
    }

    @Override
    public AccountTypes getType() {
        return AccountTypes.SAVINGS_ACCOUNT;
    }

    @Override
    public boolean isOpen() {
        return !(getAccountState().getId().equals(AccountState.SAVINGS_CANCELLED.getValue()) || getAccountState()
                .getId().equals(AccountState.SAVINGS_CLOSED.getValue()));
    }

    private void setSavingsOfferingDetails() throws AccountException {
        setMinAmntForInt(getSavingsOffering().getMinAmntForInt());
        setInterestRate(getSavingsOffering().getInterestRate());
        setInterestCalcType(getSavingsOffering().getInterestCalcType());
        try {
            setTimePerForInstcalc(getMeeting(getSavingsOffering().getTimePerForInstcalc().getMeeting()));
        } catch (ProductDefinitionException e) {
            throw new AccountException(e);
        } catch (MeetingException me) {
            throw new AccountException(me);
        }
        // setFreqOfPostIntcalc(getMeeting(getSavingsOffering().getFreqOfPostIntcalc().getMeeting()));
    }

    private MeetingBO getMeeting(final MeetingBO offeringMeeting) throws MeetingException {
        RecurrenceType recurrenceType = offeringMeeting.getMeetingDetails().getRecurrenceTypeEnum();
        MeetingType meetingType = MeetingType.fromInt(offeringMeeting.getMeetingType().getMeetingTypeId());
        return new MeetingBO(recurrenceType, offeringMeeting.getMeetingDetails().getRecurAfter(), helper
                .getFiscalStartDate(), meetingType);
    }

    private void setSavingsPerformance(final SavingsPerformanceEntity savingsPerformance) {
        this.savingsPerformance = savingsPerformance;
    }

    private SavingsPerformanceEntity createSavingsPerformance() {
        SavingsPerformanceEntity savingsPerformance = new SavingsPerformanceEntity(this);
        logger.info("In SavingsBO::createSavingsPerformance(), SavingsPerformanceEntity created successfully ");
        return savingsPerformance;
    }

    public void save() throws AccountException {
        logger.info("In SavingsBO::save(), Before Saving , accountId: " + getAccountId());

        try {
            this.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(this.getAccountState(), this
                    .getAccountState(), getPersonnelPersistence().getPersonnel(userContext.getId()), this));

            getSavingsPersistence().createOrUpdate(this);

            this.globalAccountNum = generateId(userContext.getBranchGlobalNum());

            getSavingsPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        logger.info("In SavingsBO::save(), Successfully saved , accountId: " + getAccountId());
    }

    public void update(final Money recommendedAmount, final List<CustomFieldView> customFields) throws AccountException {
        logger.debug("In SavingsBO::update(), accountId: " + getAccountId());
        this.setUpdatedBy(userContext.getId());
        this.setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
        if (isDepositScheduleBeRegenerated()) {
            if (this.recommendedAmount != null && recommendedAmount != null
                    && !this.recommendedAmount.equals(recommendedAmount)) {
                for (AccountActionDateEntity accountDate : this.getAccountActionDates()) {
                    if (accountDate.getActionDate().compareTo(new DateTimeService().getCurrentDateMidnight().toDate()) >= 0) {
                        ((SavingsScheduleEntity) accountDate).setDeposit(recommendedAmount);
                    }
                }
            }
        }
        this.recommendedAmount = recommendedAmount;
        if (this.getAccountCustomFields() != null && customFields != null) {
            for (CustomFieldView view : customFields) {
                boolean fieldPresent = false;
                for (AccountCustomFieldEntity customFieldEntity : this.getAccountCustomFields()) {
                    if (customFieldEntity.getFieldId().equals(view.getFieldId())) {
                        fieldPresent = true;
                        customFieldEntity.setFieldValue(view.getFieldValue());
                    }
                }
                if (!fieldPresent) {
                    this.getAccountCustomFields().add(
                            new AccountCustomFieldEntity(this, view.getFieldId(), view.getFieldValue()));
                }
            }
        }

        try {
            getSavingsPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        logger.info("In SavingsBO::update(), successfully updated , accountId: " + getAccountId());
    }

    public boolean isMandatory() {
        logger.debug("In SavingsBO::isMandatory(), savingTypeId: " + getSavingsType().getId());
        return getSavingsType().getId().equals(SavingsType.MANDATORY.getValue());
    }

    public boolean isDepositScheduleBeRegenerated() {
        logger.debug("In SavingsBO::isDepositScheduleBeRegenerated(), accountStateId: " + getAccountState().getId());
        return getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_APPROVED
                || getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_INACTIVE;
    }

    public boolean isActive() {
        return getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_APPROVED;
    }

    public Money calculateInterestForClosure(final Date closureDate) throws AccountException {
        Money interestCalculated = calculateInterest(null, closureDate, getInterestRate(), null);
        return getInterestToBePosted() == null ? interestCalculated : getInterestToBePosted().add(interestCalculated);
    }

    public void postInterest() throws AccountException {
        if (getInterestToBePosted() != null && getInterestToBePosted().isGreaterThanOrEqualZero()) {
            Money interestPosted = getInterestToBePosted();
            setSavingsBalance(getSavingsBalance().add(interestPosted));
            setInterestToBePosted(new Money(getCurrency()));
            setLastIntPostDate(getNextIntPostDate());
            Date currentIntPostingDate = getNextIntPostDate();
            try {
                setNextIntPostDate(helper.getNextScheduleDate(getActivationDate(), getLastIntPostDate(),
                        getMeeting(getSavingsOffering().getFreqOfPostIntcalc().getMeeting())));
            } catch (ProductDefinitionException e) {
                throw new AccountException(e);
            } catch (MeetingException me) {
                throw new AccountException(me);
            }
            PaymentTypeEntity paymentType = null;
            try {
                paymentType = (PaymentTypeEntity) getSavingsPersistence().getPersistentObject(PaymentTypeEntity.class,
                        SavingsConstants.DEFAULT_PAYMENT_TYPE);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
            makeEntriesForInterestPosting(interestPosted, paymentType, getCustomer(), currentIntPostingDate, null);
            getSavingsPerformance().setTotalInterestDetails(interestPosted);
            try {
                getSavingsPersistence().createOrUpdate(this);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
            logger.info("In SavingsBO::postInterest(), accountId: " + getAccountId() + ", Interest Of Amount: "
                    + interestPosted + " successfully");
        }
    }

    public void updateInterestAccrued() throws AccountException {
        if (getInterestToBePosted() == null) {
            setInterestToBePosted(new Money(getCurrency()));
        }
        Money interestCalculated = calculateInterest(null, getNextIntCalcDate(), getInterestRate(), null);
        setInterestToBePosted(getInterestToBePosted().add(interestCalculated));
        setLastIntCalcDate(getNextIntCalcDate());
        try {
            setNextIntCalcDate(helper.getNextScheduleDate(getActivationDate(), getLastIntCalcDate(),
                    getTimePerForInstcalc()));

        } catch (MeetingException me) {
            throw new AccountException(me);
        }
        try {
            getSavingsPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        logger.info("In SavingsBO::updateInterestAccrued(), accountId: " + getAccountId() + ", Interest Amount: "
                + interestCalculated + " calculated.");
    }

    private Money calculateInterest(Date fromDate, final Date toDate, final double interestRate,
            final SavingsTrxnDetailEntity adjustedTrxn) throws AccountException {
        boolean initialBalance = false;
        if (fromDate == null) {
            fromDate = getFromDate();
        }

        SavingsTrxnDetailEntity trxn = null;
        try {

            if (adjustedTrxn != null && fromDate.compareTo(adjustedTrxn.getActionDate()) >= 0) {
                trxn = adjustedTrxn;
            } else if (getActivationDate().equals(fromDate)) {
                trxn = getSavingsPersistence().retrieveFirstTransaction(getAccountId());
                initialBalance = true;
                if (trxn != null) {
                    fromDate = trxn.getActionDate();
                }
            } else {
                trxn = getSavingsPersistence().retrieveLastTransaction(getAccountId(), fromDate);
                if (trxn == null || trxn.getAccountAction().equals(AccountActionTypes.SAVINGS_INTEREST_POSTING)) {
                    trxn = getSavingsPersistence().retrieveFirstTransaction(getAccountId());
                    initialBalance = true;
                }

                if (trxn == null || trxn.getAccountAction().equals(AccountActionTypes.SAVINGS_ADJUSTMENT)) {
                    // trxn = (new
                    // SavingsPersistence()).retrieveLastTransactionAtAdjustment(getAccountId(),
                    // fromDate);

                    final List<AccountTrxnEntity> transactions = this.getAccountTrxnsOrderByTrxnCreationDate();

                    if (transactions != null && !transactions.isEmpty()) {
                        trxn = (SavingsTrxnDetailEntity) transactions.get(transactions.size() - 1);
                        initialBalance = true;
                    }
                }
            }
        } catch (PersistenceException pe) {
            throw new AccountException(pe);
        }
        Money principal = null;
        Money interestAmount = new Money(getCurrency());

        if (trxn != null) {
            SavingsTrxnDetailEntity savedTrxn = trxn;
            trxn = trxn.getAccountPayment().getAmount().isGreaterThanZero() ? getLastTrxnForPayment(trxn
                    .getAccountPayment()) : getLastTrxnForAdjustedPayment(trxn.getAccountPayment());
            if (!savedTrxn.getAccountTrxnId().equals(trxn.getAccountTrxnId())) {
                initialBalance = false;
            }
            if (getInterestCalcType().getId().equals(InterestCalcType.MINIMUM_BALANCE.getValue())) {
                principal = getMinimumBalance(fromDate, toDate, trxn, adjustedTrxn, initialBalance);
            } else if (getInterestCalcType().getId().equals(InterestCalcType.AVERAGE_BALANCE.getValue())) {
                principal = getAverageBalance(fromDate, toDate, trxn, adjustedTrxn);
            }
            // Do not Calculate interest if principal amount is less than the
            // minimum amount needed for interest calculation
            if (getMinAmntForInt() != null && principal != null
                    && (getMinAmntForInt().isZero() || principal.isGreaterThanOrEqual(getMinAmntForInt()))) {
                interestAmount = calculateInterestForDays(principal, interestRate, fromDate, toDate);
            }
        }
        logger.info("In SavingsBO::calculateInterest(), accountId: " + getAccountId() + ", from date:" + fromDate
                + ", toDate:" + toDate + "InterestAmt: " + interestAmount);

        return interestAmount;
    }

    private Date getFromDate() {
        return getInterIntCalcDate() != null ? getInterIntCalcDate()
                : getLastIntCalcDate() != null ? getLastIntCalcDate() : getActivationDate();
    }

    public void closeAccount(final AccountPaymentEntity payment, final AccountNotesEntity notes,
            final CustomerBO customer) throws AccountException {
        logger.debug("In SavingsBO::closeAccount(), accountId: " + getAccountId());
        try {
            PersonnelBO loggedInUser = getPersonnelPersistence().getPersonnel(userContext.getId());
            AccountStateEntity accountState = this.getAccountState();

            this.setAccountState(getSavingsPersistence().getAccountStatusObject(AccountStates.SAVINGS_ACC_CLOSED));

            setInterestToBePosted(payment.getAmount().subtract(getSavingsBalance()));
            if (getInterestToBePosted() != null && getInterestToBePosted().isGreaterThanZero()) {
                makeEntriesForInterestPosting(getInterestToBePosted(), payment.getPaymentType(), customer, payment
                        .getPaymentDate(), loggedInUser);
            }
            Date transactionDate = new DateTimeService().getCurrentDateMidnight().toDate();
            if (payment.getAmount().isGreaterThanZero()) {

                payment.addAccountTrxn(helper.createAccountPaymentTrxn(payment, new Money(getCurrency()),
                        AccountActionTypes.SAVINGS_WITHDRAWAL, customer, loggedInUser, getSavingsPersistence(),
                        transactionDate));

                payment.setCreatedDate(transactionDate);
                this.addAccountPayment(payment);
                addSavingsActivityDetails(buildSavingsActivity(payment.getAmount(), new Money(getCurrency()),
                        AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), payment.getPaymentDate(), loggedInUser));
                getSavingsPerformance().setTotalWithdrawals(
                        getSavingsPerformance().getTotalWithdrawals().add(payment.getAmount()));
                buildFinancialEntries(payment.getAccountTrxns());
            }
            this.addAccountNotes(notes);
            this.setLastIntCalcDate(transactionDate);
            this.setLastIntPostDate(transactionDate);
            this.setInterIntCalcDate(null);
            this.setSavingsBalance(new Money(getCurrency()));
            this.setInterestToBePosted(new Money(getCurrency()));
            this.setClosedDate(new DateTimeService().getCurrentJavaDateTime());
            this.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(accountState, this
                    .getAccountState(), loggedInUser, this));
            this.update();
            logger.debug("In SavingsBO::close(), account closed successfully ; accountId: " + getAccountId());

        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private void makeEntriesForInterestPosting(final Money interestAmt, final PaymentTypeEntity paymentType,
            final CustomerBO customer, final Date postingDate, final PersonnelBO loggedInUser) throws AccountException {
        AccountPaymentEntity interestPayment = helper.createAccountPayment(this, interestAmt, paymentType,
                loggedInUser, postingDate);

        interestPayment.addAccountTrxn(helper.createAccountPaymentTrxn(interestPayment, getSavingsBalance(),
                AccountActionTypes.SAVINGS_INTEREST_POSTING, customer, loggedInUser, getSavingsPersistence(),
                postingDate));

        this.addAccountPayment(interestPayment);
        if (userContext == null) {
            addSavingsActivityDetails(buildSavingsActivity(interestAmt, getSavingsBalance(),
                    AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue(), postingDate, null));
        } else {
            addSavingsActivityDetails(buildSavingsActivity(interestAmt, getSavingsBalance(),
                    AccountActionTypes.SAVINGS_INTEREST_POSTING.getValue(), postingDate, loggedInUser));
        }
        buildFinancialEntries(interestPayment.getAccountTrxns());
    }

    private int getLastTrxnIndexForDay(final List<AccountTrxnEntity> accountTrxnList, int i) {
        AccountTrxnEntity accountTrxn;
        do {
            accountTrxn = accountTrxnList.get(i);
            i++;
        } while (i < accountTrxnList.size()
                && accountTrxn.getActionDate().equals(accountTrxnList.get(i).getActionDate()));
        return i - 1;
    }

    private Money getMinimumBalanceForAdjustment(final SavingsTrxnDetailEntity initialTrxn,
            final List<AccountTrxnEntity> accountTrxnList) {
        int i = 0;
        for (AccountTrxnEntity trxn : accountTrxnList) {
            if (trxn.getAccountTrxnId().equals(initialTrxn.getAccountTrxnId())) {
                break;
            }
        }
        return getLastTrxnBalance(accountTrxnList, i);
    }

    protected Money getMinimumBalance(final Date fromDate, final Date toDate,
            final SavingsTrxnDetailEntity initialTrxn, final SavingsTrxnDetailEntity adjustedTrxn,
            final boolean initialBalance) {
        logger.debug("In SavingsBO::getMinimumBalance(), accountId: " + getAccountId());
        Money minBal = null;
        List<AccountTrxnEntity> accountTrxnList = getAccountTrxnsOrderByTrxnDate();
        if (initialTrxn.getAccountAction().equals(AccountActionTypes.SAVINGS_ADJUSTMENT)) {
            minBal = getMinimumBalanceForAdjustment(initialTrxn, accountTrxnList);
        } else {
            minBal = initialTrxn.getBalance();
        }

        if (adjustedTrxn != null && getLastPmnt().getPaymentId().equals(initialTrxn.getAccountPayment().getPaymentId())) {
            minBal = adjustedTrxn.getBalance();
        }

        for (int i = 0; i < accountTrxnList.size(); i++) {
            AccountTrxnEntity currentTrxn = accountTrxnList.get(i);
            AccountActionTypes actionType = currentTrxn.getAccountAction();
            Date actionDate = currentTrxn.getActionDate();
            if (actionType.equals(AccountActionTypes.SAVINGS_INTEREST_POSTING)) {
                if (initialTrxn.getActionDate().compareTo(actionDate) <= 0) {
                    minBal = minBal.add(currentTrxn.getAmount());
                    continue;
                }
            }

            if (initialBalance) {

                if (fromDate.compareTo(actionDate) > 0) {
                    if (actionType.equals(AccountActionTypes.SAVINGS_DEPOSIT)) {
                        if (!initialTrxn.getAccountTrxnId().equals(currentTrxn.getAccountTrxnId())) {
                            minBal = minBal.add(currentTrxn.getAmount());
                            continue;
                        }
                    } else if (actionType.equals(AccountActionTypes.SAVINGS_WITHDRAWAL)) {
                        minBal = minBal.subtract(currentTrxn.getAmount());
                        continue;
                    } else if (actionType.equals(AccountActionTypes.SAVINGS_ADJUSTMENT)) {
                        SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) currentTrxn;
                        if (savingsTrxn.getDepositAmount().isNonZero()) {
                            minBal = minBal.add(currentTrxn.getAmount());
                        } else if (savingsTrxn.getWithdrawlAmount().isNonZero()) {
                            minBal = minBal.subtract(currentTrxn.getAmount());
                        }
                        continue;
                    }
                    if (actionType.equals(AccountActionTypes.SAVINGS_INTEREST_POSTING)) {
                        minBal = minBal.add(currentTrxn.getAmount());
                        continue;
                    }

                }
            }
            if (actionDate.compareTo(fromDate) >= 0
                    && DateUtils.getDateWithoutTimeStamp(actionDate.getTime()).compareTo(
                            DateUtils.getDateWithoutTimeStamp(toDate.getTime())) < 0) {
                Money lastTrxnAmt = getLastTrxnBalance(accountTrxnList, i);
                i = getLastTrxnIndexForDay(accountTrxnList, i);

                SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accountTrxnList.get(i);

                if (initialTrxn.getActionDate().equals(savingsTrxn.getActionDate())
                        && initialTrxn.getAccountTrxnId().intValue() < savingsTrxn.getAccountTrxnId().intValue()
                        || minBal.isGreaterThan(lastTrxnAmt)) {
                    minBal = lastTrxnAmt;
                }

                if (adjustedTrxn != null
                        && getLastPmnt().getPaymentId().equals(savingsTrxn.getAccountPayment().getPaymentId())
                        && minBal.isGreaterThan(lastTrxnAmt)) {
                    minBal = adjustedTrxn.getBalance();
                }
            }
        }
        logger.debug("In SavingsBO::getMinimumBalance(), accountId: " + getAccountId() + "fromDate: " + fromDate
                + ", toDate: " + toDate + ", Min Bal: " + minBal);
        return minBal;
    }

    protected Money getAverageBalance(Date fromDate, final Date toDate, final SavingsTrxnDetailEntity initialTrxn,
            final SavingsTrxnDetailEntity adjustedTrxn) {
        logger.debug("In SavingsBO::getAverageBalance(), accountId: " + getAccountId());
        int noOfDays = 0;
        List<AccountTrxnEntity> accountTrxnList = getAccountTrxnsOrderByTrxnDate();
        Money initialBalance = initialTrxn.getBalance();

        if (adjustedTrxn != null && getLastPmnt().getPaymentId().equals(initialTrxn.getAccountPayment().getPaymentId())) {
            initialBalance = adjustedTrxn.getBalance();
        }

        Money totalBalance = new Money(getCurrency());
        for (int i = 0; i < accountTrxnList.size(); i++) {
            if (accountTrxnList.get(i).getActionDate().compareTo(fromDate) >= 0
                    && DateUtils.getDateWithoutTimeStamp(accountTrxnList.get(i).getActionDate().getTime()).compareTo(
                            DateUtils.getDateWithoutTimeStamp(toDate.getTime())) < 0) {

                Money lastTrxnAmt = getLastTrxnBalance(accountTrxnList, i);

                i = getLastTrxnIndexForDay(accountTrxnList, i);
                SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accountTrxnList.get(i);
                int days = helper.calculateDays(fromDate, savingsTrxn.getActionDate());
                fromDate = savingsTrxn.getActionDate();
                if (initialTrxn.getActionDate().equals(savingsTrxn.getActionDate())
                        && initialTrxn.getAccountTrxnId() < savingsTrxn.getAccountTrxnId()) {

                    initialBalance = lastTrxnAmt;
                }

                totalBalance = totalBalance.add(initialBalance.multiply(days));
                initialBalance = lastTrxnAmt;
                if (adjustedTrxn != null
                        && getLastPmnt().getPaymentId().equals(savingsTrxn.getAccountPayment().getPaymentId())) {
                    initialBalance = adjustedTrxn.getBalance();
                }
                noOfDays += days;
            }
        }
        int days = helper.calculateDays(fromDate, toDate);
        totalBalance = totalBalance.add(initialBalance.multiply(days));
        noOfDays += days;
        return noOfDays == 0 ? initialBalance : totalBalance.divide(noOfDays);
    }

    private Money getLastTrxnBalance(final List<AccountTrxnEntity> accountTrxnList, int i) {
        SavingsTrxnDetailEntity accountTrxn = null;
        SavingsTrxnDetailEntity lastTrxn = null;

        do {
            accountTrxn = (SavingsTrxnDetailEntity) accountTrxnList.get(i);
            if (lastTrxn == null
                    || accountTrxn.getAccountPayment().getPaymentId() != null
                    && lastTrxn.getAccountPayment().getPaymentId() != null
                    && accountTrxn.getAccountPayment().getPaymentId().intValue() > lastTrxn.getAccountPayment()
                            .getPaymentId().intValue()) {
                lastTrxn = accountTrxn;
            } else if (accountTrxn.getAccountPayment().getPaymentId() != null
                    && accountTrxn.getAccountPayment().getPaymentId().equals(
                            lastTrxn.getAccountPayment().getPaymentId())) {
                if (accountTrxn.getAccountActionEntity().getId().equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
                    if (lastTrxn.getBalance().isLessThan(accountTrxn.getBalance())) {
                        lastTrxn = accountTrxn;
                    }
                } else {
                    lastTrxn = accountTrxn;
                }
            }
            i++;
        } while (i < accountTrxnList.size()
                && accountTrxn.getActionDate().equals(accountTrxnList.get(i).getActionDate()));
        return lastTrxn.getBalance();
    }

    private Money calculateInterestForDays(final Money principal, final double interestRate, final Date fromDate,
            final Date toDate) {
        int days = helper.calculateDays(fromDate, toDate);
        return getInterest(principal, interestRate, days, SavingsConstants.DAYS);
    }

    private Money getInterest(final Money principal, final double interestRate, final int duration,
            final String durationType) {
        double intRate = interestRate;
        if (durationType.equals(SavingsConstants.DAYS)) {
            intRate = intRate / AccountingRules.getNumberOfInterestDays() * duration;
        } else {
            intRate = intRate / 12 * duration;
        }
        Money interestAmount = principal.multiply(new Double(1 + intRate / 100.0)).subtract(principal);

        interestAmount = MoneyUtils.currencyRound(interestAmount);
        return interestAmount;
    }

    @Deprecated
    public void generateAndUpdateDepositActionsForClient(final ClientBO client, final List<Days> workingDays,
            final List<Holiday> holidays) throws AccountException {

        if (client.getCustomerMeeting().getMeeting() != null) {
            if (!(getCustomer().getLevel() == CustomerLevel.GROUP && getRecommendedAmntUnit().getId().equals(
                    RecommendedAmountUnit.COMPLETE_GROUP.getValue()))) {
                generateDepositAccountActions(client, client.getCustomerMeeting().getMeeting(), workingDays, holidays);
                this.update();
            }
        }
    }

    private void generateDepositAccountActions(final List<Days> workingDays, final List<Holiday> holidays)
            throws AccountException {
        logger.debug("In SavingsBO::generateDepositAccountActions()");
        // deposit happens on each meeting date of the customer. If for
        // center/group with individual deposits, insert row for every client
        if (getCustomer().getCustomerMeeting() != null && getCustomer().getCustomerMeeting().getMeeting() != null) {
            MeetingBO depositSchedule = getCustomer().getCustomerMeeting().getMeeting();

            if (getCustomer().getCustomerLevel().getId().equals(CustomerLevel.CLIENT.getValue())
                    || getCustomer().getCustomerLevel().getId().equals(CustomerLevel.GROUP.getValue())
                    && getRecommendedAmntUnit().getId().equals(RecommendedAmountUnit.COMPLETE_GROUP.getValue())) {
                generateDepositAccountActions(getCustomer(), depositSchedule, workingDays, holidays);
            } else {
                List<CustomerBO> children;
                try {
                    children = getCustomer().getChildren(CustomerLevel.CLIENT, ChildrenStateType.ACTIVE_AND_ONHOLD);
                } catch (CustomerException ce) {
                    throw new AccountException(ce);
                }
                for (CustomerBO customer : children) {
                    generateDepositAccountActions(customer, depositSchedule, workingDays, holidays);
                }
            }
        }
    }

    @Deprecated
    public void generateDepositAccountActions(final CustomerBO customer, final MeetingBO meeting,
            final List<Days> workingDays, final List<Holiday> holidays) {

        DateTime startingFromtoday = new DateTime().toDateMidnight().toDateTime();

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);
        ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays, holidays);

        List<DateTime> depositDates = dateGeneration.generateScheduledDates(10, startingFromtoday, scheduledEvent);

        short installmentNumber = 1;
        for (DateTime date : depositDates) {
            AccountActionDateEntity actionDate = helper.createActionDateObject(this, customer, installmentNumber++,
                    date.toDate(), userContext.getId(), getRecommendedAmount());
            addAccountActionDate(actionDate);
            logger.debug("In SavingsBO::generateDepositAccountActions(), Successfully added account action on date: "
                    + date);
        }
    }

    private void generateDepositAccountActions(final CustomerBO customer, final MeetingBO meeting,
            final AccountActionDateEntity lastInstallment, final List<Days> workingDays, final List<Holiday> holidays) {

        DateTime startFromDayAfterLastKnownInstallmentDate = new DateTime(lastInstallment.getActionDate()).plusDays(1);

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);
        ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays, holidays);
        List<DateTime> depositDates = dateGeneration.generateScheduledDates(10,
                startFromDayAfterLastKnownInstallmentDate, scheduledEvent);

        short installmentNumber = lastInstallment.getInstallmentId();
        for (DateTime depositDate : depositDates) {
            AccountActionDateEntity actionDate = helper.createActionDateObject(this, customer, ++installmentNumber,
                    depositDate.toDate(), (short) 1, getRecommendedAmount());

            addAccountActionDate(actionDate);
            logger.debug("In SavingsBO::generateDepositAccountActions(), Successfully added account action on date: "
                    + depositDate);
        }
    }

    /**
     * @deprecated for deposits use {@link SavingsBO#deposit(AccountPaymentEntity, Integer)} and withdrawals use
     *             {@link SavingsBO#withdraw(AccountPaymentEntity)}
     */
    @Deprecated
    @Override
    protected AccountPaymentEntity makePayment(final PaymentData paymentData) throws AccountException {
        Money totalAmount = paymentData.getTotalAmount();
        Money enteredAmount = totalAmount;
        Date transactionDate = paymentData.getTransactionDate();
        List<AccountPaymentData> accountPayments = paymentData.getAccountPayments();

        CustomerBO customer = paymentData.getCustomer();
        AccountPaymentEntity accountPayment = new AccountPaymentEntity(this, totalAmount, paymentData.getReceiptNum(),
                paymentData.getReceiptDate(), getPaymentTypeEntity(paymentData.getPaymentTypeId()), transactionDate);
        if (this.getAccountState().getId().equals(AccountStates.SAVINGS_ACC_INACTIVE)) {
            try {
                this
                        .setAccountState(getSavingsPersistence().getAccountStatusObject(
                                AccountStates.SAVINGS_ACC_APPROVED));
            } catch (PersistenceException pe) {
                throw new AccountException(pe);
            }
        }
        if (totalAmount.isGreaterThanZero() && paymentData.getAccountPayments().size() <= 0) {
            SavingsTrxnDetailEntity accountTrxn = buildUnscheduledDeposit(accountPayment, totalAmount, paymentData
                    .getPersonnel(), customer, transactionDate);
            accountPayment.addAccountTrxn(accountTrxn);
            addSavingsActivityDetails(buildSavingsActivity(totalAmount, getSavingsBalance(),
                    AccountActionTypes.SAVINGS_DEPOSIT.getValue(), transactionDate, paymentData.getPersonnel()));
            return accountPayment;
        }

        for (AccountPaymentData accountPaymentData : accountPayments) {
            SavingsScheduleEntity accountAction = (SavingsScheduleEntity) getAccountActionDate(accountPaymentData
                    .getInstallmentId(), customer.getCustomerId());
            if (accountAction != null && depositAmountIsInExcess(enteredAmount)) {
                if (accountAction.isPaid()) {
                    throw new AccountException("errors.update", new String[] { getGlobalAccountNum() });
                }
                Money depositAmount = new Money(getCurrency());
                PaymentStatus paymentStatus = PaymentStatus.UNPAID;
                if (enteredAmount.isGreaterThanOrEqual(accountAction.getTotalDepositDue())) {
                    depositAmount = accountAction.getTotalDepositDue();
                    enteredAmount = enteredAmount.subtract(accountAction.getTotalDepositDue());
                    paymentStatus = PaymentStatus.PAID;
                } else {
                    depositAmount = enteredAmount;
                    enteredAmount = new Money(getCurrency());
                }
                if (getSavingsType().getId().equals(SavingsType.VOLUNTARY.getValue())
                        && depositAmountIsInExcess(depositAmount)) {
                    paymentStatus = PaymentStatus.PAID;
                }
                savingsBalance = savingsBalance.add(depositAmount);

                savingsPerformance.setPaymentDetails(depositAmount);
                accountAction.setPaymentDetails(depositAmount, paymentStatus, new java.sql.Date(transactionDate
                        .getTime()));

                SavingsTrxnDetailEntity accountTrxn;

                accountTrxn = new SavingsTrxnDetailEntity(accountPayment, customer, AccountActionTypes.SAVINGS_DEPOSIT,
                        depositAmount, getSavingsBalance(), paymentData.getPersonnel(), accountAction.getActionDate(),
                        paymentData.getTransactionDate(), accountAction.getInstallmentId(), "", getSavingsPersistence());

                accountPayment.addAccountTrxn(accountTrxn);
            }
        }

        if (depositAmountIsInExcess(enteredAmount)) {
            SavingsTrxnDetailEntity accountTrxn = buildUnscheduledDeposit(accountPayment, enteredAmount, paymentData
                    .getPersonnel(), customer, transactionDate);
            accountPayment.addAccountTrxn(accountTrxn);
        }
        addSavingsActivityDetails(buildSavingsActivity(totalAmount, getSavingsBalance(),
                AccountActionTypes.SAVINGS_DEPOSIT.getValue(), transactionDate, paymentData.getPersonnel()));
        return accountPayment;
    }

    private SavingsTrxnDetailEntity buildUnscheduledDeposit(final AccountPaymentEntity accountPayment,
            final Money depositAmount, final PersonnelBO personnel, final CustomerBO customer,
            final Date transactionDate) throws AccountException {
        SavingsTrxnDetailEntity accountTrxn;
        savingsBalance = savingsBalance.add(depositAmount);
        savingsPerformance.setPaymentDetails(depositAmount);

        accountTrxn = new SavingsTrxnDetailEntity(accountPayment, customer, AccountActionTypes.SAVINGS_DEPOSIT,
                depositAmount, getSavingsBalance(), personnel, transactionDate, transactionDate, null, "",
                getSavingsPersistence());

        return accountTrxn;
    }

    public void deposit(final AccountPaymentEntity payment, final CustomerBO payingCustomer) throws AccountException {

        final Money amountToDeposit = payment.getAmount();
        if (amountToDeposit.isLessThanOrEqualZero()) {
            return;
        }

        final Money savingsBalanceBeforeDeposit = new Money(getCurrency(), this.savingsBalance.getAmount());

        savingsBalance = savingsBalance.add(amountToDeposit);
        savingsPerformance.setPaymentDetails(amountToDeposit);

        final Date transactionDate = payment.getPaymentDate();
        // FIXME - keithw - question - should transactionDate really be todays
        // date?
        final SavingsActivityEntity savingsActivity = savingsTransactionActivityHelper.createSavingsActivityForDeposit(
                payment.getCreatedByUser(), amountToDeposit, this.savingsBalance, transactionDate, this);

        addSavingsActivityDetails(savingsActivity);
        addAccountPayment(payment);
        setAccountState(new AccountStateEntity(AccountState.SAVINGS_ACTIVE));

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = findAllUnpaidInstallmentsForPayingCustomerUpTo(
                transactionDate, payingCustomer.getCustomerId());

        // make scheduled payments (if any) and make an unscheduled payment
        // with any amount remaining
        final Money amountRemaining = this.savingsPaymentStrategy.makeScheduledPayments(payment,
                unpaidDepositsForPayingCustomer, payingCustomer, SavingsType.fromInt(this.savingsType.getId()),
                savingsBalanceBeforeDeposit);

        if (depositAmountIsInExcess(amountRemaining)) {
            final SavingsTrxnDetailEntity excessDepositTrxn = this.savingsTransactionActivityHelper
                    .createSavingsTrxnForDeposit(payment, amountRemaining, payingCustomer, null, savingsBalance);
            payment.addAccountTrxn(excessDepositTrxn);
        }

        buildFinancialEntries(payment.getAccountTrxns());
    }

    public void withdraw(final AccountPaymentEntity payment, final CustomerBO payingCustomer) throws AccountException {

        final Money amountToWithdraw = payment.getAmount();
        if (amountToWithdraw.isGreaterThan(savingsBalance)) {
            throw new AccountException("errors.insufficentbalance", new String[] { getGlobalAccountNum() });
        }
        final Money maxWithdrawAmount = getSavingsOffering().getMaxAmntWithdrawl();
        if (maxWithdrawAmount != null && maxWithdrawAmount.isNonZero()
                && amountToWithdraw.isGreaterThan(maxWithdrawAmount)) {
            throw new AccountException("errors.exceedmaxwithdrawal", new String[] { getGlobalAccountNum() });
        }

        final AccountStateEntity accountStateEntity = new AccountStateEntity(AccountState.SAVINGS_ACTIVE);
        this.setAccountState(accountStateEntity);
        this.addAccountPayment(payment);

        savingsBalance = savingsBalance.subtract(amountToWithdraw);
        savingsPerformance.setWithdrawDetails(amountToWithdraw);

        final SavingsActivityEntity savingsActivity = this.savingsTransactionActivityHelper
                .createSavingsActivityForWithdrawal(payment, this.savingsBalance, this);
        addSavingsActivityDetails(savingsActivity);

        final SavingsTrxnDetailEntity accountTrxnBO = this.savingsTransactionActivityHelper
                .createSavingsTrxnForWithdrawal(payment, amountToWithdraw, payingCustomer, this.savingsBalance);
        payment.addAccountTrxn(accountTrxnBO);

        buildFinancialEntries(payment.getAccountTrxns());
    }

    /**
     * @deprecated use {@link SavingsBO#withdraw(AccountPaymentEntity, CustomerBO)} instead and use save from
     *             DAO/Persistence for {@link SavingsBO}.
     */
    @Deprecated
    public void withdraw(final PaymentData accountPaymentData, final boolean persist) throws AccountException {
        Money totalAmount = accountPaymentData.getTotalAmount();
        if (totalAmount.isGreaterThan(savingsBalance)) {
            throw new AccountException("errors.insufficentbalance", new String[] { getGlobalAccountNum() });
        }
        Money maxWithdrawAmount = getSavingsOffering().getMaxAmntWithdrawl();
        if (maxWithdrawAmount != null && maxWithdrawAmount.isNonZero() && totalAmount.isGreaterThan(maxWithdrawAmount)) {
            throw new AccountException("errors.exceedmaxwithdrawal", new String[] { getGlobalAccountNum() });
        }
        savingsBalance = savingsBalance.subtract(totalAmount);
        savingsPerformance.setWithdrawDetails(totalAmount);
        CustomerBO customer = accountPaymentData.getCustomer();
        AccountPaymentEntity accountPayment = new AccountPaymentEntity(this, totalAmount, accountPaymentData
                .getReceiptNum(), accountPaymentData.getReceiptDate(), getPaymentTypeEntity(accountPaymentData
                .getPaymentTypeId()), accountPaymentData.getTransactionDate());

        SavingsTrxnDetailEntity accountTrxnBO;

        accountTrxnBO = new SavingsTrxnDetailEntity(accountPayment, customer, AccountActionTypes.SAVINGS_WITHDRAWAL,
                totalAmount, getSavingsBalance(), accountPaymentData.getPersonnel(), accountPaymentData
                        .getTransactionDate(), accountPaymentData.getTransactionDate(), null, "",
                getSavingsPersistence());

        accountPayment.addAccountTrxn(accountTrxnBO);
        addAccountPayment(accountPayment);
        addSavingsActivityDetails(buildSavingsActivity(totalAmount, getSavingsBalance(),
                AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), accountPaymentData.getTransactionDate(),
                accountPaymentData.getPersonnel()));
        buildFinancialEntries(accountPayment.getAccountTrxns());
        if (this.getAccountState().getId().equals(AccountStates.SAVINGS_ACC_INACTIVE)) {
            try {
                this
                        .setAccountState(getSavingsPersistence().getAccountStatusObject(
                                AccountStates.SAVINGS_ACC_APPROVED));
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
        if (persist) {
            try {
                getSavingsPersistence().createOrUpdate(this);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
    }

    private void setValuesForActiveState(final List<Days> workingDays, final List<Holiday> holidays)
            throws AccountException {
        this.setActivationDate(new DateTimeService().getCurrentJavaDateTime());
        this.generateDepositAccountActions(workingDays, holidays);
        try {
            Date intCalcDate = helper.getNextScheduleDate(getActivationDate(), null, getTimePerForInstcalc());
            this.setNextIntCalcDate(intCalcDate);
            Date intPostDate = helper.getNextScheduleDate(getActivationDate(), null, getMeeting(getSavingsOffering()
                    .getFreqOfPostIntcalc().getMeeting()));
            this.setNextIntPostDate(intPostDate);
        } catch (ProductDefinitionException e) {
            throw new AccountException(e);
        } catch (MeetingException me) {
            throw new AccountException(me);
        }
    }

    @Override
    protected void activationDateHelper(final Short newStatusId) throws AccountException {

        // FIXME - keithw - pass in this info to method
        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        List<Holiday> holidays = new ArrayList<Holiday>();

        if (ProcessFlowRules.isSavingsPendingApprovalStateEnabled()) {
            if (this.getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_PENDINGAPPROVAL
                    && newStatusId.shortValue() == AccountStates.SAVINGS_ACC_APPROVED) {
                setValuesForActiveState(workingDays, holidays);
            }
        } else {
            if (this.getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_PARTIALAPPLICATION
                    && newStatusId.shortValue() == AccountStates.SAVINGS_ACC_APPROVED) {
                setValuesForActiveState(workingDays, holidays);
            }
        }
    }

    public void adjustLastUserAction(final Money amountAdjustedTo, final String adjustmentComment)
            throws AccountException {
        logger.debug("In SavingsBO::generateDepositAccountActions(), accountId: " + getAccountId());
        if (!isAdjustPossibleOnLastTrxn(amountAdjustedTo)) {
            throw new AccountException(AccountExceptionConstants.CANNOTADJUST);
        }
        Date trxnDate = getTrxnDate(getLastPmnt());
        Money oldInterest = calculateInterestForAdjustment(trxnDate, null);
        adjustExistingPayment(amountAdjustedTo, adjustmentComment);
        AccountPaymentEntity newPayment = createAdjustmentPayment(amountAdjustedTo, adjustmentComment);

        // Fix for Hibernate 3.2, need to update for adjustment to existing
        // payment to be committed
        this.update();
        StaticHibernateUtil.commitTransaction();

        adjustInterest(oldInterest, trxnDate, newPayment);
        if (this.getAccountState().getId().equals(AccountState.SAVINGS_INACTIVE.getValue())) {
            try {
                this.setAccountState(getSavingsPersistence().getAccountStatusObject(
                        AccountState.SAVINGS_ACTIVE.getValue()));
            } catch (PersistenceException pe) {
                throw new AccountException(pe);
            }
        }
        this.update();
    }

    protected void adjustInterest(final Money oldInterest, final Date trxnDate, final AccountPaymentEntity newPayment)
            throws AccountException {
        if (getLastIntCalcDate() != null && trxnDate.compareTo(getLastIntCalcDate()) < 0) {
            SavingsTrxnDetailEntity adjustedTrxn = null;
            if (newPayment == null || newPayment.getAccountTrxns() == null || newPayment.getAccountTrxns().size() == 0) {
                adjustedTrxn = getLastTrxnForAdjustedPayment(getLastPmnt());
            } else {
                adjustedTrxn = getLastTrxnForPayment(newPayment);
            }
            Money newInterest = calculateInterestForAdjustment(trxnDate, adjustedTrxn);
            setInterestToBePosted(getInterestToBePosted().add(newInterest.subtract(oldInterest)));
        }
    }

    private SavingsTrxnDetailEntity getLastTrxnForPayment(final AccountPaymentEntity payment) {
        Short accountAction = helper.getPaymentActionType(payment);
        if (accountAction.equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())) {
            for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
                return (SavingsTrxnDetailEntity) accountTrxn;
            }
        } else if (accountAction.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
            SavingsTrxnDetailEntity lastTrxn = null;
            for (AccountTrxnEntity trxn : payment.getAccountTrxns()) {
                SavingsTrxnDetailEntity accountTrxn = (SavingsTrxnDetailEntity) trxn;
                if (lastTrxn == null || accountTrxn.getBalance().isGreaterThan(lastTrxn.getBalance())) {
                    lastTrxn = accountTrxn;
                }
            }
            return lastTrxn;
        }

        return null;
    }

    private SavingsTrxnDetailEntity getLastTrxnForAdjustedPayment(final AccountPaymentEntity payment) {
        Short accountAction = helper.getPaymentActionType(payment);
        if (accountAction.equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())) {
            for (AccountTrxnEntity accountTrxn : payment.getAccountTrxns()) {
                if (accountTrxn.getAccountActionEntity().getId().equals(
                        AccountActionTypes.SAVINGS_ADJUSTMENT.getValue())) {
                    return (SavingsTrxnDetailEntity) accountTrxn;
                }
            }
        } else if (accountAction.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
            SavingsTrxnDetailEntity lastTrxn = null;
            for (AccountTrxnEntity trxn : payment.getAccountTrxns()) {
                SavingsTrxnDetailEntity accountTrxn = (SavingsTrxnDetailEntity) trxn;
                if (lastTrxn == null
                        || accountTrxn.getAccountActionEntity().getId().equals(
                                AccountActionTypes.SAVINGS_ADJUSTMENT.getValue())
                        && accountTrxn.getBalance().isLessThan(lastTrxn.getBalance())) {
                    lastTrxn = accountTrxn;
                }
            }
            return lastTrxn;
        }
        return null;
    }

    private Date getTrxnDate(final AccountPaymentEntity payment) {
        Date trxnDate = null;
        for (AccountTrxnEntity accntTrxn : payment.getAccountTrxns()) {
            trxnDate = accntTrxn.getActionDate();
        }
        return trxnDate;
    }

    private Date getFromDateForInterestAdjustment(final Date trxnDate) throws AccountException {
        Date fromDate = getLastIntCalcDate();
        do {
            try {
                fromDate = helper.getPrevScheduleDate(getActivationDate(), fromDate, timePerForInstcalc);
            } catch (MeetingException me) {
                throw new AccountException(me);
            }
            if (fromDate != null) {
                fromDate = new Timestamp(fromDate.getTime());
            }
        } while (fromDate != null && trxnDate.compareTo(fromDate) < 0);
        return fromDate == null ? getActivationDate() : fromDate;
    }

    protected Money calculateInterestForAdjustment(final Date trxnDate, final SavingsTrxnDetailEntity adjustedTrxn)
            throws AccountException {
        Money oldInterest = new Money(getCurrency());
        if (getLastIntCalcDate() != null && trxnDate.compareTo(getLastIntCalcDate()) < 0) {
            Date toDate = null;
            Date fromDate = getFromDateForInterestAdjustment(trxnDate);
            try {
                for (toDate = new Timestamp(helper.getNextScheduleDate(getActivationDate(), fromDate,
                        timePerForInstcalc).getTime()); toDate.compareTo(getLastIntCalcDate()) <= 0; toDate = new Timestamp(
                        helper.getNextScheduleDate(getActivationDate(), toDate, timePerForInstcalc).getTime())) {

                    oldInterest = oldInterest.add(calculateInterest(fromDate, toDate, getInterestRate(), adjustedTrxn));
                    // //////////Removed if for adjustedTrxn!=null
                    fromDate = toDate;
                }
            } catch (MeetingException me) {
                throw new AccountException(me);
            }
        }
        return oldInterest;
    }

    protected void adjustExistingPayment(final Money amountAdjustedTo, final String adjustmentComment)
            throws AccountException {
        AccountPaymentEntity lastPayment = getLastPmnt();
        Short actionType = helper.getPaymentActionType(lastPayment);
        try {
            PersonnelBO personnel = getPersonnelPersistence().getPersonnel(userContext.getId());
            for (AccountTrxnEntity accntTrxn : lastPayment.getAccountTrxns()) {
                if (actionType.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
                    adjustForDeposit(accntTrxn);
                } else if (actionType.equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())) {
                    adjustForWithdrawal(accntTrxn);
                }
            }
            addSavingsActivityDetails(buildSavingsActivity(lastPayment.getAmount(), getSavingsBalance(),
                    AccountActionTypes.SAVINGS_ADJUSTMENT.getValue(), new DateTimeService().getCurrentJavaDateTime(),
                    personnel));
            logger.debug("transaction count before adding reversal transactions: "
                    + lastPayment.getAccountTrxns().size());
            List<AccountTrxnEntity> newlyAddedTrxns = reversalAdjustment(adjustmentComment, lastPayment);
            buildFinancialEntries(new HashSet<AccountTrxnEntity>(newlyAddedTrxns));
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    protected AccountPaymentEntity createAdjustmentPayment(final Money amountAdjustedTo, final String adjustmentComment)
            throws AccountException {
        try {
            AccountPaymentEntity lastPayment = getLastPmnt();
            AccountPaymentEntity newAccountPayment = null;
            PersonnelBO personnel = getPersonnelPersistence().getPersonnel(userContext.getId());
            Date transactionDate = new DateTimeService().getCurrentJavaDateTime();
            if (amountAdjustedTo.isGreaterThanZero()) {
                newAccountPayment = helper.createAccountPayment(this, amountAdjustedTo, lastPayment.getPaymentType(),
                        getPersonnelPersistence().getPersonnel(userContext.getId()), transactionDate);
            }
            if (newAccountPayment != null) {
                newAccountPayment.setAmount(amountAdjustedTo);
                Set<AccountTrxnEntity> accountTrxns = createTrxnsForAmountAdjusted(newAccountPayment, lastPayment,
                        amountAdjustedTo);
                for (AccountTrxnEntity accountTrxn : accountTrxns) {
                    newAccountPayment.addAccountTrxn(accountTrxn);
                }
                this.addAccountPayment(newAccountPayment);
                buildFinancialEntries(newAccountPayment.getAccountTrxns());

                addSavingsActivityDetails(buildSavingsActivity(amountAdjustedTo, getSavingsBalance(), helper
                        .getPaymentActionType(lastPayment), getTrxnDate(lastPayment), personnel));
            }
            return newAccountPayment;
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    private List<AccountActionDateEntity> getAccountActions(final Date dueDate, final Integer customerId) {
        List<AccountActionDateEntity> accountActions = new ArrayList<AccountActionDateEntity>();
        for (AccountActionDateEntity accountAction : getAccountActionDates()) {
            if (accountAction.getActionDate().compareTo(dueDate) <= 0 && !accountAction.isPaid()
                    && accountAction.getCustomer().getCustomerId().equals(customerId)) {
                accountActions.add(accountAction);
            }
        }
        return accountActions;
    }

    protected Set<AccountTrxnEntity> createTrxnsForAmountAdjusted(final AccountPaymentEntity newAccountPayment,
            final AccountPaymentEntity lastAccountPayment, final Money newAmount) throws AccountException {
        Short actionType = helper.getPaymentActionType(lastAccountPayment);
        if (isMandatory() && actionType.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
            return createDepositTrxnsForMandatoryAccountsAfterAdjust(newAccountPayment, lastAccountPayment, newAmount);
        }

        if (actionType.equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
            return createDepositTrxnsForVolAccountsAfterAdjust(newAccountPayment, lastAccountPayment, newAmount);
        }

        return createWithdrawalTrxnsAfterAdjust(newAccountPayment, lastAccountPayment, newAmount);
    }

    private Set<AccountTrxnEntity> createWithdrawalTrxnsAfterAdjust(final AccountPaymentEntity newAccountPayment,
            final AccountPaymentEntity lastAccountPayment, final Money newAmount) throws AccountException {
        Set<AccountTrxnEntity> newTrxns = new HashSet<AccountTrxnEntity>();
        SavingsTrxnDetailEntity accountTrxn = null;
        // create transaction for withdrawal
        SavingsTrxnDetailEntity oldSavingsAccntTrxn = null;
        for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment.getAccountTrxns()) {
            oldSavingsAccntTrxn = (SavingsTrxnDetailEntity) oldAccntTrxn;
            break;
        }
        setSavingsBalance(getSavingsBalance().subtract(newAmount));
        try {
            accountTrxn = new SavingsTrxnDetailEntity(newAccountPayment, oldSavingsAccntTrxn.getCustomer(),
                    AccountActionTypes.SAVINGS_WITHDRAWAL, newAmount, getSavingsBalance(), getPersonnelPersistence()
                            .getPersonnel(userContext.getId()), oldSavingsAccntTrxn.getDueDate(), oldSavingsAccntTrxn
                            .getActionDate(), null, "", getSavingsPersistence());
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        getSavingsPerformance().setTotalWithdrawals(
                getSavingsPerformance().getTotalWithdrawals().add(accountTrxn.getWithdrawlAmount()));
        newTrxns.add(accountTrxn);
        return newTrxns;
    }

    private Set<AccountTrxnEntity> createDepositTrxnsForMandatoryAccountsAfterAdjust(
            final AccountPaymentEntity newAccountPayment, final AccountPaymentEntity lastAccountPayment, Money newAmount)
            throws AccountException {
        Set<AccountTrxnEntity> newTrxns = new HashSet<AccountTrxnEntity>();
        SavingsTrxnDetailEntity accountTrxn = null;
        CustomerBO customer = null;
        Date oldTrxnDate = null;
        for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment.getAccountTrxns()) {
            customer = oldAccntTrxn.getCustomer();
            oldTrxnDate = oldAccntTrxn.getActionDate();
            break;
        }
        List<AccountActionDateEntity> accountActionList = getAccountActions(lastAccountPayment.getPaymentDate(),
                customer.getCustomerId());
        for (AccountActionDateEntity accountActionDateEntity : accountActionList) {
            SavingsScheduleEntity accountAction = (SavingsScheduleEntity) accountActionDateEntity;
            if (newAmount.isZero()) {
                break;
            }
            accountTrxn = null;
            if (accountAction.getDeposit().isLessThanOrEqual(newAmount)) {
                setSavingsBalance(getSavingsBalance().add(accountAction.getDeposit()));
                try {
                    accountTrxn = new SavingsTrxnDetailEntity(newAccountPayment, customer,
                            AccountActionTypes.SAVINGS_DEPOSIT, accountAction.getDeposit(), getSavingsBalance(),
                            getPersonnelPersistence().getPersonnel(userContext.getId()), accountAction.getActionDate(),
                            oldTrxnDate, accountAction.getInstallmentId(), "", getSavingsPersistence());
                } catch (PersistenceException e) {
                    throw new AccountException(e);
                }
                newAmount = newAmount.subtract(accountAction.getDeposit());
                accountAction.setDepositPaid(accountAction.getDepositPaid().add(accountTrxn.getDepositAmount()));
                accountAction.setPaymentStatus(PaymentStatus.PAID);
            } else {
                setSavingsBalance(getSavingsBalance().add(newAmount));
                try {
                    accountTrxn = new SavingsTrxnDetailEntity(newAccountPayment, customer,
                            AccountActionTypes.SAVINGS_DEPOSIT, newAmount, getSavingsBalance(),
                            getPersonnelPersistence().getPersonnel(userContext.getId()), accountAction.getActionDate(),
                            oldTrxnDate, accountAction.getInstallmentId(), "", getSavingsPersistence());
                } catch (PersistenceException e) {
                    throw new AccountException(e);
                }
                newAmount = newAmount.subtract(newAmount);
                accountAction.setDepositPaid(accountAction.getDepositPaid().add(accountTrxn.getDepositAmount()));
                accountAction.setPaymentStatus(PaymentStatus.UNPAID);
            }
            accountAction.setPaymentDate(new DateTimeService().getCurrentJavaSqlDate());
            getSavingsPerformance().setTotalDeposits(
                    getSavingsPerformance().getTotalDeposits().add(accountTrxn.getDepositAmount()));
            newTrxns.add(accountTrxn);
        }
        // add trxn for excess amount
        if (newAmount.isGreaterThanZero()) {
            setSavingsBalance(getSavingsBalance().add(newAmount));
            try {
                accountTrxn = new SavingsTrxnDetailEntity(newAccountPayment, customer,
                        AccountActionTypes.SAVINGS_DEPOSIT, newAmount, getSavingsBalance(), getPersonnelPersistence()
                                .getPersonnel(userContext.getId()), null, oldTrxnDate, null, "",
                        getSavingsPersistence());
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
            newAmount = newAmount.subtract(newAmount);
            getSavingsPerformance().setTotalDeposits(
                    getSavingsPerformance().getTotalDeposits().add(accountTrxn.getDepositAmount()));
            newTrxns.add(accountTrxn);
        }
        return newTrxns;
    }

    private Set<AccountTrxnEntity> createDepositTrxnsForVolAccountsAfterAdjust(
            final AccountPaymentEntity newAccountPayment, final AccountPaymentEntity lastAccountPayment, Money newAmount)
            throws AccountException {
        Set<AccountTrxnEntity> newTrxns = new HashSet<AccountTrxnEntity>();
        SavingsTrxnDetailEntity accountTrxn = null;
        CustomerBO customer = null;
        Date oldTrxnDate = null;
        for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment.getAccountTrxns()) {
            customer = oldAccntTrxn.getCustomer();
            oldTrxnDate = oldAccntTrxn.getActionDate();
            break;
        }
        for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment.getAccountTrxns()) {
            if (oldAccntTrxn.getAccountActionEntity().getId().equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
                SavingsTrxnDetailEntity oldSavingsAccntTrxn = (SavingsTrxnDetailEntity) oldAccntTrxn;
                if (oldAccntTrxn.getInstallmentId() != null) {
                    SavingsScheduleEntity accountAction = (SavingsScheduleEntity) getAccountActionDate(
                            oldSavingsAccntTrxn.getInstallmentId(), oldSavingsAccntTrxn.getCustomer().getCustomerId());
                    if (accountAction.getDeposit().isLessThanOrEqual(newAmount)) {
                        setSavingsBalance(getSavingsBalance().add(accountAction.getDeposit()));
                        try {
                            accountTrxn = new SavingsTrxnDetailEntity(newAccountPayment, customer,
                                    AccountActionTypes.SAVINGS_DEPOSIT, accountAction.getDeposit(),
                                    getSavingsBalance(), getPersonnelPersistence().getPersonnel(userContext.getId()),
                                    accountAction.getActionDate(), oldTrxnDate, oldAccntTrxn.getInstallmentId(), "",
                                    getSavingsPersistence());
                        } catch (PersistenceException e) {
                            throw new AccountException(e);
                        }
                        newAmount = newAmount.subtract(accountAction.getDeposit());
                        accountAction
                                .setDepositPaid(accountAction.getDepositPaid().add(accountTrxn.getDepositAmount()));
                        accountAction.setPaymentStatus(PaymentStatus.PAID);
                    } else if (newAmount.isNonZero()) {
                        setSavingsBalance(getSavingsBalance().add(newAmount));
                        try {
                            accountTrxn = new SavingsTrxnDetailEntity(newAccountPayment, customer,
                                    AccountActionTypes.SAVINGS_DEPOSIT, newAmount, getSavingsBalance(),
                                    getPersonnelPersistence().getPersonnel(userContext.getId()), accountAction
                                            .getActionDate(), oldTrxnDate, oldAccntTrxn.getInstallmentId(), "",
                                    getSavingsPersistence());
                        } catch (PersistenceException e) {
                            throw new AccountException(e);
                        }
                        newAmount = newAmount.subtract(newAmount);
                        accountAction
                                .setDepositPaid(accountAction.getDepositPaid().add(accountTrxn.getDepositAmount()));
                        accountAction.setPaymentStatus(PaymentStatus.UNPAID);
                    }
                    accountAction.setPaymentDate(new DateTimeService().getCurrentJavaSqlDate());
                    // FIXME : accountTrxn could only be null here, see eclipse warning
                    // it should be causing null pointer exception sometimes.
                    getSavingsPerformance().setTotalDeposits(
                            getSavingsPerformance().getTotalDeposits().add(accountTrxn.getDepositAmount()));
                    break;
                }
            }
        }

        if (accountTrxn != null) {
            newTrxns.add(accountTrxn);
        }
        // Create a new transaction with remaining amount
        if (newAmount.isGreaterThanZero()) {
            setSavingsBalance(getSavingsBalance().add(newAmount));
            try {
                accountTrxn = new SavingsTrxnDetailEntity(newAccountPayment, customer,
                        AccountActionTypes.SAVINGS_DEPOSIT, newAmount, getSavingsBalance(), getPersonnelPersistence()
                                .getPersonnel(userContext.getId()), null, oldTrxnDate, null, "",
                        getSavingsPersistence());
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
            getSavingsPerformance().setTotalDeposits(
                    getSavingsPerformance().getTotalDeposits().add(accountTrxn.getDepositAmount()));
            newTrxns.add(accountTrxn);
        }
        return newTrxns;
    }

    private void adjustForDeposit(final AccountTrxnEntity accntTrxn) {
        SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
        Short installmentId = savingsTrxn.getInstallmentId();
        setSavingsBalance(getSavingsBalance().subtract(savingsTrxn.getDepositAmount()));
        SavingsScheduleEntity accntActionDate = (SavingsScheduleEntity) getAccountActionDate(installmentId, accntTrxn
                .getCustomer().getCustomerId());
        if (accntActionDate != null) {
            accntActionDate.setDepositPaid(accntActionDate.getDepositPaid().subtract(savingsTrxn.getDepositAmount()));
            accntActionDate.setPaymentStatus(PaymentStatus.UNPAID);
            accntActionDate.setPaymentDate(null);
        }
        getSavingsPerformance().setTotalDeposits(
                getSavingsPerformance().getTotalDeposits().subtract(savingsTrxn.getDepositAmount()));
    }

    private void adjustForWithdrawal(final AccountTrxnEntity accntTrxn) {
        SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
        setSavingsBalance(getSavingsBalance().add(savingsTrxn.getWithdrawlAmount()));
        getSavingsPerformance().setTotalWithdrawals(
                getSavingsPerformance().getTotalWithdrawals().subtract(savingsTrxn.getWithdrawlAmount()));
    }

    protected boolean isAdjustPossibleOnLastTrxn(final Money amountAdjustedTo) {
        if (!(getAccountState().getId().equals(AccountStates.SAVINGS_ACC_APPROVED) || getAccountState().getId().equals(
                AccountStates.SAVINGS_ACC_INACTIVE))) {
            logger.debug("State is not active hence adjustment is not possible");
            return false;
        }
        AccountPaymentEntity accountPayment = getLastPmnt();
        if (accountPayment != null
                && getLastPmntAmnt() != 0
                && (helper.getPaymentActionType(accountPayment)
                        .equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue()) || helper.getPaymentActionType(
                        accountPayment).equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue()))) {
            if (accountPayment.getAmount().equals(amountAdjustedTo)) {
                logger
                        .debug("Either Amount to be adjusted is same as last user payment amount, or last payment is not withdrawal or deposit, therefore adjustment is not possible.");
                return false;
            }
            return adjustmentCheckForWithdrawal(accountPayment, amountAdjustedTo)
                    && adjustmentCheckForBalance(accountPayment, amountAdjustedTo);
        }
        logger.debug("No last Payment found for adjustment");
        return false;
    }

    private boolean adjustmentCheckForWithdrawal(final AccountPaymentEntity accountPayment, final Money amountAdjustedTo) {
        Money maxWithdrawAmount = getSavingsOffering().getMaxAmntWithdrawl();
        if (helper.getPaymentActionType(accountPayment).equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())
                && maxWithdrawAmount != null && maxWithdrawAmount.isNonZero()
                && amountAdjustedTo.isGreaterThan(maxWithdrawAmount)) {
            logger.debug("Amount is more than withdrawal limit");
            return false;
        }
        return true;
    }

    private boolean adjustmentCheckForBalance(final AccountPaymentEntity accountPayment, final Money amountAdjustedTo) {
        Money balanceAfterAdjust = getSavingsBalance();
        for (AccountTrxnEntity accntTrxn : accountPayment.getAccountTrxns()) {
            SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
            if (helper.getPaymentActionType(accountPayment).equals(AccountActionTypes.SAVINGS_WITHDRAWAL.getValue())
                    && amountAdjustedTo.isGreaterThan(savingsTrxn.getWithdrawlAmount())) {
                balanceAfterAdjust = balanceAfterAdjust.subtract(amountAdjustedTo.subtract(savingsTrxn
                        .getWithdrawlAmount()));
                if (balanceAfterAdjust.isLessThanZero()) {
                    logger.debug("After adjustment balance is becoming -ve, therefore adjustment is not possible.");
                    return false;
                }
            }
        }
        return true;
    }

    public AccountNotesEntity createAccountNotes(final String comment) throws AccountException {
        try {
            AccountNotesEntity accountNotes = new AccountNotesEntity(new DateTimeService().getCurrentJavaSqlDate(),
                    comment, getPersonnelPersistence().getPersonnel(userContext.getId()), this);
            return accountNotes;

        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    public Money getOverDueDepositAmount(final java.sql.Date meetingDate) {
        Money overdueAmount = new Money(getCurrency());
        if (isMandatory()) {
            for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
                if (!accountActionDate.isPaid() && accountActionDate.getActionDate().before(meetingDate)) {
                    overdueAmount = overdueAmount.add(((SavingsScheduleEntity) accountActionDate).getTotalDepositDue());
                }
            }
        }
        return overdueAmount;
    }

    public List<SavingsRecentActivityView> getRecentAccountActivity(final Integer count) {
        List<SavingsRecentActivityView> accountActivityList = new ArrayList<SavingsRecentActivityView>();
        int activitiesAdded = 0;
        for (SavingsActivityEntity activity : getSavingsActivityDetails()) {
            if (count == null || activitiesAdded < count.intValue()) {
                accountActivityList.add(createSavingsRecentActivityView(activity));
                activitiesAdded++;
            }
        }
        return accountActivityList;
    }

    private SavingsRecentActivityView createSavingsRecentActivityView(final SavingsActivityEntity savingActivity) {
        SavingsRecentActivityView savingsRecentActivityView = new SavingsRecentActivityView();
        savingsRecentActivityView.setAccountTrxnId(savingActivity.getId());
        savingsRecentActivityView.setActionDate(savingActivity.getTrxnCreatedDate());
        savingsRecentActivityView.setAmount(removeSign(savingActivity.getAmount()).toString());
        savingsRecentActivityView.setActivity(savingActivity.getActivity().getName());
        savingsRecentActivityView.setRunningBalance(savingActivity.getBalanceAmount().toString());
        return savingsRecentActivityView;
    }

    @Override
    protected Money getDueAmount(final AccountActionDateEntity installment) {
        return ((SavingsScheduleEntity) installment).getTotalDepositDue();
    }

    @Override
    public Money getTotalAmountDue() {
        return getTotalAmountInArrears().add(getTotalAmountDueForNextInstallment());
    }

    @Override
    public Money getTotalAmountInArrears() {
        List<AccountActionDateEntity> installmentsInArrears = getDetailsOfInstallmentsInArrears();
        Money totalAmount = new Money(getCurrency());
        if (installmentsInArrears != null && installmentsInArrears.size() > 0) {
            for (AccountActionDateEntity accountAction : installmentsInArrears) {
                if (!(accountAction.getCustomer().getCustomerLevel().getId().equals(CustomerLevel.CLIENT.getValue()) && accountAction
                        .getCustomer().getStatus().equals(CustomerStatus.CLIENT_CLOSED))) {
                    totalAmount = totalAmount.add(getDueAmount(accountAction));
                }
            }
        }
        return totalAmount;
    }

    public Money getTotalAmountDueForNextInstallment() {
        AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();
        Money totalAmount = new Money(getCurrency());
        if (nextAccountAction != null) {
            if (null != getAccountActionDates() && getAccountActionDates().size() > 0) {
                for (AccountActionDateEntity accntActionDate : getAccountActionDates()) {
                    if (accntActionDate.getInstallmentId().equals(nextAccountAction.getInstallmentId())
                            && !accntActionDate.isPaid()) {
                        if (!(accntActionDate.getCustomer().getCustomerLevel().getId().equals(
                                CustomerLevel.CLIENT.getValue()) && accntActionDate.getCustomer().getStatus().equals(
                                CustomerStatus.CLIENT_CLOSED))) {
                            totalAmount = totalAmount.add(((SavingsScheduleEntity) accntActionDate)
                                    .getTotalDepositDue());
                        }
                    }
                }
            }
        }
        return totalAmount;
    }

    /*
     * private Money getTotalAmountDueForInstallment(Short installmentId) { Money totalAmount = new
     * Money(getCurrency()); if (null != getAccountActionDates() && getAccountActionDates().size() > 0) { for
     * (AccountActionDateEntity accntActionDate : getAccountActionDates()) { if
     * (accntActionDate.getInstallmentId().equals(installmentId) && accntActionDate.getPaymentStatus().equals(
     * PaymentStatus.UNPAID.getValue())) { totalAmount = totalAmount .add(((SavingsScheduleEntity) accntActionDate)
     * .getTotalDepositDue()); } } }
     *
     * return totalAmount; }
     *
     * public Money getTotalAmountDueForNextInstallment() { AccountActionDateEntity nextAccountAction =
     * getDetailsOfNextInstallment(); if (nextAccountAction != null) return
     * getTotalAmountDueForInstallment(nextAccountAction .getInstallmentId()); return new Money(getCurrency()); }
     */

    private List<AccountActionDateEntity> getNextInstallment() {
        List<AccountActionDateEntity> nextInstallment = new ArrayList<AccountActionDateEntity>();
        AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();
        if (nextAccountAction != null && null != getAccountActionDates() && getAccountActionDates().size() > 0) {
            for (AccountActionDateEntity accntActionDate : getAccountActionDates()) {
                if (accntActionDate.getInstallmentId().equals(nextAccountAction.getInstallmentId())
                        && !accntActionDate.isPaid()) {
                    nextInstallment.add(accntActionDate);
                }
            }
        }
        return nextInstallment;
    }

    @Override
    public void waiveAmountDue(final WaiveEnum waiveType) throws AccountException {
        try {
            PersonnelBO personnel = getPersonnelPersistence().getPersonnel(getUserContext().getId());
            addSavingsActivityDetails(buildSavingsActivity(getTotalAmountDueForNextInstallment(), getSavingsBalance(),
                    AccountActionTypes.WAIVEOFFDUE.getValue(), new DateTimeService().getCurrentJavaDateTime(),
                    personnel));
            List<AccountActionDateEntity> nextInstallments = getNextInstallment();
            for (AccountActionDateEntity accountActionDate : nextInstallments) {
                ((SavingsScheduleEntity) accountActionDate).waiveDepositDue();
            }

            getSavingsPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    public void waiveAmountOverDue() throws AccountException {
        try {
            PersonnelBO personnel = getPersonnelPersistence().getPersonnel(getUserContext().getId());
            addSavingsActivityDetails(buildSavingsActivity(getTotalAmountInArrears(), getSavingsBalance(),
                    AccountActionTypes.WAIVEOFFOVERDUE.getValue(), new DateTimeService().getCurrentJavaDateTime(),
                    personnel));
            List<AccountActionDateEntity> installmentsInArrears = getDetailsOfInstallmentsInArrears();
            for (AccountActionDateEntity accountActionDate : installmentsInArrears) {
                ((SavingsScheduleEntity) accountActionDate).waiveDepositDue();
            }

            getSavingsPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }

    }

    private SavingsActivityEntity buildSavingsActivity(final Money amount, final Money balanceAmount,
            final short acccountActionId, final Date trxnDate, final PersonnelBO personnel) throws AccountException {
        AccountActionEntity accountAction;
        try {
            accountAction = (AccountActionEntity) getSavingsPersistence().getPersistentObject(
                    AccountActionEntity.class, acccountActionId);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        return new SavingsActivityEntity(personnel, accountAction, amount, balanceAmount, trxnDate, this);
    }

    @Override
    protected void regenerateFutureInstallments(final AccountActionDateEntity nextInstallment,
            final List<Days> workingDays, final List<Holiday> holidays) throws AccountException {

        if (!this.getAccountState().getId().equals(AccountStates.SAVINGS_ACC_CANCEL)
                && !this.getAccountState().getId().equals(AccountStates.SAVINGS_ACC_CLOSED)) {

            MeetingBO customerMeeting = getCustomer().getCustomerMeetingValue();
            DateTime startFromMeetingDate = customerMeeting.startDateForMeetingInterval(
                    new LocalDate(nextInstallment.getActionDate().getTime())).toDateTimeAtStartOfDay();

            ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(customerMeeting);
            ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysScheduledDateGeneration(workingDays,
                    holidays);

            int numberOfInstallmentsToGenerate = getLastInstallmentId();
            List<DateTime> meetingDates = dateGeneration.generateScheduledDates(numberOfInstallmentsToGenerate,
                    startFromMeetingDate, scheduledEvent);

            if (getCustomer().getCustomerLevel().getId().equals(CustomerLevel.CLIENT.getValue())
                    || getCustomer().getCustomerLevel().getId().equals(CustomerLevel.GROUP.getValue())
                    && getRecommendedAmntUnit().getId().equals(RecommendedAmountUnit.COMPLETE_GROUP.getValue())) {
                updateSchedule(nextInstallment.getInstallmentId(), meetingDates);
            } else {
                List<CustomerBO> children;
                try {
                    children = getCustomer().getChildren(CustomerLevel.CLIENT, ChildrenStateType.OTHER_THAN_CLOSED);
                } catch (CustomerException ce) {
                    throw new AccountException(ce);
                }
                updateSavingsSchedule(nextInstallment.getInstallmentId(), meetingDates, children);
            }
        }

    }

    private void updateSavingsSchedule(final Short nextInstallmentId, final List<DateTime> meetingDates,
            final List<CustomerBO> children) {
        short installmentId = nextInstallmentId;

        for (int count = 0; count < meetingDates.size(); count++) {
            for (CustomerBO customer : children) {
                AccountActionDateEntity accountActionDate = getAccountActionDate(installmentId, customer
                        .getCustomerId());

                if (accountActionDate != null) {
                    Date meetingDate = meetingDates.get(count).toDate();
                    ((SavingsScheduleEntity) accountActionDate).setActionDate(new java.sql.Date(meetingDate.getTime()));
                }
            }
            installmentId++;
        }
    }

    public Money getTotalPaymentDue(final Integer customerId) {
        return isMandatory() ? getTotalPaymentDueForManAccount(customerId)
                : getTotalPaymentDueForVolAccount(customerId);
    }

    public List<AccountActionDateEntity> getTotalInstallmentsDue(final Integer customerId) {
        return isMandatory() ? getInstallmentsDueForManAccount(customerId) : getInstallmentDueForVolAccount(customerId);
    }

    private List<AccountActionDateEntity> getInstallmentsDueForManAccount(final Integer customerId) {
        List<AccountActionDateEntity> dueInstallements = new ArrayList<AccountActionDateEntity>();
        Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        if (getAccountActionDates() != null && getAccountActionDates().size() > 0) {
            for (AccountActionDateEntity accountAction : getAccountActionDates()) {
                if (accountAction.getActionDate().compareTo(currentDate) <= 0 && !accountAction.isPaid()
                        && accountAction.getCustomer().getCustomerId().equals(customerId)) {
                    dueInstallements.add(accountAction);
                }
            }
        }
        return dueInstallements;
    }

    private List<AccountActionDateEntity> getInstallmentDueForVolAccount(final Integer customerId) {
        List<AccountActionDateEntity> dueInstallments = new ArrayList<AccountActionDateEntity>();
        List<AccountActionDateEntity> installments = getInstallmentsDueForManAccount(customerId);
        if (installments != null && installments.size() > 0) {
            dueInstallments.add(installments.get(installments.size() - 1));
        }
        return dueInstallments;

    }

    private Money getTotalPaymentDueForManAccount(final Integer customerId) {
        List<AccountActionDateEntity> dueInstallments = getInstallmentsDueForManAccount(customerId);
        Money dueAmount = new Money(getCurrency());
        if (dueInstallments != null && dueInstallments.size() > 0) {
            for (AccountActionDateEntity installment : dueInstallments) {
                dueAmount = dueAmount.add(getDueAmount(installment));
            }
        }
        return dueAmount;
    }

    private Money getTotalPaymentDueForVolAccount(final Integer customerId) {
        List<AccountActionDateEntity> dueInstallments = getInstallmentDueForVolAccount(customerId);
        Money dueAmount = new Money(getCurrency());
        if (dueInstallments != null && dueInstallments.size() > 0) {
            dueAmount = getDueAmount(dueInstallments.get(0));
        }
        return dueAmount;
    }

    public void getSavingPerformanceHistory() throws AccountException {
        try {
            String systemDate = DateUtils.getCurrentDate();
            java.sql.Date currentDate = DateUtils.getLocaleDate(systemDate);
            getSavingsPerformance().addMissedDeposits(
                    getSavingsPersistence().getMissedDeposits(getAccountId(), currentDate));
            getSavingsPerformance().addMissedDeposits(
                    getSavingsPersistence().getMissedDepositsPaidAfterDueDate(getAccountId()));
        } catch (InvalidDateException ide) {
            throw new AccountException(ide);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    public void generateNextSetOfMeetingDates(final List<Days> workingDays, final List<Holiday> holidays)
            throws AccountException {
        CustomerBO customerBO = getCustomer();
        if (customerBO.getCustomerMeeting() != null && customerBO.getCustomerMeeting().getMeeting() != null) {

            MeetingBO depositSchedule = customerBO.getCustomerMeeting().getMeeting();

            Date oldMeetingDate = depositSchedule.getStartDate();
            Short lastInstallmentId = getLastInstallmentId();
            AccountActionDateEntity lastInstallment = getAccountActionDate(lastInstallmentId);
            depositSchedule.setMeetingStartDate(lastInstallment.getActionDate());

            if (customerBO.getCustomerLevel().getId().equals(CustomerLevel.CLIENT.getValue())
                    || customerBO.getCustomerLevel().getId().equals(CustomerLevel.GROUP.getValue())
                    && getRecommendedAmntUnit().getId().equals(RecommendedAmountUnit.COMPLETE_GROUP.getValue())) {
                generateDepositAccountActions(customerBO, depositSchedule, lastInstallment, workingDays, holidays);
            } else {
                List<CustomerBO> children;
                try {
                    children = getCustomer().getChildren(CustomerLevel.CLIENT, ChildrenStateType.OTHER_THAN_CLOSED);
                } catch (CustomerException ce) {
                    throw new AccountException(ce);
                }
                for (CustomerBO customer : children) {
                    generateDepositAccountActions(customer, depositSchedule, lastInstallment, workingDays, holidays);
                }
            }
            depositSchedule.setStartDate(oldMeetingDate);
        }
    }

    @Override
    public Date getNextMeetingDate() {
        AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();

        return nextAccountAction != null ? nextAccountAction.getActionDate() : null;
    }

    @Override
    public boolean isTrxnDateValid(Date trxnDate) throws AccountException {
        if (AccountingRules.isBackDatedTxnAllowed()) {
            Date meetingDate = null;
            trxnDate = DateUtils.getDateWithoutTimeStamp(trxnDate.getTime());
            try {
                meetingDate = getCustomerPersistence().getLastMeetingDateForCustomer(getCustomer().getCustomerId());
                if (meetingDate != null) {
                    meetingDate = DateUtils.getDateWithoutTimeStamp(meetingDate.getTime());
                    return trxnDate.compareTo(meetingDate) >= 0 ? true : false;
                }
                Date activationDate = DateUtils.getDateWithoutTimeStamp(getActivationDate().getTime());
                return trxnDate.compareTo(activationDate) >= 0
                        && trxnDate.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) <= 0 ? true : false;

            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
        return trxnDate.equals(DateUtils.getCurrentDateWithoutTimeStamp());

    }

    public boolean isOfProductOffering(final SavingsOfferingBO productOffering) {
        return savingsOffering.equals(productOffering);
    }

    private boolean depositAmountIsInExcess(final Money amountRemaining) {
        return amountRemaining != null && amountRemaining.isGreaterThanZero();
    }

    private List<SavingsScheduleEntity> findAllUnpaidInstallmentsForPayingCustomerUpTo(final Date transactionDate,
            final Integer customerId) {

        final List<SavingsScheduleEntity> customerSchedulePayments = new ArrayList<SavingsScheduleEntity>();
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            if (accountActionDateEntity != null && !accountActionDateEntity.isPaid()
                    && !accountActionDateEntity.getActionDate().after(transactionDate)) {

                if (customerId == accountActionDateEntity.getCustomer().getCustomerId()) {
                    customerSchedulePayments.add((SavingsScheduleEntity) accountActionDateEntity);
                }
            }
        }

        return customerSchedulePayments;
    }

    /*
     * In order to do audit logging, we need to get the name of the PaymentTypeEntity. A new instance constructed with
     * the paymentTypeId is not good enough for this, we need to get the lookup value loaded so that we can resolve the
     * name of the PaymentTypeEntity.
     */
    private PaymentTypeEntity getPaymentTypeEntity(final short paymentTypeId) {
        return (PaymentTypeEntity) getSavingsPersistence().loadPersistentObject(PaymentTypeEntity.class, paymentTypeId);
    }

    @Override
    public MeetingBO getMeetingForAccount() {
        return getCustomer().getCustomerMeetingValue();
    }
}
