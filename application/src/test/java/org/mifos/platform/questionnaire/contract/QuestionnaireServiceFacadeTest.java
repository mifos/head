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
package org.mifos.platform.questionnaire.contract;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.platform.questionnaire.QuestionnaireServiceFacadeImpl;
import org.mifos.ui.core.controller.Question;
import org.mifos.ui.core.controller.QuestionGroupForm;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class QuestionnaireServiceFacadeTest {

    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Mock
    private QuestionnaireService questionnaireService;

    private static final String TITLE = "Title";

    @Before
    public void setUp() throws Exception {
        questionnaireServiceFacade = new QuestionnaireServiceFacadeImpl(questionnaireService);
    }

    @Test
    public void shouldCreateQuestionGroup() throws ApplicationException {
        QuestionGroupForm questionGroupForm = getQuestionGroupForm(TITLE);
        questionnaireServiceFacade.createQuestionGroup(questionGroupForm);
        verify(questionnaireService, times(1)).defineQuestionGroup(argThat(new QuestionGroupDefinitionMatcher(TITLE)));
    }

    @Test
    public void testShouldCreateQuestions() throws ApplicationException {
        String title = TITLE + System.currentTimeMillis();
        String title1 = title + 1;
        String title2 = title + 2;
        questionnaireServiceFacade.createQuestions(asList(getQuestion(title1, "Free text"), getQuestion(title2, "Date")));
        verify(questionnaireService, times(2)).defineQuestion(argThat(new QuestionDefinitionMatcher(QuestionType.FREETEXT, QuestionType.DATE)));
    }

    @Test
    public void testShouldCheckDuplicates(){
        questionnaireServiceFacade.isDuplicateQuestion(TITLE);
        verify(questionnaireService).isDuplicateQuestion(any(QuestionDefinition.class));
    }

    @Test
    public void testGetAllQuestion(){
        when(questionnaireService.getAllQuestions()).thenReturn(asList(new QuestionDetail(0, "title", "title", QuestionType.NUMERIC)));
        List<Question> questionDetailList = questionnaireServiceFacade.getAllQuestions();
        assertNotNull(questionDetailList);
        assertThat(questionDetailList.get(0).getTitle(), is("title"));
        verify(questionnaireService).getAllQuestions();
    }

    @Test
    public void testGetAllQuestionGroups() {
        when(questionnaireService.getAllQuestionGroups()).thenReturn(asList(new QuestionGroupDetail("title1"), new QuestionGroupDetail("title2")));
        List<QuestionGroupForm> questionGroups = questionnaireServiceFacade.getAllQuestionGroups();
        assertNotNull(questionGroups);
        assertThat(questionGroups.get(0).getTitle(), is("title1"));
        assertThat(questionGroups.get(1).getTitle(), is("title2"));
        verify(questionnaireService).getAllQuestionGroups();
    }

    private Question getQuestion(String title, String type) {
        Question question = new Question();
        question.setTitle(title);
        question.setType(type);
        return question;
    }

    private QuestionGroupForm getQuestionGroupForm(String title) {
        QuestionGroupForm questionGroupForm = new QuestionGroupForm();
        questionGroupForm.setTitle(title);
        return questionGroupForm;
    }

    private class QuestionDefinitionMatcher extends ArgumentMatcher<QuestionDefinition> {
        private List<QuestionType> questionType;

        public QuestionDefinitionMatcher(QuestionType... questionType) {
            this.questionType = new ArrayList<QuestionType>();
            this.questionType.addAll(Arrays.asList(questionType));
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof QuestionDefinition) {
                QuestionDefinition questionDefinition = (QuestionDefinition) argument;
                if (questionType.get(0) == questionDefinition.getType()) {
                    questionType.remove(questionDefinition.getType());
                    questionType.add(questionDefinition.getType());
                    return true;
                }
            }
            return false;
        }
    }

    private class QuestionGroupDefinitionMatcher extends ArgumentMatcher<QuestionGroupDefinition> {
        private String title;

        public QuestionGroupDefinitionMatcher(String title) {
            this.title = title;
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof QuestionGroupDefinition) {
                QuestionGroupDefinition questionGroupDefinition = (QuestionGroupDefinition) argument;
                return StringUtils.equals(questionGroupDefinition.getTitle(), title);
            }
            return false;
        }
    }
}
