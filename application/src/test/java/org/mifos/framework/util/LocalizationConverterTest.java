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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.config.AccountingRules;
import org.mifos.config.Localization;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.DoubleConversionResult;

public class LocalizationConverterTest {

    private LocalizationConverter converter;

    @Before
    public void setup() {
        short digitsAfterDecimalForMoney = Short.valueOf("1");
        short digitsBeforeDecimalForMoney= Short.valueOf("1");
        short digitsAfterDecimalForInterest = Short.valueOf("5");
        short digitsBeforeDecimalForInterest = Short.valueOf("1");
        short digitsBeforeDecimalForCashFlowValidations = Short.valueOf("1");
        short digitsAfterDecimalForCashFlowValidations = Short.valueOf("5");

        converter = new LocalizationConverter(digitsAfterDecimalForMoney, digitsBeforeDecimalForMoney, digitsAfterDecimalForInterest,
                digitsBeforeDecimalForInterest, digitsBeforeDecimalForCashFlowValidations, digitsAfterDecimalForCashFlowValidations);
    }

    @Test
    public void testDateFormattingWithFourDigitsInYear() throws Exception {
        DateFormat dateFormat = converter.getDateFormatWithFullYear();
        Assert.assertEquals("13/12/2008", dateFormat.format(DateUtils.getDate(2008, Calendar.DECEMBER, 13)));
    }

