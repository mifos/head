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

package org.mifos.customers.util.helpers;

public interface CustomerConstants {
    String CUSTOMER_NOTE_ACTION = "CustomerNoteAction";
    String CUSTOMER_HISTORICAL_DATA_ACTION = "CustomerHistoricalDataAction";
    String CUSTOMER_HISTORICAL_DATA_VO = "CustomerHistoricalDataVO";
    String CUSTOMER_HISTORICAL_DATA = "customerHistoricalData";
    String MFIJOININGDATE = "mfiJoiningDate";
    String CUSTOMERHISTORICALDATAACTIONFORM = "custHistoricalDataActionForm";
    String CUSTOMER_NOTE_VO = "CustomerNoteVO";
    String PERSONNEL_NAME = "personnelName";
    String GLOBAL_CUST_NUM = "globalCustNum";
    String BLANK = " ";
    String CUSTOMER_NAME = "customerName";

    /*
     * Like CustomerStatus.CENTER_ACTIVE.getValue() but usable in case
     * statements
     */
    short CENTER_ACTIVE_STATE = 13;
    short CENTER_INACTIVE_STATE = 14;

    String OLD_STATUS = "oldStatus";
    String NEW_STATUS = "newStatus";
    String NEW_FLAG = "newFlag";
    String IS_TRAINED = "isTrained";
    String IS_CLIENT_STATUS_ACTIVE = "isActive";
    short TRAINED_NO = 2;
    String TRAINED_DATE = "trainedDate";

    String CLIENT_STATUS = "CLIENT_STATUS";
    String CLIENT_STATUS_PARTIAL = "Partial Application";
    String LOAN_OFFICER_ID = "LOAN_OFFICER_ID";
    short All_CATEGORY_ID = 1;

    /** Exceptions */
    String UNKNOWN_EXCEPTION = "error.unknownexception";
    String CUSTOMER_NOT_FOUND = "error.customernotfound";
    String LOAN_OFFICER_REQUIRED_EXCEPTION = "LoanOfficerRequiredException";
    String NAME_REQUIRED_EXCEPTION = "NameRequiredException";
    String LOAN_OFFICER_BLANK_EXCEPTION = "LoanOfficerBlankException";
    String FORMEDBY_LOANOFFICER_BLANK_EXCEPTION = "FormedByLoanOfficerBlankException";
    String STATUS_REQUIRED_EXCEPTION = "StatusRequiredException";
    String GET_OFFICE_HOMEPAGE = "getOfficeHomePage";

    String DUPLICATE_FEE_EXCEPTION = "DuplicateFeeException";
    String DUPLICATE_GOVT_ID_EXCEPTION = "DuplicateGovtIdException";
    String CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION = "Customer.LoanOfficerInactive";
    String CUSTOMER_STATUS_NOTES_EXCEPTION = "Customer.StatusNotesRequired";
    String CUSTOMER_BRANCH_INACTIVE_EXCEPTION = "Customer.BranchInactive";
    String CUSTOMER_FEE_INACTIVE_EXCEPTION = "Customer.FeesInactive";
    String CUSTOMER_DUPLICATE_CUSTOMERNAME_EXCEPTION = "Customer.DuplicateCustomerName";
    String CUSTOMER_INVALID_VERSION_EXCEPTION = "Customer.InvalidVersion";
    String MEETING_REQUIRED_EXCEPTION = "Customer.MeetingRequired";
    String CREATE_FAILED_EXCEPTION = "Customer.CreateFailed";
    String UPDATE_FAILED_EXCEPTION = "Customer.UpdateFailed";
    String CLIENT_HAS_ACTIVE_LOAN_EXCEPTION = "Client.ActiveLoanPresent";
    String CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION = "Customer.ActiveAccountsPresent";
    String INCOMPLETE_CHECKLIST_EXCEPTION = "Customer.IncompleteChecklist";
    /** Action method names */
    String METHOD_CANCEL = "cancel";
    String METHOD_GET = "get";
    String METHOD_SEARCH_NEXT = "searchNext";
    String METHOD_SEARCH_PREV = "searchPrev";
    String METHOD_LOAD = "load";
    String METHOD_LOAD_SEARCH = "loadSearch";
    String METHOD_SEARCH = "search";
    String METHOD_MANAGE = "manage";
    String METHOD_PREVIOUS = "previous";
    String METHOD_LOAD_STATUS = "loadStatus";
    String METHOD_UPDATE_STATUS = "updateStatus";
    String METHOD_CREATE = "create";
    String METHOD_UPDATE = "update";
    String METHOD_NEXT = "next";
    String METHOD_PREVIEW = "preview";
    String METHOD = "method";
    String METHOD_LOAD_ADJUSTMENT = "loadAdjustment";
    String METHOD_PREVIEW_ADJUSTMENT = "previewAdjustment";
    String METHOD_APPLY_ADJUSTMENT = "applyAdjustment";
    String METHOD_LOAD_ADJUSTMENT_SUCCESS = "loadAdjustment_success";
    String METHOD_PREVIEW_ADJUSTMENT_SUCCESS = "previewAdjustment_success";
    String METHOD_APPLY_ADJUSTMENT_SUCCESS = "applyAdjustment_success";
    String METHOD_GET_DETAILS = "getDetails";
    String GET_SUCCESS = "get_success";
    String NOTES_SEARCH = "NotesSearch";
    String CLIENT_DETAILS_PAGE = "client_details_page";
    String GROUP_DETAILS_PAGE = "group_details_page";
    String CENTER_DETAILS_PAGE = "center_details_page";
    String VIEW_CLIENT_CHARGES = "ViewClientCharges";
    String VIEW_GROUP_CHARGES = "ViewGroupCharges";
    String VIEW_CENTER_CHARGES = "ViewCenterCharges";
    String APPLY_ADJUSTMENT_CLIENT_SUCCESS = "applyAdjustment_client_success";
    String APPLY_ADJUSTMENT_GROUP_SUCCESS = "applyAdjustment_group_success";

