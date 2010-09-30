package org.mifos.accounts.loan.business.service;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.util.helpers.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class InstallmentsValidatorImpl implements InstallmentsValidator {
    @Override
    public void validate(List<RepaymentScheduleInstallment> installments) throws ServiceException {
        validateDueDates(installments);
    }

    private void validateDueDates(List<RepaymentScheduleInstallment> installments) throws ServiceException {
        List<Integer> invalidInstallments = new ArrayList<Integer>();
        for (RepaymentScheduleInstallment installment : installments) {
            if (StringUtils.isEmpty(installment.getDueDate())) {
                invalidInstallments.add(installment.getInstallment());
            } else {
                setDueDateOnInstallment(installment, invalidInstallments);
            }
        }
        if (!invalidInstallments.isEmpty()) throw new ServiceException(AccountConstants.INSTALLMENT_DUEDATE_INVALID, null, invalidInstallments.toArray());
    }

    private void setDueDateOnInstallment(RepaymentScheduleInstallment installment, List<Integer> invalidInstallments) {
        try {
            installment.setDueDateValue(DateUtils.getDate(installment.getDueDate(), installment.getLocale(), installment.getDateFormat()));
        } catch (Exception e) {
            invalidInstallments.add(installment.getInstallment());
        }
    }
}
