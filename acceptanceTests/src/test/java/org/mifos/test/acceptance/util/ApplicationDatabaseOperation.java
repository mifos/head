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

    public void cleanBatchJobTables() throws SQLException {
        getStatement().executeUpdate("truncate table BATCH_STEP_EXECUTION_CONTEXT");
        getStatement().executeUpdate("truncate table BATCH_STEP_EXECUTION");
        getStatement().executeUpdate("truncate table BATCH_STEP_EXECUTION_SEQ");
        getStatement().executeUpdate("truncate table BATCH_JOB_EXECUTION_CONTEXT");
        getStatement().executeUpdate("truncate table BATCH_JOB_EXECUTION");
        getStatement().executeUpdate("truncate table BATCH_JOB_PARAMS");
        getStatement().executeUpdate("truncate table BATCH_JOB_INSTANCE");
        getStatement().executeUpdate("truncate table BATCH_JOB_SEQ");
        closeConnection();
    }

    public void updateCustomerState(String statusID, String inUse) throws SQLException {
        getStatement().executeUpdate("UPDATE customer_state l SET currently_in_use= " + inUse + " WHERE l.status_id=" + statusID);
        closeConnection();
    }

    public void  updateLSIM(int lsimValue) throws SQLException {
        getStatement().executeUpdate("update config_key_value set configuration_value=" + lsimValue + " where configuration_key='repaymentSchedulesIndependentOfMeetingIsEnabled'");
        closeConnection();
    }

    public void  updateGLIM(int glimValue) throws SQLException {
        getStatement().executeUpdate("update config_key_value set configuration_value=" + glimValue + " where configuration_key='loanIndividualMonitoringIsEnabled'");
        closeConnection();
    }

    public void updateGapBetweenDisbursementAndFirstMeetingDate(int gap) throws SQLException {
        getStatement().executeUpdate("update config_key_value set configuration_value=" + gap + " where configuration_key='minDaysBetweenDisbursalAndFirstRepaymentDay'");
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

    public boolean deosQuestionResponseForLoanExist(String loanID, String event, String question, String response) throws SQLException {
        return doesEntityExist("SELECT count(*) FROM question_group_response as qqr, sections_questions as sq, questions as q WHERE qqr.response = \""+response+"\" AND qqr.sections_questions_id = sq.id AND sq.question_id = q.question_id AND q.question_text = \""+question+"\""
                +" AND qqr.question_group_instance_id in ("
                    +"SELECT qqi.id FROM question_group_instance as qqi, account as a WHERE qqi.entity_id = a.account_id AND a.global_account_num = \""+loanID+"\""
                    +" AND qqi.event_source_id = ("
                        +"SELECT es.id FROM event_sources as es WHERE es.description = \""+event+"\""
                    +")"
                +");"
        );
    }

    public boolean deosQuestionResponseForSavingsExist(String savingsID, String event, String question, String response) throws SQLException {
        return doesEntityExist("SELECT count(*) FROM question_group_response as qqr, sections_questions as sq, questions as q WHERE qqr.response = \""+response+"\" AND qqr.sections_questions_id = sq.id AND sq.question_id = q.question_id AND q.question_text = \""+question+"\""
                +" AND qqr.question_group_instance_id in ("
                    +"SELECT qqi.id FROM question_group_instance as qqi, account as a WHERE qqi.entity_id = a.account_id AND a.global_account_num = \""+savingsID+"\""
                    +" AND qqi.event_source_id = ("
                        +"SELECT es.id FROM event_sources as es WHERE es.description = \""+event+"\""
                    +")"
                +");"
        );
    }

    public boolean deosQuestionResponseForClientExist(String clientID, String event, String question, String response) throws SQLException {
        return doesEntityExist("SELECT count(*) FROM question_group_response as qqr, sections_questions as sq, questions as q WHERE qqr.response = \""+response+"\" AND qqr.sections_questions_id = sq.id AND sq.question_id = q.question_id AND q.question_text = \""+question+"\""
                +" AND qqr.question_group_instance_id in ("
                    +"SELECT qqi.id FROM question_group_instance as qqi, customer as c WHERE qqi.entity_id = c.customer_id AND c.global_cust_num = \""+clientID+"\""
                    +" AND qqi.event_source_id = ("
                        +"SELECT es.id FROM event_sources as es WHERE es.description = \""+event+"\""
                    +")"
                +");"
        );
    }
}
