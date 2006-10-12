/**

 * AccountBO.java    version: xxx

 

 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.

 

 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.accounts.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountExceptionConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.FeeInstallment;
import org.mifos.application.accounts.util.helpers.InstallmentDate;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.valueobjects.AccountType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;

public class AccountBO extends BusinessObject {

	private final Integer accountId;

	protected String globalAccountNum;

	protected final AccountType accountType;

	protected final CustomerBO customer;

	protected final OfficeBO office;

	protected final PersonnelBO personnel;

	protected Set<AccountNotesEntity> accountNotes;

	protected Set<AccountStatusChangeHistoryEntity> accountStatusChangeHistory;

	private AccountStateEntity accountState;

	private Set<AccountFlagMapping> accountFlags;

	private Set<AccountFeesEntity> accountFees;

	private Set<AccountActionDateEntity> accountActionDates;

	private Set<AccountPaymentEntity> accountPayments;

	private Set<AccountCustomFieldEntity> accountCustomFields;

	private Date closedDate;

	protected AccountBO() {
		accountId = null;
		globalAccountNum = null;
		customer = null;
		office = null;
		personnel = null;
		accountType = null;
		accountFees = new HashSet<AccountFeesEntity>();
		accountPayments = new HashSet<AccountPaymentEntity>();
		accountActionDates = new HashSet<AccountActionDateEntity>();
		accountCustomFields = new HashSet<AccountCustomFieldEntity>();
		accountNotes = new HashSet<AccountNotesEntity>();
		accountStatusChangeHistory = new HashSet<AccountStatusChangeHistoryEntity>();
		accountFlags = new HashSet<AccountFlagMapping>();
	}

	protected AccountBO(UserContext userContext, CustomerBO customer,
			AccountTypes accountType, AccountState accountState)
			throws AccountException {
		super(userContext);
		validate(userContext, customer, accountType, accountState);

		accountFees = new HashSet<AccountFeesEntity>();
		accountPayments = new HashSet<AccountPaymentEntity>();
		accountActionDates = new HashSet<AccountActionDateEntity>();
		accountCustomFields = new HashSet<AccountCustomFieldEntity>();
		accountNotes = new HashSet<AccountNotesEntity>();
		accountStatusChangeHistory = new HashSet<AccountStatusChangeHistoryEntity>();
		accountFlags = new HashSet<AccountFlagMapping>();
		this.accountId = null;
		this.customer = customer;
		this.accountType = new AccountType(accountType.getValue());
		this.office = customer.getOffice();
		this.personnel = customer.getPersonnel();
		this.setAccountState(new AccountStateEntity(accountState));
		setCreateDetails();
	}

	public Integer getAccountId() {
		return accountId;
	}

	public String getGlobalAccountNum() {
		return globalAccountNum;
	}

	public AccountType getAccountType() {
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

	public Set<AccountStatusChangeHistoryEntity> getAccountStatusChangeHistory() {
		return accountStatusChangeHistory;
	}

	public AccountStateEntity getAccountState() {
		return accountState;
	}

	public Set<AccountFlagMapping> getAccountFlags() {
		return accountFlags;
	}

	public Set<AccountFeesEntity> getAccountFees() {
		return accountFees;
	}

	public Set<AccountActionDateEntity> getAccountActionDates() {
		return accountActionDates;
	}

	public Set<AccountPaymentEntity> getAccountPayments() {
		return accountPayments;
	}

	public Set<AccountCustomFieldEntity> getAccountCustomFields() {
		return accountCustomFields;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
	}

	public void setAccountPayments(Set<AccountPaymentEntity> accountPayments) {
		this.accountPayments = accountPayments;
	}

	public void setAccountState(AccountStateEntity accountState) {
		this.accountState = accountState;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
	}

	public void addAccountStatusChangeHistory(
			AccountStatusChangeHistoryEntity accountStatusChangeHistoryEntity) {
		this.accountStatusChangeHistory.add(accountStatusChangeHistoryEntity);
	}

	public void addAccountFees(AccountFeesEntity fees) {
		accountFees.add(fees);
	}

	public void addAccountActionDate(AccountActionDateEntity accountAction) {
		if (accountAction == null) {
			throw new NullPointerException();
		}
		accountActionDates.add(accountAction);
	}

	public void addAccountPayment(AccountPaymentEntity payment) {
		if (accountPayments == null)
			accountPayments = new HashSet<AccountPaymentEntity>();
		accountPayments.add(payment);
	}

	public void addAccountNotes(AccountNotesEntity notes) {
		accountNotes.add(notes);
	}

	public void addAccountFlag(AccountStateFlagEntity flagDetail) {
		AccountFlagMapping flagMap = new AccountFlagMapping();
		flagMap.setCreatedBy(this.getUserContext().getId());
		flagMap.setCreatedDate(new Date());
		flagMap.setFlag(flagDetail);
		this.accountFlags.add(flagMap);
	}

	public void addAccountCustomField(AccountCustomFieldEntity customField) {
		if (customField.getFieldId() != null) {
			AccountCustomFieldEntity accountCustomField = getAccountCustomField(customField
					.getFieldId());
			if (accountCustomField == null) {
				customField.setAccount(this);
				this.accountCustomFields.add(customField);
			} else {
				accountCustomField.setFieldValue(customField.getFieldValue());
			}
		}
	}

	public final void applyPayment(PaymentData paymentData)
			throws AccountException {
		AccountPaymentEntity accountPayment = makePayment(paymentData);
		addAccountPayment(accountPayment);
		buildFinancialEntries(accountPayment.getAccountTrxns());
		try {
			(new AccountPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public final void adjustPmnt(String adjustmentComment)
			throws AccountException {
		if (isAdjustPossibleOnLastTrxn()) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"Adjustment is possible hence attempting to adjust.");
			List<AccountTrxnEntity> reversedTrxns = getLastPmnt()
					.reversalAdjustment(adjustmentComment);
			updateInstallmentAfterAdjustment(reversedTrxns);
			buildFinancialEntries(new HashSet(reversedTrxns));
			updatePerformanceHistoryOnAdjustment(reversedTrxns.size());
			try {
				(new AccountPersistence()).createOrUpdate(this);
			} catch (PersistenceException e) {
				throw new AccountException(
						AccountExceptionConstants.CANNOTADJUST, e);
			}
		} else
			throw new AccountException(AccountExceptionConstants.CANNOTADJUST);
	}

	public final void handleChangeInMeetingSchedule() throws AccountException {
		AccountActionDateEntity accountActionDateEntity = getDetailsOfNextInstallment();
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		short installmentId = 0;
		if (accountActionDateEntity != null) {
			if (accountActionDateEntity.getActionDate().compareTo(currentDate) == 0) {
				installmentId = (short) (accountActionDateEntity
						.getInstallmentId().intValue() + 1);
			} else {
				installmentId = (short) (accountActionDateEntity
						.getInstallmentId().intValue());
			}
			MeetingBO meeting = getCustomer().getCustomerMeeting().getMeeting();
			Calendar meetingStartDate = meeting.getMeetingStartDate();
			meeting.setMeetingStartDate(DateUtils
					.getCalendarDate(accountActionDateEntity.getActionDate()
							.getTime()));
			regenerateFutureInstallments(installmentId);
			meeting.setMeetingStartDate(meetingStartDate);
			try {
				(new AccountPersistence()).createOrUpdate(this);
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}
	}

	public final void changeStatus(Short newStatusId, Short flagId,
			String comment) throws AccountException {
		try {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"In the change status method of AccountBO:: new StatusId= "
							+ newStatusId);
			activationDateHelper(newStatusId);
			MasterPersistence masterPersistence = new MasterPersistence();
			AccountStateEntity accountStateEntity;
			accountStateEntity = (AccountStateEntity) masterPersistence
					.getPersistentObject(AccountStateEntity.class, newStatusId);
			accountStateEntity.setLocaleId(this.getUserContext().getLocaleId());
			AccountStateFlagEntity accountStateFlagEntity = null;
			if (flagId != null) {
				accountStateFlagEntity = (AccountStateFlagEntity) masterPersistence
						.getPersistentObject(AccountStateFlagEntity.class,
								flagId);
			}
			PersonnelBO personnel = new PersonnelPersistence()
					.getPersonnel(getUserContext().getId());
			AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(
					this.getAccountState(), accountStateEntity, personnel, this);
			AccountNotesEntity accountNotesEntity = new AccountNotesEntity(
					new java.sql.Date(System.currentTimeMillis()), comment,
					personnel, this);
			this.addAccountStatusChangeHistory(historyEntity);
			this.setAccountState(accountStateEntity);
			this.addAccountNotes(accountNotesEntity);
			if (accountStateFlagEntity != null) {
				setFlag(accountStateFlagEntity);
			}
			if (newStatusId.equals(AccountState.LOANACC_CANCEL.getValue())
					|| newStatusId.equals(AccountState.LOANACC_OBLIGATIONSMET
							.getValue())
					|| newStatusId.equals(AccountState.LOANACC_RESCHEDULED
							.getValue())
					|| newStatusId.equals(AccountState.LOANACC_WRITTENOFF
							.getValue())
					|| newStatusId.equals(AccountState.SAVINGS_ACC_CANCEL
							.getValue()))
				this.setClosedDate(new Date(System.currentTimeMillis()));
			MifosLogManager
					.getLogger(LoggerConstants.ACCOUNTSLOGGER)
					.debug(
							"Coming out successfully from the change status method of AccountBO");
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public void updateAccountFeesEntity(Short feeId) {
		AccountFeesEntity accountFees = getAccountFees(feeId);
		if (accountFees != null) {
			accountFees.changeFeesStatus(AccountConstants.INACTIVE_FEES,
					new Date(System.currentTimeMillis()));
			accountFees.setLastAppliedDate(null);
		}
	}

	public AccountFeesEntity getAccountFees(Short feeId) {
		for (AccountFeesEntity accountFeesEntity : this.getAccountFees()) {
			if (accountFeesEntity.getFees().getFeeId().equals(feeId)) {
				return accountFeesEntity;
			}
		}
		return null;
	}

	public FeeBO getAccountFeesObject(Short feeId) {
		AccountFeesEntity accountFees = getAccountFees(feeId);
		if (accountFees != null)
			return accountFees.getFees();
		return null;
	}

	public Boolean isFeeActive(Short feeId) {
		AccountFeesEntity accountFees = getAccountFees(feeId);
		return accountFees.getFeeStatus() == null
				|| accountFees.getFeeStatus().equals(
						AccountConstants.ACTIVE_FEES);
	}

	protected Money removeSign(Money amount) {
		if (amount != null && amount.getAmountDoubleValue() < 0)
			return amount.negate();
		else
			return amount;
	}

	public double getLastPmntAmnt() {
		if (null != accountPayments && accountPayments.size() > 0) {
			return getLastPmnt().getAmount().getAmountDoubleValue();
		}
		return 0;
	}

	public AccountPaymentEntity getLastPmnt() {
		AccountPaymentEntity accntPmnt = null;
		for (AccountPaymentEntity accntPayment : accountPayments) {
			accntPmnt = accntPayment;
			break;
		}
		return accntPmnt;
	}

	public AccountActionDateEntity getAccountActionDate(Short installmentId) {
		if (null != accountActionDates && accountActionDates.size() > 0) {
			for (AccountActionDateEntity accntActionDate : accountActionDates) {
				if (accntActionDate.getInstallmentId().equals(installmentId)) {
					return accntActionDate;
				}
			}
		}
		return null;
	}

	public AccountActionDateEntity getAccountActionDate(Short installmentId,
			Integer customerId) {
		if (null != accountActionDates && accountActionDates.size() > 0) {
			for (AccountActionDateEntity accntActionDate : accountActionDates) {
				if (accntActionDate.getInstallmentId().equals(installmentId)
						&& accntActionDate.getCustomer().getCustomerId()
								.equals(customerId)) {
					return accntActionDate;
				}
			}
		}
		return null;
	}

	public List<AccountActionDateEntity> getApplicableIdsForFutureInstallments() {
		List<AccountActionDateEntity> futureActionDateList = new ArrayList<AccountActionDateEntity>();
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment != null) {
			for (AccountActionDateEntity accountActionDate : getAccountActionDates()) {
				if (accountActionDate.getPaymentStatus().equals(
						PaymentStatus.UNPAID.getValue())) {
					if (accountActionDate.getInstallmentId() > nextInstallment
							.getInstallmentId())
						futureActionDateList.add(accountActionDate);
				}
			}
		}
		return futureActionDateList;
	}

	protected List<AccountActionDateEntity> getApplicableIdsForDueInstallments() {
		List<AccountActionDateEntity> dueActionDateList = new ArrayList<AccountActionDateEntity>();
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment == null || !nextInstallment.isPaid()) {
			dueActionDateList.addAll(getDetailsOfInstallmentsInArrears());
			if (nextInstallment != null)
				dueActionDateList.add(nextInstallment);
		}
		return dueActionDateList;
	}

	public List<AccountActionDateEntity> getPastInstallments() {
		List<AccountActionDateEntity> pastActionDateList = new ArrayList<AccountActionDateEntity>();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (accountActionDateEntity.compareDate(DateUtils
					.getCurrentDateWithoutTimeStamp()) < 0) {
				pastActionDateList.add(accountActionDateEntity);
			}
		}
		return pastActionDateList;
	}

	public List<TransactionHistoryView> getTransactionHistoryView() {
		List<TransactionHistoryView> trxnHistory = new ArrayList<TransactionHistoryView>();
		for (AccountPaymentEntity accountPayment : getAccountPayments()) {
			for (AccountTrxnEntity accountTrxn : accountPayment
					.getAccountTrxns()) {
				for (FinancialTransactionBO financialTrxn : accountTrxn
						.getFinancialTransactions()) {
					TransactionHistoryView transactionHistory = new TransactionHistoryView();
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
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		return nextAccountAction != null ? nextAccountAction.getActionDate()
				: new java.sql.Date(currentDate.getTime());
	}

	public List<AccountActionDateEntity> getDetailsOfInstallmentsInArrears() {
		List<AccountActionDateEntity> installmentsInArrears = new ArrayList<AccountActionDateEntity>();
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		if (getAccountActionDates() != null
				&& getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accountAction : getAccountActionDates()) {
				if (accountAction.getActionDate().compareTo(currentDate) < 0
						&& accountAction.getPaymentStatus().equals(
								PaymentStatus.UNPAID.getValue()))
					installmentsInArrears.add(accountAction);
			}
		}
		return installmentsInArrears;
	}

	public AccountActionDateEntity getDetailsOfNextInstallment() {
		AccountActionDateEntity nextAccountAction = null;
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		if (getAccountActionDates() != null
				&& getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accountAction : getAccountActionDates()) {
				if (accountAction.getActionDate().compareTo(currentDate) >= 0)
					if (null == nextAccountAction)
						nextAccountAction = accountAction;
					else if (nextAccountAction.getInstallmentId() > accountAction
							.getInstallmentId())
						nextAccountAction = accountAction;
			}
		}
		return nextAccountAction;
	}

	public Money getTotalAmountDue() {
		Money totalAmt = getTotalAmountInArrears();
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment != null
				&& nextInstallment.getPaymentStatus().equals(
						PaymentStatus.UNPAID.getValue()))
			totalAmt = totalAmt.add(getDueAmount(nextInstallment));
		return totalAmt;
	}

	public Money getTotalPaymentDue() {
		Money totalAmt = getTotalAmountInArrears();
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment != null
				&& nextInstallment.getPaymentStatus().equals(
						PaymentStatus.UNPAID.getValue())
				&& DateUtils.getDateWithoutTimeStamp(
						nextInstallment.getActionDate().getTime()).equals(
						DateUtils.getCurrentDateWithoutTimeStamp()))
			totalAmt = totalAmt.add(getDueAmount(nextInstallment));
		return totalAmt;
	}

	public Money getTotalAmountInArrears() {
		List<AccountActionDateEntity> installmentsInArrears = getDetailsOfInstallmentsInArrears();
		Money totalAmount = new Money();
		if (installmentsInArrears != null && installmentsInArrears.size() > 0)
			for (AccountActionDateEntity accountAction : installmentsInArrears)
				totalAmount = totalAmount.add(getDueAmount(accountAction));
		return totalAmount;
	}

	public List<AccountActionDateEntity> getTotalInstallmentsDue() {
		List<AccountActionDateEntity> dueInstallments = getDetailsOfInstallmentsInArrears();
		AccountActionDateEntity nextInstallment = getDetailsOfNextInstallment();
		if (nextInstallment != null
				&& nextInstallment.getPaymentStatus().equals(
						PaymentStatus.UNPAID.getValue())
				&& DateUtils.getDateWithoutTimeStamp(
						nextInstallment.getActionDate().getTime()).equals(
						DateUtils.getCurrentDateWithoutTimeStamp()))
			dueInstallments.add(nextInstallment);
		return dueInstallments;
	}

	public boolean isTrxnDateValid(Date trxnDate) throws AccountException {
		if (Configuration.getInstance().getAccountConfig(
				getOffice().getOfficeId()).isBackDatedTxnAllowed()) {
			Date meetingDate = null;
			try {
				meetingDate = new CustomerPersistence()
						.getLastMeetingDateForCustomer(getCustomer()
								.getCustomerId());
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
			Date lastMeetingDate = null;
			if (meetingDate != null) {
				lastMeetingDate = DateUtils.getDateWithoutTimeStamp(meetingDate
						.getTime());
				return trxnDate.compareTo(lastMeetingDate) >= 0 ? true : false;
			} else
				return false;
		}

		return trxnDate.equals(DateUtils.getCurrentDateWithoutTimeStamp());
	}

	public List<AccountNotesEntity> getRecentAccountNotes() {
		List<AccountNotesEntity> notes = new ArrayList<AccountNotesEntity>();
		int count = 0;
		for (AccountNotesEntity accountNotesEntity : getAccountNotes()) {
			if (count > 2)
				break;
			notes.add(accountNotesEntity);
			count++;
		}
		return notes;
	}

	public void update() throws AccountException {
		setUpdateDetails();
		try {
			(new AccountPersistence()).createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new AccountException(e);
		}
	}

	public AccountState getState() throws AccountException {
		return AccountState.getStatus(getAccountState().getId());
	}

	public void updateAccountActivity(Money principal, Money interest,
			Money fee, Money penalty, Short personnelId, String description)
			throws AccountException {
	}

	public void waiveAmountDue(WaiveEnum waiveType) throws AccountException {
	}

	public void waiveAmountOverDue(WaiveEnum waiveType) throws AccountException {
	}

	public Boolean isCurrentDateGreaterThanFirstInstallment() {
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (DateUtils.getCurrentDateWithoutTimeStamp().compareTo(
					DateUtils.getDateWithoutTimeStamp(accountActionDateEntity
							.getActionDate().getTime())) >= 0)
				return true;
		}
		return false;
	}

	public void applyCharge(Short feeId, Double charge) throws AccountException {
	}

	public AccountTypes getType() {
		return null;
	}

	public boolean isOpen() {
		return true;
	}

	public boolean isUpcomingInstallmentUnpaid() {
		AccountActionDateEntity accountActionDateEntity = getDetailsOfUpcomigInstallment();
		if (accountActionDateEntity != null) {
			if (!accountActionDateEntity.isPaid())
				return true;
		}
		return false;
	}

	public AccountActionDateEntity getDetailsOfUpcomigInstallment() {
		AccountActionDateEntity nextAccountAction = null;
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		if (getAccountActionDates() != null
				&& getAccountActionDates().size() > 0) {
			for (AccountActionDateEntity accountAction : getAccountActionDates()) {
				if (accountAction.getActionDate().compareTo(currentDate) > 0)
					if (null == nextAccountAction)
						nextAccountAction = accountAction;
					else if (nextAccountAction.getInstallmentId() > accountAction
							.getInstallmentId())
						nextAccountAction = accountAction;
			}
		}
		return nextAccountAction;
	}

	protected final void buildFinancialEntries(
			Set<AccountTrxnEntity> accountTrxns) throws AccountException {
		try {
			FinancialBusinessService financialBusinessService = (FinancialBusinessService) ServiceFactory
					.getInstance().getBusinessService(
							BusinessServiceName.Financial);
			for (AccountTrxnEntity accountTrxn : accountTrxns) {
				financialBusinessService.buildAccountingEntries(accountTrxn);
			}
		} catch (FinancialException fe) {
			throw new AccountException("errors.update", fe);
		}
	}

	protected final String generateId(String officeGlobalNum)
			throws AccountException {
		StringBuilder systemId = new StringBuilder();
		systemId.append(officeGlobalNum);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"After appending the officeGlobalNum to loanAccountSysID  it becomes"
						+ systemId.toString());
		try {
			systemId.append(StringUtils
					.lpad(getAccountId().toString(), '0', 11));
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"After appending the running number to loanAccountSysID  it becomes"
							+ systemId.toString());
		} catch (Exception se) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).error(
					"There was some error retieving the running number", true,
					null, se);
			throw new AccountException(
					AccountExceptionConstants.IDGenerationException, se);
		}
		return systemId.toString();
	}

	protected final List<AccountActionDateEntity> getDueInstallments() {
		List<AccountActionDateEntity> dueInstallmentList = new ArrayList<AccountActionDateEntity>();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (accountActionDateEntity.getPaymentStatus().equals(
					PaymentStatus.UNPAID.getValue())) {
				if (accountActionDateEntity.compareDate(DateUtils
						.getCurrentDateWithoutTimeStamp()) > 0) {
					dueInstallmentList.add(accountActionDateEntity);
				}
			}
		}
		return dueInstallmentList;
	}

	protected final List<AccountActionDateEntity> getTotalDueInstallments() {
		List<AccountActionDateEntity> dueInstallmentList = new ArrayList<AccountActionDateEntity>();
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (accountActionDateEntity.getPaymentStatus().equals(
					PaymentStatus.UNPAID.getValue())) {
				if (accountActionDateEntity.compareDate(DateUtils
						.getCurrentDateWithoutTimeStamp()) >= 0) {
					dueInstallmentList.add(accountActionDateEntity);
				}
			}
		}
		return dueInstallmentList;
	}

	protected final Boolean isFeeAlreadyApplied(FeeBO fee) {
		return getAccountFees(fee.getFeeId()) != null;
	}

	protected final AccountFeesEntity getAccountFee(FeeBO fee, Double charge) {
		AccountFeesEntity accountFee = null;
		if (fee.isPeriodic() && isFeeAlreadyApplied(fee)) {
			accountFee = getAccountFees(fee.getFeeId());
			accountFee.setFeeAmount(charge);
			accountFee.setFeeStatus(FeeStatus.ACTIVE.getValue());
			accountFee
					.setStatusChangeDate(new Date(System.currentTimeMillis()));
		} else {
			accountFee = new AccountFeesEntity(this, fee, charge,
					FeeStatus.ACTIVE.getValue(), null, null);
		}
		return accountFee;
	}

	protected final List<InstallmentDate> getInstallmentDates(
			MeetingBO meeting, Short noOfInstallments, Short installmentToSkip)
			throws AccountException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Generating intallment dates");
		List<Date> dueDates = null;
		try {
			if (!noOfInstallments.equals(Short.valueOf("0")))
				dueDates = meeting.getAllDates(noOfInstallments
						+ installmentToSkip);
		} catch (MeetingException e) {
			throw new AccountException(e);
		}
		int installmentId = 1;
		List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
		for (Date date : dueDates)
			installmentDates.add(new InstallmentDate(new Short(Integer
					.toString(installmentId++)), date));

		removeInstallmentsNeedNotPay(installmentToSkip, installmentDates);
		return installmentDates;
	}

	protected final List<FeeInstallment> getFeeInstallment(
			List<InstallmentDate> installmentDates) throws AccountException {
		List<FeeInstallment> feeInstallmentList = new ArrayList<FeeInstallment>();
		for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
			Short accountFeeType = accountFeesEntity.getFees()
					.getFeeFrequency().getFeeFrequencyType().getId();
			if (accountFeeType.equals(FeeFrequencyType.ONETIME.getValue())) {
				feeInstallmentList.add(handleOneTime(accountFeesEntity,
						installmentDates));
			} else if (accountFeeType.equals(FeeFrequencyType.PERIODIC
					.getValue())) {
				feeInstallmentList.addAll(handlePeriodic(accountFeesEntity,
						installmentDates));
			}
		}
		return feeInstallmentList;
	}

	protected final List<Date> getFeeDates(MeetingBO feeMeetingFrequency,
			List<InstallmentDate> installmentDates) throws AccountException {
		MeetingBO repaymentFrequency = getCustomer().getCustomerMeeting()
				.getMeeting();
		Short recurAfter = repaymentFrequency.getMeetingDetails()
				.getRecurAfter();
		Calendar meetingStartDate = repaymentFrequency.getMeetingStartDate();
		repaymentFrequency.getMeetingDetails().setRecurAfter(
				feeMeetingFrequency.getMeetingDetails().getRecurAfter());
		Calendar feeStartDate = new GregorianCalendar();
		feeStartDate.setTime((installmentDates.get(0)).getInstallmentDueDate());
		repaymentFrequency.setMeetingStartDate(feeStartDate);
		Date repaymentEndDate = (installmentDates
				.get(installmentDates.size() - 1)).getInstallmentDueDate();

		List<Date> feeDueDates = null;
		try {
			feeDueDates = repaymentFrequency.getAllDates(repaymentEndDate);
		} catch (MeetingException e) {
			throw new AccountException(e);
		}
		repaymentFrequency.setMeetingStartDate(meetingStartDate);
		repaymentFrequency.getMeetingDetails().setRecurAfter(recurAfter);
		return feeDueDates;
	}

	protected final FeeInstallment buildFeeInstallment(Short installmentId,
			Money accountFeeAmount, AccountFeesEntity accountFee) {
		FeeInstallment feeInstallment = new FeeInstallment();
		feeInstallment.setInstallmentId(installmentId);
		feeInstallment.setAccountFee(accountFeeAmount);
		feeInstallment.setAccountFeesEntity(accountFee);
		accountFee.setAccountFeeAmount(accountFeeAmount);
		return feeInstallment;
	}

	protected final Short getMatchingInstallmentId(
			List<InstallmentDate> installmentDates, Date feeDate) {
		for (InstallmentDate installmentDate : installmentDates) {
			if (DateUtils
					.getDateWithoutTimeStamp(
							installmentDate.getInstallmentDueDate().getTime())
					.compareTo(
							DateUtils
									.getDateWithoutTimeStamp(feeDate.getTime())) >= 0)
				return installmentDate.getInstallmentId();
		}
		return null;
	}

	protected final List<FeeInstallment> mergeFeeInstallments(
			List<FeeInstallment> feeInstallmentList) {
		List<FeeInstallment> newFeeInstallmentList = new ArrayList<FeeInstallment>();
		for (Iterator<FeeInstallment> iterator = feeInstallmentList.iterator(); iterator
				.hasNext();) {
			FeeInstallment feeInstallment = iterator.next();
			iterator.remove();
			FeeInstallment feeInstTemp = null;
			for (FeeInstallment feeInst : newFeeInstallmentList) {
				if (feeInst.getInstallmentId().equals(
						feeInstallment.getInstallmentId())
						&& feeInst.getAccountFeesEntity().equals(
								feeInstallment.getAccountFeesEntity())) {
					feeInstTemp = feeInst;
					break;
				}
			}
			if (feeInstTemp != null) {
				newFeeInstallmentList.remove(feeInstTemp);
				feeInstTemp.setAccountFee(feeInstTemp.getAccountFee().add(
						feeInstallment.getAccountFee()));
				newFeeInstallmentList.add(feeInstTemp);
			} else {
				newFeeInstallmentList.add(feeInstallment);
			}
		}
		return newFeeInstallmentList;
	}

	protected boolean isCurrentDateEquallToInstallmentDate() {
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (accountActionDateEntity.getPaymentStatus().equals(
					PaymentStatus.UNPAID.getValue())) {
				if (accountActionDateEntity.compareDate(DateUtils
						.getCurrentDateWithoutTimeStamp()) == 0) {
					return true;
				}
			}
		}
		return false;
	}

	protected List<AccountFeesEntity> getPeriodicFeeList() {
		List<AccountFeesEntity> periodicFeeList = new ArrayList<AccountFeesEntity>();
		for (AccountFeesEntity accountFee : getAccountFees()) {
			if (accountFee.getFees().isPeriodic()) {
				new FeePersistence().getFee(accountFee.getFees().getFeeId());
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
				(new AccountPersistence()).delete(accountActionDateEntity);
			} catch (PersistenceException e) {
				throw new AccountException(e);
			}
		}
	}

	public Short getLastInstallmentId() {
		Short LastInstallmentId = null;
		for (AccountActionDateEntity date : this.getAccountActionDates()) {

			if (LastInstallmentId == null)
				LastInstallmentId = date.getInstallmentId();
			else {
				if (LastInstallmentId < date.getInstallmentId())
					LastInstallmentId = date.getInstallmentId();
			}
		}
		return LastInstallmentId;

	}

	protected List<AccountTrxnEntity> getAccountTrxnsOrderByTrxnDate() {
		List<AccountTrxnEntity> accountTrxnList = new ArrayList<AccountTrxnEntity>();
		for (AccountPaymentEntity payment : getAccountPayments()) {
			accountTrxnList.addAll(payment.getAccountTrxns());
		}

		Collections.sort(accountTrxnList, new Comparator<AccountTrxnEntity>() {
			public int compare(AccountTrxnEntity trx1, AccountTrxnEntity trx2) {
				if (trx1.getActionDate().equals(trx2.getActionDate()))
					return trx1.getAccountTrxnId().compareTo(
							trx2.getAccountTrxnId());
				else
					return trx1.getActionDate().compareTo(trx2.getActionDate());
			}
		});
		return accountTrxnList;
	}

	protected void resetAccountActionDates() {
		this.accountActionDates.clear();
	}

	protected void updatePerformanceHistoryOnAdjustment(Integer noOfTrxnReversed) {
	}

	protected void updateInstallmentAfterAdjustment(
			List<AccountTrxnEntity> reversedTrxns) throws AccountException {
	}

	protected Money getDueAmount(AccountActionDateEntity installment) {
		return null;
	}

	protected void regenerateFutureInstallments(Short nextIntallmentId)
			throws AccountException {
	}

	protected List<InstallmentDate> getInstallmentDates(MeetingBO Meeting,
			Integer installmentSkipToStartRepayment) throws AccountException {
		return null;
	}

	protected List<FeeInstallment> handlePeriodic(
			AccountFeesEntity accountFees,
			List<InstallmentDate> installmentDates) throws AccountException {
		return null;
	}

	protected AccountPaymentEntity makePayment(PaymentData accountPaymentData)
			throws AccountException {
		return null;
	}

	protected void updateTotalFeeAmount(Money totalFeeAmount) {
	}

	protected void updateTotalPenaltyAmount(Money totalPenaltyAmount) {
	}

	protected Money updateAccountActionDateEntity(List<Short> intallmentIdList,
			Short feeId) {
		return new Money();
	}

	protected boolean isAdjustPossibleOnLastTrxn() {
		return false;
	}

	public void removeFees(Short feeId, Short personnelId)
			throws AccountException {
	}

	protected void activationDateHelper(Short newStatusId)
			throws AccountException {
	}

	protected List<Short> getApplicableInstallmentIdsForRemoveFees() {
		List<Short> installmentIdList = new ArrayList<Short>();
		for (AccountActionDateEntity accountActionDateEntity : getApplicableIdsForFutureInstallments()) {
			installmentIdList.add(accountActionDateEntity.getInstallmentId());
		}
		if (getDetailsOfNextInstallment() != null) {
			installmentIdList.add(getDetailsOfNextInstallment()
					.getInstallmentId());
		}
		return installmentIdList;
	}

	private void setFinancialEntries(FinancialTransactionBO financialTrxn,
			TransactionHistoryView transactionHistory) {
		String debit = "-";
		String credit = "-";
		String notes = "-";
		if (financialTrxn.isDebitEntry()) {
			debit = String.valueOf(removeSign(financialTrxn.getPostedAmount()));
		} else if (financialTrxn.isCreditEntry()) {
			credit = String
					.valueOf(removeSign(financialTrxn.getPostedAmount()));
		}
		if (financialTrxn.getNotes() != null
				&& !financialTrxn.getNotes().equals(""))
			notes = financialTrxn.getNotes();
		if (financialTrxn.getAccountTrxn().getAccountActionEntity().getId()
				.equals(
						AccountActionTypes.CUSTOMER_ACCOUNT_REPAYMENT
								.getValue()) || 
					financialTrxn.getAccountTrxn().getAccountActionEntity().getId()
								.equals(
										AccountActionTypes.LOAN_REPAYMENT
												.getValue()))
			notes = "-";
		transactionHistory.setFinancialEnteries(financialTrxn.getTrxnId(),
				financialTrxn.getActionDate(), financialTrxn
						.getFinancialAction()
						.getName(userContext.getLocaleId()), financialTrxn
						.getGlcode().getGlcode(), debit, credit, financialTrxn
						.getPostedDate(), notes);

	}

	private void setAccountingEntries(AccountTrxnEntity accountTrxn,
			TransactionHistoryView transactionHistory) {

		transactionHistory.setAccountingEnteries(accountTrxn
				.getAccountPayment().getPaymentId(), String
				.valueOf(removeSign(accountTrxn.getAmount())), accountTrxn
				.getCustomer().getDisplayName(), getUserContext().getName());
	}

	private AccountCustomFieldEntity getAccountCustomField(Short fieldId) {
		if (null != this.accountCustomFields
				&& this.accountCustomFields.size() > 0) {
			for (AccountCustomFieldEntity obj : this.accountCustomFields) {
				if (obj.getFieldId().equals(fieldId))
					return obj;
			}
		}
		return null;
	}

	protected final FeeInstallment handleOneTime(AccountFeesEntity accountFee,
			List<InstallmentDate> installmentDates) {
		Money accountFeeAmount = accountFee.getAccountFeeAmount();
		Date feeDate = installmentDates.get(0).getInstallmentDueDate();
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"Handling OneTime fee" + feeDate);
		Short installmentId = getMatchingInstallmentId(installmentDates,
				feeDate);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"OneTime fee applicable installment id " + installmentId);
		return buildFeeInstallment(installmentId, accountFeeAmount, accountFee);
	}

	private void removeInstallmentsNeedNotPay(
			Short installmentSkipToStartRepayment,
			List<InstallmentDate> installmentDates) {
		int removeCounter = 0;
		for (int i = 0; i < installmentSkipToStartRepayment; i++)
			installmentDates.remove(removeCounter);
		// re-adjust the installment ids
		if (installmentSkipToStartRepayment > 0) {
			int count = installmentDates.size();
			for (int i = 0; i < count; i++) {
				InstallmentDate instDate = installmentDates.get(i);
				instDate.setInstallmentId(new Short(Integer.toString(i + 1)));
			}
		}
	}

	private void validate(UserContext userContext, CustomerBO customer,
			AccountTypes accountType, AccountState accountState)
			throws AccountException {
		if (userContext == null || customer == null || accountType == null
				|| accountState == null)
			throw new AccountException(
					AccountExceptionConstants.CREATEEXCEPTION);
	}

	private void setFlag(AccountStateFlagEntity accountStateFlagEntity) {
		accountStateFlagEntity.setLocaleId(this.getUserContext().getLocaleId());
		Iterator iter = this.getAccountFlags().iterator();
		while (iter.hasNext()) {
			AccountFlagMapping currentFlag = (AccountFlagMapping) iter.next();
			if (!currentFlag.getFlag().isFlagRetained())
				iter.remove();
		}
		this.addAccountFlag(accountStateFlagEntity);
	}

}
