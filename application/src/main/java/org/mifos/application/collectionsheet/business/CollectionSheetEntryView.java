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

import java.util.ArrayList;
import java.util.List;

import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.customers.business.CustomerView;
import org.mifos.customers.client.business.service.ClientAttendanceDto;
import org.mifos.customers.util.helpers.CustomerAccountView;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.business.View;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Money;

public class CollectionSheetEntryView extends View {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER);

    private boolean hasChild;

    private final List<CollectionSheetEntryView> collectionSheetEntryChildren;

    private final CustomerView customerDetail;

    private final List<LoanAccountsProductView> loanAccountDetails;

    private final List<SavingsAccountView> savingsAccountDetails;

    private CustomerAccountView customerAccountDetails;

    private Short attendence;

    private final MifosCurrency currency;

    private int countOfCustomers;

    public CollectionSheetEntryView(final CustomerView customerDetail, final MifosCurrency currency) {
        this.customerDetail = customerDetail;
        loanAccountDetails = new ArrayList<LoanAccountsProductView>();
        savingsAccountDetails = new ArrayList<SavingsAccountView>();
        collectionSheetEntryChildren = new ArrayList<CollectionSheetEntryView>();
        this.currency = currency;
    }

    public List<LoanAccountsProductView> getLoanAccountDetails() {
        return loanAccountDetails;
    }

    public void addLoanAccountDetails(final LoanAccountView loanAccount) {
        for (LoanAccountsProductView loanAccountProductView : loanAccountDetails) {
            if (isProductExists(loanAccount, loanAccountProductView)) {
                loanAccountProductView.addLoanAccountView(loanAccount);
                return;
            }
        }
        LoanAccountsProductView loanAccountProductView = new LoanAccountsProductView(loanAccount.getPrdOfferingId(),
                loanAccount.getPrdOfferingShortName());
        loanAccountProductView.addLoanAccountView(loanAccount);
        this.loanAccountDetails.add(loanAccountProductView);
    }

    public List<SavingsAccountView> getSavingsAccountDetails() {
        return savingsAccountDetails;
    }

    public void addSavingsAccountDetail(final SavingsAccountView savingsAccount) {
        for (SavingsAccountView savingsAccountView : savingsAccountDetails) {
            if (savingsAccount.getSavingsOfferingId().equals(savingsAccountView.getSavingsOfferingId())) {
                return;
            }
        }
        this.savingsAccountDetails.add(savingsAccount);
    }

    public Short getAttendence() {
        return attendence;
    }

    public void setAttendence(final Short attendence) {
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

    public void addChildNode(final CollectionSheetEntryView leafNode) {
        collectionSheetEntryChildren.add(leafNode);
        this.hasChild = true;
    }

    public MifosCurrency getCurrency() {
        return currency;
    }

    public CustomerAccountView getCustomerAccountDetails() {
        return customerAccountDetails;
    }

    public void setCustomerAccountDetails(final CustomerAccountView customerAccountDetails) {
        this.customerAccountDetails = customerAccountDetails;
    }

    public void populateLoanAccountsInformation(final List<LoanAccountView> loanAccountViewList,
            final List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
            final List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews) {

        final Integer customerId = customerDetail.getCustomerId();

        List<LoanAccountView> loanAccountsForThisCustomer = new ArrayList<LoanAccountView>();
        for (LoanAccountView loanAccountView : loanAccountViewList) {
            if (customerId.equals(loanAccountView.getCustomerId())) {
                loanAccountsForThisCustomer.add(loanAccountView);
            }
        }

        for (LoanAccountView loanAccountView : loanAccountsForThisCustomer) {
            addLoanAccountDetails(loanAccountView);
            if (loanAccountView.isDisbursalAccount()) {
                loanAccountView.setAmountPaidAtDisbursement(getAmountPaidAtDisb(loanAccountView, customerId,
                        collectionSheetEntryAccountActionViews, collectionSheetEntryAccountFeeActionViews));
            } else {
                loanAccountView.addTrxnDetails(retrieveLoanSchedule(loanAccountView.getAccountId(), customerId,
                        collectionSheetEntryAccountActionViews, collectionSheetEntryAccountFeeActionViews));
            }
        }
    }

    public void populateCustomerAccountInformation(final List<CustomerAccountView> customerAccountList,
            final List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
            final List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews) {

        Integer accountId = Integer.valueOf(0);
        for (CustomerAccountView customerAccountView : customerAccountList) {
            if (customerDetail.getCustomerId().equals(customerAccountView.getCustomerId())) {

                accountId = customerAccountView.getAccountId();
                customerAccountView.setAccountActionDates(retrieveCustomerSchedule(accountId, customerAccountView
                        .getCustomerId(), collectionSheetEntryAccountActionViews,
                        collectionSheetEntryAccountFeeActionViews));
                setCustomerAccountDetails(customerAccountView);

            }
        }

    }

    public void populateSavingsAccountsInformation(final List<SavingsAccountView> savingsAccountViewList) {

        final List<SavingsAccountView> savingsAccounts = new ArrayList<SavingsAccountView>();
        for (SavingsAccountView savingsAccountView : savingsAccountViewList) {
            if (savingsAccountView.getCustomerId().equals(customerDetail.getCustomerId())) {
                savingsAccounts.add(savingsAccountView);
            }
        }

        if (customerDetail.isCustomerCenter()) {
            for (CollectionSheetEntryView child : collectionSheetEntryChildren) {
                addSavingsAccountViewToClients(child.getCollectionSheetEntryChildren(), savingsAccounts);
            }
        } else if (customerDetail.isCustomerGroup()) {
            addSavingsAccountViewToClients(collectionSheetEntryChildren, savingsAccounts);
        }

        for (SavingsAccountView savingsAccountView : savingsAccounts) {
            addSavingsAccountDetail(savingsAccountView);
        }
    }

    public void populateSavingsAccountActions(final Integer customerId,
            final List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews) {
        if (customerDetail.isCustomerCenter()) {
            return;
        }
        for (SavingsAccountView savingsAccountView : savingsAccountDetails) {
            if (!(customerDetail.isCustomerGroup() && savingsAccountView.getRecommendedAmntUnitId().equals(
                    RecommendedAmountUnit.PER_INDIVIDUAL.getValue()))) {
                addAccountActionToSavingsView(savingsAccountView, customerId, collectionSheetEntryAccountActionViews);
            }
        }
    }

    public void populateClientAttendance(final Integer customerId,
            final List<ClientAttendanceDto> collectionSheetEntryClientAttendanceViews) {
        if (customerDetail.isCustomerCenter()) {
            return;
        }
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

    public void setSavinsgAmountsEntered(final Short prdOfferingId, final String depositAmountEnteredValue,
            final String withDrawalAmountEnteredValue) {
        for (SavingsAccountView savingsAccountView : savingsAccountDetails) {
            if (prdOfferingId.equals(savingsAccountView.getSavingsOfferingId())) {
                savingsAccountView.setDepositAmountEntered(depositAmountEnteredValue);
                savingsAccountView.setWithDrawalAmountEntered(withDrawalAmountEnteredValue);
                try {
                    if (depositAmountEnteredValue != null && !"".equals(depositAmountEnteredValue.trim())) {
                        new LocalizationConverter().getDoubleValueForCurrentLocale(depositAmountEnteredValue);
                        savingsAccountView.setValidDepositAmountEntered(true);
                    }
                } catch (NumberFormatException nfe) {
                    savingsAccountView.setValidDepositAmountEntered(false);
                }
                try {
                    if (withDrawalAmountEnteredValue != null && !"".equals(withDrawalAmountEnteredValue.trim())) {
                        new LocalizationConverter()
                                .getDoubleValueForCurrentLocale(withDrawalAmountEnteredValue);
                        savingsAccountView.setValidWithDrawalAmountEntered(true);
                    }
                } catch (NumberFormatException nfe) {
                    savingsAccountView.setValidWithDrawalAmountEntered(false);
                }
            }
        }
    }

    public void setLoanAmountsEntered(final Short prdOfferingId, final String enteredAmountValue, final String disbursementAmount) {
        for (LoanAccountsProductView loanAccountView : loanAccountDetails) {
            if (prdOfferingId.equals(loanAccountView.getPrdOfferingId())) {
                loanAccountView.setEnteredAmount(enteredAmountValue);
                loanAccountView.setDisBursementAmountEntered(disbursementAmount);
                try {
                    if (null != enteredAmountValue) {
                        new LocalizationConverter().getDoubleValueForCurrentLocale(enteredAmountValue);
                    }
                    loanAccountView.setValidAmountEntered(true);
                } catch (NumberFormatException ne) {
                    loanAccountView.setValidAmountEntered(false);
                }
                try {
                    if (null != disbursementAmount) {
                        new LocalizationConverter().getDoubleValueForCurrentLocale(disbursementAmount);
                    }
                    loanAccountView.setValidDisbursementAmount(true);
                } catch (NumberFormatException ne) {
                    loanAccountView.setValidDisbursementAmount(false);
                }

            }
        }
    }

    public void setCustomerAccountAmountEntered(final String customerAccountAmountEntered) {
        customerAccountDetails.setCustomerAccountAmountEntered(customerAccountAmountEntered);
        try {
            new LocalizationConverter().getDoubleValueForCurrentLocale(customerAccountAmountEntered);
            customerAccountDetails.setValidCustomerAccountAmountEntered(true);
        } catch (NumberFormatException nfe) {
            customerAccountDetails.setValidCustomerAccountAmountEntered(false);
        }
    }

    public int getCountOfCustomers() {
        return this.countOfCustomers;
    }

    public void setCountOfCustomers(final int countOfCustomers) {
        this.countOfCustomers = countOfCustomers;
    }

    private boolean isProductExists(final LoanAccountView loanAccount, final LoanAccountsProductView loanAccountProductView) {
        if (loanAccountProductView.getPrdOfferingId() != null
                && loanAccountProductView.getPrdOfferingId().equals(loanAccount.getPrdOfferingId())) {
            return true;
        }
        return false;

    }

    private void addAccountActionToSavingsView(final SavingsAccountView savingsAccountView, final Integer customerId,
            final List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews) {
        boolean isMandatory = false;
        if (savingsAccountView.getSavingsTypeId().equals(SavingsType.MANDATORY.getValue())) {
            isMandatory = true;
        }
        List<CollectionSheetEntryInstallmentView> accountActionDetails = retrieveSavingsAccountActions(
                savingsAccountView.getAccountId(), customerId, collectionSheetEntryAccountActionViews, isMandatory);
        if (accountActionDetails != null) {
            for (CollectionSheetEntryInstallmentView accountAction : accountActionDetails) {
                savingsAccountView.addAccountTrxnDetail(accountAction);
            }
        }
    }

    private List<CollectionSheetEntryInstallmentView> retrieveSavingsAccountActions(final Integer accountId,
            final Integer customerId, final List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
            final boolean isMandatory) {
        int index = collectionSheetEntryAccountActionViews.indexOf(new CollectionSheetEntrySavingsInstallmentView(
                accountId, customerId));
        if (!isMandatory && index != -1) {
            return collectionSheetEntryAccountActionViews.subList(index, index + 1);
        }
        int lastIndex = collectionSheetEntryAccountActionViews
                .lastIndexOf(new CollectionSheetEntrySavingsInstallmentView(accountId, customerId));
        if (lastIndex != -1 && index != -1) {
            return collectionSheetEntryAccountActionViews.subList(index, lastIndex + 1);
        }
        return null;
    }

    private SavingsAccountView createSavingsAccountViewFromExisting(final SavingsAccountView savingsAccountView) {
        return new SavingsAccountView(savingsAccountView.getAccountId(), savingsAccountView.getCustomerId(),
                savingsAccountView.getSavingsOfferingShortName(), savingsAccountView.getSavingsOfferingId(),
                savingsAccountView.getSavingsTypeId(), savingsAccountView.getRecommendedAmntUnitId());
    }

    private void addSavingsAccountViewToClients(final List<CollectionSheetEntryView> clientCollectionSheetEntryViews,
            final List<SavingsAccountView> savingsAccountViews) {
        for (CollectionSheetEntryView collectionSheetEntryView : clientCollectionSheetEntryViews) {
            for (SavingsAccountView savingsAccountView : savingsAccountViews) {
                collectionSheetEntryView.addSavingsAccountDetail(createSavingsAccountViewFromExisting(savingsAccountView));
            }
        }
    }

    private Double getAmountPaidAtDisb(final LoanAccountView loanAccountView, final Integer customerId,
            final List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
            final List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews) {

        if (loanAccountView.isInterestDeductedAtDisbursement()) {
            return getInterestAmountDedAtDisb(retrieveLoanSchedule(loanAccountView.getAccountId(), customerId,
                    collectionSheetEntryAccountActionViews, collectionSheetEntryAccountFeeActionViews));
        }

        return new LoanPersistence().getFeeAmountAtDisbursement(loanAccountView.getAccountId(), Money.getDefaultCurrency()).getAmountDoubleValue();
    }

    private Double getInterestAmountDedAtDisb(final List<CollectionSheetEntryInstallmentView> installments) {
        for (CollectionSheetEntryInstallmentView collectionSheetEntryAccountAction : installments) {
            if (collectionSheetEntryAccountAction.getInstallmentId().shortValue() == 1) {
                return ((CollectionSheetEntryLoanInstallmentView) collectionSheetEntryAccountAction)
                        .getTotalDueWithFees().getAmountDoubleValue();
            }
        }
        return 0.0;
    }

    private List<CollectionSheetEntryInstallmentView> retrieveLoanSchedule(final Integer accountId, final Integer customerId,
            final List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
            final List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews) {
        int index = collectionSheetEntryAccountActionViews.indexOf(new CollectionSheetEntryLoanInstallmentView(
                accountId, customerId, getCurrency()));
        int lastIndex = collectionSheetEntryAccountActionViews.lastIndexOf(new CollectionSheetEntryLoanInstallmentView(
                accountId, customerId, getCurrency()));
        if (lastIndex != -1 && index != -1) {
            List<CollectionSheetEntryInstallmentView> applicableInstallments = collectionSheetEntryAccountActionViews
                    .subList(index, lastIndex + 1);
            for (CollectionSheetEntryInstallmentView collectionSheetEntryAccountActionView : applicableInstallments) {
                int feeIndex = collectionSheetEntryAccountFeeActionViews
                        .indexOf(new CollectionSheetEntryAccountFeeActionView(collectionSheetEntryAccountActionView
                                .getActionDateId()));
                int feeLastIndex = collectionSheetEntryAccountFeeActionViews
                        .lastIndexOf(new CollectionSheetEntryAccountFeeActionView(collectionSheetEntryAccountActionView
                                .getActionDateId()));
                if (feeIndex != -1 && feeLastIndex != -1) {
                    ((CollectionSheetEntryLoanInstallmentView) collectionSheetEntryAccountActionView)
                            .setCollectionSheetEntryAccountFeeActions(collectionSheetEntryAccountFeeActionViews
                                    .subList(feeIndex, feeLastIndex + 1));
                }
            }
            return applicableInstallments;
        }
        return null;
    }

    private List<CollectionSheetEntryInstallmentView> retrieveCustomerSchedule(final Integer accountId, final Integer customerId,
            final List<CollectionSheetEntryInstallmentView> collectionSheetEntryAccountActionViews,
            final List<CollectionSheetEntryAccountFeeActionView> collectionSheetEntryAccountFeeActionViews) {
        int index = collectionSheetEntryAccountActionViews
                .indexOf(new CollectionSheetEntryCustomerAccountInstallmentView(accountId, customerId, getCurrency()));
        int lastIndex = collectionSheetEntryAccountActionViews
                .lastIndexOf(new CollectionSheetEntryCustomerAccountInstallmentView(accountId, customerId, getCurrency()));
        if (lastIndex != -1 && index != -1) {
            List<CollectionSheetEntryInstallmentView> applicableInstallments = collectionSheetEntryAccountActionViews
                    .subList(index, lastIndex + 1);
            for (CollectionSheetEntryInstallmentView collectionSheetEntryAccountActionView : applicableInstallments) {
                int feeIndex = collectionSheetEntryAccountFeeActionViews
                        .indexOf(new CollectionSheetEntryAccountFeeActionView(collectionSheetEntryAccountActionView
                                .getActionDateId()));
                int feeLastIndex = collectionSheetEntryAccountFeeActionViews
                        .lastIndexOf(new CollectionSheetEntryAccountFeeActionView(collectionSheetEntryAccountActionView
                                .getActionDateId()));
                if (feeIndex != -1 && feeLastIndex != -1) {
                    ((CollectionSheetEntryCustomerAccountInstallmentView) collectionSheetEntryAccountActionView)
                            .setCollectionSheetEntryAccountFeeActions(collectionSheetEntryAccountFeeActionViews
                                    .subList(feeIndex, feeLastIndex + 1));
                }
            }
            return applicableInstallments;
        }
        return null;
    }
}
