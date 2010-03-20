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

package org.mifos.customers.personnel.business;

import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficecFixture;
import org.mifos.customers.personnel.util.helpers.PersonnelLevel;

public class PersonnelFixture {

    public static PersonnelBO createPersonnel(String officeSearchId) {
        OfficeBO office = OfficecFixture.createOffice(officeSearchId);
        PersonnelBO personnel = new PersonnelBO();
        personnel.setOffice(office);
        return personnel;
    }

    public static PersonnelBO createPersonnel(Short personnelId) {
        PersonnelBO personnel = new PersonnelBO(personnelId, null, null, null);
        personnel.setOffice(OfficecFixture.createOffice(Short.valueOf("1")));
        return personnel;
    }

    public static PersonnelBO createLoanOfficer(Short personnelId) {
        PersonnelBO personnel = createPersonnel(personnelId);
        personnel.setLevel(new PersonnelLevelEntity(PersonnelLevel.LOAN_OFFICER));
        return personnel;
    }

    public static PersonnelBO createNonLoanOfficer(Short personnelId) {
        PersonnelBO personnel = createPersonnel(personnelId);
        personnel.setLevel(new PersonnelLevelEntity(PersonnelLevel.NON_LOAN_OFFICER));
        return personnel;
    }
}
