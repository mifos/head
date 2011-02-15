/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.ui.ftl;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.junit.Test;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateModelException;

public class DateFormatterTest {

    @Test
    public void formatJavaDateInEnglish() throws TemplateModelException {
        List<Object> args = getJavaDateArgs(Locale.ENGLISH);
        DateFormatter formatter = new DateFormatter();
        String result = (String) formatter.exec(args);
        String expected = "January";
        assertEquals(expected, result);
    }

    @Test
    public void formatJavaDateInFrench() throws TemplateModelException {
        List<Object> args = getJavaDateArgs(Locale.FRENCH);
        DateFormatter formatter = new DateFormatter();
        String result = (String) formatter.exec(args);
        String expected = "janvier";
        assertEquals(expected, result);
    }

    @Test
    public void formatJodaDateInEnglish() throws TemplateModelException {
        List<Object> args = getJodaDateArgs(Locale.ENGLISH);
        DateFormatter formatter = new DateFormatter();
        String result = (String) formatter.exec(args);
        String expected = "January";
        assertEquals(expected, result);
    }

    @Test
    public void formatJodaDateInFrench() throws TemplateModelException {
        List<Object> args = getJodaDateArgs(Locale.FRENCH);
        DateFormatter formatter = new DateFormatter();
        String result = (String) formatter.exec(args);
        String expected = "janvier";
        assertEquals(expected, result);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public void wrongDateTypeShouldFail() throws TemplateModelException {
        List<Object> args = getFormatterArgs("not a date!", Locale.ENGLISH);
        DateFormatter formatter = new DateFormatter();
        formatter.exec(args);
    }

    @Test(expected=IllegalArgumentException.class)
    @SuppressWarnings("PMD.EmptyCatchBlock")
    public void wrongNumberOfArgumentsShouldFail() throws TemplateModelException {
        DateFormatter formatter = new DateFormatter();
        List<Object> args = new ArrayList<Object>();
        formatter.exec(args);
    }

    private List<Object> getFormatterArgs(Object date, Locale locale) throws TemplateModelException {
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        List<Object> args = new ArrayList<Object>();
        args.add(wrapper.wrap(date));
        args.add(wrapper.wrap("MMMM"));
        args.add(wrapper.wrap(locale));
        return args;
    }

    private List<Object> getJavaDateArgs(Locale locale) throws TemplateModelException {
        return getFormatterArgs(new GregorianCalendar(2010, Calendar.JANUARY, 1).getTime(), locale);
    }

    private List<Object> getJodaDateArgs(Locale locale) throws TemplateModelException {
        return getFormatterArgs(new DateTime(2010, 1, 1, 0, 0, 0, 0), locale);
    }
}
