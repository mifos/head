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

package org.mifos.framework.plugin;

import java.util.ArrayList;
import java.util.List;

import org.mifos.config.ConfigurationManager;
import org.mifos.spi.TransactionImport;

public class PluginManager {
    public List<String> getImportPluginNames() throws ClassNotFoundException, InstantiationException,
            IllegalAccessException {
        List<String> pluginNames = new ArrayList<String>();
        ConfigurationManager configMgr = ConfigurationManager.getInstance();
        // FIXME: externalize "Importers"
        for (String fullyQualifiedImporterClassName : configMgr.getStringArray("Importers")) {
            Class<? extends TransactionImport> clazz = Class.forName(fullyQualifiedImporterClassName).asSubclass(
                    TransactionImport.class);
            TransactionImport importer = clazz.newInstance();
            pluginNames.add(importer.getDisplayName());
        }
        if (pluginNames.size() < 1) {
            // FIXME: externalize "NO IMPORT PLUGINS FOUND"
            pluginNames.add("NO IMPORT PLUGINS FOUND");
        }
        return pluginNames;
    }
}
