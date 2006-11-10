package org.mifos.framework.util.helpers;

import java.util.Locale;

import junit.framework.TestCase;

import org.mifos.framework.exceptions.ValueObjectConversionException;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class ConvertionUtilTest extends TestCase {

	public void testPopulateBusinessObjectFail() throws Exception {
		try {
			BaseActionForm baseActionForm = new BaseActionForm();
			ConvertionUtil.populateBusinessObject(baseActionForm, null,
					new Locale("EN"));
			fail();
		} catch (ValueObjectConversionException e) {
			assertEquals(
				"exception.framework.SystemException.ValueObjectConversionException",
				e.getKey());
		}

		try {
			ConvertionUtil.populateBusinessObject(null, null, new Locale("EN"));
			fail();
		} catch (ValueObjectConversionException e) {
			assertEquals(
				"exception.framework.SystemException.ValueObjectConversionException",
				e.getKey());
		}
	}
}
