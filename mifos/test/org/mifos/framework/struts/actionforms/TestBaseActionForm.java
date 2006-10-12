package org.mifos.framework.struts.actionforms;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;


public class TestBaseActionForm extends MifosTestCase {

	public void testGetDateFromString() throws Exception {
		UserContext userContext = TestObjectFactory.getContext();
		BaseActionForm baseActionForm = new BaseActionForm();
		SimpleDateFormat shortFormat = (SimpleDateFormat)DateFormat.
					getDateInstance(DateFormat.SHORT, userContext.getPereferedLocale());
		Date date = Calendar.getInstance().getTime();
		assertNotNull(baseActionForm.getDateFromString(shortFormat.format(date), userContext.getPereferedLocale()));

	}
	public void testGetStringValue() throws Exception {
		BaseActionForm baseActionForm = new BaseActionForm();
		String one = "1";
		assertEquals(one, baseActionForm.getStringValue(true));
	}
}
