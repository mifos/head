/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.ppi.business;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;
import junitx.framework.ObjectAssert;

import org.joda.time.DateMidnight;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.ppi.helpers.Country;
import org.mifos.customers.ppi.persistence.PPIPersistence;
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.business.QuestionChoice;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.business.SurveyQuestion;
import org.mifos.customers.surveys.business.SurveyResponse;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.SurveyState;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PPIChoiceIntegrationTest extends MifosIntegrationTestCase {

    public PPIChoiceIntegrationTest() throws Exception {
        super();
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        TestDatabase.resetMySQLDatabase();
    }

    public void testRetrievePPIChoice() throws Exception {
        Question question = new Question("question1", "what is your question", AnswerType.CHOICE);
        PPIChoice ppiChoice = new PPIChoice("Hello World");
        question.addChoice(ppiChoice);
        ppiChoice.setPoints(34);
        new SurveysPersistence().createOrUpdate(question);
        int choiceId = ppiChoice.getChoiceId();

        PPIChoice retrievedChoice = new PPIPersistence().getPPIChoice(choiceId);
       Assert.assertEquals("Hello World", retrievedChoice.getChoiceText());
    }

    public void testRetrieveRegularChoice() throws Exception {
        Question question = new Question("question2", "what is your question", AnswerType.CHOICE);
        QuestionChoice regularChoice = new QuestionChoice("Hello World");
        question.addChoice(regularChoice);
        new SurveysPersistence().createOrUpdate(question);
        int choiceId = regularChoice.getChoiceId();

        QuestionChoice retrievedChoice = (QuestionChoice) new SurveysPersistence().getPersistentObject(
                QuestionChoice.class, choiceId);
       Assert.assertEquals("Hello World", retrievedChoice.getChoiceText());
        Assert.assertFalse(retrievedChoice instanceof PPIChoice);

       Assert.assertEquals(null, new PPIPersistence().getPPIChoice(choiceId));
    }

    public void testNotFound() throws Exception {
        QuestionChoice retrieved = new PPIPersistence().getPPIChoice(123456);
       Assert.assertEquals(null, retrieved);
    }

    /*
     * Test throws a SQLException: Attempt to read SQL NULL as an object When
     * trying to retrieve responses from db.
     */
    // marked @Ignore in JUnit 4 so converting to xtest
    public void xtestViaResponses() throws Exception {
        Question question = new Question("question3", "what is your question", AnswerType.CHOICE);
        PPIChoice ppiChoice = new PPIChoice("Hello World");
        question.addChoice(ppiChoice);
        ppiChoice.setPoints(34);
        new SurveysPersistence().createOrUpdate(question);

        SurveyInstance instance = new SurveyInstance();
        PPISurvey survey = new PPISurvey("name", SurveyState.ACTIVE, SurveyType.CLIENT, Country.INDIA);
        SurveyQuestion surveyQuestion = survey.addQuestion(question, true);
        instance.setSurvey(survey);
        instance.setDateConducted(new Date(new DateMidnight("2004-06-27").getMillis()));
        PersonnelBO systemUser = makeSystemUser();
        instance.setOfficer(systemUser);
        instance.setCreator(systemUser);
        Set<SurveyResponse> surveyResponses = new HashSet<SurveyResponse>();
        SurveyResponse surveyResponse = new SurveyResponse(instance, surveyQuestion);
        surveyResponse.setChoiceValue(ppiChoice);
        surveyResponses.add(surveyResponse);
        instance.setSurveyResponses(surveyResponses);
       Assert.assertEquals(1, instance.getSurveyResponses().size());

        new SurveysPersistence().createOrUpdate(instance);
        new SurveysPersistence().createOrUpdate(surveyResponse);
        int instanceId = instance.getInstanceId();

        PPIPersistence ppiPersistence = new PPIPersistence();
        SurveyInstance retrievedInstance = ppiPersistence.getInstance(instanceId);
        Assert.assertFalse(null == retrievedInstance);
        Assert.assertFalse(null == retrievedInstance.getSurveyResponses());
       Assert.assertEquals(1, retrievedInstance.getSurveyResponses().size());
        // List<SurveyResponse> responseList =
        // ppiPersistence.retrieveResponsesByInstance(retrievedInstance);
        QuestionChoice retrievedChoice = ((SurveyResponse) retrievedInstance.getSurveyResponses().toArray()[0])
                .getChoiceValue();
        ObjectAssert.assertInstanceOf(PPIChoice.class, retrievedChoice);
    }

    private PersonnelBO makeSystemUser() throws Exception {
        Date date = new Date(new DateMidnight("2004-06-27").getMillis());
        Name name = new Name("XYZ", null, null, null);
        return new PersonnelBO(PersonnelLevel.LOAN_OFFICER, null, Integer.valueOf("1"), TestObjectFactory.TEST_LOCALE,
                "PASSWORD", "a test officer", "xyz@yahoo.com", null, null, name, "govId", date, Integer.valueOf("1"),
                Integer.valueOf("1"), date, date, null, PersonnelConstants.SYSTEM_USER);
    }

}
