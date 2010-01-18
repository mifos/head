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

package org.mifos.application.surveys.struts.action;

import java.util.List;

import junit.framework.Assert;

import org.apache.struts.action.ActionMapping;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;

public class SurveysActionStrutsTest extends MifosMockStrutsTestCase {

    public SurveysActionStrutsTest() throws Exception {
        super();
    }

    ActionMapping moduleMapping;
    private Question question;
    private Question question2;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        UserContext userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
    }

    @Override
    protected void tearDown() throws Exception {
        TestDatabase.resetMySQLDatabase();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.flushAndCloseSession();
    }

    private Survey makeTestSurvey(String surveyName, String questionText) throws Exception {
        Survey survey = new Survey(surveyName, SurveyState.ACTIVE, SurveyType.CLIENT);
        Question question = new Question(surveyName + questionText, questionText, AnswerType.FREETEXT);
        survey.addQuestion(question, false);
        new SurveysPersistence().createOrUpdate(survey);
        return survey;
    }

    public void testPreviewFailureNoName() throws Exception {
        setRequestPathInfo("/surveysAction");
        addRequestParameter("method", "create_entry");

        addRequestParameter("value(name)", "");
        addRequestParameter("value(appliesTo)", "client");
        addRequestParameter("value(state)", "ACTIVE");
        addRequestParameter("method", "preview");
        actionPerform();
        verifyActionErrors(new String[] { "errors.NotNullEmptyValidator.MISSING_FIELD" });
    }

    public void testMainpage() throws Exception {
        String testName = "Test Survey 1";
        String questionText = "A question";
        makeTestSurvey(testName, questionText);
        setRequestPathInfo("/surveysAction");
        addRequestParameter("method", "mainpage");
        actionPerform();
        verifyNoActionErrors();
        List<Survey> surveys = (List<Survey>) request.getAttribute(SurveysConstants.KEY_CLIENT_SURVEYS_LIST);
       Assert.assertEquals(testName, surveys.get(0).getName());
       Assert.assertEquals(1, surveys.get(0).getQuestions().size());
    }

    public void testGetAndPrint() throws Exception {
        String testName = "Test Survey 2";
        String questionText = "Some question here";
        Survey survey = makeTestSurvey(testName, questionText);

        setRequestPathInfo("/surveysAction");
        addRequestParameter("method", "get");
        addRequestParameter("value(surveyId)", Integer.toString(survey.getSurveyId()));
        actionPerform();
        verifyNoActionErrors();
        Survey retrievedSurvey = (Survey) request.getSession().getAttribute(Constants.BUSINESS_KEY);
       Assert.assertEquals(testName, retrievedSurvey.getName());
        Question question = retrievedSurvey.getQuestion(0);
       Assert.assertEquals(questionText, question.getQuestionText());

        setRequestPathInfo("/surveysAction");
        addRequestParameter("method", "printVersion");
        addRequestParameter("value(surveyId)", Integer.toString(survey.getSurveyId()));
        actionPerform();
        verifyNoActionErrors();
    }

    public void testCreateEntry() throws Exception {
        String name = "test";
        String appliesTo = "client";
        String state = "ACTIVE";
        SurveysPersistence surveysPersistence = new SurveysPersistence();
       Assert.assertEquals(0, surveysPersistence.retrieveAllSurveys().size());
        String questionText = "testCreateEntry question 1";
        String shortName = "testCreateEntry 1";
        question = new Question(shortName, questionText, AnswerType.CHOICE);
        surveysPersistence.createOrUpdate(question);
        setRequestPathInfo("/surveysAction");
        addRequestParameter("method", "create_entry");
        actionPerform();
        verifyNoActionErrors();
        List<Question> questionsList = (List<Question>) request.getSession().getAttribute(
                SurveysConstants.KEY_QUESTIONS_LIST);
        List<Question> addedQuestions = (List<Question>) request.getSession().getAttribute(
                SurveysConstants.KEY_ADDED_QUESTIONS);
       Assert.assertEquals(1, questionsList.size());
       Assert.assertEquals(0, addedQuestions.size());

        addRequestParameter("value(newQuestion)", Integer.toString(question.getQuestionId()));
        setRequestPathInfo("/surveysAction");
        addRequestParameter("method", "add_new_question");
        actionPerform();
        verifyNoActionErrors();
       Assert.assertEquals(0, questionsList.size());
       Assert.assertEquals(1, addedQuestions.size());

        addRequestParameter("value(name)", name);
        addRequestParameter("value(appliesTo)", appliesTo);
        addRequestParameter("value(state)", state);
        addRequestParameter("method", "preview");
        actionPerform();
        verifyNoActionErrors();

        addRequestParameter("method", "create");
        actionPerform();
        verifyNoActionErrors();
       Assert.assertEquals(1, surveysPersistence.retrieveAllSurveys().size());
       Assert.assertEquals(name, surveysPersistence.retrieveAllSurveys().get(0).getName());
    }

    public void testEditEntry() throws Exception {
        SurveysPersistence surveysPersistence = new SurveysPersistence();
        String questionText = "testCreateEntry question 1";
        String shortName = "testCreateEntry 1";
        question = new Question(shortName, questionText, AnswerType.CHOICE);
        question2 = new Question(shortName + 1, questionText + 1, AnswerType.CHOICE);
        surveysPersistence.createOrUpdate(question);
        surveysPersistence.createOrUpdate(question2);

        String state = "INACTIVE";
        String name = "test";
        String appliesTo = "client";
        Survey oldSurvey = new Survey(name, SurveyState.ACTIVE, SurveyType.fromString(appliesTo));
        oldSurvey.addQuestion(question2, true);
        surveysPersistence.createOrUpdate(oldSurvey);

        int surveyId = surveysPersistence.retrieveAllSurveys().get(0).getSurveyId();
        setRequestPathInfo("/surveysAction");
        addRequestParameter("method", "edit_entry");
        addRequestParameter("value(surveyId)", Integer.toString(surveyId));
        actionPerform();
        verifyNoActionErrors();
       Assert.assertEquals(1, ((List) getSession().getAttribute(SurveysConstants.KEY_ADDED_QUESTIONS)).size());
       Assert.assertEquals(1, ((List) getSession().getAttribute(SurveysConstants.KEY_QUESTIONS_LIST)).size());

        addRequestParameter("method", "add_new_question");
        addRequestParameter("value(newQuestion)", Integer.toString(question.getQuestionId()));
        actionPerform();
        verifyNoActionErrors();

        addRequestParameter("value(name)", "new name");
        addRequestParameter("value(appliesTo)", "all");
        addRequestParameter("value(state)", state);
        addRequestParameter("method", "preview_update");
        actionPerform();
        verifyNoActionErrors();

        addRequestParameter("method", "update");
        actionPerform();
        verifyNoActionErrors();

        List<Survey> listSurvey = surveysPersistence.retrieveAllSurveys();
       Assert.assertEquals(1, listSurvey.size());
        Survey survey = listSurvey.get(0);
       Assert.assertEquals("new name", survey.getName());
       Assert.assertEquals(SurveyType.ALL, survey.getAppliesToAsEnum());
       Assert.assertEquals(SurveyState.INACTIVE, survey.getStateAsEnum());
       Assert.assertEquals(surveyId, survey.getSurveyId());
       Assert.assertEquals(2, survey.getQuestions().size());
    }
}
