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

package org.mifos.accounts.fees.util.helpers;

public interface FeeConstants {

    // Constants being used in to name the collection in masterdatareterival
    String LOCALEID = "localeId";
    String CATAGORY = "catagory";
    String PAYMENT = "payment";
    String FORMULA = "formula";
    String STATUS = "status";
    // Master data Entity Name
    String FEECATEGORY = "CategoryType";
    String FEEFORMULA = "FeeFormulaMaster";
    String FEEPAYMENT = "FeePayment";
    String FEESTATUS = "FeeStatus";

    // Fees valueobject name
    String FEES = "fees";
    // Default Admin
    String YES = "Yes";
    String NO = "No";

    // Constansts used in Action Forwards
    String VIEWFEES = "viewFees";
    String CREATEFEES = "createFees";
    String CREATEFEESPREVIEW = "createFeesPreview";
    String CREATEFEESCONFIRMATION = "createFeesConfirmation";
    String ADMIN = "admin";
    String EDITFEEDETAILS = "editFeeDetails";
    String FEEDETAILS = "feeDetails";
    String VIEWEDITFEES = "viewEditFees";
    String PREVIEWFEEDETAILS = "previewFeeDetails";

    // Errors
    String VERSIONNOMATCHINGPROBLEM = "error.versionnodonotmatch";
    String AMOUNTCANNOTBEZERO = "error.amountCannotBeNull";

    String GLCODE_LIST = "glCodeList";
    String PAYMENTID = "paymentId";
    String TIMEOFCHARGES = "TimeOfCharges";
    String CUSTOMERTIMEOFCHARGES = "CustomerTimeOfCharges";
    String CATEGORYLIST = "CategoryList";
    String FORMULALIST = "FormulaList";
    String STATUSLIST = "StatusList";
    String FEE_FREQUENCY_TYPE_LIST = "feeFrequencyTypeList";

    String PRODUCT_FEES = "productFees";
    String CUSTOMER_FEES = "customerFees";

    String INACTIVE = "FEES_INACTIVE";

    String RATE_AND_FORMULA = "RateAndFormula";
    String RATE_OR_AMOUNT = "RateOrAmount";
    String REMOVE_ACTIVE = "error.RemoveActive";
    
    // warning messages for UI
    String FEE_REMOVED = "Fees.feeRemoved";
    String FEE_REMOVED_FROM_PRD = "Fees.feeRemovedFromPrd";
    String FEE_CANNOT_BE_REMOVED = "Fees.feeCannotBeRemoved";
    
    // error messages for BO
    String INVALID_FEE_NAME = "errors.Fee.invalidName";
    String INVALID_FEE_CATEGORY = "errors.Fee.invalidCategory";
    String INVALID_GLCODE = "errors.Fee.invalidGLCode";
    String INVALID_FEE_FREQUENCY_TYPE = "errors.Fee.invalidFrequencyType";
    String INVALID_FEE_PAYEMENT_TYPE = "errors.Fee.invalidPaymentType";
    String INVALID_FEE_FREQUENCY = "errors.Fee.invalidFrequency";
    String INVALID_FEE_RATE_OR_FORMULA = "errors.Fee.invalidRateOrFormula";
    String INVALID_FEE_AMOUNT = "errors.Fee.invalidAmount";
    String FEE_CREATE_ERROR = "errors.Fee.create";
    String FEE_UPDATE_ERROR = "errors.Fee.update";
    String INVALID_FEE = "errors.Fee.invalid";
    String RATE = "Fees.error.rate";
    String AMOUNT = "Fees.error.amount";

    // error messages for UI
    String ERRORS_SPECIFY_VALUE = "errors.enter";
    String ERRORS_SPECIFY_AMOUNT_OR_RATE = "errors.amountOrRate";
    String ERRORS_SPECIFY_RATE_AND_FORMULA = "errors.rateAndFormulaId";
    String ERRORS_SELECT_STATUS = "errors.selectStatus";
    String ERRORS_GENERIC = "errors.generic";
    String ERRORS_MUST_BE_GREATER_THAN_ZERO = "errors.mustBeGreaterThanZero";
}
