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

package org.mifos.customers.client.util.helpers;

import org.mifos.customers.client.business.NameType;

public interface ClientConstants {

    /** Forwards */
    String PRELOAD_SUCCESS = "preLoad_success";
    String NEXT_FAILURE = "next_failure";

    /***/
    short CLIENT_BELONGS_TO_GROUP = 1;
    int PICTURE_ALLOWED_SIZE = 300000;
    short All_CATEGORY_ID = 1;
    short CLIENT_CATEGORY_ID = 2;
    short CLIENT_ENTITY_TYPE = 1;
    short LOAN_OFFICER_LEVEL = 1;

    Short CLIENT_NAME_TYPE = NameType.CLIENT.getValue();
    Short SPOUSE_NAME_TYPE = NameType.SPOUSE.getValue();

    String CLIENTVO = "clientVO";
    /** Request parameter names */
    String SALUTATION_ENTITY = "salutationEntity";
    String GENDER_ENTITY = "genderEntity";
    String IS_PHOTO_FIELD_HIDDEN = "isPhotoFieldHidden";
    String LIVING_STATUS_ENTITY="livingStatusEntity";
    String MARITAL_STATUS_ENTITY = "maritalStatusEntity";
    String CITIZENSHIP_ENTITY = "citizenshipEntity";
    String ETHNICITY_ENTITY = "ethnicityEntity";
    String POVERTY_STATUS = "povertyStatus";
    String EDUCATION_LEVEL_ENTITY = "educationLevelEntity";
    String BUSINESS_ACTIVITIES_ENTITY = "businessActivitiesEntity";
    String HANDICAPPED_ENTITY = "handicappedEntity";
    String SPOUSE_FATHER_ENTITY = "spouseEntity";
    String FAMILY_NAME_ENTITY="familyNameEntity";
    String FAMILY_DETAIL_ENTITY="familyDetailEntity";
    String MAXIMUM_NUMBER_OF_FAMILY_MEMBERS="familyMembers";
    String ARE_FAMILY_DETAILS_REQUIRED="areFamilyDetailsRequired";
    String ARE_FAMILY_DETAILS_MANDATORY="areFamilyDetailsMandatory";
    String ARE_FAMILY_DETAILS_HIDDEN="areFamilyDetailsHidden";
    String GENDER_ENTITY_NAME = "genderEntityName";
    String BUSINESS_ACTIVITIES_ENTITY_NAME = "businessActivitiesEntityName";
    String HANDICAPPED_ENTITY_NAME = "handicappedEntityName";
    String MARITAL_STATUS_ENTITY_NAME = "maritalStatusEntityName";
    String CITIZENSHIP_ENTITY_NAME = "citizenshipEntityName";
    String ETHNICITY_ENTITY_NAME = "ethnicityEntityName";
    String EDUCATION_LEVEL_ENTITY_NAME = "educationLevelEntityName";

    /** Request parameter names for the dropdown values */
    String SALUTATION_VALUE = "salutationValue";
    String GENDER_VALUE = "genderValue";
    String SAVINGS_OFFERING_LIST = "savingsOfferingList";

    String MARITAL_STATUS_VALUE = "maritalStatusValue";
    String CITIZENSHIP_VALUE = "citizenshipValue";
    String ETHNICITY_VALUE = "ethnicityValue";

    String EDUCATION_LEVEL_VALUE = "educationLevelValue";
    String BUSINESS_ACTIVITIES_VALUE = "businessActivitiesValue";
    String HANDICAPPED_VALUE = "handicappedValue";
    String SPOUSE_FATHER_VALUE = "spouseFatherValue";
    String SPOUSE_FATHER_NAME_VALUE = "spouseFatherName";
    /** Input page constants */
    String INPUT_PERSONAL_INFO = "personalInfo";
    String INPUT_MFI_INFO = "mfiInfo";
    String INPUT_EDIT_PERSONAL_INFO = "editPersonalInfo";
    String INPUT_EDIT_MFI_INFO = "editMfiInfo";
    String INPUT_EDIT_FAMILY_INFO="editFamilyInfo";
    String INPUT_CREATE_CLIENT = "createClient";
    String INPUT_GROUP_TRANSFER = "groupTransfer";
    String CLIENT_TRANSFER = "clientTransfer";
    String INPUT_BRANCH_TRANSFER = "branchTransfer";

