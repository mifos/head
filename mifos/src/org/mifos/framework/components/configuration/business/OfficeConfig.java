/**

 * OfficeConfig.java    version: 1.0



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

/**
 * This class is a remnant of per-office configuration, which <a
 * href="http://article.gmane.org/gmane.comp.finance.mifos.devel/3498">is
 * deprecated and may be removed</a> (-Adam 22-JAN-2008).
 */
public class OfficeConfig{
	
	  private CustomerConfig customerConfig;
	  private AccountConfig accountConfig;
	  private MeetingConfig meetingConfig;
	  private Short officeId;

	  public OfficeConfig(CacheRepository cacheRepo, Short officeId){
		  this.officeId = officeId;
		  customerConfig = new CustomerConfig(cacheRepo,this);
		  accountConfig = new AccountConfig(cacheRepo,this);
		  meetingConfig = new MeetingConfig();
	  }
	  
	  public AccountConfig getAccountConfig() {
		  return accountConfig;
	  }
	  
	  public CustomerConfig getCustomerConfig() {
		  return customerConfig;
	  }
	  
	  public MeetingConfig getMeetingConfig() {
		  return meetingConfig;
	  }

	  public Short getOfficeId() {
	  	 return officeId;
	  }

	  public void setOfficeId(Short officeId) {
		  this.officeId = officeId;
	  }
}
