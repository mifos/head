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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.InstanceStatus;
import org.mifos.customers.surveys.helpers.SurveyState;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.domain.builders.CenterBuilder;
import org.mifos.domain.builders.ClientBuilder;
import org.mifos.domain.builders.GroupBuilder;
import org.mifos.domain.builders.LoanAccountBuilder;
import org.mifos.domain.builders.OfficeBuilder;
import org.mifos.domain.builders.SavingsAccountBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.Money;

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
    public static Survey getSurvey(String surveyName, String questionTitle, Date dateofCreation, SurveyType surveyType) {
        Survey survey = new Survey(surveyName, SurveyState.ACTIVE, surveyType);
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
        Question question = new Question(questionTitle, AnswerType.FREETEXT);
        question.setNickname(questionTitle);
        surveyQuestion.setQuestion(question);
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

    public static ClientBO getClientBO(Integer clientId) {
        Money.setDefaultCurrency(TestUtils.RUPEE);
        ClientBO clientBO = new ClientBuilder().buildForUnitTests();
        clientBO.setCustomerId(clientId);
        return clientBO;
    }

    public static GroupBO getGroupBO(Integer groupId) {
        Money.setDefaultCurrency(TestUtils.RUPEE);
        GroupBO groupBO = new GroupBuilder().build();
        groupBO.setCustomerId(groupId);
        return groupBO;
    }

    public static CenterBO getCenterBO(Integer centerId) {
        Money.setDefaultCurrency(TestUtils.RUPEE);
        CenterBO centerBO = new CenterBuilder().build();
        centerBO.setCustomerId(centerId);
        return centerBO;
    }

    public static LoanBO getLoanBO(Integer loanId) {
        Money.setDefaultCurrency(TestUtils.RUPEE);
        LoanBO loanBO = new LoanAccountBuilder().build();
        loanBO.setAccountId(loanId);
        return loanBO;
    }

    public static SavingsBO getSavingsBO(SavingsAccountBuilder builder, Integer savingsId) {
        Money.setDefaultCurrency(TestUtils.RUPEE);
        SavingsBO savingsBO = builder.buildForUnitTests();
        savingsBO.setAccountId(savingsId);
        return savingsBO;
    }

    public static OfficeBO getOfficeBO(Integer officeId) {
        Money.setDefaultCurrency(TestUtils.RUPEE);
        OfficeBO officeBO = new OfficeBuilder().build();
        officeBO.setOfficeId(officeId.shortValue());
        return officeBO;
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
