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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.surveys.SurveysConstants;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.MifosMockStrutsTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;

public class QuestionsActionStrutsTest extends MifosMockStrutsTestCase {

    public QuestionsActionStrutsTest() throws Exception {
        super();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        UserContext userContext = TestUtils.makeUser();
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
        ActivityContext ac = new ActivityContext((short) 0, userContext.getBranchId().shortValue(), userContext.getId()
                .shortValue());
        request.getSession(false).setAttribute("ActivityContext", ac);
        request.getSession().setAttribute(Constants.USERCONTEXT, userContext);
    }

    @Override
    protected void tearDown() throws Exception {
        TestDatabase.resetMySQLDatabase();
    }

    private Question makeTestSelectQuestion(String name, int choiceNumber) throws Exception {
        AnswerType type = AnswerType.fromInt(4);
        Question question = new Question(name, name, type);
        List<QuestionChoice> choices = new ArrayList<QuestionChoice>();
        for (int i = 1; i <= choiceNumber; i++) {
            choices.add(new QuestionChoice("Test Choice " + i));
        }
        question.setChoices(choices);
        new SurveysPersistence().createOrUpdate(question);
        return question;
    }

    private Question makeTestNumberQuestion(String name, int min, int max) throws Exception {
        AnswerType type = AnswerType.fromInt(3);
        Question question = new Question(name, name, type);
        question.setNumericMin(min);
        question.setNumericMax(max);
        new SurveysPersistence().createOrUpdate(question);
        return question;
    }

    private Question makeTestTextQuestion(String name) throws Exception {
        AnswerType type = AnswerType.fromInt(3);
        Question question = new Question(name, name, type);
        new SurveysPersistence().createOrUpdate(question);
        return question;
    }

    private Question makeTestDateQuestion(String name) throws Exception {
        AnswerType type = AnswerType.fromInt(5);
        Question question = new Question(name, name, type);
        new SurveysPersistence().createOrUpdate(question);
        return question;
    }

    public void testDefineQuestion() throws Exception {
        String questionText1 = "testDefineQuestion sample text one";
        String shortName1 = "testDefineQuestion 1";
        setRequestPathInfo("/questionsAction");
        addRequestParameter("method", "defineQuestions");
        List<String> choices = new LinkedList<String>();
        choices.add("TestChoice1");
        choices.add("TestChoice2");
        getRequest().getSession().setAttribute(SurveysConstants.KEY_NEW_QUESTION_CHOICES, choices);
        actionPerform();
        verifyNoActionErrors();

        addRequestParameter("value(shortName)", shortName1);
        addRequestParameter("value(questionText)", questionText1);
        addRequestParameter("value(answerType)", Integer.toString(AnswerType.FREETEXT.getValue()));
        addRequestParameter("method", "addQuestion");
        actionPerform();
        verifyNoActionErrors();
        List<Question> newQuestions = (List<Question>) request.getSession().getAttribute(
                SurveysConstants.KEY_NEW_QUESTIONS);
       Assert.assertEquals(1, newQuestions.size());
       Assert.assertEquals(questionText1, newQuestions.get(0).getQuestionText());
       Assert.assertEquals(AnswerType.FREETEXT, newQuestions.get(0).getAnswerTypeAsEnum());

        String questionText2 = "testDefineQuestion sample text two";
        String shortName2 = "testDefineQuestion 2";
        addRequestParameter("value(shortName)", shortName2);
        addRequestParameter("value(questionText)", questionText2);
        addRequestParameter("value(answerType)", Integer.toString(AnswerType.CHOICE.getValue()));
        choices = new LinkedList<String>();
        choices.add("TestChoice1");
        choices.add("TestChoice2");
        getRequest().getSession().setAttribute(SurveysConstants.KEY_NEW_QUESTION_CHOICES, choices);
        addRequestParameter("value(choice)", "testDefineQuestion sample choice");
        addRequestParameter("method", "addChoice");
        actionPerform();
        verifyNoActionErrors();
        List<String> newChoices = (List<String>) request.getSession().getAttribute(
                SurveysConstants.KEY_NEW_QUESTION_CHOICES);
       Assert.assertEquals(3, newChoices.size());
        addRequestParameter("method", "addQuestion");
        actionPerform();
        verifyNoActionErrors();
        newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
       Assert.assertEquals(2, newQuestions.size());
       Assert.assertEquals(shortName2, newQuestions.get(1).getShortName());
       Assert.assertEquals(questionText2, newQuestions.get(1).getQuestionText());
       Assert.assertEquals(AnswerType.CHOICE, newQuestions.get(1).getAnswerTypeAsEnum());
       Assert.assertEquals(3, newQuestions.get(1).getChoices().size());

        String questionText3 = "testDefineQuestions question text 3";
        String shortName3 = "testDefineQuestions 3";
        addRequestParameter("value(shortName)", shortName3);
        addRequestParameter("value(questionText)", questionText3);
        addRequestParameter("method", "addQuestion");
        getRequest().getSession().setAttribute(SurveysConstants.KEY_NEW_QUESTION_CHOICES, choices);
        actionPerform();
        verifyNoActionErrors();
        newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
       Assert.assertEquals(3, newQuestions.size());
       Assert.assertEquals(questionText3, newQuestions.get(2).getQuestionText());
       Assert.assertEquals(shortName3, newQuestions.get(2).getShortName());
        addRequestParameter("newQuestionNum", Integer.toString(newQuestions.size() - 1));
        addRequestParameter("method", "deleteNewQuestion");
        actionPerform();
        verifyNoActionErrors();
        newQuestions = (List<Question>) request.getSession().getAttribute(SurveysConstants.KEY_NEW_QUESTIONS);
       Assert.assertEquals(2, newQuestions.size());

        addRequestParameter("method", "createQuestions");
        actionPerform();
        verifyNoActionErrors();
        SurveysPersistence persistence = new SurveysPersistence();
        List<Question> dbQuestions = persistence.retrieveAllQuestions();
       Assert.assertEquals(2, dbQuestions.size());
       Assert.assertEquals(questionText1, dbQuestions.get(0).getQuestionText());
       Assert.assertEquals(questionText2, dbQuestions.get(1).getQuestionText());

    }

