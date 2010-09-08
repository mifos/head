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

import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.InstanceStatus;
import org.mifos.customers.surveys.helpers.SurveyState;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.Money;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public static SurveyInstance getSurveyInstance(Survey survey, Integer personnelId, Integer customerId, String response) throws ApplicationException {
        SurveyInstance surveyInstance = new SurveyInstance();
        surveyInstance.setCompletedStatus(InstanceStatus.COMPLETED);
        surveyInstance.setCreator(new PersonnelBO(Short.valueOf(personnelId.toString()), "mifos", "mifos", null));
        surveyInstance.setCustomer(getClientBO(customerId));
        surveyInstance.setDateConducted(calendar.getTime());
        surveyInstance.setSurvey(survey);
        surveyInstance.setSurveyResponses(getSurveyResponses(survey, response, surveyInstance));
        return surveyInstance;
    }

    private static Set<SurveyResponse> getSurveyResponses(Survey survey, String response, SurveyInstance surveyInstance) throws ApplicationException {
        Set<SurveyResponse> surveyResponses = new HashSet<SurveyResponse>();
        surveyResponses.add(getSurveyResponse(survey, response, surveyInstance));
        return surveyResponses;
    }

    public static ClientBO getClientBO(Integer customerId) {
        Money.setDefaultCurrency(TestUtils.RUPEE);
        ClientBO clientBO = new ClientBuilder().buildForUnitTests();
        clientBO.setCustomerId(customerId);
        return clientBO;
    }

    public static GroupBO getGroupBO(Integer customerId) {
        Money.setDefaultCurrency(TestUtils.RUPEE);
        GroupBO groupBO = new GroupBuilder().build();
        groupBO.setCustomerId(customerId);
        return groupBO;
    }

    private static SurveyResponse getSurveyResponse(Survey survey, String response, SurveyInstance surveyInstance) throws ApplicationException {
        SurveyResponse surveyResponse = new SurveyResponse();
        surveyResponse.setInstance(surveyInstance);
        SurveyQuestion surveyQuestion = survey.getQuestions().get(0);
        surveyResponse.setSurveyQuestion(surveyQuestion);
        surveyResponse.setQuestion(surveyQuestion.getQuestion());
        surveyResponse.setValue(response);
        return surveyResponse;
    }
}
