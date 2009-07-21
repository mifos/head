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

package org.mifos.application.holiday.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.mifos.application.holiday.util.helpers.RepaymentRuleTypes;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.persistence.Upgrade;

public class AddRepaymentRule extends Upgrade {

    private final RepaymentRuleTypes type;
    private final short locale;
    private final String message;
    private final String lookupKey;
    protected static final String wrongLookupValueKeyFormat = "The key format must be RepaymentRule-...";
    protected static final String keyFormat = "RepaymentRule-";

    /*
     * This constructor is used for version 174 and lower. And it must not be
     * used afterward
     */
    public AddRepaymentRule(int higherVersion, RepaymentRuleTypes type, short locale, String message) {
        super(higherVersion);
        if (higherVersion > LOOKUP_VALUE_CHANGE_VERSION)
            throw new RuntimeException(wrongConstructor);
        this.type = type;
        this.locale = locale;
        this.message = message;
        this.lookupKey = " ";
    }

    /*
     * This constructor must be used after version 174. The lookupValueKey must
     * in the format RepaymentRule-...
     */
    public AddRepaymentRule(int higherVersion, RepaymentRuleTypes type, String lookupKey) {
        super(higherVersion);
        if (!validateLookupValueKey(keyFormat, lookupKey))
            throw new RuntimeException(wrongLookupValueKeyFormat);
        this.type = type;
        this.locale = MasterDataEntity.CUSTOMIZATION_LOCALE_ID;
        this.lookupKey = lookupKey;
        message = null;
    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        int lookupEntity = MifosLookUpEntity.REPAYMENT_RULE;

        int lookupId = insertLookupValue(connection, lookupEntity, lookupKey);
        insertMessage(connection, lookupId, locale, message);
        addRepaymentRule(connection, lookupId);

        upgradeVersion(connection);
    }

    private void addRepaymentRule(Connection connection, int lookupId) throws SQLException {
        PreparedStatement statement = connection.prepareStatement("insert into REPAYMENT_RULE"
                + "  (REPAYMENT_RULE_ID, REPAYMENT_RULE_LOOKUP_ID) " + "  VALUES(?,?)");
        statement.setShort(1, type.getValue());
        statement.setInt(2, lookupId);
        statement.executeUpdate();
        statement.close();
    }
}
