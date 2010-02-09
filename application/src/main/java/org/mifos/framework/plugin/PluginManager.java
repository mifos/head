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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.mifos.accounts.api.StandardAccountService;
import org.mifos.application.acceptedpaymenttype.persistence.AcceptedPaymentTypePersistence;
import org.mifos.accounts.loan.persistance.LoanPersistence;
import org.mifos.accounts.persistence.AccountPersistence;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.util.ConfigurationLocator;
import org.mifos.spi.TransactionImport;

public class PluginManager {

    private static final Logger LOG = Logger.getLogger(LoggerConstants.FRAMEWORKLOGGER);
    
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
        ClassLoader pluginClassLoader = initializePluginClassLoader();
        ServiceLoader<TransactionImport> loader = ServiceLoader.load(TransactionImport.class, pluginClassLoader);
        for (TransactionImport ti : loader) {
            ti.setAccountService(new StandardAccountService(new AccountPersistence(), new LoanPersistence(),
                    new AcceptedPaymentTypePersistence()));
            plugins.add(ti);
        }
        return plugins;
    }

    /**
     * Extend classloader by loading jars from ${MIFOS_CONF}/plugins at runtime
     * 
     * @return pluginClassLoader
     */
    private ClassLoader initializePluginClassLoader() {
        ConfigurationLocator configurationLocator = new ConfigurationLocator();
        String libDir = configurationLocator.getConfigurationDirectory() + "/plugins";
        File dependencyDirectory = new File(libDir);
        File[] files = dependencyDirectory.listFiles();
        ArrayList<URL> urls = new ArrayList<URL>();
        if (files != null) {
            urls.addAll(getPluginURLs(files));
        }
        return new URLClassLoader(urls.toArray(new URL[urls.size()]), Thread.currentThread().getContextClassLoader());
    }

    private ArrayList<URL> getPluginURLs(File[] files) {
        ArrayList<URL> urls = new ArrayList<URL>();
        for (int i = 0; i < files.length; i++) {
            if (files[i].getName().endsWith(".jar")) {
                try {
                    urls.add(files[i].toURI().toURL());
                } catch (MalformedURLException e) {
                    LOG.log(Level.WARNING, this.getClass().getName(), e);
                }
            }
        }
        return urls;
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
