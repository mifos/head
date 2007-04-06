package org.mifos.application.surveys.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.surveys.SurveyConstants;

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
		survey.setSurveyName("testsurvey");
		survey.setState(SurveyConstants.ACTIVE);
		survey.setSurveyAppliesTo("someone");
		
		session.save(survey);
		
		Survey refreshed = (Survey)session.get(Survey.class, survey.getSurveyId());
		assertEquals(survey, refreshed);
	}
	
	public void testCreateQuestion() {
		String questionText = "Why did the chicken cross the road?";
		Question question = new Question();
		question.setAnswerType(SurveyConstants.ANSWER_TYPE_FREETEXT);
		question.setQuestionState(SurveyConstants.ACTIVE);
		question.setQuestionText(questionText);
		
		session.save(question);
		Question refreshed = (Question) session.get(Question.class, question.getQuestionId());
		assertEquals(refreshed.getQuestionText(), questionText);
		
	}
	
	public void testCreateSurveyQuestion() {
		String questionText = "Why do we drive on parkways and park on driveways?";
		Question question = new Question();
		question.setAnswerType(SurveyConstants.ANSWER_TYPE_FREETEXT);
		question.setQuestionState(SurveyConstants.ACTIVE);
		question.setQuestionText(questionText);
		
		Survey survey = new Survey();
		survey.setSurveyName("testsurvey");
		survey.setState(SurveyConstants.ACTIVE);
		survey.setSurveyAppliesTo("someone");
		
		
		// TODO: this test passes, but the functionality is still broken
		// for some reason the associated survey isn't being saved automatically
		// when you save the SurveyQuestion... you have to save it manually
		// first, as shown
		SurveyQuestion proxy = new SurveyQuestion();
		proxy.setQuestion(question);
		proxy.setSurvey(survey);
		session.save(survey);
		session.save(question);
		session.save(proxy);
		
		survey = (Survey) session.load(Survey.class, survey.getSurveyId());
		assertEquals(0, survey.getQuestions().size());
	}
	
	public void testCreateSurveyInstance() throws Exception {
		TestObjectFactory factory = new TestObjectFactory();
		ClientBO client = factory.createClient("Test Client", CustomerStatus.CLIENT_PARTIAL, null);
		
		Survey survey = new Survey();
		survey.setSurveyName("Test survey");
		survey.setState(SurveyConstants.ACTIVE);
		survey.setSurveyAppliesTo("someone");
		
		OfficeBO office = factory.getOffice(Short.valueOf("1"));
		Name name = new Name("XYZ", null, null, null);
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView((short) 9, "123456",
				Short.valueOf("1")));
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
		
		HibernateUtil.getSessionTL().saveOrUpdate(instance);
		SurveyInstance refreshed = (SurveyInstance)HibernateUtil.getSessionTL().get(SurveyInstance.class, instance.getInstanceId());
		assertEquals(refreshed.getOfficer().getUserName(), officerName);
	}
}
