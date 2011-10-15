package org.mifos.accounts.loan.business.service.validators;

import java.util.List;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.validations.ErrorEntry;

public interface InstallmentFormatValidator {
    List<ErrorEntry> validateDueDateFormat(RepaymentScheduleInstallment installment);
    List<ErrorEntry> validateTotalAmountFormat(RepaymentScheduleInstallment installment);
}
