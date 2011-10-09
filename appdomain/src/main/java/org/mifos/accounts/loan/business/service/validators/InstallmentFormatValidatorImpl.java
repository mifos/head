package org.mifos.accounts.loan.business.service.validators;

import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.loan.util.helpers.RepaymentScheduleInstallment;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.platform.validations.ErrorEntry;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mifos.accounts.util.helpers.AccountConstants.INSTALLMENT_TOTAL_AMOUNT_INVALID;

public class InstallmentFormatValidatorImpl implements InstallmentFormatValidator {
    @Override
    public List<ErrorEntry> validateDueDateFormat(RepaymentScheduleInstallment installment) {
        List<ErrorEntry> errorEntries = new ArrayList<ErrorEntry>();
        String identifier = installment.getInstallmentNumberAsString();
        if (isInValidDueDate(installment)) {
            addError(errorEntries, identifier, AccountConstants.INSTALLMENT_DUEDATE_INVALID);
        } else {
            setDueDateValue(installment, identifier, errorEntries);
        }
        return errorEntries;
    }

    @Override
    public List<ErrorEntry> validateTotalAmountFormat(RepaymentScheduleInstallment installment) {
        List<ErrorEntry> errorEntries = new ArrayList<ErrorEntry>();
        String identifier = installment.getInstallmentNumberAsString();
        if (isInvalidTotalAmount(installment)) {
            addError(errorEntries, identifier, INSTALLMENT_TOTAL_AMOUNT_INVALID);
        } else {
            setTotalAmountValue(installment, identifier, errorEntries);
        }
        return errorEntries;
    }

    private void setDueDateValue(RepaymentScheduleInstallment installment, String identifier, List<ErrorEntry> errorEntries) {
        try {
            Date dateValue = DateUtils.parseDate(installment.getDueDate());
            installment.setDueDateValue(dateValue);
        } catch (Exception e) {
            addError(errorEntries, identifier, AccountConstants.INSTALLMENT_DUEDATE_INVALID);
        }
    }

    private void addError(List<ErrorEntry> errorEntries, String identifier, String errorCode) {
        errorEntries.add(new ErrorEntry(errorCode, identifier));
    }

    private boolean isInValidDueDate(RepaymentScheduleInstallment installment) {
        return StringUtils.isEmpty(installment.getDueDate());
    }

    private void setTotalAmountValue(RepaymentScheduleInstallment installment, String identifier, List<ErrorEntry> errorEntries) {
        LocalizationConverter localizationConverter = new LocalizationConverter(installment.getCurrency());
        DoubleConversionResult conversionResult = localizationConverter.parseDoubleForInstallmentTotalAmount(installment.getTotal());
        List<ConversionError> conversionErrors = conversionResult.getErrors();
        if (conversionErrors.isEmpty()) {
            installment.setTotalValue(conversionResult.getDoubleValue());
        } else {
            processConversionErrors(installment, identifier, conversionErrors, errorEntries);
        }
    }

    private void processConversionErrors(RepaymentScheduleInstallment installment, String identifier,
                                         List<ConversionError> conversionErrors, List<ErrorEntry> errorEntries) {
        for (ConversionError error : conversionErrors) {
            String errorText = error.toLocalizedMessage(installment.getLocale(), installment.getCurrency());
            errorEntries.add(new ErrorEntry(INSTALLMENT_TOTAL_AMOUNT_INVALID, identifier, errorText));
        }
    }

    private boolean isInvalidTotalAmount(RepaymentScheduleInstallment installment) {
        return StringUtils.isEmpty(installment.getTotal());
    }
}
