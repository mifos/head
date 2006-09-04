/**

 * GroupConstants.java    version: 1.0



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

package org.mifos.application.customer.group.util.helpers;

public interface GroupConstants {
	
	public static final String BP_RESULT="result";
	
	//level id for the group 
	public static final String GROUP = "Group";
	public static final String GROUPVO = "GroupVO";
	public static final String GROUP_PARENT = "GroupParent";
	
	public static final short ENTITY_TYPE = 12;
	public static final String GROUP_PERFORMANCE_VO = "GroupPerformanceHistory";
	public static final short LOANOFFICER_LEVEL_ID=1;
	
	//for programs
	public static final String PROGRAMS_MAP="programsMap";
	public static final String PROGRAMS_SET="programsSet";
	public static final String CUSTOMER_PROGRAMS="customerPrograms";
	public static final String CUSTOMER_PROGRAMS_MAP="customerProgramsMap";
	
	//master data keys for group
	public static final String CUSTOMER_POSITIONS="customerPositions";
	public static final String LOANOFFICERS="loanOfficers";
	public static final String COLLECTION_SHEETS="collectionSheets";
	public static final String CUSTOM_FIELDS="customFields";
	
	public static final String CHECKLIST="checklist";
	public static final String TITLES="titles";
	
	public static final String CLIENT_LIST="clients";
	public static final String POSITIONS="positions";
	public static final String INPUT_PAGE="inputPage";
	public static final String PARENT_OFFICE_ID="parentOfficeId";
	
	public static final String NOTES="notes";
	public static final int NOTES_COUNT=3;
	public static final String LINK_VALUES="linkValues";
	//for fees
	public static final String ADMIN_FEES_LIST="adminFeesList";
	public static final String ADDITIONAL_FEES="additionalFees";
	public static final String FEES_LIST="feesList";
	public static final short GROUP_CATEGORY_ID=3;
	//public static final short All_CATEGORY_ID=1;
	
	//for transfers
	public static final String BRANCH_PARENTS="branchParents";
	public static final String BRANCH_OFFICES="branchOffices";
	public static final String BRANCH_LIST="branchList";
	public static final String CENTER_LIST="centerList";
	public static final String GROUP_TRANSFER_INPUT="groupTransferInput";
	public static final String UPDATE_BRANCH_RSM="updateBranchRSM";
	//public static final String UPDATE_PARENT_RSM="updateParentRSM";
	public static final String LOAD_PARENT_TRANSFER_METHOD="loadParentTransfer";
	public static final String LOAD_PARENT_TRANSFER_SUCCESS="loadParentTransfer_success";
	public static final String TRANSFER="transfer";
	public static final String NAME="Name";
	public static final String CURRENT_STATUS="currentStatus";
	public static final String CURRENT_FLAG="currentFlag";
	public static final String FLAG="flag";
	public static final String OLD_GROUP_NAME="oldGroupName";
	public static final String NEW_STATUS="newStatus";
	public static final String NEW_FLAG="newFlag";
	public static final String STATUS="status";
	public static final String STATUS_FLAG="statusFlag";
	public static final String IS_BLACKLISTED="isBlacklisted";
	public static final String BLACKLISTED_FLAG_NAME="blacklistedFlagName";
	//public static final String SEARCH_STRING="Search String";
	public static final String CENTER_HIERARCHY_EXIST="CenterHierarchyExist";
	public static final String IS_PENDING_APPROVAL_DEFINED="IsPendingApprovalDefined";
	public static final String YES="Yes";
	public static final String NO="No";
	public static final String GROUP_LO = "GROUP_LO";
	//group status constants
	public static final short PARTIAL_APPLICATION=7;
	public static final short PENDING_APPROVAL=8;
	public static final short ACTIVE=9;
	public static final short HOLD=10;
	public static final short CANCELLED=11;
	public static final short CLOSED=12;

	//flags
	public static final short BLACKLISTED=3;
	public static final short TRANSFERRED=2;
	
	public static final String OLD_STATUS="oldStatus";
	public static final String STATUS_LIST="statusList";

	//actions-methods
	public static final String HIERARCHY_CHECK_METHOD="hierarchyCheck";
	public static final String UPDATE_BRANCH_METHOD="updateBranch";
	public static final String UPDATE_BRANCH_SUCCESS="updateBranch_success";
	public static final String UPDATE_STATUS_METHOD="updateStatus";
	public static final String UPDATE_PARENT_METHOD="updateParent";
	public static final String UPDATE_PARENT_SUCCESS="updateParent_success";
	public static final String UPDATE_PARENT_FAILURE="updateParent_failure";
	public static final String LOAD_TRANSFER_METHOD="loadTransfer";
	public static final String LOAD_TRANSFER_SUCCESS="loadTransfer_success";
	public static final String CONFIRM_BRANCH_TRANSFER_METHOD="confirmBranchTransfer";
	public static final String LOAD_CENTERS_SUCCESS="loadCenters_success";
	public static final String LOAD_STATUS_METHOD="loadStatus";
	public static final String LOAD_MEETING_METHOD="loadMeeting";
	public static final String LOAD_MEETING_SUCCESS="loadMeeting_success";
	public static final String METHOD_CHOOSE_OFFICE="chooseOffice";
	public static final String LOAD_CENTER_SEARCH_SUCCESS="loadCenterSearch_success";
	public static final String CONFIRM_TRANSFER_SUCCESS="confirmTransfer_success";
	public static final String CONFIRM_PARENT_TRANSFER_METHOD="confirmParentTransfer";
	public static final String CONFIRM_PARENT_TRANSFER_SUCCESS="confirmParentTransfer_success";
	public static final String GROUP_SEARCH_CREATE_CLIENT="GroupSearch_CreateClient";
	public static final String GROUP_SEARCH_CLIENT_TRANSFER="GroupSearch_ClientTransfer";
	public static final String LOAD_SEARCH_METHOD="loadSearch";
	public static final String LOAD_SEARCH_SUCCESS="loadSearch_success";
	public static final String CHOOSE_OFFICE_SUCCESS="chooseOffice_success";
	
	//forwards to cancel pages
	public static final String CANCEL_CREATE="cancel_create";
	public static final String CANCEL_MANAGE="cancel_manage";
	public static final String GROUP_DETAILS_PAGE="group_details_page";
	public static final String CLIENT_DETAILS_PAGE="client_details_page";
	public static final String CANCEL_TRANSFER_BRANCH="cancel_transferbranch";
	//action-methods in Center Module used in Group
	public static final String CENTER_SEARCH="CenterSearch";
	public static final String CENTER_INFO="centerInfo";
	public static final String CENTER_SEARCH_INPUT="centerSearchInput";
	public static final String CENTER_SEARCH_RESULT="centerSearchResult";
	//create pages
	public static final String CREATE_NEW_GROUP="CreateNewGroup";
	public static final String CREATE_MEETING="CreateMeeting";
	public static final String CREATE_NEW_GROUP_FAILURE="CreateNewGroup_failure";
	public static final String MANAGE_GROUP="ManageGroup";
	public static final String MANAGE_GROUP_FAILURE="ManageGroup_failure";
	
	public static final String CREATE_MANAGE_GROUP="CreateManageGroup";
	public static final String CHANGE_GROUP_STATUS="ChangeGroupStatus";
	public static final String MANAGE_SUCCESS="manage_success";
	public static final String CREATE_NEW_GROUP_PAGE ="load_success";
	public static final String UPDATE_STATUS_SUCCESS="updateStatus_success";
	public static final String LOAD_CENTERS="LoadCenters";
	//preview pages
	public static final String PREVIEW_CREATE_NEW_GROUP="PreviewCreateNewGroup";
	public static final String PREVIEW_CREATE_NEW_GROUP_FAILURE="PreviewCreateNewGroup_failure";
	
	public static final String PREVIEW_MANAGE_GROUP="PreviewManageGroup";
	public static final String PREVIEW_CHANGE_GROUP_STATUS="PreviewChangeGroupStatus";
	
	public static final String PREFIX_SEARCH_STRING="1.";	
	//actions
	public static final String CENTER_SEARCH_ACTION="CenterSearchAction";
	public static final String GROUP_ACTION="GroupAction";
	
	public static final String TOTAL_AMOUNT_DUE="totalAmountDue";
	public static final String IS_GROUP_LOAN_ALLOWED="isGroupLoanAllowed";
	//Exceptions
	public static final String TRANSFER_EX_ACTIVE_LOAN_ACCOUNTS="errors.transferex_activeloans";
	public static final String TRANSFER_EX_CLIENT_HAS_ACTIVE_LOAN_ACCOUNTS="errors.transferex_clienthasactiveloan";
	public static final String TRANSFER_EX_CLIENT_TRANSFER_FAILED="errors.transferex_clienttransferfailed";
	public static final String GROUP_INVALID_VERSION="errors.groupinvalidversion";
	public static final String GROUP_HAS_ACTIVE_ACCOUNTS="errors.grouphasactiveaccounts";
	public static final String GROUP_CLIENTS_HAS_ACTIVE_LOAN="errors.groupclientshasactiveloan";
	public static final String GROUP_CLIENTS_ARE_ACTIVE="errors.groupclientsareactive";
	public static final String GROUP_LOANOFFICER_NOT_ASSIGNED="errors.grouploanofficernotassigned";
	public static final String MEETING_NOT_ASSIGNED="errors.meetingnotassigned";
	public static final String GROUP_NOT_CLOSED="errors.groupnotclosed";
	public static final String DUPLICATE_GROUP="errors.duplicategroup";
	public static final String MANDATORY="errors.mandatory";
	public static final String MAX_LENGTH="errors.maxlength";
	public static final String MANDATORY_SELECT="errors.mandatoryselect";
	public static final String VIEW_GROUP_CHARGES="ViewGroupCharges";
	
	//
	public static final String UPDATE_FAILED="errors.groupupdate";
	public static final String CREATE_FAILED="errors.groupcreate";
	public static final String GROUP_NOT_FOUND="errors.groupnotfound";
	
	
	public static final String MEETING_REQUIRED="errors.meetingrrequired";
	public static final String DUPLICATE_FEE="errors.duplicatefee";
	public final String INVALID_FEE_AMNT="errors.invalidfeeamt";
	public final String FEE_WITHOUT_MEETING="errors.feewithoutmeeting";
	public static final String TRAINED_DATE="errors.traineddate";
	public static final String SAME_PARENT="errors.sameparent";
	public static final String SAME_BRANCH="errors.samebranch";
	public static final String NO_SEARCH_STRING="errors.nosearchstring";
	public static final String INCOMPLETE_CHECKLIST="errors.incompletechecklist";
	public static final String BRANCH_INACTIVE="errors.branchinactive";
	public static final String FEE_INACTIVE="errors.feeinactive";	
	public static final String CENTER_INACTIVE="errors.centerinactive";
	public static final String LOAN_OFFICER_REQUIRED="errors.loanofficerrequired";
	public static final String LOANOFFICER_INACTIVE="errors.loanofficerinactive";
	public static final String LOAN_OFFICER_REQUIRED_FOR_ACTIVE_GROUP="errors.loanofficerrequired_activegroup";
	
	public static final int MAX_ADDRESS_LINE_LENGTH=200;
	public static final int MAX_FIELD_LENGTH=100;
	public static final String TRAINED = "1";
	public static final String NOT_TRAINED = "0";
	public static final String CREATE_GROUP="createGroup";
	public static final String GROUPLOANACCOUNTSINUSE = "groupLoanAccountsInUse";
	public static final String GROUPSAVINGSACCOUNTSINUSE = "groupSavingsAccountsInUse";
	
	public static final String GROUP_MEETING = "groupMeeting";
}
