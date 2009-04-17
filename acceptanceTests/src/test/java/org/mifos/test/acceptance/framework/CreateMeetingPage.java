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
 
package org.mifos.test.acceptance.framework;

import com.thoughtworks.selenium.Selenium;

import org.mifos.test.acceptance.framework.client.CreateClientEnterMfiDataPage;

public class CreateMeetingPage extends MifosPage {

	public CreateMeetingPage() {
		super();
	}

	
	public CreateMeetingPage(Selenium selenium) {
		super(selenium);
	}
    
    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class SubmitFormParameters {

        String weekDay;
        String weekFrequency;
        String meetingPlace;
        
        public String getWeekDay() {
            return this.weekDay;
        }
        
        public void setWeekDay(String weekDay) {
            this.weekDay = weekDay;
        }
        
        public String getWeekFrequency() {
            return this.weekFrequency;
        }
        
        public void setWeekFrequency(String weekFrequency) {
            this.weekFrequency = weekFrequency;
        }
        
        public String getMeetingPlace() {
            return this.meetingPlace;
        }
        
        public void setMeetingPlace(String meetingPlace) {
            this.meetingPlace = meetingPlace;
        }
    }
    
    public CreateCenterEnterDataPage submitAndGotoCreateCenterEnterDataPage(SubmitFormParameters parameters) {
        selectIfNotEmpty("weekDay", parameters.getWeekDay());
        typeTextIfNotEmpty("createmeeting.input.weekFrequency", parameters.getWeekFrequency());
        typeTextIfNotEmpty("createmeeting.input.meetingPlace", parameters.getMeetingPlace());
        selenium.click("createmeeting.button.save");
        waitForPageToLoad();
        return new CreateCenterEnterDataPage(selenium);
    }
    
    public CreateClientEnterMfiDataPage submitAndGotoCreateClientEnterMfiDataPage(SubmitFormParameters parameters) {
        selectIfNotEmpty("weekDay", parameters.getWeekDay());
        typeTextIfNotEmpty("createmeeting.input.weekFrequency", parameters.getWeekFrequency());
        typeTextIfNotEmpty("createmeeting.input.meetingPlace", parameters.getMeetingPlace());
        selenium.click("createmeeting.button.save");
        waitForPageToLoad();
        return new CreateClientEnterMfiDataPage(selenium);
    }        
}
