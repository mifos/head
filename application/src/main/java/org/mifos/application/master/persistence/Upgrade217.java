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

package org.mifos.application.master.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import net.sourceforge.mayfly.MayflySqlException;

import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.SqlUpgradeScriptFinder;

/**
 * Upgrade217 is a conditional upgrade because Mayfly doesn't understands the
 * syntax of sql script(Mysql specific ALTER statements) in this upgrade.
 */
public class Upgrade217 extends Upgrade {

    public Upgrade217() {
        super(217);
    }

    @Override
    public void upgrade(Connection connection) throws IOException, SQLException {
        try {
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(this.higherVersion(),
                    "upgrade_to_217_conditional.sql");
            upgrade.runScript(connection);

        } catch (MayflySqlException mayflySqlException) {
            getLogger().info(
                    "Upgrade217 is a conditional upgrade because Mayfly doesn't understands the"
                            + " syntax of sql script(Mysql specific ALTER statements) in this upgrade.");
        }
        upgradeVersion(connection);
    }

}
