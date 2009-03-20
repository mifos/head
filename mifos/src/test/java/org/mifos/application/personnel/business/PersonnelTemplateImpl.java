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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;

public class PersonnelTemplateImpl implements PersonnelTemplate {
    private PersonnelLevel personnelLevel;
    private Short officeId;
    private Integer titleId;
    private Short preferredLocale;
    private String password;
    private String userName;
    private String emailId;
    private List<Short> roleIds;
    private List<CustomFieldView> customFields;
    private Name name;
    private String governmentIdNumber;
    private Date dateOfBirth;
    private Integer maritalStatusId;
    private Integer genderId;
    private Date dateOfJoiningMFI;
    private Date dateOfJoiningBranch;
    private Address address;

    private PersonnelTemplateImpl(Short officeId) {
        this.personnelLevel = PersonnelLevel.LOAN_OFFICER;
        this.officeId = officeId;
        this.titleId = new Integer(1);
        this.preferredLocale = new Short((short) 1);
        this.userName = "TestUserName";
        this.password = "password";
        this.emailId = "foo@mifos.org";
        this.roleIds = new ArrayList<Short>();
        this.roleIds.add(new Short((short) 1));
        this.customFields = new ArrayList<CustomFieldView>();
		customFields.add(new CustomFieldView(Short.valueOf("9"), "123456",
				CustomFieldType.NUMERIC));
        this.name = new Name("TestFirstName", null, null, null);
        this.governmentIdNumber = "111111";
        this.dateOfBirth = new Date();
        this.maritalStatusId = new Integer(1);
        this.genderId = new Integer(1);
        this.dateOfJoiningMFI = new Date();
        this.dateOfJoiningBranch = new Date();
        this.address = new Address();
    }

    public PersonnelLevel getPersonnelLevel() {
        return this.personnelLevel;
    }

    public Short getOfficeId() {
        return this.officeId;
    }

    public Integer getTitleId() {
        return this.titleId;
    }

    public Short getPreferredLocale() {
        return this.preferredLocale;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUserName() {
        return this.userName;
    }

    public String getEmailId() {
        return this.emailId;
    }

    public List<Short> getRoleIds() {
        return this.roleIds;
    }

    public List<CustomFieldView> getCustomFields() {
        return this.customFields;
    }

    public Name getName() {
        return this.name;
    }

    public String getGovernmentIdNumber() {
        return this.governmentIdNumber;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public Integer getMaritalStatusId() {
        return this.maritalStatusId;
    }

    public Integer getGenderId() {
        return this.genderId;
    }

    public Date getDateOfJoiningMFI() {
        return this.dateOfJoiningMFI;
    }

    public Date getDateOfJoiningBranch() {
        return this.dateOfJoiningBranch;
    }

    public Address getAddress() {
        return this.address;
    }

    /**
     * Use this in transactions that you don't plan on committing to the database.  If
     * you commit more than one of these to the database you'll run into uniqueness
     * constraints.  Plan on always rolling back the transaction.
     * @param officeId
     * @return
     */
    public static PersonnelTemplateImpl createNonUniquePersonnelTemplate(Short officeId) {
        return new PersonnelTemplateImpl(officeId);
    }
}
