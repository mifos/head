/**

 * ConfigurationPersistence.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

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
package org.mifos.framework.components.configuration.persistence;

import java.util.List;

import org.hibernate.Hibernate;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.SupportedLocalesEntity;
import org.mifos.application.meeting.business.WeekDaysEntity;
import org.mifos.framework.components.configuration.business.ConfigEntity;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.FrameworkRuntimeException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;

public class ConfigurationPersistence extends Persistence{
	private MifosLogger logger = 
		MifosLogManager.getLogger(LoggerConstants.CONFIGURATION_LOGGER);
	
	public MifosCurrency getDefaultCurrency() throws PersistenceException {
		List queryResult = executeNamedQuery(
			NamedQueryConstants.GET_DEFAULT_CURRENCY, null);
		return defaultCurrencyFromList(queryResult);
	}

	MifosCurrency defaultCurrencyFromList(List queryResult) {
		if (queryResult.size() == 1) {
			return (MifosCurrency) queryResult.get(0);
		}
		else if (queryResult.size() > 1) {
			MifosCurrency candidate0 = (MifosCurrency) queryResult.get(0);
			MifosCurrency candidate1 = (MifosCurrency) queryResult.get(1);

			throw logAndThrow("Both " + candidate0.getCurrencyName() +
			    " and " + candidate1.getCurrencyName() + 
			    " are marked as default currencies");
		}
		else {
			throw logAndThrow("No Default Currency Specified");
		}
	}

	private FrameworkRuntimeException logAndThrow(String message) {
		logger.error(message);
		return new FrameworkRuntimeException(null, message);
	}

	
	public ConfigEntity getSystemConfiguration() throws PersistenceException{
		List<ConfigEntity> queryResult = executeNamedQuery(NamedQueryConstants.GET_SYSTEM_CONFIG,null);
		if (queryResult==null || queryResult.size()==0) {
			logger.error("No System Configuration Specified");
			throw new FrameworkRuntimeException(null, "No System Configuration Specified");
		}
		return queryResult.get(0);
	}
	
	public SupportedLocalesEntity getSupportedLocale()throws PersistenceException{
		  List<SupportedLocalesEntity> supportedLocaleList = HibernateUtil.getSessionTL().getNamedQuery(NamedQueryConstants.GET_MFI_LOCALE).list();
		  if (supportedLocaleList==null || supportedLocaleList.size()==0) {
			    logger.error("No Default Locale Specified");
				throw new FrameworkRuntimeException(null, "No Default Locale Specified");
		  }
		  SupportedLocalesEntity locale = supportedLocaleList.get(0);
		  Hibernate.initialize(locale.getCountry());
		  Hibernate.initialize(locale.getLanguage());
		  return locale;
	}
	
	public List<ConfigEntity> getOfficeConfiguration()throws PersistenceException{
		List<ConfigEntity> queryResult = executeNamedQuery(NamedQueryConstants.GET_OFFICE_CONFIG, null);
		if (queryResult==null || queryResult.size()==0) {
			logger.error("Office Configuration Not Specified");
			throw new FrameworkRuntimeException(null, "Office Configuration Not Specified");
		}
		return queryResult;
	}

	public List<WeekDaysEntity> getWeekDaysList()throws PersistenceException{
		List<WeekDaysEntity> queryResult = executeNamedQuery(NamedQueryConstants.GETWEEKDAYS, null);
		if (queryResult==null || queryResult.size()==0) {
			logger.error("WeekDays List Not Specified");
			throw new FrameworkRuntimeException(null, "WeekDays List Not Specified");
		}
		return queryResult;
	}
}
