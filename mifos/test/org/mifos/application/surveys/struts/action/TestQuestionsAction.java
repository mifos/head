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
import org.mifos.application.surveys.struts.actionforms.QuestionActionForm;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.SessionHolder;
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
		setServletConfigFile(ResourceLoader.getURI("WEB-INF/web.xml").getPath());
		setConfigFile(ResourceLoader.getURI(
				"org/mifos/application/surveys/struts-config.xml").getPath());
		UserContext userContext = TestUtils.makeUser();
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		ActivityContext ac = new ActivityContext((short) 0, userContext
				.getBranchId().shortValue(), userContext.getId().shortValue());
		request.getSession(false).setAttribute("ActivityContext", ac);
		request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
		database = TestDatabase.makeStandard();
		moduleMapping = findMapping("/questionsAction");
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
		new SurveysPersistence(database.open()).createOrUpdate(question);
		return question;
	}

	private Question makeTestNumberQuestion(String name, int min, int max)
			throws Exception {
		AnswerType type = AnswerType.fromInt(3);
		Question question = new Question(name, type);
		question.setNumericMin(min);
		question.setNumericMax(max);
		new SurveysPersistence(database.open()).createOrUpdate(question);
		return question;
	}

	private Question makeTestTextQuestion(String name) throws Exception {
		AnswerType type = AnswerType.fromInt(3);
		Question question = new Question(name, type);
		new SurveysPersistence(database.open()).createOrUpdate(question);
		return question;
	}

	private Question makeTestDateQuestion(String name) throws Exception {
		AnswerType type = AnswerType.fromInt(5);
		Question question = new Question(name, type);
		new SurveysPersistence(database.open()).createOrUpdate(question);
		return question;
	}
	
	public void testDefineQuestion() throws Exception {
		String questionText1 = "testDefineQuestion sample text one";
		QuestionsAction action = new QuestionsAction(database);
		setRequestPathInfo("/questionsAction");
		addRequestParameter("method", "defineQuestions");
		action.defineQuestions(moduleMapping, null, request, response);
		QuestionActionForm form = new QuestionActionForm();
		verifyNoActionErrors();
		form.setQuestionText(questionText1);
		form.setAnswerType(Integer.toString(AnswerType.FREETEXT.getValue()));
		addRequestParameter("method", "addQuestion");
		action.addQuestion(moduleMapping, form, request, response);
		List<Question> newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		assertEquals(1, newQuestions.size());
		assertEquals(questionText1, newQuestions.get(0).getQuestionText());
		assertEquals(AnswerType.FREETEXT, newQuestions.get(0).getAnswerTypeAsEnum());
		
		String questionText2 = "testDefineQuestion sample text two";
		form.setQuestionText(questionText2);
		form.setAnswerType(Integer.toString(AnswerType.CHOICE.getValue()));
		String choice = "testDefineQuestion sample choice";
		form.setChoice(choice);
		action.addChoice(moduleMapping, form, request, response);
		verifyNoActionErrors();
		List<String> newChoices = (List<String>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTION_CHOICES);
		assertEquals(1, newChoices.size());
		assertEquals(choice, newChoices.get(0));
		action.addQuestion(moduleMapping, form, request, response);
		newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		assertEquals(2, newQuestions.size());
		assertEquals(questionText2, newQuestions.get(1).getQuestionText());
		assertEquals(AnswerType.CHOICE, newQuestions.get(1).getAnswerTypeAsEnum());
		assertEquals(1, newQuestions.get(1).getChoices().size());
		assertEquals(choice, newQuestions.get(1).getChoices().get(0).getChoiceText());
		
		String questionText3 = "testDefineQuestions question text 3";
		form.setQuestionText(questionText3);
		action.addQuestion(moduleMapping, form, request, response);
		newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		assertEquals(3, newQuestions.size());
		assertEquals(questionText3, newQuestions.get(2).getQuestionText());
		int badQuestionId = newQuestions.get(2).getQuestionId();
		System.out.println("badQuestionId: " + badQuestionId);
		addRequestParameter("newQuestionNum", Integer.toString(newQuestions.size() - 1));
		action.deleteNewQuestion(moduleMapping, form, request, response);
		newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
		assertEquals(2, newQuestions.size());
		System.out.println(newQuestions.get(0).getQuestionText());
		System.out.println(newQuestions.get(1).getQuestionText());
		
		action.createQuestions(moduleMapping, form, request, response);
		SurveysPersistence persistence = new SurveysPersistence(database.open());
		List<Question> dbQuestions = persistence.retrieveAllQuestions();
		assertEquals(2, dbQuestions.size());
		assertEquals(questionText1, dbQuestions.get(0).getQuestionText());
		assertEquals(questionText2, dbQuestions.get(1).getQuestionText());
		
	}

	public void testViewQuestions() throws Exception {
		
		QuestionsAction action = new QuestionsAction(database);
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
	
}