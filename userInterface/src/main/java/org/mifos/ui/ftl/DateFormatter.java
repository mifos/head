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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.utility.DeepUnwrap;

/**
 * A Freemarker template method for formatting and localizing dates.
 */
public class DateFormatter implements TemplateMethodModelEx {

    /**
     * @param args
     *            a list of arguments passed in from FTL in this order:
     *            <ul>
     *            <li>date object to be formatted. It can be an instance of either java.util.Date,
     *            org.joda.time.DateTime or org.joda.time.LocalDate</li>
     *            <li>pattern: date format pattern see {@link java.text.SimpleDateFormat}</li>
     *            <li>locale: an instance of java.util.Locale</li>
     *            </ul>
     */
    @Override
    public Object exec(List args) throws TemplateModelException {
        if (args.size() != 3) {
            throw new IllegalArgumentException("Wrong arguments");
        }
        Object date = DeepUnwrap.unwrap((TemplateModel) args.get(0));
        String pattern = (String) DeepUnwrap.unwrap((TemplateModel) args.get(1));
        Locale locale = (Locale) DeepUnwrap.unwrap((TemplateModel) args.get(2));

        if (date instanceof LocalDate) {
            date = ((LocalDate)date).toDateTimeAtStartOfDay();
        }
        
        String formatted = "";
        if (date instanceof DateTime) {
            formatted = DateTimeFormat.forPattern(pattern).withLocale(locale).print((DateTime) date);
        } else if (date instanceof Date) {
            formatted = new SimpleDateFormat(pattern, locale).format((Date) date);
        } else if (date != null) {
            throw new IllegalArgumentException("Unsupported date type: " + date.getClass());
        }
        return formatted;
    }
}
