package org.mifos.accounts.loan.business.service.validators;

import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_AMOUNT_LESS_THAN_MIN_AMOUNT;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_DUEDATE_BEFORE_DISBURSE_DATE;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_DUEDATE_SAME_AS_DISBURSE_DATE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.productdefinition.business.VariableInstallmentDetailsBO;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.platform.validations.ErrorEntry;

public class InstallmentRulesValidatorImpl implements InstallmentRulesValidator {
    @Override
    public List<ErrorEntry> validateForDisbursementDate(List<RepaymentScheduleInstallment> installments, Date disbursementDate) {
        List<ErrorEntry> errorEntries = new ArrayList<ErrorEntry>();
        for (RepaymentScheduleInstallment installment : installments) {
            validateInstallmentForDisburseDate(installment, disbursementDate, errorEntries);
        }
        return errorEntries;
    }

    @Override
    public List<ErrorEntry> validateDueDatesForVariableInstallments(List<RepaymentScheduleInstallment> installments,
                                    VariableInstallmentDetailsBO variableInstallmentDetailsBO, Date disbursementDate) {
        List<ErrorEntry> errorEntries = new ArrayList<ErrorEntry>();
        if (CollectionUtils.isNotEmpty(installments)) {
            for (int i = 0, installmentsSize = installments.size(); i < installmentsSize; i++) {
                Date previousDueDate = getPreviousDueDate(installments, i, disbursementDate);
                RepaymentScheduleInstallment installment = installments.get(i);
                validateForDifferenceInDays(installment, previousDueDate, variableInstallmentDetailsBO, errorEntries);
            }
        }
        return errorEntries;
    }

    private Date getPreviousDueDate(List<RepaymentScheduleInstallment> installments, int index, Date disbursementDate) {
        return index > 0? installments.get(index - 1).getDueDateValue() : disbursementDate;
    }

    @Override
    public List<ErrorEntry> validateForHolidays(List<RepaymentScheduleInstallment> installments, HolidayServiceFacade holidayServiceFacade, Short officeId) {
        List<ErrorEntry> errorEntries = new ArrayList<ErrorEntry>();
        for (RepaymentScheduleInstallment installment : installments) {
            Calendar dueDate = installment.getDueDateValueAsCalendar();
            if (dueDate != null && holidayServiceFacade.isFutureRepaymentHoliday(officeId, dueDate)) {
                String identifier = installment.getInstallmentNumberAsString();
                errorEntries.add(new ErrorEntry(AccountConstants.INSTALLMENT_DUEDATE_IS_HOLIDAY, identifier));
            }
        }
        return errorEntries;
    }

    @Override
    public List<ErrorEntry> validateForMinimumInstallmentAmount(List<RepaymentScheduleInstallment> installments, BigDecimal minInstallmentAmount) {
        List<ErrorEntry> errorEntries = new ArrayList<ErrorEntry>();
        for (RepaymentScheduleInstallment installment : installments) {
            String identifier = installment.getInstallmentNumberAsString();
            if (installment.isTotalAmountInValid()) {
                errorEntries.add(new ErrorEntry(AccountConstants.INSTALLMENT_AMOUNT_LESS_THAN_INTEREST_FEE, identifier));
            } else if (installment.isTotalAmountLessThan(minInstallmentAmount)) {
                errorEntries.add(new ErrorEntry(INSTALLMENT_AMOUNT_LESS_THAN_MIN_AMOUNT, identifier));
            }
        }
        return errorEntries;
    }

    private void validateForDifferenceInDays(RepaymentScheduleInstallment installment, Date previousDueDate,
                                             VariableInstallmentDetailsBO variableInstallmentDetailsBO,
                                             List<ErrorEntry> errorEntries) {
        Date dueDateValue = installment.getDueDateValue();
        if (previousDueDate != null && dueDateValue != null) {
            String identifier = installment.getInstallmentNumberAsString();
            long diffInDays = DateUtils.getNumberOfDaysBetweenTwoDates(dueDateValue, previousDueDate);
            Integer minGapInDays = variableInstallmentDetailsBO.getMinGapInDays();
            if (minGapInDays != null && diffInDays < minGapInDays) {
                errorEntries.add(new ErrorEntry(INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP, identifier));
            } else {
                Integer maxGapInDays = variableInstallmentDetailsBO.getMaxGapInDays();
                if (maxGapInDays != null && diffInDays > maxGapInDays) {
                    errorEntries.add(new ErrorEntry(INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, identifier));
                }
            }
        }
    }

    private void validateInstallmentForDisburseDate(RepaymentScheduleInstallment installment, Date disbursementDate,
                                                    List<ErrorEntry> errorEntries) {
        String identifier = installment.getInstallmentNumberAsString();
        Date dueDateValue = installment.getDueDateValue();
        if (dueDateValue != null) {
            if (dueDateValue.compareTo(disbursementDate) == 0) {
                errorEntries.add(new ErrorEntry(INSTALLMENT_DUEDATE_SAME_AS_DISBURSE_DATE, identifier));
            } else if (dueDateValue.compareTo(disbursementDate) < 0) {
                errorEntries.add(new ErrorEntry(INSTALLMENT_DUEDATE_BEFORE_DISBURSE_DATE, identifier));
            }
        }
    }
}
