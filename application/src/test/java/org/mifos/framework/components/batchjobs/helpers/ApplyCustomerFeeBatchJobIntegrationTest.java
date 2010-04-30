
package org.mifos.framework.components.batchjobs.helpers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.sampleBranchOffice;
import static org.mifos.framework.util.helpers.IntegrationTestObjectMother.testUser;

import java.util.Set;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.application.collectionsheet.persistence.CenterBuilder;
import org.mifos.application.collectionsheet.persistence.FeeBuilder;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
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
public class ApplyCustomerFeeBatchJobIntegrationTest {

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

        weeklyMeeting = new MeetingBuilder().customerMeeting().weekly().every(1).withStartDate(yesterday).build();
        IntegrationTestObjectMother.saveMeeting(weeklyMeeting);

        MeetingBuilder weeklyMeetingForFees = new MeetingBuilder().periodicFeeMeeting().weekly().every(1).withStartDate(yesterday);

        weeklyPeriodicFeeForCenterOnly = new FeeBuilder().appliesToCenterOnly()
                                                        .withFeeAmount("100.0")
                                                        .withName("Center Weekly Periodic Fee")
                                                        .with(weeklyMeetingForFees)
                                                        .with(sampleBranchOffice())
                                                        .build();
        IntegrationTestObjectMother.saveFee(weeklyPeriodicFeeForCenterOnly);

        center = new CenterBuilder().with(weeklyMeeting).withName("Center").with(sampleBranchOffice())
                .withLoanOfficer(testUser()).withFee(weeklyPeriodicFeeForCenterOnly).build();
        IntegrationTestObjectMother.createCenter(center, weeklyMeeting, weeklyPeriodicFeeForCenterOnly);
    }

    @After
    public void cleanDatabaseTablesAfterTest() {
        // NOTE: - only added to stop older integration tests failing due to brittleness
        databaseCleaner.clean();
    }

    @Test
    public void appliesPeriodicFeesToAllAccountsWithAnUpcomingUnpaidInstallmentAndApplicablePeriodicFees() throws Exception {

        // setup
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        Set<AccountFeesEntity> accountFees = center.getCustomerAccount().getAccountFees();
        for (AccountFeesEntity accountFee : accountFees) {
            accountFee.setLastAppliedDate(yesterday.toDate());
        }
        IntegrationTestObjectMother.update(center);

        // exercise test
        ApplyCustomerFeeTask applyCustomerFeeTask = new ApplyCustomerFeeTask();
        ApplyCustomerFeeHelper customerFeeHelper = (ApplyCustomerFeeHelper) applyCustomerFeeTask.getTaskHelper();
        customerFeeHelper.execute(Long.valueOf("0"));

        // verification
        center = customerDao.findCenterBySystemId(center.getGlobalCustNum());
        Set<AccountFeesEntity> allAccountFees = center.getCustomerAccount().getAccountFees();
        for (AccountFeesEntity periodicAccountFee : allAccountFees) {
            assertThat(periodicAccountFee.getFees().getFeeName(), is("Center Weekly Periodic Fee"));

            DateTime feeStartDate = new DateTime(weeklyPeriodicFeeForCenterOnly.getFeeFrequency().getFeeMeetingFrequency().getStartDate());
            assertThat(periodicAccountFee.getLastAppliedDate(), is(feeStartDate.plusWeeks(1).toDate()));
            // FIXME - #000001 - keithw - BATCHJOBS: a new installment is added to customer_fee_schedule and old one is still left!!
        }
    }
}
