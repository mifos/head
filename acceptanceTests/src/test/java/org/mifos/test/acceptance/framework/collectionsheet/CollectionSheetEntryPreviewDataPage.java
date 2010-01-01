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



import org.mifos.test.acceptance.framework.MifosPage;

import com.thoughtworks.selenium.Selenium;

public class CollectionSheetEntryPreviewDataPage extends MifosPage {

    public CollectionSheetEntryPreviewDataPage() {
        super();
    }

    public CollectionSheetEntryPreviewDataPage(Selenium selenium) {
        super(selenium);
    }


    public CollectionSheetEntryPreviewDataPage verifyPage(CollectionSheetEntrySelectPage.SubmitFormParameters params) {
        verifyEqualsIfNotNull(getCenterOrGroupName(), params.getCenter());
        verifyEqualsIfNotNull(getOfficeName(), params.getBranch());
        if(params.getTransactionDay() != null) {
            verifyEqualsIfNotNull(getTransactionDate(), params.getTransactionDay()+"/"+params.getTransactionMonth()+"/"+params.getTransactionYear());
        }
        verifyEqualsIfNotNull(getPaymentType(), params.getPaymentMode());
        verifyEqualsIfNotNull(getReceiptID(), params.getReceiptId());
        if(params.getReceiptDay() != null) {
            verifyEqualsIfNotNull(getReceiptDate(), params.getReceiptDay()+"/"+params.getReceiptMonth()+"/"+params.getReceiptYear());
        }
        this.verifyPage("BulkEntryPreview");
        return this;
    }

    public CollectionSheetEntryEnterDataPage editAndGoToCollectionSheetEntryEnterDataPage() {
        selenium.click("bulkentry_preview.button.editdata");
        waitForPageToLoad();
        return new CollectionSheetEntryEnterDataPage(selenium);
    }

    public CollectionSheetEntryConfirmationPage submitAndGotoCollectionSheetEntryConfirmationPage() {
        selenium.click("bulkentry_preview.button.submit");
        waitForPageToLoad();
        return new CollectionSheetEntryConfirmationPage(selenium);
    }

    public String getCenterOrGroupName() {
        return getTextIfNotEmpty("bulkEntryPreview.text.centerOrGroupName");
    }

    public String getTransactionDate() {
        return getTextIfNotEmpty("bulkEntryPreview.text.transactionDate");
    }

    public String getOfficeName() {
        return getTextIfNotEmpty("bulkEntryPreview.text.officeName");
    }

    public String getLoanOfficerName() {
        return getTextIfNotEmpty("bulkEntryPreview.text.loanOfficer");
    }

    public String getPaymentType() {
        return getTextIfNotEmpty("bulkEntryPreview.text.paymentType");
    }

    public String getReceiptID() {
        return getTextIfNotEmpty("bulkEntryPreview.text.receiptID");
    }

    public String getReceiptDate() {
        return getTextIfNotEmpty("bulkEntryPreview.text.receiptDate");
    }
}
