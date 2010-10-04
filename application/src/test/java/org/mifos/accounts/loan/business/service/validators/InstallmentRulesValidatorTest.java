package org.mifos.accounts.loan.business.service.validators;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallmentBuilder;
import org.mifos.accounts.loan.util.helpers.VariableInstallmentDetailsDto;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.exceptions.ValidationException;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(MockitoJUnitRunner.class)
public class InstallmentRulesValidatorTest {
    private RepaymentScheduleInstallmentBuilder installmentBuilder;

    private MifosCurrency rupeeCurrency;
    private InstallmentRulesValidator installmentRulesValidator;
    private Locale locale;

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
        try {
            Date dateValue = getDate(installment, "30-Nov-2010");
            installmentRulesValidator.validate(asList(installment), dateValue);
            fail("Should have thrown validation error");
        } catch (ValidationException validationException) {
            assertThat(validationException.getKey(), is(AccountConstants.GENERIC_VALIDATION_ERROR));
            assertThat(validationException.hasChildExceptions(), is(true));
            ValidationException childException = validationException.getChildExceptions().get(0);
            assertValidationException(childException, AccountConstants.INSTALLMENT_DUEDATE_SAME_AS_DISBURSE_DATE, "3");
        }
    }
    
    @Test
    public void shouldNotValidateInstallmentForDueDateAfterDisburseDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDateValue("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        try {
            Date dateValue = getDate(installment, "30-Sep-2010");
            installmentRulesValidator.validate(asList(installment), dateValue);
        } catch (ValidationException e) {
            fail("Should not have thrown validation error");
        }
    }

    @Test
    public void shouldValidateInstallmentForDueDateBeforeDisburseDate() {
        RepaymentScheduleInstallment installment = installmentBuilder.withInstallment(3).withDueDateValue("30-Nov-2010")
                .withPrincipal(new Money(rupeeCurrency, "499.9")).withInterest(new Money(rupeeCurrency, "22.1"))
                .withFees(new Money(rupeeCurrency, "0.0")).withTotal("522.0").build();
        try {
            Date dateValue = getDate(installment, "30-Dec-2010");
            installmentRulesValidator.validate(asList(installment), dateValue);
            fail("Should have thrown validation error");
        } catch (ValidationException validationException) {
            assertThat(validationException.getKey(), is(AccountConstants.GENERIC_VALIDATION_ERROR));
            assertThat(validationException.hasChildExceptions(), is(true));
            ValidationException childException = validationException.getChildExceptions().get(0);
            assertValidationException(childException, AccountConstants.INSTALLMENT_DUEDATE_BEFORE_DISBURSE_DATE, "3");
        }
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
        try {
            VariableInstallmentDetailsDto variableInstallmentDetails = getVariableInstallmentDetails(5, null, 100, rupeeCurrency);
            List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4, installment5, installment6, installment7);
            installmentRulesValidator.validate(installments, variableInstallmentDetails);
            fail("Should have thrown validation error");
        } catch (ValidationException validationException) {
            assertThat(validationException.getKey(), is(AccountConstants.GENERIC_VALIDATION_ERROR));
            assertThat(validationException.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = validationException.getChildExceptions();
            assertThat(childExceptions.size(), is(2));
            assertValidationException(childExceptions.get(0), AccountConstants.INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP, "3");
            assertValidationException(childExceptions.get(1), AccountConstants.INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP, "7");
        }
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
        try {
            VariableInstallmentDetailsDto variableInstallmentDetails = getVariableInstallmentDetails(null, 5, 100, rupeeCurrency);
            List<RepaymentScheduleInstallment> installments = asList(installment1, installment2, installment3, installment4, installment5, installment6, installment7);
            installmentRulesValidator.validate(installments, variableInstallmentDetails);
            fail("Should have thrown validation error");
        } catch (ValidationException validationException) {
            assertThat(validationException.getKey(), is(AccountConstants.GENERIC_VALIDATION_ERROR));
            assertThat(validationException.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = validationException.getChildExceptions();
            assertThat(childExceptions.size(), is(3));
            assertValidationException(childExceptions.get(0), AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, "4");
            assertValidationException(childExceptions.get(1), AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, "5");
            assertValidationException(childExceptions.get(2), AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, "6");
        }
    }

    @Test
    public void shouldValidateMinInstallmentForVariableInstallments() {
        RepaymentScheduleInstallment installment = installmentBuilder.reset(locale).withInstallment(1).
                        withPrincipal(new Money(rupeeCurrency, "49")).withTotalValue("50").build();
        try {
            VariableInstallmentDetailsDto variableInstallmentDetails = getVariableInstallmentDetails(null, null, 100, rupeeCurrency);
            installmentRulesValidator.validate(asList(installment), variableInstallmentDetails);
            fail("Should have thrown validation error");
        } catch (ValidationException validationException) {
            assertThat(validationException.getKey(), is(AccountConstants.GENERIC_VALIDATION_ERROR));
            assertThat(validationException.hasChildExceptions(), is(true));
            List<ValidationException> childExceptions = validationException.getChildExceptions();
            assertThat(childExceptions.size(), is(1));
            assertValidationException(childExceptions.get(0), AccountConstants.INSTALLMENT_AMOUNT_LESS_THAN_MIN_AMOUNT, "1");
        }
    }

    private void assertValidationException(ValidationException validationException, String key, String identifier) {
        assertThat(validationException.getKey(), is(key));
        assertThat(validationException.getIdentifier(), is(identifier));
    }

    private Date getDate(RepaymentScheduleInstallment installment, String dateValue) {
        Locale dateLocale = installment.getLocale();
        String dateFormat = installment.getDateFormat();
        return DateUtils.getDate(dateValue, dateLocale, dateFormat);
    }

    private VariableInstallmentDetailsDto getVariableInstallmentDetails(Integer minGapInDays, Integer maxGapInDays, Integer minInstAmount, MifosCurrency currency) {
        VariableInstallmentDetailsDto variableInstallmentDetails = new VariableInstallmentDetailsDto();
        variableInstallmentDetails.setMinGapInDays(minGapInDays);
        variableInstallmentDetails.setMaxGapInDays(maxGapInDays);
        Money installmentAmount = new Money(currency, String.valueOf(minInstAmount));
        variableInstallmentDetails.setMinInstallmentAmount(installmentAmount);
        return variableInstallmentDetails;
    }

}
