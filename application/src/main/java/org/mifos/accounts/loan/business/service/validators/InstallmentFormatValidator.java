package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.exceptions.ValidationException;

public interface InstallmentFormatValidator {
    void validateDueDateFormat(RepaymentScheduleInstallment installment) throws ValidationException;
    void validateTotalAmountFormat(RepaymentScheduleInstallment installment) throws ValidationException;
}
