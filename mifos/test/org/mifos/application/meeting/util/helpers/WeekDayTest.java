package org.mifos.application.meeting.util.helpers;

import static org.mifos.application.meeting.util.helpers.WeekDay.MONDAY;
import static org.mifos.application.meeting.util.helpers.WeekDay.SATURDAY;
import static org.mifos.application.meeting.util.helpers.WeekDay.SUNDAY;
import static org.mifos.application.meeting.util.helpers.WeekDay.TUESDAY;
import static org.mifos.application.meeting.util.helpers.WeekDay.WEDNESDAY;
import junit.framework.TestCase;

public class WeekDayTest extends TestCase {
	
	public void testNext() throws Exception {
		assertEquals(MONDAY, SUNDAY.next());
		assertEquals(WEDNESDAY, TUESDAY.next());
		assertEquals(SUNDAY, SATURDAY.next());
	}

}
