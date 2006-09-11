package org.mifos.application.customer.group.business.service;

import java.sql.Date;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupBusinessServiceTest extends MifosTestCase {
	private MeetingBO meeting;

	private CustomerBO center;

	private CustomerBO group;

	private GroupBusinessService groupBusinessService;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		groupBusinessService = (GroupBusinessService) ServiceFactory
				.getInstance().getBusinessService(BusinessServiceName.Group);
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetGroupBySystemId() throws ServiceException{
		center = createCenter();
		String groupName = "Group_Active_test";
		group = TestObjectFactory.createGroup(groupName, Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		HibernateUtil.closeSession();
		group = (GroupBO) groupBusinessService.getGroupBySystemId(group.getGlobalCustNum());
		assertEquals(groupName, group.getDisplayName());
	}

	private CenterBO createCenter() {
		return createCenter("Center_Active_test");
	}

	private CenterBO createCenter(String name) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		return TestObjectFactory.createCenter(name, Short.valueOf("13"), "1.4",
				meeting, new Date(System.currentTimeMillis()));
	}
}
