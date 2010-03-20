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

package org.mifos.customers.surveys.struts.tag;

import java.sql.Statement;

import junit.framework.Assert;

import org.hibernate.Session;
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.struts.tags.XmlBuilder;

public class QuestionTagIntegrationTest extends MifosIntegrationTestCase {

    public QuestionTagIntegrationTest() throws Exception {
        super();
    }

    Session session;

    XmlBuilder result;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        session = StaticHibernateUtil.getSessionTL();
        result = new XmlBuilder();
    }

    @Override
    public void tearDown() throws Exception {
        Statement stmt = session.connection().createStatement();
        stmt.execute("truncate questions");
        StaticHibernateUtil.resetDatabase();
        super.tearDown();
    }

    public void testFreetext() throws Exception {
        SurveysPersistence persistence = new SurveysPersistence();
        String questionText = "question";
        String shortName = "QuestionTagIntegrationTest Name";
        Question question = new Question(shortName, questionText, AnswerType.FREETEXT);
        persistence.createOrUpdate(question);
        QuestionTag tag = new QuestionTag(question.getQuestionId(), "freetext answer", false);
        String markup = tag.getQuestionMarkup(result);
        String expectedMarkup = "<textarea class=\"surveyform freetext fontnormal8pt\" "
                + "cols=\"70\" rows=\"10\" name=\"value(response_1)\">freetext answer</textarea>";
       Assert.assertEquals(expectedMarkup, markup);
    }

    public void testNumber() throws Exception {
        SurveysPersistence persistence = new SurveysPersistence();
        String questionText = "question";
        String shortName = "QuestionTagIntegrationTest Name1";
        Question question = new Question(shortName, questionText, AnswerType.NUMBER);
        persistence.createOrUpdate(question);
        QuestionTag tag = new QuestionTag(question.getQuestionId(), "42", false);
        String expectedMarkup = "<input type=\"text\" class=\"surveyform number fontnormal8t\" "
                + "name=\"value(response_" + question.getQuestionId() + ")\" value=\"42\" />";
        String markup = tag.getQuestionMarkup(result);

       Assert.assertEquals(expectedMarkup, markup);

    }

    public void testDate() throws Exception {
        SurveysPersistence persistence = new SurveysPersistence();
        String questionText = "question";
        String shortName = "QuestionTagIntegrationTest Name2";
        Question question = new Question(shortName, questionText, AnswerType.DATE);
        persistence.createOrUpdate(question);
        QuestionTag tag = new QuestionTag(question.getQuestionId(), "20/3/2000", false);
        String expectedMarkup = "<input type=\"text\" maxlength=\"2\""
                + " size=\"2\" class=\"surveyform dateinput fontnormal8pt\"" + " name=\"value(response_"
                + question.getQuestionId() + "_DD)\"" + " value=\"20\" /> DD "
                + "<input type=\"text\" maxlength=\"2\" size=\"2\"" + " class=\"surveyform dateinput fontnormal8pt\""
                + " name=\"value(response_" + question.getQuestionId() + "_MM)\"" + " value=\"3\" /> MM "
                + "<input type=\"text\" maxlength=\"4\" size=\"4\"" + " class=\"surveyform dateinput fontnormal8pt\""
                + " name=\"value(response_" + question.getQuestionId() + "_YY)\"" + " value=\"2000\" /> YYYY ";
        String markup = tag.getQuestionMarkup(result);

       Assert.assertEquals(expectedMarkup, markup);
    }

}
