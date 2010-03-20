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
import org.mifos.application.master.util.helpers.PaymentTypes;

/**
 * Holds data to save a collection sheet
 */
public class SaveCollectionSheetDto {

    /*
     * The "Contract" is that the first customer must be the "top customer" (usually Center) but other customers can be
     * any or all of the groups and clients underneath the top customer in any order.
     *
     * It would be normal to have them in order thought e.g center, group 1, client 1, client 2, group 2...
     */
    private List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers;
    private Short paymentType;
    private LocalDate transactionDate;
    private String receiptId;
    private LocalDate receiptDate;
    private Short userId;

    public SaveCollectionSheetDto(List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers, Short paymentType,
            LocalDate transactionDate, String receiptId, LocalDate receiptDate, Short userId)
            throws SaveCollectionSheetException {

        validateInput(saveCollectionSheetCustomers, paymentType, transactionDate, userId);
        if (validationErrors.size() > 0) {
            throw new SaveCollectionSheetException(validationErrors);
        }

        this.saveCollectionSheetCustomers = saveCollectionSheetCustomers;
        this.paymentType = paymentType;
        this.transactionDate = transactionDate;
        this.receiptId = receiptId;
        this.receiptDate = receiptDate;
        this.userId = userId;

        analyze();
    }

    public List<SaveCollectionSheetCustomerDto> getSaveCollectionSheetCustomers() {
        return this.saveCollectionSheetCustomers;
    }

    public Short getPaymentType() {
        return this.paymentType;
    }

    public LocalDate getTransactionDate() {
        return this.transactionDate;
    }

    public String getReceiptId() {
        return this.receiptId;
    }

    public LocalDate getReceiptDate() {
        return this.receiptDate;
    }

    public Short getUserId() {
        return this.userId;
    }

    /*
     * The Dto really ends here: All the fields and methods below are only 'helpers' derived because of:
     *
     * a) self-validation (any error is a programming problem) Some may think this should not be part of this class
     * and/or errors should just throw a runtime exception. I (JPW) thought it might be useful for a programmer to have
     * the errors nicely put in an array with a method to print them for debugging purposes. We'll see how it goes.
     *
     *
     * b) analysing the save collection sheet input. Again some may think this should not be in this class. However,
     * even this simplified structure takes quite a bit of set up and I thought it would be good to have methods that
     * told the programmer how the collection sheet was made up. Some of the fields are used to determine if it is worth
     * pre-fetching (into the Hibernate session cache) collection sheet data for performance reasons.
     */

    private List<InvalidSaveCollectionSheetReason> validationErrors = new ArrayList<InvalidSaveCollectionSheetReason>();

    /*
     * summary fields
     */
    private Integer countOneLevelUnder = 0;
    private Integer countTwoLevelsUnder = 0;
    private Integer countCustomerAccounts = 0;
    private Integer countLoanDisbursements = 0;
    private Integer countLoanRepayments = 0;
    private Integer countSavingsWithdrawals = 0;
    private Integer countSavingsDeposits = 0;
    private Integer countIndividualSavingsWithdrawals = 0;
    private Integer countIndividualSavingsDeposits = 0;

    public Integer countOneLevelUnder() {
        return this.countOneLevelUnder;
    }

    public Integer countTwoLevelsUnder() {
        return this.countTwoLevelsUnder;
    }

    public Integer countCustomerAccounts() {
        return this.countCustomerAccounts;
    }

    public Integer countLoanDisbursements() {
        return this.countLoanDisbursements;
    }

    public Integer countLoanRepayments() {
        return this.countLoanRepayments;
    }

    public Integer countSavingsWithdrawals() {
        return this.countSavingsWithdrawals;
    }

    public Integer countSavingsDeposits() {
        return this.countSavingsDeposits;
    }

    public Integer countIndividualSavingsWithdrawals() {
        return this.countIndividualSavingsWithdrawals;
    }

