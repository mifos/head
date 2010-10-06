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

package org.mifos.platform.questionnaire.exceptions;

import org.junit.Test;
import org.mifos.platform.exceptions.ValidationException;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@SuppressWarnings("PMD")
public class ValidationExceptionTest {
    private static final String TOP_LEVEL_EXCEPTION = "Top level Exception";
    private static final String INSIDE_VALIDATION_METHOD1 = "Inside validationMethod1";
    private static final String INSIDE_VALIDATION_METHOD2 = "Inside validationMethod2";
    private static final String INSIDE_VALIDATION_METHOD3 = "Inside validationMethod3";

    @Test
    public void shouldPrintStackTraceOfChildExceptions() {
        ValidationException validationException = generateValidationException();
        assertThat(validationException.hasChildExceptions(), is(true));
        String stackTraceString = validationException.getStackTraceString();
        assertThat(stackTraceString, is(notNullValue()));
        assertThat(stackTraceString.contains(TOP_LEVEL_EXCEPTION), is(true));
        assertThat(stackTraceString.contains(INSIDE_VALIDATION_METHOD1), is(true));
        assertThat(stackTraceString.contains(INSIDE_VALIDATION_METHOD2), is(true));
        assertThat(stackTraceString.contains(INSIDE_VALIDATION_METHOD3), is(true));
    }

    @Test
    public void shouldCopyChildExceptionsWithChildExceptions() {
        ValidationException validationException1 = generateValidationException();
        ValidationException validationException2 = generateValidationException();
        validationException1.copyChildExceptions(validationException2);
        assertThat(validationException1.hasChildExceptions(), is(true));
        List<ValidationException> childExceptions = validationException1.getChildExceptions();
        assertThat(childExceptions.size(), is(6));
    }

    @Test
    public void shouldCopyChildExceptionsWithNoChildExceptions() {
        ValidationException validationException1 = generateValidationException();
        ValidationException validationException2 = new ValidationException(TOP_LEVEL_EXCEPTION);
        validationException1.copyChildExceptions(validationException2);
        assertThat(validationException1.hasChildExceptions(), is(true));
        List<ValidationException> childExceptions = validationException1.getChildExceptions();
        assertThat(childExceptions.size(), is(4));
    }

    private ValidationException generateValidationException() {
        ValidationException validationException = new ValidationException(TOP_LEVEL_EXCEPTION);
        validationMethod1(validationException);
        validationMethod2(validationException);
        validationMethod3(validationException);
        return validationException;
    }

    private void validationMethod1(ValidationException validationException) {
        validationException.addChildException(new ValidationException(INSIDE_VALIDATION_METHOD1));
    }

    private void validationMethod2(ValidationException validationException) {
        validationException.addChildException(new ValidationException(INSIDE_VALIDATION_METHOD2));
    }

    private void validationMethod3(ValidationException validationException) {
        validationException.addChildException(new ValidationException(INSIDE_VALIDATION_METHOD3));
    }
}
