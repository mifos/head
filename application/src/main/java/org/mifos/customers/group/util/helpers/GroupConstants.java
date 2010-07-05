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

package org.mifos.customers.group.util.helpers;

// FIXME: remove unused constants from this class. There are a bunch!
public interface GroupConstants {

    String BP_RESULT = "result";

    // level id for the group
    String GROUP = "Group";
    String GROUPVO = "GroupVO";
    String GROUP_PARENT = "GroupParent";

    short ENTITY_TYPE = 12;
    String GROUP_PERFORMANCE_VO = "GroupPerformanceHistory";
    short LOANOFFICER_LEVEL_ID = 1;

    // for programs
    String PROGRAMS_MAP = "programsMap";
    String PROGRAMS_SET = "programsSet";
    String CUSTOMER_PROGRAMS = "customerPrograms";
    String CUSTOMER_PROGRAMS_MAP = "customerProgramsMap";

    // master data keys for group
    String CUSTOMER_POSITIONS = "customerPositions";
    String LOANOFFICERS = "loanOfficers";
    String COLLECTION_SHEETS = "collectionSheets";
    String CUSTOM_FIELDS = "customFields";

    String CHECKLIST = "checklist";
    String TITLES = "titles";

    String CLIENT_LIST = "clients";
    String POSITIONS = "positions";
    String INPUT_PAGE = "inputPage";
    String PARENT_OFFICE_ID = "parentOfficeId";

    String NOTES = "notes";
    int NOTES_COUNT = 3;
    String LINK_VALUES = "linkValues";
    // for fees
    String ADMIN_FEES_LIST = "adminFeesList";
    String ADDITIONAL_FEES = "additionalFees";
    String FEES_LIST = "feesList";
    short GROUP_CATEGORY_ID = 3;
    // short All_CATEGORY_ID=1;

    // for transfers
    String BRANCH_PARENTS = "branchParents";
    String BRANCH_OFFICES = "branchOffices";
    String BRANCH_LIST = "branchList";
    String CENTER_LIST = "centerList";
    String GROUP_TRANSFER_INPUT = "groupTransferInput";
    String UPDATE_BRANCH_RSM = "updateBranchRSM";
    // String UPDATE_PARENT_RSM="updateParentRSM";
    String LOAD_PARENT_TRANSFER_METHOD = "loadParentTransfer";
    String LOAD_PARENT_TRANSFER_SUCCESS = "loadParentTransfer_success";
    String TRANSFER = "transfer";
    String NAME = "Name";
    String CURRENT_STATUS = "currentStatus";
    String CURRENT_FLAG = "currentFlag";
    String FLAG = "flag";
    String OLD_GROUP_NAME = "oldGroupName";
    String NEW_STATUS = "newStatus";
    String NEW_FLAG = "newFlag";
    String STATUS = "status";
    String STATUS_FLAG = "statusFlag";
    String IS_BLACKLISTED = "isBlacklisted";
    String BLACKLISTED_FLAG_NAME = "blacklistedFlagName";
    // String SEARCH_STRING="Search String";
    String CENTER_HIERARCHY_EXIST = "CenterHierarchyExist";
    String IS_PENDING_APPROVAL_DEFINED = "IsPendingApprovalDefined";
    String YES = "Yes";
    String NO = "No";
    String GROUP_LO = "GROUP_LO";
    // group status constants
    short PARTIAL_APPLICATION = 7;
    short PENDING_APPROVAL = 8;
    short ACTIVE = 9;
    short HOLD = 10;
    short CANCELLED = 11;
    short CLOSED = 12;

    // flags
    short BLACKLISTED = 3;
    short TRANSFERRED = 2;

    String OLD_STATUS = "oldStatus";
    String STATUS_LIST = "statusList";

    // actions-methods
    String HIERARCHY_CHECK_METHOD = "hierarchyCheck";
    String UPDATE_BRANCH_METHOD = "updateBranch";
    String UPDATE_BRANCH_SUCCESS = "updateBranch_success";
    String UPDATE_STATUS_METHOD = "updateStatus";
    String UPDATE_PARENT_METHOD = "updateParent";
    String UPDATE_PARENT_SUCCESS = "updateParent_success";
    String UPDATE_PARENT_FAILURE = "updateParent_failure";
    String LOAD_TRANSFER_METHOD = "loadTransfer";
    String LOAD_TRANSFER_SUCCESS = "loadTransfer_success";
    String CONFIRM_BRANCH_TRANSFER_METHOD = "confirmBranchTransfer";
    String LOAD_CENTERS_SUCCESS = "loadCenters_success";
    String LOAD_STATUS_METHOD = "loadStatus";
    String LOAD_MEETING_METHOD = "loadMeeting";
    String LOAD_MEETING_SUCCESS = "loadMeeting_success";
    String METHOD_CHOOSE_OFFICE = "chooseOffice";
    String LOAD_CENTER_SEARCH_SUCCESS = "loadCenterSearch_success";
    String CONFIRM_TRANSFER_SUCCESS = "confirmTransfer_success";
    String CONFIRM_PARENT_TRANSFER_METHOD = "confirmParentTransfer";
    String CONFIRM_PARENT_TRANSFER_SUCCESS = "confirmParentTransfer_success";
    String GROUP_SEARCH_CREATE_CLIENT = "GroupSearch_CreateClient";
    String GROUP_SEARCH_CLIENT_TRANSFER = "GroupSearch_ClientTransfer";
    String GROUP_SEARCH_ADD_CLIENTS_TO_GROUPS = "GroupSearch_AddClientToGroup";
    String LOAD_SEARCH_METHOD = "loadSearch";
    String LOAD_SEARCH_SUCCESS = "loadSearch_success";
    String CHOOSE_OFFICE_SUCCESS = "chooseOffice_success";

