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

package org.mifos.accounts.business;

import static org.mifos.accounts.util.helpers.AccountTypes.LOAN_ACCOUNT;
import static org.mifos.accounts.util.helpers.AccountTypes.SAVINGS_ACCOUNT;
import static org.mifos.accounts.util.helpers.AccountConstants.MONTH_CLOSING_DAY_CONFIG_KEY;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.financial.business.FinancialTransactionBO;
import org.mifos.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.accounts.util.helpers.FeeInstallment;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.accounts.util.helpers.PaymentData;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.clientportfolio.newloan.domain.CreationDetail;
import org.mifos.config.AccountingRules;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.config.business.ConfigurationKeyValue;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerMeetingEntity;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.PaymentDto;
import org.mifos.dto.screen.TransactionHistoryDto;
import org.mifos.framework.business.AbstractBusinessObject;
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

public class AccountBO extends AbstractBusinessObject {

    private static final Logger logger = LoggerFactory.getLogger(AccountBO.class);

    private Integer accountId;
    protected String globalAccountNum;
    private String externalId;
    protected AccountTypeEntity accountType;
    protected CustomerBO customer;
    protected OfficeBO office;
    protected PersonnelBO personnel;
    private AccountStateEntity accountState;
    private Date closedDate;
    private Integer offsettingAllowable;

    // associations
    protected Set<AccountNotesEntity> accountNotes;
    protected List<AccountStatusChangeHistoryEntity> accountStatusChangeHistory = new ArrayList<AccountStatusChangeHistoryEntity>();
    private final Set<AccountFlagMapping> accountFlags;

    /**
     * Links this loan to its applied fees
     */
    protected Set<AccountFeesEntity> accountFees;
    protected Set<AccountActionDateEntity> accountActionDates;
    protected List<AccountPaymentEntity> accountPayments;
    private Set<AccountCustomFieldEntity> accountCustomFields;

    private LegacyAccountDao legacyAccountDao = null;
    private LegacyMasterDao legacyMasterDao = null;
    private DateTimeService dateTimeService = null;
    private FinancialBusinessService financialBusinessService = null;
    
    private ConfigurationPersistence configurationPersistence;
    
    public ConfigurationPersistence getConfigurationPersistence() {
		if ( configurationPersistence == null ){
			configurationPersistence = ApplicationContextProvider.getBean(ConfigurationPersistence.class);
		}
    	return configurationPersistence;
	}

	public void setConfigurationPersistence(
			ConfigurationPersistence configurationPersistence) {
		this.configurationPersistence = configurationPersistence;
	}

	public FinancialBusinessService getFinancialBusinessService() {
        if (null == financialBusinessService) {
            financialBusinessService = new FinancialBusinessService();
        }
        return financialBusinessService;
    }

    public void setFinancialBusinessService(final FinancialBusinessService financialBusinessService) {
        this.financialBusinessService = financialBusinessService;
    }

    public DateTimeService getDateTimeService() {
        if (null == dateTimeService) {
            dateTimeService = new DateTimeService();
        }
        return dateTimeService;
    }

    protected FeeDao getFeeDao() {
        return ApplicationContextProvider.getBean(FeeDao.class);
    }

