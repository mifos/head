/**
 * 
 */
package org.mifos.framework.security.util;

import java.util.HashMap;
import java.util.Map;

import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.resources.SecurityConstants;

public class ActivityMapper {

	private final short SAVING_CANCHANGESTATETO_PARTIALAPPLICATION = 140;

	private final short SAVING_CANCHANGESTATETO_PENDINGAPPROVAL = 180;

	private final short SAVING_CANCHANGESTATETO_CANCEL = 181;

	private final short SAVING_CANCHANGESTATETO_APPROVED = 182;

	private final short SAVING_CANCHANGESTATETO_INACTIVE = 183;

	private final short SAVING_CANCHANGESTATETO_INACTIVE_BLACKLISTED = 184;

	private final short SAVING_BLACKLISTED_FLAG = 6;

	private final short SAVING_CANSAVEFORLATER = 137;

	private final short SAVING_CANSUBMITFORAPPROVAL = 185;

	private final short LOANACC_CANCHANGETO_PARTIALAPPLICATION = 103;

	private final short LOANACC_CANCHANGETO_PENDINGAPPROVAL = 108;

	private final short LOANACC_CANCHANGETO_APPROVED = 104;

	private final short LOANACC_CANCHANGETO_DBTOLOANOFFICER = 106;

	private final short LOANACC_CANCHANGETO_ACTIVEINGOODSTANDING = 107;

	private final short LOANACC_CANCHANGETO_OBLIGATIONSMET = 111;

	private final short LOANACC_CANCHANGETO_WRITTENOFF = 109;

	private final short LOANACC_CANCHANGETO_RESCHEDULED = 110;

	private final short LOANACC_CANCHANGETO_BADSTANDING = 112;

	private final short LOANACC_CANCHANGETO_CANCEL = 105;

	private final short LOANACC_CANSAVEFORLATER = 101;

	private final short LOANACC_CANSUBMITFORAPPROVAL = 102;

	// client state change mappings

	private final short CLIENT_CANCHANGETO_PARTIALAPPLICATION = 37;

	private final short CLIENT_CANCHANGETO_APPROVED = 38;

	private final short CLIENT_CANCHANGETO_CANCELLED = 39;

	private final short CLIENT_CANCHANGETO_ONHOLD = 40;

	private final short CLIENT_CANCHANGETO_CLOSED = 41;

	private final short CLIENT_CANCHANGETO_PENDINGAPPROVAL = 42;

	private final short CLIENT_BLACKLISTED_FLAG = 3;

	private final short CLIENT_CLOSED_BLACKLISTED_FLAG = 8;

	private final short CLIENT_CANCHANGETO_CANCEL_BLACKLISTED = 55;

	private final short CLIENT_CREATEPARTIAL = 35;

	private final short CLIENT_CREATEPENDING = 36;

	// group sate change mappings
	private final short GROUP_CANCHANGETO_PARTIALAPPLICATION = 59;

	private final short GROUP_CANCHANGETO_APPROVED = 60;

	private final short GROUP_CANCHANGETO_CANCELLED = 61;

	private final short GROUP_CANCHANGETO_ONHOLD = 62;

	private final short GROUP_CANCHANGETO_CLOSED = 63;

	private final short GROUP_CANCHANGETO_PENDINGAPPROVAL = 64;

	private final short GROUP_CANCEL_BLACKLISTED_FLAG = 13;

	private final short GROUP_CLOSED_BLACKLISTED_FLAG = 18;

	private final short GROUP_CANCHANGETO_CANCEL_BLACKLISTED = 77;

	private final short GROUP_CREATEPARTIAL = 57;

	private final short GROUP_CREATEPENDING = 58;

