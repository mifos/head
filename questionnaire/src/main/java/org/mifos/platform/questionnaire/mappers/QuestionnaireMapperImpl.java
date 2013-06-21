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

package org.mifos.platform.questionnaire.mappers;

import java.util.HashMap;
import org.mifos.platform.questionnaire.domain.AnswerType;
import org.mifos.platform.questionnaire.domain.ChoiceTagEntity;
import org.mifos.platform.questionnaire.domain.EventSourceEntity;
import org.mifos.platform.questionnaire.domain.QuestionChoiceEntity;
import org.mifos.platform.questionnaire.domain.QuestionEntity;
import org.mifos.platform.questionnaire.domain.QuestionGroup;
import org.mifos.platform.questionnaire.domain.QuestionGroupInstance;
import org.mifos.platform.questionnaire.domain.QuestionGroupLink;
import org.mifos.platform.questionnaire.domain.QuestionGroupResponse;
import org.mifos.platform.questionnaire.domain.QuestionGroupState;
import org.mifos.platform.questionnaire.domain.QuestionState;
import org.mifos.platform.questionnaire.domain.Section;
import org.mifos.platform.questionnaire.domain.SectionLink;
import org.mifos.platform.questionnaire.domain.SectionQuestion;
import org.mifos.platform.questionnaire.domain.SectionQuestionLink;
import org.mifos.platform.questionnaire.persistence.EventSourceDao;
import org.mifos.platform.questionnaire.persistence.QuestionDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupDao;
import org.mifos.platform.questionnaire.persistence.QuestionGroupInstanceDao;
import org.mifos.platform.questionnaire.persistence.SectionDao;
import org.mifos.platform.questionnaire.persistence.SectionLinkDao;
import org.mifos.platform.questionnaire.persistence.SectionQuestionDao;
import org.mifos.platform.questionnaire.persistence.SectionQuestionLinkDao;
import org.mifos.platform.questionnaire.service.SelectionDetail;
import org.mifos.platform.questionnaire.service.dtos.ChoiceDto;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetails;
import org.mifos.platform.questionnaire.service.QuestionGroupInstanceDetail;
import org.mifos.platform.questionnaire.service.QuestionLinkDetail;
import org.mifos.platform.questionnaire.service.QuestionType;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionLinkDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.QuestionDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupInstanceDto;
import org.mifos.platform.questionnaire.service.dtos.QuestionGroupResponseDto;
import org.mifos.platform.questionnaire.service.dtos.SectionDto;
import org.mifos.platform.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mifos.platform.util.CollectionUtils.asMap;
import static org.mifos.platform.util.CollectionUtils.isNotEmpty;
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
    
    @Autowired
    private SectionDao sectionDao;

    @Autowired
    private QuestionGroupInstanceDao questionGroupInstanceDao;
    
    @Autowired
    private SectionLinkDao sectionLinkDao;
    
    @Autowired
    private SectionQuestionLinkDao sectionQuestionLinkDao;
    
    public QuestionnaireMapperImpl() {
        this(null, null, null, null,null, null,null,null);
    }

    public QuestionnaireMapperImpl(EventSourceDao eventSourceDao, QuestionDao questionDao, QuestionGroupDao questionGroupDao, 
            SectionQuestionDao sectionQuestionDao, QuestionGroupInstanceDao questionGroupInstanceDao, SectionDao sectionDao, SectionLinkDao sectionLinkDao, SectionQuestionLinkDao sectionQuestionLinkDao) {
        populateAnswerToQuestionTypeMap();
        populateQuestionToAnswerTypeMap();
        this.eventSourceDao = eventSourceDao;
        this.questionDao = questionDao;
        this.questionGroupDao = questionGroupDao;
        this.sectionQuestionDao = sectionQuestionDao;
        this.questionGroupInstanceDao = questionGroupInstanceDao;
        this.sectionDao = sectionDao;
        this.sectionLinkDao = sectionLinkDao;
        this.sectionQuestionLinkDao = sectionQuestionLinkDao;
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
        return mapToQuestionDetail(question, mapToQuestionType(question.getAnswerTypeAsEnum()));
    }

    private List<ChoiceDto> mapToQuestionChoices(List<QuestionChoiceEntity> choices) {
        List<ChoiceDto> questionChoices = new LinkedList<ChoiceDto>();
        for (QuestionChoiceEntity questionChoice : choices) {
            questionChoices.add(mapToChoiceDetail(questionChoice));
        }
        return questionChoices;
    }

    private ChoiceDto mapToChoiceDetail(QuestionChoiceEntity questionChoice) {
        ChoiceDto choiceDto = new ChoiceDto(questionChoice.getChoiceText());
        mapToChoiceTags(choiceDto, questionChoice.getTags());
        return choiceDto;
    }

    private void mapToChoiceTags(ChoiceDto choiceDto, Set<ChoiceTagEntity> choiceTagEntities) {
        if (isNotEmpty(choiceTagEntities)) {
            List<String> choiceTags = new ArrayList<String>();
            for (ChoiceTagEntity tag : choiceTagEntities) {
                choiceTags.add(tag.getTagText());
            }
            choiceDto.setTags(choiceTags);
        }
    }

    @Override
    public QuestionEntity mapToQuestion(QuestionDetail questionDetail) {
        QuestionEntity question = getQuestion(questionDetail);
        question.setQuestionId(questionDetail.getId() > 0 ? questionDetail.getId() : 0);
        question.setNickname(questionDetail.getNickname());
        question.setQuestionText(questionDetail.getText());
        question.setAnswerType(mapToAnswerType(questionDetail.getType()));
        question.setChoices(mapToChoices(questionDetail.getAnswerChoices()));
        question.setQuestionState(QuestionState.getQuestionStateEnum(questionDetail.isActive(), questionDetail.isEditable()));
        mapBoundsForNumericQuestionDetail(questionDetail, question);
        return question;
    }

    private QuestionEntity getQuestion(QuestionDetail questionDetail) {
        return questionDetail.isNewQuestion() ? new QuestionEntity() : questionDao.getDetails(questionDetail.getId());
    }

    private void mapBoundsForNumericQuestionDetail(QuestionDetail questionDetail, QuestionEntity question) {
        if (questionDetail.getType() == QuestionType.NUMERIC) {
            question.setNumericMin(questionDetail.getNumericMin());
            question.setNumericMax(questionDetail.getNumericMax());
        }
    }

    private List<QuestionChoiceEntity> mapToChoices(List<ChoiceDto> choices) {
        List<QuestionChoiceEntity> questionChoices = new LinkedList<QuestionChoiceEntity>();
        if (CollectionUtils.isNotEmpty(choices)) {
            for (ChoiceDto choice : choices) {
                questionChoices.add(mapToChoice(choice));
            }
        }
        return questionChoices;
    }

    private QuestionChoiceEntity mapToChoice(ChoiceDto choice) {
        QuestionChoiceEntity choiceEntity = new QuestionChoiceEntity(choice.getValue());
        choiceEntity.setChoiceOrder(choice.getOrder());
        List<String> tags = choice.getTags();
        if (isNotEmpty(tags)) {
            Set<ChoiceTagEntity> choiceTagEntities = new LinkedHashSet<ChoiceTagEntity>();
            for (String tag : tags) {
                ChoiceTagEntity choiceTagEntity = new ChoiceTagEntity();
                choiceTagEntity.setTagText(tag);
                choiceTagEntities.add(choiceTagEntity);
            }
            choiceEntity.setTags(choiceTagEntities);
        }
        return choiceEntity;
    }

    @Override
    public QuestionGroup mapToQuestionGroup(QuestionGroupDetail questionGroupDetail) {
        QuestionGroup questionGroup = getQuestionGroup(questionGroupDetail);
        questionGroup.setTitle(questionGroupDetail.getTitle());
        questionGroup.setState(QuestionGroupState.getQuestionGroupStateAsEnum(questionGroupDetail.isActive()));
        questionGroup.setDateOfCreation(getCurrentDateTime());
        questionGroup.setSections(mapToSections(questionGroupDetail));
        questionGroup.setEventSources(mapEventSourceDtoToEntity(questionGroupDetail.getEventSources()));
        questionGroup.setEditable(questionGroupDetail.isEditable());
        questionGroup.setActivityId(questionGroupDetail.getActivityId());
        return questionGroup;
    }

    private QuestionGroup getQuestionGroup(QuestionGroupDetail questionGroupDetail) {
        return questionGroupDetail.isNewQuestionGroup() ? new QuestionGroup() : questionGroupDao.getDetails(questionGroupDetail.getId());
    }

    private Set<EventSourceEntity> mapEventSourceDtoToEntity(List<EventSourceDto> eventSourceDtos) {
        Set<EventSourceEntity> eventSources = new HashSet<EventSourceEntity>();
        for (EventSourceDto eventSourceDto : eventSourceDtos) {
            List list = eventSourceDao.retrieveByEventAndSource(eventSourceDto.getEvent(), eventSourceDto.getSource());
            for (Object obj : list) {
                eventSources.add((EventSourceEntity) obj);
            }
        }
        return eventSources;
    }

    private List<Section> mapToSections(QuestionGroupDetail questionGroupDetail) {
        List<Section> sections = new ArrayList<Section>();
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            sections.add(mapToSection(questionGroupDetail, sectionDetail));
        }
        return sections;
    }

    private Section mapToSection(QuestionGroupDetail questionGroupDetail, SectionDetail sectionDetail) {
        Section section = getSection(questionGroupDetail, sectionDetail);
        section.setQuestions(mapToSectionQuestions(sectionDetail.getQuestions(), section));
        if (sectionDetail.getSequenceNumber() != null) {
            section.setSequenceNumber(sectionDetail.getSequenceNumber());
        }
        return section;
    }

    private Section getSection(QuestionGroupDetail questionGroupDetail, SectionDetail sectionDetail) {
        String sectionName = sectionDetail.getName();
        Section section = new Section(sectionName);
        if (!questionGroupDetail.isNewQuestionGroup()) {
            List<Section> sections = questionGroupDao.retrieveSectionByNameAndQuestionGroupId(sectionName, questionGroupDetail.getId());
            if (isNotEmpty(sections)) {
                section = sections.get(0);
            }
        }
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
        QuestionDetail questionDetail = sectionQuestionDetail.getQuestionDetail();
        SectionQuestion sectionQuestion = getSectionQuestion(questionDetail, section);
        sectionQuestion.setRequired(sectionQuestionDetail.isMandatory());
        sectionQuestion.setShowOnPage(sectionQuestionDetail.isShowOnPage());
        sectionQuestion.setSequenceNumber(sectionQuestionDetail.getSequenceNumber());
        if (sectionQuestion.isNewSectionQuestion()) {
            sectionQuestion.setQuestion(mapToQuestion(questionDetail));
            sectionQuestion.setSequenceNumber(seqNum);
            sectionQuestion.setSection(section);
        }
        return sectionQuestion;
    }

    private SectionQuestion getSectionQuestion(QuestionDetail questionDetail, Section section) {
        SectionQuestion sectionQuestion = new SectionQuestion();
        if (!questionDetail.isNewQuestion() && !section.isNewSection()) {
            List<SectionQuestion> sectionQuestions = sectionQuestionDao.retrieveFromQuestionIdSectionId(section.getId(), questionDetail.getId());
            if (isNotEmpty(sectionQuestions)) {
                sectionQuestion = sectionQuestions.get(0);
            }
        }
        return sectionQuestion;
    }

    @Override
    public QuestionGroupDetail mapToQuestionGroupDetail(QuestionGroup questionGroup) {
        List<SectionDetail> sectionDetails = mapToSectionDetails(questionGroup.getSections());
        List<SectionLink> sectionLinks = new ArrayList<SectionLink>();
        List<SectionQuestionLink> sectionQuestionLinks = new ArrayList<SectionQuestionLink>();
        
        for(SectionDetail sectionDetail : sectionDetails){
            for(SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestionDetails()){
                if(!sectionLinkDao.retrieveDependentSectionLinksFromQuestion(sectionQuestionDetail.getId()).isEmpty())
                    sectionLinks.addAll(sectionLinkDao.retrieveDependentSectionLinksFromQuestion(sectionQuestionDetail.getId()));
                if(!sectionQuestionLinkDao.retrieveDependentSectionQuestionLinksFromQuestion(sectionQuestionDetail.getId()).isEmpty())
                    sectionQuestionLinks.addAll(sectionQuestionLinkDao.retrieveDependentSectionQuestionLinksFromQuestion(sectionQuestionDetail.getId()));
            }
        }
        List<EventSourceDto> eventSourceDtos = mapToEventSource(questionGroup.getEventSources());
        return new QuestionGroupDetail(questionGroup.getId(), questionGroup.getTitle(),
                eventSourceDtos, sectionDetails, questionGroup.isEditable(),
                QuestionGroupState.ACTIVE.equals(questionGroup.getState()), questionGroup.isPpi(), mapToQuestionLinkDetails(sectionQuestionLinks), mapToSectionLinkDetails(sectionLinks));
    }

    private List<EventSourceDto> mapToEventSource(Set<EventSourceEntity> eventSources) {
        if (eventSources == null || eventSources.isEmpty()) {
            return null;
        }
        List<EventSourceDto> eventSourceDtos = new ArrayList<EventSourceDto>();
        for (EventSourceEntity eventSourceEntity : eventSources) {
            eventSourceDtos.add(new EventSourceDto(eventSourceEntity.getEvent().getName(), eventSourceEntity.getSource().getEntityType(), eventSourceEntity.getDescription()));
        }

        return eventSourceDtos;
    }

    private List<SectionDetail> mapToSectionDetails(List<Section> sections) {
        List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
        for (Section section : sections) {
            sectionDetails.add(mapToSectionDetail(section));
        }
        return sectionDetails;
    }

    private SectionDetail mapToSectionDetail(Section section) {
        SectionDetail sectionDetail = new SectionDetail();
        sectionDetail.setId(section.getId());
        sectionDetail.setName(section.getName());
        if (section.getSequenceNumber() != null) {
            sectionDetail.setSequenceNumber(section.getSequenceNumber());
        }
        for (SectionQuestion sectionQuestion : section.getQuestions()) {
            QuestionEntity question = sectionQuestion.getQuestion();
            QuestionType type = mapToQuestionType(question.getAnswerTypeAsEnum());
            boolean required = sectionQuestion.isRequired();
            QuestionDetail questionDetail = mapToQuestionDetail(question, type);
            sectionDetail.addQuestion(mapToSectionQuestionDetail(sectionQuestion, required, sectionQuestion.isShowOnPage(), questionDetail));
        }
        return sectionDetail;
    }

    private SectionQuestionDetail mapToSectionQuestionDetail(SectionQuestion sectionQuestion, boolean required, boolean showOnPage, QuestionDetail questionDetail) {
        if (sectionQuestion.getSequenceNumber() != null) {
            return new SectionQuestionDetail(sectionQuestion.getId(), questionDetail, required, showOnPage, sectionQuestion.getSequenceNumber());
        }
        return new SectionQuestionDetail(sectionQuestion.getId(), questionDetail, required);
    }

    private QuestionDetail mapToQuestionDetail(QuestionEntity question, QuestionType type) {
        List<ChoiceDto> answerChoices = mapToQuestionChoices(question.getChoices());
        QuestionDetail questionDetail = new QuestionDetail(question.getQuestionId(), question.getQuestionText(), type, question.isActive(), question.isEditable());
        questionDetail.setNickname(question.getNickname());
        questionDetail.setAnswerChoices(answerChoices);
        mapBoundsForNumericQuestion(question, questionDetail);
        return questionDetail;
    }

    private void mapBoundsForNumericQuestion(QuestionEntity question, QuestionDetail questionDetail) {
        if (question.getAnswerTypeAsEnum() == AnswerType.NUMBER) {
            questionDetail.setNumericMin(question.getNumericMin());
            questionDetail.setNumericMax(question.getNumericMax());
        }
    }

    @Override
    public List<QuestionGroupDetail> mapToQuestionGroupDetails(List<QuestionGroup> questionGroups) {
        List<QuestionGroupDetail> questionGroupDetails = new ArrayList<QuestionGroupDetail>();
        for (QuestionGroup questionGroup : questionGroups) {
            questionGroupDetails.add(mapToQuestionGroupDetail(questionGroup));
        }
        return questionGroupDetails;
    }

    @Override
    public List<EventSourceDto> mapToEventSources(List<EventSourceEntity> eventSourceEntities) {
        List<EventSourceDto> eventSourceDtos = new ArrayList<EventSourceDto>();
        for (EventSourceEntity eventSourceEntity : eventSourceEntities) {
            eventSourceDtos.add(mapEventSource(eventSourceEntity));
        }
        return eventSourceDtos;
    }

    @Override
    public List<QuestionGroupInstance> mapToQuestionGroupInstances(QuestionGroupDetails questionGroupDetails) {
        List<QuestionGroupInstance> questionGroupInstances = new ArrayList<QuestionGroupInstance>();
        for (QuestionGroupDetail questionGroupDetail : questionGroupDetails.getDetails()) {
            questionGroupInstances.add(mapToQuestionGroupInstance(questionGroupDetails.getCreatorId(),
                    questionGroupDetails.getEntityId(), questionGroupDetails.getEventSourceId(), questionGroupDetail));
        }
        return questionGroupInstances;
    }

    @Override
    public List<QuestionGroupInstanceDetail> mapToQuestionGroupInstanceDetails(List<QuestionGroupInstance> questionGroupInstances) {
        List<QuestionGroupInstanceDetail> questionGroupInstanceDetails = new ArrayList<QuestionGroupInstanceDetail>();
        for (QuestionGroupInstance questionGroupInstance : questionGroupInstances) {
            questionGroupInstanceDetails.add(mapToQuestionGroupInstanceDetail(questionGroupInstance));
        }
        return questionGroupInstanceDetails;
    }

    @Override
    public QuestionGroupInstanceDetail mapToQuestionGroupInstanceDetail(QuestionGroupInstance questionGroupInstance) {
        QuestionGroupDetail questionGroupDetail = mapToQuestionGroupDetail(questionGroupInstance.getQuestionGroup());
        QuestionGroupInstanceDetail questionGroupInstanceDetail = new QuestionGroupInstanceDetail();
        questionGroupInstanceDetail.setId(questionGroupInstance.getId());
        questionGroupInstanceDetail.setDateCompleted(questionGroupInstance.getDateConducted());
        questionGroupInstanceDetail.setQuestionGroupDetail(questionGroupDetail);
        mapQuestionResponses(questionGroupInstance.getQuestionGroupResponses(), questionGroupInstanceDetail.getQuestionGroupDetail());
        return questionGroupInstanceDetail;
    }

    @Override
    public void mapToQuestionResponse(SectionQuestionDetail sectionQuestionDetail, List<QuestionGroupResponse> questionGroupResponses) {
        if (sectionQuestionDetail.isMultiSelectQuestion()) {
            setMultiChoiceResponses(questionGroupResponses, sectionQuestionDetail);
        } else {
            setResponse(questionGroupResponses, sectionQuestionDetail);
        }
    }

    @Override
    public QuestionGroupInstanceDetail mapToEmptyQuestionGroupInstanceDetail(QuestionGroup questionGroup) {
        QuestionGroupInstanceDetail questionGroupInstanceDetail = new QuestionGroupInstanceDetail();
        questionGroupInstanceDetail.setId(0);
        questionGroupInstanceDetail.setQuestionGroupDetail(mapToQuestionGroupDetail(questionGroup));
        return questionGroupInstanceDetail;
    }

    @Override
    public QuestionGroup mapToQuestionGroup(QuestionGroupDto questionGroupDto) {
        QuestionGroup questionGroup = new QuestionGroup();
        questionGroup.setEditable(questionGroupDto.isEditable());
        questionGroup.setDateOfCreation(getCurrentDateTime());
        questionGroup.setPpi(questionGroupDto.isPpi());
        questionGroup.setEventSources(mapEventSourceDtoToEntity(questionGroupDto.getEventSourceDtos()));
        questionGroup.setTitle(questionGroupDto.getTitle());
        questionGroup.setState(questionGroupDto.isActive() ? QuestionGroupState.ACTIVE : QuestionGroupState.INACTIVE);
        questionGroup.setSections(mapToSectionsFromDtos(questionGroupDto.getSections()));
        questionGroup.setActivityId(questionGroupDto.getActivityId());
        return questionGroup;
    }

    @Override
    public QuestionGroupInstance mapToQuestionGroupInstance(QuestionGroupInstanceDto questionGroupInstanceDto) {
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setCompletedStatus(questionGroupInstanceDto.isCompleted());
        questionGroupInstance.setCreatorId(questionGroupInstanceDto.getCreatorId());
        questionGroupInstance.setEventSourceId(questionGroupInstanceDto.getEventSourceId());
        questionGroupInstance.setDateConducted(questionGroupInstanceDto.getDateConducted());
        questionGroupInstance.setEntityId(questionGroupInstanceDto.getEntityId());
        questionGroupInstance.setVersionNum(questionGroupInstanceDto.getVersion());
        questionGroupInstance.setQuestionGroup(questionGroupDao.getDetails(questionGroupInstanceDto.getQuestionGroupId()));
        questionGroupInstance.setQuestionGroupResponses(mapToQuestionGroupResponses(questionGroupInstance, questionGroupInstanceDto.getQuestionGroupResponseDtos()));
        return questionGroupInstance;
    }

    private List<QuestionGroupResponse> mapToQuestionGroupResponses(QuestionGroupInstance questionGroupInstance, List<QuestionGroupResponseDto> questionGroupResponseDtos) {
        List<QuestionGroupResponse> questionGroupResponses = new ArrayList<QuestionGroupResponse>();
        if (isNotEmpty(questionGroupResponseDtos)) {
            for (QuestionGroupResponseDto questionGroupResponseDto : questionGroupResponseDtos) {
                questionGroupResponses.add(mapToQuestionGroupResponse(questionGroupInstance, questionGroupResponseDto));
            }
        }
        return questionGroupResponses;
    }

    private Map<Integer, SectionQuestion> sectionQuestionMap = new HashMap<Integer, SectionQuestion>();

    private QuestionGroupResponse mapToQuestionGroupResponse(QuestionGroupInstance questionGroupInstance, QuestionGroupResponseDto questionGroupResponseDto) {
        QuestionGroupResponse questionGroupResponse = new QuestionGroupResponse();
        questionGroupResponse.setResponse(questionGroupResponseDto.getResponse());
        questionGroupResponse.setQuestionGroupInstance(questionGroupInstance);

        SectionQuestion sq;
        if (sectionQuestionMap.containsKey(questionGroupResponseDto.getSectionQuestionId())) {
            sq = sectionQuestionMap.get(questionGroupResponseDto.getSectionQuestionId());
        } else {
            sq = sectionQuestionDao.getDetails(questionGroupResponseDto.getSectionQuestionId());
            sectionQuestionMap.put(questionGroupResponseDto.getSectionQuestionId(), sq);
        }
        questionGroupResponse.setSectionQuestion(sq);
        return questionGroupResponse;
    }

    private List<Section> mapToSectionsFromDtos(List<SectionDto> sectionDtos) {
        List<Section> sections = new ArrayList<Section>();
        for (SectionDto sectionDto : sectionDtos) {
            sections.add(mapToSection(sectionDto));
        }
        return sections;
    }

    private Section mapToSection(SectionDto sectionDto) {
        Section section = new Section();
        section.setName(sectionDto.getName());
        section.setSequenceNumber(sectionDto.getOrder());
        section.setQuestions(mapToSectionQuestionsFromDtos(sectionDto.getQuestions(), section));
        return section;
    }

    private List<SectionQuestion> mapToSectionQuestionsFromDtos(List<QuestionDto> questions, Section section) {
        List<SectionQuestion> sectionQuestions = new ArrayList<SectionQuestion>();
        for (QuestionDto questionDto : questions) {
            sectionQuestions.add(mapToSectionQuestion(questionDto, section));
        }
        return sectionQuestions;
    }

    private SectionQuestion mapToSectionQuestion(QuestionDto questionDto, Section section) {
        SectionQuestion sectionQuestion = new SectionQuestion();
        sectionQuestion.setSection(section);
        sectionQuestion.setSequenceNumber(questionDto.getOrder());
        sectionQuestion.setRequired(questionDto.isMandatory());
        sectionQuestion.setShowOnPage(questionDto.isShowOnPage());
        sectionQuestion.setQuestion(mapToQuestion(questionDto));
        return sectionQuestion;
    }

    @Override
    public QuestionEntity mapToQuestion(QuestionDto questionDto) {
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setQuestionText(questionDto.getText());
        questionEntity.setNickname(questionDto.getNickname());
        questionEntity.setAnswerType(mapToAnswerType(questionDto.getType()));
        questionEntity.setNumericMin(questionDto.getMinValue());
        questionEntity.setNumericMax(questionDto.getMaxValue());
        questionEntity.setQuestionState(questionDto.isActive() ?
                questionDto.isEditable() ? QuestionState.ACTIVE : QuestionState.ACTIVE_NOT_EDITABLE :
                questionDto.isEditable() ? QuestionState.INACTIVE : QuestionState.INACTIVE_NOT_EDITABLE);
        questionEntity.setChoices(mapToChoices(questionDto.getChoices()));
        return questionEntity;
    }

    private Date getCurrentDateTime() {
        return Calendar.getInstance().getTime();
    }

    private void mapQuestionResponses(List<QuestionGroupResponse> questionGroupResponses, QuestionGroupDetail questionGroupDetail) {
        if (isNotEmpty(questionGroupResponses)) {
            for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
                for (SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestions()) {
                    mapToQuestionResponse(sectionQuestionDetail, questionGroupResponses);
                }
            }
        }
    }

    private void setResponse(List<QuestionGroupResponse> questionGroupResponses, SectionQuestionDetail sectionQuestionDetail) {
        for (QuestionGroupResponse questionGroupResponse : questionGroupResponses) {
            if (questionGroupResponse.getSectionQuestion().getId() == sectionQuestionDetail.getId()) {
                sectionQuestionDetail.setValue(questionGroupResponse.getResponse());
            }
        }
    }

    private void setMultiChoiceResponses(List<QuestionGroupResponse> questionGroupResponses, SectionQuestionDetail sectionQuestionDetail) {
        List<SelectionDetail> answers = new ArrayList<SelectionDetail>();
        for (QuestionGroupResponse questionGroupResponse : questionGroupResponses) {
            if (questionGroupResponse.getSectionQuestion().getId() == sectionQuestionDetail.getId()) {
                answers.add(mapToSelectionDetail(questionGroupResponse));
            }
        }
        sectionQuestionDetail.setSelections(answers);
    }

    private SelectionDetail mapToSelectionDetail(QuestionGroupResponse questionGroupResponse) {
        SelectionDetail selectionDetail = new SelectionDetail();
        selectionDetail.setSelectedChoice(questionGroupResponse.getResponse());
        selectionDetail.setSelectedTag(questionGroupResponse.getTag());
        return selectionDetail;
    }

    private QuestionGroupInstance mapToQuestionGroupInstance(int creatorId, int entityId, int eventSourceId, QuestionGroupDetail questionGroupDetail) {
        QuestionGroupInstance questionGroupInstance = new QuestionGroupInstance();
        questionGroupInstance.setDateConducted(getCurrentDateTime());
        questionGroupInstance.setCompletedStatus(1);
        Integer questionGroupId = questionGroupDetail.getId();
        questionGroupInstance.setVersionNum(
                nextQuestionGroupInstanceVersion(entityId, questionGroupId));
        questionGroupInstance.setCreatorId(creatorId);
        questionGroupInstance.setEventSourceId(eventSourceId);
        questionGroupInstance.setEntityId(entityId);
        questionGroupInstance.setQuestionGroup(questionGroupDao.getDetails(questionGroupId));
        questionGroupInstance.setQuestionGroupResponses(mapToQuestionGroupResponses(questionGroupDetail, questionGroupInstance));
        return questionGroupInstance;
    }

    private int nextQuestionGroupInstanceVersion(int entityId, Integer questionGroupId) {
        int nextVersion = 0;
        List questionGroupInstances = questionGroupInstanceDao.retrieveLatestQuestionGroupInstanceByQuestionGroupAndEntity(entityId, questionGroupId);
        if (isNotEmpty(questionGroupInstances)) {
            nextVersion = ((QuestionGroupInstance) questionGroupInstances.get(0)).getVersionNum() + 1;
        }
        return nextVersion;
    }

    private List<QuestionGroupResponse> mapToQuestionGroupResponses(QuestionGroupDetail questionGroupDetail, QuestionGroupInstance questionGroupInstance) {
        List<QuestionGroupResponse> questionGroupResponses = new LinkedList<QuestionGroupResponse>();
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            for (SectionQuestionDetail sectionQuestionDetail : sectionDetail.getQuestions()) {
                SectionQuestion sectionQuestion = sectionQuestionDao.getDetails(sectionQuestionDetail.getId());
                if (sectionQuestionDetail.hasAnswer()) {
                    mapToQuestionGroupResponse(questionGroupInstance, questionGroupResponses, sectionQuestionDetail, sectionQuestion);
                }
            }
        }
        return questionGroupResponses;
    }

    private void mapToQuestionGroupResponse(QuestionGroupInstance questionGroupInstance, List<QuestionGroupResponse> questionGroupResponses, SectionQuestionDetail sectionQuestionDetail, SectionQuestion sectionQuestion) {
        if (sectionQuestionDetail.isMultiSelectQuestion()) {
            for (SelectionDetail selectionDetail : sectionQuestionDetail.getSelections()) {
                questionGroupResponses.add(mapToQuestionGroupResponse(questionGroupInstance, sectionQuestion, selectionDetail));
            }
        } else {
            questionGroupResponses.add(mapToQuestionGroupResponse(questionGroupInstance, sectionQuestion, sectionQuestionDetail.getValue()));
        }
    }

    private QuestionGroupResponse mapToQuestionGroupResponse(QuestionGroupInstance questionGroupInstance, SectionQuestion sectionQuestion, SelectionDetail selectionDetail) {
        QuestionGroupResponse questionGroupResponse = mapToQuestionGroupResponse(questionGroupInstance, sectionQuestion, selectionDetail.getSelectedChoice());
        questionGroupResponse.setTag(selectionDetail.getSelectedTag());
        return questionGroupResponse;
    }

    private QuestionGroupResponse mapToQuestionGroupResponse(QuestionGroupInstance questionGroupInstance, SectionQuestion sectionQuestion, String value) {
        QuestionGroupResponse questionGroupResponse = new QuestionGroupResponse();
        questionGroupResponse.setSectionQuestion(sectionQuestion);
        questionGroupResponse.setResponse(value);
        questionGroupResponse.setQuestionGroupInstance(questionGroupInstance);
        return questionGroupResponse;
    }

    private EventSourceDto mapEventSource(EventSourceEntity eventSourceEntity) {
        return new EventSourceDto(eventSourceEntity.getEvent().getName(), eventSourceEntity.getSource().getEntityType(),
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
                makeEntry(AnswerType.SMARTSELECT, QuestionType.SMART_SELECT),
                makeEntry(AnswerType.MULTISELECT, QuestionType.MULTI_SELECT),
                makeEntry(AnswerType.SMARTSINGLESELECT, QuestionType.SMART_SINGLE_SELECT));
    }

    private void populateQuestionToAnswerTypeMap() {
        questionToAnswerType = asMap(makeEntry(QuestionType.INVALID, AnswerType.INVALID),
                makeEntry(QuestionType.FREETEXT, AnswerType.FREETEXT),
                makeEntry(QuestionType.DATE, AnswerType.DATE),
                makeEntry(QuestionType.NUMERIC, AnswerType.NUMBER),
                makeEntry(QuestionType.SINGLE_SELECT, AnswerType.SINGLESELECT),
                makeEntry(QuestionType.SMART_SELECT, AnswerType.SMARTSELECT),
                makeEntry(QuestionType.MULTI_SELECT, AnswerType.MULTISELECT),
                makeEntry(QuestionType.SMART_SINGLE_SELECT, AnswerType.SMARTSINGLESELECT));
    }
    public SectionQuestion getSectionQuestionById (Integer sectionQuestionId){
        return sectionQuestionDao.retrieveFromSectionQuestionId(sectionQuestionId).get(0);
    }
    
    public Section getSectionById (Integer sectionId){
        return sectionDao.retrieveFromSectionId(sectionId).get(0);
    }
    
    public QuestionGroupLink mapToQuestionGroupLink(QuestionLinkDetail questionLinkDetail, SectionLinkDetail sectionLinkDetail){
        QuestionGroupLink questionGroupLink = new QuestionGroupLink();
        if(questionLinkDetail != null){
            questionGroupLink.setValue(questionLinkDetail.getValue());
            questionGroupLink.setAdditionalValue(questionLinkDetail.getAdditionalValue());
            questionGroupLink.setSourceSectionQuestion(getSectionQuestionById(questionLinkDetail.getSourceQuestion().getId()));
            questionGroupLink.setConditionTypeId(questionLinkDetail.getLinkType());
            questionGroupLink.setId(questionLinkDetail.getQuestionGroupLinkId());
        } else {
            questionGroupLink.setValue(sectionLinkDetail.getValue());
            questionGroupLink.setAdditionalValue(sectionLinkDetail.getAdditionalValue());
            questionGroupLink.setSourceSectionQuestion(getSectionQuestionById(sectionLinkDetail.getSourceQuestion().getId()));
            questionGroupLink.setConditionTypeId(sectionLinkDetail.getLinkType());
            questionGroupLink.setId(sectionLinkDetail.getQuestionGroupLinkId());
        }
        return questionGroupLink;
    }
    
    public SectionQuestionLink mapToQuestionLink(QuestionLinkDetail questionLinkDetail, QuestionGroupLink questionGroupLink) {
        SectionQuestionLink sectionQuestionLink = new SectionQuestionLink();
        sectionQuestionLink.setQuestionGroupLink(questionGroupLink);
        sectionQuestionLink.setAffectedSectionQuestion(getSectionQuestionById(questionLinkDetail.getAffectedQuestion().getId()));
        sectionQuestionLink.setId(questionLinkDetail.getLinkId());
        return sectionQuestionLink;
    }
    
    public SectionLink mapToSectionLink(SectionLinkDetail sectionLinkDetail, QuestionGroupLink questionGroupLink) {
        SectionLink sectionLink = new SectionLink();
        sectionLink.setAffectedSection(getSectionById(sectionLinkDetail.getAffectedSection().getId()));
        sectionLink.setQuestionGroupLink(questionGroupLink);
        sectionLink.setId(sectionLinkDetail.getLinkId());
        return sectionLink;
    }
    
    public List<QuestionLinkDetail> mapToQuestionLinkDetails(List<SectionQuestionLink> sectionQuestionLinks) {
        List<QuestionLinkDetail> questionLinkDetails = new ArrayList<QuestionLinkDetail>();
        for(SectionQuestionLink sectionQuestionLink : sectionQuestionLinks){
            QuestionLinkDetail questionLinkDetail = new QuestionLinkDetail();
            questionLinkDetail.setValue(sectionQuestionLink.getQuestionGroupLink().getValue());
            questionLinkDetail.setAdditionalValue(sectionQuestionLink.getQuestionGroupLink().getAdditionalValue());
            questionLinkDetail.setLinkType(sectionQuestionLink.getQuestionGroupLink().getConditionTypeId());
            questionLinkDetail.setProperLinkTypeDisplay(sectionQuestionLink.getQuestionGroupLink().getConditionType());
            questionLinkDetail.setLinkId(sectionQuestionLink.getId());
            questionLinkDetail.setQuestionGroupLinkId(sectionQuestionLink.getQuestionGroupLink().getId());
            questionLinkDetail.setState(true);
            SectionQuestion sectionQuestion = sectionQuestionLink.getQuestionGroupLink().getSourceSectionQuestion();
            questionLinkDetail.setSourceQuestion(mapToSectionQuestionDetail(sectionQuestion,
                    sectionQuestion.isRequired(), sectionQuestion.isShowOnPage(), 
                    (mapToQuestionDetail(sectionQuestion.getQuestion(), mapToQuestionType(sectionQuestion.getQuestion().getAnswerTypeAsEnum())))));
            sectionQuestion = sectionQuestionLink.getAffectedSectionQuestion();
            questionLinkDetail.setAffectedQuestion(mapToSectionQuestionDetail(sectionQuestion,
                    sectionQuestion.isRequired(), sectionQuestion.isShowOnPage(), 
                    (mapToQuestionDetail(sectionQuestion.getQuestion(), mapToQuestionType(sectionQuestion.getQuestion().getAnswerTypeAsEnum())))));
            questionLinkDetails.add(questionLinkDetail);
        }
        return questionLinkDetails;
    }
    
    public List<SectionLinkDetail> mapToSectionLinkDetails(List<SectionLink> sectionLinks) {
        List<SectionLinkDetail> sectionLinkDetails = new ArrayList<SectionLinkDetail>();
        for(SectionLink sectionLink : sectionLinks){
            SectionLinkDetail sectionLinkDetail = new SectionLinkDetail();
            sectionLinkDetail.setValue(sectionLink.getQuestionGroupLink().getValue());
            sectionLinkDetail.setAdditionalValue(sectionLink.getQuestionGroupLink().getAdditionalValue());
            sectionLinkDetail.setLinkType(sectionLink.getQuestionGroupLink().getConditionTypeId());
            sectionLinkDetail.setProperLinkTypeDisplay(sectionLink.getQuestionGroupLink().getConditionType());
            sectionLinkDetail.setLinkId(sectionLink.getId());
            sectionLinkDetail.setQuestionGroupLinkId(sectionLink.getQuestionGroupLink().getId());
            sectionLinkDetail.setState(true);
            SectionQuestion sectionQuestion = sectionLink.getQuestionGroupLink().getSourceSectionQuestion();
            sectionLinkDetail.setSourceQuestion(mapToSectionQuestionDetail(sectionQuestion,
                    sectionQuestion.isRequired(), sectionQuestion.isShowOnPage(), 
                    (mapToQuestionDetail(sectionQuestion.getQuestion(), mapToQuestionType(sectionQuestion.getQuestion().getAnswerTypeAsEnum())))));
            sectionLinkDetail.setAffectedSection(mapToSectionDetail(sectionLink.getAffectedSection()));
            sectionLinkDetails.add(sectionLinkDetail);
        }
        return sectionLinkDetails;
    }
}