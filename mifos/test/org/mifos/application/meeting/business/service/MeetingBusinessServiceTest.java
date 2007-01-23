package org.mifos.application.meeting.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class MeetingBusinessServiceTest extends MifosTestCase{
	
	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testGetWeekDaysList() throws Exception{
		MeetingBusinessService service = new MeetingBusinessService();
		List<WeekDaysEntity> weekDaysList = service.getWorkingDays(Short.valueOf("1"));
		assertNotNull(weekDaysList);
		assertEquals(6, weekDaysList.size());
	}
	
	public void testFailureGetWeekDaysList() throws Exception{
		MeetingBusinessService service = new MeetingBusinessService();
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getWorkingDays((short) 1);
		} catch (ServiceException e) {
			assertTrue(true);
		}
	}
	
	public void testGetMeeting() throws Exception{
		MeetingBusinessService service = new MeetingBusinessService();
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, 
			new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		meeting = service.getMeeting(meeting.getMeetingId());
		assertNotNull(meeting);
		assertEquals(Short.valueOf("5"), meeting.getMeetingDetails().getRecurAfter());
		assertEquals(WeekDay.MONDAY, meeting.getMeetingDetails().getWeekDay());
	}
	
	public void testGetMeetingForInvalidConnection() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, 
				new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
			meeting.save();
			fail();
		} catch (MeetingException e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}
	
	public void testFailureGetMeeting() throws Exception {
		MeetingBusinessService service = new MeetingBusinessService();
		MeetingBO meeting = new MeetingBO(WeekDay.MONDAY, (short) 5, 
			new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		meeting.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			meeting = service.getMeeting(meeting.getMeetingId());
			fail();
		} catch (ServiceException e) {
			assertEquals("exception.framework.ApplicationException", e.getKey());
		}
	}

}
