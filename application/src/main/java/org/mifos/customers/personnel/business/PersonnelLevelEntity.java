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

package org.mifos.customers.personnel.business;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;

/**
 * See also {@link PersonnelLevel}.
 */
public class PersonnelLevelEntity extends MasterDataEntity {

    private Short interactionFlag;

    private final PersonnelLevelEntity parent;

    public PersonnelLevelEntity(PersonnelLevel level) {
        super(level.getValue());
        this.parent = null;
    }

    protected PersonnelLevelEntity() {
        super();
        this.parent = null;

    }

    public boolean isInteractionFlag() {
        return this.interactionFlag > 0;
    }

    public PersonnelLevelEntity getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return PersonnelLevel.fromInt(getId()).toString();
    }
}
