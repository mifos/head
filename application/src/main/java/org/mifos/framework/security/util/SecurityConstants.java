/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.framework.security.util;

/**
 * This inteface will hold all the constants used related to the security
 * 
 * NOTE: if a new activity is being added, the values in RoleTestUtil need to be
 * increased.
 */
public interface SecurityConstants {
    public final String NOSUCHALGORITHM = "security.error.noSuchAlgorithm";

    public final String NOSUCHPADDING = "security.error.noSuchPadding";

    public final String ENCODINGNOTSUPPORTED = "security.error.encodingNotSupported";

    public final String PBEWITHMD5ANDDES = "PBEWithMD5AndDES";

    public final String DESEDE = "DESede";

    public final String DES = "DES";

    public final String INITIALIZATIONFAILED = "security.error.iniFailed";

    public final String GENERALERROR = "security.error.generalError";

    public final int MAXTRIES = 5;

    public final String ONE = "1";

    public final String PATTERN = "pattern";

    public final String ID = "ID";

    public final String OFFICECHANGEEVENT = "OfficeChange";

    public final String SECURITY_PARAM = "security_param";

    // Activity not allowd key

    public final String KEY_ACTIVITY_NOT_ALLOWED = "error.activity.not.allowed";

    // activity related mappeings
    public final short VIEW = 0;

    public final short SEARCH = 0;

    /*
     * Constants for activity IDs.
     */
    public final short ORGANIZATION_MANAGEMENT = 1;

    // funds
    public final short FUNDS_CREATE_FUNDS = 3;

    public final short FUNDS_EDIT_FUNDS = 4;

    // fees
    public final short FEES_CREATE_FEES = 6;

    public final short FEES_EDIT_FEES = 7;

    // checklist
    public final short CHECKLIST_CREATE_CHECKLIST = 9;

    public final short CHECKLIST_EDIT_CHECKLIST = 10;

    // Miscellaneous
    public final short DEFINE_NEW_DATA_FIELDS = 12;

    public final short EDIT_DATA_FIELDS = 12;

    // Office related activities
    public final short OFFICE_CREATE_OFFICE = 15;

    public final short OFFICE_EDIT_OFFICE = 16;

    public final short OFFICE_HIERATCHY_UPDATE = 193;

    // User management
    public final short PERSONNEL_CREATE_PERSONNEL = 19;

    public final short PERSONNEL_EDIT_PERSONNEL = 20;

    public final short PERSONNEL_NOTE_CREATE = 194;

    public final short PERSONNEL_UNLOCK_PERSONNEL = 21;

    public final short ROLES_CREATE_ROLES = 23;

    public final short ROLES_EDIT_ROLES = 24;

    public final short ROLES_DELETE_ROLES = 25;

    // Client attributes management
    public final short EDIT_CLIENTNAME_HIERARCHY_GROUPLOANALLOWED = 28;

    public final short CREATE_ADMINISTRATIVEFEEFOR_CLIENTS_GROUPS_CENTERS = 30;

    public final short CONFIGURE_HIDE_UNHIDE_DATAFIELDS_FOR_GROUP_CENTER = 32;

    // Client management
    public final short CLIENT_MANAGEMENT = 33;
    public final short CLIENTS = 34;

    public final short CLIENT_ADD_NOTE_TO_CLIENT = 48;

    public final short CIENT_CREATE_NEW_CLIENT_IN_SAVE_FOR_LATER_STATE = 35;

    public final short CIENT_CREATE_NEW_CLIENT_IN_SUBMIT_FOR_APPROVAL_STATE = 36;

    public final short CIENT_CHANGE_STATE_TO_SAVE_FOR_LATER_AND_SUBMIT_FOR_APPROVAL_STATE = 37;

    public final short CIENT_CHANGE_STATE_TO_APPROVAD = 38;

    public final short CIENT_CHANGE_STATE_TO_CANCELLED = 39;

    public final short CIENT_CHANGE_STATE_TO_ON_HOLD = 40;

    public final short CIENT_CHANGE_STATE_TO_ON_CLOSED = 41;

    public final short CIENT_APPLY_CHARGES_TO_CLIENT_ACCOUNT = 42;

    public final short CIENT_MAKE_PAYMENT_TO_CLIENT_ACCOUNT = 43;

    public final short CIENT_MAKE_ADJUSTMENT_ENTRIES_TO_CLIENT_ACCOUNT = 44;

    public final short CIENT_WAIVE_DUE_AMOUNT = 46;

    public final short CIENT_REMOVE_FEE_TYPE_FROM_CLIENT_ACCOUNT = 47;

