/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.application.ppi.business;

import static org.junit.Assert.assertEquals;
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
import org.mifos.application.ppi.helpers.XmlPPISurveyParser;
import org.mifos.application.ppi.persistence.PPIPersistence;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyQuestion;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.framework.MifosInMemoryIntegrationTest;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.springframework.core.io.ClassPathResource;
import org.mifos.config.GeneralConfig;

public class PPISurveyIntegrationTest extends MifosInMemoryIntegrationTest {
    private static final double DELTA = 0.00000001;
    private PPIPersistence persistence;
    private TestDatabase database;

    @Before
    public void setUp() {
        super.setUp();
        persistence = new PPIPersistence();
        database = TestDatabase.makeStandard();
        database.installInThreadLocal();
    }

    @After
    public void tearDown() {
        StaticHibernateUtil.resetDatabase();
        super.tearDown();
    }

    @Test
    public void testCreateSurveyInstance() throws Exception {
        Survey survey = new PPISurvey();
        SurveyInstance instance = survey.createSurveyInstance();
        assertTrue("Instance should be instance of PpiSurveyInstance", PPISurvey.class.isInstance(survey));
    }

    @Test
    public void createSurvey() throws Exception {
        PPISurvey ppiSurvey = makePPISurvey("PPI Test Survey");

        Survey regularSurvey = new Survey("NON-PPI Test Survey", SurveyState.ACTIVE, SurveyType.CLIENT);

        persistence.createOrUpdate(ppiSurvey);
        persistence.createOrUpdate(regularSurvey);

        assertEquals(1, persistence.retrieveAllPPISurveys().size());
        assertEquals(2, persistence.retrieveAllSurveys().size());

        PPISurvey dbPPISurvey = persistence.retrieveActivePPISurvey();
        assertNotNull(dbPPISurvey);
        assertEquals(ppiSurvey.getQuestions().size(), dbPPISurvey.getQuestions().size());
        assertEquals(ppiSurvey.getNonPoorMin(), dbPPISurvey.getNonPoorMin());
        assertEquals(ppiSurvey.getName(), dbPPISurvey.getName());
        assertEquals(ppiSurvey.getCountry(), dbPPISurvey.getCountry());
    }

    public static PPISurvey makePPISurvey(String name) throws ValidationException {
        PPISurvey survey = new PPISurvey(name, SurveyState.ACTIVE, SurveyType.CLIENT, Country.INDIA);

        survey.setVeryPoorMin(0);
        survey.setVeryPoorMax(25);
        survey.setPoorMin(26);
        survey.setPoorMax(50);
        survey.setAtRiskMin(51);
        survey.setAtRiskMax(75);
        survey.setNonPoorMin(76);
        survey.setNonPoorMax(100);

        List<SurveyQuestion> questions = new ArrayList<SurveyQuestion>();

        Question question = new Question("Test Question 1", "What is this question?", AnswerType.CHOICE);
        question.setChoices(new ArrayList<QuestionChoice>());
        PPIChoice choice = new PPIChoice("First choice");
        choice.setChoiceId(1);
        choice.setPoints(20);
        question.getChoices().add(choice);
        survey.addQuestion(question, true);
        survey.setQuestions(questions);

        addLikelihoods(survey);
        return survey;
    }

    private static void addLikelihoods(PPISurvey survey) throws ValidationException {
        List<PPILikelihood> likelihoods = new ArrayList<PPILikelihood>();
        PPILikelihood likelihood1 = new PPILikelihood(0, 49, 10, 50);
        likelihood1.setSurvey(survey);
        likelihoods.add(likelihood1);
        PPILikelihood likelihood2 = new PPILikelihood(50, 100, 20, 30);
        likelihood2.setSurvey(survey);
        likelihoods.add(likelihood2);
        survey.setLikelihoods(likelihoods);
    }

    @Test
    public void testDefaultPovertyBandLimits() throws Exception {
        PPISurvey ppiSurvey = new PPISurvey("PPI Test Survey", SurveyState.ACTIVE, SurveyType.CLIENT, Country.INDIA);
        ppiSurvey.populateDefaultValues();
        int nonPoorMax = GeneralConfig.getMaxPointsPerPPISurvey();

        assertTrue(0 == ppiSurvey.getVeryPoorMin());
        assertTrue(ppiSurvey.getVeryPoorMin() <= ppiSurvey.getVeryPoorMax());
        assertTrue(ppiSurvey.getVeryPoorMax() == ppiSurvey.getPoorMin() - 1);
        assertTrue(ppiSurvey.getPoorMin() <= ppiSurvey.getPoorMax());
        assertTrue(ppiSurvey.getPoorMax() == ppiSurvey.getAtRiskMin() - 1);
        assertTrue(ppiSurvey.getAtRiskMin() <= ppiSurvey.getAtRiskMax());
        assertTrue(ppiSurvey.getAtRiskMax() == ppiSurvey.getNonPoorMin() - 1);
        assertTrue(ppiSurvey.getNonPoorMin() <= ppiSurvey.getNonPoorMax());
        assertTrue(ppiSurvey.getNonPoorMax() == nonPoorMax);
    }

