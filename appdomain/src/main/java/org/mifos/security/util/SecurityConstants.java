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

/**
 * This inteface will hold all the constants used related to the security
 * 
 * NOTE: if a new activity is being added, the values in RoleTestUtil need to be increased.
 */
public interface SecurityConstants {
    String NOSUCHALGORITHM = "security.error.noSuchAlgorithm";

    String NOSUCHPADDING = "security.error.noSuchPadding";

    String ENCODINGNOTSUPPORTED = "security.error.encodingNotSupported";

    String PBEWITHMD5ANDDES = "PBEWithMD5AndDES";

    String DESEDE = "DESede";

    String DES = "DES";

    String INITIALIZATIONFAILED = "security.error.iniFailed";

    String GENERALERROR = "security.error.generalError";

    int MAXTRIES = 5;

    String ONE = "1";

    String PATTERN = "pattern";

    String ID = "ID";

    String OFFICECHANGEEVENT = "OfficeChange";

    String SECURITY_PARAM = "security_param";

    // Activity not allowd key

    String KEY_ACTIVITY_NOT_ALLOWED = "error.activity.not.allowed";
    
    String KEY_ACTIVITY_APPROVE_LOAN_NOT_ALLOWED = "error.activity.not.allowed.approve.loan";

    String KEY_SHUTDOWN_TIMEOUT_INVALID = "error.shutdown.timeout.invalid";

    // activity related mappeings
    short VIEW = 0;

    short SEARCH = 0;

    /*
     * Constants for activity IDs.
     */
    short ORGANIZATION_MANAGEMENT = 1;

    // fees
    short FEES_CREATE_FEES = 6;

    short FEES_EDIT_FEES = 7;

    // checklist
    short CHECKLIST_CREATE_CHECKLIST = 9;

    short CHECKLIST_EDIT_CHECKLIST = 10;

    // Miscellaneous
    short DEFINE_NEW_DATA_FIELDS = 12;

    short EDIT_DATA_FIELDS = 12;

    // Office related activities
    short OFFICE_CREATE_OFFICE = 15;

    short OFFICE_EDIT_OFFICE = 16;

    short OFFICE_HIERATCHY_UPDATE = 193;

    // User management
    short PERSONNEL_CREATE_PERSONNEL = 19;

    short PERSONNEL_EDIT_PERSONNEL = 20;

    short PERSONNEL_NOTE_CREATE = 194;

    short PERSONNEL_UNLOCK_PERSONNEL = 21;

    short ROLES_CREATE_ROLES = 23;

    short ROLES_EDIT_ROLES = 24;

    short ROLES_DELETE_ROLES = 25;
    
    //Accounting management
    short ACCOUNTING_CREATE_GLTRANSACTION = 280; 
    short ACCOUNTING_CREATE_JVTRANSACTION = 281;
    short ACCOUNTING_CREATE_OPENBALANCE = 282; 
    short ACCOUNTING_CREATE_VIEWTRANSACTIONS = 283; 
    short ACCOUNTING_CREATE_MISPROCESSING = 284; 

    // Client attributes management
    short EDIT_CLIENTNAME_HIERARCHY_GROUPLOANALLOWED = 28;

    short CREATE_ADMINISTRATIVEFEEFOR_CLIENTS_GROUPS_CENTERS = 30;

    short CONFIGURE_HIDE_UNHIDE_DATAFIELDS_FOR_GROUP_CENTER = 32;

    // Client management
    short CLIENT_MANAGEMENT = 33;
    short CLIENTS = 34;

    short CLIENT_ADD_NOTE_TO_CLIENT = 48;

    short CIENT_CREATE_NEW_CLIENT_IN_SAVE_FOR_LATER_STATE = 35;

    short CIENT_CREATE_NEW_CLIENT_IN_SUBMIT_FOR_APPROVAL_STATE = 36;

    short CIENT_CHANGE_STATE_TO_SAVE_FOR_LATER_AND_SUBMIT_FOR_APPROVAL_STATE = 37;

    short CIENT_CHANGE_STATE_TO_APPROVAD = 38;

    short CIENT_CHANGE_STATE_TO_CANCELLED = 39;

    short CIENT_CHANGE_STATE_TO_ON_HOLD = 40;

    short CIENT_CHANGE_STATE_TO_ON_CLOSED = 41;

    short CIENT_APPLY_CHARGES_TO_CLIENT_ACCOUNT = 42;

    short CIENT_MAKE_PAYMENT_TO_CLIENT_ACCOUNT = 43;

