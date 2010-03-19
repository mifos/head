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

package org.mifos.customers.util.helpers;

import java.util.Locale;

import junit.framework.Assert;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.group.util.helpers.GroupSearchResults;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerHelpersIntegrationTest extends MifosIntegrationTestCase {

    public CustomerHelpersIntegrationTest() throws Exception {
        super();
    }

    private static final double DELTA = 0.00000001;
    private CustomerBO center;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testCustomerView() {
        createCenter();
        CustomerView customerView = new CustomerView(center.getCustomerId(), center.getDisplayName(), center
                .getGlobalCustNum(), center.getStatus().getValue());
       Assert.assertEquals(center.getCustomerId(), customerView.getCustomerId());
       Assert.assertEquals(center.getDisplayName(), customerView.getDisplayName());
       Assert.assertEquals(center.getGlobalCustNum(), customerView.getGlobalCustNum());
       Assert.assertEquals(center.getStatus().getValue(), customerView.getStatusId());
        customerView = new CustomerView(center.getCustomerId(), center.getDisplayName(), center.getGlobalCustNum(),
                center.getStatus().getValue(), center.getLevel().getValue(), center.getVersionNo(), center.getOffice()
                        .getOfficeId(), center.getPersonnel().getPersonnelId());
       Assert.assertEquals(center.getCustomerId(), customerView.getCustomerId());
       Assert.assertEquals(center.getDisplayName(), customerView.getDisplayName());
       Assert.assertEquals(center.getGlobalCustNum(), customerView.getGlobalCustNum());
       Assert.assertEquals(center.getStatus().getValue(), customerView.getStatusId());
       Assert.assertEquals(center.getLevel().getValue(), customerView.getCustomerLevelId());
       Assert.assertEquals(center.getVersionNo(), customerView.getVersionNo());
       Assert.assertEquals(center.getOffice().getOfficeId(), customerView.getOfficeId());
       Assert.assertEquals(center.getPersonnel().getPersonnelId(), customerView.getPersonnelId());
    }

    public void testCustomerViewDefaultConstructor() {
        createCenter();
        CustomerView customerView = new CustomerView();
        customerView.setCustomerId(center.getCustomerId());
        customerView.setDisplayName(center.getDisplayName());
        customerView.setGlobalCustNum(center.getGlobalCustNum());
        customerView.setOfficeId(center.getOffice().getOfficeId());
        customerView.setStatusId(center.getStatus().getValue());
        customerView.setPersonnelId(center.getPersonnel().getPersonnelId());
        customerView.setCustomerLevelId(center.getLevel().getValue());
        customerView.setVersionNo(1);
       Assert.assertEquals(center.getCustomerId(), customerView.getCustomerId());
       Assert.assertEquals(center.getDisplayName(), customerView.getDisplayName());
       Assert.assertEquals(center.getGlobalCustNum(), customerView.getGlobalCustNum());
       Assert.assertEquals(center.getOffice().getOfficeId(), customerView.getOfficeId());
       Assert.assertEquals(center.getStatus().getValue(), customerView.getStatusId());
       Assert.assertEquals(center.getPersonnel().getPersonnelId(), customerView.getPersonnelId());
       Assert.assertEquals(center.getLevel().getValue(), customerView.getCustomerLevelId());
       Assert.assertEquals("1", customerView.getVersionNo().toString());
    }

    public void testIdGenerator() {
        createCenter();
        IdGenerator idGenerator = new IdGenerator();
       Assert.assertEquals("TestBranchOffice-000000003", idGenerator.generateSystemId(center.getOffice().getOfficeName(), 2));
       Assert.assertEquals("TestBranchOffice-000000002", idGenerator.generateSystemIdForCustomer(center.getOffice()
                .getOfficeName(), 2));
    }

    public void testLoanCycleCounter() {
        LoanCycleCounter loanCycleCounter = new LoanCycleCounter();
        loanCycleCounter.setCounter(1);
        loanCycleCounter.setOfferingName("offeringName");
       Assert.assertEquals("value of counter", 1, loanCycleCounter.getCounter());
       Assert.assertEquals("value of offering name", "offeringName", loanCycleCounter.getOfferingName());
        loanCycleCounter = new LoanCycleCounter("offeringName");
        LoanCycleCounter loanCycleCounter1 = new LoanCycleCounter("offeringName");
        LoanCycleCounter loanCycleCounter2 = new LoanCycleCounter("offeringName1");
       Assert.assertTrue(loanCycleCounter.equals(loanCycleCounter1));
        Assert.assertFalse(loanCycleCounter.equals(loanCycleCounter2));
    }

    public void testCustomerRecentActivityView() throws Exception {
        java.sql.Date sampleDate = new java.sql.Date(System.currentTimeMillis());
        CustomerRecentActivityView customerRecentActivityView = new CustomerRecentActivityView(sampleDate,
                "description", "1000", "mifos");
        customerRecentActivityView.setLocale(new Locale("1"));
       Assert.assertEquals("date", sampleDate, customerRecentActivityView.getActivityDate());
       Assert.assertEquals("description", customerRecentActivityView.getDescription());
       Assert.assertEquals("1000", customerRecentActivityView.getAmount());
       Assert.assertEquals("mifos", customerRecentActivityView.getPostedBy());
       Assert.assertEquals("1", customerRecentActivityView.getLocale().toString());
    }

    public void testGroupSearchResults() {
        createCenter();
        GroupSearchResults groupSearchResults = new GroupSearchResults();
        groupSearchResults.setCenterName(center.getDisplayName());
        groupSearchResults.setGroupId(1);
        groupSearchResults.setGroupName("group1");
        groupSearchResults.setOfficeName(center.getOffice().getOfficeName());
       Assert.assertEquals("center name", center.getDisplayName(), groupSearchResults.getCenterName());
       Assert.assertEquals("group id", 1, groupSearchResults.getGroupId());
       Assert.assertEquals("group name", "group1", groupSearchResults.getGroupName());
       Assert.assertEquals("office name", center.getOffice().getOfficeName(), groupSearchResults.getOfficeName());
    }

    private void createCenter() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center", meeting);
    }
}
