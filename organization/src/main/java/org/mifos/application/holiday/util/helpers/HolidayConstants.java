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

package org.mifos.application.holiday.util.helpers;

public interface HolidayConstants {

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
    String CUSTOMER_CHECKLISTMASTER_GETCHECKLISTMASTERDATA = "masterdata.customer_holidayMaster_getHolidayMasterdata";
    String PRODUCT_CHECKLISTMASTER_GETCHECKLISTMASTERDATA = "masterdata.product_holidayMaster_getHolidayMasterdata";
    String CUSTOMER_CHECKLISTDETAILS_GETCHECKLISTMASTERDATA = "masterdata.customer_holidayDetails_getHolidayMasterdata";
    String CHECKLIST_GETCHELISTMASTERDATA = "masterdata.holiday_getHolidayMasterdata";
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

    String ERRORMANDATORYFIELD = "error.holiday.mandatory_field";

    String OFFICE_NAME = "Office.officeName";
    String OFFICESHORTNAME = "Office.officeShortName";
    String OFFICELEVEL = "Office.officeLevel";
    String OFFICETYPE = "Office.officeType";

    String HOLIDAY_CREATION_EXCEPTION = "exception.holiday.creation";
    String HOLIDAY_MASTERDATA = "holiday_masterData";
    String STATES = "states";
    String HOLIDAY_NAME = "holiday name";
    String HOLIDAY_FROM_DATE = "holiday from date";
    String HOLIDAY_THRU_DATE = "holiday to date";
    String REPAYMENT_RULE = "repayment rule";
    String DETAILS = "details";
    String EXCEPTION_STATE_ALREADY_EXIST = "exceptions.application.holiday.Statusalreadydefined";
    String STATE_COMBO = "displayed when moving into Status";
    String TYPE_COMBO = "type";
    String CENTER_HOLIDAY = "centerholiday";
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

    String HOLIDAYLIST1 = "holidayList1";
    String HOLIDAYLIST2 = "holidayList2";
    String REPAYMENTRULETYPES = "RepaymentRuleType";
    String SELECTED_OFFICE_NAMES = "SelectedOfficeNames";
    String DEPTH_FIRST_HIERARCHY = "depthFirstHeadOfficeHierarchy";
    String HOLIDAY_ACTIONFORM = "holidayActionForm";
    String APPLICABLE_OFFICES = "applicableOffices";
    String HOLIDAY_APPLICABLE_OFFICES = "holiday.ApplicableOffices";
    String ALL_HOLIDAYLIST = "allHolidays";

    String HOLIDAYNAME = "holiday.HolidayName";
    String HOLIDAYFROMDATE = "holiday.HolidayFromDate";
    String HOLIDAYTHRUDATE = "holiday.HolidayThruDate";
    String HOLIDAYOFFICEID = "holiday.HolidayOfficeId";
    String HOLIDAYREPAYMENTRULE = "holiday.HolidayRepaymentRule";

    String INVALIDFROMDATE = "errors.fromdateexception";
    String INVALIDTHRUDATE = "errors.thrudateexception";

    String HOLIDAY_LIST = "holidayList";
    String YEAR = "year";
    String NO_OF_YEARS = "noOfYears";
}
