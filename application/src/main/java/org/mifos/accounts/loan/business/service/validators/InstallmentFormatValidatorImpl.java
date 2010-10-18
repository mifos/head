package org.mifos.accounts.loan.business.service.validators;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.platform.validations.ValidationException;

import java.util.Date;
import java.util.List;

import static org.mifos.accounts.util.helpers.AccountConstants.GENERIC_VALIDATION_ERROR;
import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_TOTAL_AMOUNT_INVALID;

public class InstallmentFormatValidatorImpl implements InstallmentFormatValidator {
    @Override
    public void validateDueDateFormat(RepaymentScheduleInstallment installment) throws ValidationException {
        String identifier = installment.getInstallmentNumberAsString();
        if (isInValidDueDate(installment)) {
            throw new ValidationException(AccountConstants.INSTALLMENT_DUEDATE_INVALID, identifier);
        }
        setDueDateValue(installment, identifier);
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
        String identifier = installment.getInstallmentNumberAsString();
        if (isInvalidTotalAmount(installment)) {
            throw new ValidationException(INSTALLMENT_TOTAL_AMOUNT_INVALID, identifier);
        }
        setTotalAmountValue(installment, identifier);
    }

    private void setTotalAmountValue(RepaymentScheduleInstallment installment, String identifier) {
        LocalizationConverter localizationConverter = new LocalizationConverter(installment.getCurrency());
        DoubleConversionResult conversionResult = localizationConverter.parseDoubleForMoney(installment.getTotal());
        List<ConversionError> conversionErrors = conversionResult.getErrors();
        if (conversionErrors.isEmpty()) {
            installment.setTotalValue(conversionResult.getDoubleValue());
        } else {
            processConversionErrors(installment, identifier, conversionErrors);
        }
    }

    private void processConversionErrors(RepaymentScheduleInstallment installment, String identifier, List<ConversionError> conversionErrors) {
        ValidationException parentException = new ValidationException(GENERIC_VALIDATION_ERROR);
        for (ConversionError error : conversionErrors) {
            String errorText = error.toLocalizedMessage(installment.getLocale(), installment.getCurrency());
            parentException.addChildException(new ValidationException(INSTALLMENT_TOTAL_AMOUNT_INVALID, identifier, errorText));
        }
        if (parentException.hasChildExceptions()) throw parentException;
    }

    private boolean isInvalidTotalAmount(RepaymentScheduleInstallment installment) {
        return StringUtils.isEmpty(installment.getTotal());
    }

}
