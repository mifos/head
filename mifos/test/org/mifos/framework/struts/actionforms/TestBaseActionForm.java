package org.mifos.framework.struts.actionforms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.mifos.config.Localization;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.TestObjectFactory;


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
	
	
}
