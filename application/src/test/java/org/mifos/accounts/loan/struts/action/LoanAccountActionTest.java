/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.accounts.loan.util.helpers.RequestConstants.PERSPECTIVE;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.OriginalScheduleInfoDto;
import org.mifos.accounts.loan.struts.actionforms.LoanAccountActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.application.cashflow.struts.CashFlowAdaptor;
import org.mifos.application.questionnaire.struts.QuestionnaireFlowAdapter;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.application.servicefacade.LoanCreationLoanScheduleDetailsDto;
import org.mifos.application.servicefacade.LoanServiceFacade;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.dto.domain.LoanAccountDetailsDto;
import org.mifos.dto.screen.LoanCreationPreviewDto;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
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
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

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
    private LoanAccountServiceFacade loanAccountServiceFacade;
    @Mock
    private UserContext userContext;
    private Short localeId = new Short("1");

    @Mock
    private LoanBusinessService loanBusinessService;

    @Mock
    private CustomerDao customerDao;
    @Mock
    private QuestionnaireFlowAdapter createLoanQuestionnaire;

    @Mock
    private LoanBO loanBO;

    @Before
    public void setUp() throws PageExpiredException {
        loanAccountAction = new LoanAccountAction(null, loanBusinessService, null, loanPrdBusinessService, null, null) {
            
            @SuppressWarnings("unused") 
            @Override
            LoanBO getLoan(Integer loanId) {
                return loanBO;
            }

            @Override
            protected UserContext getUserContext(@SuppressWarnings("unused") HttpServletRequest request) {
                return userContext;
            }

            @Override
            QuestionnaireFlowAdapter getCreateLoanQuestionnaire() {
                return createLoanQuestionnaire;
            }

//            @SuppressWarnings("unused") 
//            @Override
//            boolean validateInstallments(HttpServletRequest request, LoanAccountActionForm loanActionForm) throws Exception {
//              return true;
//            }
//
//            @SuppressWarnings("unused") 
//            @Override
//            LoanBO redoLoan(CustomerBO customer, LoanAccountActionForm loanAccountActionForm, DateTime disbursementDate, UserContext userContext) {
//                return loanBO;
//            }
            
            @SuppressWarnings("unused") 
            @Override
            LoanCreationPreviewDto getLoanCreatePreviewDto(Integer customerId, List<LoanAccountDetailsDto> accountDetails, List<String> selectedClientIds) {
                return new LoanCreationPreviewDto(false, false, Collections.<LoanAccountDetailsDto>emptyList());
            }
        };
        loanAccountAction.setLoanServiceFacade(loanServiceFacade);
        loanAccountAction.setLoanAccountServiceFacade(loanAccountServiceFacade);
        loanAccountAction.setCustomerDao(customerDao);

        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        flow = new Flow();
        when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        when(userContext.getLocaleId()).thenReturn(localeId);
        when(userContext.getPreferredLocale()).thenReturn(Locale.US);

    }

    @SuppressWarnings("unchecked")
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

//    @Test
//    public void pageAfterQuestionnaireIsCashFlowWhenCashFlowEnabled() {
//        DateTime firstInstallmentDueDate = new DateTime();
//        DateTime lastInstallmentDueDate = firstInstallmentDueDate.plusMonths(12);
//        when(loanOffering.isCashFlowCheckEnabled()).thenReturn(true);
//        when(loanScheduleDetailsDto.firstInstallmentDueDate()).thenReturn(firstInstallmentDueDate);
//        when(loanScheduleDetailsDto.lastInstallmentDueDate()).thenReturn(lastInstallmentDueDate);
//        ActionForward cashFlowForward = new ActionForward("cashFlow");
//        BigDecimal loanAmount = new BigDecimal(2000);
//        Locale locale = Locale.US;
//        when(cashFlowAdaptor.renderCashFlow(eq(firstInstallmentDueDate), eq(lastInstallmentDueDate), anyString(),
//                anyString(), eq(mapping), eq(request), eq(loanOffering), eq(loanAmount), eq(locale))).thenReturn(cashFlowForward);
//        ActionForward pageAfterQuestionnaire = loanAccountAction.getPageAfterQuestionnaire(mapping, request, loanOffering,
//                loanScheduleDetailsDto, cashFlowAdaptor, loanAmount);
//        assertThat(pageAfterQuestionnaire, is(cashFlowForward));
//        verify(loanOffering).isCashFlowCheckEnabled();
//        verify(loanScheduleDetailsDto).firstInstallmentDueDate();
//        verify(loanScheduleDetailsDto).lastInstallmentDueDate();
//        verify(cashFlowAdaptor).renderCashFlow(eq(firstInstallmentDueDate), eq(lastInstallmentDueDate), anyString(),
//                anyString(), eq(mapping), eq(request), eq(loanOffering), eq(loanAmount), eq(locale));
//    }

