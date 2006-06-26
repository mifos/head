/**

 * ConfigurationPersistenceService.java    version: 1.0



 * Copyright © 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License
 * Copyright (c) 2005-2006 Grameen Foundation USA
 *

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the

 * License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license

 * and how it is applied.

 *

 */
package org.mifos.framework.components.configuration.persistence.service;
import java.util.List;

import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.framework.components.configuration.business.ConfigEntity;
import org.mifos.framework.components.configuration.persistence.ConfigurationPersistence;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.service.PersistenceService;

public class ConfigurationPersistenceService extends PersistenceService {
	
	private ConfigurationPersistence serviceImpl = new ConfigurationPersistence();
	
	public MifosCurrency getDefaultCurrency(){
		return serviceImpl.getDefaultCurrency();
	}

	public SupportedLocalesEntity getSupportedLocale()throws SystemException{
		return serviceImpl.getSupportedLocale();
	}
	public ConfigEntity getSystemConfiguration() throws SystemException {
		return serviceImpl.getSystemConfiguration();
	}
	
	public List<ConfigEntity> getOfficeConfiguration() throws SystemException {
		return serviceImpl.getOfficeConfiguration();
	}
	
	public List<WeekDaysEntity> getWeekDaysList()throws SystemException{
		return serviceImpl.getWeekDaysList();
	}
}
