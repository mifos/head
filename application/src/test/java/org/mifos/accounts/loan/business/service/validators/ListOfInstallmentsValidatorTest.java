package org.mifos.accounts.loan.business.service.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.platform.validations.ValidationException;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class ListOfInstallmentsValidatorTest {
    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private ListOfInstallmentsValidator listOfInstallmentsValidator;
    private Locale locale;

    @Before
    public void setupAndInjectDependencies() {
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        listOfInstallmentsValidator = new ListOfInstallmentsValidatorImpl();
    }

    @Test
    public void shouldValidateInstallmentsForDuplicateDueDates() {
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).withDueDateValue("30-Nov-2010").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withInstallment(2).withDueDateValue("30-Nov-2010").build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withInstallment(3).build();
        RepaymentScheduleInstallment installment4 = installmentBuilder.reset(locale).withInstallment(4).withDueDateValue("01-Jan-2011").build();
        RepaymentScheduleInstallment installment5 = installmentBuilder.reset(locale).withInstallment(5).withDueDateValue("31-Dec-2010").build();
        RepaymentScheduleInstallment installment6 = installmentBuilder.reset(locale).withInstallment(6).withDueDateValue("30-Nov-2010").build();
        RepaymentScheduleInstallment installment7 = installmentBuilder.reset(locale).withInstallment(7).withDueDateValue("31-Dec-2010").build();
        try {
            listOfInstallmentsValidator.validateDuplicateDueDates(asList(installment1, installment2, installment3, installment4, installment5, installment6, installment7));
            fail("Should have thrown validation error");
        } catch (ValidationException ve) {
            assertThat(ve.getKey(), is(AccountConstants.GENERIC_VALIDATION_ERROR));
            assertThat(ve.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = ve.getChildExceptions();
            assertThat(childExceptions.size(), is(2));
            ValidationException childException1 = childExceptions.get(0);
            assertThat(childException1.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_DUPLICATE));
            assertThat(childException1.getIdentifier(), is("[1, 2, 6]"));
            ValidationException childException2 = childExceptions.get(1);
            assertThat(childException2.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_DUPLICATE));
            assertThat(childException2.getIdentifier(), is("[5, 7]"));
        }
    }

    @Test
    public void shouldValidateInstallmentsForInvalidOrderOfDueDates() {
        RepaymentScheduleInstallment installment0 = installmentBuilder.reset(locale).withInstallment(0).build();
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).withDueDateValue("30-Nov-2010").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withInstallment(2).withDueDateValue("29-Nov-2010").build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withInstallment(3).build();
        RepaymentScheduleInstallment installment4 = installmentBuilder.reset(locale).withInstallment(4).withDueDateValue("01-Jan-2011").build();
        try {
            listOfInstallmentsValidator.validateOrderingOfDueDates(asList(installment0, installment1, installment2, installment3, installment4));
            fail("Should have thrown validation error");
        } catch (ValidationException ve) {
            assertThat(ve.getKey(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID_ORDER));
            assertThat(ve.getIdentifier(), is("2"));
        }
    }

}
