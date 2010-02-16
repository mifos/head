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

package org.mifos.customers.office.util.helpers;

public interface OfficeConstants {

    // dependency names
    public final String OFFICE_DEPENDENCY_NAME = "Office";
    public final String OFFICEHIERARCHY_DEPENDENCY_NAME = "OfficeHierarchy";

    // vo names

    public final String OFFICEVO = "OfficeVo";
    public final String OFFFICEHIERARCHYVO = "OHVo";

    // master data
    public final String OFFICELEVELLIST = "OfficeLevelList";
    public final String OFFICECODELIST = "OfficeCodeList";
    public final String PARENTOFFICESMAP = "parentOfficesMap";
    public final String PARENTS = "Parents";
    public final String OFFICESTATUSLIST = "OfficeStatusList";
    public final String OFFICESUBOBJECT = "OffficeSubObject";
    public final String OFFICEDBOBJECT = "OffficeDbObject";
    public final String OLDHIERARCHYLIST = "OldHierarchyList";
    public final String OFFICESTILLBRANCHOFFICESLIST = "OfficesTillBranchOffices";
    public final String OFFICESBRANCHOFFICESLIST = "OfficesBranchOffices";
    public final String CUSTOM_FIELDS = "customFields";

    // custom methods
    public final String LOADPARENT = "loadParent";
    public final String LOADALL = "loadall";

    public final String CREATESUCESS = "createSuccess";
    public final String OFFICENAME = "officeName";
    public final String GLOBELOFFICENUMBER = "globelOfficeNumber";

    public final String MANAGE = "manage";
    public final String MANAGEPREVIEWSUCESS = "managePreview_success";
    public final String FORWARDLOADPARENTSUCESS = "loadParent_success";
    public final String FORWARDMANAGEGETSUCESS = "manageGet_success";
    public final String FORWARDMANAGEPREVOISSUCESS = "managePrevious_success";
    public final String FORWARDGETSUCESS = "get_success";
    public final String FORWARDCREATECANCEL = "cancelCreate_success";
    public final String FORWARDLOADALL = "loadall_success";

    public final String CREATE = "create";
    public final String SEARCH = "search";
    public final String UPDATEPARENTFLAG = "updateParentFlag";

    public final short HEADOFFICE = 1;
    public final short REGIONALOFFICE = 2;
    public final short SUBREGIONALOFFICE = 3;
    public final short AREAOFFICE = 4;
    public final short BRANCHOFFICE = 5;
    public final short OTHEROFFICE = 3;
    public final String SHOWDROPDOWN = "ShowDropDown";
    public final String MANAGEEDIT = "manageedit";
    public final String OFFICE_ACTIVE = "ACTIVE";
    public final short ACTIVE = 1;
    public final short INACTIVE = 2;
    public final short CONFIURE = 1;
    public final short UNCONFIURE = 0;
    public final short LOCALEENGLISH = 1;
    public final String CHECKED = "on";
    public final short ZERO = 0;
    public final short REMOTESERVER = 1;
    public final String SEARCHIDLIST = "SearchIdList";

    public static final short OFFICE_CUSTOM_FIELD_ENTITY_TYPE = 15;

    // for named queries
    public final String LOCALEID = "localeId";
    public final String LEVELID = "levelId";
    public final String DISPLAYNAME = "displayName";
    public final String SHORTNAME = "shortName";
    public final String OFFICEID = "officeId";
    public final String STATUSID = "statusId";
    public final String ENTITYTYPE = "entityType";

    // exception keys
    public final String KEYCREATEFAILED = "office.error.creationFailed";
    public final String KEYUPDATEFAILED = "office.error.updationFailed";
    public final String KEYGETFAILED = "office.error.getFailed";
    public final String KEYLOADFAILED = "office.error.loadFailed";
    public final String KEYHASACTIVECHILDREN = "Office.error.hasActiveChildern";
    public final String KEYHASACTIVEPERSONNEL = "Office.error.hasActivePersonnel";
    public final String KEYHASACTIVEOFFICEWITHLEVEL = "Office.error.hasActiveOfficeWithThisLevel";
    public final String KEYLOADINOFFICEHIERARCHYMASTERFAILED = "Office.error.errorInLoadingMasterData";
    public final String KEYOFFICEHIERARCHYUPDATEFAILED = "Office.error.updationHierarchyFailed";
    public final String KEYSELECTOFFICETYPE = "Office.formOfficeType.pleaseLelect";

    public final String KEYNAMEEXIST = "Office.formOfficeType.nameExist";
    public final String KEYSHORTNAMEEXIST = "Office.formOfficeType.shortNameExist";
    public final String KEYHIERARCHYUPDATIONFAILED = "errors.office.updationhierarchyfailed";
    public final String KEYOFFICELEVELNOTCONFIGURED = "errors.office.officelevelnotconfigured";
    public final String KEYPARENTNOTACTIVE = "errors.office.parentnotactive";
    public final String KEYPARENTNOTVALID = "errors.office.parentinvalid";
    public final String KEYLEVELNOTCONFIGURED = "error.office.levelNotConfigured";

    public static final String KEYCUSTOMFIELDREQUIRED = "errors.office.requiredCustomField";

    // M2 keys

    public static final String ERRORMANDATORYFIELD = "error.office.mandatory_field";

    public static final String OFFICE_NAME = "Office.officeName";
    public static final String OFFICESHORTNAME = "Office.officeShortName";
    public static final String OFFICELEVEL = "Office.officeLevel";
    public static final String OFFICETYPE = "Office.officeType";
    public static final String OFFICEOPERATIONMODE = "Office.operationMode";
    public static final String PARENTOFFICE = "Office.parentOffice";

    public static final String MAXOFFICELIMITREACHED = "error.office.maxReached";

    public static final String OFFICENAMEEXIST = "error.office.duplicateName";
    public static final String OFFICESHORTNAMEEXIST = "error.office.duplicateShortName";

    public static final String ENTERADDTIONALINFO = "error.office.provideadditionalInformation";
    public static final String OFFICE_LEVELS = "officelevels";
    public static final String GET_HEADOFFICE = "headOfficeList";
    public static final String GET_REGIONALOFFICE = "regionalOfficeList";
    public static final String GET_SUBREGIONALOFFICE = "divisionalOfficeList";
    public static final String GET_AREAOFFICE = "areaOfficeList";
    public static final String GET_BRANCHOFFICE = "branchOfficeList";
    public static final String ERROR_INVALID_LEVEL = "error.office.childhasmorelevel";
    public static final String ERROR_LEVEL = "error.noofficelevel";
    public static final String ERROR_STATUS = "error.noofficestatus";

    public static final String ERROR_INVLID_PARENT = "error.office.invalidparentoffice";
}
