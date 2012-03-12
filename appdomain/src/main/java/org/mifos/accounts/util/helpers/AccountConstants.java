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

package org.mifos.accounts.util.helpers;

/**
 * This public interface stores constants common to all accounts.
 *
 */
public interface AccountConstants {
    String GETPRDOFFERINGS = "getPrdOfferings";
    String CUSTOMGETPRDOFFERINGS = "customGetPrdOfferigs";
    String GETPRDOFFERINGS_SUCCESS = "getPrdOfferigs_success";

    String GET_PRDOFFERINGS_SUCCESS = "getPrdOfferings_success";
    String PRD_OFFERINGS = "prdOfferings";
    String ENTITY_TYPE = "entityType";
    // used to add an attribute to business results
    String CUSTOMERMASTER = "customerMaster";
    String SELECTEDPRDOFFERING = "selectedPrdOffering";
    // used for query parameters in hql
    String CUSTOMERID = "customerId";
    String PRDTYPEID = "prdTypeId";
    String PRDSTATUS = "prdStatus";
    String PRDOFFERINGID = "prdofferingId";
    String PRODUCT_APPLICABLE_TO = "prdApplicableMaster";
    Object GLOBALACCOUNTNUM = "globalAccountNum";

    String STATECURRENTLYINUSE = "1";

    String ACCOUNTSSEARCHDEPENDENCY = "AccountsSearch";
    String ACCOUNTSAPPLYCHARGESDEPENDENCY = "AccountsApplyCharges";

    String ACCOUNT_TRXN = "AccountTrxn";
    String ACCOUNT_GETINSTALLMENTS = "getInstallmentHistory";
    String ACCOUNT_TRXN_HISTORY = "getTrxnHistory";

    // for account notes

    String ACCOUNT_NOTES_VO = "AccountNotesVO";
    String ACCOUNT_NOTES = "AccountNotes";
    String ACCOUNT_NOTE = "AccountNote";
    String GET_SUCCESS = "get_success";
    String LOAD_SUCCESS = "load_success";
    String LOAN_DETAILS_PAGE = "loan_details_page";
    String NOTES = "notes";
    String STATUS = "status";
    String TRXN_TYPES = "trxnTypes";
    int NOTES_COUNT = 3;
    String METHOD_CANCEL = "cancel";
    String METHOD_SEARCH_NEXT = "searchNext";
    String METHOD_SEARCH = "search";
    String METHOD_SEARCH_PREV = "searchPrev";
    String METHOD_LOAD = "load";
    String METHOD_GET = "get";
    String METHOD_PREVIEW = "preview";
    String METHOD_PREVIOUS = "previous";
    String METHOD_UPDATE = "update";

    // exceptions
    String UNKNOWN_EXCEPTION = "error.unnkownexception";
    /**
     * key string for putting business result account action date
     */
    String ACCOUNT_ACTION_DATE_KEY = "accountDate";
    /**
     * key string for putting business result account action payment
     */
    String ACCOUNT_PAYMENT_KEY = "accountPayment";

    String NO_TRANSACTION_POSSIBLE = "error.noTransactionPossible";

    String SEARCH_STRING = "searchString";

    String VIEWCLIENTCHARGES = "ViewClientCharges";
    String VIEWGROUPCHARGES = "ViewGroupCharges";
    String VIEWCENTERCHARGES = "ViewCenterCharges";
    String APPLYLOANCHARGES = "reviewTransactionPage";
    String FEEFORMULAPATH = "org.mifos.accounts.fees.util.valueobjects.FeeFormula";
    String FEEFORMULAID = "feeFormulaId";
    String KEYNOMEETING = "errors.nomeeting";
    String UNEXPECTEDERROR = "errors.unexpected";
    String NOMOREINSTALLMENTS = "errors.nomoreinstallments";
    String MISC_CHARGE_NOT_APPLICABLE = "error.miscchargenotapplicable";
    // Misc Fees and Penalty
    String MISC_FEES = "-1";
    String MISC_PENALTY = "-2";

    /*
     * Payment Status Short PAYMENT_UNPAID=0; Short
     * PAYMENT_PAID=1;
     */

    String SAVINGS = "savings";
    String CUSTOMERS_FOR_SAVINGS_ACCOUNT = "customersForSavingsAccount";
    String GLOBAL_ACCOUNT_NUMBER = "globalAccountNumber";
    // Errors
    String VERSIONNOMATCHINGPROBLEM = "error.versionnodonotmatch";
    String RATE_ERROR = "errors.rateException";
    String DOUBLE_ERROR = "errors.double";
    String RATE = "Rate";
    String RATE_AMOUNT = "Rate Amount";
    // AccountTypes
    // Short LOAN_TYPE=1;
    // Short SAVING_TYPE=2;

    // For loan next meeting date, amount due, amount in arrears

    String LOAN_NEXT_MEETING_DATE = "LoanNextMeeetingDate";
    String LOAN_AMOUNT_DUE = "LoanAmountDue";
    String LOAN_AMOUNT_IN_ARREARS = "LoanAmountInArrears";

    String FEES_APPLIED = "charged";
    String FEES_REMOVED = "removed";
    String MISC_PENALTY_APPLIED = "Misc penalty applied";
    String MISC_FEES_APPLIED = "Misc fees applied";
    String LAST_PAYMENT_ACTION = "lastPaymentAction";

    String REMOVE_SUCCESS = "remove_success";
    String REMOVE = "remove";
    String CHARGES = "charges";

