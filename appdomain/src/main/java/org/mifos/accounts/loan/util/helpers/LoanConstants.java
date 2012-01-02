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

package org.mifos.accounts.loan.util.helpers;

public interface LoanConstants {
    String LOANACTIONFORM = "loanActionForm";
    String LOANPRDOFFERINGS = "loanPrdOfferings";
    String LOANAMOUNT = "amount";
    String INTERESTRATE = "Interest rate";
    String NO_OF_INST = "No. of installments";

    // used as query parameters
    String PRDTYPEID = "prdTypeID";
    String PRDSTATUS = "prdStatus";
    String PRDAPPLICABLEMASTER1 = "prdApplicableMaster1";
    String PRDAPPLICABLEMASTER2 = "prdApplicableMaster2";
    String CATEGORYID = "categoryId";
    String CATEGORYIDVALUE = "5";
    String PRDOFFERINGID = "prdOfferingId";

    // used in case o optional states.
    String ISPENDINGAPPROVAL = "isPendingApproval";

    // used for setting in context
    String LOANACCOUNTSTAES = "loanAccountStates";
    String APPLICALEFEES = "applicableFees";
    String LOANACCGLOBALNUM = "loanAccGlobalNum";
    String LOANACCOUNTID = "accountId";
    String INTDEDATDISBFLAG = "interestDedAtDisbFlag";
    String INPUTPAGE = "inputPage";
    String LOANMEETING = "loanMeeting";
    String REPAYMENTSCHEDULE = "repaymentSchedule";
    String REPAYMENTSCHEDULEINSTALLMENTS = "repaymentScheduleInstallments";
    String RECENTACCOUNTACTIVITIES = "recentAccountActivities";
    String LOAN_ALL_ACTIVITY_VIEW = "loanAllActivityView";
    String VIEW_UPCOMING_INSTALLMENT_DETAILS = "viewUpcomingInstallmentDetails";
    String VIEW_OVERDUE_INSTALLMENT_DETAILS = "viewOverDueInstallmentDetails";
    String TOTAL_AMOUNT_OVERDUE = "totalAmountOverDue";
    String CURRENT_DATE = "currentDate";
    String NEXTMEETING_DATE = "nextMeetingDate";
    String FEEFORMULALIST = "feeFormulaList";
    String CLIENT_LIST = "clientList";

    // account status

    String LOAN_OFFICERS = "loanOfficers";
    String ACCOUNTS_LIST = "accountsList";
    String SEARCH_RESULTS = "searchResults";
    String STATUS_LIST = "statusList";
    String FLAG_LIST = "flagList";
    String LOAN_STATUS_VO = "LoanStatusVO";
    String LOAN_STATUS_ACTION = "LoanStatusAction";

    String OLD_STATUS_NAME = "oldStatusName";
    String NEW_STATUS_NAME = "newStatusName";
    String FLAG_NAME = "flagName";
    String CHECKLIST = "checklist";
    String GET_STATUS_HISTORY = "getStatusHistory";
    String SEARCH_SUCCESS = "search_success";
    String STATUS_HISTORY = "statusHistory";
    String MANDATORY = "errors.mandatory";
    short COMMENT_LENGTH = 500;
    String MAX_LENGTH = "errors.maximumlength";
    String NOTES = "notes";
    String MANDATORY_SELECT = "errors.mandatoryselect";
    short CANCELLED = 10;
    short CLOSED = 11;
    String FLAG = "flag";
    String MANDATORY_TEXTBOX = "errors.mandatory_textbox";

    // view change log
    String LOAN_CHANGE_LOG = "LoanChangeLog";
    String LOAN_CHANGE_LOG_LIST = "LoanChangeLogList";
    String CHANGELOGLIST = "changeLogList";
    short LOAN_ENTITY_TYPE = 7;
    String INCOMPLETE_CHECKLIST = "error.incompletechecklist";
    String LOAN_ACCOUNT_OWNER_IS_A_GROUP = "loanaccountownerisagroup";
    String LOANACCOUNTOWNERISACLIENT = "loanaccountownerisaclient";

