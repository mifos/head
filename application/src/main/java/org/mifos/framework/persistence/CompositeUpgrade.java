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

package org.mifos.framework.persistence;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class CompositeUpgrade extends Upgrade {

    private final Upgrade[] upgrades;

    public static int findVersion(Upgrade... upgrades) {
        if (upgrades.length == 0) {
            throw new RuntimeException("must specify at least one upgrade");
        }
        int version = upgrades[0].higherVersion();
        for (int i = 1; i < upgrades.length; ++i) {
            int thisVersion = upgrades[i].higherVersion();
            if (thisVersion != version) {
                throw new RuntimeException("got upgrades to " + version + " and " + thisVersion
                        + " but expected matching versions");
            }
        }
        return version;
    }

    protected CompositeUpgrade(Upgrade... upgrades) {
        super(findVersion(upgrades));
        this.upgrades = upgrades;
    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        for (Upgrade upgrade : upgrades) {
            upgrade.upgrade(connection);
        }
    }

}