    public final short CIENT_ADD_NOTES_TO_ACCOUNT = 48;

    public final short CIENT_EDIT_MFI_INFORMATION = 49;

    public final short CIENT_CHANGE_GROUP_MEMBERSHIP = 50;

    public final short CIENT_TRANSFER_THE_CLIENT = 51;

    public final short CIENT_EDIT_MEETINGSCHEDULE = 52;

    public final short CIENT_ADD_EDIT_HISTORICAL_DATA = 53;

    public final short CIENT_EDIT_FEE_TYPE_ATTACHED_TO_ACCOUNT = 54;

    public final short CIENT_BLACKLIST_A_CLIENT = 55;

    // Group management
    public final short GROUP_ADD_NOTE_TO_GROUP = 70;

    public final short GROUP_CREATE_NEW_GROUP_IN_SAVE_FOR_LATER_STATE = 57;

    public final short GROUP_CREATE_NEW_GROUP_IN_SUBMIT_FOR_APPROVAL_STATE = 58;

    public final short GROUP_CHANGE_STATE_TO_SAVE_FOR_LATER_AND_SUBMIT_FOR_APPROVAL_STATE = 59;

    public final short GROUP_CHANGE_STATE_TO_APPROVAD = 60;

    public final short GROUP_CHANGE_STATE_TO_CANCELLED = 61;

    public final short GROUP_CHANGE_STATE_TO_ON_HOLD = 62;

    public final short GROUP_CHANGE_STATE_TO_ON_CLOSED = 63;

    public final short GROUP_APPLY_CHARGES_TO_GROUP_ACCOUNT = 64;

    public final short GROUP_MAKE_PAYMENT_TO_GROUP_ACCOUNT = 65;

    public final short GROUP_MAKE_ADJUSTMENT_ENTRIES_TO_GROUP_ACCOUNT = 66;

    public final short GROUP_WAIVE_DUE_AMOUNT = 68;

    public final short GROUP_REMOVE_FEE_TYPE_FROM_GROUP_ACCOUNT = 69;

    public final short GROUP_ADD_NOTES_TO_ACCOUNT = 70;

    public final short GROUP_EDIT_GROUP = 71;

    public final short GROUP_CHANGE_CENTER_MEMBERSHIP = 72;

    public final short GROUP_TRANSFER_THE_GROUP = 73;

    public final short GROUP_EDIT_MEETING_SCHEDULE = 74;

    public final short GROUP_ADD_EDIT_HISTORICAL_DATA = 75;

    public final short GROUP_EDIT_FEE_TYPE_ATTACHED_TO_ACCOUNT = 76;

    public final short GROUP_BLACKLIST_A_GROUP = 77;

    // Center
    public final short CENTER_ADD_NOTE_TO_CENTER = 87;

    public final short CENTER_CREATE_NEW_CENTER = 79;

    public final short CENTER_MODIFY_CENTER_INFORMATION_AND_CHANGE_CENTER_STATUS = 80;

    public final short CENTER_MAKE_PAYMENTS_TO_CENTER_ACCOUNT = 82;

    public final short CENTER_MAKE_ADJUSTMENT_ENTRIES_TO_CENTER_ACCOUNT = 83;

    public final short CENTER_WAIVE_DUE_AMOUNT = 85;

    public final short CENTER_REMOVE_FEE_TYPE_FROM_CENTER_ACCOUNT = 86;

    public final short CENTER_ADD_NOTES_TO_ACCOUNT = 87;

    public final short CENTER_EDIT_FEE_TYPE_ATTACHED_TO_ACCOUNT = 88;

    public final short PRODUCT_DEFINITION = 89;

    public final short CENTER_EDIT_STATUS = 81;

    public final short CENTER_EDIT_CENTER_STATUS = 186;

    // Product definition
    public final short DEFINE_NEW_PRODUCT_CATEGORIES = 91;

    public final short EDIT_PRODUCT_CATEGORIES = 92;

    public final short UPDATE_LATENESS_DORMANCY = 195;

    // loan product
    public final short DEFINE_NEW_LOAN_PRODUCT_INSTANCE = 94;

    public final short EDIT_LOAN_PRODUCT = 95;

    // saving product
    public final short DEFINE_NEW_SAVING_PRODUCT_INSTANCE = 97;

    public final short EDIT_SAVING_PRODUCT = 98;
    public final short LOAN_MANAGEMENT = 99;

    // Loan management
    public final short LOAN_CREATE_LOAN_ACCOUNT_IN_SAVE_FOR_LATER_STATE = 101;

