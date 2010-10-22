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

package org.mifos.application.master.business.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.accounts.api.CustomerDto;
import org.mifos.customers.personnel.business.PersonnelDto;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.dto.domain.OfficeDetailsDto;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.authorization.HierarchyManager;

import java.util.List;

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

public class MasterDataServiceIntegrationTest extends MifosIntegrationTestCase {

    private MasterDataService masterService;

    @Before
    public void setUp() throws Exception {
        masterService = new MasterDataService();
        HierarchyManager.getInstance().init();
    }

    @After
    public void tearDown() throws Exception {
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testGetListOfActiveLoanOfficers() throws Exception {
        List<PersonnelDto> loanOfficers = masterService.getListOfActiveLoanOfficers(PersonnelConstants.LOAN_OFFICER,
                Short
                .valueOf("3"), Short.valueOf("3"), PersonnelConstants.LOAN_OFFICER);
       Assert.assertEquals(1, loanOfficers.size());
    }

    @Test
    public void testGetActiveBranches() throws Exception {
        List<OfficeDetailsDto> branches = masterService.getActiveBranches(Short.valueOf("1"));
       Assert.assertEquals(1, branches.size());
    }

    @Test
    public void testGetListOfActiveParentsUnderLoanOfficer() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        CustomerBO center = TestObjectFactory.createWeeklyFeeCenter("Center_Active", meeting);
        List<CustomerDto> customers = masterService.getListOfActiveParentsUnderLoanOfficer(Short.valueOf("1"),
                CustomerLevel.CENTER.getValue(), Short.valueOf("3"));
       Assert.assertEquals(1, customers.size());
        center = null;
    }

    @Test
    public void testGetMasterEntityName() throws NumberFormatException, ServiceException {
       Assert.assertEquals("Partial Application", masterService.retrieveMasterEntities(1, Short.valueOf("1")));
    }

}
