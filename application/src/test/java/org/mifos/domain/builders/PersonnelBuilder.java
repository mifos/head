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

package org.mifos.domain.builders;

import java.util.HashSet;
import java.util.Set;

import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.customers.personnel.business.PersonnelRoleEntity;
import org.mifos.customers.personnel.business.PersonnelStatusEntity;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.customers.personnel.util.helpers.PersonnelStatus;
import org.mifos.framework.TestUtils;
import org.mifos.security.util.UserContext;

/**
 *
 */
public class PersonnelBuilder {

    private String username = "anyUserName";
    private String displayName = "BuilderLoanOfficer";
    private PersonnelLevel level = PersonnelLevel.LOAN_OFFICER;
    private OfficeBO office;
    private UserContext userContext = TestUtils.makeUser();
    private Set<PersonnelRoleEntity> personnelRoles = new HashSet<PersonnelRoleEntity>();
    private PersonnelStatusEntity status = new PersonnelStatusEntity(PersonnelStatus.ACTIVE);

    public static PersonnelBO anyLoanOfficer() {
        return new PersonnelBuilder().build();
    }

    public PersonnelBO build() {
        final PersonnelBO personnel = new PersonnelBO();
        personnel.setUserName(username);
        personnel.setStatus(status);

        personnel.setPersonnelDetails(null);
        personnel.setPreferredLocale(null);

        personnel.setLevel(new PersonnelLevelEntity(level));
        personnel.setOffice(office);
        personnel.setDisplayName(displayName);

        personnel.setUserContext(userContext);
        personnel.setCreateDetails();
        personnel.setPersonnelRoles(personnelRoles);

        return personnel;
    }

    public PersonnelBuilder withDisplayName(final String withName) {
        this.displayName = withName;
        return this;
    }

    public PersonnelBuilder asLoanOfficer() {
        this.level = PersonnelLevel.LOAN_OFFICER;
        return this;
    }

    public PersonnelBuilder with(OfficeBO withOffice) {
        this.office = withOffice;
        return this;
    }

    public PersonnelBuilder withUsername(String withUsername) {
        this.username = withUsername;
        return this;
    }
}
