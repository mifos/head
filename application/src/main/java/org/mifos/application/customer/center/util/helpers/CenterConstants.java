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

package org.mifos.application.customer.center.util.helpers;

/**
 * This interface contains all the constants used within the center module
 */
public interface CenterConstants {

    /** Level id of the client */
    short CLIENT_LEVEL_ID = 1;
    /**
     * Refers to the category id which indicates a particular fee is applicable
     * to all customers
     */
    short ALL_CATEGORY_ID = 1;
    /**
     * Refers to the category id which indicates a particular fee is applicable
     * to all centers
     */
    short CUSTOMER_CATEGORY_ID = 4;
    /** Refers to the active status of a fee */
    short FEE_ACTIVE = 1;

    /** Parameters reffering to the input page */
    String CENTER = "center";
    String INPUT_CREATE = "create";
    String INPUT_STATUS = "status";
    String INPUT_MANAGE = "manage";
    String INPUT_SEARCH = "search";
    String INPUT_SEARCH_CREATEGROUP = "CenterSearch_CreateGroup";
    String INPUT_SEARCH_TRANSFERGROUP = "CenterSearch_TransferGroup";
    String INPUT_MEETING = "CreateMeeting";
    String INPUT_CENTER = "Center";
    String FROM_PAGE = "fromPage";

    /** Request parameter names */
    String LOAN_OFFICER_LIST = "loanOfficers";
    String FEES_LIST = "feesList";
    String ADMIN_FEES_LIST = "adminFeesList";
    String COLLECTION_SHEET_TYPES = "collectionSheetTypes";
    String CHECKLISTS = "checkLists";
    String POSITIONS = "positions";
    String CUSTOM_FIELDS = "customFields";
    String MFI_TITLES = "mfiTitles";
    String SEARCH_RESULTS = "searchResults";
    String CENTERVO = "centerVO";
    String CUSTOMERVO = "customerVO";
    String GROUP_LIST = "groups";
    String PERFORMANCE_HISTORY = "performanceHistory";
    String CLIENT_LIST = "clients";
    String ADDITIONAL_FEES_LIST = "additionalFees";

    String BLANK = "";
    String ZERO = "0";
    String HYPHEN = "-";

    /** Action methods */
    String LOAD_SEARCH = "loadSearch";
    String LOAD_STATUS = "loadStatus";
    String UPDATE_STATUS = "updateStatus";
    String CENTER_DETAILS_PAGE = "centerDetails";
    String CENTER_CHARGES_DETAILS_PAGE = "ViewCenterCharges";

    /** Forwards */
    String CENTER_CREATE_PAGE = "load_success";
    String CENTER_CREATE_EDIT_PAGE = "previousCreate_success";
    String CENTER_CREATE_CANCEL_PAGE = "cancelCreate_success";
    String CENTER_SEARCH_CANCEL_PAGE = "cancelSearch_success";
    String CENTER_MANAGE_EDIT_PAGE = "previousManage_success";
    String CENTER_MANAGE_CANCEL_PAGE = "cancelManage_success";
    String CENTER_STATUS_EDIT_PAGE = "previousStatus_success";
    String CENTER_STATUS_CANCEL_PAGE = "cancelStatus_success";
    String CENTER_CHANGE_STATUS_PAGE = "loadStatus_success";
    String CENTER_CREATE_PREVIEW_PAGE = "previewCreate_success";
    String CENTER_CREATEPREVIEW_FAILURE_PAGE = "previewCreate_failure";
    String CENTER_STATUS_PREVIEW_PAGE = "previewStatus_success";
    String CENTER_STATUSPREVIEW_FAILURE_PAGE = "previewStatus_failure";
    String CENTER_MANAGE_PREVIEW_PAGE = "previewManage_success";
    String CENTER_MANAGEPREVIEW_FAILURE_PAGE = "previewManage_failure";
    String CENTER_SEARCH_PAGE = "loadSearch_success";
    String CENTER_MEETING_PAGE = "loadMeeting_success";
    String CENTER_EDIT_MEETING_PAGE = "loadEditMeeting_success";
    String CENTER_STATUS_UPDATE_PAGE = "updateStatus_success";
    String CENTER_MEETING_UPDATE_PAGE = "updateMeeting_success";
    String FATAL_ERROR_EXCEPTION = "FatalErrorException";
    String CHOOSE_OFFICE_SUCCESS = "chooseOffice_success";
    String CENTER_SEARCH_TRANSFER_CANCEL_PAGE = "cancelTransfer_success";
    String OLD_PERSONNEL = "oldPersonnel";
    String NO_SEARCH_STRING = "errors.nosearchstring";

}
