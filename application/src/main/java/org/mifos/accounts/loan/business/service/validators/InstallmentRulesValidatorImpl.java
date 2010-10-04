package org.mifos.accounts.loan.business.service.validators;

import org.apache.commons.collections.CollectionUtils;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.loan.util.helpers.VariableInstallmentDetailsDto;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.platform.exceptions.ValidationException;

import java.util.Date;
import java.util.List;

import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_AMOUNT_LESS_THAN_MIN_AMOUNT;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_DUEDATE_BEFORE_DISBURSE_DATE;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_DUEDATE_SAME_AS_DISBURSE_DATE;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP;

public class InstallmentRulesValidatorImpl implements InstallmentRulesValidator {
    @Override
    public void validate(List<RepaymentScheduleInstallment> installments, Date disbursementDate) throws ValidationException {
        ValidationException parentException = new ValidationException(AccountConstants.GENERIC_VALIDATION_ERROR);
        for (RepaymentScheduleInstallment installment : installments) {
            validateInstallmentForDisburseDate(installment, disbursementDate, parentException);
        }
        if (parentException.hasChildExceptions()) throw parentException;
    }

    @Override
    public void validate(List<RepaymentScheduleInstallment> installments, VariableInstallmentDetailsDto variableInstallmentDetailsDto)
                                                                                    throws ValidationException {
        ValidationException parentException = new ValidationException(AccountConstants.GENERIC_VALIDATION_ERROR);
        validateForVariableInstallmentDetails(installments, variableInstallmentDetailsDto, parentException);
        if (parentException.hasChildExceptions()) throw parentException;
    }

    private void validateForVariableInstallmentDetails(List<RepaymentScheduleInstallment> installments,
                                                       VariableInstallmentDetailsDto variableInstallmentDetailsDto,
                                                       ValidationException parentException) {
        if (CollectionUtils.isNotEmpty(installments)) {
            Money minInstallmentAmount = variableInstallmentDetailsDto.getMinInstallmentAmount();
            validateForMinInstallmentAmount(parentException, minInstallmentAmount, installments.get(0));
            for (int i = 1, installmentsSize = installments.size(); i < installmentsSize; i++) {
                Date previousDueDate = installments.get(i - 1).getDueDateValue();
                RepaymentScheduleInstallment installment = installments.get(i);
                validateForDifferenceInDays(installment, previousDueDate, variableInstallmentDetailsDto, parentException);
                validateForMinInstallmentAmount(parentException, minInstallmentAmount, installment);
            }
        }
    }

    private void validateForMinInstallmentAmount(ValidationException parentException, Money minInstallmentAmount, RepaymentScheduleInstallment installment) {
        Money total = installment.getTotalValue();
        if (total != null && total.isLessThan(minInstallmentAmount)) {
            String identifier = installment.getInstallmentNumberAsString();
            parentException.addChildException(new ValidationException(INSTALLMENT_AMOUNT_LESS_THAN_MIN_AMOUNT, identifier));
        }
    }

    private void validateForDifferenceInDays(RepaymentScheduleInstallment installment, Date previousDueDate, VariableInstallmentDetailsDto variableInstallmentDetailsDto, ValidationException parentException) {
        Date dueDateValue = installment.getDueDateValue();
        if (previousDueDate != null && dueDateValue != null) {
            String identifier = installment.getInstallmentNumberAsString();
            long diffInDays = DateUtils.getNumberOfDaysBetweenTwoDates(dueDateValue, previousDueDate);
            Integer minGapInDays = variableInstallmentDetailsDto.getMinGapInDays();
            if (minGapInDays != null && diffInDays < minGapInDays) {
                parentException.addChildException(new ValidationException(INSTALLMENT_DUEDATE_LESS_THAN_MIN_GAP, identifier));
            } else {
                Integer maxGapInDays = variableInstallmentDetailsDto.getMaxGapInDays();
                if (maxGapInDays != null && diffInDays > maxGapInDays) {
                    parentException.addChildException(new ValidationException(INSTALLMENT_DUEDATE_MORE_THAN_MAX_GAP, identifier));
                }
            }
        }
    }

    private void validateInstallmentForDisburseDate(RepaymentScheduleInstallment installment, Date disbursementDate,
                                                    ValidationException parentException) {
        String identifier = installment.getInstallmentNumberAsString();
        Date dueDateValue = installment.getDueDateValue();
        if (dueDateValue != null) {
            if (dueDateValue.compareTo(disbursementDate) == 0) {
                parentException.addChildException(new ValidationException(INSTALLMENT_DUEDATE_SAME_AS_DISBURSE_DATE, identifier));
            } else if (dueDateValue.compareTo(disbursementDate) < 0) {
                parentException.addChildException(new ValidationException(INSTALLMENT_DUEDATE_BEFORE_DISBURSE_DATE, identifier));
            }
        }
    }
}
