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

package org.mifos.ui.core.controller;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Locale;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value="EQ_UNUSUAL", justification="using commons equals builder")
public class MonthClosingFormBean {

    @DateTimeFormat(pattern="dd/MM/yyyy")
    private DateTime date;

    private Locale locale;

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime date) {
        this.date = date;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = org.joda.time.format.DateTimeFormat.forStyle("S-").withLocale(Locale.getDefault());
        String printedDate = "";
        if (date != null) {
            printedDate = formatter.print(date);
        }
        return printedDate;
    }

    @Override
    public boolean equals(Object obj) {
        return EqualsBuilder.reflectionEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
