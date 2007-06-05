/**

 * Configuration.java    version: 1.0



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

package org.mifos.framework.components.configuration.business;
import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.components.configuration.cache.CacheRepository;
import org.mifos.framework.components.configuration.util.helpers.ConfigurationInitializer;

public class Configuration {
  /**It is the map of instances of OfficeConfig stored based on officeid. It is cache of OfficeConfig instances */
  private Map<Short,OfficeConfig> officeConfigMap;

  private SystemConfiguration systemConfig;

  private static CacheRepository cacheRepo;
  
  private static Configuration config;
  
  public static Configuration getInstance(){
	  if(config==null){
		 config = new Configuration();
		 config.initialize();
	  }
	  return config;
  }
  
  private void initialize(){
	  synchronized (cacheRepo) {
		  new ConfigurationInitializer().initialize();
		  initializeSystemConfiguration();
	  }
  }
  
  //TODO:Currently offset is being passed for TimeZone. It should be changed to Timezone value picked from database
  private void initializeSystemConfiguration(){
	 systemConfig = cacheRepo.getSystemConfiguration();
  }
  
  private Configuration() {
	  cacheRepo = CacheRepository.getInstance();
	  officeConfigMap = new HashMap<Short,OfficeConfig>();
  }
  
  /**
   * This method will return instance of OfficeConfig based on officeId. If OfficeConfig instance is available 
   * in cache it will return the same, otherwise it will create and retuen a new instance and store that into cache also.
   */
  public OfficeConfig getOfficeConfig(Short officeId) {
	  if(officeConfigMap.containsKey(officeId))
		  return officeConfigMap.get(officeId);
	  OfficeConfig officeConfig = new OfficeConfig(cacheRepo,officeId);
	  synchronized (officeConfigMap) {
		  officeConfigMap.put(officeId, officeConfig);
	  }
	  return officeConfig;
  }
 
  public SystemConfiguration getSystemConfig() {
	  return systemConfig;
  }
  
  public AccountConfig getAccountConfig(Short officeId) {
 	 return getOfficeConfig(officeId).getAccountConfig();
  }
  
  public CustomerConfig getCustomerConfig(Short officeId) {
	  return getOfficeConfig(officeId).getCustomerConfig();
  }
  
  public MeetingConfig getMeetingConfig(Short officeId) {
	  return getOfficeConfig(officeId).getMeetingConfig();
  }

}
