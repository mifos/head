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

package org.mifos.application.accounts.util.helpers;

/**
 * This interface stores constants common to all accounts.
 * 
 */
public interface AccountConstants {
    public static final String GETPRDOFFERINGS = "getPrdOfferings";
    public static final String CUSTOMGETPRDOFFERINGS = "customGetPrdOfferigs";
    public static final String GETPRDOFFERINGS_SUCCESS = "getPrdOfferigs_success";

    public static final String GET_PRDOFFERINGS_SUCCESS = "getPrdOfferings_success";
    public static final String PRD_OFFERINGS = "prdOfferings";
    public static final String ENTITY_TYPE = "entityType";
    // used to add an attribute to business results
    public static final String CUSTOMERMASTER = "customerMaster";
    public static final String SELECTEDPRDOFFERING = "selectedPrdOffering";
    // used for query parameters in hql
    public static final String CUSTOMERID = "customerId";
    public static final String PRDTYPEID = "prdTypeId";
    public static final String PRDSTATUS = "prdStatus";
    public static final String PRDOFFERINGID = "prdofferingId";
    public static final String PRODUCT_APPLICABLE_TO = "prdApplicableMaster";
    public static final Object GLOBALACCOUNTNUM = "globalAccountNum";

    public static final String STATECURRENTLYINUSE = "1";

    public static final String ACCOUNTSSEARCHDEPENDENCY = "AccountsSearch";
    public static final String ACCOUNTSAPPLYCHARGESDEPENDENCY = "AccountsApplyCharges";

    public static final String ACCOUNT_TRXN = "AccountTrxn";
    public static final String ACCOUNT_GETINSTALLMENTS = "getInstallmentHistory";
    public static final String ACCOUNT_TRXN_HISTORY = "getTrxnHistory";

    // for account notes

    public static final String ACCOUNT_NOTES_VO = "AccountNotesVO";
    public static final String ACCOUNT_NOTES = "AccountNotes";
    public static final String ACCOUNT_NOTE = "AccountNote";
    public static final String GET_SUCCESS = "get_success";
    public static final String LOAD_SUCCESS = "load_success";
    public static final String LOAN_DETAILS_PAGE = "loan_details_page";
    public static final String NOTES = "notes";
    public static final String STATUS = "status";
    public static final String TRXN_TYPES = "trxnTypes";
    public static final int NOTES_COUNT = 3;
    public static final String METHOD_CANCEL = "cancel";
    public static final String METHOD_SEARCH_NEXT = "searchNext";
    public static final String METHOD_SEARCH = "search";
    public static final String METHOD_SEARCH_PREV = "searchPrev";
    public static final String METHOD_LOAD = "load";
    public static final String METHOD_GET = "get";
    public static final String METHOD_PREVIEW = "preview";
    public static final String METHOD_PREVIOUS = "previous";
    public static final String METHOD_UPDATE = "update";

    // exceptions
    public static final String UNKNOWN_EXCEPTION = "error.unnkownexception";
    /**
     * key string for putting business result account action date
     */
    public static final String ACCOUNT_ACTION_DATE_KEY = "accountDate";
    /**
     * key string for putting business result account action payment
     */
    public static final String ACCOUNT_PAYMENT_KEY = "accountPayment";

    public static final String NO_TRANSACTION_POSSIBLE = "error.noTransactionPossible";

    public static final String SEARCH_STRING = "searchString";

    public static final String VIEWCLIENTCHARGES = "ViewClientCharges";
    public static final String VIEWGROUPCHARGES = "ViewGroupCharges";
    public static final String VIEWCENTERCHARGES = "ViewCenterCharges";
    public static final String APPLYLOANCHARGES = "reviewTransactionPage";
    public static final String FEEFORMULAPATH = "org.mifos.application.fees.util.valueobjects.FeeFormula";
    public static final String FEEFORMULAID = "feeFormulaId";
    public static final String KEYNOMEETING = "errors.nomeeting";
    public static final String UNEXPECTEDERROR = "errors.unexpected";
    public static final String NOMOREINSTALLMENTS = "errors.nomoreinstallments";
    public static final String MISC_CHARGE_NOT_APPLICABLE = "error.miscchargenotapplicable";
    // Misc Fees and Penalty
    public static final String MISC_FEES = "-1";
    public static final String MISC_PENALTY = "-2";

    /*
     * Payment Status public static final Short PAYMENT_UNPAID=0; public static final Short
     * PAYMENT_PAID=1;
     */

