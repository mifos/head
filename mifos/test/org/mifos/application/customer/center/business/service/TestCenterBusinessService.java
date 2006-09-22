package org.mifos.application.customer.center.business.service;

import java.util.Date;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestCenterBusinessService extends MifosTestCase {
	private CustomerBO center;

	private CenterBusinessService service;
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = (CenterBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Center);
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}
	
	public void testGetCenter() throws Exception {
		center = createCenter("center1");
		assertNotNull(new CenterBusinessService().getCenter(center
				.getCustomerId()));
	}

	private CenterBO createCenter(String name) {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createCenter(name, Short.valueOf("13"), "1.4",
				meeting, new Date(System.currentTimeMillis()));
	}
	
	public void testSuccessfulGet() throws Exception {
		center = createCenter("Center1");
		HibernateUtil.closeSession();
		center = service.getCenter(center.getCustomerId());
		assertNotNull(center);
		assertEquals("Center1", center.getDisplayName());
	}

	public void testFailureGet() throws Exception {
		center = createCenter("Center1");
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.getCenter(center.getCustomerId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}
}
