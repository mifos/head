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

package org.mifos.test.acceptance.admin;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.channels.FileChannel;
import org.junit.Assert;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.mifos.test.acceptance.framework.admin.AdminPage;
import org.mifos.test.acceptance.framework.admin.ImportTransactionsPage;
import org.mifos.test.acceptance.framework.admin.ViewOrganizationSettingsPage;
import org.mifos.test.acceptance.framework.testhelpers.NavigationHelper;
import org.mifos.test.acceptance.remote.InitializeApplicationRemoteTestingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * This test requires installation of MPESA plugin and changing @Test(enabled=false) to @Test(enabled=true).
 *
 * The test imports the files from acceptanceTests/src/test/resources/mpesa/*.xls and for each file expects
 * that on import review page there will be messages from the corresponding file
 * acceptanceTests/src/test/resources/mpesa/*.xls.expected.txt
 */
@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"import"})
public class MpesaImportTest extends UiTestCaseBase {

    private NavigationHelper navigationHelper;
    private boolean copyPlugin = false;
    private static final String EXCEL_IMPORT_TYPE = "M-PESA Excel 97(-2007)";
    private static final String PLUGIN_NAME = "mpesa-xls-importer-0.0.2-SNAPSHOT-jar-with-dependencies.jar";
    private String configPath = new ConfigurationLocator().getConfigurationDirectory();
    static final String[] TEST_FILES = new String[] { "loan_product_code.xls", "mixed.xls", "saving_and_loan.xls",
        "savings_product_code.xls", "example_loan_disb.xls"};

    @Autowired
    private DbUnitUtilities dbUnitUtilities;
    @Autowired
    private InitializeApplicationRemoteTestingService initRemote;

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void loadPlugin() throws Exception{

        if (!configPath.endsWith(File.separator)) {
            configPath += File.separator;
        }

        String plugin = MpesaImportTest.class.getResource("/mpesa/" + PLUGIN_NAME).getFile();
        File mpesa = new File(configPath+"plugins" + File.separator + PLUGIN_NAME);
        File folderPlugins = new File(configPath+"plugins");

        if(!folderPlugins.exists())
        {
            folderPlugins.mkdir();
        }

        if(!mpesa.exists())
        {
            mpesa.createNewFile();
            copyFile(new File(plugin), mpesa);
            copyPlugin = true;
        }
    }

    private void unloadPlugin()
    {
        File mpesa = new File(configPath +"plugins" + File.separator + PLUGIN_NAME);

        if(copyPlugin && mpesa.exists()){
            mpesa.delete();
        }
    }

    @Override
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // one of the dependent methods throws Exception
    @BeforeMethod
    public void setUp() throws Exception {
        loadPlugin();
        navigationHelper = new NavigationHelper(selenium);
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private String copyPluginToTemp() throws Exception {
        File mpesa = new File(configPath+"plugins" + File.separator + PLUGIN_NAME);
        File temp = File.createTempFile(PLUGIN_NAME, ".tmp");

        copyFile(mpesa,temp);

        mpesa.delete();

        return temp.getAbsolutePath();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void copyPluginFromTemp(String tempFileName) throws Exception {
        File temp = new File(tempFileName);
        File mpesa = new File(configPath+"plugins" + File.separator + PLUGIN_NAME);

        copyFile(temp,mpesa);

        temp.delete();
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        FileChannel source = null;
        FileChannel destination = null;
        try {
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(destFile).getChannel();
            destination.transferFrom(source, 0, source.size());
        }
        finally {
            if(source != null) {
                source.close();
            }
            if(destination != null) {
                destination.close();
            }
        }
     }

    @AfterMethod
    public void tearDown() {
        (new MifosPage(selenium)).logout();
        unloadPlugin();
    }

    @SuppressWarnings({"PMD.SignatureDeclareThrowsException", "PMD.SystemPrintln"})
    @Test(enabled=false)
    public void importMpesaTransactions() throws Exception {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ViewOrganizationSettingsPage viewOrganizationSettingsPage = adminPage.navigateToViewOrganizationSettingsPage();
        viewOrganizationSettingsPage.verifyMiscellaneous(new String[]{"Max MPESA Disbursal Limit: 50000.0"});

        String dataset = "mpesa_export.xml";
        initRemote.dataLoadAndCacheRefresh(dbUnitUtilities, dataset, dataSource, selenium);

        for (String importFile : TEST_FILES) {
            String path = this.getClass().getResource("/mpesa/" + importFile).toString();
            importTransaction(path);
            checkIfOutputMatchesExpected(path);
        }
     }

    private void checkIfOutputMatchesExpected(String path) throws FileNotFoundException, IOException, URISyntaxException {
        DataInputStream in = new DataInputStream(new FileInputStream(new File(new URI(path + ".expected.txt"))));
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String line = null;
        while (true) {
            line = br.readLine();
            if (line == null) {
                break;
            }
            if (!selenium.isTextPresent(line.trim())) {
                Assert.fail("No text <" + line.trim() + "> present on the page");
            }
        }
    }

    private void importTransaction(String importFile) {
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ImportTransactionsPage importTransactionsPage = adminPage.navigateToImportTransactionsPage();
        importTransactionsPage.verifyPage();
        importTransactionsPage.reviewTransactions(importFile, EXCEL_IMPORT_TYPE);
    }

    //  Test the import transaction page loads with no plugins available  - regression test for MIFOS-2683
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    @Test(enabled=true)
    public void importTransactionPageLoad() throws Exception {
        String tempFileName = copyPluginToTemp();
        AdminPage adminPage = navigationHelper.navigateToAdminPage();
        ImportTransactionsPage importTransactionsPage = adminPage.navigateToImportTransactionsPage();
        importTransactionsPage.verifyPage();
        copyPluginFromTemp(tempFileName);
    }

}
