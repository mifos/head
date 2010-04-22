package org.mifos.customers.business.service;


import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.accounts.savings.persistence.GenericDaoHibernate;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.holiday.business.Holiday;
import org.mifos.application.holiday.business.HolidayBO;
import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerDaoHibernate;
import org.mifos.domain.builders.HolidayBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.StandardTestingService;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.Money;
import org.mifos.service.test.TestMode;
import org.mifos.test.framework.util.DatabaseCleaner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import edu.emory.mathcs.backport.java.util.Collections;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class CenterScheduleCreationUsingCustomerServiceIntegrationTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private static MifosCurrency oldDefaultCurrency;

    private GenericDao genericDao = new GenericDaoHibernate();
    private CustomerDao customerDao = new CustomerDaoHibernate(genericDao);

    private FiscalCalendarRules fiscalCalendarRules = new FiscalCalendarRules();
    private List<WeekDay> savedWorkingDays = fiscalCalendarRules.getWorkingDays();

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

    /***********************************************
     * Weekly meeting, no fees
     ***********************************************/

    @Test
    public void withWeeklyMeetingNoFeesNoHoliday() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting()
                                                      .weekly()
                                                      .every(1)
                                                      .startingToday()
                                                      .build();
        CenterBO center = new CenterBuilder().withMeeting(weeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, new ArrayList<AccountFeesEntity>());

        // verification
        StaticHibernateUtil.flushAndClearSession();
        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(1), startDate.plusWeeks(2),
                startDate.plusWeeks(3), startDate.plusWeeks(4), startDate.plusWeeks(5), startDate.plusWeeks(6),
                startDate.plusWeeks(7), startDate.plusWeeks(8), startDate.plusWeeks(9));
    }

    @Test
    public void withWeeklyMeetingNoFeesThirdDateInMoratorium() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        buildAndPersistHoliday(date(2010, 4, 19), date(2010, 4, 23), RepaymentRuleTypes.REPAYMENT_MORATORIUM);
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly()
                                                      .every(1)
                                                      .startingToday()
                                                      .build();
        CenterBO center = new CenterBuilder().withMeeting(weeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();
        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(1), startDate.plusWeeks(3), startDate.plusWeeks(4),
                startDate.plusWeeks(5), startDate.plusWeeks(6), startDate.plusWeeks(7), startDate.plusWeeks(8),
                startDate.plusWeeks(9), startDate.plusWeeks(10));
    }

    @Test
    public void withWeeklyMeetingNoFeesThirdAndFourthDatesInMoratorium() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        buildAndPersistHoliday(startDate.plusWeeks(2), startDate.plusWeeks(3).plusDays(4), RepaymentRuleTypes.REPAYMENT_MORATORIUM);
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly()
                                                      .every(1)
                                                      .startingToday()
                                                      .build();
        CenterBO center = new CenterBuilder().withMeeting(weeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();
        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(1), startDate.plusWeeks(4), startDate.plusWeeks(5),
                startDate.plusWeeks(6), startDate.plusWeeks(7), startDate.plusWeeks(8), startDate.plusWeeks(9),
                startDate.plusWeeks(10), startDate.plusWeeks(11));
    }

    @Test
    public void withWeeklyMeetingNoFeesThirdDateInNextMeetingHoliday() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        buildAndPersistHoliday(date(2010, 4, 19), date(2010, 4, 23), RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT);
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly()
                                                      .every(1)
                                                      .startingToday()
                                                      .build();
        CenterBO center = new CenterBuilder().withMeeting(weeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();
        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(1), startDate.plusWeeks(3), startDate.plusWeeks(3),
                startDate.plusWeeks(4), startDate.plusWeeks(5), startDate.plusWeeks(6), startDate.plusWeeks(7),
                startDate.plusWeeks(8), startDate.plusWeeks(9));
    }

    @Test
    public void withWeeklyMeetingNoFeesThirdAndFourthDatesInNextMeetingHoliday() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        buildAndPersistHoliday(startDate.plusWeeks(2), startDate.plusWeeks(3).plusDays(4), RepaymentRuleTypes.NEXT_MEETING_OR_REPAYMENT);
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly()
                                                      .every(1)
                                                      .startingToday()
                                                      .build();
        CenterBO center = new CenterBuilder().withMeeting(weeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();
        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(1), startDate.plusWeeks(4),
                startDate.plusWeeks(4), startDate.plusWeeks(4), startDate.plusWeeks(5), startDate.plusWeeks(6),
                startDate.plusWeeks(7), startDate.plusWeeks(8), startDate.plusWeeks(9));
    }

    @Test
    public void withWeeklyMeetingNoFeesThirdDateInSameDayHoliday() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        buildAndPersistHoliday(startDate.plusWeeks(2), startDate.plusWeeks(2).plusDays(3), RepaymentRuleTypes.SAME_DAY);
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly()
                                                      .every(1)
                                                      .startingToday()
                                                      .build();
        CenterBO center = new CenterBuilder().withMeeting(weeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();
        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(1), startDate.plusWeeks(2),
                startDate.plusWeeks(3), startDate.plusWeeks(4), startDate.plusWeeks(5), startDate.plusWeeks(6),
                startDate.plusWeeks(7), startDate.plusWeeks(8), startDate.plusWeeks(9));
    }

    @Test
    public void withWeeklyMeetingNoFeesThirdAndFourthDatesInSameDayHoliday() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        buildAndPersistHoliday(startDate.plusWeeks(2), startDate.plusWeeks(2).plusDays(3), RepaymentRuleTypes.SAME_DAY);
        buildAndPersistHoliday(startDate.plusWeeks(3), startDate.plusWeeks(3).plusDays(3), RepaymentRuleTypes.SAME_DAY);
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly()
                                                      .every(1)
                                                      .startingToday()
                                                      .build();
        CenterBO center = new CenterBuilder().withMeeting(weeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();
        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(1), startDate.plusWeeks(2),
                startDate.plusWeeks(3), startDate.plusWeeks(4), startDate.plusWeeks(5), startDate.plusWeeks(6),
                startDate.plusWeeks(7), startDate.plusWeeks(8), startDate.plusWeeks(9));
    }

    @Test
    public void withWeeklyMeetingNoFeesThirdDateInOneDayNextWorkingDayHoliday() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        buildAndPersistHoliday(startDate.plusWeeks(2), startDate.plusWeeks(2), RepaymentRuleTypes.NEXT_WORKING_DAY);
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly()
                                                      .every(1)
                                                      .startingToday()
                                                      .build();
        CenterBO center = new CenterBuilder().withMeeting(weeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();
        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(1), startDate.plusWeeks(2).plusDays(1),
                startDate.plusWeeks(3), startDate.plusWeeks(4), startDate.plusWeeks(5), startDate.plusWeeks(6),
                startDate.plusWeeks(7), startDate.plusWeeks(8), startDate.plusWeeks(9));
    }

    @Test
    public void withWeeklyMeetingNoFeesThirdDateInOneWeekNextWorkingDayHolidayShouldPushOutToNextMonday() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        buildAndPersistHoliday(startDate.plusWeeks(2), startDate.plusWeeks(2).plusDays(4), RepaymentRuleTypes.NEXT_WORKING_DAY);
        MeetingBO weeklyMeeting = new MeetingBuilder().customerMeeting().weekly()
                                                      .every(1)
                                                      .startingToday()
                                                      .build();
        CenterBO center = new CenterBuilder().withMeeting(weeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();
        List<AccountFeesEntity> noAccountFees = new ArrayList<AccountFeesEntity>();

        // exercise test
        customerService.createCenter(center, weeklyMeeting, noAccountFees);

        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(1), startDate.plusWeeks(2).plusWeeks(1),
                startDate.plusWeeks(3), startDate.plusWeeks(4), startDate.plusWeeks(5), startDate.plusWeeks(6),
                startDate.plusWeeks(7), startDate.plusWeeks(8), startDate.plusWeeks(9));
    }

    /***********************************************
     * Biweekly meeting, no fees
     ***********************************************/

    @Test
    public void withBiWeeklyMeetingNoFeesNoHoliday() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        MeetingBO biWeeklyMeeting = new MeetingBuilder().customerMeeting()
                                                      .weekly()
                                                      .every(2)
                                                      .startingToday()
                                                      .build();

        CenterBO center = new CenterBuilder().withMeeting(biWeeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();

        // exercise test
        customerService.createCenter(center, biWeeklyMeeting, new ArrayList<AccountFeesEntity>());

        // verification
        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(2), startDate.plusWeeks(4), startDate.plusWeeks(6),
                startDate.plusWeeks(8), startDate.plusWeeks(10), startDate.plusWeeks(12), startDate.plusWeeks(14),
                startDate.plusWeeks(16), startDate.plusWeeks(18));
    }

    @Test
    public void withBiWeeklyMeetingNoFeesThirdDateInMoratorium() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        buildAndPersistHoliday(startDate.plusWeeks(4), startDate.plusWeeks(4).plusDays(3), RepaymentRuleTypes.REPAYMENT_MORATORIUM);
        MeetingBO biWeeklyMeeting = new MeetingBuilder().customerMeeting()
                                                      .weekly()
                                                      .every(2)
                                                      .startingToday()
                                                      .build();

        CenterBO center = new CenterBuilder().withMeeting(biWeeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();

        // exercise test
        customerService.createCenter(center, biWeeklyMeeting, new ArrayList<AccountFeesEntity>());

        // verification
        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(2), startDate.plusWeeks(6), startDate.plusWeeks(8),
                startDate.plusWeeks(10), startDate.plusWeeks(12), startDate.plusWeeks(14), startDate.plusWeeks(16),
                startDate.plusWeeks(18), startDate.plusWeeks(20));
    }

    @Test
    public void withBiWeeklyMeetingNoFeesThirdAndFifthDatesInMoratorium() {

        DateTime startDate = date(2010, 4, 5);
        DateTimeUtils.setCurrentMillisFixed(startDate.getMillis()); //Monday

        // setup
        buildAndPersistHoliday(startDate.plusWeeks(4), startDate.plusWeeks(4).plusDays(3), RepaymentRuleTypes.REPAYMENT_MORATORIUM);
        buildAndPersistHoliday(startDate.plusWeeks(8), startDate.plusWeeks(8).plusDays(3), RepaymentRuleTypes.REPAYMENT_MORATORIUM);
        MeetingBO biWeeklyMeeting = new MeetingBuilder().customerMeeting()
                                                      .weekly()
                                                      .every(2)
                                                      .startingToday()
                                                      .build();

        CenterBO center = new CenterBuilder().withMeeting(biWeeklyMeeting)
                                            .withName("Center-IntegrationTest")
                                            .with(sampleBranchOffice())
                                            .withLoanOfficer(testUser()).withUserContext()
                                            .build();

        // exercise test
        customerService.createCenter(center, biWeeklyMeeting, new ArrayList<AccountFeesEntity>());

        // verification
        validateDates(center.getGlobalCustNum(), startDate, startDate.plusWeeks(2), startDate.plusWeeks(6),
                startDate.plusWeeks(10), startDate.plusWeeks(12), startDate.plusWeeks(14), startDate.plusWeeks(16),
                startDate.plusWeeks(18), startDate.plusWeeks(20), startDate.plusWeeks(22));
    }


    private DateTime date (int year, int month, int day) {
        return new DateTime().withDate(year, month, day).toDateMidnight().toDateTime();
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

    private void validateDates (String centerSystemId, DateTime... expectedDates) {
        CenterBO retrievedCenter = customerDao.findCenterBySystemId(centerSystemId);
        Set<AccountActionDateEntity> actionDateEntities = retrievedCenter.getCustomerAccount().getAccountActionDates();
        List<DateTime> actualDates = new ArrayList<DateTime>();
        for (AccountActionDateEntity entity : actionDateEntities) {
            actualDates.add(new DateTime(entity.getActionDate()));
        }
        Collections.sort(actualDates);
        Assert.assertEquals(expectedDates.length, actualDates.size());
        for (short i = 0; i < actualDates.size(); i++) {

            Assert.assertEquals("Date " + i+1 + "'s date is wrong.",
                                expectedDates[i], actualDates.get(i));
        }
    }

    private Holiday buildAndPersistHoliday (DateTime start, DateTime through, RepaymentRuleTypes rule) {
        HolidayBO holiday = (HolidayBO) new HolidayBuilder().from(start)
                                                               .to(through)
                                                               .withRepaymentRule(rule).build();
        try {
            StaticHibernateUtil.startTransaction();
            genericDao.createOrUpdate(holiday);
            StaticHibernateUtil.commitTransaction();
        } catch (Exception e) {
            StaticHibernateUtil.rollbackTransaction();
            throw new MifosRuntimeException(e);
        } finally {
            StaticHibernateUtil.closeSession();
        }
        return holiday;
    }
}
