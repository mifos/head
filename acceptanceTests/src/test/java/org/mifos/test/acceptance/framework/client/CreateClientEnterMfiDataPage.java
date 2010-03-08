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

package org.mifos.test.acceptance.framework.client;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.center.CreateMeetingPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;

import com.thoughtworks.selenium.Selenium;

public class CreateClientEnterMfiDataPage extends MifosPage {

	public CreateClientEnterMfiDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CreateClientEnterMfiDataPage(Selenium selenium) {
		super(selenium);
	}

    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class SubmitFormParameters {

        String loanOfficerId;
        MeetingParameters meeting;

        public String getLoanOfficerId() {
            return this.loanOfficerId;
        }

        public void setLoanOfficerId(String loanOfficerId) {
            this.loanOfficerId = loanOfficerId;
        }

        public MeetingParameters getMeeting() {
            return this.meeting;
        }

        public void setMeeting(MeetingParameters meeting) {
            this.meeting = meeting;
        }
    }

    public CreateClientPreviewDataPage submitAndGotoCreateClientPreviewDataPage(SubmitFormParameters parameters) {
        selectIfNotEmpty("loanOfficerId", parameters.getLoanOfficerId());
        navigateToCreateMeetingPage().submitAndGotoCreateClientEnterMfiDataPage(parameters.getMeeting());
        selenium.click("create_ClientMfiInfo.button.preview");
        waitForPageToLoad();
        return new CreateClientPreviewDataPage(selenium);
    }

    public CreateMeetingPage navigateToCreateMeetingPage()
    {
        selenium.click("create_ClientMfiInfo.link.meetingSchedule");
        waitForPageToLoad();
        return new CreateMeetingPage(selenium);
    }
}
