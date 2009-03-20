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
 
package org.mifos.api;

import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.AppNotConfiguredException;
import org.mifos.framework.exceptions.HibernateStartUpException;
import org.mifos.framework.exceptions.LoggerConfigurationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;

public class MifosService {
    MifosService() {
    }
    
    void init() throws LoggerConfigurationException, HibernateStartUpException, 
    AppNotConfiguredException {
	    MifosLogManager.configure(FilePaths.LOG_CONFIGURATION_FILE);
	    MifosLogger logger = MifosLogManager.getLogger("org.mifos.logger");
	    logger.info("Logger initialized", false, null);
	    
	    StaticHibernateUtil.initialize();
	    logger.info("Hibernate initialized", false, null);
	    
	    logger.info("Mifos Service Layer initialized", false, null);
    }
    
    public Loan getLoan(Integer id) throws Exception, ServiceException {
    	return Loan.getLoan(id);
    }
}
