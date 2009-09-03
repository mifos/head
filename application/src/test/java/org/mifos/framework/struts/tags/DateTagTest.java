/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.framework.struts.tags;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;

import java.util.Locale;

import javax.servlet.jsp.JspException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.dom4j.DocumentException;
import org.mifos.config.Localization;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.LocalizationConverter;

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
       Assert.assertEquals("<input type=\"text\" id=\"asdDD\" name=\"asdDD\" " + "maxlength=\"2\" size=\"2\" value=\"1\" "
                + "style=\"width:1.5em\"" + " />\u00a0DD\u00a0<input type=\"text\" "
                + "id=\"asdMM\" name=\"asdMM\" maxlength=\"2\" size=\"2\" value=\"1\" " + "style=\"width:1.5em\" />"
                + "\u00a0MM\u00a0" + "<input type=\"text\" id=\"asdYY\" name=\"asdYY\" "
                + "maxlength=\"4\" size=\"4\" value=\"2000\" " + "style=\"width:3em\"" + " />\u00a0YYYY\u00a0", dateTag
                .makeUserFields("asd", "1", "1", "2000", "", "d/m/y", separator).toString());
    }

    public void testGetFormat() throws Exception {
        Locale savedLocale = Localization.getInstance().getMainLocale();
        LocalizationConverter.getInstance().setCurrentLocale(Locale.US);
        DateTag dateTag = new DateTag();
       Assert.assertEquals("M/d/yy", dateTag.getUserFormat(TestUtils.makeUser().getPreferredLocale()));
        LocalizationConverter.getInstance().setCurrentLocale(savedLocale);
    }

    public void testFromPersonnel() throws Exception {
        DateTag dateTag = new DateTag();
        String format = dateTag.getUserFormat(DEFAULT_LOCALE);
       Assert.assertEquals("dd/MM/yy", format);
    }

    public void testPrepareOutputString() throws DocumentException {
        DateTag dateTag = new DateTag();
        dateTag.setKeyhm("keyHm");
       Assert.assertEquals("keyHm", dateTag.getKeyhm());

        dateTag.setIsDisabled("Disabled");
       Assert.assertEquals("Disabled", dateTag.getIsDisabled());
        assertWellFormedFragment(dateTag.prepareOutputString("asd", "asd", "asd", "asd", "asd", "asd", "asd")
                .toString());
       Assert.assertEquals("<input type=\"text\" id=\"asdYY\" name=\"asdYY\" maxlength=\"4\" "
                + "size=\"4\" value=\"asd\" onBlur=\"makeDateString('asdYY','asd','asd')\" " + "style=\"width:3em\""
                + " />\u00a0YYYY\u00a0" + "<input type=\"hidden\" id=\"asd\" name=\"asd\" value=\"asd\" />"
                + "<input type=\"hidden\" id=\"asdFormat\" name=\"asdFormat\" value=\"asd\" />"
                + "<input type=\"hidden\" id=\"datePattern\" name=\"datePattern\" value=\"asd\" />", dateTag
                .prepareOutputString("asd", "asd", "asd", "asd", "asd", "asd", "asd").toString());
    }

    public void testRender() throws JspException, DocumentException {
        DateTag dateTag = new DateTag();
        dateTag.setRenderstyle("simple");
        dateTag.setProperty("testdate");

        String output = "<!-- simple style -->" + "<input type=\"text\" id=\"testdateDD\" name=\"testdateDD\" "
                + "maxlength=\"2\" size=\"2\" value=\"16\" " + "style=\"width:1.5em\"" + " />\u00a0DD\u00a0" + ""
                + "<input type=\"text\" id=\"testdateMM\" name=\"testdateMM\" "
                + "maxlength=\"2\" size=\"2\" value=\"08\" " + "style=\"width:1.5em\"" + " />\u00a0MM\u00a0" + ""
                + "<input type=\"text\" id=\"testdateYY\" name=\"testdateYY\" "
                + "maxlength=\"4\" size=\"4\" value=\"2007\" " + "style=\"width:3em\"" + " />\u00a0YYYY\u00a0";
       Assert.assertEquals(output, dateTag.render(DEFAULT_LOCALE, DEFAULT_LOCALE_DATE));
        assertWellFormedFragment(dateTag.render(DEFAULT_LOCALE, DEFAULT_LOCALE_DATE));

        try {
            dateTag.render(DEFAULT_LOCALE, NON_LOCAL_FORMAT_ORDER_DATE);
            Assert.fail("Should have gotten an unexpected error");
        } catch (IllegalStateException e) {
            // We're expecting this
        }

        dateTag.setFormatOrder(NON_LOCALE_FORMAT_ORDER);
        try {
           Assert.assertEquals(output, dateTag.render(DEFAULT_LOCALE, DEFAULT_LOCALE_DATE));
            assertWellFormedFragment(dateTag.render(DEFAULT_LOCALE, DEFAULT_LOCALE_DATE));
            Assert.fail("Should have gotten an unexpected error");
        } catch (IllegalStateException e) {
            // We're expecting this
        }

       Assert.assertEquals(output, dateTag.render(DEFAULT_LOCALE, NON_LOCAL_FORMAT_ORDER_DATE));
        assertWellFormedFragment(dateTag.render(DEFAULT_LOCALE, NON_LOCAL_FORMAT_ORDER_DATE));
    }
}
