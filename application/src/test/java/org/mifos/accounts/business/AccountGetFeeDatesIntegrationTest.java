
package org.mifos.accounts.business;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.util.helpers.InstallmentDate;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.customers.business.CustomerAccountBO;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.persistence.CustomerDao;
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

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/integration-test-context.xml",
                                    "/org/mifos/config/resources/hibernate-daos.xml",
                                    "/org/mifos/config/resources/services.xml" })
public class AccountGetFeeDatesIntegrationTest {

    private static MifosCurrency oldDefaultCurrency;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    private MeetingBO weeklyMeeting;
    private AmountFeeBO weeklyPeriodicFeeForCenterOnly;
    private CenterBO center;
    private final DateTime yesterday = new DateTime().minusDays(1);

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
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Test
    public void getScheduledDatesForFeesForGivenCustomerForWeeklySchedules() throws Exception {

        // setup
        DateTime firstTuesdayInstallmentDate = new DateMidnight().toDateTime().withDate(2010, 4, 20);
        weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).withStartDate(firstTuesdayInstallmentDate).build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        MeetingBuilder weeklyMeetingForFees = new MeetingBuilder().periodicFeeMeeting().weekly().every(1).withStartDate(firstTuesdayInstallmentDate);

        weeklyPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly()
                                                        .withFeeAmount("100.0")
                                                        .withName("Center Weekly Periodic Fee")
                                                        .with(weeklyMeetingForFees)
                                                        .with(sampleBranchOffice())
                                                        .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForCenterOnly);

        center = new CenterBuilder().with(weeklyMeeting).withName("Center").with(sampleBranchOffice())
                .withLoanOfficer(testUser()).build();
        IntegrationTestObjectMother.createCenter(center, weeklyMeeting, weeklyPeriodicFeeForCenterOnly);

        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());

        DateTime meetingStartDate = firstTuesdayInstallmentDate.minusDays(7);
        MeetingBO feeMeetingFrequency = new MeetingBuilder().periodicFeeMeeting().weekly().every(1).occuringOnA(WeekDay.MONDAY).startingFrom(meetingStartDate.toDate()).build();

        InstallmentDate installment1 = new InstallmentDate(Short.valueOf("1"), firstTuesdayInstallmentDate.toDate());
        InstallmentDate installment2 = new InstallmentDate(Short.valueOf("2"), firstTuesdayInstallmentDate.plusWeeks(1).toDate());
        InstallmentDate installment3 = new InstallmentDate(Short.valueOf("3"), firstTuesdayInstallmentDate.plusWeeks(2).toDate());
        InstallmentDate installment4 = new InstallmentDate(Short.valueOf("4"), firstTuesdayInstallmentDate.plusWeeks(3).toDate());

        List<InstallmentDate> installmentDates = Arrays.asList(installment1, installment2, installment3, installment4);

        // exercise test
        CustomerAccountBO customerAccount = center.getCustomerAccount();
        List<Date> feeDates = customerAccount.getFeeDates(feeMeetingFrequency, installmentDates);

        // verification
        assertThat(feeDates.get(0), is(firstTuesdayInstallmentDate.toDate()));
        assertThat(feeDates.get(1), is(firstTuesdayInstallmentDate.plusWeeks(1).toDate()));
        assertThat(feeDates.get(2), is(firstTuesdayInstallmentDate.plusWeeks(2).toDate()));
        assertThat(feeDates.get(3), is(firstTuesdayInstallmentDate.plusWeeks(3).toDate()));
    }
}
