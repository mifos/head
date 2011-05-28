package org.mifos.accounts.loan.business.service.validators;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.platform.validations.ErrorEntry;
import org.mockito.runners.MockitoJUnitRunner;

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
        List<ErrorEntry> errorEntries = listOfInstallmentsValidator.validateDuplicateDueDates(
                asList(installment1, installment2, installment3, installment4, installment5, installment6, installment7));
        assertThat(errorEntries.size(), is(2));
        assertThat(errorEntries.get(0).getErrorCode(), is(AccountConstants.INSTALLMENT_DUEDATE_DUPLICATE));
        assertThat(errorEntries.get(0).getArgs(), is(Arrays.asList("[1, 2, 6]")));
        
        assertThat(errorEntries.get(1).getErrorCode(), is(AccountConstants.INSTALLMENT_DUEDATE_DUPLICATE));
        assertThat(errorEntries.get(1).getArgs(), is(Arrays.asList("[5, 7]")));
    }

    @Test
    public void shouldValidateInstallmentsForInvalidOrderOfDueDates() {
        RepaymentScheduleInstallment installment0 = installmentBuilder.reset(locale).withInstallment(0).build();
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).withDueDateValue("30-Nov-2010").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withInstallment(2).withDueDateValue("29-Nov-2010").build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withInstallment(3).build();
        RepaymentScheduleInstallment installment4 = installmentBuilder.reset(locale).withInstallment(4).withDueDateValue("01-Jan-2011").build();
        List<ErrorEntry> errorEntries = listOfInstallmentsValidator.validateOrderingOfDueDates(
                asList(installment0, installment1, installment2, installment3, installment4));
        assertThat(errorEntries.get(0).getErrorCode(), is(AccountConstants.INSTALLMENT_DUEDATE_INVALID_ORDER));
        assertThat(errorEntries.get(0).getArgs(), is(Arrays.asList("2")));
    }
}

