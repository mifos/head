package org.mifos.db.populate;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mifos.core.MifosResourceUtil;
import org.mifos.framework.persistence.SqlExecutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/test-dbContext.xml"})
public class DefaultDatabaseLoaderIntegrationTest {
    
    private Connection connection;
    
    private DefaultDatabaseLoader defaultDatabaseLoader;
    
    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private String dbName;
    
    @Autowired
    private String dbPentahoDW;
    
    private static final String INTEGRATION_TEST_DATA = "sql/integration_test_data.sql"; 
        
    @Before
    public void setup() throws SQLException {
        connection = dataSource.getConnection();
        defaultDatabaseLoader = new DefaultDatabaseLoader(connection, dbName, dbPentahoDW);
    }
    
    @After
    public void tearDown() throws SQLException, IOException {
        dropDatabases();
        defaultDatabaseLoader.createDatabase();
        defaultDatabaseLoader.populateDatabase();
        connection.setCatalog(dbName);
        SqlExecutor.execute(MifosResourceUtil.getClassPathResourceAsStream(INTEGRATION_TEST_DATA), connection);
    }
    
    @Test
    public void createDbWhenDbNotExist() throws SQLException {
        dropDatabases();
        defaultDatabaseLoader.createDatabase();
        Assert.assertEquals(defaultDatabaseLoader.getDbName(), connection.getCatalog());
        if (!"".equals(dbPentahoDW)) {
            connection.setCatalog(dbPentahoDW);
            Assert.assertEquals(defaultDatabaseLoader.getDbPentahoDW(), connection.getCatalog());
        }
    }
    
    @Test(expected=SQLException.class)
    public void populateDbWhenDbNotExist() throws SQLException, IOException {
        dropDatabases();
        defaultDatabaseLoader.populateDatabase();
    }
    
    private void dropDatabases() throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("drop database if exists " + defaultDatabaseLoader.getDbName());
        if (!"".equals(dbPentahoDW)) {
            statement.execute("drop database if exists " + defaultDatabaseLoader.getDbPentahoDW());
        }
    }
    
    @Test
    public void populateDbWhenDbExist() throws SQLException, IOException {
        defaultDatabaseLoader.createDatabase();
        defaultDatabaseLoader.populateDatabase();
        ResultSet res = connection.getMetaData().getTables(null, null, null, new String[] { "TABLE" });
        Assert.assertTrue(res.next());
        if (!"".equals(dbPentahoDW)) {
            connection.setCatalog(dbPentahoDW);
            ResultSet resDW = connection.getMetaData().getTables(null, null, null, new String[] { "TABLE" });
            Assert.assertTrue(resDW.next());
        }
    }
}
