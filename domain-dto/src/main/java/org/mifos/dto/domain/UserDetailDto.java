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

package org.mifos.dto.domain;

import java.io.Serializable;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SE_NO_SERIALVERSIONID", justification="required for spring web flow storage at a minimum - should disable at filter level and also for pmd")
public class UserDetailDto implements Serializable {

    private final String officeName;
    private final String systemId;
    private final Integer id;
    private final String firstName;
    private final String lastName;
    private final boolean loanOfficer;

    public UserDetailDto(String officeName, Integer id, String systemId, String firstName, String lastName, boolean loanOfficer) {
        this.officeName = officeName;
        this.id = id;
        this.systemId = systemId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.loanOfficer = loanOfficer;
    }

    public String getOfficeName() {
        return this.officeName;
    }

    public String getSystemId() {
        return this.systemId;
    }

    public Integer getId() {
        return this.id;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public boolean isLoanOfficer() {
        return this.loanOfficer;
    }
}