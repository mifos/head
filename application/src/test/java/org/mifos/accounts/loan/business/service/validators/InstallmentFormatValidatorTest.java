package org.mifos.accounts.loan.business.service.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.validations.ValidationException;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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
        try {
            installmentFormatValidator.validateDueDateFormat(installment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat(e.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForInvalidDueDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("abcd")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentFormatValidator.validateDueDateFormat(installment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat(e.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForDueDateInvalidFormat() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("12/12/1912")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentFormatValidator.validateDueDateFormat(installment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat(e.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForDueDateInvalidContent() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("31-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentFormatValidator.validateDueDateFormat(installment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat(e.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForValidDueDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentFormatValidator.validateDueDateFormat(installment);
        } catch (ValidationException e) {
            fail("Should not have thrown validation error");
        }
    }
    
    @Test
    public void shouldValidateInstallmentScheduleForMissingTotalAmount() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("").build();
        try {
            installmentFormatValidator.validateTotalAmountFormat(installment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_TOTAL_AMOUNT_INVALID));
            assertThat(e.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateInstallmentScheduleForInvalidTotalAmount() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("abcd").build();
        try {
            installmentFormatValidator.validateTotalAmountFormat(installment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.GENERIC_VALIDATION_ERROR));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            ValidationException childException = childExceptions.get(0);
            assertThat(childException.getKey(), is(AccountConstants.INSTALLMENT_TOTAL_AMOUNT_INVALID));
            assertThat(childException.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateInstallmentScheduleForTotalAmountWithMoreThanOneDecimalPoint() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("499.9.22.1").build();
        try {
            installmentFormatValidator.validateTotalAmountFormat(installment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.GENERIC_VALIDATION_ERROR));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            ValidationException childException = childExceptions.get(0);
            assertThat(childException.getKey(), is(AccountConstants.INSTALLMENT_TOTAL_AMOUNT_INVALID));
            assertThat(childException.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldValidateInstallmentScheduleForTotalAmountWithLessThanZero() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("-499.9").build();
        try {
            installmentFormatValidator.validateTotalAmountFormat(installment);
            fail("Should have thrown validation error");
        } catch (ValidationException e) {
            assertThat(e.getKey(), is(AccountConstants.GENERIC_VALIDATION_ERROR));
            assertThat(e.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = e.getChildExceptions();
            ValidationException childException = childExceptions.get(0);
            assertThat(childException.getKey(), is(AccountConstants.INSTALLMENT_TOTAL_AMOUNT_INVALID));
            assertThat(childException.getIdentifier(), is("3"));
        }
    }

    @Test
    public void shouldNotValidateInstallmentScheduleForValidTotalAmount() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("499.9").build();
        try {
            installmentFormatValidator.validateTotalAmountFormat(installment);
        } catch (ValidationException e) {
            fail("Should not have thrown validation error");
        }
    }
}
