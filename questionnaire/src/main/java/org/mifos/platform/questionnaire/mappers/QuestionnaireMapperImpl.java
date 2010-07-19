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

import org.mifos.platform.questionnaire.domain.*;  //NOPMD
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.service.*; //NOPMD
import org.mifos.platform.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

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

    //@SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireMapperImpl() {
        this(null, null);
    }

    public QuestionnaireMapperImpl(EventSourceDao eventSourceDao, QuestionDao questionDao) {
        populateAnswerToQuestionTypeMap();
        populateQuestionToAnswerTypeMap();
        this.eventSourceDao = eventSourceDao;
        this.questionDao = questionDao;
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


    //@SuppressWarnings({"PMD.AvoidInstantiatingObjectsInLoops"})
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
        return new QuestionGroupDetail(questionGroup.getId(), questionGroup.getTitle(), eventSource, sectionDetails);
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
            sectionDetail.addQuestion(new SectionQuestionDetail(questionDetail, required));
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