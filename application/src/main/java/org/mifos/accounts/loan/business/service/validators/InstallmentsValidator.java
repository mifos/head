package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.validations.ValidationException;

import java.util.List;

public interface InstallmentsValidator {
    void validate(List<RepaymentScheduleInstallment> installments, InstallmentValidationContext installmentValidationContext) throws ValidationException;
}
