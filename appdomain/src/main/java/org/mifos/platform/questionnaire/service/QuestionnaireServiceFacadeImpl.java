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

import java.util.Iterator;
import java.util.List;

import org.mifos.application.admin.servicefacade.RolesPermissionServiceFacade;

import org.mifos.config.Localization;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.AuditLogService;
import org.mifos.platform.questionnaire.QGFlowsService;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionnaireService;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.security.util.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;

public class QuestionnaireServiceFacadeImpl implements QuestionnaireServiceFacade {

    @Autowired
    private QuestionnaireService questionnaireService;

    @Autowired
    private AuditLogService auditLogService;

    @Autowired
    private QGFlowsService qgFlowsService;
    
    @Autowired
    private RolesPermissionServiceFacade rolesPermissionService;

    public QuestionnaireServiceFacadeImpl(QuestionnaireService questionnaireService,RolesPermissionServiceFacade rolesPermissionService) {
        this.questionnaireService = questionnaireService;
        this.rolesPermissionService = rolesPermissionService;
    }

    public QuestionnaireServiceFacadeImpl(QuestionnaireService questionnaireService, AuditLogService auditLogService,
                                          QGFlowsService qgFlowsService) {
        this.questionnaireService = questionnaireService;
        this.auditLogService = auditLogService;
        this.qgFlowsService = qgFlowsService;
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireServiceFacadeImpl() {
        this(null,null);
    }

    @Override
    public void createQuestions(List<QuestionDetail> questionDetails) throws SystemException {
        for (QuestionDetail questionDetail : questionDetails) {
            questionnaireService.defineQuestion(questionDetail);
        }
    }

    @Override
    public boolean isDuplicateQuestion(String text) {
        return questionnaireService.isDuplicateQuestionText(text);
    }

    @Override
    public Integer createQuestionGroup(QuestionGroupDetail questionGroupDetail) throws SystemException {
        questionGroupDetail.setActivityId(addActivityPermission(questionGroupDetail.getTitle(), questionGroupDetail.getId()));
        return questionnaireService.defineQuestionGroup(questionGroupDetail).getId();
    }

    @Override
    public Integer createActiveQuestionGroup(QuestionGroupDetail questionGroupDetail) throws SystemException {
        questionGroupDetail.setActivityId(addActivityPermission(questionGroupDetail.getTitle(), questionGroupDetail.getId()));
        return questionnaireService.defineQuestionGroup(questionGroupDetail).getId();
    }

    @Override
    public List<QuestionDetail> getAllQuestions() {
        return questionnaireService.getAllQuestions();
    }

    @Override
    public List<QuestionDetail> getAllActiveQuestions() {
        return questionnaireService.getAllActiveQuestions(null);
    }

    @Override
    public List<QuestionDetail> getAllActiveQuestions(List<Integer> excludedQuestions) {
        return questionnaireService.getAllActiveQuestions(excludedQuestions);
    }

    @Override
    public List<QuestionGroupDetail> getAllQuestionGroups() {
        return questionnaireService.getAllQuestionGroups();
    }

    @Override
    public QuestionGroupDetail getQuestionGroupDetail(Integer questionGroupId) throws SystemException {
        if(!checkAccessToQuestionGroup(questionGroupId)) {
            throw new AccessDeniedException("Access denied");
        }
        return questionnaireService.getQuestionGroup(questionGroupId);
    }

    @Override
    public QuestionGroupDetail getQuestionGroupDetailForLoanPrd(Integer questionGroupId) throws SystemException {
        return questionnaireService.getQuestionGroup(questionGroupId);
    }

    @Override
    public QuestionDetail getQuestionDetail(Integer questionId) throws SystemException {
        return questionnaireService.getQuestion(questionId);
    }

    @Override
    public List<EventSourceDto> getAllEventSources() {
        return questionnaireService.getAllEventSources();
    }

    @Override
    public Integer getEventSourceId(String event, String source) {
        return questionnaireService.getEventSourceId(event, source);
    }

    @Override
    public void saveResponses(QuestionGroupDetails questionGroupDetails) {
        questionnaireService.saveResponses(questionGroupDetails);
        if (auditLogService != null) {
            int creatorId = questionGroupDetails.getCreatorId();
            int entityId = questionGroupDetails.getEntityId();
            for (QuestionGroupDetail questionGroupDetail : questionGroupDetails.getDetails()) {
                EventSourceDto eventSourceDto = questionnaireService.getEventSource(questionGroupDetails.getEventSourceId());
                String source = eventSourceDto.getSource();
                String event = eventSourceDto.getEvent();

                QuestionGroupDetail secondLastQuestionGroupDetail = null;
                int max = 0;
                int secondMax = 0;
                List<QuestionGroupInstanceDetail> oldQuestionGroupDetails = questionnaireService.getQuestionGroupInstances(entityId, eventSourceDto, false, false);
                for (QuestionGroupInstanceDetail oldQuestionGroupDetail : oldQuestionGroupDetails) {
                    // find previously entered responses that belong to the same question group as one being saved
                    if (oldQuestionGroupDetail.getQuestionGroupDetail().getId().equals(questionGroupDetail.getId())) {
                        if (oldQuestionGroupDetail.getId() >= max) {
                            secondMax = max;
                            max = oldQuestionGroupDetail.getId();
                        } else if (oldQuestionGroupDetail.getId() > secondMax) {
                            secondMax = oldQuestionGroupDetail.getId();
                        }
                    }
                }
                if (secondMax != 0) {
                    secondLastQuestionGroupDetail = questionnaireService.getQuestionGroupInstance(secondMax).getQuestionGroupDetail();
                }
                auditLogService.addAuditLogRegistry(questionGroupDetail, secondLastQuestionGroupDetail, creatorId, entityId, source, event);
            }
        }
    }

    @Override
    public void validateResponses(List<QuestionGroupDetail> questionGroupDetails) {
        questionnaireService.validateResponses(questionGroupDetails);
    }

    @Override
    public List<QuestionGroupDetail> getQuestionGroups(String event, String source) throws SystemException {
        return questionnaireService.getQuestionGroups(getEventSource(event, source));
    }

    @Override
    public List<QuestionGroupInstanceDetail> getQuestionGroupInstances(Integer entityId, String event, String source) {
        return questionnaireService.getQuestionGroupInstances(entityId, getEventSource(event, source), false, false);
    }

    @Override
    public List<QuestionGroupInstanceDetail> getQuestionGroupInstancesWithUnansweredQuestionGroups(Integer entityId, String event, String source) {
        return filterInActiveQuestions(questionnaireService.getQuestionGroupInstances(entityId, getEventSource(event, source), true, true));
    }

    private List<QuestionGroupInstanceDetail> filterInActiveQuestions(List<QuestionGroupInstanceDetail> instanceDetails) {
        for (QuestionGroupInstanceDetail instanceDetail : instanceDetails) {
            for (SectionDetail sectionDetail : instanceDetail.getQuestionGroupDetail().getSectionDetails()) {
                List<SectionQuestionDetail> sectionQuestionDetails = sectionDetail.getQuestions();
                for (Iterator<SectionQuestionDetail> iterator = sectionQuestionDetails.iterator(); iterator.hasNext();) {
                    SectionQuestionDetail sectionQuestionDetail = iterator.next();
                    if (sectionQuestionDetail.isNotActive()) {
                        iterator.remove();
                    }
                }
            }
        }
        return instanceDetails;
    }

    @Override
    public QuestionGroupInstanceDetail getQuestionGroupInstance(Integer questionGroupInstanceId) {
        return questionnaireService.getQuestionGroupInstance(questionGroupInstanceId);
    }

    @Override
    public Integer createQuestionGroup(QuestionGroupDto questionGroupDto) throws SystemException {
        questionGroupDto.setActivityId(addActivityPermission(questionGroupDto.getTitle(), null));
        return questionnaireService.defineQuestionGroup(questionGroupDto);
    }

    @Override
    public List<String> getAllCountriesForPPI() {
        return questionnaireService.getAllCountriesForPPI();
    }

    @Override
    public void uploadPPIQuestionGroup(String country) {
        questionnaireService.uploadPPIQuestionGroup(country);
    }

    @Override
    public Integer saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto) {
        return questionnaireService.saveQuestionGroupInstance(questionGroupInstanceDto);
    }

