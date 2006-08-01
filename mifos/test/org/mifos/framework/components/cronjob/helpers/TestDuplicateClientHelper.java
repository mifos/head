package org.mifos.framework.components.cronjob.helpers;

import java.sql.Date;

import org.mifos.framework.MifosTestCase;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.components.cronjobs.helpers.DuplicateClientHelper;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.TestObjectPersistence;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestDuplicateClientHelper extends MifosTestCase{

	CenterBO center;
	
	ClientBO client1;
	
	ClientBO client2;
	
	ClientBO client3;
	
	ClientBO client4;
	
	GroupBO group;
	
	MeetingBO meeting;

	private static TestObjectPersistence testObjectPersistence = new TestObjectPersistence();
	
	private DuplicateClientHelper duplicateClientHelper=null;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		duplicateClientHelper = new DuplicateClientHelper();
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingHelper(1, 1, 4, 2));
		Date startDate = new Date(System.currentTimeMillis());
		center = TestObjectFactory.createCenter("Center", Short.valueOf("13"),
				"1.1", meeting, startDate);
		group = TestObjectFactory.createGroup("Group",GroupConstants.ACTIVE,"1.1.1",center,startDate);
		client1 = TestObjectFactory.createClient("Client", ClientConstants.STATUS_ACTIVE,
				"1.1.1.1", group, startDate);
		client2 = TestObjectFactory.createClient("Client", ClientConstants.STATUS_ACTIVE,
				"1.1.1.1", group, startDate);
		client3 = TestObjectFactory.createClient("Client3", ClientConstants.STATUS_ACTIVE,
				"1.1.1.1", group, startDate);
		client4 = TestObjectFactory.createClient("Client4", ClientConstants.STATUS_ACTIVE,
				"1.1.1.1", group, startDate);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		duplicateClientHelper=null;
		testObjectPersistence=null;
		TestObjectFactory.cleanUp(client4);
		TestObjectFactory.cleanUp(client3);
		TestObjectFactory.cleanUp(client2);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}
	
	public void testexecute() throws Exception{
		Date dob = new Date(System.currentTimeMillis());
		client1.setDateOfBirth(dob);
		client2.setDateOfBirth(dob);
		client1.setGovernmentId(null);
		client2.setGovernmentId(null);
		testObjectPersistence.update(client1);
		testObjectPersistence.update(client2);
		testObjectPersistence.flushandCloseSession();
		duplicateClientHelper.execute(System.currentTimeMillis());
		
		client1=(ClientBO)testObjectPersistence.getObject(ClientBO.class,client1.getCustomerId());
		client2=(ClientBO)testObjectPersistence.getObject(ClientBO.class,client2.getCustomerId());
		assertEquals(Short.valueOf(ClientConstants.STATUS_CLOSED),client1.getCustomerStatus().getId());
		assertEquals(Short.valueOf(ClientConstants.STATUS_CLOSED),client2.getCustomerStatus().getId());
		
		client3.setGovernmentId("1");
		client4.setGovernmentId("1");
		testObjectPersistence.update(client3);
		testObjectPersistence.update(client4);
		testObjectPersistence.flushandCloseSession();
		duplicateClientHelper.execute(System.currentTimeMillis());
		client3=(ClientBO)testObjectPersistence.getObject(ClientBO.class,client3.getCustomerId());
		client4=(ClientBO)testObjectPersistence.getObject(ClientBO.class,client4.getCustomerId());

		assertEquals(Short.valueOf(ClientConstants.STATUS_CLOSED),client3.getCustomerStatus().getId());
		assertEquals(Short.valueOf(ClientConstants.STATUS_CLOSED),client4.getCustomerStatus().getId());
		group=(GroupBO)testObjectPersistence.getObject(GroupBO.class,group.getCustomerId());
		center = (CenterBO)testObjectPersistence.getObject(CenterBO.class,center.getCustomerId());
	}
	
}
