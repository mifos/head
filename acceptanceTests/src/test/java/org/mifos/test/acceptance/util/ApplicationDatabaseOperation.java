package org.mifos.test.acceptance.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;

import java.sql.SQLException;

@ContextConfiguration(locations={"classpath:test-context.xml", "classpath:ui-test-context.xml"})
public class ApplicationDatabaseOperation {

    @Autowired
    private DriverManagerDataSource dataSource;


    public void  updateLSIM(int lsimValue) throws SQLException {
        dataSource.getConnection().createStatement().executeUpdate("update config_key_value_integer set configuration_value=" + lsimValue + " where configuration_key='repaymentSchedulesIndependentOfMeetingIsEnabled'");
    }
}
