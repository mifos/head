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
package org.mifos.platform.cashflow.ui.model;

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.binding.validation.ValidationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static java.text.MessageFormat.format;

public class CashFlowValidator {

    public void validateCaptureCashFlow(CashFlowForm cashFlow, ValidationContext context) {
        MessageContext messageContext = context.getMessageContext();
        for (MonthlyCashFlowForm monthlyCashFlowForm : cashFlow.getMonthlyCashFlows()) {
            validateExpense(messageContext, monthlyCashFlowForm);
            validateRevenue(messageContext, monthlyCashFlowForm);
            validateNotes(messageContext, monthlyCashFlowForm);
        }
        validateCumulativeCashFlow(cashFlow, messageContext);
        validateTotalCapitalAndLiability(cashFlow, messageContext);
        validateIndebtednessRatio(cashFlow, messageContext);
        setTotalsOnCashFlowForm(cashFlow, messageContext);
    }

    private void validateCumulativeCashFlow(CashFlowForm cashFlow, MessageContext messageContext) {
        if (!messageContext.hasErrorMessages()) {
            for (MonthlyCashFlowForm monthlyCashFlowForm : cashFlow.getMonthlyCashFlows()) {
                validateCumulativeCashFlow(messageContext, monthlyCashFlowForm);
            }
        }
    }

    private void validateCumulativeCashFlow(MessageContext messageContext, MonthlyCashFlowForm monthlyCashFlowForm) {
        if (monthlyCashFlowForm.cumulativeCashFlowIsLessThanOrEqualToZero()) {
            String message = format("Cumulative cash flow for {0} {1} should be greater than zero", monthlyCashFlowForm.getMonthInLocale(),
                    Integer.toString(monthlyCashFlowForm.getYear()));
            constructErrorMessage(CashFlowConstants.CUMULATIVE_CASH_FLOW_FOR_MONTH_SHOULD_BE_GREATER_THAN_ZERO, message, messageContext,
                    monthlyCashFlowForm.getMonthInLocale(), Integer.toString(monthlyCashFlowForm.getYear()));
        }
    }

    private void setTotalsOnCashFlowForm(CashFlowForm cashFlowForm, MessageContext messageContext) {
        if (!messageContext.hasErrorMessages()) {
            BigDecimal totalExpenses = BigDecimal.ZERO, totalRevenues = BigDecimal.ZERO;
            for (MonthlyCashFlowForm monthlyCashFlowForm : cashFlowForm.getMonthlyCashFlows()) {
                totalExpenses = totalExpenses.add(monthlyCashFlowForm.getExpense());
                totalRevenues = totalRevenues.add(monthlyCashFlowForm.getRevenue());
            }
            cashFlowForm.setTotalExpenses(totalExpenses);
            cashFlowForm.setTotalRevenues(totalRevenues);
        }
    }

    private void validateTotalCapitalAndLiability(CashFlowForm cashFlow, MessageContext messageContext) {
        if (cashFlow.isCaptureCapitalLiabilityInfo()) {
            validateTotalCapital(messageContext, cashFlow.getTotalCapital());
            validateTotalLiability(messageContext, cashFlow.getTotalLiability());
        }
    }

    private void validateIndebtednessRatio(CashFlowForm cashFlowForm, MessageContext messageContext) {
        if (cashFlowForm.shouldForValidateIndebtednessRate()) {
            Double indebtednessRatio = cashFlowForm.getIndebtednessRatio();
            BigDecimal loanAmount = cashFlowForm.getLoanAmount();
            BigDecimal totalCapital = cashFlowForm.getTotalCapital();
            BigDecimal totalLiability = cashFlowForm.getTotalLiability();
            Double calculatedIndebtednessRatio = totalLiability.add(loanAmount).multiply(CashFlowConstants.HUNDRED).
                    divide(totalCapital,2,RoundingMode.HALF_EVEN).doubleValue();
            if (calculatedIndebtednessRatio >= indebtednessRatio) {
                String message = format("Indebtedness rate of the client is {0} which should be lesser than the allowable value of {1}",
                        calculatedIndebtednessRatio, indebtednessRatio);
                constructErrorMessage(CashFlowConstants.INDEBTEDNESS_RATIO_MORE_THAN_ALLOWED, message, messageContext, calculatedIndebtednessRatio, indebtednessRatio);
            }
        }
    }

    private void validateTotalCapital(MessageContext messageContext, BigDecimal totalCapital) {
        if (isNull(totalCapital)) {
            String message = format("Please specify the total capital");
            constructErrorMessage(CashFlowConstants.TOTAL_CAPITAL_SHOULD_NOT_BE_EMPTY, message, messageContext);
            return;
        }

        if ((totalCapital.doubleValue() <= 0)) {
            String message = format("Total Capital needs to be a value greater than zero");
            constructErrorMessage(CashFlowConstants.TOTAL_CAPITAL_SHOULD_BE_GREATER_THAN_ZERO, message, messageContext);
        }
    }

    private void validateTotalLiability(MessageContext messageContext, BigDecimal totalLiability) {
        if (isNull(totalLiability)) {
            String message = format("Please specify the total liability");
            constructErrorMessage(CashFlowConstants.TOTAL_LIABILITY_SHOULD_NOT_BE_EMPTY, message, messageContext);
            return;
        }
        if (totalLiability.doubleValue() < 0) {
            String message = format("Total Liability needs to be non negative");
            constructErrorMessage(CashFlowConstants.TOTAL_LIABILITY_SHOULD_BE_NON_NEGATIVE, message, messageContext);
        }
    }

    private void validateExpense(MessageContext messageContext, MonthlyCashFlowForm monthlyCashFlowForm) {
        if (monthlyCashFlowForm.hasNoExpense()) {
            String message = format("Please specify expense for {0} {1}.",
                    Integer.toString(monthlyCashFlowForm.getYear()));
            constructErrorMessage(CashFlowConstants.EMPTY_EXPENSE, message, messageContext, Integer.toString(monthlyCashFlowForm.getYear()));
        }
    }

    private void validateRevenue(MessageContext messageContext, MonthlyCashFlowForm monthlyCashFlowForm) {
        if (monthlyCashFlowForm.hasNoRevenue()) {
            String message = format("Please specify revenue for {0} {1}.",
                    Integer.toString(monthlyCashFlowForm.getYear()));
            constructErrorMessage(CashFlowConstants.EMPTY_REVENUE, message, messageContext, Integer.toString(monthlyCashFlowForm.getYear()));
        }
    }

    private void validateNotes(MessageContext messageContext, MonthlyCashFlowForm monthlyCashFlowForm) {
        if (!StringUtils.isEmpty(monthlyCashFlowForm.getNotes()) && monthlyCashFlowForm.getNotes().length() > 300) {
            String message = format("Notes should be less than 300 characters for {0} {1}.",
                    Integer.toString(monthlyCashFlowForm.getYear()));
            constructErrorMessage(CashFlowConstants.EMPTY_NOTES, message, messageContext, Integer.toString(monthlyCashFlowForm.getYear()));
        }
    }

    private boolean isNull(BigDecimal value) {
        return value == null;
    }

    void constructErrorMessage(String code, String message, MessageContext context, Object... args) {
        MessageResolver messageResolver = new MessageBuilder().error().code(code).defaultText(message).args(args).build();
        context.addMessage(messageResolver);
    }
}