    @Override
    public Integer getSectionQuestionId(String sectionName, Integer questionId, Integer questionGroupId) {
        return questionnaireService.getSectionQuestionId(sectionName, questionId, questionGroupId);
    }

    @Override
    public Integer createQuestion(QuestionDto questionDto) {
        return questionnaireService.defineQuestion(questionDto);
    }

    @Override
    public void applyToAllLoanProducts(Integer entityId) {
        if (qgFlowsService != null) {
            qgFlowsService.applyToAllLoanProducts(entityId);
        }
    }

    private EventSourceDto getEventSource(String event, String source) {
        return new EventSourceDto(event, source, String.format("%s.%s", event, source));
    }

    private boolean checkAccessToQuestionGroup(Integer questionGroupId) {
        QuestionGroup questionGroup = this.questionnaireService.getQuestionGroupById(questionGroupId);
        boolean result = false;
        if (questionGroup != null) {
            Short activityID = questionGroup.getActivityId();
            try {
                result = this.rolesPermissionService.hasUserAccessForActivity(activityID);
            } catch (Exception ex) {
                result = false;
            }
        }
        return result;
    }
    
    @SuppressWarnings({"PMD.AvoidUsingShortType", "PMD.AvoidUsingShortType"})
    private Short addActivityPermission(String title, Integer id) {
        short parentActivity = SecurityConstants.QUESTION_MANAGMENT;
        Short oldActivityId;
        int newActivityId;
        try {
            oldActivityId = this.questionnaireService.getQuestionGroupById(id).getActivityId();
        } catch (Exception e) {
            oldActivityId = 0;
        }
        String activityNameHead = "Can edit ";
        try {
            if(null != oldActivityId && !oldActivityId.equals((short)0)) {
                newActivityId = oldActivityId;
                this.rolesPermissionService.updateLookUpValue(newActivityId, activityNameHead, title);
            }
            else {
                newActivityId = this.rolesPermissionService.calculateDynamicActivityId();
                this.rolesPermissionService.createActivityForQuestionGroup(parentActivity, activityNameHead + title);
            }
        } catch (Exception e) {
           throw new MifosRuntimeException(e);
        }
        return new Short((short)newActivityId);
    }
    
}
