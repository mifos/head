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

package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.Upgrade;

/**
 * Upgrade167 is a conditional upgrade that removes default custom fields from
 * the database if there are no custom fields in use other than those required
 * by the default 'mifos' user and 'HO' office. If there are any custom fields
 * in use, then it just leaves all custom fields alone.
 */
public class Upgrade167 extends Upgrade {

    public Upgrade167() {
        super(167);
    }

    private int countRows(Connection connection, String tableName) throws SQLException {

        int numFields = 0;
        Statement statement = connection.createStatement();
        try {
            ResultSet results = statement.executeQuery("select count(*) from " + tableName);
            if (!results.next()) {
                throw new SystemException(SystemException.DEFAULT_KEY, "Query failed on table: " + tableName);
            }
            numFields = results.getInt(1);

        } finally {
            statement.close();
        }
        return numFields;

    }

    private boolean CustomFieldsHaveBeenCreated(Connection connection, String tableName, int expectedCount)
            throws SQLException {
        return countRows(connection, tableName) > expectedCount;
    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        if (!CustomFieldsHaveBeenCreated(connection, "PERSONNEL_CUSTOM_FIELD", 1)
                && !CustomFieldsHaveBeenCreated(connection, "OFFICE_CUSTOM_FIELD", 4)
                && !CustomFieldsHaveBeenCreated(connection, "ACCOUNT_CUSTOM_FIELD", 0)
                && !CustomFieldsHaveBeenCreated(connection, "CUSTOMER_CUSTOM_FIELD", 0)) {

            execute(connection, "DELETE FROM PERSONNEL_CUSTOM_FIELD WHERE PERSONNEL_ID = 1");
            execute(connection, "DELETE FROM OFFICE_CUSTOM_FIELD WHERE OFFICE_ID = 1");

            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 3");
            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 4");
            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 5");
            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 6");
            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 9");
            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 10");
            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 11");
            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 12");
            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 13");
            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 7");
            execute(connection, "DELETE FROM CUSTOM_FIELD_DEFINITION WHERE FIELD_ID = 8");

            execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 155");
            execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 156");
            execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 157");
            execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 158");
            execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 159");
            execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 160");
            execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 161");
            execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 162");
            execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 163");
            execute(connection, "DELETE FROM LOOKUP_VALUE_LOCALE WHERE LOOKUP_ID = 164");

            execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 155");
            execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 156");
            execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 157");
            execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 158");
            execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 159");
            execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 160");
            execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 161");
            execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 162");
            execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 163");
            execute(connection, "DELETE FROM LOOKUP_VALUE WHERE LOOKUP_ID = 164");

            execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 108");
            execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 110");
            execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 112");
            execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 114");
            execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 118");
            execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 120");
            execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 122");
            execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 124");
            execute(connection, "DELETE FROM LOOKUP_LABEL WHERE LABEL_ID = 126");

            execute(connection,
                    "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 58 AND ENTITY_NAME = \'ReplacementStatus\'");
            execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 59 AND ENTITY_NAME = \'GRTStaffId\'");
            execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 60 AND ENTITY_NAME = \'MeetingTime\'");
            execute(connection,
                    "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 61 AND ENTITY_NAME = \'DistanceFromBoToCenter\'");
            execute(connection,
                    "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 63 AND ENTITY_NAME = \'NoOfClientsPerGroup\'");
            execute(connection,
                    "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 64 AND ENTITY_NAME = \'NoOfClientsPerCenter\'");
            execute(connection,
                    "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 65 AND ENTITY_NAME = \'DistanceFromHoToBO\'");
            execute(connection, "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 66 AND ENTITY_NAME = \'ExternalLoanId\'");
            execute(connection,
                    "DELETE FROM LOOKUP_ENTITY WHERE ENTITY_ID = 67 AND ENTITY_NAME = \'ExternalSavingsId\'");

            execute(connection,
                    "UPDATE LOOKUP_ENTITY SET DESCRIPTION = \'External ID\' WHERE ENTITY_ID = 62 AND ENTITY_NAME = \'ExternalId\'");
        }
        upgradeVersion(connection);
    }

}
