/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
 * This is the public interface which has the key for all the exceptions thrown from
 * loan module.
 */
public interface LoanExceptionConstants {
    String INVALIDDISBURSEMENTDATE = "exceptions.application.loan.invalidDisbursementDate";
    String DISBURSEMENTDATE_MUST_BE_A_WORKING_DAY = "exceptions.application.loan.disbursementDateMustBeAWorkingDay";
    String DISBURSEMENTDATE_MUST_NOT_BE_IN_A_HOLIDAY = "exceptions.application.loan.disbursementDateMustNotBeInAHoliday";
    String ERROR_INVALIDDISBURSEMENTDATE = "errors.invalidDisbursementDate";
    String ERROR_INVALID_DISBURSEMENT_DATE_FORMAT = "errors.invalidDisbursementDateFormat";
    String ERROR_INVALIDDISBURSEMENTDATE_FOR_REDO_LOAN = "errors.invalidDisbursementDateForRedoLoan";
    String INVALIDTRANSACTIONDATE = "exceptions.application.loan.invalidTransactionDate";
    String EXCESS_PAYMENT = "errors.makePayment";
    String INVALIDTRANSACTIONDATEORDER = "exceptions.application.loan.invalidTransactionDateOrder";
    String INVALIDTRANSACTIONDATEFORPAYMENT = "exceptions.application.loan.invalidTransactionDateForPayment";
    String INVALIDNOOFINSTALLMENTS = "exceptions.application.loan.invalidNoOfInstallments";
    String INCOMPATIBLERECCURENCE = "exceptions.application.loan.incompatibleMeetingrecurrence";
    String INCOMPATIBLEFEERECCURENCE = "exceptions.application.loan.incompatibleFeerecurrence";
    String INVALIDFEEAMNT = "exceptions.application.loan.invalidFeeAmnt";
    String LOANUPDATIONEXCEPTION = "exceptions.application.loan.loanUpdationException";
    String INVALIDLOANFIELD = "exceptions.application.loan.invalidloanfield";
    String DUPLICATEPERIODICFEE = "exceptions.application.loan.duplicatePeriodicFee";
    String NOOFINSTALLMENTSSHOULDBEGREATERTHANONE = "exceptions.application.loan.noOfInstallmentsLessThanTwo";
    String INVALIDFIELD = "exceptions.application.loan.invalidfield";
    String INVALIDMINMAX = "errors.defMinMax";
    String SELECT_ATLEAST_ONE_RECORD = "errors.alleastonerecord";
    String LOANS_CANNOT_COEXIST = "errors.loancouldnotcoexist";
    String LOANS_CANNOT_COEXIST_ACROSS_CUSTOMER_LEVELS = "errors.loancouldnotcoexistacrosscustomerlevels";
    String LOAN_DETAILS_ENTERED_WITHOUT_SELECTING_INDIVIDUAL = "errors.loanandpurposeentredwithoutselectingindividual";
    String NUMBER_OF_SELECTED_MEMBERS_IS_LESS_THAN_TWO = "errors.numberofselectedmembersisnotatleasttwo";
    String SUM_OF_INDIVIDUAL_AMOUNTS_IS_NOT_IN_THE_RANGE_OF_ALLOWED_AMOUNTS = "errors.sumofindividualamountsisnotintherangeofallowedamounts";
    String CUSTOMER_LOAN_AMOUNT_FIELD = "errors.individualamountfield";
    String CUSTOMER_PURPOSE_OF_LOAN_FIELD = "errors.individualpurposeofloanfield";
    String CUSTOMER_SOURCE_OF_FUND_FIELD = "errors.individualsourceoffundfield";
    String CUSTOMER_EXTERNAL_ID_FIELD = "errors.externalid";
    String REPAYMENTDAYISREQUIRED = "errors.repaymentDayIsRequired";
    String REPAYMENTDAY_WRONGFORMAT = "errors.repaymentDayWrongFormat";
    String NO_PARENT_ACCOUNT_EXCEPTION = "errors.loan.does.not.have.parent.account";
}
