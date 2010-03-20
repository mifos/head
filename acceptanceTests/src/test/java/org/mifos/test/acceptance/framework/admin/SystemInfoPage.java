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

package org.mifos.test.acceptance.framework.admin;

import java.util.Locale;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.mifos.test.acceptance.framework.MifosPage;
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class SystemInfoPage extends MifosPage {

    public SystemInfoPage(Selenium selenium) {
        super(selenium);
    }

    public SystemInfoPage verifyPage() {
        verifyPage("SysInfo");
        return this;
    }

    public String getDateTime() {
        return selenium.getText("sysinfo.text.dateTime");
    }

    public void verifyDateTime(DateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormat.shortDateTime().withLocale(new Locale("en","GB"));
        String expectedDateTime =  formatter.print(dateTime.getMillis());
        Assert.assertEquals(getDateTime(), expectedDateTime, "System date time and Mifos date time should be the same.");
    }

}