    public void setDateTimeService(final DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public LegacyAccountDao getlegacyAccountDao() {
        if (null == legacyAccountDao) {
            legacyAccountDao = ApplicationContextProvider.getBean(LegacyAccountDao.class);
        }
        return legacyAccountDao;
    }

    public void setlegacyAccountDao(final LegacyAccountDao legacyAccountDao) {
        this.legacyAccountDao = legacyAccountDao;
    }

    public LegacyMasterDao getlegacyMasterDao() {
        if (null == legacyMasterDao) {
            legacyMasterDao = ApplicationContextProvider.getBean(LegacyMasterDao.class);
        }
        return legacyMasterDao;
    }

    /**
     * default constructor for hibernate usage
     */
    protected AccountBO() {
        this(null);
    }

    /**
     * minimal legal constructor
     */
    public AccountBO(final AccountTypes accountType, final AccountState accountState, final CustomerBO customer,
            final Integer offsettingAllowable, final Set<AccountActionDateEntity> scheduledPayments,
            final Set<AccountFeesEntity> accountFees, final Date createdDate, final Short createdByUserId) {
        this.accountId = null;
        this.accountType = new AccountTypeEntity(accountType.getValue());
        this.accountState = new AccountStateEntity(accountState);
        this.customer = customer;
        this.offsettingAllowable = offsettingAllowable;
        this.createdDate = createdDate;
        this.createdBy = createdByUserId;
        this.accountActionDates = scheduledPayments;
        this.accountFees = accountFees;
        if (customer != null) {
            this.office = customer.getOffice();
            this.personnel = customer.getPersonnel();
        }
        this.accountFlags = new LinkedHashSet<AccountFlagMapping>();
        this.accountCustomFields = new LinkedHashSet<AccountCustomFieldEntity>();
        this.accountPayments = new ArrayList<AccountPaymentEntity>();
    }

    /**
     * minimal legal constructor for savings accounts
     */
    public AccountBO(AccountTypes accountType, AccountState accountState, CustomerBO customer, List<? extends AccountActionDateEntity> scheduledRepaymentsOrDeposits, CreationDetail creationDetail) {
        this.accountId = null;
        this.accountType = new AccountTypeEntity(accountType.getValue());
        this.accountState = new AccountStateEntity(accountState);
        this.customer = customer;
        this.createdDate = creationDetail.getCreatedDate().toDate();
        this.createdBy = creationDetail.getCreatedBy().shortValue();

        // ensure scheduled payments are linked to this account.
        for (AccountActionDateEntity scheduledPayment : scheduledRepaymentsOrDeposits) {
            scheduledPayment.setAccount(this);
        }

        this.accountActionDates = new LinkedHashSet<AccountActionDateEntity>(scheduledRepaymentsOrDeposits);
        if (customer != null) {
            this.office = customer.getOffice();
            this.personnel = customer.getPersonnel();
        }
        this.accountFlags = new LinkedHashSet<AccountFlagMapping>();
        this.accountCustomFields = new LinkedHashSet<AccountCustomFieldEntity>();
        this.accountPayments = new ArrayList<AccountPaymentEntity>();

        this.offsettingAllowable = Integer.valueOf(1);
        this.accountFees = new HashSet<AccountFeesEntity>();
    }

    /**
     * @deprecated use minimal legal constructor
     */
    @Deprecated
    AccountBO(final Integer accountId) {
        this.accountId = accountId;
        globalAccountNum = null;
        customer = null;
        office = null;
        personnel = null;
        accountType = null;
        accountFees = new LinkedHashSet<AccountFeesEntity>();
        accountPayments = new ArrayList<AccountPaymentEntity>();
        accountActionDates = new LinkedHashSet<AccountActionDateEntity>();
        accountCustomFields = new LinkedHashSet<AccountCustomFieldEntity>();
        accountNotes = new LinkedHashSet<AccountNotesEntity>();
        accountStatusChangeHistory = new ArrayList<AccountStatusChangeHistoryEntity>();
        accountFlags = new LinkedHashSet<AccountFlagMapping>();
        offsettingAllowable = new Integer(1);
    }

    /**
     * @deprecated use minimal legal constructor
     */
    @Deprecated
    protected AccountBO(final UserContext userContext, final CustomerBO customer, final AccountTypes accountType,
            final AccountState accountState) throws AccountException {
        super(userContext);
        validate(userContext, customer, accountType, accountState);
        accountFees = new LinkedHashSet<AccountFeesEntity>();
        accountPayments = new ArrayList<AccountPaymentEntity>();
        accountActionDates = new LinkedHashSet<AccountActionDateEntity>();
        accountCustomFields = new LinkedHashSet<AccountCustomFieldEntity>();
        accountNotes = new LinkedHashSet<AccountNotesEntity>();
        accountStatusChangeHistory = new ArrayList<AccountStatusChangeHistoryEntity>();
        accountFlags = new LinkedHashSet<AccountFlagMapping>();
        this.accountId = null;
        this.customer = customer;
        this.accountType = new AccountTypeEntity(accountType.getValue());
        if (customer != null) {
            this.office = customer.getOffice();
            this.personnel = customer.getPersonnel();
        }
        this.setAccountState(new AccountStateEntity(accountState));
        this.offsettingAllowable = new Integer(1);
        this.setCreateDetails();
    }

    public Integer getAccountId() {
        return accountId;
    }

    public String getGlobalAccountNum() {
        return globalAccountNum;
    }

    /**
     * Obsolete; most/all callers should call {@link #getType()} instead.
     */
    public AccountTypeEntity getAccountType() {
        return accountType;
    }

    public CustomerBO getCustomer() {
        return customer;
    }

    public OfficeBO getOffice() {
        return office;
    }

    public PersonnelBO getPersonnel() {
        return personnel;
    }

    public Set<AccountNotesEntity> getAccountNotes() {
        return accountNotes;
    }

    public List<AccountStatusChangeHistoryEntity> getAccountStatusChangeHistory() {
        return accountStatusChangeHistory;
    }

    public AccountStatusChangeHistoryEntity getLastAccountStatusChange() {
        return accountStatusChangeHistory.get(accountStatusChangeHistory.size() - 1);
    }

    /**
     * For most purposes this is deprecated and one should call {@link #getState()} instead.
     */
    public AccountStateEntity getAccountState() {
        return accountState;
    }

    public Set<AccountFlagMapping> getAccountFlags() {
        return accountFlags;
    }

    /**
     * Returns the set of {@link AccountFeesEntity}s -- links to the fees that apply to this loan.
     */
    public Set<AccountFeesEntity> getAccountFeesIncludingInactiveFees() {
        return accountFees;
    }

    public Set<AccountFeesEntity> getAccountFees() {
        Set<AccountFeesEntity> activeAccountFees = new HashSet<AccountFeesEntity>();
        for (AccountFeesEntity accountFeesEntity : getAccountFeesIncludingInactiveFees()) {
            if (accountFeesEntity.getFeeStatus() == null || accountFeesEntity.getFeeStatus().equals(FeeStatus.ACTIVE.getValue())) {
                activeAccountFees.add(accountFeesEntity);
            }
        }
        return activeAccountFees;
    }

    public Set<AccountActionDateEntity> getAccountActionDates() {
        return accountActionDates;
    }

    public List<AccountActionDateEntity> getAccountActionDatesSortedByInstallmentId() {
        List<AccountActionDateEntity> sortedList = new ArrayList<AccountActionDateEntity>(getAccountActionDates());
        Collections.sort(sortedList, new Comparator<AccountActionDateEntity>() {
            @Override
			public int compare(AccountActionDateEntity entity1, AccountActionDateEntity entity2) {
                return new Integer(entity1.getInstallmentId()).compareTo(new Integer(entity2.getInstallmentId()));
            }
        });
        return sortedList;
    }

    public List<AccountActionDateEntity> getActionDatesSortedByDate() {
        List<AccountActionDateEntity> sortedList = new ArrayList<AccountActionDateEntity>();
        sortedList.addAll(this.getAccountActionDates());
        Collections.sort(sortedList);
        return sortedList;
    }

    public List<AccountPaymentEntity> getAccountPayments() {

        return accountPayments;
    }

    public Set<AccountCustomFieldEntity> getAccountCustomFields() {
        return accountCustomFields;
    }

    public Date getClosedDate() {
        return (Date) (closedDate == null ? null : closedDate.clone());
    }

    public void setGlobalAccountNum(final String globalAccountNum) {
        this.globalAccountNum = globalAccountNum;
    }

    public void setAccountPayments(final List<AccountPaymentEntity> accountPayments) {

        this.accountPayments = accountPayments;

    }

    protected void setAccountState(final AccountStateEntity accountState) {
        this.accountState = accountState;
    }

    public void setPersonnel(final PersonnelBO personnel) {
        this.personnel = personnel;
    }

    protected void setClosedDate(final Date closedDate) {
        this.closedDate = (Date) (closedDate == null ? null : closedDate.clone());
    }

    public void addAccountStatusChangeHistory(final AccountStatusChangeHistoryEntity accountStatusChangeHistoryEntity) {
        this.accountStatusChangeHistory.add(accountStatusChangeHistoryEntity);
    }

    public void addAccountFees(final AccountFeesEntity fees) {
        accountFees.add(fees);
    }

    public void removeAccountFee(final AccountFeesEntity fee) {
        accountFees.remove(fee);
    }

    public void addAccountActionDate(final AccountActionDateEntity accountAction) {
        accountActionDates.add(accountAction);
    }

    public void addAccountPayment(final AccountPaymentEntity payment) {
        if (accountPayments == null) {
            accountPayments = new ArrayList<AccountPaymentEntity>();
        }
        accountPayments.add(payment);
    }

    public void addAccountNotes(final AccountNotesEntity notes) {
        if (this.accountNotes == null) {
            this.accountNotes = new LinkedHashSet<AccountNotesEntity>();
        }
        accountNotes.add(notes);
    }

    protected void addAccountFlag(final AccountStateFlagEntity flagDetail) {
        AccountFlagMapping flagMap = new AccountFlagMapping();
        flagMap.setCreatedBy(this.getUserContext().getId());
        flagMap.setCreatedDate(getDateTimeService().getCurrentJavaDateTime());
        flagMap.setFlag(flagDetail);
        this.accountFlags.add(flagMap);
    }

    public void addAccountCustomField(final AccountCustomFieldEntity customField) {
        if (customField.getFieldId() != null) {
            AccountCustomFieldEntity accountCustomField = getAccountCustomField(customField.getFieldId());
            if (accountCustomField == null) {
                customField.setAccount(this);
                this.accountCustomFields.add(customField);
            } else {
                accountCustomField.setFieldValue(customField.getFieldValue());
            }
        }
    }

    protected void addcustomFields(final List<CustomFieldDto> customFields) throws InvalidDateException {
        if (customFields != null) {
            for (CustomFieldDto view : customFields) {
                if (CustomFieldType.DATE.getValue().equals(view.getFieldType())
                        && org.apache.commons.lang.StringUtils.isNotBlank(view.getFieldValue())) {
                    SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, getUserContext().getPreferredLocale());
                    String userfmt = DateUtils.convertToCurrentDateFormat(format.toPattern());
                    this.getAccountCustomFields().add(new AccountCustomFieldEntity(this, view.getFieldId(),
                            DateUtils.convertUserToDbFmt(view.getFieldValue(), userfmt)));
                }
                else {
                    this.getAccountCustomFields().add(
                            new AccountCustomFieldEntity(this, view.getFieldId(), view.getFieldValue()));
                }
            }
        }
    }

    public final AccountPaymentEntity applyPayment(final PaymentData paymentData) throws AccountException {
        AccountPaymentEntity accountPayment = makePayment(paymentData);
        addAccountPayment(accountPayment);
        buildFinancialEntries(accountPayment.getAccountTrxns());
        return accountPayment;
    }


    public PaymentData createPaymentData(final Money amount, final Date trxnDate,
            final String receiptId, final Date receiptDate, final Short paymentTypeId, PersonnelBO loggedInUser) {
        if (loggedInUser == null) {
            throw new IllegalStateException(AccountConstants.ERROR_INVALID_PERSONNEL);
        }

        PaymentData paymentData = PaymentData.createPaymentData(amount, loggedInUser, paymentTypeId, trxnDate);
        if (receiptDate != null) {
            paymentData.setReceiptDate(receiptDate);
        }
        if (receiptId != null) {
            paymentData.setReceiptNum(receiptId);
        }

        for (AccountActionDateEntity installment : getTotalInstallmentsDue()) {
            if (this instanceof CustomerAccountBO) {
                paymentData.addAccountPaymentData(new CustomerAccountPaymentData(installment));
            }
        }
        return paymentData;
    }

    public final void adjustPmnt(final String adjustmentComment, PersonnelBO loggedInUser) throws AccountException {
        if (isAdjustPossibleOnLastTrxn()) {
            logger.debug("Adjustment is possible hence attempting to adjust.");
            adjustPayment(findMostRecentNonzeroPaymentByPaymentDate(), loggedInUser, adjustmentComment);
        } else {
            throw new AccountException(AccountExceptionConstants.CANNOTADJUST);
        }
    }

    public void adjustLastPayment(final String adjustmentComment, PersonnelBO loggedInUser) throws AccountException {
        if (isAdjustPossibleOnLastTrxn()) {
            logger.debug("Adjustment is possible hence attempting to adjust.");
            adjustPayment(getLastPmntToBeAdjusted(), loggedInUser, adjustmentComment);
        } else {
            throw new AccountException(AccountExceptionConstants.CANNOTADJUST);
        }

    }

    protected final void adjustPayment(final AccountPaymentEntity accountPayment, final PersonnelBO personnel,
            final String adjustmentComment) throws AccountException {
        List<AccountTrxnEntity> reversedTrxns = accountPayment.reversalAdjustment(personnel, adjustmentComment);
        updateInstallmentAfterAdjustment(reversedTrxns, personnel);
        buildFinancialEntries(new LinkedHashSet<AccountTrxnEntity>(reversedTrxns));
    }

    public final void handleChangeInMeetingSchedule(final List<Days> workingDays, final List<Holiday> holidays)
            throws AccountException {
        // find the installment to update
        AccountActionDateEntity nextInstallment = findInstallmentToUpdate();
        if (nextInstallment != null) {
            regenerateFutureInstallments(nextInstallment, workingDays, holidays);
        }
    }

    /**
     * This method should be called only from a subclass. TODO KRP this method should throw a runtime exception.
     *
     * @return null
     */
    protected MeetingBO getMeetingForAccount() {
        return null;
    }

