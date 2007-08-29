package org.mifos.application.master;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.meeting.util.helpers.WeekDay;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.DatabaseSetup;
import org.mifos.framework.util.helpers.FilePaths;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestMessageLookup {
	private static MessageLookup messageLookup;
	
	private static ApplicationContext context;
	
	@BeforeClass
	public static void init() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
		DatabaseSetup.initializeHibernate();
		
		context = new ClassPathXmlApplicationContext(
			"org/mifos/config/applicationContext.xml");		
		messageLookup = MessageLookup.getInstance();
	}
	
	@Before
	public void setUp() throws Exception {
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
