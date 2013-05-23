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

package org.mifos.platform.questionnaire.domain;

import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionLinkDetail;
import org.mifos.platform.questionnaire.service.SectionLinkDetail;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface QuestionnaireService {
    QuestionDetail defineQuestion(QuestionDetail questionDetail) throws SystemException;

    List<QuestionDetail> getAllQuestions();

    List<QuestionDetail> getAllActiveQuestions(List<Integer> questionIdsToExclude);

    QuestionGroupDetail defineQuestionGroup(QuestionGroupDetail questionGroupDetail) throws SystemException;

    List<QuestionGroupDetail> getAllQuestionGroups();

    boolean isDuplicateQuestionText(String title);

    QuestionGroupDetail getQuestionGroup(int questionGroupId) throws SystemException;

    QuestionDetail getQuestion(int questionId) throws SystemException;

    List<EventSourceDto> getAllEventSources();

    List<QuestionGroupDetail> getQuestionGroups(EventSourceDto eventSourceDto) throws SystemException;

    void saveResponses(QuestionGroupDetails questionGroupDetails);

    void validateResponses(List<QuestionGroupDetail> questionGroupDetails);

    List<QuestionGroupInstanceDetail> getQuestionGroupInstances(Integer entityId, EventSourceDto eventSourceDto, boolean includeUnansweredQuestionGroups, boolean fetchLastVersion);

    QuestionGroupInstanceDetail getQuestionGroupInstance(int questionGroupInstanceId);

    Integer defineQuestionGroup(QuestionGroupDto questionGroupDto);

    Integer defineQuestionGroup(QuestionGroupDto questionGroupDto, boolean withDuplicateQuestionTextCheck);

    List<String> getAllCountriesForPPI();

    Integer uploadPPIQuestionGroup(String country);

    Integer saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto);

    // Added for data migration. Shouldn't be used outside.
    Integer getSectionQuestionId(String sectionName, Integer questionId, Integer questionGroupId);

    // Added for data migration. Shouldn't be used outside.
    Integer defineQuestion(QuestionDto questionDto);

    EventSourceDto getEventSource(int eventSourceId);

    Integer getEventSourceId(String event, String source);
    
    QuestionGroup getQuestionGroupById(Integer questionGroupId);

    Map<String, Map<Integer, Boolean>> getHiddenVisibleQuestionsAndSections(
			Integer questionId, String response) throws ParseException;
    
    List<LookUpValueEntity>  getAllConditions();
    
    void createQuestionLinks (List<QuestionLinkDetail> questionLinks);
    
    void createSectionLinks(List<SectionLinkDetail> sectionLinks);

}