    @Test
    public void testGetDecimalFormatSymbol() {
        Locale locale = Localization.getInstance().getConfiguredLocale();
        char sep = '.';
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            Assert.assertEquals(sep, converter.getDecimalFormatSymbol());
        }
        converter.setCurrentLocale(new Locale("IS", "is"));
        sep = ',';
        Assert.assertEquals(sep, converter.getDecimalFormatSymbol());
        converter.setCurrentLocale(locale);
    }

    @Test
    public void testGetDoubleStringForMoney() {

        String doubleValueString = "2.5";
        Double dValue = 2.5000000000;
        Locale locale = Localization.getInstance().getConfiguredLocale();
        String dString = converter.getDoubleStringForMoney(dValue);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            Assert.assertEquals(doubleValueString, dString);
        }
        converter.setCurrentLocale(new Locale("IS", "is"));
        doubleValueString = "2,5";
        dString = converter.getDoubleStringForMoney(dValue);
        Assert.assertEquals(doubleValueString, dString);
        converter.setCurrentLocale(locale);
    }

    @Test
    public void testGetDoubleStringForInterest() {

        String doubleValueString = "2123.12345";
        Double dValue = 2123.12345000000;
        Locale locale = Localization.getInstance().getConfiguredLocale();
        String dString = converter.getDoubleStringForInterest(dValue);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            Assert.assertEquals(doubleValueString, dString);
        }
        converter.setCurrentLocale(new Locale("IS", "is"));
        doubleValueString = "2123,12345";
        dString = converter.getDoubleStringForInterest(dValue);
        Assert.assertEquals(doubleValueString, dString);
        converter.setCurrentLocale(locale);
    }

    @Test
    public void testGetDoubleValueString() {

        String doubleValueString = "2.59";
        Double dValue = 2.59;
        Locale locale = Localization.getInstance().getConfiguredLocale();
        String dString = converter.getDoubleValueString(dValue);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            Assert.assertEquals(doubleValueString, dString);
        }
        converter.setCurrentLocale(new Locale("IS", "is"));
        doubleValueString = "2,59";
        dString = converter.getDoubleValueString(dValue);
        Assert.assertEquals(doubleValueString, dString);
        converter.setCurrentLocale(locale);
    }

    /**
     * Currently broken -- incomplete support for multiple locales for numeric
     * input.
     */
    @Ignore
    @Test
    public void testGetDateSeparator() {
        String separator = "/";
        Locale locale = Localization.getInstance().getConfiguredLocale();
        String dateSeparator = converter.getDateSeparatorForCurrentLocale();
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            Assert.assertEquals(separator, dateSeparator);
        }
        converter.setCurrentLocale(new Locale("IS", "is"));
        dateSeparator = converter.getDateSeparatorForCurrentLocale();
        Assert.assertEquals(".", dateSeparator);
        converter.setCurrentLocale(locale);

    }

    @Test
    public void testParseDoubleForMoney() {
        String doubleValueString = "2.5";
        Double dValue = 2.5;
        Short digitsAfterForMoneySaved = AccountingRules.getDigitsAfterDecimal();
        Short digitsAfterForMoney = 1;
        AccountingRules.setDigitsAfterDecimal(digitsAfterForMoney);
        Locale locale = Localization.getInstance().getConfiguredLocale();
        DoubleConversionResult result = converter.parseDoubleForMoney(doubleValueString);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            Assert.assertEquals(result.getDoubleValue(), dValue);
            // if the wrong decimal separator is entered, error will be returned
            doubleValueString = "2,59";
            result = converter.parseDoubleForMoney(doubleValueString);
            Assert.assertEquals(result.getErrors().get(0), ConversionError.NOT_ALL_NUMBER);
            doubleValueString = "2a59";
            result = converter.parseDoubleForMoney(doubleValueString);
            Assert.assertEquals(result.getErrors().get(0), ConversionError.NOT_ALL_NUMBER);
            doubleValueString = "123456789111111.59";
            result = converter.parseDoubleForMoney(doubleValueString);
            Assert.assertEquals(result.getErrors().get(0),
                    ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_MONEY);
            Assert.assertEquals(result.getErrors().get(1),
                    ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY);
            doubleValueString = "222222222111111.5";
            result = converter.parseDoubleForMoney(doubleValueString);
            Assert.assertEquals(result.getErrors().get(0),
                    ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY);
        }
        AccountingRules.setDigitsAfterDecimal(digitsAfterForMoneySaved);
        converter.setCurrentLocale(locale);

    }

    @Test
    public void testParseDoubleForInstallmentTotalAmount() {
        MifosCurrency mifosCurrency = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        LocalizationConverter localizationConverter = new LocalizationConverter(mifosCurrency);
        DoubleConversionResult result = localizationConverter.parseDoubleForInstallmentTotalAmount("-478.2");
        assertThat(result.getDoubleValue(), is(-478.2));
        result = localizationConverter.parseDoubleForInstallmentTotalAmount("478.2");
        assertThat(result.getDoubleValue(), is(478.2));
        result = localizationConverter.parseDoubleForInstallmentTotalAmount("2,59");
        assertThat(result.getErrors().get(0), is(ConversionError.NOT_ALL_NUMBER));
        result = localizationConverter.parseDoubleForInstallmentTotalAmount("222222222111111.5");
        assertThat(result.getErrors().get(0), is(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY));
    }

    @Test
    public void testParseDoubleForInterest() {
        String doubleValueString = "222.59562";
        Double dValue = 222.59562;
        Short digitsAfterForInterestSaved = AccountingRules.getDigitsAfterDecimalForInterest();
        Short digitsAfterForInterest = 5;
        AccountingRules.setDigitsAfterDecimalForInterest(digitsAfterForInterest);
        Locale locale = Localization.getInstance().getConfiguredLocale();


        short digitsAfterDecimalForMoney = Short.valueOf("1");
        short digitsBeforeDecimalForMoney= Short.valueOf("1");
        short digitsAfterDecimalForInterest = Short.valueOf("5");
        short digitsBeforeDecimalForInterest = Short.valueOf("6");
        short digitsBeforeDecimalForCashFlowValidations = Short.valueOf("1");
        short digitsAfterDecimalForCashFlowValidations = Short.valueOf("5");

        converter = new LocalizationConverter(digitsAfterDecimalForMoney, digitsBeforeDecimalForMoney, digitsAfterDecimalForInterest,
                digitsBeforeDecimalForInterest, digitsBeforeDecimalForCashFlowValidations, digitsAfterDecimalForCashFlowValidations);

        DoubleConversionResult result = converter.parseDoubleForInterest(doubleValueString);

        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            Assert.assertEquals(result.getDoubleValue(), dValue);
            // if the wrong decimal separator is entered, error will be returned
            doubleValueString = "222,59562";
            result = converter.parseDoubleForInterest(doubleValueString);
            Assert.assertEquals(result.getErrors().get(0), ConversionError.NOT_ALL_NUMBER);
            doubleValueString = "2a5922";
            result = converter.parseDoubleForInterest(doubleValueString);
            Assert.assertEquals(result.getErrors().get(0), ConversionError.NOT_ALL_NUMBER);
            doubleValueString = "222.595690";
            result = converter.parseDoubleForInterest(doubleValueString);
            Assert.assertEquals(result.getErrors().get(0),
                    ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST);
            doubleValueString = "22222222222.5";
            result = converter.parseDoubleForInterest(doubleValueString);
            Assert.assertEquals(result.getErrors().get(0),
                    ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_INTEREST);
        }
        AccountingRules.setDigitsAfterDecimalForInterest(digitsAfterForInterestSaved);
        converter.setCurrentLocale(locale);

    }

    @Test
    public void testParseDoubleForCashFlowValidations() {
        MifosCurrency mifosCurrency = new MifosCurrency(Short.valueOf("1"), "Rupee", BigDecimal.valueOf(1), "INR");
        LocalizationConverter localizationConverter = new LocalizationConverter(mifosCurrency);
        DoubleConversionResult result = parseForCashFlow(localizationConverter, "22.59");
        assertThat(result.getDoubleValue(), is(22.59));
        result = parseForCashFlow(localizationConverter, "222,59562");
        assertThat(result.getErrors().get(0), is(ConversionError.NOT_ALL_NUMBER));
        result = parseForCashFlow(localizationConverter, "2a5922");
        assertThat(result.getErrors().get(0), is(ConversionError.NOT_ALL_NUMBER));
        result = parseForCashFlow(localizationConverter, "22222222222.5");
        assertThat(result.getErrors().get(0), is(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_CASHFLOW_VALIDATION));
        result = parseForCashFlow(localizationConverter, "222.595690");
        assertThat(result.getErrors().get(0), is(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_CASHFLOW_VALIDATION));
        result = parseForCashFlow(localizationConverter, "222.59");
        assertThat(result.getErrors().get(0), is(ConversionError.CASH_FLOW_THRESHOLD_OUT_OF_RANGE));
    }

    private DoubleConversionResult parseForCashFlow(LocalizationConverter localizationConverter, String doubleValueStr) {
        return localizationConverter.parseDoubleForCashFlowValidations(doubleValueStr,
                ConversionError.CASH_FLOW_THRESHOLD_OUT_OF_RANGE,
                AccountingRules.getMinCashFlowThreshold(),
                AccountingRules.getMaxCashFlowThreshold());
    }

    /*
     * get convert a string to a double to the config locale and the format is
     * the money format 7.1
     */
    @Test
    public void testGetDoubleValueForCurrentLocale() {
        String doubleValueString = "223.59";
        Double dValue = 223.59;
        Locale locale = Localization.getInstance().getConfiguredLocale();
        Double dNumber = converter.getDoubleValueForCurrentLocale(doubleValueString);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            Assert.assertEquals(dNumber, dValue);
            // if the wrong decimal separator is entered, it will throw
            // exception
            doubleValueString = "223,59";
            try {
                dNumber = converter.getDoubleValueForCurrentLocale(doubleValueString);
            } catch (Exception ex) {
                Assert.assertTrue(ex.getMessage().startsWith("The format of the number is invalid."));
            }
        }
        converter.setCurrentLocale(new Locale("IS", "is"));
        doubleValueString = "223,59";
        dNumber = converter.getDoubleValueForCurrentLocale(doubleValueString);
        Assert.assertEquals(dNumber, dValue);
        // if the wrong decimal separator is entered, it will throw exception
        doubleValueString = "223.59";
        try {
            dNumber = converter.getDoubleValueForCurrentLocale(doubleValueString);
        } catch (Exception ex) {
            Assert.assertTrue(ex.getMessage().startsWith("The format of the number is invalid."));
        }
        converter.setCurrentLocale(locale);

    }
}