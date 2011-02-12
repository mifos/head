/*
 * Copyright Grameen Foundation USA
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

package org.mifos.dto.screen;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

@SuppressWarnings("PMD")
@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"SE_NO_SERIALVERSIONID", "EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification="should disable at filter level and also for pmd - not important for us")
public class ClientNameDetailDto implements Serializable {

    private Short nameType;
    private Integer salutation;
    private StringBuilder displayName;
    private String firstName;
    private String middleName;
    private String lastName;
    private String secondLastName;
    private Integer customerNameId;
    private String[] names;

    public ClientNameDetailDto() {
        super();
    }

    public ClientNameDetailDto(Short nameType, Integer salutation, String firstName, String middleName,
            String lastName, String secondLastName) {
        this(nameType, salutation, new StringBuilder(), firstName, middleName, lastName, secondLastName, null);
    }

    public ClientNameDetailDto(Short nameType, Integer salutation, StringBuilder displayName, String firstName,
            String middleName, String lastName, String secondLastName, Integer customerNameId) {
        this.nameType = nameType;
        this.salutation = salutation;
        this.displayName = displayName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.secondLastName = secondLastName;
        this.customerNameId = customerNameId;
    }

    public void setDisplayName(StringBuilder displayName) {
        this.displayName = displayName;
    }

    public Short getNameType() {
        return nameType;
    }

    public void setNameType(Short nameType) {
        this.nameType = nameType;
    }

    public Integer getSalutation() {
        return salutation;
    }

    public void setSalutation(Integer salutation) {
        this.salutation = salutation;
    }

    public String getDisplayName() {
        displayName = new StringBuilder();
        addToName(displayName, names[0], false);
        for (int i = 1; i < names.length; i++) {
            addToName(displayName, names[i], true);
        }
        return displayName.toString().trim();
    }

    private void addToName(StringBuilder displayName, String nameToBeAppend, boolean isBlankRequired) {
        String firstNameConstant = "first_name";
        String middleNameConstant = "middle_name";
        String secondLastNameConstant = "second_last_name";
        String lastNameConstant = "last_name";

        if (nameToBeAppend.equals(firstNameConstant)) {
            appendToName(displayName, firstName, isBlankRequired);
        } else if (nameToBeAppend.equals(middleNameConstant)) {
            appendToName(displayName, middleName, isBlankRequired);
        } else if (nameToBeAppend.equals(lastNameConstant)) {
            appendToName(displayName, lastName, isBlankRequired);
        } else if (nameToBeAppend.equals(secondLastNameConstant)) {
            appendToName(displayName, secondLastName, isBlankRequired);
        }
    }

    private void appendToName(StringBuilder displayName, String valueToBeAppend, boolean isBlankRequired) {
        String trimmedValue = StringUtils.trim(valueToBeAppend);
        if (StringUtils.isNotBlank(trimmedValue)) {
            if (isBlankRequired) {
                displayName.append(" ");
            }
            displayName.append(trimmedValue);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getSecondLastName() {
        return secondLastName;
    }

    public void setSecondLastName(String secondLastName) {
        this.secondLastName = secondLastName;
    }

    public Integer getCustomerNameId() {
        return this.customerNameId;
    }

    public void setCustomerNameId(Integer customerNameId) {
        this.customerNameId = customerNameId;
    }

    public void setNames(String[] names) {
        this.names = names;
    }
}