/**
 *
 */
package org.mifos.framework.security.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.accounts.loan.struts.action.AccountStatusAction;
import org.mifos.application.accounts.loan.struts.action.LoanAccountAction;
import org.mifos.application.accounts.loan.struts.action.LoanDisbursmentAction;
import org.mifos.application.accounts.loan.struts.action.MultipleLoanAccountsCreationAction;
import org.mifos.application.accounts.loan.struts.action.RepayLoanAction;
import org.mifos.application.accounts.loan.struts.action.ReverseLoanDisbursalAction;
import org.mifos.application.accounts.savings.struts.action.SavingsAction;
import org.mifos.application.accounts.savings.struts.action.SavingsApplyAdjustmentAction;
import org.mifos.application.accounts.savings.struts.action.SavingsClosureAction;
import org.mifos.application.accounts.savings.struts.action.SavingsDepositWithdrawalAction;
import org.mifos.application.accounts.struts.action.AccountAppAction;
import org.mifos.application.accounts.struts.action.AccountApplyPaymentAction;
import org.mifos.application.accounts.struts.action.ApplyAdjustment;
import org.mifos.application.accounts.struts.action.ApplyChargeAction;
import org.mifos.application.accounts.struts.action.EditStatusAction;
import org.mifos.application.accounts.struts.action.NotesAction;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.admin.struts.action.AdminAction;
import org.mifos.application.bulkentry.struts.action.BulkEntryAction;
import org.mifos.application.checklist.struts.action.ChkListAction;
import org.mifos.application.configuration.struts.action.CustomFieldsAction;
import org.mifos.application.configuration.struts.action.HiddenMandatoryConfigurationAction;
import org.mifos.application.configuration.struts.action.LabelConfigurationAction;
import org.mifos.application.configuration.struts.action.LookupOptionsAction;
import org.mifos.application.customer.center.struts.action.CenterCustAction;
import org.mifos.application.customer.client.struts.action.ClientCustAction;
import org.mifos.application.customer.client.struts.action.ClientTransferAction;
import org.mifos.application.customer.group.struts.action.AddGroupMembershipAction;
import org.mifos.application.customer.group.struts.action.GroupCustAction;
import org.mifos.application.customer.group.struts.action.GroupTransferAction;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.struts.action.CustAction;
import org.mifos.application.customer.struts.action.CustHistoricalDataAction;
import org.mifos.application.customer.struts.action.CustSearchAction;
import org.mifos.application.customer.struts.action.CustomerAccountAction;
import org.mifos.application.customer.struts.action.CustomerAction;
import org.mifos.application.customer.struts.action.CustomerApplyAdjustmentAction;
import org.mifos.application.customer.struts.action.CustomerNotesAction;
import org.mifos.application.customer.struts.action.EditCustomerStatusAction;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.fees.struts.action.FeeAction;
import org.mifos.application.fund.struts.action.FundAction;
import org.mifos.application.holiday.struts.action.HolidayAction;
import org.mifos.application.login.struts.action.LoginAction;
import org.mifos.application.meeting.struts.action.MeetingAction;
import org.mifos.application.personnel.struts.action.PersonAction;
import org.mifos.application.personnel.struts.action.PersonnelNoteAction;
import org.mifos.application.personnel.struts.action.PersonnelSettingsAction;
import org.mifos.application.productdefinition.struts.action.LoanPrdAction;
import org.mifos.application.productdefinition.struts.action.PrdCategoryAction;
import org.mifos.application.productdefinition.struts.action.PrdConfAction;
import org.mifos.application.productdefinition.struts.action.SavingsPrdAction;
import org.mifos.application.productsmix.struts.action.ProductMixAction;
import org.mifos.application.reports.struts.action.BirtReportsUploadAction;
import org.mifos.application.reports.struts.action.ReportsAction;
import org.mifos.application.reports.struts.action.ReportsDataSourceAction;
import org.mifos.application.reports.struts.action.ReportsParamsAction;
import org.mifos.application.reports.struts.action.ReportsParamsMapAction;
import org.mifos.application.reports.struts.action.ReportsUploadAction;
import org.mifos.application.reports.struts.action.ReportsUserParamsAction;
import org.mifos.application.rolesandpermission.struts.action.RolesPermissionsAction;
import org.mifos.application.surveys.struts.action.QuestionsAction;
import org.mifos.application.surveys.struts.action.SurveyInstanceAction;
import org.mifos.application.surveys.struts.action.SurveysAction;
import org.mifos.application.ppi.struts.action.PPIAction;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.application.acceptedpaymenttype.struts.action.AcceptedPaymentTypeAction;



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
		// these lines don't seem to refer to a real action
		// if so, these methods can be removed safely
		addApplyChargesMappings2();
		addApplyPaymentMappings();
		addLoanStatusActionMappings();
		addLoanMappings();
		addSearchBeforeLoanMappings();
		addChecklistMappings();
		addFeeMappings();
		addSavingProductMappings();
		addManageRolesPermissionsMappings();
		addOfficeMappings();
		addCustomerSearchMappings();
		
