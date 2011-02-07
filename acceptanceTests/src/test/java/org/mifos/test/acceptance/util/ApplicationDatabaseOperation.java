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

    public void updateCustomerState(String statusID, String inUse) throws SQLException {
        getStatement().executeUpdate("UPDATE CUSTOMER_STATE l SET CURRENTLY_IN_USE= " + inUse + " WHERE l.STATUS_ID=" + statusID);
        closeConnection();
    }

    public void  updateLSIM(int lsimValue) throws SQLException {
        getStatement().executeUpdate("update config_key_value_integer set configuration_value=" + lsimValue + " where configuration_key='repaymentSchedulesIndependentOfMeetingIsEnabled'");
        closeConnection();
    }

    public void updateGapBetweenDisbursementAndFirstMeetingDate(int gap) throws SQLException {
        getStatement().executeUpdate("update config_key_value_integer set configuration_value=" + gap + " where configuration_key='minDaysBetweenDisbursalAndFirstRepaymentDay'");
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

    public boolean doesFeeExist(String feeName) throws SQLException {
        return doesEntityExist("select count(fee_name) from fees where fee_name = '" + feeName + "';");
    }

    public boolean doesDecliningPrincipalBalanceExist() throws SQLException {
        return doesEntityExist("select * from lookup_value where lookup_name='InterestTypes-DecliningPrincipalBalance';");
    }

    public boolean doesHolidayExist(String holidayName) throws SQLException {
        return doesEntityExist("select * from holiday where holiday_name='" + holidayName + "';");

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

    public void insertDecliningPrincipalBalanceInterestType() throws SQLException {
        getStatement().execute("insert into lookup_value(lookup_id,entity_id,lookup_name) values((select max(lv.lookup_id)+1 from lookup_value lv), 37, 'InterestTypes-DecliningPrincipalBalance');");
        getStatement().execute("insert into interest_types (interest_type_id, lookup_id, category_id, descripton) values(5,(select lookup_id from lookup_value where entity_id =37 and lookup_name='InterestTypes-DecliningPrincipalBalance'),1,'InterestTypes-DecliningPrincipalBalance');");
        closeConnection();
    }
}
