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
 
package org.mifos.framework.components.configuration.util.helpers;

import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.components.configuration.business.SystemConfiguration;
import org.mifos.framework.components.configuration.cache.OfficeCache;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.StartUpException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class ConfigurationIntializerIntegrationTest extends MifosIntegrationTest{
	public ConfigurationIntializerIntegrationTest() throws SystemException, ApplicationException {
        super();
    }

    private ConfigurationInitializer configInitializer;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		configInitializer= new ConfigurationInitializer();
	}

	@Override
	protected void tearDown() throws Exception {
		StaticHibernateUtil.closeSession();
		super.tearDown();
	}

	public void testCreateSystemCache() throws Exception{
		SystemConfiguration configuration = 
			configInitializer.createSystemConfiguration();
		assertNotNull(configuration);
		assertNotNull(configuration.getCurrency());
		assertNotNull(configuration.getMifosTimeZone());
	}

 	public void testCreateOfficeCache() throws Exception{
 		configInitializer.initialize();
 		OfficeCache officeCache = configInitializer.createOfficeCache();
 		assertNotNull(officeCache);
	}

	public void testStartUpException() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try {
			configInitializer.initialize();
			fail();
		} catch (StartUpException sue) {
			assertEquals(ExceptionConstants.STARTUP_EXCEPTION, sue.getKey());
		} finally {
			StaticHibernateUtil.closeSession();
		}
	}
}
