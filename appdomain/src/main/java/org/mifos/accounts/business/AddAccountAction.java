/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import org.mifos.application.master.business.LookUpEntity;
import org.mifos.config.Localization;
import org.mifos.framework.persistence.Upgrade;

public class AddAccountAction extends Upgrade {

    private final String lookupValueKey;
    private final int action;
    private final String message;
    protected static final String WRONG_KEY_FORMAT = "The key format must be AccountAction-...";
    protected static final String keyFormat = "AccountAction-";

    /*
     * This constructor must be used after version 174. The lookupValueKey must
     * in the format AccountAction-...
     */
    public AddAccountAction(int action, String lookupValueKey) {
        super();
        if (!validateLookupValueKey(keyFormat, lookupValueKey)) {
            throw new RuntimeException(WRONG_KEY_FORMAT);
        }
        this.action = action;
        this.lookupValueKey = lookupValueKey;
        this.message = null;
    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        int lookupEntity = LookUpEntity.ACCOUNT_ACTION;

        int lookupId = insertLookupValue(connection, lookupEntity, lookupValueKey);
        insertMessage(connection, lookupId, Localization.ENGLISH_LOCALE_ID, message);
        addAction(connection, action, lookupId);
    }

    private void addAction(Connection connection, int actionId, int lookupId) throws SQLException {
        PreparedStatement statement = connection
                .prepareStatement("insert into account_action(account_action_id,lookup_id) values (?,?)");
        statement.setInt(1, actionId);
        statement.setInt(2, lookupId);
        statement.executeUpdate();
        statement.close();
    }
}
