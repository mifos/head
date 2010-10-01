package org.mifos.accounts.loan.business.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.Money;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class InstallmentsValidatorTest {
    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private MifosCurrency dollarCurrency;
    private InstallmentsValidator installmentsValidator;
    private Locale locale;

    @Before
    public void setupAndInjectDependencies() {
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        dollarCurrency = new MifosCurrency(Short.valueOf("1"), "Dollar", BigDecimal.valueOf(1), "USD");
        installmentsValidator = new InstallmentsValidatorImpl();
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForMissingDueDate() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3)
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentsValidator.validate(asList(repaymentScheduleInstallment));
            fail("Should have thrown validation error");
        } catch (ServiceException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat((Integer) e.getValues()[0], is(3));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForInvalidDueDate() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3).withDueDate("abcd")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentsValidator.validate(asList(repaymentScheduleInstallment));
            fail("Should have thrown validation error");
        } catch (ServiceException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat((Integer) e.getValues()[0], is(3));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForDueDateInvalidFormat() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3).withDueDate("12/12/1912")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentsValidator.validate(asList(repaymentScheduleInstallment));
            fail("Should have thrown validation error");
        } catch (ServiceException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat((Integer) e.getValues()[0], is(3));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForDueDateInvalidContent() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3).withDueDate("31-Nov-2010")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentsValidator.validate(asList(repaymentScheduleInstallment));
            fail("Should have thrown validation error");
        } catch (ServiceException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat((Integer) e.getValues()[0], is(3));
        }
    }

    @Test
    public void shouldValidateVariableInstallmentScheduleForValidDueDate() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentsValidator.validate(asList(repaymentScheduleInstallment));
        } catch (ServiceException e) {
            fail("Should not have thrown validation error");
        }
    }
    
    @Test
    public void shouldValidateForDuplicateDates() {
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withInstallment(6).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withInstallment(31).withDueDate("27-Mar-2010")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        RepaymentScheduleInstallment installment4 = installmentBuilder.reset(locale).withInstallment(61).withDueDate("27-Mar-2010")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        RepaymentScheduleInstallment installment5 = installmentBuilder.reset(locale).withInstallment(62).withDueDate("27-Mar-2010")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentsValidator.validate(asList(installment1, installment2, installment3, installment4, installment5));
            fail("Should have thrown validation error");
        } catch (ServiceException e) {
            assertThat(e.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID));
            assertThat((Integer) e.getValues()[0], is(6));
            assertThat((Integer) e.getValues()[1], is(61));
            assertThat((Integer) e.getValues()[2], is(62));
        }
    }
}
