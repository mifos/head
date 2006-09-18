package org.mifos.application.meeting.business.service;

import java.util.List;

import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.util.helpers.BusinessServiceName;

public class MeetingBusinessServiceTest extends MifosTestCase{

	public void testGetWeekDaysList() throws Exception{
		MeetingBusinessService service = (MeetingBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Meeting);
		List<WeekDaysEntity> weekDaysList = service.getWorkingDays(Short.valueOf("1"));
		assertNotNull(weekDaysList);
		assertEquals(Integer.valueOf("6").intValue(),weekDaysList.size());
	}
}
