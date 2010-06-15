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

/**
 * Reasons a loan payment, savings deposit/withdrawl or loan disbursal can be rejected as invalid. API consumers are
 * expected to use this enum and, for instance, craft error messages with domain-specific information.
 */
public enum InvalidPaymentReason {
    /** The payment is invalid because the payment date is invalid. */
    INVALID_DATE,
    /** The payment is invalid because the payment type is not supported. */
    UNSUPPORTED_PAYMENT_TYPE,
    /** The payment is invalid because the payment amount is not valid. */
    INVALID_PAYMENT_AMOUNT,
    /** The payment amount indicates an amount disbursed, but this amount is invalid. */
    INVALID_LOAN_DISBURSAL_AMOUNT,
    /** Loans may only be disbursed if they are in a particular state. */
    INVALID_LOAN_STATE;
}
