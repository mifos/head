/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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

package org.mifos.test.framework.util;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.SQLException;

import org.dbunit.Assertion;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ReplacementDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DatabaseTestUtils {

    // This method takes a variable number of String arguments - the names of the tables to clear.
    // Note: to avoid database constraint violations, list tables in reverse order you want them to be cleared.
    @SuppressWarnings("PMD.InsufficientStringBufferDeclaration") // test method doesn't need performance optimization yet
    public void deleteDataFromTables(DriverManagerDataSource dataSource, String...tableNames) 
    throws IOException, DataSetException, SQLException, DatabaseUnitException {
        StringBuffer dataSet = new StringBuffer();
        dataSet.append("<dataset>");
        for (String tableName:tableNames) {
            dataSet.append("<" + tableName + "/>");
        }
        dataSet.append("</dataset>");
        cleanAndInsertDataSet(dataSet.toString(), dataSource);
    }

    /**
     * Execute a DbUnit CLEAN_INSERT. Parameter xmlString must be formatted as a DBUnit
     * xml dataset. This method can be safely invoked inside a Spring-managed transaction.
     * @param xmlString
     * @param dataSource
     * @throws IOException
     * @throws DataSetException
     * @throws SQLException
     * @throws DatabaseUnitException
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    //Rationale: You cannot define new local variables in the try block because the finally block must reference it.
    public void cleanAndInsertDataSet(String xmlString, DriverManagerDataSource dataSource) 
                    throws IOException, DataSetException, SQLException, DatabaseUnitException {
        StringReader dataSetXmlStream = new StringReader(xmlString);
        IDataSet dataSet = new FlatXmlDataSet(dataSetXmlStream);
        cleanAndInsertDataSet(dataSource, dataSet);
    }

    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    //Rationale: You cannot define new local variables in the try block because the finally block must reference it.
    public void cleanAndInsertDataSetWithColumnSensing(String xmlString, DriverManagerDataSource dataSource) 
                    throws IOException, DataSetException, SQLException, DatabaseUnitException {
        IDataSet dataSet = getXmlDataSet(xmlString);
        cleanAndInsertDataSet(dataSource, dataSet);
    }

    private void cleanAndInsertDataSet(DriverManagerDataSource dataSource,
            IDataSet dataSet) throws DatabaseUnitException, SQLException {
        Connection jdbcConnection = null;
        ReplacementDataSet replacementDataSet = getDataSetWithNullsReplaced(dataSet);        
        try {
            jdbcConnection = DataSourceUtils.getConnection(dataSource);
            IDatabaseConnection databaseConnection = new DatabaseConnection(jdbcConnection);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, replacementDataSet);
        }
        finally {
            if (null != jdbcConnection) {
                jdbcConnection.close();
            }
            DataSourceUtils.releaseConnection(jdbcConnection, dataSource);
        }
    }

    /** 
     * given an XML string, returns an data set with [null] replaced by actual null objects.
     * @param xmlString
     * @return
     * @throws IOException
     * @throws DataSetException
     */
    public IDataSet getXmlDataSet(String xmlString) throws IOException, DataSetException {
        boolean dtdMetadata = false;
        boolean enableColumnSensing = true;
        IDataSet xmlDataSet = new FlatXmlDataSet(createTempFile(xmlString), dtdMetadata, enableColumnSensing);
        return this.getDataSetWithNullsReplaced(xmlDataSet);
    }

    /**
     * Given a data set, return a data set with [null] replaced by actual null objects
     * @param dataSet
     * @return
     */
    public ReplacementDataSet getDataSetWithNullsReplaced(IDataSet dataSet) {
        ReplacementDataSet replacementDataSet = new ReplacementDataSet(dataSet); 
        replacementDataSet.addReplacementObject("[null]", null);
        return replacementDataSet;
    }

    private File createTempFile(String xmlString) throws IOException {
        File tempFile = File.createTempFile("simpleDataSetTemp", ".xml");
        tempFile.deleteOnExit();
        BufferedWriter out = new BufferedWriter(new FileWriter(tempFile));
        try {
           out.write(xmlString);
        } finally {
           out.close();
        }
        return tempFile;
    }
    
    /**
     * Verify that a database table matches a dataSet table. dataSetXml must be formatted as a DBUnit
     * xml dataset. This method can be safely invoked inside a Spring-managed transaction.
     * @param dataSetXml
     * @param tableName
     * @param dataSource
     * @throws Exception
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException") // one of the dependent methods throws Exception
    public void verifyTable(String dataSetXml, String tableName, DriverManagerDataSource dataSource) throws Exception {
        Connection jdbcConnection = null;
        StringReader dataSetXmlStream = new StringReader(dataSetXml);
        try {
            jdbcConnection = DataSourceUtils.getConnection(dataSource);
            IDatabaseTester databaseTester = new DataSourceDatabaseTester(dataSource);
            IDatabaseConnection databaseConnection = databaseTester.getConnection();
            IDataSet databaseDataSet = databaseConnection.createDataSet();
            ITable actualTable = databaseDataSet.getTable(tableName);
            IDataSet expectedDataSet = new FlatXmlDataSet(dataSetXmlStream);
            ITable expectedTable = expectedDataSet.getTable(tableName);
            Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, new String[] { "id" });   
        }
        finally {
            if (null != jdbcConnection) {
                jdbcConnection.close();
            }
            DataSourceUtils.releaseConnection(jdbcConnection, dataSource);
        }
    }    
    
    /**
     * Return the current contents of the specified tables as a DBUnit dataset as XML string. 
     * This method can be invoked safely inside a Spring-managed transaction.
     * 
     * @param dataSource
     * @param tableNames variable parameter list of table names
     * @return XML string containing the current contents of the specified tables
     * @throws IOException
     * @throws DataSetException
     * @throws SQLException
     * @throws DatabaseUnitException
     */
    @SuppressWarnings("PMD.DataflowAnomalyAnalysis")
    //Rationale: You cannot define new local variables in the try block because the finally block must reference it.
    public String saveTables(DriverManagerDataSource dataSource, String...tableNames) 
                    throws IOException, DataSetException, SQLException, DatabaseUnitException {
        Connection jdbcConnection = null;
        try {
            jdbcConnection = DataSourceUtils.getConnection(dataSource);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            FlatXmlDataSet.write(new DatabaseConnection(jdbcConnection).createDataSet(tableNames), 
                                 stream);
            return stream.toString();
        }
        finally {
            if (null != jdbcConnection) {
                jdbcConnection.close();
            }
            DataSourceUtils.releaseConnection(jdbcConnection, dataSource);
        }
    }
}
