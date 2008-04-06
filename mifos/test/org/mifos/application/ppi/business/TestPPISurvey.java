package org.mifos.application.ppi.business;

import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junit.framework.JUnit4TestAdapter;
import junitx.framework.ObjectAssert;

import org.joda.time.DateMidnight;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.ppi.persistence.PPIPersistence;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPPISurvey {
	
	private TestDatabase database;

	@Before public void setUp() {
		DatabaseSetup.initializeHibernate();
		database = TestDatabase.makeStandard();
		database.installInThreadLocal();
	}

	@After
	public void tearDown() {
		HibernateUtil.resetDatabase();
	}
	
	@Test
	public void testCreateSurveyInstance() throws Exception {
		Survey survey = new PPISurvey();
		SurveyInstance instance = survey.createInstance();
		assertTrue("Instance should be instance of PpiSurveyInstance",
				PPISurvey.class.isInstance(survey));
		}

	@Test
	public void createSurvey() throws Exception {
		PPISurvey survey = makePPISurvey("PPI Test Survey");
		
		Survey regularSurvey = new Survey("NON-PPI Test Survey", SurveyState.ACTIVE,
				SurveyType.CLIENT);
		
		PPIPersistence ppiPersistence = new PPIPersistence();
		ppiPersistence.createOrUpdate(survey);
		ppiPersistence.createOrUpdate(regularSurvey);
		
		
		assertEquals(1, ppiPersistence.retrieveAllPPISurveys().size());
		assertEquals(2, ppiPersistence.retrieveAllSurveys().size());
		
		PPISurvey dbPPISurvey =  ppiPersistence.retrieveActivePPISurvey();
		assertNotNull(dbPPISurvey);
		assertEquals(survey.getQuestions().size(), dbPPISurvey.getQuestions().size());
		assertEquals(survey.getNonPoorMin(), dbPPISurvey.getNonPoorMin());
		assertEquals(survey.getName(), dbPPISurvey.getName());
		assertEquals(survey.getCountry(), dbPPISurvey.getCountry());
	}

	public static PPISurvey makePPISurvey(String name) {
		PPISurvey survey = new PPISurvey(name, SurveyState.ACTIVE,
				SurveyType.CLIENT, Country.INDIA);
		
		survey.setVeryPoorMin(0);
		survey.setVeryPoorMax(25);
		survey.setPoorMin(26);
		survey.setPoorMax(50);
		survey.setAtRiskMin(51);
		survey.setAtRiskMax(75);
		survey.setNonPoorMin(76);
		survey.setNonPoorMax(100);
		
		List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();
		
		Question question = new Question("Test Question 1", "What is this question?",
				AnswerType.CHOICE);
		question.setChoices(new ArrayList<QuestionChoice>());
		PPIChoice choice = new PPIChoice("First choice");
		choice.setChoiceId(1);
		choice.setPoints(20);
		question.getChoices().add(choice);
		survey.addQuestion(question, true);
		
		survey.setQuestions(questions);
		return survey;
	}
	
	@Test
	public void testDefaultPovertyBandLimits() throws Exception {
		PPISurvey ppiSurvey = new PPISurvey("PPI Test Survey", SurveyState.ACTIVE,
				SurveyType.CLIENT, Country.INDIA);
		ppiSurvey.populateDefaultValues();
		
		assertTrue(0 == ppiSurvey.getVeryPoorMin());
		assertTrue (ppiSurvey.getVeryPoorMin()<= ppiSurvey.getVeryPoorMax());
		assertTrue (ppiSurvey.getVeryPoorMax() == ppiSurvey.getPoorMin() - 1);
		assertTrue (ppiSurvey.getPoorMin() <= ppiSurvey.getPoorMax());
		assertTrue (ppiSurvey.getPoorMax() == ppiSurvey.getAtRiskMin() - 1);
		assertTrue (ppiSurvey.getAtRiskMin() <= ppiSurvey.getAtRiskMax());
		assertTrue (ppiSurvey.getAtRiskMax() == ppiSurvey.getNonPoorMin() - 1);
		assertTrue (ppiSurvey.getNonPoorMin() <= ppiSurvey.getNonPoorMax());
		assertTrue (ppiSurvey.getNonPoorMax() == 100);
	}
	
	@Test
	public void retrieve() throws Exception {
		PPISurvey ppiSurvey = new PPISurvey("PPI Test Survey", SurveyState.ACTIVE,
			SurveyType.CLIENT, Country.INDIA);
		new PPIPersistence().createOrUpdate(ppiSurvey);

		database.installInThreadLocal();
		
		PPISurvey retrievedPPISurvey = new PPIPersistence().retrieveActivePPISurvey();
		assertEquals("PPI Test Survey", retrievedPPISurvey.getName());
	}
	
	@Test
	public void retrieveById() throws Exception {
		PPISurvey ppiSurvey = new PPISurvey("PPI Test Survey", SurveyState.ACTIVE,
			SurveyType.CLIENT, Country.INDIA);
		new PPIPersistence().createOrUpdate(ppiSurvey);
		int surveyId = ppiSurvey.getSurveyId();

		database.installInThreadLocal();
		
		PPISurvey retrievedPPISurvey = (PPISurvey) new PPIPersistence().getSurvey(surveyId);
		assertEquals("PPI Test Survey", retrievedPPISurvey.getName());
	}
	
	@Test
	public void retrieveRegularSurvey() throws Exception {
		Survey regularSurvey = new Survey("PPI Test Survey", SurveyState.ACTIVE,
			SurveyType.CLIENT);
		new SurveysPersistence().createOrUpdate(regularSurvey);
		int surveyId = regularSurvey.getSurveyId();

		database.installInThreadLocal();
		
		Survey retrievedSurvey = new SurveysPersistence().getSurvey(surveyId);
		assertEquals("PPI Test Survey", retrievedSurvey.getName());
		assertFalse(retrievedSurvey instanceof PPISurvey);
		
		assertEquals(null, new PPIPersistence().getPPISurvey(surveyId));
	}
	
	@Test public void notFound() throws Exception {
		Survey retrieved = new SurveysPersistence().getSurvey(23423);
		assertEquals(null, retrieved);
	}
	
	@Test
	public void viaInstance() throws Exception {
		PPISurvey ppiSurvey = new PPISurvey("PPI Test Survey", SurveyState.ACTIVE,
			SurveyType.CLIENT, Country.INDIA);
		new PPIPersistence().createOrUpdate(ppiSurvey);
		
		SurveyInstance instance = new SurveyInstance();
		instance.setSurvey(ppiSurvey);
		instance.setDateConducted(new Date(new DateMidnight("2007-06-27").getMillis()));
		PersonnelBO systemUser = makeSystemUser();
		instance.setOfficer(systemUser);
		instance.setCreator(systemUser);
		new PPIPersistence().createOrUpdate(instance);
		int instanceId = instance.getInstanceId();

		database.installInThreadLocal();
		
		SurveyInstance retrieved = new PPIPersistence().getInstance(instanceId);
		Survey directSurvey = retrieved.getSurvey();
		ObjectAssert.assertNotInstanceOf(PPISurvey.class, directSurvey);
		ObjectAssert.assertInstanceOf(Survey.class, directSurvey);

		Survey retrievedSurvey = 
			new PPIPersistence().getPPISurvey(directSurvey.getSurveyId());
		ObjectAssert.assertInstanceOf(PPISurvey.class, retrievedSurvey);
	}
	
	private PersonnelBO makeSystemUser() throws Exception {
		Date date = new Date(new DateMidnight("2004-06-27").getMillis());
		Name name = new Name("XYZ", null, null, null);
		return new PersonnelBO(PersonnelLevel.LOAN_OFFICER, null,
			Integer.valueOf("1"), TestObjectFactory.TEST_LOCALE, "PASSWORD",
			"a test officer", "xyz@yahoo.com", null, null, name,
			"govId", date, Integer.valueOf("1"), Integer.valueOf("1"),
			date, date, null, PersonnelConstants.SYSTEM_USER);
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestPPISurvey.class);
	}

}