	private ActivityMapper() {

		activityMap.put("/AdminAction-load", SecurityConstants.VIEW);

		// customer serach action
		activityMap.put("/CustomerSearchAction-load", SecurityConstants.VIEW);
		activityMap.put("/CustomerSearchAction-search",
				SecurityConstants.SEARCH);
		activityMap.put("/CustomerSearchAction-preview",
				SecurityConstants.SEARCH);
		activityMap.put("/CustomerSearchAction-get", SecurityConstants.SEARCH);
		activityMap.put("/CustomerSearchAction-getHomePage",
				SecurityConstants.VIEW);
		activityMap.put("/CustomerSearchAction-getOfficeHomePage",
				SecurityConstants.VIEW);
		activityMap.put("/CustomerSearchAction-loadAllBranches",
				SecurityConstants.VIEW);

		activityMap.put("/mifoslogout-logout", SecurityConstants.VIEW);

		// Change password related
		activityMap.put("/mifoslogin-update", SecurityConstants.VIEW);

		// Office related mapping
		activityMap.put("/OfficeAction-loadall", SecurityConstants.VIEW);
		activityMap.put("/OfficeAction-load",
				SecurityConstants.OFFICE_CREATE_OFFICE);

		activityMap.put("/OfficeAction-loadParent", SecurityConstants.VIEW);
		activityMap.put("/OfficeAction-preview", SecurityConstants.VIEW);
		activityMap.put("/OfficeAction-create",
				SecurityConstants.OFFICE_CREATE_OFFICE);
		activityMap.put("/OfficeAction-get", SecurityConstants.VIEW);
		activityMap.put("/OfficeAction-manage",
				SecurityConstants.OFFICE_EDIT_OFFICE);

		activityMap.put("/OfficeAction-previous", SecurityConstants.VIEW);
		activityMap.put("/OfficeAction-update",
				SecurityConstants.OFFICE_EDIT_OFFICE);

		activityMap
				.put("/OfficeHierarchyAction-cancel", SecurityConstants.VIEW);
		activityMap.put("/OfficeHierarchyAction-load", SecurityConstants.VIEW);
		activityMap.put("/OfficeHierarchyAction-update",
				SecurityConstants.OFFICE_EDIT_OFFICE);
		
		//m2 office action 
		activityMap.put("/offAction-load",
				SecurityConstants.OFFICE_CREATE_OFFICE);
		activityMap.put("/offAction-loadParent", SecurityConstants.VIEW);
		activityMap.put("/offAction-preview", SecurityConstants.VIEW);
		activityMap.put("/offAction-previous", SecurityConstants.VIEW);
		activityMap.put("/offAction-create",
				SecurityConstants.OFFICE_CREATE_OFFICE);
		activityMap.put("/offAction-get",
				SecurityConstants.VIEW);
		activityMap.put("/offAction-edit",
				SecurityConstants.OFFICE_EDIT_OFFICE);

		// roles and permission related mappings
		activityMap.put("/manageRolesAndPermission-manage",
				SecurityConstants.VIEW);
		activityMap
				.put("/manageRolesAndPermission-get", SecurityConstants.VIEW);
		activityMap.put("/manageRolesAndPermission-load",
				SecurityConstants.ROLES_CREATE_ROLES);
		activityMap.put("/manageRolesAndPermission-create",
				SecurityConstants.ROLES_CREATE_ROLES);
		activityMap.put("/manageRolesAndPermission-update",
				SecurityConstants.ROLES_EDIT_ROLES);
		activityMap.put("/manageRolesAndPermission-cancel",
				SecurityConstants.VIEW);
		activityMap.put("/manageRolesAndPermission-preview",
				SecurityConstants.ROLES_DELETE_ROLES);
		activityMap.put("/manageRolesAndPermission-delete",
				SecurityConstants.ROLES_DELETE_ROLES);

		// Group ralated mappings
		// activities related to create group
		activityMap.put("/GroupAction-chooseOffice", SecurityConstants.VIEW);
		activityMap.put("/GroupAction-hierarchyCheck", SecurityConstants.VIEW);
		activityMap.put("/GroupAction-load", SecurityConstants.VIEW);
		activityMap.put("/GroupAction-loadMeeting",
				SecurityConstants.MEETING_CREATE_GROUP_MEETING);
		activityMap.put("/GroupAction-setDefaultFormedByPersonnel",
				SecurityConstants.VIEW);
		activityMap.put("/GroupAction-previous", SecurityConstants.VIEW);
		activityMap.put("/GroupAction-preview", SecurityConstants.VIEW);
		activityMap.put("/GroupAction-create", SecurityConstants.VIEW);
		activityMap.put("/GroupAction-search", SecurityConstants.VIEW);

		// activities related to retrieve group for Group Module
		activityMap.put("/GroupAction-getDetails", SecurityConstants.VIEW);
		activityMap.put("/GroupAction-get", SecurityConstants.VIEW);

		// activities related to update group status for Group Module
		activityMap.put("/GroupAction-loadStatus", SecurityConstants.VIEW);
		activityMap.put("/GroupAction-updateStatus", SecurityConstants.VIEW);

		// activities related to update group for Group Module
		activityMap.put("/GroupAction-manage",
				SecurityConstants.GROUP_EDIT_GROUP);
		activityMap.put("/GroupAction-update",
				SecurityConstants.GROUP_EDIT_GROUP);

		// activities related to change center membership for Group Module
		activityMap.put("/GroupAction-loadParentTransfer",
				SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);
		activityMap.put("/GroupAction-confirmParentTransfer",
				SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);
		activityMap.put("/GroupAction-updateParent",
				SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);

		// activities related to change center membership for Group Module
		activityMap.put("/GroupAction-loadParentTransfer",
				SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);
		activityMap.put("/GroupAction-confirmParentTransfer",
				SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);
		activityMap.put("/GroupAction-updateParent",
				SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);

		// activities related to transfer the group for Group Module
		activityMap.put("/GroupAction-loadTransfer",
				SecurityConstants.GROUP_TRANSFER_THE_GROUP);
		activityMap.put("/GroupAction-confirmBranchTransfer",
				SecurityConstants.GROUP_TRANSFER_THE_GROUP);
		activityMap.put("/GroupAction-updateBranch",
				SecurityConstants.GROUP_TRANSFER_THE_GROUP);
		// apply charges
		activityMap.put("/closedaccsearchaction-search",
				SecurityConstants.GROUP_APPLY_CHARGES_TO_GROUP_ACCOUNT);
		activityMap.put("/GroupAction-loadSearch", SecurityConstants.VIEW);

		// Customer Notes related mapping

		// activityMap.put("/CustomerNoteAction-load",SecurityConstants.CUSTOMER_ADD_NOTES_TO_CENTER_GROUP_CLIENT);
		activityMap.put("/CustomerNoteAction-preview", SecurityConstants.VIEW);
		activityMap.put("/CustomerNoteAction-previous", SecurityConstants.VIEW);
		activityMap.put("/CustomerNoteAction-create", SecurityConstants.VIEW);
		activityMap.put("/CustomerNoteAction-get", SecurityConstants.VIEW);
		activityMap.put("/CustomerNoteAction-search", SecurityConstants.VIEW);

		// mapping for group
		activityMap.put("/CustomerNoteAction-load-Group",
				SecurityConstants.GROUP_ADD_NOTE_TO_GROUP);
		activityMap.put("/CustomerNoteAction-load-Client",
				SecurityConstants.CLIENT_ADD_NOTE_TO_CLIENT);
		activityMap.put("/CustomerNoteAction-load-Center",
				SecurityConstants.CENTER_ADD_NOTE_TO_CENTER);

		// Customer Historical Data related mapping
		activityMap.put("/CustomerHistoricalDataAction-load-Group",
				SecurityConstants.GROUP_ADD_EDIT_HISTORICAL_DATA);
		activityMap.put("/CustomerHistoricalDataAction-load-Client",
				SecurityConstants.CIENT_ADD_EDIT_HISTORICAL_DATA);
		activityMap.put("/CustomerHistoricalDataAction-get",
				SecurityConstants.VIEW);
		activityMap.put("/CustomerHistoricalDataAction-preview",
				SecurityConstants.VIEW);
		activityMap.put("/CustomerHistoricalDataAction-previous",
				SecurityConstants.VIEW);
		activityMap.put("/CustomerHistoricalDataAction-update",
				SecurityConstants.VIEW);

		// personnel related mappings
		activityMap.put("/PersonnelAction-chooseOffice",
				SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
		activityMap.put("/PersonnelAction-load",
				SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
		activityMap.put("/PersonnelAction-preview", SecurityConstants.VIEW);
		activityMap.put("/PersonnelAction-previous", SecurityConstants.VIEW);
		activityMap.put("/PersonnelAction-create",
				SecurityConstants.PERSONNEL_CREATE_PERSONNEL);

		activityMap.put("/PersonnelAction-get", SecurityConstants.VIEW);
		activityMap.put("/PersonnelAction-manage",
				SecurityConstants.PERSONNEL_EDIT_PERSONNEL);
		activityMap.put("/PersonnelAction-update",
				SecurityConstants.PERSONNEL_EDIT_PERSONNEL);

		activityMap.put("/PersonnelAction-loadSearch", SecurityConstants.VIEW);
		activityMap.put("/PersonnelAction-search", SecurityConstants.VIEW);

		activityMap.put("/PersonnelAction-loadUnLockUser",
				SecurityConstants.PERSONNEL_UNLOCK_PERSONNEL);
		activityMap.put("/PersonnelAction-unLockUserAccount",
				SecurityConstants.PERSONNEL_UNLOCK_PERSONNEL);

		// for your settings link
		activityMap.put("/PersonnelAction-getDetails", SecurityConstants.VIEW);
		activityMap.put("/PersonnelAction-editPersonalInfo",
				SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
		activityMap.put("/PersonnelAction-previewPersonalInfo",
				SecurityConstants.VIEW);
		activityMap.put("/PersonnelAction-prevPersonalInfo",
				SecurityConstants.VIEW);
		activityMap.put("/PersonnelAction-updateSettings",
				SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
		activityMap.put("/PersonnelAction-loadChangePassword",
				SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
		activityMap.put("/PersonnelNotesAction-load",
				SecurityConstants.PERSONNEL_EDIT_PERSONNEL);
		activityMap
				.put("/PersonnelNotesAction-preview", SecurityConstants.VIEW);
		activityMap.put("/PersonnelNotesAction-previous",
				SecurityConstants.VIEW);
		activityMap.put("/PersonnelNotesAction-create",
				SecurityConstants.PERSONNEL_EDIT_PERSONNEL);
		activityMap.put("/PersonnelNotesAction-search", SecurityConstants.VIEW);
		activityMap.put("/PersonnelNotesAction-get", SecurityConstants.VIEW);

		// center ralated mappings
		activityMap.put("/centerAction-chooseOffice",
				SecurityConstants.CENTER_CREATE_NEW_CENTER);
		activityMap.put("/centerAction-load",
				SecurityConstants.CENTER_CREATE_NEW_CENTER);
		activityMap.put("/centerAction-loadMeeting-CenterCreate",
				SecurityConstants.MEETING_CREATE_CENTER_MEETING);
		activityMap.put("/centerAction-loadMeeting-Center",
				SecurityConstants.MEETING_UPDATE_CENTER_MEETING);

		activityMap.put("/centerAction-getDetails", SecurityConstants.VIEW);
		activityMap.put("/centerAction-get", SecurityConstants.VIEW);
		activityMap.put("/centerAction-loadStatus",
				SecurityConstants.CENTER_EDIT_STATUS);
		activityMap.put("/centerAction-updateStatus",
				SecurityConstants.CENTER_EDIT_STATUS);
		activityMap
				.put(
						"/centerAction-manage",
						SecurityConstants.CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS);
		activityMap.put("/centerAction-previous", SecurityConstants.VIEW);
		activityMap.put("/centerAction-preview", SecurityConstants.VIEW);
		activityMap.put("/centerAction-create",
				SecurityConstants.CENTER_CREATE_NEW_CENTER);
		activityMap
				.put(
						"/centerAction-update",
						SecurityConstants.CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS);
		activityMap.put("/centerAction-loadSearch", SecurityConstants.VIEW);
		activityMap.put("/centerAction-search", SecurityConstants.VIEW);

		
		//For M2 Center ------------------------
		activityMap.put("/centerCustAction-chooseOffice", SecurityConstants.CENTER_CREATE_NEW_CENTER);
		activityMap.put("/centerCustAction-load",	SecurityConstants.CENTER_CREATE_NEW_CENTER);
		activityMap.put("/centerCustAction-loadMeeting-CenterCreate", SecurityConstants.MEETING_CREATE_CENTER_MEETING);
		activityMap.put("/centerCustAction-previous", SecurityConstants.VIEW);
		activityMap.put("/centerCustAction-preview", SecurityConstants.VIEW);
		activityMap.put("/centerCustAction-create",	SecurityConstants.CENTER_CREATE_NEW_CENTER);
		activityMap.put(
				"/centerCustAction-manage",
				SecurityConstants.CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS);
		activityMap.put("/centerCustAction-editPrevious", SecurityConstants.VIEW);
		activityMap.put("/centerCustAction-editPreview", SecurityConstants.VIEW);
		activityMap.put(
				"/centerCustAction-update",
				SecurityConstants.CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS);
		
		activityMap.put("/centerCustAction-get", SecurityConstants.VIEW);
		
		
		// For M2 Center ends
		
		// colsed account searchaction in center details page
		activityMap
				.put("/closedaccsearchaction-search", SecurityConstants.VIEW);

		// CustomerSearch
		// activityMap.put("/CustomerSearchAction-validate",SecurityConstants.VIEW);

		// client creation action
		activityMap
				.put("/clientCreationAction-preLoad", SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-load", SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-chooseOffice",
				SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-setDefaultFormedByPersonnel",
				SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-next", SecurityConstants.VIEW);
		activityMap
				.put("/clientCreationAction-preview", SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-prevPersonalInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-prevMFIInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-create", SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-get", SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-getDetails",
				SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-loadStatus",
				SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-loadTransfer",
				SecurityConstants.CIENT_CHANGE_GROUP_MEMBERSHIP);
		activityMap.put("/clientCreationAction-loadBranchTransfer",
				SecurityConstants.CIENT_TRANSFER_THE_CLIENT);
		activityMap.put("/clientCreationAction-editMFIInfo",
				SecurityConstants.CIENT_EDIT_MFI_INFORMATION);
		activityMap.put("/clientCreationAction-editPersonalInfo",
				SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
		activityMap.put("/clientCreationAction-update",
				SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
		activityMap.put("/clientCreationAction-updateMfi",
				SecurityConstants.CIENT_EDIT_MFI_INFORMATION);

		activityMap.put("/clientCreationAction-loadMeeting-ClientCreate",
				SecurityConstants.MEETING_CREATE_CLIENT_MEETING);
		activityMap.put("/clientCreationAction-loadMeeting-Client",
				SecurityConstants.MEETING_UPDATE_CLIENT_MEETING);

		activityMap.put("/clientCreationAction-previous",
				SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-retrievePictureOnPreview",
				SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-retrievePicture",
				SecurityConstants.VIEW);
		activityMap.put("/clientCreationAction-showPicture",
				SecurityConstants.VIEW);
		activityMap
				.put(
						"/clientCreationAction-loadHistoricalData",
						SecurityConstants.CUSTOMER_ADD_HISTORICAL_DATA_TO_CENTER_GROUP_CLIENT);

		activityMap.put("/clientStatusAction-load", SecurityConstants.VIEW);
		activityMap.put("/clientStatusAction-preview", SecurityConstants.VIEW);
		activityMap.put("/clientStatusAction-previous", SecurityConstants.VIEW);
		activityMap.put("/clientStatusAction-update", SecurityConstants.VIEW);

		activityMap.put("/clientTransferAction-loadGroupTransfer",
				SecurityConstants.VIEW);
		activityMap.put("/clientTransferAction-loadBranches",SecurityConstants.CIENT_TRANSFER_THE_CLIENT);
		activityMap.put("/clientTransferAction-previewBranchTransfer",SecurityConstants.VIEW);
		activityMap.put("/clientTransferAction-confirmGroupTransfer",
				SecurityConstants.VIEW);
		activityMap.put("/clientTransferAction-update",	SecurityConstants.CIENT_CHANGE_GROUP_MEMBERSHIP);	
		activityMap.put("/clientTransferAction-transferToBranch",SecurityConstants.CIENT_TRANSFER_THE_CLIENT);

		// meeting action
		activityMap.put("/MeetingAction-load", SecurityConstants.VIEW);
		activityMap.put("/MeetingAction-loadMeeting-Group",
				SecurityConstants.MEETING_UPDATE_GROUP_MEETING);
		activityMap.put("/MeetingAction-loadMeeting-Client",
				SecurityConstants.MEETING_UPDATE_CLIENT_MEETING);
		activityMap.put("/MeetingAction-create", SecurityConstants.VIEW);
		activityMap.put("/MeetingAction-get-Group",
				SecurityConstants.MEETING_UPDATE_GROUP_MEETING);
		activityMap.put("/MeetingAction-get-Client",
				SecurityConstants.MEETING_UPDATE_CLIENT_MEETING);
		activityMap.put("/MeetingAction-get-Center",
				SecurityConstants.MEETING_UPDATE_CENTER_MEETING);
		activityMap.put("/MeetingAction-update", SecurityConstants.VIEW);
		activityMap.put("/MeetingAction-preview", SecurityConstants.VIEW);
		activityMap.put("/MeetingAction-previous", SecurityConstants.VIEW);

		// product categories mapping

		activityMap.put("/mifosproddefaction-load",
				SecurityConstants.DEFINE_NEW_PRODUCT_CATEGORIES);
		activityMap.put("/mifosproddefaction-preview", SecurityConstants.VIEW);
		activityMap.put("/mifosproddefaction-create",
				SecurityConstants.DEFINE_NEW_PRODUCT_CATEGORIES);
		activityMap.put("/mifosproddefaction-search", SecurityConstants.VIEW);
		activityMap.put("/mifosproddefaction-previous", SecurityConstants.VIEW);
		activityMap.put("/mifosproddefaction-get", SecurityConstants.VIEW);
		activityMap.put("/mifosproddefaction-manage",
				SecurityConstants.EDIT_PRODUCT_CATEGORIES);
		activityMap.put("/mifosproddefaction-update",
				SecurityConstants.EDIT_PRODUCT_CATEGORIES);

		// saving product mappeings
		activityMap.put("/savingsprdaction-search", SecurityConstants.VIEW);
		activityMap.put("/savingsprdaction-load",
				SecurityConstants.DEFINE_NEW_SAVING_PRODUCT_INSTANCE);
		activityMap.put("/savingsprdaction-preview", SecurityConstants.VIEW);
		activityMap.put("/savingsprdaction-previous", SecurityConstants.VIEW);
		activityMap.put("/savingsprdaction-create",
				SecurityConstants.DEFINE_NEW_SAVING_PRODUCT_INSTANCE);
		activityMap.put("/savingsprdaction-get", SecurityConstants.VIEW);
		activityMap.put("/savingsprdaction-manage",
				SecurityConstants.EDIT_SAVING_PRODUCT);
		activityMap.put("/savingsprdaction-update",
				SecurityConstants.EDIT_SAVING_PRODUCT);

		// view lateness mappings
		activityMap.put("/prdconfigurationaction-search",
				SecurityConstants.VIEW);
		activityMap.put("/prdconfigurationaction-update",
				SecurityConstants.EDIT_PRODUCT_CATEGORIES);

		// loan product categories

		activityMap.put("/loanprdaction-load",
				SecurityConstants.DEFINE_NEW_LOAN_PRODUCT_INSTANCE);
		activityMap.put("/loanprdaction-preview", SecurityConstants.VIEW);
		activityMap.put("/loanprdaction-previous", SecurityConstants.VIEW);
		activityMap.put("/loanprdaction-create",
				SecurityConstants.DEFINE_NEW_LOAN_PRODUCT_INSTANCE);
		activityMap.put("/loanprdaction-search", SecurityConstants.VIEW);
		activityMap.put("/loanprdaction-get", SecurityConstants.VIEW);
		activityMap.put("/loanprdaction-manage",
				SecurityConstants.EDIT_LOAN_PRODUCT);
		activityMap.put("/loanprdaction-update",
				SecurityConstants.EDIT_LOAN_PRODUCT);

		// Fee mapping
		activityMap.put("/feesAction-search", SecurityConstants.VIEW);
		activityMap.put("/feesAction-load", SecurityConstants.FEES_CREATE_FEES);
		activityMap.put("/feesAction-preview", SecurityConstants.VIEW);
		activityMap.put("/feesAction-create",
				SecurityConstants.FEES_CREATE_FEES);
		activityMap.put("/feesAction-get", SecurityConstants.VIEW);
		activityMap.put("/feesAction-manage", SecurityConstants.FEES_EDIT_FEES);
		activityMap.put("/feesAction-update", SecurityConstants.FEES_EDIT_FEES);
		activityMap.put("/feesAction-previous", SecurityConstants.VIEW);

		activityMap.put("/feeaction-search", SecurityConstants.VIEW);
		activityMap.put("/feeaction-load", SecurityConstants.FEES_CREATE_FEES);
		activityMap.put("/feeaction-preview", SecurityConstants.VIEW);
		activityMap.put("/feeaction-editPreview", SecurityConstants.VIEW);
		activityMap
				.put("/feeaction-create", SecurityConstants.FEES_CREATE_FEES);
		activityMap.put("/feeaction-get", SecurityConstants.VIEW);
		activityMap.put("/feeaction-manage", SecurityConstants.FEES_EDIT_FEES);
		activityMap.put("/feeaction-update", SecurityConstants.FEES_EDIT_FEES);
		activityMap.put("/feeaction-previous", SecurityConstants.VIEW);
		activityMap.put("/feeaction-editPrevious", SecurityConstants.VIEW);
		activityMap.put("/feeaction-viewAll", SecurityConstants.VIEW);
		// checklist mapping

		activityMap.put("/checkListAction-loadall", SecurityConstants.VIEW);
		activityMap.put("/checkListAction-load",
				SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
		activityMap.put("/checkListAction-create",
				SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
		activityMap.put("/checkListAction-preview", SecurityConstants.VIEW);
		activityMap.put("/checkListAction-previous", SecurityConstants.VIEW);
		activityMap.put("/checkListAction-previous",
				SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
		activityMap.put("/checkListAction-loadParent", SecurityConstants.VIEW);
		activityMap.put("/checkListAction-get", SecurityConstants.VIEW);
		activityMap.put("/checkListAction-manage",
				SecurityConstants.CHECKLIST_EDIT_CHECKLIST);
		activityMap.put("/checkListAction-update",
				SecurityConstants.CHECKLIST_EDIT_CHECKLIST);

		// mapping for search before loan
		activityMap.put("/AccountsSearchAction-load", SecurityConstants.VIEW);
		activityMap.put("/AccountsSearchAction-search", SecurityConstants.VIEW);

		// mapping for loan
		activityMap.put("/loanAction-getPrdOfferings", SecurityConstants.VIEW);
		activityMap.put("/loanAction-load", SecurityConstants.VIEW);
		activityMap.put("/loanAction-next", SecurityConstants.VIEW);
		activityMap.put("/loanAction-preview", SecurityConstants.VIEW);
		activityMap.put("/loanAction-previous", SecurityConstants.VIEW);
		activityMap.put("/loanAction-get", SecurityConstants.VIEW);
		activityMap.put("/loanAction-manage",
				SecurityConstants.LOAN_UPDATE_LOAN);
		activityMap.put("/loanAction-update",
				SecurityConstants.LOAN_UPDATE_LOAN);
		activityMap.put("/loanAction-getLoanChangeLog", SecurityConstants.VIEW);
		activityMap.put("/loanAction-search", SecurityConstants.VIEW);
		activityMap.put("/loanAction-create", SecurityConstants.VIEW);

		// mapping for loanActivity
		activityMap.put("/loanAccountAction-getAllActivity",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-get",SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-getLoanRepaymentSchedule",SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-viewStatusHistory", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-manage", SecurityConstants.LOAN_UPDATE_LOAN);
		activityMap.put("/loanAccountAction-managePreview",SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-managePrevious",SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-cancel",SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-update", SecurityConstants.LOAN_UPDATE_LOAN);

		activityMap.put("/loanAccountAction-getPrdOfferings", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-load", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-schedulePreview", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-preview", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-previous", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-create", SecurityConstants.VIEW);
		// mapping for account status::TO BE REMOVED
		activityMap.put("/LoanStatusAction-load", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-preview", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-previous", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-update", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-search", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-writeOff", SecurityConstants.VIEW);
		
		// mapping for account status
		activityMap.put("/editStatusAction-load", SecurityConstants.VIEW);
		activityMap.put("/editStatusAction-preview", SecurityConstants.VIEW);
		activityMap.put("/editStatusAction-previous", SecurityConstants.VIEW);
		activityMap.put("/editStatusAction-update", SecurityConstants.VIEW);

		// mapping for account notes
		activityMap.put("/AccountNotesAction-load",
				SecurityConstants.LOAN_CAN_ADD_NOTES_TO_LOAN);
		activityMap.put("/AccountNotesAction-preview", SecurityConstants.VIEW);
		activityMap.put("/AccountNotesAction-previous", SecurityConstants.VIEW);
		activityMap.put("/AccountNotesAction-get", SecurityConstants.VIEW);
		activityMap.put("/AccountNotesAction-search", SecurityConstants.VIEW);
		activityMap.put("/AccountNotesAction-create",
				SecurityConstants.LOAN_CAN_ADD_NOTES_TO_LOAN);

		// mapping for apply payment
		activityMap.put("/accountTrxn-load",
				SecurityConstants.APPLY_PAYMENT_TO_CLIENT_GROUP_CENTERS_LOANS);
		activityMap.put("/accountTrxn-create",
				SecurityConstants.APPLY_PAYMENT_TO_CLIENT_GROUP_CENTERS_LOANS);
		activityMap.put("/accountTrxn-preview", SecurityConstants.VIEW);
		activityMap.put("/accountTrxn-getInstallmentHistory",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-getInstallmentDetails",
				SecurityConstants.VIEW);
		activityMap.put("/accountTrxn-previous", SecurityConstants.VIEW);

		// apply charges
		activityMap.put("/AccountsApplyChargesAction-load",
				SecurityConstants.APPLY_CHARGES_TO_CLIENT_GROUP_CENTERS_LOANS);
		activityMap.put("/AccountsApplyChargesAction-create",
				SecurityConstants.APPLY_CHARGES_TO_CLIENT_GROUP_CENTERS_LOANS);

		// fund mapping
		activityMap.put("/fundAction-load",
				SecurityConstants.FUNDS_CREATE_FUNDS);
		activityMap.put("/fundAction-create",
				SecurityConstants.FUNDS_CREATE_FUNDS);
		activityMap.put("/fundAction-preview", SecurityConstants.VIEW);
		activityMap.put("/fundAction-getAllFunds", SecurityConstants.VIEW);
		activityMap.put("/fundAction-get", SecurityConstants.VIEW);
		activityMap.put("/fundAction-update",
				SecurityConstants.FUNDS_EDIT_FUNDS);
		activityMap.put("/fundAction-previous",
				SecurityConstants.FUNDS_CREATE_FUNDS);
		activityMap.put("/fundAction-manage",
				SecurityConstants.FUNDS_EDIT_FUNDS);

		// mapping for bulk entry
		activityMap.put("/bulkentryaction-load", SecurityConstants.VIEW);
		activityMap.put("/bulkentryaction-preview", SecurityConstants.VIEW);
		activityMap.put("/bulkentryaction-previous", SecurityConstants.VIEW);
		activityMap.put("/bulkentryaction-get", SecurityConstants.VIEW);
		activityMap.put("/bulkentryaction-getLastMeetingDateForCustomer",
				SecurityConstants.VIEW);
		activityMap.put("/bulkentryaction-create", SecurityConstants.VIEW);
		activityMap.put("/bulkentryaction-loadLoanOfficers",
				SecurityConstants.VIEW);
		activityMap.put("/bulkentryaction-loadCustomerList",
				SecurityConstants.VIEW);
		activityMap.put("/bulkentryaction-validate", SecurityConstants.VIEW);

		// removing fees
		activityMap.put("/accountAppAction-removeFees",
				SecurityConstants.LOAN_REMOVE_FEE_TYPE_ATTACHED_TO_ACCOUNT);
		activityMap.put("/accountAppAction-getTrxnHistory",
				SecurityConstants.VIEW);

		// mapping for savings account
		activityMap.put("/savingsAction-getPrdOfferings",
				SecurityConstants.VIEW);
		activityMap.put("/savingsAction-load", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-reLoad", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-preview", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-previous", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-create", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-get", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-getStatusHistory", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-edit",
				SecurityConstants.SAVINGS_UPDATE_SAVINGS);
		activityMap.put("/savingsAction-editPreview",
				SecurityConstants.SAVINGS_UPDATE_SAVINGS);
		activityMap.put("/savingsAction-editPrevious",
				SecurityConstants.SAVINGS_UPDATE_SAVINGS);
		activityMap.put("/savingsAction-update",
				SecurityConstants.SAVINGS_UPDATE_SAVINGS);
		activityMap.put("/savingsAction-getRecentActivity",
				SecurityConstants.VIEW);
		activityMap.put("/savingsAction-getTransactionHistory",
				SecurityConstants.VIEW);
		activityMap.put("/savingsAction-getDepositDueDetails",
				SecurityConstants.VIEW);
		activityMap.put("/savingsAction-waiveAmountDue",
				SecurityConstants.SAVINGS_CANWAIVE_DUEAMOUNT);
		activityMap.put("/savingsAction-waiveAmountOverDue",
				SecurityConstants.SAVINGS_CANWAIVE_OVERDUEAMOUNT);

		// close savings account
		activityMap.put("/savingsClosureAction-load",
				SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
		activityMap.put("/savingsClosureAction-preview",
				SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
		activityMap.put("/savingsClosureAction-previous",
				SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
		activityMap.put("/savingsClosureAction-close",
				SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);

		// mapping for edit savings account status
		activityMap.put("/editSavingsAction-load", SecurityConstants.VIEW);
		activityMap.put("/editSavingsAction-preview", SecurityConstants.VIEW);
		activityMap.put("/editSavingsAction-previous", SecurityConstants.VIEW);
		activityMap.put("/editSavingsAction-update", SecurityConstants.VIEW);

		// savings accountadjustment
		activityMap.put("/savingsApplyAdjustmentAction-load",
				SecurityConstants.SAVINGS_APPLY_ADJUSTMENT);
		activityMap.put("/savingsApplyAdjustmentAction-preview",
				SecurityConstants.SAVINGS_APPLY_ADJUSTMENT);
		activityMap.put("/savingsApplyAdjustmentAction-previous",
				SecurityConstants.SAVINGS_APPLY_ADJUSTMENT);
		activityMap.put("/savingsApplyAdjustmentAction-adjustLastUserAction",
				SecurityConstants.SAVINGS_APPLY_ADJUSTMENT);

		// entries for apply adjustment.

		activityMap.put("/applyAdjustment-loadAdjustment",
				SecurityConstants.LOAN_MAKE_ADJUSTMENT_ENTRY_TO_ACCOUNT);
		activityMap.put("/applyAdjustment-previewAdjustment",
				SecurityConstants.LOAN_MAKE_ADJUSTMENT_ENTRY_TO_ACCOUNT);
		activityMap.put("/applyAdjustment-applyAdjustment",
				SecurityConstants.LOAN_MAKE_ADJUSTMENT_ENTRY_TO_ACCOUNT);
		activityMap.put("/applyAdjustment-cancelAdjustment",
				SecurityConstants.LOAN_MAKE_ADJUSTMENT_ENTRY_TO_ACCOUNT);

		// mapping for repaying loan account
		activityMap.put("/repayLoanAction-loadRepayment",
				SecurityConstants.LOAN_CAN_REPAY_LOAN);
		activityMap.put("/repayLoanAction-preview",
				SecurityConstants.LOAN_CAN_REPAY_LOAN);
		activityMap.put("/repayLoanAction-previous",
				SecurityConstants.LOAN_CAN_REPAY_LOAN);
		activityMap.put("/repayLoanAction-makeRepayment",
				SecurityConstants.LOAN_CAN_REPAY_LOAN);

		// mapping for reports
		activityMap.put("/reportsAction-load", SecurityConstants.VIEW);

		activityMap.put("/reportsAction-report_designer",
				SecurityConstants.CLIENTSDETAILVIEW);
		activityMap.put("/reportsAction-product_history",
				SecurityConstants.CLIENTSPRODUCTHISTORY);

		activityMap.put("/reportsAction-branch_performance",
				SecurityConstants.BRANCHPERFORMANCE);
		activityMap.put("/reportsAction-area_performance",
				SecurityConstants.AREAPERFORMANCE);

		activityMap.put("/reportsAction-collection_sheet",
				SecurityConstants.COLLECTIONSHEET);

		activityMap.put("/reportsAction-loan_distribution",
				SecurityConstants.LOANDISTRIBUTION);
		activityMap.put("/reportsAction-branch_disbursement",
				SecurityConstants.BRANCHDISBURSEMENT);

		activityMap.put("/reportsAction-staffwise_report",
				SecurityConstants.STAFFWISEREPORT);
		activityMap.put("/reportsAction-branchwise_report",
				SecurityConstants.BRANCHWISEREPORT);

		activityMap.put("/reportsAction-analysis", SecurityConstants.ANALYSIS);
		activityMap.put("/reportsAction-kendra_meeting",
				SecurityConstants.KENDRA_MEETING);

		activityMap.put("/reportsAction-administerreports_path",
				SecurityConstants.ADMINISTER_REPORTS);

		// entries for apply adjustment for customer.
		activityMap
				.put(
						"/custApplyAdjustment-loadAdjustment-Client",
						SecurityConstants.CIENT_MAKE_ADJUSTMENT_ENTRIES_TO_CLIENT_ACCOUNT);
		activityMap.put("/custApplyAdjustment-previewAdjustment-Client",
				SecurityConstants.VIEW);
		activityMap.put("/custApplyAdjustment-applyAdjustment-Client",
				SecurityConstants.VIEW);
		activityMap.put("/custApplyAdjustment-cancelAdjustment-Client",
				SecurityConstants.VIEW);

		activityMap
				.put(
						"/custApplyAdjustment-loadAdjustment-Group",
						SecurityConstants.GROUP_MAKE_ADJUSTMENT_ENTRIES_TO_GROUP_ACCOUNT);
		activityMap.put("/custApplyAdjustment-previewAdjustment-Group",
				SecurityConstants.VIEW);
		activityMap.put("/custApplyAdjustment-applyAdjustment-Group",
				SecurityConstants.VIEW);
		activityMap.put("/custApplyAdjustment-cancelAdjustment-Group",
				SecurityConstants.VIEW);

		activityMap
				.put(
						"/custApplyAdjustment-loadAdjustment-Center",
						SecurityConstants.CENTER_MAKE_ADJUSTMENT_ENTRIES_TO_CENTER_ACCOUNT);
		activityMap.put("/custApplyAdjustment-previewAdjustment-Center",
				SecurityConstants.VIEW);
		activityMap.put("/custApplyAdjustment-applyAdjustment-Center",
				SecurityConstants.VIEW);
		activityMap.put("/custApplyAdjustment-cancelAdjustment-Center",
				SecurityConstants.VIEW);

		activityMap.put("/customerAction-forwardWaiveChargeDue",
				SecurityConstants.VIEW);
		activityMap.put("/customerAction-forwardWaiveChargeOverDue",
				SecurityConstants.VIEW);
		activityMap.put("/customerAction-waiveChargeDue-Client",
				SecurityConstants.CIENT_WAIVE_FEE_INSTALLMENT);
		activityMap.put("/customerAction-waiveChargeOverDue-Client",
				SecurityConstants.CIENT_WAIVE_FEE_INSTALLMENT);
		activityMap.put("/customerAction-waiveChargeDue-Group",
				SecurityConstants.GROUP_WAIVE_FEE_INSTALLMENT);
		activityMap.put("/customerAction-waiveChargeOverDue-Group",
				SecurityConstants.GROUP_WAIVE_FEE_INSTALLMENT);
		activityMap.put("/customerAction-waiveChargeDue-Center",
				SecurityConstants.CENTER_WAIVE_FEE_INSTALLMENT);
		activityMap.put("/customerAction-waiveChargeOverDue-Center",
				SecurityConstants.CENTER_WAIVE_FEE_INSTALLMENT);

		activityMap.put("/customerAction-getAllActivity",
				SecurityConstants.VIEW);
		activityMap.put("/customerAction-getAllClosedAccounts",
				SecurityConstants.VIEW);
		
		activityMap.put("/loanAccountAction-waiveChargeDue",
				SecurityConstants.LOAN_WAIVE_FEE_INSTALLMENT);		
		activityMap.put("/loanAccountAction-waiveChargeDue",
				SecurityConstants.LOAN_WAIVE_PANELTY);	
		activityMap.put("/loanAccountAction-forwardWaiveCharge",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-waiveChargeOverDue",
				SecurityConstants.LOAN_WAIVE_FEE_INSTALLMENT);		
		activityMap.put("/loanAccountAction-waiveChargeOverDue",
				SecurityConstants.LOAN_WAIVE_PANELTY);	
				
		
		// mapping for accountPayment
		activityMap.put("/applyPaymentAction-load-Loan",	SecurityConstants.LOAN_MAKE_PAYMENT_TO_ACCOUNT);
		activityMap.put("/applyPaymentAction-load-Center",	SecurityConstants.CENTER_MAKE_PAYMENTS_TO_CENTER_ACCOUNT);
		activityMap.put("/applyPaymentAction-load-Group",	SecurityConstants.GROUP_MAKE_PAYMENT_TO_GROUP_ACCOUNT);
		activityMap.put("/applyPaymentAction-load-Client",	SecurityConstants.CIENT_MAKE_PAYMENT_TO_CLIENT_ACCOUNT);
		activityMap.put("/applyPaymentAction-preview",	SecurityConstants.VIEW);
		activityMap.put("/applyPaymentAction-previous",SecurityConstants.VIEW);
		activityMap.put("/applyPaymentAction-applyPayment",SecurityConstants.VIEW);
		
		
		//mapping fro loan disbursal 
		activityMap.put("/loanDisbursmentAction-load",	SecurityConstants.LOAN_CAN_DISBURSE_LOAN);
		activityMap.put("/loanDisbursmentAction-preview",	SecurityConstants.VIEW);
		activityMap.put("/loanDisbursmentAction-previous",SecurityConstants.VIEW);
		activityMap.put("/loanDisbursmentAction-update",SecurityConstants.VIEW);
		
		//mapping for savings deposit/withdrawal
		activityMap.put("/savingsDepositWithdrawalAction-load",	SecurityConstants.SAVINGS_CAN_MAKE_DEPOSIT_WITHDRAWAL);
		activityMap.put("/savingsDepositWithdrawalAction-reLoad",	SecurityConstants.SAVINGS_CAN_MAKE_DEPOSIT_WITHDRAWAL);
		activityMap.put("/savingsDepositWithdrawalAction-preview",	SecurityConstants.VIEW);
		activityMap.put("/savingsDepositWithdrawalAction-previous",SecurityConstants.VIEW);
		activityMap.put("/savingsDepositWithdrawalAction-makePayment",SecurityConstants.VIEW);
		
		// mapping for notes
		activityMap.put("/notesAction-load-Savings",
				SecurityConstants.SAVINGS_CAN_ADD_NOTES_TO_SAVINGS);
		activityMap.put("/notesAction-load-Loan",
				SecurityConstants.LOAN_CAN_ADD_NOTES_TO_LOAN);
		activityMap.put("/notesAction-preview", SecurityConstants.VIEW);
		activityMap.put("/notesAction-previous", SecurityConstants.VIEW);
		activityMap.put("/notesAction-get", SecurityConstants.VIEW);
		activityMap.put("/notesAction-search", SecurityConstants.VIEW);
		activityMap.put("/notesAction-create-Savings",
				SecurityConstants.SAVINGS_CAN_ADD_NOTES_TO_SAVINGS);
		
		

		//mapping for editing customer status
		activityMap.put("/editCustomerStatusAction-load-Center", SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-preview", SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-previous", SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-update", SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-load-Group", SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-load-Client", SecurityConstants.VIEW);
		

		activityMap.put("/notesAction-create-Loan",
				SecurityConstants.LOAN_CAN_ADD_NOTES_TO_LOAN);
		
	// apply charges
		activityMap.put("/applyChargeAction-load",
				SecurityConstants.VIEW);
		activityMap.put("/applyChargeAction-update",
				SecurityConstants.APPLY_CHARGES_TO_CLIENT_GROUP_CENTERS_LOANS);
		//mapping for adding customer notes
		activityMap.put("/customerNotesAction-load-Center", SecurityConstants.CENTER_ADD_NOTE_TO_CENTER);
		activityMap.put("/customerNotesAction-preview", SecurityConstants.VIEW);
		activityMap.put("/customerNotesAction-previous", SecurityConstants.VIEW);
		activityMap.put("/customerNotesAction-create", SecurityConstants.VIEW);
		activityMap.put("/customerNotesAction-search", SecurityConstants.VIEW);
		activityMap.put("/customerNotesAction-load-Group", SecurityConstants.VIEW);
		activityMap.put("/customerNotesAction-load-Client", SecurityConstants.VIEW);
		
		
		// client creation action- migration
		activityMap.put("/clientCustAction-load", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-chooseOffice",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-next", SecurityConstants.VIEW);
		activityMap
				.put("/clientCustAction-preview", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-previewPersonalInfo", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-retrievePictureOnPreview",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-prevPersonalInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-prevMFIInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-prevMeeting",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-create", SecurityConstants.VIEW);

		activityMap.put("/clientCustAction-loadMeeting-ClientCreate",
				SecurityConstants.MEETING_CREATE_CLIENT_MEETING);
		activityMap.put("/clientCustAction-get", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-editMFIInfo",SecurityConstants.CIENT_EDIT_MFI_INFORMATION);
		activityMap.put("/clientCustAction-editPersonalInfo",
				SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
		activityMap.put("/clientCustAction-previewEditPersonalInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-prevEditPersonalInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-updatePersonalInfo",
				SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
		activityMap.put("/clientCustAction-updateMfi",
				SecurityConstants.CIENT_EDIT_MFI_INFORMATION);
		activityMap.put("/clientCustAction-loadMeeting-Client",
				SecurityConstants.MEETING_UPDATE_CLIENT_MEETING);
		activityMap.put("/clientCustAction-retrievePicture",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-showPicture",
				SecurityConstants.VIEW);
		
		

		

	}

	private static ActivityMapper instance = new ActivityMapper();

	public static ActivityMapper getInstance() {
		return instance;
	}

	private Map<String, Short> activityMap = new HashMap<String, Short>();

	public Short getActivityId(String key) {
		return activityMap.get(key);
	}

	public short getActivityIdForNewStateId(short newState, short cancelFlag) {

		short activityId = -1;
		switch (newState) {
		case AccountStates.SAVINGS_ACC_APPROVED:
			activityId = SAVING_CANCHANGESTATETO_APPROVED;
			break;
		case AccountStates.SAVINGS_ACC_CANCEL:
			switch (cancelFlag) {
			case SAVING_BLACKLISTED_FLAG:
				activityId = SAVING_CANCHANGESTATETO_INACTIVE_BLACKLISTED;
				break;
			default:
				activityId = SAVING_CANCHANGESTATETO_CANCEL;
				break;
			}
			break;
		case AccountStates.SAVINGS_ACC_INACTIVE:
			activityId = SAVING_CANCHANGESTATETO_INACTIVE;
			break;
		case AccountStates.SAVINGS_ACC_PARTIALAPPLICATION:
			activityId = SAVING_CANCHANGESTATETO_PARTIALAPPLICATION;
			break;
		case AccountStates.SAVINGS_ACC_PENDINGAPPROVAL:
			activityId = SAVING_CANCHANGESTATETO_PENDINGAPPROVAL;
			break;

		// loan mappings
		case AccountStates.LOANACC_ACTIVEINGOODSTANDING:
			activityId = LOANACC_CANCHANGETO_ACTIVEINGOODSTANDING;
			break;
		case AccountStates.LOANACC_APPROVED:
			activityId = LOANACC_CANCHANGETO_APPROVED;
			break;
		case AccountStates.LOANACC_BADSTANDING:
			activityId = LOANACC_CANCHANGETO_BADSTANDING;
			break;
		case AccountStates.LOANACC_CANCEL:
			activityId = LOANACC_CANCHANGETO_CANCEL;
			break;
		case AccountStates.LOANACC_DBTOLOANOFFICER:
			activityId = LOANACC_CANCHANGETO_DBTOLOANOFFICER;
			break;
		case AccountStates.LOANACC_OBLIGATIONSMET:
			activityId = LOANACC_CANCHANGETO_OBLIGATIONSMET;
			break;
		case AccountStates.LOANACC_PARTIALAPPLICATION:
			activityId = LOANACC_CANCHANGETO_PARTIALAPPLICATION;
			break;
		case AccountStates.LOANACC_PENDINGAPPROVAL:
			activityId = LOANACC_CANCHANGETO_PENDINGAPPROVAL;
			break;
		case AccountStates.LOANACC_RESCHEDULED:
			activityId = LOANACC_CANCHANGETO_RESCHEDULED;
			break;
		case AccountStates.LOANACC_WRITTENOFF:
			activityId = LOANACC_CANCHANGETO_WRITTENOFF;
			break;
		default:
			break;
		}
		return activityId;
	}

	public short getActivityIdForState(short state) {
		short activityId = -1;
		switch (state) {
		case AccountStates.SAVINGS_ACC_PARTIALAPPLICATION:
			activityId = SAVING_CANSAVEFORLATER;
			break;
		case AccountStates.SAVINGS_ACC_PENDINGAPPROVAL:
		case AccountStates.SAVINGS_ACC_APPROVED:
			activityId = SAVING_CANSUBMITFORAPPROVAL;
			break;

		case AccountStates.LOANACC_PARTIALAPPLICATION:
			activityId = LOANACC_CANSAVEFORLATER;
			break;
		case AccountStates.LOANACC_PENDINGAPPROVAL:
		case AccountStates.LOANACC_APPROVED:
			activityId = LOANACC_CANSUBMITFORAPPROVAL;
			break;

		default:
			break;
		}
		return activityId;
	}

	public short getActivityIdForNewCustomerStateId(short newState,
			short cancelFlag) {

		short activityId = -1;
		switch (newState) {
		case CustomerConstants.CLIENT_APPROVED:
			activityId = CLIENT_CANCHANGETO_APPROVED;
			break;
		case CustomerConstants.CLIENT_CANCELLED:
			switch (cancelFlag) {
			case CLIENT_BLACKLISTED_FLAG:
				activityId = CLIENT_CANCHANGETO_CANCEL_BLACKLISTED;
				break;
			default:
				activityId = CLIENT_CANCHANGETO_CANCELLED;
				break;
			}
			break;
		case CustomerConstants.CLIENT_CLOSED:
			switch (cancelFlag) {
			case CLIENT_CLOSED_BLACKLISTED_FLAG:
				activityId = CLIENT_CANCHANGETO_CANCEL_BLACKLISTED;
				break;
			default:
				activityId = CLIENT_CANCHANGETO_CLOSED;
				break;
			}
			break;
		case CustomerConstants.CLIENT_ONHOLD:
			activityId = CLIENT_CANCHANGETO_ONHOLD;
			break;
		case CustomerConstants.CLIENT_PARTIAL:
			activityId = CLIENT_CANCHANGETO_PARTIALAPPLICATION;
			break;
		case CustomerConstants.CLIENT_PENDING:
			activityId = CLIENT_CANCHANGETO_PENDINGAPPROVAL;
			break;

		// group mappings

		case GroupConstants.PARTIAL_APPLICATION:
			activityId = GROUP_CANCHANGETO_PARTIALAPPLICATION;
			break;
		case GroupConstants.CANCELLED:
			switch (cancelFlag) {
			case GROUP_CANCEL_BLACKLISTED_FLAG:
				activityId = GROUP_CANCHANGETO_CANCEL_BLACKLISTED;
				break;
			default:
				activityId = GROUP_CANCHANGETO_CANCELLED;
				break;
			}
			break;
		case GroupConstants.CLOSED:
			switch (cancelFlag) {
			case GROUP_CLOSED_BLACKLISTED_FLAG:
				activityId = GROUP_CANCHANGETO_CANCEL_BLACKLISTED;
				break;
			default:
				activityId = GROUP_CANCHANGETO_CLOSED;
				break;
			}
			break;
		case GroupConstants.HOLD:
			activityId = GROUP_CANCHANGETO_ONHOLD;
			break;
		case GroupConstants.PENDING_APPROVAL:
			activityId = GROUP_CANCHANGETO_PENDINGAPPROVAL;
			break;
		case GroupConstants.ACTIVE:
			activityId = GROUP_CANCHANGETO_APPROVED;
			break;
		default:
			break;
		}

		return activityId;
	}

	public short getActivityIdForCustomerState(short state) {
		short activityId = -1;
		switch (state) {
		case CustomerConstants.CLIENT_PARTIAL:
			activityId = CLIENT_CREATEPARTIAL;
			break;
		case CustomerConstants.CLIENT_PENDING:
		case CustomerConstants.CLIENT_APPROVED:
			activityId = CLIENT_CREATEPENDING;
			break;
		case GroupConstants.PARTIAL_APPLICATION:
			activityId = GROUP_CREATEPARTIAL;
			break;
		case GroupConstants.PENDING_APPROVAL:
		case GroupConstants.ACTIVE:
			activityId = GROUP_CREATEPENDING;
			break;

		default:
			break;
		}
		return activityId;
	}

	public Map<String, Short> getActivityMap() {
		return activityMap;
	}

	public void setActivityMap(Map<String, Short> activityMap) {
		this.activityMap = activityMap;
	}

	public boolean isStateChangePermittedForAccount(short newSate,
			short stateFlag, UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(getActivityIdForNewStateId(newSate,
						stateFlag), recordOfficeId, recordLoanOfficerId));
	}

	public boolean isStateChangePermittedForCustomer(short newSate,
			short stateFlag, UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(getActivityIdForNewCustomerStateId(newSate,
						stateFlag), recordOfficeId, recordLoanOfficerId));
	}

	public boolean isSavePermittedForAccount(short newSate,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(getActivityIdForState(newSate),
						recordOfficeId, recordLoanOfficerId));
	}

	public boolean isSavePermittedForCustomer(short newSate,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(getActivityIdForCustomerState(newSate),
						recordOfficeId, recordLoanOfficerId));
	}
}
