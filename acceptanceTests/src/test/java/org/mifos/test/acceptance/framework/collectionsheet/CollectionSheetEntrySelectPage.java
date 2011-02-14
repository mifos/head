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

package org.mifos.test.acceptance.framework.collectionsheet;

import org.mifos.test.acceptance.framework.HomePage;
import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class CollectionSheetEntrySelectPage extends MifosPage {

    private static final String RECEIPT_INPUT_ID = "bulkentry.input.receiptId";
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
        public static final String CASH = "Cash";

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

        @SuppressWarnings("PMD.OnlyOneReturn")
        public int getPaymentModeValue() {
            if (CASH.equals(paymentMode)) {
                return 1;
            }

            return -1;
        }
    }

    public void submit() {
        selenium.click(CONTINUE_BUTTON_ID);
        waitForPageToLoad();
    }

    public CollectionSheetEntryEnterDataPage submitAndGotoCollectionSheetEntryEnterDataPage(
            SubmitFormParameters parameters) {
        boolean onlyTypeIfEmpty = true;
        boolean waitForPageToLoad = true;
        CollectionSheetEntryEnterDataPage collectionSheetEntryEnterDataPage = submitAndGotoCollectionSheetEntryEnterDataPageWithoutVerifyingPage(
                parameters, onlyTypeIfEmpty, waitForPageToLoad);
        collectionSheetEntryEnterDataPage.verifyPage();
        return collectionSheetEntryEnterDataPage;
    }

    public CollectionSheetEntryEnterDataPage submitAndGotoCollectionSheetEntryEnterDataPageWithoutVerifyingPage(
            SubmitFormParameters parameters, boolean onlyTypeIfEmpty, boolean waitForPageToLoad) {

        fillOutDropDownMenus(parameters, waitForPageToLoad);
        CollectionSheetEntryEnterDataPage collectionSheetEntryEnterDataPage = fillOutTransactionAndReceiptFieldsAndContinue(
                parameters, onlyTypeIfEmpty);
        return collectionSheetEntryEnterDataPage;
    }

    private void fillOutDropDownMenus(SubmitFormParameters parameters, boolean waitForPageToLoad) {
        selenium.select("officeId", "label=" + parameters.getBranch());
        waitForPageToLoadIfNecessary(waitForPageToLoad);
        selenium.select("loanOfficerId", "label=" + parameters.getLoanOfficer());
        waitForPageToLoadIfNecessary(waitForPageToLoad);
        selenium.select("customerId", "label=" + parameters.getCenter());
        waitForPageToLoadIfNecessary(waitForPageToLoad);
    }

    public void fillOutDropDownMenusWithGivenInput(SubmitFormParameters parameters) {
        if (parameters.getBranch() != null) {
            selenium.select("officeId", "label=" + parameters.getBranch());
            waitForPageToLoadIfNecessary(true);
        }
        if (parameters.getLoanOfficer() != null) {
            selenium.select("loanOfficerId", "label=" + parameters.getLoanOfficer());
            waitForPageToLoadIfNecessary(true);
        }
        if (parameters.getCenter() != null) {
            selenium.select("customerId", "label=" + parameters.getCenter());
            waitForPageToLoadIfNecessary(true);
        }
        if (parameters.getPaymentMode() != null) {
            selenium.select("paymentId", "value=" + parameters.getPaymentModeValue());
        }
    }

    private void waitForPageToLoadIfNecessary(boolean waitForPageToLoad) {
        if (waitForPageToLoad) {
            waitForPageToLoad();
        }
    }

    private CollectionSheetEntryEnterDataPage fillOutTransactionAndReceiptFieldsAndContinue(
            SubmitFormParameters parameters, boolean onlyTypeIfEmpty) {
        typeText("transactionDateDD", parameters.getTransactionDay(), onlyTypeIfEmpty);
        typeText("transactionDateMM", parameters.getTransactionMonth(), onlyTypeIfEmpty);
        typeText("transactionDateYY", parameters.getTransactionYear(), onlyTypeIfEmpty);
        selenium.select("paymentId", "value=" + parameters.getPaymentModeValue());
        typeText(RECEIPT_INPUT_ID, parameters.getReceiptId(), onlyTypeIfEmpty);
        typeText("receiptDateDD", parameters.getReceiptDay(), onlyTypeIfEmpty);
        typeText("receiptDateMM", parameters.getReceiptMonth(), onlyTypeIfEmpty);
        typeText("receiptDateYY", parameters.getReceiptYear(), onlyTypeIfEmpty);
        selenium.click(CONTINUE_BUTTON_ID);
        waitForPageToLoad();
        CollectionSheetEntryEnterDataPage collectionSheetEntryEnterDataPage = new CollectionSheetEntryEnterDataPage(
                selenium);
        return collectionSheetEntryEnterDataPage;
    }

    private void typeText(String locator, String value, boolean onlyTypeIfEmpty) {
        if (value != null && (!onlyTypeIfEmpty || !value.isEmpty())) {
            selenium.type(locator, value);
        }
    }
}
