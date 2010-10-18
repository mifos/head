package org.mifos.accounts.loan.business.service.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.validations.ErrorEntry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class InstallmentRulesValidatorTest {
    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private MifosCurrency rupeeCurrency;
    private InstallmentRulesValidator installmentRulesValidator;
    private Locale locale;

    @Mock
    private FiscalCalendarRules fiscalCalendarRules;

    @Before
    public void setupAndInjectDependencies() {
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        rupeeCurrency = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        installmentRulesValidator = new InstallmentRulesValidatorImpl();
    }

    @Test
    public void shouldValidateInstallmentForDueDateSameAsDisburseDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDateValue("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        Date dateValue = getDate(installment, "30-Nov-2010");
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForDisbursementDate(asList(installment), dateValue);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_SAME_AS_DISBURSE_DATE, "3");
    }

    @Test
    public void shouldNotValidateInstallmentForDueDateAfterDisburseDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDateValue("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        Date dateValue = getDate(installment, "30-Sep-2010");
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForDisbursementDate(asList(installment), dateValue);
        assertThat(errorEntries.isEmpty(), is(true));
    }

    @Test
    public void shouldValidateInstallmentForDueDateBeforeDisburseDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDateValue("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        Date dateValue = getDate(installment, "30-Dec-2010");
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForDisbursementDate(asList(installment), dateValue);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_BEFORE_DISBURSE_DATE, "3");
    }

    @Test
    public void shouldValidateMinimumGapOfFiveDaysForVariableInstallments() {
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).withDueDateValue("01-Nov-2010").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withInstallment(2).withDueDateValue("06-Nov-2010").build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withInstallment(3).withDueDateValue("08-Nov-2010").build();
        RepaymentScheduleInstallment installment4 = installmentBuilder.reset(locale).withInstallment(4).withDueDateValue("14-Nov-2010").build();
        RepaymentScheduleInstallment installment5 = installmentBuilder.reset(locale).withInstallment(5).withDueDateValue("20-Nov-2010").build();
        RepaymentScheduleInstallment installment6 = installmentBuilder.reset(locale).withInstallment(6).withDueDateValue("30-Nov-2010").build();
        RepaymentScheduleInstallment installment7 = installmentBuilder.reset(locale).withInstallment(7).withDueDateValue("01-Dec-2010").build();
        VariableInstallmentDetailsBO variableInstallmentDetails = getVariableInstallmentDetails(5, null, 100, rupeeCurrency);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4, installment5, installment6, installment7);
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForVariableInstallments(installments, variableInstallmentDetails);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP, "3");
        assertErrorEntry(errorEntries.get(1), AccountConstants.INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP, "7");
    }

    @Test
    public void shouldValidateMaximumGapOfFiveDaysForVariableInstallments() {
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).withDueDateValue("01-Nov-2010").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withInstallment(2).withDueDateValue("06-Nov-2010").build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withInstallment(3).withDueDateValue("08-Nov-2010").build();
        RepaymentScheduleInstallment installment4 = installmentBuilder.reset(locale).withInstallment(4).withDueDateValue("14-Nov-2010").build();
        RepaymentScheduleInstallment installment5 = installmentBuilder.reset(locale).withInstallment(5).withDueDateValue("20-Nov-2010").build();
        RepaymentScheduleInstallment installment6 = installmentBuilder.reset(locale).withInstallment(6).withDueDateValue("30-Nov-2010").build();
        RepaymentScheduleInstallment installment7 = installmentBuilder.reset(locale).withInstallment(7).withDueDateValue("01-Dec-2010").build();
        VariableInstallmentDetailsBO variableInstallmentDetails = getVariableInstallmentDetails(null, 5, 100, rupeeCurrency);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4, installment5, installment6, installment7);
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForVariableInstallments(installments, variableInstallmentDetails);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, "4");
        assertErrorEntry(errorEntries.get(1), AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, "5");
        assertErrorEntry(errorEntries.get(2), AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, "6");
    }

    @Test
    public void shouldValidateMinInstallmentForVariableInstallments() {
        RepaymentScheduleInstallment installment = installmentBuilder.reset(locale).withInstallment(1).
                withPrincipal(new Money(rupeeCurrency, "49")).withTotalValue("50").build();
        VariableInstallmentDetailsBO variableInstallmentDetails = getVariableInstallmentDetails(null, null, 100, rupeeCurrency);
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForVariableInstallments(asList(installment), variableInstallmentDetails);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_AMOUNT_LESS_THAN_MIN_AMOUNT, "1");
    }

    @Test
    public void shouldNotValidateForValidVariableInstallments() {
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).
                withPrincipal(new Money(rupeeCurrency, "49")).withTotalValue("50").withDueDateValue("01-Nov-2010").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withInstallment(2).
                withPrincipal(new Money(rupeeCurrency, "49")).withTotalValue("50").withDueDateValue("03-Nov-2010").build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withInstallment(3).
                withPrincipal(new Money(rupeeCurrency, "49")).withTotalValue("50").withDueDateValue("06-Nov-2010").build();
        VariableInstallmentDetailsBO variableInstallmentDetails = getVariableInstallmentDetails(2, 5, 50, rupeeCurrency);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3);
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForVariableInstallments(installments, variableInstallmentDetails);
        assertThat(errorEntries.isEmpty(), is(true));
    }

    @Test
    public void shouldValidateForDueDateFallingOnHoliday() {
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).withDueDateValue("01-Nov-2010").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withInstallment(2).build();
        Calendar holiday = installment1.getDueDateValueAsCalendar();
        when(fiscalCalendarRules.isWorkingDay(holiday)).thenReturn(false);
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForHolidays(asList(installment1, installment2), fiscalCalendarRules);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_IS_HOLIDAY, "1");
        verify(fiscalCalendarRules, times(1)).isWorkingDay(holiday);
    }

    private void assertErrorEntry(ErrorEntry errorEntry, String errorCode, String fieldName) {
        assertThat(errorEntry.getErrorCode(), is(errorCode));
        assertThat(errorEntry.getFieldName(), is(fieldName));
    }

    private Date getDate(RepaymentScheduleInstallment installment, String dateValue) {
        Locale dateLocale = installment.getLocale();
        String dateFormat = installment.getDateFormat();
        return DateUtils.getDate(dateValue, dateLocale, dateFormat);
    }

    private VariableInstallmentDetailsBO getVariableInstallmentDetails(Integer minGapInDays, Integer maxGapInDays, Integer minInstAmount, MifosCurrency currency) {
        VariableInstallmentDetailsBO variableInstallmentDetails = new VariableInstallmentDetailsBO();
        variableInstallmentDetails.setMinGapInDays(minGapInDays);
        variableInstallmentDetails.setMaxGapInDays(maxGapInDays);
        Money installmentAmount = new Money(currency, String.valueOf(minInstAmount));
        variableInstallmentDetails.setMinInstallmentAmount(installmentAmount);
        return variableInstallmentDetails;
    }

}
