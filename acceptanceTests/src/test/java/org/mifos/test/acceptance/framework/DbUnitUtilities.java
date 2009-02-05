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

package org.mifos.test.acceptance.framework;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
import org.mifos.core.ClasspathResource;
import org.mifos.core.MifosRuntimeException;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testng.Assert;

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
        columnsToIgnoreWhenVerifyingTables.put("ACCOUNT_PAYMENT", new String[] { "payment_id","payment_date" });
        columnsToIgnoreWhenVerifyingTables.put("ACCOUNT_TRXN", new String[] { "account_trxn_id","created_date","action_date","payment_id" });        
        columnsToIgnoreWhenVerifyingTables.put("CUSTOMER_ATTENDANCE", new String[] { "id", "meeting_date" });        
        columnsToIgnoreWhenVerifyingTables.put("FINANCIAL_TRXN", new String[] { "trxn_id","action_date", "account_trxn_id","balance_amount","posted_date" });        
        columnsToIgnoreWhenVerifyingTables.put("LOAN_ACTIVITY_DETAILS", new String[] { "id","created_date" });        
        columnsToIgnoreWhenVerifyingTables.put("LOAN_SCHEDULE", new String[] { "id","payment_date" });        
        columnsToIgnoreWhenVerifyingTables.put("LOAN_TRXN_DETAIL", new String[] { "account_trxn_id" });        
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
        
        Assert.assertNotNull(columnsToIgnoreWhenVerifyingTables.get(tableName), "Didn't find requested table [" + tableName + "] in columnsToIgnoreWhenVerifyingTables map.");
        ITable expectedTable = expectedDataSet.getTable(tableName);
        ITable actualTable = databaseDataSet.getTable(tableName);
        actualTable = DefaultColumnFilter.includedColumnsTable(actualTable, 
                expectedTable.getTableMetaData().getColumns());
        Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, columnsToIgnoreWhenVerifyingTables.get(tableName));
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

        Assert.assertNotNull(columnsToIgnoreWhenVerifyingTables.get(tableName), "Didn't find requested table [" + tableName + "] in columnsToIgnoreWhenVerifyingTables map.");
        ITable expectedTable = expectedDataSet.getTable(tableName);
        ITable actualTable = databaseDataSet.getTable(tableName);

        actualTable = DefaultColumnFilter.includedColumnsTable(actualTable, 
                expectedTable.getTableMetaData().getColumns());

        SortedTable sortedExpectedTable = new SortedTable(expectedTable, sortingColumns);
        sortedExpectedTable.setUseComparable(true);
        expectedTable = sortedExpectedTable;
        SortedTable sortedActualTable = new SortedTable(actualTable, sortingColumns);
        sortedActualTable.setUseComparable(true);
        actualTable = sortedActualTable;
        
        if (LOG.isDebugEnabled()) {
            printTable(expectedTable);
            printTable(actualTable);
        }
        
        Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, columnsToIgnoreWhenVerifyingTables.get(tableName));
    }
    
    public void printTable(ITable table) throws DataSetException {
       TableFormatter formatter = new TableFormatter();
       LOG.debug(formatter.format(table));
    }    
    
    public void loadDataFromFile(String filename, DriverManagerDataSource dataSource) 
    throws DatabaseUnitException, SQLException, IOException {
        Connection jdbcConnection = null;
        IDataSet dataSet = getDataSetFromFile(filename);
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

    public IDataSet getDataSetFromFile(String filename)
    throws IOException, DataSetException {
        boolean enableColumnSensing = true;
        URL url = ClasspathResource.getInstance("/dataSets/").getUrl(filename);
        if (url == null) {
            throw new MifosRuntimeException("Couldn't find file:" + filename);
        }
        return new FlatXmlDataSet(
                getUncompressed(url), false, enableColumnSensing);
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
    

    private URL getUncompressed(URL url) throws IOException {
        /* Buffer size based on this tutorial:
         * http://java.sun.com/developer/technicalArticles/Programming/compression/
         */
        int bufferSize = 2048;
        byte data[] = new byte[bufferSize];
        File tempFile = File.createTempFile("mifosDbUnitTempfile", ".tmp");
        tempFile.deleteOnExit();
        
        ZipFile zipFile = new ZipFile(url.getPath());
        Enumeration<? extends ZipEntry> zipEntries = zipFile.entries(); 
        ZipEntry zipEntry = zipEntries.nextElement();
        if (zipEntries.hasMoreElements()) {
            throw new MifosRuntimeException(
                    "only expecting one file entry per dataSet zip archive");
        }
        BufferedInputStream src =
            new BufferedInputStream(zipFile.getInputStream(zipEntry));
        BufferedOutputStream dest =
            new BufferedOutputStream(new FileOutputStream(tempFile), bufferSize);

        int count;
        while (true) {
            count = src.read(data, 0, bufferSize);
            if (count == -1) {
                break;
            }
            dest.write(data, 0, count);
        }
        dest.close();
        src.close();
        zipFile.close();
        
        return tempFile.toURI().toURL();
    }
    
}
