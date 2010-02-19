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

package org.mifos.framework.components.batchjobs.helpers;

import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_CALCULATION_TIME_PERIOD;
import static org.mifos.application.meeting.util.helpers.MeetingType.SAVINGS_INTEREST_POSTING;
import static org.mifos.application.meeting.util.helpers.RecurrenceType.MONTHLY;
import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.framework.util.helpers.TestObjectFactory.EVERY_SECOND_MONTH;

import java.util.Calendar;
import java.util.Date;

import junit.framework.Assert;

import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.savings.business.SavingBOTestUtils;
import org.mifos.accounts.savings.business.SavingsActivityEntity;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.persistence.SavingsPersistence;
import org.mifos.accounts.savings.util.helpers.SavingsTestHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.util.helpers.CustomerStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.util.helpers.ApplicableTo;
import org.mifos.accounts.productdefinition.util.helpers.InterestCalcType;
import org.mifos.accounts.productdefinition.util.helpers.PrdStatus;
import org.mifos.accounts.productdefinition.util.helpers.RecommendedAmountUnit;
import org.mifos.accounts.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class SavingsIntPostingHelperIntegrationTest extends MifosIntegrationTestCase {
    public SavingsIntPostingHelperIntegrationTest() throws Exception {
        super();
    }

    private UserContext userContext;

    private CustomerBO group;

    private CustomerBO center;

    private SavingsBO savings1;

    private SavingsBO savings2;

    private SavingsBO savings3;

    private SavingsBO savings4;

    private SavingsOfferingBO savingsOffering1;

    private SavingsOfferingBO savingsOffering2;

    private SavingsOfferingBO savingsOffering3;

    private SavingsOfferingBO savingsOffering4;

    private SavingsTestHelper helper = new SavingsTestHelper();

    private MifosCurrency currency = Configuration.getInstance().getSystemConfig().getCurrency();

    SavingsPersistence persistence = new SavingsPersistence();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        userContext = TestUtils.makeUser();
    }

    @Override
    protected void tearDown() throws Exception {
        TestObjectFactory.cleanUp(savings1);
        TestObjectFactory.cleanUp(savings2);
        TestObjectFactory.cleanUp(savings3);
        TestObjectFactory.cleanUp(savings4);
        TestObjectFactory.cleanUp(group);
        TestObjectFactory.cleanUp(center);
        StaticHibernateUtil.closeSession();
        super.tearDown();
    }

    public void testInterestPosting() throws Exception {
        createInitialObjects();
        SavingBOTestUtils.setNextIntPostDate(savings1, helper.getDate("31/03/2006"));
        SavingBOTestUtils.setActivationDate(savings1, helper.getDate("05/03/2006"));
        SavingBOTestUtils.setInterestToBePosted(savings1, new Money(currency, "500"));
        SavingBOTestUtils.setBalance(savings1, new Money(getCurrency(), "250"));

        savings1.update();

        SavingBOTestUtils.setNextIntPostDate(savings4, helper.getDate("31/03/2006"));
        SavingBOTestUtils.setActivationDate(savings4, helper.getDate("15/03/2006"));
        SavingBOTestUtils.setInterestToBePosted(savings4, new Money(currency, "800.40"));
        SavingBOTestUtils.setBalance(savings4, new Money(getCurrency(), "250"));
        savings4.update();

        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();

        Calendar cal = Calendar.getInstance(Configuration.getInstance().getSystemConfig().getMifosTimeZone());
        cal.setTime(helper.getDate("01/05/2006"));
        SavingsIntPostingTask savingsIntPostingTask = new SavingsIntPostingTask();
        ((SavingsIntPostingHelper) savingsIntPostingTask.getTaskHelper()).execute(cal.getTimeInMillis());

        savings1 = persistence.findById(savings1.getAccountId());
        savings2 = persistence.findById(savings2.getAccountId());
        savings3 = persistence.findById(savings3.getAccountId());
        savings4 = persistence.findById(savings4.getAccountId());

       Assert.assertEquals(TestUtils.createMoney(), savings1.getInterestToBePosted());
       Assert.assertEquals(TestUtils.createMoney(750.0), savings1.getSavingsBalance());
       Assert.assertEquals(1, savings1.getAccountPayments().size());
        AccountPaymentEntity payment1 = savings1.getAccountPayments().iterator().next();
       Assert.assertEquals(TestUtils.createMoney(500.0), payment1.getAmount());
       Assert.assertEquals(helper.getDate("30/04/2006"), savings1.getNextIntPostDate());

       Assert.assertEquals(1, savings1.getSavingsActivityDetails().size());
        for (SavingsActivityEntity activity : savings1.getSavingsActivityDetails())
           Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(getDate("31/03/2006").getTime()), DateUtils
                    .getDateWithoutTimeStamp(activity.getTrxnCreatedDate().getTime()));

       Assert.assertEquals(TestUtils.createMoney(1050.4), savings4.getSavingsBalance());
       Assert.assertEquals(TestUtils.createMoney(), savings4.getInterestToBePosted());
       Assert.assertEquals(1, savings4.getAccountPayments().size());

        AccountPaymentEntity payment4 = savings4.getAccountPayments().iterator().next();
       Assert.assertEquals(TestUtils.createMoney(800.4), payment4.getAmount());
       Assert.assertEquals(helper.getDate("30/04/2006"), savings4.getNextIntPostDate());

       Assert.assertEquals(1, savings1.getSavingsActivityDetails().size());
        for (SavingsActivityEntity activity : savings1.getSavingsActivityDetails())
           Assert.assertEquals(DateUtils.getDateWithoutTimeStamp(getDate("31/03/2006").getTime()), DateUtils
                    .getDateWithoutTimeStamp(activity.getTrxnCreatedDate().getTime()));

       Assert.assertEquals(0, savings2.getAccountPayments().size());
       Assert.assertEquals(0, savings3.getAccountPayments().size());
    }

    private void createInitialObjects() throws Exception {
        MeetingBO meeting = TestObjectFactory.createMeeting(TestObjectFactory.getTypicalMeeting());
        center = TestObjectFactory.createWeeklyFeeCenter("Center_Active_test", meeting);
        group = TestObjectFactory.createWeeklyFeeGroupUnderCenter("Group_Active_test", CustomerStatus.GROUP_ACTIVE, center);

        savingsOffering1 = createSavingsOffering("prd1", "ssdr", InterestCalcType.MINIMUM_BALANCE);
        savingsOffering2 = createSavingsOffering("prd2", "aser", InterestCalcType.MINIMUM_BALANCE);
        savingsOffering3 = createSavingsOffering("prd3", "zx23", InterestCalcType.MINIMUM_BALANCE);
        savingsOffering4 = createSavingsOffering("prd4", "wsas", InterestCalcType.AVERAGE_BALANCE);
        savings1 = helper.createSavingsAccount(savingsOffering1, group, AccountState.SAVINGS_ACTIVE, userContext);
        savings2 = helper.createSavingsAccount(savingsOffering2, group, AccountState.SAVINGS_PARTIAL_APPLICATION,
                userContext);
        savings3 = helper.createSavingsAccount(savingsOffering3, group, AccountState.SAVINGS_PENDING_APPROVAL,
                userContext);
        savings4 = helper.createSavingsAccount(savingsOffering4, group, AccountState.SAVINGS_ACTIVE, userContext);
    }

    private SavingsOfferingBO createSavingsOffering(String offeringName, String shortName,
            InterestCalcType interestCalcType) throws Exception {
        MeetingBO meetingIntCalc = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(MONTHLY,
                EVERY_SECOND_MONTH, SAVINGS_INTEREST_CALCULATION_TIME_PERIOD, MONDAY));
        MeetingBO meetingIntPost = TestObjectFactory.createMeeting(TestObjectFactory.getNewMeeting(MONTHLY,
                EVERY_SECOND_MONTH, SAVINGS_INTEREST_POSTING, MONDAY));
        return TestObjectFactory.createSavingsProduct(offeringName, shortName, ApplicableTo.GROUPS, new Date(System
                .currentTimeMillis()), PrdStatus.SAVINGS_ACTIVE, 300.0, RecommendedAmountUnit.PER_INDIVIDUAL, 12.0,
                200.0, 200.0, SavingsType.VOLUNTARY, interestCalcType, meetingIntCalc, meetingIntPost);
    }

}