    /*
     * Find the first installment which has an enclosing "interval" such that the entire interval is after the current
     * date. For example assume March 1 is a Monday and that weeks are defined to start on Monday. If the meeting is a
     * weekly meeting on a Wednesday then the "interval" for the meeting of Wednesday March 10 is Monday March 8 to
     * Sunday March 14. If the current date was March 7, then we would return the installment for March 10 since the
     * 3/8-14 interval is after the 7th. But if today were the 8th, then we would return the following installment.
     */
    private AccountActionDateEntity findInstallmentToUpdate() {
        List<AccountActionDateEntity> allInstallments = getAllInstallments();
        if (allInstallments.size() == 0) {
            return null;
        }

        LocalDate currentDate = new LocalDate();
        MeetingBO meeting = getMeetingForAccount();

        int installmentIndex = 0;
        AccountActionDateEntity installment = allInstallments.get(installmentIndex);
        // keep looking at the next installment as long as the current date falls on or
        // after (!before) the start of the current installment
        while (installment != null && !currentDate.isBefore(meeting.startDateForMeetingInterval(new LocalDate(installment.getActionDate().getTime())))) {

            ++installmentIndex;
            // if we've iterated over all the installments, then just return null
            if (installmentIndex == allInstallments.size()) {
                installment = null;
            } else {
                installment = allInstallments.get(installmentIndex);
            }
        }
        return installment;
    }

    public void changeStatus(final AccountState newStatus, final Short flagId, final String comment, PersonnelBO loggedInUser) throws AccountException {
        changeStatus(newStatus.getValue(), flagId, comment, loggedInUser, getDateTimeService().getCurrentJavaDateTime());
    }

    public void changeStatus(final AccountState newStatus, final Short flagId, final String comment,
                             final PersonnelBO loggedInUser, final Date transactionDate) throws AccountException {
        changeStatus(newStatus.getValue(), flagId, comment, loggedInUser, transactionDate);
    }

    public final void changeStatus(final Short newStatusId, final Short flagId, final String comment, PersonnelBO loggedInUser)
            throws AccountException {
        changeStatus(newStatusId, flagId, comment, loggedInUser, getDateTimeService().getCurrentJavaDateTime());
    }

    public final void changeStatus(final Short newStatusId, final Short flagId, final String comment,
                                   final PersonnelBO loggedInUser, final Date argumentDate)
            throws AccountException {
        Date transactionDate = argumentDate;
        if (transactionDate == null) {
            transactionDate = getDateTimeService().getCurrentJavaDateTime();
        }
        Short oldStatusId = this.getState().getValue();
        if (getUserContext() == null) {
            throw new IllegalStateException("userContext is not set for account.");
        }

        try {
            logger.debug("In the change status method of AccountBO:: new StatusId= " + newStatusId);

            activationDateHelper(newStatusId);
            LegacyMasterDao legacyMasterDao = getlegacyMasterDao();
            AccountStateEntity accountStateEntity = legacyMasterDao.getPersistentObject(
                    AccountStateEntity.class, newStatusId);
            AccountStateFlagEntity accountStateFlagEntity = null;
            if (flagId != null) {
                accountStateFlagEntity = legacyMasterDao.getPersistentObject(
                        AccountStateFlagEntity.class, flagId);
            }

            Date statusChangeDate = (argumentDate == null) ? new DateTimeService().getCurrentJavaDateTime() : argumentDate;
            AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(this
                    .getAccountState(), accountStateEntity, loggedInUser, this, statusChangeDate);
            AccountNotesEntity accountNotesEntity = new AccountNotesEntity(transactionDate, comment, loggedInUser, this);
            this.addAccountStatusChangeHistory(historyEntity);
            this.setAccountState(accountStateEntity);
            this.addAccountNotes(accountNotesEntity);
            if (accountStateFlagEntity != null) {
                setFlag(accountStateFlagEntity);
            }
            if (newStatusId.equals(AccountState.LOAN_CANCELLED.getValue())
                    || newStatusId.equals(AccountState.LOAN_CLOSED_OBLIGATIONS_MET.getValue())
                    || newStatusId.equals(AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue())
                    || newStatusId.equals(AccountState.SAVINGS_CANCELLED.getValue())
                    || newStatusId.equals(AccountState.CUSTOMER_ACCOUNT_INACTIVE.getValue())) {
                this.setClosedDate(transactionDate);
            }
            if (newStatusId.equals(AccountState.LOAN_CLOSED_WRITTEN_OFF.getValue())) {
                writeOff(transactionDate);
            }

            if (newStatusId.equals(AccountState.LOAN_CLOSED_RESCHEDULED.getValue())) {
                reschedule(transactionDate);
            }

            if (newStatusId.equals(AccountState.SAVINGS_INACTIVE.getValue())) {
                ((SavingsBO) this).removeRecommendedAmountOnFutureInstallments();
            }

            if (oldStatusId.equals(AccountState.SAVINGS_INACTIVE.getValue())
                    && newStatusId.equals(AccountState.SAVINGS_ACTIVE.getValue())) {
                ((SavingsBO) this).resetRecommendedAmountOnFutureInstallments();
            }

            logger.debug("Coming out successfully from the change status method of AccountBO");
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    /**
     * used by subclasses
     * @param transactionDate
     */
    @SuppressWarnings("unused")
    protected void writeOff(Date transactionDate) throws AccountException {
    }

    /**
     * used by subclasses
     * @param transactionDate
     */
    @SuppressWarnings("unused")
    protected void reschedule(Date transactionDate) throws AccountException {
    }

    protected void updateClientPerformanceOnRescheduleLoan() {
    }

    protected void updateAccountFeesEntity(final Short feeId) {
        AccountFeesEntity accountFees = getAccountFees(feeId);
        if (accountFees != null) {
            accountFees.changeFeesStatus(FeeStatus.INACTIVE, getDateTimeService().getCurrentJavaDateTime());
            accountFees.setLastAppliedDate(null);
        }
    }

    /**
     * Return an {@link AccountFeesEntity} that links this account to the given fee, or null if the fee does not apply
     * to this account.
     *
     * @param feeId
     *            the primary key of the {@link FeeBO} being sought.
     * @return
     */
    public AccountFeesEntity getAccountFees(final Short feeId) {
        for (AccountFeesEntity accountFeesEntity : this.getAccountFees()) {
            if (accountFeesEntity.getFees().getFeeId().equals(feeId)) {
                return accountFeesEntity;
            }
        }
        return null;
    }

    public FeeBO getAccountFeesObject(final Short feeId) {
        AccountFeesEntity accountFees = getAccountFees(feeId);
        if (accountFees != null) {
            return accountFees.getFees();
        }
        return null;
    }

    public Boolean isFeeActive(final Short feeId) {
        AccountFeesEntity accountFees = getAccountFees(feeId);
        return accountFees.getFeeStatus() == null || accountFees.getFeeStatus().equals(FeeStatus.ACTIVE.getValue());
    }

    protected Money removeSign(final Money amount) {
        if (amount != null && amount.isLessThanZero()) {
            return amount.negate();
        }

        return amount;
    }

    public double getLastPmntAmnt() {
        if (null != accountPayments && accountPayments.size() > 0) {
            return findMostRecentPaymentByPaymentDate().getAmount().getAmountDoubleValue();
        }
        return 0;
    }

    public double getLastPmntAmntToBeAdjusted() {
        if (null != getLastPmntToBeAdjusted() && null != accountPayments && accountPayments.size() > 0) {
            return getLastPmntToBeAdjusted().getAmount().getAmountDoubleValue();
        }
        return 0;
    }

    /*
     * called from ui
     */
    public AccountPaymentEntity getLastPmnt() {
        return findMostRecentPaymentByPaymentDate();
    }

    private AccountPaymentEntity findMostRecentPaymentByPaymentDate(List<AccountPaymentEntity> recentAccountPayments) {

        AccountPaymentEntity mostRecentPayment = null;

        if (!recentAccountPayments.isEmpty()) {
            mostRecentPayment = recentAccountPayments.get(0);
            for (AccountPaymentEntity accountPaymentEntity : recentAccountPayments) {
                LocalDate paymentDate = new LocalDate(accountPaymentEntity.getPaymentDate());
                if ((paymentDate.isAfter(new LocalDate(mostRecentPayment.getPaymentDate())) && paymentDate.isBefore(new LocalDate().plusDays(1))) ||
                        (paymentDate.isEqual(new LocalDate(mostRecentPayment.getPaymentDate())) &&
                                accountPaymentEntity.getPaymentId() != null && mostRecentPayment.getPaymentId() != null &&
                                accountPaymentEntity.getPaymentId() > mostRecentPayment.getPaymentId())) {
                    mostRecentPayment = accountPaymentEntity;
                }
            }
        }

        return mostRecentPayment;
    }

    public AccountPaymentEntity findMostRecentPaymentByPaymentDate() {
        return findMostRecentPaymentByPaymentDate(new ArrayList<AccountPaymentEntity>(this.accountPayments));
    }

    /**
     * This method skips zero-amount payments, ie. these which were reversed by the  "payment adjustment" process
     */
    public AccountPaymentEntity findMostRecentNonzeroPaymentByPaymentDate() {
        List<AccountPaymentEntity> nonzeroPayments = new ArrayList<AccountPaymentEntity>();
        for (AccountPaymentEntity payment : this.accountPayments) {
            if (payment.getAmount().isNonZero()) {
                nonzeroPayments.add(payment);
            }
        }
        return findMostRecentPaymentByPaymentDate(nonzeroPayments);
    }

    public AccountPaymentEntity getLastPmntToBeAdjusted() {
        AccountPaymentEntity accntPmnt = null;
        for (AccountPaymentEntity accntPayment : accountPayments) {
            if (accntPayment.getAmount().isNonZero()) {
                accntPmnt = accntPayment;
                break;
            }
        }
        return accntPmnt;
    }
    
    public AccountPaymentEntity findPaymentById(final Integer paymentId) {
        AccountPaymentEntity result = null;
        for (AccountPaymentEntity payment : this.accountPayments) {
            if (payment.getPaymentId().equals(paymentId)) {
                result = payment;
                break;
            }
        }
        return result;
    }

    public AccountActionDateEntity getAccountActionDate(final Short installmentId) {
        if (null != accountActionDates && accountActionDates.size() > 0) {
            for (AccountActionDateEntity accntActionDate : accountActionDates) {
                if (accntActionDate.getInstallmentId().equals(installmentId)) {
                    return accntActionDate;
                }
            }
        }
        return null;
    }

    public AccountActionDateEntity getAccountActionDate(final Short installmentId, final Integer customerId) {
        if (null != accountActionDates && accountActionDates.size() > 0) {
            for (AccountActionDateEntity accntActionDate : accountActionDates) {
                if (accntActionDate.getInstallmentId().equals(installmentId) && accntActionDate.getCustomer().getCustomerId() == customerId) {
                    return accntActionDate;
                }
            }
        }
        return null;
    }

    /*
     * Return those unpaid AccountActionDateEntities after the next installment.
     */
    public List<AccountActionDateEntity> getApplicableIdsForFutureInstallments() {
        List<AccountActionDateEntity> futureActionDateList = new ArrayList<AccountActionDateEntity>();
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null) {
            for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
                if (!accountActionDate.isPaid() && accountActionDate.getInstallmentId() > nextInstallment.getInstallmentId()) {
                    futureActionDateList.add(accountActionDate);
                }
            }
        }
        return futureActionDateList;
    }

