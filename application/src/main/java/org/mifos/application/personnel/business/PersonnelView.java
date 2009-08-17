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

package org.mifos.application.personnel.business;

import org.mifos.framework.business.View;

/**
 * I am an immutable DTO for PersonnelBO
 */
public class PersonnelView extends View {

    private final Short personnelId;

    private final String displayName;

    public PersonnelView(final Short personnelId, final String displayName) {
        this.personnelId = personnelId;
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Short getPersonnelId() {
        return personnelId;
    }
}
