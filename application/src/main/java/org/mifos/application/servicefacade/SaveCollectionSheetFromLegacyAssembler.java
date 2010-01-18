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
package org.mifos.application.servicefacade;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountView;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class SaveCollectionSheetFromLegacyAssembler {

    public SaveCollectionSheetDto fromWebTierLegacyStructuretoSaveCollectionSheetDto(
            CollectionSheetEntryGridDto previousCollectionSheetEntryDto, Short userId) {

        List<CollectionSheetEntryView> collectionSheetEntryViews = new ArrayList<CollectionSheetEntryView>();
        convertTreeToList(previousCollectionSheetEntryDto.getBulkEntryParent(), collectionSheetEntryViews);

        SaveCollectionSheetDto saveCollectionSheet = null;

        try {
            saveCollectionSheet = new SaveCollectionSheetDto(assembleCustomers(collectionSheetEntryViews),
                    previousCollectionSheetEntryDto.getPaymentTypeId(), DateUtils
                            .getLocalDateFromDate(previousCollectionSheetEntryDto.getTransactionDate()),
                    previousCollectionSheetEntryDto.getReceiptId(), DateUtils
                            .getLocalDateFromDate(previousCollectionSheetEntryDto.getReceiptDate()), userId);
        } catch (SaveCollectionSheetException e) {
            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
        }

        return saveCollectionSheet;

    }

    private List<SaveCollectionSheetCustomerDto> assembleCustomers(
            List<CollectionSheetEntryView> collectionSheetEntryViews) {

        List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();
        for (CollectionSheetEntryView collectionSheetEntryView : collectionSheetEntryViews) {

            Short currencyId = null;
            if (collectionSheetEntryView.getCurrency() != null) {
                currencyId = collectionSheetEntryView.getCurrency().getCurrencyId();
            } else {
                currencyId = Money.getDefaultCurrency().getCurrencyId();
            }

            final Integer customerId = collectionSheetEntryView.getCustomerDetail().getCustomerId();
            final Integer parentCustomerId = collectionSheetEntryView.getCustomerDetail().getParentCustomerId();
            final Short attendanceId = collectionSheetEntryView.getAttendence();

            final SaveCollectionSheetCustomerAccountDto SaveCollectionSheetCustomerAccount = assembleCustomerAccount(
                    collectionSheetEntryView.getCustomerAccountDetails(), currencyId);

            final List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans = assembleCustomerLoans(
                    collectionSheetEntryView.getLoanAccountDetails(), currencyId);

            final List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings = assembleCustomerSavings(
                    collectionSheetEntryView.getSavingsAccountDetails(), currencyId, false, collectionSheetEntryView
                            .getCustomerDetail().getCustomerLevelId());

            final List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerIndividualSavings = assembleCustomerSavings(
                    collectionSheetEntryView.getSavingsAccountDetails(), currencyId, true, collectionSheetEntryView
                            .getCustomerDetail().getCustomerLevelId());

            SaveCollectionSheetCustomerDto saveCollectionSheetCustomerDto = null;

            try {
                saveCollectionSheetCustomerDto = new SaveCollectionSheetCustomerDto(customerId, parentCustomerId,
                        attendanceId, SaveCollectionSheetCustomerAccount, saveCollectionSheetCustomerLoans,
                        saveCollectionSheetCustomerSavings, saveCollectionSheetCustomerIndividualSavings);
            } catch (SaveCollectionSheetException e) {
                throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
            }

            saveCollectionSheetCustomers.add(saveCollectionSheetCustomerDto);
        }

        return saveCollectionSheetCustomers;

    }

    private List<SaveCollectionSheetCustomerSavingDto> assembleCustomerSavings(
            List<SavingsAccountView> savingsAccountDetails, Short currencyId, Boolean attemptingToPopulateClientIndividualSavingsList,
            Short customerLevelId) {

        if ((null != savingsAccountDetails) && (savingsAccountDetails.size() > 0)) {
            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
            for (SavingsAccountView savingsAccountView : savingsAccountDetails) {
                
                Boolean match = false;
                if (attemptingToPopulateClientIndividualSavingsList) {
                    if (isClient(customerLevelId) && isIndividualSavingsAccount(savingsAccountView.getRecommendedAmntUnitId())) {
                        match = true;
                    }
                }
                if (!attemptingToPopulateClientIndividualSavingsList) {

                    if (isClient(customerLevelId)) {
                       if (!isIndividualSavingsAccount(savingsAccountView.getRecommendedAmntUnitId())) {
                           match = true;
                       }
                    }
                    else {
                        match = true;
                    }
                }
                
                if (match) {

                    BigDecimal depositEntered = setBigDecimalAmount(savingsAccountView.getDepositAmountEntered());
                    BigDecimal withdrawalEntered = setBigDecimalAmount(savingsAccountView.getWithDrawalAmountEntered());
                    SaveCollectionSheetCustomerSavingDto saveCollectionSheetCustomerSaving = null;

                    try {
                        saveCollectionSheetCustomerSaving = new SaveCollectionSheetCustomerSavingDto(savingsAccountView
                                .getAccountId(), currencyId, depositEntered, withdrawalEntered);
                    } catch (SaveCollectionSheetException e) {
                        throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
                    }

                    saveCollectionSheetCustomerSavings.add(saveCollectionSheetCustomerSaving);
                }
            }

            if (saveCollectionSheetCustomerSavings.size() > 0) {
                return saveCollectionSheetCustomerSavings;
            }
        }
        return null;

    }

    private boolean isIndividualSavingsAccount(Short recommendedAmntUnitId) {

        if ((recommendedAmntUnitId != null) && (recommendedAmntUnitId.compareTo(Short.valueOf("1")) == 0)) {
            return true;
        }
        return false;
    }

    private boolean isClient(Short customerLevelId) {
        if (customerLevelId.compareTo(CustomerLevel.CLIENT.getValue()) ==0) {
            return true;
        }
        return false;
    }

    private List<SaveCollectionSheetCustomerLoanDto> assembleCustomerLoans(
            List<LoanAccountsProductView> loanAccountDetails, Short currencyId) {

        if (null != loanAccountDetails && loanAccountDetails.size() > 0) {

            List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans = new ArrayList<SaveCollectionSheetCustomerLoanDto>();

            for (LoanAccountsProductView loanAccountDetail : loanAccountDetails) {

                List<LoanAccountView> loanAccountViews = loanAccountDetail.getLoanAccountViews();

                if ((null != loanAccountViews) && (loanAccountViews.size() == 1)) {

                    BigDecimal repaymentAmount = setBigDecimalAmount(loanAccountDetail.getEnteredAmount());
                    BigDecimal disbursementAmount = setBigDecimalAmount(loanAccountDetail
                            .getDisBursementAmountEntered());

                    SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan = null;

                    try {
                        saveCollectionSheetCustomerLoan = new SaveCollectionSheetCustomerLoanDto(loanAccountViews
                                .get(0).getAccountId(), currencyId, repaymentAmount, disbursementAmount);
                    } catch (SaveCollectionSheetException e) {
                        throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
                    }

                    saveCollectionSheetCustomerLoans.add(saveCollectionSheetCustomerLoan);

                } else {
                    if ((loanAccountViews == null) || (loanAccountViews.size() == 0)) {
                        throw new RuntimeException("Loan Product: " + loanAccountDetail.getPrdOfferingShortName()
                                + " has no loans associated with it.");
                    }

                    for (LoanAccountView loanAccountView : loanAccountViews) {
                        // more than one loan against a particular loan product
                        // offering
                        // The user has to either set to zero or pay all
                        // (enforced on web front end)
                        BigDecimal repaymentAmount = setBigDecimalAmount(loanAccountDetail.getEnteredAmount());
                        if (repaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
                            repaymentAmount = new BigDecimal(loanAccountView.getTotalAmountDue());
                        }
                        BigDecimal disbursementAmount = setBigDecimalAmount(loanAccountDetail
                                .getDisBursementAmountEntered());
                        if (disbursementAmount.compareTo(BigDecimal.ZERO) > 0) {
                            disbursementAmount = new BigDecimal(loanAccountView.getTotalDisburseAmount());
                        }

                        SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan = null;

                        try {
                            saveCollectionSheetCustomerLoan = new SaveCollectionSheetCustomerLoanDto(loanAccountView
                                    .getAccountId(), currencyId, repaymentAmount, disbursementAmount);
                        } catch (SaveCollectionSheetException e) {
                            throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
                        }

                        saveCollectionSheetCustomerLoans.add(saveCollectionSheetCustomerLoan);
                    }
                }
            }
            return saveCollectionSheetCustomerLoans;
        }
        return null;
    }

    private SaveCollectionSheetCustomerAccountDto assembleCustomerAccount(CustomerAccountView customerAccountDetails,
            Short currencyId) {

        // Account Id is set to -1 if no outstanding customer account
        // installment
        if ((null != customerAccountDetails) && (customerAccountDetails.getAccountId() != -1)) {
            BigDecimal amountEntered = setBigDecimalAmount(customerAccountDetails.getCustomerAccountAmountEntered());

            SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount = null;

            try {
                saveCollectionSheetCustomerAccount = new SaveCollectionSheetCustomerAccountDto(customerAccountDetails
                        .getAccountId(), currencyId, amountEntered);
            } catch (SaveCollectionSheetException e) {
                throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
            }

            return saveCollectionSheetCustomerAccount;
        }
        return null;
    }

    private void convertTreeToList(CollectionSheetEntryView collectionSheetEntryView,
            List<CollectionSheetEntryView> collectionSheetEntryViews) {

        collectionSheetEntryViews.add(collectionSheetEntryView);
        if ((null != collectionSheetEntryView.getCollectionSheetEntryChildren())
                && (collectionSheetEntryView.getCollectionSheetEntryChildren().size() > 0)) {

            for (CollectionSheetEntryView collectionSheetEntryViewChild : collectionSheetEntryView
                    .getCollectionSheetEntryChildren()) {

                convertTreeToList(collectionSheetEntryViewChild, collectionSheetEntryViews);
            }
        }

    }

    private BigDecimal setBigDecimalAmount(String stringAmount) {
        Double amount = 0.0;
        if (stringAmount != null && (!stringAmount.isEmpty())) {
            amount = Double.parseDouble(stringAmount);
        }
        return new BigDecimal(amount);
    }

}