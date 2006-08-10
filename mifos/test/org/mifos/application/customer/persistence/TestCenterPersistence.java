package org.mifos.application.customer.persistence;

import java.util.Date;
import java.util.GregorianCalendar;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCenterPersistence extends MifosTestCase{
	private CustomerBO center;
	
	@Override
	public void tearDown() {
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}
	
	public void testIsCenterExists_true()throws Exception{
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,CustomerStatus.CENTER_ACTIVE.getValue(),"",getMeeting(),new Date());
		HibernateUtil.closeSession();
		assertTrue(new CenterPersistence().isCenterExists(centerName));
	}
	
	public void testIsCenterExists_false(){
		String centerName="NewCenter";
		center = TestObjectFactory.createCenter(centerName,CustomerStatus.CENTER_ACTIVE.getValue(),"",getMeeting(),new Date());
		HibernateUtil.closeSession();
		assertFalse(new CenterPersistence().isCenterExists("NewCenter11"));
	}
	
	private MeetingBO getMeeting() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		meeting.setMeetingStartDate(new GregorianCalendar());
		return meeting;
	}
}
