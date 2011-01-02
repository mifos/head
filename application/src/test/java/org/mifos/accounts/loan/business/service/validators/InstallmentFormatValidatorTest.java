package org.mifos.accounts.loan.business.service.validators;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.validations.ErrorEntry;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InstallmentFormatValidatorTest {
    private RepaymentScheduleInstallmentBuilder installmentBuilder;
    private MifosCurrency rupeeCurrency;
    private InstallmentFormatValidator installmentFormatValidator;

    @Before
    public void setupAndInjectDependencies() {
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(new Locale("en", "GB"));
        rupeeCurrency = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        installmentFormatValidator = new InstallmentFormatValidatorImpl();
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForMissingDueDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3)
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        assertForValidDueDate(installment, "3");
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForInvalidDueDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("abcd")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        assertForValidDueDate(installment, "3");
    }

    private void assertForValidDueDate(RepaymentScheduleInstallment installment, String installmentNo) {
        List<ErrorEntry> errorEntries = installmentFormatValidator.validateDueDateFormat(installment);
        assertError(errorEntries.get(0), installmentNo, AccountConstants.INSTALLMENT_DUEDATE_INVALID);
    }

    private void assertForValidTotalAmount(RepaymentScheduleInstallment installment, String installmentNo) {
        List<ErrorEntry> errorEntries = installmentFormatValidator.validateTotalAmountFormat(installment);
        assertError(errorEntries.get(0), installmentNo, AccountConstants.INSTALLMENT_TOTAL_AMOUNT_INVALID);
    }

    private void assertError(ErrorEntry errorEntry, String installmentNo, String errorCode) {
        assertThat(errorEntry.getErrorCode(), is(errorCode));
        assertThat(errorEntry.getFieldName(), is(installmentNo));
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForDueDateInvalidFormat() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("12/12/1912")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        assertForValidDueDate(installment, "3");
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForDueDateInvalidContent() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("31-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        assertForValidDueDate(installment, "3");
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForValidDueDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        List<ErrorEntry> errorEntries = installmentFormatValidator.validateDueDateFormat(installment);
        assertThat(errorEntries.isEmpty(), is(true));
    }

    @Test
    public void shouldValidateInstallmentScheduleForMissingTotalAmount() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("").build();
        assertForValidTotalAmount(installment, "3");
    }

    @Test
    public void shouldValidateInstallmentScheduleForInvalidTotalAmount() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("abcd").build();
        assertForValidTotalAmount(installment, "3");
    }

    @Test
    public void shouldValidateInstallmentScheduleForTotalAmountWithMoreThanOneDecimalPoint() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("499.9.22.1").build();
        assertForValidTotalAmount(installment, "3");
    }

    @Test
    public void shouldNotValidateInstallmentScheduleForTotalAmountLessThanZero() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("-499.9").build();
        List<ErrorEntry> errorEntries = installmentFormatValidator.validateTotalAmountFormat(installment);
        assertThat(errorEntries.isEmpty(), is(true));
    }

    @Test
    public void shouldNotValidateInstallmentScheduleForValidTotalAmount() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("499.9").build();
        List<ErrorEntry> errorEntries = installmentFormatValidator.validateTotalAmountFormat(installment);
        assertThat(errorEntries.isEmpty(), is(true));
    }
}
