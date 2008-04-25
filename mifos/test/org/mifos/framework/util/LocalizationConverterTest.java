package org.mifos.framework.util;

import static org.junit.Assert.assertEquals;

import java.text.DateFormat;
import java.util.Calendar;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mifos.framework.util.helpers.DateUtils;


public class LocalizationConverterTest {
	@Test public void testDateFormattingWithFourDigitsInYear() throws Exception {
		LocalizationConverter instance = LocalizationConverter.getInstance();
		DateFormat dateFormat = instance.getDateFormatWithFullYear();
		assertEquals("13/12/2008", dateFormat.format(DateUtils.getDate(2008, Calendar.DECEMBER, 13))); 
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(LocalizationConverterTest.class);
	}
}
