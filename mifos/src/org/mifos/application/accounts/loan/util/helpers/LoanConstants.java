/**

 * LoanConstants.java    version: xxx

 

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

package org.mifos.application.accounts.loan.util.helpers;

public interface LoanConstants {
	public final String LOANACTIONFORM = "loanActionForm";
	public final String LOANPRDOFFERINGS = "loanPrdOfferings";
	public final String LOANAMOUNT = "amount";
	public final String INTERESTRATE = "Interest rate";
	public final String NO_OF_INST = "No. of installments";
	
	// used as query parameters
	public final String PRDTYPEID = "prdTypeID";
	public final String PRDSTATUS = "prdStatus";
	public final String PRDAPPLICABLEMASTER1 = "prdApplicableMaster1";
	public final String PRDAPPLICABLEMASTER2 = "prdApplicableMaster2";
	public final String CATEGORYID = "categoryId";
	public final String CATEGORYIDVALUE = "5";
	public final String PRDOFFERINGID = "prdOfferingId";
	
	// used in case o optional states.
	public final String ISPENDINGAPPROVAL = "isPendingApproval";

	// used for setting in context
	public final String LOANACCOUNTSTAES = "loanAccountStates";
	public final String APPLICALEFEES = "applicableFees";
	public final String LOANACCGLOBALNUM = "loanAccGlobalNum";
	public final String LOANACCOUNTID = "accountId";
	public final String INTDEDATDISBFLAG = "interestDedAtDisbFlag";
	public final String INPUTPAGE = "inputPage";
	public final String LOANMEETING = "loanMeeting";
	public final String REPAYMENTSCHEDULE = "repaymentSchedule";
	public final String REPAYMENTSCHEDULEINSTALLMENTS = "repaymentScheduleInstallments";
	public final String RECENTACCOUNTACTIVITIES = "recentAccountActivities";
	public final String LOAN_ALL_ACTIVITY_VIEW = "loanAllActivityView";
	public final String VIEW_UPCOMING_INSTALLMENT_DETAILS = "viewUpcomingInstallmentDetails";
	public final String VIEW_OVERDUE_INSTALLMENT_DETAILS = "viewOverDueInstallmentDetails";
	public final String TOTAL_AMOUNT_OVERDUE = "totalAmountOverDue";
	public final String CURRENT_DATE = "currentDate";
	public final String NEXTMEETING_DATE = "nextMeetingDate";
	public final String FEEFORMULALIST = "feeFormulaList";
	public final String CLIENT_LIST = "clientList";
		
	//account status
	
	public static final String LOAN_OFFICERS="loanOfficers";
	public static final String ACCOUNTS_LIST="accountsList";
	public static final String SEARCH_RESULTS="searchResults";
	public static final String STATUS_LIST="statusList";
	public static final String FLAG_LIST="flagList";
	public static final String LOAN_STATUS_VO ="LoanStatusVO";
	public static final String LOAN_STATUS_ACTION ="LoanStatusAction";
	
	public static final String OLD_STATUS_NAME ="oldStatusName";
	public static final String NEW_STATUS_NAME ="newStatusName";
	public static final String FLAG_NAME ="flagName";
	public static final String CHECKLIST ="checklist";
	public static final String GET_STATUS_HISTORY ="getStatusHistory";
	public static final String SEARCH_SUCCESS ="search_success";
	public static final String STATUS_HISTORY ="statusHistory";
	public static final String MANDATORY="errors.mandatory";
	public static final short COMMENT_LENGTH = 500;
	public static final String MAX_LENGTH="errors.maximumlength";
	public static final String NOTES="notes";
	public static final String MANDATORY_SELECT="errors.mandatoryselect";
	public static final short CANCELLED=10;
	public static final short CLOSED=11;
	public static final String FLAG="flag";
	public static final String MANDATORY_TEXTBOX="errors.mandatory_textbox";
	
	//view change log
	public static final String LOAN_CHANGE_LOG="LoanChangeLog";
	public static final String LOAN_CHANGE_LOG_LIST="LoanChangeLogList";
	public static final String CHANGELOGLIST="changeLogList";
	public static final short LOAN_ENTITY_TYPE=7;
	public static final String INCOMPLETE_CHECKLIST="error.incompletechecklist";
	public static final String LOANACCOUNTOWNERISAGROUP = "loanaccountownerisagroup";
	public static final String LOANACCOUNTOWNERISACLIENT = "loanaccountownerisaclient";
	
	//disburse loan
	public static final String GET_LOAD_DISBURSEMENT_DATA="getDataOnLoadOfDisbursment";
	public static final String DISBURSE_LOAN="disburseLoan";
	
	
	//View installments details
	public static final String GET_INSTALLMENT_DETAILS="getInstallmentDetails";
	public static final String WAIVE="waive";
	
	public static final String VIEWINSTALLMENTDETAILS_SUCCESS="viewInstmentDetails_success";
	
	//Apply adjustments
	public static final String LOAD_ADJUSTMENTS="loadAdjustments";
	public static final String MAKE_ADJUSTMENTS="makeAdjustments";

    //Redo Loan
    public static final String PERSPECTIVE_VALUE_REDO_LOAN="redoLoan";

    //Repay Loan
	public static final String LOAD_EARLY_REPAYMENT_DETAILS="loadEarlyRepaymentDetails";
	public static final String MAKE_EARLY_REPAYMENT_DETAILS="makeEarlyRepaymentDetails";
	
	public static final Short PAYMENT_RECIEVED=1; //check
	public static final Short PAYMENT_NOT_RECIEVED=0;
	
	public static final Short INTEREST_DEDUCTED_AT_DISBURSMENT=1;
	
	public static final String TOTAL_REPAYMENT_AMOUNT="totalRepaymentAmount";
	public static final String DONOT_PAY_FEES_PENALTY_INTEREST="doNotPayFeesPenaltyAndInterest";
	public static final String PAY_FEES_PENALTY_INTEREST="payFeesPenaltyAndInterest";
	
	public static final String STATUS_CHANGE_NOT_ALLOWED="error.statuschangenotallowed";
	public static final String FUTUREDISBURSALDATE="FutureDate";
	public static final String HANDLE_CHANGE_IN_PRODUCT_DEFINITION="exceptions.application.loan.changeInLoanOffering";
	
	public static final String ERROR="error";
	
	public static final String LOANACCOUNTOWNER="loanAccountOwner";
	public static final String CUSTOMER="customer";
	public static final String CUSTOMERNOTSELECTEDERROR="errors.customernotselected";
	public static final String LOANOFFERINGNOTSELECTEDERROR="errors.loanofferingnotselected";
	public static final String INSTANCENAME="instance name";
	public static final String LOANOFFERING="loanOffering";
	public static final String LOANFUNDS="loanfunds";
	public static final String GRACEPERIODDURATION="Grace period for repayments";
	public static final String GRACEPERIODERROR="errors.graceper";
	public static final String ADDITIONAL_FEES_LIST = "additionalFeeList";
	public static final String ERRORS_DUPLICATE_PERIODIC_FEE="errors.loan.duplicatePeriodicFee";
	public static final String ERRORS_SPECIFY_FEE_AMOUNT="errors.loan.specifyFeeAmount";
	public static final String FEE ="Fee";
	public static final String PROPOSEDDISBDATE ="proposedDisbDate";
	public static final String RECURRENCEID="recurrenceId";
	public static final String RECURRENCENAME="recurrenceName";
 	
	public static final String NOSEARCHRESULTS ="errors.nosearchresults";
	public final String FEE_WAIVED = "Fee waived";
	public final String PENALTY_WAIVED = "Penalty waived";
	public final String CUSTOM_FIELDS = "customFields";
	public final String ERRORS_SPECIFY_CUSTOM_FIELD_VALUE = "errors.Account.specifyCustomFieldValue";
	
	public final String MULTIPLE_LOANS_ACTION_FORM = "multipleloansactionform";
	public final String MULTIPLE_LOANS_OFFICES_LIST = "multipleloansofficeslist";
	public final String MULTIPLE_LOANS_LOAN_OFFICERS_LIST = "multipleloansloanofficerslist";
	public String IS_CENTER_HEIRARCHY_EXISTS = "isCenterHeirarchyExists";
	public String MULTIPLE_LOANS_CENTERS_LIST="multipleloanscenterslist";
	public String LOANOFFICERS="Loan officer";
	public String LOAN_AMOUNT_FOR="Loan Amount for ";
	public String APPL_RECORDS="applicableRecords";
	
	public final String REVERSE_LOAN_DIBURSAL_ACTION_FORM = "reverseloandisbactionform";
	public final String LOANACTIVEINGOODSTAND = "loanactiveingoodstand";
	public final String LOANACTIVEINBADSTAND = "loanactiveinbadstand";
	public final String ACCOUNTTYPE_ID = "accounttype_id";
	public static final String ERROR_LOAN_ACCOUNT_ID="errors.specifyloanaccountId";
	public static final String NOTE="Note";
	public static final String PAYMENTS_LIST="payments";
	public static final String PAYMENTS_SIZE="paymentssize";
	public static final String BRANCHID="branchId";
	public static final String LOANOFFICERID="loanOfficerId";
	public static final String LOANPRODUCTID="loanProductId";
	public static final String LOAN_INDIVIDUAL_MONITORING_IS_ENABLED="loanIndividualMonitoringIsEnabled";
	/**
	 * Toggles whether or not the <strong><em>disbursement</em></strong> day
	 * for a loan can be different from the meeting day.
	 */
	static final String REPAYMENT_SCHEDULES_INDEPENDENT_OF_MEETING_IS_ENABLED="repaymentSchedulesIndependentOfMeetingIsEnabled";
	static final String MIN_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY="minDaysBetweenDisbursalAndFirstRepaymentDay";
	static final String MAX_DAYS_BETWEEN_DISBURSAL_AND_FIRST_REPAYMENT_DAY="maxDaysBetweenDisbursalAndFirstRepaymentDay";
	static final String ADMINISTRATIVE_DOCUMENT_IS_ENABLED="AdministrativeDocumentsIsEnabled";
	public static final String MIN_RANGE_IS_NOT_MET="errors.minrangeisnotmet";
	public static final String MAX_RANGE_IS_NOT_MET="errors.maxrangeisnotmet";
	public static final String METHODCALLED="methodCalled";
	
	//Configurable mandatory fields
	public final String PURPOSE_OF_LOAN = "PurposeOfLoan";	
	public final String DISPLAY_DAY_NUMBER="displayDayNumber";
	public final String LOANACCOUNT = "loanBO";
	public static final int GLIM_ENABLED_VALUE = 1;

}
