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

package org.mifos.test.acceptance.collectionsheet;

import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntryEnterDataPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage;
import org.mifos.test.acceptance.framework.collectionsheet.CollectionSheetEntrySelectPage.SubmitFormParameters;
import org.mifos.test.acceptance.framework.testhelpers.CollectionSheetEntryTestHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations={"classpath:ui-test-context.xml"})
@Test(sequential=true, groups={"smoke","collectionsheet","acceptance","ui"})
public class CollectionSheetEntryInputErrorsTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void enteringAnInvalidAmountAndClickingPreviewShouldCauseAReturnToCollectionSheetEntryPage()
    throws Exception {
        initRemote
        .dataLoadAndCacheRefresh(dbUnitUtilities, "acceptance_small_003_dbunit.xml.zip", dataSource, selenium);
        SubmitFormParameters formParameters = getFormParametersForTestOffice();
        CollectionSheetEntryEnterDataPage enterDataPage = navigateToCollectionSheetEntryPage(formParameters);
        enterDataPage.verifyPage();

        enterDataPage.enterCustomerAccountValue(0, 6, 888.4); // invalid amount

        CollectionSheetEntryEnterDataPage nextPage = enterDataPage.clickPreviewButton();

        nextPage.verifyPage();

    }


    private CollectionSheetEntryEnterDataPage navigateToCollectionSheetEntryPage(SubmitFormParameters formParameters) {
        CollectionSheetEntrySelectPage selectPage = new CollectionSheetEntryTestHelper(selenium).loginAndNavigateToCollectionSheetEntrySelectPage();
        selectPage.verifyPage();
        CollectionSheetEntryEnterDataPage enterDataPage = selectPage
        .submitAndGotoCollectionSheetEntryEnterDataPage(formParameters);
        return enterDataPage;
    }

    private SubmitFormParameters getFormParametersForTestOffice() {
        SubmitFormParameters formParameters = new SubmitFormParameters();
        formParameters.setBranch("MyOffice1233265929385");
        formParameters.setLoanOfficer("Joe1233265931256 Guy1233265931256");
        formParameters.setCenter("MyCenter1233265933427");
        formParameters.setPaymentMode("Cash");
        return formParameters;
    }



}

