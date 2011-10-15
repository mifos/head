package org.mifos.accounts.loan.business.service.validators;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.validations.ErrorEntry;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class InstallmentRulesValidatorTest {
    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private MifosCurrency rupee;
    private InstallmentRulesValidator installmentRulesValidator;
    private Locale locale;
    private Short officeId;

    @Mock
    private HolidayServiceFacade holidayServiceFacade;

    @Before
    public void setupAndInjectDependencies() {
        locale = new Locale("en", "GB");
        installmentBuilder = new RepaymentScheduleInstallmentBuilder(locale);
        rupee = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        installmentRulesValidator = new InstallmentRulesValidatorImpl();
        officeId = Short.valueOf("1");
    }

    @Test
    public void shouldValidateInstallmentForDueDateSameAsDisburseDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDateValue("30-Nov-2010")
                .withPrincipal(new Money(rupee, "499.9")).withInterest(new Money(rupee, "22.1"))
                .withFees(new Money(rupee, "0.0")).withTotal("522.0").build();
        Date dateValue = getDate(installment, "30-Nov-2010");
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForDisbursementDate(asList(installment), dateValue);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_SAME_AS_DISBURSE_DATE, "3");
    }

    @Test
    public void shouldNotValidateInstallmentForDueDateAfterDisburseDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDateValue("30-Nov-2010")
                .withPrincipal(new Money(rupee, "499.9")).withInterest(new Money(rupee, "22.1"))
                .withFees(new Money(rupee, "0.0")).withTotal("522.0").build();
        Date dateValue = getDate(installment, "30-Sep-2010");
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForDisbursementDate(asList(installment), dateValue);
        assertThat(errorEntries.isEmpty(), is(true));
    }

    @Test
    public void shouldValidateInstallmentForDueDateBeforeDisburseDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDateValue("30-Nov-2010")
                .withPrincipal(new Money(rupee, "499.9")).withInterest(new Money(rupee, "22.1"))
                .withFees(new Money(rupee, "0.0")).withTotal("522.0").build();
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
        VariableInstallmentDetailsBO variableInstallmentDetails = getVariableInstallmentDetails(5, null, 100, rupee);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4, installment5, installment6, installment7);
        Date disbursementDate = getDate(installment1, "27-Oct-2010");
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateDueDatesForVariableInstallments(installments,
                                    variableInstallmentDetails, disbursementDate);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP, "3");
        assertErrorEntry(errorEntries.get(1), AccountConstants.INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP, "7");
    }

    @Test
    public void shouldValidateMinimumGapOfFiveDaysForFirstInstallmentAndDisbursementDate() {
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).withDueDateValue("01-Nov-2010").build();
        VariableInstallmentDetailsBO variableInstallmentDetails = getVariableInstallmentDetails(5, null, 100, rupee);
        List<RepaymentScheduleInstallment> installments = asList(installment1);
        Date disbursementDate = getDate(installment1, "30-Nov-2010");
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateDueDatesForVariableInstallments(installments,
                variableInstallmentDetails, disbursementDate);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP, "1");
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
        VariableInstallmentDetailsBO variableInstallmentDetails = getVariableInstallmentDetails(null, 5, 100, rupee);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4, installment5, installment6, installment7);
        Date disbursementDate = getDate(installment1, "27-Oct-2010");
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateDueDatesForVariableInstallments(installments,
                variableInstallmentDetails, disbursementDate);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, "4");
        assertErrorEntry(errorEntries.get(1), AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, "5");
        assertErrorEntry(errorEntries.get(2), AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, "6");
    }

    @Test
    public void shouldValidateMaximumGapOfFiveDaysForFirstInstallmentAndDisbursementDate() {
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).withDueDateValue("01-Nov-2010").build();
        VariableInstallmentDetailsBO variableInstallmentDetails = getVariableInstallmentDetails(null, 5, 100, rupee);
        List<RepaymentScheduleInstallment> installments = asList(installment1);
        Date disbursementDate = getDate(installment1, "25-Oct-2010");
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateDueDatesForVariableInstallments(installments,
                variableInstallmentDetails, disbursementDate);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, "1");
    }

    @Test
    public void shouldValidateMinInstallmentForVariableInstallments() {
        RepaymentScheduleInstallment installment = installmentBuilder.reset(locale).withInstallment(1).
                withPrincipal(new Money(rupee, "49")).withTotalValue("50").build();
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForMinimumInstallmentAmount(asList(installment), BigDecimal.valueOf(Double.valueOf("100.0")));
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_AMOUNT_LESS_THAN_MIN_AMOUNT, "1");
    }

    @Test
    public void shouldNotValidateForValidVariableInstallments() {
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).
                withPrincipal(new Money(rupee, "49")).withTotalValue("50").withDueDateValue("01-Nov-2010").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withInstallment(2).
                withPrincipal(new Money(rupee, "49")).withTotalValue("50").withDueDateValue("03-Nov-2010").build();
        RepaymentScheduleInstallment installment3 = installmentBuilder.reset(locale).withInstallment(3).
                withPrincipal(new Money(rupee, "49")).withTotalValue("50").withDueDateValue("06-Nov-2010").build();
        VariableInstallmentDetailsBO variableInstallmentDetails = getVariableInstallmentDetails(2, 5, 50, rupee);
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3);
        Date disbursementDate = getDate(installment1, "27-Oct-2010");
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateDueDatesForVariableInstallments(installments,
                variableInstallmentDetails, disbursementDate);
        assertThat(errorEntries.isEmpty(), is(true));
    }

    @Test
    public void shouldValidateForDueDateFallingOnHoliday() {
        RepaymentScheduleInstallment installment1 = installmentBuilder.reset(locale).withInstallment(1).withDueDateValue("01-Nov-2010").build();
        RepaymentScheduleInstallment installment2 = installmentBuilder.reset(locale).withInstallment(2).build();
        Calendar holiday = installment1.getDueDateValueAsCalendar();
        when(holidayServiceFacade.isFutureRepaymentHoliday(officeId, holiday)).thenReturn(true);
        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForHolidays(asList(installment1, installment2), holidayServiceFacade, officeId);
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_DUEDATE_IS_HOLIDAY, "1");
        verify(holidayServiceFacade, times(1)).isFutureRepaymentHoliday(officeId, holiday);
    }

    @Test
    public void shouldValidateForMinimumInstallmentAmount() {
        RepaymentScheduleInstallment installment1 =
                getRepaymentScheduleInstallment("01-Sep-2010", 1, "194.4", "4.6", "1", "5");
        RepaymentScheduleInstallment installment2 =
                getRepaymentScheduleInstallment("08-Sep-2010", 2, "195.3", "3.7", "1", "4");
        RepaymentScheduleInstallment installment3 =
                getRepaymentScheduleInstallment("15-Sep-2010", 3, "196.2", "2.8", "1", "3");
        RepaymentScheduleInstallment installment4 =
                getRepaymentScheduleInstallment("22-Sep-2010", 4, "414.1", "1.9", "1", "417.0");
        List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4);

        List<ErrorEntry> errorEntries = installmentRulesValidator.validateForMinimumInstallmentAmount(installments, BigDecimal.valueOf(Double.valueOf("50.0")));
        assertThat(errorEntries.size(), is(3));
        assertErrorEntry(errorEntries.get(0), AccountConstants.INSTALLMENT_AMOUNT_LESS_THAN_INTEREST_FEE, "1");
        assertErrorEntry(errorEntries.get(1), AccountConstants.INSTALLMENT_AMOUNT_LESS_THAN_INTEREST_FEE, "2");
        assertErrorEntry(errorEntries.get(2), AccountConstants.INSTALLMENT_AMOUNT_LESS_THAN_INTEREST_FEE, "3");
    }

    private RepaymentScheduleInstallment getRepaymentScheduleInstallment(String dueDate, int installment,
                                                                         String principal, String interest,
                                                                         String fees, String total) {
        return installmentBuilder.reset(locale).withInstallment(installment).withDueDateValue(dueDate).
                withPrincipal(new Money(rupee, principal)).withInterest(new Money(rupee, interest)).
                withFees(new Money(rupee, fees)).withTotalValue(total).build();
    }

    private void assertErrorEntry(ErrorEntry errorEntry, String errorCode, String fieldName) {
        assertThat(errorEntry.getErrorCode(), is(errorCode));
        assertThat(errorEntry.getFieldName(), is(fieldName));
    }

    private Date getDate(RepaymentScheduleInstallment installment, String dateValue) {
        Date date;
        try {
            date = DateUtils.parseDate(dateValue);
        } catch (ParseException e) {
            date = null;
        }
        return date;
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
