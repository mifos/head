package org.mifos.application.surveys.business;

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
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.QuestionState;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.util.helpers.CustomFieldType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.hibernate.helper.HibernateUtil;
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
	
	public void testCreateSurvey() {
		Survey survey = new Survey();
		survey.setName("testsurvey");
		survey.setState(SurveyState.ACTIVE);
		survey.setAppliesTo("someone");
		
		session.save(survey);
		
		Session reader = database.openSession();
		List result = reader.createQuery("from " + Survey.class.getName()).list();
		assertEquals(1, result.size());
		Survey read_survey = (Survey) result.get(0);
		assertEquals("testsurvey", read_survey.getName());
		assertEquals(SurveyState.ACTIVE, read_survey.getStateAsEnum());
		assertEquals("someone", read_survey.getAppliesTo());
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
	
	private SurveyInstance makeSurveyInstance(String surveyName) throws PersonnelException {
		TestObjectFactory factory = new TestObjectFactory();
		ClientBO client = factory.createClient(
				"Test Client", CustomerStatus.CLIENT_PARTIAL, null);
		
		Survey survey = new Survey();
		survey.setName("Test survey");
		survey.setState(SurveyState.ACTIVE);
		survey.setAppliesTo("someone");

		OfficeBO office = factory.getOffice(TestObjectFactory.HEAD_OFFICE);
		Name name = new Name("XYZ", null, null, null);
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView((short) 9, "123456",
				CustomFieldType.NUMERIC));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = DateUtils.getCurrentDateWithoutTimeStamp();
		String officerName = "Test Officer";
		PersonnelBO officer = new PersonnelBO(PersonnelLevel.LOAN_OFFICER, office,
				Integer.valueOf("1"), TestObjectFactory.TEST_LOCALE, "PASSWORD",
				officerName, "xyz@yahoo.com", null, customFieldView, name,
				"111111", date, Integer.valueOf("1"), Integer.valueOf("1"),
				date, date, address, PersonnelConstants.SYSTEM_USER);

		
		SurveyInstance instance = new SurveyInstance();
		instance.setOfficer(officer);
		instance.setSurvey(survey);
		instance.setClient(client);
		return instance;
	}
	
	public void testSurveyResponseTypeChecks() throws Exception {
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
		response.setValue("2");
	}
	
	public void testCreateSurveyResponse() throws Exception {
		SurveyInstance instance = makeSurveyInstance("Test survey");
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
		response.setValue(Integer.toString(choice2.getChoiceId()));

		HibernateUtil.getSessionTL().saveOrUpdate(instance);
//		HibernateUtil.getSessionTL().saveOrUpdate(response);
		
		SurveyInstance refreshedInstance = 
			(SurveyInstance)HibernateUtil.getSessionTL().get(
				SurveyInstance.class, instance.getInstanceId());
		assertEquals(AnswerType.CHOICE,
			refreshedInstance.getSurvey().getQuestion(0).getAnswerTypeAsEnum());
		assertEquals("Test Client", refreshedInstance.getClient().getFirstName());
	}
	
}