    public final short LOAN_CREATE_LOAN_ACCOUNT_IN_SUBMIT_FOR_APPROVAL_STATE = 102;

    public final short LOAN_CHANGE_STATE_TO_SAVE_FOR_LATER_AND_SUBMIT_FOR_APPROVAL = 103;

    public final short LOAN_CHANGE_STATE_TO_APPROVAD = 104;

    public final short LOAN_CHANGE_STATE_TO_CANCELLED = 105;

    public final short LOAN_CHANGE_STATE_TO_DISBURSED_TO_LOAN_OFFICER = 106;

    public final short LOAN_CHANGE_STATE_TO_ACTIVE_IN_GOOD_STANDING = 107;

    public final short LOAN_CHANGE_STATE_TO_IN_ARREARS = 108;

    public final short LOAN_CHANGE_STATE_TO_CLOSED_WRITTEN_OFF = 109;

    public final short LOAN_CHANGE_STATE_TO_CLOSED_RESCHEDULE = 110;

    public final short LOAN_CHANGE_STATE_TO_CLOSED_OBLIGATION_MET = 111;

    public final short LOAN_MODIFY_THE_DEFAULTS_AMOUNT = 112;

    // savings
    public final short SAVINGS_APPLY_ADJUSTMENT = 186;

    // Loan Transactions
    public final short LOAN_CAN_ADD_NOTES_TO_LOAN = 179;

    public final short LOAN_APPLY_FEE_MISC_CHARGES_TO_ACCOUNT = 114;

    public final short LOAN_MAKE_PAYMENT_TO_ACCOUNT = 115;

    public final short LOAN_MAKE_ADJUSTMENT_ENTRY_TO_ACCOUNT = 116;

    public final short LOAN_EDIT_ACCOUNT_INFORMATION = 117;

    public final short LOAN_WAIVE_PANELTY = 118;

    public final short LOAN_WAIVE_FEE_INSTALLMENT = 119;

    public final short LOAN_REMOVE_FEE_TYPE_ATTACHED_TO_ACCOUNT = 120;

    public final short GROUP_UPDATE_STATUS = 121;

    public final short GROUP_CREATE_GROUP = 122;

    public final short CUSTOMER_ADD_NOTES_TO_CENTER_GROUP_CLIENT = 123;

    public final short CUSTOMER_ADD_HISTORICAL_DATA_TO_CENTER_GROUP_CLIENT = 124;

    public final short CLIENT_CREATE_CLIENT = 125;

    public final short CLIENT_UPDATE_PERSONNEL_INFO = 126;

    public final short CLIENT_UPDATE_STATUS = 127;

    public final short LOAN_CREATE_LOAN = 128;

    public final short LOAN_UPDATE_LOAN = 129;

    public final short LOAN_UPDATE_LOAN_STATUS = 130;

    public final short APPLY_CHARGES_TO_CLIENT_GROUP_CENTERS_LOANS = 131;

    public final short APPLY_PAYMENT_TO_CLIENT_GROUP_CENTERS_LOANS = 132;

    public final short MEETING_UPDATE_MEETING = 133;

    public final short MEETING_CREATE_MEETING = 134;

    public final short MEETING_CREATE_CLIENT_MEETING = 121;

    public final short MEETING_UPDATE_CLIENT_MEETING = 52;

    public final short MEETING_CREATE_GROUP_MEETING = 122;

    public final short MEETING_UPDATE_GROUP_MEETING = 74;

    public final short MEETING_CREATE_CENTER_MEETING = 128;

    public final short MEETING_UPDATE_CENTER_MEETING = 127;

    public final short PERSONNEL_EDIT_SELF_INFO = 135;

    public final short SAVINGS_CREATE_SAVINGS = 137;

    public final short SAVINGS_UPDATE_SAVINGS = 138;

    public final short SAVINGS_CLOSE_SAVINGS_ACCOUNT = 139;

    public final short SAVINGS_CHANGE_STATUS = 140;

    public final short ADMINISTER_REPORTS = 142;

    public final short ANALYSIS = 150;

    public final short CLIENTSDETAILVIEW = 152;

    public final short CLIENTSPRODUCTHISTORY = 153;

    public final short BRANCHPERFORMANCE = 164;

    public final short AREAPERFORMANCE = 165;

    public final short COLLECTIONSHEET = 159;

    public final short LOANDISTRIBUTION = 172;

    public final short BRANCHDISBURSEMENT = 173;

    public final short STAFFWISEREPORT = 160;

    public final short BRANCHWISEREPORT = 161;

    public final short CAN_VIEW_DAILY_PORTFOLIO = 176;

