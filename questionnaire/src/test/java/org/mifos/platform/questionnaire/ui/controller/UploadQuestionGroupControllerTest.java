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
import org.mifos.framework.exceptions.SystemException;
import org.mifos.platform.questionnaire.QuestionnaireConstants;
import org.mifos.platform.questionnaire.exceptions.ValidationException;
import org.mifos.platform.questionnaire.matchers.MessageMatcher;
import org.mifos.platform.questionnaire.service.QuestionnaireServiceFacade;
import org.mifos.platform.questionnaire.ui.model.UploadQuestionGroupForm;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.execution.RequestContext;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.DUPLICATE_QUESTION_FOUND_IN_SECTION;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.FETCH_PPI_COUNTRY_XML_FAILED;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.GENERIC_VALIDATION;
import static org.mifos.platform.questionnaire.QuestionnaireConstants.QUESTION_GROUP_TITLE_NOT_PROVIDED;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UploadQuestionGroupControllerTest {

    private UploadQuestionGroupController controller;

    @Mock
    private QuestionnaireServiceFacade questionnaireServiceFacade;

    @Mock
    private RequestContext requestContext;

    @Mock
    private MessageContext messageContext;

    @Before
    public void setUp() {
        controller = new UploadQuestionGroupController(questionnaireServiceFacade);
    }

    @Test
    public void testGetAllCountriesForPPI() {
        List<String> countries = asList("India", "China", "Canada");
        when(questionnaireServiceFacade.getAllCountriesForPPI()).thenReturn(countries);
        assertThat(controller.getAllCountriesForPPI(), is(countries));
    }

    @Test
    public void testUploadQuestionGroup() {
        UploadQuestionGroupForm form = new UploadQuestionGroupForm();
        form.setSelectedCountry("INDIA");
        String result = controller.upload(form, requestContext);
        assertThat(result, is("success"));
        verify(questionnaireServiceFacade).uploadPPIQuestionGroup("INDIA");
    }

    @Test
    public void testUploadQuestionGroup_ValidateForInvalidCountry() {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        UploadQuestionGroupForm form = new UploadQuestionGroupForm();
        form.setSelectedCountry(QuestionnaireConstants.SELECT_ONE);
        String result = controller.upload(form, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher("questionnaire.error.ppi.country")));
    }

    @Test
    public void testUploadQuestionGroup_UploadFailureDuringPPIProcessing() {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        doThrow(new SystemException(FETCH_PPI_COUNTRY_XML_FAILED)).when(questionnaireServiceFacade).uploadPPIQuestionGroup("INDIA");
        UploadQuestionGroupForm form = new UploadQuestionGroupForm();
        form.setSelectedCountry("INDIA");
        String result = controller.upload(form, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(FETCH_PPI_COUNTRY_XML_FAILED)));
    }

    @Test
    public void testUploadQuestionGroup_UploadFailureDueToGenericError() {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        String exceptionCode = "Random Exception";
        doThrow(new RuntimeException(exceptionCode)).when(questionnaireServiceFacade).uploadPPIQuestionGroup("INDIA");
        UploadQuestionGroupForm form = new UploadQuestionGroupForm();
        form.setSelectedCountry("INDIA");
        String result = controller.upload(form, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(exceptionCode)));
    }

    @Test
    public void testUploadQuestionGroup_UploadFailureDuringValidation() {
        when(requestContext.getMessageContext()).thenReturn(messageContext);
        ValidationException validationException = new ValidationException(GENERIC_VALIDATION);
        validationException.addChildException(new ValidationException(QUESTION_GROUP_TITLE_NOT_PROVIDED));
        validationException.addChildException(new ValidationException(DUPLICATE_QUESTION_FOUND_IN_SECTION));
        doThrow(validationException).when(questionnaireServiceFacade).uploadPPIQuestionGroup("INDIA");
        UploadQuestionGroupForm form = new UploadQuestionGroupForm();
        form.setSelectedCountry("INDIA");
        String result = controller.upload(form, requestContext);
        assertThat(result, is("failure"));
        verify(requestContext).getMessageContext();
        verify(messageContext).addMessage(argThat(new MessageMatcher(QUESTION_GROUP_TITLE_NOT_PROVIDED)));
        verify(messageContext).addMessage(argThat(new MessageMatcher(DUPLICATE_QUESTION_FOUND_IN_SECTION)));
    }
}
