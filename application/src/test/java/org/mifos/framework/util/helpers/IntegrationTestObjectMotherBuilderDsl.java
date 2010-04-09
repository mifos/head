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

package org.mifos.framework.util.helpers;

import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.TestUtils;
import org.mifos.security.util.UserContext;

public class IntegrationTestObjectMotherBuilderDsl {

    public static OfficeBO anExistingBranchOffice(OfficeBuilder aBranchOffice) {
        OfficeBO headOffice = new OfficeBuilder().withGlobalOfficeNum("xxxx-001").withName("builder-head-office").withShortName("hf1").withSearchId("1.1.").headOffice().build();
        IntegrationTestObjectMother.createOffice(headOffice);

        OfficeBO areaOffice = new OfficeBuilder().withParentOffice(headOffice).withGlobalOfficeNum("xxxx-002").withName("builder-area-office").withShortName("af1").withSearchId("1.1.1.").areaOffice().build();
        IntegrationTestObjectMother.createOffice(areaOffice);

        OfficeBO branchOffice = aBranchOffice.withParentOffice(areaOffice).branchOffice().build();
        IntegrationTestObjectMother.createOffice(branchOffice);

        return branchOffice;
    }

    public static GroupBO anExistingGroupUnderCenterInDifferentBranchAs(CenterBuilder centerBuilder) {

        OfficeBO office = new OfficeBuilder().withGlobalOfficeNum("xxxx-123").withName("newOffice").build();
        IntegrationTestObjectMother.createOffice(office);

        CenterBO centerAsParent = centerBuilder.build();
        IntegrationTestObjectMother.createCenter(centerAsParent, centerAsParent.getCustomerMeetingValue());

        GroupBO group = anActiveGroup().withParentCustomer(centerAsParent).formedBy(anExistingApplicationUser()).build();
        IntegrationTestObjectMother.createGroup(group, group.getCustomerMeetingValue());

        return group;
    }

    public static GroupBO anExistingGroupUnderCenterInSameBranchAs(CenterBuilder centerBuilder) {

        CenterBO centerAsParent = centerBuilder.build();
        IntegrationTestObjectMother.createCenter(centerAsParent, centerAsParent.getCustomerMeetingValue());

        GroupBO group = anActiveGroup().withParentCustomer(centerAsParent).formedBy(anExistingApplicationUser()).build();
        IntegrationTestObjectMother.createGroup(group, group.getCustomerMeetingValue());

        return group;
    }

    public static GroupBuilder anActiveGroup() {
        UserContext userContext = TestUtils.makeUser();
        return new GroupBuilder().with(userContext);
    }

    public static CenterBuilder anActiveCenter() {
        UserContext userContext = TestUtils.makeUser();
        return new CenterBuilder().with(userContext);
    }

    public static CenterBO anExistingActiveCenter(CenterBuilder centerBuilder) {

        CenterBO existingCenter = centerBuilder.build();
        IntegrationTestObjectMother.createCenter(existingCenter, existingCenter.getCustomerMeetingValue());
        return existingCenter;
    }

    public static PersonnelBO anExistingLoanOfficer() {
        return IntegrationTestObjectMother.testUser();
    }

    public static PersonnelBO aDifferentExistingLoanOfficer() {
        return IntegrationTestObjectMother.systemUser();
    }

    public static PersonnelBO anExistingApplicationUser() {
        return IntegrationTestObjectMother.testUser();
    }

    public static OfficeBO anExistingOffice() {
        return IntegrationTestObjectMother.sampleBranchOffice();
    }

    public static MeetingBuilder aWeeklyMeeting() {
        return new MeetingBuilder().customerMeeting().weekly().every(1).startingToday();
    }
}
