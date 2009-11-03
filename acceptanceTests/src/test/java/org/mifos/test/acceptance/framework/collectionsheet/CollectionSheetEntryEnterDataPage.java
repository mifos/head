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
import org.testng.Assert;

import com.thoughtworks.selenium.Selenium;

public class CollectionSheetEntryEnterDataPage extends AbstractPage {

    public static final int ATTENDANCE_P = 1;
    public static final int ATTENDANCE_A = 2;
    public static final int ATTENDANCE_AA = 3;
    public static final int ATTENDANCE_L = 4;

    public CollectionSheetEntryEnterDataPage() {
        super();
    }

    public CollectionSheetEntryEnterDataPage(Selenium selenium) {
        super(selenium);
    }

    public CollectionSheetEntryEnterDataPage verifyPage() {
        this.verifyPage("BulkEntryData");
        return this;
    }

    public CollectionSheetEntryPreviewDataPage submitAndGotoCollectionSheetEntryPreviewDataPage() {
        selenium.click("id=bulkentry_data.button.preview");
        waitForPageToLoad();
        return new CollectionSheetEntryPreviewDataPage(selenium);
    }

    public CollectionSheetEntryEnterDataPage clickPreviewButton() {
        selenium.click("id=bulkentry_data.button.preview");
        waitForPageToLoad();
        return new CollectionSheetEntryEnterDataPage(selenium);
    }

    public CollectionSheetEntryEnterDataPage enterAccountValue(int row, int column, double amount) {
        selenium.type("enteredAmount[" + row + "][" + column + "]", Double.toString(amount));
        return this;
    }

    public CollectionSheetEntryEnterDataPage enterDepositAccountValue(int row, int column, double amount) {
        selenium.type("depositAmountEntered[" + row + "][" + column + "]", Double.toString(amount));
        return this;
    }

    public CollectionSheetEntryEnterDataPage enterCustomerAccountValue(int row, int column, double amount) {
        selenium.type("customerAccountAmountEntered[" + row + "][" + column + "]", Double.toString(amount));
        return this;
    }

    public CollectionSheetEntryEnterDataPage enterAttendance(int row, int attendance) {
        selenium.select("attendanceSelected[" + row + "]", "value=" + attendance);
        return this;
    }

    public void verifyAccountValue(int row, int col, double fee) {
        Assert.assertEquals(selenium.getValue("customerAccountAmountEntered[" + row + "][" + col + "]"),  Double.toString(fee));

    }

    public HomePage cancel() {
        selenium.click("id=bulkentry_data.button.cancel");
        waitForPageToLoad();
        return new HomePage(selenium);
    }

    public void verifyCustomerAccountValue(int row, int col, double amount) {
        Assert.assertEquals(selenium.getValue("customerAccountAmountEntered[" + row + "][" + col + "]"), Double.toString(amount));

    }
    public void verifyLoanAmountValue(int row, int col, double amount) {
        Assert.assertEquals(selenium.getValue("enteredAmount[" + row + "][" + col + "]"), Double.toString(amount));

    }

}
