package org.mifos.application.meeting.util.helpers;

import java.util.Date;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.persistence.MeetingPersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class MeetingHelperTest extends MifosTestCase{
	private MeetingHelper helper = new MeetingHelper();
	
	public void testGetWeekMessage()throws Exception{
		String str ="Recur every 5 Week(s) on Monday";
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("5"), new Date(), MeetingType.CUSTOMERMEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(str, helper.getMessage(meeting, TestObjectFactory.getContext()));
	}
	
	public void testGetMonthMessage()throws Exception{
		String str ="Recur on First Monday of every 5 month(s)";
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, RankType.FIRST, Short.valueOf("5"), new Date(), MeetingType.CUSTOMERMEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(str, helper.getMessage(meeting, TestObjectFactory.getContext()));
	}

	public void testGetMonthlyOnDayMessage()throws Exception{
		String str ="Recur on day 7 of every 2 month(s)";
		MeetingBO meeting = new MeetingBO(Short.valueOf("7"), Short.valueOf("2"), new Date(), MeetingType.CUSTOMERMEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(str, helper.getMessage(meeting, TestObjectFactory.getContext()));
	}
	
	public void testGetWeekFrequency()throws Exception{
		String str ="5 week(s)";
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("5"), new Date(), MeetingType.CUSTOMERMEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(str, helper.getMessageWithFrequency(meeting, TestObjectFactory.getContext()));
	}
	
	public void testGetMonthFrequecny()throws Exception{
		String str ="5 month(s)";
		MeetingBO meeting = new MeetingBO(Short.valueOf("7"), Short.valueOf("5"), new Date(), MeetingType.CUSTOMERMEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(str, helper.getMessageWithFrequency(meeting, TestObjectFactory.getContext()));
	}
	
	public void testGetDetailWeekFrequency()throws Exception{
		String str ="Recur every 5 week(s)";
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, Short.valueOf("5"), new Date(), MeetingType.CUSTOMERMEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(str, helper.getDetailMessageWithFrequency(meeting, TestObjectFactory.getContext()));
	}
	
	public void testGetDetailMonthFrequecny()throws Exception{
		String str ="Recur every 5 month(s)";
		MeetingBO meeting = new MeetingBO(Short.valueOf("7"), Short.valueOf("5"), new Date(), MeetingType.CUSTOMERMEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertEquals(str, helper.getDetailMessageWithFrequency(meeting, TestObjectFactory.getContext()));
	}
}