//    @Test
//    public void pageAfterQuestionnaireIsNotCashFlowWhenCashFlowEnabledAndIfRedoPerspectiveIsSet() {
//        when(loanOffering.isCashFlowCheckEnabled()).thenReturn(true);
//        when(request.getParameter(PERSPECTIVE)).thenReturn(PERSPECTIVE_VALUE_REDO_LOAN);
//        ActionForward schedulePreviewSuccess = new ActionForward(ActionForwards.schedulePreview_success.toString());
//        BigDecimal loanAmount = new BigDecimal(2000);
//        when(mapping.findForward(ActionForwards.schedulePreview_success.toString())).thenReturn(schedulePreviewSuccess);
//        ActionForward pageAfterQuestionnaire = loanAccountAction.getPageAfterQuestionnaire(mapping, request, loanOffering,
//                loanScheduleDetailsDto, cashFlowAdaptor, loanAmount);
//        assertThat(pageAfterQuestionnaire, is(schedulePreviewSuccess));
//        verify(loanOffering).isCashFlowCheckEnabled();
//        verify(cashFlowAdaptor, never()).renderCashFlow(Matchers.<DateTime>anyObject(), Matchers.<DateTime>anyObject(), anyString(),
//                anyString(), Matchers.<ActionMapping>anyObject(), Matchers.<HttpServletRequest>anyObject(),
//                Matchers.<LoanOfferingBO>anyObject(), Matchers.<BigDecimal>anyObject(), Matchers.<Locale>anyObject());
//    }

//    @SuppressWarnings("unchecked")
//    @Test
//    public void previewShouldBeFailureIfErrorMessagesArePresent() throws Exception {
//        ActionForward previewFailure = new ActionForward("preview_failure");
//        CashFlowForm cashFlowForm = mock(CashFlowForm.class);
//        Errors errors = new Errors();
//        errors.addError("preview is failing", new String[]{});
//        double repaymentCapacity = 123d;
//        when(loanOffering.isCashFlowCheckEnabled()).thenReturn(true);
//        when(loanOffering.getRepaymentCapacity()).thenReturn(repaymentCapacity);
//        List<RepaymentScheduleInstallment> installments = Collections.EMPTY_LIST;
//        when(form.getInstallments()).thenReturn(installments);
//        when(loanPrdBusinessService.getLoanOffering(anyShort(), eq(localeId))).thenReturn(loanOffering);
//        when(form.getCashFlowForm()).thenReturn(cashFlowForm);
//        when(cashFlowForm.getMonthlyCashFlows()).thenReturn(Collections.EMPTY_LIST);
//        
//        List<CashFlowDataDto> cashflowDtos = new ArrayList<CashFlowDataDto>();
//        when(form.getCashflowDataDtos()).thenReturn(cashflowDtos);
//        when(loanAccountServiceFacade.validateCashFlowForInstallmentsForWarnings((List<CashFlowDataDto>)anyObject(), anyInt())).thenReturn(errors);
//        when(loanAccountServiceFacade.validateCashFlowForInstallments((LoanInstallmentsDto)anyObject(), (List<MonthlyCashFlowDto>)anyObject(), (Double)anyObject(), (BigDecimal)anyObject())).thenReturn(errors);
//        
//        when(mapping.findForward("preview_failure")).thenReturn(previewFailure);
//        ActionForward forward = loanAccountAction.preview(mapping, form, request, response);
//        assertThat(forward, is(previewFailure));
//        verify(mapping, never()).findForward("preview_success");
//    }

