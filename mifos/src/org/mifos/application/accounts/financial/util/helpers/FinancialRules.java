/**

 * FinancialRules.java    version: 1.0

 

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

package org.mifos.application.accounts.financial.util.helpers;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.framework.spring.SpringUtil;

/**
 * Resolves general ledger accounts from financial actions. Currently just
 * delegates to {@link DynamicFinancialRules} and will be replaced by the
 * functionality in that class soon. (-Adam 06-FEB-2008)
 */
public class FinancialRules {
	// TODO: inject dynamic financial rules into this class
	// * add members, getters/setters
	// * modify applicationContext.xml to refer to this

	public static short getCategoryAssociatedToAction(short financialActionId,
			FinancialConstants type) throws FinancialException {
		FinancialActionConstants financialAction = FinancialActionConstants
				.getFinancialAction(financialActionId);
		// TODO: after dependency injection is complete, it should not be
		// necessary to refer to the ApplicationContext
		DynamicFinancialRules dfr = (DynamicFinancialRules) SpringUtil
				.getAppContext().getBean("dynamicFinancialRules",
						DynamicFinancialRules.class);
		return dfr.getCategoryAssociatedToAction(financialAction, type);
	}

	public static short getCategoryAssociatedToAction(
			FinancialActionConstants financialAction, FinancialConstants type)
			throws FinancialException {
		return getCategoryAssociatedToAction(financialAction.getValue(), type);
	}

}
