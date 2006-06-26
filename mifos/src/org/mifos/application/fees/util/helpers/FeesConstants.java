/* FeesConstants.java    version: xxx



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


package org.mifos.application.fees.util.helpers;

public interface FeesConstants {
	
	//Constants being used in to name the collection in masterdatareterival
	public static final String LOCALEID="localeId";
	public static final String CATAGORY="catagory"; 
	public static final String PAYMENT="payment";
	public static final String FORMULA="formula";
	public static final String STATUS="status";
	//Master data Entity Name
	public static final String FEECATEGORY = "CategoryType";
	public static final String FEEFORMULA="FeeFormulaMaster";
	public static final String FEEPAYMENT="FeePayment";
	public static final String FEESTATUS="FeeStatus";
	
	//Status  
	public static  final Short STATUS_ACTIVE=1;
	public static  final Short STATUS_INACTIVE=2;
	
	//Used in RateFlatFlag
	public static final String AMOUNT="0";
	public static final String RATE="1";
	
	//Fee frequency
	public static final Short PERIODIC=1;
	public static final Short ONETIME=2;
	
	//Fees valueobject name
	public static final String FEES="fees";
	
	//Fees CategoryTypes
	public static final String ALLCUSTOMERS="1";
	public static final String CLIENT="2";
	public static final String GROUP="3";
	public static final String CENTER="4";
	public static final String LOAN="5";
	
	//Default Admin
	public static final String YES="Yes";
	public static final String NO="No";
	
	
	//Admin level for different categories
	public static final Short LEVEL_ID_CLIENT=1;
	public static final Short LEVEL_ID_GRUOP=2;
	public static final Short LEVEL_ID_CENTER=3;
	
	//PaymentId 
	public static final Short UPFRONT=1;
	public static final Short TIME_OF_DISBURSMENT=2;
	public static final Short TIME_OF_FIRSTLOANREPAYMENT=3;
	
	//Meeting Type
	public static final Short WEEKLY=1;
	public static final Short MONTHLY=2;
	public static final Short FEEMEETING=5;
	
	
	//Constansts used in Action Forwards
	public static final String VIEWFEES="viewFees";
	public static final String CREATEFEES="createFees";
	public static final String CREATEFEESPREVIEW="createFeesPreview";
	public static final String CREATEFEESCONFIRMATION="createFeesConfirmation";
	public static final String ADMIN="admin";
	public static final String EDITFEEDETAILS="editFeeDetails";
	public static final String FEEDETAILS="feeDetails";
	public static final String VIEWEDITFEES="viewEditFees";
	public static final String PREVIEWFEEDETAILS="previewFeeDetails";
	
	//Errors
	public static final String VERSIONNOMATCHINGPROBLEM="error.versionnodonotmatch";
	public static final String AMOUNTCANNOTBEZERO="error.amountCannotBeNull";

	
	//M2 style conversion
	public static final String METHOD="method";
	public static final String PREVIEW_METHOD="preview";
	public static final String EDITPREVIEW_METHOD="editPreview";
	public static final String LOADSUCCESS="load_success";
	public static final String PREVIEWSUCCESS="preview_success";
	public static final String PREVIEWFAILURE="preview_failure";
	public static final String EDITPREVIEWFAILURE="editpreview_failure";
	public static final String PREVIOUSSUCCESS="previous_success";
	public static final String CREATESUCCESS="create_success";
	public static final String MANAGESUCCESS="manage_success";
	public static final String EDITPREVIEWSUCCESS="editpreview_success";
	public static final String EDITPREVIOUSSUCCESS="editprevious_success";
	public static final String UPDATESUCCESS="update_success";
	public static final String GLCODE_LIST="glCodeList";
	public static final String PAYMENTID="paymentId";
	public static final String LOANTIMEOFCHARGES="LoanTimeOfCharges";
	public static final String CUSTOMERTIMEOFCHARGES="CustomerTimeOfCharges";
	public static final String CATEGORYLIST="CategoryList";
	public static final String FORMULALIST="FormulaList";
	public static final String STATUSLIST="StatusList";
	
	public static final String FEES_INACTIVE="2";
	
}
