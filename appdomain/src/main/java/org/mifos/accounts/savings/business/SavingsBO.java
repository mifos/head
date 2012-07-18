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

package org.mifos.accounts.savings.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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
import org.mifos.accounts.business.AccountNotesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.business.AccountStatusChangeHistoryEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.financial.business.service.activity.BaseFinancialActivity;
import org.mifos.accounts.financial.business.service.activity.SavingsInterestPostingFinancialActivity;
import org.mifos.accounts.financial.exceptions.FinancialException;
import org.mifos.accounts.productdefinition.business.InterestCalcTypeEntity;
import org.mifos.accounts.productdefinition.business.RecommendedAmntUnitEntity;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsTypeEntity;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.savings.interest.CalendarPeriod;
import org.mifos.accounts.savings.interest.InterestPostingPeriodResult;
import org.mifos.accounts.savings.interest.SavingsProductHistoricalInterestDetail;
import org.mifos.accounts.savings.interest.schedule.InterestScheduledEvent;
import org.mifos.accounts.savings.interest.schedule.SavingsInterestScheduledEventFactory;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.util.helpers.SavingsHelper;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.accounts.util.helpers.AccountPaymentData;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.calendar.CalendarEvent;
import org.mifos.clientportfolio.newloan.domain.CreationDetail;
import org.mifos.config.AccountingRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerNoteDto;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.dto.domain.SavingsPerformanceHistoryDto;
import org.mifos.dto.screen.SavingsRecentActivityDto;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.schedule.ScheduledDateGeneration;
import org.mifos.schedule.ScheduledEvent;
import org.mifos.schedule.ScheduledEventFactory;
import org.mifos.schedule.internal.HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A savings account is ...
 */
public class SavingsBO extends AccountBO {

    private static final Logger logger = LoggerFactory.getLogger(SavingsBO.class);

    private Money recommendedAmount;
    private RecommendedAmntUnitEntity recommendedAmntUnit;

    private Money savingsBalance;
    private Date activationDate;
    private Money interestToBePosted;
    private Date lastIntPostDate;
    private Date nextIntPostDate;

    private SavingsOfferingBO savingsOffering;
    private SavingsPerformanceEntity savingsPerformance;

    // the follow three fields cant be null at present on savings_account table so populate them for now but dont use em.
    private SavingsTypeEntity savingsType;
    private InterestCalcTypeEntity interestCalcType;
    private Double interestRate;

    private Set<SavingsActivityEntity> savingsActivityDetails = new LinkedHashSet<SavingsActivityEntity>();

    private final SavingsHelper helper = new SavingsHelper();
    private SavingsTransactionActivityHelper savingsTransactionActivityHelper = new SavingsTransactionActivityHelperImpl();
    private SavingsPaymentStrategy savingsPaymentStrategy = new SavingsPaymentStrategyImpl(savingsTransactionActivityHelper);
    private SavingsPersistence savingsPersistence = null;

    @Deprecated
    public SavingsPersistence getSavingsPersistence() {
        if (null == savingsPersistence) {
            savingsPersistence = new SavingsPersistence();
        }
        return savingsPersistence;
    }

    @Deprecated
    public void setSavingsPersistence(final SavingsPersistence savingsPersistence) {
        this.savingsPersistence = savingsPersistence;
    }

    private LegacyPersonnelDao personnelPersistence = null;

    @Deprecated
    public LegacyPersonnelDao getPersonnelPersistence() {
        if (null == personnelPersistence) {
            personnelPersistence = ApplicationContextProvider.getBean(LegacyPersonnelDao.class);
        }
        return personnelPersistence;
    }

    @Deprecated
    public void setPersonnelPersistence(final LegacyPersonnelDao personnelPersistence) {
        this.personnelPersistence = personnelPersistence;
    }

    private CustomerPersistence customerPersistence;

    @Deprecated
    public CustomerPersistence getCustomerPersistence() {
        if (null == customerPersistence) {
            customerPersistence = new CustomerPersistence();
        }
        return customerPersistence;
    }

    @Deprecated
    public void setCustomerPersistence(final CustomerPersistence customerPersistence) {
        this.customerPersistence = customerPersistence;
    }

    /**
     * Responsible for creating savings account in valid initial state.
     */
    public static SavingsBO createOpeningBalanceIndividualSavingsAccount(CustomerBO customer, SavingsOfferingBO savingsProduct,
            Money recommendedOrMandatoryAmount, AccountState savingsAccountState, LocalDate createdDate, Integer createdById,
            SavingsAccountActivationDetail activationDetails, PersonnelBO createdBy, Money openingBalance) {

        RecommendedAmountUnit recommendedAmountUnit = RecommendedAmountUnit.COMPLETE_GROUP;
        CreationDetail creationDetail = new CreationDetail(createdDate.toDateMidnight().toDateTime(), createdById);
        SavingsBO savingsAccount = new SavingsBO(savingsAccountState, customer, activationDetails, creationDetail, savingsProduct, recommendedAmountUnit, recommendedOrMandatoryAmount, createdBy, openingBalance);

        return savingsAccount;
    }

    /**
     * Responsible for creating savings account in valid initial state.
     */
    public static SavingsBO createIndividalSavingsAccount(CustomerBO customer, SavingsOfferingBO savingsProduct,
            Money recommendedOrMandatoryAmount, AccountState savingsAccountState, LocalDate createdDate, Integer createdById,
            CalendarEvent calendarEvents, PersonnelBO createdBy) {

        LocalDate activationDate = new LocalDate();
        SavingsAccountActivationDetail activationDetails = determineAccountActivationDetails(customer, savingsProduct, recommendedOrMandatoryAmount, savingsAccountState, calendarEvents, activationDate);

        Money startingBalance = Money.zero(savingsProduct.getCurrency());
        RecommendedAmountUnit recommendedAmountUnit = RecommendedAmountUnit.COMPLETE_GROUP;
        CreationDetail creationDetail = new CreationDetail(createdDate.toDateMidnight().toDateTime(), createdById);
        SavingsBO savingsAccount = new SavingsBO(savingsAccountState, customer, activationDetails, creationDetail, savingsProduct, recommendedAmountUnit, recommendedOrMandatoryAmount, createdBy, startingBalance);

        return savingsAccount;
    }

    public static SavingsBO createJointSavingsAccount(CustomerBO customer, SavingsOfferingBO savingsProduct,
            Money recommendedOrMandatoryAmount, AccountState savingsAccountState, LocalDate createdDate, Integer createdById,
            CalendarEvent calendarEvents, PersonnelBO createdBy, List<CustomerBO> activeAndOnHoldClients) {

        SavingsAccountActivationDetail activationDetails = determineAccountActivationDetails(customer, savingsProduct, recommendedOrMandatoryAmount, savingsAccountState, calendarEvents, activeAndOnHoldClients);

        Money startingBalance = Money.zero(savingsProduct.getCurrency());
        RecommendedAmountUnit recommendedAmountUnit = RecommendedAmountUnit.PER_INDIVIDUAL;
        CreationDetail creationDetail = new CreationDetail(createdDate.toDateMidnight().toDateTime(), createdById);
        SavingsBO savingsAccount = new SavingsBO(savingsAccountState, customer, activationDetails, creationDetail, savingsProduct, recommendedAmountUnit, recommendedOrMandatoryAmount, createdBy, startingBalance);

        return savingsAccount;
    }

    /**
     * valid minimal legal constructor
     */
    public SavingsBO(AccountState savingsAccountState, CustomerBO customer, SavingsAccountActivationDetail activationDetails, CreationDetail creationDetail, SavingsOfferingBO savingsProduct,
            RecommendedAmountUnit recommendedAmountUnit, Money recommendedOrMandatoryAmount, PersonnelBO createdBy, Money startingBalance) {
        super(AccountTypes.SAVINGS_ACCOUNT, savingsAccountState, customer, activationDetails.getScheduledPayments(), creationDetail);
        this.savingsOffering = savingsProduct;
        this.recommendedAmntUnit = new RecommendedAmntUnitEntity(recommendedAmountUnit);
        this.recommendedAmount = recommendedOrMandatoryAmount;
        this.savingsBalance = startingBalance;
        this.savingsPerformance = new SavingsPerformanceEntity(this);

        // inherited from savings product for now but should be removed and cleaned up.
        this.interestRate = this.savingsOffering.getInterestRate();
        this.interestCalcType = new InterestCalcTypeEntity(InterestCalcType.fromInt(this.savingsOffering.getInterestCalcType().getId()));
        this.savingsType = new SavingsTypeEntity(this.savingsOffering.getSavingsTypeAsEnum());

        if (savingsAccountState.isActiveSavingsAccountState()) {
            this.activationDate = activationDetails.getActivationDate().toDateMidnight().toDate();
            this.nextIntPostDate = activationDetails.getNextInterestPostingDate().toDateMidnight().toDate();
        }

        AccountStateEntity newStatus = new AccountStateEntity(savingsAccountState);
        AccountStatusChangeHistoryEntity statusChange = new AccountStatusChangeHistoryEntity(null, newStatus, createdBy, this);
        this.accountStatusChangeHistory.add(statusChange);
    }

