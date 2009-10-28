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
import java.util.ServiceLoader;

import org.mifos.accounts.api.StandardAccountService;
import org.mifos.application.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.spi.TransactionImport;

public class PluginManager {
    /**
     * Returns specified import plugin or null.
     */
    public TransactionImport getImportPlugin(String importPluginClassname) {
        TransactionImport plugin = null;
        for (TransactionImport ti : loadImportPlugins()) {
            if (ti.getClass().getName().equals(importPluginClassname)) {
                plugin = ti;
            }
        }
        return plugin;
    }

    /**
     * Returns list of import plugins. Note that {@link ServiceLoader} caches
     * loads, so multiple invocations should not incur extra overhead.
     */
    public List<TransactionImport> loadImportPlugins() {
        List<TransactionImport> plugins = new ArrayList<TransactionImport>();
        ServiceLoader<TransactionImport> loader = ServiceLoader.load(TransactionImport.class);
        for (TransactionImport ti : loader) {
            ti.setAccountService(new StandardAccountService(new AccountPersistence(), new LoanPersistence(),
                    new AcceptedPaymentTypePersistence()));
            plugins.add(ti);
        }
        return plugins;
    }

    public List<String> getImportPluginNames() {
        List<String> pluginNames = new ArrayList<String>();
        List<TransactionImport> plugins = loadImportPlugins();
        for (TransactionImport ti : plugins) {
            pluginNames.add(ti.getDisplayName());
        }
        return pluginNames;
    }
}
