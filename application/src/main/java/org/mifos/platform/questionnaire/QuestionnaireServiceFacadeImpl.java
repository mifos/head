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

package org.mifos.platform.questionnaire;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.ui.core.controller.Question;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static org.mifos.framework.util.MapEntry.makeEntry;

public class QuestionnaireServiceFacadeImpl implements QuestionnaireServiceFacade {

    @Autowired
    private QuestionnaireService questionnaireService;
    private Map<String, QuestionType> stringToQuestionTypeMap;

    public QuestionnaireServiceFacadeImpl(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
        populateStringToQuestionTypeMap();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireServiceFacadeImpl() {
        this(null);
    }

    @Override
    public void createQuestions(List<Question> questions) throws ApplicationException {
        for (Question question : questions) {
            questionnaireService.defineQuestion(mapToQuestionDefinition(question));
        }
    }

    @Override
    public boolean isDuplicateQuestion(String title) {
        return questionnaireService.isDuplicateQuestion(new QuestionDefinition(title));
    }

    @Override
    public void createQuestionGroup(QuestionGroupDetail questionGroupDetail) throws ApplicationException {
        questionnaireService.defineQuestionGroup(questionGroupDetail);
    }

    @Override
    public List<QuestionDetail> getAllQuestions() {
        return questionnaireService.getAllQuestions();
    }

    @Override
    public List<QuestionGroupDetail> getAllQuestionGroups() {
        return questionnaireService.getAllQuestionGroups();
    }

    @Override
    public QuestionGroupDetail getQuestionGroupDetail(int questionGroupId) throws ApplicationException {
        return questionnaireService.getQuestionGroup(questionGroupId);
    }

    @Override
    public QuestionDetail getQuestionDetail(int questionId) throws ApplicationException {
        return questionnaireService.getQuestion(questionId);
    }

    @Override
    public List<EventSource> getAllEventSources() {
        return questionnaireService.getAllEventSources();
    }

    private void populateStringToQuestionTypeMap() {
        stringToQuestionTypeMap = CollectionUtils.asMap(makeEntry("Free text", QuestionType.FREETEXT),
                makeEntry("Date", QuestionType.DATE),
                makeEntry("Number", QuestionType.NUMERIC));
    }

    private QuestionDefinition mapToQuestionDefinition(Question question) {
        return new QuestionDefinition(question.getTitle(), stringToQuestionTypeMap.get(question.getType()));
    }
}
