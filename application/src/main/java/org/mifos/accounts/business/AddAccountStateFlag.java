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

package org.mifos.accounts.business;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.framework.persistence.Upgrade;

public class AddAccountStateFlag extends Upgrade {

    /**
     * TODO: What is this? There must be a constant for it somewhere. Existing
     * flags have a status of either 10 (loan) or 15 (savings).
     */
    public static final int STATUS_10 = 10;

    private final int newFlagId;
    private final String description;

    private final Short locale;
    private final String message;
    private final String lookupValueKey;
    protected static final String wrongLookupValueKeyFormat = "The key format must be AccountFlags-...";
    protected static final String keyFormat = "AccountFlags-";

    /*
     * This constructor is used for version 174 and lower. And it must not be
     * used afterward
     */
    public AddAccountStateFlag(int higherVersion, int newFlagId, String description, Short locale, String message) {
        super(higherVersion);
        if (higherVersion > LOOKUP_VALUE_CHANGE_VERSION) {
            throw new RuntimeException(wrongConstructor);
        }
        this.newFlagId = newFlagId;
        this.description = description;
        this.locale = locale;
        this.message = message;
        this.lookupValueKey = " ";
    }

    /*
     * This constructor must be used after version 174. The lookupValueKey must
     * in the format AccountFlags-...
     */

    public AddAccountStateFlag(int higherVersion, int newFlagId, String description, String lookupValueKey) {
        super(higherVersion);
        if (!validateLookupValueKey(keyFormat, lookupValueKey)) {
            throw new RuntimeException(wrongLookupValueKeyFormat);
        }
        this.newFlagId = newFlagId;
        this.description = description;
        this.locale = MasterDataEntity.CUSTOMIZATION_LOCALE_ID;
        ;
        this.message = null;
        this.lookupValueKey = lookupValueKey;
    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        int lookupEntity = LookUpEntity.ACCOUNT_STATE_FLAG;

        int lookupId = insertLookupValue(connection, lookupEntity, lookupValueKey);
        insertMessage(connection, lookupId, locale, message);
        addFlag(connection, newFlagId, description, lookupId);
        upgradeVersion(connection);
    }

    private void addFlag(Connection connection, int newFlagId, String description, int lookupId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("INSERT INTO ACCOUNT_STATE_FLAG("
                + "  FLAG_ID,LOOKUP_ID,STATUS_ID,FLAG_DESCRIPTION,RETAIN_FLAG)" + "VALUES(?,?,?,?,?)");
        statement.setInt(1, newFlagId);
        statement.setInt(2, lookupId);
        statement.setInt(3, STATUS_10);
        statement.setString(4, description);
        boolean retain = false;
        statement.setInt(5, retain ? 1 : 0);
        statement.executeUpdate();
        statement.close();
    }
}
