package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.validations.ErrorEntry;

import java.util.List;

public interface InstallmentFormatValidator {
    List<ErrorEntry> validateDueDateFormat(RepaymentScheduleInstallment installment);
    List<ErrorEntry> validateTotalAmountFormat(RepaymentScheduleInstallment installment);
}
