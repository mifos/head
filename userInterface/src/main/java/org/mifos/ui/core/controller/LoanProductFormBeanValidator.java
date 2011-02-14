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

package org.mifos.ui.core.controller;

import java.util.Set;

import javax.validation.ConstraintViolation;

import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops") // suppress optimisation rule
public class LoanProductFormBeanValidator {

    private LocalValidatorFactoryBean validator;

    public void validate(LoanProductFormBean loanProductFormBean, BindingResult result) {
        validateLoanAmount(loanProductFormBean, result);
        validateInterestRateRange(loanProductFormBean, result);
        validateInstallments(loanProductFormBean, result);
    }

    private void validateInterestRateRange(LoanProductFormBean loanProductFormBean, BindingResult result) {
        if (!result.hasErrors()) {
            if (loanProductFormBean.getMinInterestRate() > loanProductFormBean.getMaxInterestRate()) {
                ObjectError error = new ObjectError("loanProduct",
                        new String[] { "Range.loanProduct.maxInterestRate" }, new Object[] {},
                        "The min must be less than max.");
                result.addError(error);
            }

            if (loanProductFormBean.getDefaultInterestRate() < loanProductFormBean.getMinInterestRate()
                    || loanProductFormBean.getDefaultInterestRate() > loanProductFormBean.getMaxInterestRate()) {
                ObjectError error = new ObjectError("loanProduct",
                        new String[] { "Range.loanProduct.defaultInterestRate" }, new Object[] {},
                        "The min must be less than max.");
                result.addError(error);
            }
        }
    }

    private void validateInstallments(LoanProductFormBean loanProductFormBean, BindingResult result) {
        Integer loanAmountType = Integer.valueOf(loanProductFormBean.getSelectedInstallmentsCalculationType());
        switch (loanAmountType) {
        case 1:
            SameForAllLoanBean sameForAllLoanBean = loanProductFormBean.getInstallmentsSameForAllLoans();
            Set<ConstraintViolation<SameForAllLoanBean>> violations = validator.validate(sameForAllLoanBean);
            convertInstallmentsSameForAllLoansJsr303ViolationsToSpringErrors(result, sameForAllLoanBean, violations);
            break;
        case 2:
            ByLastLoanAmountBean[] byLastLoanAmounts = loanProductFormBean.getInstallmentsByLastLoanAmount();
            int lastLoanRow = 0;
            for (ByLastLoanAmountBean byLoanCycle : byLastLoanAmounts) {
                Set<ConstraintViolation<ByLastLoanAmountBean>> cycleViolations = validator.validate(byLoanCycle);
                convertInstallmentsByLastLoanJsr303ViolationsToSpringErrors(result, lastLoanRow, byLoanCycle,
                        cycleViolations);
                lastLoanRow++;
            }
            break;
        case 3:
            ByLoanCycleBean[] byLoanCycleBeans = loanProductFormBean.getInstallmentsByLoanCycle();
            int loanCycleRow = 0;
            for (ByLoanCycleBean byLoanCycle : byLoanCycleBeans) {
                Set<ConstraintViolation<ByLoanCycleBean>> cycleViolations = validator.validate(byLoanCycle);
                convertInstallmentsByLoanCycleJsr303ViolationsToSpringErrors(result, loanCycleRow, byLoanCycle,
                        cycleViolations);
                loanCycleRow++;
            }

            break;
        default:
            break;
        }
    }

    private void convertInstallmentsByLoanCycleJsr303ViolationsToSpringErrors(BindingResult result, int loanCycleRow,
            ByLoanCycleBean byLoanCycle, Set<ConstraintViolation<ByLoanCycleBean>> cycleViolations) {
        for (ConstraintViolation<ByLoanCycleBean> constraintViolation : cycleViolations) {
            ObjectError error = new ObjectError("loanProduct", new String[] { buildViolationMessage(
                    "loanProduct.installmentsByLoanCycle", constraintViolation) }, new Object[] {loanCycleRow + 1},
                    constraintViolation.getMessage());
            result.addError(error);

        }

        if (cycleViolations.isEmpty() && !byLoanCycle.minIsLessThanMax()) {
            ObjectError error = new ObjectError("loanProduct", new String[] { "Max.loanProduct.installmentsByLoanCycle.max" },
                    new Object[] {loanCycleRow + 1}, "The min must be less than max.");
            result.addError(error);
        }

        if (cycleViolations.isEmpty() && !byLoanCycle.defaultIsBetweenMinAndMax()) {
            ObjectError error = new ObjectError("loanProduct", new String[] { "Max.loanProduct.installmentsByLoanCycle.theDefault" },
                    new Object[] {loanCycleRow + 1}, "The default is not within min and max range.");
            result.addError(error);
        }
    }