    String APPLY_ADJUSTMENT_CENTER_SUCCESS = "applyAdjustment_center_success";
    String CANCELADJ_CLIENT_SUCCESS = "canceladj_client_success";
    String CANCELADJ_GROUP_SUCCESS = "canceladj_group_success";
    String CANCELADJ_CENTER_SUCCESS = "canceladj_center_success";

    String SEARCH_FAILURE = "search_failure";
    String SEARCH_FAILURE_TRANSFER = "search_failure_transfer";
    String CURRENT_CUSTOMER_STATUS = "currentStatus";
    String CUSTOMER_SEARCH_INPUT = "customerSearchInput";
    String CLIENT_HAS_ACTIVE_LOAN = "Client.HasActiveLoan";

    String LINK_VALUES = "linkValues";
    String LOAD_SUCCESS = "load_success";
    String IS_HISTORICAL_DATA_PRESENT = "isHistoricalDataPresent";
    String YES = "Yes";
    String NO = "No";
    String FORMEDBY_LOAN_OFFICER_LIST = "formedByLoanOfficers";
    String GROUP_HIERARCHY_REQUIRED = "groupHierarchyRequired";
    String PENDING_APPROVAL_DEFINED = "pendingApprovalDefined";
    String CONFIGURATION_LSM = "configurationLSM";
    /** Request Parameters names */
    String LOAN_OFFICER_LIST = "loanOfficers";

    String DISBURSEMENT_DATE ="disbursementDate";
    String LOAN_AMOUNT="loanAmount";
    String INTEREST_RATE="interestRate";
    String NO_OF_INSTALLMENTS="noOfInstallments";
    String GRACE_PERIOD_DURATION="gracePeriodDuration";
    String FUND_NAME="fundName";
    String INTEREST_DEDUCTED_AT_DISBURSEMENT="interestDeductedAtDisbursement";
    String BUSINESS_ACTIVITY_ID="businessActivityId";
    String COLLATERAL_TYPE_ID="collateralTypeId";
    String COLLATERAL_NOTE="collateralNote";
    String EXTERNAL_ID="externalId";
    String ACCOUNT_FEES="accountFees";

    String STATUS_LIST = "statusList";
    String FEES_LIST = "feesList";
    String ADMIN_FEES_LIST = "adminFeesList";
    String ADDITIONAL_FEES_LIST = "additionalFeeList";
    String CLIENT_LIST = "clients";
    String COLLECTION_SHEET_TYPES = "collectionSheetTypes";
    String CHECKLISTS = "checkLists";
    String POSITIONS = "positions";
    String CUSTOM_FIELDS_LIST = "customFields";

    short CLIENT_PARTIAL = 1;
    short CLIENT_PENDING = 2;
    short CLIENT_APPROVED = 3;
    short CLIENT_ONHOLD = 4;
    short CLIENT_CANCELLED = 5;
    short CLIENT_CLOSED = 6;

    short COMMENT_LENGTH = 500;
    String DEFAULT_ADDRESS_NAME = "Address";

    short ACTIVE_HIERARCHY = 1;
    short INACTIVE_HIERARCHY = 0;

    String LOAN_CYCLE_NUMBER = "Loan cycle number";
    String NUMBER_OF_MISSED_PAYMENTS = "Number of missed payments";
    String TOTAL_NUMBER_OF_PAYMENTS = "Total number of payments";

