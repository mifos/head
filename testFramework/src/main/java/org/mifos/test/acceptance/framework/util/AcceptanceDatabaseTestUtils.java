package org.mifos.test.acceptance.framework.util;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class AcceptanceDatabaseTestUtils {

    public void deleteDataFromTable(String tableName, DriverManagerDataSource dataSource) throws IOException, DataSetException, SQLException, DatabaseUnitException {
        StringReader dataSetXmlStream = new StringReader("<dataset><" + tableName + "/></dataset>");
        IDataSet dataSet = new FlatXmlDataSet(dataSetXmlStream);
        IDatabaseConnection databaseConnection = new DatabaseDataSourceConnection(dataSource);
        DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
    }
}
