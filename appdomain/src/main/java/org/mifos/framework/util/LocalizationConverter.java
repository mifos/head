/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DoubleConversionResult;
import org.mifos.framework.util.helpers.Money;


/**
 * Localization is UI concern, it should never be used beyond controller/action layer
 */
public class LocalizationConverter {

    /**
     * Only support English number format
     */
    private Locale locale = Locale.UK;

    private DecimalFormat decimalFormat;
    private DecimalFormat decimalFormatForMoney;
    private DecimalFormat decimalFormatForInterest;

    private String dateSeparator;
    private char decimalFormatSymbol;

    private short digitsAfterDecimalForMoney;
    private short digitsBeforeDecimalForMoney;

    private short digitsAfterDecimalForInterest;
    private short digitsBeforeDecimalForInterest;

    private short digitsBeforeDecimalForCashFlowValidations;
    private short digitsAfterDecimalForCashFlowValidations;

    private char minusSign;

    public LocalizationConverter() {
        this(Money.getDefaultCurrency());
    }

    public LocalizationConverter(MifosCurrency currency) {
        digitsAfterDecimalForMoney = AccountingRules.getDigitsAfterDecimal(currency);
        digitsBeforeDecimalForMoney = AccountingRules.getDigitsBeforeDecimal();
        digitsAfterDecimalForInterest = AccountingRules.getDigitsAfterDecimalForInterest();
        digitsBeforeDecimalForInterest = AccountingRules.getDigitsBeforeDecimalForInterest();
        digitsBeforeDecimalForCashFlowValidations = AccountingRules.getDigitsBeforeDecimalForCashFlowValidations();
        digitsAfterDecimalForCashFlowValidations = AccountingRules.getDigitsAfterDecimalForCashFlowValidations();
        loadDecimalFormats();
        dateSeparator = getDateSeparator();
    }

    /**
     * Unit Test only
     *
     * @param digitsAfterDecimalForMoney
     * @param digitsBeforeDecimalForMoney
     * @param digitsAfterDecimalForInterest
     * @param digitsBeforeDecimalForInterest
     * @param digitsBeforeDecimalForCashFlowValidations
     * @param digitsAfterDecimalForCashFlowValidations
     */
    protected LocalizationConverter(Short digitsAfterDecimalForMoney, Short digitsBeforeDecimalForMoney, Short digitsAfterDecimalForInterest,
                                    Short digitsBeforeDecimalForInterest, Short digitsBeforeDecimalForCashFlowValidations, Short digitsAfterDecimalForCashFlowValidations) {
        this.digitsAfterDecimalForMoney = digitsAfterDecimalForMoney;
        this.digitsBeforeDecimalForMoney = digitsBeforeDecimalForMoney;
        this.digitsAfterDecimalForInterest = digitsAfterDecimalForInterest;
        this.digitsBeforeDecimalForInterest = digitsBeforeDecimalForInterest;
        this.digitsBeforeDecimalForCashFlowValidations = digitsBeforeDecimalForCashFlowValidations;
        this.digitsAfterDecimalForCashFlowValidations = digitsAfterDecimalForCashFlowValidations;
        loadDecimalFormats();
        dateSeparator = getDateSeparator();
    }

    public void setCurrentLocale(Locale locale) {
        this.locale = locale;
        loadDecimalFormats();
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
        if (locale == null) {
            throw new RuntimeException("The current locale is not set for LocalizationConverter.");
        }
        // use this English locale for decimal format for 1.1 release
        boolean localeSupported = isLocaleSupported(NumberFormat.getAvailableLocales(), locale);
        if (!localeSupported) {
            throw new RuntimeException("NumberFormat class doesn't support this country code: "
                    + locale.getCountry() + " and language code: " + locale.getLanguage());
        }
        NumberFormat format = DecimalFormat.getInstance(locale);
        if (format instanceof DecimalFormat) {
            decimalFormat = (DecimalFormat) format;
            decimalFormatForMoney = buildDecimalFormat(digitsBeforeDecimalForMoney, digitsAfterDecimalForMoney,
                    (DecimalFormat) decimalFormat.clone(), Boolean.TRUE);
            decimalFormatForInterest = buildDecimalFormat(digitsBeforeDecimalForInterest,
                    digitsAfterDecimalForInterest, (DecimalFormat) decimalFormat.clone(), Boolean.FALSE);
            DecimalFormatSymbols decimalFormatSymbols = decimalFormat.getDecimalFormatSymbols();
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

        if (decimalFormatForInterest == null) {
            loadDecimalFormats();
        }
        Double dNum = null;
        try {
            ParsePosition pp = new ParsePosition(0);
            Number num = decimalFormatForInterest.parse(doubleValueString, pp);
            if ((doubleValueString.length() != pp.getIndex()) || (num == null)) {
                throw new NumberFormatException("The format of the number is invalid. index " + pp.getIndex()
                        + " locale " + locale.getCountry() + " " + locale.getLanguage());
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
            Number num = decimalFormatForMoney.parse(doubleValueString, pp);
            if ((doubleValueString.length() != pp.getIndex()) || (num == null)) {
                throw new NumberFormatException("The format of the number is invalid. index " + pp.getIndex()
                        + " locale " + locale.getCountry() + " " + locale.getLanguage());
            }
            dNum = num.doubleValue();
        } catch (Exception e) {
            throw new NumberFormatException(e.getMessage() + " .Number " + doubleValueString);
        }
        return dNum;
    }

    public String getDoubleStringForMoney(Double dNumber) {
        return decimalFormatForMoney.format(dNumber);
    }

    public String getDoubleStringForInterest(Double dNumber) {
        return decimalFormatForInterest.format(dNumber);
    }

    public String getDoubleValueString(Double dNumber) {
        return decimalFormat.format(dNumber);
    }

    public String getDateSeparatorForCurrentLocale() {
        return dateSeparator;
    }

    private String getDateSeparator() {
        Locale[] locales = DateFormat.getAvailableLocales();
        if (!isLocaleSupported(locales, locale)) {
            throw new RuntimeException("DateFormat class doesn't support this country code: " + locale.getCountry()
                    + " and language code: " + locale.getLanguage());
        }

        return getDateSeparator(locale, DateFormat.SHORT);
    }

    public String getDateSeparator(Locale locale, int dateFormat) {
        String separator = "";
        DateFormat format = DateFormat.getDateInstance(dateFormat, locale);
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
        Locale[] locales = DateFormat.getAvailableLocales();
        if (!isLocaleSupported(locales, locale)) {
            throw new RuntimeException("DateFormat class doesn't support this country code: " + locale.getCountry()
                    + " and language code: " + locale.getLanguage());
        }

        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, locale);
        return format;

    }

    /**
     * Use this if you want the year part to have 4 digits
     **/
    public DateFormat getDateFormatWithFullYear() {
        DateFormat dateFormat = getDateFormat();
        if (SimpleDateFormat.class.equals(dateFormat.getClass())) {
            return new SimpleDateFormat(((SimpleDateFormat) dateFormat).toPattern().replace("yy", "yyyy"), locale);
        }
        return dateFormat;
    }

}
