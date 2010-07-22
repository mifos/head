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

package org.mifos.platform.questionnaire.ui.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.platform.questionnaire.service.QuestionGroupDetail;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ModelMap;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("PMD")
public class QuestionGroupControllerTest {

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    private QuestionGroupController questionGroupController;

    @Before
    public void setUp() {
        questionGroupController = new QuestionGroupController(questionnaireServiceFacade);
    }

    @Test
    public void shouldGetAllQuestionGroupResponses() {
        List<QuestionGroupDetail> questionGroupDetails = Arrays.asList(new QuestionGroupDetail());
        when(questionnaireServiceFacade.getQuestionGroups(101, "Create", "Client")).thenReturn(questionGroupDetails);
        ModelMap modelMap = questionGroupController.getAllQuestionGroupResponses(101, "Create", "Client");
        assertThat(modelMap, is(notNullValue()));
        assertThat(modelMap.containsAttribute("questionGroupDetails"), is(true));
        assertThat((List<QuestionGroupDetail>) modelMap.get("questionGroupDetails"), is(questionGroupDetails));
        Mockito.verify(questionnaireServiceFacade, Mockito.times(1)).getQuestionGroups(101, "Create", "Client");
    }
}