    public Integer countIndividualSavingsDeposits() {
        return this.countIndividualSavingsDeposits;
    }

    private void validateInput(List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers, Short paymentType,
            LocalDate transactionDate, Short userId) {

        if (saveCollectionSheetCustomers == null || saveCollectionSheetCustomers.size() < 1) {
            validationErrors.add(InvalidSaveCollectionSheetReason.NO_TOP_CUSTOMER_PROVIDED);
        }

        if (paymentType == null) {
            validationErrors.add(InvalidSaveCollectionSheetReason.PAYMENT_TYPE_NULL);
        } else {
            if (!validPaymentType(paymentType)) {
                validationErrors.add(InvalidSaveCollectionSheetReason.UNSUPPORTED_PAYMENT_TYPE);
            }
        }

        if (transactionDate == null) {
            validationErrors.add(InvalidSaveCollectionSheetReason.TRANSACTION_DATE_NULL);
        }

        if (userId == null) {
            validationErrors.add(InvalidSaveCollectionSheetReason.USERID_NULL);
        }

        if (!uniqueCustomers(saveCollectionSheetCustomers)) {
            validationErrors.add(InvalidSaveCollectionSheetReason.CUSTOMER_LISTED_MORE_THAN_ONCE);
        }
    }

    private boolean uniqueCustomers(List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers) {

        if (saveCollectionSheetCustomers != null && saveCollectionSheetCustomers.size() > 0) {

            for (Integer i = 0; i < saveCollectionSheetCustomers.size() - 1; i++) {
                for (Integer j = i + 1; j < saveCollectionSheetCustomers.size(); j++) {
                    if (saveCollectionSheetCustomers.get(i).getCustomerId().compareTo(
                            saveCollectionSheetCustomers.get(j).getCustomerId()) == 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean validPaymentType(Short paymentType) {
        // Emily Tucker/ Adam F -
        // Although the mifos model supports allocating different
        // payment types against different transaction types it is okay for
        // collection sheet to just validate against payment type enum
        for (PaymentTypes pt : PaymentTypes.values()) {
            if (pt.getValue().compareTo(paymentType) == 0) {
                return true;
            }
        }
        return false;
    }

    private void analyze() {

        for (SaveCollectionSheetCustomerDto saveCollectionSheetCustomer : this.getSaveCollectionSheetCustomers()) {

            if (saveCollectionSheetCustomer.getParentCustomerId() != null) {
                if (saveCollectionSheetCustomer.getParentCustomerId().equals(
                        this.getSaveCollectionSheetCustomers().get(0).getCustomerId())) {
                    countOneLevelUnder += 1;
                } else {
                    countTwoLevelsUnder += 1;
                }
            }

            SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount = saveCollectionSheetCustomer
                    .getSaveCollectionSheetCustomerAccount();
            if (null != saveCollectionSheetCustomerAccount) {
                if (saveCollectionSheetCustomerAccount.getTotalCustomerAccountCollectionFee()
                        .compareTo(BigDecimal.ZERO) > 0) {
                    countCustomerAccounts += 1;
                }
            }

            List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans = saveCollectionSheetCustomer
                    .getSaveCollectionSheetCustomerLoans();
            if (null != saveCollectionSheetCustomerLoans && saveCollectionSheetCustomerLoans.size() > 0) {
                for (SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan : saveCollectionSheetCustomerLoans) {

                    if (saveCollectionSheetCustomerLoan.getTotalDisbursement().compareTo(BigDecimal.ZERO) > 0) {
                        countLoanDisbursements += 1;
                    }
                    if (saveCollectionSheetCustomerLoan.getTotalLoanPayment().compareTo(BigDecimal.ZERO) > 0) {
                        countLoanRepayments += 1;
                    }
                }
            }

            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings = saveCollectionSheetCustomer
                    .getSaveCollectionSheetCustomerSavings();
            if (null != saveCollectionSheetCustomerSavings && saveCollectionSheetCustomerSavings.size() > 0) {
                for (SaveCollectionSheetCustomerSavingDto saveCollectionSheetCustomerSaving : saveCollectionSheetCustomerSavings) {

                    if (saveCollectionSheetCustomerSaving.getTotalWithdrawal().compareTo(BigDecimal.ZERO) > 0) {
                        countSavingsWithdrawals += 1;
                    }
                    if (saveCollectionSheetCustomerSaving.getTotalDeposit().compareTo(BigDecimal.ZERO) > 0) {
                        countSavingsDeposits += 1;
                    }
                }
            }

            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavingsIndividual = saveCollectionSheetCustomer
                    .getSaveCollectionSheetCustomerIndividualSavings();
            if (null != saveCollectionSheetCustomerSavingsIndividual
                    && saveCollectionSheetCustomerSavingsIndividual.size() > 0) {
                for (SaveCollectionSheetCustomerSavingDto saveCollectionSheetCustomerSavingIndividual : saveCollectionSheetCustomerSavingsIndividual) {

                    if (saveCollectionSheetCustomerSavingIndividual.getTotalWithdrawal().compareTo(BigDecimal.ZERO) > 0) {
                        countIndividualSavingsWithdrawals += 1;
                    }
                    if (saveCollectionSheetCustomerSavingIndividual.getTotalDeposit().compareTo(BigDecimal.ZERO) > 0) {
                        countIndividualSavingsDeposits += 1;
                    }
                }
            }
        }

    }

    public void print() {

        doLog("");
        doLog("");
        doLog(">>>>>Printing SaveCollectionSheetDto ");
        doLog("Payment Type: " + this.getPaymentType());
        doLog("Receipt Id: " + this.getReceiptId());
        doLog("Receipt Date: " + this.getReceiptDate());
        doLog("Transaction Date: " + this.getTransactionDate());
        doLog("User Id: " + this.getUserId());

        doLog("Listing Customers...");

        for (SaveCollectionSheetCustomerDto saveCollectionSheetCustomer : this.getSaveCollectionSheetCustomers()) {

            doLog("Customer: Id: " + saveCollectionSheetCustomer.getCustomerId() + " Parent Id: "
                    + saveCollectionSheetCustomer.getParentCustomerId() + " Attendance Id: "
                    + saveCollectionSheetCustomer.getAttendanceId());

            SaveCollectionSheetCustomerAccountDto saveCollectionSheetCustomerAccount = saveCollectionSheetCustomer
                    .getSaveCollectionSheetCustomerAccount();
            if (null != saveCollectionSheetCustomerAccount) {
                doLog("            : A/C Collections");
                doLog("            : Account Id: " + saveCollectionSheetCustomerAccount.getAccountId()
                        + " Currency Id: " + saveCollectionSheetCustomerAccount.getCurrencyId() + " A/C Collections: "
                        + saveCollectionSheetCustomerAccount.getTotalCustomerAccountCollectionFee());

            } else {
                doLog("            : No A/C Collections");
            }

            List<SaveCollectionSheetCustomerLoanDto> saveCollectionSheetCustomerLoans = saveCollectionSheetCustomer
                    .getSaveCollectionSheetCustomerLoans();
            if (null != saveCollectionSheetCustomerLoans && saveCollectionSheetCustomerLoans.size() > 0) {
                doLog("            : Loans");
                for (SaveCollectionSheetCustomerLoanDto saveCollectionSheetCustomerLoan : saveCollectionSheetCustomerLoans) {
                    doLog("            : Account Id: " + saveCollectionSheetCustomerLoan.getAccountId()
                            + " Currency Id: " + saveCollectionSheetCustomerLoan.getCurrencyId() + " Payment: "
                            + saveCollectionSheetCustomerLoan.getTotalLoanPayment() + " Disbursement: "
                            + saveCollectionSheetCustomerLoan.getTotalDisbursement());
                }
            } else {
                doLog("            : No Loans");
            }

            List<SaveCollectionSheetCustomerSavingDto> saveCollectionSheetCustomerSavings = saveCollectionSheetCustomer
                    .getSaveCollectionSheetCustomerSavings();
            if (null != saveCollectionSheetCustomerSavings && saveCollectionSheetCustomerSavings.size() > 0) {
                doLog("            : Normal Saving Accounts");
                for (SaveCollectionSheetCustomerSavingDto saveCollectionSheetCustomerSaving : saveCollectionSheetCustomerSavings) {
                    doLog("            : Account Id: " + saveCollectionSheetCustomerSaving.getAccountId()
                            + " Currency Id: " + saveCollectionSheetCustomerSaving.getCurrencyId() + " Deposit: "
                            + saveCollectionSheetCustomerSaving.getTotalDeposit() + " Withdrawal: "
                            + saveCollectionSheetCustomerSaving.getTotalWithdrawal());
                }
            } else {
                doLog("            : No Normal Saving Accounts");
            }

            saveCollectionSheetCustomerSavings = saveCollectionSheetCustomer
                    .getSaveCollectionSheetCustomerIndividualSavings();
            if (null != saveCollectionSheetCustomerSavings && saveCollectionSheetCustomerSavings.size() > 0) {
                doLog("            : Individual Saving Accounts");
                for (SaveCollectionSheetCustomerSavingDto saveCollectionSheetCustomerSaving : saveCollectionSheetCustomerSavings) {
                    doLog("            : Account Id: " + saveCollectionSheetCustomerSaving.getAccountId()
                            + " Currency Id: " + saveCollectionSheetCustomerSaving.getCurrencyId() + " Deposit: "
                            + saveCollectionSheetCustomerSaving.getTotalDeposit() + " Withdrawal: "
                            + saveCollectionSheetCustomerSaving.getTotalWithdrawal());
                }
            } else {
                doLog("            : No Individual Saving Accounts");
            }
        }

        doLog("");
        doLog("================= Summary (only items > 0.00 are processed)===================");
        doLog("No. of Customers One level Under Top Customer : " + countOneLevelUnder);
        doLog("No. of Customers Two levels Under Top Customer: " + countTwoLevelsUnder);
        doLog("");
        doLog("Total No. of Customer Accounts to be Processed: " + countCustomerAccounts);
        doLog("Total No. of Repayments to be Processed: " + countLoanRepayments);
        doLog("Total No. of Disbursals to be Processed: " + countLoanDisbursements);
        doLog("Total No. of Normal Savings Account Deposits to be Processed: " + countSavingsDeposits);
        doLog("Total No. of Normal Savings Account Withdrawals to be Processed: " + countSavingsWithdrawals);
        doLog("Total No. of Individual Savings Account Deposits to be Processed: " + countIndividualSavingsDeposits);
        doLog("Total No. of Individual Savings Account Withdrawals to be Processed: "
                + countIndividualSavingsWithdrawals);
        doLog("==============================================================================");

    }

    public String printSummary() {

        final StringBuilder builder = new StringBuilder();
        final String comma = ", ";

        builder.append("Collection Sheet Summary:");
        builder.append(comma);
        builder.append(this.getSaveCollectionSheetCustomers().get(0).getCustomerId());
        builder.append(comma);
        builder.append(countOneLevelUnder);
        builder.append(comma);
        builder.append(countTwoLevelsUnder);
        builder.append(comma);
        builder.append(countCustomerAccounts);
        builder.append(comma);
        builder.append(countLoanRepayments);
        builder.append(comma);
        builder.append(countLoanDisbursements);
        builder.append(comma);
        builder.append(countSavingsDeposits);
        builder.append(comma);
        builder.append(countSavingsWithdrawals);
        builder.append(comma);
        builder.append(countIndividualSavingsDeposits);
        builder.append(comma);
        builder.append(countIndividualSavingsWithdrawals);

        return builder.toString();
    }


    private void doLog(String str) {
        System.out.println(str);
    }

}