    private void convertInstallmentsByLastLoanJsr303ViolationsToSpringErrors(BindingResult result, int lastLoanRow,
            ByLastLoanAmountBean byLoanCycle, Set<ConstraintViolation<ByLastLoanAmountBean>> cycleViolations) {
        for (ConstraintViolation<ByLastLoanAmountBean> constraintViolation : cycleViolations) {
            ObjectError error = new ObjectError("loanProduct", new String[] { buildViolationMessage(
                    "loanProduct.installmentsByLastLoanAmount", constraintViolation) }, new Object[] {lastLoanRow + 1},
                    constraintViolation.getMessage());
            result.addError(error);

        }

        if (cycleViolations.isEmpty() && !byLoanCycle.isRangeValid()) {
            ObjectError error = new ObjectError("loanProduct",
                    new String[] { "Max.loanProduct.installmentsByLastLoanAmount.upper" },
                    new Object[] {lastLoanRow + 1}, "The upper value must be greater than lower.");
            result.addError(error);
        }

        if (cycleViolations.isEmpty() && !byLoanCycle.minIsLessThanMax()) {
            ObjectError error = new ObjectError("loanProduct",
                    new String[] { "Max.loanProduct.installmentsByLastLoanAmount.max" },
                    new Object[] {lastLoanRow + 1}, "The min must be less than max.");
            result.addError(error);
        }

        if (cycleViolations.isEmpty() && !byLoanCycle.defaultIsBetweenMinAndMax()) {
            ObjectError error = new ObjectError("loanProduct",
                    new String[] { "Max.loanProduct.installmentsByLastLoanAmount.theDefault" },
                    new Object[] {lastLoanRow + 1}, "The default is not within min and max range.");
            result.addError(error);
        }
    }

    private void convertInstallmentsSameForAllLoansJsr303ViolationsToSpringErrors(BindingResult result,
            SameForAllLoanBean sameForAllLoanBean, Set<ConstraintViolation<SameForAllLoanBean>> violations) {
        for (ConstraintViolation<SameForAllLoanBean> constraintViolation : violations) {
            ObjectError error = new ObjectError("loanProduct", new String[] { buildViolationMessage(
                    "loanProduct.installmentsSameForAllLoans", constraintViolation) }, new Object[] {},
                    constraintViolation.getMessage());
            result.addError(error);
        }

        if (violations.isEmpty() && !sameForAllLoanBean.minIsLessThanMax()) {
            ObjectError error = new ObjectError("loanProduct",
                    new String[] { "Max.loanProduct.installmentsSameForAllLoans.max" }, new Object[] {},
                    "The min must be less than max.");
            result.addError(error);
        }

        if (violations.isEmpty() && !sameForAllLoanBean.defaultIsBetweenMinAndMax()) {
            ObjectError error = new ObjectError("loanProduct",
                    new String[] { "Max.loanProduct.installmentsSameForAllLoans.theDefault" }, new Object[] {},
                    "The default is not within min and max range.");
            result.addError(error);
        }
    }

    private void validateLoanAmount(LoanProductFormBean loanProductFormBean, BindingResult result) {
        Integer loanAmountType = Integer.valueOf(loanProductFormBean.getSelectedLoanAmountCalculationType());
        switch (loanAmountType) {
        case 1:
            SameForAllLoanBean sameForAllLoanBean = loanProductFormBean.getLoanAmountSameForAllLoans();
            Set<ConstraintViolation<SameForAllLoanBean>> violations = validator.validate(sameForAllLoanBean);

            convertLoanAmountSameForAllLoansJsr303ViolationsToSpringErrors(result, sameForAllLoanBean, violations);

            break;
        case 2:
            ByLastLoanAmountBean[] byLastLoanAmounts = loanProductFormBean.getLoanAmountByLastLoanAmount();
            int lastLoanRow = 0;
            for (ByLastLoanAmountBean byLoanCycle : byLastLoanAmounts) {
                Set<ConstraintViolation<ByLastLoanAmountBean>> cycleViolations = validator.validate(byLoanCycle);

                convertLoanAmountByLastLoanJsr303ViolationsToSpringErrors(result, lastLoanRow, byLoanCycle,
                        cycleViolations);
                lastLoanRow++;
            }
            break;
        case 3:
            ByLoanCycleBean[] byLoanCycleBeans = loanProductFormBean.getLoanAmountByLoanCycle();
            int loanCycleRow = 0;
            for (ByLoanCycleBean byLoanCycle : byLoanCycleBeans) {
                Set<ConstraintViolation<ByLoanCycleBean>> cycleViolations = validator.validate(byLoanCycle);
                convertLoanAmountByLoanCycleJsr303ViolationsToSpringErrors(result, loanCycleRow, byLoanCycle,
                        cycleViolations);
                loanCycleRow++;
            }

            break;
        default:
            break;
        }
    }

