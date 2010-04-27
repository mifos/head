package org.mifos.accounts.util.helper;

    import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Test;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.fees.business.AmountFeeBO;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.fees.util.helpers.FeeFrequencyType;
import org.mifos.accounts.fees.util.helpers.FeeStatus;
import org.mifos.accounts.financial.business.GLCodeEntity;
import org.mifos.accounts.util.helpers.FeeInstallment;
import org.mifos.application.collectionsheet.persistence.MeetingBuilder;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.meeting.util.helpers.MeetingType;
import org.mifos.application.meeting.util.helpers.RecurrenceType;
import org.mifos.domain.builders.ScheduledEventBuilder;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.schedule.ScheduledEvent;

public class FeeInstallmentTest {

    /*************************************
     * These tests verify that fees are correctly merged for a single fee on an account.
     ****************************************/
    @Test
    public void createMergedFeeInstallmentsForOneFeeBothAccountAndFeeAreScheduledEveryWeekShouldGetOneFeeInstallmentPerAccountEvent() {
        ScheduledEvent masterEvent = new ScheduledEventBuilder().every(1).weeks().build();
        FeeBO feeBO = createWeeklyFeeBO(1);
        AccountFeesEntity accountFeesEntity = createAccountFeesEntity(feeBO, 10.0);

       List<FeeInstallment> feeInstallments = FeeInstallment
                                                    .createMergedFeeInstallmentsForOneFee(masterEvent,
                                                                                          accountFeesEntity, 3);
        assertThat(feeInstallments.size(), is(3));
        assertFeeInstallment(feeInstallments.get(0), 1, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(1), 2, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(2), 3, 10.0, feeBO);

    }

    @Test
    public void createMergedFeeInstallmentsForOneFeeAccountScheduledEveryWeekFeeScheduledEveryOtherWeekShouldGetOneFeeInstallmentPerEveryOtherAccountEvent() {
        ScheduledEvent accountEvent = new ScheduledEventBuilder().every(1).weeks().build();
        FeeBO feeBO = createWeeklyFeeBO(2);
        AccountFeesEntity accountFeesEntity = createAccountFeesEntity(feeBO, 10.0);

        List<FeeInstallment> feeInstallments = FeeInstallment
                                                    .createMergedFeeInstallmentsForOneFee(accountEvent,
                                                                                          accountFeesEntity, 4);
        assertThat(feeInstallments.size(), is(2));
        assertFeeInstallment(feeInstallments.get(0), 1, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(1), 3, 10.0, feeBO);

    }

    @Test
    public void createMergedFeeInstallmentsForOneFeeAccountScheduledEveryOtherWeekFeeScheduledEveryWeekt() {
        ScheduledEvent masterEvent = new ScheduledEventBuilder().every(2).weeks().build();
        FeeBO feeBO = createWeeklyFeeBO(1);
        AccountFeesEntity accountFeesEntity = createAccountFeesEntity(feeBO, 10.0);

        List<FeeInstallment> feeInstallments = FeeInstallment
                                                    .createMergedFeeInstallmentsForOneFee(masterEvent,
                                                                                          accountFeesEntity, 4);
        assertThat(feeInstallments.size(), is(4));
        assertFeeInstallment(feeInstallments.get(0), 1, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(1), 2, 20.0, feeBO);
        assertFeeInstallment(feeInstallments.get(2), 3, 20.0, feeBO);
        assertFeeInstallment(feeInstallments.get(3), 4, 20.0, feeBO);

    }

    @Test
    public void createMergedFeeInstallmentsForOneFeeAccountScheduledEverySecondWeekFeeScheduledEveryThirdWeek() {
        ScheduledEvent masterEvent = new ScheduledEventBuilder().every(2).weeks().build();
        FeeBO feeBO = createWeeklyFeeBO(3);
        AccountFeesEntity accountFeesEntity = createAccountFeesEntity(feeBO, 10.0);

        List<FeeInstallment> feeInstallments = FeeInstallment
                                                    .createMergedFeeInstallmentsForOneFee(masterEvent,
                                                                                          accountFeesEntity, 7);
        assertThat(feeInstallments.size(), is(5));
        assertFeeInstallment(feeInstallments.get(0), 1, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(1), 3, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(2), 4, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(3), 6, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(4), 7, 10.0, feeBO);

    }

