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

package org.mifos.customers.checklist.util.helpers;

public interface CheckListConstants {

    // custom methods
    String LOADPARENT = "loadParent";
    String LOADALL = "loadall";
    String FORWARDLOADPARENTSUCESS = "loadParent_success";
    String LISTOFSTATUSES = "list";
    String FORWARDONLOADALL = "forward_view";
    String CATAGORY = "catagory";
    String GETPARENT = "preLoad";
    String MANDATORY = "errors.mandatory";
    String MAX_LENGTH = "errors.maximumlength";
    String MANDATORY_ITEM = "errors.mandatory_item";
    String ITEM = "Item";
    String TYPE = "Type";
    String ITEM_LENGTH = "errors.itemlength";
    String CATEGORYEXCEPTION = "exceptions.application.checklist.Statusalreadydefined";
    String STATUS = "status";
    String ITEM_SIZE = "250";
    String LOCALEID = "1";
    String CUSTOMER_LOADMASTERDATA = "masterdata.customer_loadMasterdata";
    String PRODUCT_LOADMASTERDATA = "masterdata.product_loadMasterdata";
    String CUSTOMER_CHECKLISTMASTER_GETCHECKLISTMASTERDATA = "masterdata.customer_checkListMaster_getCheckListMasterdata";
    String PRODUCT_CHECKLISTMASTER_GETCHECKLISTMASTERDATA = "masterdata.product_checkListMaster_getCheckListMasterdata";
    String CUSTOMER_CHECKLISTDETAILS_GETCHECKLISTMASTERDATA = "masterdata.customer_checkListDetails_getCheckListMasterdata";
    String CHECKLIST_GETCHELISTMASTERDATA = "masterdata.checkList_getCheckListMasterdata";
    String PRODUCTSTATUSLIST = "productStatusList";
    String PRODUCTLIST_GET = "masterdata.productList_get";
    String PRDLIST_GET = "masterdata.prdList_get";
    String CUSTOMER_VALIDATESTATE = "customer.validateState";
    String PRODUCT_VALIDATESTATE = "product.validateState";
    String CREATEDBY = "checklist.createdBy";
    String CUSTOMERSTATUSLIST_WITHOUTPENDING_APPROVAL = "customerstatuslist_withoutpending_approval";
    String PRODUCTSTATUSLIST_WITHOUTPENDING_APPROVAL = "productstatuslist_withoutpending_approval";
    String PENDINGAPPROVAL = "Application Pending Approval";
    Short STATUS_ACTIVE = 1;
    Short STATUS_INACTIVE = 0;

    String CHECKLIST_CREATION_EXCEPTION = "exception.checklist.creation";
    String CHECKLIST_MASTERDATA = "checkList_masterData";
    String STATES = "states";
    String CHECKLIST_NAME = "checklist name";
    String DETAILS = "details";
    String EXCEPTION_STATE_ALREADY_EXIST = "exceptions.application.checklist.Statusalreadydefined";
    String STATE_COMBO = "displayed when moving into Status";
    String TYPE_COMBO = "type";
    String CENTER_CHECKLIST = "centerchecklist";
    String GROUP_CHECKLIST = "groupchecklist";
    String CLIENT_CHECKLIST = "clientchecklist";
    String LOAN_CHECKLIST = "loanchecklist";
    String SAVINGS_CHECKLIST = "savingschecklist";
    String CREATED_BY_NAME = "createdname";
    String ACTIVE = "active";
    String INACTIVE = "inactive";
    String CHECKLIST_STATUS = "checklist status";
    String CHECKLIST_ITEMS = "items";
    String OLDCHECKLISTNAME = "oldChecklistName";
    String CHECKLIST_STATUS_RESOURCE = "checklist.checklistStatus";
    String CHECKLIST_NAME_RESOURCE = "checklist.checklist_Name";
    String CHECKLIST_ITEMS_RESOURCE = "checklist.items";
    String CHECKLIST_TYPE_RESOURCE = "checklist.checklistType";
    String CHECKLIST_DISPLAY_STATUS_RESOURCE = "checklist.display_status";
    String CHECKLIST_DETAIL_RESOURCE = "checklist.details";

}
