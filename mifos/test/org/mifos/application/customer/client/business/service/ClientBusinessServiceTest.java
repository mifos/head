package org.mifos.application.customer.client.business.service;

import java.util.List;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.PrdApplicableMaster;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientBusinessServiceTest extends MifosTestCase {

	private SavingsOfferingBO savingsOffering1;

	private SavingsOfferingBO savingsOffering2;

	private SavingsOfferingBO savingsOffering3;

	private SavingsOfferingBO savingsOffering4;

	private ClientBusinessService service;

	private ClientBO client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		service = (ClientBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Client);
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(savingsOffering1);
		TestObjectFactory.removeObject(savingsOffering2);
		TestObjectFactory.removeObject(savingsOffering3);
		TestObjectFactory.removeObject(savingsOffering4);
		TestObjectFactory.cleanUp(client);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetClient() throws Exception {
		client = createClient("abc");
		HibernateUtil.closeSession();
		client = service.getClient(client.getCustomerId());
		assertNotNull(client);
		assertEquals("abc", client.getClientName().getName().getFirstName());
		assertEquals("abc", client.getClientName().getName().getLastName());
	}

	public void testFailureGetClient() throws Exception {
		client = createClient("abc");
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			client = service.getClient(client.getCustomerId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	public void testFailureRetrieveOfferings() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsOffering("Offering1",
				"s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		HibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.retrieveOfferingsApplicableToClient();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	public void testRetrieveOfferingsApplicableToClient() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsOffering("Offering1",
				"s1", SavingsType.MANDATORY, PrdApplicableMaster.CLIENTS);
		savingsOffering2 = TestObjectFactory.createSavingsOffering("Offering2",
				"s2", SavingsType.VOLUNTARY, PrdApplicableMaster.CLIENTS);
		savingsOffering3 = TestObjectFactory.createSavingsOffering("Offering3",
				"s3", SavingsType.MANDATORY, PrdApplicableMaster.GROUPS);
		savingsOffering4 = TestObjectFactory.createSavingsOffering("Offering4",
				"s4", SavingsType.VOLUNTARY, PrdApplicableMaster.CENTERS);
		HibernateUtil.closeSession();
		List<SavingsOfferingBO> offerings = service
				.retrieveOfferingsApplicableToClient();
		assertEquals(2, offerings.size());
		for (SavingsOfferingBO offering : offerings) {
			if (offering.getPrdOfferingId().equals(
					savingsOffering1.getPrdOfferingId()))
				assertTrue(true);
			if (offering.getPrdOfferingId().equals(
					savingsOffering2.getPrdOfferingId()))
				assertTrue(true);
		}
		HibernateUtil.closeSession();
	}

	public void testGetActiveClientsUnderParent() throws ServiceException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingForToday(1, 1, 4, 2));
		CenterBO center = TestObjectFactory.createCenter("Center", meeting);
		GroupBO group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		ClientBO client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client1 = TestObjectFactory.createClient("Client Two",
				CustomerStatus.CLIENT_ACTIVE, group);
		List<ClientBO> clients = new ClientBusinessService()
				.getActiveClientsUnderParent(center.getSearchId(), center
						.getOffice().getOfficeId());
		assertEquals(2, clients.size());

		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
	}

	public void testGetActiveClientsUnderParentforInvalidConnection() {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getMeetingForToday(1, 1, 4, 2));
		CenterBO center = TestObjectFactory.createCenter("Center", meeting);
		GroupBO group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		ClientBO client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client1 = TestObjectFactory.createClient("Client Two",
				CustomerStatus.CLIENT_ACTIVE, group);
		TestObjectFactory.simulateInvalidConnection();

		try {
			List<ClientBO> clients = new ClientBusinessService()
					.getActiveClientsUnderParent(center.getSearchId(), center
							.getOffice().getOfficeId());
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
		}
		HibernateUtil.closeSession();
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
	}

	private ClientBO createClient(String clientName) {
		return TestObjectFactory.createClient(clientName, null,
				CustomerStatus.CLIENT_PARTIAL);
	}
}
