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

package org.mifos.ui.core.controller;

import org.joda.time.MutableDateTime;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;

import java.util.Date;
import java.math.BigDecimal;

@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.ExcessiveMethodLength"})
public class SavingsProductFormValidator implements Validator {
    final private LazyBindingErrorProcessor errorProcessor;

    public SavingsProductFormValidator(LazyBindingErrorProcessor errorProcessor) {
        this.errorProcessor = errorProcessor;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return SavingsProductFormBean.class.isAssignableFrom(clazz);
    }

    @SuppressWarnings({"ThrowableInstanceNeverThrown"})
    @Override
    public void validate(Object target, Errors errors) {

        SavingsProductFormBean formBean = (SavingsProductFormBean) target;

        if (formBean.getGeneralDetails().getName().trim().isEmpty()) {
            errors.reject("NotEmpty.generalDetails.name");
        }

        if (formBean.getGeneralDetails().getShortName().trim().isEmpty()) {
            errors.reject("NotEmpty.generalDetails.shortName");
        }

        if (formBean.getGeneralDetails().getSelectedCategory().trim().isEmpty()) {
            errors.reject("NotEmpty.generalDetails.selectedCategory");
        }

        if (null == formBean.getGeneralDetails().getStartDateDay() ||
                1 > formBean.getGeneralDetails().getStartDateDay() ||
                31 < formBean.getGeneralDetails().getStartDateDay()) {
            errors.reject("Min.generalDetails.startDateDay");
        }

        if (null == formBean.getGeneralDetails().getStartDateMonth() ||
                1 > formBean.getGeneralDetails().getStartDateMonth() ||
                12 < formBean.getGeneralDetails().getStartDateMonth()) {
            errors.reject("Min.generalDetails.startDateMonth");
        }


        if (null == formBean.getGeneralDetails().getStartDateAsDateTime() ||
                !(formBean.getGeneralDetails().getStartDateYear().length() == 4)) {
            errors.reject("Min.generalDetails.startDate");
        } else {
            MutableDateTime nextYear = new MutableDateTime();
            nextYear.setDate(new Date().getTime());
            nextYear.addYears(1);

            if (formBean.getGeneralDetails().getStartDateAsDateTime() != null &&
                    formBean.getGeneralDetails().getStartDateAsDateTime().compareTo(nextYear) > 0) {
                errors.reject("Min.generalDetails.startDate");
            }
        }

        if ((null != formBean.getGeneralDetails().getEndDateDay() ||
                null != formBean.getGeneralDetails().getEndDateMonth() ||
                null != formBean.getGeneralDetails().getEndDateMonth()) &&
                formBean.getGeneralDetails().getEndDateAsDateTime() == null) {
                errors.reject("Min.generalDetails.endDate");
        }

        if (formBean.getGeneralDetails().getStartDateAsDateTime() != null &&
                formBean.getGeneralDetails().getEndDateAsDateTime() != null &&
                formBean.getGeneralDetails().getStartDateAsDateTime().compareTo(formBean.getGeneralDetails().getEndDateAsDateTime()) > 0) {
            errors.reject("Min.generalDetails.endDate");
        }


        if (formBean.getGeneralDetails().getSelectedApplicableFor().trim().isEmpty()) {
            errors.reject("NotEmpty.generalDetails.selectedApplicableFor");
        }

        if (formBean.getSelectedDepositType().trim().isEmpty()) {
            errors.reject("NotEmpty.savingsProduct.selectedDepositType");
        } else if (formBean.isMandatory() && (null == formBean.getAmountForDeposit() || formBean.getAmountForDeposit() <= 0)) {
            errors.reject("Min.savingsProduct.amountForDesposit");
        }

        if (formBean.isGroupSavingAccount() && formBean.getSelectedGroupSavingsApproach().trim().isEmpty()) {
            errors.reject("NotEmpty.savingsProduct.selectedGroupSavingsApproach");
        }

        if (null == formBean.getInterestRate() ||
                formBean.getInterestRate().intValue() < 0 ||
                formBean.getInterestRate().intValue() > 100 ||
                errorProcessor.getRejectedValue("interestRate") != null) {
            if (errorProcessor.getTarget() == null) {
                errors.reject("NotNull.savingsProduct.interestRate");
            }
            else {
                BindException bindException = new BindException(errorProcessor.getTarget(), errorProcessor.getObjectName());
                bindException.addError(new FieldError(errorProcessor.getObjectName(), "interestRate", errorProcessor.getRejectedValue("interestRate"),
                        true, new String[] {"NotNull.savingsProduct.interestRate"}, null, null));
                errors.addAllErrors(bindException);
            }
        }

        if (formBean.getSelectedInterestCalculation().trim().isEmpty()) {
            errors.reject("NotEmpty.savingsProduct.selectedInterestCalculation");
        }

        if (null == formBean.getInterestCalculationFrequency() || formBean.getInterestCalculationFrequency() < 1 ||
                errorProcessor.getRejectedValue("interestCalculationFrequency") != null) {
            if (errorProcessor.getTarget() == null) {
                errors.reject("NotNull.savingsProduct.interestCalculationFrequency");
            }
            else {
                BindException bindException = new BindException(errorProcessor.getTarget(), errorProcessor.getObjectName());
                bindException.addError(new FieldError(errorProcessor.getObjectName(), "interestCalculationFrequency", errorProcessor.getRejectedValue("interestCalculationFrequency"),
                        true, new String[] {"NotNull.savingsProduct.interestCalculationFrequency"}, null, null));
                errors.addAllErrors(bindException);
            }
        }

        if (null == formBean.getInterestPostingMonthlyFrequency() || formBean.getInterestPostingMonthlyFrequency() < 1 ||
                errorProcessor.getRejectedValue("interestPostingMonthlyFrequency") != null) {
            if (errorProcessor.getTarget() == null) {
                errors.reject("Min.savingsProduct.interestPostingMonthlyFrequency");
            }
            else {
                BindException bindException = new BindException(errorProcessor.getTarget(), errorProcessor.getObjectName());
                bindException.addError(new FieldError(errorProcessor.getObjectName(), "interestPostingMonthlyFrequency", errorProcessor.getRejectedValue("interestPostingMonthlyFrequency"),
                        true, new String[] {"Min.savingsProduct.interestPostingMonthlyFrequency"}, null, null));
                errors.addAllErrors(bindException);
            }
        }

        BigDecimal minBalanceForInterestCalculation;
        try {
            minBalanceForInterestCalculation = BigDecimal.valueOf(Double.valueOf(formBean.getMinBalanceRequiredForInterestCalculation()));
        }
        catch (NumberFormatException e) {
            minBalanceForInterestCalculation = new BigDecimal("-1");
        }
        if (minBalanceForInterestCalculation.compareTo(BigDecimal.ZERO) < 0) {
            errors.reject("Min.savingsProduct.balanceRequiredForInterestCalculation");
        }

        if (formBean.getSelectedPrincipalGlCode().trim().isEmpty()) {
            errors.reject("NotEmpty.savingsProduct.selectedPrincipalGlCode");
        }

        if (formBean.getSelectedInterestGlCode().trim().isEmpty()) {
            errors.reject("NotEmpty.savingsProduct.selectedInterestGlCode");
        }

    }

}
