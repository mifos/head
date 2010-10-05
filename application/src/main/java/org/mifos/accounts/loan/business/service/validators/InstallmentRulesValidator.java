package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.VariableInstallmentDetailsDto;
import org.mifos.config.FiscalCalendarRules;
import org.mifos.platform.exceptions.ValidationException;

import java.util.Date;
import java.util.List;

public interface InstallmentRulesValidator {
    void validateForDisbursementDate(List<RepaymentScheduleInstallment> installments, Date disbursementDate) throws ValidationException;
    void validateForVariableInstallments(List<RepaymentScheduleInstallment> installments, VariableInstallmentDetailsDto variableInstallmentDetailsDto) throws ValidationException;
    void validateForHolidays(List<RepaymentScheduleInstallment> installments, FiscalCalendarRules fiscalCalendarRules) throws ValidationException;
}
