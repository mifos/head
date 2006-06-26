/**

 * BulkEntryConstants.java    version: 1.0

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.application.bulkentry.util.helpers;

public interface BulkEntryConstants {

	public String BULKENTRY="BulkEntry";
	public String BULKENTRYPATH="BulkEntryAction";
	public String BULKENTRYACTIONFORM="bulkentryactionform";
	
	public String LOADLOANOFFICERS="loadLoanOfficers";
	public String LOADCUSTOMERS="loadCustomers";
	
	public String LOADSUCCESS="load_success";
	public String GETSUCCESS="get_success";
	public String PREVIEWSUCCESS="preview_success";
	public String CREATESUCCESS="create_success";
	public String PREVIUOSSUCCESS="previuos_success";
	
	public String BRANCHOFFICESLIST="BranchOfficesList";
	public String LOANOFFICERSLIST="LoanOfficersList";
	public String CUSTOMERSLIST="CustomersList";
	
	public String MEETINGDATE="MeetingDate";
	public String PRODUCTSLIST="ProductsList";
	public String ACCOUNTSLIST="AccountsList";
	public String CUSTOMERCOUNT="CustomerCount";
	public String PRODUCTSIZE="ProductsSize";
	public String CUSTOMERATTENDANCETYPES="CustAttTypes";
	public String PAYMENT_TYPES_LIST="PaymentTypesList";
	public String SAVINGSPRODUCTSLIST="SavingsProductsList";
	
	public Short LOANDISB=1;
	public Short LOANCOLL=2;
	public Short CUSTOMERCOLL=3;
	public Short SAVINGSCOLL=4;
	
	public String METHOD="method";
	public String PREVIEWMETHOD="preview";
	public String LOADMETHOD="load";
	public String CANCELMETHOD="cancel";
	public String GETMETHOD="get";
	public String PREVIOUSMETHOD="previous";
	public String CREATEMETHOD="create";
	public String VALIDATEMETHOD="validate";
	
	public String BRANCH="branch";
	public String LOANOFFICERS="Loan officer";
	public String CENTER="Center";
	public String DATEOFTRXN="Date of transaction";    
	public String MODEOFPAYMENT="Mode of payment";
	public String RECEIPTID="Receipt ID";
	public String RECEIPTDATE="Receipt date";
	
	public String BULKENTRYINVALIDAMOUNT="errors.invalidamount";
	public String ERRORINVALIDAMOUNT="errors.invalidamountvalue";
	public String MANDATORYFIELDS="errors.mandatoryselect";
	public String MANDATORYENTER="errors.mandatoryenter";
	public String INVALIDENDDATE="errors.invaliddate";
	public String MEETINGDATEEXCEPTION = "errors.meetingdate";
	public String ERRORSUPDATE = "errors.update";
	public String REFRESH = "refresh";
	
	public String PRESENT="1";
	
	public Short LOANREPAYMENT = 1;
	public Short SAVINGSDEPOSIT = 2;
	public Short LOANDISBURSEMENT = 3;
	public Short SAVINGSWITHDRAWAL = 4;
	
	public String ISBACKDATEDTRXNALLOWED = "isBackDatedTrxnAllowed";
	public String ISCENTERHEIRARCHYEXISTS = "isCenterHeirarchyExists";
	
}
