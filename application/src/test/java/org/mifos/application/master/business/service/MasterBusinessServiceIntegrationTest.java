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

import static org.mifos.application.meeting.util.helpers.MeetingType.CUSTOMER_MEETING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.WEEKLY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_WEEK;

import java.util.List;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountStateEntity;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerView;
import org.mifos.customers.office.business.OfficeView;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.authorization.HierarchyManager;

public class MasterBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    public MasterBusinessServiceIntegrationTest() throws Exception {
        super();
    }

    private MasterDataService masterService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        masterService = new MasterDataService();
        HierarchyManager.getInstance().init();
    }

    @Override
    public void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetListOfActiveLoanOfficers() throws Exception {
        List<PersonnelView> loanOfficers = masterService.getListOfActiveLoanOfficers(PersonnelConstants.LOAN_OFFICER,
                Short
                .valueOf("3"), Short.valueOf("3"), PersonnelConstants.LOAN_OFFICER);
       Assert.assertEquals(1, loanOfficers.size());
    }

    public void testGetListOfActiveLoanOfficersForInvalidConnection() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            masterService.getListOfActiveLoanOfficers(PersonnelConstants.LOAN_OFFICER, Short.valueOf("3"), Short
                    .valueOf("3"), PersonnelConstants.LOAN_OFFICER);
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testGetActiveBranches() throws Exception {
        List<OfficeView> branches = masterService.getActiveBranches(Short.valueOf("1"));
       Assert.assertEquals(1, branches.size());
    }

    public void testGetActiveBranchesForInvalidConnection() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            masterService.getActiveBranches(Short.valueOf("1"));
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testGetListOfActiveParentsUnderLoanOfficer() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        CustomerBO center = TestObjectFactory.createWeeklyFeeCenter("Center_Active", meeting);
        List<CustomerView> customers = masterService.getListOfActiveParentsUnderLoanOfficer(Short.valueOf("1"),
                CustomerLevel.CENTER.getValue(), Short.valueOf("3"));
       Assert.assertEquals(1, customers.size());
        TestObjectFactory.cleanUp(center);
    }

    public void testGetListOfActiveParentsUnderLoanOfficerForInvalidConnection() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeetingForToday(WEEKLY, EVERY_WEEK,
                CUSTOMER_MEETING));
        CustomerBO center = TestObjectFactory.createWeeklyFeeCenter("Center_Active", meeting);
        TestObjectFactory.simulateInvalidConnection();
        try {
            masterService.getListOfActiveParentsUnderLoanOfficer(Short.valueOf("1"), CustomerLevel.CENTER.getValue(),
                    Short.valueOf("3"));
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
        TestObjectFactory.cleanUp(center);
    }

    public void testGetMasterEntityName() throws NumberFormatException, ServiceException {
       Assert.assertEquals("Partial Application", masterService.retrieveMasterEntities(1, Short.valueOf("1")));
    }

    public void testGetMasterEntityNameForInvalidConnection() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            masterService.retrieveMasterEntities(1, Short.valueOf("1"));
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testretrieveCustomFieldsDefinition() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            masterService.retrieveCustomFieldsDefinition(EntityType.CENTER);
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testGetMasterDataEntity() {
        TestObjectFactory.simulateInvalidConnection();
        try {
            masterService.getMasterDataEntity(AccountStateEntity.class, AccountState.LOAN_PARTIAL_APPLICATION
                    .getValue());
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }
}
