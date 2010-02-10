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

package org.mifos.accounts.savings.struts.tag;

import java.sql.Date;
import java.util.Locale;

import junit.framework.Assert;

import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsOverDueDepositsTagIntegrationTest extends MifosIntegrationTestCase {

    public SavingsOverDueDepositsTagIntegrationTest() throws Exception {
        super();
    }

    CenterBO center;
    GroupBO group;
    SavingsOfferingBO savingsOffering;
    SavingsBO savings;

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(savings);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testBuildDateUI() {
        Date date = new Date(System.currentTimeMillis());
       Assert.assertTrue(new SavingsOverDueDepositsTag().buildDateUI(new Locale("en", "GB"), date).toString().contains(
                DateUtils.getUserLocaleDate(new Locale("en", "GB"), date.toString())));

    }

    public void testBuildAmountUI() {
       Assert.assertTrue(new SavingsOverDueDepositsTag().buildAmountUI(new Money(getCurrency(), "1000")).toString().contains("1000"));

    }

    public void testBuildDepositDueUIRow() {
        Date date = new Date(System.currentTimeMillis());

        String outString = new SavingsOverDueDepositsTag().buildDepositDueUIRow(new Locale("en", "GB"), date,
                new Money(getCurrency(), "1000")).toString();
       Assert.assertTrue(outString.contains(DateUtils.getUserLocaleDate(new Locale("en", "GB"), date.toString())));

       Assert.assertTrue(outString.contains("1000"));
    }

    public void testbuildUI() throws Exception {
        createInitialObjects();
        Assert.assertNotNull(new SavingsOverDueDepositsTag().buildUI(savings.getDetailsOfInstallmentsInArrears(), new Locale(
                "en", "GB")));
    }

    private void createInitialObjects() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);

        SavingsTestHelper helper = new SavingsTestHelper();
        savingsOffering = helper.createSavingsOffering("2333dsf", "2132");
        savings = helper
                .createSavingsAccount(savingsOffering, group, AccountState.SAVINGS_ACTIVE, TestUtils.makeUser());

    }
}
