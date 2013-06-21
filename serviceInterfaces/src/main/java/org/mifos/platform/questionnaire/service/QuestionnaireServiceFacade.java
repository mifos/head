/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.springframework.security.access.prepost.PreAuthorize;

public interface QuestionnaireServiceFacade {
    void createQuestions(List<QuestionDetail> questionDetails) throws SystemException;

    boolean isDuplicateQuestion(String title);

    QuestionGroupDetail createQuestionGroup(QuestionGroupDetail questionGroupDetail) throws SystemException;

    void createQuestionLinks(List<QuestionLinkDetail> questionLinks);
    
    void createSectionLinks(List<SectionLinkDetail> sectionLinks);
    
    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_ACTIVATE_QUESTION_GROUPS')")
    QuestionGroupDetail createActiveQuestionGroup(QuestionGroupDetail questionGroupDetail) throws SystemException;

    List<QuestionDetail> getAllQuestions();

    List<QuestionDetail> getAllActiveQuestions();

    List<QuestionDetail> getAllActiveQuestions(List<Integer> excludedQuestions);

    List<QuestionGroupDetail> getAllQuestionGroups();

    QuestionGroupDetail getQuestionGroupDetail(Integer questionGroupId) throws SystemException;
    
    QuestionGroupDetail getQuestionGroupDetailForLoanPrd(Integer questionGroupId) throws SystemException;

    QuestionDetail getQuestionDetail(Integer questionId) throws SystemException;

    List<EventSourceDto> getAllEventSources();

    Integer getEventSourceId(String event, String source);

    List<QuestionGroupDetail> getQuestionGroups(String event, String source) throws SystemException;

    @PreAuthorize("isFullyAuthenticated() and hasRole('CAN_EDIT_QUESTION_GROUP_RESPONSES')")
    void saveResponses(QuestionGroupDetails questionGroupDetails);

    void validateResponses(List<QuestionGroupDetail> questionGroupDetails);

    List<QuestionGroupInstanceDetail> getQuestionGroupInstances(Integer entityId, String event, String source);

    List<QuestionGroupInstanceDetail> getQuestionGroupInstancesWithUnansweredQuestionGroups(Integer entityId, String event, String source);

    QuestionGroupInstanceDetail getQuestionGroupInstance(Integer questionGroupInstanceId);

    Integer createQuestionGroup(QuestionGroupDto questionGroupDto) throws SystemException;

    List<String> getAllCountriesForPPI();

    @PreAuthorize("isFullyAuthenticated() and hasRole('ROLE_CAN_MANAGE_QUESTION_GROUPS')")
    void uploadPPIQuestionGroup(String country);

    Integer saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto);

    // Added for data migration. Shouldn't be used outside.
    Integer getSectionQuestionId(String sectionName, Integer questionId, Integer questionGroupId);

    // Added for data migration. Shouldn't be used outside.
    Integer createQuestion(QuestionDto questionDto);

    void applyToAllLoanProducts(Integer entityId);
    
    Map<String, Map<Integer, Boolean>> getHiddenVisibleQuestionsAndSections(Integer questionId, String response) throws ParseException;

    Map<String, String> getAllLinkTypes();
}
