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

package org.mifos.application.collectionsheet.util.helpers;

public interface CollectionSheetEntryConstants {

    public String BULKENTRY = "BulkEntry";
    public String BULKENTRYACTIONFORM = "collectionsheetactionform";
    public String COLLECTION_SHEET_ENTRY_FORM_DTO = "CollectionSheetEntryFormDto";

    public String LOADLOANOFFICERS = "loadLoanOfficers";
    public String LOADCUSTOMERS = "loadCustomers";

    // FIXME: add underbars for readability
    public String LOADSUCCESS = "load_success";
    public String GETSUCCESS = "get_success";
    public String PREVIEWSUCCESS = "preview_success";
    public String PREVIEWFAILURE = "preview_failure";
    public String CREATESUCCESS = "create_success";
    public String CREATE_FAILURE = "create_failure";
    public String PREVIOUSSUCCESS = "previous_success";
    public String GET_FAILURE = "get_failure";

    public String BRANCHOFFICESLIST = "BranchOfficesList";
    public String LOANOFFICERSLIST = "LoanOfficersList";
    public String CUSTOMERSLIST = "CustomersList";

    public String MEETINGDATE = "MeetingDate";
    public String PRODUCTSLIST = "ProductsList";
    public String ACCOUNTSLIST = "AccountsList";
    public String CUSTOMERCOUNT = "CustomerCount";
    public String PRODUCTSIZE = "ProductsSize";
    public String CUSTOMERATTENDANCETYPES = "CustAttTypes";
    public String PAYMENT_TYPES_LIST = "PaymentTypesList";
    public String SAVINGSPRODUCTSLIST = "SavingsProductsList";

    public Short LOANDISB = 1;
    public Short LOANCOLL = 2;
    public Short CUSTOMERCOLL = 3;
    public Short SAVINGSCOLL = 4;

    public String METHOD = "method";
    public String PREVIEWMETHOD = "preview";
    public String LOADMETHOD = "load";
    public String CANCELMETHOD = "cancel";
    public String GETMETHOD = "get";
    public String PREVIOUSMETHOD = "previous";
    public String CREATEMETHOD = "create";
    public String VALIDATEMETHOD = "validate";

    public String CENTER = "Center";

    public String BULKENTRYINVALIDAMOUNT = "errors.invalidamount";
    public String INVALID_RECEIPT_DATE = "errors.invaliddate";
    public String ERRORINVALIDAMOUNT = "errors.invalidamountvalue";
    public String MANDATORYFIELDS = "errors.mandatoryselect";
    public String MANDATORYENTER = "errors.mandatoryenter";
    public String INVALIDENDDATE = "errors.invaliddate";
    public String INVALIDDATE = "errors.invaliddate";
    public String MEETINGDATEEXCEPTION = "errors.meetingdate";
    public String ERRORSUPDATE = "errors.update";
    public String DATABASE_ERROR = "errors.database";
    public String REFRESH = "refresh";

    public String PRESENT = "1";

    public Short LOANREPAYMENT = 1;
    public Short SAVINGSDEPOSIT = 2;
    public Short LOANDISBURSEMENT = 3;
    public Short SAVINGSWITHDRAWAL = 4;

    public String ISBACKDATEDTRXNALLOWED = "isBackDatedTrxnAllowed";
    public String ISCENTERHIERARCHYEXISTS = "isCenterHierarchyExists";
    public String LOANS = "loans";
    public String SAVINGS = "savings";
    public String COLLECTION_SHEET_ENTRY = "collectionSheetEntry";
    public String CUSTOMERACCOUNTS = "customeraccounts";
    public String ERRORLOANS = "errorloans";
    public String ERRORSAVINGSDEPOSIT = "errorsavingsdeposit";
    public String ERRORSAVINGSWITHDRAW = "errorsavingswithdraw";

    public String AC_COLLECTION = "bulkEntry.acCollection";
    public String DUE_COLLECTION = "bulkEntry.dueCollections";
    public String ISSUE_WITHDRAWAL = "bulkEntry.issuesWithdrawals";
    public String ATTN = "bulkEntry.attn";
    public String ACCOUNT_GROUP_CENTER = "bulkEntry.accountGroupCenter";
    public String TOTAL_GROUP_CENTER = "bulkEntry.totalGroupCenter";
    public String TOTAL = "bulkEntry.total";
    public String TOTAL_COLLECTION = "bulkEntry.totalCollections";
    public String TOTAL_ISSUE_WITHDRAWAL = "bulkEntry.totalIssuesWithdrawals";
    public String DUE_COLLECTION2 = "bulkEntry.dueCollections2";
    public String LOAN_DISBURSEMENT = "bulkEntry.loanDisbursements";
    public String LOAN_REPAYMENT = "bulkEntry.loanRepayments";
    public String NET_CASH = "bulkEntry.netCash";
    public String WITHDRAWAL = "bulkEntry.withdrawals";
    public String OTHER_COLLECTION = "bulkEntry.otherCollections";
    public String MODE_OF_PAYMENT = "bulkEntry.pmnttype";
    public String SAVING_DEPOSIT = "bulkEntry.savingsDeposit";
    public String SAVING_WITHDRAWAL = "bulkEntry.savingsWithdrawal";
    public String CLIENT_NAME = "bulkEntry.clientName";
    public String DATEOFTRXN = "bulkEntry.dateoftrxn";
    public String LOANOFFICERS = "bulkEntry.loanofficer";
    public String RECEIPTDATE = "bulkEntry.rcptdate";
}
