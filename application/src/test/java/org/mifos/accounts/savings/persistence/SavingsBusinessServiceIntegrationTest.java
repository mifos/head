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

package org.mifos.accounts.savings.persistence;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.service.SavingsBusinessService;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.security.util.UserContext;

public class SavingsBusinessServiceIntegrationTest extends MifosIntegrationTestCase {

    private SavingsBusinessService service;
    private CustomerBO center;
    private CustomerBO group;
    private SavingsBO savings;
    private SavingsOfferingBO savingsOffering;
    private SavingsOfferingBO savingsOffering2;

    @Before
    public void setUp() throws Exception {
        service = new SavingsBusinessService();
    }

    @After
    public void tearDown() throws Exception {
        savings = null;
        group = null;
        center = null;
        TestObjectFactory.removeObject(savingsOffering2);
        StaticHibernateUtil.flushSession();
    }

    @Test
    public void testFindById() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", "kh6y", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("FFFF", savingsOffering, AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
        SavingsBO savings1 = service.findById(savings.getAccountId());
        Assert.assertNotNull(savings1);
    }

    @Test
    public void testFindBySystemId() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", "cadf", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("YYYY", savingsOffering, AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);
        SavingsBO savings1 = service.findBySystemId(savings.getGlobalAccountNum());

        Assert.assertEquals(savings.getAccountId(), savings1.getAccountId());
    }

    @Test
    public void testGetAllClosedAccounts() throws Exception {
        createInitialObjects();
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());

        Date startDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", ApplicableTo.GROUPS, new Date(System
                .currentTimeMillis()), PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 1.2,
                200.0, 200.0, SavingsType.VOLUNTARY, InterestCalcType.MINIMUM_BALANCE, meetingIntCalc, meetingIntPost);
        savings = TestObjectFactory.createSavingsAccount("432434", center, AccountState.SAVINGS_CLOSED.getValue(),
                startDate, savingsOffering);
        List<SavingsBO> savingsAccounts = service.getAllClosedAccounts(center.getCustomerId());
        Assert.assertEquals(1, savingsAccounts.size());
    }

    @Test
    public void testGetAllSavingsAccount() throws Exception {
        createInitialObjects();
        Date currentDate = new Date(System.currentTimeMillis());
        savingsOffering = TestObjectFactory.createSavingsProduct("SavingPrd1", "kh6y", currentDate,
                RecommendedAmountUnit.COMPLETE_GROUP);
        savings = createSavingsAccount("FFFF", savingsOffering, AccountStates.SAVINGS_ACC_PARTIALAPPLICATION);

        List<SavingsBO> savingsAccounts = service.getAllSavingsAccount();
        Assert.assertNotNull(savingsAccounts);
        Assert.assertEquals(1, savingsAccounts.size());
    }

    private void createInitialObjects() {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);
    }

    /**
     * Deprecated in favor of {@link #createSavingsAccount(String, SavingsOfferingBO, AccountState)}
     */
    private SavingsBO createSavingsAccount(String globalAccountNum, SavingsOfferingBO savingsOffering,
            short accountStateId) throws Exception {
        AccountState state = AccountState.fromShort(accountStateId);
        return createSavingsAccount(globalAccountNum, savingsOffering, state);
    }

    private SavingsBO createSavingsAccount(String globalAccountNum, SavingsOfferingBO savingsOffering,
            AccountState state) throws Exception {
        UserContext userContext = new UserContext(Locale.getDefault(), Short.valueOf("1"));
        userContext.setId(PersonnelConstants.SYSTEM_USER);
        userContext.setBranchGlobalNum("1001");
        return TestObjectFactory.createSavingsAccount(globalAccountNum, group, state, new Date(), savingsOffering,
                userContext);
    }
}