    // meetings
    short CUSTOMER_MEETING_TYPE = 4;
    String CUSTOMER_MEETING = "customerMeeting";
    String TOTAL_FEE_DUE = "totalFeeDue";

    // get to the home page
    String GETHOMEPAGE = "getHomePage";
    String LOADALLBRANCHES = "loadAllBranches";
    String GETHOMEPAGE_SUCCESS = "getHomePage_success";
    String INVALID_FEE_AMNT = "errors.invalidfeeamt";
    String INVALID_NUMBER = "errors.invalidNumber";
    short CENTER_ENTITY_TYPE = 20;
    short GROUP_ACTIVE_STATE = 9;
    String ERROR_CUSTOMFIELD_REQUIRED = "errors.requiredCustomField";
    String ERROR_MANDATORY_CHECKBOX = "errors.mandatorycheckbox";
    String ERROR_MANDATORY_TEXT_AREA = "errors.mandatorytextarea";
    String ERROR_ADJUSTMENT_NOTE_TOO_BIG = "errors.adjustmentNoteTooBig";

    String ERROR_STATE_CHANGE_EXCEPTION = "Customer.StateChangeException";
    String CENTER_STATE_CHANGE_EXCEPTION = "Customer.CenterStateChangeException";
    String CUSTOMER_ACTIVE_LOAN_ACCOUNTS = "CustomerActiveLoanAccounts";
    String CUSTOMER_ACTIVE_SAVINGS_ACCOUNTS = "CustomerActiveSavingsAccounts";
    // number of meetings attended and missed
    String CUSTOMERPERFORMANCE = "customerPerformance";
    String CUSTOMERPERFORMANCEHISTORY = "customerPerformanceHistory";
    String CUSTOMERPERFORMANCEHISTORYVIEW = "customerPerformanceHistoryView";
    String CURRENT_CLIENT_NAME = "currentClientName";
    String CURRENT_DOB = "currentDOB";
    String CURRENT_GOVT_ID = "currentGovtId";
    String CLIENTRECENTACCACTIVITYLIST = "customerAccountActivityList";
    short CUSTOMER_TYPE_ID = 3;
    short GROUP_ONHOLD = 3;

    // -------------------------------------------constants for M2 Migration
    int SYSTEM_ID_LENGTH = 9;
    String DISPLAY_NAME = "DISPLAY_NAME";
    String OFFICE_ID = "OFFICE_ID";
    String NAME = "Name";
    String LOAN_OFFICER = "LoanOfficer";
    String MEETING = "Meeting";
    String CUSTOM_FIELD = "CustomField";
    String FEE = "errors.fee";
    String INVALID_MEETING = "errors.Customer.invalidMeeting";
    String INVALID_LOAN_OFFICER = "errors.Customer.invalidLoanOfficer";
    String INVALID_FORMED_BY = "errors.Customer.invalidFormedBy";
    String INVALID_STATUS = "errors.Customer.invalidStatus";
    String INVALID_NAME = "errors.Customer.invalidName";
    String INVALID_OFFICE = "errors.Customer.invalidOffice";
    String INVALID_PARENT = "errors.Customer.invalidParent";
    String INVALID_TRAINED_OR_TRAINEDDATE = "errors.Customer.invalidTrainedOrTrainedDate";
    String INVALID_TRAINED_DATE = "errors.InvalidTrainedDate";
    String TRAINED_DATE_BEFORE_DOB = "errors.trainedBeforeDob";
    String INVALID_MFI_JOINING_DATE = "errors.InvalidMfiJoiningDate";

    String TRANSITION_CONFIG_FILE_PATH_CENTER = "org/mifos/framework/util/resources/stateMachine/StateMachine_center.xml";
    String TRANSITION_CONFIG_FILE_PATH_GROUP = "org/mifos/framework/util/resources/stateMachine/StateMachine_group.xml";
    String TRANSITION_CONFIG_FILE_PATH_CLIENT = "org/mifos/framework/util/resources/stateMachine/StateMachine_client.xml";

