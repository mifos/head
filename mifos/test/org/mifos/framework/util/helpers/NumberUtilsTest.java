package org.mifos.framework.util.helpers;

import java.math.BigDecimal;

import junit.framework.TestCase;

public class NumberUtilsTest extends TestCase {
	public void testPercentageReturnsZeroIfFullValueIsZero() throws Exception {
		BigDecimal percentage = NumberUtils.getPercentage(null, Integer
				.valueOf(0));
		assertEquals(0d, percentage.doubleValue());
	}

	public void testPercentage() throws Exception {
		assertEquals(25d, NumberUtils.getPercentage(Integer.valueOf(1),
				Integer.valueOf(4)).doubleValue());
	}
}
