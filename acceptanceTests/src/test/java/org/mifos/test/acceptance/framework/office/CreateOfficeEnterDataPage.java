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
 
package org.mifos.test.acceptance.framework.office;

import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class CreateOfficeEnterDataPage extends MifosPage {

	public CreateOfficeEnterDataPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CreateOfficeEnterDataPage(Selenium selenium) {
		super(selenium);
	}
    
    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class SubmitFormParameters {
        public static final String REGIONAL_OFFICE = "Regional Office";
        public static final String DIVISIONAL_OFFICE = "Divisional Office";
        public static final String AREA_OFFICE = "Area Office";
        public static final String BRANCH_OFFICE = "Branch Office";
        
        String officeName;
        String shortName;
        String officeType;
        String parentOffice;
        String address1;
        String address2;
        String address3;
        String state;
        String country;
        String postalCode;
        String phoneNumber;        
        
        public String getOfficeName() {
            return this.officeName;
        }
        
        public void setOfficeName(String officeName) {
            this.officeName = officeName;
        }
        
        public String getShortName() {
            return this.shortName;
        }
        
        public void setShortName(String shortName) {
            this.shortName = shortName;
        }
        
        public String getOfficeType() {
            return this.officeType;
        }
        
        public void setOfficeType(String officeType) {
            this.officeType = officeType;
        }
        
        public String getParentOffice() {
            return this.parentOffice;
        }
        
        public void setParentOffice(String parentOffice) {
            this.parentOffice = parentOffice;
        }
        
        public String getAddress1() {
            return this.address1;
        }
        
        public void setAddress1(String address1) {
            this.address1 = address1;
        }
        
        public String getAddress2() {
            return this.address2;
        }
        
        public void setAddress2(String address2) {
            this.address2 = address2;
        }
        
        public String getAddress3() {
            return this.address3;
        }
        
        public void setAddress3(String address3) {
            this.address3 = address3;
        }
        
        public String getState() {
            return this.state;
        }
        
        public void setState(String state) {
            this.state = state;
        }
        
        public String getCountry() {
            return this.country;
        }
        
        public void setCountry(String country) {
            this.country = country;
        }
        
        public String getPostalCode() {
            return this.postalCode;
        }
        
        public void setPostalCode(String postalCode) {
            this.postalCode = postalCode;
        }
        
        public String getPhoneNumber() {
            return this.phoneNumber;
        }
        
        public void setPhoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
        }
        
        /**
         * Maps the office type to a value that's used to choose the right element in the drop-down box.
         */
        @SuppressWarnings("PMD.OnlyOneReturn")
        public int getOfficeTypeValue() {
            if (REGIONAL_OFFICE.equals(officeType)) { return 2; }
            if (DIVISIONAL_OFFICE.equals(officeType)) { return 3; }
            if (AREA_OFFICE.equals(officeType)) { return 4; }
            if (BRANCH_OFFICE.equals(officeType)) { return 5; }

            return -1;
        }
    }
    
    public CreateOfficePreviewDataPage submitAndGotoCreateOfficePreviewDataPage(SubmitFormParameters parameters) {
        typeTextIfNotEmpty("CreateNewOffice.input.officeName", parameters.getOfficeName());
        typeTextIfNotEmpty("CreateNewOffice.input.shortName", parameters.getShortName());
        selenium.select("officeLevel", "value=" + parameters.getOfficeTypeValue());
        waitForPageToLoad(); // loading the possible parent offices
        selectIfNotEmpty("parentOfficeId", parameters.getParentOffice());
        typeTextIfNotEmpty("CreateNewOffice.input.address1", parameters.getAddress1());
        typeTextIfNotEmpty("CreateNewOffice.input.address2", parameters.getAddress2());
        typeTextIfNotEmpty("CreateNewOffice.input.address3", parameters.getAddress3());
        typeTextIfNotEmpty("CreateNewOffice.input.state", parameters.getState());
        typeTextIfNotEmpty("CreateNewOffice.input.country", parameters.getCountry());
        typeTextIfNotEmpty("CreateNewOffice.input.postalCode", parameters.getPostalCode());
        typeTextIfNotEmpty("CreateNewOffice.input.phoneNumber", parameters.getPhoneNumber());
        selenium.click("CreateNewOffice.button.preview");
        waitForPageToLoad();
        return new CreateOfficePreviewDataPage(selenium);
    }    
}
