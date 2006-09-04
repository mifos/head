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
import java.util.Set;

import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.CustomerAccountBO;
import org.mifos.application.accounts.business.CustomerAccountView;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.business.LoanAccountsProductView;
import org.mifos.application.accounts.business.SavingsAccountView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.business.View;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.util.helpers.Money;

public class BulkEntryView extends View {

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

	public void populateLoanAccountsInformation(CustomerBO customer,
			Date transactionDate,
			List<BulkEntryInstallmentView> bulkEntryAccountActionViews,
			List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActionViews) {
		Integer customerId = customerDetail.getCustomerId();
		List<LoanBO> customerLoanAccounts = customer
				.getActiveAndApprovedLoanAccounts(transactionDate);
		if (customerLoanAccounts != null && customerLoanAccounts.size() > 0) {
			for (LoanBO loan : customerLoanAccounts) {
				LoanAccountView loanAccountView = getLoanAccountView(loan);
				addLoanAccountDetails(loanAccountView);
				if (loanAccountView.isDisbursalAccount())
					loanAccountView
							.setAmountPaidAtDisbursement(getAmountPaidAtDisb(
									loanAccountView, customerId,
									bulkEntryAccountActionViews,
									bulkEntryAccountFeeActionViews, loan));
				else
					loanAccountView.addTrxnDetails(retrieveLoanSchedule(
							loanAccountView.getAccountId(), customerId,
							bulkEntryAccountActionViews,
							bulkEntryAccountFeeActionViews));
			}
		}
	}

	private LoanAccountView getLoanAccountView(LoanBO loan) {
		Short interestDedAtDisb = loan.isInterestDeductedAtDisbursement() ? 
				LoanConstants.INTEREST_DEDUCTED_AT_DISBURSMENT : (short)0;
		return new LoanAccountView(loan.getAccountId(), loan.getLoanOffering()
				.getPrdOfferingShortName(), loan.getAccountType()
				.getAccountTypeId(), loan.getLoanOffering().getPrdOfferingId(),
				loan.getAccountState().getId(),
				interestDedAtDisb, loan.getLoanAmount());
	}

	private Double getAmountPaidAtDisb(LoanAccountView loanAccountView,
			Integer customerId,
			List<BulkEntryInstallmentView> bulkEntryAccountActionViews,
			List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActionViews,
			LoanBO loan) {
		if (loanAccountView.isInterestDeductedAtDisbursement())
			return getInterestAmountDedAtDisb(retrieveLoanSchedule(
					loanAccountView.getAccountId(), customerId,
					bulkEntryAccountActionViews, bulkEntryAccountFeeActionViews));
		else
			return getFeeAmountAtDisb(loan.getAccountFees());
	}

	private Double getInterestAmountDedAtDisb(
			List<BulkEntryInstallmentView> installments) {
		for (BulkEntryInstallmentView bulkEntryAccountAction : installments)
			if (bulkEntryAccountAction.getInstallmentId().shortValue() == 1)
				return ((BulkEntryLoanInstallmentView) bulkEntryAccountAction)
						.getTotalDueWithFees().getAmountDoubleValue();
		return 0.0;
	}

	private Double getFeeAmountAtDisb(Set<AccountFeesEntity> accountFees) {
		Money feeAtDisbursement = new Money();
		for (AccountFeesEntity entity : accountFees) {
			if (entity.isTimeOfDisbursement())
				feeAtDisbursement = feeAtDisbursement.add(entity
						.getAccountFeeAmount());
		}
		return feeAtDisbursement.getAmountDoubleValue();
	}

