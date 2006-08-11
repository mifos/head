package org.mifos.application.customer.center.business.service;

import java.util.Date;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCenterBusinessService extends MifosTestCase {
	
	public void testGetCenter(){
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		CenterBO center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, new Date(System.currentTimeMillis()));
		
		assertNotNull(new CenterBusinessService().getCenter(center.getCustomerId()));
		
		TestObjectFactory.cleanUp(center);

		
	}

}
