package org.mifos.application.surveys.struts.action;

import java.util.List;

import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.struts.actionforms.GenericActionForm;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

public class TestSurveysAction extends MifosMockStrutsTestCase {
	
	ActionMapping moduleMapping;
	private TestDatabase database;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		
		database = TestDatabase.makeStandard();
		HibernateUtil.closeSession();
		AuditInterceptor interceptor = new AuditInterceptor();
		Session session = database.openSession(interceptor);
		SessionHolder holder = new SessionHolder(session);
		holder.setInterceptor(interceptor);
		HibernateUtil.setThreadLocal(holder);
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
		HibernateUtil.resetDatabase();
	}

	private Survey makeTestSurvey(String surveyName, String questionText) throws Exception {
		Survey survey = new Survey(surveyName, SurveyState.ACTIVE, SurveyType.CLIENT);
		Question question = new Question(surveyName + questionText, questionText, AnswerType.FREETEXT);
		survey.addQuestion(question, false);
		new SurveysPersistence().createOrUpdate(survey);
		return survey;
	}
	
	public void testPreviewFailureNoName() throws Exception {
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "create_entry");
		
		addRequestParameter("value(name)", "");
		addRequestParameter("value(appliesTo)", "client");
		addRequestParameter("value(state)", "ACTIVE");
		addRequestParameter("method", "preview");
		actionPerform();
		verifyActionErrors(new String[] {"errors.NotNullEmptyValidator.MISSING"});
	}
	
	public void testMainpage() throws Exception {
		String testName = "Test Survey 1";
		String questionText= "A question";
		makeTestSurvey(testName, questionText);
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "mainpage");
		actionPerform();
		verifyNoActionErrors();
		List<Survey> surveys = (List<Survey>) request.getAttribute(SurveysConstants.KEY_CLIENT_SURVEYS_LIST);
		assertEquals(testName, surveys.get(0).getName());
		assertEquals(1, surveys.get(0).getQuestions().size());
	}
	
	public void testGetAndPrint() throws Exception {
		String testName = "Test Survey 2";
		String questionText= "Some question here";
		Survey survey = makeTestSurvey(testName, questionText);
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "get");
		addRequestParameter("value(surveyId)", Integer.toString(survey.getSurveyId()));
		actionPerform();
		verifyNoActionErrors();
		Survey retrievedSurvey = (Survey) request.getSession().getAttribute(Constants.BUSINESS_KEY);
		assertEquals(testName, retrievedSurvey.getName());
		Question question = retrievedSurvey.getQuestion(0);
		assertEquals(questionText, question.getQuestionText());
		
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "printVersion");
		addRequestParameter("value(surveyId)", Integer.toString(survey.getSurveyId()));
		actionPerform();
		verifyNoActionErrors();
	}

	public void testCreateEntry() throws Exception {
		//String name = "testCreateEntry test survey name";
		String name = "test";
		String appliesTo = "client";
		String state = "ACTIVE";
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		assertEquals(0, surveysPersistence.retrieveAllSurveys().size());
		String questionText = "testCreateEntry question 1";
		String shortName = "testCreateEntry 1";
		Question question = new Question(shortName, questionText, AnswerType.CHOICE);
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
		
		addRequestParameter("value(newQuestion)", Integer.toString(question.getQuestionId()));
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "add_new_question");
		actionPerform();
		verifyNoActionErrors();
		assertEquals(0, questionsList.size());
		assertEquals(1, addedQuestions.size());
		
		addRequestParameter("value(name)", name);
		addRequestParameter("value(appliesTo)", appliesTo);
		addRequestParameter("value(state)", state);
		addRequestParameter("method", "preview");
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("method", "create");
		actionPerform();
		verifyNoActionErrors();
		assertEquals(1, surveysPersistence.retrieveAllSurveys().size());
		assertEquals(name, surveysPersistence.retrieveAllSurveys().get(0).getName());
	}
	
	public void testEditEntry() throws Exception {
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		String questionText = "testCreateEntry question 1";
		String shortName = "testCreateEntry 1";
		Question question = new Question(shortName, questionText, AnswerType.CHOICE);
		surveysPersistence.createOrUpdate(question);
		
		String state = "INACTIVE";
		String name = "test";
		String appliesTo = "client";
		Survey oldSurvey = new Survey(name, SurveyState.ACTIVE,SurveyType.fromString(appliesTo));
		surveysPersistence.createOrUpdate(oldSurvey);
		
		int surveyId = surveysPersistence.retrieveAllSurveys().get(0).getSurveyId();
		setRequestPathInfo("/surveysAction");
		addRequestParameter("method", "newVersion");
		addRequestParameter("value(surveyId)", Integer.toString(surveyId));
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("method", "add_new_question");
		addRequestParameter("value(newQuestion)", Integer.toString(question.getQuestionId()));
		actionPerform();
		verifyNoActionErrors();
		
		GenericActionForm actionForm = (GenericActionForm) getActionForm();
		name = actionForm.getValue("name");
		
		addRequestParameter("value(name)", name);
		addRequestParameter("value(appliesTo)", appliesTo);
		addRequestParameter("value(state)", state);
		addRequestParameter("method", "preview_update");
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("method", "update");
		actionPerform();
		verifyNoActionErrors();
		
		List<Survey> listSurvey = surveysPersistence.retrieveAllSurveys();
		assertEquals(1, listSurvey.size());
		assertEquals(oldSurvey.getName(), listSurvey.get(0).getName());
		assertEquals(surveyId, listSurvey.get(0).getSurveyId());
		assertEquals(1, listSurvey.get(0).getQuestions().size());
	}
}
