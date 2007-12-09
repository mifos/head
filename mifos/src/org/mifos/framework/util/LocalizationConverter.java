package org.mifos.framework.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;
import org.mifos.config.Localization;
import java.text.DecimalFormatSymbols;

import java.text.SimpleDateFormat;

public class LocalizationConverter {
	
	private static DecimalFormat currentDecimalFormat;
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
		currentDecimalFormat = getDecimalFormatForCurrentLocale();
		dateSeparator = getDateSeparator();
		decimalFormatSymbol = loadDecimalFormatSymbol();
	}
	
	// for testing purpose only
	public void setCurrentLocale(Locale locale)
	{
		if (currentLocale.equals(locale))
			return;
		currentLocale = locale;
		currentDecimalFormat = getDecimalFormatForCurrentLocale();
		dateSeparator = getDateSeparator();
		decimalFormatSymbol = loadDecimalFormatSymbol();
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
	
	private DecimalFormat getDecimalFormatForCurrentLocale()
	{
		if (currentLocale == null)
			throw new RuntimeException("The current locale is not set for LocalizationConverter.");
		Locale[] locales = NumberFormat.getInstance().getAvailableLocales();
		boolean find = supportThisLocale(locales);
		if (find == false)
			throw new RuntimeException("NumberFormat class doesn't support this country code: " +
					currentLocale.getCountry() + " and language code: " + currentLocale.getLanguage());
		DecimalFormat decimalFormat = null;
		NumberFormat format = NumberFormat.getInstance(currentLocale);
		format = DecimalFormat.getInstance(currentLocale);
		if (format instanceof DecimalFormat) 
			decimalFormat = (DecimalFormat)format;
		return decimalFormat;
	}
	
	private char loadDecimalFormatSymbol()
	{
		DecimalFormatSymbols symbols = currentDecimalFormat.getDecimalFormatSymbols();
		char symbol = symbols.getDecimalSeparator();
		return symbol;
	}
	
	public char getDecimalFormatSymbol()
	{
		return decimalFormatSymbol;
	}
	
	
	public Double getDoubleValueForCurrentLocale(String doubleValueString)
	{
		
		if (currentDecimalFormat == null)
			currentDecimalFormat = getDecimalFormatForCurrentLocale();
		Double dNum = null;
		try
		{
			Number num = currentDecimalFormat.parse(doubleValueString);
			dNum = num.doubleValue();
		}
		catch (Exception e)
		{
			throw new NumberFormatException(e.getMessage() + " .Number " + doubleValueString);
		}
		return dNum;
	}
	
	public String getDoubleValueStringForCurrentLocale(Double dNumber)
	{
		if (currentDecimalFormat == null)
			currentDecimalFormat = getDecimalFormatForCurrentLocale();
		
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
