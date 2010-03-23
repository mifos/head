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

package org.mifos.test.framework.util;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.dbunit.DatabaseUnitException;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Convenience wrapper for DbUnit's FlatXmlDataSet. Provides a simple
 * interface for clearing tables and creating database records.
 * <p/>
 * Usage:
 * <p/>
 * SimpleDataSet simpleDataSet = new SimpleDataSet();
 * simpleDataSet.row("tableTwo", "id=13", "column1=value1", "column2=3.1415", "column3=[null]");
 * simpleDataSet.row("tableTwo", "id=14", "column1=anotherValue", "column2=2.178", "column3=55");
 * simpleDataSet.row("tableThree", "id=1", "fooColumn=something", "barColumn=2");
 * simpleDataSet.clearTable("tableToBeCleared");
 * <p/>
 * simpleDataSet.insert(dataSource);
 * <p/>
 * or
 * <p/>
 * String flatXmlDataSetString = simpleDataSet.toString();
 * <p/>
 * Note that all data in a table will be removed before the new data is inserted.
 * Not supplying name value pairs results in the table being emptied.
 * <p/>
 * The first row of the DataSet must specify all columns; for null values,
 * specify "columnFoo=[null]" if you want columnFoo to be null.
 * <p/>
 * Limitations: error reporting is primitive. Missing columns get null values,
 * following DbUnit's FlatXmlDataSet's limitations. '=' characters are not allowed in
 * field names or values because the parser is primitive. '"' characters must be
 * escaped.
 *
 *
 */
public class SimpleDataSet {

    private final List<Row> rows;

    public SimpleDataSet() {
        this.rows = new ArrayList<Row>();
    }

    /**
     * Adds a database row entry to the data set. Accepts 0 or more
     * @param columnAndValuePairs to insert into the database. Database
     * table is cleared on insertion of the first row.
     *
     * @param tableName  the table name that the data should be inserted into.
     */
    public void row(String tableName, String...columnAndValuePairs) {
        rows.add(new Row(tableName, columnAndValuePairs));
    }

    /**
     * Clears specified table. Only necessary if you want to clear a table
     * without inserting new rows.
     *
     * @param tableName  the table name that the data should be inserted into.
     *
     */
    public void clearTable(String tableName) {
        rows.add(new Row(tableName));
    }

    /**
     * Returns a DbUnit FlatXmlDataSet string representation of the data set.
     */

    @SuppressWarnings("PMD.InsufficientStringBufferDeclaration") // test method doesn't need performance optimization yet
    public String toString() {
        StringBuffer dataSet = new StringBuffer();
        dataSet.append("<dataset>\n");
        for (Row row : this.rows) {
            dataSet.append(row.toString());
        }
        dataSet.append("</dataset>");
        return dataSet.toString();
    }

    /**
     * Returns a DbUnit data set.
     */
    public IDataSet getDataSet() throws DataSetException, IOException {
        return new DatabaseTestUtils().getXmlDataSet(this.toString());
    }

    /**
     * Converts the data set to a FlatXmlDataSet representation and inserts it into
     * the dataSource.
     *
     * @param dataSource
     * @throws DataSetException
     * @throws IOException
     * @throws SQLException
     * @throws DatabaseUnitException
     */
    public void insert(DriverManagerDataSource dataSource) throws DataSetException, IOException, SQLException, DatabaseUnitException {
            (new DatabaseTestUtils()).cleanAndInsertDataSet(this.toString(), dataSource);
    }

    private static class Row {

        private static final String NAME_VALUE_PAIR_SEPARATOR = "=";
        private final String tableName;
        private final List<ColumnAndValuePair> columnAndValuePairs;

        public Row(String tableName, String...columnAndValuePairs) {
            this.tableName = tableName;
            this.columnAndValuePairs = new ArrayList<ColumnAndValuePair>();
            for (String columnAndValuePairString:columnAndValuePairs ) {
                this.columnAndValuePairs.add(this.getColumnAndValuePair(columnAndValuePairString));
            }
        }

        public String getTableName() {
            return tableName;
        }

        public List<ColumnAndValuePair> getColumnAndValuePairs() {
            return new ArrayList<ColumnAndValuePair>(columnAndValuePairs);
        }

        private ColumnAndValuePair getColumnAndValuePair(
                String columnAndValuePairString) {
            String[] tokens = columnAndValuePairString.split(NAME_VALUE_PAIR_SEPARATOR);
            return new ColumnAndValuePair(tokens[0], tokens[1]);
        }

        @SuppressWarnings("PMD.InsufficientStringBufferDeclaration") // test method doesn't need performance optimization yet
        public String toString() {
            StringBuffer rowDataSet = new StringBuffer();
            rowDataSet.append('<');
            rowDataSet.append(this.tableName);
            rowDataSet.append(' ');
            for (ColumnAndValuePair columnAndValuePair: this.columnAndValuePairs) {
                rowDataSet.append(columnAndValuePair.toString());
            }
            rowDataSet.append("/>\n");
            return rowDataSet.toString();
        }
    }

    private static class ColumnAndValuePair {

        private final String column;
        private final String value;

        public ColumnAndValuePair(String column, String value) {
            this.column = column;
            this.value = value;
        }

        public String getColumn() {
            return column;
        }

        public String getValue() {
            return value;
        }

        @SuppressWarnings("PMD.InsufficientStringBufferDeclaration") // test method doesn't need performance optimization yet
        public String toString() {
            StringBuffer columnValuePairDataSet = new StringBuffer();
            columnValuePairDataSet.append(this.column);
            columnValuePairDataSet.append("=\"");
            columnValuePairDataSet.append(this.value);
            columnValuePairDataSet.append("\" ");
            return columnValuePairDataSet.toString();
        }

    }

}
