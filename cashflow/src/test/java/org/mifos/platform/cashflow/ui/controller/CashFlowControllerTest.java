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


import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.cashflow.CashFlowConstants;
import org.mifos.platform.cashflow.builder.CashFlowDetailsBuilder;
import org.mifos.platform.cashflow.matchers.CashFlowFormMatcher;
import org.mifos.platform.cashflow.service.CashFlowDetail;
import org.mifos.platform.cashflow.service.MonthlyCashFlowDetail;
import org.mifos.platform.matchers.MessageMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;
import org.springframework.webflow.context.ExternalContext;
import org.springframework.webflow.core.collection.SharedAttributeMap;
import org.springframework.webflow.execution.RequestContext;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CashFlowControllerTest {
    @Mock
    RequestContext requestContext;
    @Mock
    ExternalContext externalContext;
    @Mock
    SharedAttributeMap sharedAttributeMap;
    @Mock
    MessageContext messageContext;

    @Test
    public void captureCashFlow() {
        Assert.assertThat(new CashFlowController().prepareCashFlowForm(9, 2010, 6), new CashFlowFormMatcher(getCashFlowForm()));
    }

    @Test
    public void captureCashFlowShouldReturnSuccess() {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.hasErrorMessages()).thenReturn(false);
        when(externalContext.getGlobalSessionMap()).thenReturn(sharedAttributeMap);
        Assert.assertEquals("success", new CashFlowController().capture(getCashFlowForm(), requestContext, externalContext));
        Mockito.verify(requestContext, Mockito.times(1)).getMessageContext();
        Mockito.verify(externalContext, Mockito.times(1)).getGlobalSessionMap();
        Mockito.verify(sharedAttributeMap, Mockito.times(1)).put(Mockito.eq("cashFlow"), Mockito.argThat(new CashFlowFormMatcher(getCashFlowForm())));
    }

    @Test
    public void shouldReturnFailureOnError() {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        when(messageContext.hasErrorMessages()).thenReturn(true);
        Assert.assertEquals("failure", new CashFlowController().capture(getCashFlowForm(), requestContext, externalContext));
        Mockito.verify(requestContext, Mockito.times(1)).getMessageContext();
    }

    @Test
    public void shouldReturnTrueOnValidForm() {
        Assert.assertTrue(new CashFlowController().validate(getValidCashFlowForm(), messageContext));
        Mockito.verify(messageContext, Mockito.times(0)).addMessage(Mockito.<MessageResolver>any());
    }

    @Test
    public void shouldReturnFalseOnInvalidExpense() {
        when(messageContext.hasErrorMessages()).thenReturn(true);
        Assert.assertFalse(new CashFlowController().validate(getInvalidCashFlowForm(
                new MonthlyCashFlowDetail(new DateTime(2010, 10, 1, 1, 1, 1, 1), 1.1d, null, "testNotes")), messageContext));
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.EMPTY_EXPENSE)));
    }

    @Test
    public void shouldReturnFalseOnInvalidRevenue() {
        when(messageContext.hasErrorMessages()).thenReturn(true);
        Assert.assertFalse(new CashFlowController().validate(getInvalidCashFlowForm(
                new MonthlyCashFlowDetail(new DateTime(2010, 10, 1, 1, 1, 1, 1), null, 2.2d, "testNotes")), messageContext));
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.EMPTY_REVENUE)));
    }

    @Test
    public void shouldReturnFalseOnEmptyNotes() {
        when(messageContext.hasErrorMessages()).thenReturn(true);
        Assert.assertFalse(new CashFlowController().validate(getInvalidCashFlowForm(
                new MonthlyCashFlowDetail(new DateTime(2010, 10, 1, 1, 1, 1, 1), 1.1d, 2.2d, "")), messageContext));
        Mockito.verify(messageContext).addMessage(argThat(new MessageMatcher(CashFlowConstants.EMPTY_NOTES)));
    }

    private org.mifos.platform.cashflow.ui.model.CashFlowForm getInvalidCashFlowForm(MonthlyCashFlowDetail monthlyCashFlowDetail) {
        CashFlowDetail cashFlowDetail = new CashFlowDetailsBuilder().
                withMonthlyCashFlow(new MonthlyCashFlowDetail(new DateTime(2010, 9, 1, 1, 1, 1, 1), 1d, 2d, "testNotes")).
                withMonthlyCashFlow(monthlyCashFlowDetail).
                withMonthlyCashFlow(new MonthlyCashFlowDetail(new DateTime(2010, 11, 1, 1, 1, 1, 1), 1d, 2d, "testNotes")).
                build();
        return new org.mifos.platform.cashflow.ui.model.CashFlowForm(cashFlowDetail);
    }

    private org.mifos.platform.cashflow.ui.model.CashFlowForm getValidCashFlowForm() {
        CashFlowDetail cashFlowDetail = new CashFlowDetailsBuilder().
                withMonthlyCashFlow(new MonthlyCashFlowDetail(new DateTime(2010, 9, 1, 1, 1, 1, 1), 1d, 2d, "testNotes")).
                withMonthlyCashFlow(new MonthlyCashFlowDetail(new DateTime(2010, 9, 1, 1, 1, 1, 1), 1.1d, 2.2d, "testNotes")).
                build();
        return new org.mifos.platform.cashflow.ui.model.CashFlowForm(cashFlowDetail);
    }

    private org.mifos.platform.cashflow.ui.model.CashFlowForm getCashFlowForm() {
        CashFlowDetail cashFlowDetail = new CashFlowDetailsBuilder().
                withMonthlyCashFlow(new MonthlyCashFlowDetail(new DateTime(2010, 9, 1, 1, 1, 1, 1), null, null, null)).
                withMonthlyCashFlow(new MonthlyCashFlowDetail(new DateTime(2010, 10, 2, 2, 2, 2, 2), null, null, null)).
                withMonthlyCashFlow(new MonthlyCashFlowDetail(new DateTime(2010, 11, 3, 3, 3, 3, 3), null, null, null)).
                withMonthlyCashFlow(new MonthlyCashFlowDetail(new DateTime(2010, 12, 4, 4, 4, 4, 4), null, null, null)).
                withMonthlyCashFlow(new MonthlyCashFlowDetail(new DateTime(2011, 1, 5, 5, 6, 7, 8), null, null, null)).
                withMonthlyCashFlow(new MonthlyCashFlowDetail(new DateTime(2011, 2, 1, 2, 3, 4, 5), null, null, null)).
                build();
        return new org.mifos.platform.cashflow.ui.model.CashFlowForm(cashFlowDetail);
    }
}

