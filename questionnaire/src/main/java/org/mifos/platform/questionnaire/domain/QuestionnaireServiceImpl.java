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

package org.mifos.platform.questionnaire.domain;

import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.domain.ppi.PPISurveyLocator;
import org.mifos.platform.questionnaire.mappers.QuestionnaireMapper;
import org.mifos.platform.questionnaire.parsers.QuestionGroupDefinitionParser;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupInstanceDao;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.validators.QuestionnaireValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.mifos.platform.questionnaire.QuestionnaireConstants.PPI_SURVEY_FILE_EXT;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.PPI_SURVEY_FILE_PREFIX;
import static org.mifos.platform.util.CollectionUtils.isNotEmpty;

public class QuestionnaireServiceImpl implements QuestionnaireService {

    @Autowired
    private QuestionnaireValidator questionnaireValidator;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionGroupDao questionGroupDao;

    @Autowired
    private EventSourceDao eventSourceDao;

    @Autowired
    private QuestionGroupInstanceDao questionGroupInstanceDao;

    @Autowired
    private QuestionnaireMapper questionnaireMapper;

    @Autowired
    private PPISurveyLocator ppiSurveyLocator;

    @Autowired
    private QuestionGroupDefinitionParser questionGroupDefinitionParser;

    @SuppressWarnings({"UnusedDeclaration"})
    private QuestionnaireServiceImpl() {
    }

    public QuestionnaireServiceImpl(QuestionnaireValidator questionnaireValidator, QuestionDao questionDao,
                                    QuestionnaireMapper questionnaireMapper, QuestionGroupDao questionGroupDao,
                                    EventSourceDao eventSourceDao, QuestionGroupInstanceDao questionGroupInstanceDao,
                                    PPISurveyLocator ppiSurveyLocator, QuestionGroupDefinitionParser questionGroupDefinitionParser) {
        this.questionnaireValidator = questionnaireValidator;
        this.questionDao = questionDao;
        this.questionnaireMapper = questionnaireMapper;
        this.questionGroupDao = questionGroupDao;
        this.eventSourceDao = eventSourceDao;
        this.questionGroupInstanceDao = questionGroupInstanceDao;
        this.ppiSurveyLocator = ppiSurveyLocator;
        this.questionGroupDefinitionParser = questionGroupDefinitionParser;
    }

    @Override
    public QuestionDetail defineQuestion(QuestionDetail questionDetail) throws SystemException {
        questionnaireValidator.validateForDefineQuestion(questionDetail);
        QuestionEntity question = questionnaireMapper.mapToQuestion(questionDetail);
        persistQuestion(question);
        return questionnaireMapper.mapToQuestionDetail(question);
    }

    @Override
    public List<QuestionDetail> getAllQuestions() {
        List<QuestionEntity> questions = questionDao.retrieveAll();
        return questionnaireMapper.mapToQuestionDetails(questions);
    }

    @Override
    public List<QuestionDetail> getAllActiveQuestions(List<Integer> questionIdsToExclude) {
        List<QuestionEntity> questions;
        if (isNotEmpty(questionIdsToExclude)) {
            questions = questionDao.retrieveByStateExcluding(questionIdsToExclude, QuestionState.ACTIVE.getValue());
        } else {
            questions = questionDao.retrieveByState(QuestionState.ACTIVE.getValue());
        }
        return questionnaireMapper.mapToQuestionDetails(questions);
    }

    @Override
    public QuestionGroupDetail defineQuestionGroup(QuestionGroupDetail questionGroupDetail) throws SystemException {
        questionnaireValidator.validateForDefineQuestionGroup(questionGroupDetail);
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(questionGroupDetail);
        questionGroupDao.create(questionGroup);
        return questionnaireMapper.mapToQuestionGroupDetail(questionGroup);
    }

    @Override
    public List<QuestionGroupDetail> getAllQuestionGroups() {
        List<QuestionGroup> questionGroups = questionGroupDao.getDetailsAll();
        return questionnaireMapper.mapToQuestionGroupDetails(questionGroups);
    }

    @Override
    public boolean isDuplicateQuestionTitle(String title) {
        List result = questionDao.retrieveCountOfQuestionsWithTitle(title);
        return (Long) result.get(0) > 0;
    }

