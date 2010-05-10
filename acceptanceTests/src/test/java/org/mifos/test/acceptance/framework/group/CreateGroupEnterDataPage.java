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

package org.mifos.test.acceptance.framework.group;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.center.CreateCenterPreviewDataPage;
import org.mifos.test.acceptance.framework.center.CreateMeetingPage;
import org.mifos.test.acceptance.framework.center.MeetingParameters;

import com.thoughtworks.selenium.Selenium;

public class CreateGroupEnterDataPage extends MifosPage {

	public CreateGroupEnterDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CreateGroupEnterDataPage(Selenium selenium) {
		super(selenium);
	}

    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class SubmitFormParameters {
        String centerName;
        String loanOfficer;
        MeetingParameters meeting;

        public String getCenterName() {
            return this.centerName;
        }


        public String getLoanOfficer() {
            return this.loanOfficer;
        }


        public void setCenterName(String centerName) {
            this.centerName = centerName;
        }

        public void setLoanOfficer(String loanOfficer) {
            this.loanOfficer = loanOfficer;
        }

        public MeetingParameters getMeeting() {
            return this.meeting;
        }

        public void setMeeting(MeetingParameters meeting) {
            this.meeting = meeting;
        }
    }

    public CreateCenterPreviewDataPage submitAndGotoCreateGroupPreviewDataPage(SubmitFormParameters parameters) {
        typeTextIfNotEmpty("createnewcenter.input.name", parameters.getCenterName());
        selectIfNotEmpty("loanOfficerId", parameters.getLoanOfficer());
        selenium.click("createnewcenter.link.meetingSchedule");
        waitForPageToLoad();
        CreateMeetingPage meetingPage = new CreateMeetingPage(selenium);
        meetingPage.submitAndGotoCreateCenterEnterDataPage(parameters.getMeeting());
        selenium.click("createnewcenter.button.preview");
        waitForPageToLoad();
        return new CreateCenterPreviewDataPage(selenium);
    }
}
