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

package org.mifos.accounts.loan.util.helpers;

/**
 * This is the interface which has the key for all the exceptions thrown from
 * loan module.
 */
public interface LoanExceptionConstants {
    public final String INVALIDDISBURSEMENTDATE = "exceptions.application.loan.invalidDisbursementDate";
    public final String DISBURSEMENTDATE_MUST_BE_A_WORKING_DAY = "exceptions.application.loan.disbursementDateMustBeAWorkingDay";
    public final String DISBURSEMENTDATE_MUST_NOT_BE_IN_A_HOLIDAY = "exceptions.application.loan.disbursementDateMustNotBeInAHoliday";
    public final String ERROR_INVALIDDISBURSEMENTDATE = "errors.invalidDisbursementDate";
    public final String ERROR_INVALID_DISBURSEMENT_DATE_FORMAT = "errors.invalidDisbursementDateFormat";
    public final String ERROR_INVALIDDISBURSEMENTDATE_FOR_REDO_LOAN = "errors.invalidDisbursementDateForRedoLoan";
    public final String INVALIDTRANSACTIONDATE = "exceptions.application.loan.invalidTransactionDate";
    public final String INVALIDTRANSACTIONDATEFORPAYMENT = "exceptions.application.loan.invalidTransactionDateForPayment";
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
    public final String LOANS_CANNOT_COEXIST = "errors.loancouldnotcoexist";
    public final String LOANS_CANNOT_COEXIST_ACROSS_CUSTOMER_LEVELS = "errors.loancouldnotcoexistacrosscustomerlevels";
    public final String LOAN_DETAILS_ENTERED_WITHOUT_SELECTING_INDIVIDUAL = "errors.loanandpurposeentredwithoutselectingindividual";
    public final String NUMBER_OF_SELECTED_MEMBERS_IS_LESS_THAN_TWO = "errors.numberofselectedmembersisnotatleasttwo";
    public final String SUM_OF_INDIVIDUAL_AMOUNTS_IS_NOT_IN_THE_RANGE_OF_ALLOWED_AMOUNTS = "errors.sumofindividualamountsisnotintherangeofallowedamounts";
    public final String CUSTOMER_LOAN_AMOUNT_FIELD = "errors.individualamountfield";
    public final String CUSTOMER_PURPOSE_OF_LOAN_FIELD = "errors.individualpurposeofloanfield";
    public final String CUSTOMER_SOURCE_OF_FUND_FIELD = "errors.individualsourceoffundfield";
    public final String REPAYMENTDAYISREQUIRED = "errors.repaymentDayIsRequired";
}
