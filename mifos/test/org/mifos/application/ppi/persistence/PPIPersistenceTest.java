package org.mifos.application.ppi.persistence;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mifos.application.ppi.business.PPILikelihood;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class PPIPersistenceTest extends MifosTestCase {
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
		assertEquals(80.0, lh.getBottomHalfBelowPovertyLinePercent());
		assertEquals(20.0, lh.getTopHalfBelowPovertyLinePercent());
		
		lh = retreivedSurvey.getLikelihood(46);
		assertEquals(21, lh.getScoreFrom());
		assertEquals(100, lh.getScoreTo());
		assertEquals(30.0, lh.getBottomHalfBelowPovertyLinePercent());
		assertEquals(70.0, lh.getTopHalfBelowPovertyLinePercent());
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
	
	private int createSurveyWithLikelihoods(String name) throws ValidationException, PersistenceException {
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
