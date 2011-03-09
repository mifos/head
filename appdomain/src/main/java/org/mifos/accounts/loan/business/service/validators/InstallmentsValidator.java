package org.mifos.accounts.loan.business.service.validators;

import java.math.BigDecimal;
import java.util.List;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.validations.Errors;

public interface InstallmentsValidator {
    
    Errors validateInputInstallments(List<RepaymentScheduleInstallment> installments, InstallmentValidationContext installmentValidationContext);

    Errors validateInstallmentSchedule(List<RepaymentScheduleInstallment> installments, BigDecimal minInstallmentAmountAllowed);
}