    String NOTES_FIELD = "notes";
    String FLAG_FIELD = "flag";
    String STATUS_FIELD = "status";
    String MANDATORY_TEXTBOX = "errors.mandatory_textbox";
    String MANDATORY_SELECT = "errors.mandatoryselect";
    String MAXIMUM_LENGTH = "errors.maxlength";
    String ERRORS_DUPLICATE_CUSTOMER = "errors.Customer.duplicateCustomer";
    String ERRORS_SPECIFY_NAME = "errors.Customer.specifyName";
    String ERRORS_SELECT_LOAN_OFFICER = "errors.Customer.specifyLoanOfficer";
    String ERRORS_SPECIFY_MEETING = "errors.Customer.specifyMeeting";
    String ERRORS_SPECIFY_CUSTOM_FIELD_VALUE = "errors.Customer.specifyCustomFieldValue";
    String ERRORS_CUSTOM_DATE_FIELD = "errors.Customer.customdatefield";
    String ERRORS_DUPLICATE_PERIODIC_FEE = "errors.Customer.duplicatePeriodicFee";
    String ERRORS_MUST_NOT_BE_NEGATIVE = "errors.mustNotBeNegative";
    String ERRORS_SPECIFY_FEE_AMOUNT = "errors.Customer.specifyFeeAmount";
    String ERRORS_FEE_FREQUENCY_MISMATCH = "errors.Customer.feeFrequencyMismatch";
    String ERRORS_SAME_BRANCH_TRANSFER = "errors.Customer.sameBranchTransfer";
    String ERRORS_TRANSFER_IN_INACTIVE_OFFICE = "errors.Customer.officeInactive";
    String ERRORS_INTRANSFER_PARENT_INACTIVE = "errors.Customer.parentInactive";
    String ERRORS_SAME_GROUP_TRANSFER = "errors.Customer.sameGroupTransferException";
    String ERRORS_SAME_CENTER_TRANSFER = "errors.Customer.sameCenterTransferException";
    String ERRORS_HAS_ACTIVE_ACCOUNT = "errors.Customer.hasActiveAccount";
    String ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT = "errors.Customer.clientHasActiveAccount";
    String ERRORS_MEETING_FREQUENCY_MISMATCH = "errors.Customer.meetingFrequencyMismatch";
    String ERRORS_MANDATORY = "errors.mandatory";
    String ERRORS_SELECT = "errors.requiredSelect";
    String SALUTATION = "Salutation";
    String FIRST_NAME = "First Name";
    String LAST_NAME = "Last Name";
    String DOB = "Date of birth";
    String GENDER = "Gender";
    String SPOUSE_FIRST_NAME = "Spouse/Father First Name";
    String SPOUSE_LAST_NAME = "Spouse/Father Last Name";
    String SPOUSE_TYPE = "Spouse/Father Relationship";
    String FORMED_BY_LOANOFFICER = "Formed By Loan officer";
    String TRAINED_CHECKED = "errors.TrainedCheckbox";
    String TRAINED_DATE_MANDATORY = "errors.TrainedDateNeeded";
    String MFI_JOINING_DATE_MANDATORY = "errors.MfiJoiningDateNeeded";

    // Customer Account
    String CUSTOMER_ACCOUNT = "customerAccount";
    String RECENT_ACTIVITIES = "recentActivities";
    int HISTORICALDATA_COMMENT_LENGTH = 500;
    String HISTORICALDATA_NOTES = "Notes";
    String GROUP_HAS_ACTIVE_ACCOUNTS_EXCEPTION = "errors.Group.GroupActiveAccountsPresent";
    String CLIENT_HAS_ACTIVE_ACCOUNTS_EXCEPTION = "errors.Group.ClientActiveAccountsPresent";
    String CLIENT_IS_A_TITLE_HOLDER_EXCEPTION = "errors.Group.ClientIsATitleHolder";
    String CLIENT_IS_CLOSED_OR_CANCELLED_EXCEPTION = "error.Customer.ClientIsClosedOrCancelled";
    String CLIENT_HAVE_OPEN_LOAN_ACCOUNT_EXCEPTION = "error.Customer.ClientHaveOpenLoanAccount";
    String CLIENT_CANT_BE_ADDED_TO_INACTIVE_GROUP = "errors.Customer.ClientCannotBeAddedToInactiveGroup";
    String TARGET_GROUP_STATUS_LOWER_THAN_CLIENT = "errors.Customer.TargetGroupStatusLowerThanClient";
    String SURVEY_KEY = "customerSurveys";
    String SURVEY_COUNT = "surveyCount";
    String CLIENT_WITH_SAME_GOVT_ID_EXIST_IN_CLOSED = "warning.clientWithSameGovtIdExistInClosed";
    String CLIENT_WITH_SAME_NAME_DOB_EXIST_IN_BLACKLISTED = "warning.clientWithSameNameDOBExistInBlackListed";
    String CLIENT_WITH_SAME_NAME_DOB_EXIST_IN_CLOSED = "warning.clientWithSameNameDOBExistInClosed";
    String AMOUNT_OF_LOAN_KEY = "label.amountofloanLabel";
    String INTEREST_PAID_KEY = "Customer.History.InterestPaid";
    String TOTAL_AMOUNT_PAID_KEY = "label.totalamountpaidLabel";
    String QUESTION_GROUPS_LIST = "questionGroups";
    String VARIABLE_INSTALLMENT_ENABLED = "variableInstallmentsEnabled";
    String LOAN_ACCOUNT = "loanAccount";

    String URL_MAP = "urlMap";

}
