package org.mifos.framework.struts.tags;

import java.util.Date;
import java.util.Locale;

import junit.framework.TestCase;

import org.joda.time.DateMidnight;
import org.mifos.framework.exceptions.InvalidDateException;

public class DateHelperTest extends TestCase {
	
	public void testDateToDatabaseFormat() throws Exception {
		java.util.Date date = new Date(1000333444000L);
		String databaseFormat = DateHelper.toDatabaseFormat(date);
		// System's time zone is GMT minus something
		//assertEquals("2001-09-12", databaseFormat);
		// System's time zone is GMT plus something
		//assertEquals("2001-09-13", databaseFormat);
		assertTrue("expected 2001-09-12 or 13 but got " + databaseFormat,
			databaseFormat.startsWith("2001-09-1")
		);
	}
	
	// test that getLocaleDate is parsing various localized date strings into 
	// java.sql.Date objects in our localization setting
	public void testGetLocaleDate() throws Exception {
		long expectedDate = new DateMidnight(2005, 03, 4).getMillis();
		assertEquals(expectedDate, DateHelper.getLocaleDate(Locale.GERMANY, "04.03.2005").getTime());
		assertEquals(expectedDate, DateHelper.getLocaleDate(Locale.US, "03/04/2005").getTime());
		assertEquals(expectedDate, DateHelper.getLocaleDate(Locale.UK, "04/03/2005").getTime());
		checkException(Locale.US, "04.03.2005");
	}
	
	public void testGetDateFromBrowser() throws Exception {
		
		// test that day and month order is correct
		long expectedDate = new DateMidnight(2005, 03, 04).getMillis();
		java.sql.Date result = DateHelper.getDateAsSentFromBrowser("04/03/2005");
		assertEquals(expectedDate, result.getTime());
		
		expectedDate = new DateMidnight(2005, 04, 03).getMillis();
		result = DateHelper.getDateAsSentFromBrowser("3/4/2005");
		assertEquals(expectedDate, result.getTime());
		
		// test non-lenient day/month parsing (must be in the normal range, i.e. 1-12 for months)
		String badDate = "3/15/2006";
		try {
			result = DateHelper.getDateAsSentFromBrowser(badDate);
			fail();
		}	
		catch (InvalidDateException e) {
			assertEquals(e.getDateString(), badDate);
		}
		
		// test acceptance of two-digit years
		expectedDate = new DateMidnight(2005, 5, 5).getMillis();
		result = DateHelper.getDateAsSentFromBrowser("5/5/05");
		assertEquals(expectedDate, result.getTime());
		
		
		
	}
	
	public void testIsValidDate() throws Exception {
		assertFalse(DateHelper.isValidDate("notadate"));
		assertFalse(DateHelper.isValidDate("2/13/2000"));
		assertFalse(DateHelper.isValidDate("32/1/2000"));
		assertFalse(DateHelper.isValidDate("1\1\2000"));
		assertTrue(DateHelper.isValidDate("1/2/2000"));
		assertFalse(DateHelper.isValidDate("2/20/2000"));
	}

	private void checkException(Locale locale, String input) {
		try {
			DateHelper.getLocaleDate(locale, input);
			fail("did not get exception for " + input + " in " + locale);
		}
		catch (InvalidDateException e) {
			assertEquals(input, e.getDateString());
		}
	}

}