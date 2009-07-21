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

package org.mifos.framework.util;

import java.net.URL;

import org.mifos.framework.persistence.SqlResource;
import org.mifos.framework.persistence.SqlUpgrade;

public class SqlUpgradeScriptFinder {
    public static SqlUpgrade findUpgradeScript(int higherVersion, String scriptName) {
        // Currently, SQL files are located in the same package as
        // SqlUpgradeScriptFinder so we need to load the file from this
        // class
        URL url = getSqlResourceLocation(scriptName);
        boolean foundInSql = url != null;

        if (foundInSql) {
            return new SqlUpgrade(url, higherVersion);
        } else {
            throw new IllegalStateException("Did not find upgrade to " + higherVersion + " in java or in an sql file");
        }
    }
    
    static URL getSqlResourceLocation(String name) {
        return SqlResource.getInstance().getUrl(name);
    }
}
