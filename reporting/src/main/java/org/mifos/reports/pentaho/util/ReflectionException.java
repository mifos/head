/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
package org.mifos.reports.pentaho.util;

public class ReflectionException extends Exception {

    private static final long serialVersionUID = 3306229424623519121L;

    private final Class<?> targetClass;
    private final String sourceString;

    public ReflectionException(Class<?> targetClass, String sourceString) {
        super("Cannot convert " + sourceString + " to " + targetClass.getCanonicalName());
        this.targetClass = targetClass;
        this.sourceString = sourceString;
    }

    public Class<?> getTargetClass() {
        return targetClass;
    }

    public String getSourceString() {
        return sourceString;
    }
}
