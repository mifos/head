package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.platform.validations.Errors;
import org.springframework.beans.factory.annotation.Autowired;

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
    public Errors validateInputInstallments(List<RepaymentScheduleInstallment> installments, InstallmentValidationContext installmentValidationContext) {
        Errors errors = new Errors();
        validateInstallmentFormat(installments, errors);
        validateListOfInstallments(installments, errors);
        validateBusinessRulesForInstallments(installments, installmentValidationContext, errors);
        return errors;
    }

    @Override
    public Errors validateInstallmentSchedule(List<RepaymentScheduleInstallment> installments, VariableInstallmentDetailsBO variableInstallmentDetailsBO) {
        Errors errors = new Errors();
        errors.addErrors(installmentRulesValidator.validateForMinimumInstallmentAmount(installments, variableInstallmentDetailsBO));
        return errors;
    }

    private void validateInstallmentFormat(List<RepaymentScheduleInstallment> installments, Errors errors) {
        for (RepaymentScheduleInstallment installment : installments) {
            errors.addErrors(installmentFormatValidator.validateTotalAmountFormat(installment));
            errors.addErrors(installmentFormatValidator.validateDueDateFormat(installment));
        }
    }

    private void validateListOfInstallments(List<RepaymentScheduleInstallment> installments, Errors errors) {
        errors.addErrors(listOfInstallmentsValidator.validateDuplicateDueDates(installments));
        errors.addErrors(listOfInstallmentsValidator.validateOrderingOfDueDates(installments));
    }

    private void validateBusinessRulesForInstallments(List<RepaymentScheduleInstallment> installments,
                                                      InstallmentValidationContext context, Errors errors) {
        errors.addErrors(installmentRulesValidator.validateForDisbursementDate(installments, context.getDisbursementDate()));
        errors.addErrors(installmentRulesValidator.validateDueDatesForVariableInstallments(installments, context.getVariableInstallmentDetails()));
        errors.addErrors(installmentRulesValidator.validateForHolidays(installments, context.getHolidayServiceFacade(), context.getOfficeId()));
    }
}