	private List<BulkEntryInstallmentView> retrieveLoanSchedule(
			Integer accountId, Integer customerId,
			List<BulkEntryInstallmentView> bulkEntryAccountActionViews,
			List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActionViews) {
		int index = bulkEntryAccountActionViews
				.indexOf(new BulkEntryLoanInstallmentView(accountId, customerId));
		int lastIndex = bulkEntryAccountActionViews
				.lastIndexOf(new BulkEntryLoanInstallmentView(accountId,
						customerId));
		if (lastIndex != -1 && index != -1) {
			List<BulkEntryInstallmentView> applicableInstallments = bulkEntryAccountActionViews
					.subList(index, lastIndex + 1);
			for (BulkEntryInstallmentView bulkEntryAccountActionView : applicableInstallments) {
				int feeIndex = bulkEntryAccountFeeActionViews
						.indexOf(new BulkEntryAccountFeeActionView(
								bulkEntryAccountActionView.getActionDateId()));
				int feeLastIndex = bulkEntryAccountFeeActionViews
						.lastIndexOf(new BulkEntryAccountFeeActionView(
								bulkEntryAccountActionView.getActionDateId()));
				if (feeIndex != -1 && feeLastIndex != -1)
					((BulkEntryLoanInstallmentView) bulkEntryAccountActionView)
							.setBulkEntryAccountFeeActions(bulkEntryAccountFeeActionViews
									.subList(feeIndex, feeLastIndex + 1));
			}
			return applicableInstallments;
		}
		return null;
	}

	private List<BulkEntryInstallmentView> retrieveCustomerSchedule(
			Integer accountId, Integer customerId,
			List<BulkEntryInstallmentView> bulkEntryAccountActionViews,
			List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActionViews) {
		int index = bulkEntryAccountActionViews
				.indexOf(new BulkEntryCustomerAccountInstallmentView(accountId,
						customerId));
		int lastIndex = bulkEntryAccountActionViews
				.lastIndexOf(new BulkEntryCustomerAccountInstallmentView(
						accountId, customerId));
		if (lastIndex != -1 && index != -1) {
			List<BulkEntryInstallmentView> applicableInstallments = bulkEntryAccountActionViews
					.subList(index, lastIndex + 1);
			for (BulkEntryInstallmentView bulkEntryAccountActionView : applicableInstallments) {
				int feeIndex = bulkEntryAccountFeeActionViews
						.indexOf(new BulkEntryAccountFeeActionView(
								bulkEntryAccountActionView.getActionDateId()));
				int feeLastIndex = bulkEntryAccountFeeActionViews
						.lastIndexOf(new BulkEntryAccountFeeActionView(
								bulkEntryAccountActionView.getActionDateId()));
				if (feeIndex != -1 && feeLastIndex != -1)
					((BulkEntryCustomerAccountInstallmentView) bulkEntryAccountActionView)
							.setBulkEntryAccountFeeActions(bulkEntryAccountFeeActionViews
									.subList(feeIndex, feeLastIndex + 1));
			}
			return applicableInstallments;
		}
		return null;
	}

	public void populateCustomerAccountInformation(CustomerBO customer,
			List<BulkEntryInstallmentView> bulkEntryAccountActionViews,
			List<BulkEntryAccountFeeActionView> bulkEntryAccountFeeActionViews) {
		CustomerAccountBO customerAccount = customer.getCustomerAccount();
		CustomerAccountView customerAccountView = new CustomerAccountView(
				customerAccount.getAccountId());
		customerAccountView.setAccountActionDates(retrieveCustomerSchedule(
				customerAccount.getAccountId(), customer.getCustomerId(),
				bulkEntryAccountActionViews, bulkEntryAccountFeeActionViews));
		setCustomerAccountDetails(customerAccountView);
	}

	public void populateSavingsAccountsInformation(CustomerBO customer) {
		List<SavingsAccountView> savingsAccounts = getSavingsAccountViews(customer);
		if (customerDetail.isCustomerCenter()) {
			for (BulkEntryView child : bulkEntryChildren) {
				addSavingsAccountViewToClients(child.getBulkEntryChildren(),
						savingsAccounts);
			}
		} else if (customerDetail.isCustomerGroup()) {
			addSavingsAccountViewToClients(bulkEntryChildren, savingsAccounts);
		}
		for (SavingsAccountView savingsAccountView : savingsAccounts)
			addSavingsAccountDetail(savingsAccountView);
	}

