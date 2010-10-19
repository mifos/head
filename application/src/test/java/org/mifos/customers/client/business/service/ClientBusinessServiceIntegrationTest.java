/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.customers.client.business.service;

import junit.framework.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

import java.util.List;

public class ClientBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    private SavingsOfferingBO savingsOffering1;

    private SavingsOfferingBO savingsOffering2;

    private SavingsOfferingBO savingsOffering3;

    private SavingsOfferingBO savingsOffering4;

    private ClientBusinessService service;

    private ClientBO client;

    @Before
    public void setUp() throws Exception {
        service = new ClientBusinessService();
    }

    @After
    public void tearDown() throws Exception {
        savingsOffering1 = null;
        TestObjectFactory.removeObject(savingsOffering2);
        TestObjectFactory.removeObject(savingsOffering3);
        TestObjectFactory.removeObject(savingsOffering4);
        client = null;
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testGetClient() throws Exception {
        client = createClient(this.getClass().getSimpleName() + " abc");
        StaticHibernateUtil.flushSession();
        client = service.getClient(client.getCustomerId());
        Assert.assertNotNull(client);
        Assert.assertEquals(this.getClass().getSimpleName() + " abc", client.getClientName().getName().getFirstName());
        Assert.assertEquals(this.getClass().getSimpleName() + " abc", client.getClientName().getName().getLastName());
    }

    @Test
    public void testGetActiveClientsUnderGroup() throws ServiceException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        CenterBO center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        GroupBO group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group",
                CustomerStatus.GROUP_ACTIVE, center);
        ClientBO client = TestObjectFactory.createClient(this.getClass().getSimpleName() + " Client",
                CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client1 = TestObjectFactory.createClient(this.getClass().getSimpleName() + " Client Two",
                CustomerStatus.CLIENT_ACTIVE, group);
        List<ClientBO> clients = new ClientBusinessService().getActiveClientsUnderGroup(group.getCustomerId());
        Assert.assertEquals(2, clients.size());

//        client = null;
//        TestObjectFactory.cleanUp(client1);
//        group = null;
//        center = null;
    }

    @Test
    public void testGetActiveClientsUnderParent() throws ServiceException {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        CenterBO center = TestObjectFactory.createWeeklyFeeCenter(this.getClass().getSimpleName() + " Center", meeting);
        GroupBO group = TestObjectFactory.createWeeklyFeeGroupUnderCenter(this.getClass().getSimpleName() + " Group",
                CustomerStatus.GROUP_ACTIVE, center);
        ClientBO client = TestObjectFactory.createClient(this.getClass().getSimpleName() + " Client",
                CustomerStatus.CLIENT_ACTIVE, group);
        ClientBO client1 = TestObjectFactory.createClient(this.getClass().getSimpleName() + " Client Two",
                CustomerStatus.CLIENT_ACTIVE, group);
        List<ClientBO> clients = new ClientBusinessService().getActiveClientsUnderParent(center.getSearchId(), center
                .getOffice().getOfficeId());
        Assert.assertEquals(2, clients.size());

        client = null;
//        TestObjectFactory.cleanUp(client1);
        group = null;
        center = null;
    }

    private ClientBO createClient(String clientName) {
        return TestObjectFactory.createClient(clientName, null, CustomerStatus.CLIENT_PARTIAL);
    }
}
