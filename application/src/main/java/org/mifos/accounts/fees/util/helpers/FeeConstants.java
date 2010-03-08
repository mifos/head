/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.accounts.fees.util.helpers;

public interface FeeConstants {

    // Constants being used in to name the collection in masterdatareterival
    public static final String LOCALEID = "localeId";
    public static final String CATAGORY = "catagory";
    public static final String PAYMENT = "payment";
    public static final String FORMULA = "formula";
    public static final String STATUS = "status";
    // Master data Entity Name
    public static final String FEECATEGORY = "CategoryType";
    public static final String FEEFORMULA = "FeeFormulaMaster";
    public static final String FEEPAYMENT = "FeePayment";
    public static final String FEESTATUS = "FeeStatus";

    // Fees valueobject name
    public static final String FEES = "fees";
    // Default Admin
    public static final String YES = "Yes";
    public static final String NO = "No";

    // Constansts used in Action Forwards
    public static final String VIEWFEES = "viewFees";
    public static final String CREATEFEES = "createFees";
    public static final String CREATEFEESPREVIEW = "createFeesPreview";
    public static final String CREATEFEESCONFIRMATION = "createFeesConfirmation";
    public static final String ADMIN = "admin";
    public static final String EDITFEEDETAILS = "editFeeDetails";
    public static final String FEEDETAILS = "feeDetails";
    public static final String VIEWEDITFEES = "viewEditFees";
    public static final String PREVIEWFEEDETAILS = "previewFeeDetails";

    // Errors
    public static final String VERSIONNOMATCHINGPROBLEM = "error.versionnodonotmatch";
    public static final String AMOUNTCANNOTBEZERO = "error.amountCannotBeNull";

    public static final String GLCODE_LIST = "glCodeList";
    public static final String PAYMENTID = "paymentId";
    public static final String TIMEOFCHARGES = "TimeOfCharges";
    public static final String CUSTOMERTIMEOFCHARGES = "CustomerTimeOfCharges";
    public static final String CATEGORYLIST = "CategoryList";
    public static final String FORMULALIST = "FormulaList";
    public static final String STATUSLIST = "StatusList";
    public static final String FEE_FREQUENCY_TYPE_LIST = "feeFrequencyTypeList";

    public static final String PRODUCT_FEES = "productFees";
    public static final String CUSTOMER_FEES = "customerFees";

    public static final String INACTIVE = "FEES_INACTIVE";

    public static final String RATE_AND_FORMULA = "RateAndFormula";
    public static final String RATE_OR_AMOUNT = "RateOrAmount";

    // error messages for BO
    public static final String INVALID_FEE_NAME = "errors.Fee.invalidName";
    public static final String INVALID_FEE_CATEGORY = "errors.Fee.invalidCategory";
    public static final String INVALID_GLCODE = "errors.Fee.invalidGLCode";
    public static final String INVALID_FEE_FREQUENCY_TYPE = "errors.Fee.invalidFrequencyType";
    public static final String INVALID_FEE_PAYEMENT_TYPE = "errors.Fee.invalidPaymentType";
    public static final String INVALID_FEE_FREQUENCY = "errors.Fee.invalidFrequency";
    public static final String INVALID_FEE_RATE_OR_FORMULA = "errors.Fee.invalidRateOrFormula";
    public static final String INVALID_FEE_AMOUNT = "errors.Fee.invalidAmount";
    public static final String FEE_CREATE_ERROR = "errors.Fee.create";
    public static final String FEE_UPDATE_ERROR = "errors.Fee.update";
    public static final String INVALID_FEE = "errors.Fee.invalid";
    public static final String RATE = "Fees.error.rate";
    public static final String AMOUNT = "Fees.error.amount";

    // error messages for UI
    public static final String ERRORS_SPECIFY_VALUE = "errors.enter";
    public static final String ERRORS_SPECIFY_AMOUNT_OR_RATE = "errors.amountOrRate";
    public static final String ERRORS_SPECIFY_RATE_AND_FORMULA = "errors.rateAndFormulaId";
    public static final String ERRORS_SELECT_STATUS = "errors.selectStatus";
    public static final String ERRORS_GENERIC = "errors.generic";
    public static final String ERRORS_MUST_BE_GREATER_THAN_ZERO = "errors.mustBeGreaterThanZero";
}
