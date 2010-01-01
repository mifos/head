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

package org.mifos.test.acceptance.framework.testhelpers;

import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryConfirmationPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryPreviewDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;

import com.thoughtworks.selenium.Selenium;

/**
 * Holds common methods used for CollectionSheetEntry tests.
 *
 */
public class CollectionSheetEntryTestHelper {

     private final NavigationHelper navigationHelper;

    public CollectionSheetEntryTestHelper(Selenium selenium) {
        this.navigationHelper = new NavigationHelper(selenium);
    }

    public CollectionSheetEntrySelectPage loginAndNavigateToCollectionSheetEntrySelectPage() {
        return navigationHelper
                .navigateToClientsAndAccountsPage()
                .navigateToEnterCollectionSheetDataUsingLeftMenu();
    }

    public CollectionSheetEntryConfirmationPage submitDefaultCollectionSheetEntryData(SubmitFormParameters formParameters) {
        CollectionSheetEntryPreviewDataPage previewPage = loginAndNavigateToCollectionSheetEntrySelectPage()
           .submitAndGotoCollectionSheetEntryEnterDataPage(formParameters)
           .submitAndGotoCollectionSheetEntryPreviewDataPage();
        previewPage.verifyPage(formParameters);
        return previewPage.submitAndGotoCollectionSheetEntryConfirmationPage();
    }

    public CollectionSheetEntryConfirmationPage submitCollectionSheetWithChangedAttendance(SubmitFormParameters collectionSheetParams, int[] prevAttendanceValues, int[] attendanceValues) {
        CollectionSheetEntryEnterDataPage enterDataPage = loginAndNavigateToCollectionSheetEntrySelectPage()
            .submitAndGotoCollectionSheetEntryEnterDataPage(collectionSheetParams);
        for(int i = 0; i < attendanceValues.length; i++) {
            enterDataPage.verifyAttendance(i, prevAttendanceValues[i]);
            enterDataPage.enterAttendance(i, attendanceValues[i]);
        }
        return enterDataPage
                .submitAndGotoCollectionSheetEntryPreviewDataPage()
                .submitAndGotoCollectionSheetEntryConfirmationPage();
    }

}