    @Override
    public QuestionGroupDetail getQuestionGroup(int questionGroupId) throws SystemException {
        QuestionGroup questionGroup = questionGroupDao.getDetails(questionGroupId);
        if (questionGroup == null) {
            throw new SystemException(QuestionnaireConstants.QUESTION_GROUP_NOT_FOUND);
        }
        return questionnaireMapper.mapToQuestionGroupDetail(questionGroup);
    }

    @Override
    public QuestionDetail getQuestion(int questionId) throws SystemException {
        QuestionEntity question = questionDao.getDetails(questionId);
        if (question == null) {
            throw new SystemException(QuestionnaireConstants.QUESTION_NOT_FOUND);
        }
        return questionnaireMapper.mapToQuestionDetail(question);
    }

    @Override
    public List<EventSourceDto> getAllEventSources() {
        return questionnaireMapper.mapToEventSources(eventSourceDao.getDetailsAll());
    }

    @Override
    public List<QuestionGroupDetail> getQuestionGroups(EventSourceDto eventSourceDto) throws SystemException {
        questionnaireValidator.validateForEventSource(eventSourceDto);
        List<QuestionGroup> questionGroups = questionGroupDao.retrieveQuestionGroupsByEventSource(eventSourceDto.getEvent(), eventSourceDto.getSource());
        List<QuestionGroupDetail> questionGroupDetails = questionnaireMapper.mapToQuestionGroupDetails(questionGroups);
        removeInactiveSectionsAndQuestions(questionGroupDetails);
        return questionGroupDetails;
    }

    @Override
    public void saveResponses(QuestionGroupDetails questionGroupDetails) {
        questionnaireValidator.validateForQuestionGroupResponses(questionGroupDetails.getDetails());
        questionGroupInstanceDao.saveOrUpdateAll(questionnaireMapper.mapToQuestionGroupInstances(questionGroupDetails));
    }

    @Override
    public void validateResponses(List<QuestionGroupDetail> questionGroupDetails) {
        questionnaireValidator.validateForQuestionGroupResponses(questionGroupDetails);
    }

    @Override
    public List<QuestionGroupInstanceDetail> getQuestionGroupInstances(Integer entityId, EventSourceDto eventSourceDto, Boolean includeUnansweredQuestionGroups, boolean fetchLastVersion) {
        questionnaireValidator.validateForEventSource(eventSourceDto);
        Integer eventSourceId = getEventSourceEntity(eventSourceDto).getId();
        List<QuestionGroupInstance> questionGroupInstances = getQuestionGroupInstanceEntities(entityId, eventSourceId, fetchLastVersion);
        List<QuestionGroupInstanceDetail> questionGroupInstanceDetails = questionnaireMapper.mapToQuestionGroupInstanceDetails(questionGroupInstances);
        if (includeUnansweredQuestionGroups) {
            List<QuestionGroup> questionGroups = questionGroupDao.retrieveQuestionGroupsByEventSource(eventSourceDto.getEvent(), eventSourceDto.getSource());
            questionGroupInstanceDetails = mergeUnansweredQuestionGroups(questionGroupInstanceDetails, questionGroups);
        }
        return questionGroupInstanceDetails;
    }

    private List<QuestionGroupInstance> getQuestionGroupInstanceEntities(Integer entityId, Integer eventSourceId, boolean fetchLastVersion) {
        List<QuestionGroupInstance> questionGroupInstances;
        if(fetchLastVersion){
            questionGroupInstances = questionGroupInstanceDao.retrieveLatestQuestionGroupInstancesByEntityIdAndEventSourceId(entityId, eventSourceId);
        }else{
            questionGroupInstances = questionGroupInstanceDao.retrieveQuestionGroupInstancesByEntityIdAndEventSourceId(entityId, eventSourceId);
        }
        return questionGroupInstances;
    }

    private List<QuestionGroupInstanceDetail> mergeUnansweredQuestionGroups(List<QuestionGroupInstanceDetail> instancesWithResponses, List<QuestionGroup> questionGroups) {
        List<QuestionGroupInstanceDetail> allInstances = new ArrayList<QuestionGroupInstanceDetail>(instancesWithResponses);
        for (QuestionGroup questionGroup : questionGroups) {
            if (!hasResponse(questionGroup, instancesWithResponses)) {
                allInstances.add(questionnaireMapper.mapToEmptyQuestionGroupInstanceDetail(questionGroup));
            }
        }
        return allInstances;
    }