    /** Forward constants */
    String CLIENT_CREATE_PREVIEW_PAGE = "previewCreate_success";
    String CLIENT_CREATE_FAILURE_PAGE = "create_failure";
    String CLIENT_CREATE_PERSONAL_PAGE = "previewPersonalInfo_failure";
    String CLIENT_CREATE_MFI_PAGE = "previewMfiInfo_failure";
    String CLIENT_PREV_PERSONAL_INFO_PAGE = "prevPersonalInfo_success";
    String CLIENT_PREV_EDIT_PERSONAL_INFO_PAGE = "prevEditPersonalInfo_success";
    String CLIENT_PREV_MFI_INFO_PAGE = "prevMfiInfo_success";
    String CLIENT_PREV_EDIT_MFI_INFO_PAGE = "prevEditMfiInfo_success";
    String CLIENT_EDIT_PREVIEW_PERSONAL_PAGE = "previewEditPersonalInfo_success";
    String CLIENT_EDIT_PERSONAL_PAGE = "editPersonalInfo_success";
    String CLIENT_EDIT_PERSONAL_FAILURE_PAGE = "previewEditPersonalInfo_failure";
    String CLIENT_STATUS_PAGE = "loadStatus_success";
    String CLIENT_EDIT_MFI_PAGE = "editMfiInfo_success";
    String CLIENT_EDIT_PREVIEW_MFI_PAGE = "previewEditMfiInfo_success";
    String METHOD_LOAD_TRANSFER = "loadTransfer";
    String CLIENT_EDIT_MFI_FAILURE_PAGE = "previewEditMfiInfo_failure";
    String CLIENT_MEETING_PAGE = "loadMeeting_success";
    String CLIENT_EDIT_MEETING_PAGE = "loadEditMeeting_success";
    String CLIENT_MEETING_UPDATE_PAGE = "updateMeeting_success";
    String CLIENT_CREATE_CANCEL_PAGE = "cancelCreate_success";
    String PREVIOUS_SUCCESS = "next_success";

    String CLIENT_EDIT_CANCEL_PAGE = "cancelEdit_success";
    String CLIENT_TRANSFER_PAGE = "loadTransfer_success";

    String GROUP_TRANSFER_SEARCH_PAGE = "loadGroup_success";
    String BRANCH_TRANSFER_SEARCH_PAGE = "loadBranchSearch_success";
    String CLIENT_BRANCH_TRANSFER_PAGE = "loadBranchTransfer_success";
    String GROUP_TRANSFER_CONFIRMATION_PAGE = "confirmGroupTransfer_success";
    String BRANCH_TRANSFER_CONFIRMATION_PAGE = "confirmBranchTransfer_success";
    String CHOOSE_OFFICE_PAGE = "chooseOffice_success";

    /** Action specific to clients **/
    String METHOD_PREVIOUS_PERSONAL_INFO = "prevPersonalInfo";
    String METHOD_PREVIOUS_MFI_INFO = "prevMFIInfo";

