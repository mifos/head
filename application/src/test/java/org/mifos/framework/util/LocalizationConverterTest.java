/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
import java.util.Calendar;
import java.util.Locale;

import junit.framework.TestCase;

import org.mifos.config.AccountingRules;
import org.mifos.config.Localization;
import org.mifos.framework.util.helpers.ConversionError;
import org.mifos.framework.util.helpers.ConversionResult;
import org.mifos.framework.util.helpers.DateUtils;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class LocalizationConverterTest extends TestCase {

    public void testDateFormattingWithFourDigitsInYear() throws Exception {
        LocalizationConverter instance = LocalizationConverter.getInstance();
        DateFormat dateFormat = instance.getDateFormatWithFullYear();
        assertEquals("13/12/2008", dateFormat.format(DateUtils.getDate(2008, Calendar.DECEMBER, 13)));
    }
    
    public void testGetDecimalFormatSymbol() {
        Locale locale = Localization.getInstance().getMainLocale();
        LocalizationConverter converter = LocalizationConverter.getInstance();
        char sep = '.';
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
            assertEquals(sep, converter.getDecimalFormatSymbol());
        converter.setCurrentLocale(new Locale("IS", "is"));
        sep = ',';
        assertEquals(sep, converter.getDecimalFormatSymbol());
        converter.setCurrentLocale(locale);
    }

    public void testGetDoubleStringForMoney() {

        String doubleValueString = "2.5";
        Double dValue = 2.5000000000;
        Locale locale = Localization.getInstance().getMainLocale();
        LocalizationConverter converter = LocalizationConverter.getInstance();
        String dString = converter.getDoubleStringForMoney(dValue);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
            assertEquals(doubleValueString, dString);
        converter.setCurrentLocale(new Locale("IS", "is"));
        doubleValueString = "2,5";
        dString = converter.getDoubleStringForMoney(dValue);
        assertEquals(doubleValueString, dString);
        converter.setCurrentLocale(locale);
    }

    public void testGetDoubleStringForInterest() {

        String doubleValueString = "2123.12345";
        Double dValue = 2123.12345000000;
        Locale locale = Localization.getInstance().getMainLocale();
        LocalizationConverter converter = LocalizationConverter.getInstance();
        String dString = converter.getDoubleStringForInterest(dValue);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
            assertEquals(doubleValueString, dString);
        converter.setCurrentLocale(new Locale("IS", "is"));
        doubleValueString = "2123,12345";
        dString = converter.getDoubleStringForInterest(dValue);
        assertEquals(doubleValueString, dString);
        converter.setCurrentLocale(locale);
    }

    public void testGetDoubleValueString() {

        String doubleValueString = "2.59";
        Double dValue = 2.59;
        Locale locale = Localization.getInstance().getMainLocale();
        LocalizationConverter converter = LocalizationConverter.getInstance();
        String dString = converter.getDoubleValueString(dValue);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
            assertEquals(doubleValueString, dString);
        converter.setCurrentLocale(new Locale("IS", "is"));
        doubleValueString = "2,59";
        dString = converter.getDoubleValueString(dValue);
        assertEquals(doubleValueString, dString);
        converter.setCurrentLocale(locale);
    }

    public void testGetDateSeparator() {
        String separator = "/";
        Locale locale = Localization.getInstance().getMainLocale();
        LocalizationConverter converter = LocalizationConverter.getInstance();
        String dateSeparator = converter.getDateSeparatorForCurrentLocale();
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
            assertEquals(separator, dateSeparator);
        converter.setCurrentLocale(new Locale("IS", "is"));
        dateSeparator = converter.getDateSeparatorForCurrentLocale();
        assertEquals(".", dateSeparator);
        converter.setCurrentLocale(locale);

    }

    public void testParseDoubleForMoney() {
        String doubleValueString = "2.5";
        Double dValue = 2.5;
        Short digitsAfterForMoneySaved = AccountingRules.getDigitsAfterDecimal();
        Short digitsBeforeForMoneySaved = AccountingRules.getDigitsBeforeDecimal();
        Short digitsAfterForMoney = 1;
        Short digitsBeforeForMoney = 7;
        AccountingRules.setDigitsAfterDecimal(digitsAfterForMoney);
        AccountingRules.setDigitsBeforeDecimal(digitsBeforeForMoney);
        Locale locale = Localization.getInstance().getMainLocale();
        LocalizationConverter converter = LocalizationConverter.getInstance();
        ConversionResult result = converter.parseDoubleForMoney(doubleValueString);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            assertEquals(result.getDoubleValue(), dValue);
            // if the wrong decimal separator is entered, error will be returned
            doubleValueString = "2,59";
            result = converter.parseDoubleForMoney(doubleValueString);
            assertEquals(result.getErrors().get(0), ConversionError.NOT_ALL_NUMBER);
            doubleValueString = "2a59";
            result = converter.parseDoubleForMoney(doubleValueString);
            assertEquals(result.getErrors().get(0), ConversionError.NOT_ALL_NUMBER);
            doubleValueString = "123456789.59";
            result = converter.parseDoubleForMoney(doubleValueString);
            assertEquals(result.getErrors().get(0),
                    ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_MONEY);
            assertEquals(result.getErrors().get(1),
                    ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY);
            doubleValueString = "222222222.5";
            result = converter.parseDoubleForMoney(doubleValueString);
            assertEquals(result.getErrors().get(0),
                    ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY);
        }
        AccountingRules.setDigitsAfterDecimal(digitsAfterForMoneySaved);
        AccountingRules.setDigitsBeforeDecimal(digitsBeforeForMoneySaved);
        converter.setCurrentLocale(locale);

    }

    public void testParseDoubleForInterest() {
        String doubleValueString = "222.59562";
        Double dValue = 222.59562;
        Short digitsAfterForInterestSaved = AccountingRules.getDigitsAfterDecimalForInterest();
        Short digitsBeforeForInterestSaved = AccountingRules.getDigitsBeforeDecimalForInterest();
        Short digitsAfterForInterest = 5;
        Short digitsBeforeForInterest = 10;

        AccountingRules.setDigitsAfterDecimalForInterest(digitsAfterForInterest);
        AccountingRules.setDigitsBeforeDecimalForInterest(digitsBeforeForInterest);
        Locale locale = Localization.getInstance().getMainLocale();
        LocalizationConverter converter = LocalizationConverter.getInstance();
        ConversionResult result = converter.parseDoubleForInterest(doubleValueString);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            assertEquals(result.getDoubleValue(), dValue);
            // if the wrong decimal separator is entered, error will be returned
            doubleValueString = "222,59562";
            result = converter.parseDoubleForInterest(doubleValueString);
            assertEquals(result.getErrors().get(0), ConversionError.NOT_ALL_NUMBER);
            doubleValueString = "2a5922";
            result = converter.parseDoubleForInterest(doubleValueString);
            assertEquals(result.getErrors().get(0), ConversionError.NOT_ALL_NUMBER);
            doubleValueString = "222.595690";
            result = converter.parseDoubleForInterest(doubleValueString);
            assertEquals(result.getErrors().get(0),
                    ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST);
            doubleValueString = "22222222222.5";
            result = converter.parseDoubleForInterest(doubleValueString);
            assertEquals(result.getErrors().get(0),
                    ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_INTEREST);
        }
        AccountingRules.setDigitsAfterDecimalForInterest(digitsAfterForInterestSaved);
        AccountingRules.setDigitsBeforeDecimalForInterest(digitsBeforeForInterestSaved);
        converter.setCurrentLocale(locale);

    }

    /*
     * get convert a string to a double to the config locale and the format is
     * the money format 7.1
     */
    public void testGetDoubleValueForCurrentLocale() {
        String doubleValueString = "223.59";
        Double dValue = 223.59;
        Locale locale = Localization.getInstance().getMainLocale();
        LocalizationConverter converter = LocalizationConverter.getInstance();
        Double dNumber = converter.getDoubleValueForCurrentLocale(doubleValueString);
        if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN")) {
            assertEquals(dNumber, dValue);
            // if the wrong decimal separator is entered, it will throw
            // exception
            doubleValueString = "223,59";
            try {
                dNumber = converter.getDoubleValueForCurrentLocale(doubleValueString);
            } catch (Exception ex) {
                assertTrue(ex.getMessage().startsWith("The format of the number is invalid."));
            }
        }
        converter.setCurrentLocale(new Locale("IS", "is"));
        doubleValueString = "223,59";
        dNumber = converter.getDoubleValueForCurrentLocale(doubleValueString);
        assertEquals(dNumber, dValue);
        // if the wrong decimal separator is entered, it will throw exception
        doubleValueString = "223.59";
        try {
            dNumber = converter.getDoubleValueForCurrentLocale(doubleValueString);
        } catch (Exception ex) {
            assertTrue(ex.getMessage().startsWith("The format of the number is invalid."));
        }
        converter.setCurrentLocale(locale);

    }

}
