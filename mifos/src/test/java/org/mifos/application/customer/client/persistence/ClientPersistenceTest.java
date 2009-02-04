/**

* ClientPersistenceTest.java version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.application.customer.client.persistence;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.mifos.application.customer.center.CenterTemplate;
import org.mifos.application.customer.center.CenterTemplateImpl;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.persistence.CenterPersistence;
import org.mifos.application.customer.client.ClientTemplate;
import org.mifos.application.customer.client.ClientTemplateImpl;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.GroupTemplate;
import org.mifos.application.customer.group.GroupTemplateImpl;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.persistence.GroupPersistence;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.MeetingTemplateImpl;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.exceptions.MeetingException;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeTemplate;
import org.mifos.application.office.business.OfficeTemplateImpl;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.ApplicableTo;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ValidationException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientPersistenceTest extends MifosTestCase {
	private static final Integer UNKNOWN_CUSTOMER_ID = Integer.valueOf(0);

	private static final String DEFAULT_CLIENT_NAME = "Active Client";

	private static final Date DEFAULT_DOB = DateUtils.getDate(1983, Calendar.JANUARY, 1);

	private static final String GOVT_ID = "1234";

	private SavingsOfferingBO savingsOffering1;

	private SavingsOfferingBO savingsOffering2;

	private SavingsOfferingBO savingsOffering3;

	private SavingsOfferingBO savingsOffering4;
    private OfficePersistence officePersistence;
    private CenterPersistence centerPersistence;
    private GroupPersistence groupPersistence;
    private ClientPersistence clientPersistence;

	private MeetingBO meeting;

	private CenterBO center;

	private GroupBO group;

	private ClientBO client;

	private ClientBO client1;

	private ClientBO localTestClient;

	private ClientBO clientWithSameGovtId;

    @Override
	protected void setUp() throws Exception {
    	super.setUp();
        this.officePersistence = new OfficePersistence();
        this.centerPersistence = new CenterPersistence();
        this.groupPersistence = new GroupPersistence();
        this.clientPersistence = new ClientPersistence();
        initializeStatisticsService();
    }

    @Override
	protected void tearDown() throws Exception {
    	super.tearDown();
		TestObjectFactory.removeObject(savingsOffering1);
		TestObjectFactory.removeObject(savingsOffering2);
		TestObjectFactory.removeObject(savingsOffering3);
		TestObjectFactory.removeObject(savingsOffering4);
		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(localTestClient );
		TestObjectFactory.cleanUp(clientWithSameGovtId );
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
		HibernateUtil.closeSession();
	}

    public void testCreateClient()
            throws PersistenceException, OfficeException,
            MeetingException, CustomerException, ValidationException {
        long transactionCount = getStatisticsService().getSuccessfulTransactionCount();
        try {
            UserContext userContext = TestUtils.makeUser();

            OfficeTemplate template =
                    OfficeTemplateImpl.createNonUniqueOfficeTemplate(OfficeLevel.BRANCHOFFICE);
            OfficeBO office = getOfficePersistence().createOffice(userContext, template);

            MeetingBO meeting = new MeetingBO(MeetingTemplateImpl.createWeeklyMeetingTemplate());

            CenterTemplate centerTemplate = new CenterTemplateImpl(meeting, office.getOfficeId());
            CenterBO center = getCenterPersistence().createCenter(userContext, centerTemplate);

            GroupTemplate groupTemplate = GroupTemplateImpl.createNonUniqueGroupTemplate(center.getCustomerId());
            GroupBO group = getGroupPersistence().createGroup(userContext, groupTemplate);

            ClientTemplate clientTemplate = ClientTemplateImpl.createActiveGroupClientTemplate(
                    office.getOfficeId(), group.getCustomerId());
            ClientBO client = getClientPersistence().createClient(userContext, clientTemplate);

            assertNotNull(client.getCustomerId());
            assertTrue(client.isActive());
        }
        finally {
            HibernateUtil.rollbackTransaction();
        }
        assertTrue(transactionCount == getStatisticsService().getSuccessfulTransactionCount());
    }

    public void testCreateClientInvalidParentCustomer() throws PersistenceException, CustomerException {
        try {
            UserContext userContext = TestUtils.makeUser();
            ClientTemplate clientTemplate = ClientTemplateImpl.createActiveGroupClientTemplate(
                    (short)1, -11);
            try {
                ClientBO client = getClientPersistence().createClient(userContext, clientTemplate);
                fail("should not have been able to create client " + client.getDisplayName());
            } catch (ValidationException e) {
                assertTrue(e.getMessage().equals(CustomerConstants.INVALID_PARENT));
            }
        }
        finally {
            HibernateUtil.rollbackTransaction();
        }
    }

    public void testRetrieveOfferingsApplicableToClient() throws Exception {
		Date currentTimestamp = new Date(System.currentTimeMillis());
		savingsOffering1 = TestObjectFactory.createSavingsProduct("Offering1",
				"s1", SavingsType.MANDATORY, ApplicableTo.CLIENTS, currentTimestamp);
		savingsOffering2 = TestObjectFactory.createSavingsProduct("Offering2",
				"s2", SavingsType.VOLUNTARY, ApplicableTo.CLIENTS, currentTimestamp);
		savingsOffering3 = TestObjectFactory.createSavingsProduct("Offering3",
				"s3", SavingsType.MANDATORY, ApplicableTo.GROUPS, currentTimestamp);
		savingsOffering4 = TestObjectFactory.createSavingsProduct("Offering4",
				"s4", SavingsType.VOLUNTARY, ApplicableTo.CENTERS, currentTimestamp);
		HibernateUtil.closeSession();
		List<SavingsOfferingBO> offerings = new ClientPersistence()
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

	public void testGetActiveClientsUnderParent() throws PersistenceException {
		setUpClients();
		List<ClientBO> clients = new ClientPersistence()
				.getActiveClientsUnderParent(center.getSearchId(), center
						.getOffice().getOfficeId());
		assertEquals(2, clients.size());
	}

	private void setUpClients() {
		meeting = TestObjectFactory.createMeeting(TestObjectFactory
						.getTypicalMeeting());
		center = TestObjectFactory.createCenter("Center", meeting);
		group = TestObjectFactory.createGroupUnderCenter("Group",
						CustomerStatus.GROUP_ACTIVE, center);
		client = TestObjectFactory.createClient("Client",
						CustomerStatus.CLIENT_ACTIVE, group);
		client1 = TestObjectFactory.createClient("Client Two",
						CustomerStatus.CLIENT_ACTIVE, group);
	}

	public void testShouldThrowExceptionWhenTryingToCreateACustomerWithSameGovtId() throws Exception {
		setUpClients();
		localTestClient = createActiveClientWithGovtId();
		try {
			TestObjectFactory.createClient("Duplicate Client", CustomerStatus.CLIENT_ACTIVE, group, null, GOVT_ID, new Date(1222333444000L));
			fail("Should have thrown exception on creating a duplicate client with same government id");
		}
		catch (RuntimeException e) {
			assertEquals(CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION, ((CustomerException)e.getCause()).getKey());
		}
	}
	
	public void testShouldNotThrowExceptionWhenTryingToCreateACustomerWithSameGovtIdWhenOldIsInClosedState() throws Exception {
		setUpClients();
		localTestClient = createClosedClientWithGovtId();
		try {
			clientWithSameGovtId = TestObjectFactory.createClient("Duplicate Client", CustomerStatus.CLIENT_ACTIVE, group, null, GOVT_ID, new Date(1222333444000L));
		}
		catch (RuntimeException e) {
			fail("Should not have thrown exception on creating a duplicate client with same government id when old is closed");
		}
	}
	
	public void testShouldNotThrowExceptionWhenTryingToUpdateClientGovtIdToGovtIdOfAClosedClient() throws Exception {
		setUpClients();
		localTestClient = createClosedClientWithGovtId();
		clientWithSameGovtId = TestObjectFactory.createClient("Duplicate Client", CustomerStatus.CLIENT_ACTIVE, group);
		try {
			clientWithSameGovtId.updatePersonalInfo("Duplicate Client", GOVT_ID, 
			        DateUtils.getDate(1980, Calendar.JANUARY, 1), new CustomerPersistence());
			HibernateUtil.commitTransaction();
		}
		catch (RuntimeException e) {
			fail("Should not throw error when updating to a government id of closed client");
		}
	}
	
	public void testShouldThrowExceptionWhenTryingToUpdateClientGovtIdToGovtIdOfAnActiveClient() throws Exception {
		setUpClients();
		localTestClient = createActiveClientWithGovtId();
		clientWithSameGovtId = TestObjectFactory.createClient("Duplicate Client", CustomerStatus.CLIENT_ACTIVE, group);
		try {
			clientWithSameGovtId.updatePersonalInfo("Duplicate Client", GOVT_ID, 
			        DateUtils.getDate(1980, Calendar.JANUARY, 1), new CustomerPersistence());
			HibernateUtil.commitTransaction();
			fail("Should throw error when updating to a government id of active client");
		}
		catch (CustomerException e) {
			assertEquals(CustomerConstants.DUPLICATE_GOVT_ID_EXCEPTION, e.getKey());
		}
	}

	public void testShouldReturnTrueForDuplicacyCheckOnGovtIdIfClientWithGovtIdPresentInClosedState() throws Exception {
		setUpClients();
		localTestClient = createClosedClientWithGovtId();
		assertTrue(clientPersistence.checkForDuplicacyOnGovtIdForClosedClients(GOVT_ID));
	}

	public void testShouldReturnFalseForDuplicayCheckOnGovtIdIfClientWithGovtIdPresentInActiveState() throws Exception {
		setUpClients();
		localTestClient = createActiveClientWithGovtId();
		assertFalse(clientPersistence.checkForDuplicacyOnGovtIdForClosedClients(GOVT_ID));
	}
	
	public void testShouldReturnFalseForDuplicacyCheckOnGovtIdIfClientWithNullGovtIdInClosedState() throws Exception {
		setUpClients();
		localTestClient = createClosedClient("Closed Client", DateUtils.getDate(1983, Calendar.JANUARY, 1), null);
		assertFalse(clientPersistence.checkForDuplicacyOnGovtIdForClosedClients(null));
	}
	
	public void testShouldReturnFalseForDuplicacyCheckOnGovtIdIfClientWithEmptyGovtIdInClosedState() throws Exception {
		setUpClients();
		localTestClient = createClosedClient("Closed Client", DateUtils.getDate(1983, Calendar.JANUARY, 1), StringUtils.EMPTY);
		assertFalse(clientPersistence.checkForDuplicacyOnGovtIdForClosedClients(StringUtils.EMPTY));
	}
	
	public void testShouldReturnFalseForDuplicacyCheckOnGovtIdForNonClosedClientIfGovtIdIsNull()
			throws Exception {
		setUpClients();
		localTestClient = createActiveClient(DEFAULT_CLIENT_NAME, new Date(1222333444000L), null);
		assertFalse(clientPersistence
				.checkForDuplicacyOnGovtIdForNonClosedClients(null,
						localTestClient.getCustomerId()));
	}
	
	public void testShouldReturnFalseForDuplicacyCheckOnGovtIdForNonClosedClientIfGovtIdIsEmpty()
			throws Exception {
		setUpClients();
		localTestClient = createActiveClientWithGovtId();
		assertFalse(clientPersistence
				.checkForDuplicacyOnGovtIdForNonClosedClients(
						StringUtils.EMPTY, localTestClient.getCustomerId()));
	}
	
	public void testShouldReturnTrueForDuplicacyCheckOnNameIfPrevClientInNonClosedState()
			throws Exception {
		setUpClients();
		localTestClient = createActiveClientWithNameAndDob(DEFAULT_CLIENT_NAME,
				DEFAULT_DOB);
		assertTrue(clientPersistence
				.checkForDuplicacyForNonClosedClientsOnNameAndDob(
						DEFAULT_CLIENT_NAME, DEFAULT_DOB,UNKNOWN_CUSTOMER_ID));
	}
	
	public void testShouldReturnFalseForDuplicacyCheckOnNameIfPrevClientInClosedState()
			throws Exception {
		setUpClients();
		localTestClient = createCloseClientWithNameAndDob(DEFAULT_CLIENT_NAME,
				DEFAULT_DOB);
		assertFalse(clientPersistence
				.checkForDuplicacyForNonClosedClientsOnNameAndDob(
						DEFAULT_CLIENT_NAME, DEFAULT_DOB, UNKNOWN_CUSTOMER_ID));
	}
	
	public void testShouldReturnTrueIfClientWithSameNameAndDobInClosedState()
			throws Exception {
		setUpClients();
		localTestClient = createCloseClientWithNameAndDob(DEFAULT_CLIENT_NAME,
				DEFAULT_DOB);
		assertTrue(clientPersistence
				.checkForDuplicacyForClosedClientsOnNameAndDob(
						DEFAULT_CLIENT_NAME, DEFAULT_DOB));
	}
	
	private ClientBO createCloseClientWithNameAndDob(String clientName, Date dob) {
		return createClosedClient(clientName, dob, GOVT_ID);
	}

	private ClientBO createActiveClientWithNameAndDob(String displayName, Date dateOfBirth) {
		return createActiveClient(displayName, dateOfBirth, GOVT_ID);
	}

	private ClientBO createActiveClientWithGovtId() {
		return createActiveClient(DEFAULT_CLIENT_NAME, DEFAULT_DOB, GOVT_ID);
	}

	private ClientBO createActiveClient(String displayName, Date dateOfBirth, String governmentId) {
		return TestObjectFactory.createClient(displayName,
				CustomerStatus.CLIENT_ACTIVE, group, null, governmentId,
				dateOfBirth);
	}
	
	private ClientBO createClosedClientWithGovtId() {
		return createClosedClient("Closed Client", DateUtils.getDate(1983, Calendar.JANUARY, 1), GOVT_ID);
	}

	private ClientBO createClosedClient(String displayName, Date dateOfBirth, String governmentId) {
		return TestObjectFactory.createClient(displayName,
				CustomerStatus.CLIENT_CLOSED, group, null, governmentId,
				dateOfBirth);
	}

	public OfficePersistence getOfficePersistence() {
        return officePersistence;
    }

    public CenterPersistence getCenterPersistence() {
        return centerPersistence;
    }

    public GroupPersistence getGroupPersistence() {
        return groupPersistence;
    }

    public ClientPersistence getClientPersistence() {
        return clientPersistence;
    }
}