    String METHOD_PRELOAD = "preLoad";
    String METHOD_LOAD_STATUS = "loadStatus";
    String METHOD_LOAD_MEETING = "loadMeeting";
    String METHOD_GET_BY_GLOBAL = "getByGlobalCustNum";
    String METHOD_UPDATE_MEETING = "updateMeeting";
    String METHOD_EDIT_PERSONAL_INFO = "editPersonalInfo";
    String METHOD_EDIT_MFI_INFO = "editMFIInfo";
    String METHOD_PREVIEW_PERSONAL_INFO = "previewPersonalInfo";
    String METHOD_PREVIEW_MFI_INFO = "previewMFIInfo";
    String METHOD_SET_DEFAULT_FORMEDBY = "setDefaultFormedByPersonnel";
    String METHOD_UPDATE_MFI = "updateMfi";
    String METHOD_LOAD_GROUP_TRANSFER = "loadGroupTransfer";
    String METHOD_LOAD_BRANCH_TRANSFER = "loadBranchTransfer";
    String METHOD_LOAD_HISTORICAL_DATA = "loadHistoricalData";
    String METHOD_CONFIRM_GROUP_TRANSFER = "confirmGroupTransfer";
    String METHOD_CONFIRM_BRANCH_TRANSFER = "confirmBranchTransfer";
    String METHOD_UPDATE_BRANCH = "updateBranch";
    String METHOD_CHOOSE_OFFICE = "chooseOffice";
    String METHOD_RETRIEVE_PICTURE = "retrievePicture";
    String METHOD_RETRIEVE_PICTURE_PREVIEW = "retrievePictureOnPreview";
    String OLDCLIENT = "oldClient";
    String CLIENT_STATUS_CHANGE = "clientStatusChange";
    String CLIENTSTATUSVO = "clientStatusVO";
    String CLIENT_TRANSFERVO = "clientTransferVO";
    String CLIENT_LOANOFFICER_NOT_ASSIGNED = "LoanOfficerNotAssignedException";
    String LOAN_ACCOUNT_ACTIVE_EXCEPTION = "LoanAccountActiveException";
    String SAME_GROUP_TRANSFER_EXCEPTION = "SameGroupTransferException";
    String SAME_OFFICE_TRANSFER_EXCEPTION = "SameOfficeTransferException";
    String UPDATE_FAILED = "UpdateFailedException";
    String METHOD_SHOW_PICTURE = "showPicture";
    String CUSTOMER_PICTURE_PAGE = "retrievePicture_success";
    String TRAINED_DATE_MANDATORY = "Client.TrainedDateNeeded";
    String TRAINED_CHECKED = "Client.TrainedCheckbox";
    String FUTURE_DOB_EXCEPTION = "Client.FutureDOB";
    String INVALID_DOB_EXCEPTION = "Client.InvalidDOB";
    String INVALID_AGE="Client.InvalidAge";
    String INVALID_FAMILY_DOB_EXCPETION= "Family.InvalidDOB";
    String INVALID_NUMBER_OF_SPOUSES="Family.InvalidSpouseNumber";
    String INVALID_NUMBER_OF_FATHERS="Family.InvalidFatherNumber";
    String INVALID_FAMILY_RELATIONSHIP="Family.InvalidRelationship";
    String INVALID_FAMILY_GENDER="Family.InvalidGender";
    String INVALID_FAMILY_LIVING_STATUS="Family.InvalidLivingStatus";
    String INVALID_NUMBER_OF_FAMILY_MEMBERS="Family.InvalidNumberOfFamilyMembers";
    String INVALID_FAMILY_FIRST_NAME="Family.InvalidFamilyFirstName";
    String INVALID_FAMILY_LAST_NAME="Family.InvalidFamilyLastName";
    String BAD_CHARACTERS_IN_INPUT_STRING="Family.InvalidCharacter";
    String FLAG_EXCEPTION = "Client.FlagException";
    String CLIENT_HISTORICAL_DATA_PAGE = "loadHistoricalData_success";

    String INVALID_PHOTO = "Client.InvaildPhoto";

    String INVALID_CLIENT_STATUS_EXCEPTION = "GroupStatusException";
    String ERRORS_GROUP_CANCELLED = "errors.Client.groupCancelled";
    String ERRORS_LOWER_GROUP_STATUS = "errors.Client.lowerGroupStatus";
    String ERRORS_ACTIVE_ACCOUNTS_PRESENT = "errors.Client.hasActiveAccount";
    String ERRORS_ACTIVE_PERIODIC_FEES_PRESENT = "errors.Client.hasActivePeriodicFees";
    String ERRORS_DUPLICATE_OFFERING_SELECTED = "errors.Client.duplicateOfferingSelected";

    String CLIENTPERFORMANCEHISTORY = "ClientPerformanceHistory";
    String YES = "1";
    String NO = "0";
    Short SPOUSE_VALUE = 1;
    Short FATHER_VALUE = 2;
    int MAX_OFFERINGS_SIZE = 3;
    String AGE = "age";
    String LOANCYCLECOUNTER = "loanCycleCounter";
    String CUSTOMERLOANACCOUNTSINUSE = "customerLoanAccountsInUse";
    String CUSTOMERSAVINGSACCOUNTSINUSE = "customerSavingsAccountsInUse";

    String EVENT_CREATE = "Create";
    String SOURCE_CLIENT = "Client";
    String ERROR_REQUIRED = "errors.required";
    String INVALID_NUMERIC_RANGE_RESPONSE = "questionnaire.invalid.numeric.range.response";
    String INVALID_NUMERIC_MIN_RESPONSE = "questionnaire.invalid.numeric.min.response";
    String INVALID_NUMERIC_MAX_RESPONSE = "questionnaire.invalid.numeric.max.response";
    String INVALID_NUMERIC_RESPONSE = "questionnaire.invalid.numeric.response";
}
