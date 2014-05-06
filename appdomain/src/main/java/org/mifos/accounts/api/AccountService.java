/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.accounts.api;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.mifos.accounts.business.AccountOverpaymentEntity;
import org.mifos.dto.domain.AccountPaymentParametersDto;
import org.mifos.dto.domain.AccountReferenceDto;
import org.mifos.dto.domain.AccountTrxDto;
import org.mifos.dto.domain.OverpaymentDto;
import org.mifos.dto.domain.PaymentTypeDto;
import org.mifos.framework.exceptions.PersistenceException;

/**
 * The Interface AccountService provides methods to make and support making
 * loan payments. This interface is intended to be used by implementers of
 * the <b>org.mifos.spi.TransactionImport</b> Interface.
 */
public interface AccountService {

    /**
     * Make a payment on a loan account within a single transaction.
     *
     * @param accountPaymentParametersDto the parameters to make a loan payment
     */
    void makePayment(AccountPaymentParametersDto accountPaymentParametersDto);

    /**
     * Make multiple loan account payments within a single transaction.
     *
     * @param accountPaymentParametersDtoList a list of loan payment parameters
     */
    void makePayments(List<AccountPaymentParametersDto> accountPaymentParametersDtoList) throws Exception;
    
    /**
     * Added for undo full import payments
     * Make multiple loan account payments within a single transaction.
     *
     * @param accountPaymentParametersDtoList a list of loan payment parameters
     * @return 
     */
    List<AccountTrxDto> makePaymentsForImport(List<AccountPaymentParametersDto> accountPaymentParametersDtoList) throws Exception;
    

    /**
     * Disburse multiple loan accounts within a single transaction.
     *
     * @param accountPaymentParametersDtoList a list of loan payment parameters
     * @param locale
     */
    void disburseLoans(List<AccountPaymentParametersDto> accountPaymentParametersDtoList, Locale locale, Short paymentTypeIdForFees,
            Integer accountForTransferId) throws Exception;

    /**
     * Mock up method of disburseLoans to fix the MIFOS-6127
     *
     * @param accountPaymentParametersDtoList a list of loan payment parameters
     * @param locale
     */

    void disburseLoans(List<AccountPaymentParametersDto> accountPaymentParametersDtoList, Locale locale) throws Exception;

    /**
     * Lookup a loan account reference for a loan with a matching primary key.
     *
     * @param id primary key of loan account
     *
     * @return a reference to the account found
     *
     * @throws Exception if no loan is found
     */
    AccountReferenceDto lookupLoanAccountReferenceFromId(Integer id) throws Exception;

    /**
     * Lookup a loan account reference for a loan with a matching external id. External id is a field on a
     * loan account created specifically for external lookups.
     *
     * @param externalId the external id to find.
     *
     * @return a reference to the account found
     *
     * @throws Exception if no loan is found
     */
    AccountReferenceDto lookupLoanAccountReferenceFromExternalId(String externalId) throws Exception;

    /**
     * Lookup a loan account reference for a loan with a matching global account number.
     *
     * @param globalAccountNumber the the global account number to find.
     *
     * @return a reference to the account found
     *
     * @throws Exception If no loan is found
     */
    AccountReferenceDto lookupLoanAccountReferenceFromGlobalAccountNumber(String globalAccountNumber) throws Exception;

    /**
     * Lookup a loan account reference for a loan based on the borrower's government id and the loan product short name.
     *
     * @param clientGovernmentId government ID
     *
     * @return a reference to the account found
     *
     * @throws Exception If no loan is found
     */
    AccountReferenceDto lookupLoanAccountReferenceFromClientGovernmentIdAndLoanProductShortName(
            String clientGovernmentId, String loanProductShortName) throws Exception;

    /**
     * Lookup a Savings account reference for a savings based on the borrower's government id and the savings product short name.
     *
     * @param clientGovernmentId government ID
     *
     * @return a reference to the account found
     *
     * @throws Exception If no savings is found
     */
    AccountReferenceDto lookupSavingsAccountReferenceFromClientGovernmentIdAndSavingsProductShortName(
            String clientGovernmentId, String savingsProductShortName) throws Exception;

