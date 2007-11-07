package org.mifos.framework.struts.tags;

import junit.framework.TestCase;
import org.dom4j.DocumentException;
import org.mifos.framework.TestUtils;
import static org.mifos.framework.TestUtils.assertWellFormedFragment;

import javax.servlet.jsp.JspException;
import java.util.Locale;

public class DateTagTest extends TestCase {
	
    private static final Locale DEFAULT_LOCALE = Locale.UK;
    private static final String DEFAULT_LOCALE_DATE = "16/08/2007";
    private static final String NON_LOCALE_FORMAT_ORDER = "Y-M-D";
    private static final String NON_LOCAL_FORMAT_ORDER_DATE = "2007-08-16";

    public void testSimpleStyle() throws DocumentException {
        DateTag dateTag = new DateTag();
        dateTag.setRenderstyle("simple");
        String separator = "/";
		assertWellFormedFragment(dateTag.makeUserFields("asd", "1", "1", "2000", "", "d/m/y", separator).toString());
		assertEquals(
			"<input type=\"text\" id=\"asdDD\" name=\"asdDD\" "
				+ "maxlength=\"2\" size=\"2\" value=\"1\" " 
				+ "style=\"width:1.5em\""
				+ " />\u00a0DD\u00a0<input type=\"text\" "
				+ "id=\"asdMM\" name=\"asdMM\" maxlength=\"2\" size=\"2\" value=\"1\" "
				+ "style=\"width:1.5em\" />"
				+ "\u00a0MM\u00a0"
				+ "<input type=\"text\" id=\"asdYY\" name=\"asdYY\" "
				+ "maxlength=\"4\" size=\"4\" value=\"2000\" " 
				+ "style=\"width:3em\""
				+ " />\u00a0YYYY\u00a0",dateTag.makeUserFields("asd", "1", "1", "2000", "", "d/m/y", separator).toString());
	}
	
	public void testGetFormat() throws Exception {
        DateTag dateTag = new DateTag();
        assertEquals("M/d/yy", dateTag.getUserFormat(TestUtils.makeUser().getPreferredLocale()));
	}
	
	public void testFromPersonnel() throws Exception {
        DateTag dateTag = new DateTag();
        String format = dateTag.getUserFormat(DEFAULT_LOCALE);
		assertEquals("dd/MM/yy", format);
	}

	public void testPrepareOutputString() throws DocumentException {
        DateTag dateTag = new DateTag();
        dateTag.setKeyhm("keyHm");
		assertEquals("keyHm", dateTag.getKeyhm());

		dateTag.setIsDisabled("Disabled");
		assertEquals("Disabled", dateTag.getIsDisabled());
		assertWellFormedFragment(dateTag.prepareOutputString("asd", "asd", "asd", "asd","asd","asd","asd").toString());
		assertEquals(
			"<input type=\"text\" id=\"asdYY\" name=\"asdYY\" maxlength=\"4\" " +
			"size=\"4\" value=\"asd\" onBlur=\"makeDateString('asdYY','asd','asd')\" " +
			"style=\"width:3em\"" +
			" />\u00a0YYYY\u00a0" +
			"<input type=\"hidden\" id=\"asd\" name=\"asd\" value=\"asd\" />" +
			"<input type=\"hidden\" id=\"asdFormat\" name=\"asdFormat\" value=\"asd\" />" +
			"<input type=\"hidden\" id=\"datePattern\" name=\"datePattern\" value=\"asd\" />",
			dateTag.prepareOutputString("asd", "asd", "asd", "asd","asd","asd","asd").toString());
	}
	
	public void testRender() throws JspException, DocumentException {
        DateTag dateTag = new DateTag();
        dateTag.setRenderstyle("simple");
		dateTag.setProperty("testdate");

        String output = "<!-- simple style -->" +
			"<input type=\"text\" id=\"testdateDD\" name=\"testdateDD\" " +
			"maxlength=\"2\" size=\"2\" value=\"16\" " +
			"style=\"width:1.5em\"" +
			" />\u00a0DD\u00a0" +
			"" +
			"<input type=\"text\" id=\"testdateMM\" name=\"testdateMM\" " +
			"maxlength=\"2\" size=\"2\" value=\"08\" " +
			"style=\"width:1.5em\"" +
			" />\u00a0MM\u00a0" +
			"" +
			"<input type=\"text\" id=\"testdateYY\" name=\"testdateYY\" " +
			"maxlength=\"4\" size=\"4\" value=\"2007\" " +
			"style=\"width:3em\"" +
			" />\u00a0YYYY\u00a0";
        assertEquals(output, dateTag.render(DEFAULT_LOCALE, DEFAULT_LOCALE_DATE));
		assertWellFormedFragment(dateTag.render(DEFAULT_LOCALE, DEFAULT_LOCALE_DATE));

        try {
            dateTag.render(DEFAULT_LOCALE, NON_LOCAL_FORMAT_ORDER_DATE);
            fail("Should have gotten an unexpected error");
        }
        catch (IllegalStateException e) {
            // We're expecting this
        }

        dateTag.setFormatOrder(NON_LOCALE_FORMAT_ORDER);
        try {
            assertEquals(output, dateTag.render(DEFAULT_LOCALE, DEFAULT_LOCALE_DATE));
            assertWellFormedFragment(dateTag.render(DEFAULT_LOCALE, DEFAULT_LOCALE_DATE));
            fail("Should have gotten an unexpected error");
        }
        catch (IllegalStateException e) {
            // We're expecting this
        }

        assertEquals(output, dateTag.render(DEFAULT_LOCALE, NON_LOCAL_FORMAT_ORDER_DATE));
        assertWellFormedFragment(dateTag.render(DEFAULT_LOCALE, NON_LOCAL_FORMAT_ORDER_DATE));        
    }
}
