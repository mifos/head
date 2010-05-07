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

package org.mifos.customers.business.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.OfficeBuilder;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.domain.builders.PersonnelBuilder;

public class GroupCreationAndValidationTest {

    private GroupBO group;

    private CenterBO center;

    @Before
    public void setupDependencies() {
        OfficeBO office = new OfficeBuilder().build();
        PersonnelBO loanOfficer = new PersonnelBuilder().asLoanOfficer().build();
        MeetingBO meeting = new MeetingBuilder().customerMeeting().build();

        center = new CenterBuilder().with(office).withLoanOfficer(loanOfficer).with(meeting).build();
    }

    @Test
    public void givenNameIsNotSetThenShouldThrowCustomerExceptionWhenCreatingAGroup() {
        group = new GroupBuilder().withName(null).withParentCustomer(center).build();

        try {
            group.validate();
            fail("should throw customer exception as name must be set on group.");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_NAME));
        }
    }

    @Test
    public void givenCenterHierarchyIsOffAndOfficeIsNotSetThenShouldThrowCustomerExceptionWhenCreatingAGroup() {
        group = new GroupBuilder().withName("group-On-branch").withOffice(null).buildAsTopOfHierarchy();

        try {
            group.validate();
            fail("should throw customer exception as office must exist when creating group under a branch.");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_OFFICE));
        }
    }

    @Test
    public void givenCenterHierarchyIsOffAndLoanOfficerIsNotSetThenShouldThrowCustomerExceptionWhenCreatingAGroup() {

        OfficeBO office = new OfficeBuilder().build();
        group = new GroupBuilder().withName("group-On-branch").withOffice(office).withLoanOfficer(null).buildAsTopOfHierarchy();

        try {
            group.validate();
            fail("should throw customer exception as loan officer must exist when creating group under a branch.");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_LOAN_OFFICER));
        }
    }

    @Test
    public void givenCenterHierarchyExistsAndFormedByPersonnelIsNotSetThenShouldThrowCustomerExceptionWhenCreatingAGroup() {

        group = new GroupBuilder().withName("group-On-center").withParentCustomer(center).build();

        try {
            group.validate();
            fail("should throw customer exception as personnel that fomed group must exist when creating group.");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_FORMED_BY));
        }
    }

    @Test
    public void givenCenterHierarchyExistsAndIsTrainedButTrainedDateIsNotSetThenShouldThrowCustomerExceptionWhenCreatingAGroup() {

        PersonnelBO formedBy = new PersonnelBuilder().build();
        group = new GroupBuilder().withName("group-On-center").withParentCustomer(center).formedBy(formedBy).isTrained().trainedOn(null).build();

        try {
            group.validate();
            fail("should throw customer exception as trained date must be provided if in trained state when creating group.");
        } catch (CustomerException e) {
            assertThat(e.getKey(), is(CustomerConstants.INVALID_TRAINED_OR_TRAINEDDATE));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenGroupWithNullParentShouldThrowIllegalArgumentException() {
        OfficeBO office = new OfficeBuilder().build();
        PersonnelBO loanOfficer = new PersonnelBuilder().asLoanOfficer().build();
        MeetingBO meeting = new MeetingBuilder().customerMeeting().build();
        group = new GroupBuilder().withName("group-On-center").withOffice(office).withLoanOfficer(loanOfficer).withMeeting(meeting).withParentCustomer(null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenGroupWithNullMeetingShouldThrowIllegalArgumentException() {
        OfficeBO office = new OfficeBuilder().build();
        PersonnelBO loanOfficer = new PersonnelBuilder().asLoanOfficer().build();
        group = new GroupBuilder().withName("group-On-branch").withOffice(office).withLoanOfficer(loanOfficer).withMeeting(null).buildAsTopOfHierarchy();
    }

    @Test(expected = IllegalArgumentException.class)
    public void givenGroupWithNullCustomerStatusShouldThrowIllegalArgumentException() {
        group = new GroupBuilder().withName("group1").withStatus(null).withParentCustomer(center).build();
    }
}