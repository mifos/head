/**
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.framework.components.configuration.business;

import org.mifos.framework.components.configuration.cache.CacheRepository;
import org.mifos.framework.components.configuration.util.helpers.ConfigConstants;

/**
 * This class defines methods that tells configuration details for Account
 * (Loan/Savings/CustomerAccount). It has specific methods for specific type of
 * configuration items related to Accounts.
 * <p>
 * This class is a remnant of per-office configuration, which <a
 * href="http://article.gmane.org/gmane.comp.finance.mifos.devel/3498">is
 * deprecated and may be removed</a> (-Adam 22-JAN-2008).
 */
public class AccountConfig extends BaseConfig {
	public AccountConfig(CacheRepository cacheRepo, OfficeConfig officeConf) {
		super(cacheRepo, officeConf);
	}

	public Short getLatenessDays() {
		return getShortValueFromCache(ConfigConstants.LATENESS_DAYS,
				ConfigConstants.LATENESS_DAYS_DEFAULT);
	}

	public Short getDormancyDays() {
		return getShortValueFromCache(ConfigConstants.DORMANCY_DAYS,
				ConfigConstants.DORMANCY_DAYS_DEFAULT);
	}
}
