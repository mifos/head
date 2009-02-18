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

package org.mifos.test.acceptance.framework.collectionsheet;


import org.mifos.test.acceptance.framework.AbstractPage;
import org.mifos.test.acceptance.framework.HomePage;

import com.thoughtworks.selenium.Selenium;


public class CollectionSheetEntrySelectPage extends AbstractPage {

	private static final String RECEIPT_INPUT_ID	= "bulkentry.input.receiptId";
	private static final String CONTINUE_BUTTON_ID = "bulkentry.button.continue";
	private static final String CANCEL_BUTTON_ID = "bulkentry.button.cancel";

	public CollectionSheetEntrySelectPage() {
		super();
	}

	public CollectionSheetEntrySelectPage(Selenium selenium) {
		super(selenium);
	}

	public CollectionSheetEntrySelectPage verifyPage() {
        this.verifyPage("BulkEntry");
		return this;
	}

	public HomePage cancelPage() {
		verifyPage();
		selenium.click(CANCEL_BUTTON_ID);
		waitForPageToLoad();
		return new HomePage(selenium);
	}
	
	public static class SubmitFormParameters {
        public String getBranch() {
            return this.branch;
        }

        public void setBranch(String branch) {
            this.branch = branch;
        }

        public String getLoanOfficer() {
            return this.loanOfficer;
        }

        public void setLoanOfficer(String loanOfficer) {
            this.loanOfficer = loanOfficer;
        }

        public String getCenter() {
            return this.center;
        }

        public void setCenter(String center) {
            this.center = center;
        }

        public String getTransactionDay() {
            return this.transactionDay;
        }

        public void setTransactionDay(String transactionDay) {
            this.transactionDay = transactionDay;
        }

        public String getTransactionMonth() {
            return this.transactionMonth;
        }

        public void setTransactionMonth(String transactionMonth) {
            this.transactionMonth = transactionMonth;
        }

        public String getTransactionYear() {
            return this.transactionYear;
        }

        public void setTransactionYear(String transactionYear) {
            this.transactionYear = transactionYear;
        }

        public String getPaymentMode() {
            return this.paymentMode;
        }

        public void setPaymentMode(String paymentMode) {
            this.paymentMode = paymentMode;
        }

        public String getReceiptId() {
            return this.receiptId;
        }

        public void setReceiptId(String receiptId) {
            this.receiptId = receiptId;
        }

        public String getReceiptDay() {
            return this.receiptDay;
        }

        public void setReceiptDay(String receiptDay) {
            this.receiptDay = receiptDay;
        }

        public String getReceiptMonth() {
            return this.receiptMonth;
        }

        public void setReceiptMonth(String receiptMonth) {
            this.receiptMonth = receiptMonth;
        }

        public String getReceiptYear() {
            return this.receiptYear;
        }

        public void setReceiptYear(String receiptYear) {
            this.receiptYear = receiptYear;
        }

        private String branch;
        private String loanOfficer;
        private String center;
        private String transactionDay;
        private String transactionMonth;
        private String transactionYear;
        private String paymentMode;
        private String receiptId;
        private String receiptDay;
        private String receiptMonth;
        private String receiptYear;

    }

    public CollectionSheetEntryEnterDataPage submitAndGotoCollectionSheetEntryEnterDataPage(
            SubmitFormParameters parameters) {

        selenium.select("officeId", "label=" + parameters.getBranch());
        waitForPageToLoad();
        selenium.select("loanOfficerId", "label=" + parameters.getLoanOfficer());
        waitForPageToLoad();
        selenium.select("customerId", "label=" + parameters.getCenter());
        waitForPageToLoad();
        typeTextIfNotEmpty("transactionDateDD", parameters.getTransactionDay());
        typeTextIfNotEmpty("transactionDateMM", parameters.getTransactionMonth());
        typeTextIfNotEmpty("transactionDateYY", parameters.getTransactionYear());
        selenium.select("paymentId", "label=" + parameters.getPaymentMode());
        typeTextIfNotEmpty(RECEIPT_INPUT_ID, parameters.getReceiptId());
        typeTextIfNotEmpty("receiptDateDD", parameters.getReceiptDay());
        typeTextIfNotEmpty("receiptDateMM", parameters.getReceiptMonth());
        typeTextIfNotEmpty("receiptDateYY", parameters.getReceiptYear());
        selenium.click(CONTINUE_BUTTON_ID);
        waitForPageToLoad();
        CollectionSheetEntryEnterDataPage collectionSheetEntryEnterDataPage = new CollectionSheetEntryEnterDataPage(
                selenium);
        collectionSheetEntryEnterDataPage.verifyPage();
        return collectionSheetEntryEnterDataPage;
    }
	
	private void typeTextIfNotEmpty(String locator, String value) {
		if (value!= null && !value.isEmpty()) {
			selenium.type(locator, value);
		}
	}
}
