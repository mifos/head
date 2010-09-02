/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.customers.surveys.business;

import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.SurveyState;
import org.mifos.customers.surveys.helpers.SurveyType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class SurveyUtils {
    private static final Calendar calendar = Calendar.getInstance();

    public static Survey getSurvey(String surveyName, String questionTitle) {
        return getSurvey(surveyName, questionTitle, calendar.getTime());
    }

    public static Survey getSurvey(String surveyName, String questionTitle, Date dateofCreation) {
        Survey survey = new Survey(surveyName, SurveyState.ACTIVE, SurveyType.CLIENT);
        survey.setDateOfCreation(dateofCreation);
        List<SurveyQuestion> surveyQuestions = new ArrayList<SurveyQuestion>();
        surveyQuestions.add(getSurveyQuestion(questionTitle, survey));
        survey.setQuestions(surveyQuestions);
        return survey;
    }

    private static SurveyQuestion getSurveyQuestion(String questionTitle, Survey survey) {
        SurveyQuestion surveyQuestion = new SurveyQuestion();
        surveyQuestion.setMandatory(true);
        surveyQuestion.setOrder(0);
        surveyQuestion.setSurvey(survey);
        surveyQuestion.setQuestion(new Question(questionTitle, questionTitle, AnswerType.FREETEXT));
        return surveyQuestion;
    }
}
