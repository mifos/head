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

package org.mifos.framework.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;

@SuppressWarnings("PMD.CyclomaticComplexity")
// Rationale: readFile() has a lot of if's and that's OK.
public class SqlUpgrade extends Upgrade {

    private final URL script;

    public SqlUpgrade(URL sqlScript, int higherVersion) {
        super(higherVersion);
        this.script = sqlScript;
    }

    public URL sql() {
        return script;
    }

    public void runScript(Connection connection) throws IOException, SQLException {
        InputStream in = sql().openStream();
        SqlExecutor.execute(in, connection);
        in.close();
    }

    @Override
    public void upgrade(Connection connection)
            throws IOException, SQLException {
        runScript(connection);
    }

}
