/**

* CheckListConstants  version: 1.0



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
package org.mifos.application.checklist.util.resources;

public interface CheckListConstants {

	//custom methods 
	public final String LOADPARENT="loadParent";
	public final String LOADALL="loadall";		
	public final String FORWARDLOADPARENTSUCESS="loadParent_success";
	public final String LISTOFSTATUSES="list";
	public final String FORWARDONLOADALL="forward_view";
	public static final String CATAGORY="catagory";
	public final String GETPARENT="preLoad";
	public static final String MANDATORY="errors.mandatory";
	public static final String MAX_LENGTH="errors.maximumlength";
	public static final String MANDATORY_ITEM="errors.mandatory_item";
	public static final String ITEM="Item";
	public static final String TYPE="Type";
	public static final String ITEM_LENGTH="errors.itemlength";
	public static final String CATEGORYEXCEPTION="exceptions.application.checklist.Statusalreadydefined";
	public static final String STATUS="status";
	public static final String ITEM_SIZE="250";
	public static final String LOCALEID="1";
	public String CUSTOMER_LOADMASTERDATA="masterdata.customer_loadMasterdata";
	public String PRODUCT_LOADMASTERDATA="masterdata.product_loadMasterdata";
	public String CUSTOMER_CHECKLISTMASTER_GETCHECKLISTMASTERDATA="masterdata.customer_checkListMaster_getCheckListMasterdata";
	public String PRODUCT_CHECKLISTMASTER_GETCHECKLISTMASTERDATA="masterdata.product_checkListMaster_getCheckListMasterdata";
	public String CUSTOMER_CHECKLISTDETAILS_GETCHECKLISTMASTERDATA="masterdata.customer_checkListDetails_getCheckListMasterdata";
	public String CHECKLIST_GETCHELISTMASTERDATA="masterdata.checkList_getCheckListMasterdata";
	public String PRODUCTSTATUSLIST="productStatusList";
	public String PRODUCTLIST_GET="masterdata.productList_get";
	public String PRDLIST_GET="masterdata.prdList_get";
	public String CUSTOMER_VALIDATESTATE="customer.validateState";
	public String PRODUCT_VALIDATESTATE="product.validateState";
	public String CREATEDBY="checklist.createdBy";
	public String CUSTOMERSTATUSLIST_WITHOUTPENDING_APPROVAL = "customerstatuslist_withoutpending_approval";
	public String PRODUCTSTATUSLIST_WITHOUTPENDING_APPROVAL = "productstatuslist_withoutpending_approval";
	public String PENDINGAPPROVAL="Application Pending Approval";
	public final Short STATUS_ACTIVE = 1;
	public final Short STATUS_INACTIVE = 0;
	
	public final String CHECKLIST_CREATION_EXCEPTION = "exception.checklist.creation";
	public final String CHECKLIST_MASTERDATA="checkList_masterData";
	public final String STATES="states";
	public final String CHECKLIST_NAME="checklist name";
	public final String DETAILS="details";
	public final String EXCEPTION_STATE_ALREADY_EXIST="exceptions.application.checklist.Statusalreadydefined";
	public final String STATE_COMBO="displayed when moving into Status";
	public final String TYPE_COMBO="type";
	public final String CENTER_CHECKLIST="centerchecklist";
	public final String GROUP_CHECKLIST="groupchecklist";
	public final String CLIENT_CHECKLIST="clientchecklist";
	public final String LOAN_CHECKLIST="loanchecklist";
	public final String SAVINGS_CHECKLIST="savingschecklist";
	public final String CREATED_BY_NAME="createdname";
	public final String ACTIVE = "active";
	public final String INACTIVE = "inactive";
	public final String CHECKLIST_STATUS="checklist status";
	public final String CHECKLIST_ITEMS="items";
	
}
