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

package org.mifos.reports.ui;

import static org.mifos.reports.util.helpers.ReportsConstants.NA_DATE;
import static org.mifos.reports.util.helpers.ReportsConstants.NOT_APPLICABLE_DISPLAY_NAME;

import java.io.Serializable;
import java.util.Date;

import org.mifos.framework.util.helpers.DateUtils;

public class DateSelectionItem implements Serializable {

    public static final DateSelectionItem NA_MEETING_DATE = new DateSelectionItem(NA_DATE, NOT_APPLICABLE_DISPLAY_NAME);

    private Date date;
    private String displayDate;

    // For Hibernate
    public DateSelectionItem() {
        super();
    }

    public DateSelectionItem(Date date) {
        this.date = date;
    }

    public DateSelectionItem(Date date, String display) {
        this.date = date;
        this.displayDate = display;
    }

    public Date getDate() {
        return date;
    }

    public String getDisplayDate() {
        return displayDate != null ? displayDate : DateUtils.getLocalizedDateFormat().format(date);
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((date == null) ? 0 : date.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final DateSelectionItem other = (DateSelectionItem) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        return true;
    }
}
