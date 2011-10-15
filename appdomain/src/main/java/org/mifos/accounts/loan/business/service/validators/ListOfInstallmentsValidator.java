package org.mifos.accounts.loan.business.service.validators;

import java.util.List;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.validations.ErrorEntry;

public interface ListOfInstallmentsValidator {
    List<ErrorEntry> validateDuplicateDueDates(List<RepaymentScheduleInstallment> installments);
    List<ErrorEntry> validateOrderingOfDueDates(List<RepaymentScheduleInstallment> installments);
}
