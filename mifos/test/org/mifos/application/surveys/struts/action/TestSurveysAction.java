package org.mifos.application.surveys.struts.action;

import java.util.List;

import org.apache.struts.action.ActionMapping;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.PersistenceAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

public class TestSurveysAction extends MifosMockStrutsTestCase {
	
	ActionMapping moduleMapping;
	private TestDatabase database;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		database = TestDatabase.makeStandard();
		PersistenceAction.setDefaultSessionOpener(database);
		
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml")
				.getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/surveys/struts-config.xml")
				.getPath());
		UserContext userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
	}
	
	@Override
	protected void tearDown() throws Exception {
		PersistenceAction.resetDefaultSessionOpener();
	}

	private Survey makeTestSurvey(String surveyName, String questionText) throws Exception {
		Survey survey = new Survey(surveyName, SurveyState.ACTIVE, SurveyType.CUSTOMERS);
		Question question = new Question(questionText, AnswerType.FREETEXT);
		survey.addQuestion(question, false);
		new SurveysPersistence(database.open()).createOrUpdate(survey);
		return survey;
	}
	
	public void testMainpage() throws Exception {
		String testName = "Test Survey 1";
		String questionText= "A question";
		makeTestSurvey(testName, questionText);
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "mainpage");
		actionPerform();
		verifyNoActionErrors();
		List<Survey> surveys = (List<Survey>) request.getSession().getAttribute(SurveysConstants.KEY_CUSTOMERS_SURVEYS_LIST);
		assertEquals(testName, surveys.get(0).getName());
		assertEquals(1, surveys.get(0).getQuestions().size());
	}
	
	public void testGetAndPrint() throws Exception {
		String testName = "Test Survey 2";
		String questionText= "Some question here";
		Survey survey = makeTestSurvey(testName, questionText);
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "get");
		addRequestParameter("surveyId", Integer.toString(survey.getSurveyId()));
		actionPerform();
		verifyNoActionErrors();
		Survey retrievedSurvey = (Survey) request.getSession().getAttribute(Constants.BUSINESS_KEY);
		assertEquals(testName, retrievedSurvey.getName());
		Question question = retrievedSurvey.getQuestion(0);
		assertEquals(questionText, question.getQuestionText());
		
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "printVersion");
		addRequestParameter("surveyId", Integer.toString(survey.getSurveyId()));
		actionPerform();
		verifyNoActionErrors();
	}

	public void testCreateEntry() throws Exception {
		//String name = "testCreateEntry test survey name";
		String name = "g";
		String appliesTo = "customers";
		SurveysPersistence surveysPersistence = new SurveysPersistence(database.open());
		assertEquals(0, surveysPersistence.retrieveAllSurveys().size());
		String questionText = "testCreateEntry question 1";
		Question question = new Question(questionText, AnswerType.CHOICE);
		surveysPersistence.createOrUpdate(question);
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "create_entry");
		actionPerform();
		verifyNoActionErrors();	
		List<Question> questionsList = (List<Question>) request.getSession()
				.getAttribute(SurveysConstants.KEY_QUESTIONS_LIST);
		List<Question> addedQuestions = (List<Question>) request.getSession()
				.getAttribute(SurveysConstants.KEY_ADDED_QUESTIONS);
		assertEquals(1, questionsList.size());
		assertEquals(0, addedQuestions.size());
		
		addRequestParameter("newQuestion", Integer.toString(question.getQuestionId()));
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "add_new_question");
		actionPerform();
		verifyNoActionErrors();
		assertEquals(0, questionsList.size());
		assertEquals(1, addedQuestions.size());
		
		addRequestParameter("name", name);
		addRequestParameter("appliesTo", appliesTo);
		addRequestParameter("method", "preview");
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
		assertEquals(1, surveysPersistence.retrieveAllSurveys().size());
		assertEquals(name, surveysPersistence.retrieveAllSurveys().get(0).getName());
	}
}
