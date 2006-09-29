package org.mifos.application.customer.group.persistence;

import java.sql.Date;

import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupPersistenceTest extends MifosTestCase {
	private MeetingBO meeting;

	private CustomerBO center;

	private CustomerBO group;

	private GroupPersistence groupPersistence = new GroupPersistence();

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() throws Exception {
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetGroupBySystemId() throws PersistenceException{
		createGroup();
		group = (GroupBO) groupPersistence.getGroupBySystemId(group.getGlobalCustNum());
		assertEquals("Group_Active_test", group.getDisplayName());
	}

	
	public void testSearch() throws Exception{
		createGroup();
		QueryResult queryResult = groupPersistence.search(group.getDisplayName(),group.getOffice().getOfficeId(),Short.valueOf("1"),Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());
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
	private void createGroup(){
		center = createCenter();
		group = TestObjectFactory.createGroup("Group_Active_test", Short
				.valueOf("9"), "1.1.1", center, new Date(System
				.currentTimeMillis()));
		HibernateUtil.closeSession();

		
	}
}
