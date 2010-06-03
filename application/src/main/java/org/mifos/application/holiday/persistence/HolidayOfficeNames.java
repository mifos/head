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
package org.mifos.application.holiday.persistence;

import java.util.Iterator;
import java.util.List;

public class HolidayOfficeNames {
    private HolidayDetails holidayDetails;
    private List<String> officeNames;

    public HolidayOfficeNames(HolidayDetails holidayDetails, List<String> officeNames) {
        this.holidayDetails = holidayDetails;
        this.officeNames = officeNames;
    }

    public HolidayDetails getHolidayDetails() {
        return this.holidayDetails;
    }

    public List<String> getOfficeNames() {
        return this.officeNames;
    }

    public String getOfficeNamesAsString() {
        StringBuffer stringBuffer = new StringBuffer();
        for (Iterator<String> iterator = officeNames.iterator(); iterator.hasNext();) {
            stringBuffer.append(iterator.next());
            if (iterator.hasNext()) {
                stringBuffer.append(", ");
            }
        }
        return stringBuffer.toString();
    }
}