    public final short KENDRA_MEETING = 177;

    public final short LOAN_CAN_REPAY_LOAN = 178;

    public final short SAVINGS_CANWAIVE_DUEAMOUNT = 187;

    public final short SAVINGS_CANWAIVE_OVERDUEAMOUNT = 188;

    public final short LOAN_CAN_DISBURSE_LOAN = 189;

    public final short SAVINGS_CAN_MAKE_DEPOSIT_WITHDRAWAL = 190;

    public final short SAVINGS_CAN_ADD_NOTES_TO_SAVINGS = 191;

    public final short CAN_APPROVE_LOANS_IN_BULK = 192;

    public final short CAN_ENTER_COLLECTION_SHEET_DATA = 197;

    public final short REPORTS_MANAGEMENT = 141;

    // FIXME: both use the same ID? Is this a mistake?
    public final short ADMINISTER_REPORTPARAMS = 142;
    public final short ADMINISTER_REPORTDS = 142;

    public final short LOAN_CAN_APPLY_CHARGES = 131;

    /**
     * What is this? It is the parent of
     * {@link #CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS} but I'm not sure why it didn't
     * have its own constant until now.
     */
    public final short BULK = 196;

    public final short CLIENT_CAN_APPLY_CHARGES = 198;
    public final short GROUP_CAN_APPLY_CHARGES = 199;
    public final short CENTER_CAN_APPLY_CHARGES = 200;

    public final short CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS = 201;
    public final short CAN_REVERSE_LOAN_DISBURSAL = 202;

    public final short CONFIGURATION_MANAGEMENT = 203;
    public final short CAN_DEFINE_LABELS = 204;

    public final short CAN_DEFINE_HIDDEN_MANDATORY_FIELDS = 205;

    // Remove Group MemberShip
    public final short CAN_REMOVE_CLIENTS_FROM_GROUPS = 206;

    public final short CAN_VIEW_DETAILED_AGING_OF_PORTFOLIO_AT_RISK = 207;

    public final short CAN_VIEW_COLLECTION_SHEET_REPORT = 229;

    // Add Group MemberShip
    public final short CAN_ADD_CLIENTS_TO_GROUPS = 208;

    // Limiting product combination
    public final short PRODUCT_MIX = 209;

    public final short CAN_DEFINE_PRODUCT_MIX = 210;

    public final short CAN_EDIT_PRODUCT_MIX = 211;

    public final short CAN_VIEW_ACTIVE_LOANS_BY_LOAN_OFFICER = 212;

    public final short CAN_DEFINE_LOOKUP_OPTIONS = 213;

    public final short UPLOAD_REPORT_TEMPLATE = 214;

    public final short VIEW_REPORTS = 215;

    public final short EDIT_REPORT_INFORMATION = 216;

    public final short CAN_ADJUST_PAYMENT_WHEN_OBLIGATION_MET = 217;

    public final short CAN_REDO_LOAN_DISPURSAL = 218;

    public final short CAN_DEFINE_ACCEPTED_PAYMENT_TYPE = 219;

    public final short DEFINE_REPORT_CATEGORY = 220;

    public final short VIEW_REPORT_CATEGORY = 221;

    public final short DELETE_REPORT_CATEGORY = 222;

    public final short DOWNLOAD_REPORT_TEMPLATE = 223;

    public final short CAN_DEFINE_CUSTOM_FIELD = 224;

    public final short CAN_UPLOAD_ADMIN_DOCUMENTS = 225;

    /**
     * This permission is used for multiple operations:
     * <ul>
     * <li>listing all available admin documents. From the UI: "Admin->View
     * admin documents"
     * <li>running/executing an admin document when it shows up during a
     * particular loan or savings account state
     * </ul>
     * 
     * This was done to fix the issue quickly and since the operations seem to
     * have reasonable overlap.
     * 
     */
    public final short CAN_VIEW_ADMIN_DOCUMENTS = 226;

    // FIXME: remove this; CONFIGURATION_MANAGEMENT should be parent for
    // CAN_VIEW_SYSTEM_INFO
    public final short SYSTEM_INFORMATION = 227;

    /** Can view database driver version, svn build revision, etc. */
    public final short CAN_VIEW_SYSTEM_INFO = 228;

    /** Can view install-time configuration settings. */
    public final short CAN_VIEW_ORGANIZATION_SETTINGS = 230;

    public final short CAN_VIEW_BRANCH_CASH_CONFIRMATION_REPORT = 231;

    public final Short CAN_VIEW_BRANCH_REPORT = 232;
}
