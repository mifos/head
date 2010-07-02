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

import org.mifos.customers.surveys.business.Question;
import org.mifos.customers.surveys.helpers.AnswerType;
import org.mifos.customers.surveys.helpers.QuestionState;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupState;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.mifos.framework.util.CollectionUtils.asMap;
import static org.mifos.framework.util.MapEntry.makeEntry;

public class QuestionnaireMapperImpl implements QuestionnaireMapper {
    private Map<AnswerType, QuestionType> answerToQuestionType;
    private Map<QuestionType, AnswerType> questionToAnswerType;

    @Autowired
    private EventSourceDao eventSourceDao;

    public QuestionnaireMapperImpl() {
        populateAnswerToQuestionTypeMap();
        populateQuestionToAnswerTypeMap();
    }

    public QuestionnaireMapperImpl(EventSourceDao eventSourceDao) {
        this();
        this.eventSourceDao = eventSourceDao;
    }

    @Override
    public List<QuestionDetail> mapToQuestionDetails(List<Question> questions) {
        List<QuestionDetail> questionDetails = new ArrayList<QuestionDetail>();
        for (Question question : questions) {
            questionDetails.add(mapToQuestionDetail(question));
        }
        return questionDetails;
    }

    @Override
    public QuestionDetail mapToQuestionDetail(Question question) {
        return new QuestionDetail(question.getQuestionId(),
                question.getQuestionText(),
                question.getShortName(),
                mapToQuestionType(question.getAnswerTypeAsEnum()));
    }

    @Override
    public Question mapToQuestion(QuestionDefinition questionDefinition) {
        Question question = new Question();
        question.setShortName(questionDefinition.getTitle());
        question.setQuestionText(questionDefinition.getTitle());
        question.setAnswerType(mapToAnswerType(questionDefinition.getType()));
        question.setQuestionState(QuestionState.ACTIVE);
        return question;
    }

    @Override
    public QuestionGroup mapToQuestionGroup(QuestionGroupDefinition questionGroupDefinition) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setTitle(questionGroupDefinition.getTitle());
        questionGroup.setState(QuestionGroupState.ACTIVE);
        questionGroup.setDateOfCreation(Calendar.getInstance().getTime());
        questionGroup.setSections(mapToSections(questionGroupDefinition.getSectionDefinitions()));
        questionGroup.setEventSources(mapToEventSources(questionGroupDefinition));
        return questionGroup;
    }

    private Set<EventSourceEntity> mapToEventSources(QuestionGroupDefinition questionGroupDefinition) {
        Set<EventSourceEntity> eventSources = new HashSet<EventSourceEntity>();
        EventSource eventSource = questionGroupDefinition.getEventSource();
        List list = eventSourceDao.retrieveByEventAndSource(eventSource.getEvent(), eventSource.getSource());
        for (Object obj : list) eventSources.add((EventSourceEntity) obj);
        return eventSources;
    }

    private List<Section> mapToSections(List<SectionDefinition> sectionDefinitions) {
        List<Section> sections = new ArrayList<Section>();
        for (SectionDefinition sectionDefinition : sectionDefinitions) {
            sections.add(mapToSection(sectionDefinition));
        }
        return sections;
    }

    private Section mapToSection(SectionDefinition sectionDefinition) {
        return new Section(sectionDefinition.getName());
    }

    @Override
    public QuestionGroupDetail mapToQuestionGroupDetail(QuestionGroup questionGroup) {
        List<SectionDefinition> sectionDefinitions = mapToSectionDefinitions(questionGroup.getSections());
        EventSource eventSource = mapToEventSource(questionGroup.getEventSources());
        return new QuestionGroupDetail(questionGroup.getId(), questionGroup.getTitle(), eventSource, sectionDefinitions);
    }

    private EventSource mapToEventSource(Set<EventSourceEntity> eventSources) {
        if (eventSources == null || eventSources.isEmpty()) return null;
        EventSourceEntity eventSourceEntity = eventSources.toArray(new EventSourceEntity[eventSources.size()])[0];
        return new EventSource(eventSourceEntity.getEvent().getName(), eventSourceEntity.getSource().getEntityType(), eventSourceEntity.getDescription());
    }

    private List<SectionDefinition> mapToSectionDefinitions(List<Section> sections) {
        ArrayList<SectionDefinition> sectionDefinitions = new ArrayList<SectionDefinition>();
        for(Section section: sections){
            sectionDefinitions.add(mapToSectionDefinition(section));
        }
        return sectionDefinitions;
    }

    private SectionDefinition mapToSectionDefinition(Section section) {
        SectionDefinition sectionDefinition = new SectionDefinition();
        sectionDefinition.setName(section.getName());
        return sectionDefinition;
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
        answerToQuestionType = asMap(makeEntry(AnswerType.INVALID, QuestionType.INVALID),
                makeEntry(AnswerType.FREETEXT, QuestionType.FREETEXT),
                makeEntry(AnswerType.DATE, QuestionType.DATE),
                makeEntry(AnswerType.NUMBER, QuestionType.NUMERIC));
    }

    private void populateQuestionToAnswerTypeMap() {
        questionToAnswerType = asMap(makeEntry(QuestionType.INVALID, AnswerType.INVALID),
                makeEntry(QuestionType.FREETEXT, AnswerType.FREETEXT),
                makeEntry(QuestionType.DATE, AnswerType.DATE),
                makeEntry(QuestionType.NUMERIC, AnswerType.NUMBER));
    }

}