package org.mifos.application.master.business;

import java.math.RoundingMode;

import junit.framework.TestCase;

public class MifosCurrencyTest extends TestCase {
	
	public void testRoundingModeUp() throws Exception {
		MifosCurrency currency = new MifosCurrency();
		currency.setRoundingMode((short) 1);
		assertEquals(RoundingMode.CEILING, currency.getRoundingModeEnum());
	}

	public void testRoundingModeDown() throws Exception {
		MifosCurrency currency = new MifosCurrency();
		currency.setRoundingMode((short) 2);
		assertEquals(RoundingMode.FLOOR, currency.getRoundingModeEnum());
	}

	public void testRoundingModeInvalid() throws Exception {
		MifosCurrency currency = new MifosCurrency();
		currency.setRoundingMode((short) 0);
		try {
			currency.getRoundingModeEnum();
			fail();
		}
		catch (IllegalStateException e) {
			assertEquals("bad rounding mode 0", e.getMessage());
		}
	}

}
