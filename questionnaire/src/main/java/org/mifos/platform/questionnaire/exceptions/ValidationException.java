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

import org.mifos.framework.exceptions.SystemException;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.mifos.platform.util.CollectionUtils.isEmpty;

@SuppressWarnings("PMD")
public class ValidationException extends SystemException {
    private static final long serialVersionUID = -8094463668575047971L;
    private List<ValidationException> childExceptions;
    protected final String questionTitle;

    public ValidationException(String key) {
        this(key, null);
    }

    protected ValidationException(String key, String questionTitle) {
        super(key);
        this.questionTitle = questionTitle;
    }

    public void addChildException(ValidationException validationException) {
        if (childExceptions == null) childExceptions = new ArrayList<ValidationException>();
        childExceptions.add(validationException);
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        StackTraceElement[] stackTraceElements = super.getStackTrace();
        if (containsChildExceptions()) {
            List<StackTraceElement> stackTraceElementList = new ArrayList<StackTraceElement>();
            for (ValidationException validationException : childExceptions) {
                stackTraceElementList.addAll(asList(validationException.getStackTrace()));
            }
            stackTraceElements = stackTraceElementList.toArray(new StackTraceElement[childExceptions.size()]);
        }
        return stackTraceElements;
    }

    public List<ValidationException> getChildExceptions() {
        return childExceptions;
    }

    public boolean containsChildExceptions() {
        return !isEmpty(childExceptions);
    }

    public String getQuestionTitle() {
        return questionTitle;
    }
}
