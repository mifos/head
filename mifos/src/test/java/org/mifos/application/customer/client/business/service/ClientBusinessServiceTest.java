/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.customer.client.business.service;

import java.util.Date;
import java.util.List;

import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientBusinessServiceTest extends MifosIntegrationTest {

	public ClientBusinessServiceTest() throws SystemException, ApplicationException {
        super();
    }

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
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testGetClient() throws Exception {
		client = createClient("abc");
		StaticHibernateUtil.closeSession();
		client = service.getClient(client.getCustomerId());
		assertNotNull(client);
		assertEquals("abc", client.getClientName().getName().getFirstName());
		assertEquals("abc", client.getClientName().getName().getLastName());
	}

	public void testFailureGetClient() throws Exception {
		client = createClient("abc");
		StaticHibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			client = service.getClient(client.getCustomerId());
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		StaticHibernateUtil.closeSession();
	}

	public void testFailureRetrieveOfferings() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsProduct("Offering1",
				"s1", SavingsType.MANDATORY, ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
		StaticHibernateUtil.closeSession();
		TestObjectFactory.simulateInvalidConnection();
		try {
			service.retrieveOfferingsApplicableToClient();
			assertTrue(false);
		} catch (ServiceException e) {
			assertTrue(true);
		}
		StaticHibernateUtil.closeSession();
	}

	public void testRetrieveOfferingsApplicableToClient() throws Exception {
		savingsOffering1 = TestObjectFactory.createSavingsProduct("Offering1",
				"s1", SavingsType.MANDATORY, ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
		savingsOffering2 = TestObjectFactory.createSavingsProduct("Offering2",
				"s2", SavingsType.VOLUNTARY, ApplicableTo.CLIENTS, new Date(System.currentTimeMillis()));
		savingsOffering3 = TestObjectFactory.createSavingsProduct("Offering3",
				"s3", SavingsType.MANDATORY, ApplicableTo.GROUPS, new Date(System.currentTimeMillis()));
		savingsOffering4 = TestObjectFactory.createSavingsProduct("Offering4",
				"s4", SavingsType.VOLUNTARY, ApplicableTo.CENTERS, new Date(System.currentTimeMillis()));
		StaticHibernateUtil.closeSession();
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
		StaticHibernateUtil.closeSession();
	}

	
	
	public void testGetActiveClientsUnderGroup() throws ServiceException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		CenterBO center = TestObjectFactory.createCenter("Center", meeting);
		GroupBO group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		ClientBO client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client1 = TestObjectFactory.createClient("Client Two",
				CustomerStatus.CLIENT_ACTIVE, group);
		List<ClientBO> clients = new ClientBusinessService()
				.getActiveClientsUnderGroup(group.getCustomerId());
		assertEquals(2, clients.size());

		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
	}


	public void testGetActiveClientsUnderParent() throws ServiceException {
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
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
				.getTypicalMeeting());
		CenterBO center = TestObjectFactory.createCenter("Center", meeting);
		GroupBO group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		ClientBO client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client1 = TestObjectFactory.createClient("Client Two",
				CustomerStatus.CLIENT_ACTIVE, group);
		TestObjectFactory.simulateInvalidConnection();

		try {
			new ClientBusinessService()
					.getActiveClientsUnderParent(center.getSearchId(), center
							.getOffice().getOfficeId());
			fail();
		} catch (ServiceException e) {
			assertEquals("exception.framework.ApplicationException", e.getKey());
		}
		StaticHibernateUtil.closeSession();
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
