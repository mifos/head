/*
 * Copyright Grameen Foundation USA
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

public class DummyUpgrade extends Upgrade {
    private StringBuilder log = new StringBuilder();

    public DummyUpgrade() {
        super();
    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
//        log.append("upgrade to " + ) + "\n");
        connection.createStatement().execute("CREATE TABLE FOO ( "+
                "FOO_ID INTEGER,"+
                "Description VARCHAR(25),"+
                "PRIMARY KEY(FOO_ID) ) ENGINE=InnoDB CHARACTER SET utf8 ");


        connection.createStatement().execute("INSERT INTO FOO VALUES(1, 'BAR')");

        connection.createStatement().execute("INSERT INTO FOO VALUES(2, 'BAZ')");
        connection.commit();
    }

    String getLog() {
        return log.toString();
    }

}
