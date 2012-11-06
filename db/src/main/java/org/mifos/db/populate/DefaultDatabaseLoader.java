package org.mifos.db.populate;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.core.MifosResourceUtil;
import org.mifos.framework.persistence.SqlExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultDatabaseLoader {
    private final String[] BASE_DB_SCRIPTS = { "sql/base-schema.sql", "sql/base-data.sql", "sql/init_mifos_password.sql" };
    private final String[] DB_PENTAHO_DW_SCRIPTS = { "sql/load_mifos_datawarehouse.sql",
            "sql/load_mifos_datawarehouse_stored_procedures.sql", "sql/load_ppi_poverty_lines.sql",
            "sql/load_dw_ppi_survey.sql" };
    
    private String dbName;
    private String dbPentahoDW;
    
    private Connection connection;
    
    private static Logger logger = LoggerFactory.getLogger(DefaultDatabaseLoader.class);
    
    public DefaultDatabaseLoader(Connection connection, String dbName,
            String dbPentahoDW) {
        this.connection = connection;
        this.dbName = dbName;
        this.dbPentahoDW = dbPentahoDW;
    }

    public void createDatabase() throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            if (!"".equals(dbName)) {
                statement.execute(String.format("create database if not exists %s", dbName));
            }
            if (!"".equals(dbPentahoDW)) {
                statement.execute(String.format("create database if not exists %s", dbPentahoDW));
            }
            statement.close();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }
    
    public void populateDatabase() throws SQLException, IOException {
        loadDbFromSqlFiles(dbName, BASE_DB_SCRIPTS);
        loadDbFromSqlFiles(dbPentahoDW, DB_PENTAHO_DW_SCRIPTS);
    }
    
    private void loadDbFromSqlFiles(String dbToLoadName, String[] fileNames) throws SQLException, IOException {
        if (!"".equals(dbToLoadName)) {
            connection.setCatalog(dbToLoadName);
            ResultSet res = connection.getMetaData().getTables(null, null, null, new String[] { "TABLE" });
            if (res.next()) {
                logger.info("Database " + dbToLoadName + " already populated");
            } else {
                InputStream[] inputStreams = MifosResourceUtil.getSQLFilesAsStreams(fileNames);
                SqlExecutor.executeMultipleFiles(inputStreams, connection);
            }
        }
    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public String getDbPentahoDW() {
        return dbPentahoDW;
    }

    public void setDbPentahoDW(String dbPentahoDW) {
        this.dbPentahoDW = dbPentahoDW;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

}
