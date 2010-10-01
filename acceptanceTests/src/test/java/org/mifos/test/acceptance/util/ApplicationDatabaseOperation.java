package org.mifos.test.acceptance.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@ContextConfiguration(locations={"classpath:test-context.xml", "classpath:ui-test-context.xml"})
public class ApplicationDatabaseOperation {

    @Autowired
    private DriverManagerDataSource dataSource;
    private Connection connection;


    public void  updateLSIM(int lsimValue) throws SQLException {
        getStatement().executeUpdate("update config_key_value_integer set configuration_value=" + lsimValue + " where configuration_key='repaymentSchedulesIndependentOfMeetingIsEnabled'");
    }

    public boolean isUserAlreadyExist() {
        return false;
    }

    public boolean doesBranchOfficeExist(String officeName, int officeType, String shortName) throws SQLException {
        boolean officeExist = getStatement().execute("select * from office where " +
                "office_level_id='" + officeType + "' and " +
                "office_short_name='" + shortName + "' and " +
                "display_name='" + officeName + "';");
        closeConnection();
        return officeExist;
    }


    public boolean doesSystemUserExist(String userLoginName, String userName, String officeName) throws SQLException {
        boolean userExist = getStatement().execute("select * from personnel where display_name = '" + userName + "' and login_name = '" + userLoginName + "';");
        closeConnection();
        return userExist;
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

    public boolean doesClientExist(String clientName, String officeName) throws SQLException {
        boolean clientExist = getStatement().execute("select * from customer where display_name='" + clientName + "';");
        closeConnection();
        return clientExist;
    }
}
