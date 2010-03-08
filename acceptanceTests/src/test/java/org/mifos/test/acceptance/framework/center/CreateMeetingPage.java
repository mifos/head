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

package org.mifos.test.acceptance.framework.center;

import org.junit.Assert;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;

import com.thoughtworks.selenium.Selenium;

public class CreateMeetingPage extends MifosPage {

	public CreateMeetingPage() {
		super();
	}


	public CreateMeetingPage(Selenium selenium) {
		super(selenium);
	}

    public CreateCenterEnterDataPage submitAndGotoCreateCenterEnterDataPage(MeetingParameters parameters) {
        selectValueIfNotZero("weekDay", parameters.getWeekDay());
        typeTextIfNotEmpty("createmeeting.input.weekFrequency", parameters.getWeekFrequency());
        typeTextIfNotEmpty("createmeeting.input.meetingPlace", parameters.getMeetingPlace());
        selenium.click("createmeeting.button.save");
        waitForPageToLoad();
        return new CreateCenterEnterDataPage(selenium);
    }

    public CreateClientEnterMfiDataPage submitAndGotoCreateClientEnterMfiDataPage(MeetingParameters parameters) {
        selectValueIfNotZero("weekDay", parameters.getWeekDay());
        typeTextIfNotEmpty("createmeeting.input.weekFrequency", parameters.getWeekFrequency());
        typeTextIfNotEmpty("createmeeting.input.meetingPlace", parameters.getMeetingPlace());
        selenium.click("createmeeting.button.save");
        waitForPageToLoad();
        return new CreateClientEnterMfiDataPage(selenium);
    }

    public void verifyWorkingDays(String workingDays)
    {
        String workingDaysInWeekFromPage[] =  selenium.getSelectOptions("createmeeting.input.dayOfWeek");
        String workingDaysInWeek[] = new String[workingDaysInWeekFromPage.length-1];
        //remove the first item in drop down --Select--
        System.arraycopy(workingDaysInWeekFromPage, 1, workingDaysInWeek, 0, workingDaysInWeekFromPage.length-1);
        String workingDaysArray[] = workingDays.split(",");
        Assert.assertArrayEquals(workingDaysInWeek, workingDaysArray);
    }
}
