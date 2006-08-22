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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.accounts.exceptions.IDGenerationException;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.FeeInstallment;
import org.mifos.application.accounts.util.helpers.InstallmentDate;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.accounts.util.valueobjects.AccountFees;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.persistence.FeePersistence;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.fees.util.valueobjects.Fees;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.master.util.valueobjects.AccountType;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.valueobjects.Meeting;
import org.mifos.application.meeting.util.valueobjects.MeetingDetails;
import org.mifos.application.meeting.util.valueobjects.MeetingRecurrence;
import org.mifos.application.meeting.util.valueobjects.MeetingType;
import org.mifos.application.meeting.util.valueobjects.RecurrenceType;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.persistence.service.PersonnelPersistenceService;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.repaymentschedule.MeetingScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentSchedule;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleFactory;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleHelper;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleIfc;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleInputsIfc;
import org.mifos.framework.components.scheduler.SchedulerException;
import org.mifos.framework.components.scheduler.SchedulerIntf;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;

public class AccountBO extends BusinessObject {
	
	private  Integer accountId;

	protected  String globalAccountNum;

	private Date closedDate;

	protected  CustomerBO customer;

	private AccountStateEntity accountState;

	private Set<AccountFlagMapping> accountFlags;

	protected  AccountType accountType;

	protected  OfficeBO office;

	protected  PersonnelBO personnel;

	private Set<AccountFeesEntity> accountFees;

	private Set<AccountActionDateEntity> accountActionDates;

	private Set<AccountPaymentEntity> accountPayments;

	private Set<AccountCustomFieldEntity> accountCustomFields;

	public Set<AccountNotesEntity> accountNotes;

	public Set<AccountStatusChangeHistoryEntity> accountStatusChangeHistory;
	
	protected AccountBO() {
		this(null);
	}
	
