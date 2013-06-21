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

package org.mifos.platform.questionnaire.ui.model;

import static java.lang.String.format;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.trim;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DEFAULT_APPLIES_TO_OPTION;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.service.QuestionDetail;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionLinkDetail;
import org.mifos.platform.questionnaire.service.SectionDetail;
import org.mifos.platform.questionnaire.service.SectionLinkDetail;
import org.mifos.platform.questionnaire.service.SectionQuestionDetail;
import org.mifos.platform.questionnaire.service.dtos.EventSourceDto;
import org.mifos.platform.validation.ScreenObject;
@SuppressWarnings("PMD")
public class QuestionGroupForm extends ScreenObject {
    private static final long serialVersionUID = -7545625058942409636L;

    private QuestionGroupDetail questionGroupDetail;
    private SectionDetailForm currentSection = new SectionDetailForm();
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private List<String> selectedQuestionIds = new ArrayList<String>();
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_BAD_FIELD")
    private final List<SectionQuestionDetail> questionPool = new ArrayList<SectionQuestionDetail>();
    @javax.validation.Valid
    private Question currentQuestion = new Question(new QuestionDetail());
    private boolean addQuestionFlag;
    private List<SectionDetailForm> sections;
    private int initialCountOfSections;
    private List<String> sectionsToAdd = new ArrayList<String>();
    private List<Integer> questionsToAdd = new ArrayList<Integer>();
    private boolean applyToAllLoanProducts;

    private List<QuestionLinkDetail> questionLinks = new ArrayList<QuestionLinkDetail>();
    private List<SectionLinkDetail> sectionLinks = new ArrayList<SectionLinkDetail>();

	public List<QuestionLinkDetail> getQuestionLinks() {
		return questionLinks;
	}

	public void setQuestionLinks(List<QuestionLinkDetail> questionLinks) {
		this.questionLinks = questionLinks;
	}

	public List<SectionLinkDetail> getSectionLinks() {
		return sectionLinks;
	}

	public void setSectionLinks(List<SectionLinkDetail> sectionLinks) {
		this.sectionLinks = sectionLinks;
	}

	public QuestionGroupForm() {
        this(new QuestionGroupDetail());
    }

    public QuestionGroupForm(QuestionGroupDetail questionGroupDetail) {
        this.questionGroupDetail = questionGroupDetail;
        this.sections = initSections();
    }

    public QuestionGroupDetail getQuestionGroupDetail() {
        return questionGroupDetail;
    }

    public String getTitle() {
        return questionGroupDetail.getTitle();
    }

    public void setTitle(String title) {
        this.questionGroupDetail.setTitle(trim(title));
    }

    public List<String> getSectionsToAdd() {
        return this.sectionsToAdd;
    }

    public void setSectionsToAdd(List<String> sectionsToAdd) {
        this.sectionsToAdd = sectionsToAdd;
    }

    public List<Integer> getQuestionsToAdd() {
        return this.questionsToAdd;
    }

    public void setQuestionsToAdd(List<Integer> questionsToAdd) {
        this.questionsToAdd = questionsToAdd;
    }

    public List<String> getEventSourceIds() {
        List<String> eventSources = new ArrayList<String>();
        List<EventSourceDto> eventSourcesDtos = this.questionGroupDetail.getEventSources();
        if (eventSourcesDtos == null) {
            return eventSources;
        }
        for (EventSourceDto eventSourceDto : eventSourcesDtos) {
            if (eventSourceDto == null || isEmpty(eventSourceDto.getEvent()) || isEmpty(eventSourceDto.getSource())) {
                continue;
            }
            eventSources.add(format("%s.%s", eventSourceDto.getEvent(), eventSourceDto.getSource()));
        }
        return eventSources;
    }

    public String getId() {
        return questionGroupDetail.getId().toString();
    }

    public void setEventSourceIds(List<String> eventSourceIds) {
        List<EventSourceDto> eventSourceDtos = new ArrayList<EventSourceDto>();
        if (eventSourceIds != null) {
            for (String eventSourceId : eventSourceIds) {
                if (StringUtils.isNotEmpty(eventSourceId) && !StringUtils.equals(DEFAULT_APPLIES_TO_OPTION, eventSourceId)) {
                    String[] parts = eventSourceId.split("\\.");
                    eventSourceDtos.add(new EventSourceDto(parts[0], parts[1], eventSourceId));
                }
            }
        }
        this.questionGroupDetail.setEventSources(eventSourceDtos);
    }

