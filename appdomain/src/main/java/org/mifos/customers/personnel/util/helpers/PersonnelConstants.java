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

package org.mifos.customers.personnel.util.helpers;

/**
 * This is the public interface that holds the constants for the personnel
 */
public interface PersonnelConstants {
    String PERSONNEL = "Personnel";
    String PERSONNEL_VO = "PersonnelVO";
    String PERSONNEL_NOTES_VO = "PersonnelNotesVO";
    String GENDER_LIST = "genderList";
    String MARITAL_STATUS_LIST = "maritalStatusList";
    String LANGUAGE_LIST = "languageList";
    String SITE_TYPE_PREFERRED = "siteTypePreferred";
    String SITE_TYPES_LIST = "siteTypesList";
    String TITLE_LIST = "titleList";
    String CUSTOM_FIELDS = "customFields";
    String DISPLAY_ADDRESS = "displayAddress";
    String PERSONNEL_LEVEL_LIST = "personnelLevelList";
    String PERSONNEL_NOTES_ACTION = "PersonnelNotesAction";
    String PERSONNEL_NOTES = "PersonnelNotes";
    String ROLES_LIST = "rolesList";
    String OFFICE_LIST = "officeList";
    String OFFICE = "office";
    String PERSONNEL_ROLES_LIST = "personnelRolesList";
    short PERSONNEL_CUSTOM_FIELD_ENTITY_TYPE = 17;
    String PERSONNEL_OFFICE = "personnelOffice";
    String OLD_PERSONNEL = "oldPersonnel";
    String METHOD_PREVIEW = "preview";
    String METHOD_PREVIOUS = "previous";
    String METHOD_UPDATE = "previous";
    String METHOD_GET_DETAILS = "getDetails";
    String METHOD_LOAD = "load";
    String LOAD_SUCCESS = "load_success";
    String MANAGE_SUCCESS = "manage_success";
    String GET_SUCCESS = "get_success";
    String GETDETAILS_SUCCESS = "getDetails_success";
    String METHOD_CANCEL = "cancel";
    String METHOD_SEARCH_NEXT = "searchNext";
    String METHOD_SEARCH = "search";
    String METHOD_SEARCH_PREV = "searchPrev";
    String METHOD_MANAGE = "manage";
    String METHOD_GET = "get";
    String METHOD_CREATE = "create";
    String METHOD_EDIT_PERSONAL_INFO = "editPersonalInfo";
    String METHOD_PREV_PERSONAL_INFO = "prevPersonalInfo";
    String METHOD_PREVIEW_PERSONAL_INFO = "previewPersonalInfo";
    String METHOD_CHOOSE_OFFICE = "chooseOffice";
    String METHOD_UPDATE_SETTINGS = "updateSettings";
    String METHOD_LOAD_UNLOCK_USER = "loadUnLockUser";
    String METHOD_LOAD_CHANGE_PASSWORD = "loadChangePassword";
    String LOAD_UNLOCK_USER_SUCCESS = "loadUnLockUser_success";
    String EDIT_PERSONAL_INFO_SUCCESS = "editPersonalInfo_success";
    String PREVIEW_PERSONAL_INFO_SUCCESS = "previewPersonalInfo_success";
    String LOAD_CHANGE_PASSWORD_SUCCESS = "loadChangePassword_success";
    String PREVIEW_PERSONAL_INFO_FAILURE = "previewPersonalInfo_failure";

    String UPDATE_SETTINGS_SUCCESS = "updateSettings_success";
    String METHOD_UNLOCK_USER_ACCOUNT = "unLockUserAccount";
    String METHOD_LOAD_SEARCH = "loadSearch";
    String LOAD_SEARCH_SUCCESS = "loadSearch_success";
    String CHOOSE_OFFICE_SUCCESS = "chooseOffice_success";
    String INPUT_PAGE = "inputPage";
    String PERSONNEL_AGE = "personnelAge";
    String PASSWORD = "password";
    // names for master data
    String LANGUAGE_NAME = "languageName";
    String LOANOFFICERACTIVE = "ACTIVE";

    // perosnnel status
    Short ACTIVE = 1;
    Short INACTIVE = 2;
    Short LOAN_OFFICER = 1;
    Short NON_LOAN_OFFICER = 2;
    String CURRENT_STATUS = "currentStatus";
    String STATUS = "status";
    String STATUS_LIST = "statusList";
    String ROLES_TO_DELETE = "rolesToDelete";