    // disburse loan
    String GET_LOAD_DISBURSEMENT_DATA = "getDataOnLoadOfDisbursement";
    String DISBURSE_LOAN = "disburseLoan";

    // View installments details
    String GET_INSTALLMENT_DETAILS = "getInstallmentDetails";
    String WAIVE = "waive";

    String VIEWINSTALLMENTDETAILS_SUCCESS = "viewInstmentDetails_success";

    // Apply adjustments
    String LOAD_ADJUSTMENTS = "loadAdjustments";
    String MAKE_ADJUSTMENTS = "makeAdjustments";

    // Redo Loan
    String PERSPECTIVE_VALUE_REDO_LOAN = "redoLoan";

    // Repay Loan
    String LOAD_EARLY_REPAYMENT_DETAILS = "loadEarlyRepaymentDetails";
    String MAKE_EARLY_REPAYMENT_DETAILS = "makeEarlyRepaymentDetails";

    Short PAYMENT_RECIEVED = 1; // check
    Short PAYMENT_NOT_RECIEVED = 0;

    /**
     * @deprecated interest deducted at disbursement not supported since version 1.1!!
     */
    @Deprecated
    Short INTEREST_DEDUCTED_AT_DISBURSEMENT = 1;

    String TOTAL_REPAYMENT_AMOUNT = "totalRepaymentAmount";
    String WAIVED_REPAYMENT_AMOUNT = "waivedRepaymentAmount";
    String DONOT_PAY_FEES_PENALTY_INTEREST = "doNotPayFeesPenaltyAndInterest";
    String PAY_FEES_PENALTY_INTEREST = "payFeesPenaltyAndInterest";
    String PAY_FEES_PENALTY = "payFeesPenalty";
    String WAIVER_INTEREST = "waiverInterest";

    String STATUS_CHANGE_NOT_ALLOWED = "error.statuschangenotallowed";
    String FUTUREDISBURSALDATE = "FutureDate";
    String HANDLE_CHANGE_IN_PRODUCT_DEFINITION = "exceptions.application.loan.changeInLoanOffering";

    String ERROR = "error";

    String LOAN_AMOUNT_KEY = "loan.amount";
    String LOAN_ADDITIONAL_FEE_KEY = "loan.additionalFee";
    String LOAN_DEFAULT_FEE_KEY = "loan.defaultFee";
    String LOAN_INTEREST_RATE_KEY = "loan.interestRate";
    String LOANACCOUNTOWNER = "loanAccountOwner";
    String CUSTOMER = "customer";
    String CUSTOMERNOTSELECTEDERROR = "errors.customernotselected";
    String LOANOFFERINGNOTSELECTEDERROR = "errors.loanofferingnotselected";
    String INSTANCENAME = "instance name";
    String LOANOFFERING = "loanOffering";
    String LOANFUNDS = "loanfunds";
    String GRACEPERIODDURATION = "Grace period for repayments";
    String GRACEPERIODERROR = "errors.graceper";
    String ADDITIONAL_FEES_LIST = "additionalFeeList";
    String ERRORS_DUPLICATE_PERIODIC_FEE = "errors.loan.duplicatePeriodicFee";
    String ERRORS_MUST_BE_GREATER_THAN_ZERO = "errors.mustBeGreaterThanZero";
    String ERRORS_HAS_INVALID_FORMAT= "errors.hasInvalidFormat";
    String ERRORS_MUST_NOT_BE_NEGATIVE = "errors.mustNotBeNegative";
    String ERRORS_SPECIFY_FEE_AMOUNT = "errors.loan.specifyFeeAmount";
    String FEE = "Fee";
    String PROPOSED_DISBURSAL_DATE = "proposedDisbDate";
    String RECURRENCEID = "recurrenceId";

