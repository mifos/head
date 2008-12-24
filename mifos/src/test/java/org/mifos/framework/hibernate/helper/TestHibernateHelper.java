package org.mifos.framework.hibernate.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.mifos.framework.MifosTestCase;

public class TestHibernateHelper extends MifosTestCase {

	public void testDTOBuilderCapitalize() {
		DTOBuilder dtoBuilder = new DTOBuilder();
		assertEquals("String", dtoBuilder.capitalize("string"));
		assertEquals("", dtoBuilder.capitalize(""));
	}

	public void testDTOBuilderGetParameterTypes() throws Exception {
		DTOBuilder dtoBuilder = new DTOBuilder();
		Class[] classParams = dtoBuilder.getParameterTypes("long");
		assertTrue(classParams[0].equals(Long.TYPE));
		classParams = dtoBuilder.getParameterTypes("integer");
		assertTrue(classParams[0].equals(Integer.TYPE));
		classParams = dtoBuilder.getParameterTypes("string");
		assertTrue(classParams[0].equals(new String("").getClass()));
		classParams = dtoBuilder.getParameterTypes("date");
		assertTrue(classParams[0].equals(new java.util.GregorianCalendar()
				.getClass().getSuperclass()));
		classParams = dtoBuilder.getParameterTypes("double");
		assertTrue(classParams[0].equals(Double.TYPE));
		classParams = dtoBuilder.getParameterTypes("short");
		assertTrue(classParams[0].equals(Short.TYPE));
		classParams = dtoBuilder.getParameterTypes("CHAR");
		assertTrue(classParams[0].equals(Character.TYPE));
		classParams = dtoBuilder.getParameterTypes("TIMESTAMP");
		assertTrue(classParams[0].equals(new java.sql.Timestamp(System
				.currentTimeMillis()).getClass()));
		classParams = dtoBuilder.getParameterTypes("TIME");
		assertTrue(classParams[0].equals(new java.sql.Time(System
				.currentTimeMillis()).getClass()));
		classParams = dtoBuilder.getParameterTypes("invalidDataType");
		assertNull(classParams);
	}

	public void testDTOBuilderGetValue() {
		DTOBuilder dtoBuilder = new DTOBuilder();
		assertEquals("string", dtoBuilder.getValue("string", "string"));
		Long l = System.currentTimeMillis();
		Date date = new Date(l);
		Calendar c = new GregorianCalendar();
		Calendar cal = (Calendar) dtoBuilder.getValue(new Date(l), "date");
		c.setTime(date);
		assertEquals(c.getTime(), cal.getTime());
	}

}
