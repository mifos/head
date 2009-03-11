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

package org.mifos.framework.hibernate;

import java.util.Properties;

import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.hibernate.configuration.ConfigureSession;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;

/**
 * Hibernate Start up class called during the system start up configures
 * hibernate with relevant mapping files & connection parameters
 */
abstract class HibernateStartUp {
    /**
     * This method is called by the StartUP plug in class during the system
     * start up , this will initialize the hibernate configuration files and
     * database connection parameters required for hibernate for database
     * connectivity.
     */
    private static void initialize(String hibernatePropertiesPath) throws HibernateStartUpException {
        ConfigureSession.configure();
        HibernateSessionFactory.setConfiguration(ConfigureSession.getConfiguration());
    }


}
