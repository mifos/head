/**

 * ConfigurationPersistenceService.java    version: 1.0

 

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
package org.mifos.application.configuration.persistence.service;

import java.util.List;

import org.mifos.application.configuration.persistence.ConfigurationPersistence;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.application.master.business.SupportedLocalesEntity;

/**
 * 
 *
 */
public class ConfigurationPersistenceService {
	
	private ConfigurationPersistence serviceImpl = new ConfigurationPersistence();
	
	public List<MifosLookUpEntity> getLookupEntities(){
		return serviceImpl.getLookupEntities();
	}

	public List<LookUpValueEntity> getLookupValues(){
		return serviceImpl.getLookupValues();
	}
	public List<SupportedLocalesEntity> getSupportedLocale(){
		return serviceImpl.getSupportedLocale();
	}
	
	
}
