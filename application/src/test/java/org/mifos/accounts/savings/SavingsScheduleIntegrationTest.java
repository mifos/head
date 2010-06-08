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

package org.mifos.accounts.savings;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.productdefinition.business.SavingsOfferingBO;
import org.mifos.accounts.productdefinition.business.SavingsProductBuilder;
import org.mifos.accounts.savings.business.SavingsBO;
import org.mifos.accounts.savings.business.SavingsScheduleEntity;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.ClientBuilder;
import org.mifos.application.collectionsheet.persistence.GroupBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.collectionsheet.persistence.SavingsAccountBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.persistence.HolidayDao;
import org.mifos.application.holiday.persistence.HolidayDetails;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.calendar.DayOfWeek;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.emory.mathcs.backport.java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml"})
public class SavingsScheduleIntegrationTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    private FiscalCalendarRules fiscalCalendarRules = new FiscalCalendarRules();
    private List<WeekDay> savedWorkingDays = fiscalCalendarRules.getWorkingDays();
    private MeetingBO meeting;
    private CenterBO center;
    private GroupBO group;
    private ClientBO client;
    private SavingsOfferingBO savingsProduct;
    private SavingsBO savingsAccount;
    private int savingsDueDayOfWeek;
    private DateTime expectedFirstDepositDate;

    @BeforeClass
    public static void initialiseHibernateUtil() {

        oldDefaultCurrency = Money.getDefaultCurrency();
        Money.setDefaultCurrency(TestUtils.RUPEE);
        new StandardTestingService().setTestMode(TestMode.INTEGRATION);
        DatabaseSetup.initializeHibernate();
    }

   @AfterClass
    public static void resetCurrency() {
        Money.setDefaultCurrency(oldDefaultCurrency);
    }

   @Before
   public void setupDates() {
       savingsDueDayOfWeek = DayOfWeek.monday();

       //First Monday on or after today
       expectedFirstDepositDate = new DateTime().withDayOfWeek(savingsDueDayOfWeek);
       if (expectedFirstDepositDate.isBefore(new DateTime())) {
           expectedFirstDepositDate = expectedFirstDepositDate.plusWeeks(1);
       }
   }

    @Before
    public void cleanDatabaseTables() {
        databaseCleaner.clean();
        fiscalCalendarRules.setWorkingDays("MONDAY,TUESDAY,WEDNESDAY,THURSDAY,FRIDAY");
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
        DateTimeUtils.setCurrentMillisSystem();
        fiscalCalendarRules.setWorkingDays(weekDaysToPropertyString(savedWorkingDays));
   }

    @Ignore
    @Test
    public void createWeeklySavingScheduleSecondInstallmentFallsInMoratorium() throws Exception {
        buildAndPersistHoliday(expectedFirstDepositDate.plusWeeks(1), expectedFirstDepositDate.plusWeeks(1),
                RepaymentRuleTypes.REPAYMENT_MORATORIUM);

        createClientSavingsAccount();

        short installmentId = 1;
        DateTime installmentDate = expectedFirstDepositDate;
        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(savingsAccount)) {
            SavingsScheduleEntity scheduleEntity = (SavingsScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            if (installmentId < 2) {
                assertThat(new LocalDate(scheduleEntity.getActionDate()),
                        is(new LocalDate(installmentDate.toDate())));
            } else { // second and following dates pushed out one week.
                assertThat(new LocalDate(scheduleEntity.getActionDate()),
                        is(new LocalDate(installmentDate.plusWeeks(1).toDate())));
            }
            assertThat(scheduleEntity.getDeposit().getAmountDoubleValue(), is(13.0));
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    @Test
    public void createWeeklySavingScheduleSecondInstallmentFallsInNextMeetingHoliday() throws Exception {
        buildAndPersistHoliday(expectedFirstDepositDate.plusWeeks(1), expectedFirstDepositDate.plusWeeks(1),
                RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT);
        createClientSavingsAccount();

        short installmentId = 1;
        DateTime installmentDate = expectedFirstDepositDate;
        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(savingsAccount)) {
            SavingsScheduleEntity scheduleEntity = (SavingsScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            if (installmentId == 2) {  // only second installment pushed out one week.
                assertThat(new LocalDate(scheduleEntity.getActionDate()),
                        is(new LocalDate(installmentDate.plusWeeks(1).toDate())));
            } else {
                assertThat(new LocalDate(scheduleEntity.getActionDate()),
                        is(new LocalDate(installmentDate.toDate())));
            }
            assertThat(scheduleEntity.getDeposit().getAmountDoubleValue(), is(13.0));
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    @Test
    public void createWeeklySavingScheduleSecondInstallmentFallsInNextWorkingDayHoliday() throws Exception {

        // One-day holiday on the second deposit date, Monday
        buildAndPersistHoliday(expectedFirstDepositDate.plusWeeks(1), expectedFirstDepositDate.plusWeeks(1),
                RepaymentRuleTypes.NEXT_WORKING_DAY);

        createClientSavingsAccount();

        short installmentId = 1;
        DateTime installmentDate = expectedFirstDepositDate;
        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(savingsAccount)) {
            SavingsScheduleEntity scheduleEntity = (SavingsScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            if (installmentId == 2) {
                assertThat(new LocalDate(scheduleEntity.getActionDate()),
                        is(new LocalDate(installmentDate.plusDays(1).toDate()))); //Tuesday after holiday
            } else {
                assertThat(new LocalDate(scheduleEntity.getActionDate()),
                        is(new LocalDate(installmentDate.toDate())));
            }
            assertThat(scheduleEntity.getDeposit().getAmountDoubleValue(), is(13.0));
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    @Test
    public void createWeeklySavingScheduleNoHoliday() throws Exception {

        createClientSavingsAccount();

        short installmentId = 1;
        DateTime installmentDate = expectedFirstDepositDate;
        for (AccountActionDateEntity accountActionDate : getActionDatesSortedByDate(savingsAccount)) {
            SavingsScheduleEntity scheduleEntity = (SavingsScheduleEntity) accountActionDate;
                assertThat(new LocalDate(scheduleEntity.getActionDate()),
                            is(new LocalDate(installmentDate.toDate())));
            assertThat(scheduleEntity.getDeposit().getAmountDoubleValue(), is(13.0));
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    private String weekDaysToPropertyString(List<WeekDay> weekDays) {
        String propertyString = "";
        Boolean first = true;
        for (WeekDay day : weekDays) {
            if (!first) {
                propertyString = propertyString + ",";
            } else {
                first = false;
            }
            propertyString = propertyString + day.toString();
        }
        return propertyString;
    }


    public void createClientSavingsAccount() throws Exception {
        meeting = new MeetingBuilder().customerMeeting().weekly().every(1).withStartDate(expectedFirstDepositDate).build();
        IntegrationTestObjectMother.saveMeeting(meeting);

        center = new CenterBuilder().with(meeting).withName("Center").with(sampleBranchOffice())
                .withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.createCenter(center, meeting);

        group = new GroupBuilder().withMeeting(meeting).withName("Group").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withParentCustomer(center).build();
        IntegrationTestObjectMother.createGroup(group, meeting);

        client = new ClientBuilder().withMeeting(meeting).withName("Client 1").withOffice(sampleBranchOffice())
                .withLoanOfficer(testUser()).withParentCustomer(group).buildForIntegrationTests();
        IntegrationTestObjectMother.createClient(client, meeting);

        savingsProduct = new SavingsProductBuilder().mandatory().appliesToClientsOnly().buildForIntegrationTests();


        HolidayDao holidayDao = DependencyInjectedServiceLocator.locateHolidayDao();
        List<Holiday> holidays = holidayDao.findAllHolidaysThisYearAndNext(client.getOfficeId());

        savingsAccount = new SavingsAccountBuilder().mandatory().withSavingsProduct(savingsProduct)
                .withCustomer(client).with(holidays).build();
        IntegrationTestObjectMother.saveSavingsProductAndAssociatedSavingsAccounts(savingsProduct, savingsAccount);
    }

    private List<AccountActionDateEntity> getActionDatesSortedByDate(AccountBO customerAccount) {
        List<AccountActionDateEntity> sortedList = new ArrayList<AccountActionDateEntity>();
        sortedList.addAll(customerAccount.getAccountActionDates());
        Collections.sort(sortedList);
        return sortedList;
    }

    private void buildAndPersistHoliday (DateTime start, DateTime through, RepaymentRuleTypes rule) throws Exception {
        HolidayDetails holidayDetails = new HolidayDetails("testHoliday", start.toDate(), through.toDate(), rule);
        List<Short> officeIds = new LinkedList<Short>();
        officeIds.add((short)1);
        DependencyInjectedServiceLocator.locateHolidayServiceFacade().createHoliday(holidayDetails, officeIds);
    }
}