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

package org.mifos.application.meeting.util.helpers;

import java.util.Date;

import junit.framework.Assert;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class MeetingHelperIntegrationTest extends MifosIntegrationTestCase {
    public MeetingHelperIntegrationTest() throws Exception {
        super();
    }

    private MeetingHelper helper = new MeetingHelper();

    public void testGetWeekMessage() throws Exception {
        String expected = "Recur every 5 Week(s) on Monday";
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
       Assert.assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser()));
    }

    public void testNoSave() throws Exception {
        String expected = "Recur every 5 Week(s) on Monday";
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
       Assert.assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser()));
    }

    public void testGetMonthMessage() throws Exception {
        String expected = "Recur on First Monday of every 5 month(s)";
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, RankType.FIRST, (short) 5, new Date(),
                MeetingType.CUSTOMER_MEETING, "Delhi");
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
       Assert.assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser()));
    }

    public void testGetMonthlyOnDayMessage() throws Exception {
        String expected = "Recur on day 7 of every 2 month(s)";
        MeetingBO meeting = new MeetingBO((short) 7, (short) 2, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
       Assert.assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser()));
    }

    public void testGetWeekFrequency() throws Exception {
        String expected = "5 week(s)";
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
       Assert.assertEquals(expected, helper.getMessageWithFrequency(meeting, TestUtils.makeUser()));
    }

    public void testGetMonthFrequecny() throws Exception {
        String expected = "5 month(s)";
        MeetingBO meeting = new MeetingBO((short) 7, (short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
       Assert.assertEquals(expected, helper.getMessageWithFrequency(meeting, TestUtils.makeUser()));
    }

    public void testGetDetailWeekFrequency() throws Exception {
        String expected = "Recur every 5 week(s)";
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
       Assert.assertEquals(expected, helper.getDetailMessageWithFrequency(meeting, TestUtils.makeUser()));
    }

    public void testGetDetailMonthFrequecny() throws Exception {
        String expected = "Recur every 5 month(s)";
        MeetingBO meeting = new MeetingBO((short) 7, (short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
        meeting.save();
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
       Assert.assertEquals(expected, helper.getDetailMessageWithFrequency(meeting, TestUtils.makeUser()));
    }

    public void testGetUpdatedMeetingScheduledWeekMessage() throws Exception {
        String expected = "(Meetings will be changed to recur every 5 Week(s) on Monday after the batch jobs run)";
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
       Assert.assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser(), true));
    }

    public void testGetUpdatedMeetingScheduledMonthMessage() throws Exception {
        String expected = "(Meetings will be changed to recur on First Monday of every 5 month(s) after the batch jobs run)";
        MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, RankType.FIRST, (short) 5, new Date(),
                MeetingType.CUSTOMER_MEETING, "Delhi");
       Assert.assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser(), true));
    }

    public void testGetUpdatedMeetingScheduledMonthlyOnDayMessage() throws Exception {
        String expected = "(Meetings will be changed to recur on day 7 of every 2 month(s) after the batch jobs run)";
        MeetingBO meeting = new MeetingBO((short) 7, (short) 2, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
       Assert.assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser(), true));
    }

}
