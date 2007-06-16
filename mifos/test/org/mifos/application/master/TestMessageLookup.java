package org.mifos.application.master;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.ApplicationInitializer;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.FilePaths;

public class TestMessageLookup {
	private MessageLookup messageLookup;
	
	@Before
	public void setUp() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
		ApplicationInitializer.initializeSpring();
		messageLookup = MessageLookup.getInstance();
	}

	@After
	public void tearDown() throws Exception {
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

}
