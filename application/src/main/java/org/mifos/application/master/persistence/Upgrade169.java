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

/*
 * Upgrade169 is an upgrade that cleans up remaining custom field related
 * entries, sets all label text in the database to null so that these
 * labels can serve as overrides for properties file based localized
 * labels, and introduces a locale for Spanish and French.
 */
public class Upgrade169 extends Upgrade {

    public Upgrade169() {
        super(169);
    }

    private short getLookupIdForSupporteLanguageId(Connection connection, int languageId) throws SQLException {

        short lookupId = 0;
        Statement statement = connection.createStatement();
        try {
            ResultSet results = statement.executeQuery("select LOOKUP_ID from LANGUAGE where LANG_ID = " + languageId);
            if (!results.next()) {
                throw new SystemException(SystemException.DEFAULT_KEY, "Query failed on table LANGUAGE for LANG_ID: "
                        + languageId);
            }
            lookupId = results.getShort(1);

        } finally {
            statement.close();
        }
        return lookupId;

    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        execute(connection, "DELETE FROM PERSONNEL_CUSTOM_FIELD WHERE PERSONNEL_ID = 1");
        /* Clean up unused custom field entities and labels */
        execute(connection, "DELETE FROM  LOOKUP_LABEL WHERE LABEL_ID = 61 AND ENTITY_NAME = \'CustomField1\'");
        execute(connection, "DELETE FROM  LOOKUP_LABEL WHERE LABEL_ID = 63 AND ENTITY_NAME = \'CustomField2\'");
        execute(connection, "DELETE FROM  LOOKUP_LABEL WHERE LABEL_ID = 65 AND ENTITY_NAME = \'CustomField3\'");

        execute(connection,
                "DELETE FROM  LOOKUP_VALUE_LOCALE WHERE LOOKUP_VALUE_ID = 123 AND LOOKUP_VALUE = \'CustomField1\'");
        execute(connection,
                "DELETE FROM  LOOKUP_VALUE_LOCALE WHERE LOOKUP_VALUE_ID = 125 AND LOOKUP_VALUE = \'CustomField2\'");
        execute(connection,
                "DELETE FROM  LOOKUP_VALUE_LOCALE WHERE LOOKUP_VALUE_ID = 127 AND LOOKUP_VALUE = \'CustomField3\'");

        execute(connection, "DELETE FROM  LOOKUP_VALUE WHERE LOOKUP_ID = 62 AND ENTITY_ID = 31");
        execute(connection, "DELETE FROM  LOOKUP_VALUE WHERE LOOKUP_ID = 63 AND ENTITY_ID = 32");
        execute(connection, "DELETE FROM  LOOKUP_VALUE WHERE LOOKUP_ID = 64 AND ENTITY_ID = 33");

        execute(connection,
                "DELETE FROM  LOOKUP_ENTITY WHERE ENTITY_ID = 31 AND ENTITY_NAME = \'PersonnelCustomField1\'");
        execute(connection,
                "DELETE FROM  LOOKUP_ENTITY WHERE ENTITY_ID = 32 AND ENTITY_NAME = \'PersonnelCustomField2\'");
        execute(connection,
                "DELETE FROM  LOOKUP_ENTITY WHERE ENTITY_ID = 33 AND ENTITY_NAME = \'PersonnelCustomField3\'");

        /*
         * set all lookup labels to be null so that text will come from resource
         * bundles
         */
        execute(connection, "UPDATE LOOKUP_LABEL SET ENTITY_NAME = NULL");

        /* Spanish locale support */
        execute(connection,
                "INSERT INTO COUNTRY(COUNTRY_ID,COUNTRY_NAME,COUNTRY_SHORT_NAME) VALUES(8,\'Spain\',\'ES\')");
        int lookup_id = insertLookupValue(connection, 74);
        execute(connection,
                "INSERT INTO LANGUAGE(LANG_ID,LANG_NAME,LANG_SHORT_NAME,LOOKUP_ID) VALUES(3,\'Spanish\',\'es\',"
                        + lookup_id + ")");
        execute(connection,
                "INSERT INTO SUPPORTED_LOCALE(LOCALE_ID,COUNTRY_ID,LANG_ID,LOCALE_NAME,DEFAULT_LOCALE) VALUES(3,8,3,\'Spanish\',0)");

        /* French locale support */
        execute(connection,
                "INSERT INTO COUNTRY(COUNTRY_ID,COUNTRY_NAME,COUNTRY_SHORT_NAME) VALUES(9,\'France\',\'FR\')");
        lookup_id = insertLookupValue(connection, 74);
        execute(connection,
                "INSERT INTO LANGUAGE(LANG_ID,LANG_NAME,LANG_SHORT_NAME,LOOKUP_ID) VALUES(4,\'French\',\'fr\',"
                        + lookup_id + ")");
        execute(connection,
                "INSERT INTO SUPPORTED_LOCALE(LOCALE_ID,COUNTRY_ID,LANG_ID,LOCALE_NAME,DEFAULT_LOCALE) VALUES(4,9,4,\'French\',0)");

        upgradeVersion(connection);
    }

}
