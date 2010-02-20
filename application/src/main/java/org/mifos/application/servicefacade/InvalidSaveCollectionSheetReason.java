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

/**
 * The Enum InvalidSaveCollectionSheetReason lists the reasons a collection
 * sheet save can be rejected as invalid.
 */
public enum InvalidSaveCollectionSheetReason {
    
    //errors checked at constructor time
    NO_TOP_CUSTOMER_PROVIDED,
    PAYMENT_TYPE_NULL,
    UNSUPPORTED_PAYMENT_TYPE,
    TRANSACTION_DATE_NULL,
    USERID_NULL,
    
    CUSTOMERID_NULL,
    CUSTOMERID_NEGATIVE,
    PARENTCUSTOMERID_NEGATIVE,
    CUSTOMER_LISTED_MORE_THAN_ONCE,
    
    ACCOUNTID_NULL,
    ACCOUNTID_NEGATIVE,
    ACCOUNT_LISTED_MORE_THAN_ONCE,
    
    CURRENCYID_NULL,
    CURRENCYID_NEGATIVE,
    
    TOTALCUSTOMERACCOUNTCOLLECTIONFEE_NEGATIVE,
    TOTALLOANPAYMENT_NEGATIVE,
    TOTALDISBURSEMENT_NEGATIVE,
    TOTALDEPOSIT_NEGATIVE,
    TOTALWITHDRAWAL_NEGATIVE,
    
    //errors checked when processing the saveCollectionSheet method
    INVALID_TOP_CUSTOMER,
    CUSTOMER_NOT_FOUND,
    INVALID_CUSTOMER_STATUS,
    INVALID_CUSTOMER_PARENT,
    CUSTOMER_IS_NOT_PART_OF_TOPCUSTOMER_HIERARCHY,

    INDIVIDUAL_SAVINGS_ACCOUNTS_ONLY_VALID_FOR_CLIENTS,
    ATTENDANCE_TYPE_ONLY_VALID_FOR_CLIENTS,
    ATTENDANCE_TYPE_NULL,
    UNSUPPORTED_ATTENDANCE_TYPE,

    ACCOUNT_NOT_FOUND,
    ACCOUNT_DOESNT_BELONG_TO_CUSTOMER,
    ACCOUNT_NOT_A_CUSTOMER_ACCOUNT,
    ACCOUNT_NOT_A_LOAN_ACCOUNT,
    INVALID_LOAN_ACCOUNT_STATUS,
    INVALID_SAVINGS_ACCOUNT_STATUS,
    ACCOUNT_NOT_A_SAVINGS_ACCOUNT,
    
    INVALID_CURRENCY, 
    INVALID_DATE;
    
}
