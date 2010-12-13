/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.accounts.loan.struts.action;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.cashflow.struts.CashFlowAdaptor;
import org.mifos.application.servicefacade.LoanCreationLoanScheduleDetailsDto;
import org.mifos.application.servicefacade.LoanServiceFacade;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.platform.cashflow.ui.model.CashFlowForm;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.validations.Errors;
import org.mifos.security.util.UserContext;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class LoanAccountActionTest {

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @Mock
    private HttpSession session;

    @Mock
    private FlowManager flowManager;

    @Mock
    private LoanCreationLoanScheduleDetailsDto loanScheduleDetailsDto;

    @Mock
    private CashFlowAdaptor cashFlowAdaptor;

    @Mock
    private LoanOfferingBO loanOffering;

    @Mock
    private ActionMapping mapping;

    private LoanAccountAction loanAccountAction;
    public static final String FLOW_KEY = "FlowKey";
    @Mock
    private LoanAccountActionForm form;
    private Flow flow;
    @Mock
    private LoanPrdBusinessService loanPrdBusinessService;
    @Mock
    private LoanServiceFacade loanServiceFacade;
    @Mock
    private UserContext userContext;

    @Before
    public void setUp() throws PageExpiredException {
        loanAccountAction = new LoanAccountAction(null, null, null, loanPrdBusinessService, null, null, null, null) {
            @Override
            protected UserContext getUserContext(@SuppressWarnings("unused") HttpServletRequest request) {
                return userContext;
            }
        };
        loanAccountAction.setLoanServiceFacade(loanServiceFacade);

        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        flow = new Flow();
        when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
    }

    @Test
    public void shouldSetQuestionGroupInstanceDetailsInSession() throws PageExpiredException {
        List<QuestionGroupInstanceDetail> instanceDetails = asList(getQuestionGroupInstanceDetail("QG1"), getQuestionGroupInstanceDetail("QG2"));
        when(questionnaireServiceFacade.getQuestionGroupInstances(101, "View", "Loan")).thenReturn(instanceDetails);
        loanAccountAction.setQuestionGroupInstances(questionnaireServiceFacade, request, 101);
        assertThat((List<QuestionGroupInstanceDetail>) flow.getObjectFromSession("questionGroupInstances"), is(instanceDetails));
        verify(questionnaireServiceFacade, times(1)).getQuestionGroupInstances(101, "View", "Loan");
        verify(request, times(1)).getAttribute(Constants.CURRENTFLOWKEY);
        verify(request, times(1)).getSession();
        verify(session, times(1)).getAttribute(Constants.FLOWMANAGER);
    }

    @Test
    public void shouldSchedulePreviewForCashFlow() {
        DateTime firstInstallmentDueDate = new DateTime();
        DateTime lastInstallmentDueDate = firstInstallmentDueDate.plusMonths(12);
        when(loanOffering.isCashFlowCheckEnabled()).thenReturn(true);
        when(loanScheduleDetailsDto.firstInstallmentDueDate()).thenReturn(firstInstallmentDueDate);
        when(loanScheduleDetailsDto.lastInstallmentDueDate()).thenReturn(lastInstallmentDueDate);
        ActionForward cashFlowForward = new ActionForward("cashFlow");
        BigDecimal loanAmount = new BigDecimal(2000);
        when(cashFlowAdaptor.renderCashFlow(eq(firstInstallmentDueDate), eq(lastInstallmentDueDate), anyString(),
                anyString(), eq(mapping), eq(request), eq(loanOffering), eq(loanAmount))).thenReturn(cashFlowForward);
        ActionForward pageAfterQuestionnaire = loanAccountAction.getPageAfterQuestionnaire(mapping, request, loanOffering,
                loanScheduleDetailsDto, cashFlowAdaptor, loanAmount);
        assertThat(pageAfterQuestionnaire, is(cashFlowForward));
        verify(loanOffering).isCashFlowCheckEnabled();
        verify(loanScheduleDetailsDto).firstInstallmentDueDate();
        verify(loanScheduleDetailsDto).lastInstallmentDueDate();
        verify(cashFlowAdaptor).renderCashFlow(eq(firstInstallmentDueDate), eq(lastInstallmentDueDate), anyString(),
                anyString(), eq(mapping), eq(request), eq(loanOffering), eq(loanAmount));
    }

    @Test
    public void previewShouldBeFailureIfErrorMessagesArePresent() throws Exception {
        ActionForward previewFailure = new ActionForward("preview_failure");
        Short localeId = new Short("1");
        CashFlowForm cashFlowForm = mock(CashFlowForm.class);
        Errors errors = new Errors();
        errors.addError("preview is failing",new String[]{});
        double repaymentCapacity = 123d;
        when(loanOffering.isCashFlowCheckEnabled()).thenReturn(true);
        when(loanOffering.getRepaymentCapacity()).thenReturn(repaymentCapacity);
        List installments = Collections.EMPTY_LIST;
        when(form.getInstallments()).thenReturn(installments);
        when(userContext.getLocaleId()).thenReturn(localeId);
        when(loanPrdBusinessService.getLoanOffering(anyShort(), eq(localeId))).thenReturn(loanOffering);
        when(form.getCashFlowForm()).thenReturn(cashFlowForm);
        when(cashFlowForm.getMonthlyCashFlows()).thenReturn(Collections.EMPTY_LIST);
        when(loanServiceFacade.validateCashFlowForInstallmentsForWarnings(form, localeId)).thenReturn(errors);
        when(loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, repaymentCapacity)).thenReturn(errors);
        when(mapping.findForward("preview_failure")).thenReturn(previewFailure);
        ActionForward forward = loanAccountAction.preview(mapping, form, request, response);
        assertThat(forward, is(previewFailure));
        verify(mapping,never()).findForward("preview_success");
    }
    @Test
    public void previewShouldBeSuccessIfOnlyWarningMessagesArePresentAndNoErrorMessagesArePresent() throws Exception {
        ActionForward previewSuccess = new ActionForward("preview_success");
        Short localeId = new Short("1");
        CashFlowForm cashFlowForm = mock(CashFlowForm.class);
        Errors warning = new Errors();
        Errors error = new Errors();
        warning.addError("this is warning message",new String[]{});
        double repaymentCapacity = 123d;
        when(loanOffering.isCashFlowCheckEnabled()).thenReturn(true);
        when(loanOffering.getRepaymentCapacity()).thenReturn(repaymentCapacity);
        List installments = Collections.EMPTY_LIST;
        when(form.getInstallments()).thenReturn(installments);
        when(userContext.getLocaleId()).thenReturn(localeId);
        when(loanPrdBusinessService.getLoanOffering(anyShort(), eq(localeId))).thenReturn(loanOffering);
        when(form.getCashFlowForm()).thenReturn(cashFlowForm);
        when(cashFlowForm.getMonthlyCashFlows()).thenReturn(Collections.EMPTY_LIST);
        when(loanServiceFacade.validateCashFlowForInstallmentsForWarnings(form, localeId)).thenReturn(warning);
        when(loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, repaymentCapacity)).thenReturn(error);
        when(mapping.findForward("preview_success")).thenReturn(previewSuccess);
        ActionForward forward = loanAccountAction.preview(mapping, form, request, response);
        assertThat(forward, is(previewSuccess));
        verify(mapping,never()).findForward("preview_failure");
    }

    @Test
    public void previewShouldBeSuccessIfNoErrorMessagesArePresent() throws Exception {
        ActionForward previewSuccess = new ActionForward("preview_success");
        Short localeId = new Short("1");
        CashFlowForm cashFlowForm = mock(CashFlowForm.class);
        Errors errors = new Errors();
        List installments = Collections.EMPTY_LIST;
        double repaymentCapacity = 123d;
        when(loanOffering.getRepaymentCapacity()).thenReturn(repaymentCapacity);
        when(loanOffering.isCashFlowCheckEnabled()).thenReturn(true);
        when(form.getInstallments()).thenReturn(installments);
        when(userContext.getLocaleId()).thenReturn(localeId);
        when(loanPrdBusinessService.getLoanOffering(anyShort(), eq(localeId))).thenReturn(loanOffering);
        when(form.getCashFlowForm()).thenReturn(cashFlowForm);
        when(cashFlowForm.getMonthlyCashFlows()).thenReturn(Collections.EMPTY_LIST);
        when(loanServiceFacade.validateCashFlowForInstallmentsForWarnings(form, localeId)).thenReturn(errors);
        when(loanServiceFacade.validateCashFlowForInstallments(installments, cashFlowForm, repaymentCapacity)).thenReturn(errors);

        when(mapping.findForward("preview_success")).thenReturn(previewSuccess);
        ActionForward forward = loanAccountAction.preview(mapping, form, request, response);
        assertThat(forward, is(previewSuccess));
        verify(mapping,never()).findForward("preview_failure");
    }

    private QuestionGroupInstanceDetail getQuestionGroupInstanceDetail(String questionGroupTitle) {
        QuestionGroupInstanceDetail detail = new QuestionGroupInstanceDetail();
        detail.setDateCompleted(Calendar.getInstance().getTime());
        detail.setQuestionGroupDetail(getQuestionGroupDetail(questionGroupTitle, asList("red", "green", "blue")));
        return detail;
    }

    private QuestionGroupDetail getQuestionGroupDetail(String title, List<String> answerChoices) {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail();
        questionGroupDetail.setTitle(title);
        questionGroupDetail.setId(123);
        questionGroupDetail.setSectionDetails(asList(getSectionDetail("Section1", "Question1", answerChoices)));
        return questionGroupDetail;
    }

    private SectionDetail getSectionDetail(String name, String title, List<String> answerChoices) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(name);
        QuestionDetail questionDetail = new QuestionDetail(111, title, QuestionType.SINGLE_SELECT, true, true);
        List<ChoiceDto> choiceDtos = new ArrayList<ChoiceDto>();
        for (String answerChoice : answerChoices) {
            choiceDtos.add(new ChoiceDto(answerChoice));
        }
        questionDetail.setAnswerChoices(choiceDtos);
        sectionDetail.setQuestionDetails(asList(new SectionQuestionDetail(questionDetail, true)));
        return sectionDetail;
    }
}
