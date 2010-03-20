/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.application.holiday.business;

import java.io.Serializable;
import java.util.Date;

public class HolidayPK implements Serializable {

    private Date holidayFromDate;
    private Short officeId;

    public HolidayPK() {
    }

    public HolidayPK(Short officeId, Date holidayFromDate) {
        this.officeId = officeId;
        this.holidayFromDate = holidayFromDate;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final HolidayPK other = (HolidayPK) obj;
        if (holidayFromDate == null) {
            if (other.holidayFromDate != null) {
                return false;
            }
        } else if (!holidayFromDate.equals(other.holidayFromDate)) {
            return false;
        }
        if (officeId == null) {
            if (other.officeId != null) {
                return false;
            }
        } else if (!officeId.equals(other.officeId)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = super.hashCode();
        result = PRIME * result + ((holidayFromDate == null) ? 0 : holidayFromDate.hashCode());
        result = PRIME * result + ((officeId == null) ? 0 : officeId.hashCode());
        return result;
    }

    public Date getHolidayFromDate() {
        return this.holidayFromDate;
    }

    public void setHolidayFromDate(Date holidayFromDate) {
        this.holidayFromDate = holidayFromDate;
    }

    /**
     * @return the officeId
     */
    public Short getOfficeId() {
        return officeId;
    }

    /**
     * @param officeId
     *            the officeId to set
     */
    public void setOfficeId(Short officeId) {
        this.officeId = officeId;
    }
}
