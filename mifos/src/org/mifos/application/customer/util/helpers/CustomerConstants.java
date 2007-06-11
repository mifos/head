/**

 * CustomerConstants.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.application.customer.util.helpers;


public interface CustomerConstants {
	public static final String CUSTOMER_NOTE_ACTION="CustomerNoteAction";
	public static final String CUSTOMER_HISTORICAL_DATA_ACTION="CustomerHistoricalDataAction";
	public static final String CUSTOMER_HISTORICAL_DATA_VO="CustomerHistoricalDataVO";
	public static final String CUSTOMER_HISTORICAL_DATA="customerHistoricalData";
	public static final String MFIJOININGDATE="mfiJoiningDate";
	public static final String CUSTOMERHISTORICALDATAACTIONFORM="custHistoricalDataActionForm";
	public static final String CUSTOMER_NOTE_VO="CustomerNoteVO";
	public static final String PERSONNEL_NAME="personnelName";
	public static final String GLOBAL_CUST_NUM="globalCustNum";
	public static final String BLANK=" ";

	/* Like CustomerStatus.CENTER_ACTIVE.getValue()
	   but usable in case statements */
	public static final short CENTER_ACTIVE_STATE = 13;
	public static final short CENTER_INACTIVE_STATE = 14;

	public static final String OLD_STATUS = "oldStatus";
	public static final String NEW_STATUS = "newStatus";
	public static final String NEW_FLAG = "newFlag";
	public static final String IS_TRAINED = "isTrained";
	public static final String IS_CLIENT_STATUS_ACTIVE = "isActive";
	public static final short TRAINED_NO = 2;
	public static final String TRAINED_DATE = "trainedDate";
	
	public static final short All_CATEGORY_ID =1;
	
	/**Exceptions*/
	public static final String UNKNOWN_EXCEPTION="error.unknownexception";
	public static final String CUSTOMER_NOT_FOUND="error.customernotfound";
	public static final String LOAN_OFFICER_REQUIRED_EXCEPTION = "LoanOfficerRequiredException";
	public static final String NAME_REQUIRED_EXCEPTION = "NameRequiredException";
	public static final String LOAN_OFFICER_BLANK_EXCEPTION = "LoanOfficerBlankException";
	public static final String FORMEDBY_LOANOFFICER_BLANK_EXCEPTION = "FormedByLoanOfficerBlankException";
	public static final String STATUS_REQUIRED_EXCEPTION = "StatusRequiredException";
	public static final String GET_OFFICE_HOMEPAGE = "getOfficeHomePage";
	
	public static final String DUPLICATE_FEE_EXCEPTION = "DuplicateFeeException";
	public static final String DUPLICATE_GOVT_ID_EXCEPTION = "DuplicateGovtIdException";
	public static final String CUSTOMER_LOAN_OFFICER_INACTIVE_EXCEPTION = "Customer.LoanOfficerInactive";
	public static final String CUSTOMER_STATUS_NOTES_EXCEPTION = "Customer.StatusNotesRequired";
	public static final String CUSTOMER_BRANCH_INACTIVE_EXCEPTION = "Customer.BranchInactive";
	public static final String CUSTOMER_FEE_INACTIVE_EXCEPTION = "Customer.FeesInactive";
	public static final String CUSTOMER_DUPLICATE_CUSTOMERNAME_EXCEPTION = "Customer.DuplicateCustomerName";
	public static final String CUSTOMER_INVALID_VERSION_EXCEPTION = "Customer.InvalidVersion";
	public static final String MEETING_REQUIRED_EXCEPTION = "Customer.MeetingRequired";
	public static final String CREATE_FAILED_EXCEPTION = "Customer.CreateFailed";
	public static final String UPDATE_FAILED_EXCEPTION = "Customer.UpdateFailed";
	public static final String CLIENT_HAS_ACTIVE_LOAN_EXCEPTION = "Client.ActiveLoanPresent";
	public static final String CUSTOMER_HAS_ACTIVE_ACCOUNTS_EXCEPTION = "Customer.ActiveAccountsPresent";
	public static final String INCOMPLETE_CHECKLIST_EXCEPTION = "Customer.IncompleteChecklist";
	/**Action method names*/
	public static final String METHOD_CANCEL = "cancel";
	public static final String METHOD_GET = "get";
	public static final String METHOD_SEARCH_NEXT = "searchNext";
	public static final String METHOD_SEARCH_PREV = "searchPrev";
	public static final String METHOD_LOAD = "load";
	public static final String METHOD_LOAD_SEARCH = "loadSearch";
	public static final String METHOD_SEARCH = "search";
	public static final String METHOD_MANAGE = "manage";
	public static final String METHOD_PREVIOUS = "previous";
	public static final String METHOD_LOAD_STATUS = "loadStatus";
	public static final String METHOD_UPDATE_STATUS = "updateStatus";
	public static final String METHOD_CREATE =  "create";
	public static final String METHOD_UPDATE = "update";
	public static final String METHOD_NEXT= "next";
	public static final String METHOD_PREVIEW = "preview";
	public static final String METHOD = "method";
	public static final String METHOD_LOAD_ADJUSTMENT = "loadAdjustment";
	public static final String METHOD_PREVIEW_ADJUSTMENT = "previewAdjustment";
	public static final String METHOD_APPLY_ADJUSTMENT = "applyAdjustment";
	public static final String METHOD_LOAD_ADJUSTMENT_SUCCESS = "loadAdjustment_success";
	public static final String METHOD_PREVIEW_ADJUSTMENT_SUCCESS = "previewAdjustment_success";
	public static final String METHOD_APPLY_ADJUSTMENT_SUCCESS = "applyAdjustment_success";
	public static final String METHOD_GET_DETAILS="getDetails";
	public static final String GET_SUCCESS="get_success";
	public static final String NOTES_SEARCH="NotesSearch";
	public static final String CLIENT_DETAILS_PAGE="client_details_page";
	public static final String GROUP_DETAILS_PAGE="group_details_page";
	public static final String CENTER_DETAILS_PAGE="center_details_page";
	public static final String VIEW_CLIENT_CHARGES="ViewClientCharges";
	public static final String VIEW_GROUP_CHARGES="ViewGroupCharges";
	public static final String VIEW_CENTER_CHARGES="ViewCenterCharges";
	public static final String APPLY_ADJUSTMENT_CLIENT_SUCCESS="applyAdjustment_client_success";
	public static final String APPLY_ADJUSTMENT_GROUP_SUCCESS="applyAdjustment_group_success";
	
	public static final String APPLY_ADJUSTMENT_CENTER_SUCCESS="applyAdjustment_center_success";
	public static final String CANCELADJ_CLIENT_SUCCESS="canceladj_client_success";
	public static final String CANCELADJ_GROUP_SUCCESS="canceladj_group_success";
	public static final String CANCELADJ_CENTER_SUCCESS="canceladj_center_success";
	
	public static final String SEARCH_FAILURE = "search_failure";
	public static final String SEARCH_FAILURE_TRANSFER = "search_failure_transfer";
	public static final String CURRENT_CUSTOMER_STATUS = "currentStatus";
	public static final String CUSTOMER_SEARCH_INPUT = "customerSearchInput";
	public static final String YES_SMALL = "yes";
	public static final String NO_SMALL = "no";
	public static final String CLIENT_HAS_ACTIVE_LOAN = "Client.HasActiveLoan";
	
	public static final String LINK_VALUES="linkValues";
	public static final String LOAD_SUCCESS="load_success";
	public static final String IS_HISTORICAL_DATA_PRESENT="isHistoricalDataPresent";
	public static final String YES="Yes";
	public static final String NO="No";
	public static final String FORMEDBY_LOAN_OFFICER_LIST = "formedByLoanOfficers";
	public static final String GROUP_HIERARCHY_REQUIRED = "groupHierarchyRequired";
	public static final String PENDING_APPROVAL_DEFINED = "pendingApprovalDefined";
	public static final String CONFIGURATION_LSM = "configurationLSM" ;
	/**Request Parameters names*/
	String LOAN_OFFICER_LIST ="loanOfficers";
	
	String STATUS_LIST ="statusList";
	String FEES_LIST ="feesList";
	String ADMIN_FEES_LIST ="adminFeesList";
	public static final String ADDITIONAL_FEES_LIST = "additionalFeeList";
	public static final String CLIENT_LIST="clients";
	String COLLECTION_SHEET_TYPES ="collectionSheetTypes";
	String CHECKLISTS ="checkLists";
	String POSITIONS ="positions";
	String CUSTOM_FIELDS_LIST ="customFields";
	
	public static final short CLIENT_PARTIAL = 1;
	public static final short CLIENT_PENDING = 2;
	public static final short CLIENT_APPROVED = 3;
	public static final short CLIENT_ONHOLD = 4;	
	public static final short CLIENT_CANCELLED = 5;
	public static final short CLIENT_CLOSED = 6;
	
	public static final short COMMENT_LENGTH = 500;
	public static final String DEFAULT_ADDRESS_NAME = "Address";
	
	public static final short ACTIVE_HIERARCHY=1;
	public static final short INACTIVE_HIERARCHY=0;
	
	public static final String LOAN_CYCLE_NUMBER = "Loan cycle number";
	public static final String NUMBER_OF_MISSED_PAYMENTS = "Number of missed payments";
	public static final String TOTAL_NUMBER_OF_PAYMENTS = "Total number of payments";
	
	//meetings
	public static final short CUSTOMER_MEETING_TYPE = 4;
	public static final String CUSTOMER_MEETING= "customerMeeting";
	public static final String TOTAL_FEE_DUE = "totalFeeDue";
	
	//get to the home page
	public String GETHOMEPAGE = "getHomePage";
	public String LOADALLBRANCHES = "loadAllBranches";
	public String GETHOMEPAGE_SUCCESS = "getHomePage_success";
	public static final String INVALID_FEE_AMNT = "errors.invalidfeeamt";
	public static final String INVALID_NUMBER = "errors.invalidNumber";
	public static final short CENTER_ENTITY_TYPE =20;
	public static final short GROUP_ACTIVE_STATE = 9;
	public static final String ERROR_CUSTOMFIELD_REQUIRED = "errors.requiredCustomField";
	public static final String ERROR_MANDATORY_CHECKBOX = "errors.mandatorycheckbox";
	public static final String ERROR_MANDATORY_TEXT_AREA = "errors.mandatorytextarea";
	public static final String ERROR_ADJUSTMENT_NOTE_TOO_BIG = "errors.adjustmentNoteTooBig";
	
	public static final String ERROR_STATE_CHANGE_EXCEPTION = "Customer.StateChangeException";
	public String CENTER_STATE_CHANGE_EXCEPTION = "Customer.CenterStateChangeException";
	public static final String CUSTOMER_ACTIVE_LOAN_ACCOUNTS = "CustomerActiveLoanAccounts";
	public static final String CUSTOMER_ACTIVE_SAVINGS_ACCOUNTS = "CustomerActiveSavingsAccounts";
