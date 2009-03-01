package org.mifos.application.ppi.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.ppi.business.PPILikelihood;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.ppi.business.PPISurveyInstance;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PPIPersistenceTest extends MifosTestCase {
	public PPIPersistenceTest() throws SystemException, ApplicationException {
        super();
    }

    private static final double DELTA = 0.00000001;
    private TestDatabase database;
	private PPIPersistence persistence;
	
	@Override
	@Before
	public void setUp() {
		DatabaseSetup.initializeHibernate();
		database = TestDatabase.makeStandard();
		database.installInThreadLocal();
		persistence = new PPIPersistence();
	}

	@Override
	@Before
	public void tearDown() {
		HibernateUtil.resetDatabase();
	}
	
	@Test
	public void testLikelihoods() throws Exception {
		int surveyId = createSurveyWithLikelihoods("surveyName");
		PPISurvey retreivedSurvey = (PPISurvey) persistence.getSurvey(surveyId);
		assertNotNull(retreivedSurvey);
		PPILikelihood lh = retreivedSurvey.getLikelihood(17);
		assertEquals(0, lh.getScoreFrom());
		assertEquals(20, lh.getScoreTo());
		assertEquals(80.0, lh.getBottomHalfBelowPovertyLinePercent(), DELTA);
		assertEquals(20.0, lh.getTopHalfBelowPovertyLinePercent(), DELTA);
		
		lh = retreivedSurvey.getLikelihood(46);
		assertEquals(21, lh.getScoreFrom());
		assertEquals(100, lh.getScoreTo());
		assertEquals(30.0, lh.getBottomHalfBelowPovertyLinePercent(), DELTA);
		assertEquals(70.0, lh.getTopHalfBelowPovertyLinePercent(), DELTA);
	}
	
	@Test
	public void testRetrieveActivePPISurvey() throws Exception {
		createSurveyWithLikelihoods("surveyName");
		assertEquals("surveyName", persistence.retrieveActivePPISurvey().getName());
	}
	
	@Test
	public void testRetrieveAllPPISurveys() throws Exception {
		createSurveyWithLikelihoods("survey1");
		assertEquals(1, persistence.retrieveAllPPISurveys().size());
	}
	
	@Test
	public void testRetrievePPISurveyByCountry() throws Exception {
		createSurveyWithLikelihoods("surveyForIndia");
		assertEquals(Country.INDIA, persistence.retrievePPISurveyByCountry(Country.INDIA).getCountryAsEnum());
	}
	
	@Test
	public void testGetPPISurvey() throws Exception {
		createSurveyWithLikelihoods("surveyName");
		assertEquals("surveyName", persistence.getPPISurvey(1).getName());
	}
	
	@Test
	public void testPersistPPISurveyInstance() throws Exception {
		int surveyId = createSurveyWithLikelihoods("surveyName");
		PPISurvey survey = persistence.getPPISurvey(surveyId);
		int instanceId = createSurveyInstance(survey);
		
		PPISurveyInstance retrievedInstance = (PPISurveyInstance) persistence.getInstance(instanceId);
		assertEquals("surveyName", retrievedInstance.getSurvey().getName());
		assertEquals(5, retrievedInstance.getScore());
		assertEquals(80.0, retrievedInstance.getBottomHalfBelowPovertyLinePercent(), DELTA);
		assertEquals(20.0, retrievedInstance.getTopHalfBelowPovertyLinePercent(), DELTA);
	}

	private int createSurveyInstance(PPISurvey survey) throws Exception {
		PPISurveyInstance instance = new PPISurveyInstance();
		instance.setSurvey(survey);
		instance.setCreator(createOfficer());
		instance.setDateConducted(new Date());
		Set<SurveyResponse> responses = new HashSet<SurveyResponse>();
		responses.add(createSurveyResponse(instance));
		instance.setSurveyResponses(responses);
		instance.initialize();
		persistence.createOrUpdate(instance);
		return instance.getInstanceId();
	}

	private PersonnelBO createOfficer() throws Exception {
		TestObjectFactory factory = new TestObjectFactory();
		OfficeBO office = factory.getOffice(TestObjectFactory.HEAD_OFFICE);
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView((short) 9, "123456", CustomFieldType.NUMERIC));
		Name name = new Name("XYZ", null, null, null);
		Address address = new Address("abcd" + "ppiSurvey", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd", "abcd");
		Date date = new Date();
		return new PersonnelBO(PersonnelLevel.LOAN_OFFICER, office,
				Integer.valueOf("1"), TestObjectFactory.TEST_LOCALE, "PASSWORD",
				"officer" + System.currentTimeMillis(), "officer@mifos.org", null, customFieldView, name,
				"govId" + "ppiSurvey", date, Integer.valueOf("1"), Integer.valueOf("1"),
				date, date, address, PersonnelConstants.SYSTEM_USER);
	}

	private SurveyResponse createSurveyResponse(SurveyInstance instance) throws Exception {
		Question question = new Question("shortName", "questionText", AnswerType.CHOICE);
		PPIChoice choice1 = new PPIChoice("choice1");
		choice1.setPoints(5);
		PPIChoice choice2 = new PPIChoice("choice2");
		choice2.setPoints(10);
		question.addChoice(choice1);
		question.addChoice(choice2);
		SurveyQuestion surveyQuestion = new SurveyQuestion();
		surveyQuestion.setSurvey(instance.getSurvey());
		surveyQuestion.setQuestion(question);
		surveyQuestion.setOrder(0);
		SurveyResponse response = new SurveyResponse(instance, surveyQuestion);
		response.setChoiceValue(choice1);
		persistence.createOrUpdate(response);
		return response;
	}
	
	private int createSurveyWithLikelihoods(String name) throws Exception {
		PPISurvey survey = new PPISurvey(name, SurveyState.ACTIVE, SurveyType.CLIENT, Country.INDIA);
		List<PPILikelihood> list = new ArrayList<PPILikelihood>();
		PPILikelihood likelihood1 = new PPILikelihood(0, 20, 80, 20);
		likelihood1.setSurvey(survey);
		list.add(likelihood1);
		PPILikelihood likelihood2 = new PPILikelihood(21, 100, 30, 70);
		likelihood2.setSurvey(survey);
		list.add(likelihood2);
		survey.setLikelihoods(list);
		persistence.createOrUpdate(survey);
		return survey.getSurveyId();
	}
}
