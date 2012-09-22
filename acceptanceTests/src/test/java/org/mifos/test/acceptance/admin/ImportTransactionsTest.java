/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.test.acceptance.admin;

import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.ImportTransactionsConfirmationPage;
import org.mifos.test.acceptance.framework.admin.ImportTransactionsPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.util.PluginsUtil;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

@ContextConfiguration(locations = {"classpath:ui-test-context.xml"})
@Test(singleThreaded = true, groups = {"import"})
public class ImportTransactionsTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    private final PluginsUtil pluginsUtil = new PluginsUtil("audibank-xls-importer-0.0.3-SNAPSHOT-jar-with-dependencies.jar");

    private static final String EXCEL_IMPORT_TYPE = "Audi Bank (Excel 2007)";

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void importExcelFormatAudiBankTransactions() throws Exception {
        pluginsUtil.loadPlugin();
        navigationHelper = new NavigationHelper(selenium);

        String importFile = this.getClass().getResource("/AudiUSD-SevenTransactions.xls").toString();
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, "mpesa_export_dbunit.xml", dataSource, selenium);

        importTransaction(importFile, EXCEL_IMPORT_TYPE);

        // TODO - add proper UI verifications and enable this test after MIFOS-4651 is fixed

        (new MifosPage(selenium)).logout();
        pluginsUtil.unloadPlugin();
    }

    //  Test the import transaction page loads with no plugins available  - regression test for MIFOS-2683
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true, groups = "no_db_unit")
    public void importTransactionPageLoad() throws Exception {
        navigationHelper = new NavigationHelper(selenium);

        String tempFileName = pluginsUtil.movePluginToTemp();
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ImportTransactionsPage importTransactionsPage = adminPage.navigateToImportTransactionsPage();
        importTransactionsPage.verifyPage();
        pluginsUtil.movePluginFromTemp(tempFileName);

        (new MifosPage(selenium)).logout();
    }

    private void importTransaction(String importFile, String importType) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ImportTransactionsPage importTransactionsPage = adminPage.navigateToImportTransactionsPage();
        importTransactionsPage.verifyPage();
        ImportTransactionsConfirmationPage importTransactionsConfirmationPage = importTransactionsPage.importAudiTransactions(importFile, importType);
        importTransactionsConfirmationPage.verifyPage();
    }
}
