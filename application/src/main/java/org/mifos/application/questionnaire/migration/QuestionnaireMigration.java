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

package org.mifos.application.questionnaire.migration;

import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.questionnaire.migration.mappers.QuestionnaireMigrationMapper;
import org.mifos.customers.surveys.business.Survey;
import org.mifos.customers.surveys.business.SurveyInstance;
import org.mifos.customers.surveys.persistence.SurveysPersistence;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireMigration {

    @Autowired
    private QuestionnaireMigrationMapper questionnaireMigrationMapper;

    @Autowired
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Autowired
    private SurveysPersistence surveysPersistence;

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireMigration() {
        // used for Spring wiring
    }

    // Intended to be used only from unit tests for injecting mocks
    public QuestionnaireMigration(QuestionnaireMigrationMapper questionnaireMigrationMapper,
                                  QuestionnaireServiceFacade questionnaireServiceFacade,
                                  SurveysPersistence surveysPersistence) {
        this.questionnaireMigrationMapper = questionnaireMigrationMapper;
        this.questionnaireServiceFacade = questionnaireServiceFacade;
        this.surveysPersistence = surveysPersistence;
    }

    public Integer migrate(List<CustomFieldDefinitionEntity> customFields) {
        QuestionGroupDto questionGroupDto = questionnaireMigrationMapper.map(customFields);
        return questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
    }

    public List<Integer> migrateSurveys(List<Survey> surveys) throws PersistenceException {
        List<Integer> questionGroupIds = new ArrayList<Integer>();
        for (Survey survey : surveys) {
            Integer questionGroupId = migrateSurvey(survey);
            questionGroupIds.add(questionGroupId);
        }
        return questionGroupIds;
    }

    private Integer migrateSurvey(Survey survey) throws PersistenceException {
        QuestionGroupDto questionGroupDto = questionnaireMigrationMapper.map(survey);
        Integer questionGroupId = questionnaireServiceFacade.createQuestionGroup(questionGroupDto);
        migrateSurveyResponses(survey, questionGroupId);
        return questionGroupId;
    }

    private void migrateSurveyResponses(Survey survey, Integer questionGroupId) throws PersistenceException {
        List<SurveyInstance> surveyInstances = surveysPersistence.retrieveInstancesBySurvey(survey);
        for (SurveyInstance surveyInstance : surveyInstances) {
            QuestionGroupInstanceDto questionGroupInstanceDto = questionnaireMigrationMapper.map(surveyInstance, questionGroupId);
            questionnaireServiceFacade.saveQuestionGroupInstance(questionGroupInstanceDto);
        }
    }
}