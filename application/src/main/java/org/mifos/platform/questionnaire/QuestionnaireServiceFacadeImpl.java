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

package org.mifos.platform.questionnaire;

import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.CollectionUtils;
import org.mifos.platform.questionnaire.contract.*;
import org.mifos.ui.core.controller.*;
import org.mifos.ui.core.controller.SectionForm;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mifos.framework.util.MapEntry.makeEntry;

public class QuestionnaireServiceFacadeImpl implements QuestionnaireServiceFacade {

    @Autowired
    private QuestionnaireService questionnaireService;
    private Map<String, QuestionType> stringToQuestionTypeMap;
    private Map<QuestionType, String> questionTypeToStringMap;

    public QuestionnaireServiceFacadeImpl(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
        populateStringToQuestionTypeMap();
        populateQuestionTypeToStringMap();
    }

    @SuppressWarnings({"UnusedDeclaration"})
    public QuestionnaireServiceFacadeImpl() {
        this(null);
    }

    @Override
    public void createQuestions(List<Question> questions) throws ApplicationException {
        for (Question question : questions) {
            questionnaireService.defineQuestion(mapToQuestionDefinition(question));
        }
    }

    @Override
    public boolean isDuplicateQuestion(String title) {
        return questionnaireService.isDuplicateQuestion(new QuestionDefinition(title));
    }

    @Override
    public void createQuestionGroup(QuestionGroupForm questionGroupForm) throws ApplicationException {
        questionnaireService.defineQuestionGroup(mapToQuestionDefinition(questionGroupForm));
    }

    private QuestionGroupDefinition mapToQuestionDefinition(QuestionGroupForm questionGroupForm) {
        return new QuestionGroupDefinition(questionGroupForm.getTitle(), mapToSectionDefinitions(questionGroupForm.getSections()));
    }

    private static List<SectionDefinition> mapToSectionDefinitions(List<SectionForm> sectionForms) {
        List<SectionDefinition> sections = new ArrayList<SectionDefinition>();
        for (SectionForm sectionForm: sectionForms){
            sections.add(mapToSectionDefinition(sectionForm));
        }
        return sections;
    }

    private static SectionDefinition mapToSectionDefinition(SectionForm sectionForm) {
        SectionDefinition section = new SectionDefinition();
        section.setName(sectionForm.getName());
        return section;
    }

    @Override
    public List<Question> getAllQuestions() {
        return mapToQuestions(questionnaireService.getAllQuestions());
    }

    private List<Question> mapToQuestions(List<QuestionDetail> questionDetails) {
        List<Question> questions = new ArrayList<Question>();
        for (QuestionDetail questionDetail : questionDetails) {
            Question question = mapToQuestion(questionDetail);
            questions.add(question);
        }
        return questions;
    }

    private Question mapToQuestion(QuestionDetail questionDetail) {
        Question question = new Question();
        question.setTitle(questionDetail.getText());
        question.setId(questionDetail.getId().toString());
        question.setType(questionTypeToStringMap.get(questionDetail.getType()));
        return question;
    }

    @Override
    public List<QuestionGroupForm> getAllQuestionGroups() {
        return mapToQuestionGroups(questionnaireService.getAllQuestionGroups());
    }

    @Override
    public QuestionGroupForm getQuestionGroup(int questionGroupId) throws ApplicationException {
        return mapToQuestionGroup(questionnaireService.getQuestionGroup(questionGroupId));
    }

    @Override
    public Question getQuestion(int questionId) throws ApplicationException {
        return mapToQuestion(questionnaireService.getQuestion(questionId));
    }

    @Override
    public List<EventSource> getAllEventSources() {
        return questionnaireService.getAllEventSources();
    }

    private List<QuestionGroupForm> mapToQuestionGroups(List<QuestionGroupDetail> questionGroupDetails) {
        List<QuestionGroupForm> questionGroupForms = new ArrayList<QuestionGroupForm>();
        for (QuestionGroupDetail questionGroupDetail : questionGroupDetails) {
            QuestionGroupForm questionGroupForm = mapToQuestionGroup(questionGroupDetail);
            questionGroupForms.add(questionGroupForm);
        }
        return questionGroupForms;
    }

    private QuestionGroupForm mapToQuestionGroup(QuestionGroupDetail questionGroupDetail) {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setId(questionGroupDetail.getId().toString());
        questionGroupForm.setTitle(questionGroupDetail.getTitle());
        return questionGroupForm;
    }

    private void populateStringToQuestionTypeMap() {
        stringToQuestionTypeMap = CollectionUtils.asMap(makeEntry("Free text", QuestionType.FREETEXT),
                makeEntry("Date", QuestionType.DATE),
                makeEntry("Number", QuestionType.NUMERIC));
    }

    private void populateQuestionTypeToStringMap() {
        questionTypeToStringMap = CollectionUtils.asMap(makeEntry(QuestionType.FREETEXT, "Free text"),
                makeEntry(QuestionType.DATE, "Date"),
                makeEntry(QuestionType.NUMERIC, "Number"));
    }

    private QuestionDefinition mapToQuestionDefinition(Question question) {
        return new QuestionDefinition(question.getTitle(), stringToQuestionTypeMap.get(question.getType()));
    }
}
