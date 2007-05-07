package org.mifos.application.surveys.struts.action;

import java.util.List;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.config.ForwardConfig;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.surveys.struts.actionforms.SurveyActionForm;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

public class TestSurveysAction extends MifosMockStrutsTestCase {
	
	SurveysPersistence surveysPersistence;
	ActionMapping moduleMapping;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/surveys/struts-config.xml")
				.getPath());
		UserContext userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		addRequestParameter("recordLoanOfficerId", "1");
		addRequestParameter("recordOfficeId", "1");
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		TestDatabase database = TestDatabase.makeStandard();
		SessionHolder holder = new SessionHolder(database.openSession());
		surveysPersistence = new SurveysPersistence(holder);
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "findActionMapping");
		actionPerform();
		moduleMapping = (ActionMapping) request.getAttribute(Constants.ACTION_MAPPING);
	}
	
	private Survey makeTestSurvey(String surveyName, String questionText) throws Exception {
		Survey survey = new Survey(surveyName, SurveyState.ACTIVE, SurveyType.CUSTOMERS);
		Question question = new Question(questionText, AnswerType.FREETEXT);
		survey.addQuestion(question, false);
		surveysPersistence.createOrUpdate(survey);
		return survey;
	}
	
	public void testMainpage() throws Exception {
		String testName = "Test Survey 1";
		String questionText= "A question";
		makeTestSurvey(testName, questionText);
		SurveysAction action = new SurveysAction(surveysPersistence);
		action.mainpage(moduleMapping, null, request, response);
		verifyNoActionErrors();
		List<Survey> surveys = (List<Survey>) request.getSession().getAttribute(SurveysConstants.KEY_CUSTOMERS_SURVEYS_LIST);
		assertEquals(testName, surveys.get(0).getName());
		assertEquals(1, surveys.get(0).getQuestions().size());
	}
	
	public void testGet() throws Exception {
		String testName = "Test Survey 2";
		String questionText= "Some question here";
		Survey survey = makeTestSurvey(testName, questionText);
		SurveysAction action = new SurveysAction(surveysPersistence);
		SurveyActionForm form = new SurveyActionForm();
		form.setSurveyId(Integer.toString(survey.getSurveyId()));
		action.get(moduleMapping, form, request, response);
		verifyNoActionErrors();
		Survey retrievedSurvey = (Survey) request.getSession().getAttribute(Constants.BUSINESS_KEY);
		assertEquals(testName, retrievedSurvey.getName());
		Question question = retrievedSurvey.getQuestion(0);
		assertEquals(questionText, question.getQuestionText());
	}
}
