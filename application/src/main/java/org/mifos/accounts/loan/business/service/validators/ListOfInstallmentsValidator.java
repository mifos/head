package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.exceptions.ValidationException;

import java.util.List;

public interface ListOfInstallmentsValidator {
    void validateDuplicateDueDates(List<RepaymentScheduleInstallment> installments) throws ValidationException;
    void validateOrderingOfDueDates(List<RepaymentScheduleInstallment> installments) throws ValidationException;
}
