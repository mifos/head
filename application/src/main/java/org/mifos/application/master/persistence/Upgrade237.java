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

import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.SqlUpgradeScriptFinder;

public class Upgrade237 extends Upgrade {

    public Upgrade237() {
        super(237);
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {

        if (!alreadyUpgraded(connection)) {
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(this.higherVersion(),
                    "upgrade_to_237_conditional.sql");
            upgrade.runScript(connection);
        }

        upgradeVersion(connection);
    }

    /*
     * Skip this upgrade for GK as they are going to upgrade this manually in multiple steps
     */
    private static boolean alreadyUpgraded(Connection connection) throws SQLException {
        ResultSet rs = connection.createStatement().executeQuery("DESCRIBE FEES");
        Boolean skipUpdate = Boolean.FALSE;
        while (rs.next()) {
            if (rs.getString(1).equals("FEE_AMOUNT") && rs.getString(2).equals("decimal(21,4)")) {
                skipUpdate = Boolean.TRUE;
            }
        }
        return skipUpdate;
    }
}
