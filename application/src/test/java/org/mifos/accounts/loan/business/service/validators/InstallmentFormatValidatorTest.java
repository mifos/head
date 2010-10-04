package org.mifos.accounts.loan.business.service.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.exceptions.ValidationException;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class InstallmentFormatValidatorTest {
    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private MifosCurrency dollarCurrency;
    private InstallmentFormatValidator installmentFormatValidator;
    private Locale locale;

    @Before
    public void setupAndInjectDependencies() {
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        dollarCurrency = new MifosCurrency(Short.valueOf("1"), "Dollar", BigDecimal.valueOf(1), "USD");
        installmentFormatValidator = new InstallmentFormatValidatorImpl();
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForMissingDueDate() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3)
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentFormatValidator.validateDueDateFormat(repaymentScheduleInstallment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat(e.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForInvalidDueDate() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3).withDueDate("abcd")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentFormatValidator.validateDueDateFormat(repaymentScheduleInstallment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat(e.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForDueDateInvalidFormat() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3).withDueDate("12/12/1912")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentFormatValidator.validateDueDateFormat(repaymentScheduleInstallment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat(e.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForDueDateInvalidContent() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3).withDueDate("31-Nov-2010")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentFormatValidator.validateDueDateFormat(repaymentScheduleInstallment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat(e.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForValidDueDate() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentFormatValidator.validateDueDateFormat(repaymentScheduleInstallment);
        } catch (ValidationException e) {
            fail("Should not have thrown validation error");
        }
    }
}
