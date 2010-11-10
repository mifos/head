package org.mifos.ui.core.controller;

import org.joda.time.MutableDateTime;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Date;

/**
 * User: morzechowski@soldevelo.com
 * Date: 2010-10-21
 */
@SuppressWarnings({"PMD.CyclomaticComplexity", "PMD.NPathComplexity", "PMD.ExcessiveMethodLength"})
public class SavingsProductFormValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return SavingsProductFormBean.class.isAssignableFrom(clazz);
    }

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
                formBean.getInterestRate().intValue() > 100) {
            errors.reject("NotNull.savingsProduct.interestRate");
        }

        if (formBean.getSelectedInterestCalculation().trim().isEmpty()) {
            errors.reject("NotEmpty.savingsProduct.selectedInterestCalculation");
        }

        if (null == formBean.getInterestCalculationFrequency()) {
            errors.reject("NotNull.savingsProduct.interestCalculationFrequency");
        }

        if (null == formBean.getInterestPostingMonthlyFrequency() || formBean.getInterestPostingMonthlyFrequency().intValue() < 1) {
            errors.reject("Min.savingsProduct.interestPostingMonthlyFrequency");
        }

        if (formBean.getSelectedPrincipalGlCode().trim().isEmpty()) {
            errors.reject("NotEmpty.savingsProduct.selectedPrincipalGlCode");
        }

        if (formBean.getSelectedInterestGlCode().trim().isEmpty()) {
            errors.reject("NotEmpty.savingsProduct.selectedInterestGlCode");
        }

    }

}
