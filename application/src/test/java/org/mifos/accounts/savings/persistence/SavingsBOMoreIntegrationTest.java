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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.Date;

import junit.framework.Assert;

import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.servicefacade.TestCollectionSheetRetrieveSavingsAccountsUtils;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.domain.builders.CenterBuilder;
import org.mifos.domain.builders.ClientBuilder;
import org.mifos.domain.builders.GroupBuilder;
import org.mifos.domain.builders.MeetingBuilder;
import org.mifos.framework.MifosIntegrationTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;

public class SavingsBOMoreIntegrationTest extends MifosIntegrationTestCase {

    private CenterBO center;
    private GroupBO group;
    private ClientBO client;
    private SavingsBO savings;
    private Money recommendedAmount;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
    }

    @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Before
    public void cleanDatabaseTables() throws Exception {
        databaseCleaner.clean();

        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).startingToday().build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        center = new CenterBuilder().with(weeklyMeeting).withName("Savings Center").with(sampleBranchOffice())
                .withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.createCenter(center, weeklyMeeting);

        group = new GroupBuilder().withMeeting(weeklyMeeting).withName("Savings Group")
                .withOffice(sampleBranchOffice()).withLoanOfficer(testUser()).withParentCustomer(center).build();
        IntegrationTestObjectMother.createGroup(group, weeklyMeeting);

        client = new ClientBuilder().withMeeting(weeklyMeeting).withName("Savings Client").withOffice(
                sampleBranchOffice()).withLoanOfficer(testUser()).withParentCustomer(group).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(client, weeklyMeeting);

        String recommendedAmountString = "3.0";
        recommendedAmount = new Money(Money.getDefaultCurrency(),recommendedAmountString);
        savings = new TestCollectionSheetRetrieveSavingsAccountsUtils().createSavingsAccount(client, "clm", recommendedAmountString,
                false, false);
    }

    @Test
    public void changingSavingsAccountToInactiveRemovesRecommendedAmountsFromFutureInstallments() throws Exception {

        assertAllFutureSchedulesAreAsExpected(savings, recommendedAmount);

        //
        PersonnelBO loggedInUser = IntegrationTestObjectMother.testUser();
        savings.changeStatus(AccountState.SAVINGS_INACTIVE, null, "Make Inactive", loggedInUser);
        savings.save();
        StaticHibernateUtil.flushSession();
        // refresh hibernate data
        savings = (SavingsBO) new LegacyAccountDao().getAccount(savings.getAccountId());

        Money zero = new Money(Money.getDefaultCurrency());
        assertAllFutureSchedulesAreAsExpected(savings, zero);
    }

    @Test
    public void changingSavingsAccountFromInactiveToActiveResetsRecommendedAmountsFromFutureInstallments() throws Exception {

        //make inactive first
        PersonnelBO loggedInUser = IntegrationTestObjectMother.testUser();
        savings.changeStatus(AccountState.SAVINGS_INACTIVE, null, "Make Inactive", loggedInUser);
        savings.save();
        StaticHibernateUtil.flushSession();
        // refresh hibernate data
        savings = (SavingsBO) new LegacyAccountDao().getAccount(savings.getAccountId());

        Money zero = new Money(Money.getDefaultCurrency());
        assertAllFutureSchedulesAreAsExpected(savings, zero);


        //make active again
        savings.changeStatus(AccountState.SAVINGS_ACTIVE, null, "Make Active Again", loggedInUser);
        savings.save();
        StaticHibernateUtil.flushSession();
        // refresh hibernate data
        savings = (SavingsBO) new LegacyAccountDao().getAccount(savings.getAccountId());

        assertAllFutureSchedulesAreAsExpected(savings, recommendedAmount);
    }

    private void assertAllFutureSchedulesAreAsExpected(SavingsBO savingsParam, Money expectedAmount) {

        LocalDate currentLocalDate = new LocalDate();
        Date currentDate = DateUtils.getDateFromLocalDate(currentLocalDate);

        Assert.assertNotNull(savingsParam.getAccountActionDates());
        assertThat(savingsParam.getAccountActionDates().size(), is(10));
        for (AccountActionDateEntity accountAction : savingsParam.getAccountActionDates()) {
            if (accountAction.getActionDate().compareTo(currentDate) >= 0 && !accountAction.isPaid()) {
                SavingsScheduleEntity savingsSchedule = (SavingsScheduleEntity) accountAction;

                assertThat(savingsSchedule.getDeposit(), is(expectedAmount));
            }
        }
    }

}