    short CIENT_MAKE_ADJUSTMENT_ENTRIES_TO_CLIENT_ACCOUNT = 44;

    short CIENT_WAIVE_DUE_AMOUNT = 46;

    short CIENT_REMOVE_FEE_TYPE_FROM_CLIENT_ACCOUNT = 47;

    short CIENT_ADD_NOTES_TO_ACCOUNT = 48;

    short CIENT_EDIT_MFI_INFORMATION = 49;

    short CIENT_CHANGE_GROUP_MEMBERSHIP = 50;

    short CIENT_TRANSFER_THE_CLIENT = 51;

    short CIENT_EDIT_MEETINGSCHEDULE = 52;

    short CIENT_ADD_EDIT_HISTORICAL_DATA = 53;

    short CIENT_EDIT_FEE_TYPE_ATTACHED_TO_ACCOUNT = 54;

    short CIENT_BLACKLIST_A_CLIENT = 55;

    short CAN_EDIT_PHONE_NUMBER = 245;

    // Group management
    short GROUP_ADD_NOTE_TO_GROUP = 70;

    short GROUP_CREATE_NEW_GROUP_IN_SAVE_FOR_LATER_STATE = 57;

    short GROUP_CREATE_NEW_GROUP_IN_SUBMIT_FOR_APPROVAL_STATE = 58;

    short GROUP_CHANGE_STATE_TO_SAVE_FOR_LATER_AND_SUBMIT_FOR_APPROVAL_STATE = 59;

    short GROUP_CHANGE_STATE_TO_APPROVAD = 60;

    short GROUP_CHANGE_STATE_TO_CANCELLED = 61;

    short GROUP_CHANGE_STATE_TO_ON_HOLD = 62;

    short GROUP_CHANGE_STATE_TO_ON_CLOSED = 63;

    short GROUP_APPLY_CHARGES_TO_GROUP_ACCOUNT = 64;

    short GROUP_MAKE_PAYMENT_TO_GROUP_ACCOUNT = 65;

    short GROUP_MAKE_ADJUSTMENT_ENTRIES_TO_GROUP_ACCOUNT = 66;

    short GROUP_WAIVE_DUE_AMOUNT = 68;

    short GROUP_REMOVE_FEE_TYPE_FROM_GROUP_ACCOUNT = 69;

    short GROUP_ADD_NOTES_TO_ACCOUNT = 70;

    short GROUP_EDIT_GROUP = 71;

    short GROUP_CHANGE_CENTER_MEMBERSHIP = 72;

    short GROUP_TRANSFER_THE_GROUP = 73;

    short GROUP_EDIT_MEETING_SCHEDULE = 74;

    short GROUP_ADD_EDIT_HISTORICAL_DATA = 75;

    short GROUP_EDIT_FEE_TYPE_ATTACHED_TO_ACCOUNT = 76;

    short GROUP_BLACKLIST_A_GROUP = 77;

    // Center
    short CENTER_ADD_NOTE_TO_CENTER = 87;

    short CENTER_CREATE_NEW_CENTER = 79;

    short CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS = 80;

    short CENTER_MAKE_PAYMENTS_TO_CENTER_ACCOUNT = 82;

    short CENTER_MAKE_ADJUSTMENT_ENTRIES_TO_CENTER_ACCOUNT = 83;

    short CENTER_WAIVE_DUE_AMOUNT = 85;

    short CENTER_REMOVE_FEE_TYPE_FROM_CENTER_ACCOUNT = 86;

    short CENTER_ADD_NOTES_TO_ACCOUNT = 87;

    short CENTER_EDIT_FEE_TYPE_ATTACHED_TO_ACCOUNT = 88;

    short PRODUCT_DEFINITION = 89;

    short CENTER_EDIT_STATUS = 81;

    short CENTER_EDIT_CENTER_STATUS = 186;

    // loan product
    short DEFINE_NEW_LOAN_PRODUCT_INSTANCE = 94;

    short EDIT_LOAN_PRODUCT = 95;

    // saving product
    short DEFINE_NEW_SAVING_PRODUCT_INSTANCE = 97;

    short EDIT_SAVING_PRODUCT = 98;
    short LOAN_MANAGEMENT = 99;

    short LOAN_TRANSACTIONS = 113;

    // Loan management
    short LOAN_CREATE_LOAN_ACCOUNT_IN_SAVE_FOR_LATER_STATE = 101;

    short LOAN_CREATE_LOAN_ACCOUNT_IN_SUBMIT_FOR_APPROVAL_STATE = 102;