    // forwards to cancel pages
    String CANCEL_CREATE = "cancel_create";
    String CANCEL_MANAGE = "cancel_manage";
    String GROUP_DETAILS_PAGE = "group_details_page";
    String CLIENT_DETAILS_PAGE = "client_details_page";
    String CANCEL_TRANSFER_BRANCH = "cancel_transferbranch";
    // action-methods in Center Module used in Group
    String CENTER_SEARCH = "CenterSearch";
    String CENTER_INFO = "centerInfo";
    String CENTER_SEARCH_INPUT = "centerSearchInput";
    String CENTER_SEARCH_RESULT = "centerSearchResult";
    // create pages
    String CREATE_NEW_GROUP = "CreateNewGroup";
    String CREATE_MEETING = "CreateMeeting";
    String CREATE_NEW_GROUP_FAILURE = "CreateNewGroup_failure";
    String MANAGE_GROUP = "ManageGroup";
    String MANAGE_GROUP_FAILURE = "ManageGroup_failure";

    String CREATE_MANAGE_GROUP = "CreateManageGroup";
    String CHANGE_GROUP_STATUS = "ChangeGroupStatus";
    String MANAGE_SUCCESS = "manage_success";
    String CREATE_NEW_GROUP_PAGE = "load_success";
    String UPDATE_STATUS_SUCCESS = "updateStatus_success";
    String LOAD_CENTERS = "LoadCenters";
    // preview pages
    String PREVIEW_CREATE_NEW_GROUP = "PreviewCreateNewGroup";
    String PREVIEW_CREATE_NEW_GROUP_FAILURE = "PreviewCreateNewGroup_failure";

    String PREVIEW_MANAGE_GROUP = "PreviewManageGroup";
    String PREVIEW_CHANGE_GROUP_STATUS = "PreviewChangeGroupStatus";

    String PREFIX_SEARCH_STRING = "1.";
    // actions
    String CENTER_SEARCH_ACTION = "CenterSearchAction";
    String GROUP_ACTION = "GroupAction";

    String TOTAL_AMOUNT_DUE = "totalAmountDue";
    String IS_GROUP_LOAN_ALLOWED = "isGroupLoanAllowed";
    // Exceptions
    String TRANSFER_EX_ACTIVE_LOAN_ACCOUNTS = "errors.transferex_activeloans";
    String TRANSFER_EX_CLIENT_HAS_ACTIVE_LOAN_ACCOUNTS = "errors.transferex_clienthasactiveloan";
    String TRANSFER_EX_CLIENT_TRANSFER_FAILED = "errors.transferex_clienttransferfailed";
    String GROUP_INVALID_VERSION = "errors.groupinvalidversion";
    String GROUP_HAS_ACTIVE_ACCOUNTS = "errors.grouphasactiveaccounts";
    String GROUP_CLIENTS_HAS_ACTIVE_LOAN = "errors.groupclientshasactiveloan";
    String GROUP_CLIENTS_ARE_ACTIVE = "errors.groupclientsareactive";
    String GROUP_LOANOFFICER_NOT_ASSIGNED = "errors.grouploanofficernotassigned";
    String MEETING_NOT_ASSIGNED = "errors.meetingnotassigned";
    String GROUP_NOT_CLOSED = "errors.groupnotclosed";
    String DUPLICATE_GROUP = "errors.duplicategroup";
    String MANDATORY = "errors.mandatory";
    String MAX_LENGTH = "errors.maxlength";
    String MANDATORY_SELECT = "errors.mandatoryselect";
    String VIEW_GROUP_CHARGES = "ViewGroupCharges";

    //
    String UPDATE_FAILED = "errors.groupupdate";
    String CREATE_FAILED = "errors.groupcreate";
    String GROUP_NOT_FOUND = "errors.groupnotfound";

    String MEETING_REQUIRED = "errors.meetingrrequired";
    String DUPLICATE_FEE = "errors.duplicatefee";
    String INVALID_FEE_AMNT = "errors.invalidfeeamt";
    String FEE_WITHOUT_MEETING = "errors.feewithoutmeeting";
    String TRAINED_DATE = "errors.traineddate";
    String SAME_PARENT = "errors.sameparent";
    String SAME_BRANCH = "errors.samebranch";
    String INCOMPLETE_CHECKLIST = "errors.incompletechecklist";
    String BRANCH_INACTIVE = "errors.branchinactive";
    String FEE_INACTIVE = "errors.feeinactive";
    String CENTER_INACTIVE = "errors.centerinactive";
    String LOAN_OFFICER_REQUIRED = "errors.loanofficerrequired";
    String LOANOFFICER_INACTIVE = "errors.loanofficerinactive";
    String LOAN_OFFICER_REQUIRED_FOR_ACTIVE_GROUP = "errors.loanofficerrequired_activegroup";

    int MAX_ADDRESS_LINE_LENGTH = 200;
    int MAX_FIELD_LENGTH = 100;
    String TRAINED = "1";
    String NOT_TRAINED = "0";
    String CREATE_GROUP = "createGroup";
    String GROUPLOANACCOUNTSINUSE = "groupLoanAccountsInUse";
    String GROUPSAVINGSACCOUNTSINUSE = "groupSavingsAccountsInUse";
    String ASSIGNED_LOAN_OFFICER_REQUIRED = "errors.assignedloanofficerrequired";
    String METHODCALLED = "methodCalled";
    String IMPOSSIBLE_TO_CREATE_GROUP_LOAN = "errors.impossibletocreategrouploan";

}
