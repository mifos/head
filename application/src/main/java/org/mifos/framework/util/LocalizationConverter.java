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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.config.Localization;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DoubleConversionResult;

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
    // the decimalFormatLocale is introduced because the double format is not
    // supported for
    // 1.1 realease yet and the English format is still used no matter what the
    // configured locale is
    private Locale decimalFormatLocale;
    // the dateLocale is introduced because the date format is not supported for
    // 1.1 realease yet and the English format is still used no matter what the
    // configured locale is
    private Locale dateLocale;

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

    private boolean supportThisLocale(Locale[] locales, Locale locale) {

        Locale tempLocale = null;
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
        Locale[] locales = NumberFormat.getInstance().getAvailableLocales();
        // use this English locale for decimal format for 1.1 release
        boolean find = supportThisLocale(locales, decimalFormatLocale);
        if (find == false) {
            throw new RuntimeException("NumberFormat class doesn't support this country code: "
                    + decimalFormatLocale.getCountry() + " and language code: " + decimalFormatLocale.getLanguage());
        }
        NumberFormat format = DecimalFormat.getInstance(decimalFormatLocale);
        if (format instanceof DecimalFormat) {
            currentDecimalFormat = (DecimalFormat) format;
            currentDecimalFormatForMoney = buildDecimalFormat(digitsBeforeDecimalForMoney, digitsAfterDecimalForMoney,
                    (DecimalFormat) currentDecimalFormat.clone(), Boolean.TRUE);
            //
            currentDecimalFormatForInterest = buildDecimalFormat(digitsBeforeDecimalForInterest,
                    digitsAfterDecimalForInterest, (DecimalFormat) currentDecimalFormat.clone(), Boolean.FALSE);
            decimalFormatSymbol = currentDecimalFormat.getDecimalFormatSymbols().getDecimalSeparator();
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
                ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_MONEY, doubleStr);
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
                ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST, doubleStr);
        result.setErrors(errors);
        if (errors.size() > 0) {
            return result;
        }
        try {
            Double interest = getDoubleValueForInterest(doubleStr);
            if ((interest > AccountingRules.getMaxInterest()) || (interest < AccountingRules.getMinInterest())) {
                errors.add(ConversionError.INTEREST_OUT_OF_RANGE);
            } else {
                result.setDoubleValue(interest);
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
            ConversionError errorDigitsAfter, String number) {
        List<ConversionError> errors = new ArrayList<ConversionError>();
        char temp;
        ConversionError error = null;
        for (int i = 0; i < number.length(); i++) {
            if (Character.isDigit(number.charAt(i)) == false) {
                temp = number.charAt(i);
                if (temp != decimalFormatSymbol) {
                    error = ConversionError.NOT_ALL_NUMBER;
                    errors.add(error);
                    return errors;
                }

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

    private Double getDoubleValueForInterest(String doubleValueString) {

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
        if (!supportThisLocale(locales, dateLocale)) {
            throw new RuntimeException("DateFormat class doesn't support this country code: " + dateLocale.getCountry()
                    + " and language code: " + dateLocale.getLanguage());
        }

        String separator = "";
        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, dateLocale);
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
        if (!supportThisLocale(locales, dateLocale)) {
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
