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

package org.mifos.test.acceptance.framework.customer;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.client.ClientViewDetailsPage;

import com.thoughtworks.selenium.Selenium;

public class CustomerChangeStatusPage extends MifosPage {

	public CustomerChangeStatusPage() {
		super();
	}

	/**
	 * @param selenium
	 */
	public CustomerChangeStatusPage(Selenium selenium) {
		super(selenium);
	}

    public void verifyPage() {
        this.verifyPage("customerchangeStatus");
    }

    @SuppressWarnings("PMD.TooManyFields") // lots of fields ok for form input case
    public static class SubmitFormParameters {
        // statuses
        public final static int APPROVED = 3;
        public final static int PARTIAL_APPLICATION = 1;
        public final static int PENDING_APPROVAL = 2;

        private int status;
        private String notes;

        public int getStatus() {
            return this.status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getNotes() {
            return this.notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }
    }

    public CustomerChangeStatusPreviewDataPage submitAndGotoCustomerChangeStatusPreviewDataPage(SubmitFormParameters parameters) {
        selenium.check("name=newStatusId value="+ parameters.getStatus());
        typeTextIfNotEmpty("customerchangeStatus.input.notes", parameters.getNotes());
        selenium.click("customerchangeStatus.button.preview");
        waitForPageToLoad();
        return new CustomerChangeStatusPreviewDataPage(selenium);
    }

    public ClientViewDetailsPage cancelAndGotoClientViewDetailsPage(SubmitFormParameters parameters) {
        //checkIfNotEmpty("customerchangeStatus.input.status", parameters.getStatus());
        //typeTextIfNotEmpty("customerchangeStatus.input.notes", parameters.getNotes());
        selenium.click("customerchangeStatus.button.cancel");
        waitForPageToLoad();
        return new ClientViewDetailsPage(selenium);
    }
}
