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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Utility methods for running SQL from files
 */
@SuppressWarnings( { "PMD.CyclomaticComplexity", "PMD.AssignmentInOperand", "PMD.AppendCharacterWithChar",
        "PMD.AvoidThrowingRawExceptionTypes", "PMD.DoNotThrowExceptionInFinally" })
public class SqlExecutor {
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(value = { "OBL_UNSATISFIED_OBLIGATION",
            "SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE" }, justification = "The resource is closed and the string cannot be static.")
    @SuppressWarnings("PMD.CloseResource")
    // Rationale: It's closed.
    public static void execute(InputStream stream, Connection conn) throws SQLException {
        String[] sqls = readFile(stream);
        boolean wasAutoCommit = conn.getAutoCommit();

        // Entring: Set auto commit false if auto commit was true
        if (wasAutoCommit) {
            conn.setAutoCommit(false);
        }

        Statement statement = conn.createStatement();
        for (String sql : sqls) {
            statement.addBatch(sql);
        }
        statement.executeBatch();
        statement.close();

        // Leaving: Set auto commit true if auto commit was true
        if (wasAutoCommit) {
            conn.commit();
            conn.setAutoCommit(true);
        }
    }

    /**
     * Closes the stream when done.
     * 
     * @return individual statements
     * */
    @SuppressWarnings( { "PMD.CyclomaticComplexity", "PMD.AssignmentInOperand", "PMD.AppendCharacterWithChar",
            "PMD.AvoidThrowingRawExceptionTypes", "PMD.DoNotThrowExceptionInFinally" })
    // Rationale: If the Apache Ant team thinks it's OK, we do too. Perhaps bad
    // reasoning, but inshallah.
    public static String[] readFile(InputStream stream) {
        // mostly ripped from
        // http://svn.apache.org/viewvc/ant/core/trunk/src/main/org/apache/tools/ant/taskdefs/SQLExec.java
        try {
            ArrayList<String> statements = new ArrayList<String>();
            Charset utf8 = Charset.forName("UTF-8");
            CharsetDecoder decoder = utf8.newDecoder();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream, decoder));
            StringBuffer sql = new StringBuffer();
            String line;
            while ((line = in.readLine()) != null) {
                if (line.startsWith("//") || line.startsWith("--")) {
                    continue;
                }

                line = line.trim();
                if ("".equals(line)) {
                    continue;
                }

                sql.append("\n");
                sql.append(line);

                // SQL defines "--" as a comment to EOL
                // and in Oracle it may contain a hint
                // so we cannot just remove it, instead we must end it
                if (line.indexOf("--") >= 0) {
                    sql.append("\n");
                }

                if (sql.length() > 0 && sql.charAt(sql.length() - 1) == ';') {
                    statements.add(sql.substring(0, sql.length() - 1));
                    sql.setLength(0);
                }
            }
            // Catch any statements not followed by ;
            if (sql.length() > 0) {
                statements.add(sql.toString());
            }

            return statements.toArray(new String[statements.size()]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                stream.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
