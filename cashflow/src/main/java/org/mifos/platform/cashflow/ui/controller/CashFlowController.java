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
package org.mifos.platform.cashflow.ui.controller;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.cashflow.ui.model.MonthlyCashFlowForm;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.execution.RequestContext;

import java.util.ArrayList;
import java.util.List;

import static java.text.MessageFormat.format;

@Controller
@SessionAttributes(value = {"cashFlow", "joinUrl", "cancelUrl"})
public class CashFlowController {

    public String capture(org.mifos.platform.cashflow.ui.model.CashFlowForm cashFlowForm, RequestContext requestContext, ExternalContext externalContext) {
        String result = "failure";
        if (validate(cashFlowForm, requestContext.getMessageContext())) {
            externalContext.getGlobalSessionMap().put("cashFlow", cashFlowForm);
            result = "success";
        }
        return result;
    }
    
    @SuppressWarnings("PMD.AvoidInstantiatingObjectsInLoops")
    public org.mifos.platform.cashflow.ui.model.CashFlowForm prepareCashFlowForm(int startMonth, int startYear, int noOfMonths) {
        DateTime startMonthYear = new DateTime(startYear, startMonth, 1, 1, 1, 1, 1);
        List<MonthlyCashFlowDetail> monthlyCashFlowDetails = new ArrayList<MonthlyCashFlowDetail>();
        for (int i = 0; i < noOfMonths; i++) {
            monthlyCashFlowDetails.add(new MonthlyCashFlowDetail(startMonthYear, null, null, null));
            startMonthYear = startMonthYear.plusMonths(1);
        }
        return new org.mifos.platform.cashflow.ui.model.CashFlowForm(new CashFlowDetail(monthlyCashFlowDetails));
    }

    public boolean validate(org.mifos.platform.cashflow.ui.model.CashFlowForm cashFlowForm, MessageContext messageContext) {
        List<MonthlyCashFlowForm> monthlyCashFlows = cashFlowForm.getMonthlyCashFlows();
        for (MonthlyCashFlowForm monthlyCashFlowForm : monthlyCashFlows) {
            validateExpense(messageContext, monthlyCashFlowForm);
            validateRevenue(messageContext, monthlyCashFlowForm);
            validateNotes(messageContext, monthlyCashFlowForm);
        }
        return !messageContext.hasErrorMessages();
    }

    private void validateExpense(MessageContext messageContext, MonthlyCashFlowForm monthlyCashFlowForm) {
        if (isNull(monthlyCashFlowForm.getExpense())) {
            String message = format("Please specify expense for {0} {1}.", monthlyCashFlowForm.getMonth(),
                    Integer.toString(monthlyCashFlowForm.getYear()));
            constructErrorMessage(CashFlowConstants.EMPTY_EXPENSE, message, messageContext,
                    monthlyCashFlowForm.getMonth(), Integer.toString(monthlyCashFlowForm.getYear()));
        }
    }

    private void validateRevenue(MessageContext messageContext, MonthlyCashFlowForm monthlyCashFlowForm) {
        if (isNull(monthlyCashFlowForm.getRevenue())) {
            String message = format("Please specify revenue for {0} {1}.", monthlyCashFlowForm.getMonth(),
                    Integer.toString(monthlyCashFlowForm.getYear()));
            constructErrorMessage(CashFlowConstants.EMPTY_REVENUE, message, messageContext,
                    monthlyCashFlowForm.getMonth(), Integer.toString(monthlyCashFlowForm.getYear()));
        }
    }

    private void validateNotes(MessageContext messageContext, MonthlyCashFlowForm monthlyCashFlowForm) {
        if (isEmpty(monthlyCashFlowForm.getNotes())) {
            String message = format("Please specify notes for {0} {1}.", monthlyCashFlowForm.getMonth(),
                    Integer.toString(monthlyCashFlowForm.getYear()));
            constructErrorMessage(CashFlowConstants.EMPTY_NOTES, message, messageContext,
                    monthlyCashFlowForm.getMonth(), Integer.toString(monthlyCashFlowForm.getYear()));
        }
    }

    private boolean isEmpty(String notes) {
        return StringUtils.isEmpty(notes);
    }

    private boolean isNull(Double value) {
        return value == null;
    }

    void constructErrorMessage(String code, String message, MessageContext context, Object... args) {
        MessageResolver messageResolver = new MessageBuilder().error().code(code).defaultText(message).args(args).build();
        context.addMessage(messageResolver);
    }

}
