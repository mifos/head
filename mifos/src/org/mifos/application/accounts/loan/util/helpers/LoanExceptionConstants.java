/**

 * LoanExceptionConstants.java    version: xxx

 

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

/**
 * This is the interface which has the key for all the exceptions thrown
 * from loan module.
 */
public interface LoanExceptionConstants {
	public final String INVALIDDISBURSEMENTDATE = "exceptions.application.loan.invalidDisbursementDate";
	public final String ERROR_INVALIDDISBURSEMENTDATE = "errors.invalidDisbursementDate";
	public final String ERROR_INVALIDDISBURSEMENTDATE_FOR_REDO_LOAN = "errors.invalidDisbursementDateForRedoLoan";
	public final String INVALIDTRANSACTIONDATE = "exceptions.application.loan.invalidTransactionDate";
    public final String INVALIDNOOFINSTALLMENTS = "exceptions.application.loan.invalidNoOfInstallments";
	public final String INCOMPATIBLERECCURENCE = "exceptions.application.loan.incompatibleMeetingrecurrence";
	public final String INCOMPATIBLEFEERECCURENCE = "exceptions.application.loan.incompatibleFeerecurrence";
	public final String INVALIDFEEAMNT = "exceptions.application.loan.invalidFeeAmnt";
	public final String LOANUPDATIONEXCEPTION = "exceptions.application.loan.loanUpdationException";
	public final String INVALIDLOANFIELD = "exceptions.application.loan.invalidloanfield";
	public final String DUPLICATEPERIODICFEE = "exceptions.application.loan.duplicatePeriodicFee";
	public final String NOOFINSTALLMENTSSHOULDBEGREATERTHANONE = "exceptions.application.loan.noOfInstallmentsLessThanTwo";
	public final String INVALIDFIELD = "exceptions.application.loan.invalidfield";
	public final String INVALIDMINMAX = "errors.defMinMax";
	public final String SELECT_ATLEAST_ONE_RECORD = "errors.alleastonerecord";
	public final String LOANCOULDNOTCOEXIST = "errors.loancouldnotcoexist";
	public final String LOANANDPURPOSEENTREDWITHOUTSELECTINGINDIVIDUAL = "errors.loanandpurposeentredwithoutselectingindividual";
	public final String NUMBEROFSELECTEDMEMBERSISNOTATLEASTTWO = "errors.numberofselectedmembersisnotatleasttwo";
	public final String SUMOFINDIVIDUALAMOUNTSISNOTINTHERANGEOFALLOWEDAMOUNTS = "errors.sumofindividualamountsisnotintherangeofallowedamounts";
	public final String CUSTOMERLOANAMOUNTFIELD = "errors.individualamountfield";
	public final String CUSTOMERPURPOSEOFLOANFIELD = "errors.individualpurposeofloanfield";
	public final String REPAYMENTDAYISREQUIRED = "errors.repaymentDayIsRequired";	
}
