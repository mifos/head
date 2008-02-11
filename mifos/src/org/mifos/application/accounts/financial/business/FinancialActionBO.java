/**

 * FinancialActionBO.java    version: 1.0

 

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

package org.mifos.application.accounts.financial.business;

import java.util.Set;

import org.mifos.application.accounts.financial.exceptions.FinancialException;
import org.mifos.application.accounts.financial.util.helpers.ChartOfAccountsCache;
import org.mifos.application.accounts.financial.util.helpers.FinancialConstants;
import org.mifos.application.accounts.financial.util.helpers.FinancialRules;
import org.mifos.application.master.business.MasterDataEntity;

public class FinancialActionBO extends MasterDataEntity {


	public Set<COABO> getApplicableDebitCharts() throws FinancialException {
		COABO chart = ChartOfAccountsCache.get(FinancialRules.getInstance()
				.getCategoryAssociatedToAction(getId(),
						FinancialConstants.DEBIT));
		return chart.getAssociatedChartOfAccounts();
	}

	public Set<COABO> getApplicableCreditCharts() throws FinancialException {
		COABO chart = ChartOfAccountsCache.get(FinancialRules.getInstance()
				.getCategoryAssociatedToAction(getId(),
						FinancialConstants.CREDIT));
		return chart.getAssociatedChartOfAccounts();
	}

}
