/**
 
 * PersonnelConstants.java    version: 1.0
 
 
 
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
package org.mifos.application.personnel.util.helpers;

/**
 * This is the interface that holds the constants for the personnel
 */
public interface PersonnelConstants {
	public static final String PERSONNEL  ="Personnel";
	public static final String PERSONNEL_VO  ="PersonnelVO";
	public static final String PERSONNEL_NOTES_VO  ="PersonnelNotesVO";
	public static final String GENDER_LIST  ="genderList";
	public static final String MARITAL_STATUS_LIST = "maritalStatusList";
	public static final String LANGUAGE_LIST = "languageList";
	public static final String TITLE_LIST = "titleList";
	public static final String CUSTOM_FIELDS  ="customFields";
	public static final String DISPLAY_ADDRESS  ="displayAddress";
	public static final String PERSONNEL_LEVEL_LIST = "personnelLevelList";
	public static final String PERSONNEL_NOTES_ACTION="PersonnelNotesAction";
	public static final String PERSONNEL_NOTES="PersonnelNotes";
	public static final String ROLES_LIST="rolesList";
	public static final String OFFICE_LIST="officeList";
	public static final String OFFICE="office";
	public static final String PERSONNEL_ROLES_LIST="personnelRolesList";
	public static final short  PERSONNEL_CUSTOM_FIELD_ENTITY_TYPE=17;
	public static final String PERSONNEL_OFFICE="personnelOffice";
	public static final String OLD_PERSONNEL="oldPersonnel";
	public static final String METHOD_PREVIEW="preview";
	public static final String METHOD_PREVIOUS="previous";
	public static final String METHOD_UPDATE="previous";
	public static final String METHOD_GET_DETAILS="getDetails";
	public static final String METHOD_LOAD="load";
	public static final String LOAD_SUCCESS="load_success";
	public static final String MANAGE_SUCCESS="manage_success";
	public static final String GET_SUCCESS="get_success";
	public static final String GETDETAILS_SUCCESS="getDetails_success";
	public static final String METHOD_CANCEL="cancel";
	public static final String METHOD_SEARCH_NEXT="searchNext";
	public static final String METHOD_SEARCH="search";
	public static final String METHOD_SEARCH_PREV="searchPrev";
	public static final String METHOD_MANAGE="manage";
	public static final String METHOD_GET="get";
	public static final String METHOD_CREATE="create";
	public static final String METHOD_EDIT_PERSONAL_INFO="editPersonalInfo";
	public static final String METHOD_PREV_PERSONAL_INFO="prevPersonalInfo";
	public static final String METHOD_PREVIEW_PERSONAL_INFO="previewPersonalInfo";
	public static final String METHOD_CHOOSE_OFFICE="chooseOffice";
	public static final String METHOD_UPDATE_SETTINGS="updateSettings";
	public static final String METHOD_LOAD_UNLOCK_USER="loadUnLockUser";
	public static final String METHOD_LOAD_CHANGE_PASSWORD="loadChangePassword";
	public static final String LOAD_UNLOCK_USER_SUCCESS="loadUnLockUser_success";
	public static final String EDIT_PERSONAL_INFO_SUCCESS="editPersonalInfo_success";
	public static final String PREVIEW_PERSONAL_INFO_SUCCESS="previewPersonalInfo_success";
	public static final String LOAD_CHANGE_PASSWORD_SUCCESS="loadChangePassword_success";
	public static final String PREVIEW_PERSONAL_INFO_FAILURE="previewPersonalInfo_failure";

	public static final String UPDATE_SETTINGS_SUCCESS="updateSettings_success";
	public static final String METHOD_UNLOCK_USER_ACCOUNT="unLockUserAccount";
	public static final String METHOD_LOAD_SEARCH="loadSearch";
	public static final String LOAD_SEARCH_SUCCESS="loadSearch_success";
	public static final String CHOOSE_OFFICE_SUCCESS="chooseOffice_success";
	public static final String INPUT_PAGE="inputPage";
	public static final String PERSONNEL_AGE="personnelAge";
	public static final String PASSWORD="password";
	//names for master data
	public static final String LANGUAGE_NAME="languageName";
	public static final String LOANOFFICERACTIVE="ACTIVE";
	
	//perosnnel status
	public static final Short ACTIVE=1;
	public static final Short INACTIVE=2;
	public static final Short LOAN_OFFICER=1;
	public static final Short NON_LOAN_OFFICER=2;
	public static final String CURRENT_STATUS="currentStatus";
	public static final String STATUS="status";
	public static final String STATUS_LIST="statusList";
	public static final String ROLES_TO_DELETE="rolesToDelete";
	
