/**

 * AccountBO.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.exceptions.AccountExceptionConstants;
import org.mifos.application.accounts.exceptions.IDGenerationException;
import org.mifos.application.accounts.financial.business.FinancialTransactionBO;
import org.mifos.application.accounts.financial.business.service.FinancialBusinessService;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.fees.business.FeesBO;
import org.mifos.application.fees.util.helpers.FeesConstants;
import org.mifos.application.master.util.valueobjects.AccountType;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.StringUtils;

/**
 * This class acts as base class for all types of accounts.
 * 
 * @author ashishsm
 * 
 */
public class AccountBO extends BusinessObject {
	AccountPersistanceService accountPersistanceService = null;

	public AccountBO(UserContext userContext) {
		super(userContext);
		accountFees = new HashSet<AccountFeesEntity>();
		accountPayments = new HashSet<AccountPaymentEntity>();
		accountActionDates = new HashSet<AccountActionDateEntity>();
		accountCustomFields = new HashSet<AccountCustomFieldEntity>();
		accountNotes = new HashSet<AccountNotesEntity>();
		accountStatusChangeHistory = new HashSet<AccountStatusChangeHistoryEntity>();
		accountFlags = new HashSet<AccountFlagMapping>();

	}

	// TODO: make constructor as protected and remove usage of this constructor
	// from test cases.
	public AccountBO() {
		accountFees = new HashSet<AccountFeesEntity>();
		accountPayments = new HashSet<AccountPaymentEntity>();
		accountActionDates = new HashSet<AccountActionDateEntity>();
		accountCustomFields = new HashSet<AccountCustomFieldEntity>();
		accountNotes = new HashSet<AccountNotesEntity>();
		accountStatusChangeHistory = new HashSet<AccountStatusChangeHistoryEntity>();
		accountFlags = new HashSet<AccountFlagMapping>();
	}

	private Integer accountId;

	private String globalAccountNum;

	private Date closedDate;

	private CustomerBO customer;

	private AccountStateEntity accountState;

	private Set<AccountFlagMapping> accountFlags;

	private AccountType accountType;

	private OfficeBO office;

	private PersonnelBO personnel;

	private Set<AccountFeesEntity> accountFees;

	private Set<AccountActionDateEntity> accountActionDates;

	private Set<AccountPaymentEntity> accountPayments;

	private Set<AccountCustomFieldEntity> accountCustomFields;

	public Set<AccountNotesEntity> accountNotes;

	public Set<AccountStatusChangeHistoryEntity> accountStatusChangeHistory;

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

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

	public Set<AccountPaymentEntity> getAccountPayments() {
		return accountPayments;
	}

