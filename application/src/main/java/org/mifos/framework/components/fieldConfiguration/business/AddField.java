/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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

package org.mifos.framework.components.fieldConfiguration.business;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;

public class AddField extends Upgrade {

    private final int newFieldId;
    private final String fieldName;
    private final EntityType type;
    private final boolean isMandatory;
    private final boolean isHidden;

    /*
     * TODO: should we just be letting the database assign the field ID? I see
     * it is auto-increment. I think the combination of fieldName and type is
     * probably sufficient to identify the records.
     */

    public AddField(int higherVersion, int newFieldId, String fieldName, EntityType type, boolean isMandatory,
            boolean isHidden) {
        super(higherVersion);
        this.newFieldId = newFieldId;
        this.fieldName = fieldName;
        this.type = type;
        this.isMandatory = isMandatory;
        this.isHidden = isHidden;
    }

    @Override
    public void upgrade(Connection connection, DatabaseVersionPersistence databaseVersionPersistence)
            throws IOException, SQLException {
        PreparedStatement statement = connection
                .prepareStatement("INSERT INTO FIELD_CONFIGURATION(FIELD_CONFIG_ID,FIELD_NAME,"
                        + "  ENTITY_ID,MANDATORY_FLAG,HIDDEN_FLAG)" + "  VALUES(?,?,?,?,?)");
        statement.setInt(1, newFieldId);
        statement.setString(2, fieldName);
        statement.setShort(3, type.getValue());
        statement.setShort(4, isMandatory ? (short) 1 : (short) 0);
        statement.setShort(5, isHidden ? (short) 1 : (short) 0);
        statement.executeUpdate();
        statement.close();
    }
}