	private List<SavingsAccountView> getSavingsAccountViews(CustomerBO customer) {
		List<SavingsBO> customerSavingsAccounts = customer
				.getActiveSavingsAccounts();
		List<SavingsAccountView> savingsAccounts = new ArrayList<SavingsAccountView>();
		if (customerSavingsAccounts != null
				&& customerSavingsAccounts.size() > 0)
			for (SavingsBO savingsAccount : customerSavingsAccounts) {
				SavingsAccountView savingsAccountView = getSavingsAccountView(savingsAccount);
				savingsAccounts.add(savingsAccountView);
			}
		return savingsAccounts;
	}

	private SavingsAccountView getSavingsAccountView(SavingsBO savingsAccount) {
		return new SavingsAccountView(savingsAccount.getAccountId(),
				savingsAccount.getAccountType().getAccountTypeId(),
				savingsAccount.getSavingsOffering());

	}

	private SavingsAccountView getSavingsAccountView(
			SavingsAccountView savingsAccountView) {
		return new SavingsAccountView(savingsAccountView.getAccountId(),
				savingsAccountView.getAccountType(), savingsAccountView
						.getSavingsOffering());

	}

	private void addSavingsAccountViewToClients(
			List<BulkEntryView> clientBulkEntryViews,
			List<SavingsAccountView> savingsAccountViews) {
		for (BulkEntryView bulkEntryView : clientBulkEntryViews) {
			for (SavingsAccountView savingsAccountView : savingsAccountViews) {
				bulkEntryView
						.addSavingsAccountDetail(getSavingsAccountView(savingsAccountView));
			}
		}
	}

	public void populateSavingsAccountActions(Integer customerId,
			Date transactionDate,
			List<BulkEntryInstallmentView> bulkEntryAccountActionViews) {
		if (customerDetail.isCustomerCenter())
			return;
		for (SavingsAccountView savingsAccountView : savingsAccountDetails) {
			if (!(customerDetail.isCustomerGroup() && savingsAccountView
					.getSavingsOffering().getRecommendedAmntUnit()
					.getId().equals(RecommendedAmountUnit.PERINDIVIDUAL.getValue()))) {
				addAccountActionToSavingsView(savingsAccountView, customerId,
						transactionDate, bulkEntryAccountActionViews);
			}
		}
	}

	private void addAccountActionToSavingsView(
			SavingsAccountView savingsAccountView, Integer customerId,
			Date transactionDate,
			List<BulkEntryInstallmentView> bulkEntryAccountActionViews) {
		boolean isMandatory = false;
		if (savingsAccountView.getSavingsOffering().getSavingsType()
				.getId()
				.equals(SavingsType.MANDATORY.getValue()))
			isMandatory = true;
		List<BulkEntryInstallmentView> accountActionDetails = retrieveSavingsAccountActions(
				savingsAccountView.getAccountId(), customerId,
				bulkEntryAccountActionViews, isMandatory);
		if (accountActionDetails != null)
			for (BulkEntryInstallmentView accountAction : accountActionDetails) {
				savingsAccountView.addAccountTrxnDetail(accountAction);
			}
	}

	private List<BulkEntryInstallmentView> retrieveSavingsAccountActions(
			Integer accountId, Integer customerId,
			List<BulkEntryInstallmentView> bulkEntryAccountActionViews,
			boolean isMandatory) {
		int index = bulkEntryAccountActionViews
				.indexOf(new BulkEntrySavingsInstallmentView(accountId,
						customerId));
		if (!isMandatory && index != -1) {
			return bulkEntryAccountActionViews.subList(index, index + 1);
		}
		int lastIndex = bulkEntryAccountActionViews
				.lastIndexOf(new BulkEntrySavingsInstallmentView(accountId,
						customerId));
		if (lastIndex != -1 && index != -1)
			return bulkEntryAccountActionViews.subList(index, lastIndex + 1);
		return null;
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
