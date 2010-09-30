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

    @Before
    public void setupAndInjectDependencies() {
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(new Locale("en", "GB"));
        dollarCurrency = new MifosCurrency(Short.valueOf("1"), "Dollar", BigDecimal.valueOf(1), "USD");
        installmentsValidator = new InstallmentsValidatorImpl();
    }

    @Test
    public void testValidateVariableInstallmentScheduleForMissingDueDate() {
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
    public void testValidateVariableInstallmentScheduleForInvalidDueDate() {
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
    public void testValidateVariableInstallmentScheduleForDueDateInvalidFormat() {
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
    public void testValidateVariableInstallmentScheduleForDueDateInvalidContent() {
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
    public void testValidateVariableInstallmentScheduleForValidDueDate() {
        RepaymentScheduleInstallment repaymentScheduleInstallment = installmentBuilder.withInstallment(3).withDueDate("30-Nov-2010")
                .withPrincipal(new Money(dollarCurrency, "499.9")).withInterest(new Money(dollarCurrency, "22.1"))
                .withFees(new Money(dollarCurrency, "0.0")).withTotal("522.0").build();
        try {
            installmentsValidator.validate(asList(repaymentScheduleInstallment));
        } catch (ServiceException e) {
            fail("Should not have thrown validation error");
        }
    }
}
