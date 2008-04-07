package org.mifos.application.meeting.util.helpers;

import java.util.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class MeetingHelperTest extends MifosTestCase{
	{
		TestUtils.initializeSpring();
	}
	
	private MeetingHelper helper = new MeetingHelper();
	
	public void testGetWeekMessage() throws Exception {
		String expected ="Recur every 5 Week(s) on Monday";
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, 
			new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser()));
	}
	
	public void testNoSave() throws Exception {
		String expected ="Recur every 5 Week(s) on Monday";
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, 
			new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser()));
	}
	
	public void testGetMonthMessage() throws Exception {
		String expected ="Recur on First Monday of every 5 month(s)";
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, RankType.FIRST, 
			(short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser()));
	}

	public void testGetMonthlyOnDayMessage() throws Exception {
		String expected ="Recur on day 7 of every 2 month(s)";
		MeetingBO meeting = new MeetingBO(
			(short) 7, (short) 2, new Date(), 
			MeetingType.CUSTOMER_MEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(expected, helper.getMessage(meeting, TestUtils.makeUser()));
	}
	
	public void testGetWeekFrequency() throws Exception {
		String expected ="5 week(s)";
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, 
			new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(expected, helper.getMessageWithFrequency(meeting, TestUtils.makeUser()));
	}
	
	public void testGetMonthFrequecny() throws Exception {
		String expected ="5 month(s)";
		MeetingBO meeting = new MeetingBO((short) 7, 
			(short) 5, new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(expected, helper.getMessageWithFrequency(meeting, TestUtils.makeUser()));
	}
	
	public void testGetDetailWeekFrequency() throws Exception {
		String expected ="Recur every 5 week(s)";
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, 
			new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(expected, helper.getDetailMessageWithFrequency(meeting, TestUtils.makeUser()));
	}
	
	public void testGetDetailMonthFrequecny() throws Exception {
		String expected ="Recur every 5 month(s)";
		MeetingBO meeting = new MeetingBO((short) 7, (short) 5, 
			new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(expected, helper.getDetailMessageWithFrequency(meeting, TestUtils.makeUser()));
	}

}
