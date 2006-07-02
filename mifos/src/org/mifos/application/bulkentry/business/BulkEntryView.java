/**

 * BulkEntryView.java    version: 1.0

 

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

package org.mifos.application.bulkentry.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.business.LoanAccountsProductView;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.bulkentry.business.service.BulkEntryBusinessService;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.business.View;
import org.mifos.framework.components.configuration.business.Configuration;

/**
 * @author rohitr
 * 
 */
public class BulkEntryView extends View {

	private BulkEntryBusinessService businessService;

	private boolean hasChild;

	private List<BulkEntryView> bulkEntryChildren;

	private CustomerView customerDetail;

	private List<LoanAccountsProductView> loanAccountDetails;

	private List<SavingsAccountView> savingsAccountDetails;

	private CustomerAccountView customerAccountDetails;

	private String attendence;

	private MifosCurrency currency;

	public BulkEntryView(CustomerView customerDetail) {
		this.customerDetail = customerDetail;
		loanAccountDetails = new ArrayList<LoanAccountsProductView>();
		savingsAccountDetails = new ArrayList<SavingsAccountView>();
		bulkEntryChildren = new ArrayList<BulkEntryView>();
		businessService = new BulkEntryBusinessService();
		currency = Configuration.getInstance().getSystemConfig().getCurrency();
	}

	public List<LoanAccountsProductView> getLoanAccountDetails() {
		return loanAccountDetails;
	}

	public void addLoanAccountDetails(LoanAccountView loanAccount) {
		for (LoanAccountsProductView loanAccountProductView : loanAccountDetails)
			if (isProductExists(loanAccount, loanAccountProductView)) {
				loanAccountProductView.addLoanAccountView(loanAccount);
				return;
			}
		LoanAccountsProductView loanAccountProductView = new LoanAccountsProductView(
				loanAccount.getPrdOfferingId(), loanAccount
						.getPrdOfferingShortName());
		loanAccountProductView.addLoanAccountView(loanAccount);
		this.loanAccountDetails.add(loanAccountProductView);
	}

	private boolean isProductExists(LoanAccountView loanAccount,
			LoanAccountsProductView loanAccountProductView) {
		if (loanAccountProductView.getPrdOfferingId() != null
				&& loanAccountProductView.getPrdOfferingId().equals(
						loanAccount.getPrdOfferingId()))
			return true;
		return false;

	}

	public List<SavingsAccountView> getSavingsAccountDetails() {
		return savingsAccountDetails;
	}

	public void addSavingsAccountDetail(SavingsAccountView savingsAccount) {
		for (SavingsAccountView savingsAccountView : savingsAccountDetails)
			if (savingsAccount.getSavingsOffering().getPrdOfferingId().equals(
					savingsAccountView.getSavingsOffering().getPrdOfferingId()))
				return;
		this.savingsAccountDetails.add(savingsAccount);
	}

	public String getAttendence() {
		return attendence;
	}

	public void setAttendence(String attendence) {
		this.attendence = attendence;
	}

	public List<BulkEntryView> getBulkEntryChildren() {
		return bulkEntryChildren;
	}

	public CustomerView getCustomerDetail() {
		return customerDetail;
	}

	public boolean isHasChild() {
		return hasChild;
	}

	public void addChildNode(BulkEntryView leafNode) {
		bulkEntryChildren.add(leafNode);
		this.hasChild = true;
	}

	public MifosCurrency getCurrency() {
		return currency;
	}

	public CustomerAccountView getCustomerAccountDetails() {
		return customerAccountDetails;
	}

	public void setCustomerAccountDetails(
			CustomerAccountView customerAccountDetails) {
		this.customerAccountDetails = customerAccountDetails;
	}

