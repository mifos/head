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

public class PropertyName {

    String name = null;

    String displayKey = null;

    String doNotLog = null;

    String lookUp = null;

    String parentName = null;

    EntityName entityName = null;

    String methodName;

    public String getDisplayKey() {
        return displayKey;
    }

    public void setDisplayKey(String displayKey) {
        this.displayKey = displayKey;
    }

    public String getDoNotLog() {
        return doNotLog;
    }

    public void setDoNotLog(String doNotLog) {
        this.doNotLog = doNotLog;
    }

    public EntityName getEntityName() {
        return entityName;
    }

    public void setEntityName(EntityName entityName) {
        this.entityName = entityName;
    }

    public String getLookUp() {
        return lookUp;
    }

    public void setLookUp(String lookUp) {
        this.lookUp = lookUp;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

}
