/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.ui.core.controller.util;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mifos.platform.questionnaire.exceptions.BadNumericResponseException;
import org.mifos.platform.questionnaire.exceptions.MandatoryAnswerNotFoundException;
import org.mifos.platform.validations.ValidationException;
import org.mifos.ui.validation.StubMessageContext;
import org.springframework.binding.message.Message;
import org.springframework.binding.message.MessageContext;
import org.springframework.binding.message.MessageResolver;

public class ValidationExceptionMessageExtractorTest {

    private MessageContext context;

    @Before
    public void setUp() {
        context = mock(MessageContext.class);
    }

    @Test
    public void testBadNumericResponseException_NoBound() {
        BadNumericResponseException e = new BadNumericResponseException("Title", null, null);
        testBadNumericResponseException(e, "questionnaire.invalid.numeric.response");
    }
    
    @Test
    public void testBadNumericResponseException_UpperLowerBounds() {
        BadNumericResponseException e = new BadNumericResponseException("Title", 0, 100);
        testBadNumericResponseException(e, "questionnaire.invalid.numeric.range.response");
    }

    @Test
    public void testBadNumericResponseException_LowerBoundOnly() {
        BadNumericResponseException e = new BadNumericResponseException("Title", 0, null);
        testBadNumericResponseException(e, "questionnaire.invalid.numeric.min.response");
    }

    @Test
    public void testBadNumericResponseException_UpperBoundOnly() {
        BadNumericResponseException e = new BadNumericResponseException("Title", null, 100);
        testBadNumericResponseException(e, "questionnaire.invalid.numeric.max.response");
    }

    private void testBadNumericResponseException(BadNumericResponseException e, String expectedMessage) {
        ValidationException parent = new ValidationException("Key");
        parent.addChildException(e);
        ValidationExceptionMessageExtractor extractor = new ValidationExceptionMessageExtractor();
        MessageContext context = new StubMessageContext();
        extractor.extract(context, parent);
        
        Message[] messages = context.getAllMessages();
        Assert.assertEquals(1, messages.length);
        Message m = messages[0];
        Assert.assertEquals(expectedMessage, m.getText());
    }
    
    @Test
    public void testMandatoryAnswerNotFoundException() {
        MandatoryAnswerNotFoundException e = new MandatoryAnswerNotFoundException("Title");
        ValidationException parent = new ValidationException("Key");
        parent.addChildException(e);
        ValidationExceptionMessageExtractor extractor = new ValidationExceptionMessageExtractor();
        extractor.extract(context, parent);
        verify(context).addMessage(any(MessageResolver.class));
    }

    @Test
    public void testValidationException() {
        ValidationException parent = new ValidationException("Key 1");
        ValidationException child = new ValidationException("Key 2");
        parent.addChildException(child);
        ValidationExceptionMessageExtractor extractor = new ValidationExceptionMessageExtractor();
        extractor.extract(context, parent);
        verify(context).addMessage(any(MessageResolver.class));
    }
}
