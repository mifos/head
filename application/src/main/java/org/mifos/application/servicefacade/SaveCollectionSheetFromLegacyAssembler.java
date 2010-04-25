/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import org.mifos.accounts.loan.util.helpers.LoanAccountDto;
import org.mifos.accounts.loan.util.helpers.LoanAccountsProductDto;
import org.mifos.accounts.savings.util.helpers.SavingsAccountDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryDto;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.util.helpers.CustomerAccountView;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class SaveCollectionSheetFromLegacyAssembler {

    public SaveCollectionSheetDto fromWebTierLegacyStructuretoSaveCollectionSheetDto(
            CollectionSheetEntryGridDto previousCollectionSheetEntryDto, Short userId) {

        List<CollectionSheetEntryDto> collectionSheetEntryDtos = new ArrayList<CollectionSheetEntryDto>();
        convertTreeToList(previousCollectionSheetEntryDto.getBulkEntryParent(), collectionSheetEntryDtos);

        SaveCollectionSheetDto saveCollectionSheet = null;

        try {
            saveCollectionSheet = new SaveCollectionSheetDto(assembleCustomers(collectionSheetEntryDtos),
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
            List<CollectionSheetEntryDto> collectionSheetEntryDtos) {

        List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();
        for (CollectionSheetEntryDto collectionSheetEntryDto : collectionSheetEntryDtos) {

            Short currencyId = null;
            if (collectionSheetEntryDto.getCurrency() != null) {
                currencyId = collectionSheetEntryDto.getCurrency().getCurrencyId();
            } else {
                currencyId = Money.getDefaultCurrency().getCurrencyId();
            }

            final Integer customerId = collectionSheetEntryDto.getCustomerDetail().getCustomerId();
            final Integer parentCustomerId = collectionSheetEntryDto.getCustomerDetail().getParentCustomerId();
            final Short attendanceId = collectionSheetEntryDto.getAttendence();

            final SaveCollectionSheetCustomerAccountDto SaveCollectionSheetCustomerAccount = assembleCustomerAccount(
                    collectionSheetEntryDto.getCustomerAccountDetails(), currencyId);

            final List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans = assembleCustomerLoans(
                    collectionSheetEntryDto.getLoanAccountDetails(), currencyId);

            final List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings = assembleCustomerSavings(
                    collectionSheetEntryDto.getSavingsAccountDetails(), currencyId, false, collectionSheetEntryDto
                            .getCustomerDetail().getCustomerLevelId());

            final List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerIndividualSavings = assembleCustomerSavings(
                    collectionSheetEntryDto.getSavingsAccountDetails(), currencyId, true, collectionSheetEntryDto
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
            List<SavingsAccountDto> savingsAccountDetails, Short currencyId, Boolean attemptingToPopulateClientIndividualSavingsList,
            Short customerLevelId) {

        if ((null != savingsAccountDetails) && (savingsAccountDetails.size() > 0)) {
            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
            for (SavingsAccountDto savingsAccountDto : savingsAccountDetails) {

                Boolean match = false;
                if (attemptingToPopulateClientIndividualSavingsList) {
                    if (isClient(customerLevelId) && isIndividualSavingsAccount(savingsAccountDto.getRecommendedAmntUnitId())) {
                        match = true;
                    }
                }
                if (!attemptingToPopulateClientIndividualSavingsList) {

                    if (isClient(customerLevelId)) {
                       if (!isIndividualSavingsAccount(savingsAccountDto.getRecommendedAmntUnitId())) {
                           match = true;
                       }
                    }
                    else {
                        match = true;
                    }
                }

                if (match) {

                    BigDecimal depositEntered = setBigDecimalAmount(savingsAccountDto.getDepositAmountEntered());
                    BigDecimal withdrawalEntered = setBigDecimalAmount(savingsAccountDto.getWithDrawalAmountEntered());
                    SaveCollectionSheetCustomerSavingDto saveCollectionSheetCustomerSaving = null;

                    try {
                        saveCollectionSheetCustomerSaving = new SaveCollectionSheetCustomerSavingDto(savingsAccountDto
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
            List<LoanAccountsProductDto> loanAccountDetails, Short currencyId) {

        if (null != loanAccountDetails && loanAccountDetails.size() > 0) {

            List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans = new ArrayList<SaveCollectionSheetCustomerLoanDto>();

            for (LoanAccountsProductDto loanAccountDetail : loanAccountDetails) {

                List<LoanAccountDto> loanAccountDtos = loanAccountDetail.getLoanAccountViews();

                if ((null != loanAccountDtos) && (loanAccountDtos.size() == 1)) {

                    BigDecimal repaymentAmount = setBigDecimalAmount(loanAccountDetail.getEnteredAmount());
                    BigDecimal disbursementAmount = setBigDecimalAmount(loanAccountDetail
                            .getDisBursementAmountEntered());

                    SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan = null;

                    try {
                        saveCollectionSheetCustomerLoan = new SaveCollectionSheetCustomerLoanDto(loanAccountDtos
                                .get(0).getAccountId(), currencyId, repaymentAmount, disbursementAmount);
                    } catch (SaveCollectionSheetException e) {
                        throw new MifosRuntimeException(e.printInvalidSaveCollectionSheetReasons());
                    }

                    saveCollectionSheetCustomerLoans.add(saveCollectionSheetCustomerLoan);

                } else {
                    if ((loanAccountDtos == null) || (loanAccountDtos.size() == 0)) {
                        throw new RuntimeException("Loan Product: " + loanAccountDetail.getPrdOfferingShortName()
                                + " has no loans associated with it.");
                    }

                    for (LoanAccountDto loanAccountDto : loanAccountDtos) {
                        // more than one loan against a particular loan product
                        // offering
                        // The user has to either set to zero or pay all
                        // (enforced on web front end)
                        BigDecimal repaymentAmount = setBigDecimalAmount(loanAccountDetail.getEnteredAmount());
                        if (repaymentAmount.compareTo(BigDecimal.ZERO) > 0) {
                            repaymentAmount = new BigDecimal(loanAccountDto.getTotalAmountDue());
                        }
                        BigDecimal disbursementAmount = setBigDecimalAmount(loanAccountDetail
                                .getDisBursementAmountEntered());
                        if (disbursementAmount.compareTo(BigDecimal.ZERO) > 0) {
                            disbursementAmount = new BigDecimal(loanAccountDto.getTotalDisburseAmount());
                        }

                        SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan = null;

                        try {
                            saveCollectionSheetCustomerLoan = new SaveCollectionSheetCustomerLoanDto(loanAccountDto
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

    private void convertTreeToList(CollectionSheetEntryDto collectionSheetEntryDto,
            List<CollectionSheetEntryDto> collectionSheetEntryDtos) {

        collectionSheetEntryDtos.add(collectionSheetEntryDto);
        if ((null != collectionSheetEntryDto.getCollectionSheetEntryChildren())
                && (collectionSheetEntryDto.getCollectionSheetEntryChildren().size() > 0)) {

            for (CollectionSheetEntryDto collectionSheetEntryViewChild : collectionSheetEntryDto
                    .getCollectionSheetEntryChildren()) {

                convertTreeToList(collectionSheetEntryViewChild, collectionSheetEntryDtos);
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