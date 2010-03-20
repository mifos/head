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

package org.mifos.customers.group.persistence;

import junit.framework.Assert;

import org.mifos.application.meeting.MeetingTemplateImpl;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.CenterTemplate;
import org.mifos.customers.center.CenterTemplateImpl;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.persistence.CenterPersistence;
import org.mifos.customers.group.GroupTemplate;
import org.mifos.customers.group.GroupTemplateImpl;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficeTemplate;
import org.mifos.customers.office.business.OfficeTemplateImpl;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.customers.office.util.helpers.OfficeLevel;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class GroupPersistenceIntegrationTest extends MifosIntegrationTestCase {
    public GroupPersistenceIntegrationTest() throws Exception {
        super();
    }

    private MeetingBO meeting;

    private CustomerBO center;

    private GroupBO group;

    private GroupPersistence groupPersistence;
    private OfficePersistence officePersistence;
    private CenterPersistence centerPersistence;

    @Override
    protected void setUp() throws Exception {
        this.officePersistence = new OfficePersistence();
        this.centerPersistence = new CenterPersistence();
        this.groupPersistence = new GroupPersistence();
        initializeStatisticsService();
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception {
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testUpdateGroupInfoAndGroupPerformanceHistoryForPortfolioAtRisk() throws Exception {
        createGroup();
        double portfolioAtRisk = 0.567;
        Integer groupId = group.getCustomerId();
        boolean result = getGroupPersistence().updateGroupInfoAndGroupPerformanceHistoryForPortfolioAtRisk(
                portfolioAtRisk, groupId);
       Assert.assertTrue(result);
        group = TestObjectFactory.getGroup(group.getCustomerId());
       Assert.assertEquals(1, group.getUpdatedBy().intValue());
        java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
       Assert.assertEquals(1, group.getUpdatedBy().intValue());
       Assert.assertEquals(currentDate.toString(), group.getUpdatedDate().toString());
       Assert.assertEquals(new Money(getCurrency(), "0.567"), group.getGroupPerformanceHistory().getPortfolioAtRisk());

    }

    // FIXME - keithw - IGNORING AFTER REMOVAL OF CENTER PERSISTENCE
    public void ignore_testCreateGroup() throws Exception {
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        try {
            UserContext userContext = TestUtils.makeUser();

            OfficeTemplate template = OfficeTemplateImpl.createNonUniqueOfficeTemplate(OfficeLevel.BRANCHOFFICE);
            OfficeBO office = getOfficePersistence().createOffice(userContext, template);

            MeetingBO meeting = new MeetingBO(MeetingTemplateImpl.createWeeklyMeetingTemplate());

            CenterTemplate centerTemplate = new CenterTemplateImpl(meeting, office.getOfficeId());
            CenterBO center = getCenterPersistence().createCenter(userContext, centerTemplate);

            GroupTemplate groupTemplate = GroupTemplateImpl.createNonUniqueGroupTemplate(center.getCustomerId());
            GroupBO group = getGroupPersistence().createGroup(userContext, groupTemplate);

            Assert.assertNotNull(group.getCustomerId());
           Assert.assertTrue(group.isActive());
        } finally {
            StaticHibernateUtil.rollbackTransaction();
        }
       Assert.assertTrue(transactionCount == getStatisticsService().getSuccessfulTransactionCount());
    }

    public void testGetGroupBySystemId() throws PersistenceException {
        createGroup();
        group = groupPersistence.findBySystemId(group.getGlobalCustNum());
       Assert.assertEquals("Group_Active_test", group.getDisplayName());
    }

    public void testSearch() throws Exception {
        createGroup();
        QueryResult queryResult = groupPersistence.search(group.getDisplayName(), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
       Assert.assertEquals(1, queryResult.getSize());
       Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    public void testSearchForAddingClientToGroup() throws Exception {
        createGroup_ON_HOLD_STATUS();
        QueryResult queryResult = groupPersistence.searchForAddingClientToGroup(group.getDisplayName(), Short
                .valueOf("1"));
        Assert.assertNotNull(queryResult);
       Assert.assertEquals(0, queryResult.getSize());
       Assert.assertEquals(0, queryResult.get(0, 10).size());
    }

    private CenterBO createCenter() {
        return createCenter("Center_Active_test");
    }

    private CenterBO createCenter(String name) {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createWeeklyFeeCenter(name, meeting);
    }

    private void createGroup() {
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
        StaticHibernateUtil.closeSession();

    }

    private void createGroup_ON_HOLD_STATUS() {
        center = createCenter();
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_ON_HOLD_test", CustomerStatus.GROUP_HOLD, center);
        StaticHibernateUtil.closeSession();

    }

    public OfficePersistence getOfficePersistence() {
        return officePersistence;
    }

    public CenterPersistence getCenterPersistence() {
        return centerPersistence;
    }

    public GroupPersistence getGroupPersistence() {
        return groupPersistence;
    }
}
