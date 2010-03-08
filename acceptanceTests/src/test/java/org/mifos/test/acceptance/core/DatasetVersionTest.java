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

package org.mifos.test.acceptance.core;

import java.io.File;
import java.io.FilenameFilter;
import org.dbunit.dataset.IDataSet;
import org.mifos.framework.util.DbUnitUtilities;
import org.mifos.test.acceptance.framework.MifosPage;
import org.mifos.test.acceptance.framework.UiTestCaseBase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

@ContextConfiguration(locations = { "classpath:ui-test-context.xml" })
@Test(sequential = true, groups = {"core","acceptance"})
public class DatasetVersionTest extends UiTestCaseBase {

    @Autowired
    private DriverManagerDataSource dataSource;
    @Autowired
    private DbUnitUtilities dbUnitUtilities;


    @AfterMethod
    public void logOut() {
        (new MifosPage(selenium)).logout();
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    public void checkDataSetDatabaseVersion() throws Exception {


        class FileExtensionFilter implements FilenameFilter{
            String prefix="*";
            String ext="*";
            public FileExtensionFilter(String prefix, String ext){
                this.prefix = prefix;
                this.ext = ext;
            }

            public boolean accept(@SuppressWarnings("unused") File dir, String name){
                boolean validFile = false;
                if (name.startsWith(prefix) && name.endsWith(ext) ){
                    validFile = true;
                }
                return validFile;
            }
        }

        // FIXME - Path works for maven build but not from eclipse
        // To run this test in eclipse, change path below to: "acceptanceTests/target/test-classes/dataSets"
        File f = new File("target/test-classes/dataSets");
        FileExtensionFilter filter = new FileExtensionFilter("", "dbunit.xml.zip");
        File[] acceptList = f.listFiles(filter);

        for (File element : acceptList) {
            verifyDatabaseVersion(element.getName());
        }
    }

    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    private void verifyDatabaseVersion(String acceptanceDataSetFile) throws Exception {
        String[] tablesToValidate = { "DATABASE_VERSION" };

        IDataSet acceptanceDataSet = dbUnitUtilities.getDataSetFromDataSetDirectoryFile(acceptanceDataSetFile);
        IDataSet databaseDataSet = dbUnitUtilities.getDataSetForTables(dataSource, tablesToValidate);

        //compare acceptance data set version to db version on running application
        dbUnitUtilities.verifyTables(tablesToValidate, acceptanceDataSet, databaseDataSet);

    }
}