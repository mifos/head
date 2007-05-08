package org.mifos.application.surveys.struts.action;

import java.util.List;
import java.util.ArrayList;

import org.apache.struts.action.ActionMapping;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.QuestionState;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ResourceLoader;

public class TestQuestionsAction extends MifosMockStrutsTestCase {

	SurveysPersistence surveysPersistence;
	ActionMapping moduleMapping;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/surveys/struts-config.xml").getPath());
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
		setRequestPathInfo("/questionsAction");
		addRequestParameter("method", "findActionMapping");
		actionPerform();
		moduleMapping = (ActionMapping) request
				.getAttribute(Constants.ACTION_MAPPING);
	}

	private Question makeTestSelectQuestion(String name, int choiceNumber)
			throws Exception {
		AnswerType type = AnswerType.fromInt(4);
		Question question = new Question(name, type);
		List<QuestionChoice> choices = new ArrayList<QuestionChoice>();
		for (int i = 1; i <= choiceNumber; i++) {
			choices.add(new QuestionChoice("Test Choice " + i));
		}
		question.setChoices(choices);
		surveysPersistence.createOrUpdate(question);
		return question;
	}

	private Question makeTestNumberQuestion(String name, int min, int max)
			throws Exception {
		AnswerType type = AnswerType.fromInt(3);
		Question question = new Question(name, type);
		question.setNumericMin(min);
		question.setNumericMax(max);
		surveysPersistence.createOrUpdate(question);
		return question;
	}

	private Question makeTestTextQuestion(String name) throws Exception {
		AnswerType type = AnswerType.fromInt(3);
		Question question = new Question(name, type);
		surveysPersistence.createOrUpdate(question);
		return question;
	}

	private Question makeTestDateQuestion(String name) throws Exception {
		AnswerType type = AnswerType.fromInt(5);
		Question question = new Question(name, type);
		surveysPersistence.createOrUpdate(question);
		return question;
	}

	public void testViewQuestions() throws Exception {
		
		QuestionsAction action = new QuestionsAction(surveysPersistence);
		// Set up test cases
		List<Question> testQuestionList = new ArrayList<Question>();
		testQuestionList.add(makeTestSelectQuestion("Select Text", 5));
		testQuestionList.add(makeTestNumberQuestion("Number Text", 0, 100));
		testQuestionList.add(makeTestTextQuestion("Freetext Text"));
		testQuestionList.add(makeTestDateQuestion("Date Text"));

		// Initialize QuestionsAction and retrieve Questions from session
		setRequestPathInfo("/questionsAction");
		addRequestParameter("method", "viewQuestions");
		action.viewQuestions(moduleMapping, null, request, response);
		verifyNoActionErrors();
		List<Question> retrievedQuestionList = (List<Question>) request
				.getSession().getAttribute("questionList");
		// Tests
		for (int i = 0; i < testQuestionList.size(); i++) {
			Question tempQuestion = retrievedQuestionList.get(i);
			assertEquals(testQuestionList.get(i).getQuestionText(),
					tempQuestion.getQuestionText());
			assertEquals(testQuestionList.get(i).getNumericMin(), tempQuestion
					.getNumericMin());
			assertEquals(testQuestionList.get(i).getNumericMax(), tempQuestion
					.getNumericMax());
			assertEquals(testQuestionList.get(i).getChoices().size(),
					tempQuestion.getChoices().size());
		}
	}
	/*
	 public void testGet() throws Exception {
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
	 }
	 */
}
