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

package org.mifos.accounts.api;

import java.util.List;

/**
 * The Interface AccountService provides methods to make and support making
 * loan payments.  This interface is intended to be used by implementers of
 * the {@link TransactionImport} Interface.
 */
public interface AccountService {
    
    /**
     * Make a payment on a loan account within a single transaction.
     * 
     * @param accountPaymentParametersDto the parameters to make a loan payment
     * 
     * @throws Exception
     */
    void makePayment(AccountPaymentParametersDto accountPaymentParametersDto) throws Exception;
    
    /**
     * Make multiple loan account payments within a single transaction.
     * 
     * @param accountPaymentParametersDtoList a list of loan payment parameters
     * 
     * @throws Exception
     */
    void makePayments(List<AccountPaymentParametersDto> accountPaymentParametersDtoList) throws Exception;
    
    /**
     * Lookup a loan account reference for a loan with a matching external id.  If no loan is found 
     * with a matching external id, then an exception is thrown.
     * 
     * @param externalId the external id to find.
     * 
     * @return a reference to the account found
     * 
     * @throws Exception
     */
    AccountReferenceDto lookupLoanAccountReferenceFromExternalId(String externalId) throws Exception;
    
    /**
     * Validate a payment by checking for any errors that would result from making a
     * payment using the DTO being passed in.
     * 
     * @param payment the payment parameters to validate
     * 
     * @return a list of invalid payment reasons
     * 
     * @throws Exception
     */
    List<InvalidPaymentReason> validatePayment(AccountPaymentParametersDto payment) throws Exception;
    
    /**
     * Gets the payment types that are valid for loans.
     * 
     * @return a list of payment types
     * 
     * @throws Exception
     */
    List<PaymentTypeDto> getLoanPaymentTypes() throws Exception;
    
    /**
     * Gets the payment types that are valid for fee payments.
     * 
     * @return a list of payment types
     * 
     * @throws Exception
     */
    List<PaymentTypeDto> getFeePaymentTypes() throws Exception;

}
