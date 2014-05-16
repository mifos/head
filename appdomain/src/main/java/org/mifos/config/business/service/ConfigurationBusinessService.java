/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

package org.mifos.config.business.service;

import java.util.List;

import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.config.business.ConfigurationKeyValue;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.security.util.UserContext;

public class ConfigurationBusinessService implements BusinessService {

    private final ConfigurationPersistence configurationPersistence;

    ConfigurationBusinessService(ConfigurationPersistence configurationPersistence) {
        this.configurationPersistence = configurationPersistence;
    }

    public ConfigurationBusinessService() {
        this(new ConfigurationPersistence());
    }

    @Override
    public AbstractBusinessObject getBusinessObject(@SuppressWarnings("unused") UserContext userContext) {
        return null;
    }

    public List<ConfigurationKeyValue> getConfiguration() throws ServiceException {
        try {
            return configurationPersistence.getAllConfigurationKeyValues();
        } catch (PersistenceException e) {
            throw new ServiceException(e);
        }
    }

    public boolean isGlimEnabled() {
        return configurationPersistence.isGlimEnabled();
    }
    public boolean isNewGlimEnabled(){
    	return configurationPersistence.isNewGlimEnabled();
    }

     public boolean isRepaymentIndepOfMeetingEnabled() {
        return new ConfigurationPersistence().isRepaymentIndepOfMeetingEnabled();
    }
     
     public boolean isRecalculateInterestEnabled() {
    	 return (configurationPersistence.getConfigurationValueIntegerWithoutFlush(LoanConstants.RECALCULATE_INTEREST)==1);
     }

}
