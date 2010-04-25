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
        CustomerDto customerDto = new CustomerDto(center.getCustomerId(), center.getDisplayName(), center
                .getGlobalCustNum(), center.getStatus().getValue());
       Assert.assertEquals(center.getCustomerId(), customerDto.getCustomerId());
       Assert.assertEquals(center.getDisplayName(), customerDto.getDisplayName());
       Assert.assertEquals(center.getGlobalCustNum(), customerDto.getGlobalCustNum());
       Assert.assertEquals(center.getStatus().getValue(), customerDto.getStatusId());
        customerDto = new CustomerDto(center.getCustomerId(), center.getDisplayName(), center.getGlobalCustNum(),
                center.getStatus().getValue(), center.getLevel().getValue(), center.getVersionNo(), center.getOffice()
                        .getOfficeId(), center.getPersonnel().getPersonnelId());
       Assert.assertEquals(center.getCustomerId(), customerDto.getCustomerId());
       Assert.assertEquals(center.getDisplayName(), customerDto.getDisplayName());
       Assert.assertEquals(center.getGlobalCustNum(), customerDto.getGlobalCustNum());
       Assert.assertEquals(center.getStatus().getValue(), customerDto.getStatusId());
       Assert.assertEquals(center.getLevel().getValue(), customerDto.getCustomerLevelId());
       Assert.assertEquals(center.getVersionNo(), customerDto.getVersionNo());
       Assert.assertEquals(center.getOffice().getOfficeId(), customerDto.getOfficeId());
       Assert.assertEquals(center.getPersonnel().getPersonnelId(), customerDto.getPersonnelId());
    }

    public void testCustomerViewDefaultConstructor() {
        createCenter();
        CustomerDto customerDto = new CustomerDto();
        customerDto.setCustomerId(center.getCustomerId());
        customerDto.setDisplayName(center.getDisplayName());
        customerDto.setGlobalCustNum(center.getGlobalCustNum());
        customerDto.setOfficeId(center.getOffice().getOfficeId());
        customerDto.setStatusId(center.getStatus().getValue());
        customerDto.setPersonnelId(center.getPersonnel().getPersonnelId());
        customerDto.setCustomerLevelId(center.getLevel().getValue());
        customerDto.setVersionNo(1);
       Assert.assertEquals(center.getCustomerId(), customerDto.getCustomerId());
       Assert.assertEquals(center.getDisplayName(), customerDto.getDisplayName());
       Assert.assertEquals(center.getGlobalCustNum(), customerDto.getGlobalCustNum());
       Assert.assertEquals(center.getOffice().getOfficeId(), customerDto.getOfficeId());
       Assert.assertEquals(center.getStatus().getValue(), customerDto.getStatusId());
       Assert.assertEquals(center.getPersonnel().getPersonnelId(), customerDto.getPersonnelId());
       Assert.assertEquals(center.getLevel().getValue(), customerDto.getCustomerLevelId());
       Assert.assertEquals("1", customerDto.getVersionNo().toString());
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
        CustomerRecentActivityDto customerRecentActivityDto = new CustomerRecentActivityDto(sampleDate,
                "description", "1000", "mifos");
        customerRecentActivityDto.setLocale(new Locale("1"));
       Assert.assertEquals("date", sampleDate, customerRecentActivityDto.getActivityDate());
       Assert.assertEquals("description", customerRecentActivityDto.getDescription());
       Assert.assertEquals("1000", customerRecentActivityDto.getAmount());
       Assert.assertEquals("mifos", customerRecentActivityDto.getPostedBy());
       Assert.assertEquals("1", customerRecentActivityDto.getLocale().toString());
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
