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

package org.mifos.framework.business.util;

import org.apache.commons.lang.StringUtils;


public class Name {

    private String firstName;

    private String middleName;

    private String lastName;

    private String secondLastName;

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

    public Name() {

    }

    public Name(String firstName, String middleName, String secondLastName, String lastName) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.secondLastName = secondLastName;
        this.lastName = lastName;
    }

    public String getDisplayName() {
        StringBuffer displayName = new StringBuffer("");
        if (StringUtils.isNotBlank(firstName)) {
            displayName.append(firstName);
        }
        if (StringUtils.isNotBlank(middleName)) {
            displayName.append(" ").append(middleName);
        }
        if (StringUtils.isNotBlank(secondLastName)) {
            displayName.append(" ").append(secondLastName);
        }
        if (StringUtils.isNotBlank(lastName)) {
            displayName.append(" ").append(lastName);
        }
        return displayName.toString();
    }
}
