package org.mifos.application.surveys.struts.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.struts.action.ActionMapping;
import org.hibernate.Session;
import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
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

public class TestQuestionsAction extends MifosMockStrutsTestCase {

	private TestDatabase database;
	ActionMapping moduleMapping;

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
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/surveys/struts-config.xml").getPath());
		UserContext userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
	}
	
	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.resetDatabase();
	}

	private Question makeTestSelectQuestion(String name, int choiceNumber)
			throws Exception {
		AnswerType type = AnswerType.fromInt(4);
		Question question = new Question(name, name, type);
		List<QuestionChoice> choices = new ArrayList<QuestionChoice>();
		for (int i = 1; i <= choiceNumber; i++) {
			choices.add(new QuestionChoice("Test Choice " + i));
		}
		question.setChoices(choices);
		new SurveysPersistence().createOrUpdate(question);
		return question;
	}

	private Question makeTestNumberQuestion(String name, int min, int max)
			throws Exception {
		AnswerType type = AnswerType.fromInt(3);
		Question question = new Question(name, name, type);
		question.setNumericMin(min);
		question.setNumericMax(max);
		new SurveysPersistence().createOrUpdate(question);
		return question;
	}

	private Question makeTestTextQuestion(String name) throws Exception {
		AnswerType type = AnswerType.fromInt(3);
		Question question = new Question(name, name, type);
		new SurveysPersistence().createOrUpdate(question);
		return question;
	}

	private Question makeTestDateQuestion(String name) throws Exception {
		AnswerType type = AnswerType.fromInt(5);
		Question question = new Question(name, name, type);
		new SurveysPersistence().createOrUpdate(question);
		return question;
	}
	
	public void testDefineQuestion() throws Exception {
		String questionText1 = "testDefineQuestion sample text one";
		String shortName1 = "testDefineQuestion 1";
		setRequestPathInfo("/questionsAction");
		addRequestParameter("method", "defineQuestions");
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("value(shortName)", shortName1);
		addRequestParameter("value(questionText)", questionText1);
		addRequestParameter("value(answerType)", Integer.toString(AnswerType.FREETEXT.getValue()));
		addRequestParameter("method", "addQuestion");
		actionPerform();
		verifyNoActionErrors();
		List<Question> newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		assertEquals(1, newQuestions.size());
		assertEquals(questionText1, newQuestions.get(0).getQuestionText());
		assertEquals(AnswerType.FREETEXT, newQuestions.get(0).getAnswerTypeAsEnum());
		
		String questionText2 = "testDefineQuestion sample text two";
		String shortName2 = "testDefineQuestion 2";
		addRequestParameter("value(shortName)", shortName2);
		addRequestParameter("value(questionText)", questionText2);
		addRequestParameter("value(answerType)",
				Integer.toString(AnswerType.CHOICE.getValue()));
		String choice = "testDefineQuestion sample choice";
		addRequestParameter("value(choice)", choice);
		addRequestParameter("method", "addChoice");
		actionPerform();
		verifyNoActionErrors();
		List<String> newChoices = (List<String>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTION_CHOICES);
		assertEquals(1, newChoices.size());
		assertEquals(choice, newChoices.get(0));
		addRequestParameter("method", "addQuestion");
		actionPerform();
		verifyNoActionErrors();
		newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		assertEquals(2, newQuestions.size());
		assertEquals(shortName2, newQuestions.get(1).getShortName());
		assertEquals(questionText2, newQuestions.get(1).getQuestionText());
		assertEquals(AnswerType.CHOICE, newQuestions.get(1).getAnswerTypeAsEnum());
		assertEquals(1, newQuestions.get(1).getChoices().size());
		assertEquals(choice, newQuestions.get(1).getChoices().get(0).getChoiceText());
		
		
		String questionText3 = "testDefineQuestions question text 3";
		String shortName3 = "testDefineQuestions 3";
		addRequestParameter("value(shortName)", shortName3);
		addRequestParameter("value(questionText)", questionText3);
		addRequestParameter("method", "addQuestion");
		actionPerform();
		verifyNoActionErrors();
		newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		assertEquals(3, newQuestions.size());
		assertEquals(questionText3, newQuestions.get(2).getQuestionText());
		assertEquals(shortName3, newQuestions.get(2).getShortName());
//		int badQuestionId = newQuestions.get(2).getQuestionId();
//		System.out.println("badQuestionId: " + badQuestionId);
		addRequestParameter("newQuestionNum", Integer.toString(newQuestions.size() - 1));
		addRequestParameter("method", "deleteNewQuestion");
		actionPerform();
		verifyNoActionErrors();
		newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		assertEquals(2, newQuestions.size());
//		System.out.println(newQuestions.get(0).getQuestionText());
//		System.out.println(newQuestions.get(1).getQuestionText());
		
		addRequestParameter("method", "createQuestions");
		actionPerform();
		verifyNoActionErrors();
		SurveysPersistence persistence = new SurveysPersistence();
		List<Question> dbQuestions = persistence.retrieveAllQuestions();
		assertEquals(2, dbQuestions.size());
		assertEquals(questionText1, dbQuestions.get(0).getQuestionText());
		assertEquals(questionText2, dbQuestions.get(1).getQuestionText());
		
	}

	
	public void testViewQuestions() throws Exception {
		
		// Set up test cases
		List<Question> testQuestionList = new ArrayList<Question>();
		testQuestionList.add(makeTestSelectQuestion("Select Text", 5));
		testQuestionList.add(makeTestNumberQuestion("Number Text", 0, 100));
		testQuestionList.add(makeTestTextQuestion("Freetext Text"));
		testQuestionList.add(makeTestDateQuestion("Date Text"));

		// Initialize QuestionsAction and retrieve Questions from session
		setRequestPathInfo("/questionsAction");
		addRequestParameter("method", "viewQuestions");
		actionPerform();
		verifyNoActionErrors();
		List<Question> retrievedQuestionList = (List<Question>) request
				.getAttribute(SurveysConstants.KEY_QUESTIONS_LIST);
		// Tests
		Collections.sort(retrievedQuestionList);
		Collections.sort(testQuestionList);
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
	
	public void testEdit() throws Exception{
		setRequestPathInfo("/questionsAction");
		addRequestParameter("method", "defineQuestions");
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("value(shortName)", "Short Name 1");
		addRequestParameter("value(questionText)", "Question Text 1");
		addRequestParameter("value(answerType)", Integer.toString(AnswerType.FREETEXT.getValue()));
		addRequestParameter("method", "addQuestion");
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("method", "createQuestions");
		actionPerform();
		verifyNoActionErrors();
		
		SurveysPersistence persistence = new SurveysPersistence();
		int questionId = persistence.retrieveAllQuestions().get(0).getQuestionId();
		
		addRequestParameter("method", "get");
		addRequestParameter("questionId", Integer.toString(questionId));
		actionPerform();
		verifyNoActionErrors();
		
		addRequestParameter("method", "edit_entry");
		actionPerform();
		
		String shortName = "New Name";
		String questionText = "New question text";
		addRequestParameter("value(shortName)", shortName);
		addRequestParameter("value(questionText)", questionText);
		addRequestParameter("value(status)", "1");
		addRequestParameter("method", "preview_entry");
		
		addRequestParameter("method", "update_entry");
		actionPerform();
		verifyNoActionErrors();
		
		Question newQuestion = persistence.getQuestion(questionId);
		
		assertEquals(newQuestion.getShortName(), shortName);
		assertEquals(newQuestion.getQuestionText(), questionText);
	}
	
	public void testEditPPI() throws Exception {
		Question question = new Question("New Q", "What's the q?", AnswerType.CHOICE);
		
		PPIChoice choice = new PPIChoice("choice 1");
		choice.setPoints(11);
		
		question.addChoice(choice);
		
		request.getSession().setAttribute(SurveysConstants.KEY_QUESTION, question);
		
		int id = question.getQuestionId();
		
		setRequestPathInfo("/questionsAction");
		addRequestParameter("questionId", Integer.toString(id));
		addRequestParameter("method", "edit_entry");
		actionPerform();
		verifyActionErrors(new String[] {"errors.readonly"});
	}
	
}