	public void populate(Date transactionDate) {
		Integer customerId = customerDetail.getCustomerId();
		List<LoanAccountView> loanAccounts = retrieveAccountInformation(
				customerId, transactionDate);
		for (LoanAccountView accountView : loanAccounts) {
			if (accountView.getAccountSate().shortValue() == AccountStates.LOANACC_APPROVED
					|| accountView.getAccountSate().shortValue() == AccountStates.LOANACC_DBTOLOANOFFICER) {

				if (accountView.isInterestDeductedAtDisbursement()) {
					List<AccountActionDateEntity> installments = retrieveLoanAccountTransactionDetail(
							accountView.getAccountId(), transactionDate);
					// first intallment will be interest or any fee to be
					// deducted
					for (AccountActionDateEntity entity : installments) {
						if (entity.getInstallmentId().shortValue() == 1) {
							accountView.setAmountPaidAtDisbursement(entity
									.getTotalDueWithFees()
									.getAmountDoubleValue());
							break;
						}

					}
				} else {
					accountView.setAmountPaidAtDisbursement(businessService
							.getFeeAmountAtDisbursement(accountView
									.getAccountId(), transactionDate));
				}
				continue;
			}
			accountView.addTrxnDetails(retrieveLoanAccountTransactionDetail(
					accountView.getAccountId(), transactionDate));
		}
		for (LoanAccountView loanAccountView : loanAccounts)
			addLoanAccountDetails(loanAccountView);

		populateSavingsAccountInformation(customerId);

		populateCustomerAccountInformation(customerId, transactionDate);
	}

	private List<LoanAccountView> retrieveAccountInformation(
			Integer customerId, Date disbursementDate) {
		return businessService.retrieveAccountInformationForCustomer(
				customerId, disbursementDate);
	}

	private List<AccountActionDateEntity> retrieveLoanAccountTransactionDetail(
			Integer accountId, Date transactionDate) {
		return businessService.retrieveLoanAccountTransactionDetail(accountId,
				transactionDate);
	}

	private void populateCustomerAccountInformation(Integer customerId,
			Date transactionDate) {
		CustomerBO customer = businessService
				.retrieveCustomerAccountInfo(customerId);
		CustomerAccountView customerAccountView = new CustomerAccountView(
				customer.getCustomerAccount().getAccountId());
		customerAccountView.setAccountActionDates(businessService
				.retrieveCustomerAccountActionDetails(customerAccountView
						.getAccountId(), transactionDate));
		setCustomerAccountDetails(customerAccountView);
	}

	private void populateSavingsAccountInformation(Integer customerId) {
		List<SavingsAccountView> savingsAccounts = businessService
				.retrieveSavingsAccountInformationForCustomer(customerId);

		if (customerDetail.getCustomerLevelId().equals(
				CustomerConstants.CENTER_LEVEL_ID)) {
			for (BulkEntryView child : bulkEntryChildren) {
				addSavingsAccountView(child.getBulkEntryChildren(),
						savingsAccounts);
			}
		} else if (customerDetail.getCustomerLevelId().equals(
				CustomerConstants.GROUP_LEVEL_ID)) {
			addSavingsAccountView(bulkEntryChildren, savingsAccounts);
		}
		for (SavingsAccountView savingsAccountView : savingsAccounts)
			addSavingsAccountDetail(savingsAccountView);
	}

	private void addSavingsAccountView(List<BulkEntryView> parent,
			List<SavingsAccountView> savingsAccountViews) {
		for (BulkEntryView bulkEntryView : parent) {
			for (SavingsAccountView savingsAccountView : savingsAccountViews) {
				SavingsAccountView savings = new SavingsAccountView(
						savingsAccountView.getAccountId(), savingsAccountView
								.getAccountType(), savingsAccountView
								.getSavingsOffering());
				bulkEntryView.addSavingsAccountDetail(savings);
			}
		}
	}

	public void populateSavingsAccountActions(Integer customerId,
			Date transactionDate) {
		if (customerDetail.getCustomerLevelId().equals(
				CustomerConstants.CENTER_LEVEL_ID)) {
			return;
		}
		for (SavingsAccountView savingsAccountView : savingsAccountDetails) {
			if (!(customerDetail.getCustomerLevelId().equals(
					CustomerConstants.GROUP_LEVEL_ID) && savingsAccountView
					.getSavingsOffering().getRecommendedAmntUnit()
					.getRecommendedAmntUnitId().equals(
							ProductDefinitionConstants.PERINDIVIDUAL))) {
				addAccountActionToSavingsView(savingsAccountView, customerId,
						transactionDate);
			}
		}

	}