//    @SuppressWarnings("unchecked")
//    @Test
//    public void previewShouldBeSuccessIfOnlyWarningMessagesArePresentAndNoErrorMessagesArePresent() throws Exception {
//        ActionForward previewSuccess = new ActionForward("preview_success");
//        Short localeId = new Short("1");
//        CashFlowForm cashFlowForm = mock(CashFlowForm.class);
//        Errors warning = new Errors();
//        Errors error = new Errors();
//        warning.addError("this is warning message", new String[]{});
//        double repaymentCapacity = 123d;
//        when(loanOffering.isCashFlowCheckEnabled()).thenReturn(true);
//        when(loanOffering.getRepaymentCapacity()).thenReturn(repaymentCapacity);
//        List<RepaymentScheduleInstallment> installments = Collections.EMPTY_LIST;
//        when(form.getInstallments()).thenReturn(installments);
//        when(userContext.getLocaleId()).thenReturn(localeId);
//        when(loanPrdBusinessService.getLoanOffering(anyShort(), eq(localeId))).thenReturn(loanOffering);
//        when(form.getCashFlowForm()).thenReturn(cashFlowForm);
//        when(cashFlowForm.getMonthlyCashFlows()).thenReturn(Collections.EMPTY_LIST);
//        
//        List<CashFlowDataDto> cashflowDtos = new ArrayList<CashFlowDataDto>();
//        when(form.getCashflowDataDtos()).thenReturn(cashflowDtos);
//        when(loanAccountServiceFacade.validateCashFlowForInstallmentsForWarnings((List<CashFlowDataDto>)anyObject(), anyInt())).thenReturn(warning);
//        when(loanAccountServiceFacade.validateCashFlowForInstallments((LoanInstallmentsDto)anyObject(), (List<MonthlyCashFlowDto>)anyObject(), (Double)anyObject(), (BigDecimal)anyObject())).thenReturn(error);
//        
//        when(mapping.findForward("preview_success")).thenReturn(previewSuccess);
//        ActionForward forward = loanAccountAction.preview(mapping, form, request, response);
//        assertThat(forward, is(previewSuccess));
//        verify(mapping, never()).findForward("preview_failure");
//    }

