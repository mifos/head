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
 
package org.mifos.framework.util;

import java.util.Date;

import org.joda.time.DateMidnight;
import org.joda.time.DateTime;
import org.joda.time.DateTimeUtils;

public class DateTimeService {

    public Date getCurrentJavaDateTime() {
        return new DateTime().toDate();
    }

    public void setCurrentDateTime(DateTime someDateTime) {
        resetToCurrentSystemDateTime();
        DateTimeUtils.setCurrentMillisOffset(someDateTime.getMillis() - new DateTime().getMillis());        
    }
    
    public void resetToCurrentSystemDateTime() {
        DateTimeUtils.setCurrentMillisSystem();
    }

    public DateTime getCurrentDateTime() {
        return new DateTime();
    }

    public void setCurrentDateTimeFixed(DateTime dateTime) {
        DateTimeUtils.setCurrentMillisFixed(dateTime.getMillis());        
    }

    public java.sql.Date getCurrentJavaSqlDate() {
        return new java.sql.Date(new DateTime().getMillis());
    }

    public DateMidnight getCurrentDateMidnight() {
        return new DateMidnight();
    }

}
