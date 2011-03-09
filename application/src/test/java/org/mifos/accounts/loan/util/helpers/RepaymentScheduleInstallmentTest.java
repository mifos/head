package org.mifos.accounts.loan.util.helpers;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class RepaymentScheduleInstallmentTest {
    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private MifosCurrency rupee;

    private Locale locale;

    @Before
    public void setUp() throws Exception {
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
    }

    @Test
    public void shouldValidateTotalAmount() {
        RepaymentScheduleInstallment installment = getRepaymentScheduleInstallment("25-Sep-2010", 1, "178.6", "20.4", "1", "10", "0");
        assertThat(installment.isTotalAmountInValid(), is(true));
        installment.setInterest(null);
        assertThat(installment.isTotalAmountInValid(), is(false));
        installment = getRepaymentScheduleInstallment("25-Sep-2010", 1, "178.6", "20.4", "1", "-10", "0");
        installment.setInterest(null);
        assertThat(installment.isTotalAmountInValid(), is(true));
        installment = getRepaymentScheduleInstallment("25-Sep-2010", 1, "178.6", "20.4", "1", "21.4", "0");
        assertThat(installment.isTotalAmountInValid(), is(false));
    }


    @Test
    public void shouldTestWhetherTotalAmountLessThanSpecifiedAmount() {
        RepaymentScheduleInstallment installment = getRepaymentScheduleInstallment("25-Sep-2010", 1, "178.6", "20.4", "1", "10", "0");
        assertThat(installment.isTotalAmountLessThan(BigDecimal.ZERO), is(false));
        assertThat(installment.isTotalAmountLessThan(asMoney("100")), is(true));
        assertThat(installment.isTotalAmountLessThan(asMoney("5")), is(false));
        assertThat(installment.isTotalAmountLessThan(asMoney("10")), is(false));
    }

    @Test
    public void shouldTestIfFeeWithMiscFeeIsComputedCorrectly(){
        RepaymentScheduleInstallment installment = getRepaymentScheduleInstallment("25-Sep-2010", 1, "178.6", "20.4", "1", "300", "100");
        assertThat(installment.isTotalAmountLessThan(BigDecimal.ZERO), is(false));
        assertThat(installment.getFeesWithMiscFee(),is(asMoney("101")));
        assertThat(installment.getFees(),is(asMoney("1")));
        assertThat(installment.getMiscFees(),is(asMoney("100")));
    }


    private RepaymentScheduleInstallment getRepaymentScheduleInstallment(String dueDate, int installment,
                                                                         String principal, String interest,
                                                                         String fees, String total, String miscFee) {
        return installmentBuilder.reset(locale).withInstallment(installment).withDueDateValue(dueDate).
                withPrincipal(asMoney(principal)).withInterest(asMoney(interest)).
                withFees(asMoney(fees)).withMiscFee(asMoney(miscFee)).withTotalValue(total).build();
    }

    private Money asMoney(String amount) {
        return new Money(rupee, amount);
    }
}
