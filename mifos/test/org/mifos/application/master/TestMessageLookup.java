package org.mifos.application.master;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import junit.framework.JUnit4TestAdapter;

import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.FilePaths;

public class TestMessageLookup {
	private static MessageLookup messageLookup;
	
	@BeforeClass
	public static void init() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
		DatabaseSetup.initializeHibernate();
		TestUtils.initializeSpring();
		messageLookup = MessageLookup.getInstance();
	}

	@Test
	public void testWeekDayLookup() {
		// default locale
		assertEquals("Monday",messageLookup.lookup(WeekDay.MONDAY, Locale.US));
		assertEquals("Tuesday",messageLookup.lookup(WeekDay.TUESDAY, Locale.US));
		assertEquals("Wednesday",messageLookup.lookup(WeekDay.WEDNESDAY, Locale.US));
		assertEquals("Thursday",messageLookup.lookup(WeekDay.THURSDAY, Locale.US));
		assertEquals("Friday",messageLookup.lookup(WeekDay.FRIDAY, Locale.US));
		assertEquals("Saturday",messageLookup.lookup(WeekDay.SATURDAY, Locale.US));
		assertEquals("Sunday",messageLookup.lookup(WeekDay.SUNDAY, Locale.US));
		// Spanish locale
		assertEquals("Lunes",messageLookup.lookup(WeekDay.MONDAY, new Locale("es")));
		// French locale
		assertEquals("Lundi",messageLookup.lookup(WeekDay.MONDAY, new Locale("fr")));
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(TestMessageLookup.class);
	}
	

}
