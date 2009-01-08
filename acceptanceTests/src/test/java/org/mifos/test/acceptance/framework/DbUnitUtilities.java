/**
 * 
 */
package org.mifos.test.acceptance.framework;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
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
import org.mifos.core.MifosRuntimeException;
import org.mifos.test.acceptance.collectionsheet.DbUnitResource;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testng.Assert;

/**
 * Utility methods for operating on dbunit data.
 *
 */
public class DbUnitUtilities {
    private static final Log LOG = LogFactory.getLog(DbUnitUtilities.class);
    
    // Below are sets of columns to ignore when calling verify table methods
    // These could be refactored to better encapsulate this information,
    // perhaps with a DatabaseTable class of some kind
    static Map<String, String[]> columnsToIgnore = new HashMap<String, String[]>();
    static {
        columnsToIgnore.put("ACCOUNT_PAYMENT", new String[] { "payment_id","payment_date" });
        columnsToIgnore.put("ACCOUNT_TRXN", new String[] { "account_trxn_id","created_date","action_date","payment_id" });        
        columnsToIgnore.put("FINANCIAL_TRXN", new String[] { "trxn_id","action_date", "account_trxn_id","balance_amount","posted_date" });        
        columnsToIgnore.put("LOAN_ACTIVITY_DETAILS", new String[] { "id","created_date" });        
        columnsToIgnore.put("LOAN_SCHEDULE", new String[] { "id","payment_date" });        
        columnsToIgnore.put("LOAN_TRXN_DETAIL", new String[] { "account_trxn_id" });        
    }

    public static void verifyTable(String tableName, IDataSet databaseDataSet, IDataSet expectedDataSet) throws DataSetException,
            DatabaseUnitException {
        
        Assert.assertNotNull(columnsToIgnore.get(tableName), "Didn't find requested table [" + tableName + "] in columnsToIgnore map.");
        ITable expectedTable = expectedDataSet.getTable(tableName);
        ITable actualTable = databaseDataSet.getTable(tableName);
        actualTable = DefaultColumnFilter.includedColumnsTable(actualTable, 
                expectedTable.getTableMetaData().getColumns());
        Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, columnsToIgnore.get(tableName));
    }

    public static void verifySortedTable(String tableName, IDataSet databaseDataSet, 
            IDataSet expectedDataSet, String[] sortingColumns) throws DataSetException,
    DatabaseUnitException {

        Assert.assertNotNull(columnsToIgnore.get(tableName), "Didn't find requested table [" + tableName + "] in columnsToIgnore map.");
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
        
        Assertion.assertEqualsIgnoreCols(expectedTable, actualTable, columnsToIgnore.get(tableName));
    }
    
    public static void printTable(ITable table) throws DataSetException {
       TableFormatter formatter = new TableFormatter();
       LOG.debug(formatter.format(table));
    }    
    
    public static void loadDataFromFile(String filename, DriverManagerDataSource dataSource) 
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

    public static IDataSet getDataSetFromFile(String filename) throws IOException, DataSetException {
        boolean enableColumnSensing = true;
        URL url = DbUnitResource.getInstance().getUrl(filename);
        if (url == null) {
            throw new MifosRuntimeException("Couldn't find file:" + filename);
        }
        return new FlatXmlDataSet(url,false,enableColumnSensing);
    }

    
}