    public static final String SAVINGS = "savings";
    public static final String CUSTOMERS_FOR_SAVINGS_ACCOUNT = "customersForSavingsAccount";
    public static final String GLOBAL_ACCOUNT_NUMBER = "globalAccountNumber";
    // Errors
    public static final String VERSIONNOMATCHINGPROBLEM = "error.versionnodonotmatch";
    public static final String RATE_ERROR = "errors.rateException";
    public static final String RATE = "Rate";
    // AccountTypes
    // public static Short LOAN_TYPE=1;
    // public static Short SAVING_TYPE=2;

    // For loan next meeting date, amount due, amount in arrears

    public static final String LOAN_NEXT_MEETING_DATE = "LoanNextMeeetingDate";
    public static final String LOAN_AMOUNT_DUE = "LoanAmountDue";
    public static final String LOAN_AMOUNT_IN_ARREARS = "LoanAmountInArrears";

    public static final String FEES_APPLIED = "charged";
    public static final String FEES_REMOVED = "removed";
    public static final String MISC_PENALTY_APPLIED = "Misc penalty applied";
    public static final String MISC_FEES_APPLIED = "Misc fees applied";
    public static final String LAST_PAYMENT_ACTION = "lastPaymentAction";

    public static final String REMOVE_SUCCESS = "remove_success";
    public static final String REMOVE = "remove";
    public static final String CHARGES = "charges";

    public static final String ERROR_MANDATORY = "errors.mandatory";
    public static final String ERROR_FUTUREDATE = "errors.futuredate";
    public static final String ERROR_INVALIDDATE = "errors.invaliddate";
    public static final String ERROR_INVALID_TRXN = "errors.invalidTxndate";
    public static final String ERROR_INVALID_PERSONNEL = "errors.invalidPersonnel";
    public static final String ERRORS_MUST_BE_GREATER_THAN_ZERO = "errors.mustBeGreaterThanZero";
    public static final String WAIVE_TYPE = "WaiveType";
    public static final short COMMENT_LENGTH = 500;
    public static final String MAX_LENGTH = "errors.maximumlength";
    public static final String APPLICABLE_CHARGE_LIST = "applicableChargeList";

    public static final String NOT_SUPPORTED_GRACE_TYPE = "errors.not_supported_gracetype";
    public static final String INTERESTDEDUCTED_PRINCIPALLAST = "errors.interestdedcuted_principallast";
    public static final String PRINCIPALLASTPAYMENT_INVALIDGRACETYPE = "errors.principallast_invalidgrace";
    public static final String INTERESTDEDUCTED_INVALIDGRACETYPE = "errors.interestdeducted_invalidgrace";
    public static final String NOT_SUPPORTED_EMI_GENERATION = "errors.emitype_not_supported";
    public static final String DATES_MISMATCH = "errors.datemismatch";
    public static int INTEREST_DAYS_360 = 360;
    public static int INTEREST_DAYS_365 = 365;

    // public static final int INTEREST_DAYS=365;
    public static final int DAYS_IN_WEEK = 7;
    public static final int DAYS_IN_MONTH = 30;

    public static final String WEEK_INSTALLMENT = "Week";
    public static final String MONTH_INSTALLMENT = "Month";

    public static final String NOT_SUPPORTED_DURATION_TYPE = "errors.not_supported_durationtype";
    public static final String NOT_SUPPORTED_INTEREST_DAYS = "errors.not_supported_interestdays";
    public static final String MAX_NOTE_LENGTH = "errors.maximumlengthForNotes";
    public static final String CLOSEDLOANACCOUNTSLIST = "ClosedLoanAccountsList";
    public static final String CLOSEDSAVINGSACCOUNTSLIST = "ClosedSavingsAccountsList";

    public static final String LOAN_DISBURSAL = "Loan Disbursal";
    public static final String PAYMENT_RCVD = "Payment rcvd.";
    public static final String LOAN_REPAYMENT = "Loan Repayment";
    public static final String LOAN_WRITTEN_OFF = "Loan Written Off";
    public static final String WAIVED = " waived";
    public static final String AMOUNT = "Amnt ";
    public static final String LOAN_ADJUSTED = "Loan Adjusted";
    public static final String APPLIED = " applied";
    public static final String AMNT_ADJUSTED = "Amnt Adjusted";
    public static final String AMNT_WAIVED = "Amnt waived";
    public static final String LOAN_RESCHEDULED = "Loan Rescheduled";
    public static final String ACCOUNT_AMOUNT = "accounts.amount";

    public static final String SURVEY_KEY = "accountSurveys";
    public static final String SELECTED_ACCOUNT_STATUS = "SelectedAccountStatus";

    public static final String GL_CODE = "glCode";
}
