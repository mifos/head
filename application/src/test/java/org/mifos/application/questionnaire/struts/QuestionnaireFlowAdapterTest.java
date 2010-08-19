package org.mifos.application.questionnaire.struts;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireFlowAdapterTest {

    @Mock
    private QuestionResponseCapturer questionForm;

    @Mock
    private ActionMapping mapping;

    @Mock
    private ActionForward captureResponseActFwd;

    @Mock
    HttpServletRequest request;

    @Mock
    private ActionForward schedulePreview_successFwd;

    @Mock
    QuestionnaireServiceFacadeLocator serviceLocator;

    @Mock
    QuestionnaireServiceFacade questionnaireServiceFacade;


    @Before
    public void setUp() {
       /* questionForm = new QuestionResponseCapturer() {
            private List<QuestionGroupDetail> questionGroups;
            @Override
            public void setQuestionGroups(List<QuestionGroupDetail> questionGroups) {
                this.questionGroups =  questionGroups;
            }
            @Override
            public List<QuestionGroupDetail> getQuestionGroups() {
                return this.questionGroups;
            }
        };*/
    }

    @Test
    public void testShouldFetchQuestions() {
        QuestionnaireFlowAdapter createLoanQuestionnaire =
            new QuestionnaireFlowAdapter("Create","Loan",
                ActionForwards.schedulePreview_success,
                "custSearchAction.do?method=loadMainSearch", serviceLocator);
        List<QuestionGroupDetail> applicableGroups = getQuestionGroups();

        Mockito.when(mapping.findForward("captureQuestionResponses")).thenReturn(captureResponseActFwd);
        Mockito.when(mapping.findForward("schedulePreview_success")).thenReturn(schedulePreview_successFwd);
        Mockito.when(serviceLocator.getService(request)).thenReturn(questionnaireServiceFacade);
        Mockito.when(request.getRequestURI()).thenReturn("/mifos/loanAccountAction.do");
        Mockito.when(request.getContextPath()).thenReturn("/mifos");
        Mockito.when(questionnaireServiceFacade.getQuestionGroups("Create", "Loan")).thenReturn(applicableGroups);

        ActionForward fwdTo = createLoanQuestionnaire.fetchAppliedQuestions(mapping, questionForm, request, ActionForwards.schedulePreview_success);
        Assert.assertEquals(captureResponseActFwd, fwdTo);
        //Assert.assertEquals(applicableGroups, questionForm.getQuestionGroups());
        Mockito.verify(questionForm).setQuestionGroups(applicableGroups);
        Mockito.verify(request).setAttribute("questionsHostForm", questionForm);
        Mockito.verify(request).setAttribute("origFlowRequestURI", "/loanAccountAction.do");
        Mockito.verify(request).setAttribute("cancelToURL", "custSearchAction.do?method=loadMainSearch");
    }

    private List<QuestionGroupDetail> getQuestionGroups() {
        List<QuestionDetail> questions = Arrays.asList(new QuestionDetail(12, "Question1", "Question1", QuestionType.FREETEXT, true));
        List<SectionDetail> section = Arrays.asList(getSectionDetailWithQuestions("Section1", questions, null, true));
        QuestionGroupDetail questionGroup = getQuestionGroupDetail("Group1", "Create", "Loan", section);
        return Arrays.asList(questionGroup);
    }

    private QuestionGroupDetail getQuestionGroupDetail(String title, String event, String source, List<SectionDetail> sections) {
        return new QuestionGroupDetail(1, title, new EventSourceDto(event, source, null), sections, true);
    }

    private SectionDetail getSectionDetailWithQuestions(String sectionName, List<QuestionDetail> questionDetails,
            String value, boolean mandatory) {
        SectionDetail section = new SectionDetail();
        section.setName(sectionName);
        List<SectionQuestionDetail> sectionQuestions = new ArrayList<SectionQuestionDetail>();
        for (QuestionDetail questionDetail : questionDetails) {
            SectionQuestionDetail sectionQuestion = new SectionQuestionDetail(questionDetail, mandatory);
            sectionQuestion.setValue(value);
            sectionQuestions.add(sectionQuestion);
        }
        section.setQuestionDetails(sectionQuestions);
        return section;
    }

    public void testShouldCaptureResponses() {

    }


    public void testShouldAllowEditResponses() {

    }

}