    short LOAN_CHANGE_STATE_TO_SAVE_FOR_LATER_AND_SUBMIT_FOR_APPROVAL = 103;

    short LOAN_CHANGE_STATE_TO_APPROVAD = 104;

    short LOAN_CHANGE_STATE_TO_CANCELLED = 105;

    short LOAN_CHANGE_STATE_TO_DISBURSED_TO_LOAN_OFFICER = 106;

    short LOAN_CHANGE_STATE_TO_ACTIVE_IN_GOOD_STANDING = 107;

    short LOAN_CHANGE_STATE_TO_IN_ARREARS = 108;

    short LOAN_CHANGE_STATE_TO_CLOSED_WRITTEN_OFF = 109;

    short LOAN_CHANGE_STATE_TO_CLOSED_RESCHEDULE = 110;

    short LOAN_CHANGE_STATE_TO_CLOSED_OBLIGATION_MET = 111;

    short LOAN_MODIFY_THE_DEFAULTS_AMOUNT = 112;

    short LOAN_ADJUST_BACK_DATED_TRXNS = 244;

    // savings
    short SAVINGS_APPLY_ADJUSTMENT = 186;

    // Loan Transactions
    short LOAN_CAN_ADD_NOTES_TO_LOAN = 179;

    short LOAN_APPLY_FEE_MISC_CHARGES_TO_ACCOUNT = 114;

    short LOAN_MAKE_PAYMENT_TO_ACCOUNT = 115;

    short LOAN_MAKE_ADJUSTMENT_ENTRY_TO_ACCOUNT = 116;

    short LOAN_EDIT_ACCOUNT_INFORMATION = 117;

    short LOAN_WAIVE_PANELTY = 118;

    short LOAN_WAIVE_FEE_INSTALLMENT = 119;

    short LOAN_REMOVE_FEE_TYPE_ATTACHED_TO_ACCOUNT = 120;

    short GROUP_UPDATE_STATUS = 121;

    short GROUP_CREATE_GROUP = 122;

    short CUSTOMER_ADD_NOTES_TO_CENTER_GROUP_CLIENT = 123;

    short CUSTOMER_ADD_HISTORICAL_DATA_TO_CENTER_GROUP_CLIENT = 124;

    short CLIENT_CREATE_CLIENT = 125;

    short CLIENT_UPDATE_PERSONNEL_INFO = 126;

    short CLIENT_UPDATE_STATUS = 127;

    short LOAN_CREATE_LOAN = 128;

    short LOAN_UPDATE_LOAN = 129;

    short LOAN_UPDATE_LOAN_STATUS = 130;

    short APPLY_CHARGES_TO_CLIENT_GROUP_CENTERS_LOANS = 131;

    short APPLY_PAYMENT_TO_CLIENT_GROUP_CENTERS_LOANS = 132;

    short MEETING_UPDATE_MEETING = 133;

    short MEETING_CREATE_MEETING = 134;

    short MEETING_CREATE_CLIENT_MEETING = 121;

    short MEETING_UPDATE_CLIENT_MEETING = 52;

    short MEETING_CREATE_GROUP_MEETING = 122;

    short MEETING_UPDATE_GROUP_MEETING = 74;

    short MEETING_CREATE_CENTER_MEETING = 128;

    short MEETING_UPDATE_CENTER_MEETING = 127;

    short PERSONNEL_EDIT_SELF_INFO = 135;

    short SAVINGS_CREATE_SAVINGS = 137;

    short SAVINGS_UPDATE_SAVINGS = 138;

    short SAVINGS_CLOSE_SAVINGS_ACCOUNT = 139;

    short SAVINGS_CHANGE_STATUS = 140;

    short ADMINISTER_REPORTS = 142;

    short ANALYSIS = 150;

    short CLIENTSDETAILVIEW = 152;

    short CLIENTSPRODUCTHISTORY = 153;

    short BRANCHPERFORMANCE = 164;

    short AREAPERFORMANCE = 165;

    short COLLECTIONSHEET = 159;

    short LOANDISTRIBUTION = 172;

    short BRANCHDISBURSEMENT = 173;

    short STAFFWISEREPORT = 160;

    short BRANCHWISEREPORT = 161;

    short CAN_VIEW_DAILY_PORTFOLIO = 176;

    short KENDRA_MEETING = 177;

    short LOAN_CAN_REPAY_LOAN = 178;

    short SAVINGS_CANWAIVE_DUEAMOUNT = 187;