	protected AccountBO(UserContext userContext) {
		super(userContext);
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
			throws AccountException{
		super(userContext);
		validate(userContext, customer, accountType,accountState);
		try{
			accountFees = new HashSet<AccountFeesEntity>();
			accountPayments = new HashSet<AccountPaymentEntity>();
			accountActionDates = new HashSet<AccountActionDateEntity>();
			accountCustomFields = new HashSet<AccountCustomFieldEntity>();
			accountNotes = new HashSet<AccountNotesEntity>();
			accountStatusChangeHistory = new HashSet<AccountStatusChangeHistoryEntity>();
			accountFlags = new HashSet<AccountFlagMapping>();
			this.accountId = null;
			this.globalAccountNum = generateId(userContext.getBranchGlobalNum());
			this.customer = customer;
			this.accountType = new AccountType(accountType.getValue());
			this.office = customer.getOffice();
			this.personnel = customer.getPersonnel();
			this.setAccountState(new AccountStateEntity(accountState));
			setCreateDetails();
		}catch(IDGenerationException idge){
			throw new AccountException(idge);
		}
	}
	
	public Integer getAccountId() {
		return accountId;
	}

	public Set<AccountActionDateEntity> getAccountActionDates() {
		return accountActionDates;
	}

	public Set<AccountNotesEntity> getAccountNotes() {
		return accountNotes;
	}

	private void setAccountNotes(Set<AccountNotesEntity> accountNotes) {
		this.accountNotes = accountNotes;
	}

	private void setAccountActionDates(
			Set<AccountActionDateEntity> accountActionDates) {
		this.accountActionDates = accountActionDates;
	}

	public Set<AccountFeesEntity> getAccountFees() {
		return accountFees;
	}

	private void setAccountFees(Set<AccountFeesEntity> accountFees) {
		this.accountFees = accountFees;
	}

	public Set<AccountPaymentEntity> getAccountPayments() {
		return accountPayments;
	}

	public void setAccountPayments(Set<AccountPaymentEntity> accountPayments) {
		this.accountPayments = accountPayments;
	}

	public AccountStateEntity getAccountState() {
		return accountState;
	}

	public void setAccountState(AccountStateEntity accountState) {
		this.accountState = accountState;
	}

	public AccountType getAccountType() {
		return accountType;
	}

	public String getGlobalAccountNum() {
		return globalAccountNum;
	}

	public Date getClosedDate() {
		return closedDate;
	}

	public void setClosedDate(Date closedDate) {
		this.closedDate = closedDate;
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

	public Set<AccountStatusChangeHistoryEntity> getAccountStatusChangeHistory() {
		return accountStatusChangeHistory;
	}

	private void setAccountStatusChangeHistory(
			Set<AccountStatusChangeHistoryEntity> accountStatusChangeHistory) {
		this.accountStatusChangeHistory = accountStatusChangeHistory;
	}

	public void addAccountStatusChangeHistory(
			AccountStatusChangeHistoryEntity accountStatusChangeHistoryEntity) {
		accountStatusChangeHistoryEntity.setAccount(this);
		this.accountStatusChangeHistory.add(accountStatusChangeHistoryEntity);
	}

	public Set<AccountCustomFieldEntity> getAccountCustomFields() {
		return accountCustomFields;
	}

	/*
	 * public setter is needed at the time of action form to business object
	 * conversion.
	 */
	public void setAccountCustomFields(
			Set<AccountCustomFieldEntity> accountCustomFields) {
		this.accountCustomFields = accountCustomFields;
	}

	public void setAccountCustomFieldSet(
			Set<AccountCustomFieldEntity> accountCustomFields) {
		if (accountCustomFields != null) {
			for (AccountCustomFieldEntity customField : accountCustomFields) {
				this.addAccountCustomField(customField);
			}
		}
	}

	private void addAccountCustomField(AccountCustomFieldEntity customField) {
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

	public void resetAccountActionDates() {
		this.accountActionDates.clear();
	}

	public void addAccountFees(AccountFeesEntity fees) {
		fees.setAccount(this);
		accountFees.add(fees);
		setAccountFees(accountFees);
	}

	public void addAccountActionDate(AccountActionDateEntity accountAction) {
		if (accountAction == null) {
			// TODO generate a new InvalidStateException
			throw new NullPointerException();
		}
		this.getAccountActionDates().add(accountAction);
	}

	public void addAccountPayment(AccountPaymentEntity payment) {
		if (accountPayments == null)
			accountPayments = new HashSet<AccountPaymentEntity>();
		accountPayments.add(payment);
	}

	public void addAccountNotes(AccountNotesEntity notes) {
		notes.setAccount(this);
		accountNotes.add(notes);
	}

	public Set<AccountFlagMapping> getAccountFlags() {
		return accountFlags;
	}

	private void setAccountFlags(Set<AccountFlagMapping> accountFlags) {
		this.accountFlags = accountFlags;
	}

	public void addAccountFlag(AccountStateFlagEntity flagDetail) {
		AccountFlagMapping flagMap = new AccountFlagMapping();
		flagMap.setCreatedBy(this.getUserContext().getId());
		flagMap.setCreatedDate(new Date());
		flagMap.setFlag(flagDetail);
		this.accountFlags.add(flagMap);
	}

	public void applyPayment(PaymentData paymentData) throws AccountException{
		AccountPaymentEntity accountPayment = makePayment(paymentData);
		addAccountPayment(accountPayment);
		buildFinancialEntries(accountPayment.getAccountTrxns());
		(new AccountPersistence()).createOrUpdate(this);
	}

	protected AccountPaymentEntity makePayment(PaymentData accountPaymentData)
			throws AccountException{
		return null;
	}

	protected void updateTotalFeeAmount(Money totalFeeAmount) {
	}

	public void updateTotalPenaltyAmount(Money totalPenaltyAmount) {
	}

	public Money updateAccountActionDateEntity(List<Short> intallmentIdList,
			Short feeId) {
		return new Money();
	}

	public void updateAccountFeesEntity(Short feeId) {
		Set<AccountFeesEntity> accountFeesEntitySet = this.getAccountFees();
		for (AccountFeesEntity accountFeesEntity : accountFeesEntitySet) {
			if (accountFeesEntity.getFees().getFeeId().equals(feeId)) {
				accountFeesEntity.changeFeesStatus(
						AccountConstants.INACTIVE_FEES, new Date(System
								.currentTimeMillis()));
				accountFeesEntity.setLastAppliedDate(null);
			}
		}
	}

	public FeeBO getAccountFeesObject(Short feeId) {
		Set<AccountFeesEntity> accountFeesEntitySet = this.getAccountFees();
		for (AccountFeesEntity accountFeesEntity : accountFeesEntitySet) {
			if (accountFeesEntity.getFees().getFeeId().equals(feeId)) {
				return accountFeesEntity.getFees();
			}
		}
		return null;
	}

	public AccountFeesEntity getAccountFees(Short feeId) {
		Set<AccountFeesEntity> accountFeesEntitySet = this.getAccountFees();
		for (AccountFeesEntity accountFeesEntity : accountFeesEntitySet) {
			if (accountFeesEntity.getFees().getFeeId().equals(feeId)) {
				return accountFeesEntity;
			}
		}
		return null;
	}

	public Boolean isFeeActive(Short feeId) {
		Set<AccountFeesEntity> accountFeesEntitySet = this.getAccountFees();
		for (AccountFeesEntity accountFeesEntity : accountFeesEntitySet) {
			if (accountFeesEntity.getFees().getFeeId().equals(feeId)) {
				if (accountFeesEntity.getFeeStatus() == null
						|| accountFeesEntity.getFeeStatus().equals(
								AccountConstants.ACTIVE_FEES)) {
					return true;
				}
			}
		}
		return false;
	}

	protected final void buildFinancialEntries(
			Set<AccountTrxnEntity> accountTrxns) throws AccountException{
		try {
			FinancialBusinessService financialBusinessService = (FinancialBusinessService) ServiceFactory
					.getInstance().getBusinessService(
							BusinessServiceName.Financial);
			for (AccountTrxnEntity accountTrxn : accountTrxns) {
				financialBusinessService.buildAccountingEntries(accountTrxn);
			}
		} catch (ServiceException se) {
			throw new AccountException("errors.update",se);
		} catch (FinancialException fe) {
			throw new AccountException("errors.update",fe);
		}
	}

	protected String generateId(String officeGlobalNum)
			throws  IDGenerationException {
		StringBuilder systemId = new StringBuilder();
		systemId.append(officeGlobalNum);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"After appending the officeGlobalNum to loanAccountSysID  it becomes"
						+ systemId.toString());
		// setting the 11 digits of account running number.
		try {
			systemId.append(StringUtils.lpad((new AccountPersistence())
					.getAccountRunningNumber().toString(), '0', 11));
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"After appending the running number to loanAccountSysID  it becomes"
							+ systemId.toString());
		} catch (PersistenceException se) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).error(
					"There was some error retieving the running number", true,
					null, se);
			throw new IDGenerationException(
					AccountExceptionConstants.IDGenerationException, se);
		}
		return systemId.toString();
	}

	public double getLastPmntAmnt() {
		if (null != accountPayments && accountPayments.size() > 0) {
			return getLastPmnt().getAmount().getAmountDoubleValue();
		}
		return 0;
	}

	/**
	 * If there are no accountPayments associated then this method will throw a
	 * NullPointerException.
	 */
	public AccountPaymentEntity getLastPmnt() {
		AccountPaymentEntity accntPmnt = null;
		for (AccountPaymentEntity accntPayment : accountPayments) {

			accntPmnt = accntPayment;
			break;
		}
		return accntPmnt;
	}

	/**
	 * This is just a dummy implementation, actual implementation should be with
	 * LoanBO or SavingsBO.
	 */
	public boolean isAdjustPossibleOnLastTrxn() {
		return false;
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

	public void removeFees(Short feeId, Short personnelId)
			throws AccountException {
		List<Short> installmentIdList = getApplicableInstallmentIdsForRemoveFees();
		Money totalFeeAmount = new Money();
		if (installmentIdList != null && installmentIdList.size() != 0
				&& isFeeActive(feeId)) {
			totalFeeAmount = updateAccountActionDateEntity(installmentIdList,
					feeId);
			updateAccountFeesEntity(feeId);
			updateTotalFeeAmount(totalFeeAmount);
			FeeBO feesBO = getAccountFeesObject(feeId);
			String description = feesBO.getFeeName() + " "
					+ AccountConstants.FEES_REMOVED;
			updateAccountActivity(totalFeeAmount, personnelId, description);
			roundInstallments(installmentIdList);
		}

	}
	
	private List<Short> getApplicableInstallmentIdsForRemoveFees() {
		List<Short> installmentIdList = new ArrayList<Short>();
		for(AccountActionDateEntity accountActionDateEntity : getApplicableIdsForFutureInstallments()){
			installmentIdList.add(accountActionDateEntity.getInstallmentId());
		}
		if(getDetailsOfNextInstallment() != null) {
			installmentIdList.add(getDetailsOfNextInstallment().getInstallmentId());
		}	
		return installmentIdList;
	}

	public void roundInstallments(List<Short> installmentIdList) {
	}

	public void updateAccountActivity(Money totalFeeAmount, Short personnelId,
			String description) {
	}

	public void adjustPmnt(String adjustmentComment) throws AccountException {
		if (isAdjustPossibleOnLastTrxn()) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"Adjustment is possible hence attempting to adjust.");
			List<AccountTrxnEntity> reversedTrxns = getLastPmnt()
					.reversalAdjustment(adjustmentComment);
			updateInstallmentAfterAdjustment(reversedTrxns);
			buildFinancialEntries(new HashSet(reversedTrxns));
			updatePerformanceHistoryOnAdjustment(reversedTrxns.size());
			(new AccountPersistence()).createOrUpdate(this);
		} else
			throw new AccountException(AccountExceptionConstants.CANNOTADJUST);
	}

	protected void updatePerformanceHistoryOnAdjustment(Integer noOfTrxnReversed) {
	}

	protected void updateInstallmentAfterAdjustment(
			List<AccountTrxnEntity> reversedTrxns) {
	}

	protected List<AccountActionDateEntity> getApplicableIdsForDueInstallments() {
		List<AccountActionDateEntity> dueActionDateList = new ArrayList<AccountActionDateEntity>();
		if (isCurrentDateEquallToInstallmentDate()) {
			for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
				if (accountActionDateEntity.getPaymentStatus().equals(
						PaymentStatus.UNPAID.getValue())) {
					if (accountActionDateEntity.compareDate(DateUtils
							.getCurrentDateWithoutTimeStamp()) <= 0) {
						dueActionDateList.add(accountActionDateEntity);
					}
				}
			}
		} else {
			Boolean flag = true;
			for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
				if (accountActionDateEntity.getPaymentStatus().equals(
						PaymentStatus.UNPAID.getValue())) {
					if (accountActionDateEntity.compareDate(DateUtils
							.getCurrentDateWithoutTimeStamp()) < 0) {
						dueActionDateList.add(accountActionDateEntity);
					} else if (flag == true
							&& accountActionDateEntity
									.getActionDate()
									.compareTo(
											DateUtils
													.getCurrentDateWithoutTimeStamp()) > 0) {
						dueActionDateList.add(accountActionDateEntity);
						flag = false;
					}
				}
			}
		}
		return dueActionDateList;
	}

	public List<AccountActionDateEntity> getApplicableIdsForFutureInstallments() {
		List<AccountActionDateEntity> futureActionDateList = new ArrayList<AccountActionDateEntity>();
		AccountActionDateEntity accountActionDate = null;
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (accountActionDateEntity.getPaymentStatus().equals(
					PaymentStatus.UNPAID.getValue())) {
				if (accountActionDateEntity.compareDate(DateUtils
						.getCurrentDateWithoutTimeStamp()) >= 0) {
					if (accountActionDate == null) {
						accountActionDate = accountActionDateEntity;
					} else if (!accountActionDate.getInstallmentId().equals(
							(accountActionDateEntity.getInstallmentId())))
						futureActionDateList.add(accountActionDateEntity);
				}
			}
		}
		return futureActionDateList;
	}
	
	protected final List<AccountActionDateEntity> getDueInstallments() {
		List<AccountActionDateEntity> dueInstallmentList = new ArrayList<AccountActionDateEntity>();
		AccountActionDateEntity accountActionDate = null;
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
	
	protected final List<Short> getIdList(List<AccountActionDateEntity> dueInstallments){
		List<Short> ids=new ArrayList<Short>();
		for(AccountActionDateEntity accountActionDateEntity : dueInstallments)
			ids.add(accountActionDateEntity.getInstallmentId());
		return ids;
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

	public Short getEntityID() {

		return null;
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

	public Money removeSign(Money amount) {
		if (amount != null && amount.getAmountDoubleValue() < 0)
			return amount.negate();
		else
			return amount;
	}

	private void setFinancialEntries(FinancialTransactionBO financialTrxn,
			TransactionHistoryView transactionHistory) {
		String debit = "-", credit = "-", notes = "-";
		if (financialTrxn.isDebitEntry()) {
			debit = String.valueOf(removeSign(financialTrxn.getPostedAmount()));
		} else if (financialTrxn.isCreditEntry()) {
			credit = String
					.valueOf(removeSign(financialTrxn.getPostedAmount()));
		}
		if (financialTrxn.getNotes() != null
				&& !financialTrxn.getNotes().equals(""))
			notes = financialTrxn.getNotes();
		transactionHistory.setFinancialEnteries(financialTrxn.getActionDate(),
				financialTrxn.getFinancialAction().getName(
						userContext.getLocaleId()), financialTrxn.getGlcode()
						.getGlcode(), debit, credit, financialTrxn
						.getPostedDate(), notes);

	}

	private void setAccountingEntries(AccountTrxnEntity accountTrxn,
			TransactionHistoryView transactionHistory) {

		transactionHistory.setAccountingEnteries(accountTrxn
				.getAccountPayment().getPaymentId(), accountTrxn
				.getAccountTrxnId(), String.valueOf(removeSign(accountTrxn
				.getAmount())), accountTrxn.getCustomer().getDisplayName(),
				accountTrxn.getCustomer().getPersonnel().getDisplayName());
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

	public void waiveAmountDue(WaiveEnum waiveType) throws AccountException {
	}

	public void waiveAmountOverDue(WaiveEnum waiveType)
			throws AccountException {
	}

	public Date getNextMeetingDate() {
		AccountActionDateEntity nextAccountAction = getDetailsOfNextInstallment();
		Date currentDate = DateUtils.getCurrentDateWithoutTimeStamp();
		return nextAccountAction != null ? nextAccountAction.getActionDate()
				: currentDate;
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

	public AccountFeesEntity getPeriodicAccountFees(Short feeId) {
		for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
			if (feeId.equals(accountFeesEntity.getFees().getFeeId())) {
				return accountFeesEntity;
			}
		}
		return null;
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

	protected Money getDueAmount(AccountActionDateEntity installment) {
		return null;
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
		try{
			if (Configuration.getInstance().getAccountConfig(
					getOffice().getOfficeId()).isBackDatedTxnAllowed()) {
				Date meetingDate = new CustomerPersistence()
						.getLastMeetingDateForCustomer(
								getCustomer().getCustomerId());
				Date lastMeetingDate = null;
				if (meetingDate != null) {
					lastMeetingDate = DateUtils.getDateWithoutTimeStamp(meetingDate
							.getTime());
					return trxnDate.compareTo(lastMeetingDate) >= 0 ? true : false;
				} else
					return false;
	
			}
		}catch(ApplicationException ae){
			throw new AccountException(ae);
		}
		return trxnDate.equals(DateUtils.getCurrentDateWithoutTimeStamp());
	}


	public void handleChangeInMeetingSchedule() throws AccountException {
		AccountActionDateEntity accountActionDateEntity = getDetailsOfNextInstallment();
		if (accountActionDateEntity != null) {
			MeetingBO meeting = getCustomer().getCustomerMeeting().getMeeting();
			Calendar meetingStartDate = meeting.getMeetingStartDate();
			meeting.setMeetingStartDate(DateUtils
					.getCalendarDate(accountActionDateEntity.getActionDate()
							.getTime()));
			regenerateFutureInstallments((short) (accountActionDateEntity
					.getInstallmentId().intValue() + 1));
			meeting.setMeetingStartDate(meetingStartDate);
			(new AccountPersistence()).createOrUpdate(this);
		}
	}

	protected void regenerateFutureInstallments(Short nextIntallmentId)
			throws AccountException {
	}

	protected void deleteFutureInstallments() {
		List<AccountActionDateEntity> futureInstllments = getApplicableIdsForFutureInstallments();
		for (AccountActionDateEntity accountActionDateEntity : futureInstllments) {
			accountActionDates.remove(accountActionDateEntity);
			(new AccountPersistence()).delete(accountActionDateEntity);
		}
	}

	public Money getTotalPrincipalAmountInArrears() {
		Money amount = new Money();
		List<AccountActionDateEntity> actionDateList = getDetailsOfInstallmentsInArrears();
		for (AccountActionDateEntity accountActionDateEntity : actionDateList) {
			amount = amount.add(((LoanScheduleEntity)accountActionDateEntity).getPrincipal());
		}
		return amount;
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

	public void update() {
		this.setUpdatedBy(userContext.getId());
		this.setUpdatedDate(new Date());
		(new AccountPersistence()).createOrUpdate(this);
	}
	
	protected Meeting convertM2StyleToM1(MeetingBO meeting) {

		Meeting meetingM1 = null;
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			meetingM1 = (Meeting) session.get(Meeting.class, meeting
					.getMeetingId());
		} catch (HibernateProcessException e) {
			e.printStackTrace();
		} finally {
			try {
				HibernateUtil.closeSession(session);
			} catch (HibernateProcessException e) {
				e.printStackTrace();
			}
		}
		return meetingM1;
	}
	
	// TODO this method will go once scheduler is moved to m2 style
	protected AccountFees getAccountFees(Integer accountFeeId) {
		AccountFees accountFees = new AccountFees();
		Session session = null;
		try {
			session = HibernateUtil.getSession();
			accountFees = (AccountFees) session.get(AccountFees.class,
					accountFeeId);
			Fees fees = accountFees.getFees();
			initializeMeetings(fees);
			if (null != fees) {
				fees.getFeeFrequency().getFeeFrequencyId();
			}
		} catch (HibernateProcessException e) {
			e.printStackTrace();
		} finally {
			try {
				HibernateUtil.closeSession(session);
			} catch (HibernateProcessException e) {
				e.printStackTrace();
			}
		}
		Hibernate.initialize(accountFees);
		return accountFees;
	}
	


	private void initializeMeetings(Fees fees) {

		if (fees.getFeeFrequency().getFeeFrequencyTypeId().equals(
				FeeFrequencyType.PERIODIC.getValue())) {
			Meeting meeting = fees.getFeeFrequency().getFeeMeetingFrequency();
			meeting.getMeetingType().getMeetingPurpose();
		}

	}
	
	protected  Short getLastInstallmentId(){
		
		Short LastInstallmentId = null;
		for (AccountActionDateEntity date : this.getAccountActionDates()) {
			
			if( LastInstallmentId ==null) LastInstallmentId = date.getInstallmentId();
			else {
				if ( LastInstallmentId < date.getInstallmentId()) LastInstallmentId= date.getInstallmentId();
			}
			
		}
		return LastInstallmentId;
		
	}
	
	public void changeStatus(Short newStatusId, Short flagId, String comment) throws AccountException {
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"In the change status method of AccountBO:: new StatusId= "	+ newStatusId);
		if (null != getCustomer().getPersonnel().getPersonnelId())
			checkPermissionForStatusChange(newStatusId, this.getUserContext(),
					flagId, getOffice().getOfficeId(), getCustomer()
							.getPersonnel().getPersonnelId());
		else
			checkPermissionForStatusChange(newStatusId, this.getUserContext(),
					flagId, getOffice().getOfficeId(), this.getUserContext()
							.getId());
		MasterPersistence masterPersistence = new MasterPersistence();
		AccountStateEntity accountStateEntity = (AccountStateEntity) masterPersistence
				.findById(AccountStateEntity.class, newStatusId);
		//checkStatusChangeAllowed(accountStateEntity);
		accountStateEntity.setLocaleId(this.getUserContext().getLocaleId());
		AccountStateFlagEntity accountStateFlagEntity = null;
		if (flagId != null) {
			accountStateFlagEntity = (AccountStateFlagEntity) masterPersistence
					.findById(AccountStateFlagEntity.class, flagId);
		}
		PersonnelBO personnel = new PersonnelPersistenceService()
				.getPersonnel(getUserContext().getId());
		AccountStatusChangeHistoryEntity historyEntity = new AccountStatusChangeHistoryEntity(
				this.getAccountState(), accountStateEntity, personnel);
		AccountNotesEntity accountNotesEntity = createAccountNotes(comment,personnel);
		this.addAccountStatusChangeHistory(historyEntity);
		this.setAccountState(accountStateEntity);
		this.addAccountNotes(accountNotesEntity);
		if (accountStateFlagEntity != null) {
			accountStateFlagEntity.setLocaleId(this.getUserContext()
					.getLocaleId());
			this.addAccountFlag(accountStateFlagEntity);
		}
		if(newStatusId.equals(AccountState.LOANACC_CANCEL.getValue()) || newStatusId.equals(AccountState.LOANACC_OBLIGATIONSMET.getValue()) 
				|| newStatusId.equals(AccountState.LOANACC_RESCHEDULED.getValue()) || newStatusId.equals(AccountState.LOANACC_WRITTENOFF.getValue()))
			this.setClosedDate(new Date(System.currentTimeMillis()));
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Coming out successfully from the change status method of AccountBO");
	}
	
	private void checkPermissionForStatusChange(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) {
		if (!isPermissionAllowed(newState, userContext, flagSelected,
				recordOfficeId, recordLoanOfficerId))
			throw new SecurityException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	private boolean isPermissionAllowed(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isStateChangePermittedForAccount(
				newState.shortValue(),
				null != flagSelected ? flagSelected.shortValue() : 0,
				userContext, recordOfficeId, recordLoanOfficerId);
	}
	
	private AccountNotesEntity createAccountNotes(String comment,PersonnelBO personnel){
		AccountNotesEntity accountNotes = new AccountNotesEntity();
		accountNotes.setCommentDate(new java.sql.Date(System
				.currentTimeMillis()));
		accountNotes.setPersonnel(personnel);
		accountNotes.setComment(comment);
		return accountNotes;
	}
	
	public void initializeStateMachine(Short localeId) throws AccountException{
	}
	
	public List<AccountStateEntity> getStatusList() {
		return null;
	}
	
	public String getStatusName(Short localeId, Short accountStateId) throws AccountException{
		return null;
	}

	public String getFlagName(Short flagId) throws AccountException{
		return null;
	}
	
	private void validate(UserContext userContext, CustomerBO customer,
			AccountTypes accountType, AccountState accountState) throws AccountException {
		if(userContext == null || customer==null || accountType==null || accountState==null)
			throw new AccountException(AccountExceptionConstants.CREATEEXCEPTION);
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
	
	public void applyCharge(Short feeId,Money charge) throws AccountException{}
	
	protected final Map<Short, Money> getFeeInstallmentMap(
			AccountFeesEntity accountFee, Date feeStartDate)
			throws AccountException {
		Set<AccountFeesEntity> accountFeeSet = new HashSet<AccountFeesEntity>();
		accountFeeSet.add(accountFee);
		RepaymentSchedule repaymentSchedule = getFeeInstallment(accountFeeSet,
				feeStartDate);
		List<org.mifos.framework.components.repaymentschedule.FeeInstallment> feeInstallmentList = repaymentSchedule
				.getRepaymentFeeInstallment();
		Map<Short, Money> feeInstallmentMap = new HashMap<Short, Money>();
		for (Iterator<org.mifos.framework.components.repaymentschedule.FeeInstallment> iter = feeInstallmentList.iterator(); iter
				.hasNext();) {
			org.mifos.framework.components.repaymentschedule.FeeInstallment feeInstallment = iter.next();
			feeInstallmentMap.put(new Short(Integer.toString(feeInstallment
					.getInstallmentId())), feeInstallment
					.getSummaryAccountFeeInstallment().get(0)
					.getAccountFeeAmount());
			accountFee.setAccountFeeAmount(feeInstallment
					.getSummaryAccountFeeInstallment().get(0).getAccountFee()
					.getAccountFeeAmount());
		}
		return feeInstallmentMap;
	}
	
	protected final RepaymentSchedule getFeeInstallment(
			Set<AccountFeesEntity> accountFeeSet,
			Date feeStartDate) throws AccountException {
		RepaymentScheduleInputsIfc inputs = RepaymentScheduleFactory
				.getRepaymentScheduleInputs();
		RepaymentScheduleIfc repaymentScheduler = RepaymentScheduleFactory
				.getRepaymentScheduler();
		Short accountType = this.getAccountType().getAccountTypeId();
		if (accountType.equals(AccountTypes.LOANACCOUNT.getValue())){
			setLoanInput(inputs, feeStartDate);
		} else if (accountType.equals(AccountTypes.CUSTOMERACCOUNT.getValue())) {
			setCustomerInput(inputs, feeStartDate);
		}
		inputs.setAccountFeeEntity(accountFeeSet);
		inputs.setFeeStartDate(feeStartDate);
		RepaymentSchedule repaymentSchedule=null;
		try {
			repaymentScheduler.setRepaymentScheduleInputs(inputs);
			repaymentSchedule= repaymentScheduler.getRepaymentSchedule();
		} catch (RepaymentScheduleException e) {
			throw new AccountException(e);
		}
		return repaymentSchedule;
		
	}
	
	protected void setLoanInput(RepaymentScheduleInputsIfc inputs,Date feeStartDate){}
	
	protected void setCustomerInput(RepaymentScheduleInputsIfc inputs,Date feeStartDate){}
	
	protected final Boolean isFeeAlreadyApplied(FeeBO fee) {
		if (getAccountFees() != null)
			for (AccountFeesEntity accountFeesEntity : getAccountFees()) {
				if (accountFeesEntity.getFees().getFeeId().equals(
						fee.getFeeId()))
					return true;
			}
		return false;
	}
	
	protected final AccountFeesEntity getAccountFee(FeeBO fee,Money charge){
		AccountFeesEntity accountFee = null;
		if(fee.isPeriodic() && isFeeAlreadyApplied(fee)){
			accountFee = getAccountFees(fee.getFeeId());
			accountFee.setFeeAmount(charge);
			accountFee.setFeeStatus(FeeStatus.ACTIVE.getValue());
		}else{
			accountFee = new AccountFeesEntity(this,fee,charge,
					FeeStatus.ACTIVE.getValue(),new Date(System
							.currentTimeMillis()),null);
		}
		return accountFee;
	}
	
	protected final List<InstallmentDate> getInstallmentDates(MeetingBO meeting,Short noOfInstallments,Short installmentToSkip) throws AccountException
	{
		SchedulerIntf scheduler;
		try {
			scheduler = RepaymentScheduleHelper.getSchedulerObject(convertMeeting(meeting),true);
		} catch (RepaymentScheduleException e) {
			throw new AccountException(e);
		}
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("RepamentSchedular:getInstallmentDates , installments input  ");
		List<InstallmentDate> installmentDates =  getInstallmentDates(scheduler,noOfInstallments,installmentToSkip);
		removeInstallmentsNeedNotPay(installmentToSkip,installmentDates);
		return installmentDates;
	}
	
	private List<InstallmentDate> getInstallmentDates(SchedulerIntf scheduler,Short noOfInstallments,Short installmentSkipToStartRepayment) throws AccountException{
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("InstallmentGenerator:getInstallmentDates no of installments..");
		List<Date> dueDates=null;
		try {
			if(!noOfInstallments.equals(Short.valueOf("0")))
				dueDates = scheduler.getAllDates(noOfInstallments+installmentSkipToStartRepayment);
			else
				dueDates = scheduler.getAllDates();
		} catch (SchedulerException e) {
			throw new AccountException(e);
		}
		int installmentId = 1;
		List<InstallmentDate> installmentDates = new ArrayList<InstallmentDate>();
		for(Date date : dueDates)
			installmentDates.add(new InstallmentDate(new Short(Integer.toString(installmentId++)),date));
		return installmentDates;
	}
	
	private void removeInstallmentsNeedNotPay(Short installmentSkipToStartRepayment,List<InstallmentDate> installmentDates)
	{
		int removeCounter = 0;
		for(int i =0; i < installmentSkipToStartRepayment; i++)
			installmentDates.remove(removeCounter);
		// re-adjust the installment ids
		if(installmentSkipToStartRepayment > 0)
		{
			int count = installmentDates.size();
			for(int i = 0; i < count ; i++)
			{
				InstallmentDate instDate = installmentDates.get(i);
				instDate.setInstallmentId(new Short(Integer.toString(i+1)));
			}
		}
	}
	
	protected  List<InstallmentDate> getInstallmentDates(SchedulerIntf scheduler,
			Integer installmentSkipToStartRepayment) throws AccountException {
		return null;
	}
	
	
	/*protected Integer getInstallmentSkipToStartRepayment() {
		return 0;
	}*/
	
	protected final Meeting convertMeeting(MeetingBO M2meeting) {
		Meeting meetingToReturn = new Meeting();
		meetingToReturn.setMeetingStartDate(M2meeting.getMeetingStartDate());
		meetingToReturn.setMeetingPlace("");
		MeetingType meetingType = new MeetingType();
		meetingType.setMeetingTypeId(M2meeting.getMeetingType()
				.getMeetingTypeId());
		meetingToReturn.setMeetingType(meetingType);

		MeetingRecurrence meetingRecToReturn = new MeetingRecurrence();
		meetingRecToReturn.setDayNumber(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getDayNumber());
		if(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getRankOfDays()!=null){
			meetingRecToReturn.setRankOfDays(M2meeting.getMeetingDetails()
					.getMeetingRecurrence().getRankOfDays().getRankOfDayId());
		}
		if(M2meeting.getMeetingDetails()
				.getMeetingRecurrence().getWeekDay()!=null){
			meetingRecToReturn.setWeekDay(M2meeting.getMeetingDetails()
					.getMeetingRecurrence().getWeekDay().getWeekDayId());
		}

		MeetingDetails meetingDetailsToReturn = new MeetingDetails();
		meetingDetailsToReturn.setMeetingRecurrence(meetingRecToReturn);
		meetingDetailsToReturn.setRecurAfter(M2meeting.getMeetingDetails()
				.getRecurAfter());

		RecurrenceType recurrenceType = new RecurrenceType();
		recurrenceType.setRecurrenceId(M2meeting.getMeetingDetails()
				.getRecurrenceType().getRecurrenceId());

		meetingDetailsToReturn.setRecurrenceType(recurrenceType);

		meetingToReturn.setMeetingDetails(meetingDetailsToReturn);

		return meetingToReturn;

	}
	
	protected final List<FeeInstallment> getFeeInstallment(List<InstallmentDate> installmentDates) throws AccountException 
	{
		List<FeeInstallment> feeInstallmentList=new ArrayList<FeeInstallment>();
		for(AccountFeesEntity accountFeesEntity :  getAccountFees()){
			Short accountFeeType = accountFeesEntity.getFees().getFeeFrequency().getFeeFrequencyType().getId();
			if(accountFeeType.equals(FeeFrequencyType.ONETIME.getValue()))
			{
				feeInstallmentList.add(handleOneTime(accountFeesEntity,installmentDates));
			}
			else if(accountFeeType.equals(FeeFrequencyType.PERIODIC.getValue()))
			{
				feeInstallmentList.addAll(handlePeriodic(accountFeesEntity,installmentDates));
			}
		}
		return feeInstallmentList;
	}
	
	protected List<FeeInstallment> handlePeriodic(AccountFeesEntity accountFees,List<InstallmentDate> installmentDates) throws AccountException{
		return null;
	}

	
	private FeeInstallment handleOneTime(AccountFeesEntity accountFee,List<InstallmentDate> installmentDates)
	{
			Money accountFeeAmount = getAccountFeeAmount(accountFee);
			Date feeDate=installmentDates.get(0).getInstallmentDueDate();
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("FeeInstallmentGenerator:handleOneTime fee start date "+feeDate);
			Short installmentId = getMatchingInstallmentId(installmentDates,feeDate);
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("FeeInstallmentGenerator:handleOneTime applicable installment id "+installmentId);
			return buildFeeInstallment(installmentId,accountFeeAmount,accountFee);
	}
	
	protected Money getAccountFeeAmount(AccountFeesEntity accountFeesEntity){
		return accountFeesEntity.getFeeAmount();
	}
	
	protected final List<Date> getFeeDates(MeetingBO feeMeetingFrequency,List<InstallmentDate> installmentDates)throws AccountException{
		
		MeetingBO repaymentFrequency = getCustomer().getCustomerMeeting().getMeeting();
		Meeting newFeeMeetingFrequency = MeetingScheduleHelper.mergeFrequency(convertMeeting(repaymentFrequency),convertMeeting(feeMeetingFrequency));
		Calendar feeStartDate=new GregorianCalendar();
		feeStartDate.setTime((installmentDates.get(0)).getInstallmentDueDate());
		newFeeMeetingFrequency.setMeetingStartDate(feeStartDate);
		Date repaymentEndDate = (installmentDates.get(installmentDates.size() - 1)).getInstallmentDueDate();
		SchedulerIntf scheduler;
		List<Date> feeDueDates=null;
		try {
			scheduler = MeetingScheduleHelper.getSchedulerObject(newFeeMeetingFrequency,false);
			feeDueDates = scheduler.getAllDates(repaymentEndDate);
		} catch (ApplicationException e) {
				throw new AccountException(e);
		}
		return feeDueDates;
}
	
	protected final FeeInstallment buildFeeInstallment(Short installmentId, Money accountFeeAmount , AccountFeesEntity accountFee){
		FeeInstallment feeInstallment = new FeeInstallment();
		feeInstallment.setInstallmentId(installmentId);
		feeInstallment.setAccountFee(accountFeeAmount);
		feeInstallment.setAccountFeesEntity(accountFee);
		accountFee.setAccountFeeAmount(accountFeeAmount);
		return feeInstallment;
	}
	
	
	protected final Short getMatchingInstallmentId(List<InstallmentDate> installmentDates,Date feeDate){
		for(InstallmentDate installmentDate : installmentDates){
			if(DateUtils.getDateWithoutTimeStamp(installmentDate.getInstallmentDueDate().getTime()).
					compareTo(DateUtils.getDateWithoutTimeStamp(feeDate.getTime()))>=0)
				return installmentDate.getInstallmentId();
		}
		return null;
	}
		
	protected final List<FeeInstallment> mergeFeeInstallments(List<FeeInstallment> feeInstallmentList){
		List<FeeInstallment> newFeeInstallmentList=new ArrayList<FeeInstallment>();
		for (Iterator<FeeInstallment> iterator = feeInstallmentList.iterator(); iterator.hasNext();) {
			FeeInstallment feeInstallment = iterator.next();
			iterator.remove();
			FeeInstallment feeInstTemp=null;
			for(FeeInstallment feeInst : newFeeInstallmentList){
				if(feeInst.getInstallmentId().equals(feeInstallment.getInstallmentId())
							&& feeInst.getAccountFeesEntity().equals(feeInstallment.getAccountFeesEntity())){
						feeInstTemp=feeInst;
						break;
				}
			}
			if(feeInstTemp!=null){
					newFeeInstallmentList.remove(feeInstTemp);
					feeInstTemp.setAccountFee(feeInstTemp.getAccountFee().add(feeInstallment.getAccountFee()));
					newFeeInstallmentList.add(feeInstTemp);
			}else{
					newFeeInstallmentList.add(feeInstallment);
			}
		} 
		return newFeeInstallmentList;
	}

	
	
}
