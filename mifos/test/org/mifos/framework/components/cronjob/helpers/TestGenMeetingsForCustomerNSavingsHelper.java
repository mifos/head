package org.mifos.framework.components.cronjob.helpers;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.components.cronjobs.helpers.GenMeetingsForCustomerNSavingsHelper;
import org.mifos.framework.components.repaymentschedule.MeetingScheduleHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestGenMeetingsForCustomerNSavingsHelper extends MifosTestCase {
	CustomerBO center = null;

	public void testExecute() throws Exception {
			MeetingBO meeting = TestObjectFactory
					.createMeeting(TestObjectFactory.getMeetingHelper(1, 1, 4,
							2));
			Date startDate = new Date(System.currentTimeMillis());
			center = TestObjectFactory.createCenter("center1", Short
					.valueOf("13"), "1.4", meeting, startDate);
			new GenMeetingsForCustomerNSavingsHelper().execute(Calendar
					.getInstance().getTimeInMillis());
			TestObjectFactory.updateObject(center);
			TestObjectFactory.flushandCloseSession();
			center = (CustomerBO) HibernateUtil.getSessionTL().get(
					CustomerBO.class, center.getCustomerId());
			MeetingBO meetingBO = center.getCustomerMeeting().getMeeting();
			meetingBO.setMeetingStartDate(DateUtils
					.getFistDayOfNextYear(Calendar.getInstance()));
			List<Date> meetingDates = MeetingScheduleHelper.getSchedulerObject(
					meetingBO).getAllDates();
			Date date = center.getCustomerAccount().getAccountActionDate(
					Short.valueOf("4")).getActionDate();
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			Calendar calendar2 = Calendar.getInstance();
			calendar2.setTime(meetingDates.get(0));
			assertEquals(0, new GregorianCalendar(calendar.get(Calendar.YEAR),
					calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE),
					0, 0, 0).compareTo(new GregorianCalendar(calendar2
					.get(Calendar.YEAR), calendar2.get(Calendar.MONTH),
					calendar2.get(Calendar.DATE), 0, 0, 0)));


		TestObjectFactory.cleanUp(center);
	}

}
