package org.mifos.application.ppi.business;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.JUnit4TestAdapter;
import junitx.framework.ObjectAssert;

import org.joda.time.DateMidnight;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.ppi.persistence.PPIPersistence;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPPIChoice {
	
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
	public void retrievePPIChoice() throws Exception {
		Question question = 
			new Question("question1", "what is your question", AnswerType.CHOICE);
		PPIChoice ppiChoice = new PPIChoice("Hello World");
		question.addChoice(ppiChoice);
		ppiChoice.setPoints(34);
		new SurveysPersistence().createOrUpdate(question);
		int choiceId = ppiChoice.getChoiceId();

		database.installInThreadLocal();
		
		PPIChoice retrievedChoice = new PPIPersistence().getPPIChoice(choiceId);
		assertEquals("Hello World", retrievedChoice.getChoiceText());
	}
	
	@Test
	public void retrieveRegularChoice() throws Exception {
		Question question = 
			new Question("question1", "what is your question", AnswerType.CHOICE);
		QuestionChoice regularChoice = new QuestionChoice("Hello World");
		question.addChoice(regularChoice);
		new SurveysPersistence().createOrUpdate(question);
		int choiceId = regularChoice.getChoiceId();

		database.installInThreadLocal();
		
		QuestionChoice retrievedChoice = (QuestionChoice)
			new SurveysPersistence().getPersistentObject(QuestionChoice.class, choiceId);
		assertEquals("Hello World", retrievedChoice.getChoiceText());
		assertFalse(retrievedChoice instanceof PPIChoice);
		
		assertEquals(null, new PPIPersistence().getPPIChoice(choiceId));
	}
	
	@Test public void notFound() throws Exception {
		QuestionChoice retrieved = new PPIPersistence().getPPIChoice(123456);
		assertEquals(null, retrieved);
	}
	
	 @Test
	 @Ignore
 	/* Test throws a SQLException: Attempt to read SQL NULL as an object
 	 * When trying to retrieve responses from db.*/
	public void viaResponses() throws Exception {
		Question question = 
			new Question("question1", "what is your question", AnswerType.CHOICE);
		PPIChoice ppiChoice = new PPIChoice("Hello World");
		question.addChoice(ppiChoice);
		ppiChoice.setPoints(34);
		new SurveysPersistence().createOrUpdate(question);
		
		SurveyInstance instance = new SurveyInstance(); 
		PPISurvey survey = 
			new PPISurvey("name", SurveyState.ACTIVE, SurveyType.CLIENT, Country.INDIA);
		SurveyQuestion surveyQuestion = survey.addQuestion(question, true);
		instance.setSurvey(
				survey);
		instance.setDateConducted(new Date(new DateMidnight("2004-06-27").getMillis()));
		PersonnelBO systemUser = makeSystemUser();
		instance.setOfficer(systemUser);
		instance.setCreator(systemUser);
		Set<SurveyResponse> surveyResponses = new HashSet<SurveyResponse>();
		SurveyResponse surveyResponse = new SurveyResponse(instance, surveyQuestion);
		surveyResponse.setChoiceValue(ppiChoice);
		surveyResponses.add(surveyResponse);
		instance.setSurveyResponses(surveyResponses);
		assertEquals(1, instance.getSurveyResponses().size());
		
		new SurveysPersistence().createOrUpdate(instance);
		new SurveysPersistence().createOrUpdate(surveyResponse);
		int instanceId = instance.getInstanceId();
		database.installInThreadLocal();
		
		PPIPersistence ppiPersistence = new PPIPersistence();
		SurveyInstance retrievedInstance = ppiPersistence.getInstance(instanceId);
		assertFalse(null == retrievedInstance);
		assertFalse(null == retrievedInstance.getSurveyResponses());
		assertEquals(1, retrievedInstance.getSurveyResponses().size());
		//List<SurveyResponse> responseList =
			//ppiPersistence.retrieveResponsesByInstance(retrievedInstance);
		QuestionChoice retrievedChoice = ((SurveyResponse)retrievedInstance.getSurveyResponses().toArray()[0])
		                                         .getChoiceValue(); 
		ObjectAssert.assertInstanceOf(PPIChoice.class, retrievedChoice);
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
		return new JUnit4TestAdapter(TestPPIChoice.class);
	}

}
