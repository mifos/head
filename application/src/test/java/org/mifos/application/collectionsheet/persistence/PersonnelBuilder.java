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
package org.mifos.application.collectionsheet.persistence;

import java.util.Date;
import java.util.List;

import org.mifos.application.master.business.CustomFieldDto;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelLevelEntity;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.business.util.Address;
import org.mifos.security.rolesandpermission.business.RoleBO;

/**
 *
 */
public class PersonnelBuilder {

    private String name = "BuilderLoanOfficer";
    private PersonnelLevel level = PersonnelLevel.LOAN_OFFICER;
    private OfficeBO office;
    private Integer title;
    private Short preferredLocale;
    private String password;
    private String userName;
    private String emailId;
    private List<RoleBO> roles;
    private List<CustomFieldDto> customFields;
    private String governmentIdNumber;
    private Date dob;
    private Integer maritalStatus;
    private Integer gender;
    private Date dateOfJoiningMFI;
    private Date dateOfJoiningBranch;
    private Address address;
    private Short createdBy;

    public PersonnelBO build() {
        final PersonnelBO personnel = new PersonnelBO();
        personnel.setLevel(new PersonnelLevelEntity(level));
        personnel.setDisplayName(name);
        return personnel;
    }

    public PersonnelBuilder withName(final String withName) {
        this.name = withName;
        return this;
    }

    public PersonnelBuilder asLoanOfficer() {
        this.level = PersonnelLevel.LOAN_OFFICER;
        return this;
    }
}
