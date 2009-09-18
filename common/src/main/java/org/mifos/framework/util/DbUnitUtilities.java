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

package org.mifos.framework.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.Assertion;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.SortedTable;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.dbunit.util.TableFormatter;
import org.joda.time.DateTime;
import org.mifos.core.MifosRuntimeException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Utility methods for operating on DbUnit data.
 */
public class DbUnitUtilities {
    private static final Log LOG = LogFactory.getLog(DbUnitUtilities.class);

    static Map<String, String[]> columnsToIgnoreWhenVerifyingTables = new HashMap<String, String[]>();

    public DbUnitUtilities() {
        initialize();
    }

    private void initialize() {
        columnsToIgnoreWhenVerifyingTables.put("ACCOUNT_PAYMENT", new String[] { "payment_id","payment_date", "receipt_date" });
        columnsToIgnoreWhenVerifyingTables.put("ACCOUNT_TRXN", new String[] { "account_trxn_id","created_date","action_date","payment_id", "due_date", "installment_id" });
        columnsToIgnoreWhenVerifyingTables.put("CUSTOMER_ATTENDANCE", new String[] { "id", "meeting_date" });
        columnsToIgnoreWhenVerifyingTables.put("FINANCIAL_TRXN", new String[] { "trxn_id","action_date", "account_trxn_id","balance_amount","posted_date", "debit_credit_flag", "fin_action_id" });
        columnsToIgnoreWhenVerifyingTables.put("LOAN_ACTIVITY_DETAILS", new String[] { "id", "account_id", "created_date" });
        columnsToIgnoreWhenVerifyingTables.put("LOAN_SCHEDULE", new String[] { "id","payment_date" });
        columnsToIgnoreWhenVerifyingTables.put("LOAN_TRXN_DETAIL", new String[] { "account_trxn_id" });
        columnsToIgnoreWhenVerifyingTables.put("CUSTOMER_FEE_SCHEDULE", new String[] { "account_fees_detail_id" });
        columnsToIgnoreWhenVerifyingTables.put("FEE_TRXN_DETAIL", new String[] { "account_trxn_id", "account_fee_id", "fee_trxn_detail_id" });
        columnsToIgnoreWhenVerifyingTables.put("CUSTOMER_ACCOUNT_ACTIVITY", new String[] { "customer_account_activity_id", "created_date" });
        columnsToIgnoreWhenVerifyingTables.put("CUSTOMER_TRXN_DETAIL", new String[] { "account_trxn_id" });
        columnsToIgnoreWhenVerifyingTables.put("LOAN_SUMMARY", new String[] { "account_id" });
        columnsToIgnoreWhenVerifyingTables.put("LOAN_SCHEDULE", new String[] { "id", "account_id", "payment_date" });
        columnsToIgnoreWhenVerifyingTables.put("ACCOUNT_STATUS_CHANGE_HISTORY", new String[] { "changed_date", "account_status_change_id", "account_id" });
        columnsToIgnoreWhenVerifyingTables.put("HOLIDAY", new String[] {});
        columnsToIgnoreWhenVerifyingTables.put("ACCOUNT", new String[] { "closed_date"});
        columnsToIgnoreWhenVerifyingTables.put("ACCOUNT_FLAG_DETAIL", new String[] { "created_date", "account_flag_id" });
        columnsToIgnoreWhenVerifyingTables.put("ACCOUNT_NOTES", new String[] { "comment_date", "account_notes_id" });
        columnsToIgnoreWhenVerifyingTables.put("CLIENT_PERF_HISTORY", new String[] { "id" });
        columnsToIgnoreWhenVerifyingTables.put("LOAN_ACCOUNT", new String[] { "account_id", "meeting_id"});
        columnsToIgnoreWhenVerifyingTables.put("PRD_OFFERING", new String[] { "prd_offering_id", "global_prd_offering_num", "prd_offering_name", "prd_offering_short_name", "created_date", "start_date"});
        columnsToIgnoreWhenVerifyingTables.put("SAVINGS_OFFERING", new String[] { "prd_offering_id" });

    }

