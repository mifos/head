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

package org.mifos.accounts.fees.struts.actionforms;

import java.util.Locale;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeConstants;
import org.mifos.accounts.fees.util.helpers.FeeFormula;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeePayment;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.exceptions.PropertyNotFoundException;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.Money;

public class FeeActionForm extends BaseActionForm {
    private String feeId;

    private String feeName;

    private String categoryType;

    private boolean customerDefaultFee = false;

    private String feeFrequencyType;

    private String feeRecurrenceType;
    
    private Short currencyId;

    private String amount;

    private String rate;

    private String feeFormula;

    private String glCode;

    private String loanCharge;

    private String customerCharge;

    private String weekRecurAfter;

    private String monthRecurAfter;

    private String feeStatus;

    public String getFeeId() {
        return feeId;
    }

    public Short getFeeIdValue() {
        return getShortValue(feeId);
    }

    public void setFeeId(String feeId) {
        this.feeId = feeId;
    }

    public void setCurrencyId(Short currencyId) {
        this.currencyId = currencyId;
    }

    public Short getCurrencyId() {
        return currencyId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public FeeCategory getCategoryTypeValue() throws PropertyNotFoundException {
        return StringUtils.isNotBlank(categoryType) ? FeeCategory.getFeeCategory(Short.valueOf(categoryType)) : null;
    }

    public String getCustomerCharge() {
        return customerCharge;
    }

    public void setCustomerCharge(String customerCharge) {
        this.customerCharge = customerCharge;
    }

    public void setCustomerDefaultFee(boolean customerDefaultFee) {
        this.customerDefaultFee = customerDefaultFee;
    }

    public boolean isCustomerDefaultFee() {
        return customerDefaultFee;
    }

    public String getFeeFormula() {
        return feeFormula;
    }

    public void setFeeFormula(String feeFormula) {
        this.feeFormula = feeFormula;
    }

    public FeeFormula getFeeFormulaValue() throws PropertyNotFoundException {
        return StringUtils.isNotBlank(feeFormula) ? FeeFormula.getFeeFormula(Short.valueOf(feeFormula)) : null;
    }

    public String getFeeFrequencyType() {
        return feeFrequencyType;
    }

    public void setFeeFrequencyType(String feeFrequencyType) {
        this.feeFrequencyType = feeFrequencyType;
    }

    public FeeFrequencyType getFeeFrequencyTypeValue() throws PropertyNotFoundException {
        return StringUtils.isNotBlank(feeFrequencyType) ? FeeFrequencyType.getFeeFrequencyType(Short
                .valueOf(feeFrequencyType)) : null;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public String getFeePaymentType() {
        return StringUtils.isNotBlank(categoryType) && isCategoryLoan() ? loanCharge : customerCharge;
    }

    public FeePayment getFeePaymentTypeValue() throws PropertyNotFoundException {
        return StringUtils.isNotBlank(getFeePaymentType()) ? FeePayment.getFeePayment(Short
                .valueOf(getFeePaymentType())) : null;
    }

    public String getFeeRecurrenceType() {
        return feeRecurrenceType;
    }

    public void setFeeRecurrenceType(String feeRecurrenceType) {
        this.feeRecurrenceType = feeRecurrenceType;
    }

    public RecurrenceType getFeeRecurrenceTypeValue() {
        return StringUtils.isNotBlank(feeRecurrenceType) ? RecurrenceType.fromInt(Short.valueOf(feeRecurrenceType))
                : null;
    }

    public String getFeeStatus() {
        return feeStatus;
    }

    public void setFeeStatus(String feeStatus) {
        this.feeStatus = feeStatus;
    }

    public FeeStatus getFeeStatusValue() {
        return StringUtils.isNotBlank(feeStatus) ? FeeStatus.getFeeStatus(Short.valueOf(feeStatus)) : null;
    }

    public String getGlCode() {
        return glCode;
    }

    public void setGlCode(String glCode) {
        this.glCode = glCode;
    }

    public Short getGlCodeValue() {
        return getShortValue(glCode);
    }

    public String getLoanCharge() {
        return loanCharge;
    }

    public void setLoanCharge(String loanCharge) {
        this.loanCharge = loanCharge;
    }

    public String getMonthRecurAfter() {
        return monthRecurAfter;
    }

    public void setMonthRecurAfter(String monthRecurAfter) {
        this.monthRecurAfter = monthRecurAfter;
    }

    public Short getMonthRecurAfterValue() {
        return getShortValue(monthRecurAfter);
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public Double getRateValue() {
        return getDoubleValue(rate);
    }

    public String getWeekRecurAfter() {
        return weekRecurAfter;
    }

    public void setWeekRecurAfter(String weekRecurAfter) {
        this.weekRecurAfter = weekRecurAfter;
    }

    public Short getWeekRecurAfterValue() {
        return getShortValue(weekRecurAfter);
    }

    public boolean isCategoryLoan() {
        return FeeCategory.LOAN.getValue().equals(Short.valueOf(categoryType));
    }

    public boolean isRateFee() {
        return StringUtils.isNotBlank(rate) && StringUtils.isNotBlank(feeFormula);
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        String method = request.getParameter(Methods.method.toString());
        if (method != null && method.equals(Methods.preview.toString())) {
            customerDefaultFee = false;
            loanCharge = null;
            customerCharge = null;
            amount = null;
            rate = null;
        }
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        Locale locale = getUserContext(request).getPreferredLocale();
        ActionErrors errors = new ActionErrors();
        String methodCalled = request.getParameter(Methods.method.toString());
        if (!methodCalled.equals(Methods.validate.toString())) {
            request.setAttribute("methodCalled", methodCalled);
        } else {
            request.setAttribute("methodCalled", request.getAttribute("methodCalled"));
        }
        if (null != methodCalled) {
            if (methodCalled.equals(Methods.preview.toString())) {
                // fee creation
                errors.add(super.validate(mapping, request));
                validateForPreview(errors, locale);
            } else if (methodCalled.equalsIgnoreCase(Methods.editPreview.toString())) {
                // editing fees
                validateForEditPreview(errors, locale);
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
        }
        return errors;
    }

    /**
     * Used while creating fees.
     */
    private void validateForPreview(ActionErrors errors, Locale locale) {
        if (StringUtils.isNotBlank(getCategoryType()) && isCategoryLoan()) {
            validateForPreviewLoanCategory(errors, locale);
        } else {
            if (StringUtils.isBlank(getAmount())) {
                // neither rate nor amount was specified
                addError(errors, FeeConstants.AMOUNT, FeeConstants.ERRORS_SPECIFY_VALUE);
            } else {
                validateAmount(errors, locale);
            }
        }
        if (getGlCodeValue() == null) {
            addError(errors, FeeConstants.INVALID_GLCODE, FeeConstants.INVALID_GLCODE);
        }
    }

    private void validateForPreviewLoanCategory(ActionErrors errors, Locale locale) {
        if (StringUtils.isNotBlank(getRate()) && StringUtils.isNotBlank(getAmount())) {
            // rate and amount must not both be specified
            addError(errors, FeeConstants.RATE_OR_AMOUNT, FeeConstants.ERRORS_SPECIFY_AMOUNT_OR_RATE);
        } else if (StringUtils.isNotBlank(getRate()) && StringUtils.isNotBlank(getFeeFormula())) {
            validateRate(errors, locale);
        } else if (StringUtils.isNotBlank(getRate()) && StringUtils.isBlank(getFeeFormula())) {
            addError(errors, FeeConstants.RATE_AND_FORMULA, FeeConstants.ERRORS_SPECIFY_RATE_AND_FORMULA);
        } else if (StringUtils.isNotBlank(getAmount())) {
            validateAmount(errors, locale);
        } else {
            // neither rate nor amount was specified
            addError(errors, FeeConstants.RATE_OR_AMOUNT, FeeConstants.ERRORS_SPECIFY_AMOUNT_OR_RATE);
        }
    }

    /**
     * Used while editing fees.
     */
    private void validateForEditPreview(ActionErrors errors, Locale locale) {
        if (StringUtils.isBlank(getRate()) && StringUtils.isBlank(getAmount())) {
            // neither rate nor amount was specified
            addError(errors, FeeConstants.RATE_OR_AMOUNT, FeeConstants.ERRORS_SPECIFY_AMOUNT_OR_RATE);
        } else if (StringUtils.isNotBlank(getRate()) && StringUtils.isNotBlank(getFeeFormula())) {
            validateRate(errors, locale);
        } else {
            validateAmount(errors, locale);
        }

        if (getFeeStatusValue() == null) {
            addError(errors, FeeConstants.AMOUNT, FeeConstants.ERRORS_SELECT_STATUS);
        }
    }

    protected void validateAmount(ActionErrors errors, Locale locale) {
        DoubleConversionResult conversionResult = validateAmount(getAmount(), FeeConstants.AMOUNT, errors, locale,
                FilePaths.FEE_UI_RESOURCE_PROPERTYFILE);
        if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
            addError(errors, FeeConstants.AMOUNT, FeeConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                    lookupLocalizedPropertyValue(FeeConstants.AMOUNT, locale, FilePaths.FEE_UI_RESOURCE_PROPERTYFILE));
        }
    }

    protected void validateRate(ActionErrors errors, Locale locale) {
        DoubleConversionResult conversionResult = validateInterest(getRate(), FeeConstants.RATE, errors, locale,
                FilePaths.FEE_UI_RESOURCE_PROPERTYFILE);
        if (conversionResult.getErrors().size() == 0 && !(conversionResult.getDoubleValue() > 0.0)) {
            addError(errors, FeeConstants.RATE, FeeConstants.ERRORS_MUST_BE_GREATER_THAN_ZERO,
                    lookupLocalizedPropertyValue(FeeConstants.RATE, locale, FilePaths.FEE_UI_RESOURCE_PROPERTYFILE));
        }
    }

}
