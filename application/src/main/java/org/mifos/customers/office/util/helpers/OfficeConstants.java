/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.office.util.helpers;

public interface OfficeConstants {

    // dependency names
    String OFFICE_DEPENDENCY_NAME = "Office";
    String OFFICEHIERARCHY_DEPENDENCY_NAME = "OfficeHierarchy";

    // vo names

    String OFFICEVO = "OfficeVo";
    String OFFFICEHIERARCHYVO = "OHVo";

    // master data
    String OFFICELEVELLIST = "OfficeLevelList";
    String OFFICECODELIST = "OfficeCodeList";
    String PARENTOFFICESMAP = "parentOfficesMap";
    String PARENTS = "Parents";
    String OFFICESTATUSLIST = "OfficeStatusList";
    String OFFICESUBOBJECT = "OffficeSubObject";
    String OFFICEDBOBJECT = "OffficeDbObject";
    String OLDHIERARCHYLIST = "OldHierarchyList";
    String OFFICESTILLBRANCHOFFICESLIST = "OfficesTillBranchOffices";
    String OFFICESBRANCHOFFICESLIST = "OfficesBranchOffices";
    String CUSTOM_FIELDS = "customFields";
    String OFFICE_DTO = "officeDto";

    // custom methods
    String LOADPARENT = "loadParent";
    String LOADALL = "loadall";

    String CREATESUCESS = "createSuccess";
    String OFFICENAME = "officeName";
    String GLOBELOFFICENUMBER = "globelOfficeNumber";

    String MANAGE = "manage";
    String MANAGEPREVIEWSUCESS = "managePreview_success";
    String FORWARDLOADPARENTSUCESS = "loadParent_success";
    String FORWARDMANAGEGETSUCESS = "manageGet_success";
    String FORWARDMANAGEPREVOISSUCESS = "managePrevious_success";
    String FORWARDGETSUCESS = "get_success";
    String FORWARDCREATECANCEL = "cancelCreate_success";
    String FORWARDLOADALL = "loadall_success";

    String CREATE = "create";
    String SEARCH = "search";
    String UPDATEPARENTFLAG = "updateParentFlag";

    short HEADOFFICE = 1;
    short REGIONALOFFICE = 2;
    short SUBREGIONALOFFICE = 3;
    short AREAOFFICE = 4;
    short BRANCHOFFICE = 5;
    short OTHEROFFICE = 3;
    String SHOWDROPDOWN = "ShowDropDown";
    String MANAGEEDIT = "manageedit";
    String OFFICE_ACTIVE = "ACTIVE";
    short ACTIVE = 1;
    short INACTIVE = 2;
    short CONFIURE = 1;
    short UNCONFIURE = 0;
    short LOCALEENGLISH = 1;
    String CHECKED = "on";
    short ZERO = 0;
    short REMOTESERVER = 1;
    String SEARCHIDLIST = "SearchIdList";

    short OFFICE_CUSTOM_FIELD_ENTITY_TYPE = 15;

    // for named queries
    String LOCALEID = "localeId";
    String LEVELID = "levelId";
    String DISPLAYNAME = "displayName";
    String SHORTNAME = "shortName";
    String OFFICEID = "officeId";
    String STATUSID = "statusId";
    String ENTITYTYPE = "entityType";

    // exception keys
    String KEYCREATEFAILED = "office.error.creationFailed";
    String KEYUPDATEFAILED = "office.error.updationFailed";
    String KEYGETFAILED = "office.error.getFailed";
    String KEYLOADFAILED = "office.error.loadFailed";
    String KEYHASACTIVECHILDREN = "Office.error.hasActiveChildern";
    String KEYHASACTIVEPERSONNEL = "Office.error.hasActivePersonnel";
    String KEYHASACTIVEOFFICEWITHLEVEL = "Office.error.hasActiveOfficeWithThisLevel";
    String KEYLOADINOFFICEHIERARCHYMASTERFAILED = "Office.error.errorInLoadingMasterData";
    String KEYOFFICEHIERARCHYUPDATEFAILED = "Office.error.updationHierarchyFailed";
    String KEYSELECTOFFICETYPE = "Office.formOfficeType.pleaseLelect";

    String KEYNAMEEXIST = "Office.formOfficeType.nameExist";
    String KEYSHORTNAMEEXIST = "Office.formOfficeType.shortNameExist";
    String KEYHIERARCHYUPDATIONFAILED = "errors.office.updationhierarchyfailed";
    String KEYOFFICELEVELNOTCONFIGURED = "errors.office.officelevelnotconfigured";
    String KEYPARENTNOTACTIVE = "errors.office.parentnotactive";
    String KEYPARENTNOTVALID = "errors.office.parentinvalid";
    String KEYLEVELNOTCONFIGURED = "error.office.levelNotConfigured";

    String KEYCUSTOMFIELDREQUIRED = "errors.office.requiredCustomField";

    // M2 keys

    String ERRORMANDATORYFIELD = "error.office.mandatory_field";

    String OFFICE_NAME = "Office.officeName";
    String OFFICESHORTNAME = "Office.officeShortName";
    String OFFICELEVEL = "Office.officeLevel";
    String OFFICETYPE = "Office.officeType";
    String OFFICEOPERATIONMODE = "Office.operationMode";
    String PARENTOFFICE = "Office.parentOffice";

    String MAXOFFICELIMITREACHED = "error.office.maxReached";

    String OFFICENAMEEXIST = "error.office.duplicateName";
    String OFFICESHORTNAMEEXIST = "error.office.duplicateShortName";

    String ENTERADDTIONALINFO = "error.office.provideadditionalInformation";
    String ERROR_CUSTOMDATEFIELD = "error.office.customdatefield";
    String OFFICE_LEVELS = "officelevels";
    String GET_HEADOFFICE = "headOfficeList";
    String GET_REGIONALOFFICE = "regionalOfficeList";
    String GET_SUBREGIONALOFFICE = "divisionalOfficeList";
    String GET_AREAOFFICE = "areaOfficeList";
    String GET_BRANCHOFFICE = "branchOfficeList";
    String ERROR_INVALID_LEVEL = "error.office.childhasmorelevel";
    String ERROR_LEVEL = "error.noofficelevel";
    String ERROR_STATUS = "error.noofficestatus";

    String ERROR_INVALID_PARENT = "error.office.invalidparentoffice";
    String ERROR_REPARENT_NOT_ALLOWED_AS_FUTURE_APPLICABLE_HOLIDAYS_ARE_DIFFERENT_ON_PREVIOUS_AND_NEW_PARENT = "error.office.futureholidaysnotthesame";
}