    @Test
    public void retrieve() throws Exception {
        PPISurvey ppiSurvey = new PPISurvey("PPI Test Survey", SurveyState.ACTIVE, SurveyType.CLIENT, Country.INDIA);
        addLikelihoods(ppiSurvey);
        persistence.createOrUpdate(ppiSurvey);

        PPISurvey retrievedPPISurvey = persistence.retrieveActivePPISurvey();
        assertEquals("PPI Test Survey", retrievedPPISurvey.getName());
    }

    @Test
    public void retrieveById() throws Exception {
        PPISurvey ppiSurvey = new PPISurvey("PPI Test Survey", SurveyState.ACTIVE, SurveyType.CLIENT, Country.INDIA);
        addLikelihoods(ppiSurvey);
        persistence.createOrUpdate(ppiSurvey);

        Survey retrievedPPISurvey = persistence.getSurvey(ppiSurvey.getSurveyId());
        assertEquals("PPI Test Survey", retrievedPPISurvey.getName());
    }

    @Test
    public void retrieveRegularSurvey() throws Exception {
        Survey regularSurvey = new Survey("PPI Test Survey", SurveyState.ACTIVE, SurveyType.CLIENT);
        persistence.createOrUpdate(regularSurvey);
        int surveyId = regularSurvey.getSurveyId();

        Survey retrievedSurvey = persistence.getSurvey(surveyId);
        assertEquals("PPI Test Survey", retrievedSurvey.getName());
        assertFalse(retrievedSurvey instanceof PPISurvey);

        assertEquals(null, new PPIPersistence().getPPISurvey(surveyId));
    }

    @Test
    public void notFound() throws Exception {
        Survey retrieved = persistence.getSurvey(23423);
        assertEquals(null, retrieved);
    }

    @Test
    public void viaInstance() throws Exception {
        PPISurvey ppiSurvey = makePPISurvey("survey name");
        persistence.createOrUpdate(ppiSurvey);

        SurveyInstance instance = new SurveyInstance();
        instance.setSurvey(ppiSurvey);
        instance.setDateConducted(new Date(new DateMidnight("2007-06-27").getMillis()));
        PersonnelBO systemUser = makeSystemUser();
        instance.setOfficer(systemUser);
        instance.setCreator(systemUser);
        persistence.createOrUpdate(instance);
        int instanceId = instance.getInstanceId();

        SurveyInstance retrieved = persistence.getInstance(instanceId);
        Survey directSurvey = retrieved.getSurvey();
        // ObjectAssert.assertNotInstanceOf(PPISurvey.class, directSurvey);
        ObjectAssert.assertInstanceOf(Survey.class, directSurvey);

        Survey retrievedSurvey = persistence.getPPISurvey(directSurvey.getSurveyId());
        ObjectAssert.assertInstanceOf(PPISurvey.class, retrievedSurvey);
    }

    @Test
    public void testLoadFromXmlAndStoreToDatabase() throws Exception {
        XmlPPISurveyParser parser = new XmlPPISurveyParser();
        PPISurvey survey = new PPISurvey();
        ClassPathResource surveyXml = new ClassPathResource("org/mifos/framework/util/resources/ppi/PPISurveyINDIA.xml");
        parser.parseInto(surveyXml.getPath(), survey);

        survey.setAppliesTo(SurveyType.CLIENT);
        persistence.createOrUpdate(survey);

        PPISurvey retrievedSurvey = persistence.retrievePPISurveyByCountry(Country.INDIA);
        assertEquals("PPI Survey India", retrievedSurvey.getName());
        assertEquals(Country.INDIA, retrievedSurvey.getCountryAsEnum());

        assertEquals(10, retrievedSurvey.getQuestions().size());
        assertEquals("Number of children", retrievedSurvey.getQuestion(0).getShortName());
        assertEquals("What is the household's primary energy source for cooking?", retrievedSurvey.getQuestion(1)
                .getQuestionText());

        assertEquals(0, retrievedSurvey.getLikelihood(2).getOrder());
        assertEquals(1, retrievedSurvey.getLikelihood(5).getOrder());
        assertEquals(5, retrievedSurvey.getLikelihood(25).getOrder());

        assertEquals(6.9, retrievedSurvey.getLikelihood(43).getBottomHalfBelowPovertyLinePercent(), DELTA);
        assertEquals(29.5, retrievedSurvey.getLikelihood(43).getTopHalfBelowPovertyLinePercent(), DELTA);
    }

    private PersonnelBO makeSystemUser() throws Exception {
        Date date = new Date(new DateMidnight("2004-06-27").getMillis());
        Name name = new Name("XYZ", null, null, null);
        return new PersonnelBO(PersonnelLevel.LOAN_OFFICER, null, Integer.valueOf("1"), TestObjectFactory.TEST_LOCALE,
                "PASSWORD", "a test officer", "xyz@yahoo.com", null, null, name, "govId", date, Integer.valueOf("1"),
                Integer.valueOf("1"), date, date, null, PersonnelConstants.SYSTEM_USER);
    }

    public static junit.framework.Test suite() {
        return new JUnit4TestAdapter(PPISurveyIntegrationTest.class);
    }

}
