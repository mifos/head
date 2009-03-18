package org.mifos.application.meeting.persistence;

import java.util.Date;
import java.util.List;

import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;

public class MeetingPersistenceTest extends MifosIntegrationTest{

	public MeetingPersistenceTest() throws SystemException, ApplicationException {
        super();
    }

    public void testGetWeekDaysList() throws Exception{
		List<WeekDay> weekDaysList = new MeetingPersistence().getWorkingDays();
		assertNotNull(weekDaysList);
		//assertEquals(Integer.valueOf("6").intValue(),weekDaysList.size());
		assertEquals(Integer.valueOf("7").intValue(),weekDaysList.size());
	}
	
	public void testGetMeeting() throws Exception{
		MeetingBO meeting  = new MeetingBO(WeekDay.MONDAY, Short.valueOf("5"), new Date(), MeetingType.CUSTOMER_MEETING, "Delhi");
		meeting.save();
		StaticHibernateUtil.commitTransaction();
		StaticHibernateUtil.closeSession();
		meeting = new MeetingPersistence().getMeeting(meeting.getMeetingId());
		assertNotNull(meeting);
		assertEquals(Short.valueOf("5"), meeting.getMeetingDetails().getRecurAfter());
		assertEquals(WeekDay.MONDAY, meeting.getMeetingDetails().getWeekDay());
	}
}