	private void addAccountActionToSavingsView(
			SavingsAccountView savingsAccountView, Integer customerId,
			Date transactionDate) {
		boolean isMandatory = false;
		if (savingsAccountView.getSavingsOffering().getSavingsType()
				.getSavingsTypeId()
				.equals(ProductDefinitionConstants.MANDATORY))
			isMandatory = true;
		List<AccountActionDateEntity> accountActionDetails = retrieveSavingsAccountActions(
				savingsAccountView.getAccountId(), customerId, transactionDate,
				isMandatory);
		for (AccountActionDateEntity accountAction : accountActionDetails) {
			savingsAccountView.addAccountTrxnDetail(accountAction);
		}
	}

	private List<AccountActionDateEntity> retrieveSavingsAccountActions(
			Integer accountId, Integer customerId, Date transactionDate,
			boolean isMandatory) {
		return businessService.retrieveSavingsAccountTransactionDetail(
				accountId, customerId, transactionDate, isMandatory);
	}

	public void setSavinsgAmountsEntered(Short prdOfferingId,
			String depositAmountEnteredValue,
			String withDrawalAmountEnteredValue) {
		for (SavingsAccountView savingsAccountView : savingsAccountDetails) {
			if (prdOfferingId.equals(savingsAccountView.getSavingsOffering()
					.getPrdOfferingId())) {
				savingsAccountView
						.setDepositAmountEntered(depositAmountEnteredValue);
				savingsAccountView
						.setWithDrawalAmountEntered(withDrawalAmountEnteredValue);
				try {
					if (depositAmountEnteredValue != null
							&& !"".equals(depositAmountEnteredValue.trim())) {
						Double.valueOf(depositAmountEnteredValue);
						savingsAccountView.setValidDepositAmountEntered(true);
					}
				} catch (NumberFormatException nfe) {
					savingsAccountView.setValidDepositAmountEntered(false);
				}
				try {
					if (withDrawalAmountEnteredValue != null
							&& !"".equals(withDrawalAmountEnteredValue.trim())) {
						Double.valueOf(withDrawalAmountEnteredValue);
						savingsAccountView
								.setValidWithDrawalAmountEntered(true);
					}
				} catch (NumberFormatException nfe) {
					savingsAccountView.setValidWithDrawalAmountEntered(false);
				}
			}
		}
	}

	public void setLoanAmountsEntered(Short prdOfferingId,
			String enteredAmountValue, String disbursementAmount) {
		for (LoanAccountsProductView loanAccountView : loanAccountDetails) {
			if (prdOfferingId.equals(loanAccountView.getPrdOfferingId())) {
				loanAccountView.setEnteredAmount(enteredAmountValue);
				loanAccountView
						.setDisBursementAmountEntered(disbursementAmount);
				try {
					if (null != enteredAmountValue) {
						Double.valueOf(enteredAmountValue);
					}
					loanAccountView.setValidAmountEntered(true);
				} catch (NumberFormatException ne) {
					loanAccountView.setValidAmountEntered(false);
				}
				try {
					if (null != disbursementAmount) {
						Double.valueOf(disbursementAmount);
					}
					loanAccountView.setValidDisbursementAmount(true);
				} catch (NumberFormatException ne) {
					loanAccountView.setValidDisbursementAmount(false);
				}

			}
		}
	}

	public void setCustomerAccountAmountEntered(
			String customerAccountAmountEntered) {
		customerAccountDetails
				.setCustomerAccountAmountEntered(customerAccountAmountEntered);
		try {
			Double.valueOf(customerAccountAmountEntered);
			customerAccountDetails.setValidCustomerAccountAmountEntered(true);
		} catch (NumberFormatException nfe) {
			customerAccountDetails.setValidCustomerAccountAmountEntered(false);
		}
	}
}
