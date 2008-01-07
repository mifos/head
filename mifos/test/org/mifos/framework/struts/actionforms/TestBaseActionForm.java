package org.mifos.framework.struts.actionforms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.mifos.config.AccountingRules;
import org.mifos.config.Localization;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.ConversionResult;
import org.mifos.framework.util.helpers.TestObjectFactory;
import org.mifos.framework.util.helpers.ConversionError;


public class TestBaseActionForm extends MifosTestCase {

	public void testGetDateFromString() throws Exception {
		UserContext userContext = TestObjectFactory.getContext();
		BaseActionForm baseActionForm = new BaseActionForm();
		SimpleDateFormat shortFormat = (SimpleDateFormat)DateFormat.
					getDateInstance(DateFormat.SHORT, userContext.getPreferredLocale());
		Date date = Calendar.getInstance().getTime();
		assertNotNull(baseActionForm.getDateFromString(shortFormat.format(date), userContext.getPreferredLocale()));

	}
	
	public void testGetStringValue() throws Exception {
		BaseActionForm baseActionForm = new BaseActionForm();
		String one = "1";
		assertEquals(one, baseActionForm.getStringValue(true));
		String strValue = "0.25";
		Locale locale = Localization.getInstance().getMainLocale();
		LocalizationConverter converter = LocalizationConverter.getInstance();
		if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
			assertEquals(strValue, baseActionForm.getStringValue(0.25));
		converter.setCurrentLocale(new Locale("IS", "is"));
		strValue = "0,25";
		assertEquals(strValue, baseActionForm.getStringValue(0.25));
		converter.setCurrentLocale(locale);
	}
	
	public void testGetDoubleValue() throws Exception {
		BaseActionForm baseActionForm = new BaseActionForm();
		Locale locale = Localization.getInstance().getMainLocale();
		LocalizationConverter converter = LocalizationConverter.getInstance();
		double dValue = 2.34;
		if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
			assertEquals(dValue, baseActionForm.getDoubleValue("2.34"));
		
		converter.setCurrentLocale(new Locale("IS", "is"));
		assertEquals(dValue, baseActionForm.getDoubleValue("2,34"));
		converter.setCurrentLocale(locale);
	}
	
	public void  testParseDoubleForMoney()
    {
		
		BaseActionForm baseActionForm = new BaseActionForm();
		
		Locale locale = Localization.getInstance().getMainLocale();
		LocalizationConverter converter = LocalizationConverter.getInstance();
		converter.setCurrentLocale(new Locale("en", "GB"));
		String doubleStr = "222.4";
		Double value = 222.4;
		ConversionResult result = baseActionForm.parseDoubleForMoney(doubleStr);
		assertEquals(value, result.getDoubleValue());
		
		doubleStr = "222,4";
		result = baseActionForm.parseDoubleForMoney(doubleStr);
		assertEquals(ConversionError.NOT_ALL_NUMBER, result.getErrors().get(0));
		doubleStr = "222a4";
		result = baseActionForm.parseDoubleForMoney(doubleStr);
		assertEquals(ConversionError.NOT_ALL_NUMBER, result.getErrors().get(0));
		doubleStr = "222.40000";
		result = baseActionForm.parseDoubleForMoney(doubleStr);
		assertEquals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_MONEY, result.getErrors().get(0));
		doubleStr = "222222222222.4";
		result = baseActionForm.parseDoubleForMoney(doubleStr);
		assertEquals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY, result.getErrors().get(0));
		
		converter.setCurrentLocale(new Locale("IS", "is"));
		result = baseActionForm.parseDoubleForMoney("222,4");
		assertEquals(value, result.getDoubleValue());
		doubleStr = "222.4";
		result = baseActionForm.parseDoubleForMoney(doubleStr);
		assertEquals(ConversionError.NOT_ALL_NUMBER, result.getErrors().get(0));
		doubleStr = "222a4";
		result = baseActionForm.parseDoubleForMoney(doubleStr);
		assertEquals(ConversionError.NOT_ALL_NUMBER, result.getErrors().get(0));
		doubleStr = "222,40000";
		result = baseActionForm.parseDoubleForMoney(doubleStr);
		assertEquals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_MONEY, result.getErrors().get(0));
		doubleStr = "222222222222,4";
		result = baseActionForm.parseDoubleForMoney(doubleStr);
		assertEquals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY, result.getErrors().get(0));
	
		converter.setCurrentLocale(locale);
    
    }
	
	public void  testParseDoubleForInterest()
    {
		
		BaseActionForm baseActionForm = new BaseActionForm();
		
		Locale locale = Localization.getInstance().getMainLocale();
		LocalizationConverter converter = LocalizationConverter.getInstance();
		converter.setCurrentLocale(new Locale("en", "GB"));
		String doubleStr = "222.12345";
		Double value = 222.12345;
		ConversionResult result = baseActionForm.parseDoubleForInterest(doubleStr);
		assertEquals(value, result.getDoubleValue());
		
		doubleStr = "222,12345";
		result = baseActionForm.parseDoubleForInterest(doubleStr);
		assertEquals(ConversionError.NOT_ALL_NUMBER, result.getErrors().get(0));
		doubleStr = "222a4";
		result = baseActionForm.parseDoubleForInterest(doubleStr);
		assertEquals(ConversionError.NOT_ALL_NUMBER, result.getErrors().get(0));
		doubleStr = "222.123456";
		result = baseActionForm.parseDoubleForInterest(doubleStr);
		assertEquals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST, result.getErrors().get(0));
		doubleStr = "12345678910.12345";
		result = baseActionForm.parseDoubleForInterest(doubleStr);
		assertEquals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_INTEREST, result.getErrors().get(0));
		
		converter.setCurrentLocale(new Locale("IS", "is"));
		result = baseActionForm.parseDoubleForInterest("222,12345");
		assertEquals(value, result.getDoubleValue());
		doubleStr = "222.12345";
		result = baseActionForm.parseDoubleForInterest(doubleStr);
		assertEquals(ConversionError.NOT_ALL_NUMBER, result.getErrors().get(0));
		doubleStr = "2a2";
		result = baseActionForm.parseDoubleForInterest(doubleStr);
		assertEquals(ConversionError.NOT_ALL_NUMBER, result.getErrors().get(0));
		doubleStr = "222,123456";
		result = baseActionForm.parseDoubleForInterest(doubleStr);
		assertEquals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST, result.getErrors().get(0));
		doubleStr = "12345678910,12345";
		result = baseActionForm.parseDoubleForInterest(doubleStr);
		assertEquals(ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_INTEREST, result.getErrors().get(0));
		
		converter.setCurrentLocale(locale);
    
    }
	
	
}
