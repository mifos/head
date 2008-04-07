package org.mifos.application.personnel.business;

import java.util.Date;
import java.util.List;

import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;

/**
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the
 * License.
 * <p/>
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license
 * and how it is applied.
 */
public interface PersonnelTemplate {
    public PersonnelLevel getPersonnelLevel();

    public Short getOfficeId();

    public Integer getTitleId();

    public Short getPreferredLocale();

    public String getPassword();

    public String getUserName();

    public String getEmailId();

    public List<Short> getRoleIds();

    public List<CustomFieldView> getCustomFields();

    public Name getName();

    public String getGovernmentIdNumber();

    public Date getDateOfBirth();

    public Integer getMaritalStatusId();

    public Integer getGenderId();

    public Date getDateOfJoiningMFI();

    public Date getDateOfJoiningBranch();

    public Address getAddress();
}
