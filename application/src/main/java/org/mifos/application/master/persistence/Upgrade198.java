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
import org.mifos.framework.persistence.SqlUpgrade;
import org.mifos.framework.persistence.Upgrade;
import org.mifos.framework.util.SqlUpgradeScriptFinder;

/**
 * Upgrade198 is a conditional upgrade that removes 7 orphan lookup values
 * (DBUpgrade.OfficeLevels.Unsued,DBUpgrade.PrdApplicableMaster.Unused,
 * DBUpgrade.InterestCalcRule.Unused, DBUpgrade.Address3.Unused,
 * DBUpgrade.City.Unused
 * ,DBUpgrade.LoanPurposes1.Unused,DBUpgrade.LoanPurposes2.Unused) from the
 * database if the database is clean (the proxy for "clean" is that no other
 * offices than the default head offices exist in the database)
 */
public class Upgrade198 extends Upgrade {

    public Upgrade198() {
        super(198);
    }

    /**
     * Don't apply the upgrade if there is any data in the database. This
     * upgrade is used to keep the testing framework consistent in being able to
     * upgrade and downgrade a "clean" database.
     */
    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        if (noOfficesHaveBeenCreatedByEndUsers(connection)) {
            SqlUpgrade upgrade = SqlUpgradeScriptFinder.findUpgradeScript(this.higherVersion(),
                    "upgrade_to_198_conditional.sql");
            upgrade.runScript(connection);
        }
        upgradeVersion(connection);
    }

}