    String ERROR_MANDATORY = "errors.mandatory";
    String ERROR_FUTUREDATE = "errors.futuredate";
    String ERROR_INVALIDDATE = "errors.invaliddate";
    String ERROR_INVALID_TRXN = "errors.invalidTxndate";
    String ERROR_INVALID_PERSONNEL = "errors.invalidPersonnel";
    String ERRORS_MUST_BE_GREATER_THAN_ZERO = "errors.mustBeGreaterThanZero";
    String WAIVE_TYPE = "WaiveType";
    short COMMENT_LENGTH = 500;
    String MAX_LENGTH = "errors.maximumlength";
    String APPLICABLE_CHARGE_LIST = "applicableChargeList";

    String ERROR_ADJUSTMENT_PREVIOUS_DATE = "errors.adjustmentPreviousDate";
    String ERROR_ADJUSTMENT_NEXT_DATE = "errors.adjustmentNextDate";
    
    String NOT_SUPPORTED_GRACE_TYPE = "errors.not_supported_gracetype";
    String INTERESTDEDUCTED_PRINCIPALLAST = "errors.interestdedcuted_principallast";
    String PRINCIPALLASTPAYMENT_INVALIDGRACETYPE = "errors.principallast_invalidgrace";
    String INTERESTDEDUCTED_INVALIDGRACETYPE = "errors.interestdeducted_invalidgrace";
    String NOT_SUPPORTED_EMI_GENERATION = "errors.emitype_not_supported";
    String DATES_MISMATCH = "errors.datemismatch";
    int INTEREST_DAYS_360 = 360;
    int INTEREST_DAYS_365 = 365;

    // int INTEREST_DAYS=365;
    int DAYS_IN_WEEK = 7;
    int DAYS_IN_MONTH = 30;

    String WEEK_INSTALLMENT = "Week";
    String MONTH_INSTALLMENT = "Month";

    String NOT_SUPPORTED_DURATION_TYPE = "errors.not_supported_durationtype";
    String NOT_SUPPORTED_INTEREST_DAYS = "errors.not_supported_interestdays";
    String MAX_NOTE_LENGTH = "errors.maximumlengthForNotes";
    String CLOSEDLOANACCOUNTSLIST = "ClosedLoanAccountsList";
    String CLOSEDSAVINGSACCOUNTSLIST = "ClosedSavingsAccountsList";

    String LOAN_DISBURSAL = "Loan Disbursal";
    String PAYMENT_RCVD = "Payment rcvd.";
    String LOAN_REPAYMENT = "Loan Repayment";
    String LOAN_WRITTEN_OFF = "Loan Written Off";
    String WAIVED = " waived";
    String AMOUNT = "Amnt ";
    String LOAN_ADJUSTED = "Loan Adjusted";
    String APPLIED = " applied";
    String AMNT_ADJUSTED = "Amnt Adjusted";
    String AMNT_WAIVED = "Amnt waived";
    String LOAN_RESCHEDULED = "Loan Rescheduled";
    String ACCOUNT_AMOUNT = "accounts.amount";

    String SURVEY_KEY = "accountSurveys";
    String SELECTED_ACCOUNT_STATUS = "SelectedAccountStatus";

    String GL_CODE = "glCode";
    String INSTALLMENT_DUEDATE_INVALID = "errors.installment.duedate.invalid";
    String INSTALLMENT_DUEDATE_SAME_AS_DISBURSE_DATE = "errors.installment.duedate.same.as.disburse.date";
    String INSTALLMENT_DUEDATE_IS_HOLIDAY = "errors.installment.duedate.is.holiday";
    String INSTALLMENT_DUEDATE_BEFORE_DISBURSE_DATE = "errors.installment.duedate.before.disburse.date";
    String INSTALLMENT_DUEDATE_DUPLICATE = "errors.installment.duedate.duplicate";
    String INSTALLMENT_DUEDATE_INVALID_ORDER = "errors.installment.duedate.invalid.order";
    String INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP = "errors.installment.duedate.within.min.gap";
    String INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP = "errors.installment.duedate.beyond.max.gap";
    String INSTALLMENT_AMOUNT_LESS_THAN_MIN_AMOUNT = "errors.installment.amount.less.than.min.amount";
    String INSTALLMENT_AMOUNT_LESS_THAN_INTEREST_FEE = "errors.installment.amount.less.than.interest.fee";
    String INSTALLMENT_TOTAL_AMOUNT_INVALID = "errors.installment.total.amount.invalid";
    String GENERIC_VALIDATION_ERROR = "Generic Error";

    String BEYOND_CASHFLOW_THRESHOLD="warning.cashflow_installment_diff_beyond_threshold";
    String CUMULATIVE_CASHFLOW_NEGATIVE = "warning.cumulative_cashflow_negative";
    String CUMULATIVE_CASHFLOW_ZERO = "warning.cumulative_cashflow_zero";
    String INSTALLMENT_BEYOND_CASHFLOW_DATE = "errors.installment_due_date_beyond_cashflow_date";
    String REPAYMENT_CAPACITY_LESS_THAN_ALLOWED = "errors.repayment.capacity.less.than.allowed";

    String NO_COMMENT = "";
    String ERROR_PAYMENT_DATE_BEFORE_LAST_PAYMENT = "errors.payment.date.before.last.payment";
    
    String PENALTIES_REMOVED = "removed";
}
