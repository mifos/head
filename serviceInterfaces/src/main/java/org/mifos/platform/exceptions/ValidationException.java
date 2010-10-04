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

package org.mifos.platform.exceptions;

import org.mifos.framework.exceptions.SystemException;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;
import static org.mifos.platform.util.CollectionUtils.isEmpty;

@SuppressWarnings("PMD")
public class ValidationException extends SystemException {
    private static final long serialVersionUID = -8094463668575047971L;
    private List<ValidationException> childExceptions;
    protected final String identifier;
    private static final String MIFOS_PACKAGE_PREFIX = "org.mifos";

    public ValidationException(String key) {
        this(key, null);
    }

    public ValidationException(String key, String identifier) {
        super(key);
        this.identifier = identifier;
    }

    public void addChildException(ValidationException validationException) {
        if (childExceptions == null) childExceptions = new ArrayList<ValidationException>();
        childExceptions.add(validationException);
    }

    public List<ValidationException> getChildExceptions() {
        return childExceptions;
    }

    public boolean hasChildExceptions() {
        return !isEmpty(childExceptions);
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public void printStackTrace() {
        System.err.println(getStackTraceString());
    }

    @Override
    public void printStackTrace(PrintStream printStream) {
        printStream.println(getStackTraceString());
    }

    @Override
    public void printStackTrace(PrintWriter printWriter) {
        printWriter.println(getStackTraceString());
    }

    public String getStackTraceString() {
        StringBuilder buffer = new StringBuilder();
        makeStackTrace(buffer, getStackTrace(), getKey());
        if (childExceptions != null) {
            for (ValidationException childException : childExceptions) {
                makeStackTrace(buffer, childException.getStackTrace(), childException.getKey());
            }
        }
        return buffer.toString();
    }

    // Filters 'noise' frames from the stack trace by including only the frames from the 'mifos' code base
    private void makeStackTrace(StringBuilder buffer, StackTraceElement[] stackTraceElements, String key) {
        buffer.append(key);
        if (stackTraceElements != null) {
            for (StackTraceElement stackTraceElement : stackTraceElements) {
                String stackFrameStr = stackTraceElement.toString();
                if (startsWithIgnoreCase(stackFrameStr, MIFOS_PACKAGE_PREFIX)) {
                    buffer.append("\n   at ").append(stackFrameStr);
                }
            }
        } else {
            buffer.append("\n   <no stack trace available>");
        }
        buffer.append("\n");
    }

}
