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

package org.mifos.application.meeting.business.service;

import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class MeetingBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    public MeetingBusinessServiceIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    @Override
    protected void tearDown() throws Exception {
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testGetWeekDaysList() throws Exception {
        MeetingBusinessService service = new MeetingBusinessService();
        List<WeekDay> weekDaysList = service.getWorkingDays();
        Assert.assertNotNull(weekDaysList);
       Assert.assertEquals(7, weekDaysList.size());
    }

    public void testGetMeeting() throws Exception {
        MeetingBusinessService service = new MeetingBusinessService();
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = service.getMeeting(meeting.getMeetingId());
        Assert.assertNotNull(meeting);
       Assert.assertEquals(Short.valueOf("5"), meeting.getMeetingDetails().getRecurAfter());
       Assert.assertEquals(WeekDay.MONDAY, meeting.getMeetingDetails().getWeekDay());
    }

    public void testGetMeetingForInvalidConnection() throws Exception {
        TestObjectFactory.simulateInvalidConnection();
        try {
            MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, new Date(), MeetingType.CUSTOMER_MEETING,
                    "Delhi");
            meeting.save();
            Assert.fail();
        } catch (MeetingException e) {
           Assert.assertTrue(true);
        } finally {
            StaticHibernateUtil.closeSession();
        }
    }

    public void testFailureGetMeeting() throws Exception {
        MeetingBusinessService service = new MeetingBusinessService();
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        TestObjectFactory.simulateInvalidConnection();
        try {
            meeting = service.getMeeting(meeting.getMeetingId());
            Assert.fail();
        } catch (ServiceException e) {
           Assert.assertEquals("exception.framework.ApplicationException", e.getKey());
        }
    }

}