    private boolean hasResponse(QuestionGroup questionGroup, List<QuestionGroupInstanceDetail> questionGroupInstances) {
        boolean result = false;
        for (QuestionGroupInstanceDetail questionGroupInstance : questionGroupInstances) {
            if (questionGroupInstance.getQuestionGroupDetail().getId() == questionGroup.getId()) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Override
    public QuestionGroupInstanceDetail getQuestionGroupInstance(int questionGroupInstanceId) {
        QuestionGroupInstance questionGroupInstance = questionGroupInstanceDao.getDetails(questionGroupInstanceId);
        return questionnaireMapper.mapToQuestionGroupInstanceDetail(questionGroupInstance);
    }

    @Override
    public Integer defineQuestionGroup(QuestionGroupDto questionGroupDto) {
        questionnaireValidator.validateForDefineQuestionGroup(questionGroupDto);
        QuestionGroup questionGroup = questionnaireMapper.mapToQuestionGroup(questionGroupDto);
        return persistQuestionGroup(questionGroup);
    }

    private Integer persistQuestionGroup(QuestionGroup questionGroup) {
        List<SectionQuestion> sectionQuestions = questionGroup.getAllSectionQuestions();
        for (SectionQuestion sectionQuestion : sectionQuestions) {
            List<QuestionEntity> questionEntities = questionDao.retrieveByName(sectionQuestion.getQuestionTitle());
            if (isNotEmpty(questionEntities)) {
                QuestionEntity questionEntity = questionEntities.get(0);
                questionEntity.setQuestionState(QuestionState.ACTIVE);
                sectionQuestion.setQuestion(questionEntity);
            }
        }
        return questionGroupDao.create(questionGroup);
    }

    @Override
    public List<String> getAllCountriesForPPI() {
        List<String> ppiSurveyFiles = ppiSurveyLocator.getAllPPISurveyFiles();
        List<String> countries = new ArrayList<String>();
        for (String ppiSurveyFile : ppiSurveyFiles) {
            String country = ppiSurveyFile.substring(PPI_SURVEY_FILE_PREFIX.length(), ppiSurveyFile.indexOf(PPI_SURVEY_FILE_EXT));
            countries.add(country);
        }
        return countries;
    }

    @Override
    public Integer uploadPPIQuestionGroup(String country) {
        String ppiXmlForCountry = ppiSurveyLocator.getPPIUploadFileForCountry(country);
        QuestionGroupDto questionGroupDto = questionGroupDefinitionParser.parse(ppiXmlForCountry);
        return defineQuestionGroup(questionGroupDto);
    }

    @Override
    public Integer saveQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto) {
        QuestionGroupInstance questionGroupInstance = questionnaireMapper.mapToQuestionGroupInstance(questionGroupInstanceDto);
        return questionGroupInstanceDao.create(questionGroupInstance);
    }

    private EventSourceEntity getEventSourceEntity(EventSourceDto eventSourceDto) {
        return eventSourceDao.retrieveByEventAndSource(eventSourceDto.getEvent(), eventSourceDto.getSource()).get(0);
    }

    private void persistQuestion(QuestionEntity question) throws SystemException {
        try {
            questionDao.saveOrUpdate(question);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new SystemException(QuestionnaireConstants.QUESTION_TITLE_DUPLICATE, e);
        }
    }

    private void removeInactiveSectionsAndQuestions(List<QuestionGroupDetail> questionGroupDetails) {
        for (QuestionGroupDetail questionGroupDetail : questionGroupDetails) {
            removeInactiveSectionsAndQuestions(questionGroupDetail);
        }
    }

    private void removeInactiveSectionsAndQuestions(QuestionGroupDetail questionGroupDetail) {
        for (Iterator<SectionDetail> sectionDetailIterator = questionGroupDetail.getSectionDetails().iterator(); sectionDetailIterator.hasNext();) {
            SectionDetail sectionDetail = sectionDetailIterator.next();
            if (sectionDetail.hasNoActiveQuestions()) {
                sectionDetailIterator.remove();
                continue;
            }
            removeInactiveQuestions(sectionDetail);
        }
    }

    private void removeInactiveQuestions(SectionDetail sectionDetail) {
        for (Iterator<SectionQuestionDetail> sectionQuestionDetailIterator = sectionDetail.getQuestions().iterator(); sectionQuestionDetailIterator.hasNext();) {
            if (sectionQuestionDetailIterator.next().isInactive()) {
                sectionQuestionDetailIterator.remove();
            }
        }
    }

}
