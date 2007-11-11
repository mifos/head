/**

 * CustomerConfig.java    version: 1.0



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

import org.mifos.framework.components.configuration.cache.CacheRepository;
import org.mifos.framework.components.configuration.util.helpers.ConfigConstants;
import org.mifos.framework.components.configuration.util.helpers.OfficeConfigConstants;

/**
 *  This class defines methods that tells configuration details about Customer (Center/Group/Client). 
 *  It has specific methods for specific type of configuration items related to Customer. 
 *  This class restricts the interface of configuration items specific to Customers only.  
 *  e.g.  isCenterHierarchyExists(), isPendingApprovalStateDefinedForGroup() etc.
 */
public class CustomerConfig extends BaseConfig{

	  public CustomerConfig(CacheRepository cacheRepo,OfficeConfig officeConf) {
		  super(cacheRepo,officeConf);
	  }

	  // kim commented out to replace by ClientRules
	  //public boolean isCenterHierarchyExists() {
		//  return getBooleanValueFromCache(OfficeConfigConstants.CENTER_HIERARCHY_EXIST,true);
	  //}

	  //public boolean canGroupApplyForLoan() {
	  //	  return getBooleanValueFromCache(OfficeConfigConstants.GROUP_CAN_APPLY_LOANS,true);
	  //}
	  
	  //public boolean canClientExistOutsideGroup() {
	//	  return getBooleanValueFromCache(OfficeConfigConstants.CLIENT_CAN_EXIST_OUTSIDE_GROUP,true);	  
	  //}

	  public boolean isPendingApprovalStateDefinedForClient() {
		  return getBooleanValueFromCache(ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_CLIENT,true);
	  }

	  public boolean isPendingApprovalStateDefinedForGroup() {
		  return getBooleanValueFromCache(ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_GROUP,true);
	  }

	  public String getNameSequence() {
		  return getStringValueFromCache(OfficeConfigConstants.NAME_SEQUENCE,ConfigConstants.NAME_SEQUENCE_DEFAULT);
	  }
}