    short SAVINGS_CANWAIVE_OVERDUEAMOUNT = 188;

    short LOAN_CAN_DISBURSE_LOAN = 189;

    short SAVINGS_CAN_MAKE_DEPOSIT_WITHDRAWAL = 190;

    short SAVINGS_CAN_ADD_NOTES_TO_SAVINGS = 191;

    short CAN_APPROVE_LOANS_IN_BULK = 192;

    short CAN_ENTER_COLLECTION_SHEET_DATA = 197;

    short REPORTS_MANAGEMENT = 141;
    
    short QUESTION_MANAGMENT = 294;

    // FIXME: both use the same ID? Is this a mistake?
    short ADMINISTER_REPORTPARAMS = 142;
    short ADMINISTER_REPORTDS = 142;

    short LOAN_CAN_APPLY_CHARGES = 131;

    /**
     * What is this? It is the parent of {@link #CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS} but I'm not sure why it didn't have
     * its own constant until now.
     */
    short BULK = 196;

    short CLIENT_CAN_APPLY_CHARGES = 198;
    short GROUP_CAN_APPLY_CHARGES = 199;
    short CENTER_CAN_APPLY_CHARGES = 200;

    short CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS = 201;
    short CAN_REVERSE_LOAN_DISBURSAL = 202;

    short CONFIGURATION_MANAGEMENT = 203;

    // Remove Group MemberShip
    short CAN_REMOVE_CLIENTS_FROM_GROUPS = 206;

    short CAN_VIEW_DETAILED_AGING_PORTFOLIO_AT_RISK = 236;

    short CAN_VIEW_GENERAL_LEDGER = 237;

    short CAN_VIEW_COLLECTION_SHEET_REPORT = 229;

    // Add Group MemberShip
    short CAN_ADD_CLIENTS_TO_GROUPS = 208;

    // Limiting product combination
    short PRODUCT_MIX = 209;

    short CAN_VIEW_ACTIVE_LOANS_BY_LOAN_OFFICER = 212;

    short CAN_DEFINE_LOOKUP_OPTIONS = 213;

    short UPLOAD_REPORT_TEMPLATE = 214;

    short VIEW_REPORTS = 215;

    short EDIT_REPORT_INFORMATION = 216;

    short CAN_ADJUST_PAYMENT_WHEN_OBLIGATION_MET = 217;

    short CAN_REDO_LOAN_DISPURSAL = 218;

    short DEFINE_REPORT_CATEGORY = 220;

    short VIEW_REPORT_CATEGORY = 221;

    short DELETE_REPORT_CATEGORY = 222;

    short DOWNLOAD_REPORT_TEMPLATE = 223;

    short CAN_UPLOAD_ADMIN_DOCUMENTS = 225;

    /**
     * This permission is used for multiple operations:
     * <ul>
     * <li>listing all available admin documents. From the UI: "Admin->View admin documents"
     * <li>running/executing an admin document when it shows up during a particular loan or savings account state
     * </ul>
     * 
     * This was done to fix the issue quickly and since the operations seem to have reasonable overlap.
     * 
     */
    short CAN_VIEW_ADMIN_DOCUMENTS = 226;

    // FIXME: remove this; CONFIGURATION_MANAGEMENT should be parent for
    // CAN_VIEW_SYSTEM_INFO
    short SYSTEM_INFORMATION = 227;

    /** Can view install-time configuration settings. */
    short CAN_VIEW_ORGANIZATION_SETTINGS = 230;

    short CAN_VIEW_BRANCH_CASH_CONFIRMATION_REPORT = 231;

    short CAN_VIEW_BRANCH_REPORT = 232;

    short CAN_IMPORT_TRANSACTIONS = 233;

    /** Can define new holidays */
    short CAN_DEFINE_HOLIDAY = 235;

    /** Can shutdown Mifos */
    short CAN_OPEN_SHUTDOWN_PAGE = 234;
    short CAN_VIEW_ACTIVE_SESSIONS = 238;
    short CAN_SHUT_DOWN_MIFOS = 239;

    /** Batch Jobs */
    short CAN_RUN_BATCH_JOBS_ON_DEMAND = 241;
    short CAN_UPDATE_BATCH_JOBS_CONFIGURATION = 242;

    /** Question Groups */
    short CAN_MANAGE_QUESTION_GROUPS = 240;
    short CAN_ACTIVATE_QUESTION_GROUPS = 243;

    /** Penalties */
    short LOAN_REMOVE_PENALTY_TYPE_ATTACHED_TO_ACCOUNT = 249;
}
