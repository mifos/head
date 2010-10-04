package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.VariableInstallmentDetailsDto;
import org.mifos.platform.exceptions.ValidationException;

import java.util.Date;
import java.util.List;

public interface InstallmentRulesValidator {
    void validate(List<RepaymentScheduleInstallment> installments, Date disbursementDate) throws ValidationException;
    void validate(List<RepaymentScheduleInstallment> installment, VariableInstallmentDetailsDto variableInstallmentDetailsDto)
                                                                                        throws ValidationException;
}
