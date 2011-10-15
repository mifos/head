package org.mifos.accounts.loan.business.service.validators;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.platform.validations.ErrorEntry;

public interface InstallmentRulesValidator {
    
    List<ErrorEntry> validateForDisbursementDate(List<RepaymentScheduleInstallment> installments, Date disbursementDate);

    List<ErrorEntry> validateDueDatesForVariableInstallments(List<RepaymentScheduleInstallment> installments, VariableInstallmentDetailsBO variableInstallmentDetailsBO, Date disbursementDate);

    List<ErrorEntry> validateForHolidays(List<RepaymentScheduleInstallment> installments, HolidayServiceFacade holidayServiceFacade, Short officeId);

    List<ErrorEntry> validateForMinimumInstallmentAmount(List<RepaymentScheduleInstallment> installments, BigDecimal minInstallmentAmountAllowed);
}