    @Test
    public void createMergedFeeInstallmentsForOneFeeAccountScheduledEveryThirdWeekFeeScheduledEverySecondWeek() {
        ScheduledEvent accountScheduledEvent = new ScheduledEventBuilder().every(3).weeks().build();
        FeeBO feeBO = createWeeklyFeeBO(2);
        AccountFeesEntity accountFeesEntity = createAccountFeesEntity(feeBO, 10.0);

        List<FeeInstallment> feeInstallments = FeeInstallment
                                                    .createMergedFeeInstallmentsForOneFee(accountScheduledEvent,
                                                                                          accountFeesEntity, 4);

        assertThat(feeInstallments.size(), is(4));
        assertFeeInstallment(feeInstallments.get(0), 1, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(1), 2, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(2), 3, 20.0, feeBO);
        assertFeeInstallment(feeInstallments.get(3), 4, 10.0, feeBO);

    }

    @Test
    public void createMergedFeeInstallmentsForOneFeeAccountScheduledEveryThirdMonthFeeScheduledEverySecondMonth()
                throws Exception {
        ScheduledEvent accountScheduledEvent = new ScheduledEventBuilder().every(3).months().onDayOfMonth(3).build();
        FeeBO feeBO = createMonthlyOnDayFeeBO(2);
        AccountFeesEntity accountFeesEntity = createAccountFeesEntity(feeBO, 10.0);

        List<FeeInstallment> feeInstallments = FeeInstallment
                                                    .createMergedFeeInstallmentsForOneFee(accountScheduledEvent,
                                                                                          accountFeesEntity, 4);

        assertThat(feeInstallments.size(), is(4));
        assertFeeInstallment(feeInstallments.get(0), 1, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(1), 2, 10.0, feeBO);
        assertFeeInstallment(feeInstallments.get(2), 3, 20.0, feeBO);
        assertFeeInstallment(feeInstallments.get(3), 4, 10.0, feeBO);

    }

    /**********************************
     * Tests verify merging fees when two or more fees are attached to the account
     *
     * TODO: KRP: Remove the assumption of order that fee installments appear in the created list.
     **********************************/
    @Test
    public void createMergedFeeInstallmentsForTwoFeesBothAccountAndFeesAreScheduledEveryWeekShouldGetOneFeeInstallmentPerAccountEventPerFee() {
        ScheduledEvent masterEvent = new ScheduledEventBuilder().every(1).weeks().build();
        FeeBO feeBO1 = createWeeklyFeeBO(1);
        FeeBO feeBO2 = createWeeklyFeeBO(1);
        AccountFeesEntity accountFeesEntity1 = createAccountFeesEntity(feeBO1, 10.0);
        AccountFeesEntity accountFeesEntity2 = createAccountFeesEntity(feeBO2, 13.0);

        List<AccountFeesEntity> accountFees = Arrays.asList(new AccountFeesEntity[]{accountFeesEntity1,accountFeesEntity2});

       List<FeeInstallment> feeInstallments = FeeInstallment
                                                    .createMergedFeeInstallments(
                                                            masterEvent,
                                                            accountFees, 3);
        assertThat(feeInstallments.size(), is(6));
        assertFeeInstallment(feeInstallments.get(0), 1, 10.0, feeBO1);
        assertFeeInstallment(feeInstallments.get(1), 2, 10.0, feeBO1);
        assertFeeInstallment(feeInstallments.get(2), 3, 10.0, feeBO1);
        assertFeeInstallment(feeInstallments.get(3), 1, 13.0, feeBO2);
        assertFeeInstallment(feeInstallments.get(4), 2, 13.0, feeBO2);
        assertFeeInstallment(feeInstallments.get(5), 3, 13.0, feeBO2);

    }

