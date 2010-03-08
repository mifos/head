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

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.mifos.application.NamedQueryConstants;
import org.mifos.accounts.business.AccountBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.surveys.SurveysConstants;
import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.business.SurveyResponse;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.QuestionState;
import org.mifos.customers.surveys.helpers.SurveyState;
import org.mifos.customers.surveys.helpers.SurveyType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class SurveysPersistence extends Persistence {

    public List<Survey> retrieveAllSurveys() throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_ALL);
        return query.list();
    }

    public List<SurveyResponse> retrieveAllResponses() throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.RESPONSES_RETRIEVE_ALL);
        return query.list();
    }

    public List<SurveyResponse> retrieveResponsesByInstance(SurveyInstance instance) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.RESPONSES_RETRIEVE_BY_INSTANCE);
        query.setParameter("INSTANCE", instance);
        return query.list();
    }

    public List<Survey> retrieveSurveysByType(SurveyType type) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_TYPE);
        query.setParameter("SURVEY_TYPE", type.getValue());
        return query.list();
    }

    public List<Survey> retrieveSurveysByTypeAndState(SurveyType type, SurveyState state) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_TYPE_AND_STATE);
        query.setParameter("SURVEY_TYPE", type.getValue());
        query.setParameter("SURVEY_STATE", state.getValue());
        return query.list();
    }

    public List<Survey> retrieveCustomersSurveys() throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_CUSTOMERS_TYPES);
        return query.list();
    }

    public List<Survey> retrieveAccountsSurveys() throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_ACCOUNTS_TYPES);
        return query.list();
    }

    public List<Survey> retrieveSurveysByName(String name) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_TYPE);
        query.setParameter("SURVEY_NAME", name);
        return query.list();
    }

    public List<Survey> retrieveSurveysByStatus(SurveyState state) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_STATUS);
        query.setParameter("SURVEY_STATUS", state.getValue());
        return query.list();
    }

    public Survey getSurvey(int id) {
        return (Survey) getSession().get(Survey.class, id);
    }

    public SurveyInstance getInstance(int id) {
        return (SurveyInstance) getSession().get(SurveyInstance.class, id);
    }

    public int getNumQuestions() {
        Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_GET_NUM);
        return Integer.parseInt(query.list().get(0).toString());
    }

    public List<Question> retrieveAllQuestions() throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_RETRIEVE_ALL);
        return query.list();
    }

    public List<Question> getQuestionsByQuestionType(int questionType) throws PersistenceException {
        return getQuestionsByQuestionType(retrieveAllQuestions(), questionType);
    }

    public List<Question> getQuestionsByQuestionType(List<Question> questions, int questionType)
            throws PersistenceException {
        List<Question> filteredQuestions = new ArrayList<Question>();
        for (Question question : questions) {
            if (question.getQuestionType() == questionType) {
                filteredQuestions.add(question);
            }
        }
        return filteredQuestions;
    }

    public List<Question> retrieveSomeQuestions(int offset, int limit) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_RETRIEVE_ALL);
        query.setFirstResult(offset);
        query.setMaxResults(limit);
        return query.list();
    }

    public List<Question> retrieveGeneralQuestionsByState(QuestionState state) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_RETRIEVE_BY_STATE);
        query.setParameter("QUESTION_STATE", state.getValue());
        return getQuestionsByQuestionType(query.list(), SurveysConstants.QUESTION_TYPE_GENERAL);
    }

    public List<Question> retrieveQuestionsByAnswerType(AnswerType type) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_RETRIEVE_BY_TYPE);
        query.setParameter("ANSWER_TYPE", type.getValue());
        return query.list();
    }

    public Question getQuestion(int id) {
        return (Question) getSession().get(Question.class, id);
    }

    public List<Question> retrieveQuestionsByName(String name) {
        Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_RETRIEVE_BY_NAME);
        query.setParameter("SHORT_NAME", name);
        return query.list();
    }

    public List<SurveyInstance> retrieveInstancesByCustomer(CustomerBO customer) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYINSTANCE_RETRIEVE_BY_CUSTOMER);
        query.setParameter("INSTANCE_CUSTOMER", customer);
        return query.list();
    }

    public List<SurveyInstance> retrieveInstancesByAccount(AccountBO account) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYINSTANCE_RETRIEVE_BY_ACCOUNT);
        query.setParameter("INSTANCE_ACCOUNT", account);
        return query.list();
    }

    public List<SurveyInstance> retrieveInstancesBySurvey(Survey survey) throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYINSTANCE_RETRIEVE_BY_SURVEY);
        query.setParameter("INSTANCE_SURVEY", survey);
        return query.list();
    }
}
