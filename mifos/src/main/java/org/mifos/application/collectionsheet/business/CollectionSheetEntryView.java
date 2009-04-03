/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.application.collectionsheet.business;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerAccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.business.View;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Money;

public class CollectionSheetEntryView extends View {

	private boolean hasChild;

	private List<CollectionSheetEntryView> collectionSheetEntryChildren;

	private CustomerView customerDetail;

	private List<LoanAccountsProductView> loanAccountDetails;

	private List<SavingsAccountView> savingsAccountDetails;

	private CustomerAccountView customerAccountDetails;

	private Short attendence;

	private MifosCurrency currency;
    
    private static MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.BULKENTRYLOGGER);

	public CollectionSheetEntryView(CustomerView customerDetail) {
		this.customerDetail = customerDetail;
		loanAccountDetails = new ArrayList<LoanAccountsProductView>();
		savingsAccountDetails = new ArrayList<SavingsAccountView>();
		collectionSheetEntryChildren = new ArrayList<CollectionSheetEntryView>();
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
			{
				return;
			}
		this.savingsAccountDetails.add(savingsAccount);
	}

	public Short getAttendence() {
		return attendence;
	}

	public void setAttendence(Short attendence) {
		this.attendence = attendence;
	}

	public List<CollectionSheetEntryView> getCollectionSheetEntryChildren() {
		return collectionSheetEntryChildren;
	}

	public CustomerView getCustomerDetail() {
		return customerDetail;
	}

	public boolean isHasChild() {
		return hasChild;
	}

	public void addChildNode(CollectionSheetEntryView leafNode) {
		collectionSheetEntryChildren.add(leafNode);
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
			List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
			List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews) {
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
									collectionSheetEntryAccountActionViews,
									collectionSheetEntryAccountFeeActionViews, loan));
				else
					loanAccountView.addTrxnDetails(retrieveLoanSchedule(
							loanAccountView.getAccountId(), customerId,
							collectionSheetEntryAccountActionViews,
							collectionSheetEntryAccountFeeActionViews));
			}
		}
	}

	private LoanAccountView getLoanAccountView(LoanBO loan) {
		return new LoanAccountView(loan.getAccountId(), loan.getLoanOffering()
				.getPrdOfferingShortName(), loan.getType(),
				loan.getLoanOffering().getPrdOfferingId(),
				loan.getState(),
				loan.isInterestDeductedAtDisbursement(), 
				loan.getLoanAmount());
	}

	private Double getAmountPaidAtDisb(LoanAccountView loanAccountView,
			Integer customerId,
			List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
			List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews,
			LoanBO loan) {
		if (loanAccountView.isInterestDeductedAtDisbursement())
			return getInterestAmountDedAtDisb(retrieveLoanSchedule(
					loanAccountView.getAccountId(), customerId,
					collectionSheetEntryAccountActionViews, collectionSheetEntryAccountFeeActionViews));
		else
			return getFeeAmountAtDisb(loan.getAccountFees());
	}

	private Double getInterestAmountDedAtDisb(
			List<CollectionSheetEntryInstallmentView> installments) {
		for (CollectionSheetEntryInstallmentView collectionSheetEntryAccountAction : installments)
			if (collectionSheetEntryAccountAction.getInstallmentId().shortValue() == 1)
				return ((CollectionSheetEntryLoanInstallmentView) collectionSheetEntryAccountAction)
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

	private List<CollectionSheetEntryInstallmentView> retrieveLoanSchedule(
			Integer accountId, Integer customerId,
			List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
			List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews) {
		int index = collectionSheetEntryAccountActionViews
				.indexOf(new CollectionSheetEntryLoanInstallmentView(accountId, customerId));
		int lastIndex = collectionSheetEntryAccountActionViews
				.lastIndexOf(new CollectionSheetEntryLoanInstallmentView(accountId,
						customerId));
		if (lastIndex != -1 && index != -1) {
			List<CollectionSheetEntryInstallmentView> applicableInstallments = collectionSheetEntryAccountActionViews
					.subList(index, lastIndex + 1);
			for (CollectionSheetEntryInstallmentView collectionSheetEntryAccountActionView : applicableInstallments) {
				int feeIndex = collectionSheetEntryAccountFeeActionViews
						.indexOf(new CollectionSheetEntryAccountFeeActionView(
								collectionSheetEntryAccountActionView.getActionDateId()));
				int feeLastIndex = collectionSheetEntryAccountFeeActionViews
						.lastIndexOf(new CollectionSheetEntryAccountFeeActionView(
								collectionSheetEntryAccountActionView.getActionDateId()));
				if (feeIndex != -1 && feeLastIndex != -1)
					((CollectionSheetEntryLoanInstallmentView) collectionSheetEntryAccountActionView)
							.setCollectionSheetEntryAccountFeeActions(collectionSheetEntryAccountFeeActionViews
									.subList(feeIndex, feeLastIndex + 1));
			}
			return applicableInstallments;
		}
		return null;
	}

	private List<CollectionSheetEntryInstallmentView> retrieveCustomerSchedule(
			Integer accountId, Integer customerId,
			List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
			List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews) {
		int index = collectionSheetEntryAccountActionViews
				.indexOf(new CollectionSheetEntryCustomerAccountInstallmentView(accountId,
						customerId));
		int lastIndex = collectionSheetEntryAccountActionViews
				.lastIndexOf(new CollectionSheetEntryCustomerAccountInstallmentView(
						accountId, customerId));
		if (lastIndex != -1 && index != -1) {
			List<CollectionSheetEntryInstallmentView> applicableInstallments = collectionSheetEntryAccountActionViews
					.subList(index, lastIndex + 1);
			for (CollectionSheetEntryInstallmentView collectionSheetEntryAccountActionView : applicableInstallments) {
				int feeIndex = collectionSheetEntryAccountFeeActionViews
						.indexOf(new CollectionSheetEntryAccountFeeActionView(
								collectionSheetEntryAccountActionView.getActionDateId()));
				int feeLastIndex = collectionSheetEntryAccountFeeActionViews
						.lastIndexOf(new CollectionSheetEntryAccountFeeActionView(
								collectionSheetEntryAccountActionView.getActionDateId()));
				if (feeIndex != -1 && feeLastIndex != -1)
					((CollectionSheetEntryCustomerAccountInstallmentView) collectionSheetEntryAccountActionView)
							.setCollectionSheetEntryAccountFeeActions(collectionSheetEntryAccountFeeActionViews
									.subList(feeIndex, feeLastIndex + 1));
			}
			return applicableInstallments;
		}
		return null;
	}

	public void populateCustomerAccountInformation(CustomerBO customer,
			List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
			List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews) {
		CustomerAccountBO customerAccount = customer.getCustomerAccount();
		CustomerAccountView customerAccountView = new CustomerAccountView(
				customerAccount.getAccountId());
		customerAccountView.setAccountActionDates(retrieveCustomerSchedule(
				customerAccount.getAccountId(), customer.getCustomerId(),
				collectionSheetEntryAccountActionViews, collectionSheetEntryAccountFeeActionViews));
		setCustomerAccountDetails(customerAccountView);
	}

	public void populateSavingsAccountsInformation(CustomerBO customer) {
		List<SavingsAccountView> savingsAccounts = getSavingsAccountViews(customer);
		if (customerDetail.isCustomerCenter()) {
			for (CollectionSheetEntryView child : collectionSheetEntryChildren) {
				addSavingsAccountViewToClients(child.getCollectionSheetEntryChildren(),
						savingsAccounts);
			}
		} else if (customerDetail.isCustomerGroup()) {
			addSavingsAccountViewToClients(collectionSheetEntryChildren, savingsAccounts);
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
				// kim, check if account is active
				if (savingsAccount.getAccountState().getId().equals(AccountState.SAVINGS_ACTIVE.getValue()))
				{
					SavingsAccountView savingsAccountView = getSavingsAccountView(savingsAccount);
					savingsAccounts.add(savingsAccountView);
				}
			}
		return savingsAccounts;
	}

	private SavingsAccountView getSavingsAccountView(SavingsBO savingsAccount) {
		return new SavingsAccountView(savingsAccount.getAccountId(),
				savingsAccount.getType(),
				savingsAccount.getSavingsOffering());

	}

	private SavingsAccountView getSavingsAccountView(
			SavingsAccountView savingsAccountView) {
		return new SavingsAccountView(savingsAccountView.getAccountId(),
				savingsAccountView.getType(), savingsAccountView
						.getSavingsOffering());

	}

	private void addSavingsAccountViewToClients(
			List<CollectionSheetEntryView> clientCollectionSheetEntryViews,
			List<SavingsAccountView> savingsAccountViews) {
		for (CollectionSheetEntryView collectionSheetEntryView : clientCollectionSheetEntryViews) {
			for (SavingsAccountView savingsAccountView : savingsAccountViews) {
				collectionSheetEntryView
						.addSavingsAccountDetail(getSavingsAccountView(savingsAccountView));
			}
		}
	}

	public void populateSavingsAccountActions(Integer customerId,
			Date transactionDate,
			List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews) {
		if (customerDetail.isCustomerCenter())
			return;
		for (SavingsAccountView savingsAccountView : savingsAccountDetails) {
			if (!(customerDetail.isCustomerGroup() && savingsAccountView
					.getSavingsOffering().getRecommendedAmntUnit()
					.getId().equals(RecommendedAmountUnit.PER_INDIVIDUAL.getValue()))) {
				addAccountActionToSavingsView(savingsAccountView, customerId,
						transactionDate, collectionSheetEntryAccountActionViews);
			}
		}
	}
	
    public void populateClientAttendance(Integer customerId, Date transactionDate,
            List<ClientAttendanceDto> collectionSheetEntryClientAttendanceViews) {
        if (customerDetail.isCustomerCenter())
            return;
        for (ClientAttendanceDto clientAttendanceView : collectionSheetEntryClientAttendanceViews) {
            logger.debug("populateClientAttendance");
            logger.debug("clientAttendanceView.getCustomerId() " + clientAttendanceView.getClientId());
            logger.debug("customerId " + customerId);
            logger.debug("customerDetail.getCustomerId() " + customerDetail.getCustomerId());
            if (clientAttendanceView.getClientId().compareTo(customerId) == 0) {
                Short attendanceId = clientAttendanceView.getAttendanceId();
                setAttendence(attendanceId);
            }
        }
    }
   
   
	private void addAccountActionToSavingsView(
			SavingsAccountView savingsAccountView, Integer customerId,
			Date transactionDate,
			List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews) {
		boolean isMandatory = false;
		if (savingsAccountView.getSavingsOffering().getSavingsType()
				.getId()
				.equals(SavingsType.MANDATORY.getValue()))
			isMandatory = true;
		List<CollectionSheetEntryInstallmentView> accountActionDetails = retrieveSavingsAccountActions(
				savingsAccountView.getAccountId(), customerId,
				collectionSheetEntryAccountActionViews, isMandatory);
		if (accountActionDetails != null)
			for (CollectionSheetEntryInstallmentView accountAction : accountActionDetails) {
				savingsAccountView.addAccountTrxnDetail(accountAction);
			}
	}

	private List<CollectionSheetEntryInstallmentView> retrieveSavingsAccountActions(
			Integer accountId, Integer customerId,
			List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
			boolean isMandatory) {
		int index = collectionSheetEntryAccountActionViews
				.indexOf(new CollectionSheetEntrySavingsInstallmentView(accountId,
						customerId));
		if (!isMandatory && index != -1) {
			return collectionSheetEntryAccountActionViews.subList(index, index + 1);
		}
		int lastIndex = collectionSheetEntryAccountActionViews
				.lastIndexOf(new CollectionSheetEntrySavingsInstallmentView(accountId,
						customerId));
		if (lastIndex != -1 && index != -1)
			return collectionSheetEntryAccountActionViews.subList(index, lastIndex + 1);
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
						LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(depositAmountEnteredValue);
						savingsAccountView.setValidDepositAmountEntered(true);
					}
				} catch (NumberFormatException nfe) {
					savingsAccountView.setValidDepositAmountEntered(false);
				}
				try {
					if (withDrawalAmountEnteredValue != null
							&& !"".equals(withDrawalAmountEnteredValue.trim())) {
						LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(withDrawalAmountEnteredValue);
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
						LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(enteredAmountValue);
					}
					loanAccountView.setValidAmountEntered(true);
				} catch (NumberFormatException ne) {
					loanAccountView.setValidAmountEntered(false);
				}
				try {
					if (null != disbursementAmount) {
						LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(disbursementAmount);
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
			LocalizationConverter.getInstance().getDoubleValueForCurrentLocale(customerAccountAmountEntered);
			customerAccountDetails.setValidCustomerAccountAmountEntered(true);
		} catch (NumberFormatException nfe) {
			customerAccountDetails.setValidCustomerAccountAmountEntered(false);
		}
	}
}
