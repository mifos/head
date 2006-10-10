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

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.business.LoanAccountsProductView;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.bulkentry.business.BulkEntryBO;
import org.mifos.application.bulkentry.business.BulkEntryInstallmentView;
import org.mifos.application.bulkentry.persistance.service.BulkEntryPersistanceService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

public class BulkEntryBusinessService extends BusinessService {

	private BulkEntryPersistanceService bulkEntryPersistanceService;

	private CustomerPersistence customerPersistence;

	public BulkEntryBusinessService() {
		bulkEntryPersistanceService = new BulkEntryPersistanceService();
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

	public void saveSavingsDepositAccount(SavingsAccountView accountView,
			Short personnelId, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate,
			boolean isCenterGroupIndvAccount, Integer customerId)
			throws ServiceException {
		Integer accountId = accountView.getAccountId();
		PaymentData accountPaymentDataView = getSavingsAccountPaymentData(
				accountView, customerId, personnelId, recieptId, paymentId,
				receiptDate, transactionDate, isCenterGroupIndvAccount);
		saveSavingsAccountPayment(accountId, accountPaymentDataView);
	}

	public void saveSavingsWithdrawalAccount(SavingsAccountView accountView,
			Short personnelId, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate, Integer customerId)
			throws ServiceException {
		if (null != accountView) {
			Integer accountId = accountView.getAccountId();
			if (null != accountId) {
				PaymentData accountPaymentDataView = getWithdrawalSavingsPaymentDataView(
						accountView, customerId, personnelId, recieptId,
						paymentId, receiptDate, transactionDate);
				saveSavingsWithdrawal(accountId, accountPaymentDataView);
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
			account = getAccount(accountId, AccountTypes.CUSTOMERACCOUNT);
			account.applyPayment(accountPaymentDataView);
		} catch (AccountException ae) {
			throw new ServiceException("errors.update", ae,
					new String[] { account.getGlobalAccountNum() });
		}
	}

	public void saveAttendance(Integer customerId, Date meetingDate,
			Short attendance) throws ServiceException {
		ClientBO client = (ClientBO) getCustomer(customerId);
		try {
			client.handleAttendance(meetingDate, attendance);
		} catch (CustomerException e) {
			throw new ServiceException("errors.update", e,
					new String[] { client.getGlobalCustNum() });
		}
	}

	private AccountBO getAccount(Integer accountId, AccountTypes type)
			throws ServiceException {
		try {
			if (type.equals(AccountTypes.LOANACCOUNT))

				return bulkEntryPersistanceService
						.getLoanAccountWithAccountActionsInitialized(accountId);

			else if (type.equals(AccountTypes.SAVINGSACCOUNT))
				return bulkEntryPersistanceService
						.getSavingsAccountWithAccountActionsInitialized(accountId);
			else if (type.equals(AccountTypes.CUSTOMERACCOUNT))
				return bulkEntryPersistanceService
						.getCustomerAccountWithAccountActionsInitialized(accountId);
			return null;
		} catch (PersistenceException e) {
			throw new ServiceException("errors.update", e,
					new String[] { accountId.toString() });
		}
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
		if (Double.valueOf(disbursementAmountEntered).doubleValue() > 0) {
			LoanBO account = null;
			try {
				account = (LoanBO) getAccount(accountId,
						AccountTypes.LOANACCOUNT);
				account.disburseLoan(recieptId, transactionDate, paymentId,
						getPersonnel(personnelId), receiptDate, paymentId);
			} catch (AccountException ae) {
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
		Double amount = Double.valueOf(loanAccountsProductView
				.getEnteredAmount());
		if (amount > 0.0) {
			Money enteredAmount = new Money();
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
				account = getAccount(accountId, AccountTypes.LOANACCOUNT);
				account.applyPayment(paymentData);
			} catch (AccountException ae) {
				throw new ServiceException("errors.update", ae,
						new String[] { account.getGlobalAccountNum() });
			}
		}
	}

	private PaymentData getLoanAccountPaymentData(
			List<BulkEntryInstallmentView> accountActions, Money totalAmount,
			Short personnelId, String recieptNum, Short paymentId,
			Date receiptDate, Date transactionDate) throws ServiceException {
		PaymentData paymentData = new PaymentData(totalAmount,
				getPersonnel(personnelId), paymentId, transactionDate);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptNum);
		return paymentData;
	}

	private void saveSavingsAccountPayment(Integer accountId,
			PaymentData accountPaymentDataView) throws ServiceException {
		AccountBO account = null;
		try {
			account = getAccount(accountId, AccountTypes.SAVINGSACCOUNT);
			account.applyPayment(accountPaymentDataView);
		} catch (AccountException ae) {
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
		PaymentData paymentData = new PaymentData(enteredAmount,
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
			PaymentData accountPaymentDataView) throws ServiceException {
		SavingsBO account = null;
		try {
			account = (SavingsBO) getAccount(accountId,
					AccountTypes.SAVINGSACCOUNT);
			account.withdraw(accountPaymentDataView);
		} catch (AccountException ae) {
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
		PaymentData paymentData = new PaymentData(enteredAmount,
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
		PaymentData paymentData = new PaymentData(totalAmount,
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