	private void setAccountPayments(Set<AccountPaymentEntity> accountPayments) {
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

	public void setAccountType(AccountType accountType) {
		this.accountType = accountType;
	}

	public String getGlobalAccountNum() {
		return globalAccountNum;
	}

	public void setGlobalAccountNum(String globalAccountNum) {
		this.globalAccountNum = globalAccountNum;
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

	public void setCustomer(CustomerBO customer) {
		this.customer = customer;
	}

	public OfficeBO getOffice() {
		return office;
	}

	public void setOffice(OfficeBO office) {
		this.office = office;
	}

	public PersonnelBO getPersonnel() {
		return personnel;
	}

	public void setPersonnel(PersonnelBO personnel) {
		this.personnel = personnel;
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
		// TODO does this relation need to be bidirectional??
		accountAction.setAccount(this);
	}

	public void addAccountPayment(AccountPaymentEntity payment) {
		payment.setAccount(this);
		for (AccountTrxnEntity trxn : payment.getAccountTrxns()) {
			trxn.setAccount(this);
		}
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

	public void applyPayment(PaymentData paymentData) throws AccountException,
			SystemException {
		AccountPaymentEntity accountPayment = makePayment(paymentData);
		addAccountPayment(accountPayment);
		try {
			buildFinancialEntries(accountPayment.getAccountTrxns());
			getAccountPersistenceService().update(this);
		} catch (FinancialException fe) {
			throw new AccountException("errors.update", fe);
		} catch (ServiceException e) {
			throw new AccountException("errors.update", e);
		}

	}

	protected AccountPaymentEntity makePayment(PaymentData accountPaymentData)
			throws AccountException, SystemException {
		return null;
	}

	protected void updateTotalFeeAmount(Money totalFeeAmount) {
	}

	public Money updateAccountActionDateEntity(List<Short> intallmentIdList,
			Short feeId) {
		Money totalFeeAmount = new Money();
		Set<AccountActionDateEntity> accountActionDateEntitySet = this
				.getAccountActionDates();
		for (AccountActionDateEntity accountActionDateEntity : accountActionDateEntitySet) {
			if (intallmentIdList.contains(accountActionDateEntity
					.getInstallmentId())) {
				totalFeeAmount = totalFeeAmount.add(accountActionDateEntity
						.removeFees(feeId));
			}
		}
		return totalFeeAmount;
	}

	public void updateAccountFeesEntity(Short feeId) {
		Set<AccountFeesEntity> accountFeesEntitySet = this.getAccountFees();
		for (AccountFeesEntity accountFeesEntity : accountFeesEntitySet) {
			if (accountFeesEntity.getFees().getFeeId().equals(feeId)) {
				accountFeesEntity.changeFeesStatus(
						AccountConstants.INACTIVE_FEES, new Date(System
								.currentTimeMillis()));
			}
		}
	}

	public FeesBO getAccountFeesObject(Short feeId) {
		Set<AccountFeesEntity> accountFeesEntitySet = this.getAccountFees();
		for (AccountFeesEntity accountFeesEntity : accountFeesEntitySet) {
			if (accountFeesEntity.getFees().getFeeId().equals(feeId)) {
				return accountFeesEntity.getFees();
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

	protected void buildFinancialEntries(Set<AccountTrxnEntity> accountTrxns)
			throws ServiceException, FinancialException {
		FinancialBusinessService financialBusinessService = (FinancialBusinessService) ServiceFactory
				.getInstance()
				.getBusinessService(BusinessServiceName.Financial);
		for (AccountTrxnEntity accountTrxn : accountTrxns) {
			financialBusinessService.buildAccountingEntries(accountTrxn);
		}
	}

	protected String generateId(String officeGlobalNum)
			throws ServiceException, IDGenerationException {
		StringBuilder systemId = new StringBuilder();
		systemId.append(officeGlobalNum);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
				"After appending the officeGlobalNum to loanAccountSysID  it becomes"
						+ systemId.toString());
		// setting the 11 digits of account running number.
		try {
			systemId.append(StringUtils.lpad(getAccountPersistenceService()
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

	protected AccountPersistanceService getAccountPersistenceService()
			throws ServiceException {
		if (accountPersistanceService == null) {
			accountPersistanceService = (AccountPersistanceService) ServiceFactory
					.getInstance().getPersistenceService(
							PersistenceServiceName.Account);
		}
		return accountPersistanceService;
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
			throws SystemException, ApplicationException {
	}

	public void adjustPmnt(String adjustmentComment)
			throws ApplicationException, SystemException {
		if (isAdjustPossibleOnLastTrxn()) {
			MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug(
					"Adjustment is possible hence attempting to adjust.");
			List<AccountTrxnEntity> reversedTrxns = getLastPmnt()
					.reversalAdjustment(adjustmentComment);
			updateInstallmentAfterAdjustment(reversedTrxns);
			buildFinancialEntries(new HashSet(reversedTrxns));
			((AccountPersistanceService) ServiceFactory.getInstance()
					.getPersistenceService(PersistenceServiceName.Account))
					.save(this);
		} else
			throw new ApplicationException(
					AccountExceptionConstants.CANNOTADJUST);
	}

	protected void updateInstallmentAfterAdjustment(
			List<AccountTrxnEntity> reversedTrxns) {
	}

	protected List<AccountActionDateEntity> getApplicableIdsForDueInstallments() {
		List<AccountActionDateEntity> dueActionDateList = new ArrayList<AccountActionDateEntity>();
		if (isCurrentDateEquallToInstallmentDate()) {
			for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
				if (accountActionDateEntity.getPaymentStatus().equals(
						AccountConstants.PAYMENT_UNPAID)) {
					if (accountActionDateEntity
							.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) <= 0) {
						dueActionDateList.add(accountActionDateEntity);
					}
				}
			}
		} else {
			Boolean flag = true;
			for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
				if (accountActionDateEntity.getPaymentStatus().equals(
						AccountConstants.PAYMENT_UNPAID)) {
					if (accountActionDateEntity
							.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) < 0) {
						dueActionDateList.add(accountActionDateEntity);
					} else if (flag == true
							&& accountActionDateEntity.getActionDate()
									.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
						dueActionDateList.add(accountActionDateEntity);
						flag = false;
					}
				}
			}
		}
		return dueActionDateList;
	}

	protected List<AccountActionDateEntity> getApplicableIdsForFutureInstallments() {
		List<AccountActionDateEntity> futureActionDateList = new ArrayList<AccountActionDateEntity>();
		if (isCurrentDateEquallToInstallmentDate()) {
			for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
				if (accountActionDateEntity.getPaymentStatus().equals(
						AccountConstants.PAYMENT_UNPAID)) {
					if (accountActionDateEntity
							.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
						futureActionDateList.add(accountActionDateEntity);
					}
				}
			}
		} else {
			Boolean flag = true;
			for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
				if (accountActionDateEntity.getPaymentStatus().equals(
						AccountConstants.PAYMENT_UNPAID)) {
					if (accountActionDateEntity
							.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) > 0) {
						if (flag == false)
							futureActionDateList.add(accountActionDateEntity);
						flag = false;
					}
				}
			}
		}
		return futureActionDateList;
	}

	protected boolean isCurrentDateEquallToInstallmentDate() {
		for (AccountActionDateEntity accountActionDateEntity : getAccountActionDates()) {
			if (accountActionDateEntity.getPaymentStatus().equals(
					AccountConstants.PAYMENT_UNPAID)) {
				if (accountActionDateEntity
						.compareDate(DateUtils.getCurrentDateWithoutTimeStamp()) == 0) {
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
		try {
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
		} catch (Exception e) {
			e.printStackTrace();
		}

		return trxnHistory;
	}

	private Double removeSign(Money amount) {
		if (amount.getAmountDoubleValue() < 0)
			return amount.negate().getAmountDoubleValue();
		else
			return amount.getAmountDoubleValue();
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

	
	private void setAccountingEntries(AccountTrxnEntity accountTrxn,TransactionHistoryView transactionHistory){
		
		transactionHistory.setAccountingEnteries(accountTrxn.getAccountPayment().getPaymentId(),
				accountTrxn.getAccountTrxnId(),String.valueOf(removeSign(accountTrxn.getAmount())),
				accountTrxn.getCustomer().getDisplayName(),accountTrxn.getCustomer().getPersonnel().getDisplayName());
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

	public void waiveAmountDue() throws ServiceException,AccountException {
	}

	public void waiveAmountOverDue() throws ServiceException,AccountException {
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
								AccountConstants.PAYMENT_UNPAID))
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
	
	protected List<AccountFeesEntity> getPeriodicFeeList(){	
		List<AccountFeesEntity> periodicFeeList = new ArrayList<AccountFeesEntity>();		
		for(AccountFeesEntity accountFee: getAccountFees()){				
			if(accountFee.getFees().getFeeFrequency().getFeeFrequencyType().getFeeFrequencyTypeId().equals(FeesConstants.PERIODIC)){
				periodicFeeList.add(accountFee);
			}
		}		
		return periodicFeeList;
	}
	
	public AccountFeesEntity getPeriodicAccountFees(Short feeId) {
		for(AccountFeesEntity accountFeesEntity : getAccountFees()){
			if(feeId.equals(accountFeesEntity.getFees().getFeeId())){
				return accountFeesEntity;
			}
		}
		return null;
	}
}
