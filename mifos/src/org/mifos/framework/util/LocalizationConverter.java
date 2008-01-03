package org.mifos.framework.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import org.mifos.config.Localization;
import java.text.ParsePosition;
import org.mifos.config.AccountingRules;

import java.text.SimpleDateFormat;

public class LocalizationConverter {
	private static DecimalFormat currentDecimalFormat;
	private static DecimalFormat currentDecimalFormatForMoney;
	private static DecimalFormat currentDecimalFormatForInterest;
	private static String dateSeparator;
	private static Locale currentLocale;
	private static char decimalFormatSymbol;
	
	private static final LocalizationConverter localizationConverter = 
		new LocalizationConverter();

	public static LocalizationConverter getInstance() {
		return localizationConverter;	
	}
	
	private LocalizationConverter() {
		currentLocale = Localization.getInstance().getMainLocale();
		loadDecimalFormats();
		dateSeparator = getDateSeparator();
	}
	
	// for testing purpose only
	public void setCurrentLocale(Locale locale)
	{
		currentLocale = locale;
		loadDecimalFormats();
		dateSeparator = getDateSeparator();
	}
	
	private boolean supportThisLocale(Locale[] locales)
	{

		Locale tempLocale = null;
		boolean find = false;
		
		for (int i=0; i < locales.length; i++)
		{
			tempLocale = locales[i];
			if (tempLocale.getCountry().equals(currentLocale.getCountry()) && 
					(tempLocale.getLanguage().equals(currentLocale.getLanguage())))
			{
				find = true;
				break;
			}
		}
		return find;
	}
	
	private DecimalFormat buildDecimalFormat(Short digitsBefore, Short digitsAfter, DecimalFormat decimalFormat)
	{
		StringBuffer pattern = new StringBuffer();
		for (short i=0; i < digitsBefore ; i++)
			pattern.append('#');
		pattern.append(decimalFormat.getDecimalFormatSymbols().getDecimalSeparator());
		for (short i=0; i < digitsAfter ; i++)
			pattern.append('#');
		decimalFormat.applyLocalizedPattern(pattern.toString());
		return decimalFormat;
	}
	
	private void loadDecimalFormats()
	{
		if (currentLocale == null)
			throw new RuntimeException("The current locale is not set for LocalizationConverter.");
		Locale[] locales = NumberFormat.getInstance().getAvailableLocales();
		boolean find = supportThisLocale(locales);
		if (find == false)
			throw new RuntimeException("NumberFormat class doesn't support this country code: " +
					currentLocale.getCountry() + " and language code: " + currentLocale.getLanguage());
		NumberFormat format = DecimalFormat.getInstance(currentLocale);
		if (format instanceof DecimalFormat)
		{
			Short defaultValue = 1;
			Short digitsBeforeDecimal = AccountingRules.getDigitsBeforeDecimal();
			Short digitsAfterDecimal = AccountingRules.getDigitsAfterDecimal(defaultValue);
			currentDecimalFormat = (DecimalFormat)format;
			currentDecimalFormatForMoney = buildDecimalFormat(digitsBeforeDecimal, digitsAfterDecimal, (DecimalFormat)currentDecimalFormat.clone());
			//
			digitsBeforeDecimal = AccountingRules.getDigitsBeforeDecimalForInterest();
			digitsAfterDecimal = AccountingRules.getDigitsAfterDecimalForInterest();
			currentDecimalFormatForInterest = buildDecimalFormat(digitsBeforeDecimal, digitsAfterDecimal, (DecimalFormat)currentDecimalFormat.clone());
			decimalFormatSymbol = currentDecimalFormat.getDecimalFormatSymbols().getDecimalSeparator();
		}
	}
	
	
	public char getDecimalFormatSymbol()
	{
		return decimalFormatSymbol;
	}
	
	
	public Double getDoubleValueForCurrentLocale(String doubleValueString)
	{
		
		if (currentDecimalFormatForMoney == null)
			loadDecimalFormats();
		Double dNum = null;
		try
		{
			ParsePosition pp = new ParsePosition(0);
			Number num = currentDecimalFormatForMoney.parse(doubleValueString, pp);
			if ((doubleValueString.length() != pp.getIndex()) || (num == null))
			{
				throw new NumberFormatException("The format of the number is invalid. index " + pp.getIndex() +
						" locale " + currentLocale.getCountry() + " " + currentLocale.getLanguage());
			}
			dNum = num.doubleValue();
		}
		catch (Exception e)
		{
			throw new NumberFormatException(e.getMessage() + " .Number " + doubleValueString);
		}
		return dNum;
	}
	
	public Double getDoubleValue(String doubleValueString)
	{
		
		if (currentDecimalFormat == null)
			loadDecimalFormats();
		Double dNum = null;
		try
		{
			ParsePosition pp = new ParsePosition(0);
			Number num = currentDecimalFormat.parse(doubleValueString, pp);
			if ((doubleValueString.length() != pp.getIndex()) || (num == null))
			{
				throw new NumberFormatException("The format of the number is invalid for locale " +
						 currentLocale.getCountry() + " " + currentLocale.getLanguage());
			}
			dNum = num.doubleValue();
		}
		catch (Exception e)
		{
			throw new NumberFormatException(e.getMessage() + " .Number " + doubleValueString);
		}
		return dNum;
	}
	
	
	public String getDoubleValueString(Double dNumber)
	{
		if (currentDecimalFormat == null)
			loadDecimalFormats();
		return currentDecimalFormat.format(dNumber);
	}
	
	public String getDateSeparatorForCurrentLocale()
	{
		if (dateSeparator == null)
			dateSeparator = getDateSeparator();
		
		return dateSeparator;
		
	}
	
	private String getDateSeparator()
	{
		if (currentLocale == null)
			throw new RuntimeException("The current locale is not set for LocalizationConverter.");
		Locale[] locales = DateFormat.getInstance().getAvailableLocales();
		boolean find = supportThisLocale(locales);
		if (find == false)
			throw new RuntimeException("DateFormat class doesn't support this country code: " +
					currentLocale.getCountry() + " and language code: " + currentLocale.getLanguage());
		
		String separator = "";
		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, currentLocale);
		String now = format.format(new java.util.Date());
		char chArray[] = now.toCharArray();
		for (int i = 0; i < chArray.length; i++) 
		{
			if (Character.isDigit(chArray[i]) == false)
			{
				separator = String.valueOf(chArray[i]);
				break;
			}
		}
		return separator;
		
	}
	
	public DateFormat getDateFormat()
	{
		if (currentLocale == null)
			throw new RuntimeException("The current locale is not set for LocalizationConverter.");
		Locale[] locales = DateFormat.getInstance().getAvailableLocales();
		boolean find = supportThisLocale(locales);
		if (find == false)
			throw new RuntimeException("DateFormat class doesn't support this country code: " +
					currentLocale.getCountry() + " and language code: " + currentLocale.getLanguage());

		DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT, currentLocale);
		DateFormat simpleFormat = SimpleDateFormat.getDateInstance(DateFormat.SHORT, currentLocale);
		return format;
		
	}
	
	

}