    private void convertLoanAmountByLoanCycleJsr303ViolationsToSpringErrors(BindingResult result, int loanCycleRow,
            ByLoanCycleBean byLoanCycle, Set<ConstraintViolation<ByLoanCycleBean>> cycleViolations) {
        for (ConstraintViolation<ByLoanCycleBean> constraintViolation : cycleViolations) {
            ObjectError error = new ObjectError("loanProduct", new String[] { buildViolationMessage(
                    "loanProduct.loanAmountByLoanCycle", constraintViolation) }, new Object[] {loanCycleRow + 1},
                    constraintViolation.getMessage());
            result.addError(error);

        }

        if (cycleViolations.isEmpty() && !byLoanCycle.minIsLessThanMax()) {
            ObjectError error = new ObjectError("loanProduct", new String[] { "Max.loanProduct.loanAmountByLoanCycle.max" },
                    new Object[] {loanCycleRow + 1}, "The min must be less than max.");
            result.addError(error);
        }

        if (cycleViolations.isEmpty() && !byLoanCycle.defaultIsBetweenMinAndMax()) {
            ObjectError error = new ObjectError("loanProduct", new String[] { "Max.loanProduct.loanAmountByLoanCycle.theDefault" },
                    new Object[] {loanCycleRow + 1}, "The default is not within min and max range.");
            result.addError(error);
        }
    }

    private void convertLoanAmountByLastLoanJsr303ViolationsToSpringErrors(BindingResult result, int lastLoanRow,
            ByLastLoanAmountBean byLoanCycle, Set<ConstraintViolation<ByLastLoanAmountBean>> cycleViolations) {
        for (ConstraintViolation<ByLastLoanAmountBean> constraintViolation : cycleViolations) {
            ObjectError error = new ObjectError("loanProduct", new String[] { buildViolationMessage(
                    "loanProduct.loanAmountByLastLoanAmount", constraintViolation) }, new Object[] {lastLoanRow+1},
                    constraintViolation.getMessage());
            result.addError(error);

        }

        if (cycleViolations.isEmpty() && !byLoanCycle.isRangeValid()) {
            ObjectError error = new ObjectError("loanProduct",
                    new String[] { "Max.loanProduct.loanAmountByLastLoanAmount.upper" },
                    new Object[] {lastLoanRow + 1}, "The upper value must be greater than lower.");
            result.addError(error);
        }

        if (cycleViolations.isEmpty() && !byLoanCycle.minIsLessThanMax()) {
            ObjectError error = new ObjectError("loanProduct",
                    new String[] { "Max.loanProduct.loanAmountByLastLoanAmount.max" },
                    new Object[] {lastLoanRow + 1}, "The min must be less than max.");
            result.addError(error);
        }

        if (cycleViolations.isEmpty() && !byLoanCycle.defaultIsBetweenMinAndMax()) {
            ObjectError error = new ObjectError("loanProduct",
                    new String[] { "Max.loanProduct.loanAmountByLastLoanAmount.theDefault" },
                    new Object[] {lastLoanRow + 1}, "The default is not within min and max range.");
            result.addError(error);
        }
    }

    private void convertLoanAmountSameForAllLoansJsr303ViolationsToSpringErrors(BindingResult result,
            SameForAllLoanBean sameForAllLoanBean, Set<ConstraintViolation<SameForAllLoanBean>> violations) {
        for (ConstraintViolation<SameForAllLoanBean> constraintViolation : violations) {
            ObjectError error = new ObjectError("loanProduct", new String[] { buildViolationMessage(
                    "loanProduct.sameForAllLoans", constraintViolation) }, new Object[] {}, constraintViolation
                    .getMessage());
            result.addError(error);
        }

        if (violations.isEmpty() && !sameForAllLoanBean.minIsLessThanMax()) {
            ObjectError error = new ObjectError("loanProduct", new String[] { "Max.loanProduct.sameForAllLoans.max" },
                    new Object[] {}, "The min must be less than max.");
            result.addError(error);
        }

        if (violations.isEmpty() && !sameForAllLoanBean.defaultIsBetweenMinAndMax()) {
            ObjectError error = new ObjectError("loanProduct",
                    new String[] { "Max.loanProduct.sameForAllLoans.theDefault" }, new Object[] {},
                    "The default is not within min and max range.");
            result.addError(error);
        }
    }
    private String buildViolationMessage(String objectName, ConstraintViolation<?> constraintViolation) {
        StringBuilder builder = new StringBuilder();
        String constraintMessage = constraintViolation.getMessageTemplate().substring(30);
        String constraintType = constraintMessage.substring(0, constraintMessage.indexOf('.'));
        builder.append(constraintType).append('.').append(objectName).append('.').append(
                constraintViolation.getPropertyPath().toString());
        return builder.toString();
    }

    public void setValidator(LocalValidatorFactoryBean validator) {
        this.validator = validator;
    }
}
