/**

* HolidayConstants  version: 1.0



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
package org.mifos.application.holiday.util.resources;

public interface HolidayConstants {

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
	public String CUSTOMER_CHECKLISTMASTER_GETCHECKLISTMASTERDATA="masterdata.customer_holidayMaster_getHolidayMasterdata";
	public String PRODUCT_CHECKLISTMASTER_GETCHECKLISTMASTERDATA="masterdata.product_holidayMaster_getHolidayMasterdata";
	public String CUSTOMER_CHECKLISTDETAILS_GETCHECKLISTMASTERDATA="masterdata.customer_holidayDetails_getHolidayMasterdata";
	public String CHECKLIST_GETCHELISTMASTERDATA="masterdata.holiday_getHolidayMasterdata";
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

	public static final String ERRORMANDATORYFIELD="error.holiday.mandatory_field";
	
	public static final String OFFICE_NAME="Office.officeName";
	public static final String OFFICESHORTNAME="Office.officeShortName";
	public static final String OFFICELEVEL="Office.officeLevel";
	public static final String OFFICETYPE="Office.officeType";	
	
	public final String HOLIDAY_CREATION_EXCEPTION = "exception.holiday.creation";
	public final String HOLIDAY_MASTERDATA="holiday_masterData";
	public final String STATES="states";
	public final String HOLIDAY_NAME="holiday name";
	public final String HOLIDAY_FROM_DATE="holiday from date";
	public final String HOLIDAY_THRU_DATE="holiday to date";
	public final String REPAYMENT_RULE="repayment rule";
	public final String DETAILS="details";
	public final String EXCEPTION_STATE_ALREADY_EXIST="exceptions.application.holiday.Statusalreadydefined";
	public final String STATE_COMBO="displayed when moving into Status";
	public final String TYPE_COMBO="type";
	public final String CENTER_HOLIDAY="centerholiday";
	public final String GROUP_CHECKLIST="groupchecklist";
	public final String CLIENT_CHECKLIST="clientchecklist";
	public final String LOAN_CHECKLIST="loanchecklist";
	public final String SAVINGS_CHECKLIST="savingschecklist";
	public final String CREATED_BY_NAME="createdname";
	public final String ACTIVE = "active";
	public final String INACTIVE = "inactive";
	public final String CHECKLIST_STATUS="checklist status";
	public final String CHECKLIST_ITEMS="items";
	public final String OLDCHECKLISTNAME="oldChecklistName";
	
	public final String HOLIDAYLIST1 = "holidayList1";
	public final String HOLIDAYLIST2 = "holidayList2";
	public final String REPAYMENTRULETYPES = "RepaymentRuleType";
	public String HOLIDAY_ACTIONFORM = "holidayActionForm";
	public static final String ALL_HOLIDAYLIST = "allHolidays";

	public static final String HOLIDAYNAME = "holiday.HolidayName";
	public static final String HOLIDAYFROMDATE = "holiday.HolidayFromDate";
	public static final String HOLIDAYTHRUDATE = "holiday.HolidayThruDate";
	public static final String HOLIDAYOFFICEID = "holiday.HolidayOfficeId";
	public static final String HOLIDAYREPAYMENTRULE = "holiday.HolidayRepaymentRule";
	
	public static final String INVALIDFROMDATE = "errors.fromdateexception";
	public static final String INVALIDTHRUDATE = "errors.thrudateexception";
	
	
	public static final String HOLIDAY_LIST = "holidayList";
	public static final String YEAR = "year";
	public static final String NO_OF_YEARS = "noOfYears";
}