    /**
     * default constructor for hibernate usage
     */
    protected SavingsBO() {
        // default constructor for hibernate
    }

    public static SavingsAccountActivationDetail determineAccountActivationDetails(CustomerBO customer,
            SavingsOfferingBO savingsProduct, Money recommendedOrMandatoryAmount, AccountState savingsAccountState,
            CalendarEvent calendarEvents, List<CustomerBO> activeAndOnHoldClients) {

        List<AccountActionDateEntity> scheduledPayments = new ArrayList<AccountActionDateEntity>();
        LocalDate activationDate = new LocalDate();
        LocalDate nextInterestPostingDate = new LocalDate();

        if (savingsAccountState.isActiveSavingsAccountState()) {
            activationDate = new LocalDate();
            ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(customer.getCustomerMeetingValue());
            ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(calendarEvents.getWorkingDays(), calendarEvents.getHolidays());

            for (CustomerBO client : activeAndOnHoldClients) {
                List<DateTime> depositDates = dateGeneration.generateScheduledDates(10, activationDate.toDateTimeAtStartOfDay(), scheduledEvent, false);

                short installmentNumber = 1;
                for (DateTime date : depositDates) {
                    java.sql.Date depositDueDate = new java.sql.Date(date.toDate().getTime());
                    AccountActionDateEntity scheduledSavingsDeposit = new SavingsScheduleEntity(client, installmentNumber,
                            depositDueDate, PaymentStatus.UNPAID, recommendedOrMandatoryAmount, savingsProduct.getCurrency());
                    scheduledPayments.add(scheduledSavingsDeposit);
                }
            }

            InterestScheduledEvent interestPostingEvent = new SavingsInterestScheduledEventFactory().createScheduledEventFrom(savingsProduct.getFreqOfPostIntcalc().getMeeting());
            nextInterestPostingDate = interestPostingEvent.nextMatchingDateAfter(new LocalDate(startOfFiscalYear()),activationDate);
        }

        return new SavingsAccountActivationDetail(activationDate, nextInterestPostingDate, scheduledPayments);

    }

    public static SavingsAccountActivationDetail determineAccountActivationDetails(CustomerBO customer, SavingsOfferingBO savingsProduct,
            Money recommendedOrMandatoryAmount, AccountState savingsAccountState, CalendarEvent calendarEvents, LocalDate activationDate) {

        List<AccountActionDateEntity> scheduledPayments = new ArrayList<AccountActionDateEntity>();
        LocalDate nextInterestPostingDate = new LocalDate();

        if (savingsAccountState.isActiveSavingsAccountState()) {
            ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(customer.getCustomerMeetingValue());
            ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(calendarEvents.getWorkingDays(), calendarEvents.getHolidays());

            List<DateTime> depositDates = dateGeneration.generateScheduledDates(10, activationDate.toDateTimeAtStartOfDay(), scheduledEvent, false);

            short installmentNumber = 1;
            for (DateTime date : depositDates) {
                java.sql.Date depositDueDate = new java.sql.Date(date.toDate().getTime());
                AccountActionDateEntity scheduledSavingsDeposit = new SavingsScheduleEntity(customer, installmentNumber,
                        depositDueDate, PaymentStatus.UNPAID, recommendedOrMandatoryAmount, savingsProduct.getCurrency());
                scheduledPayments.add(scheduledSavingsDeposit);
            }

            InterestScheduledEvent interestPostingEvent = new SavingsInterestScheduledEventFactory().createScheduledEventFrom(savingsProduct.getFreqOfPostIntcalc().getMeeting());
            nextInterestPostingDate = interestPostingEvent.nextMatchingDateAfter(new LocalDate(startOfFiscalYear()), activationDate);
        }

        return new SavingsAccountActivationDetail(activationDate, nextInterestPostingDate, scheduledPayments);
    }

    /**
     * @deprecated use minimal legal constructor from builder
     * create a constructor that doesnt take customFields or delegate to goActiveForFristTimeAndGenerateSavingsSchedule which contains persistence.
     */
    @Deprecated
    public SavingsBO(final UserContext userContext, final SavingsOfferingBO savingsOffering, final CustomerBO customer,
            final AccountState accountState, final Money recommendedAmount, final List<CustomFieldDto> customFields)
            throws AccountException {
        super(userContext, customer, AccountTypes.SAVINGS_ACCOUNT, accountState);

        this.savingsOffering = savingsOffering;
        this.savingsPerformance = createSavingsPerformance();
        this.savingsBalance = Money.zero();

        // inherit details from savings product definition
        this.interestRate = this.savingsOffering.getInterestRate();
        this.interestCalcType = new InterestCalcTypeEntity(InterestCalcType.fromInt(this.savingsOffering.getInterestCalcType().getId()));
        this.savingsType = new SavingsTypeEntity(this.savingsOffering.getSavingsTypeAsEnum());

        this.recommendedAmntUnit = savingsOffering.getRecommendedAmntUnit();
        this.recommendedAmount = recommendedAmount;

        // FIXME - keithw - create constructor that does not take DTO of customFields
        try {
            addcustomFields(customFields);
        } catch (InvalidDateException e) {
            throw new AccountException(e);
        }

        // generated the deposit action dates only if savings account is being
        // saved in approved state
        if (isActive()) {
            goActiveForFristTimeAndGenerateSavingsSchedule(customer);
        }
    }

