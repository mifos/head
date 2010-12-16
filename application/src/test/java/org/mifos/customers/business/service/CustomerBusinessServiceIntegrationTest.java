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

package org.mifos.customers.business.service;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import java.math.BigDecimal;
import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.config.AccountingRulesConstants;
import org.mifos.config.ConfigurationManager;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.business.OfficecFixture;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class CustomerBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    private static final Integer THREE = Integer.valueOf(3);
    private static final Integer ONE = Integer.valueOf(1);
    private static final OfficeBO OFFICE = OfficecFixture.createOffice(Short.valueOf("1"));

    private CustomerBO center;
    private GroupBO group;
    private MeetingBO meeting;
    private final SavingsTestHelper helper = new SavingsTestHelper();
    private SavingsOfferingBO savingsOffering;
    private SavingsBO savingsBO;
    private CustomerBusinessService service;
    private CustomerPersistence customerPersistenceMock;
    private CustomerBusinessService customerBusinessServiceWithMock;

    @Before
    public void setUp() throws Exception {
        service = new CustomerBusinessService();
        customerPersistenceMock = createMock(CustomerPersistence.class);
        customerBusinessServiceWithMock = new CustomerBusinessService(customerPersistenceMock);
    }

    @After
    public void tearDown() throws Exception {
        try {
            // if there is an additional currency code defined, then clear it
            ConfigurationManager.getInstance().clearProperty(AccountingRulesConstants.ADDITIONAL_CURRENCY_CODES);
            savingsBO = null;
            group = null;
            center = null;
            StaticHibernateUtil.flushSession();
        } catch (Exception e) {
            // throwing here tends to mask other failures
            e.printStackTrace();
        }
    }

    @Test
    public void testFindBySystemId() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE,
                center);
        savingsBO = getSavingsAccount(group, "fsaf5", "ads5");
        StaticHibernateUtil.flushAndClearSession();
        group = (GroupBO) service.findBySystemId(group.getGlobalCustNum());
        Assert.assertEquals("Group_Active_test", group.getDisplayName());
        Assert.assertEquals(2, group.getAccounts().size());
        Assert.assertEquals(0, group.getOpenLoanAccounts().size());
        Assert.assertEquals(1, group.getOpenSavingAccounts().size());
        Assert.assertEquals(CustomerStatus.GROUP_ACTIVE, group.getStatus());
        StaticHibernateUtil.flushSession();
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
        group = TestObjectFactory.getGroup(group.getCustomerId());
    }

    @Test
    public void testSuccessfulGet() throws Exception {
        center = createCenter("MyCenter");
        savingsBO = getSavingsAccount(center, "fsaf5", "ads5");
        StaticHibernateUtil.flushAndClearSession();
        center = service.getCustomer(center.getCustomerId());
        Assert.assertNotNull(center);
        Assert.assertEquals("MyCenter", center.getDisplayName());
        Assert.assertEquals(2, center.getAccounts().size());
        Assert.assertEquals(0, center.getOpenLoanAccounts().size());
        Assert.assertEquals(1, center.getOpenSavingAccounts().size());
        Assert.assertEquals(CustomerStatus.CENTER_ACTIVE.getValue(), center.getCustomerStatus().getId());
        StaticHibernateUtil.flushSession();
        savingsBO = TestObjectFactory.getObject(SavingsBO.class, savingsBO.getAccountId());
        center = TestObjectFactory.getCenter(center.getCustomerId());
    }

    @Test
    public void testDropOutRate() throws Exception {
        expect(customerPersistenceMock.getDropOutClientsCountForOffice(OFFICE)).andReturn(ONE);
        expect(customerPersistenceMock.getActiveOrHoldClientCountForOffice(OFFICE)).andReturn(THREE);
        replay(customerPersistenceMock);
        BigDecimal dropOutRate = customerBusinessServiceWithMock.getClientDropOutRateForOffice(OFFICE);
        verify(customerPersistenceMock);
        Assert.assertEquals(25d, dropOutRate.doubleValue(), 0.001);
    }

    @Test
    public void testVeryPoorClientDropoutRate() throws Exception {
        expect(customerPersistenceMock.getVeryPoorDropOutClientsCountForOffice(OFFICE)).andReturn(ONE);
        expect(customerPersistenceMock.getVeryPoorActiveOrHoldClientCountForOffice(OFFICE)).andReturn(THREE);
        replay(customerPersistenceMock);
        BigDecimal veryPoorClientDropoutRateForOffice = customerBusinessServiceWithMock
                .getVeryPoorClientDropoutRateForOffice(OFFICE);
        Assert.assertEquals(25d, veryPoorClientDropoutRateForOffice.doubleValue(), 0.001);
        verify(customerPersistenceMock);
    }

    private CenterBO createCenter(String name) throws Exception {
        meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        return TestObjectFactory.createWeeklyFeeCenter(name, meeting);
    }

    private SavingsBO getSavingsAccount(CustomerBO customerBO, String offeringName, String shortName) throws Exception {
        savingsOffering = helper.createSavingsOffering(offeringName, shortName);
        return TestObjectFactory.createSavingsAccount("000100000000017", customerBO,
                AccountStates.SAVINGS_ACC_APPROVED, new Date(System.currentTimeMillis()), savingsOffering);
    }

}