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

package org.mifos.accounts.api;

import java.util.List;

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
    void makePayment(AccountPaymentParametersDto accountPaymentParametersDto) throws Exception;

    /**
     * Make multiple loan account payments within a single transaction.
     *
     * @param accountPaymentParametersDtoList a list of loan payment parameters
     */
    void makePayments(List<AccountPaymentParametersDto> accountPaymentParametersDtoList) throws Exception;

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
     * Validate a payment by checking for any errors that would result from making a
     * payment using the DTO being passed in.
     *
     * @param payment the payment parameters to validate
     *
     * @return a list of invalid payment reasons
     */
    List<InvalidPaymentReason> validatePayment(AccountPaymentParametersDto payment) throws Exception;

    /**
     * Gets the payment types that are valid for loans.
     *
     * @return a list of payment types
     */
    List<PaymentTypeDto> getLoanPaymentTypes() throws Exception;

    /**
     * Gets the payment types that are valid for fee payments.
     *
     * @return a list of payment types
     */
    List<PaymentTypeDto> getFeePaymentTypes() throws Exception;

}
