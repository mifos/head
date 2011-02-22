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

package org.mifos.test.acceptance.framework.admin;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;
import org.testng.log4testng.Logger;

import com.thoughtworks.selenium.Selenium;

public class SystemInfoPage extends MifosPage {

    public SystemInfoPage(Selenium selenium) {
        super(selenium);
        verifyPage("SysInfo");
    }

    public String getDateTime() {
        return selenium.getText("sysinfo.text.dateTime");
    }

    public long getDateTimeInMilis() {
        DateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm", Locale.getDefault());
        Date date = null;
        try {
            date = dateFormat.parse(getDateTime());
        } catch (ParseException e) {
            Logger.getLogger(getClass()).error("Error while parsing date format");
        }
        return date.getTime();
    }

    public void verifyDateTime(DateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormat.shortDateTime().withLocale(Locale.getDefault());
        String expectedDateTime =  formatter.print(dateTime.getMillis());
        String currentDateTime = formatter.print(getDateTimeInMilis());

        Assert.assertEquals(currentDateTime, expectedDateTime, "System date time and Mifos date time should be the same.");
    }

}
