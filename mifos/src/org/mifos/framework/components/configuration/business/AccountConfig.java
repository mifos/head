/**

 * AccountConfig.java    version: 1.0



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
import org.mifos.config.AccountingRules;

/**
 *  This class defines methods that tells configuration details for Account (Loan/Savings/CustomerAccount). 
 *  It has specific methods for specific type of configuration items related to Accounts. 
 *  This class restricts the interface of configuration items specific to Accounts only.  
 *  e.g.  isPendignApprovalStateDefinedForLoan(), getDormancyDays() etc.
 */ 
public class AccountConfig extends BaseConfig{
	  public AccountConfig(CacheRepository cacheRepo,OfficeConfig officeConf) {
		  super(cacheRepo,officeConf);
	  }
	
	  public boolean isPendingApprovalStateDefinedForLoan() {
		  return getBooleanValueFromCache(ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_LOAN,true);
	  }
	
	  public boolean isDisbursedToLOStateDefinedForLoan() {
		  return getBooleanValueFromCache(ConfigConstants.DISBURSED_TO_LO_DEFINED_FOR_LOAN,true);
	  }
	
	  public boolean isPendingApprovalStateDefinedForSavings() {
		  return getBooleanValueFromCache(ConfigConstants.PENDING_APPROVAL_DEFINED_FOR_SAVINGS,true);
	  }
	
	  public boolean isBackDatedTxnAllowed() {
		  return getBooleanValueFromCache(OfficeConfigConstants.BACK_DATED_TRXN_ALLOWED,true);
	  }
	  
	  public Short getLatenessDays() {
		 return getShortValueFromCache(ConfigConstants.LATENESS_DAYS,ConfigConstants.LATENESS_DAYS_DEFAULT);
	  }
	  
	  public Short getDormancyDays() {
		  return getShortValueFromCache(ConfigConstants.DORMANCY_DAYS,ConfigConstants.DORMANCY_DAYS_DEFAULT);
	  }
	  
	  //public Short getInterestDays() {
		  // kim replace the code below with AccountingRule.getNumberOfInterestDays
		  //return getShortValueFromCache(OfficeConfigConstants.NO_OF_INTEREST_DAYS,ConfigConstants.INTEREST_DAYS_DEFAULT);
		//  return AccountingRules.getNumberOfInterestDays();
	  //}
}
