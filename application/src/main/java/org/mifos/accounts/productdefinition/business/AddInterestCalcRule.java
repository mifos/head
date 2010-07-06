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

package org.mifos.accounts.productdefinition.business;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.LookUpEntity;
import org.mifos.framework.persistence.Upgrade;

/* AddInterestCalcRule adds a new type of interest calculation method
 * e.g. Declining Balance - Equal Principal. This setting is used to
 * calculate interest with declining method, but with principal being
 * equal in all installments.
 */
public class AddInterestCalcRule extends Upgrade {

    private final int newRuleId;
    private final int categoryId;
    private final String lookupName;
    private final String description;

    private final Short locale;
    private final String message;
    protected static final String wrongLookupValueKeyFormat = "The key format must be InterestTypes-...";
    protected static final String keyFormat = "InterestTypes-";

    /*
     * This constructor is used for version 174 and lower. And it must not be
     * used afterward
     */
    public AddInterestCalcRule(int higherVersion, int newRuleId, int categoryId, String lookupName, String description,
            Short locale, String message) {
        super(higherVersion);
        if (higherVersion > LOOKUP_VALUE_CHANGE_VERSION) {
            throw new RuntimeException(WRONG_CONSTRUCTOR);
        }
        this.newRuleId = newRuleId;
        this.lookupName = lookupName;
        this.categoryId = categoryId;
        this.description = description;
        this.locale = locale;
        this.message = message;
    }

    /*
     * This constructor must be used after version 174. The lookupValueKey must
     * in the format InterestTypes-...
     */
    public AddInterestCalcRule(int higherVersion, int newRuleId, int categoryId, String lookupName, String description) {
        super(higherVersion);
        if (!validateLookupValueKey(keyFormat, lookupName)) {
            throw new RuntimeException(wrongLookupValueKeyFormat);
        }
        this.newRuleId = newRuleId;
        this.lookupName = lookupName;
        this.categoryId = categoryId;
        this.description = description;
        this.locale = MasterDataEntity.CUSTOMIZATION_LOCALE_ID;
        this.message = null;
    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        int lookupEntity = LookUpEntity.INTEREST_TYPES;

        int lookupId = insertLookupValue(connection, lookupEntity, lookupName);
        insertMessage(connection, lookupId, locale, message);
        addInterestType(connection, newRuleId, description, lookupId);
        upgradeVersion(connection);
    }

    private void addInterestType(Connection connection, int newRuleId, String description, int lookupId)
            throws SQLException {
        PreparedStatement statement = connection.prepareStatement("insert into interest_types("
                + "  interest_type_id,lookup_id,category_id,descripton)" + "values(?,?,?,?)");
        statement.setInt(1, newRuleId);
        statement.setInt(2, lookupId);
        statement.setInt(3, categoryId);
        statement.setString(4, description);
        statement.executeUpdate();
        statement.close();
    }

}
