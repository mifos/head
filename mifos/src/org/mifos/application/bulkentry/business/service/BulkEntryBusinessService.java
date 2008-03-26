/**

 * BulkEntryBusinessService.java    version: 1.0

 

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

package org.mifos.application.bulkentry.business.service;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.FlushMode;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.bulkentry.business.BulkEntryBO;
import org.mifos.application.bulkentry.business.BulkEntryInstallmentView;
import org.mifos.application.bulkentry.business.BulkEntryView;
import org.mifos.application.bulkentry.persistance.BulkEntryPersistence;
import org.mifos.application.bulkentry.persistance.service.BulkEntryPersistenceService;
import org.mifos.application.bulkentry.util.helpers.BulkEntryClientAttendanceThread;
import org.mifos.application.bulkentry.util.helpers.BulkEntryCustomerAccountThread;
import org.mifos.application.bulkentry.util.helpers.BulkEntryLoanThread;
import org.mifos.application.bulkentry.util.helpers.BulkEntrySavingsCache;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.LocalizationConverter;

public class BulkEntryBusinessService extends BusinessService {

	private BulkEntryPersistenceService bulkEntryPersistanceService;

	private CustomerPersistence customerPersistence;

	public BulkEntryBusinessService() {
		bulkEntryPersistanceService = new BulkEntryPersistenceService();
		customerPersistence = new CustomerPersistence();
	}

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return new BulkEntryBO(userContext);
	}

	public Date getLastMeetingDateForCustomer(Integer customerId)
			throws ServiceException {
		try {
			return customerPersistence
					.getLastMeetingDateForCustomer(customerId);
		} catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public void setData(List<BulkEntryView> customerViews,
			Map<Integer, BulkEntrySavingsCache> savingsCache,
			List<ClientBO> clients, List<String> savingsDepNames,
			List<String> savingsWithNames, List<String> customerNames,
			Short personnelId, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate, Date meetingDate) {
		StringBuffer isThreadDone = new StringBuffer();
		BulkEntryClientAttendanceThread bulkEntryClientAttendanceThread = 
			new BulkEntryClientAttendanceThread(
				customerViews, clients, customerNames, meetingDate,
				isThreadDone);
		Thread thread = new Thread(bulkEntryClientAttendanceThread);
		thread.start();
		for (BulkEntryView parent : customerViews) {
			setSavingsDepositDetails(parent.getSavingsAccountDetails(),
					personnelId, recieptId, paymentId, receiptDate,
					transactionDate, savingsDepNames, parent
							.getCustomerDetail().getCustomerLevelId(), parent
							.getCustomerDetail().getCustomerId(), savingsCache);
			setSavingsWithdrawalsDetails(parent.getSavingsAccountDetails(),
					personnelId, recieptId, paymentId, receiptDate,
					transactionDate, savingsWithNames, parent
							.getCustomerDetail().getCustomerId(), savingsCache);
		}
		
		/* We probably could just join the thread here (but what do
		   we need to do with InterruptedException?). */
		while (!isThreadDone.toString().equals("Done")) {
			try {
				Thread.sleep(100L);
			}
			catch (InterruptedException e) {
			}
		}
	}
	
	private Double getDoubleValue(String str) {
		return StringUtils.isNullAndEmptySafe(str) ? LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(str) :null;
	}

	private void setSavingsWithdrawalsDetails(
			List<SavingsAccountView> accountViews, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate, List<String> accountNums, Integer customerId,
			Map<Integer, BulkEntrySavingsCache> savings) {
		if (null != accountViews) {
			for (SavingsAccountView accountView : accountViews) {
				String amount = accountView.getWithDrawalAmountEntered();
				if (null != amount && !"".equals(amount.trim())
						&& !getDoubleValue(amount).equals(0.0)) {
					try {
						setSavingsWithdrawalAccountDetails(accountView,
								personnelId, recieptId, paymentId, receiptDate,
								transactionDate, customerId, savings);
					} catch (ServiceException be) {
						if (savings.containsKey(accountView.getAccountId()))
							savings.get(accountView.getAccountId())
									.setYesNoFlag(YesNoFlag.NO);
						accountNums.add((String) (be.getValues()[0]));
						HibernateUtil.rollbackTransaction();

					} catch (Exception e) {
						if (savings.containsKey(accountView.getAccountId()))
							savings.get(accountView.getAccountId())
									.setYesNoFlag(YesNoFlag.NO);
						accountNums.add(accountView.getAccountId().toString());
						HibernateUtil.rollbackTransaction();
					} finally {
						HibernateUtil.closeSession();
					}
				}
			}
		}
	}

	private void setSavingsDepositDetails(
			List<SavingsAccountView> accountViews, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate, List<String> accountNums, Short levelId,
			Integer customerId, Map<Integer, BulkEntrySavingsCache> savings) {
		if (null != accountViews) {
			for (SavingsAccountView accountView : accountViews) {
				String amount = accountView.getDepositAmountEntered();
				if (null != amount && !getDoubleValue(amount).equals(0.0)) {
					if ((!savings.containsKey(accountView.getAccountId()))
							|| (savings.containsKey(accountView.getAccountId()) && (!savings
									.get(accountView.getAccountId())
									.getYesNoFlag().equals(YesNoFlag.NO))))
						try {
							boolean isCenterGroupIndvAccount = false;
							if (levelId
									.equals(CustomerLevel.CENTER.getValue())
									|| (levelId
											.equals(CustomerLevel.GROUP.getValue()) && accountView
											.getSavingsOffering()
											.getRecommendedAmntUnit()
											.getId()
											.equals(
													RecommendedAmountUnit.PER_INDIVIDUAL
															.getValue()))) {
								isCenterGroupIndvAccount = true;
							}
							setSavingsDepositAccountDetails(accountView,
									personnelId, recieptId, paymentId,
									receiptDate, transactionDate,
									isCenterGroupIndvAccount, customerId,
									savings);
						} catch (ServiceException be) {
							if (savings.containsKey(accountView.getAccountId()))
								savings.get(accountView.getAccountId())
										.setYesNoFlag(YesNoFlag.NO);
							accountNums.add((String) (be.getValues()[0]));
							HibernateUtil.rollbackTransaction();
						} catch (Exception e) {
							if (savings.containsKey(accountView.getAccountId()))
								savings.get(accountView.getAccountId())
										.setYesNoFlag(YesNoFlag.NO);
							accountNums.add(accountView.getAccountId()
									.toString());
							HibernateUtil.rollbackTransaction();
						} finally {
							HibernateUtil.closeSession();
						}
				}
			}
		}
	}

	public void saveData(List<LoanAccountsProductView> accountViews,
			Short personnelId, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate, List<String> accountNums,
			List<SavingsBO> savings, List<String> savingsNames,
			List<ClientBO> clients, List<String> customerNames,
			List<CustomerAccountView> customerAccounts,
			List<String> customerAccountNums) {
		StringBuffer isThreadOneDone = new StringBuffer();
		StringBuffer isCustomerAccountThreadDone = new StringBuffer();
		BulkEntryLoanThread bulkEntryLoanThreadOne = new BulkEntryLoanThread(
				accountViews, personnelId, recieptId, paymentId, receiptDate,
				transactionDate, accountNums, isThreadOneDone);
		Thread threadOne = new Thread(bulkEntryLoanThreadOne);
		threadOne.start();
		BulkEntryCustomerAccountThread bulkEntryCustomerAccountThread = 
			new BulkEntryCustomerAccountThread(
				customerAccounts, personnelId, recieptId, paymentId,
				receiptDate, transactionDate, customerAccountNums,
				isCustomerAccountThreadDone);
		Thread threadCustomerAccount = new Thread(
				bulkEntryCustomerAccountThread);
		threadCustomerAccount.start();
		saveAttendance(clients, customerNames);
		saveSavingsAccount(savings, savingsNames);

		/* We probably could just join the threads here (but what do
		   we need to do with InterruptedException?). */
		while ((!isThreadOneDone.toString().equals("Done"))
				|| (!isCustomerAccountThreadDone.toString().equals("Done"))) {
			try {
				Thread.sleep(100L);
			}
			catch (InterruptedException e) {
			}
		}
		
		/* Commented out until we can figure out what is up
		   with TestBulkEntryAction#SUPPLY_ENTERED_AMOUNT_PARAMETERS
		Exception exception = bulkEntryLoanThreadOne.exception;
		if (exception != null) {
			throw new RuntimeException(exception);
		}
		 */
	}

	private void saveAttendance(List<ClientBO> clients,
			List<String> customerNames) {
		for (ClientBO client : clients) {
			try {
				saveClientAttendance(client);
				HibernateUtil.commitTransaction();
			} catch (ServiceException e) {
				HibernateUtil.rollbackTransaction();
				customerNames.add(client.getDisplayName());
			} finally {
				HibernateUtil.closeSession();
			}
		}
	}

	private void saveSavingsAccount(List<SavingsBO> savings,
			List<String> customerNames) {
		for (SavingsBO saving : savings) {
			try {
				saveSavingsAccount(saving);
				HibernateUtil.commitTransaction();
			} catch (ServiceException e) {
				HibernateUtil.rollbackTransaction();
				customerNames.add(saving.getGlobalAccountNum());
			} finally {
				HibernateUtil.closeSession();
			}
		}
	}

	public void saveLoanAccount(
			LoanAccountsProductView loanAccountsProductView, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate) throws ServiceException {
		for (LoanAccountView accountView : loanAccountsProductView
				.getLoanAccountViews()) {
			Integer accountId = accountView.getAccountId();
			if (accountView.isDisbursalAccount()) {
				saveLoanDisbursement(accountId, personnelId, recieptId,
						paymentId, transactionDate, loanAccountsProductView
								.getDisBursementAmountEntered(), receiptDate);
			} else {
				saveLoanAccountPayment(accountId, personnelId, recieptId,
						paymentId, receiptDate, transactionDate,
						loanAccountsProductView, accountView);
			}
		}
	}

	public void setSavingsDepositAccountDetails(SavingsAccountView accountView,
			Short personnelId, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate,
			boolean isCenterGroupIndvAccount, Integer customerId,
			Map<Integer, BulkEntrySavingsCache> savings)
			throws ServiceException {
		Integer accountId = accountView.getAccountId();
		PaymentData accountPaymentDataView = getSavingsAccountPaymentData(
				accountView, customerId, personnelId, recieptId, paymentId,
				receiptDate, transactionDate, isCenterGroupIndvAccount);
		saveSavingsAccountPayment(accountId, accountPaymentDataView, savings);
	}

	public void setSavingsWithdrawalAccountDetails(
			SavingsAccountView accountView, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate, Integer customerId,
			Map<Integer, BulkEntrySavingsCache> savings)
			throws ServiceException {
		if (null != accountView) {
			Integer accountId = accountView.getAccountId();
			if (null != accountId) {
				PaymentData accountPaymentDataView = getWithdrawalSavingsPaymentDataView(
						accountView, customerId, personnelId, recieptId,
						paymentId, receiptDate, transactionDate);
				saveSavingsWithdrawal(accountId, accountPaymentDataView,
						savings);
			}
		}
	}

	public void saveCustomerAccountCollections(
			CustomerAccountView customerAccountView, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate) throws ServiceException {
		Integer accountId = customerAccountView.getAccountId();
		PaymentData accountPaymentDataView = getCustomerAccountPaymentDataView(
				customerAccountView.getAccountActionDates(),
				customerAccountView.getTotalAmountDue(), personnelId,
				recieptId, paymentId, receiptDate, transactionDate);
		AccountBO account = null;
		try {
			account = getAccount(accountId, AccountTypes.CUSTOMER_ACCOUNT);
			account.applyPaymentWithPersist(accountPaymentDataView);
		} catch (AccountException ae) {
			throw new ServiceException("errors.update", ae,
					new String[] { account.getGlobalAccountNum() });
		}
	}

	public void setClientAttendance(Integer customerId, Date meetingDate,
			Short attendance, List<ClientBO> clients) throws ServiceException {
		try {
			ClientBO client = (ClientBO) getCustomer(customerId);
			HibernateUtil.getSessionTL().setFlushMode(FlushMode.COMMIT);
			client.handleAttendance(meetingDate, attendance);
			HibernateUtil.getSessionTL().clear();
			clients.add(client);
		} catch (Exception e) {
			throw new ServiceException("errors.update", e,
					new String[] { customerId.toString() });
		}
	}

	public void saveLoanAccount(LoanBO loan) throws ServiceException {
		try {
			new BulkEntryPersistence().createOrUpdate(loan);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public void saveClientAttendance(ClientBO client) throws ServiceException {
		try {
			new BulkEntryPersistence().createOrUpdate(client);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public void saveSavingsAccount(SavingsBO savings) throws ServiceException {
		try {
			new BulkEntryPersistence().createOrUpdate(savings);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	private AccountBO getAccount(Integer accountId, AccountTypes type)
			throws ServiceException {
		AccountBO account = null;
		try {
			if (type.equals(AccountTypes.LOAN_ACCOUNT)) {
				account = bulkEntryPersistanceService
						.getLoanAccountWithAccountActionsInitialized(accountId);
			} else if (type.equals(AccountTypes.SAVINGS_ACCOUNT)) {
				account = bulkEntryPersistanceService
						.getSavingsAccountWithAccountActionsInitialized(accountId);
			} else if (type.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
				account = bulkEntryPersistanceService
						.getCustomerAccountWithAccountActionsInitialized(accountId);
			}
		} catch (PersistenceException e) {
			throw new ServiceException("errors.update", e,
					new String[] { accountId.toString() });
		}
		return account;
	}

	private CustomerBO getCustomer(Integer customerId) throws ServiceException {
		try {
			return bulkEntryPersistanceService.getCustomer(customerId);
		} catch (PersistenceException pe) {
			throw new ServiceException("errors.update", pe,
					new String[] { customerId.toString() });
		}
	}

	private PersonnelBO getPersonnel(Short personnelId) throws ServiceException {
		try {
			return bulkEntryPersistanceService.getPersonnel(personnelId);
		} catch (PersistenceException pe) {
			throw new ServiceException("errors.update", pe,
					new String[] { personnelId.toString() });
		}
	}

	private void saveLoanDisbursement(Integer accountId, Short personnelId,
			String recieptId, Short paymentId, Date transactionDate,
			String disbursementAmountEntered, Date receiptDate)
			throws ServiceException {
		if (getDoubleValue(disbursementAmountEntered).doubleValue() > 0) {
			LoanBO account = null;
			try {
				account = (LoanBO) getAccount(accountId,
						AccountTypes.LOAN_ACCOUNT);
				account.disburseLoan(recieptId, transactionDate, paymentId,
						getPersonnel(personnelId), receiptDate, paymentId);
			} catch (Exception ae) {
				throw new ServiceException("errors.update", ae,
						new String[] { account.getGlobalAccountNum() });
			}
		}
	}

	private void saveLoanAccountPayment(Integer accountId, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate,
			LoanAccountsProductView loanAccountsProductView,
			LoanAccountView loanAccountView) throws ServiceException {
		Double amount = getDoubleValue(loanAccountsProductView
				.getEnteredAmount());
		if (amount > 0.0) {
			Money enteredAmount;
			if (loanAccountsProductView.getLoanAccountViews().size() > 1)
				enteredAmount = new Money(Configuration.getInstance()
						.getSystemConfig().getCurrency(), String
						.valueOf(loanAccountView.getTotalAmountDue()));
			else
				enteredAmount = new Money(Configuration.getInstance()
						.getSystemConfig().getCurrency(),
						loanAccountsProductView.getEnteredAmount());
			PaymentData paymentData = getLoanAccountPaymentData(loanAccountView
					.getAccountTrxnDetails(), enteredAmount, personnelId,
					recieptId, paymentId, receiptDate, transactionDate);
			AccountBO account = null;
			try {
				account = getAccount(accountId, AccountTypes.LOAN_ACCOUNT);
				account.applyPaymentWithPersist(paymentData);
			} catch (Exception ae) {
				throw new ServiceException("errors.update", ae,
						new String[] { account.getGlobalAccountNum() });
			}
		}
	}

	private PaymentData getLoanAccountPaymentData(
			List<BulkEntryInstallmentView> accountActions, Money totalAmount,
			Short personnelId, String recieptNum, Short paymentId,
			Date receiptDate, Date transactionDate) throws ServiceException {
		PaymentData paymentData = PaymentData.createPaymentData(totalAmount,
				getPersonnel(personnelId), paymentId, transactionDate);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptNum);
		return paymentData;
	}

	private void saveSavingsAccountPayment(Integer accountId,
			PaymentData accountPaymentDataView,
			Map<Integer, BulkEntrySavingsCache> savings)
			throws ServiceException {
		AccountBO account = null;
		try {
			if (savings.containsKey(accountId))
				account = savings.get(accountId).getAccount();
			else
				account = getAccount(accountId, AccountTypes.SAVINGS_ACCOUNT);
			HibernateUtil.getSessionTL().setFlushMode(FlushMode.COMMIT);
			account.applyPaymentWithPersist(accountPaymentDataView);
			HibernateUtil.getSessionTL().clear();
			savings.put(account.getAccountId(), new BulkEntrySavingsCache(
					(SavingsBO) account, YesNoFlag.YES));
		} catch (Exception ae) {
			if (savings.containsKey(accountId))
				savings.get(accountId).setYesNoFlag(YesNoFlag.NO);
			throw new ServiceException("errors.update", ae,
					new String[] { account.getGlobalAccountNum() });
		}
	}

	private PaymentData getSavingsAccountPaymentData(
			SavingsAccountView savingsAccountView, Integer customerId,
			Short personnelId, String recieptNum, Short paymentId,
			Date receiptDate, Date transactionDate,
			boolean isCenterGroupIndvAccount) throws ServiceException {
		Money enteredAmount = new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), savingsAccountView
				.getDepositAmountEntered());
		PaymentData paymentData = PaymentData.createPaymentData(enteredAmount,
				getPersonnel(personnelId), paymentId, transactionDate);
		if (!isCenterGroupIndvAccount
				&& savingsAccountView.getAccountTrxnDetails().size() > 0)
			buildIndividualAccountSavingsPayments(paymentData,
					savingsAccountView, enteredAmount);
		paymentData.setCustomer(getCustomer(customerId));
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptNum);
		return paymentData;
	}

	private void buildIndividualAccountSavingsPayments(PaymentData paymentData,
			SavingsAccountView savingsAccountView, Money enteredAmount) {
		for (BulkEntryInstallmentView accountActionDate : savingsAccountView
				.getAccountTrxnDetails()) {
			SavingsPaymentData savingsPaymentData = new SavingsPaymentData(
					accountActionDate);
			paymentData.addAccountPaymentData(savingsPaymentData);
		}
	}

	private void saveSavingsWithdrawal(Integer accountId,
			PaymentData accountPaymentDataView,
			Map<Integer, BulkEntrySavingsCache> savings)
			throws ServiceException {
		SavingsBO account = null;
		try {
			if (savings.containsKey(accountId))
				account = savings.get(accountId).getAccount();
			else
				account = (SavingsBO) getAccount(accountId,
						AccountTypes.SAVINGS_ACCOUNT);
			// TODO: Committing the transaction fixes unit test, but is this the right thing to do?
			HibernateUtil.getSessionTL().setFlushMode(FlushMode.COMMIT);
			account.withdraw(accountPaymentDataView);
			HibernateUtil.commitTransaction();
			HibernateUtil.getSessionTL().clear();
			savings.put(account.getAccountId(), new BulkEntrySavingsCache(
					account, YesNoFlag.YES));
		} catch (Exception ae) {
			if (savings.containsKey(accountId))
				savings.get(accountId).setYesNoFlag(YesNoFlag.NO);
			throw new ServiceException("errors.update", ae,
					new String[] { account.getGlobalAccountNum() });
		}
	}

	private PaymentData getWithdrawalSavingsPaymentDataView(
			SavingsAccountView savingsAccountView, Integer customerId,
			Short personnelId, String recieptNum, Short paymentId,
			Date receiptDate, Date transactionDate) throws ServiceException {
		Money enteredAmount = new Money(Configuration.getInstance()
				.getSystemConfig().getCurrency(), savingsAccountView
				.getWithDrawalAmountEntered());
		PaymentData paymentData = PaymentData.createPaymentData(enteredAmount,
				getPersonnel(personnelId), paymentId, transactionDate);
		paymentData.setCustomer(getCustomer(customerId));
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptNum);
		return paymentData;
	}

	private PaymentData getCustomerAccountPaymentDataView(
			List<BulkEntryInstallmentView> accountActions, Money totalAmount,
			Short personnelId, String recieptNum, Short paymentId,
			Date receiptDate, Date transactionDate) throws ServiceException {
		PaymentData paymentData = PaymentData.createPaymentData(totalAmount,
				getPersonnel(personnelId), paymentId, transactionDate);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptNum);
		for (BulkEntryInstallmentView actionDate : accountActions) {
			CustomerAccountPaymentData customerAccountPaymentData = new CustomerAccountPaymentData(
					actionDate);
			paymentData.addAccountPaymentData(customerAccountPaymentData);
		}
		return paymentData;
	}
}