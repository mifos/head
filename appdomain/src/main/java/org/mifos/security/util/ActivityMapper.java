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

package org.mifos.security.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.WaiveEnum;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.reports.business.ReportsBO;
import org.mifos.reports.persistence.ReportsPersistence;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;

/**
 * Singleton.
 */
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

    private static ActivityMapper instance = new ActivityMapper();

    private LegacyRolesPermissionsDao legacyRolesPermissionsDao = ApplicationContextProvider.getBean(LegacyRolesPermissionsDao.class);

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

    // for testing
    public void setLegacyRolesPermissionDao(LegacyRolesPermissionsDao legacyRolesPermissionsDao) {
        this.legacyRolesPermissionsDao = legacyRolesPermissionsDao;
    }

    public void init() {
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
        addOfficeMappings();
        addCustomerSearchMappings();

        // new style security configuration
        parseActionSecurity(getAdminActionSecurity());
        parseActionSecurity(getCustSearchSecurity());
        parseActionSecurity(getLoginSecurity());
        parseActionSecurity(getCustHistoricalSecurity());
        parseActionSecurity(getPersonSecurity());
        parseActionSecurity(getCenterCustSecurity());
        parseActionSecurity(getClientTransferSecurity());
        parseActionSecurity(getGroupTransferSecurity());
        parseActionSecurity(getMeetingSecurity());
        parseActionSecurity(getLoanPrdSecurity());
        parseActionSecurity(getFeeSecurity());
        parseActionSecurity(getChkListSecurity());
        parseActionSecurity(getEditStatusSecurity());
        parseActionSecurity(getCustSecurity());
        parseActionSecurity(getCollectionSheetEntrySecurity());
        parseActionSecurity(getAccountAppSecurity());
        parseActionSecurity(getSavingsSecurity());
        parseActionSecurity(getSavingsClosureSecurity());
        parseActionSecurity(getSavingsApplyAdjustmentSecurity());
        parseActionSecurity(getApplyAdjustmentSecurity());
        parseActionSecurity(getCustomerApplyAdjustmentSecurity());
        parseActionSecurity(getRepayLoanSecurity());
        parseActionSecurity(getCustomerSecurity());
        parseActionSecurity(getLoanAccountSecurity());
        parseActionSecurity(getGroupLoanAccountSecurity());
        parseActionSecurity(getGroupAccountApplyPaymentSecurity());
        parseActionSecurity(getGroupIndividualLoanAccountSecurity());
        parseActionSecurity(getAccountApplyPaymentSecurity());
        parseActionSecurity(getLoanDisbursementSecurity());
        parseActionSecurity(getSavingsDepositWithdrawalSecurity());
        parseActionSecurity(getEditCustomerStatusSecurity());
        parseActionSecurity(getApplyChargeSecurity());
        parseActionSecurity(getClientCustSecurity());
        parseActionSecurity(getAccountStatusSecurity());
        parseActionSecurity(getGroupCustSecurity());
        parseActionSecurity(getPersonnelSettingsSecurity());
        parseActionSecurity(getCustomerAccountSecurity());
        parseActionSecurity(getRolesPermissionSecurity());
        parseActionSecurity(getPersonnelNoteSecurity());
        parseActionSecurity(getCustomerNotesSecurity());
        parseActionSecurity(getNotesSecurity());
        parseActionSecurity(getMultipleLoanAccountsCreationSecurity());
        parseActionSecurity(getReverseLoanDisbursalSecurity());
        parseActionSecurity(getGeneralLedgerActionSecurity()); 
        parseActionSecurity(getJournalVoucherActionSecurity()); 
        parseActionSecurity(getOpenBalanceActionSecurity()); 
        parseActionSecurity(getViewGlTransactionsActionSecurity());
        parseActionSecurity(getProcessAccountingTransactionsActionSecurity()); 
        parseActionSecurity(getYearEndProcessActionSecurity()); 
        parseActionSecurity(getReportsSecurity());
        parseActionSecurity(getReportsDataSourceSecurity());
        parseActionSecurity(getReportsParamsSecurity());
        parseActionSecurity(getReportsParamsMapSecurity());
        parseActionSecurity(getReportsUploadSecurity());
        parseActionSecurity(getReportsUserParamsSecurity());
        parseActionSecurity(getHolidaySecurity());
        parseActionSecurity(getSurveysSecurity());
        parseActionSecurity(getQuestionsSecurity());
        parseActionSecurity(getAddGroupMembershipSecurity());
        parseActionSecurity(getSurveyInstanceSecurity());
        parseActionSecurity(getBirtReportsUploadSecurity());
        parseActionSecurity(getLookupOptionsSecurity());
        parseActionSecurity(getPPISecurity());
        parseActionSecurity(getReportsCategorySecurity());
        parseActionSecurity(getBirtAdminDocumentUploadSecurity());
        parseActionSecurity(getImportTransactionsSecurity());
        parseActionSecurity(getFinancialAccountingSecurity());
        parseActionSecurity(getAccountGroupIndividualPaymentSecurity());
        parseActionSecurity(getMigrateSecurity());
    }

    private ActionSecurity getMigrateSecurity() {
        ActionSecurity security = new ActionSecurity("migrateAction");
        security.allow("get", SecurityConstants.VIEW);
        security.allow("migrateSurveys", SecurityConstants.VIEW);
        security.allow("migrateAdditionalFields", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getImportTransactionsSecurity() {
        final ActionSecurity security = new ActionSecurity("manageImportAction");
        security.allow("load", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        security.allow("upload", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        security.allow("confirm", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        security.allow("downloadLog", SecurityConstants.CAN_IMPORT_TRANSACTIONS);
        return security;
    }

    private ActionSecurity getBirtAdminDocumentUploadSecurity() {
        ActionSecurity security = new ActionSecurity("birtAdminDocumentUploadAction");
        security.allow("getBirtAdminDocumentUploadPage", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("preview", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("loadProductInstance", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);

        security.allow("getProductTypes", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("previous", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("upload", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("getViewBirtAdminDocumentPage", SecurityConstants.CAN_VIEW_ADMIN_DOCUMENTS);
        security.allow("edit", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("editpreview", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("editprevious", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("editThenUpload", SecurityConstants.CAN_UPLOAD_ADMIN_DOCUMENTS);
        security.allow("downloadAdminDocument", SecurityConstants.DOWNLOAD_REPORT_TEMPLATE);
        security.allow("getFileNotFoundPage", SecurityConstants.DOWNLOAD_REPORT_TEMPLATE);
        return security;
    }

    private ActionSecurity getReportsCategorySecurity() {
        ActionSecurity security = new ActionSecurity("reportsCategoryAction");
        security.allow("loadDefineNewCategoryPage", SecurityConstants.DEFINE_REPORT_CATEGORY);
        security.allow("preview", SecurityConstants.DEFINE_REPORT_CATEGORY);
        security.allow("addNewCategory", SecurityConstants.DEFINE_REPORT_CATEGORY);
        security.allow("viewReportsCategory", SecurityConstants.VIEW_REPORT_CATEGORY);
        security.allow("confirmDeleteReportsCategory", SecurityConstants.DELETE_REPORT_CATEGORY);
        security.allow("edit", SecurityConstants.VIEW_REPORT_CATEGORY);
        security.allow("editPreview", SecurityConstants.VIEW_REPORT_CATEGORY);
        security.allow("deleteReportsCategory", SecurityConstants.DELETE_REPORT_CATEGORY);
        security.allow("editThenSubmit", SecurityConstants.VIEW_REPORT_CATEGORY);
        return security;
    }

    private ActionSecurity getPPISecurity() {
        ActionSecurity security = new ActionSecurity("ppiAction");
        security.allow("configure", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getLookupOptionsSecurity() {
        ActionSecurity security = new ActionSecurity("lookupOptionsAction");

        security.allow("load", SecurityConstants.CAN_DEFINE_LOOKUP_OPTIONS);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("addEditLookupOption", SecurityConstants.VIEW);
        security.allow("addEditLookupOption_cancel", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getBirtReportsUploadSecurity() {
        ActionSecurity security = new ActionSecurity("birtReportsUploadAction");
        security.allow("getBirtReportsUploadPage", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
        security.allow("preview", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
        security.allow("previous", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
        security.allow("upload", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
        security.allow("getViewReportPage", SecurityConstants.UPLOAD_REPORT_TEMPLATE);
        security.allow("edit", SecurityConstants.EDIT_REPORT_INFORMATION);
        security.allow("editpreview", SecurityConstants.EDIT_REPORT_INFORMATION);
        security.allow("editprevious", SecurityConstants.EDIT_REPORT_INFORMATION);
        security.allow("editThenUpload", SecurityConstants.EDIT_REPORT_INFORMATION);
        security.allow("downloadBirtReport", SecurityConstants.DOWNLOAD_REPORT_TEMPLATE);
        return security;
    }

    private ActionSecurity getSurveyInstanceSecurity() {
        ActionSecurity security = new ActionSecurity("surveyInstanceAction");
        security.allow("create_entry", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("choosesurvey", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("edit", SecurityConstants.VIEW);
        security.allow("delete", SecurityConstants.VIEW);
        security.allow("clear", SecurityConstants.VIEW);
        security.allow("back", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getAddGroupMembershipSecurity() {
        ActionSecurity security = new ActionSecurity("addGroupMembershipAction");
        security.allow("loadSearch", SecurityConstants.CAN_ADD_CLIENTS_TO_GROUPS);
        security.allow("previewParentAddClient", SecurityConstants.CAN_ADD_CLIENTS_TO_GROUPS);
        security.allow("updateParent", SecurityConstants.CAN_ADD_CLIENTS_TO_GROUPS);

        return security;
    }

    private ActionSecurity getQuestionsSecurity() {
        ActionSecurity security = new ActionSecurity("questionsAction");
        security.allow("viewQuestions", SecurityConstants.VIEW);
        security.allow("defineQuestions", SecurityConstants.VIEW);
        security.allow("addChoice", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("deleteChoice", SecurityConstants.VIEW);
        security.allow("deleteNewQuestion", SecurityConstants.VIEW);
        security.allow("addQuestion", SecurityConstants.VIEW);
        security.allow("createQuestions", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("edit_entry", SecurityConstants.VIEW);
        security.allow("update_entry", SecurityConstants.VIEW);
        security.allow("preview_entry", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getSurveysSecurity() {
        ActionSecurity security = new ActionSecurity("surveysAction");
        security.allow("mainpage", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("create_entry", SecurityConstants.VIEW);
        security.allow("add_new_question", SecurityConstants.VIEW);
        security.allow("delete_new_question", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("edit_entry", SecurityConstants.VIEW);
        security.allow("preview_update", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("edit", SecurityConstants.VIEW);
        security.allow("edit_update", SecurityConstants.VIEW);
        security.allow("printVersion", SecurityConstants.VIEW);
        security.allow("prePrintVersion", SecurityConstants.VIEW);
        security.allow("delete_new_question_edit", SecurityConstants.VIEW);
        security.allow("add_new_question_edit", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getHolidaySecurity() {
        ActionSecurity security = new ActionSecurity("holidayAction");
        security.allow("load", SecurityConstants.CAN_DEFINE_HOLIDAY);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("getHolidays", SecurityConstants.VIEW);
        security.allow("addHoliday", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("officeHierarchy", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.CAN_DEFINE_HOLIDAY);
        return security;
    }

    private ActionSecurity getReportsUserParamsSecurity() {
        ReportActionSecurity security = new ReportActionSecurity("reportsUserParamsAction", "loadAddList");

        // FIXME: no associated activity exists for this constant
        security.allow("reportuserparamslist_path", SecurityConstants.ADMINISTER_REPORTPARAMS);

        for (ReportsBO report : new ReportsPersistence().getAllReports()) {
            security.allowReport(report.getReportId().intValue(), report.getActivityId());
        }

        // FIXME: no associated activity exists for this constant
        security.allow("loadAddList", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("processReport", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("reportsuserprocess_path", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("loadAdminReport", SecurityConstants.CAN_VIEW_ADMIN_DOCUMENTS);

        return security;
    }

    private ActionSecurity getReportsUploadSecurity() {
        ActionSecurity security = new ActionSecurity("reportsUploadAction");
        security.allow("uploadReport", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("administerreports_path", SecurityConstants.ADMINISTER_REPORTPARAMS);
        return security;
    }

    private ActionSecurity getReportsParamsMapSecurity() {
        ActionSecurity security = new ActionSecurity("reportsParamsMap");
        security.allow("loadAddList", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("createParamsMap", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("deleteParamsMap", SecurityConstants.ADMINISTER_REPORTPARAMS);

        security.allow("reportparamsmapaddlist_path", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("reportparamsmap_path", SecurityConstants.ADMINISTER_REPORTPARAMS);

        return security;
    }

    private ActionSecurity getReportsParamsSecurity() {
        ActionSecurity security = new ActionSecurity("reportsParamsAction");
        security.allow("load", SecurityConstants.ADMINISTER_REPORTS);
        security.allow("loadList", SecurityConstants.ADMINISTER_REPORTS);

        security.allow("createParams", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("deleteParams", SecurityConstants.ADMINISTER_REPORTPARAMS);

        security.allow("reportparams_path", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("reportparamsadd_path", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("reportparamslist_path", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("loadView", SecurityConstants.ADMINISTER_REPORTPARAMS);
        security.allow("reportparamsview_path", SecurityConstants.ADMINISTER_REPORTPARAMS);

        return security;
    }

    private ActionSecurity getReportsDataSourceSecurity() {
        ActionSecurity security = new ActionSecurity("reportsDataSourceAction");
        security.allow("load", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("loadList", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("createDataSource", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("deleteDataSource", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("loadView", SecurityConstants.ADMINISTER_REPORTDS);

        security.allow("reportdatasource_path", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("reportdatasourceadd_path", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("reportdatasourcelist_path", SecurityConstants.ADMINISTER_REPORTDS);
        security.allow("reportdatasourceview_path", SecurityConstants.ADMINISTER_REPORTDS);

        return security;
    }

    private ActionSecurity getReportsSecurity() {
        ActionSecurity security = new ActionSecurity("reportsAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("report_designer", SecurityConstants.CLIENTSDETAILVIEW);
        security.allow("product_history", SecurityConstants.CLIENTSPRODUCTHISTORY);

        security.allow("branch_performance", SecurityConstants.BRANCHPERFORMANCE);
        security.allow("area_performance", SecurityConstants.AREAPERFORMANCE);
        security.allow("collection_sheet", SecurityConstants.COLLECTIONSHEET);
        security.allow("loan_distribution", SecurityConstants.LOANDISTRIBUTION);
        security.allow("branch_disbursement", SecurityConstants.BRANCHDISBURSEMENT);
        security.allow("staffwise_report", SecurityConstants.STAFFWISEREPORT);
        security.allow("branchwise_report", SecurityConstants.BRANCHWISEREPORT);
        security.allow("analysis", SecurityConstants.ANALYSIS);
        security.allow("kendra_meeting", SecurityConstants.KENDRA_MEETING);
        security.allow("administerreports_path", SecurityConstants.ADMINISTER_REPORTS);
        security.allow("administerreportslist_path", SecurityConstants.ADMINISTER_REPORTS);
        return security;
    }

    private ActionSecurity getReverseLoanDisbursalSecurity() {
        ActionSecurity security = new ActionSecurity("reverseloandisbaction");
        security.allow("search", SecurityConstants.CAN_REVERSE_LOAN_DISBURSAL);
        security.allow("load", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("validate", SecurityConstants.VIEW);
        return security;
    }
    
    private ActionSecurity getGeneralLedgerActionSecurity() {
        ActionSecurity security = new ActionSecurity("generalledgeraction");
        security.allow("load", SecurityConstants.ACCOUNTING_CREATE_GLTRANSACTION);
        security.allow("loadOffices", SecurityConstants.VIEW);
        security.allow("loadMainAccounts", SecurityConstants.VIEW);
        security.allow("loadAccountHeads", SecurityConstants.VIEW);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("submit", SecurityConstants.VIEW);
        return security;
    }
    
    private ActionSecurity getJournalVoucherActionSecurity() {
        ActionSecurity security = new ActionSecurity("journalvoucheraction");
        security.allow("load", SecurityConstants.ACCOUNTING_CREATE_JVTRANSACTION);
        security.allow("loadOffices", SecurityConstants.VIEW);
        security.allow("loadCreditAccount", SecurityConstants.VIEW);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("submit", SecurityConstants.VIEW);
        return security;
    }
    
    private ActionSecurity getOpenBalanceActionSecurity() {
        ActionSecurity security = new ActionSecurity("openbalanceaction");
        security.allow("load", SecurityConstants.ACCOUNTING_CREATE_OPENBALANCE);
        security.allow("loadOffices", SecurityConstants.VIEW);
        security.allow("loadOpenBalance", SecurityConstants.VIEW);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("submit", SecurityConstants.VIEW);
        return security;
    }
    
    private ActionSecurity getViewGlTransactionsActionSecurity() {
        ActionSecurity security = new ActionSecurity("viewgltransactionsaction");
        security.allow("load", SecurityConstants.ACCOUNTING_CREATE_VIEWTRANSACTIONS);
        security.allow("submit", SecurityConstants.VIEW);
        return security;
    }
    
    private ActionSecurity getProcessAccountingTransactionsActionSecurity() {
        ActionSecurity security = new ActionSecurity("processaccountingtransactionsaction");
        security.allow("load", SecurityConstants.ACCOUNTING_CREATE_MISPROCESSING);
        security.allow("process", SecurityConstants.VIEW);
        return security;
    }
    
    private ActionSecurity getYearEndProcessActionSecurity() {
        ActionSecurity security = new ActionSecurity("yearEndProcessAction");
        security.allow("load", SecurityConstants.VIEW);
        return security;
    }


    private ActionSecurity getMultipleLoanAccountsCreationSecurity() {
        ActionSecurity security = new ActionSecurity("multipleloansaction");
        security.allow("load", SecurityConstants.CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS);
        security.allow("getLoanOfficers", SecurityConstants.VIEW);
        security.allow("getCenters", SecurityConstants.VIEW);
        security.allow("getPrdOfferings", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getNotesSecurity() {
        ActionSecurity security = new ActionSecurity("notesAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getCustomerNotesSecurity() {
        ActionSecurity security = new ActionSecurity("customerNotesAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getPersonnelNoteSecurity() {
        ActionSecurity security = new ActionSecurity("personnelNoteAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getRolesPermissionSecurity() {
        ActionSecurity security = new ActionSecurity("rolesPermission");
        security.allow("viewRoles", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.ROLES_CREATE_ROLES);
        security.allow("create", SecurityConstants.ROLES_CREATE_ROLES);
        security.allow("manage", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.ROLES_EDIT_ROLES);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.ROLES_DELETE_ROLES);
        security.allow("delete", SecurityConstants.ROLES_DELETE_ROLES);
        return security;
    }

    private ActionSecurity getCustomerAccountSecurity() {
        ActionSecurity security = new ActionSecurity("customerAccountAction");
        security.allow("load", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getPersonnelSettingsSecurity() {
        ActionSecurity security = new ActionSecurity("yourSettings");
        security.allow("get", SecurityConstants.VIEW);
        security.allow("manage", SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
        security.allow("loadChangePassword", SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
        return security;
    }

    private ActionSecurity getGroupCustSecurity() {
        ActionSecurity security = new ActionSecurity("groupCustAction");
        security.allow("hierarchyCheck", SecurityConstants.VIEW);
        security.allow("chooseOffice", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.VIEW);
        security.allow("loadMeeting", SecurityConstants.MEETING_CREATE_GROUP_MEETING);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previewOnly", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);

        security.allow("getDetails", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("manage", SecurityConstants.GROUP_EDIT_GROUP);
        security.allow("previewManage", SecurityConstants.VIEW);
        security.allow("previousManage", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.GROUP_EDIT_GROUP);
        security.allow("loadSearch", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);

        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getAccountStatusSecurity() {
        ActionSecurity security = new ActionSecurity("ChangeAccountStatus");
        security.allow("load", SecurityConstants.CAN_APPROVE_LOANS_IN_BULK);
        security.allow("searchResults", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("getLoanOfficers", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getClientCustSecurity() {
        ActionSecurity security = new ActionSecurity("clientCustAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("chooseOffice", SecurityConstants.VIEW);
        security.allow("next", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previewPersonalInfo", SecurityConstants.VIEW);
        security.allow("retrievePictureOnPreview", SecurityConstants.VIEW);
        security.allow("prevPersonalInfo", SecurityConstants.VIEW);
        security.allow("prevMFIInfo", SecurityConstants.VIEW);
        security.allow("prevMeeting", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("loadMeeting", SecurityConstants.MEETING_CREATE_CLIENT_MEETING);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("editPersonalInfo", SecurityConstants.VIEW);
        security.allow("editFamilyInfo", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("editAddFamilyRow", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("editDeleteFamilyRow", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("previewEditFamilyInfo", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("previewEditPersonalInfo", SecurityConstants.VIEW);
        security.allow("prevEditPersonalInfo", SecurityConstants.VIEW);
        security.allow("updatePersonalInfo", SecurityConstants.VIEW);
        security.allow("editMfiInfo", SecurityConstants.CIENT_EDIT_MFI_INFORMATION);
        security.allow("previewEditMfiInfo", SecurityConstants.VIEW);
        security.allow("prevEditMfiInfo", SecurityConstants.VIEW);
        security.allow("updateMfiInfo", SecurityConstants.CIENT_EDIT_MFI_INFORMATION);
        security.allow("retrievePicture", SecurityConstants.VIEW);
        security.allow("showPicture", SecurityConstants.VIEW);
        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);
        security.allow("familyInfoNext", SecurityConstants.VIEW);
        security.allow("prevFamilyInfo", SecurityConstants.VIEW);
        security.allow("prevFamilyInfoNext", SecurityConstants.VIEW);
        security.allow("addFamilyRow", SecurityConstants.VIEW);
        security.allow("deleteFamilyRow", SecurityConstants.VIEW);
        security.allow("updateFamilyInfo", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("editPreviewEditFamilyInfo", SecurityConstants.CLIENT_UPDATE_PERSONNEL_INFO);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getApplyChargeSecurity() {
        ActionSecurity security = new ActionSecurity("applyChargeAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("divide", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getEditCustomerStatusSecurity() {
        ActionSecurity security = new ActionSecurity("editCustomerStatusAction");
        security.allow("loadStatus", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("previewStatus", SecurityConstants.VIEW);
        security.allow("previousStatus", SecurityConstants.VIEW);
        security.allow("updateStatus", SecurityConstants.VIEW);
        security.allow("cancelStatus", SecurityConstants.VIEW);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getSavingsDepositWithdrawalSecurity() {
        ActionSecurity security = new ActionSecurity("savingsDepositWithdrawalAction");
        security.allow("load", SecurityConstants.SAVINGS_CAN_MAKE_DEPOSIT_WITHDRAWAL);
        security.allow("reLoad", SecurityConstants.SAVINGS_CAN_MAKE_DEPOSIT_WITHDRAWAL);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("makePayment", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getLoanDisbursementSecurity() {
        ActionSecurity security = new ActionSecurity("loanDisbursementAction");
        security.allow("load", SecurityConstants.LOAN_CAN_DISBURSE_LOAN);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getAccountApplyPaymentSecurity() {
        ActionSecurity security = new ActionSecurity("applyPaymentAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("applyPayment", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getLoanAccountSecurity() {
        ActionSecurity security = new ActionSecurity("loanAccountAction");
        security.allow("getAllActivity", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("getLoanRepaymentSchedule", SecurityConstants.VIEW);
        security.allow("viewStatusHistory", SecurityConstants.VIEW);
        security.allow("manage", SecurityConstants.LOAN_UPDATE_LOAN);
        security.allow("managePreview", SecurityConstants.VIEW);
        security.allow("managePrevious", SecurityConstants.VIEW);
        security.allow("cancel", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.LOAN_UPDATE_LOAN);

        security.allow("getPrdOfferings", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.VIEW);
        security.allow("schedulePreview", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);

        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);
        security.allow("waiveChargeDue", SecurityConstants.VIEW);
        security.allow("forwardWaiveCharge", SecurityConstants.VIEW);
        security.allow("waiveChargeOverDue", SecurityConstants.VIEW);
        security.allow("redoLoanBegin", SecurityConstants.CAN_REDO_LOAN_DISPURSAL);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        security.allow("validateInstallments", SecurityConstants.VIEW);
        security.allow("showPreview", SecurityConstants.VIEW);
        security.allow("viewOriginalSchedule", SecurityConstants.VIEW);
        security.allow("viewAndEditAdditionalInformation", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getGroupLoanAccountSecurity() {
        ActionSecurity security = new ActionSecurity("groupLoanAccountAction");
        security.allow("get", SecurityConstants.VIEW);
        security.allow("getLoanRepaymentSchedule", SecurityConstants.VIEW);
        security.allow("getAllActivity", SecurityConstants.VIEW);
        return security;
    }
    
    private ActionSecurity getGroupAccountApplyPaymentSecurity() {
        ActionSecurity security = new ActionSecurity("applyGroupPaymentAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("divide", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("applyPayment", SecurityConstants.VIEW);
        return security;
    }
    
    private ActionSecurity getGroupIndividualLoanAccountSecurity() {
        ActionSecurity security = new ActionSecurity("groupIndividualLoanAccountAction");
        security.allow("get", SecurityConstants.VIEW);
        return security;
    }
    
    private ActionSecurity getCustomerSecurity() {
        ActionSecurity security = new ActionSecurity("customerAction");
        security.allow("forwardWaiveChargeDue", SecurityConstants.VIEW);
        security.allow("forwardWaiveChargeOverDue", SecurityConstants.VIEW);
        security.allow("waiveChargeDue", SecurityConstants.VIEW);
        security.allow("waiveChargeOverDue", SecurityConstants.VIEW);
        security.allow("getAllActivity", SecurityConstants.VIEW);
        security.allow("getAllClosedAccounts", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getRepayLoanSecurity() {
        ActionSecurity security = new ActionSecurity("repayLoanAction");
        security.allow("loadRepayment", SecurityConstants.LOAN_CAN_REPAY_LOAN);
        security.allow("preview", SecurityConstants.LOAN_CAN_REPAY_LOAN);
        security.allow("previous", SecurityConstants.LOAN_CAN_REPAY_LOAN);
        security.allow("makeRepayment", SecurityConstants.LOAN_CAN_REPAY_LOAN);
        return security;
    }

    private ActionSecurity getCustomerApplyAdjustmentSecurity() {
        ActionSecurity security = new ActionSecurity("custApplyAdjustment");
        security.allow("loadAdjustment", SecurityConstants.VIEW);
        security.allow("previewAdjustment", SecurityConstants.VIEW);
        security.allow("applyAdjustment", SecurityConstants.VIEW);
        security.allow("cancelAdjustment", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getApplyAdjustmentSecurity() {
        ActionSecurity security = new ActionSecurity("applyAdjustment");
        security.allow("loadAdjustment", SecurityConstants.VIEW);
        security.allow("previewAdjustment", SecurityConstants.VIEW);
        security.allow("applyAdjustment", SecurityConstants.VIEW);
        security.allow("cancelAdjustment", SecurityConstants.VIEW);
        security.allow("loadAdjustmentWhenObligationMet", SecurityConstants.CAN_ADJUST_PAYMENT_WHEN_OBLIGATION_MET);
        security.allow("listPossibleAdjustments", SecurityConstants.VIEW);
        security.allow("editAdjustment", SecurityConstants.VIEW);
        security.allow("divide", SecurityConstants.VIEW);
        
        return security;
    }

    private ActionSecurity getSavingsApplyAdjustmentSecurity() {
        ActionSecurity security = new ActionSecurity("savingsApplyAdjustmentAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("adjustLastUserAction", SecurityConstants.VIEW);
        security.allow("list", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getSavingsClosureSecurity() {
        ActionSecurity security = new ActionSecurity("savingsClosureAction");
        security.allow("load", SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
        security.allow("preview", SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
        security.allow("previous", SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
        security.allow("close", SecurityConstants.SAVINGS_CLOSE_SAVINGS_ACCOUNT);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getSavingsSecurity() {
        ActionSecurity security = new ActionSecurity("savingsAction");

        security.allow("getPrdOfferings", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.VIEW);
        security.allow("reLoad", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("getStatusHistory", SecurityConstants.VIEW);
        security.allow("edit", SecurityConstants.SAVINGS_UPDATE_SAVINGS);
        security.allow("editPreview", SecurityConstants.SAVINGS_UPDATE_SAVINGS);
        security.allow("editPrevious", SecurityConstants.SAVINGS_UPDATE_SAVINGS);
        security.allow("update", SecurityConstants.SAVINGS_UPDATE_SAVINGS);
        security.allow("getRecentActivity", SecurityConstants.VIEW);
        security.allow("getTransactionHistory", SecurityConstants.VIEW);
        security.allow("getDepositDueDetails", SecurityConstants.VIEW);
        security.allow("waiveAmountDue", SecurityConstants.SAVINGS_CANWAIVE_DUEAMOUNT);
        security.allow("waiveAmountOverDue", SecurityConstants.SAVINGS_CANWAIVE_OVERDUEAMOUNT);
        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getAccountAppSecurity() {
        ActionSecurity security = new ActionSecurity("accountAppAction");
        security.allow("removeFees", SecurityConstants.VIEW);
        security.allow("getTrxnHistory", SecurityConstants.VIEW);
        security.allow("removePenalties", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getCollectionSheetEntrySecurity() {
        ActionSecurity security = new ActionSecurity("collectionsheetaction");
        security.allow("load", SecurityConstants.CAN_ENTER_COLLECTION_SHEET_DATA);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("getLastMeetingDateForCustomer", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("loadLoanOfficers", SecurityConstants.VIEW);
        security.allow("loadCustomerList", SecurityConstants.VIEW);
        security.allow("validate", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getCustSecurity() {
        ActionSecurity security = new ActionSecurity("custAction");
        security.allow("getClosedAccounts", SecurityConstants.VIEW);
        security.allow("getBackToDetailsPage", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getEditStatusSecurity() {
        ActionSecurity security = new ActionSecurity("editStatusAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getChkListSecurity() {
        ActionSecurity security = new ActionSecurity("chkListAction");
        security.allow("load", SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
        security.allow("getStates", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
        security.allow("create", SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
        security.allow("cancelCreate", SecurityConstants.VIEW);
        security.allow("cancelManage", SecurityConstants.VIEW);

        security.allow("manage", SecurityConstants.CHECKLIST_EDIT_CHECKLIST);
        security.allow("getEditStates", SecurityConstants.VIEW);
        security.allow("managePreview", SecurityConstants.VIEW);
        security.allow("managePrevious", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.CHECKLIST_EDIT_CHECKLIST);

        security.allow("loadAllChecklist", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getFeeSecurity() {
        ActionSecurity security = new ActionSecurity("feeaction");
        security.allow("search", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.FEES_CREATE_FEES);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("editPreview", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.FEES_CREATE_FEES);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("manage", SecurityConstants.FEES_EDIT_FEES);
        security.allow("update", SecurityConstants.FEES_EDIT_FEES);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("editPrevious", SecurityConstants.VIEW);
        security.allow("viewAll", SecurityConstants.VIEW);
        security.allow("cancelCreate", SecurityConstants.VIEW);
        security.allow("cancelEdit", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getLoanPrdSecurity() {
        ActionSecurity security = new ActionSecurity("loanproductaction");
        security.allow("load", SecurityConstants.DEFINE_NEW_LOAN_PRODUCT_INSTANCE);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("cancelCreate", SecurityConstants.VIEW);
        security.allow("validate", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.DEFINE_NEW_LOAN_PRODUCT_INSTANCE);
        security.allow("viewAllLoanProducts", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("editPreview", SecurityConstants.VIEW);
        security.allow("editPrevious", SecurityConstants.VIEW);
        security.allow("editCancel", SecurityConstants.VIEW);
        security.allow("manage", SecurityConstants.EDIT_LOAN_PRODUCT);
        security.allow("update", SecurityConstants.EDIT_LOAN_PRODUCT);
        security.allow("update", SecurityConstants.EDIT_LOAN_PRODUCT);
        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getMeetingSecurity() {
        ActionSecurity security = new ActionSecurity("meetingAction");
        security.allow("load", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.VIEW);
        security.allow("edit", SecurityConstants.VIEW);
        security.allow("cancelCreate", SecurityConstants.VIEW);
        security.allow("cancelUpdate", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getGroupTransferSecurity() {
        ActionSecurity security = new ActionSecurity("groupTransferAction");
        security.allow("loadParents", SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);
        security.allow("loadBranches", SecurityConstants.GROUP_TRANSFER_THE_GROUP);
        security.allow("previewBranchTransfer", SecurityConstants.VIEW);
        security.allow("previewParentTransfer", SecurityConstants.VIEW);
        security.allow("transferToCenter", SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);
        security.allow("transferToBranch", SecurityConstants.GROUP_TRANSFER_THE_GROUP);
        security.allow("loadGrpMemberShip", SecurityConstants.GROUP_TRANSFER_THE_GROUP);
        security.allow("removeGroupMemberShip", SecurityConstants.CAN_REMOVE_CLIENTS_FROM_GROUPS);
        return security;
    }

    private ActionSecurity getClientTransferSecurity() {
        ActionSecurity security = new ActionSecurity("clientTransferAction");
        security.allow("loadParents", SecurityConstants.CIENT_CHANGE_GROUP_MEMBERSHIP);
        security.allow("loadBranches", SecurityConstants.CIENT_TRANSFER_THE_CLIENT);
        security.allow("previewBranchTransfer", SecurityConstants.VIEW);
        security.allow("previewParentTransfer", SecurityConstants.VIEW);
        security.allow("updateParent", SecurityConstants.CIENT_CHANGE_GROUP_MEMBERSHIP);
        security.allow("transferToBranch", SecurityConstants.CIENT_TRANSFER_THE_CLIENT);
        return security;
    }

    private ActionSecurity getCenterCustSecurity() {
        ActionSecurity security = new ActionSecurity("centerCustAction");
        security.allow("chooseOffice", SecurityConstants.CENTER_CREATE_NEW_CENTER);
        security.allow("load", SecurityConstants.CENTER_CREATE_NEW_CENTER);
        security.allow("loadMeeting", SecurityConstants.MEETING_CREATE_CENTER_MEETING);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.CENTER_CREATE_NEW_CENTER);
        security.allow("manage", SecurityConstants.CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS);
        security.allow("editPrevious", SecurityConstants.VIEW);
        security.allow("editPreview", SecurityConstants.VIEW);
        security.allow("update", SecurityConstants.CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS);

        security.allow("get", SecurityConstants.VIEW);
        security.allow("loadSearch", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);
        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);

        security.allow("loadTransferSearch", SecurityConstants.VIEW);
        security.allow("searchTransfer", SecurityConstants.VIEW);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getPersonSecurity() {
        ActionSecurity security = new ActionSecurity("PersonAction");
        security.allow("get", SecurityConstants.VIEW);
        security.allow("loadSearch", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);
        security.allow("chooseOffice", SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
        security.allow("load", SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
        security.allow("manage", SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
        security.allow("previewManage", SecurityConstants.VIEW);

        security.allow("previousManage", SecurityConstants.PERSONNEL_EDIT_SELF_INFO);
        security.allow("update", SecurityConstants.PERSONNEL_EDIT_PERSONNEL);
        security.allow("/PersonnelAction-prevPersonalInfo", SecurityConstants.VIEW);

        security.allow("preview", SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
        security.allow("previous", SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
        security.allow("create", SecurityConstants.PERSONNEL_CREATE_PERSONNEL);
        security.allow("loadUnLockUser", SecurityConstants.PERSONNEL_UNLOCK_PERSONNEL);
        security.allow("unLockUserAccount", SecurityConstants.PERSONNEL_UNLOCK_PERSONNEL);
        security.allow("loadChangeLog", SecurityConstants.VIEW);
        security.allow("cancelChangeLog", SecurityConstants.VIEW);
        security.allow("captureQuestionResponses", SecurityConstants.VIEW);
        security.allow("editQuestionResponses", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getCustHistoricalSecurity() {
        ActionSecurity security = new ActionSecurity("custHistoricalDataAction");
        security.allow("loadHistoricalData", SecurityConstants.VIEW);
        security.allow("getHistoricalData", SecurityConstants.VIEW);
        security.allow("previewHistoricalData", SecurityConstants.VIEW);
        security.allow("previousHistoricalData", SecurityConstants.VIEW);
        security.allow("updateHistoricalData", SecurityConstants.VIEW);
        security.allow("cancelHistoricalData", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getLoginSecurity() {
        ActionSecurity security = new ActionSecurity("loginAction");
        security.allow("login", SecurityConstants.VIEW);
        security.allow("logout", SecurityConstants.VIEW);
        security.allow("updatePassword", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getCustSearchSecurity() {
        ActionSecurity security = new ActionSecurity("custSearchAction");
        security.allow("loadSearch", SecurityConstants.VIEW);
        security.allow("search", SecurityConstants.VIEW);
        security.allow("load", SecurityConstants.VIEW);
        security.allow("loadMainSearch", SecurityConstants.VIEW);
        security.allow("mainSearch", SecurityConstants.VIEW);
        security.allow("getHomePage", SecurityConstants.VIEW);
        security.allow("loadAllBranches", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("getOfficeHomePage", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getAdminActionSecurity() {
        ActionSecurity security = new ActionSecurity("AdminAction");
        security.allow("load", SecurityConstants.VIEW);
        return security;
    }

    private ActionSecurity getFinancialAccountingSecurity() {
        ActionSecurity security = new ActionSecurity("FinancialAccountingAction");
        security.allow("load", SecurityConstants.VIEW);
        return security;
    }
    
    private ActionSecurity getAccountGroupIndividualPaymentSecurity() {
        ActionSecurity security = new ActionSecurity("applyIndividualPayment");
        security.allow("load", SecurityConstants.VIEW);
        return security;
    }

    private void addCustomerSearchMappings() {
        activityMap.put("/CustomerSearchAction-load", SecurityConstants.VIEW);
        activityMap.put("/CustomerSearchAction-search", SecurityConstants.SEARCH);
        activityMap.put("/CustomerSearchAction-preview", SecurityConstants.SEARCH);
        activityMap.put("/CustomerSearchAction-get", SecurityConstants.SEARCH);
        activityMap.put("/CustomerSearchAction-getHomePage", SecurityConstants.VIEW);
        activityMap.put("/CustomerSearchAction-getOfficeHomePage", SecurityConstants.VIEW);
        activityMap.put("/CustomerSearchAction-loadAllBranches", SecurityConstants.VIEW);
    }

    private void addOfficeMappings() {
        activityMap.put("/OfficeAction-loadall", SecurityConstants.VIEW);
        activityMap.put("/OfficeAction-load", SecurityConstants.OFFICE_CREATE_OFFICE);

        activityMap.put("/OfficeAction-loadParent", SecurityConstants.VIEW);
        activityMap.put("/OfficeAction-preview", SecurityConstants.VIEW);
        activityMap.put("/OfficeAction-create", SecurityConstants.OFFICE_CREATE_OFFICE);
        activityMap.put("/OfficeAction-get", SecurityConstants.VIEW);
        activityMap.put("/OfficeAction-manage", SecurityConstants.OFFICE_EDIT_OFFICE);

        activityMap.put("/OfficeAction-previous", SecurityConstants.VIEW);
        activityMap.put("/OfficeAction-update", SecurityConstants.OFFICE_EDIT_OFFICE);

        activityMap.put("/OfficeHierarchyAction-cancel", SecurityConstants.VIEW);
        activityMap.put("/OfficeHierarchyAction-load", SecurityConstants.VIEW);
        activityMap.put("/OfficeHierarchyAction-update", SecurityConstants.OFFICE_EDIT_OFFICE);

        activityMap.put("/offhierarchyaction-cancel", SecurityConstants.VIEW);
        activityMap.put("/offhierarchyaction-load", SecurityConstants.VIEW);
        activityMap.put("/offhierarchyaction-update", SecurityConstants.OFFICE_HIERATCHY_UPDATE);

        // m2 office action
        activityMap.put("/offAction-load", SecurityConstants.OFFICE_CREATE_OFFICE);
        activityMap.put("/offAction-loadParent", SecurityConstants.VIEW);
        activityMap.put("/offAction-preview", SecurityConstants.VIEW);
        activityMap.put("/offAction-previous", SecurityConstants.VIEW);
        activityMap.put("/offAction-create", SecurityConstants.OFFICE_CREATE_OFFICE);
        activityMap.put("/offAction-get", SecurityConstants.VIEW);
        activityMap.put("/offAction-edit", SecurityConstants.OFFICE_EDIT_OFFICE);
        activityMap.put("/offAction-editpreview", SecurityConstants.OFFICE_EDIT_OFFICE);
        activityMap.put("/offAction-editprevious", SecurityConstants.OFFICE_EDIT_OFFICE);
        activityMap.put("/offAction-update", SecurityConstants.OFFICE_EDIT_OFFICE);
        activityMap.put("/offAction-getAllOffices", SecurityConstants.VIEW);
        activityMap.put("/offAction-updateCache", SecurityConstants.VIEW);
        activityMap.put("/offAction-captureQuestionResponses", SecurityConstants.VIEW);
        activityMap.put("/offAction-editQuestionResponses", SecurityConstants.VIEW);
    }

    private void addSavingProductMappings() {
        activityMap.put("/savingsprdaction-search", SecurityConstants.VIEW);
        activityMap.put("/savingsprdaction-load", SecurityConstants.DEFINE_NEW_SAVING_PRODUCT_INSTANCE);
        activityMap.put("/savingsprdaction-preview", SecurityConstants.VIEW);
        activityMap.put("/savingsprdaction-previous", SecurityConstants.VIEW);
        activityMap.put("/savingsprdaction-create", SecurityConstants.DEFINE_NEW_SAVING_PRODUCT_INSTANCE);
        activityMap.put("/savingsprdaction-get", SecurityConstants.VIEW);
        activityMap.put("/savingsprdaction-manage", SecurityConstants.EDIT_SAVING_PRODUCT);
        activityMap.put("/savingsprdaction-update", SecurityConstants.EDIT_SAVING_PRODUCT);
    }

    private void addFeeMappings() {
        activityMap.put("/feesAction-search", SecurityConstants.VIEW);
        activityMap.put("/feesAction-load", SecurityConstants.FEES_CREATE_FEES);
        activityMap.put("/feesAction-preview", SecurityConstants.VIEW);
        activityMap.put("/feesAction-create", SecurityConstants.FEES_CREATE_FEES);
        activityMap.put("/feesAction-get", SecurityConstants.VIEW);
        activityMap.put("/feesAction-manage", SecurityConstants.FEES_EDIT_FEES);
        activityMap.put("/feesAction-update", SecurityConstants.FEES_EDIT_FEES);
        activityMap.put("/feesAction-previous", SecurityConstants.VIEW);
    }

    private void addChecklistMappings() {

        activityMap.put("/checkListAction-loadall", SecurityConstants.VIEW);
        activityMap.put("/checkListAction-load", SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
        activityMap.put("/checkListAction-create", SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
        activityMap.put("/checkListAction-preview", SecurityConstants.VIEW);
        activityMap.put("/checkListAction-previous", SecurityConstants.VIEW);
        activityMap.put("/checkListAction-previous", SecurityConstants.CHECKLIST_CREATE_CHECKLIST);
        activityMap.put("/checkListAction-loadParent", SecurityConstants.VIEW);
        activityMap.put("/checkListAction-get", SecurityConstants.VIEW);
        activityMap.put("/checkListAction-manage", SecurityConstants.CHECKLIST_EDIT_CHECKLIST);
        activityMap.put("/checkListAction-update", SecurityConstants.CHECKLIST_EDIT_CHECKLIST);
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
        activityMap.put("/loanAction-manage", SecurityConstants.LOAN_UPDATE_LOAN);
        activityMap.put("/loanAction-update", SecurityConstants.LOAN_UPDATE_LOAN);
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
        activityMap.put("/accountTrxn-load", SecurityConstants.APPLY_PAYMENT_TO_CLIENT_GROUP_CENTERS_LOANS);
        activityMap.put("/accountTrxn-create", SecurityConstants.APPLY_PAYMENT_TO_CLIENT_GROUP_CENTERS_LOANS);
        activityMap.put("/accountTrxn-preview", SecurityConstants.VIEW);
        activityMap.put("/accountTrxn-getInstallmentHistory", SecurityConstants.VIEW);
        activityMap.put("/loanAccountAction-getInstallmentDetails", SecurityConstants.VIEW);
        activityMap.put("/accountTrxn-previous", SecurityConstants.VIEW);
    }

    private void addApplyChargesMappings2() {
        activityMap.put("/AccountsApplyChargesAction-load",
                SecurityConstants.APPLY_CHARGES_TO_CLIENT_GROUP_CENTERS_LOANS);
        activityMap.put("/AccountsApplyChargesAction-create",
                SecurityConstants.APPLY_CHARGES_TO_CLIENT_GROUP_CENTERS_LOANS);
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

    public short getActivityIdForNewCustomerStateId(short newState, short cancelFlag) {
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

    public boolean isStateChangePermittedForAccount(short newSate, short stateFlag, UserContext userContext,
            Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao
                .isActivityAllowed(
                        userContext,
                        new ActivityContext(getActivityIdForNewStateId(newSate, stateFlag), recordOfficeId,
                                recordLoanOfficerId));
    }

    public boolean isStateChangePermittedForCustomer(short newSate, short stateFlag, UserContext userContext,
            Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(getActivityIdForNewCustomerStateId(newSate, stateFlag), recordOfficeId,
                        recordLoanOfficerId));
    }

    public boolean isSavePermittedForAccount(short newSate, UserContext userContext, Short recordOfficeId,
            Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(userContext,
                new ActivityContext(getActivityIdForState(newSate), recordOfficeId, recordLoanOfficerId));
    }

    public boolean isSavePermittedForCustomer(short newSate, UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {

        final short activityId = getActivityIdForCustomerState(newSate);

        Short officeId = recordOfficeId;
        if (officeId == null) {
            officeId = userContext.getBranchId();
        }

        Short loanOfficerId = recordLoanOfficerId;
        if (loanOfficerId == null) {
            loanOfficerId = userContext.getId();
        }

        ActivityContext activityContext = new ActivityContext(activityId, officeId, loanOfficerId);

        return legacyRolesPermissionsDao.isActivityAllowed(userContext, activityContext);
    }

    public boolean isAddingNotesPermittedForAccounts(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {

        short activityId = getActivityIdForAddingNotes(accountTypes, customerLevel);
        ActivityContext activityContext = new ActivityContext(activityId, recordOfficeId, recordLoanOfficerId);
        return legacyRolesPermissionsDao.isActivityAllowed(userContext, activityContext);
    }

    private short getActivityIdForAddingNotes(AccountTypes accountTypes, CustomerLevel customerLevel) {
        short activityId = -1;
        if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT)) {
            activityId = SecurityConstants.LOAN_CAN_ADD_NOTES_TO_LOAN;
        } else if (accountTypes.equals(AccountTypes.SAVINGS_ACCOUNT)) {
            activityId = SecurityConstants.SAVINGS_CAN_ADD_NOTES_TO_SAVINGS;
        } else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
            if (customerLevel.equals(CustomerLevel.CENTER)) {
                activityId = SecurityConstants.CENTER_ADD_NOTE_TO_CENTER;
            } else if (customerLevel.equals(CustomerLevel.GROUP)) {
                activityId = SecurityConstants.GROUP_ADD_NOTE_TO_GROUP;
            } else if (customerLevel.equals(CustomerLevel.CLIENT)) {
                activityId = SecurityConstants.CLIENT_ADD_NOTE_TO_CLIENT;
            }
        }
        return activityId;
    }

    public boolean isAddingNotesPermittedForPersonnel(UserContext userContext, Short recordOfficeId,
            Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(userContext,
                new ActivityContext(SecurityConstants.PERSONNEL_NOTE_CREATE, recordOfficeId, recordLoanOfficerId));
    }

    public boolean isPaymentPermittedForAccounts(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(getActivityIdForPayment(accountTypes, customerLevel), recordOfficeId,
                        recordLoanOfficerId));
    }

    private short getActivityIdForApplyCharges(AccountTypes accountTypes, CustomerLevel customerLevel) {
        short activityId = -1;
        if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT) || accountTypes.equals(AccountTypes.GROUP_LOAN_ACCOUNT)) {
            activityId = SecurityConstants.LOAN_CAN_APPLY_CHARGES;
        } else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
            if (customerLevel.equals(CustomerLevel.CENTER)) {
                activityId = SecurityConstants.CENTER_CAN_APPLY_CHARGES;
            } else if (customerLevel.equals(CustomerLevel.GROUP)) {
                activityId = SecurityConstants.GROUP_CAN_APPLY_CHARGES;
            } else if (customerLevel.equals(CustomerLevel.CLIENT)) {
                activityId = SecurityConstants.CLIENT_CAN_APPLY_CHARGES;
            }
        }
        return activityId;
    }

    public boolean isAdjustmentPermittedForAccounts(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(getActivityIdForAdjustment(accountTypes, customerLevel), recordOfficeId,
                        recordLoanOfficerId));
    }

    private short getActivityIdForAdjustment(AccountTypes accountTypes, CustomerLevel customerLevel) {
        short activityId = -1;
        if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT)) {
            activityId = SecurityConstants.LOAN_MAKE_ADJUSTMENT_ENTRY_TO_ACCOUNT;
        } else if (accountTypes.equals(AccountTypes.SAVINGS_ACCOUNT)) {
            activityId = SecurityConstants.SAVINGS_APPLY_ADJUSTMENT;
        } else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
            if (customerLevel.equals(CustomerLevel.CENTER)) {
                activityId = SecurityConstants.CENTER_MAKE_ADJUSTMENT_ENTRIES_TO_CENTER_ACCOUNT;
            } else if (customerLevel.equals(CustomerLevel.GROUP)) {
                activityId = SecurityConstants.GROUP_MAKE_ADJUSTMENT_ENTRIES_TO_GROUP_ACCOUNT;
            } else if (customerLevel.equals(CustomerLevel.CLIENT)) {
                activityId = SecurityConstants.CIENT_MAKE_ADJUSTMENT_ENTRIES_TO_CLIENT_ACCOUNT;
            }
        }
        return activityId;
    }

    public boolean isAddingHistoricaldataPermittedForCustomers(CustomerLevel customerLevel, UserContext userContext,
            Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(getActivityIdForAddingHistoricaldata(customerLevel), recordOfficeId,
                        recordLoanOfficerId));
    }

    private short getActivityIdForAddingHistoricaldata(CustomerLevel customerLevel) {
        short activityId = -1;
        if (customerLevel.equals(CustomerLevel.GROUP)) {
            activityId = SecurityConstants.GROUP_ADD_EDIT_HISTORICAL_DATA;
        } else if (customerLevel.equals(CustomerLevel.CLIENT)) {
            activityId = SecurityConstants.CIENT_ADD_EDIT_HISTORICAL_DATA;
        }
        return activityId;
    }

    public boolean isWaiveDuePermittedForCustomers(WaiveEnum waiveEnum, AccountTypes accountTypes,
            CustomerLevel customerLevel, UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(getActivityIdForWaiveDue(waiveEnum, accountTypes, customerLevel), recordOfficeId,
                        recordLoanOfficerId));
    }

    private short getActivityIdForWaiveDue(WaiveEnum waiveEnum, AccountTypes accountTypes, CustomerLevel customerLevel) {
        short activityId = -1;
        if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT)) {
            if (waiveEnum.equals(WaiveEnum.FEES)) {
                activityId = SecurityConstants.LOAN_WAIVE_FEE_INSTALLMENT;
            } else if (waiveEnum.equals(WaiveEnum.PENALTY)) {
                activityId = SecurityConstants.LOAN_WAIVE_PANELTY;
            }
        } else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
            if (customerLevel.equals(CustomerLevel.CENTER)) {
                activityId = SecurityConstants.CENTER_WAIVE_DUE_AMOUNT;
            } else if (customerLevel.equals(CustomerLevel.GROUP)) {
                activityId = SecurityConstants.GROUP_WAIVE_DUE_AMOUNT;
            } else if (customerLevel.equals(CustomerLevel.CLIENT)) {
                activityId = SecurityConstants.CIENT_WAIVE_DUE_AMOUNT;
            }
        }
        return activityId;
    }

    public boolean isAccessAllowed(UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isAccessAllowed(userContext, recordOfficeId, recordLoanOfficerId);
    }

    public boolean isRemoveFeesPermittedForAccounts(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(getActivityIdForRemoveFees(accountTypes, customerLevel), recordOfficeId,
                        recordLoanOfficerId));
    }

    public boolean isViewActiveSessionsPermitted(UserContext userContext, Short officeId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(SecurityConstants.CAN_VIEW_ACTIVE_SESSIONS, officeId));
    }

    private short getActivityIdForRemoveFees(AccountTypes accountTypes, CustomerLevel customerLevel) {
        short activityId = -1;
        if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT)) {
            activityId = SecurityConstants.LOAN_REMOVE_FEE_TYPE_ATTACHED_TO_ACCOUNT;
        } else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
            if (customerLevel.equals(CustomerLevel.CENTER)) {
                activityId = SecurityConstants.CENTER_REMOVE_FEE_TYPE_FROM_CENTER_ACCOUNT;
            } else if (customerLevel.equals(CustomerLevel.GROUP)) {
                activityId = SecurityConstants.GROUP_REMOVE_FEE_TYPE_FROM_GROUP_ACCOUNT;
            } else if (customerLevel.equals(CustomerLevel.CLIENT)) {
                activityId = SecurityConstants.CIENT_REMOVE_FEE_TYPE_FROM_CLIENT_ACCOUNT;
            }
        }
        return activityId;
    }

    public boolean isApplyChargesPermittedForAccounts(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(getActivityIdForApplyCharges(accountTypes, customerLevel), recordOfficeId,
                        recordLoanOfficerId));
    }

    private short getActivityIdForPayment(AccountTypes accountTypes, CustomerLevel customerLevel) {
        short activityId = -1;
        if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT) || accountTypes.equals(AccountTypes.GROUP_LOAN_ACCOUNT) ) {
            activityId = SecurityConstants.LOAN_MAKE_PAYMENT_TO_ACCOUNT;
        } else if (accountTypes.equals(AccountTypes.CUSTOMER_ACCOUNT)) {
            if (customerLevel.equals(CustomerLevel.CENTER)) {
                activityId = SecurityConstants.CENTER_MAKE_PAYMENTS_TO_CENTER_ACCOUNT;
            } else if (customerLevel.equals(CustomerLevel.GROUP)) {
                activityId = SecurityConstants.GROUP_MAKE_PAYMENT_TO_GROUP_ACCOUNT;
            } else if (customerLevel.equals(CustomerLevel.CLIENT)) {
                activityId = SecurityConstants.CIENT_MAKE_PAYMENT_TO_CLIENT_ACCOUNT;
            }
        }
        return activityId;
    }

    public boolean isEditMeetingSchedulePermittedForCustomers(CustomerLevel customerLevel, UserContext userContext,
            Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(getActivityIdForEditMeetingSchedule(customerLevel), recordOfficeId,
                        recordLoanOfficerId));
    }

    private short getActivityIdForEditMeetingSchedule(CustomerLevel customerLevel) {
        short activityId = -1;
        if (customerLevel.equals(CustomerLevel.CENTER)) {
            activityId = SecurityConstants.MEETING_UPDATE_CENTER_MEETING;
        } else if (customerLevel.equals(CustomerLevel.GROUP)) {
            activityId = SecurityConstants.MEETING_UPDATE_GROUP_MEETING;
        } else if (customerLevel.equals(CustomerLevel.CLIENT)) {
            activityId = SecurityConstants.MEETING_UPDATE_CLIENT_MEETING;
        }
        return activityId;
    }

    private void parseActionSecurity(ActionSecurity security) {
        for (String method : security.methods()) {
            // example fullKey: "/reportsUserParamsAction-loadAdminReport"
            String fullKey = "/" + security.getActionName() + "-" + method;
            // value maps to a primary key in the activity table?
            activityMap.put(fullKey, security.get(method));
        }
        allSecurity.add(security);
    }

    public boolean isAdjustmentPermittedForBackDatedPayments(Date lastPaymentDate, UserContext userContext,
                                                             Short recordOfficeId, Short recordLoanOfficer) {
        boolean activityAllowed = true;
        Date today = DateUtils.currentDate();
        if(DateUtils.dateFallsBeforeDate(lastPaymentDate, today)){
            ActivityContext activityContext = new ActivityContext(SecurityConstants.LOAN_ADJUST_BACK_DATED_TRXNS, recordOfficeId, recordLoanOfficer);
            activityAllowed = legacyRolesPermissionsDao.isActivityAllowed(userContext, activityContext);
        }

        return activityAllowed;
    }
    
    public boolean isModeOfPaymentSecurity(UserContext userContext) {
        return legacyRolesPermissionsDao.isActivityAllowed(userContext,
                new ActivityContext(SecurityConstants.LOAN_CAN_TRANSFER_FROM_SAVINGS_ACCOUNT, userContext.getBranchId()));
    }

     public boolean isEditPhoneNumberPermitted(UserContext useContext, Short officeId){
        return legacyRolesPermissionsDao.isActivityAllowed(
                useContext,
                new ActivityContext(SecurityConstants.CAN_EDIT_PHONE_NUMBER, officeId));
    }

    public Object isRemovePenaltiesPermittedForAccounts(AccountTypes accountTypes, CustomerLevel customerLevel,
            UserContext userContext, Short recordOfficeId, Short recordLoanOfficerId) {
        return legacyRolesPermissionsDao.isActivityAllowed(
                userContext,
                new ActivityContext(getActivityIdForRemovePenalties(accountTypes, customerLevel), recordOfficeId,
                        recordLoanOfficerId));
    }

    private short getActivityIdForRemovePenalties(AccountTypes accountTypes, CustomerLevel customerLevel) {
        short activityId = -1;
        
        if (accountTypes.equals(AccountTypes.LOAN_ACCOUNT)) {
            activityId = SecurityConstants.LOAN_REMOVE_PENALTY_TYPE_ATTACHED_TO_ACCOUNT;
        }
        
        return activityId;
    }
}