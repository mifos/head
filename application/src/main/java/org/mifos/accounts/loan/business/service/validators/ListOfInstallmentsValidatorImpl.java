package org.mifos.accounts.loan.business.service.validators;

import org.apache.commons.collections.CollectionUtils;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.platform.exceptions.ValidationException;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mifos.accounts.util.helpers.AccountConstants.GENERIC_VALIDATION_ERROR;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_DUEDATE_DUPLICATE;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_DUEDATE_INVALID_ORDER;

public class ListOfInstallmentsValidatorImpl implements ListOfInstallmentsValidator {
    @Override
    public void validateDuplicateDueDates(List<RepaymentScheduleInstallment> installments) throws ValidationException {
        Map<Date, List<String>> dateInstallmentsLookup = new LinkedHashMap<Date, List<String>>();
        for (RepaymentScheduleInstallment installment : installments) {
            Date dueDateValue = installment.getDueDateValue();
            if (dueDateValue != null) {
                List<String> installmentList;
                if (dateInstallmentsLookup.containsKey(dueDateValue)) {
                    installmentList = dateInstallmentsLookup.get(dueDateValue);
                } else {
                    installmentList = new ArrayList<String>();
                    dateInstallmentsLookup.put(dueDateValue, installmentList);
                }
                installmentList.add(String.valueOf(installment.getInstallment()));
            }
        }
        throwValidationExceptionIfRequired(dateInstallmentsLookup);
    }

    private void throwValidationExceptionIfRequired(Map<Date, List<String>> dateInstallmentsLookup) {
        ValidationException parentException = new ValidationException(GENERIC_VALIDATION_ERROR);
        for (List<String> installments : dateInstallmentsLookup.values()) {
            if (installments.size() > 1) {
                ValidationException childException = new ValidationException(INSTALLMENT_DUEDATE_DUPLICATE, installments.toString());
                parentException.addChildException(childException);
            }
        }
        if (parentException.hasChildExceptions()) throw parentException;
    }

    @Override
    public void validateOrderingOfDueDates(List<RepaymentScheduleInstallment> installments) throws ValidationException {
        if (CollectionUtils.isNotEmpty(installments)) {
            for (int i = 1; i < installments.size(); i++) {
                Date previousDate = installments.get(i - 1).getDueDateValue();
                RepaymentScheduleInstallment installment = installments.get(i);
                Date currentDate = installment.getDueDateValue();
                if (!isCurrentDateGreaterThanPreviousDate(previousDate, currentDate)) {
                    throw new ValidationException(INSTALLMENT_DUEDATE_INVALID_ORDER, String.valueOf(installment.getInstallment()));
                }
            }

        }
    }

    private boolean isCurrentDateGreaterThanPreviousDate(Date previousDate, Date currentDate) {
        return previousDate == null || currentDate != null && currentDate.compareTo(previousDate) > 0;
    }
}
