package org.mifos.application.meeting.persistence;

import java.util.List;

import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.framework.MifosTestCase;

public class MeetingPersistenceTest extends MifosTestCase{

	public void testGetWeekDaysList() throws Exception{
		List<WeekDaysEntity> weekDaysList = new MeetingPersistence().getWorkingDays(Short.valueOf("1"));
		assertNotNull(weekDaysList);
		assertEquals(Integer.valueOf("6").intValue(),weekDaysList.size());
	}
}
