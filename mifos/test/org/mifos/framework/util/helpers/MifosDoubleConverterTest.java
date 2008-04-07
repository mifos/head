package org.mifos.framework.util.helpers;

import java.util.Locale;

import org.mifos.config.Localization;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.util.LocalizationConverter;

public class MifosDoubleConverterTest extends MifosTestCase {

	private MifosDoubleConverter mifosDoubleConverter = null;

	public void testConvert() {
		mifosDoubleConverter = new MifosDoubleConverter();
		Double test = new Double(2.0);
		Locale locale = Localization.getInstance().getMainLocale();
		LocalizationConverter converter = LocalizationConverter.getInstance();
		if (locale.getCountry().equalsIgnoreCase("GB") && locale.getLanguage().equalsIgnoreCase("EN"))
			assertEquals(test, mifosDoubleConverter.convert(String.class, "2.0"));
		converter.setCurrentLocale(new Locale("IS", "is"));
		assertEquals(test, mifosDoubleConverter.convert(String.class, "2,0"));
		converter.setCurrentLocale(locale);
	}

}
