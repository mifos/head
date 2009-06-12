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

package org.mifos.application.master.business;

import java.util.Set;

/**
 * {@link LookUpEntity} is now deprecated and should not be used. Use
 * {@link MifosLookUpEntity} instead which provides the same functionality and
 * includes the constants previously defined here.
 * 
 * This class can probably be deleted now-- will make sure no issues show up
 * first.
 */
public class LookUpEntity {

    public LookUpEntity() {
        super();
    }

    private Short entityId;

    private String entityType;

    private Set lookUpLabelSet;

    private Set lookUpValueSet;

    public Short getEntityId() {
        return entityId;
    }

    public void setEntityId(Short entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Set getLookUpLabelSet() {
        return lookUpLabelSet;
    }

    public void setLookUpLabelSet(Set lookUpLabelSet) {
        this.lookUpLabelSet = lookUpLabelSet;
    }

    public Set getLookUpValueSet() {
        return lookUpValueSet;
    }

    public void setLookUpValueSet(Set lookUpValueSet) {
        this.lookUpValueSet = lookUpValueSet;
    }

}
