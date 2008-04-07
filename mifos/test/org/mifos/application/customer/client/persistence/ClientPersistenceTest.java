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

import java.util.Date;
import java.util.List;

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
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ClientPersistenceTest extends MifosTestCase {
	private SavingsOfferingBO savingsOffering1;

	private SavingsOfferingBO savingsOffering2;

	private SavingsOfferingBO savingsOffering3;

	private SavingsOfferingBO savingsOffering4;
    private OfficePersistence officePersistence;
    private CenterPersistence centerPersistence;
    private GroupPersistence groupPersistence;
    private ClientPersistence clientPersistence;

    @Override
	protected void setUp() throws Exception {
        this.officePersistence = new OfficePersistence();
        this.centerPersistence = new CenterPersistence();
        this.groupPersistence = new GroupPersistence();
        this.clientPersistence = new ClientPersistence();
        initializeStatisticsService();
        super.setUp();
    }

    @Override
	protected void tearDown() throws Exception {
		TestObjectFactory.removeObject(savingsOffering1);
		TestObjectFactory.removeObject(savingsOffering2);
		TestObjectFactory.removeObject(savingsOffering3);
		TestObjectFactory.removeObject(savingsOffering4);
		HibernateUtil.closeSession();
		super.tearDown();
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
		MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory
				.getTypicalMeeting());
		CenterBO center = TestObjectFactory.createCenter("Center", meeting);
		GroupBO group = TestObjectFactory.createGroupUnderCenter("Group",
				CustomerStatus.GROUP_ACTIVE, center);
		ClientBO client = TestObjectFactory.createClient("Client",
				CustomerStatus.CLIENT_ACTIVE, group);
		ClientBO client1 = TestObjectFactory.createClient("Client Two",
				CustomerStatus.CLIENT_ACTIVE, group);
		List<ClientBO> clients = new ClientPersistence()
				.getActiveClientsUnderParent(center.getSearchId(), center
						.getOffice().getOfficeId());
		assertEquals(2, clients.size());

		TestObjectFactory.cleanUp(client);
		TestObjectFactory.cleanUp(client1);
		TestObjectFactory.cleanUp(group);
		TestObjectFactory.cleanUp(center);
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