//    @SuppressWarnings("unchecked")
//    @Test
//    public void previewShouldBeSuccessIfNoErrorMessagesArePresent() throws Exception {
//        ActionForward previewSuccess = new ActionForward("preview_success");
//        Short localeId = new Short("1");
//        CashFlowForm cashFlowForm = mock(CashFlowForm.class);
//        Errors errors = new Errors();
//        List<RepaymentScheduleInstallment> installments = Collections.EMPTY_LIST;
//        double repaymentCapacity = 123d;
//        when(loanOffering.getRepaymentCapacity()).thenReturn(repaymentCapacity);
//        when(loanOffering.isCashFlowCheckEnabled()).thenReturn(true);
//        when(form.getInstallments()).thenReturn(installments);
//        when(userContext.getLocaleId()).thenReturn(localeId);
//        when(loanPrdBusinessService.getLoanOffering(anyShort(), eq(localeId))).thenReturn(loanOffering);
//        when(form.getCashFlowForm()).thenReturn(cashFlowForm);
//        when(cashFlowForm.getMonthlyCashFlows()).thenReturn(Collections.EMPTY_LIST);
//        
//        List<CashFlowDataDto> cashflowDtos = new ArrayList<CashFlowDataDto>();
//        when(form.getCashflowDataDtos()).thenReturn(cashflowDtos);
//        when(loanAccountServiceFacade.validateCashFlowForInstallmentsForWarnings((List<CashFlowDataDto>)anyObject(), anyInt())).thenReturn(errors);
//        when(loanAccountServiceFacade.validateCashFlowForInstallments((LoanInstallmentsDto)anyObject(), (List<MonthlyCashFlowDto>)anyObject(), (Double)anyObject(), (BigDecimal)anyObject())).thenReturn(errors);        
//
//        when(mapping.findForward("preview_success")).thenReturn(previewSuccess);
//        ActionForward forward = loanAccountAction.preview(mapping, form, request, response);
//        assertThat(forward, is(previewSuccess));
//        verify(mapping, never()).findForward("preview_failure");
//    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldViewOriginalSchedule() throws Exception {
        ActionForward viewOriginalScheduleForward = new ActionForward("viewOriginalSchedule");
        int loanId = 1;
        String loanAmount = "123";
        List<RepaymentScheduleInstallment> installments = Collections.EMPTY_LIST;
        java.sql.Date disbursementDate = new java.sql.Date(new DateTime().toDate().getTime());

        OriginalScheduleInfoDto dto = mock(OriginalScheduleInfoDto.class);
        when(dto.getOriginalLoanScheduleInstallment()).thenReturn(installments);
        when(dto.getLoanAmount()).thenReturn(loanAmount);
        when(dto.getDisbursementDate()).thenReturn(disbursementDate);
        when(request.getParameter(LoanAccountAction.ACCOUNT_ID)).thenReturn(String.valueOf(loanId));
        when(loanServiceFacade.retrieveOriginalLoanSchedule(loanId, Locale.US)).thenReturn(dto);
        when(mapping.findForward("viewOriginalSchedule")).thenReturn(viewOriginalScheduleForward);

        ActionForward forward = loanAccountAction.viewOriginalSchedule(mapping, form, request, response);

        assertThat(forward, is(viewOriginalScheduleForward));
        verify(request).getParameter(LoanAccountAction.ACCOUNT_ID);
        verify(loanServiceFacade).retrieveOriginalLoanSchedule(loanId, Locale.US);
        verify(dto).getOriginalLoanScheduleInstallment();
        verify(dto).getLoanAmount();
        verify(dto).getDisbursementDate();
        verify(mapping).findForward("viewOriginalSchedule");
    }

    @Test
    public void showPreviewShouldSetPerspective() throws Exception {
        when(request.getParameter(PERSPECTIVE)).thenReturn(LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN);
        loanAccountAction.showPreview(mapping, form, request, response);
        verify(request,times(1)).getParameter("preview_mode");
        verify(request,times(1)).setAttribute(LoanConstants.METHODCALLED, "showPreview");
        verify(request,times(1)).setAttribute(PERSPECTIVE, LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN);
    }

    @Test
    public void captureQuestionResponses() throws Exception {
        String redoLoan = "redoLoan";
        when(form.getPerspective()).thenReturn(redoLoan);
        ActionErrors errors = mock(ActionErrors.class);
        ActionForward forward = mock(ActionForward.class);

        when(createLoanQuestionnaire.validateResponses(request, form)).thenReturn(errors);
        when(errors.isEmpty()).thenReturn(true);
        when(createLoanQuestionnaire.rejoinFlow(mapping)).thenReturn(forward);

        loanAccountAction.captureQuestionResponses(mapping, form, request, response);

        verify(request,times(1)).setAttribute(eq(LoanConstants.METHODCALLED), eq("captureQuestionResponses"));
        verify(request,times(1)).setAttribute(PERSPECTIVE, redoLoan);
        verify(createLoanQuestionnaire).rejoinFlow(mapping);
    }

    @Test
    public void getLoanRepaymentScheduleShouldCalculateExtraInterest() throws Exception {
        when(loanBusinessService.computeExtraInterest(eq(loanBO), Matchers.<Date>any())).thenReturn(new Errors());
        when(request.getParameter("accountId")).thenReturn("1");
        when(loanServiceFacade.retrieveOriginalLoanSchedule(Matchers.<Integer>any(), Matchers.<Locale>anyObject())).
                thenReturn(new OriginalScheduleInfoDto("100", new Date(), Collections.<RepaymentScheduleInstallment>emptyList()));
        loanAccountAction.getLoanRepaymentSchedule(mapping, form, request, response);
        verify(loanBusinessService, times(1)).computeExtraInterest(Matchers.<LoanBO>any(), Matchers.<Date>any());
    }

    @Test
    public void getLoanRepaymentScheduleShouldValidateViewDate() throws Exception {
        ActionForward getLoanScheduleFailure = new ActionForward("getLoanRepaymentScheduleFailure");
        java.sql.Date extraInterestDate = TestUtils.getSqlDate(10, 7, 2010);
        Errors errors = new Errors();
        errors.addError(LoanConstants.CANNOT_VIEW_REPAYMENT_SCHEDULE, new String[] {extraInterestDate.toString()});
        when(loanBusinessService.computeExtraInterest(loanBO, extraInterestDate)).thenReturn(errors);
        when(form.getScheduleViewDateValue(Locale.US)).thenReturn(extraInterestDate);
        when(request.getParameter("accountId")).thenReturn("1");
        when(mapping.findForward("getLoanRepaymentScheduleFailure")).thenReturn(getLoanScheduleFailure);
        when(loanServiceFacade.retrieveOriginalLoanSchedule(Matchers.<Integer>any(), Matchers.<Locale>anyObject())).
                thenReturn(new OriginalScheduleInfoDto("100", new Date(), Collections.<RepaymentScheduleInstallment>emptyList()));
        ActionForward forward = loanAccountAction.getLoanRepaymentSchedule(mapping, form, request, response);
        assertThat(forward, is(getLoanScheduleFailure));
        verify(form).resetScheduleViewDate();
    }

//    @Test
//    public void previewForRedoShouldComputeExtraInterest() throws Exception {
//        when(form.getPerspective()).thenReturn("redoLoan");
//        loanAccountAction.preview(mapping, form, request, response);
//        verify(loanBusinessService, times(1)).computeExtraInterest(Matchers.<LoanBO>any(), Matchers.<Date>any());
//    }
//
//    @Test
//    public void previousActionShouldSetPerspective() throws Exception {
//        when(request.getParameter(PERSPECTIVE)).thenReturn("redoLoan");
//        loanAccountAction.previous(mapping, form, request, response);
//        verify(request, times(1)).getParameter(PERSPECTIVE);
//        verify(request, times(1)).setAttribute(PERSPECTIVE, "redoLoan");
//    }


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
