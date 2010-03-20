/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.framework.components.audit.util.helpers;

/**
 * What does this do? It is potentially confused with the more widely used
 * {@link org.mifos.application.util.helpers.EntityType}.
 */
public class EntityType {

    String name;

    String classPath;

    EntitiesToLog entitiesToLogs;

    PropertyName[] propertyNames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PropertyName[] getPropertyNames() {
        return propertyNames;
    }

    public void setPropertyNames(PropertyName[] propertyNames) {
        this.propertyNames = propertyNames;
    }

    public EntitiesToLog getEntitiesToLog() {
        return entitiesToLogs;
    }

    public void setEntitiesToLog(EntitiesToLog entitiesToLog) {
        this.entitiesToLogs = entitiesToLog;
    }

    public String getClassPath() {
        return classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

}
