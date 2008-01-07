package org.mifos.framework.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import org.mifos.config.Localization;
import java.text.ParsePosition;
import org.mifos.config.AccountingRules;

import java.text.SimpleDateFormat;
import org.mifos.framework.util.helpers.ConversionResult;
import org.mifos.framework.util.helpers.ConversionError;
import java.util.List;

public class LocalizationConverter {
	private static DecimalFormat currentDecimalFormat;
	private static DecimalFormat currentDecimalFormatForMoney;
	private static DecimalFormat currentDecimalFormatForInterest;
	private static String dateSeparator;
	private static Locale currentLocale;
	private static char decimalFormatSymbol;
	private static short digitsAfterDecimalForMoney;
	private static short digitsBeforeDecimalForMoney;
	private static short digitsAfterDecimalForInterest;
	private static short digitsBeforeDecimalForInterest;
	
	
	
	
	private static final LocalizationConverter localizationConverter = 
		new LocalizationConverter();

	public static LocalizationConverter getInstance() {
		return localizationConverter;	
	}
	
	private LocalizationConverter() {
		currentLocale = Localization.getInstance().getMainLocale();
		Short defaultValue = 1;
		digitsAfterDecimalForMoney = AccountingRules.getDigitsAfterDecimal(defaultValue);
		digitsBeforeDecimalForMoney = AccountingRules.getDigitsBeforeDecimal();
		digitsAfterDecimalForInterest = AccountingRules.getDigitsAfterDecimalForInterest();
		digitsBeforeDecimalForInterest = AccountingRules.getDigitsBeforeDecimalForInterest();
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
			currentDecimalFormat = (DecimalFormat)format;
			currentDecimalFormatForMoney = buildDecimalFormat(digitsBeforeDecimalForMoney, digitsAfterDecimalForMoney, (DecimalFormat)currentDecimalFormat.clone());
			//
			currentDecimalFormatForInterest = buildDecimalFormat(digitsBeforeDecimalForInterest, digitsAfterDecimalForInterest, (DecimalFormat)currentDecimalFormat.clone());
			decimalFormatSymbol = currentDecimalFormat.getDecimalFormatSymbols().getDecimalSeparator();
		}
	}
	
	
	public ConversionResult parseDoubleForMoney(String doubleStr)
	{
		ConversionResult result = new ConversionResult();
		List<ConversionError> errors = checkDigits(digitsBeforeDecimalForMoney, digitsAfterDecimalForMoney, 
				ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_MONEY,
				ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_MONEY, doubleStr);
		result.setErrors(errors);
		if (errors.size() > 0)
		{
			return result;
		}
		try
		{
			result.setDoubleValue(getDoubleValueForCurrentLocale(doubleStr));
		}
		catch (Exception ex)
		{
			// after all the checkings this is not likely to happen, but just in case
			ConversionError error = ConversionError.CONVERSION_ERROR;
			result.getErrors().add(error);
		}
		return result;
	}
	
	public ConversionResult parseDoubleForInterest(String doubleStr)
	{
		ConversionResult result = new ConversionResult();
		List<ConversionError> errors = checkDigits(digitsBeforeDecimalForInterest, digitsAfterDecimalForInterest,
				ConversionError.EXCEEDING_NUMBER_OF_DIGITS_BEFORE_DECIMAL_SEPARATOR_FOR_INTEREST,
				ConversionError.EXCEEDING_NUMBER_OF_DIGITS_AFTER_DECIMAL_SEPARATOR_FOR_INTEREST,doubleStr);
		result.setErrors(errors);
		if (errors.size() > 0)
		{
			return result;
		}
		try
		{
			Double interest = getDoubleValueForInterest(doubleStr);
			if ((interest > AccountingRules.getMaxInterest()) || (interest < AccountingRules.getMinInterest()))
			{
				errors.add(ConversionError.INTEREST_OUT_OF_RANGE);
			}
			else
				result.setDoubleValue(interest);
		}
		catch (Exception ex)
		{
			ConversionError error = ConversionError.CONVERSION_ERROR;
			result.getErrors().add(error);
		}
		return result;
	}
	
	public char getDecimalFormatSymbol()
	{
		return decimalFormatSymbol;
	}
	
	private List<ConversionError> checkDigits(Short digitsBefore, Short digitsAfter, 
			ConversionError errorDigitsBefore, ConversionError errorDigitsAfter, String number)
	{
		List<ConversionError> errors = new ArrayList();;
		char temp;
		ConversionError error = null;
		for (int i=0 ; i < number.length(); i++)
			if (Character.isDigit(number.charAt(i)) == false)
			{
				temp = number.charAt(i);
				if (temp != decimalFormatSymbol)
				{
					error = ConversionError.NOT_ALL_NUMBER;
					errors.add(error);
					return errors;
				}
				
			}
		int index = number.indexOf(decimalFormatSymbol);
		if (index < 0)
		{
			if (number.length() > digitsBefore)
			{
				error = errorDigitsBefore;
				errors.add(error);
			}
		}
		else
		{
			String digitsAfterNum = number.substring(index + 1, number.length());
			if (digitsAfterNum.length() > digitsAfter)
			{
				error =  errorDigitsAfter;
				errors.add(error);
			}
			
		    String digitsBeforeNum = number.substring(0, index);
		    if (digitsBeforeNum.length() > digitsBefore)
		    {
		    	error =  errorDigitsBefore;
		    	errors.add(error);
		    }
			
		}
		return errors;
	}
	

	private Double getDoubleValueForInterest(String doubleValueString)
	{
		
		if (currentDecimalFormatForInterest == null)
			loadDecimalFormats();
		Double dNum = null;
		try
		{
			ParsePosition pp = new ParsePosition(0);
			Number num = currentDecimalFormatForInterest.parse(doubleValueString, pp);
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
	// this method will become private after all the validation is done in struct
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
