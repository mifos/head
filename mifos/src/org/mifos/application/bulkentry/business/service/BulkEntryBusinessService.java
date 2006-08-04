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

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.business.LoanAccountsProductView;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.accounts.exceptions.AccountException;
import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.persistance.service.LoanPersistenceService;
import org.mifos.application.accounts.persistence.service.AccountPersistanceService;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.persistence.service.SavingsPersistenceService;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.CustomerAccountPaymentData;
import org.mifos.application.accounts.util.helpers.LoanPaymentData;
import org.mifos.application.accounts.util.helpers.PaymentData;
import org.mifos.application.accounts.util.helpers.SavingsPaymentData;
import org.mifos.application.bulkentry.business.BulkEntryInstallmentView;
import org.mifos.application.bulkentry.business.BulkEntryBO;
import org.mifos.application.bulkentry.exceptions.BulkEntryAccountUpdateException;
import org.mifos.application.bulkentry.persistance.service.BulkEntryPersistanceService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.repaymentschedule.RepaymentScheduleException;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class BulkEntryBusinessService extends BusinessService {

	private BulkEntryPersistanceService bulkEntryPersistanceService;

	private CustomerPersistenceService customerPersistenceService;

	private AccountPersistanceService accountPersistanceService;

	private LoanPersistenceService loanPersistenceService;

	private SavingsPersistenceService savingsPersistenceService;

	public BulkEntryBusinessService() {
		try {
			bulkEntryPersistanceService = new BulkEntryPersistanceService();
			customerPersistenceService = (CustomerPersistenceService) ServiceFactory
					.getInstance().getPersistenceService(
							PersistenceServiceName.Customer);
			accountPersistanceService = (AccountPersistanceService) ServiceFactory
					.getInstance().getPersistenceService(
							PersistenceServiceName.Account);
			loanPersistenceService = (LoanPersistenceService) ServiceFactory
					.getInstance().getPersistenceService(
							PersistenceServiceName.Loan);

			savingsPersistenceService = (SavingsPersistenceService) ServiceFactory
					.getInstance().getPersistenceService(
							PersistenceServiceName.Savings);

		} catch (ServiceException se) {
		}
	}

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return new BulkEntryBO(userContext);
	}

	public List<LoanAccountView> retrieveAccountInformationForCustomer(
			Integer customerId, Date disbursementDate) {
		return loanPersistenceService.getLoanAccountsForCustomer(customerId,
				disbursementDate);
	}

	public List<AccountActionDateEntity> retrieveLoanAccountTransactionDetail(
			Integer accountId, Date transactionDate) {
		return loanPersistenceService.getTransactionDetailForLoanAccount(
				accountId, transactionDate);
	}

	public List<SavingsAccountView> retrieveSavingsAccountInformationForCustomer(
			Integer customerId) {
		return savingsPersistenceService
				.getSavingsAccountsForCustomer(customerId);
	}

	public List<AccountActionDateEntity> retrieveSavingsAccountTransactionDetail(
			Integer accountId, Integer customerId, Date transactionDate,
			boolean isMandatory) {
		return savingsPersistenceService.getTransactionDetailForSavingsAccount(
				accountId, customerId, transactionDate, isMandatory);
	}

	public Double getFeeAmountAtDisbursement(Integer accountId,
			Date transactionDate) {
		return loanPersistenceService.getFeeAmountAtDisbursement(accountId,
				transactionDate);
	}

	public CustomerBO retrieveCustomerAccountInfo(Integer customerId) {
		return customerPersistenceService.getCustomer(customerId);
	}

	public List<AccountActionDateEntity> retrieveCustomerAccountActionDetails(
			Integer accountId, Date transactionDate) {
		return accountPersistanceService.retrieveCustomerAccountActionDetails(
				accountId, transactionDate);
	}

	public Date getLastMeetingDateForCustomer(Integer customerId)
			throws SystemException, ApplicationException {
		return customerPersistenceService
				.getLastMeetingDateForCustomer(customerId);
	}

	public List<PrdOfferingBO> getLoanOfferingBOForCustomer(
			String customerSearchId, Date trxnDate) {
		return loanPersistenceService.getLoanOfferingBOForCustomer(
				customerSearchId, trxnDate);
	}

	public void saveLoanAccount(
			LoanAccountsProductView loanAccountsProductView, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate) throws BulkEntryAccountUpdateException {
		for (LoanAccountView accountView : loanAccountsProductView
				.getLoanAccountViews()) {
			Integer accountId = accountView.getAccountId();
			if (isDisbursalAccount(accountView)) {
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
			throws BulkEntryAccountUpdateException {
		Integer accountId = accountView.getAccountId();
		PaymentData accountPaymentDataView = getSavingsAccountPaymentData(
				accountView, customerId, personnelId, recieptId, paymentId,
				receiptDate, transactionDate, isCenterGroupIndvAccount);
		saveSavingsAccountPayment(accountId, accountPaymentDataView);
	}

	public void saveSavingsWithdrawalAccount(SavingsAccountView accountView,
			Short personnelId, String recieptId, Short paymentId,
			Date receiptDate, Date transactionDate, Integer customerId)
			throws BulkEntryAccountUpdateException {
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
			Date transactionDate) throws BulkEntryAccountUpdateException {
		Integer accountId = customerAccountView.getAccountId();
		PaymentData accountPaymentDataView = getCustomerAccountPaymentDataView(
				customerAccountView.getAccountActionDates(),
				customerAccountView.getTotalAmountDue(), personnelId,
				recieptId, paymentId, receiptDate, transactionDate);
		AccountBO account = getAccount(accountId, AccountTypes.CUSTOMERACCOUNT);
		try {
			account.applyPayment(accountPaymentDataView);
		} catch (AccountException ae) {
			throw new BulkEntryAccountUpdateException("errors.update", ae,
					new String[] { account.getGlobalAccountNum() });
		} catch (SystemException se) {
			throw new BulkEntryAccountUpdateException("errors.update", se,
					new String[] { account.getGlobalAccountNum() });
		}
	}

	public void saveAttendance(Integer customerId, Date meetingDate,
			Short attendance) throws BulkEntryAccountUpdateException {
		ClientBO client = (ClientBO) getCustomer(customerId);
		try {
			client.handleAttendance(meetingDate, attendance);
		} catch (ServiceException se) {
			throw new BulkEntryAccountUpdateException("errors.update", se,
					new String[] { client.getGlobalCustNum() });
		}
	}

	private AccountBO getAccount(Integer accountId, String type) {
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
	}

	private CustomerBO getCustomer(Integer customerId) {
		return bulkEntryPersistanceService.getCustomer(customerId);
	}

	private PersonnelBO getPersonnel(Short personnelId) {
		return bulkEntryPersistanceService.getPersonnel(personnelId);
	}

	private void saveLoanDisbursement(Integer accountId, Short personnelId,
			String recieptId, Short paymentId, Date transactionDate,
			String disbursementAmountEntered, Date receiptDate)
			throws BulkEntryAccountUpdateException {
		if (Double.valueOf(disbursementAmountEntered).doubleValue() > 0) {
			LoanBO account = (LoanBO) getAccount(accountId,
					AccountTypes.LOANACCOUNT);
			try {
				account.disburseLoan(recieptId, transactionDate, paymentId,
						getPersonnel(personnelId), receiptDate, paymentId);
			} catch (RepaymentScheduleException rse) {
				throw new BulkEntryAccountUpdateException("errors.update", rse,
						new String[] { account.getGlobalAccountNum() });
			} catch (FinancialException fe) {
				throw new BulkEntryAccountUpdateException("errors.update", fe,
						new String[] { account.getGlobalAccountNum() });
			} catch (AccountException ae) {
				throw new BulkEntryAccountUpdateException("errors.update", ae,
						new String[] { account.getGlobalAccountNum() });
			} catch (SystemException se) {
				throw new BulkEntryAccountUpdateException("errors.update", se,
						new String[] { account.getGlobalAccountNum() });
			}
		}
	}

	private void saveLoanAccountPayment(Integer accountId, Short personnelId,
			String recieptId, Short paymentId, Date receiptDate,
			Date transactionDate,
			LoanAccountsProductView loanAccountsProductView,
			LoanAccountView loanAccountView)
			throws BulkEntryAccountUpdateException {
		Double amount = Double.valueOf(loanAccountsProductView
				.getEnteredAmount());
		if (amount > 0) {
			Money enteredAmount = new Money(Configuration.getInstance()
					.getSystemConfig().getCurrency(), loanAccountView
					.getTotalAmountDue());
			PaymentData paymentData = getLoanAccountPaymentData(loanAccountView
					.getAccountTrxnDetails(), enteredAmount, personnelId,
					recieptId, paymentId, receiptDate, transactionDate);
			AccountBO account = getAccount(accountId, AccountTypes.LOANACCOUNT);
			try {
				account.applyPayment(paymentData);
			} catch (AccountException ae) {
				throw new BulkEntryAccountUpdateException("errors.update", ae,
						new String[] { account.getGlobalAccountNum() });
			} catch (SystemException se) {
				throw new BulkEntryAccountUpdateException("errors.update", se,
						new String[] { account.getGlobalAccountNum() });
			}
		}
	}

	private boolean isDisbursalAccount(LoanAccountView loanAccountView) {
		short accountSate = loanAccountView.getAccountSate().shortValue();
		return accountSate == AccountStates.LOANACC_APPROVED
				|| accountSate == AccountStates.LOANACC_DBTOLOANOFFICER;

	}

	private PaymentData getLoanAccountPaymentData(
			List<BulkEntryInstallmentView> accountActions, Money totalAmount,
			Short personnelId, String recieptNum, Short paymentId,
			Date receiptDate, Date transactionDate) {
		PaymentData paymentData = new PaymentData(totalAmount,
				getPersonnel(personnelId), paymentId, transactionDate);
		paymentData.setRecieptDate(receiptDate);
		paymentData.setRecieptNum(recieptNum);
		for (BulkEntryInstallmentView actionDate : accountActions) {
			LoanPaymentData loanPaymentData = new LoanPaymentData(actionDate);
			paymentData.addAccountPaymentData(loanPaymentData);
		}
		return paymentData;
	}

	private void saveSavingsAccountPayment(Integer accountId,
			PaymentData accountPaymentDataView)
			throws BulkEntryAccountUpdateException {
		AccountBO account = getAccount(accountId, AccountTypes.SAVINGSACCOUNT);
		try {
			account.applyPayment(accountPaymentDataView);
		} catch (AccountException ae) {
			throw new BulkEntryAccountUpdateException("errors.update", ae,
					new String[] { account.getGlobalAccountNum() });
		} catch (SystemException se) {
			throw new BulkEntryAccountUpdateException("errors.update", se,
					new String[] { account.getGlobalAccountNum() });
		}
	}

	private PaymentData getSavingsAccountPaymentData(
			SavingsAccountView savingsAccountView, Integer customerId,
			Short personnelId, String recieptNum, Short paymentId,
			Date receiptDate, Date transactionDate,
			boolean isCenterGroupIndvAccount) {
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
			PaymentData accountPaymentDataView)
			throws BulkEntryAccountUpdateException {
		SavingsBO account = (SavingsBO) getAccount(accountId,
				AccountTypes.SAVINGSACCOUNT);
		try {
			account.withdraw(accountPaymentDataView);
		} catch (AccountException ae) {
			throw new BulkEntryAccountUpdateException("errors.update", ae,
					new String[] { account.getGlobalAccountNum() });
		} catch (SystemException se) {
			throw new BulkEntryAccountUpdateException("errors.update", se,
					new String[] { account.getGlobalAccountNum() });
		}
	}

	private PaymentData getWithdrawalSavingsPaymentDataView(
			SavingsAccountView savingsAccountView, Integer customerId,
			Short personnelId, String recieptNum, Short paymentId,
			Date receiptDate, Date transactionDate) {
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
			Date receiptDate, Date transactionDate) {
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