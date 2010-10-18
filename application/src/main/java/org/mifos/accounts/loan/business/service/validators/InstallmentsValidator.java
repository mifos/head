package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.validations.Errors;

import java.util.List;

public interface InstallmentsValidator {
    Errors validate(List<RepaymentScheduleInstallment> installments, InstallmentValidationContext installmentValidationContext);
}
