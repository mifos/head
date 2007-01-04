package org.mifos.framework.util.helpers;

import org.mifos.framework.MifosTestCase;

public class MifosDoubleConverterTest extends MifosTestCase {

	private MifosDoubleConverter mifosDoubleConverter = null;

	public void testConvert() {
		mifosDoubleConverter = new MifosDoubleConverter();
		Double test = new Double(2.0);
		assertEquals(test, mifosDoubleConverter.convert(String.class, "2.0"));
		assertEquals(test, mifosDoubleConverter.convert(String.class, Integer.valueOf(2)));
	}

}
