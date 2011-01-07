package org.mifos.accounts.loan.business.service.validators;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.validations.ErrorEntry;

import java.util.List;

public interface ListOfInstallmentsValidator {
    List<ErrorEntry> validateDuplicateDueDates(List<RepaymentScheduleInstallment> installments);
    List<ErrorEntry> validateOrderingOfDueDates(List<RepaymentScheduleInstallment> installments);
}
