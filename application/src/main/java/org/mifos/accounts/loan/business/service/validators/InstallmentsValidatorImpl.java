package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.platform.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

public class InstallmentsValidatorImpl implements InstallmentsValidator {

    @Autowired
    private InstallmentFormatValidator installmentFormatValidator;

    @Autowired
    private ListOfInstallmentsValidator listOfInstallmentsValidator;

    @Autowired
    private InstallmentRulesValidator installmentRulesValidator;

    @SuppressWarnings({"UnusedDeclaration"})
    private InstallmentsValidatorImpl() {
        // Used for Spring wiring
    }

    public InstallmentsValidatorImpl(InstallmentFormatValidator installmentFormatValidator,
                                     ListOfInstallmentsValidator listOfInstallmentsValidator,
                                     InstallmentRulesValidator installmentRulesValidator) {
        this.installmentFormatValidator = installmentFormatValidator;
        this.listOfInstallmentsValidator = listOfInstallmentsValidator;
        this.installmentRulesValidator = installmentRulesValidator;
    }

    @Override
    public void validate(List<RepaymentScheduleInstallment> installments, InstallmentValidationContext installmentValidationContext) throws ValidationException {
        ValidationException parentException = new ValidationException(AccountConstants.GENERIC_VALIDATION_ERROR);
        validateInstallmentFormat(installments, parentException);
        validateListOfInstallments(installments, parentException);
        validateBusinessRulesForInstallments(installments, installmentValidationContext, parentException);
        if (parentException.hasChildExceptions()) throw parentException;
    }

    private void validateInstallmentFormat(List<RepaymentScheduleInstallment> installments, ValidationException parentException) {
        for (RepaymentScheduleInstallment installment : installments) {
            validateTotalAmountFormat(parentException, installment);
            validateDueDateFormat(parentException, installment);
        }
    }

    private void validateListOfInstallments(List<RepaymentScheduleInstallment> installments, ValidationException parentException) {
        validateForDuplicateDates(installments, parentException);
        validateForOrderingOfDates(installments, parentException);
    }

    private void validateBusinessRulesForInstallments(List<RepaymentScheduleInstallment> installments,
                                                      InstallmentValidationContext installmentValidationContext,
                                                      ValidationException parentException) {
        validateForDisbursementDate(installments, installmentValidationContext.getDisbursementDate(), parentException);
        validateForVariableInstallments(installments, installmentValidationContext.getVariableInstallmentDetails(), parentException);
        validateForHolidays(installments, installmentValidationContext.getFiscalCalendarRules(), parentException);
    }

    // TODO Remove the use of exceptions for validations and cleanup the following methods
    private void validateForHolidays(List<RepaymentScheduleInstallment> installments,
                                     FiscalCalendarRules fiscalCalendarRules, ValidationException parentException) {
        try {
            installmentRulesValidator.validateForHolidays(installments, fiscalCalendarRules);
        } catch (ValidationException e) {
            parentException.copyChildExceptions(e);
        }
    }

    private void validateForVariableInstallments(List<RepaymentScheduleInstallment> installments,
                                                 VariableInstallmentDetailsBO variableInstallmentDetails,
                                                 ValidationException parentException) {
        try {
            installmentRulesValidator.validateForVariableInstallments(installments, variableInstallmentDetails);
        } catch (ValidationException e) {
            parentException.copyChildExceptions(e);
        }
    }

    private void validateForDisbursementDate(List<RepaymentScheduleInstallment> installments,
                                             Date disbursementDate, ValidationException parentException) {
        try {
            installmentRulesValidator.validateForDisbursementDate(installments, disbursementDate);
        } catch (ValidationException e) {
            parentException.copyChildExceptions(e);
        }
    }

    private void validateForOrderingOfDates(List<RepaymentScheduleInstallment> installments, ValidationException parentException) {
        try {
            listOfInstallmentsValidator.validateOrderingOfDueDates(installments);
        } catch (ValidationException e) {
            parentException.copyChildExceptions(e);
        }
    }

    private void validateForDuplicateDates(List<RepaymentScheduleInstallment> installments, ValidationException parentException) {
        try {
            listOfInstallmentsValidator.validateDuplicateDueDates(installments);
        } catch (ValidationException e) {
            parentException.copyChildExceptions(e);
        }
    }

    private void validateDueDateFormat(ValidationException parentException, RepaymentScheduleInstallment installment) {
        try {
            installmentFormatValidator.validateDueDateFormat(installment);
        } catch (ValidationException e) {
            parentException.copyChildExceptions(e);
        }
    }

    private void validateTotalAmountFormat(ValidationException parentException, RepaymentScheduleInstallment installment) {
        try {
            installmentFormatValidator.validateTotalAmountFormat(installment);
        } catch (ValidationException e) {
            parentException.copyChildExceptions(e);
        }
    }
}
