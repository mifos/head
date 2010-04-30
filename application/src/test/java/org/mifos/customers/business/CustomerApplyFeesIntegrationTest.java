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

package org.mifos.customers.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesActionDetailEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.calendar.DayOfWeek;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.IntegrationTestObjectMother;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class CustomerApplyFeesIntegrationTest {

    private static MifosCurrency oldDefaultCurrency;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private MeetingBO weeklyMeeting;
    private AmountFeeBO weeklyPeriodicFeeForCenterOnly;
    private CenterBO center;
    private DateTimeService dateTimeService = new DateTimeService();
    DateTime firstTuesdayInstallmentDate;
    MeetingBuilder weeklyMeetingForFees;
    MeetingBuilder biWeeklyMeetingForFees;
    AmountFeeBO weeklyAppliedPeriodicFeeForCenterOnly;
    AmountFeeBO biWeeklyAppliedPeriodicFeeForCenterOnly;

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
    public void setup() {
        databaseCleaner.clean();
        firstTuesdayInstallmentDate = getNearestDayOfWeekFromNow(DayOfWeek.tuesday());

        weeklyMeeting = new MeetingBuilder().customerMeeting()
                                            .weekly()
                                            .every(1)
                                            .withStartDate(firstTuesdayInstallmentDate)
                                            .build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        weeklyMeetingForFees = new MeetingBuilder().periodicFeeMeeting()
                                                                  .weekly()
                                                                  .every(1)
                                                                  .withStartDate(firstTuesdayInstallmentDate);

        biWeeklyMeetingForFees = new MeetingBuilder().periodicFeeMeeting()
                                                                  .weekly()
                                                                  .every(2)
                                                                  .withStartDate(firstTuesdayInstallmentDate);

        weeklyPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly()
                                                        .withFeeAmount("100.0")
                                                        .withName("Center Weekly Periodic Fee")
                                                        .with(weeklyMeetingForFees)
                                                        .with(sampleBranchOffice())
                                                        .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForCenterOnly);

        weeklyAppliedPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly()
                                                        .withFeeAmount("100.0")
                                                        .withName("Center Applied Weekly Periodic Fee")
                                                        .with(weeklyMeetingForFees)
                                                        .with(sampleBranchOffice())
                                                        .build();
        IntegrationTestObjectMother.saveFee(weeklyAppliedPeriodicFeeForCenterOnly);

        biWeeklyAppliedPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly()
                                                        .withFeeAmount("100.0")
                                                        .withName("Center Applied Biweekly Periodic Fee")
                                                        .with(biWeeklyMeetingForFees)
                                                        .with(sampleBranchOffice())
                                                        .build();
        IntegrationTestObjectMother.saveFee(biWeeklyAppliedPeriodicFeeForCenterOnly);


    }

    @After
    public void cleanup() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        StaticHibernateUtil.commitTransaction();
        StaticHibernateUtil.closeSession();
        databaseCleaner.clean();
        dateTimeService.resetToCurrentSystemDateTime();

    }

    @Test
    public void baselineTestForCenterFeeScheduling() {

        // use default setup
        createCenterWithFeeStartingNextTuesday(weeklyMeeting, weeklyPeriodicFeeForCenterOnly);


        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());

        //verification
        verifyUnadjustedActionDates();
        verifyOriginalWeeklyFeeForWeeklyMeeting();
    }

    @Test
    public void applyPeriodicWeeklyFeeOnSecondMeetingDate() throws Exception {

        // use default setup
        createCenterWithFeeStartingNextTuesday(weeklyMeeting, weeklyPeriodicFeeForCenterOnly);

        //Exercise test, applying the fee on the date of the second meeting
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        center.getCustomerAccount().setUserContext(TestUtils.makeUser());
        dateTimeService.setCurrentDateTime(firstTuesdayInstallmentDate.plusWeeks(1));
        center.getCustomerAccount().applyCharge(weeklyAppliedPeriodicFeeForCenterOnly.getFeeId(), 13.0);

        //verification
        verifyUnadjustedActionDates();
        verifyAppliedPeriodicFeeToWeeklyMeeting(weeklyAppliedPeriodicFeeForCenterOnly, 13.0);
    }

    @Test
    public void applyPeriodicBiWeeklyFeeOnSecondMeetingDate() throws Exception {

        // use default setup
        createCenterWithFeeStartingNextTuesday(weeklyMeeting, weeklyPeriodicFeeForCenterOnly);

        //Exercise test, applying the fee on the date of the second meeting
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        center.getCustomerAccount().setUserContext(TestUtils.makeUser());
        dateTimeService.setCurrentDateTime(firstTuesdayInstallmentDate.plusWeeks(1));
        center.getCustomerAccount().applyCharge(biWeeklyAppliedPeriodicFeeForCenterOnly.getFeeId(), 16.0);

        //verification
        verifyUnadjustedActionDates();
        verifyAppliedPeriodicFeeToWeeklyMeeting(biWeeklyAppliedPeriodicFeeForCenterOnly, 16.0);
    }

    @Test
    public void applyPeriodicFeeOnSecondMeetingDateFallsInMoratorium() throws Exception {

        // setup with moratorium
        Holiday moratorium = new HolidayBuilder().from(firstTuesdayInstallmentDate.plusWeeks(1))
                                                 .to(new DateTime().plusWeeks(2).minusDays(1))
                                                 .withRepaymentMoratoriumRule()
                                                 .build();
        IntegrationTestObjectMother.saveHoliday(moratorium);
        createCenterWithFeeStartingNextTuesday(weeklyMeeting, weeklyPeriodicFeeForCenterOnly);

        // Exercise test, applying fee on the day after the first meeting
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        center.getCustomerAccount().setUserContext(TestUtils.makeUser());

        dateTimeService.setCurrentDateTime(firstTuesdayInstallmentDate.plusDays(1));
        center.getCustomerAccount().applyCharge(weeklyAppliedPeriodicFeeForCenterOnly.getFeeId(), 15.0);

        //verification
        verifyDatesAdjustedForMoratorium();
        verifyAppliedPeriodicFeeToWeeklyMeeting(weeklyAppliedPeriodicFeeForCenterOnly, 15.0);
    }

    @Test
    public void applyPeriodicBiWeeklyFeeOnSecondMeetingDateFallsInMoratorium() throws Exception {

        // setup with moratorium
        Holiday moratorium = new HolidayBuilder().from(firstTuesdayInstallmentDate.plusWeeks(1))
                                                 .to(new DateTime().plusWeeks(2).minusDays(1))
                                                 .withRepaymentMoratoriumRule()
                                                 .build();
        IntegrationTestObjectMother.saveHoliday(moratorium);
        createCenterWithFeeStartingNextTuesday(weeklyMeeting, weeklyPeriodicFeeForCenterOnly);

        // Exercise test, applying fee on the day after the first meeting
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        center.getCustomerAccount().setUserContext(TestUtils.makeUser());

        dateTimeService.setCurrentDateTime(firstTuesdayInstallmentDate.plusDays(1));
        center.getCustomerAccount().applyCharge(biWeeklyAppliedPeriodicFeeForCenterOnly.getFeeId(), 1.0);

        //verification. Fee should be applied to second installment meeting
        verifyDatesAdjustedForMoratorium();
        verifyAppliedPeriodicFeeToWeeklyMeeting(biWeeklyAppliedPeriodicFeeForCenterOnly, 1.0);
    }

    /********************************
     * Helper methods
     ***********************************/

    private DateTime getNearestDayOfWeekFromNow (int dayOfWeek) {
        DateTime installmentDate = dateTimeService.getCurrentDateTime().withDayOfWeek(dayOfWeek);
        if (installmentDate.isBeforeNow()) {
            installmentDate = installmentDate.plusWeeks(1);
        }
        return installmentDate;
    }

    private void printSchedules (List<AccountActionDateEntity> accountActionDates) {
        for (AccountActionDateEntity actionDate : accountActionDates) {
            CustomerScheduleEntity customerSchedule = (CustomerScheduleEntity) actionDate;
            String out = "Installment" + customerSchedule.getInstallmentId() + " on " + customerSchedule.getActionDate();
            out = out + ". Fees: ";
            for (AccountFeesActionDetailEntity feeActionDetail : customerSchedule.getAccountFeesActionDetailsSortedByFeeId()) {
                out = out + feeActionDetail.getFee().getFeeName() + " - " +feeActionDetail.getFeeAmount().getAmountDoubleValue() + ", ";
            }
            System.out.println(out);
        }
    }

