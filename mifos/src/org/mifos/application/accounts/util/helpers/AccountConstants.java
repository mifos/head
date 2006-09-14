/**

 * AccountConstants.java    version: xxx

 

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
 * This interface stores constants common to all accounts.
 * 
 */
public interface AccountConstants {
	public final String GETPRDOFFERINGS = "getPrdOfferings";
	public final String CUSTOMGETPRDOFFERINGS = "customGetPrdOfferigs";
	public static final String GETPRDOFFERINGS_SUCCESS = "getPrdOfferigs_success";
	
	public static final String GET_PRDOFFERINGS_SUCCESS = "getPrdOfferings_success";
	public static final String PRD_OFFERINGS="prdOfferings";
	public static final String ENTITY_TYPE="entityType";
	// used to add an attribute to business results
	public final String CUSTOMERMASTER = "customerMaster";
	public final String SELECTEDPRDOFFERING = "selectedPrdOffering";
	// used for query parameters in hql
	public final String CUSTOMERID = "customerId";
	public final String PRDTYPEID="prdTypeId";
	public final String PRDSTATUS = "prdStatus";
	public final String PRODUCT_APPLICABLE_TO="prdApplicableMaster";
	public final Object GLOBALACCOUNTNUM = "globalAccountNum";
	
	public final String STATECURRENTLYINUSE = "1";
	
	public final String ACCOUNTSSEARCHDEPENDENCY="AccountsSearch";
	public final String ACCOUNTSAPPLYCHARGESDEPENDENCY="AccountsApplyCharges";
	
	public final String ACCOUNT_TRXN = "AccountTrxn";
	public final String ACCOUNT_GETINSTALLMENTS = "getInstallmentHistory";
	public final String ACCOUNT_TRXN_HISTORY = "getTrxnHistory";
	
	// 	for account notes
	
	public static final String ACCOUNT_NOTES_VO="AccountNotesVO";
	public static final String ACCOUNT_NOTES="AccountNotes";
	public static final String ACCOUNT_NOTE="AccountNote";
	public static final String GET_SUCCESS="get_success";
	public static final String LOAD_SUCCESS="load_success";
	public static final String LOAN_DETAILS_PAGE="loan_details_page";
	public static final String NOTES="notes";
	public static final String STATUS="status";
	public static final String TRXN_TYPES="trxnTypes";
	public static final int NOTES_COUNT=3;
	public static final String METHOD_CANCEL="cancel";
	public static final String METHOD_SEARCH_NEXT="searchNext";
	public static final String METHOD_SEARCH="search";
	public static final String METHOD_SEARCH_PREV="searchPrev";
	public static final String METHOD_LOAD="load";
	public static final String METHOD_GET="get";
	public static final String METHOD_PREVIEW="preview";
	public static final String METHOD_PREVIOUS="previous";
	public static final String METHOD_UPDATE="update";
	public static final short ACTION_LOAN_REPAYMENT=1;
	public static final short ACTION_LOAN_PENALTY=2;
	public static final short ACTION_LOAN_PENALTY_MISC=3;
	public static final short ACTION_FEE_REPAYMENT=4;
	public static final short ACTION_FEE_REPAYMENT_MISC=5;
	public static final short ACTION_SAVINGS_DEPOSIT=6;
	public static final short ACTION_SAVINGS_WITHDRAWAL=7;
	
	
	//Contants for payment and adjusment
	public static final short ACTION_PAYMENT=8;
	public static final short ACTION_LOAN_ADJUSTMENT=9;
	public static final short ACTION_DISBURSAL=10;
	
	public static final short ACTION_SAVINGS_INTEREST_POSTING=11;
	public static final short ACTION_CUSTOMER_ACCOUNT_REPAYMENT=12;
	public static final short ACTION_CUSTOMER_ADJUSTMENT=13;
	
	public static final short ACTION_SAVINGS_ADJUSTMENT=14;
	public static final short ACTION_WRITEOFF=15;
	public static final short ACTION_WAIVEOFFDUE=16;
	public static final short ACTION_WAIVEOFFOVERDUE=17;
	//exceptions
	public static final String UNKNOWN_EXCEPTION="error.unnkownexception";
	
	/**
	 * key string for putting business result account action date
	 */
	public static final String ACCOUNT_ACTION_DATE_KEY = "accountDate";
	/**
	 * key string for putting business result account action payment 
	 */
	public static final String ACCOUNT_PAYMENT_KEY = "accountPayment";
	
