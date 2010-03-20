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

package org.mifos.customers.surveys.persistence;

import java.util.List;

import junit.framework.Assert;

import org.mifos.customers.ppi.business.PPIChoice;
import org.mifos.customers.surveys.SurveysConstants;
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.business.QuestionChoice;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.PersistenceException;

public class SurveysPersistenceIntegrationTest extends MifosIntegrationTestCase {

    public SurveysPersistenceIntegrationTest() throws Exception {
        super();
    }

    private Question question1;
    private Question question2;
    private Question question3;

    public void testPpi() throws Exception {
        createQuestions();
        SurveysPersistence persistence = new SurveysPersistence();

        Question question = persistence.getQuestion(question1.getQuestionId());
       Assert.assertEquals(SurveysConstants.QUESTION_TYPE_GENERAL, question.getQuestionType());
       Assert.assertEquals("question1", question.getShortName());

        question = persistence.getQuestion(question2.getQuestionId());
       Assert.assertEquals(SurveysConstants.QUESTION_TYPE_PPI, question.getQuestionType());
       Assert.assertEquals("this is a ppi question", question.getQuestionText());

        List<Question> allQuestions = persistence.retrieveAllQuestions();
       Assert.assertEquals(3, allQuestions.size());

        List<Question> ppiQuestions = persistence.getQuestionsByQuestionType(SurveysConstants.QUESTION_TYPE_PPI);
       Assert.assertEquals(2, ppiQuestions.size());

        List<Question> generalQuestions = persistence
                .getQuestionsByQuestionType(SurveysConstants.QUESTION_TYPE_GENERAL);
       Assert.assertEquals(1, generalQuestions.size());
    }

    private void createQuestions() throws PersistenceException {
        question1 = new Question("question1", "this is a non-ppi question", AnswerType.CHOICE);
        QuestionChoice regularChoice1 = new QuestionChoice("Hello World 1");
        QuestionChoice regularChoice2 = new QuestionChoice("Hello World 2");
        question1.addChoice(regularChoice1);
        question1.addChoice(regularChoice2);

        question2 = new Question("question2", "this is a ppi question", AnswerType.CHOICE);
        PPIChoice ppiChoice1 = new PPIChoice("Hello PPI World 1");
        PPIChoice ppiChoice2 = new PPIChoice("Hello PPI World 2");
        question2.addChoice(ppiChoice1);
        question2.addChoice(ppiChoice2);

        question3 = new Question("question3", "this is another ppi question", AnswerType.CHOICE);
        PPIChoice ppiChoice3 = new PPIChoice("Hello PPI World 3");
        PPIChoice ppiChoice4 = new PPIChoice("Hello PPI World 4");
        PPIChoice ppiChoice5 = new PPIChoice("Hello PPI World 5");
        question3.addChoice(ppiChoice3);
        question3.addChoice(ppiChoice4);
        question3.addChoice(ppiChoice5);

        SurveysPersistence surveysPersistence = new SurveysPersistence();
        surveysPersistence.createOrUpdate(question1);
        surveysPersistence.createOrUpdate(question2);
        surveysPersistence.createOrUpdate(question3);
    }
}