    public List<EventSourceDto> getEventSources() {
        return questionGroupDetail.getEventSources();
    }

    public void setEventSources(List<EventSourceDto> eventSourceDtos) {
        questionGroupDetail.setEventSources(eventSourceDtos);
    }

    public List<SectionDetailForm> getSections() {
        return this.sections;
    }

    public void setSections(List<SectionDetailForm> sections) {
        List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
        this.sections = new ArrayList<SectionDetailForm>();
        for (SectionDetailForm sectionDetailForm : sections) {
            this.sections.add(sectionDetailForm);
            sectionDetails.add(sectionDetailForm.getSectionDetail());
        }
        questionGroupDetail.setSectionDetails(sectionDetails);
    }

    public void addCurrentSection() {
        currentSection.trimName();
        String sectionName = getSectionName();
        addCurrentSectionToSections();
        if (addQuestionFlag) {
            addNewQuestion();
        } else {
            addSelectedQuestionsToCurrentSection();
        }
        currentSection = new SectionDetailForm();
        currentSection.setName(sectionName);
        selectedQuestionIds = new ArrayList<String>();
    }

    private void addSelectedQuestionsToCurrentSection() {
        List<SectionQuestionDetail> addedQuestions = new ArrayList<SectionQuestionDetail>();
        for (SectionQuestionDetail questionDetail : questionPool) {
            if (selectedQuestionIds.contains(String.valueOf(questionDetail.getQuestionId()))) {
                questionDetail.setSequenceNumber(currentSection.getSectionQuestionDetails().size());
                currentSection.addSectionQuestion(questionDetail);
                addedQuestions.add(questionDetail);
                questionsToAdd.add(questionDetail.getQuestionId());
            }
        }
        questionPool.removeAll(addedQuestions);
    }

    private void addNewQuestion() {
        currentQuestion.setChoices();
        SectionQuestionDetail sectionQuestionDetail = new SectionQuestionDetail(currentQuestion.getQuestionDetail(), false);
        currentSection.addSectionQuestion(sectionQuestionDetail);
        Integer newQuestionId = getNewQuestionId();
        sectionQuestionDetail.getQuestionDetail().setId(newQuestionId);
        currentQuestion = new Question(new QuestionDetail());
        questionsToAdd.add(newQuestionId);
        updateSequenceNumbers();
    }
    
    private Integer getNewQuestionId() {
        Integer minId = 0;
        List<SectionQuestionDetail> sectionQuestionDetails = currentSection.getSectionQuestionDetails();
        for (SectionQuestionDetail sectionQuestionDetail : sectionQuestionDetails) {
            if (sectionQuestionDetail.getQuestionId() < minId) {
                minId = sectionQuestionDetail.getQuestionId();
            }
        }
        return minId - 1;
    }
    
    private void updateSequenceNumbers() {
        List<SectionQuestionDetailForm> questions = currentSection.getSectionQuestions();
        int sequenceNumber = 0;
        for (SectionQuestionDetailForm sectionQuestionDetailForm : questions) {
            sectionQuestionDetailForm.setSequenceNumber(sequenceNumber++);
        }
    }

