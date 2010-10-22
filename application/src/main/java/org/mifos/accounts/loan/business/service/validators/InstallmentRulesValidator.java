package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.platform.validations.ErrorEntry;

import java.util.Date;
import java.util.List;

public interface InstallmentRulesValidator {
    List<ErrorEntry> validateForDisbursementDate(List<RepaymentScheduleInstallment> installments, Date disbursementDate);

    List<ErrorEntry> validateForVariableInstallments(List<RepaymentScheduleInstallment> installments, VariableInstallmentDetailsBO variableInstallmentDetailsBO);

    List<ErrorEntry> validateForHolidays(List<RepaymentScheduleInstallment> installments, FiscalCalendarRules fiscalCalendarRules);

    List<ErrorEntry> validateForMinimumInstallmentAmount(List<RepaymentScheduleInstallment> installments);
}
