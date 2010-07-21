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

package org.mifos.platform.questionnaire.mappers;

import org.mifos.platform.questionnaire.domain.AnswerType;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionChoiceEntity;
import org.mifos.platform.questionnaire.domain.QuestionEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupInstance;
import org.mifos.platform.questionnaire.domain.QuestionGroupResponse;
import org.mifos.platform.questionnaire.domain.QuestionGroupState;
import org.mifos.platform.questionnaire.domain.QuestionState;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.domain.SectionQuestion;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.persistence.SectionQuestionDao;
import org.mifos.platform.questionnaire.service.EventSource;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mifos.platform.util.CollectionUtils.asMap;
import static org.mifos.platform.util.MapEntry.makeEntry;

@SuppressWarnings({"PMD", "UnusedDeclaration"})
public class QuestionnaireMapperImpl implements QuestionnaireMapper {
    private Map<AnswerType, QuestionType> answerToQuestionType;
    private Map<QuestionType, AnswerType> questionToAnswerType;

    @Autowired
    private EventSourceDao eventSourceDao;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private QuestionGroupDao questionGroupDao;

    @Autowired
    private SectionQuestionDao sectionQuestionDao;

    //@SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireMapperImpl() {
        this(null, null, null, null);
    }

    public QuestionnaireMapperImpl(EventSourceDao eventSourceDao, QuestionDao questionDao, QuestionGroupDao questionGroupDao, SectionQuestionDao sectionQuestionDao) {
        populateAnswerToQuestionTypeMap();
        populateQuestionToAnswerTypeMap();
        this.eventSourceDao = eventSourceDao;
        this.questionDao = questionDao;
        this.questionGroupDao = questionGroupDao;
        this.sectionQuestionDao = sectionQuestionDao;
    }

    @Override
    public List<QuestionDetail> mapToQuestionDetails(List<QuestionEntity> questions) {
        List<QuestionDetail> questionDetails = new ArrayList<QuestionDetail>();
        for (QuestionEntity question : questions) {
            questionDetails.add(mapToQuestionDetail(question));
        }
        return questionDetails;
    }

    @Override
    public QuestionDetail mapToQuestionDetail(QuestionEntity question) {
        return new QuestionDetail(question.getQuestionId(),
                question.getQuestionText(),
                question.getShortName(),
                mapToQuestionType(question.getAnswerTypeAsEnum()), mapToAnswerChoices(question.getChoices()));
    }

    private List<String> mapToAnswerChoices(List<QuestionChoiceEntity> choices) {
        List<String> questionChoices = new LinkedList<String>();
        for (QuestionChoiceEntity questionChoice : choices) {
            questionChoices.add(questionChoice.getChoiceText());
        }
        return questionChoices;
    }


    @Override
    public QuestionEntity mapToQuestion(QuestionDetail questionDetail) {
        QuestionEntity question = new QuestionEntity();
        question.setShortName(questionDetail.getTitle());
        question.setQuestionText(questionDetail.getTitle());
        QuestionType type = questionDetail.getType();
        question.setAnswerType(mapToAnswerType(type));
        question.setQuestionState(QuestionState.ACTIVE);
        question.setChoices(mapToChoices(questionDetail.getAnswerChoices()));
        return question;
    }

    private List<QuestionChoiceEntity> mapToChoices(List<String> choices) {
        List<QuestionChoiceEntity> questionChoices = new LinkedList<QuestionChoiceEntity>();
        for (String choice : choices) {
            questionChoices.add(new QuestionChoiceEntity(choice));
        }
        return questionChoices;
    }

    @Override
    public QuestionGroup mapToQuestionGroup(QuestionGroupDetail questionGroupDetail) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTitle(questionGroupDetail.getTitle());
        questionGroup.setState(QuestionGroupState.ACTIVE);
        questionGroup.setDateOfCreation(Calendar.getInstance().getTime());
        questionGroup.setSections(mapToSections(questionGroupDetail.getSectionDetails()));
        questionGroup.setEventSources(mapToEventSources(questionGroupDetail));
        questionGroup.setEditable(questionGroupDetail.isEditable());
        return questionGroup;
    }

    private Set<EventSourceEntity> mapToEventSources(QuestionGroupDetail questionGroupDetail) {
        Set<EventSourceEntity> eventSources = new HashSet<EventSourceEntity>();
        EventSource eventSource = questionGroupDetail.getEventSource();
        List list = eventSourceDao.retrieveByEventAndSource(eventSource.getEvent(), eventSource.getSource());
        for (Object obj : list) {
            eventSources.add((EventSourceEntity) obj);
        }
        return eventSources;
    }

    private List<Section> mapToSections(List<SectionDetail> sectionDetails) {
        List<Section> sections = new ArrayList<Section>();
        for (SectionDetail sectionDetail : sectionDetails) {
            sections.add(mapToSection(sectionDetail));
        }
        return sections;
    }

    private Section mapToSection(SectionDetail sectionDetail) {
        Section section = new Section(sectionDetail.getName());
        section.setQuestions(mapToSectionQuestions(sectionDetail.getQuestions(), section));
        return section;
    }

    private List<SectionQuestion> mapToSectionQuestions(List<SectionQuestionDetail> sectionQuestionDetails, Section section) {
        List<SectionQuestion> sectionQuestions = new ArrayList<SectionQuestion>();
        for (int i = 0, sectionQuestionDetailsSize = sectionQuestionDetails.size(); i < sectionQuestionDetailsSize; i++) {
            SectionQuestionDetail sectionQuestionDetail = sectionQuestionDetails.get(i);
            sectionQuestions.add(mapToSectionQuestion(sectionQuestionDetail, i, section));
        }
        return sectionQuestions;
    }

    private SectionQuestion mapToSectionQuestion(SectionQuestionDetail sectionQuestionDetail, int seqNum, Section section) {
        SectionQuestion sectionQuestion = new SectionQuestion();
        sectionQuestion.setRequired(sectionQuestionDetail.isMandatory());
        sectionQuestion.setQuestion(questionDao.getDetails(sectionQuestionDetail.getQuestionId()));
        sectionQuestion.setSequenceNumber(seqNum);
        sectionQuestion.setSection(section);
        return sectionQuestion;
    }

    @Override
    public QuestionGroupDetail mapToQuestionGroupDetail(QuestionGroup questionGroup) {
        List<SectionDetail> sectionDetails = mapToSectionDefinitions(questionGroup.getSections());
        EventSource eventSource = mapToEventSource(questionGroup.getEventSources());
        return new QuestionGroupDetail(questionGroup.getId(), questionGroup.getTitle(), eventSource, sectionDetails, questionGroup.isEditable());
    }

    private EventSource mapToEventSource(Set<EventSourceEntity> eventSources) {
        if (eventSources == null || eventSources.isEmpty()) {
            return null;
        }
        EventSourceEntity eventSourceEntity = eventSources.toArray(new EventSourceEntity[eventSources.size()])[0];
        return new EventSource(eventSourceEntity.getEvent().getName(), eventSourceEntity.getSource().getEntityType(), eventSourceEntity.getDescription());
    }

    private List<SectionDetail> mapToSectionDefinitions(List<Section> sections) {
        List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
        for (Section section : sections) {
            sectionDetails.add(mapToSectionDefinition(section));
        }
        return sectionDetails;
    }

    private SectionDetail mapToSectionDefinition(Section section) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setName(section.getName());
        for (SectionQuestion sectionQuestion : section.getQuestions()) {
            QuestionEntity question = sectionQuestion.getQuestion();
            QuestionType type = mapToQuestionType(question.getAnswerTypeAsEnum());
            boolean required = sectionQuestion.isRequired();
            QuestionDetail questionDetail = new QuestionDetail(question.getQuestionId(), question.getQuestionText(), question.getShortName(), type);
            sectionDetail.addQuestion(new SectionQuestionDetail(sectionQuestion.getId(), questionDetail, required));
        }
        return sectionDetail;
    }

    @Override
    public List<QuestionGroupDetail> mapToQuestionGroupDetails(List<QuestionGroup> questionGroups) {
        List<QuestionGroupDetail> questionGroupDetails = new ArrayList<QuestionGroupDetail>();
        for (QuestionGroup questionGroup : questionGroups) {
            questionGroupDetails.add(new QuestionGroupDetail(questionGroup.getId(), questionGroup.getTitle(), mapToSectionDefinitions(questionGroup.getSections())));
        }
        return questionGroupDetails;
    }

    @Override
    public List<EventSource> mapToEventSources(List<EventSourceEntity> eventSourceEntities) {
        List<EventSource> eventSources = new ArrayList<EventSource>();
        for (EventSourceEntity eventSourceEntity : eventSourceEntities) {
            eventSources.add(mapEventSource(eventSourceEntity));
        }
        return eventSources;
    }

    @Override
    public List<QuestionGroupInstance> mapToQuestionGroupInstances(QuestionGroupDetails questionGroupDetails) {
        List<QuestionGroupInstance> questionGroupInstances = new ArrayList<QuestionGroupInstance>();
        for (QuestionGroupDetail questionGroupDetail : questionGroupDetails.getDetails()) {
            questionGroupInstances.add(mapToQuestionGroupInstance(questionGroupDetails.getCreatorId(),
                    questionGroupDetails.getEntityId(), questionGroupDetail));
        }
        return questionGroupInstances;
    }

    private QuestionGroupInstance mapToQuestionGroupInstance(int creatorId, int entityId, QuestionGroupDetail questionGroupDetail) {
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setDateConducted(Calendar.getInstance().getTime());
        questionGroupInstance.setCompletedStatus(1);
        questionGroupInstance.setVersionNum(1);
        questionGroupInstance.setCreatorId(creatorId);
        questionGroupInstance.setEntityId(entityId);
        questionGroupInstance.setQuestionGroup(questionGroupDao.getDetails(questionGroupDetail.getId()));
        questionGroupInstance.setQuestionGroupResponses(mapToQuestionGroupResponses(questionGroupDetail, questionGroupInstance));
        return questionGroupInstance;
    }

    private List<QuestionGroupResponse> mapToQuestionGroupResponses(QuestionGroupDetail questionGroupDetail, QuestionGroupInstance questionGroupInstance) {
        List<QuestionGroupResponse> questionGroupResponses = new LinkedList<QuestionGroupResponse>();
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            for (SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestions()) {
                QuestionGroupResponse questionGroupResponse = new QuestionGroupResponse();
                questionGroupResponse.setSectionQuestion(sectionQuestionDao.getDetails(sectionQuestionDetail.getId()));
                questionGroupResponse.setResponse(sectionQuestionDetail.getValue());
                questionGroupResponse.setQuestionGroupInstance(questionGroupInstance);
                questionGroupResponses.add(questionGroupResponse);
            }
        }
        return questionGroupResponses;
    }

    private EventSource mapEventSource(EventSourceEntity eventSourceEntity) {
        return new EventSource(eventSourceEntity.getEvent().getName(), eventSourceEntity.getSource().getEntityType(),
                eventSourceEntity.getDescription());
    }

    private QuestionType mapToQuestionType(AnswerType answerType) {
        return answerToQuestionType.get(answerType);
    }

    private AnswerType mapToAnswerType(QuestionType questionType) {
        return questionToAnswerType.get(questionType);
    }

    private void populateAnswerToQuestionTypeMap() {
        answerToQuestionType = CollectionUtils.asMap(makeEntry(AnswerType.INVALID, QuestionType.INVALID),
                makeEntry(AnswerType.FREETEXT, QuestionType.FREETEXT),
                makeEntry(AnswerType.DATE, QuestionType.DATE),
                makeEntry(AnswerType.NUMBER, QuestionType.NUMERIC),
                makeEntry(AnswerType.SINGLESELECT, QuestionType.SINGLE_SELECT),
                makeEntry(AnswerType.CHOICE, QuestionType.SINGLE_SELECT),
                makeEntry(AnswerType.MULTISELECT, QuestionType.MULTI_SELECT));
    }

    private void populateQuestionToAnswerTypeMap() {
        questionToAnswerType = asMap(makeEntry(QuestionType.INVALID, AnswerType.INVALID),
                makeEntry(QuestionType.FREETEXT, AnswerType.FREETEXT),
                makeEntry(QuestionType.DATE, AnswerType.DATE),
                makeEntry(QuestionType.NUMERIC, AnswerType.NUMBER),
                makeEntry(QuestionType.SINGLE_SELECT, AnswerType.SINGLESELECT),
                makeEntry(QuestionType.MULTI_SELECT, AnswerType.MULTISELECT));
    }

}