    /**
     * Expected results:
     * | week           |  1 |  2 |  3 |  4 |  5 |  6 |  7 |  8 |  9 | 10 | 11 | 12 |
     * | account event  |    |  1 |    |  2 |    |  3 |    |  4 |    |  5 |    |  6 |
     * | fee1 event     |  x |  x |  x |  x |  x |  x |  x |  x |  x |  x |  x |  x |
     * | fee2 event     |    |    |  y |    |    |  y |    |    |  y |    |    |  y |
     */
    @Test
    public void createMergedFeeInstallmentsForTwoFeesAccountScheduledBiWeeklyFee1ScheduledWeeklyFee2ScheduledEveryThreeWeeks() {
        ScheduledEvent masterEvent = new ScheduledEventBuilder().every(2).weeks().build();
        FeeBO feeBO1 = createWeeklyFeeBO(1);
        FeeBO feeBO2 = createWeeklyFeeBO(3);
        AccountFeesEntity accountFeesEntity1 = createAccountFeesEntity(feeBO1, 10.0);
        AccountFeesEntity accountFeesEntity2 = createAccountFeesEntity(feeBO2, 13.0);

        List<AccountFeesEntity> accountFees = Arrays.asList(new AccountFeesEntity[]{accountFeesEntity1,accountFeesEntity2});

       List<FeeInstallment> feeInstallments = FeeInstallment
                                                    .createMergedFeeInstallments(
                                                            masterEvent,
                                                            accountFees, 6);
        assertThat(feeInstallments.size(), is(10));
        assertFeeInstallment(feeInstallments.get(0), 1, 10.0, feeBO1);
        assertFeeInstallment(feeInstallments.get(1), 2, 20.0, feeBO1);
        assertFeeInstallment(feeInstallments.get(2), 3, 20.0, feeBO1);
        assertFeeInstallment(feeInstallments.get(3), 4, 20.0, feeBO1);
        assertFeeInstallment(feeInstallments.get(4), 5, 20.0, feeBO1);
        assertFeeInstallment(feeInstallments.get(5), 6, 20.0, feeBO1);
        assertFeeInstallment(feeInstallments.get(6), 1, 13.0, feeBO2);
        assertFeeInstallment(feeInstallments.get(7), 3, 13.0, feeBO2);
        assertFeeInstallment(feeInstallments.get(8), 4, 13.0, feeBO2);
        assertFeeInstallment(feeInstallments.get(9), 6, 13.0, feeBO2);

    }

    private AccountBO createAccountBOForTesting() {
        return new AccountBO() {
            @Override
            public MifosCurrency getCurrency() {
                return TestUtils.RUPEE;
            }
        };
    }

    private void assertFeeInstallment (FeeInstallment feeInstallment, int installmentId,
            double expectedMergedFeeAmount, FeeBO expectedFeeBO) {
        assertThat(feeInstallment.getAccountFee(), is(new Money(TestUtils.RUPEE, new BigDecimal(expectedMergedFeeAmount))));
        assertThat(feeInstallment.getInstallmentId(), is((short) (installmentId)));
        assertThat(feeInstallment.getAccountFee().getAmountDoubleValue(), is(expectedMergedFeeAmount));
        assertThat(feeInstallment.getAccountFeesEntity().getFees(), is(expectedFeeBO));

    }

    private FeeBO createWeeklyFeeBO (int every) {
        MeetingBO feeMeeting = new MeetingBuilder().every(every).weekly().build();
        FeeBO feeBO = new AmountFeeBO(new Money(TestUtils.RUPEE, "10.0"), "Fee", FeeCategory.ALLCUSTOMERS,
                FeeFrequencyType.PERIODIC, new GLCodeEntity(Short.valueOf("1"), "10000"),
                feeMeeting, null, new DateTime().minusDays(14).toDate(), TestUtils.makeUserWithLocales().getId());
        return feeBO;
    }

    private FeeBO createMonthlyOnDayFeeBO (int every) throws Exception {
        MeetingBO feeMeeting = new MeetingBO(RecurrenceType.MONTHLY, (short) every, new DateTime().toDate(), MeetingType.PERIODIC_FEE);
        FeeBO feeBO = new AmountFeeBO(new Money(TestUtils.RUPEE, "10.0"), "Fee", FeeCategory.ALLCUSTOMERS,
                FeeFrequencyType.PERIODIC, new GLCodeEntity(Short.valueOf("1"), "10000"),
                feeMeeting, null, new DateTime().minusDays(14).toDate(), TestUtils.makeUserWithLocales().getId());
        return feeBO;
    }

    private AccountFeesEntity createAccountFeesEntity (FeeBO feeBO, double feeAmount) {
        AccountFeesEntity accountFeesEntity
            = new AccountFeesEntity(createAccountBOForTesting(), feeBO, feeAmount,
                FeeStatus.ACTIVE.getValue(), new DateTime().toDate(), null);
        return accountFeesEntity;
    }
}
