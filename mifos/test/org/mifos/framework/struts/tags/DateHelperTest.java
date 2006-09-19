package org.mifos.framework.struts.tags;

import java.util.Date;

import junit.framework.TestCase;

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

}