	//transfer
	public static final String TRANSFER="transfer";
	//input pages
	public static final String CREATE_USER="CreateUser";
	public static final String MANAGE_USER="ManageUser";
	public static final String UNLOCK_USER="UnLockUser";
	public static final String PREVIEW_CREATE_USER="PreviewCreateUser";
	public static final String PREVIEW_MANAGE_USER="PreviewManageUser";
	public static final String CREATE_USER_FAILURE="CreateUserFaliuer";
	public static final String MANAGE_USER_FAILURE="ManageUserFaliuer";
	public static final String USER_DETAILS_PAGE="UserDetailsPage";
	public static final String USER_CHANGE_LOG="UserChangeLog";
	public static final String USER_CHANGE_LOG_LIST="UserChangeLogList";
	public static final String ADMIN_PAGE="AdminPage";
	
	public static final short NOTES_COUNT=3;
	public static final String NOTES="notes";
	public static final String GENDER ="Gender";
	public static final String MARITAL_STATUS ="Marital Status";
	public static final String MARITALSTATUS ="MaritalStatus";
	public static final String LANGUAGE  ="Languages";
	public static final String USERLEVLELS  ="User Levels";
	public static final String ROLEMASTERLIST  ="Roles";
	public static final String USER_LIST="UserList";
	public static final short PERSONNEL_ENTITY_TYPE=17;
	public static final String LOGIN_ATTEMPTS_COUNT="loginAttemptsCount";
	//error messages
	public static final String INVALID_VERSION="error.invalidversion";
	public static final String DUPLICATE_USER="error.duplicateuser";
	public static final String USER_NOT_FOUND="error.usernotfound";
	public static final String UNKNOWN_EXCEPTION="error.unknownexception";
	public static final String HIERARCHY_CHANGE_EXCEPTION="error.hierarchychange";
	public static final String STATUS_CHANGE_EXCEPTION="error.userstatuschange";
	public static final String INACTIVE_BRANCH="error.inactivebranch";
	public static final String TRANSFER_NOT_POSSIBLE_EXCEPTION="error.usertransfer";
	public static final String CREATE_FAILED="error.createfailed";
	public static final String UPDATE_FAILED="error.updatefailed";
	public static final String INVALID_DOB="error.invaliddob";
	public static final String DUPLICATE_GOVT_ID="error.duplicategovtid";
	public static final String DUPLICATE_USER_NAME_OR_DOB="error.duplicate_username_or_dob";
	public static final String LO_ONLY_IN_BRANCHES="error.lo_only_in_branches";
	public static final String NO_SEARCH_STRING="error.nosearchstring";
	public static final String PASSWORD_MASK="errors.spacesmask";
	public static final String USER_CHANGE_LOG_ERROR="error.userchangelog";
	public static final String ERROR_MASTERDATA="error.masterdata";
	
	//M2 Constants
	public static final String PERSONNELUIRESOURCESPATH="org/mifos/application/personnel/util/resources/PersonnelUIResources";
	public static final String ERRORMANDATORY="errors.mandatory";
	public static final String  USERNAME="Personnel.UserNameLabel";
	public static final String VALID_PASSWORD = "errors.validpassword";
	public static final String ERROR_MANDATORY_TEXT_AREA = "errors.mandatorytextarea";
	public static final short COMMENT_LENGTH = 500;
	public static final String MAXIMUM_LENGTH="errors.maxlength";
	public static final String MAXIMUMLENGTH="errors.maximumlength";
	public static final String MANDATORYSELECT="errors.mandatorySelect";
	public static final String GENDERVALUE ="gender";
	public static final String FIRSTNAME ="firstName";
	public static final String LASTNAME ="lastName";
	public static final String FIRST_NAME ="first name";
	public static final String LAST_NAME ="last name";
	public static final String PERSONNELNAMELENGTH ="100";
	public static final short PERSONNELLENGTH =100;
	public static final String PERSONNELDISPLAYLENGTH ="200";
	public static final short PERSONNELDISPLAYNAMELENGTH =200;
	public static final String DISPLAY_NAME ="display name";
	public static final String DISPLAYNAME ="displayName";
	
	public static final String ERROR_FIRSTNAME="error.firstName";
	public static final String ERROR_LASTNAME="error.lastName";
	public static final String ERROR_GENDER="error.gender";
	public static final String ERROR_EMAIL="error.email";
	public static final String ERROR_VALID_EMAIL="error.vaildEmail";
	public static final String ERROR_LEVEL="error.level";
	public static final String ERROR_USER_NAME="error.username";
	public static final String ERROR_DOB="error.dob";
	public static final String ERROR_PASSWORD_LENGTH="error.passwordLength";
	
	public static final String ERROR_STATUS="errors.status";
	public static final String ERROR_NO_LEVEL="error.nolevel";
	
}
