package org.mifos.accounts.productdefinition.struts.action;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.QuestionGroupReference;
import org.mifos.accounts.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.domain.builders.LoanProductBuilder;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@SuppressWarnings("unchecked")
@RunWith(MockitoJUnitRunner.class)
public class LoanPrdActionTest {
    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpSession session;

    @Mock
    private FlowManager flowManager;

    private LoanPrdAction loanPrdAction;
    private static final String FLOW_KEY = "FlowKey";

    @Before
    public void setUp() {
        loanPrdAction = new LoanPrdAction();
    }

    @Test
    public void shouldSetQuestionGroupsOnSession() throws PageExpiredException {
        List<QuestionGroupDetail> questionGroupDetails = asList(getQuestionGroupDetail(1, "QG1", true), getQuestionGroupDetail(2, "QG2", true), getQuestionGroupDetail(3, "QG3", true));
        when(questionnaireServiceFacade.getQuestionGroups("Create", "Loan")).thenReturn(questionGroupDetails);
        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        Flow flow = new Flow();
        when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        loanPrdAction.setQuestionGroupsOnSession(request, questionnaireServiceFacade);
        assertThat((List<QuestionGroupDetail>) flow.getObjectFromSession(ProductDefinitionConstants.SRCQGLIST), is(questionGroupDetails));
        verify(questionnaireServiceFacade, times(1)).getQuestionGroups("Create", "Loan");
        verify(request, times(1)).getAttribute(Constants.CURRENTFLOWKEY);
        verify(request, times(1)).getSession();
        verify(session, times(1)).getAttribute(Constants.FLOWMANAGER);
    }

    @Test
    public void shouldSetSelectedQuestionGroupsOnSession() throws PageExpiredException {
        List<QuestionGroupDetail> questionGroupDetails = asList(getQuestionGroupDetail(1, "QG1", true), getQuestionGroupDetail(2, "QG2", true), getQuestionGroupDetail(3, "QG3", false));
        when(questionnaireServiceFacade.getQuestionGroupDetailForLoanPrd(1)).thenReturn(questionGroupDetails.get(0));
        when(questionnaireServiceFacade.getQuestionGroupDetailForLoanPrd(2)).thenReturn(questionGroupDetails.get(1));
        when(questionnaireServiceFacade.getQuestionGroupDetailForLoanPrd(3)).thenReturn(questionGroupDetails.get(2));
        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        Flow flow = new Flow();
        when(flowManager.getFlowWithValidation(FLOW_KEY)).thenReturn(flow);
        LoanOfferingBO loanOfferingBO = new LoanProductBuilder().buildForUnitTests();
        loanOfferingBO.setQuestionGroups(getQustionGroups(1, 2, 3));
        loanPrdAction.setSelectedQuestionGroupsOnSession(request, loanOfferingBO, questionnaireServiceFacade);
        List<QuestionGroupDetail> questionGroupDetailList = (List<QuestionGroupDetail>) flow.getObjectFromSession(ProductDefinitionConstants.SELECTEDQGLIST);
        assertThat(questionGroupDetailList, is(notNullValue()));
        assertThat(questionGroupDetailList.size(), is(2));
        assertQuestionGroup(questionGroupDetailList.get(0), 1, "QG1");
        assertQuestionGroup(questionGroupDetailList.get(1), 2, "QG2");
        verify(questionnaireServiceFacade, times(3)).getQuestionGroupDetailForLoanPrd(anyInt());
        verify(request, times(1)).getAttribute(Constants.CURRENTFLOWKEY);
        verify(request, times(1)).getSession();
        verify(session, times(1)).getAttribute(Constants.FLOWMANAGER);
    }

    private void assertQuestionGroup(QuestionGroupDetail questionGroupDetail, int id, String title) {
        assertThat(questionGroupDetail.getId(), is(id));
        assertThat(questionGroupDetail.getTitle(), is(title));
    }

    @Test
    public void shouldGetQuestionGroupResponses() throws PageExpiredException {
        when(request.getAttribute(Constants.CURRENTFLOWKEY)).thenReturn(FLOW_KEY);
        when(request.getSession()).thenReturn(session);
        when(session.getAttribute(Constants.FLOWMANAGER)).thenReturn(flowManager);
        List<QuestionGroupDetail> questionGroupDetails = asList(getQuestionGroupDetail(1, "QG1", true), getQuestionGroupDetail(2, "QG2", true), getQuestionGroupDetail(3, "QG3", true));
        when(flowManager.getFromFlow(FLOW_KEY, ProductDefinitionConstants.SELECTEDQGLIST)).thenReturn(questionGroupDetails);
        Set<QuestionGroupReference> questionGroupReferences = loanPrdAction.getQuestionGroups(request);
        assertThat(questionGroupReferences, is(notNullValue()));
        assertThat(questionGroupReferences.size(), is(3));
        QuestionGroupReference[] qgReferences = questionGroupReferences.toArray(new QuestionGroupReference[questionGroupReferences.size()]);
        assertThat(qgReferences[0].getQuestionGroupId(), is(1));
        assertThat(qgReferences[1].getQuestionGroupId(), is(2));
        assertThat(qgReferences[2].getQuestionGroupId(), is(3));
        verify(request, times(1)).getAttribute(Constants.CURRENTFLOWKEY);
        verify(request, times(1)).getSession();
        verify(session, times(1)).getAttribute(Constants.FLOWMANAGER);
        verify(flowManager, times(1)).getFromFlow(FLOW_KEY, ProductDefinitionConstants.SELECTEDQGLIST);
    }

    private Set<QuestionGroupReference> getQustionGroups(int... questionGroupIds) {
        Set<QuestionGroupReference> questionGroupReferences = new HashSet<QuestionGroupReference>();
        for (int questionGroupId : questionGroupIds) {
            questionGroupReferences.add(makeQuestionGroupRef(questionGroupId));
        }
        return questionGroupReferences;
    }

    private QuestionGroupReference makeQuestionGroupRef(int questionGroupId) {
        QuestionGroupReference questionGroupReference = new QuestionGroupReference();
        questionGroupReference.setQuestionGroupId(questionGroupId);
        return questionGroupReference;
    }

    private QuestionGroupDetail getQuestionGroupDetail(int id, String title, boolean active) {
        QuestionGroupDetail questionGroupDetail = new QuestionGroupDetail();
        questionGroupDetail.setId(id);
        questionGroupDetail.setTitle(title);
        questionGroupDetail.setActive(active);
        return questionGroupDetail;
    }
}
