package org.mifos.application.surveys.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.meeting.business.MeetingBO;
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
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestSurvey extends MifosTestCase {
	
	@Override
	public void setUp() throws Exception {
		super.setUp();

		// Force loading the chart of accounts since this data would otherwise
		// not be present in a Mayfly database freshly initialized from
		// particular SQL scripts. Why does setUp blow away the database every
		// time, anyway? Must be working around *another* issue...
		FinancialInitializer.loadCOA();
	}

	@Override
	public void tearDown() throws Exception {
		HibernateUtil.closeSession();
		TestDatabase.resetMySQLDatabase();
		super.tearDown();
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
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		String questionText1 = "testGetQuestionsByState question 1";
		String questionText2 = "testGetQuestionsByState question 2";
		String questionText3 = "testGetQuestionsByState question 3";
		
		Question question1 = new Question(questionText1);
		question1.setShortName("Q1");
		Question question2 = new Question(questionText2);
		question2.setQuestionState(QuestionState.INACTIVE);
		question2.setShortName("Q2");
		Question question3 = new Question(questionText3);
		question3.setQuestionState(QuestionState.ACTIVE);
		question3.setShortName("Q3");
		
		surveysPersistence.createOrUpdate(question1);
		surveysPersistence.createOrUpdate(question2);
		surveysPersistence.createOrUpdate(question3);
		
		List<Question> results = surveysPersistence.retrieveGeneralQuestionsByState(QuestionState.ACTIVE);
		assertEquals(2, results.size());
		assertEquals(questionText1, results.get(0).getQuestionText());
		results = surveysPersistence.retrieveGeneralQuestionsByState(QuestionState.INACTIVE);
		assertEquals(questionText2, results.get(0).getQuestionText());
	}
	
	
	public void testRetrieveInstances() throws Exception {
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		SurveyInstance instance1 = makeSurveyInstance("survey1");
		SurveyInstance instance2 = new SurveyInstance();
		Survey survey2 = new Survey();
		survey2.setName("survey2");
		survey2.setState(SurveyState.ACTIVE);
		survey2.setAppliesTo(SurveyType.CLIENT);
		instance2.setSurvey(survey2);
		instance2.setCustomer(instance1.getCustomer());
		instance2.setDateConducted(DateUtils.getCurrentDateWithoutTimeStamp());
		instance2.setOfficer(instance1.getOfficer());
		instance2.setCreator(instance1.getCreator());
		
		HibernateUtil.startTransaction();
		MeetingBO meeting = TestObjectFactory.getTypicalMeeting();
		HibernateUtil.commitTransaction();
		
		CenterBO center = TestObjectFactory.createCenter("centerName", meeting);
		meeting.setMeetingPlace("somewhere");
		SurveyInstance instance3 = new SurveyInstance();
		instance3.setCustomer(center);
		instance3.setSurvey(survey2);
		instance3.setDateConducted(DateUtils.getCurrentDateWithoutTimeStamp());
		instance3.setOfficer(instance1.getOfficer());
		instance3.setCreator(instance1.getCreator());
		surveysPersistence.createOrUpdate(instance2);
		surveysPersistence.createOrUpdate(instance3);
		HibernateUtil.commitTransaction();
		
		List<SurveyInstance> retrievedInstances = surveysPersistence.retrieveInstancesByCustomer(instance1.getCustomer());
		assertEquals(2, retrievedInstances.size());
		assertEquals("survey2", retrievedInstances.get(1).getSurvey().getName());
		
		retrievedInstances = surveysPersistence.retrieveInstancesByCustomer(center);
		assertEquals(1, retrievedInstances.size());
		assertEquals("survey2", retrievedInstances.get(0).getSurvey().getName());
		SurveyInstance retrievedInstance = retrievedInstances.get(0);
		CenterBO retrievedCenter = (CenterBO) retrievedInstance.getCustomer();
		assertEquals("somewhere", retrievedCenter.getCustomerMeeting().getMeeting().getMeetingPlace());
		
		retrievedInstances = surveysPersistence.retrieveInstancesBySurvey(instance1.getSurvey());
		assertEquals(1, retrievedInstances.size());
		assertEquals("survey1", retrievedInstances.get(0).getSurvey().getName());
		retrievedInstances = surveysPersistence.retrieveInstancesBySurvey(survey2);
		assertEquals(2, retrievedInstances.size());
		assertEquals("survey2", retrievedInstances.get(0).getSurvey().getName());
	}
	
	
	public void testRetrieveQuestionsByAnswerType() throws Exception {
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		String questionText1 = "testGetQuestionsByState question 1";
		String questionText2 = "testGetQuestionsByState question 2";
		String questionText3 = "testGetQuestionsByState question 3";
		String questionText4 = "testGetQuestionsByState question 4";
		String shortName1 = "name 1";
		String shortName2 = "name 2";
		String shortName3 = "name 3";
		String shortName4 = "name 4";
		Question question1 = new Question(shortName1, questionText1, AnswerType.FREETEXT);
		Question question2 = new Question(shortName2, questionText2, AnswerType.NUMBER);
		Question question3 = new Question(shortName3, questionText3, AnswerType.DATE);
		Question question4 = new Question(shortName4, questionText4, AnswerType.CHOICE);
		
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
		SurveysPersistence surveysPersistence = new SurveysPersistence();
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
	
	public void testCreateSurvey() throws Exception {
		Survey survey = new Survey(
			"testsurvey", SurveyState.ACTIVE, SurveyType.CLIENT);
		
		HibernateUtil.startTransaction();
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		surveysPersistence.createOrUpdate(survey);
		
		List result = HibernateUtil.getSessionTL().createQuery("from " + Survey.class.getName()).list();
		assertEquals(1, result.size());
		Survey read_survey = (Survey) result.get(0);
		HibernateUtil.commitTransaction();
		
		assertEquals("testsurvey", read_survey.getName());
		assertEquals(SurveyState.ACTIVE, read_survey.getStateAsEnum());
		assertEquals(SurveyType.CLIENT, read_survey.getAppliesToAsEnum());
	}
	
	public void testCreateQuestion() {
		String questionText = "Why did the chicken cross the road?";

		Question question = new Question();
		question.setAnswerType(AnswerType.FREETEXT);
		question.setQuestionText(questionText);
		question.setShortName("Short Name Test");
		question.setQuestionState(QuestionState.ACTIVE);
		HibernateUtil.getSessionTL().save(question);
		
		List result = HibernateUtil.getSessionTL().createQuery("from " + Question.class.getName()).list();
		assertEquals(1, result.size());
		Question retrieved = (Question) result.get(0);
		assertEquals(questionText, retrieved.getQuestionText());
		assertEquals(AnswerType.FREETEXT, retrieved.getAnswerTypeAsEnum());
		assertEquals(QuestionState.ACTIVE, retrieved.getQuestionStateAsEnum());
	}
	
	public void testRetrieveQuestions() throws Exception {
		String questionText1 = "test question 1";
		String questionText2 = "test question 2";
		String questionText3 = "test question 3";
		Question question1 = new Question("test name 1", "test question 1", AnswerType.FREETEXT);
		Question question2 = new Question("test name 2", "test question 2", AnswerType.NUMBER);
		Question question3 = new Question("test name 3", "test question 3", AnswerType.DATE);
		question2.setQuestionState(QuestionState.INACTIVE);
		
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		surveysPersistence.createOrUpdate(question1);
		surveysPersistence.createOrUpdate(question2);
		surveysPersistence.createOrUpdate(question3);
		
		List<Question> results = surveysPersistence.retrieveAllQuestions();
		assertEquals(3, results.size());
		assertEquals(questionText1, results.get(0).getQuestionText());
		assertEquals(questionText2, results.get(1).getQuestionText());
		assertEquals(questionText3, results.get(2).getQuestionText());
		
		results = surveysPersistence.retrieveGeneralQuestionsByState(QuestionState.ACTIVE);
		assertEquals(2, results.size());
		assertEquals(questionText1, results.get(0).getQuestionText());
		assertEquals(questionText3, results.get(1).getQuestionText());
		
	}
	
	public void testRetrieveQuestionsByName() throws Exception {
		String name1 = "name1";
		Question question1 = new Question(name1, "test question text", AnswerType.FREETEXT);
		
		SurveysPersistence surveysPersistence = new SurveysPersistence();
		surveysPersistence.createOrUpdate(question1);
		
		List<Question> results = surveysPersistence.retrieveQuestionsByName(name1);
		assertEquals(1, results.size());
		assertEquals(name1, results.get(0).getShortName());
	}
	
	public static SurveyInstance makeSurveyInstance(String surveyName) throws PersonnelException, PersistenceException, ValidationException {
		TestObjectFactory factory = new TestObjectFactory();
		ClientBO client = factory.createClient(
				"Test Client " + surveyName, CustomerStatus.CLIENT_PARTIAL, null);
		
		Survey survey = new Survey();
		survey.setName(surveyName);
		survey.setState(SurveyState.ACTIVE);
		survey.setAppliesTo(SurveyType.CLIENT);

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
				officerName + System.currentTimeMillis(), "xyz@yahoo.com", null, customFieldView, name,
				"govId" + surveyName, date, Integer.valueOf("1"), Integer.valueOf("1"),
				date, date, address, PersonnelConstants.SYSTEM_USER);

		SurveyInstance instance = new SurveyInstance();
		instance.setOfficer(officer);
		instance.setCreator(officer);
		instance.setSurvey(survey);
		instance.setCustomer(client);
		instance.setDateConducted(DateUtils.getCurrentDateWithoutTimeStamp());
		new SurveysPersistence().createOrUpdate(instance);		
		HibernateUtil.commitTransaction();
		return instance;
	}
	
	public void testSurveyResponseWithChoices() throws Exception {
		SurveysPersistence persistence = new SurveysPersistence();
		SurveyInstance instance = makeSurveyInstance("Test choice type survey response");
		Survey survey = instance.getSurvey();
		String questionText = "Why did the chicken cross the road?";
		String shortName = "Chicken Question";
		Question question = new Question(shortName, questionText, AnswerType.CHOICE);
		QuestionChoice choice1 = new QuestionChoice("To get to the other side.");
		QuestionChoice choice2 = new QuestionChoice("Exercise");
		List<QuestionChoice> choices = new LinkedList<QuestionChoice>();
		choices.add(choice1);
		choices.add(choice2);
		question.setChoices(choices);
		SurveyQuestion surveyQuestion = survey.addQuestion(question, false);
		HibernateUtil.getSessionTL().save(question);
		SurveyResponse response = new SurveyResponse();
		response.setSurveyQuestion(surveyQuestion);	
		response.setChoiceValue(choice1);
		response.setInstance(instance);
		HibernateUtil.getSessionTL().save(response);
		List<SurveyResponse> responses = persistence.retrieveAllResponses();
		assertEquals(1, responses.size());
		assertEquals(choice1.getChoiceId(), responses.get(0).getChoiceValue().getChoiceId());
	}
	
	// this test was created because of problems persisting number survey responses
	// in mayfly
	public void testNumberSurveyResponse() throws Exception {
		SurveyInstance instance = makeSurveyInstance("Test number survey response");
		Survey survey = instance.getSurvey();
		String questionText = "Sample question with a numeric answer";
		String shortName = "Sample Name";
		Question question = new Question(shortName, questionText, AnswerType.NUMBER);
		SurveyQuestion surveyQuestion = survey.addQuestion(question, false);
		HibernateUtil.getSessionTL().save(question);
		SurveyResponse response = new SurveyResponse();
		response.setSurveyQuestion(surveyQuestion);
		response.setNumberValue(new Double(5));
		response.setInstance(instance);
		HibernateUtil.getSessionTL().save(response);
		
		List<SurveyResponse> responses = new SurveysPersistence().retrieveAllResponses();
		assertEquals(1, responses.size());
		assertEquals(questionText, responses.get(0).getQuestion().getQuestionText());
	}
	
	public void testSurveyResponseTypechecks() throws Exception {
		SurveyInstance instance = makeSurveyInstance("Test survey response typechecks");
		Survey survey = instance.getSurvey();
		
		String questionText = "Dummy question text";
		String shortName = "Short name";
		Question question = new Question(shortName, questionText, AnswerType.FREETEXT);
		survey.addQuestion(question, true);
		
		String freetextAnswer = "Some answer";
		Date dateAnswer = new Date();
		BigDecimal numberAnswer = new BigDecimal("123.4");
		QuestionChoice choiceAnswer = new QuestionChoice("Some choice");
		question.addChoice(choiceAnswer);
		
		SurveyQuestion surveyQuestion = new SurveyQuestion();
		surveyQuestion.setQuestion(question);
		
		SurveyResponse response = new SurveyResponse(instance, surveyQuestion);
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
	
	public void testCreateRetrieveSurveyResponse() throws Exception {
		SurveysPersistence persistence = new SurveysPersistence();
		SurveyInstance instance1 = makeSurveyInstance("Test survey create response1");
		Survey survey = instance1.getSurvey();
		
		String questionText = "Text for testCreateSurveyResponse question";
		String shortName = "Short name uno";
		Question question = new Question(shortName, questionText, AnswerType.FREETEXT);
		SurveyQuestion surveyQuestion = survey.addQuestion(question, true);
		
		SurveyResponse response1 = new SurveyResponse();
		response1.setSurveyQuestion(surveyQuestion);
		String response1Value = "response 1 value";
		response1.setStringValue(response1Value);
		response1.setInstance(instance1);
		
		persistence.createOrUpdate(response1);
		List<SurveyResponse> allResponses = persistence.retrieveAllResponses();
		assertEquals(1, allResponses.size());
		
		questionText = "text for second testCreateSurveyResponse question";
		shortName = "Short name two";
		Question question2 = new Question(shortName, questionText, AnswerType.NUMBER);
		SurveyQuestion surveyQuestion2 = survey.addQuestion(question2, true);
		SurveyResponse response2 = new SurveyResponse();
		response2.setSurveyQuestion(surveyQuestion2);
		response2.setNumberValue(new Double(5));
		response2.setInstance(instance1);
		
		persistence.createOrUpdate(response2);
		allResponses = persistence.retrieveAllResponses();
		assertEquals(2, allResponses.size());
		
		SurveyInstance instance2 = makeSurveyInstance("Test survey create response2");
		SurveyResponse response3 = new SurveyResponse();
		response3.setInstance(instance2);
		response3.setSurveyQuestion(surveyQuestion2);
		response3.setStringValue("42");
		
		persistence.createOrUpdate(response3);
		allResponses = persistence.retrieveAllResponses();
		assertEquals(3, allResponses.size());
		
		
		List<SurveyResponse> responses = persistence.retrieveResponsesByInstance(instance1);
		assertEquals(2, responses.size());
		assertTrue(responses.contains(response1));
		assertTrue(responses.contains(response2));
		assertEquals(AnswerType.NUMBER, responses.get(1).getQuestion().getAnswerTypeAsEnum());
		assertEquals(5.0, responses.get(1).getNumberValue());
		assertEquals(response1Value, responses.get(0).getFreetextValue());
		
		responses = persistence.retrieveResponsesByInstance(instance2);
		assertEquals(1, responses.size());
		assertTrue(responses.contains(response3));
		assertEquals(AnswerType.NUMBER, responses.get(0).getQuestion().getAnswerTypeAsEnum());
		assertEquals(42.0, responses.get(0).getNumberValue());
		assertNull(responses.get(0).getFreetextValue());
		
		SurveyInstance retrievedInstance = persistence.getInstance(instance1.getInstanceId());
		responses = persistence.retrieveResponsesByInstance(retrievedInstance);
		assertEquals(2, responses.size());
	}
	
	public void testSerialize() throws Exception {
		Survey survey = new Survey(
			"my survey", SurveyState.ACTIVE, SurveyType.CLIENT);
		Question question = new Question("Can I be written to the session?");
		survey.addQuestion(question, false);
		
		TestUtils.assertCanSerialize(survey);
	}
	
}
