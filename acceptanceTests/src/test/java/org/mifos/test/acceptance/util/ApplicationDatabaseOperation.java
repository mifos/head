package org.mifos.test.acceptance.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@ContextConfiguration(locations={"classpath:test-context.xml", "classpath:ui-test-context.xml"})
public class ApplicationDatabaseOperation {

    @Autowired
    private DriverManagerDataSource dataSource;
    private Connection connection;


    public void  updateLSIM(int lsimValue) throws SQLException {
        getStatement().executeUpdate("update config_key_value_integer set configuration_value=" + lsimValue + " where configuration_key='repaymentSchedulesIndependentOfMeetingIsEnabled'");
        closeConnection();
    }

    public boolean doesBranchOfficeExist(String officeName, int officeType, String shortName) throws SQLException {
        return doesEntityExist("select count(*) from office where " +
                "office_level_id='" + officeType + "' and " +
                "office_short_name='" + shortName + "' and " +
                "display_name='" + officeName + "';");
    }

    public boolean doesClientExist(String clientName, String officeName) throws SQLException {
        return doesEntityExist("select * from customer where display_name='" + clientName + "';");
    }

    public boolean doesSystemUserExist(String userLoginName, String userName, String officeName) throws SQLException {
        return doesEntityExist("select * from personnel where "+
                "display_name = '" + userName + "' and " +
                "login_name = '" + userLoginName + "';");
    }

    private boolean doesEntityExist(String entityCountQuery) throws SQLException {
        ResultSet resultSet = null;
        try{
            int noOfOffices = 0;
            resultSet = getStatement().executeQuery(entityCountQuery);
            if (resultSet.next()) {
                noOfOffices = resultSet.getInt(1);
            }
            resultSet.close();
            closeConnection();
            return (noOfOffices > 0);
        }
        finally {
            resultSet.close();
            closeConnection();
        }


    }

    private void closeConnection() throws SQLException {
        connection.close();
    }

    private Statement getStatement() throws SQLException {
        return getConnection().createStatement();
    }

    private Connection getConnection() throws SQLException {
        connection = dataSource.getConnection();
        return connection;
    }
}
