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

package org.mifos.config.util.helpers;

public interface ConfigurationConstants {
    // boolean IS_PENDING_APPROVAL_STATE_DEFINED=true;
    boolean IS_LSM = false;
    boolean DOES_CENTERHIERARCHY_EXIST = true;

    // constants for label lookup
    String BULKENTRY = "BulkEntry";
    String CENTER = "Center";
    String OFFICE = "Office";
    String CLIENT = "Client";
    String CLIENT_ID = "Clientid";
    String GROUP = "Group";
    String ADDRESS1 = "Address1";
    String ADDRESS2 = "Address2";
    String ADDRESS3 = "Address3";
    String CITY = "City";
    String STATE = "State";
    String POSTAL_CODE = "PostalCode";
    String GOVERNMENT_ID = "GovernmentId";
    String GOVERNMENT = "Government";
    String ID = "ID";
    String HANDICAPPED = "Handicapped";
    String CITIZENSHIP = "Citizenship";
    String ETHNICITY = "Ethnicity";
    String EXTERNALID = "ExternalId";
    String SAVINGS = "Savings";
    String INTEREST = "Interest";
    String LOAN = "Loan";
    String PERSONNEL = "Personnel";

    /*
     * Constants for retrieving Lookup Value text
     */
    String BRANCHOFFICE = "OfficeLevels-BranchOffice";

    String KEY_NO_MESSAGE_FOR_THIS_KEY = "No message exist for this key";
    String SERVICE_CHARGE = "ServiceCharge";
    short INTEREST_DAYS = 360;

    String SURVEY = "Survey";
    String MEETING = "Meeting";

    String PRODUCTS = "products";
    String PRODUCT = "product";
    String PRO = "pro";
    String CONFIG_SALUTATION = "ConfigSalutation";
    String CONFIG_MARITAL_STATUS = "ConfigMaritalStatus";
    String CONFIG_PERSONNEL_TITLE = "ConfigUserTitle";
    String CONFIG_EDUCATION_LEVEL = "ConfigEducationLevel";
    String CONFIG_CITIZENSHIP = "ConfigCitizenship";
    String CONFIG_HANDICAPPED = "ConfigHandicapped";
    String CONFIG_OFFICER_TITLE = "ConfigOfficerTitle";
    String CONFIG_BUSINESS_ACTIVITY = "ConfigBusinessActivity";
    String CONFIG_LOAN_PURPOSE = "ConfigPurposeOfLoan";
    String CONFIG_COLLATERAL_TYPE = "ConfigCollateralType";
    String CONFIG_PAYMENT_TYPE = "ConfigPaymentType";
    String CONFIG_ETHNICITY = "ConfigEthnicity";
    String NO_VALUE_ERROR = "errorNoValueMessage";
    String DUPLICATE_VALUE_ERROR = "errorDuplicateValueMessage";
    String SELECT_VALUE_ERROR = "errorSelectValueMessage";
    String ENTITY = "entity";
    String ADD_OR_EDIT = "addOrEdit";
    String LOOKUP_OPTION_DATA = "LookupOptionData";
    String LOOKUP_TYPE = "lookupType";
    String ALL_DATA_TYPES = "allDataTypes";
    String ALL_CATEGORIES = "allCategories";
    String CURRENT_DATA_TYPE = "currentDataType";
    String CURRENT_CATEGORY = "currentCategory";
    String CURRENT_CUSTOM_FIELD = "currentCustomField";

}
