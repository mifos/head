/**

 * TestCaseInitializer.java    version: xxx



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
package org.mifos.framework.util.helpers;

import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.config.AccountingRules;
import org.mifos.config.Localization;
import org.mifos.framework.TestUtils;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;

/**
 * Many tests initialize themselves via this class.
 * 
 * However, the fact that it is a static block, and initializes
 * more than it may need to for a given test, means that it might 
 * be desirable
 * to call {@link DatabaseSetup} directly in some cases,
 * or to avoid everything here in others (that is, those
 * tests written to not need the database).
 */
public class TestCaseInitializer {
	static {
		try {
			DatabaseSetup.configureLogging();
			DatabaseSetup.initializeHibernate();
			//	add this because it is added to Application Initializer
			Localization.getInstance().init(); 
			/* initializeSpring needs to come before AuditConfiguration.init
			 * in order for MasterDataEntity data to be loaded.
			 */
			
			/* shouldn't we have other initialization from ApplicationInitializer in here ? */
			
			Money.setDefaultCurrency(AccountingRules.getMifosCurrency());
			
			TestUtils.initializeSpring();
			// Spring must be initialized before FinancialInitializer
			FinancialInitializer.initialize();
			AuthorizationManager.getInstance().init();
			HierarchyManager.getInstance().init();
			
			MifosConfiguration.getInstance().init();
			AuditConfigurtion.init(Localization.getInstance().getMainLocale());
		} catch (Exception e) {
			e.printStackTrace();
			throw new Error("Failed to start up", e);
		}
	}
}