//		 new style security configuration
		parseActionSecurity(AdminAction.getSecurity());
		parseActionSecurity(CustSearchAction.getSecurity());
		parseActionSecurity(LoginAction.getSecurity());
		parseActionSecurity(CustHistoricalDataAction.getSecurity());
		parseActionSecurity(PersonAction.getSecurity());
		parseActionSecurity(CenterCustAction.getSecurity());
		parseActionSecurity(ClientTransferAction.getSecurity());
		parseActionSecurity(GroupTransferAction.getSecurity());
		parseActionSecurity(MeetingAction.getSecurity());
		parseActionSecurity(PrdCategoryAction.getSecurity());
		parseActionSecurity(SavingsPrdAction.getSecurity());
		parseActionSecurity(PrdConfAction.getSecurity());
		parseActionSecurity(LoanPrdAction.getSecurity());
		parseActionSecurity(FeeAction.getSecurity());
		parseActionSecurity(ChkListAction.getSecurity());
		parseActionSecurity(EditStatusAction.getSecurity());
		parseActionSecurity(CustAction.getSecurity());
		parseActionSecurity(FundAction.getSecurity());
		parseActionSecurity(BulkEntryAction.getSecurity());
		parseActionSecurity(AccountAppAction.getSecurity());
		parseActionSecurity(SavingsAction.getSecurity());
		parseActionSecurity(SavingsClosureAction.getSecurity());
		parseActionSecurity(SavingsApplyAdjustmentAction.getSecurity());
		parseActionSecurity(ApplyAdjustment.getSecurity());
		parseActionSecurity(CustomerApplyAdjustmentAction.getSecurity());
		parseActionSecurity(RepayLoanAction.getSecurity());
		parseActionSecurity(CustomerAction.getSecurity());
		parseActionSecurity(LoanAccountAction.getSecurity());
		parseActionSecurity(AccountApplyPaymentAction.getSecurity());
		parseActionSecurity(LoanDisbursmentAction.getSecurity());
		parseActionSecurity(SavingsDepositWithdrawalAction.getSecurity());
		parseActionSecurity(EditCustomerStatusAction.getSecurity());
		parseActionSecurity(ApplyChargeAction.getSecurity());
		parseActionSecurity(ClientCustAction.getSecurity());
		parseActionSecurity(AccountStatusAction.getSecurity());
		parseActionSecurity(GroupCustAction.getSecurity());
		parseActionSecurity(PersonnelSettingsAction.getSecurity());
		parseActionSecurity(CustomerAccountAction.getSecurity());
		parseActionSecurity(RolesPermissionsAction.getSecurity());
		parseActionSecurity(PersonnelNoteAction.getSecurity());
		parseActionSecurity(CustomerNotesAction.getSecurity());
		parseActionSecurity(NotesAction.getSecurity());
		parseActionSecurity(MultipleLoanAccountsCreationAction.getSecurity());
		parseActionSecurity(ReverseLoanDisbursalAction.getSecurity());
		parseActionSecurity(ReportsAction.getSecurity());
		parseActionSecurity(ReportsDataSourceAction.getSecurity());
		parseActionSecurity(ReportsParamsAction.getSecurity());
		parseActionSecurity(ReportsParamsMapAction.getSecurity());
		parseActionSecurity(ReportsUploadAction.getSecurity());
		parseActionSecurity(ReportsUserParamsAction.getSecurity());
		parseActionSecurity(LabelConfigurationAction.getSecurity());
		parseActionSecurity(HolidayAction.getSecurity());
		parseActionSecurity(SurveysAction.getSecurity());
		parseActionSecurity(QuestionsAction.getSecurity());
		parseActionSecurity(HiddenMandatoryConfigurationAction.getSecurity());
		parseActionSecurity(AddGroupMembershipAction.getSecurity());
		parseActionSecurity(ProductMixAction.getSecurity());
		parseActionSecurity(SurveyInstanceAction.getSecurity());
		parseActionSecurity(BirtReportsUploadAction.getSecurity());
		parseActionSecurity(LookupOptionsAction.getSecurity());
		parseActionSecurity(CustomFieldsAction.getSecurity());
		parseActionSecurity(PPIAction.getSecurity());
		parseActionSecurity(AcceptedPaymentTypeAction.getSecurity());
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
	}

	private void addChecklistMappings() {

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

		private void addLoanStatusActionMappings() {
		// mapping for account status::TO BE REMOVED
		activityMap.put("/LoanStatusAction-load", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-preview", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-previous", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-update", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-search", SecurityConstants.VIEW);
		activityMap.put("/LoanStatusAction-writeOff", SecurityConstants.VIEW);
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

	private static ActivityMapper instance = new ActivityMapper();

	public static ActivityMapper getInstance() {
		return instance;
	}

	private Map<String, Short> activityMap = new HashMap<String, Short>();

	private List<ActionSecurity> allSecurity = new ArrayList<ActionSecurity>();

	public Short getActivityId(String key) {
		return activityMap.get(key);
	}
	
	public List<ActionSecurity> getAllSecurity() {
		return Collections.unmodifiableList(allSecurity);
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
 
	private void parseActionSecurity(ActionSecurity security) {
		for (String method : security.methods()) {
			String fullKey = "/" + security.getActionName() + "-" + method;
			activityMap.put(fullKey, security.get(method));
		}
		allSecurity.add(security);
	}

}
