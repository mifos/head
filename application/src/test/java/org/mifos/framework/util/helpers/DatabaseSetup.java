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

package org.mifos.framework.util.helpers;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;

import org.hibernate.cfg.Configuration;
import org.mifos.core.ClasspathResource;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;
import org.mifos.framework.hibernate.helper.HibernateConstants;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.SqlExecutor;
import org.mifos.framework.persistence.SqlResource;

public class DatabaseSetup {

    public static void initializeHibernate() {
        MifosLogManager.configureLogging();

        if (HibernateSessionFactory.isConfigured()) {
            return;
        }
            setMysql();
    }

    public static void setMysql() {
        StaticHibernateUtil.initialize();
    }

    public static Configuration getHibernateConfiguration() {
        MifosLogManager.configureLogging();

        Configuration configuration = new Configuration();
        try {
            configuration.configure(ClasspathResource.getURI(FilePaths.HIBERNATECFGFILE).toURL());
        } catch (Exception e) {
            throw new HibernateStartUpException(HibernateConstants.CFGFILENOTFOUND, e);
        }
        return configuration;
    }

    public static void executeScript(String name, Connection connection) throws SQLException, IOException {
        InputStream sql = SqlResource.getInstance().getAsStream(name);
        SqlExecutor.execute(sql, connection);
    }

}