    /**
     * use minimal constructor which generates scheduled savings payments correctly and does not
     * do through this method.
     */
    @Deprecated
    private void goActiveForFristTimeAndGenerateSavingsSchedule(final CustomerBO customer) throws AccountException {

        HolidayDao holidayDao = ApplicationContextProvider.getBean(HolidayDao.class);

        CalendarEvent futureCalendarEventsApplicableToOffice = holidayDao.findCalendarEventsForThisYearAndNext(customer
                .getOfficeId());
        this.activationDate = new DateTime(new DateTimeService().getCurrentJavaDateTime()).toDate();
        List<Days> workingDays = futureCalendarEventsApplicableToOffice.getWorkingDays();
        List<Holiday> holidays = futureCalendarEventsApplicableToOffice.getHolidays();

        logger.debug("In SavingsBO::generateDepositAccountActions()");
        // deposit happens on each meeting date of the customer. If for
        // center/group with individual deposits, insert row for every client
        if (this.getCustomer().getCustomerMeeting() != null && this.getCustomer().getCustomerMeeting().getMeeting() != null) {
            MeetingBO depositSchedule = this.getCustomer().getCustomerMeeting().getMeeting();

            if (this.getCustomer().getCustomerLevel().getId().equals(CustomerLevel.CLIENT.getValue())
                    || this.getCustomer().getCustomerLevel().getId().equals(CustomerLevel.GROUP.getValue())
                    && this.getRecommendedAmntUnit().getId().equals(RecommendedAmountUnit.COMPLETE_GROUP.getValue())) {
                this.generateDepositAccountActions(this.getCustomer(), depositSchedule, workingDays, holidays, new DateTime(this.activationDate));
            } else {
                List<CustomerBO> children;
                try {
                    children = this.getCustomer().getChildren(CustomerLevel.CLIENT, ChildrenStateType.ACTIVE_AND_ONHOLD);
                } catch (CustomerException ce) {
                    throw new AccountException(ce);
                }
                for (CustomerBO customer1 : children) {
                    this.generateDepositAccountActions(customer1, depositSchedule, workingDays, holidays, new DateTime(this.activationDate));
                }
            }
        }

        InterestScheduledEvent interestPostingEvent = new SavingsInterestScheduledEventFactory()
                .createScheduledEventFrom(this.savingsOffering.getFreqOfPostIntcalc().getMeeting());
        this.nextIntPostDate = interestPostingEvent.nextMatchingDateAfter(new LocalDate(startOfFiscalYear()),
                new LocalDate(this.activationDate)).toDateMidnight().toDate();
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

    public void setSavingsBalance(final Money savingsBalance) {
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

    public void setActivationDate(final Date activationDate) {
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

    public Money getInterestToBePosted() {
        return interestToBePosted;
    }

    public void setInterestToBePosted(final Money interestToBePosted) {
        this.interestToBePosted = interestToBePosted;
    }

    public Date getLastIntPostDate() {
        return lastIntPostDate;
    }

    void setLastIntPostDate(final Date lastIntPostDate) {
        this.lastIntPostDate = lastIntPostDate;
    }

    public Date getNextIntPostDate() {
        return nextIntPostDate;
    }

    public void setNextIntPostDate(final Date nextIntPostDate) {
        this.nextIntPostDate = nextIntPostDate;
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

    /**
     * @deprecated use {@link SavingsDao#save(SavingsBO)} to persist savings account.
     */
    @Deprecated
    public void save() throws AccountException {
        logger.info("In SavingsBO::save(), Before Saving , accountId: " + getAccountId());

        try {
            this.addAccountStatusChangeHistory(new AccountStatusChangeHistoryEntity(this.getAccountState(), this
                    .getAccountState(), getPersonnelPersistence().getPersonnel(userContext.getId()), this));

            getSavingsPersistence().createOrUpdate(this);
            OfficeBO branch = getSavingsPersistence().getPersistentObject(OfficeBO.class, userContext.getBranchId());
            this.globalAccountNum = generateId(branch.getGlobalOfficeNum());

            getSavingsPersistence().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        logger.info("In SavingsBO::save(), Successfully saved , accountId: " + getAccountId());
    }

    public void update(final Money recommendedAmount, final Set<AccountCustomFieldEntity> accountCustomFields) {

        if (isDepositScheduleBeRegenerated()) {
            if (this.recommendedAmount != null && recommendedAmount != null && !this.recommendedAmount.equals(recommendedAmount)) {
                for (AccountActionDateEntity scheduledDeposit : this.getAccountActionDates()) {
                    if (scheduledDeposit.isOnOrAfter(new LocalDate())) {
                        ((SavingsScheduleEntity) scheduledDeposit).setDeposit(recommendedAmount);
                    }
                }
            }
        }
        this.recommendedAmount = recommendedAmount;
        this.setAccountCustomFields(accountCustomFields);
    }

    public boolean isMandatory() {
        return this.savingsOffering.isMandatory();
    }

    public boolean isVoluntary() {
        return this.savingsOffering.isVoluntary();
    }

    public boolean isDepositScheduleBeRegenerated() {
        return getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_APPROVED
                || getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_INACTIVE;
    }

    public boolean isActive() {
        return AccountState.SAVINGS_ACTIVE.getValue().equals(this.getAccountState().getId());
    }

    public boolean isInActive() {
        return AccountState.SAVINGS_INACTIVE.getValue().equals(this.getAccountState().getId());
    }

    public void postInterest(InterestScheduledEvent postingSchedule, InterestPostingPeriodResult interestPostingPeriodResult, PersonnelBO createdBy) {

        Money actualInterestToBePosted = interestPostingPeriodResult.getDifferenceInInterest();

        LocalDate currentPostingDate = interestPostingPeriodResult.getPostingPeriod().getEndDate();
        LocalDate nextPostingDate = postingSchedule.nextMatchingDateFromAlreadyMatchingDate(currentPostingDate);

        doPostInterest(currentPostingDate, actualInterestToBePosted, createdBy);
        updatePostingDetails(nextPostingDate);
    }

    private void doPostInterest(LocalDate currentPostingDate, Money actualInterestToBePosted, PersonnelBO loggedInUser) {
        this.savingsBalance = this.savingsBalance.add(actualInterestToBePosted);
        this.savingsPerformance.setTotalInterestDetails(actualInterestToBePosted);

        SavingsActivityEntity savingsActivity = SavingsActivityEntity.savingsInterestPosting(this, personnel, this.savingsBalance, actualInterestToBePosted, currentPostingDate.toDateMidnight().toDate());
        savingsActivityDetails.add(savingsActivity);

        AccountPaymentEntity interestPayment = AccountPaymentEntity.savingsInterestPosting(this, actualInterestToBePosted, currentPostingDate.toDateMidnight().toDate(), loggedInUser);

        DateTime dueDate = new DateTime();
        SavingsTrxnDetailEntity interestPostingTransaction = SavingsTrxnDetailEntity.savingsInterestPosting(interestPayment, this.customer, this.savingsBalance, currentPostingDate.toDateMidnight().toDate(), dueDate, loggedInUser);

        interestPayment.addAccountTrxn(interestPostingTransaction);
        this.addAccountPayment(interestPayment);

        // NOTE: financial Transaction Processing should be decoupled from application domain model.
        try {
            BaseFinancialActivity baseFinancialActivity = new SavingsInterestPostingFinancialActivity(interestPostingTransaction);
            baseFinancialActivity.buildAccountEntries();
        } catch (FinancialException e) {
            throw new MifosRuntimeException(e);
        }
    }

    public void updatePostingDetails(LocalDate nextPostingDate) {
        this.lastIntPostDate = this.nextIntPostDate;
        this.nextIntPostDate = nextPostingDate.toDateMidnight().toDate();
        this.interestToBePosted = Money.zero(this.getCurrency());
    }

    public void closeAccount(final AccountPaymentEntity payment, final AccountNotesEntity notes, final CustomerBO customer, PersonnelBO loggedInUser) {

        AccountStateEntity previousAccountState = this.getAccountState();
        AccountStateEntity closedAccountState = new AccountStateEntity(AccountState.SAVINGS_CLOSED);

        AccountStatusChangeHistoryEntity statusChangeHistory = new AccountStatusChangeHistoryEntity(
                previousAccountState, closedAccountState, loggedInUser, this);
        this.addAccountStatusChangeHistory(statusChangeHistory);
        this.setAccountState(closedAccountState);

        Money interestOutstanding = payment.getAmount().subtract(this.savingsBalance);

        if (interestOutstanding.isGreaterThanZero()) {
            LocalDate currentPostingDate = new LocalDate(payment.getPaymentDate());
            LocalDate nextPostingDate = new LocalDate();
            doPostInterest(currentPostingDate, interestOutstanding, loggedInUser);
            updatePostingDetails(nextPostingDate);
        }

        Date transactionDate = new DateTimeService().getCurrentDateMidnight().toDate();
        if (payment.getAmount().isGreaterThanZero()) {

            SavingsTrxnDetailEntity withdrawal = SavingsTrxnDetailEntity.savingsWithdrawal(payment, customer,
                    this.savingsBalance, payment.getAmount(), loggedInUser, transactionDate, transactionDate,
                    transactionDate);

            payment.addAccountTrxn(withdrawal);

            this.addAccountPayment(payment);

            SavingsActivityEntity interestPostingActivity = SavingsActivityEntity.savingsWithdrawal(this, loggedInUser,
                    this.savingsBalance, payment.getAmount(), payment.getPaymentDate());
            savingsActivityDetails.add(interestPostingActivity);

            this.savingsPerformance.setTotalWithdrawals(this.savingsPerformance.getTotalWithdrawals().add(
                    payment.getAmount()));

            try {
                buildFinancialEntries(payment.getAccountTrxns());
            } catch (AccountException e) {
                throw new BusinessRuleException(e.getKey(), e);
            }
        }

        this.addAccountNotes(notes);
//        this.lastIntCalcDate = transactionDate;
        this.lastIntPostDate = transactionDate;
//        this.interIntCalcDate = null;
        this.savingsBalance = new Money(getCurrency());
        this.interestToBePosted = new Money(getCurrency());
        this.setClosedDate(new DateTimeService().getCurrentJavaDateTime());
    }

    /**
     * remove after usuage in constructor is removed.
     */
    @Deprecated
    public void generateAndUpdateDepositActionsForClient(final ClientBO client, final List<Days> workingDays,
            final List<Holiday> holidays) throws AccountException {

        if (client.getCustomerMeeting().getMeeting() != null) {
            if (!(getCustomer().getLevel() == CustomerLevel.GROUP && getRecommendedAmntUnit().getId().equals(
                    RecommendedAmountUnit.COMPLETE_GROUP.getValue()))) {
                DateTime today = new DateTime().toDateMidnight().toDateTime();
                generateDepositAccountActions(client, client.getCustomerMeeting().getMeeting(), workingDays, holidays, today);
                this.update();
            }
        }
    }

    @Deprecated
    public void generateDepositAccountActions(final CustomerBO customer, final MeetingBO meeting,
            final List<Days> workingDays, final List<Holiday> holidays, final DateTime startingFrom) {

        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);
        ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(
                workingDays, holidays);

        List<DateTime> depositDates = dateGeneration.generateScheduledDates(10, startingFrom, scheduledEvent, false);

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
        ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(
                workingDays, holidays);
        List<DateTime> depositDates = dateGeneration.generateScheduledDates(10,
                startFromDayAfterLastKnownInstallmentDate, scheduledEvent, false);

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

        if(paymentData.getCustomer() == null) {
            throw new NullPointerException("Customer in payment data during payment should not be null");
        }

        CustomerBO customer = paymentData.getCustomer();
        AccountPaymentEntity accountPayment = new AccountPaymentEntity(this, totalAmount, paymentData.getReceiptNum(),
                paymentData.getReceiptDate(), getPaymentTypeEntity(paymentData.getPaymentTypeId()), transactionDate);
        accountPayment.setCreatedByUser(paymentData.getPersonnel());
        accountPayment.setComment(paymentData.getComment());

        // make savings account active if inactive
        if (this.getState().getValue().equals(AccountState.SAVINGS_INACTIVE.getValue())) {
            this.changeStatus(AccountState.SAVINGS_ACTIVE, null, "Account Made Active Due to Payment", paymentData.getPersonnel());
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
                if (this.isVoluntary() && depositAmountIsInExcess(depositAmount)) {
                    paymentStatus = PaymentStatus.PAID;
                }
                savingsBalance = savingsBalance.add(depositAmount);

                savingsPerformance.setPaymentDetails(depositAmount);
                accountAction.setPaymentDetails(depositAmount, paymentStatus, new java.sql.Date(transactionDate
                        .getTime()));

                Short installmentId = accountAction.getInstallmentId();
                SavingsTrxnDetailEntity accountTrxn = SavingsTrxnDetailEntity.savingsDeposit(accountPayment, customer, this.savingsBalance,
                        depositAmount, paymentData.getPersonnel(), accountAction.getActionDate(), paymentData.getTransactionDate(), paymentData.getTransactionDate(), installmentId);

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
            final Date transactionDate) {

        savingsBalance = savingsBalance.add(depositAmount);
        savingsPerformance.setPaymentDetails(depositAmount);

        Short installmentId = null;
        return SavingsTrxnDetailEntity.savingsDeposit(accountPayment, customer, this.savingsBalance,
                depositAmount, personnel, transactionDate, transactionDate, transactionDate, installmentId);
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
        final SavingsActivityEntity savingsActivity = savingsTransactionActivityHelper.createSavingsActivityForDeposit(
                payment.getCreatedByUser(), amountToDeposit, this.savingsBalance, transactionDate, this);

        addSavingsActivityDetails(savingsActivity);
        addAccountPayment(payment);

        // make savings account active if inactive
        if (this.getState().getValue().equals(AccountState.SAVINGS_INACTIVE.getValue())) {
            this.changeStatus(AccountState.SAVINGS_ACTIVE, null, "Account Made Active Due to Payment", payment.getCreatedByUser());
        }

        final List<SavingsScheduleEntity> unpaidDepositsForPayingCustomer = findAllUnpaidInstallmentsForPayingCustomerUpTo(
                transactionDate, payingCustomer.getCustomerId());

        // make scheduled payments (if any) and make an unscheduled payment
        // with any amount remaining
        final Money amountRemaining = this.savingsPaymentStrategy.makeScheduledPayments(payment,
                unpaidDepositsForPayingCustomer, payingCustomer, this.savingsOffering.getSavingsTypeAsEnum(),
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

        // make savings account active if inactive
        if (this.getState().getValue().equals(AccountState.SAVINGS_INACTIVE.getValue())) {
            this.changeStatus(AccountState.SAVINGS_ACTIVE, null, "Account Made Active Due to Payment", payment.getCreatedByUser());
        }
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
    public AccountPaymentEntity withdraw(final PaymentData accountPaymentData, final boolean persist) throws AccountException {
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

        SavingsTrxnDetailEntity accountTrxnBO = SavingsTrxnDetailEntity.savingsWithdrawal(accountPayment, customer, this.savingsBalance,
                totalAmount, accountPaymentData.getPersonnel(), accountPaymentData.getTransactionDate(), accountPaymentData.getTransactionDate(), accountPaymentData.getTransactionDate());
        accountPayment.addAccountTrxn(accountTrxnBO);

        addAccountPayment(accountPayment);
        addSavingsActivityDetails(buildSavingsActivity(totalAmount, getSavingsBalance(),
                AccountActionTypes.SAVINGS_WITHDRAWAL.getValue(), accountPaymentData.getTransactionDate(),
                accountPaymentData.getPersonnel()));
        buildFinancialEntries(accountPayment.getAccountTrxns());

        // make savings account active if inactive
        if (this.getState().getValue().equals(AccountState.SAVINGS_INACTIVE.getValue())) {
            this.changeStatus(AccountState.SAVINGS_ACTIVE, null, "Account Made Active Due to Payment", accountPayment.getCreatedByUser());
        }

        if (persist) {
            try {
                getSavingsPersistence().createOrUpdate(this);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
        return accountPayment;
    }

    @Override
    protected void activationDateHelper(final Short newStatusId) throws AccountException {

        if (ProcessFlowRules.isSavingsPendingApprovalStateEnabled()) {
            if (this.getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_PENDINGAPPROVAL
                    && newStatusId.shortValue() == AccountStates.SAVINGS_ACC_APPROVED) {

                goActiveForFristTimeAndGenerateSavingsSchedule(customer);
            }
        } else {
            if (this.getAccountState().getId().shortValue() == AccountStates.SAVINGS_ACC_PARTIALAPPLICATION
                    && newStatusId.shortValue() == AccountStates.SAVINGS_ACC_APPROVED) {
                goActiveForFristTimeAndGenerateSavingsSchedule(customer);
            }
        }
    }

    public AccountPaymentEntity adjustLastUserAction(Money amountAdjustedTo, String adjustmentNote,
            PersonnelBO updatedBy) {
        AccountPaymentEntity lastPayment = getLastPmnt();
        return adjustUserAction(amountAdjustedTo, adjustmentNote, new LocalDate(lastPayment.getPaymentDate()), updatedBy, lastPayment);
    }

    public AccountPaymentEntity adjustUserAction(Money amountAdjustedTo, String adjustmentNote, LocalDate adjustmentDate,
            PersonnelBO updatedBy, Integer paymentId) {
        AccountPaymentEntity payment = findPaymentById(paymentId);
        return adjustUserAction(amountAdjustedTo, adjustmentNote, adjustmentDate, updatedBy, payment);
    }

    private AccountPaymentEntity adjustUserAction(Money amountAdjustedTo, String adjustmentNote, LocalDate adjustmentDate,
            PersonnelBO updatedBy, AccountPaymentEntity payment) {
        AccountPaymentEntity newPayment = null;
        try {
            if (!isAdjustPossibleOnTrxn(amountAdjustedTo, payment)) {
                throw new BusinessRuleException(AccountExceptionConstants.CANNOTADJUST);
            }

            AccountActionTypes savingsTransactionType = findFirstDepositOrWithdrawalTransaction(payment);
            Date adjustedOn = new DateTimeService().getCurrentJavaDateTime();

            List<AccountTrxnEntity> reversedTransactions = reverseTransaction(adjustmentNote, updatedBy, payment,
                    savingsTransactionType, adjustedOn);

            buildFinancialEntries(new LinkedHashSet<AccountTrxnEntity>(reversedTransactions));

            if (amountAdjustedTo.isGreaterThanZero()) {
                Set<AccountTrxnEntity> adjustedPaymentTransactions = createNewAccountPaymentWithAdjustedAmount(
                        amountAdjustedTo, updatedBy, payment, savingsTransactionType, adjustedOn, adjustmentDate);

                buildFinancialEntries(adjustedPaymentTransactions);
                newPayment = adjustedPaymentTransactions.toArray(new AccountTrxnEntity[adjustedPaymentTransactions
                        .size()])[0].getAccountPayment();
            }
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }

        goActiveDueToDepositOrWithdrawalOnAccount(updatedBy);
        return newPayment;
    }

    private void goActiveDueToDepositOrWithdrawalOnAccount(PersonnelBO updatedBy) {
        if (!this.isActive()) {
            AccountStateEntity oldAccountState = this.getAccountState();
            AccountStateEntity newAccountState = new AccountStateEntity(AccountState.SAVINGS_ACTIVE);
            this.setAccountState(newAccountState);

            AccountStatusChangeHistoryEntity savingsAccountToActive = new AccountStatusChangeHistoryEntity(oldAccountState, newAccountState, updatedBy, this);
            this.accountStatusChangeHistory.add(savingsAccountToActive);
        }
    }

    private Set<AccountTrxnEntity> createNewAccountPaymentWithAdjustedAmount(Money amountAdjustedTo,
            PersonnelBO updatedBy, AccountPaymentEntity payment, AccountActionTypes savingsTransactionType,
            Date adjustedOn, LocalDate adjustmentDate) {

        AccountPaymentEntity newAccountPayment = new AccountPaymentEntity(this, amountAdjustedTo, payment.getReceiptNumber(),
                payment.getReceiptDate(), payment.getPaymentType(), adjustmentDate.toDateMidnight().toDate());
        newAccountPayment.setCreatedByUser(updatedBy);
        newAccountPayment.setAmount(amountAdjustedTo);

        Set<AccountTrxnEntity> accountTrxns = new HashSet<AccountTrxnEntity>();
        if (isMandatory() && savingsTransactionType.equals(AccountActionTypes.SAVINGS_DEPOSIT)) {
            accountTrxns = createDepositTrxnsForMandatoryAccountsAfterAdjust(newAccountPayment, payment, amountAdjustedTo, adjustmentDate, updatedBy);
        } else if (isVoluntary() && savingsTransactionType.equals(AccountActionTypes.SAVINGS_DEPOSIT)) {
            accountTrxns = createDepositTrxnsForVolAccountsAfterAdjust(newAccountPayment, payment, amountAdjustedTo, adjustmentDate, updatedBy);
        } else {
            accountTrxns = createWithdrawalTrxnsAfterAdjust(newAccountPayment, payment, amountAdjustedTo, adjustmentDate, updatedBy);
        }

        for (AccountTrxnEntity accountTrxn : accountTrxns) {
            newAccountPayment.addAccountTrxn(accountTrxn);
        }

        this.addAccountPayment(newAccountPayment);

        AccountActionEntity depositOrWithdrawalTransactionType = new AccountActionEntity(savingsTransactionType);
        SavingsActivityEntity depositOrWithdrawalActivity = new SavingsActivityEntity(updatedBy, depositOrWithdrawalTransactionType, amountAdjustedTo, this.savingsBalance, adjustedOn, this);
        this.savingsActivityDetails.add(depositOrWithdrawalActivity);
        return newAccountPayment.getAccountTrxns();
    }

    private List<AccountTrxnEntity> reverseTransaction(String adjustmentNote, PersonnelBO updatedBy,
            AccountPaymentEntity payment, AccountActionTypes savingsTransactionType, Date adjustedOn)
            throws AccountException {

        for (AccountTrxnEntity accntTrxn : payment.getAccountTrxns()) {
            if (AccountActionTypes.SAVINGS_DEPOSIT.equals(savingsTransactionType)) {
                adjustForDeposit(accntTrxn);
            } else if (AccountActionTypes.SAVINGS_WITHDRAWAL.equals(savingsTransactionType)) {
                adjustForWithdrawal(accntTrxn);
            }
        }

        SavingsActivityEntity adjustment = SavingsActivityEntity.savingsAdjustment(this, updatedBy, this.savingsBalance, payment.getAmount(), adjustedOn);
        savingsActivityDetails.add(adjustment);

        return payment.reversalAdjustment(updatedBy, adjustmentNote);
    }

    private AccountActionTypes findFirstDepositOrWithdrawalTransaction(AccountPaymentEntity lastPayment) {
        AccountActionTypes accountActionTypes = AccountActionTypes.SAVINGS_INTEREST_POSTING;
        for (AccountTrxnEntity accntTrxn : lastPayment.getAccountTrxns()) {
            if (!accntTrxn.getAccountActionEntity().isSavingsAdjustment()) {
                accountActionTypes = AccountActionTypes.fromInt(accntTrxn.getAccountActionEntity().getId());
            }
        }
        return accountActionTypes;
    }

    public AccountPaymentEntity findMostRecentDepositOrWithdrawalByDate() {
        AccountPaymentEntity mostRecentPayment = null; 
        if (!this.accountPayments.isEmpty()) {
            for (AccountPaymentEntity accountPaymentEntity : this.accountPayments) {
                if (mostRecentPayment == null && accountPaymentEntity.isSavingsDepositOrWithdrawal()) {
                    mostRecentPayment = accountPaymentEntity;
                } else if (mostRecentPayment != null) {
                    LocalDate paymentDate = new LocalDate(accountPaymentEntity.getPaymentDate());
                    if ((paymentDate.isAfter(new LocalDate(mostRecentPayment.getPaymentDate())) && paymentDate.isBefore(new LocalDate().plusDays(1))) ||
                            (paymentDate.isEqual(new LocalDate(mostRecentPayment.getPaymentDate())) &&
                                    accountPaymentEntity.getPaymentId() != null && mostRecentPayment.getPaymentId() != null &&
                                    accountPaymentEntity.getPaymentId() > mostRecentPayment.getPaymentId())
                                    && accountPaymentEntity.isSavingsDepositOrWithdrawal()) {
                        mostRecentPayment = accountPaymentEntity;
                    }
                }
            }
        }
        return mostRecentPayment;
    }
    
    private List<AccountActionDateEntity> getAccountActions(final Date dueDate, final Integer customerId) {
        List<AccountActionDateEntity> accountActions = new ArrayList<AccountActionDateEntity>();
        for (AccountActionDateEntity accountAction : getAccountActionDates()) {
            if (accountAction.getActionDate().compareTo(dueDate) <= 0 &&
                    !accountAction.isPaid() &&
                    accountAction.getCustomer().getCustomerId() == customerId) {
                accountActions.add(accountAction);
            }
        }
        return accountActions;
    }

    private Set<AccountTrxnEntity> createWithdrawalTrxnsAfterAdjust(final AccountPaymentEntity newAccountPayment,
            final AccountPaymentEntity lastAccountPayment, final Money newAmount, final LocalDate adjustmentDate, PersonnelBO loggedInUser) {

        Set<AccountTrxnEntity> newTrxns = new LinkedHashSet<AccountTrxnEntity>();
        SavingsTrxnDetailEntity accountTrxn = null;

        // create transaction for withdrawal
        SavingsTrxnDetailEntity oldSavingsAccntTrxn = null;

        for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment.getAccountTrxns()) {
            oldSavingsAccntTrxn = (SavingsTrxnDetailEntity) oldAccntTrxn;
            break;
        }

        this.savingsBalance = this.savingsBalance.subtract(newAmount);

        Date transactionCreatedDate = new DateTimeService().getCurrentJavaDateTime();
        accountTrxn = SavingsTrxnDetailEntity.savingsWithdrawal(newAccountPayment, oldSavingsAccntTrxn.getCustomer(), newAmount, newAmount, loggedInUser,
                oldSavingsAccntTrxn.getDueDate(), adjustmentDate.toDateMidnight().toDate(), transactionCreatedDate);

        this.savingsPerformance.setTotalWithdrawals(this.savingsPerformance.getTotalWithdrawals().add(accountTrxn.getWithdrawlAmount()));
        newTrxns.add(accountTrxn);
        return newTrxns;
    }

    private Set<AccountTrxnEntity> createDepositTrxnsForMandatoryAccountsAfterAdjust(
            final AccountPaymentEntity newAccountPayment, final AccountPaymentEntity lastAccountPayment, Money newAmount, LocalDate adjustmentDate, PersonnelBO createdBy) {

        Set<AccountTrxnEntity> newTrxns = new LinkedHashSet<AccountTrxnEntity>();
        SavingsTrxnDetailEntity accountTrxn = null;
        CustomerBO customer = null;
        Date trxnDate = adjustmentDate.toDateMidnight().toDate();

        for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment.getAccountTrxns()) {
            customer = oldAccntTrxn.getCustomer();
            break;
        }

        List<AccountActionDateEntity> accountActionList = getAccountActions(lastAccountPayment.getPaymentDate(), customer.getCustomerId());
        for (AccountActionDateEntity accountActionDateEntity : accountActionList) {
            SavingsScheduleEntity accountAction = (SavingsScheduleEntity) accountActionDateEntity;
            if (newAmount.isZero()) {
                break;
            }
            accountTrxn = null;

            // if payment covers required deposit
            if (accountAction.getDeposit().isLessThanOrEqual(newAmount)) {
                this.savingsBalance = this.savingsBalance.add(accountAction.getDeposit());

                Short installmentId = accountAction.getInstallmentId();
                Date dueDate = accountAction.getActionDate();
                Date transactionCreatedDate = new DateTimeService().getCurrentJavaDateTime();
                accountTrxn = SavingsTrxnDetailEntity.savingsDeposit(newAccountPayment, customer, this.savingsBalance, accountAction.getDeposit(), createdBy, dueDate, trxnDate, transactionCreatedDate, installmentId);

                newAmount = newAmount.subtract(accountAction.getDeposit());

                accountAction.setDepositPaid(accountAction.getDepositPaid().add(accountTrxn.getDepositAmount()));
                accountAction.setPaymentStatus(PaymentStatus.PAID);

            } else {
                this.savingsBalance = this.savingsBalance.add(newAmount);

                Short installmentId = accountAction.getInstallmentId();
                Date dueDate = accountAction.getActionDate();
                Date transactionCreatedDate = new DateTimeService().getCurrentJavaDateTime();
                accountTrxn = SavingsTrxnDetailEntity.savingsDeposit(newAccountPayment, customer, this.savingsBalance, newAmount, createdBy, dueDate, trxnDate, transactionCreatedDate, installmentId);

                newAmount = newAmount.subtract(newAmount);
                accountAction.setDepositPaid(accountAction.getDepositPaid().add(accountTrxn.getDepositAmount()));
                accountAction.setPaymentStatus(PaymentStatus.UNPAID);
            }
            accountAction.setPaymentDate(new DateTimeService().getCurrentJavaSqlDate());
            getSavingsPerformance().setTotalDeposits(getSavingsPerformance().getTotalDeposits().add(accountTrxn.getDepositAmount()));
            newTrxns.add(accountTrxn);
        }

        // add trxn for excess amount
        if (newAmount.isGreaterThanZero()) {
            this.savingsBalance = this.savingsBalance.add(newAmount);
            Short installmentId = null;
            Date dueDate = null;
            Date transactionCreatedDate = new DateTimeService().getCurrentJavaDateTime();
            accountTrxn = SavingsTrxnDetailEntity.savingsDeposit(newAccountPayment, customer, this.savingsBalance, newAmount, createdBy, dueDate, trxnDate, transactionCreatedDate, installmentId);

            newAmount = newAmount.subtract(newAmount);
            getSavingsPerformance().setTotalDeposits(getSavingsPerformance().getTotalDeposits().add(accountTrxn.getDepositAmount()));
            newTrxns.add(accountTrxn);
        }
        return newTrxns;
    }

    /*
     * FIXME - keithw - it doesnt make sense to be that voluntary account break up account payments into more than one account transaction
     * just because the amount deposited is greater than the 'recommended' amount.
     *
     * As a result there is no need to make a distinction between the amount deposited (be it less or greater than recommended amount)
     */
    private Set<AccountTrxnEntity> createDepositTrxnsForVolAccountsAfterAdjust(final AccountPaymentEntity newAccountPayment,
            final AccountPaymentEntity lastAccountPayment, Money newAmount, LocalDate adjustmentDate, PersonnelBO loggedInUser) {

        Set<AccountTrxnEntity> newTrxns = new LinkedHashSet<AccountTrxnEntity>();
        SavingsTrxnDetailEntity accountTrxn = null;
        CustomerBO customer = null;
        Date trxnDate = adjustmentDate.toDateMidnight().toDate();

        for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment.getAccountTrxns()) {
            customer = oldAccntTrxn.getCustomer();
            break;
        }

        Short installmentId = null;
        Date dueDate = null;
        Date transactionCreatedDate = new DateTimeService().getCurrentJavaDateTime();

        for (AccountTrxnEntity oldAccntTrxn : lastAccountPayment.getAccountTrxns()) {

            if (oldAccntTrxn.getAccountActionEntity().getId().equals(AccountActionTypes.SAVINGS_DEPOSIT.getValue())) {
                SavingsTrxnDetailEntity oldSavingsAccntTrxn = (SavingsTrxnDetailEntity) oldAccntTrxn;

                if (oldAccntTrxn.getInstallmentId() != null) {
                    SavingsScheduleEntity accountAction = (SavingsScheduleEntity) getAccountActionDate(oldSavingsAccntTrxn.getInstallmentId(), oldSavingsAccntTrxn.getCustomer().getCustomerId());

                    installmentId = accountAction.getInstallmentId();
                    dueDate = accountAction.getActionDate();

                    // if recommended amount is covered by payment
                    if (accountAction.getDeposit().isLessThanOrEqual(newAmount)) {

                        this.savingsBalance = this.savingsBalance.add(accountAction.getDeposit());

                        accountTrxn = SavingsTrxnDetailEntity.savingsDeposit(newAccountPayment, customer, this.savingsBalance, accountAction.getDeposit(), loggedInUser, dueDate, trxnDate, transactionCreatedDate, installmentId);

                        newAmount = newAmount.subtract(accountAction.getDeposit());
                        accountAction.setDepositPaid(accountAction.getDepositPaid().add(accountTrxn.getDepositAmount()));
                        accountAction.setPaymentStatus(PaymentStatus.PAID);
                        accountAction.setPaymentDate(new DateTimeService().getCurrentJavaSqlDate());
                        this.savingsPerformance.setTotalDeposits(this.savingsPerformance.getTotalDeposits().add(accountTrxn.getDepositAmount()));
                    } else if (newAmount.isNonZero()) {
                        // not zero and amount paid is less that recommended amount
                        this.savingsBalance = this.savingsBalance.add(newAmount);

                        accountTrxn = SavingsTrxnDetailEntity.savingsDeposit(newAccountPayment, customer, this.savingsBalance, newAmount, loggedInUser, dueDate, trxnDate, transactionCreatedDate, installmentId);

                        newAmount = newAmount.subtract(newAmount);
                        accountAction.setDepositPaid(accountAction.getDepositPaid().add(accountTrxn.getDepositAmount()));
                        accountAction.setPaymentStatus(PaymentStatus.UNPAID);
                        accountAction.setPaymentDate(new DateTimeService().getCurrentJavaSqlDate());
                        this.savingsPerformance.setTotalDeposits(this.savingsPerformance.getTotalDeposits().add(accountTrxn.getDepositAmount()));
                    }
                    break;
                }
            }
        }

        if (accountTrxn != null) {
            newTrxns.add(accountTrxn);
        }

        // Create a new transaction with remaining amount
        if (newAmount.isGreaterThanZero()) {
            this.savingsBalance = this.savingsBalance.add(newAmount);

            accountTrxn = SavingsTrxnDetailEntity.savingsDeposit(newAccountPayment, customer, this.savingsBalance, newAmount, loggedInUser, dueDate, trxnDate, transactionCreatedDate, installmentId);

            this.savingsPerformance.setTotalDeposits(this.savingsPerformance.getTotalDeposits().add(accountTrxn.getDepositAmount()));
            newTrxns.add(accountTrxn);
        }
        return newTrxns;
    }

    private void adjustForDeposit(final AccountTrxnEntity accntTrxn) {
        SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
        Money depositAmount = savingsTrxn.getDepositAmount();

        Short installmentId = savingsTrxn.getInstallmentId();
        this.savingsBalance = this.savingsBalance.subtract(depositAmount);

        SavingsScheduleEntity accntActionDate = (SavingsScheduleEntity) getAccountActionDate(installmentId, accntTrxn.getCustomer().getCustomerId());
        if (accntActionDate != null) {
            accntActionDate.setDepositPaid(accntActionDate.getDepositPaid().subtract(depositAmount));
            accntActionDate.setPaymentStatus(PaymentStatus.UNPAID);
            accntActionDate.setPaymentDate(null);
        }
        this.savingsPerformance.setTotalDeposits(this.savingsPerformance.getTotalDeposits().subtract(depositAmount));
    }

    private void adjustForWithdrawal(final AccountTrxnEntity accntTrxn) {
        SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;
        setSavingsBalance(getSavingsBalance().add(savingsTrxn.getWithdrawlAmount()));
        getSavingsPerformance().setTotalWithdrawals(
                getSavingsPerformance().getTotalWithdrawals().subtract(savingsTrxn.getWithdrawlAmount()));
    }

    public boolean isAdjustPossibleOnTrxn(final Money amountAdjustedTo, AccountPaymentEntity accountPayment) {

        boolean adjustmentIsPossible = false;

        if (this.isActive() || this.isInActive()) {

            if (paymentIsGreaterThanZero(accountPayment) && paymentIsADepositOrWithdrawal(accountPayment)) {
                if (accountPayment.getAmount().equals(amountAdjustedTo)) {
                    adjustmentIsPossible = false;
                } else {
                    adjustmentIsPossible = true;
                }

                if (adjustmentIsPossible && withdrawalAdjustmentIsValid(accountPayment, amountAdjustedTo)
                        && withdrawalAdjustmentDoesNotMakeBalanceNegative(accountPayment, amountAdjustedTo)) {
                    adjustmentIsPossible = true;
                } else {
                    adjustmentIsPossible = false;
                }
            }
        }

        return adjustmentIsPossible;
    }

    private boolean paymentIsADepositOrWithdrawal(AccountPaymentEntity accountPayment) {
        return accountPayment.isSavingsDepositOrWithdrawal();
    }

    private boolean paymentIsGreaterThanZero(AccountPaymentEntity accountPayment) {
        return accountPayment != null && accountPayment.getAmount().isGreaterThanZero();
    }

    private boolean withdrawalAdjustmentIsValid(final AccountPaymentEntity accountPayment, final Money withdrawalAmount) {
        boolean withdrawalAdjustmentIsValid = true;

        if (accountPayment.isSavingsWithdrawal() && withdrawalAmount != null && withdrawalAmount.isNonZero() &&
                this.savingsOffering.isMaxWithdrawalAmountExceeded(withdrawalAmount)) {
            withdrawalAdjustmentIsValid = false;
        }
        return withdrawalAdjustmentIsValid;
    }

    private boolean withdrawalAdjustmentDoesNotMakeBalanceNegative(final AccountPaymentEntity accountPayment, final Money amountAdjustedTo) {

        boolean balanceIsPositive = true;
        Money balanceAfterAdjust = this.savingsBalance;

        for (AccountTrxnEntity accntTrxn : accountPayment.getAccountTrxns()) {
            SavingsTrxnDetailEntity savingsTrxn = (SavingsTrxnDetailEntity) accntTrxn;

            if (accountPayment.isSavingsWithdrawal() && amountAdjustedTo.isGreaterThan(savingsTrxn.getWithdrawlAmount())) {
                balanceAfterAdjust = balanceAfterAdjust.subtract(amountAdjustedTo.subtract(savingsTrxn.getWithdrawlAmount()));
                if (balanceAfterAdjust.isLessThanZero()) {
                    balanceIsPositive = false;
                }
            }
        }
        return balanceIsPositive;
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

    public List<SavingsRecentActivityDto> getRecentAccountActivity(final Integer count) {
        List<SavingsRecentActivityDto> accountActivityList = new ArrayList<SavingsRecentActivityDto>();
        int activitiesAdded = 0;
        for (SavingsActivityEntity activity : getSavingsActivityDetails()) {
            if (count == null || activitiesAdded < count.intValue()) {
                accountActivityList.add(createSavingsRecentActivityView(activity));
                activitiesAdded++;
            }
        }
        return accountActivityList;
    }

    private SavingsRecentActivityDto createSavingsRecentActivityView(final SavingsActivityEntity savingActivity) {
        SavingsRecentActivityDto savingsRecentActivityDto = new SavingsRecentActivityDto();
        savingsRecentActivityDto.setAccountTrxnId(savingActivity.getId());
        savingsRecentActivityDto.setActionDate(savingActivity.getTrxnCreatedDate());
        String preferredDate = DateUtils.getUserLocaleDate(this.userContext.getPreferredLocale(), savingActivity.getTrxnCreatedDate().toString());
        savingsRecentActivityDto.setUserPrefferedDate(preferredDate);
        savingsRecentActivityDto.setAmount(removeSign(savingActivity.getAmount()).toString());
        savingsRecentActivityDto.setActivity(savingActivity.getActivity().getName());
        savingsRecentActivityDto.setRunningBalance(savingActivity.getBalanceAmount().toString());
        return savingsRecentActivityDto;
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

    public void waiveNextDepositAmountDue(PersonnelBO loggedInUser) {

        SavingsActivityEntity savingsActivity = SavingsActivityEntity.savingsWaiveAmountDueOnNextDeposit(this,
                loggedInUser, this.savingsBalance, getTotalAmountDueForNextInstallment(), new DateTime().toDate());
        savingsActivityDetails.add(savingsActivity);

        List<AccountActionDateEntity> nextInstallments = getNextInstallment();
        for (AccountActionDateEntity accountActionDate : nextInstallments) {
            ((SavingsScheduleEntity) accountActionDate).waiveDepositDue();
        }
    }

    public void waiveAmountOverDue(PersonnelBO loggedInUser) {

        SavingsActivityEntity savingsActivity = SavingsActivityEntity.savingsWaiveDepositAmountOverdue(this,
                loggedInUser, this.savingsBalance, getTotalAmountInArrears(), new DateTime().toDate());
        savingsActivityDetails.add(savingsActivity);

        List<AccountActionDateEntity> installmentsInArrears = getDetailsOfInstallmentsInArrears();
        for (AccountActionDateEntity accountActionDate : installmentsInArrears) {
            ((SavingsScheduleEntity) accountActionDate).waiveDepositDue();
        }
    }

    /**
     */
    @Deprecated
    private SavingsActivityEntity buildSavingsActivity(final Money amount, final Money balanceAmount,
            final short acccountActionId, final Date trxnDate, final PersonnelBO personnel) throws AccountException {
        AccountActionEntity accountAction;
        try {
            accountAction = getSavingsPersistence().getPersistentObject(
                    AccountActionEntity.class, acccountActionId);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
        return new SavingsActivityEntity(personnel, accountAction, amount, balanceAmount, trxnDate, this);
    }

    @Override
    protected void regenerateFutureInstallments(final AccountActionDateEntity nextInstallment,
            final List<Days> workingDays, final List<Holiday> holidays) throws AccountException {

        MeetingBO customerMeeting = getCustomer().getCustomerMeetingValue();
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(customerMeeting);
        LocalDate currentDate = new LocalDate();
        LocalDate thisIntervalStartDate = customerMeeting.startDateForMeetingInterval(currentDate);
        LocalDate nextMatchingDate = new LocalDate(scheduledEvent.nextEventDateAfter(thisIntervalStartDate
                .toDateTimeAtStartOfDay()));
        DateTime futureIntervalStartDate = customerMeeting.startDateForMeetingInterval(nextMatchingDate)
                .toDateTimeAtStartOfDay();

        ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(
                workingDays, holidays);

        int numberOfInstallmentsToGenerate = getLastInstallmentId();
        List<DateTime> meetingDates = dateGeneration.generateScheduledDates(numberOfInstallmentsToGenerate,
                futureIntervalStartDate, scheduledEvent, false);

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
            if (lastInstallment == null) { // a special workaround for MIFOS-5107
                lastInstallment = new SavingsScheduleEntity(this, this.getCustomer(), (short) 0,
                        new java.sql.Date(new LocalDate().minusDays(1).toDateMidnight().getMillis()), PaymentStatus.UNPAID,
                        new Money(Money.getDefaultCurrency(), 0.0));
            }
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
    public boolean isTrxnDateValid(Date trxnDate, Date lastMeetingDate, boolean repaymentIndependentOfMeetingEnabled) {
        LocalDate transactionLocalDate = new LocalDate(trxnDate);
        LocalDate today = new LocalDate();

        if (AccountingRules.isBackDatedTxnAllowed()) {

            if (repaymentIndependentOfMeetingEnabled) {
                Date activationDate = this.getActivationDate();
                return trxnDate.compareTo(DateUtils.getDateWithoutTimeStamp(activationDate)) >= 0;
            }
            
            InterestScheduledEvent postingEvent = new SavingsInterestScheduledEventFactory()
                    .createScheduledEventFrom(this.getInterestPostingMeeting());
            LocalDate nextPostingDate = new LocalDate(this.nextIntPostDate);
            LocalDate currentPostingPeriodStartDate = postingEvent
                    .findFirstDateOfPeriodForMatchingDate(nextPostingDate);

            // FIXME throw an exception with the correct reason instead of returning false
            if (transactionLocalDate.isBefore(currentPostingPeriodStartDate)) {
                return false;
            }
            
            LocalDate activationDate = new LocalDate(this.activationDate);
            
            if (lastMeetingDate != null) {
                LocalDate meetingDate = new LocalDate(lastMeetingDate);
                return (transactionLocalDate.isAfter(meetingDate) || transactionLocalDate.isEqual(meetingDate))
                        && (transactionLocalDate.isAfter(activationDate) || transactionLocalDate.isEqual(activationDate));
            }

            return (transactionLocalDate.isAfter(activationDate) || transactionLocalDate.isEqual(activationDate))
                    && (transactionLocalDate.isBefore(today) || transactionLocalDate.isEqual(today));
        }
        return transactionLocalDate.isEqual(today);
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
        return getSavingsPersistence().loadPersistentObject(PaymentTypeEntity.class, paymentTypeId);
    }

    @Override
    public MeetingBO getMeetingForAccount() {
        return getCustomer().getCustomerMeetingValue();
    }

    public void removeRecommendedAmountOnFutureInstallments() {
        Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        if (getAccountActionDates() != null && getAccountActionDates().size() > 0) {
            for (AccountActionDateEntity accountAction : getAccountActionDates()) {
                if (accountAction.getActionDate().compareTo(currentDate) >= 0 && !accountAction.isPaid()) {
                    SavingsScheduleEntity savingsSchedule = (SavingsScheduleEntity) accountAction;
                    if (savingsSchedule.getDepositPaid().isGreaterThanZero()) {
                        savingsSchedule.setDeposit(savingsSchedule.getDepositPaid());
                        savingsSchedule.setPaymentStatus(PaymentStatus.PAID);
                    } else {
                        savingsSchedule.setDeposit(new Money(this.getCurrency()));
                    }
                }
            }
        }
    }

    public void resetRecommendedAmountOnFutureInstallments() {
        Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        if (getAccountActionDates() != null && getAccountActionDates().size() > 0) {
            for (AccountActionDateEntity accountAction : getAccountActionDates()) {
                if (accountAction.getActionDate().compareTo(currentDate) >= 0 && !accountAction.isPaid()) {
                    SavingsScheduleEntity savingsSchedule = (SavingsScheduleEntity) accountAction;
                    savingsSchedule.setDeposit(this.getRecommendedAmount());
                }
            }
        }
    }

    public MeetingBO getInterestCalculationMeeting() {
        return this.savingsOffering.getTimePerForInstcalc().getMeeting();
    }

    public MeetingBO getInterestPostingMeeting() {
        return this.savingsOffering.getFreqOfPostIntcalc().getMeeting();
    }

    public boolean isGroupModelWithIndividualAccountability() {

        return this.customer.isCenter() || (this.customer.isGroup() && this.recommendedAmntUnit.isPerIndividual());
    }

    public InterestCalcType getInterestCalcType() {
        return InterestCalcType.fromInt(this.savingsOffering.getInterestCalcType().getId());
    }

    public Double getInterestRate() {
        return this.savingsOffering.getInterestRate();
    }

    public Money getMinAmntForInt() {
        return this.savingsOffering.getMinAmntForInt();
    }

    public List<SavingsProductHistoricalInterestDetail> getHistoricalInterestDetailsForPeriod(CalendarPeriod period) {

        List<SavingsProductHistoricalInterestDetail> validHistoricalDetails = new ArrayList<SavingsProductHistoricalInterestDetail>();

        List<SavingsProductHistoricalInterestDetail> allHistoricalDetails = this.savingsOffering.getHistoricalInterestDetails();
        for (SavingsProductHistoricalInterestDetail interestDetail : allHistoricalDetails) {
            if (period.contains(interestDetail.getStartDate())) {
                validHistoricalDetails.add(interestDetail);
            }
        }

        return validHistoricalDetails;
    }

    private static Date startOfFiscalYear() {
        return new LocalDate().withMonthOfYear(1).withDayOfYear(1).toDateMidnight().toDate();
    }

    private void setSavingsPerformance(final SavingsPerformanceEntity savingsPerformance) {
        this.savingsPerformance = savingsPerformance;
    }

    private SavingsPerformanceEntity createSavingsPerformance() {
        SavingsPerformanceEntity savingsPerformance = new SavingsPerformanceEntity(this);
        logger.info("In SavingsBO::createSavingsPerformance(), SavingsPerformanceEntity created successfully ");
        return savingsPerformance;
    }

    public void generateSystemId(String officeGlobalNumber) {
        try {
            this.globalAccountNum = generateId(officeGlobalNumber);
        } catch (AccountException e) {
            throw new BusinessRuleException(e.getKey(), e);
        }
    }

    public SavingsAccountDetailDto toDto() {
        List<SavingsRecentActivityDto> recentActivity = this.getRecentAccountActivity(3);

        List<CustomerNoteDto> recentNoteDtos = new ArrayList<CustomerNoteDto>();
        List<AccountNotesEntity> recentNotes = this.getRecentAccountNotes();
        for (AccountNotesEntity accountNotesEntity : recentNotes) {
            recentNoteDtos.add(new CustomerNoteDto(accountNotesEntity.getCommentDate(), accountNotesEntity.getComment(), accountNotesEntity.getPersonnelName()));
        }

        SavingsPerformanceHistoryDto savingsPerformanceHistoryDto = new SavingsPerformanceHistoryDto(getActivationDate(),
                savingsPerformance.getTotalDeposits().toString(), savingsPerformance.getTotalWithdrawals().toString(),
                savingsPerformance.getTotalInterestEarned().toString(),
                savingsPerformance.getMissedDeposits() != null ? savingsPerformance.getMissedDeposits().toString() : "0");
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();

        return new SavingsAccountDetailDto(this.savingsOffering.toFullDto(), recentActivity, recentNoteDtos,
                this.recommendedAmount.toString(), this.globalAccountNum, getAccountId(), getState().getValue(), getState().name(), getSavingsBalance().toString(),
                nextInstallment != null ? nextInstallment.getActionDate() : null, getTotalAmountDue().toString(), getTotalAmountDueForNextInstallment().toString(), 
                getTotalAmountInArrears().toString(), savingsPerformanceHistoryDto, this.savingsOffering.getSavingsTypeAsEnum().name(), this.customer.getCustomerId());
    }

    public void setSavingsTransactionActivityHelper(SavingsTransactionActivityHelper savingsTransactionActivityHelper) {
        this.savingsTransactionActivityHelper = savingsTransactionActivityHelper;
    }

    public void setSavingsPaymentStrategy(SavingsPaymentStrategy savingsPaymentStrategy) {
        this.savingsPaymentStrategy = savingsPaymentStrategy;
    }
    
    public List<AccountPaymentEntity> getInterestPostingPaymentsForRemoval(Date fromDate) {
        
        List<AccountPaymentEntity> paymentsForRemoval = new ArrayList<AccountPaymentEntity>();
        
        Iterator<AccountPaymentEntity> paymentIter = this.accountPayments.iterator();
        while (paymentIter.hasNext()) {
            AccountPaymentEntity payment = paymentIter.next();
            Date paymentDate = payment.getPaymentDate();
            if (payment.isSavingsInterestPosting() && DateUtils.dateFallsOnOrBeforeDate(fromDate, paymentDate)) {
                this.nextIntPostDate = paymentDate;
                this.savingsBalance = this.savingsBalance.subtract(payment.getAmount());
                this.savingsPerformance.substractFromTotalInterestDetails(payment.getAmount());
                
                paymentsForRemoval.add(payment);
                paymentIter.remove();
            }
        }
        return paymentsForRemoval;
    }
    
    public List<SavingsActivityEntity> getInterestPostingActivitesForRemoval(Date fromDate) {
        
        List<SavingsActivityEntity> activitesForRemoval = new ArrayList<SavingsActivityEntity>();
        
        Iterator<SavingsActivityEntity> activityIter = this.savingsActivityDetails.iterator();
        while (activityIter.hasNext()) {
            SavingsActivityEntity activity = activityIter.next();
            Date activityDate = activity.getTrxnCreatedDate();
            if (activity.getActivity().isSavingsInterestPosting() && DateUtils.dateFallsOnOrBeforeDate(fromDate, activityDate)) {
                activitesForRemoval.add(activity);
                activityIter.remove();
            }
        }
        
        return activitesForRemoval;
    }
    
    public int countInterestPostingsForRecalculation(Date fromDate) {
        int postingsForRecalculation = 0;
        for (AccountPaymentEntity payment : this.accountPayments) {
            if (payment.isSavingsInterestPosting() && DateUtils.dateFallsOnOrBeforeDate(fromDate, payment.getPaymentDate())) {
                postingsForRecalculation++;
            }
        }
        return postingsForRecalculation;
    }
    
}