    String RECURRENCENAME = "recurrenceName";
    String NOSEARCHRESULTS = "errors.nosearchresults";
    String FEE_WAIVED = "Fee waived";
    String PENALTY_WAIVED = "Penalty waived";
    String CUSTOM_FIELDS = "customFields";
    String ERRORS_SPECIFY_CUSTOM_FIELD_VALUE = "errors.Account.specifyCustomFieldValue";

    String ERRORS_CUSTOM_DATE_FIELD = "errors.Account.customdatefield";
    String MULTIPLE_LOANS_ACTION_FORM = "multipleloansactionform";
    String MULTIPLE_LOANS_OFFICES_LIST = "multipleloansofficeslist";
    String MULTIPLE_LOANS_LOAN_OFFICERS_LIST = "multipleloansloanofficerslist";
    String IS_CENTER_HIERARCHY_EXISTS = "isCenterHierarchyExists";
    String MULTIPLE_LOANS_CENTERS_LIST = "multipleloanscenterslist";
    String LOANOFFICERS = "Loan officer";
    String LOAN_AMOUNT_FOR = "Loan Amount for ";

    String APPL_RECORDS = "applicableRecords";
    String REVERSE_LOAN_DIBURSAL_ACTION_FORM = "reverseloandisbactionform";
    String LOANACTIVEINGOODSTAND = "loanactiveingoodstand";
    String LOANACTIVEINBADSTAND = "loanactiveinbadstand";
    String ACCOUNTTYPE_ID = "accounttype_id";
    String ERROR_LOAN_ACCOUNT_ID = "errors.specifyloanaccountId";
    String NOTE = "Note";
    String PAYMENTS_LIST = "payments";
    String PAYMENTS_SIZE = "paymentssize";
    String BRANCHID = "branchId";
    String LOANOFFICERID = "loanOfficerId";
    String LOANPRODUCTID = "loanProductId";
    String LOAN_INDIVIDUAL_MONITORING_IS_ENABLED = "loanIndividualMonitoringIsEnabled";
    /**
     * Toggles whether or not the <strong><em>disbursement</em></strong> day for
     * a loan can be different from the meeting day.
     */
    String REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED = "repaymentSchedulesIndependentOfMeetingIsEnabled";
    String MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY = "minDaysBetweenDisbursalAndFirstRepaymentDay";
    String MAX_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY = "maxDaysBetweenDisbursalAndFirstRepaymentDay";
    String PRORATE_RULE="prorate_Rule";
    String ADMINISTRATIVE_DOCUMENT_IS_ENABLED = "AdministrativeDocumentsIsEnabled";
    String MIN_RANGE_IS_NOT_MET = "errors.minrangeisnotmet";
    String MAX_RANGE_IS_NOT_MET = "errors.maxrangeisnotmet";

    String METHODCALLED = "methodCalled";
    // Configurable mandatory fields
    String PURPOSE_OF_LOAN = "PurposeOfLoan";
    String SOURCE_OF_FUND = "SourceOfFund";
    String EXTERNAL_ID = "ExternalId";
    String DISPLAY_DAY_NUMBER = "displayDayNumber";
    String LOANACCOUNT = "loanBO";
    String LOAN_ACCOUNT_OWNER_IS_GROUP_YES = "yes";
    int MINIMUM_NUMBER_OF_CLIENTS_IN_GROUP_LOAN = 2;
    int GLIM_ENABLED_VALUE = 1;

    int GLIM_DISABLED_VALUE = 0;
    String INSTALLMENTS = "installments";
    String WAIVER_INTEREST_SELECTED = "waiverInterestSelected";
    String WAIVER_INTEREST_NOT_CONFIGURED= "loan.waiverInterest.not.configured";
    String ORIGINAL_INSTALLMENTS = "originalInstallments";

    String CANNOT_VIEW_REPAYMENT_SCHEDULE = "cannot.view.repayment.schedule";
    String QUESTION_GROUP_INSTANCES = "questionGroupInstances";
}