private void createCenterWithFeeStartingNextTuesday(MeetingBO centerMeeting, AmountFeeBO fee) {

    center = new CenterBuilder().with(centerMeeting)
                                .withName("Center")
                                .with(sampleBranchOffice())
                                .withLoanOfficer(testUser())
                                .with(TestUtils.makeUserWithLocales())
                                .build();
    IntegrationTestObjectMother.createCenter(center, centerMeeting, fee);
    }

    private void verifyUnadjustedActionDates() {
        List<AccountActionDateEntity> accountActionDates = center.getCustomerAccount().getActionDatesSortedByDate();
        assertThat(accountActionDates.size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = firstTuesdayInstallmentDate;

        for (AccountActionDateEntity accountActionDate : accountActionDates) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    private void verifyDatesAdjustedForMoratorium() {
        List<AccountActionDateEntity> accountActionDates = center.getCustomerAccount().getActionDatesSortedByDate();

        printSchedules(accountActionDates);

        assertThat(accountActionDates.size(), is(10));
        Short installmentId = Short.valueOf("1");
        DateTime installmentDate = firstTuesdayInstallmentDate;

        for (AccountActionDateEntity accountActionDate : accountActionDates) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            assertThat(scheduleEntity.getInstallmentId(), is(installmentId));
            if (installmentId < 2) {
                assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.toDate())));
            } else {
                assertThat(new LocalDate(scheduleEntity.getActionDate()), is(new LocalDate(installmentDate.plusWeeks(1).toDate())));
            }
            installmentId++;
            installmentDate = installmentDate.plusWeeks(1);
        }
    }

    private void verifyOriginalWeeklyFeeForWeeklyMeeting() {
        List<AccountActionDateEntity> accountActionDates = center.getCustomerAccount().getActionDatesSortedByDate();
        for (AccountActionDateEntity accountActionDate : accountActionDates) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(1));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(),
                        is("Center Weekly Periodic Fee"));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(),
                        is(100.0));
        }
    }

    private void verifyAppliedPeriodicFeeToWeeklyMeeting(AmountFeeBO appliedFee, Double feeAmount) {
        List<AccountActionDateEntity> accountActionDates = center.getCustomerAccount().getActionDatesSortedByDate();
        Short installmentId = Short.valueOf("1");
        for (AccountActionDateEntity accountActionDate : accountActionDates) {
            CustomerScheduleEntity scheduleEntity = (CustomerScheduleEntity) accountActionDate;
            if (installmentId == 2) {
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(2));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(),
                        is("Center Weekly Periodic Fee"));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(),
                        is(100.0));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(1).getFee().getFeeName(),
                        is(appliedFee.getFeeName()));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(1).getFeeAmount().getAmountDoubleValue(),
                        is(feeAmount));

            } else {
                assertThat(scheduleEntity.getAccountFeesActionDetails().size(), is(1));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFee().getFeeName(),
                        is("Center Weekly Periodic Fee"));
                assertThat(scheduleEntity.getAccountFeesActionDetailsSortedByFeeId().get(0).getFeeAmount().getAmountDoubleValue(),
                        is(100.0));
                }
            installmentId++;
        }


    }
}
