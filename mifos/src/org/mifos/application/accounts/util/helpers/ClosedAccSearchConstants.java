/**

 * ClosedAccSearchConstants.java    version: xxx

 

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
package org.mifos.application.accounts.util.helpers;

/**
 * @author mohammedn
 *
 */
public interface ClosedAccSearchConstants {
	
	public String CLOSEDACCOUNTSLIST="ClosedAccountsList";
	public String CLOSEDLOANACCOUNTSLIST="ClosedLoanAccountsList";
	public String CLOSEDSAVINGSACCOUNTSLIST="ClosedSavingsAccountsList";
	public String CLIENTUPCOMINGFEECHARGESLIST="ClientUpcomingFeeChargesList";
	public String CLIENTRECENTACCACTIVITYLIST="RecentAcctActivityList";
	public String CUSTOMERRECENTACCACTIVITYLIST="AllRecentAcctActivityList";
	public int CUSTOMERRECENTACCACTIVITYLISTSIZE=3;
	public String CLIENTFEECHARGEDUE="ClientFeeChargeDue";
	public String CLIENTFEECHARGEOVERDUE="ClientFeeChargeOverDue";
	public String CLIENTCHANGELOGLIST="ClientChangeLogList";
	public String CUSTOMERID="customerId";
	public String ACCOUNTID="accountId";
	public String VIEWCLIENTCHARGES="ViewClientCharges";
	public String SEARCHCLIENTCHARGESSUCCESS="search_client_charges_success";
	public String VIEWALLACCACTIVITY="viewAllAccActivity";
	public String SEARCHCUSTOMERACTIVITYSUCCESS="search_customer_activity_success";
	public String VIEWGROUPCHARGES="ViewGroupCharges";
	public String SEARCHGROUPCHARGESSUCCESS="search_group_charges_success";
	public String VIEWCENTERCHARGES="ViewCenterCharges";
	public String SEARCHCENTERCHARGESSUCCESS="search_center_charges_success";
	public String VIEWCLIENTCHANGELOG="ViewClientLog";
	public String SEARCHCLIENTLOGSUCCESS="search_client_log_success";
	public String GETPATHCLOSEDACCSEARCH="ClosedAccSearch";
	public String ENTITYTYPE="entityType";
	public Short CLIENTENTITYTYPEID=1;
	public Short GROUPENTITYTYPEID=12;
	public Short CENTERENTITYTYPEID=20;
	public String VIEWGROUPCHANGELOG="ViewGroupChangelog";
	public String SEARCHGROUPCHANGELOGSUCCESS="search_group_log_success";
	public String VIEWCENTERCHANGELOG="ViewCenterChangelog";
	public String SEARCHCENTERCHANGELOGSUCCESS="search_center_log_success";
	public String UPCOMINGCHARGESDATE="UpcomingChargesDate";
	
	public String VIEW_GROUP_CLOSED_ACCOUNTS="ViewGroupClosedAccounts";
	public String VIEW_CLIENT_CLOSED_ACCOUNTS="ViewClientClosedAccounts";
	public String VIEW_CENTER_CLOSED_ACCOUNTS="ViewCenterClosedAccounts";
	public String SEARCH_GROUP_CLOSED_ACCOUNT_SUCCESS="search_group_closed_account_success";
	public String SEARCH_CLIENT_CLOSED_ACCOUNT_SUCCESS="search_client_closed_account_success";
	public String SEARCH_CENTER_CLOSED_ACCOUNT_SUCCESS="search_center_closed_account_success";
	public String GROUP_DETAILS_PAGE="group_details_page";
	public String CENTER_DETAILS_PAGE="center_details_page";
	public String CLIENT_DETAILS_PAGE="client_details_page";
	public String RECURRENCEFEESCHARGESLIST="RecurrenceFeesChargesList";
	
}
