/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.util;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.config.Localization;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DoubleConversionResult;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalizationConverter {
    private DecimalFormat currentDecimalFormat;
    private DecimalFormat currentDecimalFormatForMoney;
    private DecimalFormat currentDecimalFormatForInterest;
    private String dateSeparator;
    private Locale currentLocale;
    private char decimalFormatSymbol;
    private short digitsAfterDecimalForMoney;
    private short digitsBeforeDecimalForMoney;
    private short digitsAfterDecimalForInterest;
    private short digitsBeforeDecimalForInterest;
    private short digitsBeforeDecimalForCashFlowValidations;
    private short digitsAfterDecimalForCashFlowValidations;
    // the decimalFormatLocale is introduced because the double format is not
    // supported for
    // 1.1 realease yet and the English format is still used no matter what the
    // configured locale is
    private Locale decimalFormatLocale;
    // the dateLocale is introduced because the date format is not supported for
    // 1.1 realease yet and the English format is still used no matter what the
    // configured locale is
    private Locale dateLocale;
    private char minusSign;

    public LocalizationConverter() {
        digitsAfterDecimalForMoney = AccountingRules.getDigitsAfterDecimal();
        initLocalizationConverter();
    }

    public LocalizationConverter(MifosCurrency currency) {
        digitsAfterDecimalForMoney = AccountingRules.getDigitsAfterDecimal(currency);
        initLocalizationConverter();
    }


    private void initLocalizationConverter() {
        digitsBeforeDecimalForMoney = AccountingRules.getDigitsBeforeDecimal();
        digitsAfterDecimalForInterest = AccountingRules.getDigitsAfterDecimalForInterest();
        digitsBeforeDecimalForInterest = AccountingRules.getDigitsBeforeDecimalForInterest();
        digitsBeforeDecimalForCashFlowValidations = AccountingRules.getDigitsBeforeDecimalForCashFlowValidations();
        digitsAfterDecimalForCashFlowValidations = AccountingRules.getDigitsAfterDecimalForCashFlowValidations();
        currentLocale = Localization.getInstance().getMainLocale();
        // for this 1.1. release this will be defaulted to the English locale
        // and
        // later on this will be the configured locale so these lines will be
        // removed
        decimalFormatLocale = new Locale("en", "GB");
        loadDecimalFormats();
        dateLocale = new Locale("en", "GB");
        dateSeparator = getDateSeparator();
    }

    // this method will be removed when date is localized
    public Locale getDateLocale() {
        return dateLocale;
    }

    /**
     * @deprecated Members are no longer static, hence, this no longer works for
     *             unit tests. No replacement available.
     */
    @Deprecated
    public void setCurrentLocale(Locale locale) {
        currentLocale = locale;
        decimalFormatLocale = locale;
        loadDecimalFormats();
        dateLocale = locale;
        dateSeparator = getDateSeparator();
    }

    private boolean isLocaleSupported(Locale[] locales, Locale locale) {

        Locale tempLocale;
        boolean find = false;

        for (Locale locale2 : locales) {
            tempLocale = locale2;
            if (tempLocale.getCountry().equals(locale.getCountry())
                    && (tempLocale.getLanguage().equals(locale.getLanguage()))) {
                find = true;
                break;
            }
        }
        return find;
    }

    private DecimalFormat buildDecimalFormat(Short digitsBefore, Short digitsAfter, DecimalFormat decimalFormat,
            Boolean allowTrailingZero) {
        StringBuilder pattern = new StringBuilder();
        for (short i = 0; i < digitsBefore; i++) {
            pattern.append('#');
        }
        pattern.append(decimalFormat.getDecimalFormatSymbols().getDecimalSeparator());
        for (short i = 0; i < digitsAfter; i++) {
            pattern.append('#');
        }
        decimalFormat.applyLocalizedPattern(pattern.toString());
        decimalFormat.setDecimalSeparatorAlwaysShown(false);
        if (allowTrailingZero) {
            decimalFormat.setMinimumFractionDigits(digitsAfter);
        }
        return decimalFormat;
    }

    private void loadDecimalFormats() {
        if (currentLocale == null) {
            throw new RuntimeException("The current locale is not set for LocalizationConverter.");
        }
        // use this English locale for decimal format for 1.1 release
        boolean localeSupported = isLocaleSupported(NumberFormat.getAvailableLocales(), decimalFormatLocale);
        if (!localeSupported) {
            throw new RuntimeException("NumberFormat class doesn't support this country code: "
                    + decimalFormatLocale.getCountry() + " and language code: " + decimalFormatLocale.getLanguage());
        }
        NumberFormat format = DecimalFormat.getInstance(decimalFormatLocale);
        if (format instanceof DecimalFormat) {
            currentDecimalFormat = (DecimalFormat) format;
            currentDecimalFormatForMoney = buildDecimalFormat(digitsBeforeDecimalForMoney, digitsAfterDecimalForMoney,
                    (DecimalFormat) currentDecimalFormat.clone(), Boolean.TRUE);
            currentDecimalFormatForInterest = buildDecimalFormat(digitsBeforeDecimalForInterest,
                    digitsAfterDecimalForInterest, (DecimalFormat) currentDecimalFormat.clone(), Boolean.FALSE);
            DecimalFormatSymbols decimalFormatSymbols = currentDecimalFormat.getDecimalFormatSymbols();
            decimalFormatSymbol = decimalFormatSymbols.getDecimalSeparator();
            minusSign = decimalFormatSymbols.getMinusSign();
        }
    }

    public DoubleConversionResult parseDoubleForMoney(String doubleStr) {
        DoubleConversionResult result = new DoubleConversionResult();
        if (doubleStr == null) {
            List<ConversionError> errors = new ArrayList<ConversionError>();
            errors.add(ConversionError.CONVERSION_ERROR);
            result.setErrors(errors);
            return result;
        }
        List<ConversionError> errors = checkDigits(digitsBeforeDecimalForMoney, digitsAfterDecimalForMoney,
                ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY,
                ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_MONEY, doubleStr, false);
        result.setErrors(errors);
        if (errors.size() > 0) {
            return result;
        }
        try {
            result.setDoubleValue(getDoubleValueForCurrentLocale(doubleStr));
        } catch (Exception ex) {
            // after all the checkings this is not likely to happen, but just in
            // case
            result.getErrors().add(ConversionError.CONVERSION_ERROR);
        }
        return result;
    }

    public DoubleConversionResult parseDoubleForInstallmentTotalAmount(String totalAmountStr) {
        DoubleConversionResult result = new DoubleConversionResult();
        if (totalAmountStr == null) {
            List<ConversionError> errors = new ArrayList<ConversionError>();
            errors.add(ConversionError.CONVERSION_ERROR);
            result.setErrors(errors);
            return result;
        }
        List<ConversionError> errors = checkDigits(digitsBeforeDecimalForMoney, digitsAfterDecimalForMoney,
                ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY,
                ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_MONEY, totalAmountStr, true);
        result.setErrors(errors);
        if (errors.size() > 0) {
            return result;
        }
        try {
            result.setDoubleValue(getDoubleValueForCurrentLocale(totalAmountStr));
        } catch (Exception ex) {
            // after all the checkings this is not likely to happen, but just in
            // case
            result.getErrors().add(ConversionError.CONVERSION_ERROR);
        }
        return result;
    }

    public DoubleConversionResult parseDoubleForInterest(String doubleStr) {
        DoubleConversionResult result = new DoubleConversionResult();
        if (doubleStr == null) {
            List<ConversionError> errors = new ArrayList<ConversionError>();
            errors.add(ConversionError.CONVERSION_ERROR);
            result.setErrors(errors);
            return result;
        }

        List<ConversionError> errors = checkDigits(digitsBeforeDecimalForInterest, digitsAfterDecimalForInterest,
                ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_INTEREST,
                ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST, doubleStr, false);
        result.setErrors(errors);

        if (errors.size() > 0) {
            return result;
        }

        return validateTheValueIsWithinTheRange(ConversionError.INTEREST_OUT_OF_RANGE, AccountingRules.getMinInterest(), AccountingRules.getMaxInterest(), result, errors, doubleStr);
    }

    public DoubleConversionResult parseDoubleForCashFlowValidations(String doubleStr,
                                                                    ConversionError cashFlowValidationOutOfRange,
                                                                    Double minimumLimit, Double maximumLimit) {
        DoubleConversionResult result = new DoubleConversionResult();
        if (doubleStr == null) {
            List<ConversionError> errors = new ArrayList<ConversionError>();
            errors.add(ConversionError.CONVERSION_ERROR);
            result.setErrors(errors);
            return result;
        }

        List<ConversionError> errors = checkDigits(digitsBeforeDecimalForCashFlowValidations, digitsAfterDecimalForCashFlowValidations,
                ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_CASHFLOW_VALIDATION,
                ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_CASHFLOW_VALIDATION,
                doubleStr, false);
        result.setErrors(errors);

        if (errors.size() > 0) {
            return result;
        }

        return validateTheValueIsWithinTheRange(cashFlowValidationOutOfRange, minimumLimit, maximumLimit, result, errors, doubleStr);
    }

    private DoubleConversionResult validateTheValueIsWithinTheRange(ConversionError outOfRangeConversionError,
                                                                    Double minimumLimit, Double maximumLimit,
                                                                    DoubleConversionResult result,
                                                                    List<ConversionError> errors, String validationValue) {
        try {
            Double value = getDoubleValueForPercent(validationValue);
            if ((value > maximumLimit) || (value < minimumLimit)) {
                errors.add(outOfRangeConversionError);
            } else {
                result.setDoubleValue(value);
            }
        } catch (Exception ex) {
            result.getErrors().add(ConversionError.CONVERSION_ERROR);
        }
        return result;
    }


    public char getDecimalFormatSymbol() {
        return decimalFormatSymbol;
    }

    private List<ConversionError> checkDigits(Short digitsBefore, Short digitsAfter, ConversionError errorDigitsBefore,
                                              ConversionError errorDigitsAfter, String number, boolean allowNegativeValue) {
        List<ConversionError> errors = new ArrayList<ConversionError>();
        ConversionError error;
        for (int i = 0; i < number.length(); i++) {
            if (!Character.isDigit(number.charAt(i))) {
                char charAt = number.charAt(i);
                if (charAt == decimalFormatSymbol || (allowNegativeValue && charAt == minusSign)) {
                    continue;
                }
                error = ConversionError.NOT_ALL_NUMBER;
                errors.add(error);
                return errors;

            }
        }
        int index = number.indexOf(decimalFormatSymbol);
        if (index < 0) {
            if (number.length() > digitsBefore) {
                error = errorDigitsBefore;
                errors.add(error);
            }
        } else {
            String digitsAfterNum = number.substring(index + 1, number.length());
            if (digitsAfterNum.length() > digitsAfter) {
                error = errorDigitsAfter;
                errors.add(error);
            }

            String digitsBeforeNum = number.substring(0, index);
            if (digitsBeforeNum.length() > digitsBefore) {
                error = errorDigitsBefore;
                errors.add(error);
            }

        }
        return errors;
    }

    private Double getDoubleValueForPercent(String doubleValueString) {

        if (currentDecimalFormatForInterest == null) {
            loadDecimalFormats();
        }
        Double dNum = null;
        try {
            ParsePosition pp = new ParsePosition(0);
            Number num = currentDecimalFormatForInterest.parse(doubleValueString, pp);
            if ((doubleValueString.length() != pp.getIndex()) || (num == null)) {
                throw new NumberFormatException("The format of the number is invalid. index " + pp.getIndex()
                        + " locale " + currentLocale.getCountry() + " " + currentLocale.getLanguage());
            }
            dNum = num.doubleValue();
        } catch (Exception e) {
            throw new NumberFormatException(e.getMessage() + " .Number " + doubleValueString);
        }
        return dNum;
    }

    public Double getDoubleValueForCurrentLocale(String doubleValueString) {
        Double dNum = null;
        try {
            ParsePosition pp = new ParsePosition(0);
            Number num = currentDecimalFormatForMoney.parse(doubleValueString, pp);
            if ((doubleValueString.length() != pp.getIndex()) || (num == null)) {
                throw new NumberFormatException("The format of the number is invalid. index " + pp.getIndex()
                        + " locale " + currentLocale.getCountry() + " " + currentLocale.getLanguage());
            }
            dNum = num.doubleValue();
        } catch (Exception e) {
            throw new NumberFormatException(e.getMessage() + " .Number " + doubleValueString);
        }
        return dNum;
    }

    public String getDoubleStringForMoney(Double dNumber) {
        return currentDecimalFormatForMoney.format(dNumber);
    }

    public String getDoubleStringForInterest(Double dNumber) {
        return currentDecimalFormatForInterest.format(dNumber);
    }

    public String getDoubleValueString(Double dNumber) {
        return currentDecimalFormat.format(dNumber);
    }

    public String getDateSeparatorForCurrentLocale() {
        return dateSeparator;
    }

    private String getDateSeparator() {
        Locale[] locales = DateFormat.getInstance().getAvailableLocales();
        // decimalFormatLocale is the English locale used temporarily because
        // 1.1 release doesn't
        // support date/time/double localization yet
        if (!isLocaleSupported(locales, dateLocale)) {
            throw new RuntimeException("DateFormat class doesn't support this country code: " + dateLocale.getCountry()
                    + " and language code: " + dateLocale.getLanguage());
        }

        return getDateSeparator(dateLocale, DateFormat.SHORT);
    }

    public String getDateSeparator(Locale dateLocale, int dateFormat) {
        String separator = "";
        DateFormat format = DateFormat.getDateInstance(dateFormat, dateLocale);
        String now = format.format(new DateTimeService().getCurrentJavaDateTime());
        char chArray[] = now.toCharArray();
        for (char element : chArray) {
            if (Character.isDigit(element) == false) {
                separator = String.valueOf(element);
                break;
            }
        }
        return separator;
    }

    public DateFormat getDateFormat() {
        // dateLocale is the English locale used temporarily because 1.1 release
        // doesn't
        // support date/time/double localization yet
        Locale[] locales = DateFormat.getInstance().getAvailableLocales();
        if (!isLocaleSupported(locales, dateLocale)) {
            throw new RuntimeException("DateFormat class doesn't support this country code: " + dateLocale.getCountry()
                    + " and language code: " + dateLocale.getLanguage());
        }

        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, dateLocale);
        // DateFormat simpleFormat =
        // SimpleDateFormat.getDateInstance(DateFormat.SHORT,
        // decimalFormatLocale);
        return format;

    }

    /**
     * Use this if you want the year part to have 4 digits
     **/
    public DateFormat getDateFormatWithFullYear() {
        DateFormat dateFormat = getDateFormat();
        if (SimpleDateFormat.class.equals(dateFormat.getClass())) {
            return new SimpleDateFormat(((SimpleDateFormat) dateFormat).toPattern().replace("yy", "yyyy"), dateLocale);
        }
        return dateFormat;
    }

}
