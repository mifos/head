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

package org.mifos.application.collectionsheet.util.helpers;

public interface CollectionSheetEntryConstants {

    String BULKENTRY = "BulkEntry";
    String BULKENTRYACTIONFORM = "collectionsheetactionform";
    String COLLECTION_SHEET_ENTRY_FORM_DTO = "CollectionSheetEntryFormDto";

    String LOADLOANOFFICERS = "loadLoanOfficers";
    String LOADCUSTOMERS = "loadCustomers";

    // FIXME: add underbars for readability
    String LOADSUCCESS = "load_success";
    String GETSUCCESS = "get_success";
    String PREVIEWSUCCESS = "preview_success";
    String PREVIEWFAILURE = "preview_failure";
    String CREATESUCCESS = "create_success";
    String CREATE_FAILURE = "create_failure";
    String PREVIOUSSUCCESS = "previous_success";
    String GET_FAILURE = "get_failure";

    String BRANCHOFFICESLIST = "BranchOfficesList";
    String LOANOFFICERSLIST = "LoanOfficersList";
    String CUSTOMERSLIST = "CustomersList";

    String MEETINGDATE = "MeetingDate";
    String PRODUCTSLIST = "ProductsList";
    String ACCOUNTSLIST = "AccountsList";
    String CUSTOMERCOUNT = "CustomerCount";
    String PRODUCTSIZE = "ProductsSize";
    String CUSTOMERATTENDANCETYPES = "CustAttTypes";
    String PAYMENT_TYPES_LIST = "PaymentTypesList";
    String SAVINGSPRODUCTSLIST = "SavingsProductsList";

    Short LOANDISB = 1;
    Short LOANCOLL = 2;
    Short CUSTOMERCOLL = 3;
    Short SAVINGSCOLL = 4;

    String METHOD = "method";
    String PREVIEWMETHOD = "preview";
    String LOADMETHOD = "load";
    String CANCELMETHOD = "cancel";
    String GETMETHOD = "get";
    String PREVIOUSMETHOD = "previous";
    String CREATEMETHOD = "create";
    String VALIDATEMETHOD = "validate";

    String CENTER = "Center";

    String BULKENTRYINVALIDAMOUNT = "errors.invalidamount";
    String BULKENTRYINVALIDPREPAYAMOUNT = "errors.invalidprepayamount";
    String BULKENTRYINVALIDACCOLLECTIONS = "errors.invalidaccollections";
    String INVALID_RECEIPT_DATE = "errors.invaliddate";
    String ERRORINVALIDAMOUNT = "errors.invalidamountvalue";
    String MANDATORYFIELDS = "errors.mandatoryselect";
    String MANDATORYENTER = "errors.mandatoryenter";
    String INVALIDENDDATE = "errors.invaliddate";
    String INVALIDDATE = "errors.invaliddate";
    String MEETINGDATEEXCEPTION = "errors.meetingdate";
    String ERRORSUPDATE = "errors.update";
    String DATABASE_ERROR = "errors.database";
    String REFRESH = "refresh";

    String PRESENT = "1";

    Short LOANREPAYMENT = 1;
    Short SAVINGSDEPOSIT = 2;
    Short LOANDISBURSEMENT = 3;
    Short SAVINGSWITHDRAWAL = 4;

    String ISBACKDATEDTRXNALLOWED = "isBackDatedTrxnAllowed";
    String ISCENTERHIERARCHYEXISTS = "isCenterHierarchyExists";
    String LOANS = "loans";
    String SAVINGS = "savings";
    String COLLECTION_SHEET_ENTRY = "collectionSheetEntry";
    String CUSTOMERACCOUNTS = "customeraccounts";
    String ERRORLOANS = "errorloans";
    String ERRORSAVINGSDEPOSIT = "errorsavingsdeposit";
    String ERRORSAVINGSWITHDRAW = "errorsavingswithdraw";

    String AC_COLLECTION = "bulkEntry.acCollection";
    String DUE_COLLECTION = "bulkEntry.dueCollections";
    String ISSUE_WITHDRAWAL = "bulkEntry.issuesWithdrawals";
    String ATTN = "bulkEntry.attn";
    String ACCOUNT_GROUP_CENTER = "bulkEntry.accountGroupCenter";
    String TOTAL_GROUP_CENTER = "bulkEntry.totalGroupCenter";
    String TOTAL = "bulkEntry.total";
    String TOTAL_COLLECTION = "bulkEntry.totalCollections";
    String TOTAL_ISSUE_WITHDRAWAL = "bulkEntry.totalIssuesWithdrawals";
    String DUE_COLLECTION2 = "bulkEntry.dueCollections2";
    String LOAN_DISBURSEMENT = "bulkEntry.loanDisbursements";
    String LOAN_REPAYMENT = "bulkEntry.loanRepayments";
    String NET_CASH = "bulkEntry.netCash";
    String WITHDRAWAL = "bulkEntry.withdrawals";
    String OTHER_COLLECTION = "bulkEntry.otherCollections";
    String MODE_OF_PAYMENT = "bulkEntry.pmnttype";
    String SAVING_DEPOSIT = "bulkEntry.savingsDeposit";
    String SAVING_WITHDRAWAL = "bulkEntry.savingsWithdrawal";
    String CLIENT_NAME = "bulkEntry.clientName";
    String DATEOFTRXN = "bulkEntry.dateoftrxn";
    String LOANOFFICERS = "bulkEntry.loanofficer";
    String RECEIPTDATE = "bulkEntry.rcptdate";
}
