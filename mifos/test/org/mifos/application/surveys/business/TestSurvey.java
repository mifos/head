package org.mifos.application.surveys.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.exceptions.PersonnelException;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.surveys.exceptions.SurveyExceptionConstants;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.QuestionState;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSurvey extends MifosTestCase {
	
	Session session;
	TestDatabase database;
	
	@Override
	public void setUp() throws Exception {
		super.setUp();
		database = TestDatabase.makeStandard();
		session = database.openSession();
	}
	
	@Override
	public void tearDown() {
		session.close();
	}
	
	public void testSurveyType() {
		assertEquals("client", SurveyType.CLIENT.getValue());
		assertEquals("group", SurveyType.GROUP.getValue());
		assertEquals("center", SurveyType.CENTER.getValue());
		assertEquals("loan", SurveyType.LOAN.getValue());
		assertEquals("savings", SurveyType.SAVINGS.getValue());
		assertEquals("all", SurveyType.ALL.getValue());
		
		assertEquals(SurveyType.CLIENT, SurveyType.fromString("client"));
		assertEquals(SurveyType.GROUP, SurveyType.fromString("group"));
		assertEquals(SurveyType.CENTER, SurveyType.fromString("center"));
		assertEquals(SurveyType.LOAN, SurveyType.fromString("loan"));
		assertEquals(SurveyType.SAVINGS, SurveyType.fromString("savings"));
	}
	
	public void testRetrieveQuestionsByState() throws Exception {
		SessionHolder holder = new SessionHolder(session);
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		String questionText1 = "testGetQuestionsByState question 1";
		String questionText2 = "testGetQuestionsByState question 2";
		String questionText3 = "testGetQuestionsByState question 3";
		
		Question question1 = new Question(questionText1);
		Question question2 = new Question(questionText2);
		question2.setQuestionState(QuestionState.INACTIVE);
		Question question3 = new Question(questionText3);
		question3.setQuestionState(QuestionState.ACTIVE);
		
		surveysPersistence.createOrUpdate(question1);
		surveysPersistence.createOrUpdate(question2);
		surveysPersistence.createOrUpdate(question3);
		
		List<Question> results = surveysPersistence.retrieveQuestionsByState(QuestionState.ACTIVE);
		assertEquals(2, results.size());
		assertEquals(questionText1, results.get(0).getQuestionText());
		results = surveysPersistence.retrieveQuestionsByState(QuestionState.INACTIVE);
		assertEquals(questionText2, results.get(0).getQuestionText());
	}
	
	public void testRetrieveQuestionsByAnswerType() throws Exception {
		SessionHolder holder = new SessionHolder(session);
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		String questionText1 = "testGetQuestionsByState question 1";
		String questionText2 = "testGetQuestionsByState question 2";
		String questionText3 = "testGetQuestionsByState question 3";
		String questionText4 = "testGetQuestionsByState question 4";
		Question question1 = new Question(questionText1, AnswerType.FREETEXT);
		Question question2 = new Question(questionText2, AnswerType.NUMBER);
		Question question3 = new Question(questionText3, AnswerType.DATE);
		Question question4 = new Question(questionText4, AnswerType.CHOICE);
		
		surveysPersistence.createOrUpdate(question1);
		surveysPersistence.createOrUpdate(question2);
		surveysPersistence.createOrUpdate(question3);
		surveysPersistence.createOrUpdate(question4);
		
		List<Question> results = surveysPersistence.retrieveQuestionsByAnswerType(AnswerType.FREETEXT);
		assertEquals(1, results.size());
		assertEquals(questionText1, results.get(0).getQuestionText());
		
		results = surveysPersistence.retrieveQuestionsByAnswerType(AnswerType.NUMBER);
		assertEquals(1, results.size());
		assertEquals(questionText2, results.get(0).getQuestionText());
		
		results = surveysPersistence.retrieveQuestionsByAnswerType(AnswerType.DATE);
		assertEquals(1, results.size());
		assertEquals(questionText3, results.get(0).getQuestionText());
		
		results = surveysPersistence.retrieveQuestionsByAnswerType(AnswerType.CHOICE);
		assertEquals(1, results.size());
		assertEquals(questionText4, results.get(0).getQuestionText());
		
	}

	
	public void testGetSurveysByType() throws Exception {
		SessionHolder holder = new SessionHolder(session);
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		Survey survey1 = new Survey(
			"Survey 1", SurveyState.ACTIVE, SurveyType.CLIENT);
		Survey survey2 = new Survey(
				"Survey 2", SurveyState.ACTIVE, SurveyType.GROUP);
		Survey survey3 = new Survey(
				"Survey 3", SurveyState.ACTIVE, SurveyType.CENTER);
		Survey survey4 = new Survey(
				"Survey 4", SurveyState.ACTIVE, SurveyType.LOAN);
		Survey survey5 = new Survey(
			"Survey 5", SurveyState.ACTIVE, SurveyType.SAVINGS);
		Survey survey6 = new Survey(
			"Survey 6", SurveyState.ACTIVE, SurveyType.ALL);
		
		surveysPersistence.createOrUpdate(survey1);
		surveysPersistence.createOrUpdate(survey2);
		surveysPersistence.createOrUpdate(survey3);
		surveysPersistence.createOrUpdate(survey4);
		surveysPersistence.createOrUpdate(survey5);
		surveysPersistence.createOrUpdate(survey6);
		
		List<Survey> allOnlyResults = surveysPersistence.retrieveSurveysByType(SurveyType.ALL);
		assertEquals(1, allOnlyResults.size());
		assertEquals(survey6.getName(), allOnlyResults.get(0).getName());
		
		List<Survey> clientResults = surveysPersistence.retrieveSurveysByType(SurveyType.CLIENT);
		assertEquals(2, clientResults.size());
		assertEquals(survey1.getName(), clientResults.get(0).getName());
		
		List<Survey> loanResults = surveysPersistence.retrieveSurveysByType(SurveyType.LOAN);
		assertEquals(2, loanResults.size());
		assertEquals(survey4.getName(), loanResults.get(0).getName());
		
		List<Survey> allResults = surveysPersistence.retrieveAllSurveys();
		assertEquals(6, allResults.size());

	}
	
	public void testCreateSurvey() {
		Survey survey = new Survey(
			"testsurvey", SurveyState.ACTIVE, SurveyType.CLIENT);
		
		session.save(survey);
		
		Session reader = database.openSession();
		List result = reader.createQuery("from " + Survey.class.getName()).list();
		assertEquals(1, result.size());
		Survey read_survey = (Survey) result.get(0);
		assertEquals("testsurvey", read_survey.getName());
		assertEquals(SurveyState.ACTIVE, read_survey.getStateAsEnum());
		assertEquals(SurveyType.CLIENT, read_survey.getAppliesToAsEnum());
	}
	
	public void testCreateQuestion() {
		String questionText = "Why did the chicken cross the road?";
		{
			Question question = new Question();
			question.setAnswerType(AnswerType.FREETEXT);
			question.setQuestionText(questionText);
			question.setQuestionState(QuestionState.ACTIVE);
			session.save(question);
		}
		
		{
			Session reader = database.openSession();
			List result = reader.createQuery("from " + Question.class.getName()).list();
			assertEquals(1, result.size());
			Question retrieved = (Question) result.get(0);
			assertEquals(questionText, retrieved.getQuestionText());
			assertEquals(AnswerType.FREETEXT, retrieved.getAnswerTypeAsEnum());
			assertEquals(QuestionState.ACTIVE, retrieved.getQuestionStateAsEnum());
		}
	}
	
	public void testRetrieveQuestions() throws Exception {
		String questionText1 = "test question 1";
		String questionText2 = "test question 2";
		String questionText3 = "test question 3";
		Question question1 = new Question("test question 1", AnswerType.FREETEXT);
		Question question2 = new Question("test question 2", AnswerType.NUMBER);
		Question question3 = new Question("test question 3", AnswerType.DATE);
		question2.setQuestionState(QuestionState.INACTIVE);
		
		SessionHolder holder = new SessionHolder(session);
		SurveysPersistence surveysPersistence = new SurveysPersistence(holder);
		surveysPersistence.createOrUpdate(question1);
		surveysPersistence.createOrUpdate(question2);
		surveysPersistence.createOrUpdate(question3);
		
		List<Question> results = surveysPersistence.retrieveAllQuestions();
		assertEquals(3, results.size());
		assertEquals(questionText1, results.get(0).getQuestionText());
		assertEquals(questionText2, results.get(1).getQuestionText());
		assertEquals(questionText3, results.get(2).getQuestionText());
		
		results = surveysPersistence.retrieveQuestionsByState(QuestionState.ACTIVE);
		assertEquals(2, results.size());
		assertEquals(questionText1, results.get(0).getQuestionText());
		assertEquals(questionText3, results.get(1).getQuestionText());
		
	}
	
	public static SurveyInstance makeSurveyInstance(String surveyName) throws PersonnelException {
		TestObjectFactory factory = new TestObjectFactory();
		ClientBO client = factory.createClient(
				"Test Client " + surveyName, CustomerStatus.CLIENT_PARTIAL, null);
		
		Survey survey = new Survey();
		survey.setName(surveyName);
		survey.setState(SurveyState.ACTIVE);
		survey.setAppliesTo(SurveyType.CENTER);

		OfficeBO office = factory.getOffice(TestObjectFactory.HEAD_OFFICE);
		Name name = new Name("XYZ", null, null, null);
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView((short) 9, "123456",
				CustomFieldType.NUMERIC));
		Address address = new Address("abcd" + surveyName, "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = DateUtils.getCurrentDateWithoutTimeStamp();
		String officerName = "Test Officer " + surveyName;
		PersonnelBO officer = new PersonnelBO(PersonnelLevel.LOAN_OFFICER, office,
				Integer.valueOf("1"), TestObjectFactory.TEST_LOCALE, "PASSWORD",
				officerName, "xyz@yahoo.com", null, customFieldView, name,
				"govId" + surveyName, date, Integer.valueOf("1"), Integer.valueOf("1"),
				date, date, address, PersonnelConstants.SYSTEM_USER);

		
		SurveyInstance instance = new SurveyInstance();
		instance.setOfficer(officer);
		instance.setSurvey(survey);
		instance.setClient(client);
		instance.setDateConducted(DateUtils.getCurrentDateWithoutTimeStamp());
		HibernateUtil.getSessionTL().save(instance);
		return instance;
	}
	
	public void testSurveyResponseWithChoices() throws Exception {
		String questionText = "Why did the chicken cross the road?";
		Question question = new Question(questionText, AnswerType.CHOICE);
		QuestionChoice choice1 = new QuestionChoice("To get to the other side.");
		QuestionChoice choice2 = new QuestionChoice("Exercise");
		List<QuestionChoice> choices = new LinkedList<QuestionChoice>();
		choices.add(choice1);
		choices.add(choice2);
		question.setChoices(choices);
		session.save(question);
		SurveyResponse response = new SurveyResponse();
		response.setQuestion(question);	
		response.setChoiceValue(choice1);
	}
	
	public void testSurveyResponseTypechecks() throws Exception {
		SurveyInstance instance = makeSurveyInstance("Test survey response typechecks");
		Survey survey = instance.getSurvey();
		
		String questionText = "Dummy question text";
		Question question = new Question(questionText, AnswerType.FREETEXT);
		survey.addQuestion(question, true);
		
		String freetextAnswer = "Some answer";
		Date dateAnswer = new Date();
		BigDecimal numberAnswer = new BigDecimal("123.4");
		QuestionChoice choiceAnswer = new QuestionChoice("Some choice");
		question.addChoice(choiceAnswer);
		
		SurveyResponse response = new SurveyResponse(instance, question);
		try {
			response.setValue(dateAnswer);
			fail();
		}
		catch (ApplicationException e) {
			assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
		}
		
		try {
			response.setValue(choiceAnswer);
			fail();
		}
		catch (ApplicationException e) {
			assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
		}
		
		try {
			response.setValue(numberAnswer);
			fail();
		}
		catch (ApplicationException e) {
			assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
		}
		
		// verify date answertype check
		question.setAnswerType(AnswerType.DATE);
		
		try {
			response.setValue(freetextAnswer);
			fail();
		}
		catch (ApplicationException e) {
			assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
		}
		
		try {
			response.setValue(choiceAnswer);
			fail();
		}
		catch (ApplicationException e) {
			assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
		}

		try {
			response.setValue(numberAnswer);
			fail();
		}
		catch (ApplicationException e) {
			assertEquals(SurveyExceptionConstants.WRONG_RESPONSE_TYPE, e.getKey());
		}
		
	}
	
	public void testCreateSurveyResponse() throws Exception {
		String surveyName = "Test survey create response";
		SurveyInstance instance = makeSurveyInstance(surveyName);
		Survey survey = instance.getSurvey();
		
		String questionText = "What is the answer to life, the universe and everything?";
		Question question = new Question(questionText, AnswerType.CHOICE);
		SurveyQuestion surveyQuestion = new SurveyQuestion();
		surveyQuestion.setQuestion(question);
		List<SurveyQuestion> surveyQuestions = new LinkedList<SurveyQuestion>();
		surveyQuestions.add(surveyQuestion);
		survey.setQuestions(surveyQuestions);
		
		QuestionChoice choice1 = new QuestionChoice("Pizza");
		QuestionChoice choice2 = new QuestionChoice("42");
		List<QuestionChoice> questionChoices = new LinkedList<QuestionChoice>();
		questionChoices.add(choice1);
		questionChoices.add(choice2);
		question.setChoices(questionChoices);
		
		SurveyResponse response = new SurveyResponse();
		response.setInstance(instance);
		response.setQuestion(question);
		response.setChoiceValue(choice2);

		HibernateUtil.getSessionTL().saveOrUpdate(instance);
		HibernateUtil.getSessionTL().saveOrUpdate(response);
		
		SurveyInstance refreshedInstance = 
			(SurveyInstance)HibernateUtil.getSessionTL().get(
				SurveyInstance.class, instance.getInstanceId());
		assertEquals(AnswerType.CHOICE,
			refreshedInstance.getSurvey().getQuestion(0).getAnswerTypeAsEnum());
		assertEquals(refreshedInstance.getSurvey().getName(), surveyName);
	}
	
	public void testSerialize() throws Exception {
		Survey survey = new Survey(
			"my survey", SurveyState.ACTIVE, SurveyType.CLIENT);
		Question question = new Question("Can I be written to the session?");
		survey.addQuestion(question, false);
		
		TestUtils.assertCanSerialize(survey);
	}
	
}