    public boolean isDuplicateText(String questionTitle) {
        boolean result = false;
        if(StringUtils.isNotEmpty(questionTitle)){
            for (SectionQuestionDetail sectionQuestionDetail : getAllQuestionsInAllSections()) {
                if(StringUtils.equalsIgnoreCase(questionTitle, sectionQuestionDetail.getText())){
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    private List<SectionQuestionDetail> getAllQuestionsInAllSections() {
        List<SectionQuestionDetail> sectionQuestionDetails = new ArrayList<SectionQuestionDetail>();
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            sectionQuestionDetails.addAll(sectionDetail.getQuestions());
        }
        return sectionQuestionDetails;
    }

    private void addCurrentSectionToSections() {
        for (SectionDetailForm section : sections) {
            if (StringUtils.equalsIgnoreCase(section.getName(), currentSection.getName())) {
                currentSection = section;
                return;
            }
        }
        List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
        for (SectionDetailForm sectionDetailForm : sections) {
            SectionDetail sectionDetail = sectionDetailForm.getSectionDetail();
            sectionDetail.setSequenceNumber(sectionDetail.getSequenceNumber() + 1);
            sectionDetails.add(sectionDetail);
        }
        currentSection.getSectionDetail().setSequenceNumber(0);
        sectionDetails.add(0, currentSection.getSectionDetail());
        sections.add(0, currentSection);
        questionGroupDetail.setSectionDetails(sectionDetails);
        sectionsToAdd.add(currentSection.getName());
    }

    public String getSectionName() {
        if (StringUtils.isEmpty(currentSection.getName())) {
            currentSection.setName(QuestionnaireConstants.DEFAULT_SECTION_NAME);
        }
        return currentSection.getName();
    }

    public void setSectionName(String sectionName) {
        currentSection.setName(sectionName);
    }

    public void removeSection(String sectionName) {
        SectionDetail sectionToDelete = null;
        for (Iterator<SectionDetailForm> iterator = sections.iterator(); iterator.hasNext();) {
            SectionDetailForm section = iterator.next();
            if (StringUtils.equalsIgnoreCase(sectionName, section.getName())) {
                sectionToDelete = section.getSectionDetail();
                iterator.remove();
                break;
            }
        }

        if (sectionToDelete != null) {
            List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
            int sequence = 0;
            for (SectionDetailForm sectionDetailForm : sections) {
                SectionDetail sectionDetail = sectionDetailForm.getSectionDetail();
                sectionDetail.setSequenceNumber(sequence++);
                sectionDetails.add(sectionDetail);
            }
            markQuestionsOptionalAndReturnToPool(sectionToDelete);
            questionGroupDetail.setSectionDetails(sectionDetails);
            for (int i = 0; i < sectionsToAdd.size(); i++) {
                String name = sectionsToAdd.get(i);
                if (name.equals(sectionName)) {
                    sectionsToAdd.remove(i);
                    break;
                }
            }
            
            for(Iterator<SectionLinkDetail> iterator = sectionLinks.iterator(); iterator.hasNext();){
                SectionLinkDetail sectionLinkDetail = iterator.next();
                if(sectionLinkDetail.getAffectedSection().getName().equals(sectionToDelete.getName()))
                    iterator.remove();
                for(SectionQuestionDetail sectionQuestionDetail : sectionToDelete.getQuestionDetails()){
                    if(sectionLinkDetail.getSourceQuestion().getQuestionDetail().getId().equals(sectionQuestionDetail.getQuestionDetail().getId()))
                        iterator.remove();
                }
                
            }
            for(SectionQuestionDetail sectionQuestionDetail : sectionToDelete.getQuestionDetails()){
                for(Iterator<QuestionLinkDetail> iterator = questionLinks.iterator(); iterator.hasNext();){
                    QuestionLinkDetail questionLinkDetail = iterator.next();
                    if(questionLinkDetail.getSourceQuestion().getQuestionDetail().getId().equals(sectionQuestionDetail.getQuestionId()))
                        iterator.remove();
                    if(questionLinkDetail.getAffectedQuestion().getQuestionDetail().getId().equals(sectionQuestionDetail.getQuestionId()))
                        iterator.remove();
                }
            }
        }
    }

    private void markQuestionsOptionalAndReturnToPool(SectionDetail sectionDetail) {
        List<SectionQuestionDetail> sectionQuestionDetails = sectionDetail.getQuestions();
        for(SectionQuestionDetail sectionQuestionDetail: sectionQuestionDetails){
            sectionQuestionDetail.setMandatory(false);
            questionPool.add(sectionQuestionDetail);
        }
    }

    public List<SectionQuestionDetail> getQuestionPool() {
        return questionPool;
    }

    public void setQuestionPool(List<SectionQuestionDetail> questionPool) {
        this.questionPool.clear();
        this.questionPool.addAll(questionPool);
    }

    public List<String> getSelectedQuestionIds() {
        return selectedQuestionIds;
    }

    public void setSelectedQuestionIds(List<String> selectedQuestionIds) {
        this.selectedQuestionIds = selectedQuestionIds;
    }

    public void removeQuestion(String sectionName, String questionId) {
        for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
            if (StringUtils.equalsIgnoreCase(sectionName, sectionDetail.getName())) {
                removeQuestionFromSection(questionId, sectionDetail);
                for (int i = 0; i < questionsToAdd.size(); i++) {
                    int name = questionsToAdd.get(i);
                    if (name == Integer.parseInt(questionId)) {
                        questionsToAdd.remove(i);
                        break;
                    }
                }
                if (sectionHasNoQuestions(sectionDetail)) {
                    removeSection(sectionName);
                }
                break;
            }
        }
        
        for(Iterator<SectionLinkDetail> iterator = sectionLinks.iterator(); iterator.hasNext();){
            SectionLinkDetail sectionLinkDetail = iterator.next();
            if(sectionLinkDetail.getSourceQuestion().getQuestionDetail().getId().equals(Integer.valueOf(questionId)))
            iterator.remove();
        }
        
        for(Iterator<QuestionLinkDetail> iterator = questionLinks.iterator(); iterator.hasNext();){
            QuestionLinkDetail questionLinkDetail = iterator.next();
            if(questionLinkDetail.getSourceQuestion().getQuestionDetail().getId().equals(Integer.valueOf(questionId)))
                iterator.remove();
            if(questionLinkDetail.getAffectedQuestion().getQuestionDetail().getId().equals(Integer.valueOf(questionId)))
                iterator.remove();
        }
    }

    public void moveQuestionUp(String sectionName, String questionId) {
        for (SectionDetailForm section : sections) {
            if (StringUtils.equalsIgnoreCase(sectionName, section.getName())) {
                List<SectionQuestionDetail> questions = section.getSectionQuestionDetails();
                for (SectionQuestionDetail question : questions) {
                    if (Integer.parseInt(questionId) == question.getQuestionId()) {
                        int actualSeqNumber = question.getSequenceNumber();
                        if (actualSeqNumber > 0) {
                            SectionQuestionDetail questionToSwap = questions.get(actualSeqNumber - 1);
                            question.setSequenceNumber(questionToSwap.getSequenceNumber());
                            questionToSwap.setSequenceNumber(actualSeqNumber);
                            questions.set(actualSeqNumber, questionToSwap);
                            questions.set(actualSeqNumber - 1, question);
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    public void moveQuestionDown(String sectionName, String questionId) {
        for (SectionDetailForm section : sections) {
            if (StringUtils.equalsIgnoreCase(sectionName, section.getName())) {
                List<SectionQuestionDetail> questions = section.getSectionQuestionDetails();
                for (SectionQuestionDetail question : questions) {
                    if (Integer.parseInt(questionId) == question.getQuestionId()) {
                        int actualSeqNumber = question.getSequenceNumber();
                        if (actualSeqNumber < questions.size() - 1) {
                            SectionQuestionDetail questionToSwap = questions.get(actualSeqNumber + 1);
                            question.setSequenceNumber(questionToSwap.getSequenceNumber());
                            questionToSwap.setSequenceNumber(actualSeqNumber);
                            questions.set(actualSeqNumber, questionToSwap);
                            questions.set(actualSeqNumber + 1, question);
                        }
                        break;
                    }
                }
                break;
            }
        }
    }

    public void moveSectionUp(String sectionName) {
        for (SectionDetailForm sectionForm : sections) {
            SectionDetail section = sectionForm.getSectionDetail();
            if (StringUtils.equalsIgnoreCase(sectionName, section.getName())) {
                int actualSeqNumber = section.getSequenceNumber();
                if (actualSeqNumber > 0) {
                    SectionDetailForm sectionToSwap = sections.remove(actualSeqNumber - 1);
                    section.setSequenceNumber(sectionToSwap.getSectionDetail().getSequenceNumber());
                    sectionToSwap.getSectionDetail().setSequenceNumber(actualSeqNumber);
                    sections.add(actualSeqNumber - 1, sectionForm);
                    sections.set(actualSeqNumber, sectionToSwap);
                    List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
                    for (SectionDetailForm sectionDetailForm : sections) {
                        sectionDetails.add(sectionDetailForm.getSectionDetail());
                    }
                    questionGroupDetail.setSectionDetails(sectionDetails);
                }
                break;
            }
        }
    }

    public void moveSectionDown(String sectionName) {
        for (SectionDetailForm sectionForm : sections) {
            SectionDetail section = sectionForm.getSectionDetail();
            if (StringUtils.equalsIgnoreCase(sectionName, section.getName())) {
                int actualSeqNumber = section.getSequenceNumber();
                if (actualSeqNumber < sections.size() - 1) {
                    SectionDetailForm sectionToSwap = sections.remove(actualSeqNumber + 1);
                    section.setSequenceNumber(sectionToSwap.getSectionDetail().getSequenceNumber());
                    sectionToSwap.getSectionDetail().setSequenceNumber(actualSeqNumber);
                    sections.add(actualSeqNumber + 1, sectionForm);
                    sections.set(actualSeqNumber, sectionToSwap);
                    List<SectionDetail> sectionDetails = new ArrayList<SectionDetail>();
                    for (SectionDetailForm sectionDetailForm : sections) {
                        sectionDetails.add(sectionDetailForm.getSectionDetail());
                    }
                    questionGroupDetail.setSectionDetails(sectionDetails);
                }
                break;
            }
        }
    }

    public boolean hasNoQuestionsInCurrentSection() {
        return selectedQuestionIds.size() == 0;
    }

    private boolean sectionHasNoQuestions(SectionDetail section) {
        return section.getQuestions().size() == 0;
    }

    private void removeQuestionFromSection(String questionId, SectionDetail section) {
        SectionQuestionDetail questionToRemove = null;
        List<SectionQuestionDetail> questions = section.getQuestions();
        for (SectionQuestionDetail question : questions) {
            if (StringUtils.equals(questionId, String.valueOf(question.getQuestionId()))) {
                questionToRemove = question;
                break;
            }
        }
        if (questionToRemove != null) {
            questionToRemove.setMandatory(false);
            questionToRemove.setSequenceNumber(0);
            questions.remove(questionToRemove);
            int sequence = 0;
            for (SectionQuestionDetail sectionQuestionDetail : questions) {
                sectionQuestionDetail.setSequenceNumber(sequence++);
            }
            questionPool.add(questionToRemove);
        }
    }

    public boolean isEditable() {
        return questionGroupDetail.isEditable();
    }

    public void setEditable(boolean editable) {
       questionGroupDetail.setEditable(editable);
    }

    public boolean isActive() {
        return questionGroupDetail.isActive();
    }

    public void setActive(boolean active) {
        questionGroupDetail.setActive(active);
    }

    public Question getCurrentQuestion() {
        return currentQuestion;
    }

    public void setCurrentQuestion(Question currentQuestion) {
        this.currentQuestion = currentQuestion;
    }

    public boolean isAddQuestionFlag() {
        return addQuestionFlag;
    }

    public void setAddQuestionFlag(boolean addQuestionFlag) {
        this.addQuestionFlag = addQuestionFlag;
    }

    public void setQuestionGroupDetail(QuestionGroupDetail questionGroupDetail) {
        this.questionGroupDetail = questionGroupDetail;
        this.sections = initSections();
        this.sectionLinks = questionGroupDetail.getSectionLinks();
        this.questionLinks= questionGroupDetail.getQuestionLinks();
    }

    private List<SectionDetailForm> initSections() {
        List<SectionDetailForm> sectionDetailForms = new ArrayList<SectionDetailForm>();
        if (questionGroupDetail != null) {
            for (SectionDetail sectionDetail : questionGroupDetail.getSectionDetails()) {
                SectionDetailForm sectionDetailForm = new SectionDetailForm(sectionDetail);
                sectionDetailForm.setInitialCountOfQuestions(sectionDetail.getCountOfQuestions());
                sectionDetailForms.add(sectionDetailForm);
            }
        }
        this.initialCountOfSections = sectionDetailForms.size();
        return sectionDetailForms;
    }

    public int getInitialCountOfSections() {
        return initialCountOfSections;
    }

    public boolean getApplyToAllLoanProducts() {
        return applyToAllLoanProducts;
    }

    public void setApplyToAllLoanProducts(boolean applyToAllLoanProducts) {
        this.applyToAllLoanProducts = applyToAllLoanProducts;
    }

}
