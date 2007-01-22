package org.mifos.application.customer.group.persistence;


import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class GroupPersistenceTest extends MifosTestCase {
	private MeetingBO meeting;

	private CustomerBO center;

	private GroupBO group;

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
		group = groupPersistence.findBySystemId(group.getGlobalCustNum());
		assertEquals("Group_Active_test", group.getDisplayName());
	}

	
	public void testSearch() throws Exception{
		createGroup();
		QueryResult queryResult = groupPersistence.search(group.getDisplayName(),Short.valueOf("1"));
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());
	}
	
	private CenterBO createCenter() {
		return createCenter("Center_Active_test");
	}

	private CenterBO createCenter(String name) {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingForToday(1, 1, 4, 2));
		return TestObjectFactory.createCenter(name, meeting);
	}
	private void createGroup(){
		center = createCenter();
		group = TestObjectFactory.createGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
		HibernateUtil.closeSession();

		
	}
}
