/**
 *
 */
package org.mifos.framework.security.util;

import java.util.HashMap;
import java.util.Map;

import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
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
	
	private final short CENTER_CHANGE_STATUS = 81;

	private ActivityMapper() {
		addSearchActionMappings();
		addCustomerSearchMappings();
		addLoginMappings();
		addOfficeMappings();
		addManageRolesPermissionsMappings();
		addCustomerHistoricalDataMappings();
		addPersonnelMappings();
		addCenterMappings();
		addClientAndGroupTransferMappings();
		addMeetingActionMappings();
		addProductCategoryMappings();
		addSavingProductMappings();
		addViewLatenessMappings();
		addLoanProductCategoryMappings();
		addFeeMappings();
		addChecklistMappings();
		addSearchBeforeLoanMappings();
		addLoanMappings();
		addLoanActivityMappings();
		addLoanStatusActionMappings();
		addAccountStatusMappings();
		addCustomerActionMappings();
		addApplyPaymentMappings();
		addApplyChargesMappings2();
		addFundMappings();
		addBulkEntryMappings();
		addRemoveFeeMappings();
		addSavingsAccountMappings();
		addCloseSavingsAccountMappings();
		addSavingsAccountAdjustmentMappings();
		addApplyAdjustmentMappings();
		addApplyCustomerAdjustmentMappings();
		addRepayLoanAccountMappings();
		addWaiveChargesMappings();
		addPaymentMappings();
		addLoanDisbursalMappings();
		addSavingsDepositMappings();
		addEditCustomerStatusMappings();
		addApplyChargesMappings();
		addClientCustomerMappings();
		addChangeAccountStatusMappings();
		addGroupMappings();
		addYourSettingsMappings();
		addCustomerAccountActionMappings();
		addRolesPermissionsMappings();
		addPersonnelNotesMappings();
		addCustomerNotesMappings();
		addNotesMappings();
		addReportsMappings();
		addLabelConfigurationMappings();
		addHolidayMappings();
		addSurveysMappings();
		addHiddenMandatoryConfigurationMappings();
	}

	private void addCustomerAccountActionMappings() {
		activityMap.put("/customerAccountAction-load", SecurityConstants.VIEW);
	}

	private void addSearchActionMappings() {
		activityMap.put("/custSearchAction-loadSearch", SecurityConstants.VIEW);
		activityMap.put("/custSearchAction-search", SecurityConstants.VIEW);
		activityMap.put("/custSearchAction-load", SecurityConstants.VIEW);
		activityMap.put("/custSearchAction-loadMainSearch",
				SecurityConstants.VIEW);
		activityMap.put("/custSearchAction-mainSearch", SecurityConstants.VIEW);
		activityMap
				.put("/custSearchAction-getHomePage", SecurityConstants.VIEW);
		activityMap.put("/AdminAction-load", SecurityConstants.VIEW);
		activityMap.put("/custSearchAction-loadAllBranches",
				SecurityConstants.VIEW);
		activityMap.put("/custSearchAction-get", SecurityConstants.VIEW);
		activityMap.put("/custSearchAction-preview", SecurityConstants.VIEW);
		activityMap.put("/custSearchAction-getOfficeHomePage",
				SecurityConstants.VIEW);
	}

	private void addCustomerSearchMappings() {
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
	}

	private void addLoginMappings() {
		activityMap.put("/loginAction-login", SecurityConstants.VIEW);
		activityMap.put("/loginAction-logout", SecurityConstants.VIEW);
		activityMap.put("/loginAction-updatePassword", SecurityConstants.VIEW);
	}

	private void addOfficeMappings() {
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

		activityMap.put("/offhierarchyaction-cancel", SecurityConstants.VIEW);
		activityMap.put("/offhierarchyaction-load", SecurityConstants.VIEW);
		activityMap.put("/offhierarchyaction-update",
				SecurityConstants.OFFICE_HIERATCHY_UPDATE);

		// m2 office action
		activityMap.put("/offAction-load",
				SecurityConstants.OFFICE_CREATE_OFFICE);
		activityMap.put("/offAction-loadParent", SecurityConstants.VIEW);
		activityMap.put("/offAction-preview", SecurityConstants.VIEW);
		activityMap.put("/offAction-previous", SecurityConstants.VIEW);
		activityMap.put("/offAction-create",
				SecurityConstants.OFFICE_CREATE_OFFICE);
		activityMap.put("/offAction-get", SecurityConstants.VIEW);
		activityMap
				.put("/offAction-edit", SecurityConstants.OFFICE_EDIT_OFFICE);
		activityMap.put("/offAction-editpreview",
				SecurityConstants.OFFICE_EDIT_OFFICE);
		activityMap.put("/offAction-editprevious",
				SecurityConstants.OFFICE_EDIT_OFFICE);
		activityMap.put("/offAction-update",
				SecurityConstants.OFFICE_EDIT_OFFICE);
		activityMap.put("/offAction-getAllOffices", SecurityConstants.VIEW);
		activityMap.put("/offAction-updateCache", SecurityConstants.VIEW);
	}

	private void addManageRolesPermissionsMappings() {
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
	}

	private void addCustomerHistoricalDataMappings() {
		activityMap.put("/custHistoricalDataAction-loadHistoricalData",
				SecurityConstants.VIEW);
		activityMap.put("/custHistoricalDataAction-getHistoricalData",
				SecurityConstants.VIEW);
		activityMap.put("/custHistoricalDataAction-previewHistoricalData",
				SecurityConstants.VIEW);
		activityMap.put("/custHistoricalDataAction-previousHistoricalData",
				SecurityConstants.VIEW);
		activityMap.put("/custHistoricalDataAction-updateHistoricalData",
				SecurityConstants.VIEW);
		activityMap.put("/custHistoricalDataAction-cancelHistoricalData",
				SecurityConstants.VIEW);
	}

	private void addPersonnelMappings() {
		activityMap.put("/PersonAction-get", SecurityConstants.VIEW);
		activityMap.put("/PersonAction-loadSearch", SecurityConstants.VIEW);
		activityMap.put("/PersonAction-search", SecurityConstants.VIEW);
		activityMap.put("/PersonAction-chooseOffice",
				SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
		activityMap.put("/PersonAction-load",
				SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
		activityMap.put("/PersonAction-manage",
				SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
		activityMap.put("/PersonAction-previewManage", SecurityConstants.VIEW);

		activityMap.put("/PersonAction-previousManage",
				SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
		activityMap.put("/PersonAction-update",
				SecurityConstants.PERSONNEL_EDIT_PERSONNEL);
		activityMap.put("/PersonnelAction-prevPersonalInfo",
				SecurityConstants.VIEW);

		activityMap.put("/PersonAction-preview",
				SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
		activityMap.put("/PersonAction-previous",
				SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
		activityMap.put("/PersonAction-create",
				SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
		activityMap.put("/PersonAction-loadUnLockUser",
				SecurityConstants.PERSONNEL_UNLOCK_PERSONNEL);
		activityMap.put("/PersonAction-unLockUserAccount",
				SecurityConstants.PERSONNEL_UNLOCK_PERSONNEL);
		activityMap.put("/PersonAction-loadChangeLog", SecurityConstants.VIEW);
		activityMap
				.put("/PersonAction-cancelChangeLog", SecurityConstants.VIEW);
	}

	private void addCenterMappings() {
		activityMap.put("/centerCustAction-chooseOffice",
				SecurityConstants.CENTER_CREATE_NEW_CENTER);
		activityMap.put("/centerCustAction-load",
				SecurityConstants.CENTER_CREATE_NEW_CENTER);
		activityMap.put("/centerCustAction-loadMeeting",
				SecurityConstants.MEETING_CREATE_CENTER_MEETING);
		activityMap.put("/centerCustAction-previous", SecurityConstants.VIEW);
		activityMap.put("/centerCustAction-preview", SecurityConstants.VIEW);
		activityMap.put("/centerCustAction-create",
				SecurityConstants.CENTER_CREATE_NEW_CENTER);
		activityMap
				.put(
						"/centerCustAction-manage",
						SecurityConstants.CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS);
		activityMap.put("/centerCustAction-editPrevious",
				SecurityConstants.VIEW);
		activityMap
				.put("/centerCustAction-editPreview", SecurityConstants.VIEW);
		activityMap
				.put(
						"/centerCustAction-update",
						SecurityConstants.CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS);

		activityMap.put("/centerCustAction-get", SecurityConstants.VIEW);
		activityMap.put("/centerCustAction-loadSearch", SecurityConstants.VIEW);
		activityMap.put("/centerCustAction-search", SecurityConstants.VIEW);
		activityMap.put("/centerCustAction-loadChangeLog",
				SecurityConstants.VIEW);
		activityMap.put("/centerCustAction-cancelChangeLog",
				SecurityConstants.VIEW);

		activityMap.put("/centerCustAction-loadTransferSearch", SecurityConstants.VIEW);
		activityMap.put("/centerCustAction-searchTransfer", SecurityConstants.VIEW);
	}

	private void addClientAndGroupTransferMappings() {
		activityMap.put("/clientTransferAction-loadParents",
				SecurityConstants.CIENT_CHANGE_GROUP_MEMBERSHIP);
		activityMap.put("/clientTransferAction-loadBranches",
				SecurityConstants.CIENT_TRANSFER_THE_CLIENT);
		activityMap.put("/clientTransferAction-previewBranchTransfer",
				SecurityConstants.VIEW);
		activityMap.put("/clientTransferAction-previewParentTransfer",
				SecurityConstants.VIEW);
		activityMap.put("/clientTransferAction-updateParent",
				SecurityConstants.CIENT_CHANGE_GROUP_MEMBERSHIP);
		activityMap.put("/clientTransferAction-transferToBranch",
				SecurityConstants.CIENT_TRANSFER_THE_CLIENT);

		activityMap.put("/groupTransferAction-loadParents",
				SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);
		activityMap.put("/groupTransferAction-loadBranches",
				SecurityConstants.GROUP_TRANSFER_THE_GROUP);
		activityMap.put("/groupTransferAction-previewBranchTransfer",
				SecurityConstants.VIEW);
		activityMap.put("/groupTransferAction-previewParentTransfer",
				SecurityConstants.VIEW);
		activityMap.put("/groupTransferAction-transferToCenter",
				SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);
		activityMap.put("/groupTransferAction-transferToBranch",
				SecurityConstants.GROUP_TRANSFER_THE_GROUP);
	}

	private void addMeetingActionMappings() {
		activityMap.put("/meetingAction-load", SecurityConstants.VIEW);
		activityMap.put("/meetingAction-create", SecurityConstants.VIEW);
		activityMap.put("/meetingAction-update", SecurityConstants.VIEW);
		activityMap.put("/meetingAction-edit",SecurityConstants.VIEW);
		activityMap.put("/meetingAction-cancelCreate", SecurityConstants.VIEW);
		activityMap.put("/meetingAction-cancelUpdate", SecurityConstants.VIEW);
	}

	private void addProductCategoryMappings() {
		activityMap.put("/productCategoryAction-load",
				SecurityConstants.DEFINE_NEW_PRODUCT_CATEGORIES);
		activityMap.put("/productCategoryAction-createPreview",
				SecurityConstants.VIEW);
		activityMap.put("/productCategoryAction-create",
				SecurityConstants.DEFINE_NEW_PRODUCT_CATEGORIES);
		activityMap.put("/productCategoryAction-get", SecurityConstants.VIEW);
		activityMap.put("/productCategoryAction-managePreview",
				SecurityConstants.VIEW);
		activityMap.put("/productCategoryAction-manage",
				SecurityConstants.EDIT_PRODUCT_CATEGORIES);
		activityMap.put("/productCategoryAction-update",
				SecurityConstants.EDIT_PRODUCT_CATEGORIES);
		activityMap.put("/productCategoryAction-getAllCategories",
				SecurityConstants.VIEW);
		activityMap.put("/productCategoryAction-createPrevious",
				SecurityConstants.VIEW);
		activityMap.put("/productCategoryAction-managePrevious",
				SecurityConstants.VIEW);
	}

	private void addSavingProductMappings() {
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

		activityMap.put("/savingsproductaction-loadChangeLog",
				SecurityConstants.VIEW);
		activityMap.put("/savingsproductaction-cancelChangeLog",
				SecurityConstants.VIEW);
		activityMap.put("/savingsproductaction-search", SecurityConstants.VIEW);
		activityMap.put("/savingsproductaction-load",
				SecurityConstants.DEFINE_NEW_SAVING_PRODUCT_INSTANCE);
		activityMap
				.put("/savingsproductaction-preview", SecurityConstants.VIEW);
		activityMap.put("/savingsproductaction-previous",
				SecurityConstants.VIEW);
		activityMap.put("/savingsproductaction-create",
				SecurityConstants.DEFINE_NEW_SAVING_PRODUCT_INSTANCE);
		activityMap.put("/savingsproductaction-get", SecurityConstants.VIEW);
		activityMap.put("/savingsproductaction-cancelCreate",
				SecurityConstants.VIEW);
		activityMap.put("/savingsproductaction-manage",
				SecurityConstants.EDIT_SAVING_PRODUCT);
		activityMap.put("/savingsproductaction-previewManage",
				SecurityConstants.VIEW);
		activityMap.put("/savingsproductaction-previousManage",
				SecurityConstants.VIEW);
		activityMap.put("/savingsproductaction-update",
				SecurityConstants.EDIT_SAVING_PRODUCT);
		activityMap.put("/savingsproductaction-cancelEdit",
				SecurityConstants.VIEW);
	}

	private void addViewLatenessMappings() {
		activityMap.put("/prdconfigurationaction-search",
				SecurityConstants.VIEW);
		activityMap.put("/prdconfigurationaction-update",
				SecurityConstants.EDIT_PRODUCT_CATEGORIES);
		activityMap.put("/prdconfaction-load", SecurityConstants.VIEW);
		activityMap.put("/prdconfaction-update",
				SecurityConstants.UPDATE_LATENESS_DORMANCY);
	}

	private void addLoanProductCategoryMappings() {
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

		activityMap.put("/loanproductaction-load",
				SecurityConstants.DEFINE_NEW_LOAN_PRODUCT_INSTANCE);
		activityMap.put("/loanproductaction-preview", SecurityConstants.VIEW);
		activityMap.put("/loanproductaction-previous", SecurityConstants.VIEW);
		activityMap.put("/loanproductaction-cancelCreate",
				SecurityConstants.VIEW);
		activityMap.put("/loanproductaction-validate", SecurityConstants.VIEW);
		activityMap.put("/loanproductaction-create",
				SecurityConstants.DEFINE_NEW_LOAN_PRODUCT_INSTANCE);
		activityMap.put("/loanproductaction-viewAllLoanProducts",
				SecurityConstants.VIEW);
		activityMap.put("/loanproductaction-get", SecurityConstants.VIEW);
		activityMap.put("/loanproductaction-editPreview",
				SecurityConstants.VIEW);
		activityMap.put("/loanproductaction-editPrevious",
				SecurityConstants.VIEW);
		activityMap
				.put("/loanproductaction-editCancel", SecurityConstants.VIEW);
		activityMap.put("/loanproductaction-manage",
				SecurityConstants.EDIT_LOAN_PRODUCT);
		activityMap.put("/loanproductaction-update",
				SecurityConstants.EDIT_LOAN_PRODUCT);
		activityMap.put("/loanproductaction-update",
				SecurityConstants.EDIT_LOAN_PRODUCT);
		activityMap.put("/loanproductaction-loadChangeLog",
				SecurityConstants.VIEW);
		activityMap.put("/loanproductaction-cancelChangeLog",
				SecurityConstants.VIEW);
	}

	private void addFeeMappings() {
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
		activityMap.put("/feeaction-cancelCreate", SecurityConstants.VIEW);
		activityMap.put("/feeaction-cancelEdit", SecurityConstants.VIEW);
	}

	private void addChecklistMappings() {
		activityMap.put("/chkListAction-load",
				SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
		activityMap.put("/chkListAction-getStates", SecurityConstants.VIEW);
		activityMap.put("/chkListAction-preview", SecurityConstants.VIEW);
		activityMap.put("/chkListAction-previous",
				SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
		activityMap.put("/chkListAction-create",
				SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
		activityMap.put("/chkListAction-cancelCreate", SecurityConstants.VIEW);
		activityMap.put("/chkListAction-cancelManage", SecurityConstants.VIEW);

		activityMap.put("/chkListAction-manage",
				SecurityConstants.CHECKLIST_EDIT_CHECKLIST);
		activityMap.put("/chkListAction-getEditStates", SecurityConstants.VIEW);
		activityMap.put("/chkListAction-managePreview", SecurityConstants.VIEW);
		activityMap
				.put("/chkListAction-managePrevious", SecurityConstants.VIEW);
		activityMap.put("/chkListAction-update",
				SecurityConstants.CHECKLIST_EDIT_CHECKLIST);

		activityMap.put("/chkListAction-loadAllChecklist",
				SecurityConstants.VIEW);
		activityMap.put("/chkListAction-get", SecurityConstants.VIEW);

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
	}

	private void addSearchBeforeLoanMappings() {
		activityMap.put("/AccountsSearchAction-load", SecurityConstants.VIEW);
		activityMap.put("/AccountsSearchAction-search", SecurityConstants.VIEW);
	}

	private void addLoanMappings() {
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
	}

	private void addLoanActivityMappings() {
		activityMap.put("/loanAccountAction-getAllActivity",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-get", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-getLoanRepaymentSchedule",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-viewStatusHistory",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-manage",
				SecurityConstants.LOAN_UPDATE_LOAN);
		activityMap.put("/loanAccountAction-managePreview",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-managePrevious",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-cancel", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-update",
				SecurityConstants.LOAN_UPDATE_LOAN);

		activityMap.put("/loanAccountAction-getPrdOfferings",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-load", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-schedulePreview",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-preview", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-previous", SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-create", SecurityConstants.VIEW);

		activityMap.put("/loanAccountAction-loadChangeLog",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-cancelChangeLog",
				SecurityConstants.VIEW);
	}

	private void addLoanStatusActionMappings() {
		// mapping for account status::TO BE REMOVED
		activityMap.put("/LoanStatusAction-load", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-preview", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-previous", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-update", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-search", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-writeOff", SecurityConstants.VIEW);
	}

	private void addAccountStatusMappings() {
		activityMap.put("/editStatusAction-load", SecurityConstants.VIEW);
		activityMap.put("/editStatusAction-preview", SecurityConstants.VIEW);
		activityMap.put("/editStatusAction-previous", SecurityConstants.VIEW);
		activityMap.put("/editStatusAction-update", SecurityConstants.VIEW);
	}

	private void addCustomerActionMappings() {
		activityMap
				.put("/custAction-getClosedAccounts", SecurityConstants.VIEW);
		activityMap.put("/custAction-getBackToDetailsPage",
				SecurityConstants.VIEW);
	}

	private void addApplyPaymentMappings() {
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
	}

	private void addApplyChargesMappings2() {
		activityMap.put("/AccountsApplyChargesAction-load",
				SecurityConstants.APPLY_CHARGES_TO_CLIENT_GROUP_CENTERS_LOANS);
		activityMap.put("/AccountsApplyChargesAction-create",
				SecurityConstants.APPLY_CHARGES_TO_CLIENT_GROUP_CENTERS_LOANS);
	}

	private void addFundMappings() {
		activityMap.put("/fundAction-load",
				SecurityConstants.FUNDS_CREATE_FUNDS);
		activityMap.put("/fundAction-create",
				SecurityConstants.FUNDS_CREATE_FUNDS);
		activityMap.put("/fundAction-preview", SecurityConstants.VIEW);
		activityMap.put("/fundAction-previous",
				SecurityConstants.FUNDS_CREATE_FUNDS);
		activityMap.put("/fundAction-cancelCreate", SecurityConstants.VIEW);
		activityMap.put("/fundAction-cancelManage", SecurityConstants.VIEW);
		activityMap.put("/fundAction-manage",
				SecurityConstants.FUNDS_EDIT_FUNDS);
		activityMap.put("/fundAction-previewManage",
				SecurityConstants.FUNDS_EDIT_FUNDS);
		activityMap.put("/fundAction-previousManage",
				SecurityConstants.FUNDS_EDIT_FUNDS);
		activityMap.put("/fundAction-update",
				SecurityConstants.FUNDS_EDIT_FUNDS);
		activityMap.put("/fundAction-viewAllFunds", SecurityConstants.VIEW);
	}

	private void addBulkEntryMappings() {
		activityMap.put("/bulkentryaction-load",
				SecurityConstants.CAN_ENTER_COLLECTION_SHEET_DATA);
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
	}

	private void addRemoveFeeMappings() {
		activityMap.put("/accountAppAction-removeFees",	SecurityConstants.VIEW);
		activityMap.put("/accountAppAction-getTrxnHistory",	SecurityConstants.VIEW);
	}

	private void addSavingsAccountMappings() {
		activityMap.put("/savingsAction-getPrdOfferings",
				SecurityConstants.VIEW);
		activityMap.put("/savingsAction-load", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-reLoad", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-preview", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-previous", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-create", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-get", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-getStatusHistory",
				SecurityConstants.VIEW);
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
		activityMap.put("/savingsAction-loadChangeLog", SecurityConstants.VIEW);
		activityMap.put("/savingsAction-cancelChangeLog",
				SecurityConstants.VIEW);
	}

	private void addCloseSavingsAccountMappings() {
		activityMap.put("/savingsClosureAction-load",
				SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
		activityMap.put("/savingsClosureAction-preview",
				SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
		activityMap.put("/savingsClosureAction-previous",
				SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
		activityMap.put("/savingsClosureAction-close",
				SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
	}

	private void addSavingsAccountAdjustmentMappings() {
		activityMap.put("/savingsApplyAdjustmentAction-load",
				SecurityConstants.VIEW);
		activityMap.put("/savingsApplyAdjustmentAction-preview",
				SecurityConstants.VIEW);
		activityMap.put("/savingsApplyAdjustmentAction-previous",
				SecurityConstants.VIEW);
		activityMap.put("/savingsApplyAdjustmentAction-adjustLastUserAction",
				SecurityConstants.VIEW);
	}

	private void addApplyAdjustmentMappings() {
		activityMap.put("/applyAdjustment-loadAdjustment",
				SecurityConstants.VIEW);
		activityMap.put("/applyAdjustment-previewAdjustment",
				SecurityConstants.VIEW);
		activityMap.put("/applyAdjustment-applyAdjustment",
				SecurityConstants.VIEW);
		activityMap.put("/applyAdjustment-cancelAdjustment",
				SecurityConstants.VIEW);
	}

	private void addApplyCustomerAdjustmentMappings() {
		activityMap.put("/custApplyAdjustment-loadAdjustment",
				SecurityConstants.VIEW);
		activityMap.put("/custApplyAdjustment-previewAdjustment",
				SecurityConstants.VIEW);
		activityMap.put("/custApplyAdjustment-applyAdjustment",
				SecurityConstants.VIEW);
		activityMap.put("/custApplyAdjustment-cancelAdjustment",
				SecurityConstants.VIEW);
	}

	private void addRepayLoanAccountMappings() {
		activityMap.put("/repayLoanAction-loadRepayment",
				SecurityConstants.LOAN_CAN_REPAY_LOAN);
		activityMap.put("/repayLoanAction-preview",
				SecurityConstants.LOAN_CAN_REPAY_LOAN);
		activityMap.put("/repayLoanAction-previous",
				SecurityConstants.LOAN_CAN_REPAY_LOAN);
		activityMap.put("/repayLoanAction-makeRepayment",
				SecurityConstants.LOAN_CAN_REPAY_LOAN);
	}

	private void addRunReportsMappings() {
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
	}

	private void addWaiveChargesMappings() {
		activityMap.put("/customerAction-forwardWaiveChargeDue",
				SecurityConstants.VIEW);
		activityMap.put("/customerAction-forwardWaiveChargeOverDue",
				SecurityConstants.VIEW);
		activityMap.put("/customerAction-waiveChargeDue",
				SecurityConstants.VIEW);
		activityMap.put("/customerAction-waiveChargeOverDue",
				SecurityConstants.VIEW);
		activityMap.put("/customerAction-getAllActivity",
				SecurityConstants.VIEW);
		activityMap.put("/customerAction-getAllClosedAccounts",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-waiveChargeDue",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-forwardWaiveCharge",
				SecurityConstants.VIEW);
		activityMap.put("/loanAccountAction-waiveChargeOverDue",
				SecurityConstants.VIEW);
	}

	private void addPaymentMappings() {
		activityMap.put("/applyPaymentAction-load", SecurityConstants.VIEW);
		activityMap.put("/applyPaymentAction-preview", SecurityConstants.VIEW);
		activityMap.put("/applyPaymentAction-previous", SecurityConstants.VIEW);
		activityMap.put("/applyPaymentAction-applyPayment",
				SecurityConstants.VIEW);
	}

	private void addLoanDisbursalMappings() {
		activityMap.put("/loanDisbursmentAction-load",
				SecurityConstants.LOAN_CAN_DISBURSE_LOAN);
		activityMap.put("/loanDisbursmentAction-preview",
				SecurityConstants.VIEW);
		activityMap.put("/loanDisbursmentAction-previous",
				SecurityConstants.VIEW);
		activityMap
				.put("/loanDisbursmentAction-update", SecurityConstants.VIEW);
	}

	private void addSavingsDepositMappings() {
		activityMap.put("/savingsDepositWithdrawalAction-load",
				SecurityConstants.SAVINGS_CAN_MAKE_DEPOSIT_WITHDRAWAL);
		activityMap.put("/savingsDepositWithdrawalAction-reLoad",
				SecurityConstants.SAVINGS_CAN_MAKE_DEPOSIT_WITHDRAWAL);
		activityMap.put("/savingsDepositWithdrawalAction-preview",
				SecurityConstants.VIEW);
		activityMap.put("/savingsDepositWithdrawalAction-previous",
				SecurityConstants.VIEW);
		activityMap.put("/savingsDepositWithdrawalAction-makePayment",
				SecurityConstants.VIEW);
	}

	private void addEditCustomerStatusMappings() {
		activityMap.put("/editCustomerStatusAction-loadStatus",
				SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-preview",
				SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-previous",
				SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-update",
				SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-previewStatus",
				SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-previousStatus",
				SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-updateStatus",
				SecurityConstants.VIEW);
		activityMap.put("/editCustomerStatusAction-cancelStatus",
				SecurityConstants.VIEW);
	}

	private void addApplyChargesMappings() {
		activityMap.put("/applyChargeAction-load", SecurityConstants.VIEW);
		activityMap.put("/applyChargeAction-update",
				SecurityConstants.VIEW);
	}

	private void addChangeAccountStatusMappings() {
		activityMap.put("/ChangeAccountStatus-load",
				SecurityConstants.CAN_APPROVE_LOANS_IN_BULK);
		activityMap.put("/ChangeAccountStatus-searchResults",
				SecurityConstants.VIEW);
		activityMap.put("/ChangeAccountStatus-update", SecurityConstants.VIEW);
		activityMap.put("/ChangeAccountStatus-getLoanOfficers",
				SecurityConstants.VIEW);
	}

	private void addClientCustomerMappings() {
		activityMap.put("/clientCustAction-load", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-chooseOffice",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-next", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-preview", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-previewPersonalInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-retrievePictureOnPreview",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-prevPersonalInfo",
				SecurityConstants.VIEW);
		activityMap
				.put("/clientCustAction-prevMFIInfo", SecurityConstants.VIEW);
		activityMap
				.put("/clientCustAction-prevMeeting", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-create", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-loadMeeting",
				SecurityConstants.MEETING_CREATE_CLIENT_MEETING);
		activityMap.put("/clientCustAction-get", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-editPersonalInfo",
				SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
		activityMap.put("/clientCustAction-previewEditPersonalInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-prevEditPersonalInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-updatePersonalInfo",
				SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
		activityMap.put("/clientCustAction-editMfiInfo",
				SecurityConstants.CIENT_EDIT_MFI_INFORMATION);
		activityMap.put("/clientCustAction-previewEditMfiInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-prevEditMfiInfo",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-updateMfiInfo",
				SecurityConstants.CIENT_EDIT_MFI_INFORMATION);
		activityMap.put("/clientCustAction-retrievePicture",
				SecurityConstants.VIEW);
		activityMap
				.put("/clientCustAction-showPicture", SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-loadChangeLog",
				SecurityConstants.VIEW);
		activityMap.put("/clientCustAction-cancelChangeLog",
				SecurityConstants.VIEW);
	}

	private void addGroupMappings() {
		activityMap.put("/groupCustAction-hierarchyCheck",
				SecurityConstants.VIEW);
		activityMap
				.put("/groupCustAction-chooseOffice", SecurityConstants.VIEW);
		activityMap.put("/groupCustAction-load", SecurityConstants.VIEW);
		activityMap.put("/groupCustAction-loadMeeting",
				SecurityConstants.MEETING_CREATE_GROUP_MEETING);
		activityMap.put("/groupCustAction-preview", SecurityConstants.VIEW);
		activityMap.put("/groupCustAction-previous", SecurityConstants.VIEW);
		activityMap.put("/groupCustAction-create", SecurityConstants.VIEW);

		activityMap.put("/groupCustAction-getDetails", SecurityConstants.VIEW);
		activityMap.put("/groupCustAction-get", SecurityConstants.VIEW);
		activityMap.put("/groupCustAction-manage",
				SecurityConstants.GROUP_EDIT_GROUP);
		activityMap.put("/groupCustAction-previewManage",
				SecurityConstants.VIEW);
		activityMap.put("/groupCustAction-previousManage",
				SecurityConstants.VIEW);
		activityMap.put("/groupCustAction-update",
				SecurityConstants.GROUP_EDIT_GROUP);
		activityMap.put("/groupCustAction-loadSearch", SecurityConstants.VIEW);
		activityMap.put("/groupCustAction-search", SecurityConstants.VIEW);

		activityMap.put("/groupCustAction-loadChangeLog",
				SecurityConstants.VIEW);
		activityMap.put("/groupCustAction-cancelChangeLog",
				SecurityConstants.VIEW);
	}

	private void addYourSettingsMappings() {
		activityMap.put("/yourSettings-get", SecurityConstants.VIEW);
		activityMap.put("/yourSettings-manage",
				SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
		activityMap.put("/yourSettings-preview", SecurityConstants.VIEW);
		activityMap.put("/yourSettings-previous", SecurityConstants.VIEW);
		activityMap.put("/yourSettings-update",
				SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
		activityMap.put("/yourSettings-loadChangePassword",
				SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
	}

	private void addRolesPermissionsMappings() {
		activityMap.put("/rolesPermission-viewRoles", SecurityConstants.VIEW);
		activityMap.put("/rolesPermission-load",
				SecurityConstants.ROLES_CREATE_ROLES);
		activityMap.put("/rolesPermission-create",
				SecurityConstants.ROLES_CREATE_ROLES);
		activityMap.put("/rolesPermission-manage", SecurityConstants.VIEW);
		activityMap.put("/rolesPermission-update",
				SecurityConstants.ROLES_EDIT_ROLES);
		activityMap.put("/rolesPermission-cancel", SecurityConstants.VIEW);
		activityMap.put("/rolesPermission-preview",
				SecurityConstants.ROLES_DELETE_ROLES);
		activityMap.put("/rolesPermission-delete",
				SecurityConstants.ROLES_DELETE_ROLES);
	}

	private void addPersonnelNotesMappings() {
		activityMap.put("/personnelNoteAction-load", SecurityConstants.VIEW);
		activityMap.put("/personnelNoteAction-preview", SecurityConstants.VIEW);
		activityMap
				.put("/personnelNoteAction-previous", SecurityConstants.VIEW);
		activityMap.put("/personnelNoteAction-create", SecurityConstants.VIEW);
		activityMap.put("/personnelNoteAction-search", SecurityConstants.VIEW);
	}

	private void addCustomerNotesMappings() {
		activityMap.put("/customerNotesAction-load", SecurityConstants.VIEW);
		activityMap.put("/customerNotesAction-preview", SecurityConstants.VIEW);
		activityMap
				.put("/customerNotesAction-previous", SecurityConstants.VIEW);
		activityMap.put("/customerNotesAction-create", SecurityConstants.VIEW);
		activityMap.put("/customerNotesAction-search", SecurityConstants.VIEW);
	}

	private void addReportsMappings() {
		addRunReportsMappings();
		addAdministerReportsMappings();
	}

	private void addAdministerReportsMappings() {
		activityMap.put("/reportsAction-administerreports_path",
				SecurityConstants.ADMINISTER_REPORTS);
		activityMap.put("/reportsAction-administerreportslist_path",
				SecurityConstants.ADMINISTER_REPORTS);
		activityMap.put("/reportsParamsAction-load",
				SecurityConstants.ADMINISTER_REPORTS);
		activityMap.put("/reportsParamsAction-loadList",
				SecurityConstants.ADMINISTER_REPORTS);

		activityMap.put("/reportsParamsAction-createParams",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsParamsAction-deleteParams",
				SecurityConstants.ADMINISTER_REPORTPARAMS);

		activityMap.put("/reportsParamsAction-reportparams_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsParamsAction-reportparamsadd_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsParamsAction-reportparamslist_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsParamsAction-loadView",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsParamsAction-reportparamsview_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);

		activityMap.put("/reportsDataSourceAction-load",
				SecurityConstants.ADMINISTER_REPORTDS);
		activityMap.put("/reportsDataSourceAction-loadList",
				SecurityConstants.ADMINISTER_REPORTDS);
		activityMap.put("/reportsDataSourceAction-createDataSource",
				SecurityConstants.ADMINISTER_REPORTDS);
		activityMap.put("/reportsDataSourceAction-deleteDataSource",
				SecurityConstants.ADMINISTER_REPORTDS);
		activityMap.put("/reportsDataSourceAction-loadView",
				SecurityConstants.ADMINISTER_REPORTDS);

		activityMap.put("/reportsDataSourceAction-reportdatasource_path",
				SecurityConstants.ADMINISTER_REPORTDS);
		activityMap.put("/reportsDataSourceAction-reportdatasourceadd_path",
				SecurityConstants.ADMINISTER_REPORTDS);
		activityMap.put("/reportsDataSourceAction-reportdatasourcelist_path",
				SecurityConstants.ADMINISTER_REPORTDS);
		activityMap.put("/reportsDataSourceAction-reportdatasourceview_path",
				SecurityConstants.ADMINISTER_REPORTDS);

		activityMap.put("/reportsParamsMapAction-loadAddList",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsParamsMapAction-createParamsMap",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsParamsMapAction-deleteParamsMap",
				SecurityConstants.ADMINISTER_REPORTPARAMS);

		activityMap.put("/reportsParamsMapAction-reportparamsmapaddlist_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsParamsMapAction-reportparamsmap_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsUploadAction-uploadReport",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsUploadAction-administerreports_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);

		activityMap.put("/reportsUserParamsAction-reportuserparamslist_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsUserParamsAction-loadAddList",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsUserParamsAction-processReport",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsUserParamsAction-reportsuserprocess_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);

		activityMap.put("/reportsUserParamsAction-reportsuserprocess_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
		activityMap.put("/reportsUserParamsAction-reportsuserprocess_path",
				SecurityConstants.ADMINISTER_REPORTPARAMS);
	}

	private void addNotesMappings() {
		activityMap.put("/notesAction-load", SecurityConstants.VIEW);
		activityMap.put("/notesAction-preview", SecurityConstants.VIEW);
		activityMap.put("/notesAction-previous", SecurityConstants.VIEW);
		activityMap.put("/notesAction-search", SecurityConstants.VIEW);
		activityMap.put("/notesAction-create", SecurityConstants.VIEW);
		
		activityMap.put("/multipleloansaction-load",
				SecurityConstants.CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS);
		activityMap.put("/multipleloansaction-getLoanOfficers",
				SecurityConstants.VIEW);
		activityMap.put("/multipleloansaction-getCenters",
				SecurityConstants.VIEW);
		activityMap.put("/multipleloansaction-getPrdOfferings",
				SecurityConstants.VIEW);
		activityMap.put("/multipleloansaction-get", SecurityConstants.VIEW);
		activityMap.put("/multipleloansaction-create", SecurityConstants.VIEW);
		
		activityMap.put("/reverseloandisbaction-search",
				SecurityConstants.CAN_REVERSE_LOAN_DISBURSAL);
		activityMap.put("/reverseloandisbaction-load",
				SecurityConstants.VIEW);
		activityMap.put("/reverseloandisbaction-preview",
				SecurityConstants.VIEW);
		activityMap.put("/reverseloandisbaction-update",
				SecurityConstants.VIEW);
		activityMap.put("/reverseloandisbaction-cancel", SecurityConstants.VIEW);
		activityMap.put("/reverseloandisbaction-validate", SecurityConstants.VIEW);
	}
	
	private void addLabelConfigurationMappings() {
		activityMap.put("/labelconfigurationaction-load",
				SecurityConstants.CAN_DEFINE_LABELS);
		activityMap.put("/labelconfigurationaction-update",
				SecurityConstants.VIEW);
		activityMap.put("/labelconfigurationaction-cancel",
				SecurityConstants.VIEW);
		activityMap.put("/labelconfigurationaction-validate",
				SecurityConstants.VIEW);
	}
	
	private void addHiddenMandatoryConfigurationMappings() {
		activityMap.put("/hiddenmandatoryconfigurationaction-load",
				SecurityConstants.CAN_DEFINE_HIDDEN_MANDATORY_FIELDS);
		activityMap.put("/hiddenmandatoryconfigurationaction-update",
				SecurityConstants.VIEW);
		activityMap.put("/hiddenmandatoryconfigurationaction-cancel",
				SecurityConstants.VIEW);
		activityMap.put("/hiddenmandatoryconfigurationaction-validate",
				SecurityConstants.VIEW);
	}
	
	private void addHolidayMappings() {
		activityMap.put("/holidayAction-load", SecurityConstants.VIEW);
		activityMap.put("/holidayAction-get", SecurityConstants.VIEW);		
		activityMap.put("/holidayAction-preview", SecurityConstants.VIEW);
		activityMap.put("/holidayAction-getHolidays", SecurityConstants.VIEW);
		activityMap.put("/holidayAction-addHoliday", SecurityConstants.VIEW);
		activityMap.put("/holidayAction-previous", SecurityConstants.VIEW);
		activityMap.put("/holidayAction-update", SecurityConstants.VIEW);		
	}
	
	private void addSurveysMappings() {
		activityMap.put("/surveysAction-mainpage", SecurityConstants.VIEW);
		activityMap.put("/surveysAction-get", SecurityConstants.VIEW);
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
			
		case CustomerConstants.CENTER_ACTIVE_STATE:
			activityId = CENTER_CHANGE_STATUS;
			break;
		case CustomerConstants.CENTER_INACTIVE_STATE:
			activityId = CENTER_CHANGE_STATUS;
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

	public boolean isAddingNotesPermittedForAccounts(AccountTypes accountTypes,
			CustomerLevel customerLevel, UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(getActivityIdForAddingNotes(accountTypes,
						customerLevel), recordOfficeId, recordLoanOfficerId));
	}

	private short getActivityIdForAddingNotes(AccountTypes accountTypes,
			CustomerLevel customerLevel) {
		short activityId = -1;
		if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT))
			activityId = SecurityConstants.LOAN_CAN_ADD_NOTES_TO_LOAN;
		else if (accountTypes.equals(AccountTypes.SAVINGS_ACCOUNT))
			activityId = SecurityConstants.SAVINGS_CAN_ADD_NOTES_TO_SAVINGS;
		else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
			if (customerLevel.equals(CustomerLevel.CENTER))
				activityId = SecurityConstants.CENTER_ADD_NOTE_TO_CENTER;
			else if (customerLevel.equals(CustomerLevel.GROUP))
				activityId = SecurityConstants.GROUP_ADD_NOTE_TO_GROUP;
			else if (customerLevel.equals(CustomerLevel.CLIENT))
				activityId = SecurityConstants.CLIENT_ADD_NOTE_TO_CLIENT;
		}
		return activityId;
	}

	public boolean isAddingNotesPermittedForPersonnel(UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(SecurityConstants.PERSONNEL_NOTE_CREATE,
						recordOfficeId, recordLoanOfficerId));
	}

	public boolean isPaymentPermittedForAccounts(AccountTypes accountTypes,
			CustomerLevel customerLevel, UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(getActivityIdForPayment(accountTypes,
						customerLevel), recordOfficeId, recordLoanOfficerId));
	}

	private short getActivityIdForApplyCharges(AccountTypes accountTypes,
			CustomerLevel customerLevel) {
		short activityId = -1;
		if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT))
			activityId = SecurityConstants.LOAN_CAN_APPLY_CHARGES;
		else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
			if (customerLevel.equals(CustomerLevel.CENTER))
				activityId = SecurityConstants.CENTER_CAN_APPLY_CHARGES;
			else if (customerLevel.equals(CustomerLevel.GROUP))
				activityId = SecurityConstants.GROUP_CAN_APPLY_CHARGES;
			else if (customerLevel.equals(CustomerLevel.CLIENT))
				activityId = SecurityConstants.CLIENT_CAN_APPLY_CHARGES;
		}
		return activityId;
	}

	public boolean isAdjustmentPermittedForAccounts(AccountTypes accountTypes,
			CustomerLevel customerLevel, UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(getActivityIdForAdjustment(accountTypes,
						customerLevel), recordOfficeId, recordLoanOfficerId));
	}

	private short getActivityIdForAdjustment(AccountTypes accountTypes,
			CustomerLevel customerLevel) {
		short activityId = -1;
		if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT))
			activityId = SecurityConstants.LOAN_MAKE_ADJUSTMENT_ENTRY_TO_ACCOUNT;
		else if (accountTypes.equals(AccountTypes.SAVINGS_ACCOUNT))
			activityId = SecurityConstants.SAVINGS_APPLY_ADJUSTMENT;
		else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
			if (customerLevel.equals(CustomerLevel.CENTER))
				activityId = SecurityConstants.CENTER_MAKE_ADJUSTMENT_ENTRIES_TO_CENTER_ACCOUNT;
			else if (customerLevel.equals(CustomerLevel.GROUP))
				activityId = SecurityConstants.GROUP_MAKE_ADJUSTMENT_ENTRIES_TO_GROUP_ACCOUNT;
			else if (customerLevel.equals(CustomerLevel.CLIENT))
				activityId = SecurityConstants.CIENT_MAKE_ADJUSTMENT_ENTRIES_TO_CLIENT_ACCOUNT;
		}
		return activityId;
	}

	public boolean isAddingHistoricaldataPermittedForCustomers(
			CustomerLevel customerLevel, UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(
						getActivityIdForAddingHistoricaldata(customerLevel),
						recordOfficeId, recordLoanOfficerId));
	}

	private short getActivityIdForAddingHistoricaldata(
			CustomerLevel customerLevel) {
		short activityId = -1;
		if (customerLevel.equals(CustomerLevel.GROUP))
			activityId = SecurityConstants.GROUP_ADD_EDIT_HISTORICAL_DATA;
		else if (customerLevel.equals(CustomerLevel.CLIENT))
			activityId = SecurityConstants.CIENT_ADD_EDIT_HISTORICAL_DATA;
		return activityId;
	}

	public boolean isWaiveDuePermittedForCustomers(WaiveEnum waiveEnum,
			AccountTypes accountTypes, CustomerLevel customerLevel,
			UserContext userContext, Short recordOfficeId,
			Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(getActivityIdForWaiveDue(waiveEnum,
						accountTypes, customerLevel), recordOfficeId,
						recordLoanOfficerId));
	}

	private short getActivityIdForWaiveDue(WaiveEnum waiveEnum,
			AccountTypes accountTypes, CustomerLevel customerLevel) {
		short activityId = -1;
		if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT)) {
			if (waiveEnum.equals(WaiveEnum.FEES))
				activityId = SecurityConstants.LOAN_WAIVE_FEE_INSTALLMENT;
			else if (waiveEnum.equals(WaiveEnum.PENALTY))
				activityId = SecurityConstants.LOAN_WAIVE_PANELTY;
		} else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
			if (customerLevel.equals(CustomerLevel.CENTER))
				activityId = SecurityConstants.CENTER_WAIVE_DUE_AMOUNT;
			else if (customerLevel.equals(CustomerLevel.GROUP))
				activityId = SecurityConstants.GROUP_WAIVE_DUE_AMOUNT;
			else if (customerLevel.equals(CustomerLevel.CLIENT))
				activityId = SecurityConstants.CIENT_WAIVE_DUE_AMOUNT;
		}
		return activityId;
	}

	public boolean isRemoveFeesPermittedForAccounts(AccountTypes accountTypes,
			CustomerLevel customerLevel, UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(getActivityIdForRemoveFees(accountTypes,
						customerLevel), recordOfficeId, recordLoanOfficerId));
	}

	private short getActivityIdForRemoveFees(AccountTypes accountTypes,
			CustomerLevel customerLevel) {
		short activityId = -1;
		if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT))
			activityId = SecurityConstants.LOAN_REMOVE_FEE_TYPE_ATTACHED_TO_ACCOUNT;
		else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
			if (customerLevel.equals(CustomerLevel.CENTER))
				activityId = SecurityConstants.CENTER_REMOVE_FEE_TYPE_FROM_CENTER_ACCOUNT;
			else if (customerLevel.equals(CustomerLevel.GROUP))
				activityId = SecurityConstants.GROUP_REMOVE_FEE_TYPE_FROM_GROUP_ACCOUNT;
			else if (customerLevel.equals(CustomerLevel.CLIENT))
				activityId = SecurityConstants.CIENT_REMOVE_FEE_TYPE_FROM_CLIENT_ACCOUNT;
		}
		return activityId;
	}

	public boolean isApplyChargesPermittedForAccounts(AccountTypes accountTypes,
			CustomerLevel customerLevel, UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(getActivityIdForApplyCharges(accountTypes,
						customerLevel), recordOfficeId, recordLoanOfficerId));
	}

	private short getActivityIdForPayment(AccountTypes accountTypes,
			CustomerLevel customerLevel) {
		short activityId = -1;
		if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT))
			activityId = SecurityConstants.LOAN_MAKE_PAYMENT_TO_ACCOUNT;
		else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
			if (customerLevel.equals(CustomerLevel.CENTER))
				activityId = SecurityConstants.CENTER_MAKE_PAYMENTS_TO_CENTER_ACCOUNT;
			else if (customerLevel.equals(CustomerLevel.GROUP))
				activityId = SecurityConstants.GROUP_MAKE_PAYMENT_TO_GROUP_ACCOUNT;
			else if (customerLevel.equals(CustomerLevel.CLIENT))
				activityId = SecurityConstants.CIENT_MAKE_PAYMENT_TO_CLIENT_ACCOUNT;
		}
		return activityId;
	}
	
	
	public boolean isEditMeetingSchedulePermittedForCustomers(
			CustomerLevel customerLevel, UserContext userContext,
			Short recordOfficeId, Short recordLoanOfficerId) {
		return AuthorizationManager.getInstance().isActivityAllowed(
				userContext,
				new ActivityContext(
						getActivityIdForEditMeetingSchedule(customerLevel),
						recordOfficeId, recordLoanOfficerId));
	}

	private short getActivityIdForEditMeetingSchedule(CustomerLevel customerLevel) {
		short activityId = -1;
		if (customerLevel.equals(CustomerLevel.CENTER))
			activityId = SecurityConstants.MEETING_UPDATE_CENTER_MEETING;
		else if (customerLevel.equals(CustomerLevel.GROUP))
			activityId = SecurityConstants.MEETING_UPDATE_GROUP_MEETING;
		else if (customerLevel.equals(CustomerLevel.CLIENT))
			activityId = SecurityConstants.MEETING_UPDATE_CLIENT_MEETING;
		return activityId;
	}
}
