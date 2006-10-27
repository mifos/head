package org.mifos.framework.struts.tag;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.struts.tags.DateTag;

public class DateTagTest extends MifosTestCase {

	public void testPrepareOutputString() {
		DateTag dateTag = new DateTag();
		dateTag.setKeyhm("keyHm");
		assertEquals("keyHm", dateTag.getKeyhm());

		dateTag.setIsDisabled("Disabled");
		assertEquals("Disabled", dateTag.getIsDisabled());

		dateTag.setIndexed(true);

		assertEquals("<input type=\"text\" id=\"asdYY\"  name=\"asdYY\" maxlength=\"4\" size=\"4\" value=\"asd\" onBlur=\"makeDateString('asdYY','asd','asd')\" style=\"width:40px\" onKeyPress=\"return numbersonly(this, event)\"/>&nbsp;YYYY&nbsp;<input type=\"hidden\" id=\"asd\" name=\"asd\" value=\"asd\"/><input type=\"hidden\" id=\"asdFormat\" name=\"asdFormat\" value=\"asd\"/><input type=\"hidden\" id=\"datePattern\" name=\"datePattern\" value=\"asd\"/>",
				dateTag.prepareOutputString("asd", "asd", "asd", "asd","asd","asd","asd"));
	}

}
