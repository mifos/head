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

package org.mifos.customers.center.persistence;

import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.office.persistence.OfficePersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CenterPersistenceIntegrationTest extends MifosIntegrationTestCase {

    private CustomerBO center;
    private OfficePersistence officePersistence;
    private CenterPersistence centerPersistence;

    @Before
    public void setUp() throws Exception {
        officePersistence = new OfficePersistence();
        centerPersistence = new CenterPersistence();
        initializeStatisticsService();
    }

    @After
    public void tearDown() throws Exception {
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
    }

    @Test
    public void testGetCenter() throws Exception {
        String centerName = "NewCenter";
        center = TestObjectFactory.createWeeklyFeeCenter(centerName, getMeeting());
        center = new CenterPersistence().getCenter(center.getCustomerId());
        Assert.assertEquals(centerName, center.getDisplayName());
    }

    private MeetingBO getMeeting() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        meeting.setMeetingStartDate(new Date());
        return meeting;
    }

    @Test
    public void testSearch() throws Exception {
        String centerName = "NewCenter";
        center = TestObjectFactory.createWeeklyFeeCenter(centerName, getMeeting());
        QueryResult queryResult = new CenterPersistence().search(center.getDisplayName(), Short.valueOf("1"));
        Assert.assertNotNull(queryResult);
        Assert.assertEquals(1, queryResult.getSize());
        Assert.assertEquals(1, queryResult.get(0, 10).size());
    }

    OfficePersistence getOfficePersistence() {
        return officePersistence;
    }

    CenterPersistence getCenterPersistence() {
        return centerPersistence;
    }
}