	public static final String NO_TRANSACTION_POSSIBLE="error.noTransactionPossible";
	
	String BLANK = "";
	public static final String NO_SEARCH_STRING="errors.nosearchstring";
	public static final String SEARCH_STRING="searchString";

	public final String VIEWCLIENTCHARGES="ViewClientCharges";
	public final String VIEWGROUPCHARGES="ViewGroupCharges";
	public final String VIEWCENTERCHARGES="ViewCenterCharges";
	public final String APPLYLOANCHARGES="reviewTransactionPage";
	public final String FEEFORMULAPATH="org.mifos.application.fees.util.valueobjects.FeeFormula";
	public final String FEEFORMULAID="feeFormulaId";
	public final String KEYNOMEETING="errors.nomeeting";
	public final String UNEXPECTEDERROR="errors.unexpected";
	public final String NOMOREINSTALLMENTS="errors.nomoreinstallments";
	public final String MISC_CHARGE_NOT_APPLICABLE="error.miscchargenotapplicable";
	//Misc Fees and Penalty
	public final String MISC_FEES="-1";
	public final String MISC_PENALTY="-2";
	
	/*Payment Status
		public final Short PAYMENT_UNPAID=0;
		public final Short PAYMENT_PAID=1;
	*/
	
	//Account Fees Status
	public final Short ACTIVE_FEES=1;
	public final Short INACTIVE_FEES=2;
	
	public final String SAVINGS = "savings";
	public static final String CUSTOMERS_FOR_SAVINGS_ACCOUNT="customersForSavingsAccount";
	public static final String GLOBAL_ACCOUNT_NUMBER="globalAccountNumber"; 
	//Errors
	public static final String VERSIONNOMATCHINGPROBLEM="error.versionnodonotmatch";
	
	//AccountTypes
	//public static Short LOAN_TYPE=1;
	//public static Short SAVING_TYPE=2;
	
	//For loan next meeting date, amount due, amount in arrears
	
	public final String LOAN_NEXT_MEETING_DATE="LoanNextMeeetingDate";
	public final String LOAN_AMOUNT_DUE="LoanAmountDue";
	public final String LOAN_AMOUNT_IN_ARREARS="LoanAmountInArrears";
	
	public final String FEES_APPLIED = "charged";
	public final String FEES_REMOVED = "removed";
	public final String MISC_PENALTY_APPLIED = "Misc penalty applied";
	public final String MISC_FEES_APPLIED = "Misc fees applied";
	public final String LAST_PAYMENT_ACTION="lastPaymentAction";
	
	public final String REMOVE_SUCCESS="remove_success";
	public final String REMOVE="remove";
	public final String CHARGES="charges";
	
	public final String ERROR_MANDATORY="errors.mandatory";
	public final String ERROR_FUTUREDATE="errors.futuredate";
	public final String ERROR_INVALID_TRXN="errors.invalidTxndate";
	public final String WAIVE_TYPE="WaiveType";	
	public static final short COMMENT_LENGTH = 500;
	public static final String MAX_LENGTH="errors.maximumlength";
	public static final String APPLICABLE_CHARGE_LIST="applicableChargeList";
	
	public static final String NOT_SUPPORTED_GRACE_TYPE="errors.not_supported_gracetype";
	public static final String INTERESTDEDUCTED_PRINCIPALLAST="errors.interestdedcuted_principallast";
	public static final String PRINCIPALLASTPAYMENT_INVALIDGRACETYPE="errors.principallast_invalidgrace";
	public static final String INTERESTDEDUCTED_INVALIDGRACETYPE="errors.interestdeducted_invalidgrace";
	public static final String NOT_SUPPORTED_EMI_GENERATION="errors.emitype_not_supported";
	public static final String DATES_MISMATCH = "errors.datemismatch";
	public static int INTEREST_DAYS_360 = 360;
	public static int INTEREST_DAYS_365 = 365;

	public static final int INTEREST_DAYS=365;
	public static final int DAYS_IN_WEEK=7;
	public static final int DAYS_IN_MONTH=30;
	
	public static final String WEEK_INSTALLMENT = "Week";
	public static final String MONTH_INSTALLMENT = "Month";
	
	public static final String NOT_SUPPORTED_DURATION_TYPE = "errors.not_supported_durationtype";
	public static final String NOT_SUPPORTED_INTEREST_DAYS = "errors.not_supported_interestdays";
}

