package org.mifos.accounts.loan.business.service.validators;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.platform.exceptions.ValidationException;

import java.util.Date;

public class InstallmentFormatValidatorImpl implements InstallmentFormatValidator {
    @Override
    public void validateDueDateFormat(RepaymentScheduleInstallment installment) throws ValidationException {
        String identifier = String.valueOf(installment.getInstallment());
        if (isInValidDueDate(installment)) {
            throw new ValidationException(AccountConstants.INSTALLMENT_DUEDATE_INVALID, identifier);
        } else {
            setDueDateValue(installment, identifier);
        }
    }

    private void setDueDateValue(RepaymentScheduleInstallment installment, String identifier) {
        try {
            Date dateValue = DateUtils.getDate(installment.getDueDate(), installment.getLocale(), installment.getDateFormat());
            installment.setDueDateValue(dateValue);
        } catch (Exception e) {
            throw new ValidationException(AccountConstants.INSTALLMENT_DUEDATE_INVALID, identifier);
        }
    }

    private boolean isInValidDueDate(RepaymentScheduleInstallment installment) {
        return StringUtils.isEmpty(installment.getDueDate());
    }

    @Override
    public void validateTotalAmountFormat(RepaymentScheduleInstallment installment) throws ValidationException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