    /**
     * Lookup a loan account reference for a loan based on the borrower's phone number (with nondigit characters
     * stripped) and the loan product short name.
     *
     * @param clientGovernmentId government ID
     *
     * @return a reference to the account found
     *
     * @throws Exception If no loan is found
     */
    AccountReferenceDto lookupLoanAccountReferenceFromClientPhoneNumberAndLoanProductShortName(
            String phoneNumber, String loanProductShortName) throws Exception;

    /**
     * Lookup a Savings account reference for a savings based on the borrower's phone number (with nondigit characters
     * stripped) and the savings product short name.
     *
     * @param clientGovernmentId government ID
     *
     * @return a reference to the account found
     *
     * @throws Exception If no savings is found
     */
    AccountReferenceDto lookupSavingsAccountReferenceFromClientPhoneNumberAndSavingsProductShortName(
            String phoneNumber, String savingsProductShortName) throws Exception;

    /**
     * Checks if there is more than one savings/loan account for a given client's phone number and product short name
     */
    boolean existsMoreThanOneSavingsAccount(String phoneNumber, String loanProductShortName);

    boolean existsMoreThanOneLoanAccount(String phoneNumber, String loanProductShortName);

    /**
     * Validate a payment by checking for any errors that would result from making a
     * payment using the DTO being passed in.
     *
     * @param payment the payment parameters to validate
     *
     * @return a list of invalid payment reasons
     */
    List<InvalidPaymentReason> validatePayment(AccountPaymentParametersDto payment) throws Exception;

    /**
     * Validate a disbursement by checking for any errors that would result from making a
     * payment using the DTO being passed in.
     *
     * @param payment the payment parameters to validate
     *
     * @return a list of invalid payment reasons
     */
    List<InvalidPaymentReason> validateLoanDisbursement(AccountPaymentParametersDto payment) throws Exception;

    /**
     * Gets the payment types that are valid for loans.
     *
     * @return a list of payment types
     */
    List<PaymentTypeDto> getLoanPaymentTypes() throws Exception;

    /**
     * Gets the payment types that are valid for savings deposits.
     *
     * @return a list of payment types
     */
    List<PaymentTypeDto> getSavingsPaymentTypes() throws Exception;

    /**
     * Gets the payment types that are valid for savings withdrawals.
     *
     * @return a list of payment types
     */
    List<PaymentTypeDto> getSavingsWithdrawalTypes() throws Exception;

    List<PaymentTypeDto> getLoanDisbursementTypes() throws Exception;

    /**
     * Gets the payment types that are valid for fee payments.
     *
     * @return a list of payment types
     */
    List<PaymentTypeDto> getFeePaymentTypes() throws Exception;

    /**
     * Gets the total payment due for an account.
     *
     * @return big decimal total payment due
     *
     */
    BigDecimal getTotalPaymentDueAmount(AccountReferenceDto account) throws Exception;

    /**
     * Gets a property from the mifos configuration file. This is the most basic get method for retrieving values of
     * properties.
     *
     * @param propertyKey
     * @return the value to which this configuration maps the specified key, or null if the configuration contains no
     *         mapping for this key.
     */
    Object getMifosConfiguration(String propertyKey);

    /**
     * Lookup all payments associated with a given account.
     *
     * @param accountRef is a reference to the account for which payment information is requested.
     */
    List<AccountPaymentParametersDto> lookupPayments(AccountReferenceDto accountRef) throws Exception;

    /**
     * Checks if given receipt number already exists in account_payments table
     */
    boolean receiptExists(String receiptNumber) throws Exception;

    List<AccountReferenceDto> lookupLoanAccountReferencesFromClientPhoneNumberAndWithdrawAmount(
            String phoneNumber, BigDecimal withdrawAmount) throws Exception;

    OverpaymentDto getOverpayment(String overpaymentId) throws Exception;

    /**
     * Makes a payment from a savings account. The amount paid is withdrawn from the savings account.
     * @param accountPaymentParametersDto payment parameters
     * @param savingsGlobalAccNum savings account number
     */
    public void makePaymentFromSavings(AccountPaymentParametersDto accountPaymentParametersDto, String savingsGlobalAccNum);

    /**
     * Checks if account with accountId is member of group loan account
     * @param accountId checks account with this ID
     */
    boolean isAccountGroupLoanMember(Integer accountId) throws Exception;
}
