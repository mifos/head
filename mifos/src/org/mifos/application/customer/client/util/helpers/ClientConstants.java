/**

* ClientConstants.java   version: 1.0



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

package org.mifos.application.customer.client.util.helpers;

public interface ClientConstants {
	
	/**Forwards*/
	public final static String PRELOAD_SUCCESS ="preLoad_success";
	public final static String NEXT_FAILURE = "next_failure";
	

	/***/
	public static final short CLIENT_BELONGS_TO_GROUP =1;
	public static final short PICTURE_ALLOWED_SIZE =30720;
	public static final short All_CATEGORY_ID =1;
	public static final short CLIENT_CATEGORY_ID =2;
	public static final short CLIENT_ENTITY_TYPE =1;
	public static final short LOAN_OFFICER_LEVEL =1;
	public static final Short CLIENT_NAME_TYPE =3;
	public static final String CLIENTVO ="clientVO";
	/**Request parameter names*/
	public static final String SALUTATION_ENTITY  = "salutationEntity";
	public static final String GENDER_ENTITY  ="genderEntity";
	public static final String  MARITAL_STATUS_ENTITY = "maritalStatusEntity";
	public static final String  CITIZENSHIP_ENTITY = "citizenshipEntity";
	public static final String  ETHINICITY_ENTITY= "ethinicityEntity";
	public static final String  POVERTY_STATUS= "povertyStatus";
	public static final String  EDUCATION_LEVEL_ENTITY = "educationLevelEntity";
	public static final String BUSINESS_ACTIVITIES_ENTITY = "businessActivitiesEntity";
	public static final String  HANDICAPPED_ENTITY = "handicappedEntity";
	public static final String  SPOUSE_FATHER_ENTITY = "spouseEntity";
	public static final String GENDER_ENTITY_NAME  ="genderEntityName";
	public static final String BUSINESS_ACTIVITIES_ENTITY_NAME = "businessActivitiesEntityName";
	public static final String  HANDICAPPED_ENTITY_NAME = "handicappedEntityName";
	public static final String  MARITAL_STATUS_ENTITY_NAME = "maritalStatusEntityName";
	public static final String  CITIZENSHIP_ENTITY_NAME = "citizenshipEntityName";
	public static final String  ETHINICITY_ENTITY_NAME= "ethinicityEntityName";
	public static final String  EDUCATION_LEVEL_ENTITY_NAME = "educationLevelEntityName";
	/**Status Constants*/
	public static final short STATUS_PARTIAL =1;
	public static final short STATUS_PENDING =2;
	public static final short STATUS_ACTIVE =3;
	public static final short STATUS_HOLD =4;
	public static final short STATUS_CANCELLED =5;
	public static final short STATUS_CLOSED =6;
	
	/**Request parameter names for the dropdown values*/
	public static final String SALUTATION_VALUE  = "salutationValue";
	public static final String GENDER_VALUE  ="genderValue";
	
	
	public static final String  MARITAL_STATUS_VALUE = "maritalStatusValue";
	public static final String  CITIZENSHIP_VALUE = "citizenshipValue";
	public static final String  ETHINICITY_VALUE= "ethinicityValue";
	
	public static final String  EDUCATION_LEVEL_VALUE = "educationLevelValue";
	public static final String BUSINESS_ACTIVITIES_VALUE = "businessActivitiesValue";
	public static final String  HANDICAPPED_VALUE = "handicappedValue";
	public static final String  SPOUSE_FATHER_VALUE = "spouseFatherValue";
	public static final String  SPOUSE_FATHER_NAME_VALUE = "spouseFatherName";
	/**Input page constants*/
	public static final String INPUT_PERSONAL_INFO  = "personalInfo";
	public static final String INPUT_MFI_INFO  = "mfiInfo";
	public static final String INPUT_EDIT_PERSONAL_INFO  = "editPersonalInfo";
	public static final String INPUT_EDIT_MFI_INFO  = "editMfiInfo";
	public static final String INPUT_CREATE_CLIENT  = "createClient";
	public static final String INPUT_GROUP_TRANSFER  = "groupTransfer";
	public static final String CLIENT_TRANSFER = "clientTransfer";
	public static final String INPUT_BRANCH_TRANSFER  = "branchTransfer";
	
	/**Forward constants*/
	public static final String CLIENT_CREATE_PREVIEW_PAGE = "previewCreate_success" ;
	public static final String CLIENT_CREATE_FAILURE_PAGE = "create_failure" ;
	public static final String CLIENT_CREATE_PERSONAL_PAGE = "previewPersonalInfo_failure" ;
	public static final String CLIENT_CREATE_MFI_PAGE =  "previewMfiInfo_failure" ;
	public final static String CLIENT_PREV_PERSONAL_INFO_PAGE = "prevPersonalInfo_success" ;
	public static final String CLIENT_PREV_EDIT_PERSONAL_INFO_PAGE = "prevEditPersonalInfo_success";
	public final static String CLIENT_PREV_MFI_INFO_PAGE = "prevMfiInfo_success";
	public static final String CLIENT_PREV_EDIT_MFI_INFO_PAGE = "prevEditMfiInfo_success";
	public static final String CLIENT_EDIT_PREVIEW_PERSONAL_PAGE =  "previewEditPersonalInfo_success" ;
	public static final String CLIENT_EDIT_PERSONAL_PAGE =  "editPersonalInfo_success" ;
	public static final String CLIENT_EDIT_PERSONAL_FAILURE_PAGE =  "previewEditPersonalInfo_failure" ;
	public static final String CLIENT_STATUS_PAGE =  "loadStatus_success" ;
	public static final String CLIENT_EDIT_MFI_PAGE =  "editMfiInfo_success" ;
	public static final String CLIENT_EDIT_PREVIEW_MFI_PAGE =  "previewEditMfiInfo_success" ;
	public static final String METHOD_LOAD_TRANSFER = "loadTransfer";
	public static final String CLIENT_EDIT_MFI_FAILURE_PAGE =  "previewEditMfiInfo_failure" ;
	public static final String CLIENT_MEETING_PAGE =  "loadMeeting_success" ;
	public static final String CLIENT_EDIT_MEETING_PAGE ="loadEditMeeting_success";
	public static final String CLIENT_MEETING_UPDATE_PAGE = "updateMeeting_success";
	public static final String CLIENT_CREATE_CANCEL_PAGE =  "cancelCreate_success" ;
	public static final String PREVIOUS_SUCCESS =  "next_success" ;
	
	public static final String CLIENT_EDIT_CANCEL_PAGE =  "cancelEdit_success" ;
	public static final String CLIENT_TRANSFER_PAGE = "loadTransfer_success";
	
	public static final String GROUP_TRANSFER_SEARCH_PAGE =  "loadGroup_success" ;
	public static final String BRANCH_TRANSFER_SEARCH_PAGE = "loadBranchSearch_success";
	public static final String CLIENT_BRANCH_TRANSFER_PAGE = "loadBranchTransfer_success";
	public static final String GROUP_TRANSFER_CONFIRMATION_PAGE =  "confirmGroupTransfer_success" ;
	public static final String BRANCH_TRANSFER_CONFIRMATION_PAGE = "confirmBranchTransfer_success";
	public static final String CHOOSE_OFFICE_PAGE = "chooseOffice_success";
	
	/**Action specific to clients**/
	public static final String METHOD_PREVIOUS_PERSONAL_INFO =  "prevPersonalInfo" ;
	public static final String METHOD_PREVIOUS_MFI_INFO =  "prevMFIInfo" ;
	
	public static final String METHOD_PRELOAD =  "preLoad" ;
	public static final String METHOD_LOAD_STATUS = "loadStatus";
	public static final String METHOD_LOAD_MEETING = "loadMeeting";
	public static final String METHOD_GET_BY_GLOBAL = "getByGlobalCustNum";
	public static final String METHOD_UPDATE_MEETING = "updateMeeting";
	public static final String METHOD_EDIT_PERSONAL_INFO =  "editPersonalInfo" ;
	public static final String METHOD_EDIT_MFI_INFO =  "editMFIInfo" ;
	public static final String METHOD_PREVIEW_PERSONAL_INFO =  "previewPersonalInfo" ;
	public static final String METHOD_PREVIEW_MFI_INFO =  "previewMFIInfo" ;
	public static final String METHOD_SET_DEFAULT_FORMEDBY =  "setDefaultFormedByPersonnel";
	public static final String METHOD_UPDATE_MFI =  "updateMfi";
	public static final String METHOD_LOAD_GROUP_TRANSFER = "loadGroupTransfer";
	public static final String METHOD_LOAD_BRANCH_TRANSFER = "loadBranchTransfer";
	public static final String METHOD_LOAD_HISTORICAL_DATA = "loadHistoricalData";
	public static final String METHOD_CONFIRM_GROUP_TRANSFER = "confirmGroupTransfer";
	public static final String METHOD_CONFIRM_BRANCH_TRANSFER = "confirmBranchTransfer";
	public static final String METHOD_UPDATE_BRANCH = "updateBranch";
	public static final String METHOD_CHOOSE_OFFICE = "chooseOffice";
	public static final String METHOD_RETRIEVE_PICTURE = "retrievePicture";
	public static final String METHOD_RETRIEVE_PICTURE_PREVIEW = "retrievePictureOnPreview";
	public static final String OLDCLIENT = "oldClient";
	public static final String CLIENT_STATUS_CHANGE = "clientStatusChange";
	public static final String CLIENTSTATUSVO = "clientStatusVO";
	public static final String CLIENT_TRANSFERVO = "clientTransferVO";
	public static final String CLIENT_LOANOFFICER_NOT_ASSIGNED = "LoanOfficerNotAssignedException";
	public static final String LOAN_ACCOUNT_ACTIVE_EXCEPTION = "LoanAccountActiveException";
	public static final String SAME_GROUP_TRANSFER_EXCEPTION= "SameGroupTransferException";
	public static final String SAME_OFFICE_TRANSFER_EXCEPTION = "SameOfficeTransferException";
	public static final String UPDATE_FAILED = "UpdateFailedException";
	public static final String METHOD_SHOW_PICTURE = "showPicture";
	public static final String CUSTOMER_PICTURE_PAGE = "retrievePicture_success";
	public static final String TRAINED_DATE_MANDATORY = "Client.TrainedDateNeeded";
	public static final String TRAINED_CHECKED = "Client.TrainedCheckbox";
	public static final String INVALID_DOB_EXCEPTION = "Client.InvalidDOB";
	public static final String FLAG_EXCEPTION = "Client.FlagException";
	public static final String CLIENT_HISTORICAL_DATA_PAGE = "loadHistoricalData_success";
	
	public static final String PICTURE_EXCEPTION = "Client.PictureExtensionException";
	public static final String PICTURE_SIZE_EXCEPTION = "Client.PictureSizeException";
	
	public static final String INVALID_CLIENT_STATUS_EXCEPTION ="GroupStatusException";
	public static final String ERRORS_LOWER_GROUP_STATUS="errors.Client.lowerGroupStatus";
	public static final String ERRORS_ACTIVE_ACCOUNTS_PRESENT="errors.Client.hasActiveAccount";
	public static final String ERRORS_DUPLICATE_OFFERING_SELECTED="errors.Client.duplicateOfferingSelected";
	
	public String CLIENTPERFORMANCEHISTORY = "ClientPerformanceHistory";
	public static final String YES ="1";
	public static final String NO ="0";
	public static final Short SPOUSE_VALUE = 1;
	public static final Short FATHER_VALUE = 2;
	public static final Short MAX_OFFERINGS_SIZE=3;
	public static final String AGE = "age";
	public static final String LOANCYCLECOUNTER = "loanCycleCounter";
	public static final String CUSTOMERLOANACCOUNTSINUSE = "customerLoanAccountsInUse";
	public static final String CUSTOMERSAVINGSACCOUNTSINUSE = "customerSavingsAccountsInUse";

}