    public void testViewQuestions() throws Exception {

        // Set up test cases
        List<Question> testQuestionList = new ArrayList<Question>();
        testQuestionList.add(makeTestSelectQuestion("Select Text", 5));
        testQuestionList.add(makeTestNumberQuestion("Number Text", 0, 100));
        testQuestionList.add(makeTestTextQuestion("Freetext Text"));
        testQuestionList.add(makeTestDateQuestion("Date Text"));

        // Initialize QuestionsAction and retrieve Questions from session
        setRequestPathInfo("/questionsAction");
        addRequestParameter("method", "viewQuestions");
        actionPerform();
        verifyNoActionErrors();
        List<Question> retrievedQuestionList = (List<Question>) request
                .getAttribute(SurveysConstants.KEY_QUESTIONS_LIST);
        // Tests
        Collections.sort(retrievedQuestionList);
        Collections.sort(testQuestionList);
        for (int i = 0; i < testQuestionList.size(); i++) {
            Question tempQuestion = retrievedQuestionList.get(i);
           Assert.assertEquals(testQuestionList.get(i).getQuestionText(), tempQuestion.getQuestionText());
           Assert.assertEquals(testQuestionList.get(i).getNumericMin(), tempQuestion.getNumericMin());
           Assert.assertEquals(testQuestionList.get(i).getNumericMax(), tempQuestion.getNumericMax());
           Assert.assertEquals(testQuestionList.get(i).getChoices().size(), tempQuestion.getChoices().size());
        }
    }

    public void testEdit() throws Exception {
        setRequestPathInfo("/questionsAction");
        addRequestParameter("method", "defineQuestions");
        actionPerform();
        verifyNoActionErrors();

        addRequestParameter("value(shortName)", "Short Name 1");
        addRequestParameter("value(questionText)", "Question Text 1");
        addRequestParameter("value(answerType)", Integer.toString(AnswerType.FREETEXT.getValue()));
        addRequestParameter("method", "addQuestion");
        actionPerform();
        verifyNoActionErrors();

        addRequestParameter("method", "createQuestions");
        actionPerform();
        verifyNoActionErrors();

        SurveysPersistence persistence = new SurveysPersistence();
        int questionId = persistence.retrieveAllQuestions().get(0).getQuestionId();

        addRequestParameter("method", "get");
        addRequestParameter("questionId", Integer.toString(questionId));
        actionPerform();
        verifyNoActionErrors();

        addRequestParameter("method", "edit_entry");
        actionPerform();

        String shortName = "New Name";
        String questionText = "New question text";
        addRequestParameter("value(shortName)", shortName);
        addRequestParameter("value(questionText)", questionText);
        addRequestParameter("value(status)", "1");
        addRequestParameter("method", "preview_entry");

        addRequestParameter("method", "update_entry");
        actionPerform();
        verifyNoActionErrors();

        Question newQuestion = persistence.getQuestion(questionId);

       Assert.assertEquals(newQuestion.getShortName(), shortName);
       Assert.assertEquals(newQuestion.getQuestionText(), questionText);
    }

    public void testEditPPI() throws Exception {
        Question question = new Question("New Q", "What's the q?", AnswerType.CHOICE);

        PPIChoice choice = new PPIChoice("choice 1");
        choice.setPoints(11);

        question.addChoice(choice);

        request.getSession().setAttribute(SurveysConstants.KEY_QUESTION, question);

        int id = question.getQuestionId();

        setRequestPathInfo("/questionsAction");
        addRequestParameter("questionId", Integer.toString(id));
        addRequestParameter("method", "edit_entry");
        actionPerform();
        verifyActionErrors(new String[] { "errors.readonly" });
    }

}