    /*
     * Return those unpaid AccountActionDateEntities on or after the next installment.
     */
    public List<AccountActionDateEntity> getApplicableIdsForFutureInstallmentsForWriteOffOrReschedule() {
        List<AccountActionDateEntity> futureActionDateList = new ArrayList<AccountActionDateEntity>();
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null) {
            for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
                if (!accountActionDate.isPaid() && accountActionDate.getInstallmentId() >= nextInstallment.getInstallmentId()) {
                    futureActionDateList.add(accountActionDate);
                }
            }
        }
        return futureActionDateList;
    }

    protected List<AccountActionDateEntity> getApplicableIdsForNextInstallmentAndArrears() {
        List<AccountActionDateEntity> dueActionDateList = new ArrayList<AccountActionDateEntity>(getApplicableIdsForArrears());
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null && !nextInstallment.isPaid()) {
            dueActionDateList.add(nextInstallment);
        }
        return dueActionDateList;
    }

    protected List<AccountActionDateEntity> getApplicableIdsForArrears() {
        List<AccountActionDateEntity> dueActionDateList = new ArrayList<AccountActionDateEntity>();
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment == null || !nextInstallment.isPaid()) {
            dueActionDateList.addAll(getDetailsOfInstallmentsInArrears());
        }
        return dueActionDateList;
    }

    public List<AccountActionDateEntity> getPastInstallments() {
        List<AccountActionDateEntity> pastActionDateList = new ArrayList<AccountActionDateEntity>();
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            if (accountActionDateEntity.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) < 0) {
                pastActionDateList.add(accountActionDateEntity);
            }
        }
        return pastActionDateList;
    }

    public List<AccountActionDateEntity> getFutureInstallments() {
        List<AccountActionDateEntity> futureActionDateList = new ArrayList<AccountActionDateEntity>();
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            if (accountActionDateEntity.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
                futureActionDateList.add(accountActionDateEntity);
            }
        }
        return futureActionDateList;
    }

    public List<AccountActionDateEntity> getAllInstallments() {
        List<AccountActionDateEntity> actionDateList = new ArrayList<AccountActionDateEntity>();
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            actionDateList.add(accountActionDateEntity);
        }
        return actionDateList;
    }

    public List<TransactionHistoryDto> getTransactionHistoryView() {
        List<TransactionHistoryDto> trxnHistory = new ArrayList<TransactionHistoryDto>();
        for (AccountPaymentEntity accountPayment : getAccountPayments()) {
            for (AccountTrxnEntity accountTrxn : accountPayment.getAccountTrxns()) {
                for (FinancialTransactionBO financialTrxn : accountTrxn.getFinancialTransactions()) {
                    TransactionHistoryDto transactionHistory = new TransactionHistoryDto();
                    setFinancialEntries(financialTrxn, transactionHistory);
                    setAccountingEntries(accountTrxn, transactionHistory);
                    trxnHistory.add(transactionHistory);
                }
            }
        }

        return trxnHistory;
    }

    public Date getNextMeetingDate() {
        AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();
        if (nextAccountAction != null) {
            return nextAccountAction.getActionDate();
        }
        // calculate the next date based on the customer's meeting object
        CustomerMeetingEntity customerMeeting = customer.getCustomerMeeting();
        if (customerMeeting != null) {

            ScheduledEvent scheduledEvent = ScheduledEventFactory
                    .createScheduledEventFrom(customerMeeting.getMeeting());
            DateTime nextMeetingDate = scheduledEvent.nearestMatchingDateBeginningAt(new LocalDate()
                    .toDateTimeAtStartOfDay());
            return new java.sql.Date(nextMeetingDate.toDate().getTime());
        }
        return new java.sql.Date(DateUtils.getCurrentDateWithoutTimeStamp().getTime());
    }

    public List<AccountActionDateEntity> getDetailsOfInstallmentsInArrearsOn(LocalDate asOf) {
        List<AccountActionDateEntity> installmentsInArrears = new ArrayList<AccountActionDateEntity>();

        Set<AccountActionDateEntity> accountActionDates = getAccountActionDates();
        
        if (accountActionDates!= null && !accountActionDates.isEmpty()) {
            for (AccountActionDateEntity accountAction : accountActionDates) {
            	LocalDate installmentDate = new LocalDate(accountAction.getActionDate());
                if (asOf.isAfter(installmentDate) && !accountAction.isPaid()) {
                    installmentsInArrears.add(accountAction);
                }
            }
        }
        return installmentsInArrears;
    }

    public List<AccountActionDateEntity> getDetailsOfUnpaidInstallmentsOn(LocalDate asOf) {
        List<AccountActionDateEntity> unpaidInstallments = new ArrayList<AccountActionDateEntity>();

        Set<AccountActionDateEntity> accountActionDates = getAccountActionDates();
        
        if (accountActionDates!= null && !accountActionDates.isEmpty()) {
            for (AccountActionDateEntity accountAction : accountActionDates) {
                if (!accountAction.isPaid()) {
                    unpaidInstallments.add(accountAction);
                }
            }
        }
        return unpaidInstallments;
    }
    
    public List<AccountActionDateEntity> getDetailsOfPaidInstallmentsOn(LocalDate asOf) {
        List<AccountActionDateEntity> paidInstallments = new ArrayList<AccountActionDateEntity>();

        Set<AccountActionDateEntity> accountActionDates = getAccountActionDates();
        
        if (accountActionDates!= null && !accountActionDates.isEmpty()) {
            for (AccountActionDateEntity accountAction : accountActionDates) {
                if (accountAction.isPaid()) {
                    paidInstallments.add(accountAction);
                }
            }
        }
        return paidInstallments;
    }
    
    
    public List<AccountActionDateEntity> getDetailsOfInstallmentsInArrears() {
        List<AccountActionDateEntity> installmentsInArrears = new ArrayList<AccountActionDateEntity>();
        Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        if (getAccountActionDates() != null && getAccountActionDates().size() > 0) {
            for (AccountActionDateEntity accountAction : getAccountActionDates()) {
                if (accountAction.getActionDate().compareTo(currentDate) < 0 && !accountAction.isPaid()) {
                    installmentsInArrears.add(accountAction);
                }
            }
        }
        return installmentsInArrears;
    }

    /**
     * Return the earliest-dated AccountActionDateEntity on or after today.
     */
    public AccountActionDateEntity getDetailsOfNextInstallment() {
        AccountActionDateEntity nextAccountAction = null;
        Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        if (getAccountActionDates() != null && getAccountActionDates().size() > 0) {
            for (AccountActionDateEntity accountAction : getAccountActionDates()) {
                if (accountAction.getActionDate().compareTo(currentDate) >= 0) {
                    if (null == nextAccountAction
                            || nextAccountAction.getInstallmentId() > accountAction.getInstallmentId()) {
                        nextAccountAction = accountAction;
                    }
                }
            }
        }
        return nextAccountAction;
    }
    
    public AccountActionDateEntity getDetailsOfNextInstallmentOn(LocalDate asOf) {
        AccountActionDateEntity nextAccountAction = null;
        Date currentDate = asOf.toDateMidnight().toDate();
        if (getAccountActionDates() != null && getAccountActionDates().size() > 0) {
            for (AccountActionDateEntity accountAction : getAccountActionDates()) {
                if (accountAction.getActionDate().compareTo(currentDate) >= 0) {
                    if (null == nextAccountAction
                            || nextAccountAction.getInstallmentId() > accountAction.getInstallmentId()) {
                        nextAccountAction = accountAction;
                    }
                }
            }
        }
        return nextAccountAction;
    }

    public Money getTotalAmountDue() {
        Money totalAmt = getTotalAmountInArrears();
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null && !nextInstallment.isPaid()) {
            totalAmt = totalAmt.add(getDueAmount(nextInstallment));
        }
        return totalAmt;
    }

    public Money getTotalPaymentDue() {
        Money totalAmt = getTotalAmountInArrears();
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null
                && !nextInstallment.isPaid()
                && DateUtils.getDateWithoutTimeStamp(nextInstallment.getActionDate().getTime()).equals(
                        DateUtils.getCurrentDateWithoutTimeStamp())) {
            totalAmt = totalAmt.add(getDueAmount(nextInstallment));
        }
        return totalAmt;
    }

    /*
     * By default, an account will use the system defined default currency. Derived classes like LoanBO can override
     * this to get the currency from the associated product (PrdOfferingBO)
     */
    public MifosCurrency getCurrency() {
        return Money.getDefaultCurrency();
    }
    
    public Money getTotalAmountDueOn(LocalDate paymentDueAsOf) {
    	
    	Money amountInArrearsOn = getTotalAmountInArrearsOn(paymentDueAsOf);
    	
    	Money amountDue = getUpcomingInstallmentAmountDueOn(paymentDueAsOf);
    	
		return amountInArrearsOn.add(amountDue);
	}

    private Money getUpcomingInstallmentAmountDueOn(LocalDate asOf) {
    	
    	Money upcomingAmount = Money.zero(getCurrency());
    	
    	AccountActionDateEntity nextInstallment = getDetailsOfNextInstallmentOn(asOf);
    	if (nextInstallment != null) {
    		upcomingAmount = upcomingAmount.add(getDueAmount(nextInstallment));
    	}
		return upcomingAmount;
	}

	private Money getTotalAmountInArrearsOn(LocalDate asOf) {
    	List<AccountActionDateEntity> installmentsInArrears = getDetailsOfInstallmentsInArrearsOn(asOf);
        Money totalAmount = Money.zero(getCurrency());
        for (AccountActionDateEntity accountAction : installmentsInArrears) {
            totalAmount = totalAmount.add(getDueAmount(accountAction));
        }
        return totalAmount;
	}

	public Money getTotalAmountInArrears() {
        List<AccountActionDateEntity> installmentsInArrears = getDetailsOfInstallmentsInArrears();
        Money totalAmount = new Money(getCurrency());
        if (installmentsInArrears != null && installmentsInArrears.size() > 0) {
            for (AccountActionDateEntity accountAction : installmentsInArrears) {
                totalAmount = totalAmount.add(getDueAmount(accountAction));
            }
        }
        return totalAmount;
    }

    public List<AccountActionDateEntity> getTotalInstallmentsDue() {
        List<AccountActionDateEntity> dueInstallments = getDetailsOfInstallmentsInArrears();
        AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
        if (nextInstallment != null
                && !nextInstallment.isPaid()
                && DateUtils.getDateWithoutTimeStamp(nextInstallment.getActionDate().getTime()).equals(
                        DateUtils.getCurrentDateWithoutTimeStamp())) {
            dueInstallments.add(nextInstallment);
        }
        return dueInstallments;
    }

    private boolean isTrxnDateBeforePreviousMeetingDateAllowed(final Date trxnDate, Date meetingDate, boolean repaymentIndependentOfMeetingEnabled) {

            if (repaymentIndependentOfMeetingEnabled) {
                // payment date for loans must be >= disbursement date
                if (this instanceof LoanBO) {
                    Date approvalDate = this.getAccountApprovalDate();
                    return trxnDate.compareTo(DateUtils.getDateWithoutTimeStamp(approvalDate)) >= 0;
                }
                // must be >= creation date for other accounts
                return trxnDate.compareTo(DateUtils.getDateWithoutTimeStamp(this.getCreatedDate())) >= 0;
            } else if (meetingDate != null) {
                    return trxnDate.compareTo(DateUtils.getDateWithoutTimeStamp(meetingDate)) >= 0;
            }
            return false;
    }
    
    private boolean isTrxnDateAfterMonthClosingDate(final Date trxnDate){
        ConfigurationKeyValue configurationKeyValue = getConfigurationPersistence().getConfigurationKeyValue(MONTH_CLOSING_DAY_CONFIG_KEY);

        if (configurationKeyValue != null) {
        	Date monthClosingDay = DateUtils.getDateAsRetrievedFromDb(configurationKeyValue.getValue());
            return monthClosingDay.compareTo(DateUtils.getDateWithoutTimeStamp(trxnDate)) < 0;
        }
        
        return true;
    }

    public boolean isTrxnDateValid(final Date trxnDate, Date lastCustomerMeetingDate, boolean repaymentIndependentOfMeetingEnabled) {
        boolean isTrxnDateAfterMonthClosingDay = true;
    	
    	if (getConfigurationPersistence().isMonthClosingDaySet()){
        	isTrxnDateAfterMonthClosingDay = isTrxnDateAfterMonthClosingDate(trxnDate);
        }
    	if (AccountingRules.isBackDatedTxnAllowed()) {
            return isTrxnDateAfterMonthClosingDay && isTrxnDateBeforePreviousMeetingDateAllowed(trxnDate, lastCustomerMeetingDate, repaymentIndependentOfMeetingEnabled);
        }
        return isTrxnDateAfterMonthClosingDay && trxnDate.equals(DateUtils.getCurrentDateWithoutTimeStamp());
    }

    public List<AccountNotesEntity> getRecentAccountNotes() {
        List<AccountNotesEntity> notes = new ArrayList<AccountNotesEntity>();
        int count = 0;
        for (AccountNotesEntity accountNotesEntity : getAccountNotes()) {
            if (count > 2) {
                break;
            }
            notes.add(accountNotesEntity);
            count++;
        }
        return notes;
    }

    public void update() throws AccountException {
        setUpdateDetails();
        try {
            getlegacyAccountDao().createOrUpdate(this);
        } catch (PersistenceException e) {
            throw new AccountException(e);
        }
    }

    public AccountState getState() {
        return AccountState.fromShort(getAccountState().getId());
    }

    protected void updateAccountActivity(final Money principal, final Money interest, final Money fee,
            final Money penalty, final Short personnelId, final String description) throws AccountException {
    }

    public void waiveAmountOverDue(final WaiveEnum waiveType) throws AccountException {
    }

    public Boolean isCurrentDateGreaterThanFirstInstallment() {
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            if (DateUtils.getCurrentDateWithoutTimeStamp().compareTo(
                    DateUtils.getDateWithoutTimeStamp(accountActionDateEntity.getActionDate().getTime())) >= 0) {
                return true;
            }
        }
        return false;
    }

    public void applyCharge(final Short feeId, final Double charge) throws AccountException, PersistenceException {
    }

    public AccountTypes getType() {
        throw new RuntimeException("should be implemented in subclass");
    }

    public boolean isOpen() {
        return true;
    }

    public boolean isUpcomingInstallmentUnpaid() {
        AccountActionDateEntity accountActionDateEntity = getDetailsOfUpcomigInstallment();
        if (accountActionDateEntity != null) {
            if (!accountActionDateEntity.isPaid()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the next {@link AccountActionDateEntity} occuring after today, if any, otherwise return null.
     * <p>
     * If more than one installment occurs on the next closest installment date, the method returns the entity with the
     * lowest installment id.
     */
    public AccountActionDateEntity getDetailsOfUpcomigInstallment() {
        AccountActionDateEntity nextAccountAction = null;
        Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
        if (getAccountActionDates() != null && getAccountActionDates().size() > 0) {
            for (AccountActionDateEntity accountAction : getAccountActionDates()) {
                if (accountAction.getActionDate().compareTo(currentDate) > 0) {
                    if (null == nextAccountAction) {
                        nextAccountAction = accountAction;
                    } else if (nextAccountAction.getInstallmentId() > accountAction.getInstallmentId()) {
                        nextAccountAction = accountAction;
                    }
                }
            }
        }
        return nextAccountAction;
    }

    /*
     * Delegate to an injected service.
     */
    public final void buildFinancialEntries(final Set<AccountTrxnEntity> accountTrxns) throws AccountException {
        getFinancialBusinessService().buildFinancialEntries(accountTrxns);
    }

    public final String generateId(final String officeGlobalNum) throws AccountException {

        if (StringUtils.isBlank(officeGlobalNum)) {
            throw new BusinessRuleException(AccountExceptionConstants.IDGenerationException);
        }

        StringBuilder systemId = new StringBuilder();
        systemId.append(officeGlobalNum);
        try {
            systemId.append(StringUtils.leftPad(getAccountId().toString(), 11, '0'));
        } catch (Exception se) {
            throw new AccountException(AccountExceptionConstants.IDGenerationException, se);
        }
        return systemId.toString();
    }

    protected final List<AccountActionDateEntity> getDueInstallments() {
        List<AccountActionDateEntity> dueInstallmentList = new ArrayList<AccountActionDateEntity>();
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            if (!accountActionDateEntity.isPaid()) {
                if (accountActionDateEntity.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
                    dueInstallmentList.add(accountActionDateEntity);
                }
            }
        }
        return dueInstallmentList;
    }

    /**
     * Get all unpaid {@link AccountActionDateEntity}s due today or in the future for this account.
     */
    protected final List<AccountActionDateEntity> getTotalDueInstallments() {
        List<AccountActionDateEntity> dueInstallmentList = new ArrayList<AccountActionDateEntity>();
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            if (!accountActionDateEntity.isPaid()) {
                if (accountActionDateEntity.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) >= 0) {
                    dueInstallmentList.add(accountActionDateEntity);
                }
            }
        }
        return dueInstallmentList;
    }

    protected final Boolean isFeeAlreadyApplied(final FeeBO fee) {
        return getAccountFees(fee.getFeeId()) != null;
    }

    /**
     * If the given {@FeeBO} has not yet been applied to this account, build and return a new
     * {@link AccountFeesEntity} linking this account to the fee; otherwise return the link object with the fee amount
     * replaced with the charge.
     *
     * @param fee
     *            the fee to apply or update
     * @param charge
     *            the amount to charge for the given fee
     * @return return the new or updated {@link AccountFeesEntity} linking this account to the fee
     */
    protected final AccountFeesEntity getAccountFee(final FeeBO fee, final Double charge) {
        AccountFeesEntity accountFee = null;
        if (fee.isPeriodic() && isFeeAlreadyApplied(fee)) {
            accountFee = getAccountFees(fee.getFeeId());
            accountFee.setFeeAmount(charge);
            accountFee.setFeeStatus(FeeStatus.ACTIVE);
            accountFee.setStatusChangeDate(getDateTimeService().getCurrentJavaDateTime());
        } else {
            accountFee = new AccountFeesEntity(this, fee, charge, FeeStatus.ACTIVE.getValue(), null, null);
        }
        return accountFee;
    }

    @Deprecated
    public final List<InstallmentDate> getInstallmentDates(final MeetingBO meeting, final Short noOfInstallments,
            final Short installmentToSkip) {
        return getInstallmentDates(meeting, noOfInstallments, installmentToSkip, false);
    }

    /**
     * @deprecated - logic for generating installments is pulled out of loan/account.
     */
    @Deprecated
    public final List<InstallmentDate> getInstallmentDates(final MeetingBO meeting, final Short noOfInstallments,
            final Short installmentToSkip, final boolean isRepaymentIndepOfMeetingEnabled) {

        return getInstallmentDates(meeting, noOfInstallments, installmentToSkip, false, true);
    }

    /**
     * @deprecated - used to create installment dates based on 'loan meeting' and working das, holidays, moratoria etc
     *
     * better to pull capability of creating 'installments' out of loan into something more reuseable and isolated
     */
    @Deprecated
    public final List<InstallmentDate> getInstallmentDates(final MeetingBO meeting, final Short noOfInstallments,
            final Short installmentToSkip, final boolean isRepaymentIndepOfMeetingEnabled,
            final boolean adjustForHolidays) {

        logger.debug("Generating intallment dates");

        List<InstallmentDate> dueInstallmentDates = new ArrayList<InstallmentDate>();
        if (noOfInstallments > 0) {
            List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
            List<Holiday> holidays = new ArrayList<Holiday>();

            DateTime startFromMeetingDate = new DateTime(meeting.getMeetingStartDate());

            if (adjustForHolidays) {
                HolidayDao holidayDao = ApplicationContextProvider.getBean(HolidayDao.class);
                holidays = holidayDao.findAllHolidaysFromDateAndNext(getOffice().getOfficeId(), startFromMeetingDate.toLocalDate().toString());
            }

            final int occurrences = noOfInstallments + installmentToSkip;

            ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(meeting);
            ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(workingDays, holidays);

            List<Date> dueDates = new ArrayList<Date>();
            // FIXME - keithw - this whole area of installment creation should be pulled out of domain
            DateTime startFromDayAfterAssignedMeetingDateRatherThanSkippingInstallments = startFromMeetingDate;
            if (this.isLoanAccount()) {
                // ensure loans that are created or disbursed on a meeting date start on next valid meeting date and not todays meeting
                // ensure loans that are created or disbursed before a meeting date start on next valid meeting date
                startFromDayAfterAssignedMeetingDateRatherThanSkippingInstallments = startFromMeetingDate.plusDays(1);
            }
            List<DateTime> installmentDates = dateGeneration.generateScheduledDates(occurrences, startFromDayAfterAssignedMeetingDateRatherThanSkippingInstallments, scheduledEvent, false);
            for (DateTime installmentDate : installmentDates) {
                dueDates.add(installmentDate.toDate());
            }

            dueInstallmentDates = createInstallmentDates(installmentToSkip, dueDates);
        }
        return dueInstallmentDates;
    }

    /**
     * @deprecated - remove when loan schedules or 'installment' creation responsibility is moved out of account/loan
     */
    @Deprecated
    protected List<InstallmentDate> createInstallmentDates(final Short installmentToSkip, final List<Date> dueDates) {
        List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
        int installmentId = 1;
        for (Date date : dueDates) {
            installmentDates.add(new InstallmentDate((short) installmentId++, date));
        }
        removeInstallmentsNeedNotPay(installmentToSkip, installmentDates);
        return installmentDates;
    }

    /**
     *
     * @param installmentDates
     *            dates adjusted for holidays
     * @param nonAdjustedInstallmentDates
     *            dates not adjusted for holidays
     */
    protected final List<FeeInstallment> getFeeInstallments(final List<InstallmentDate> installmentDates,
            final List<InstallmentDate> nonAdjustedInstallmentDates) throws AccountException {
        List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
        for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
            if (accountFeesEntity.isActive()) {
                Short accountFeeType = accountFeesEntity.getFees().getFeeFrequency().getFeeFrequencyType().getId();
                if (accountFeeType.equals(FeeFrequencyType.ONETIME.getValue())) {
                    feeInstallmentList.add(handleOneTime(accountFeesEntity, installmentDates));
                } else if (accountFeeType.equals(FeeFrequencyType.PERIODIC.getValue())) {
                    feeInstallmentList.addAll(handlePeriodic(accountFeesEntity, installmentDates,
                            nonAdjustedInstallmentDates));
                }
            }
        }
        return feeInstallmentList;
    }

    public final List<Date> getFeeDates(final MeetingBO feeMeetingFrequency,
            final List<InstallmentDate> installmentDates) {
        return getFeeDates(feeMeetingFrequency, installmentDates, true);
    }

    public final List<Date> getFeeDates(final MeetingBO feeMeetingFrequency,
            final List<InstallmentDate> installmentDates, final boolean adjustForHolidays) {

        MeetingBO customerMeeting = getCustomer().getCustomerMeetingValue();

        List<Days> workingDays = new FiscalCalendarRules().getWorkingDaysAsJodaTimeDays();
        HolidayDao holidayDao = ApplicationContextProvider.getBean(HolidayDao.class);
        List<Holiday> holidays;
        if (adjustForHolidays) {
            holidays = holidayDao.findAllHolidaysThisYearAndNext(getOffice().getOfficeId());
        } else {
            holidays = new ArrayList<Holiday>();
        }

        DateTime startFromMeetingDate = new DateTime(installmentDates.get(0).getInstallmentDueDate());
        DateTime repaymentEndDatetime = new DateTime(installmentDates.get(installmentDates.size() - 1)
                .getInstallmentDueDate());
        ScheduledEvent scheduledEvent = ScheduledEventFactory.createScheduledEventFrom(customerMeeting,
                feeMeetingFrequency);
        ScheduledDateGeneration dateGeneration = new HolidayAndWorkingDaysAndMoratoriaScheduledDateGeneration(
                workingDays, holidays);

        List<DateTime> feeScheduleDates = dateGeneration.generateScheduledDatesThrough(startFromMeetingDate,
                repaymentEndDatetime, scheduledEvent, true);

        List<Date> feeSchedulesAsJavaDates = new ArrayList<Date>();
        for (DateTime feeSchedule : feeScheduleDates) {
            feeSchedulesAsJavaDates.add(feeSchedule.toDate());
        }

        return feeSchedulesAsJavaDates;
    }

    protected final FeeInstallment buildFeeInstallment(final Short installmentId, final Money accountFeeAmount,
            final AccountFeesEntity accountFee) {
        FeeInstallment feeInstallment = new FeeInstallment();
        feeInstallment.setInstallmentId(installmentId);
        feeInstallment.setAccountFee(accountFeeAmount);
        feeInstallment.setAccountFeesEntity(accountFee);
        accountFee.setAccountFeeAmount(accountFeeAmount);
        return feeInstallment;
    }

    protected final Short getMatchingInstallmentId(final List<InstallmentDate> installmentDates, final Date feeDate) {
        for (InstallmentDate installmentDate : installmentDates) {
            if (DateUtils.getDateWithoutTimeStamp(installmentDate.getInstallmentDueDate().getTime()).compareTo(
                    DateUtils.getDateWithoutTimeStamp(feeDate.getTime())) >= 0) {
                return installmentDate.getInstallmentId();
            }
        }
        return null;
    }

    protected final List<FeeInstallment> mergeFeeInstallments(final List<FeeInstallment> feeInstallmentList) {
        List<FeeInstallment> newFeeInstallmentList = new ArrayList<FeeInstallment>();
        for (Iterator<FeeInstallment> iterator = feeInstallmentList.iterator(); iterator.hasNext();) {
            FeeInstallment feeInstallment = iterator.next();
            iterator.remove();
            FeeInstallment feeInstTemp = null;
            for (FeeInstallment feeInst : newFeeInstallmentList) {
                if (feeInst.getInstallmentId().equals(feeInstallment.getInstallmentId())
                        && feeInst.getAccountFeesEntity().equals(feeInstallment.getAccountFeesEntity())) {
                    feeInstTemp = feeInst;
                    break;
                }
            }
            if (feeInstTemp != null) {
                newFeeInstallmentList.remove(feeInstTemp);
                feeInstTemp.setAccountFee(feeInstTemp.getAccountFee().add(feeInstallment.getAccountFee()));
                newFeeInstallmentList.add(feeInstTemp);
            } else {
                newFeeInstallmentList.add(feeInstallment);
            }
        }
        return newFeeInstallmentList;
    }

    protected boolean isCurrentDateEquallToInstallmentDate() {
        for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
            if (!accountActionDateEntity.isPaid()) {
                if (accountActionDateEntity.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    protected List<AccountFeesEntity> getPeriodicFeeList() {
        List<AccountFeesEntity> periodicFeeList = new ArrayList<AccountFeesEntity>();
        for (AccountFeesEntity accountFee : getAccountFees()) {
            if (accountFee.getFees().isPeriodic() && accountFee.isActive()) {
                // Why? Doesn't appear to do anything with the retrieved FeeBO
                // getFeePersistence().getFee(accountFee.getFees().getFeeId());
                periodicFeeList.add(accountFee);
            }
        }
        return periodicFeeList;
    }

    protected void deleteFutureInstallments() throws AccountException {
        List<AccountActionDateEntity> futureInstllments = getApplicableIdsForFutureInstallments();
        for (AccountActionDateEntity accountActionDateEntity : futureInstllments) {
            accountActionDates.remove(accountActionDateEntity);
            try {
                getlegacyAccountDao().delete(accountActionDateEntity);
            } catch (PersistenceException e) {
                throw new AccountException(e);
            }
        }
    }

    /**
     * This can be very inefficient if an account has many action dates. Note that an account having too many action
     * dates may also be a sign of bad data--at least, this was the case with some GK data.
     */
    public Short getLastInstallmentId() {
        Short lastInstallmentId = null;
        for (AccountActionDateEntity date : this.getAccountActionDates()) {

            if (lastInstallmentId == null) {
                lastInstallmentId = date.getInstallmentId();
            } else {
                if (lastInstallmentId < date.getInstallmentId()) {
                    lastInstallmentId = date.getInstallmentId();
                }
            }
        }
        return lastInstallmentId;

    }

    protected List<AccountTrxnEntity> getAccountTrxnsOrderByTrxnCreationDate() {
        List<AccountTrxnEntity> accountTrxnList = new ArrayList<AccountTrxnEntity>();
        for (AccountPaymentEntity payment : getAccountPayments()) {
            accountTrxnList.addAll(payment.getAccountTrxns());
        }

        Collections.sort(accountTrxnList, new Comparator<AccountTrxnEntity>() {
            @Override
            public int compare(final AccountTrxnEntity trx1, final AccountTrxnEntity trx2) {
                if (trx1.getTrxnCreatedDate().equals(trx2.getTrxnCreatedDate())) {
                    return trx1.getAccountTrxnId().compareTo(trx2.getAccountTrxnId());
                }
                return trx1.getTrxnCreatedDate().compareTo(trx2.getTrxnCreatedDate());
            }
        });
        return accountTrxnList;
    }

    protected List<AccountTrxnEntity> getAccountTrxnsOrderByTrxnDate() {
        List<AccountTrxnEntity> accountTrxnList = new ArrayList<AccountTrxnEntity>();
        for (AccountPaymentEntity payment : getAccountPayments()) {
            accountTrxnList.addAll(payment.getAccountTrxns());
        }

        Collections.sort(accountTrxnList, new Comparator<AccountTrxnEntity>() {
            @Override
            public int compare(final AccountTrxnEntity trx1, final AccountTrxnEntity trx2) {
                if (trx1.getAccountTrxnId() != null && trx2.getAccountTrxnId() != null && trx1.getActionDate().equals(trx2.getActionDate())) {
                    return trx1.getAccountTrxnId().compareTo(trx2.getAccountTrxnId());
                }
                return trx1.getActionDate().compareTo(trx2.getActionDate());
            }
        });
        return accountTrxnList;
    }

    protected void resetAccountActionDates() {
        this.accountActionDates.clear();
    }

    protected void updateInstallmentAfterAdjustment(final List<AccountTrxnEntity> reversedTrxns, PersonnelBO loggedInUser)
            throws AccountException {
    }

    protected Money getDueAmount(final AccountActionDateEntity installment) {
        return null;
    }

    protected void regenerateFutureInstallments(final AccountActionDateEntity nextInstallment,
            final List<Days> workingDays, final List<Holiday> holidays) throws AccountException {
    }

    protected List<FeeInstallment> handlePeriodic(final AccountFeesEntity accountFees,
            final List<InstallmentDate> installmentDates, final List<InstallmentDate> nonAdjustedInstallmentDates)
            throws AccountException {
        return null;
    }

    /**
     * {@link AccountPaymentEntity} and not {@link PaymentData} dto
     */
    protected AccountPaymentEntity makePayment(final PaymentData accountPaymentData) throws AccountException {
        return null;
    }

    protected void updateTotalFeeAmount(final Money totalFeeAmount) {
    }

    protected void updateTotalPenaltyAmount(final Money totalPenaltyAmount) {
    }

    protected Money updateAccountActionDateEntity(final List<Short> intallmentIdList, final Short feeId) {
        return new Money(getCurrency());
    }

    protected boolean isAdjustPossibleOnLastTrxn() {
        return false;
    }

    public void removeFeesAssociatedWithUpcomingAndAllKnownFutureInstallments(final Short feeId, final Short personnelId)
            throws AccountException {
    }

    protected void activationDateHelper(final Short newStatusId) throws AccountException {
    }

    /**
     * Return list of unpaid AccountActionDateEntities occurring on or after today
     */
    protected List<Short> getApplicableInstallmentIdsForRemoveFees() {
        List<Short> installmentIdList = new ArrayList<Short>();
        for (AccountActionDateEntity accountActionDateEntity : getApplicableIdsForFutureInstallments()) {
            installmentIdList.add(accountActionDateEntity.getInstallmentId());
        }
        AccountActionDateEntity accountActionDateEntity = getDetailsOfNextInstallment();
        if (accountActionDateEntity != null) {
            installmentIdList.add(accountActionDateEntity.getInstallmentId());
        }

        return installmentIdList;
    }

    private void setFinancialEntries(final FinancialTransactionBO financialTrxn,
            final TransactionHistoryDto transactionHistory) {
        String debit = "-";
        String credit = "-";
        String notes = "-";
        if (financialTrxn.isDebitEntry()) {
            debit = String.valueOf(removeSign(financialTrxn.getPostedAmount()));
        } else if (financialTrxn.isCreditEntry()) {
            credit = String.valueOf(removeSign(financialTrxn.getPostedAmount()));
        }
        Short entityId = financialTrxn.getAccountTrxn().getAccountActionEntity().getId();
        if (financialTrxn.getNotes() != null && !financialTrxn.getNotes().equals("")
                && !entityId.equals(AccountActionTypes.CUSTOMER_ACCOUNT_REPAYMENT.getValue())
                && !entityId.equals(AccountActionTypes.LOAN_REPAYMENT.getValue())) {
            notes = financialTrxn.getNotes();
        }

        transactionHistory.setFinancialEnteries(financialTrxn.getTrxnId(), financialTrxn.getActionDate(), financialTrxn
                .getFinancialAction().getName(), financialTrxn.getGlcode().getGlcode(), financialTrxn.getGlcode().getAssociatedCOA().getAccountName(), debit, credit, financialTrxn
                .getPostedDate(), notes);

    }

    private void setAccountingEntries(final AccountTrxnEntity accountTrxn,
            final TransactionHistoryDto transactionHistory) {

        transactionHistory.setAccountingEnteries(accountTrxn.getAccountPayment().getPaymentId(), String
                .valueOf(removeSign(accountTrxn.getAmount())), accountTrxn.getCustomer().getDisplayName(), accountTrxn
                .getPersonnel().getDisplayName());
    }

    private AccountCustomFieldEntity getAccountCustomField(final Short fieldId) {
        if (null != this.accountCustomFields && this.accountCustomFields.size() > 0) {
            for (AccountCustomFieldEntity obj : this.accountCustomFields) {
                if (obj.getFieldId().equals(fieldId)) {
                    return obj;
                }
            }
        }
        return null;
    }

    protected final FeeInstallment handleOneTime(final AccountFeesEntity accountFee,
            final List<InstallmentDate> installmentDates) {
        Money accountFeeAmount = accountFee.getAccountFeeAmount();
        Date feeDate = installmentDates.get(0).getInstallmentDueDate();
        logger.debug("Handling OneTime fee" + feeDate);
        Short installmentId = getMatchingInstallmentId(installmentDates, feeDate);
        logger.debug(
                "OneTime fee applicable installment id " + installmentId);
        return buildFeeInstallment(installmentId, accountFeeAmount, accountFee);
    }

    private void removeInstallmentsNeedNotPay(final Short installmentSkipToStartRepayment,
            final List<InstallmentDate> installmentDates) {
        int removeCounter = 0;
        for (int i = 0; i < installmentSkipToStartRepayment; i++) {
            installmentDates.remove(removeCounter);
        }
        // re-adjust the installment ids
        if (installmentSkipToStartRepayment > 0) {
            int count = installmentDates.size();
            for (int i = 0; i < count; i++) {
                InstallmentDate instDate = installmentDates.get(i);
                instDate.setInstallmentId(new Short(Integer.toString(i + 1)));
            }
        }
    }

    private void validate(final UserContext userContext, final CustomerBO customer, final AccountTypes accountType,
            final AccountState accountState) throws AccountException {
        if (userContext == null || customer == null || accountType == null || accountState == null) {
            throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION);
        }
    }

    private void setFlag(final AccountStateFlagEntity accountStateFlagEntity) {
        Iterator iter = this.getAccountFlags().iterator();
        while (iter.hasNext()) {
            AccountFlagMapping currentFlag = (AccountFlagMapping) iter.next();
            if (!currentFlag.getFlag().isFlagRetained()) {
                iter.remove();
            }
        }
        this.addAccountFlag(accountStateFlagEntity);
    }

    protected void updateCustomFields(final List<CustomFieldDto> customFields) throws InvalidDateException {
        if (customFields == null) {
            return;
        }
        for (CustomFieldDto fieldView : customFields) {
            if (fieldView.getFieldType().equals(CustomFieldType.DATE.getValue())
                    && org.apache.commons.lang.StringUtils.isNotBlank(fieldView.getFieldValue())) {
                SimpleDateFormat format = (SimpleDateFormat) DateFormat.getDateInstance(DateFormat.SHORT, getUserContext().getPreferredLocale());
                String userfmt = DateUtils.convertToCurrentDateFormat(format.toPattern());
                fieldView.setFieldValue(DateUtils.convertUserToDbFmt(fieldView.getFieldValue(), userfmt));
            }
            if (getAccountCustomFields().size() > 0) {
                for (AccountCustomFieldEntity fieldEntity : getAccountCustomFields()) {
                    if (fieldView.getFieldId().equals(fieldEntity.getFieldId())) {
                        fieldEntity.setFieldValue(fieldView.getFieldValue());
                    }
                }
            } else {
                for (CustomFieldDto view : customFields) {
                    this.getAccountCustomFields().add(
                            new AccountCustomFieldEntity(this, view.getFieldId(), view.getFieldValue()));
                }
            }
        }
    }

    public Date getAccountApprovalDate() {
        Date approvalDate = null;
        List<AccountStatusChangeHistoryEntity> statusChangeHistory = this.getAccountStatusChangeHistory();

        for (AccountStatusChangeHistoryEntity status : statusChangeHistory) {

            if (status.getNewStatus().isInState(AccountState.LOAN_APPROVED)) {
                approvalDate = status.getCreatedDate();
                break;
            }
        }
        return approvalDate;
    }

    public Integer getOffsettingAllowable() {
        return offsettingAllowable;
    }

    public void setOffsettingAllowable(final Integer offsettingAllowable) {
        this.offsettingAllowable = offsettingAllowable;
    }

    public boolean isInState(final AccountState state) {
        return accountState.isInState(state);
    }

    public boolean isLoanAccount() {
        return isOfType(LOAN_ACCOUNT);
    }

    public boolean isSavingsAccount() {
        return isOfType(SAVINGS_ACCOUNT);
    }

    public boolean isOfType(final AccountTypes accountType) {
        return accountType.equals(getType());
    }

    @Override
    public String toString() {
        return "{" + globalAccountNum + "}";
    }

    public boolean isActiveLoanAccount() {
        return AccountState.fromShort(accountState.getId()).isActiveLoanAccountState();
    }

    public void setExternalId(final String externalId) {
        this.externalId = externalId;
    }

    public String getExternalId() {
        return externalId;
    }

    /**
     * Return true if a given payment amount valid for this account.
     *
     * @param amount
     *            the payment amount to validate.
     *
     */

    public boolean paymentAmountIsValid(final Money amount, Set<AccountPaymentParametersDto.PaymentOptions> options) {
        return true;
    }

    protected void updateSchedule(final Short nextInstallmentId, final List<DateTime> meetingDates) {
        short installmentId = nextInstallmentId;
        for (int count = 0; count < meetingDates.size(); count++) {
            AccountActionDateEntity accountActionDate = getAccountActionDate(installmentId);
            if (accountActionDate != null) {
                DateTime meetingDate = meetingDates.get(count);
                accountActionDate.setActionDate(new java.sql.Date(meetingDate.toDate().getTime()));
            }
            installmentId++;
        }
    }

    // To be used strictly from test code
    @Deprecated
    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public void setAccountCustomFields(Set<AccountCustomFieldEntity> accountCustomFields) {
        this.accountCustomFields = accountCustomFields;
    }

    public Short getOfficeId() {
        return office.getOfficeId();
    }

    public boolean isCustomerAccount() {
        return AccountTypes.CUSTOMER_ACCOUNT.equals(this.getType());
    }

    private void changeActionDateOfFirstInstallment(Calendar date, Set<AccountActionDateEntity> accountActionDates) {
        if (accountActionDates.isEmpty()) {
            return;
        }
        java.sql.Date actionDate = new java.sql.Date(date.getTimeInMillis());
        accountActionDates.toArray(new AccountActionDateEntity[accountActionDates.size()])[0].setActionDate(actionDate);
    }

    public void changeFirstInstallmentDateBy(final int numberOfDays) {
        Calendar currentDateCalendar = new GregorianCalendar();
        int year = currentDateCalendar.get(Calendar.YEAR);
        int month = currentDateCalendar.get(Calendar.MONTH);
        int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
        currentDateCalendar = new GregorianCalendar(year, month, day + numberOfDays);
        changeActionDateOfFirstInstallment(currentDateCalendar, getAccountActionDates());
    }
    /**
    * Check if account is active savings account
    * @return true if account is savings account and is in active state
    */
    public boolean isActiveSavingsAccount(){
        return AccountState.fromShort(accountState.getId()).isActiveSavingsAccountState();
    }

}