//	number of meetings attended and missed
	public String CUSTOMERPERFORMANCE = "customerPerformance";
	public String CUSTOMERPERFORMANCEHISTORY = "customerPerformanceHistory";
	public String CUSTOMERPERFORMANCEHISTORYVIEW = "customerPerformanceHistoryView";
	public static final String CURRENT_CLIENT_NAME = "currentClientName";
	public static final String CURRENT_DOB = "currentDOB";
	public static final String CURRENT_GOVT_ID = "currentGovtId"; 
	public static final String CLIENTRECENTACCACTIVITYLIST="customerAccountActivityList";
	public static final short CUSTOMER_TYPE_ID = 3;
	public static final short GROUP_ONHOLD = 3;
	
	//-------------------------------------------constants for M2 Migration
	public static final int SYSTEM_ID_LENGTH = 9;
	public static final String DISPLAY_NAME="DISPLAY_NAME";
	public static final String OFFICE_ID="OFFICE_ID";
	public static final String NAME="Name";
	public static final String LOAN_OFFICER="LoanOfficer";
	public static final String MEETING = "Meeting";
	public static final String CUSTOM_FIELD = "CustomField";
	public static final String FEE ="Fee";
	public static final String INVALID_MEETING="errors.Customer.invalidMeeting";
	public static final String INVALID_LOAN_OFFICER="errors.Customer.invalidLoanOfficer";
	public static final String INVALID_FORMED_BY="errors.Customer.invalidFormedBy";
	public static final String INVALID_STATUS="errors.Customer.invalidStatus";
	public static final String INVALID_NAME="errors.Customer.invalidName";
	public static final String INVALID_OFFICE="errors.Customer.invalidOffice";
	public static final String INVALID_PARENT="errors.Customer.invalidParent";
	public static final String INVALID_TRAINED_OR_TRAINEDDATE="errors.Customer.invalidTrainedOrTrainedDate";
	public static final String INVALID_TRAINED_DATE = "errors.InvalidTrainedDate";
	public static final String INVALID_MFI_JOINING_DATE = "errors.InvalidMfiJoiningDate";
	
	public final String TRANSITION_CONFIG_FILE_PATH_CENTER="org/mifos/framework/util/resources/stateMachine/StateMachine_center.xml";
	public final String TRANSITION_CONFIG_FILE_PATH_GROUP="org/mifos/framework/util/resources/stateMachine/StateMachine_group.xml";
	public final String TRANSITION_CONFIG_FILE_PATH_CLIENT="org/mifos/framework/util/resources/stateMachine/StateMachine_client.xml";
	
	public static final String NOTES_FIELD="notes";
	public static final String FLAG_FIELD="flag";
	public static final String STATUS_FIELD="status";
	public static final String MANDATORY_TEXTBOX="errors.mandatory_textbox";
	public static final String MANDATORY_SELECT="errors.mandatoryselect";
	public static final String MAXIMUM_LENGTH="errors.maxlength";
	public static final String ERRORS_DUPLICATE_CUSTOMER="errors.Customer.duplicateCustomer";
	public static final String ERRORS_SPECIFY_NAME="errors.Customer.specifyName";
	public static final String ERRORS_SELECT_LOAN_OFFICER="errors.Customer.specifyLoanOfficer";
	public static final String ERRORS_SPECIFY_MEETING="errors.Customer.specifyMeeting";
	public static final String ERRORS_SPECIFY_CUSTOM_FIELD_VALUE="errors.Customer.specifyCustomFieldValue";
	public static final String ERRORS_DUPLICATE_PERIODIC_FEE="errors.Customer.duplicatePeriodicFee";
	public static final String ERRORS_SPECIFY_FEE_AMOUNT="errors.Customer.specifyFeeAmount";
	public static final String ERRORS_FEE_FREQUENCY_MISMATCH="errors.Customer.feeFrequencyMismatch";
	public static final String ERRORS_SAME_BRANCH_TRANSFER="errors.Customer.sameBranchTransfer";
	public static final String ERRORS_TRANSFER_IN_INACTIVE_OFFICE="errors.Customer.officeInactive";
	public static final String ERRORS_INTRANSFER_PARENT_INACTIVE="errors.Customer.parentInactive";
	public static final String ERRORS_SAME_PARENT_TRANSFER="errors.Customer.sameParentTransferException";
	public static final String ERRORS_HAS_ACTIVE_ACCOUNT="errors.Customer.hasActiveAccount";
	public static final String ERRORS_CHILDREN_HAS_ACTIVE_ACCOUNT="errors.Customer.clientHasActiveAccount";
	public static final String ERRORS_MEETING_FREQUENCY_MISMATCH="errors.Customer.meetingFrequencyMismatch";
	public static final String ERRORS_MANDATORY="errors.mandatory";
	public static final String ERRORS_SELECT="errors.requiredSelect";
	public static final String SALUTATION = "Salutation";
	public static final String FIRST_NAME = "First Name";
	public static final String LAST_NAME = "Last Name";
	public static final String DOB = "Date of birth";
	public static final String GENDER = "Gender";
	public static final String SPOUSE_FIRST_NAME = "Spouse/Father First Name";
	public static final String SPOUSE_LAST_NAME = "Spouse/Father Last Name";
	public static final String SPOUSE_TYPE = "Spouse/Father Relationship";
	public static final String FORMED_BY_LOANOFFICER = "Formed By Loan officer";
	public static final String TRAINED_CHECKED = "errors.TrainedCheckbox";
	public static final String TRAINED_DATE_MANDATORY = "errors.TrainedDateNeeded";
	public static final String MFI_JOINING_DATE_MANDATORY = "errors.MfiJoiningDateNeeded";
	
	//Customer Account 
	public static final String CUSTOMER_ACCOUNT = "customerAccount";	
	public static final String RECENT_ACTIVITIES = "recentActivities";
	public int HISTORICALDATA_COMMENT_LENGTH = 500;
	public String HISTORICALDATA_NOTES = "Notes";
	public static final String GROUP_HAS_ACTIVE_ACCOUNTS_EXCEPTION = "errors.Group.GroupActiveAccountsPresent";
	public static final String CLIENT_HAS_ACTIVE_ACCOUNTS_EXCEPTION = "errors.Group.ClientActiveAccountsPresent";
	public static final String CLIENT_IS_A_TITLE_HOLDER_EXCEPTION = "errors.Group.ClientIsATitleHolder";
	public static final String CLIENT_IS_CLOSED_OR_CANCELLED_EXCEPTION = "error.Customer.ClientIsClosedOrCancelled";
	public static final String CLIENT_HAVE_OPEN_LOAN_ACCOUNT_EXCEPTION = "error.Customer.ClientHaveOpenLoanAccount";
	
}
