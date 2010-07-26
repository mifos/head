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

package org.mifos.platform.questionnaire.service;

import org.mifos.framework.exceptions.SystemException;

import java.util.List;

public interface QuestionnaireServiceFacade {
    void createQuestions(List<QuestionDetail> questionDetails) throws SystemException;

    boolean isDuplicateQuestion(String title);

    void createQuestionGroup(QuestionGroupDetail questionGroupDetail) throws SystemException;

    List<QuestionDetail> getAllQuestions();

    List<QuestionGroupDetail> getAllQuestionGroups();

    QuestionGroupDetail getQuestionGroupDetail(int questionGroupId) throws SystemException;

    QuestionDetail getQuestionDetail(int questionId) throws SystemException;

    List<EventSource> getAllEventSources();

    List<QuestionGroupDetail> getQuestionGroups(String event, String source) throws SystemException;

    void saveResponses(QuestionGroupDetails questionGroupDetails);

    void validateResponses(List<QuestionGroupDetail> questionGroupDetails);

    List<QuestionGroupDetail> getQuestionGroups(Integer entityId, String event, String source);

    List<QuestionGroupInstanceDetail> getQuestionGroupInstances(Integer entityId, String event, String source);
}