    // transfer
    String TRANSFER = "transfer";
    // input pages
    String CREATE_USER = "CreateUser";
    String MANAGE_USER = "ManageUser";
    String UNLOCK_USER = "UnLockUser";
    String PREVIEW_CREATE_USER = "PreviewCreateUser";
    String PREVIEW_MANAGE_USER = "PreviewManageUser";
    String CREATE_USER_FAILURE = "CreateUserFaliuer";
    String MANAGE_USER_FAILURE = "ManageUserFaliuer";
    String USER_DETAILS_PAGE = "UserDetailsPage";
    String USER_CHANGE_LOG = "UserChangeLog";
    String USER_CHANGE_LOG_LIST = "UserChangeLogList";
    String ADMIN_PAGE = "AdminPage";

    short NOTES_COUNT = 3;
    String NOTES = "notes";
    String GENDER = "Gender";
    String MARITAL_STATUS = "Marital Status";
    String MARITALSTATUS = "MaritalStatus";
    String LANGUAGE = "Languages";
    String USERLEVLELS = "User Levels";
    String ROLEMASTERLIST = "Roles";
    String USER_LIST = "UserList";
    short PERSONNEL_ENTITY_TYPE = 17;
    String LOGIN_ATTEMPTS_COUNT = "loginAttemptsCount";
    // error messages
    String INVALID_VERSION = "error.invalidversion";
    String DUPLICATE_USER = "error.duplicateuser";
    String USER_NOT_FOUND = "error.usernotfound";
    String UNKNOWN_EXCEPTION = "error.unknownexception";
    String HIERARCHY_CHANGE_EXCEPTION = "error.hierarchychange";
    String STATUS_CHANGE_EXCEPTION = "error.userstatuschange";
    String INACTIVE_BRANCH = "error.inactivebranch";
    String TRANSFER_NOT_POSSIBLE_EXCEPTION = "error.usertransfer";
    String CREATE_FAILED = "error.createfailed";
    String UPDATE_FAILED = "error.updatefailed";
    String INVALID_DOB = "error.invaliddob";
    String INVALID_MFIDATE = "error.invalidmfidate";
    String DUPLICATE_GOVT_ID = "error.duplicategovtid";
    String DUPLICATE_USER_NAME_OR_DOB = "error.duplicate_username_or_dob";
    String LO_ONLY_IN_BRANCHES = "error.lo_only_in_branches";
    String NO_SEARCH_STRING = "error.nosearchstring";
    String PASSWORD_MASK = "errors.spacesmask";
    String USER_CHANGE_LOG_ERROR = "error.userchangelog";
    String ERROR_MASTERDATA = "error.masterdata";

    // M2 Constants

    String ERRORMANDATORY = "errors.mandatory";
    String USERNAME = "Personnel.UserNameLabel";
    String VALID_PASSWORD = "errors.validpassword";
    String ERROR_MANDATORY_TEXT_AREA = "errors.mandatorytextarea";
    short COMMENT_LENGTH = 500;
    String MAXIMUM_LENGTH = "errors.maxlength";
    String MAXIMUMLENGTH = "errors.maximumlength";
    String MANDATORYSELECT = "errors.mandatorySelect";
    String GENDERVALUE = "gender";
    String FIRSTNAME = "firstName";
    String LASTNAME = "lastName";
    String FIRST_NAME = "first name";
    String LAST_NAME = "last name";
    String PERSONNELNAMELENGTH = "100";
    short PERSONNELLENGTH = 100;
    String PERSONNELDISPLAYLENGTH = "200";
    short PERSONNELDISPLAYNAMELENGTH = 200;
    String DISPLAY_NAME = "display name";
    String DISPLAYNAME = "displayName";

    String ERROR_FIRSTNAME = "error.firstName";
    String ERROR_LASTNAME = "error.lastName";
    String ERROR_GENDER = "error.gender";
    String ERROR_EMAIL = "error.email";
    String ERROR_VALID_EMAIL = "error.vaildEmail";
    String ERROR_LEVEL = "error.level";
    String ERROR_USER_NAME = "error.username";
    String INVALID_USER_NAME = "error.invalidusername";
    String ERROR_DOB = "error.dob";
    String ERROR_MFIDATE = "error.mfidate";
    String ERROR_PASSWORD_LENGTH = "error.passwordLength";
    String ERROR_CUSTOMfIELD = "error.customfield";
    String ERROR_CUSTOMDATEFIELD = "error.customdatefield";
    String ERROR_STATUS = "errors.status";

    /**
     * This is a user which we set up as part of master data. It is used a lot
     * for tests, but also certain batch jobs specify this user.
     */
    Short SYSTEM_USER = 1;
    Short TEST_USER = 3;

}