    /**
     * Compare two tables using DbUnit DataSets
     * @param tableName
     * @param databaseDataSet
     * @param expectedDataSet
     * @throws DataSetException
     * @throws DatabaseUnitException
     */
    public void verifyTable(String tableName, IDataSet databaseDataSet, IDataSet expectedDataSet) throws DataSetException,
            DatabaseUnitException {

        Assert.assertNotNull("Didn't find requested table [" + tableName + "] in columnsToIgnoreWhenVerifyingTables map.", columnsToIgnoreWhenVerifyingTables.get(tableName));
        ITable expectedTable = expectedDataSet.getTable(tableName);
        ITable actualTable = databaseDataSet.getTable(tableName);
        actualTable = DefaultColumnFilter.includedColumnsTable(actualTable,
                expectedTable.getTableMetaData().getColumns());

        try {
            Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, columnsToIgnoreWhenVerifyingTables.get(tableName));
        } catch (AssertionError e) {
            throw new DatabaseUnitException(getTableDiff(expectedTable, actualTable), e);
        }
    }

    private String getTableDiff(ITable expectedTable, ITable actualTable) throws DataSetException {
        TableFormatter formatter = new TableFormatter();
        String newline = System.getProperty("line.separator");
        StringBuilder differences = new StringBuilder();
        differences.append("---Expected Table---" + newline);
        differences.append(formatter.format(expectedTable) + newline);
        differences.append("---Actual Table---" + newline);
        differences.append(formatter.format(actualTable) + newline);
        return differences.toString();
    }

    /**
     * Convenience method for comparing multiple tables using DbUnit DataSets
     * @param tableNames
     * @param databaseDataSet
     * @param expectedDataSet
     * @throws DataSetException
     * @throws DatabaseUnitException
     */

    public void verifyTables(String[] tableNames, IDataSet databaseDataSet, IDataSet expectedDataSet) throws DataSetException,
    DatabaseUnitException {
        for (String tableName : tableNames) {
            this.verifyTable(tableName, databaseDataSet, expectedDataSet);

        }
     }

    public void verifySortedTable(String tableName, IDataSet databaseDataSet,
            IDataSet expectedDataSet, String[] sortingColumns) throws DataSetException, DatabaseUnitException {
        Boolean actualDBSortFlag = true;
        Boolean expectedDBSortFlag = true;
        verifySortedTableWithOrdering(tableName, databaseDataSet, expectedDataSet, sortingColumns, actualDBSortFlag, expectedDBSortFlag);
    }

    public void verifySortedTableWithOrdering(String tableName, IDataSet databaseDataSet,
            IDataSet expectedDataSet, String[] sortingColumns, Boolean actualDBComparableFlag, Boolean expectedDBComparableFlag) throws DataSetException, DatabaseUnitException {

        Assert.assertNotNull("Didn't find requested table [" + tableName + "] in columnsToIgnoreWhenVerifyingTables map.", columnsToIgnoreWhenVerifyingTables.get(tableName));
        ITable expectedTable = expectedDataSet.getTable(tableName);
        ITable actualTable = databaseDataSet.getTable(tableName);
        actualTable = DefaultColumnFilter.includedColumnsTable(actualTable,
                expectedTable.getTableMetaData().getColumns());
        SortedTable sortedExpectedTable = new SortedTable(expectedTable, sortingColumns);
        sortedExpectedTable.setUseComparable(expectedDBComparableFlag);
        expectedTable = sortedExpectedTable;
        SortedTable sortedActualTable = new SortedTable(actualTable, sortingColumns);
        sortedActualTable.setUseComparable(actualDBComparableFlag);
        actualTable = sortedActualTable;

        if (LOG.isDebugEnabled()) {
            printTable(expectedTable);
            printTable(actualTable);
        }

        try {
            Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, columnsToIgnoreWhenVerifyingTables.get(tableName));
        } catch (AssertionError e) {
            throw new DatabaseUnitException(getTableDiff(expectedTable, actualTable), e);
        }
    }

    public void printTable(ITable table) throws DataSetException {
       TableFormatter formatter = new TableFormatter();
       LOG.debug(formatter.format(table));
    }

    public void loadDataFromFile(String filename, DriverManagerDataSource dataSource)
    throws DatabaseUnitException, SQLException, IOException, URISyntaxException {
        Connection jdbcConnection = null;
        IDataSet dataSet = getDataSetFromDataSetDirectoryFile(filename);
        try {
            jdbcConnection = DataSourceUtils.getConnection(dataSource);
            IDatabaseConnection databaseConnection = new DatabaseConnection(jdbcConnection);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
        }
        finally {
            if (jdbcConnection != null) {
                jdbcConnection.close();
            }
            DataSourceUtils.releaseConnection(jdbcConnection, dataSource);
        }
    }

    public IDataSet getDataSetFromDataSetDirectoryFile(String filename)
    throws IOException, DataSetException {
        return getDataSetFromClasspathFile(filename,"/dataSets/");
    }

    public IDataSet getDataSetFromClasspathFile(String filename, String directory)
    throws IOException, DataSetException {
        boolean enableColumnSensing = true;
        ClassPathResource resource = new ClassPathResource(directory + filename);
        File file = resource.getFile();
        if (file == null) {
            throw new MifosRuntimeException("Couldn't find file:" + filename);
        }
        return new FlatXmlDataSet(
                getUncompressed(file), false, enableColumnSensing);
    }

    public IDataSet getDataSetFromFile(String filename)
    throws IOException, DataSetException {
        boolean enableColumnSensing = true;
        File file = new File(filename);
        return new FlatXmlDataSet(
                getUncompressed(file), false, enableColumnSensing);
    }

    /**
     * Convenience method to get a DbUnit DataSet of only one table.
     * @param driverManagerDataSource
     * @param tableName
     * @return
     * @throws Exception
     * @throws Exception
     * @throws SQLException
     * @throws DataSetException
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public IDataSet getDataSetForTable(DriverManagerDataSource driverManagerDataSource, String tableName) throws Exception  {
        return this.getDataSetForTables(driverManagerDataSource, new String[] { tableName });
    }

    /**
     * Returns a DbUnit DataSet for several tables.
     * @param driverManagerDataSource TODO
     * @param tableNames
     * @return
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public IDataSet getDataSetForTables(DriverManagerDataSource driverManagerDataSource, String[] tableNames) throws Exception  {
        Connection jdbcConnection = null;
        IDataSet databaseDataSet = null;
        try {
            jdbcConnection = DataSourceUtils.getConnection(driverManagerDataSource);
            IDatabaseTester databaseTester = new DataSourceDatabaseTester(driverManagerDataSource);
            IDatabaseConnection databaseConnection = databaseTester.getConnection();
            databaseDataSet = databaseConnection.createDataSet(tableNames);
        }
        finally {
            jdbcConnection.close();
            DataSourceUtils.releaseConnection(jdbcConnection, driverManagerDataSource);
        }
        return databaseDataSet;
    }

    public void dumpDatabase(String fileName, DriverManagerDataSource dataSource) throws SQLException, FileNotFoundException, ClassNotFoundException, DatabaseUnitException, IOException {
        Connection jdbcConnection = null;
        try {
            jdbcConnection = dataSource.getConnection();
            this.dumpDatabase(fileName, jdbcConnection);
        } finally {
            jdbcConnection.close();
        }
    }

    public void dumpDatabase(String fileName, Connection jdbcConnection) throws ClassNotFoundException, SQLException, DatabaseUnitException, FileNotFoundException, IOException {
        IDatabaseConnection connection = new DatabaseConnection(jdbcConnection);
        IDataSet fullDataSet = connection.createDataSet();
        FlatXmlDataSet.write(fullDataSet, new FileOutputStream(fileName));
    }

    public void dumpDatabaseToTimestampedFileInConfigurationDirectory(DriverManagerDataSource dataSource) throws FileNotFoundException, ClassNotFoundException, SQLException, DatabaseUnitException, IOException {
        String configurationDirectory = new ConfigurationLocator().getConfigurationDirectory();
        DateTime currentTime = new DateTimeService().getCurrentDateTime();
        String timeStamp = currentTime.toString().replace(":", "_");
        String databaseDumpPathName = configurationDirectory + '/' + "databaseDump-" + timeStamp;
        dumpDatabase(databaseDumpPathName, dataSource);
    }

    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "OS_OPEN_STREAM_EXCEPTION_PATH" }, justification = "buffered streams close streams they wrap")
    private URL getUncompressed(File file) throws IOException {
        /* Buffer size based on this tutorial:
         * http://java.sun.com/developer/technicalArticles/Programming/compression/
         */
        int bufferSize = 2048;
        byte data[] = new byte[bufferSize];
        File tempFile = File.createTempFile("mifosDbUnitTempfile", ".tmp");
        tempFile.deleteOnExit();

        ZipFile zipFile = new ZipFile(file);
        BufferedInputStream src = null;
        BufferedOutputStream dest = null;

        try {
            Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
            ZipEntry zipEntry = zipEntries.nextElement();
            if (zipEntries.hasMoreElements()) {
                throw new MifosRuntimeException(
                "only expecting one file entry per dataSet zip archive");
            }
            src = new BufferedInputStream(zipFile.getInputStream(zipEntry));
            dest = new BufferedOutputStream(new FileOutputStream(tempFile), bufferSize);

            int count;
            while (true) {
                count = src.read(data, 0, bufferSize);
                if (count == -1) {
                    break;
                }
                dest.write(data, 0, count);
            }
        } finally {
            if (dest != null) {
                dest.close();
            }
            if (src != null) {
                src.close();
            }
            zipFile.close();
        }

        return tempFile.toURI().toURL();
    }


    public void verifyTableWithSort(String[] columnOrder, String tableName, IDataSet expectedDataSet, IDataSet databaseDataSet) throws DataSetException,
            DatabaseUnitException {
        verifySortedTableWithOrdering(tableName, databaseDataSet, expectedDataSet,
                columnOrder, false, true);
    }



}
