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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.master.util.helpers.PaymentTypes;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * I test {@link SaveCollectionSheetDto}.
 */
@RunWith(MockitoJUnitRunner.class)
public class SaveCollectionSheetDtoTest {

    private Short validUserId = Short.valueOf("1");
    private Integer validCustomerId = 0;
    private Date validDate = new Date();
    private Short validPaymentType = PaymentTypes.CHEQUE.getValue();
    private Short invalidPaymentType = Short.valueOf("-1");
    private Short validAttendanceId = AttendanceType.LATE.getValue();

    @Test
    public void shouldBeSuccessfulObjectCreationWithValidInput() {

        Boolean newSuccess = true;
        try {
            List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();
            SaveCollectionSheetCustomerDto saveCollectionSheetCustomer = new SaveCollectionSheetCustomerDto(
                    validCustomerId, null, null, null, null, null, null);
            saveCollectionSheetCustomers.add(saveCollectionSheetCustomer);

            new SaveCollectionSheetDto(saveCollectionSheetCustomers, validPaymentType, validDate, null, null,
                    validUserId);
        } catch (SaveCollectionSheetException e) {
            newSuccess = false;
        }
        assertThat("New was not successful", newSuccess, is(true));
    }

    @Test
    public void shouldGetNO_TOP_CUSTOMER_PROVIDEDIfEmptyCustomerList() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();
            new SaveCollectionSheetDto(saveCollectionSheetCustomers, validPaymentType, validDate, null, null,
                    validUserId);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0),
                is(InvalidSaveCollectionSheetReason.NO_TOP_CUSTOMER_PROVIDED));
    }

    @Test
    public void shouldGetPAYMENT_TYPE_NULLIfPaymentTypeNull() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();
            SaveCollectionSheetCustomerDto saveCollectionSheetCustomer = new SaveCollectionSheetCustomerDto(
                    validCustomerId, null, null, null, null, null, null);
            saveCollectionSheetCustomers.add(saveCollectionSheetCustomer);

            new SaveCollectionSheetDto(saveCollectionSheetCustomers, null, validDate, null, null, validUserId);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(InvalidSaveCollectionSheetReason.PAYMENT_TYPE_NULL));
    }

    @Test
    public void shouldGetUNSUPPORTED_PAYMENT_TYPEIfInvalidPaymentType() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();
            SaveCollectionSheetCustomerDto saveCollectionSheetCustomer = new SaveCollectionSheetCustomerDto(
                    validCustomerId, null, null, null, null, null, null);
            saveCollectionSheetCustomers.add(saveCollectionSheetCustomer);

            new SaveCollectionSheetDto(saveCollectionSheetCustomers, invalidPaymentType, validDate, null, null,
                    validUserId);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0),
                is(InvalidSaveCollectionSheetReason.UNSUPPORTED_PAYMENT_TYPE));
    }

    @Test
    public void shouldGetTRANSACTION_DATE_NULLIfTransactionDateNull() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();
            SaveCollectionSheetCustomerDto saveCollectionSheetCustomer = new SaveCollectionSheetCustomerDto(
                    validCustomerId, null, null, null, null, null, null);
            saveCollectionSheetCustomers.add(saveCollectionSheetCustomer);

            new SaveCollectionSheetDto(saveCollectionSheetCustomers, validPaymentType, null, null, null, validUserId);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(InvalidSaveCollectionSheetReason.TRANSACTION_DATE_NULL));
    }

    @Test
    public void shouldGetUSERID_NULLIfUserIdNull() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();
            SaveCollectionSheetCustomerDto saveCollectionSheetCustomer = new SaveCollectionSheetCustomerDto(
                    validCustomerId, null, null, null, null, null, null);
            saveCollectionSheetCustomers.add(saveCollectionSheetCustomer);

            new SaveCollectionSheetDto(saveCollectionSheetCustomers, validPaymentType, validDate, null, null, null);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(1));
        assertThat(InvalidSaveCollectionSheetReasons.get(0), is(InvalidSaveCollectionSheetReason.USERID_NULL));
    }

    @Test
    public void shouldGetFourReasonsForAllInvalidInput() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        try {
            List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = null;
            new SaveCollectionSheetDto(saveCollectionSheetCustomers, invalidPaymentType, null, null, null, null);
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNotNull("List was not set", InvalidSaveCollectionSheetReasons);
        assertThat(InvalidSaveCollectionSheetReasons.size(), is(4));
        assertThat(InvalidSaveCollectionSheetReasons
                .contains(InvalidSaveCollectionSheetReason.NO_TOP_CUSTOMER_PROVIDED), is(true));
        assertThat(InvalidSaveCollectionSheetReasons
                .contains(InvalidSaveCollectionSheetReason.UNSUPPORTED_PAYMENT_TYPE), is(true));
        assertThat(InvalidSaveCollectionSheetReasons.contains(InvalidSaveCollectionSheetReason.TRANSACTION_DATE_NULL),
                is(true));
        assertThat(InvalidSaveCollectionSheetReasons.contains(InvalidSaveCollectionSheetReason.USERID_NULL), is(true));
    }

    @Test
    public void shouldReturnCorrectFiguresForAllCountFields() {

        List<InvalidSaveCollectionSheetReason> InvalidSaveCollectionSheetReasons = null;
        SaveCollectionSheetDto saveCollectionSheet = null;
        try {
            saveCollectionSheet = generateValidSaveCollectionSheet();
        } catch (SaveCollectionSheetException e) {
            InvalidSaveCollectionSheetReasons = e.getInvalidSaveCollectionSheetReasons();
        }

        assertNull("There were errors", InvalidSaveCollectionSheetReasons);
        assertNotNull("saveCollectionSheet is null", saveCollectionSheet);
        assertThat("countOneLevelUnder", saveCollectionSheet.countOneLevelUnder(), is(2));
        assertThat("countTwoLevelsUnder", saveCollectionSheet.countTwoLevelsUnder(), is(4));
        assertThat("countCustomerAccounts", saveCollectionSheet.countCustomerAccounts(), is(4));
        assertThat("countLoanRepayments", saveCollectionSheet.countLoanRepayments(), is(3));
        assertThat("countLoanDisbursements", saveCollectionSheet.countLoanDisbursements(), is(2));
        assertThat("countSavingsDeposits", saveCollectionSheet.countSavingsDeposits(), is(2));
        assertThat("countSavingsWithdrawals", saveCollectionSheet.countSavingsWithdrawals(), is(2));
        assertThat("countIndividualSavingsDeposits", saveCollectionSheet.countIndividualSavingsDeposits(), is(3));
        assertThat("countIndividualSavingsWithdrawals", saveCollectionSheet.countIndividualSavingsWithdrawals(), is(2));
    }

    // generate a sample SaveCollectionSheet

    private Short validCurrencyId = 2;
    private BigDecimal greaterThanZero = new BigDecimal("33.58");
    private final Integer centerIndividualSavingsAccountId = 1;
    private Integer newAccountId = 10;

    private enum SAVING_ACCOUNT_AMOUNT {
        ZERO, DEPOSIT, WITHDRAWAL, DEPOSIT_AND_WITHDRAWAL;
    }

    private enum LOAN_ACCOUNT_AMOUNT {
        ZERO, REPAYMENT, DISBURSEMENT, REPAYMENT_AND_DISBURSEMENT;
    }

    private enum CUSTOMER_ACCOUNT_AMOUNT {
        ZERO, ACCOUNT_CHARGE;
    }

    private SaveCollectionSheetDto generateValidSaveCollectionSheet() throws SaveCollectionSheetException {

        List<SaveCollectionSheetCustomerDto> saveCollectionSheetCustomers = new ArrayList<SaveCollectionSheetCustomerDto>();

        SaveCollectionSheetCustomerDto center = createCenterWithDepositAndCustomerCharge(1, null, null);
        SaveCollectionSheetCustomerDto group1 = createGroupWithRepaymentAndDisbursement(2, center.getCustomerId(), null);
        SaveCollectionSheetCustomerDto group1Client1 = createCustomerWithDepositAndTwoWithdrawalsAndIndividualDepositAndCustomerCharge(
                3, group1.getCustomerId(), validAttendanceId);
        SaveCollectionSheetCustomerDto group1Client2 = createCustomerWithNoPayments(4, group1.getCustomerId(),
                validAttendanceId);
        SaveCollectionSheetCustomerDto group2 = createGroupWithCustomerCharge(5, center.getCustomerId(), null);
        SaveCollectionSheetCustomerDto group2Client1 = createCustomerWithIndividualDepositAndIndividualWithdrawalAndTwoRepaymentsAndDisbursement(
                6, group2.getCustomerId(), validAttendanceId);
        SaveCollectionSheetCustomerDto group2Client2 = createCustomerWithIndividualDepositAndIndividualWithdrawalAndCustomerCharge(
                7, group2.getCustomerId(), validAttendanceId);

        saveCollectionSheetCustomers.add(center);
        saveCollectionSheetCustomers.add(group1);
        saveCollectionSheetCustomers.add(group1Client1);
        saveCollectionSheetCustomers.add(group1Client2);
        saveCollectionSheetCustomers.add(group2);
        saveCollectionSheetCustomers.add(group2Client1);
        saveCollectionSheetCustomers.add(group2Client2);

        Short paymentType = PaymentTypes.CHEQUE.getValue();
        Date transactionDate = new Date();
        String receiptId = "Receipt 100";
        Date receiptDate = new Date();
        Short userId = Short.valueOf("1");

        SaveCollectionSheetDto saveCollectionSheet = new SaveCollectionSheetDto(saveCollectionSheetCustomers,
                paymentType, transactionDate, receiptId, receiptDate, userId);

        return saveCollectionSheet;
    }

    private SaveCollectionSheetCustomerDto createCustomerWithIndividualDepositAndIndividualWithdrawalAndCustomerCharge(
            Integer customerId, Integer parentCustomerId, Short attendanceId) throws SaveCollectionSheetException {

        List<SaveCollectionSheetCustomerSavingDto> customerSavingsAccounts = new ArrayList<SaveCollectionSheetCustomerSavingDto>();

        List<SaveCollectionSheetCustomerSavingDto> customerIndividualSavingsAccounts = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
        customerIndividualSavingsAccounts.add(customerIndividualSaving(SAVING_ACCOUNT_AMOUNT.DEPOSIT_AND_WITHDRAWAL));

        List<SaveCollectionSheetCustomerLoanDto> customerLoans = null;

        SaveCollectionSheetCustomerAccountDto customerAccount = customerCharge(CUSTOMER_ACCOUNT_AMOUNT.ACCOUNT_CHARGE);

        return new SaveCollectionSheetCustomerDto(customerId, parentCustomerId, attendanceId, customerAccount,
                customerLoans, customerSavingsAccounts, customerIndividualSavingsAccounts);
    }

    private SaveCollectionSheetCustomerDto createCustomerWithIndividualDepositAndIndividualWithdrawalAndTwoRepaymentsAndDisbursement(
            Integer customerId, Integer parentCustomerId, Short attendanceId) throws SaveCollectionSheetException {

        List<SaveCollectionSheetCustomerSavingDto> customerSavingsAccounts = null;

        List<SaveCollectionSheetCustomerSavingDto> customerIndividualSavingsAccounts = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
        customerIndividualSavingsAccounts.add(customerIndividualSaving(SAVING_ACCOUNT_AMOUNT.DEPOSIT_AND_WITHDRAWAL));

        List<SaveCollectionSheetCustomerLoanDto> customerLoans = new ArrayList<SaveCollectionSheetCustomerLoanDto>();
        customerLoans.add(customerLoan(LOAN_ACCOUNT_AMOUNT.REPAYMENT_AND_DISBURSEMENT));
        customerLoans.add(customerLoan(LOAN_ACCOUNT_AMOUNT.ZERO));
        customerLoans.add(customerLoan(LOAN_ACCOUNT_AMOUNT.REPAYMENT));

        SaveCollectionSheetCustomerAccountDto customerAccount = customerCharge(CUSTOMER_ACCOUNT_AMOUNT.ZERO);

        return new SaveCollectionSheetCustomerDto(customerId, parentCustomerId, attendanceId, customerAccount,
                customerLoans, customerSavingsAccounts, customerIndividualSavingsAccounts);
    }

    private SaveCollectionSheetCustomerDto createCenterWithDepositAndCustomerCharge(Integer customerId,
            Integer parentCustomerId, Short attendanceId) throws SaveCollectionSheetException {

        List<SaveCollectionSheetCustomerSavingDto> customerSavingsAccounts = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
        customerSavingsAccounts.add(customerIndividualSaving(SAVING_ACCOUNT_AMOUNT.DEPOSIT));

        List<SaveCollectionSheetCustomerSavingDto> customerIndividualSavingsAccounts = null;

        List<SaveCollectionSheetCustomerLoanDto> customerLoans = new ArrayList<SaveCollectionSheetCustomerLoanDto>();
        customerLoans.add(customerLoan(LOAN_ACCOUNT_AMOUNT.ZERO));
        customerLoans.add(customerLoan(LOAN_ACCOUNT_AMOUNT.ZERO));
        customerLoans.add(customerLoan(LOAN_ACCOUNT_AMOUNT.ZERO));

        SaveCollectionSheetCustomerAccountDto customerAccount = customerCharge(CUSTOMER_ACCOUNT_AMOUNT.ACCOUNT_CHARGE);

        return new SaveCollectionSheetCustomerDto(customerId, parentCustomerId, attendanceId, customerAccount,
                customerLoans, customerSavingsAccounts, customerIndividualSavingsAccounts);

    }

    private SaveCollectionSheetCustomerDto createGroupWithRepaymentAndDisbursement(Integer customerId,
            Integer parentCustomerId, Short attendanceId) throws SaveCollectionSheetException {

        List<SaveCollectionSheetCustomerSavingDto> customerSavingsAccounts = null;
        List<SaveCollectionSheetCustomerSavingDto> customerIndividualSavingsAccounts = null;

        List<SaveCollectionSheetCustomerLoanDto> customerLoans = new ArrayList<SaveCollectionSheetCustomerLoanDto>();
        customerLoans.add(customerLoan(LOAN_ACCOUNT_AMOUNT.ZERO));
        customerLoans.add(customerLoan(LOAN_ACCOUNT_AMOUNT.REPAYMENT_AND_DISBURSEMENT));
        customerLoans.add(customerLoan(LOAN_ACCOUNT_AMOUNT.ZERO));

        SaveCollectionSheetCustomerAccountDto customerAccount = customerCharge(CUSTOMER_ACCOUNT_AMOUNT.ZERO);

        return new SaveCollectionSheetCustomerDto(customerId, parentCustomerId, attendanceId, customerAccount,
                customerLoans, customerSavingsAccounts, customerIndividualSavingsAccounts);

    }

    private SaveCollectionSheetCustomerDto createCustomerWithDepositAndTwoWithdrawalsAndIndividualDepositAndCustomerCharge(
            Integer customerId, Integer parentCustomerId, Short attendanceId) throws SaveCollectionSheetException {

        List<SaveCollectionSheetCustomerSavingDto> customerSavingsAccounts = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
        customerSavingsAccounts.add(customerSaving(SAVING_ACCOUNT_AMOUNT.WITHDRAWAL));
        customerSavingsAccounts.add(customerSaving(SAVING_ACCOUNT_AMOUNT.ZERO));
        customerSavingsAccounts.add(customerSaving(SAVING_ACCOUNT_AMOUNT.ZERO));
        customerSavingsAccounts.add(customerSaving(SAVING_ACCOUNT_AMOUNT.DEPOSIT_AND_WITHDRAWAL));
        customerSavingsAccounts.add(customerSaving(SAVING_ACCOUNT_AMOUNT.ZERO));

        List<SaveCollectionSheetCustomerSavingDto> customerIndividualSavingsAccounts = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
        customerIndividualSavingsAccounts.add(customerIndividualSaving(SAVING_ACCOUNT_AMOUNT.DEPOSIT));

        List<SaveCollectionSheetCustomerLoanDto> customerLoans = null;

        SaveCollectionSheetCustomerAccountDto customerAccount = customerCharge(CUSTOMER_ACCOUNT_AMOUNT.ACCOUNT_CHARGE);

        return new SaveCollectionSheetCustomerDto(customerId, parentCustomerId, attendanceId, customerAccount,
                customerLoans, customerSavingsAccounts, customerIndividualSavingsAccounts);

    }

    private SaveCollectionSheetCustomerDto createCustomerWithNoPayments(Integer customerId, Integer parentCustomerId,
            Short attendanceId) throws SaveCollectionSheetException {

        List<SaveCollectionSheetCustomerSavingDto> customerSavingsAccounts = new ArrayList<SaveCollectionSheetCustomerSavingDto>();

        List<SaveCollectionSheetCustomerSavingDto> customerIndividualSavingsAccounts = new ArrayList<SaveCollectionSheetCustomerSavingDto>();
        customerSavingsAccounts.add(customerIndividualSaving(SAVING_ACCOUNT_AMOUNT.ZERO));
        customerSavingsAccounts.add(customerIndividualSaving(SAVING_ACCOUNT_AMOUNT.ZERO));
        customerSavingsAccounts.add(customerIndividualSaving(SAVING_ACCOUNT_AMOUNT.ZERO));
        customerSavingsAccounts.add(customerIndividualSaving(SAVING_ACCOUNT_AMOUNT.ZERO));

        List<SaveCollectionSheetCustomerLoanDto> customerLoans = null;

        SaveCollectionSheetCustomerAccountDto customerAccount = customerCharge(CUSTOMER_ACCOUNT_AMOUNT.ZERO);

        return new SaveCollectionSheetCustomerDto(customerId, parentCustomerId, attendanceId, customerAccount,
                customerLoans, customerSavingsAccounts, customerIndividualSavingsAccounts);

    }

    private SaveCollectionSheetCustomerDto createGroupWithCustomerCharge(Integer customerId, Integer parentCustomerId,
            Short attendanceId) throws SaveCollectionSheetException {

        List<SaveCollectionSheetCustomerSavingDto> customerSavingsAccounts = null;

        List<SaveCollectionSheetCustomerSavingDto> customerIndividualSavingsAccounts = new ArrayList<SaveCollectionSheetCustomerSavingDto>();

        List<SaveCollectionSheetCustomerLoanDto> customerLoans = null;

        SaveCollectionSheetCustomerAccountDto customerAccount = customerCharge(CUSTOMER_ACCOUNT_AMOUNT.ACCOUNT_CHARGE);

        return new SaveCollectionSheetCustomerDto(customerId, parentCustomerId, attendanceId, customerAccount,
                customerLoans, customerSavingsAccounts, customerIndividualSavingsAccounts);

    }

    private SaveCollectionSheetCustomerSavingDto customerIndividualSaving(SAVING_ACCOUNT_AMOUNT savingAmount)
            throws SaveCollectionSheetException {

        BigDecimal deposit = BigDecimal.ZERO;
        BigDecimal withdrawal = BigDecimal.ZERO;
        if (savingAmount.compareTo(SAVING_ACCOUNT_AMOUNT.DEPOSIT_AND_WITHDRAWAL) == 0) {
            deposit = greaterThanZero;
            withdrawal = greaterThanZero;
        }
        if (savingAmount.compareTo(SAVING_ACCOUNT_AMOUNT.DEPOSIT) == 0) {
            deposit = greaterThanZero;
        }
        if (savingAmount.compareTo(SAVING_ACCOUNT_AMOUNT.WITHDRAWAL) == 0) {
            withdrawal = greaterThanZero;
        }

        return new SaveCollectionSheetCustomerSavingDto(centerIndividualSavingsAccountId, validCurrencyId, deposit,
                withdrawal);
    }

    private SaveCollectionSheetCustomerSavingDto customerSaving(SAVING_ACCOUNT_AMOUNT savingAmount)
            throws SaveCollectionSheetException {

        BigDecimal deposit = BigDecimal.ZERO;
        BigDecimal withdrawal = BigDecimal.ZERO;
        if (savingAmount.compareTo(SAVING_ACCOUNT_AMOUNT.DEPOSIT_AND_WITHDRAWAL) == 0) {
            deposit = greaterThanZero;
            withdrawal = greaterThanZero;
        }
        if (savingAmount.compareTo(SAVING_ACCOUNT_AMOUNT.DEPOSIT) == 0) {
            deposit = greaterThanZero;
        }
        if (savingAmount.compareTo(SAVING_ACCOUNT_AMOUNT.WITHDRAWAL) == 0) {
            withdrawal = greaterThanZero;
        }

        return new SaveCollectionSheetCustomerSavingDto(newAccountId++, validCurrencyId, deposit, withdrawal);
    }

    private SaveCollectionSheetCustomerLoanDto customerLoan(LOAN_ACCOUNT_AMOUNT loanAmount)
            throws SaveCollectionSheetException {

        BigDecimal repayment = BigDecimal.ZERO;
        BigDecimal disbursement = BigDecimal.ZERO;
        if (loanAmount.compareTo(LOAN_ACCOUNT_AMOUNT.REPAYMENT_AND_DISBURSEMENT) == 0) {
            repayment = greaterThanZero;
            disbursement = greaterThanZero;
        }
        if (loanAmount.compareTo(LOAN_ACCOUNT_AMOUNT.REPAYMENT) == 0) {
            repayment = greaterThanZero;
        }
        if (loanAmount.compareTo(LOAN_ACCOUNT_AMOUNT.DISBURSEMENT) == 0) {
            disbursement = greaterThanZero;
        }

        return new SaveCollectionSheetCustomerLoanDto(newAccountId++, validCurrencyId, repayment, disbursement);
    }

    private SaveCollectionSheetCustomerAccountDto customerCharge(CUSTOMER_ACCOUNT_AMOUNT chargeAmount)
            throws SaveCollectionSheetException {

        BigDecimal charge = BigDecimal.ZERO;
        if (chargeAmount.compareTo(CUSTOMER_ACCOUNT_AMOUNT.ACCOUNT_CHARGE) == 0) {
            charge = greaterThanZero;
        }

        return new SaveCollectionSheetCustomerAccountDto(newAccountId++, validCurrencyId, charge);
    }
}
