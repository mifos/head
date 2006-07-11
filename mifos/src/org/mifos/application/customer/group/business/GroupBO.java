/**

 * Group.java    version: 1.0



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

package org.mifos.application.customer.group.business;

import java.util.List;

import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;

/**
 * This class denotes the Group (row in customer table) object and all attributes associated with it.
 * It has a composition of other objects like Custom fields, fees, personnel etc., since it inherits from Customer
 * @author navitas
 */
public class GroupBO extends CustomerBO {
	
	private GroupPerformanceHistoryEntity groupPerformanceHistory;

	public GroupBO(){}
	
	public GroupBO(UserContext userContext){
		super(userContext);
	}
	public boolean isCustomerActive()
	{
		if(getCustomerStatus().getStatusId().equals(GroupConstants.ACTIVE))
			return true;
		return false;
	}

	public GroupPerformanceHistoryEntity getGroupPerformanceHistory() {
		return groupPerformanceHistory;
	}

	public void setGroupPerformanceHistory(
			GroupPerformanceHistoryEntity groupPerformanceHistory) {
		this.groupPerformanceHistory = groupPerformanceHistory;
	}
	
	public void generatePortfolioAtRisk() throws PersistenceException, ServiceException{
		Money amount=getBalanceForAccountsAtRisk();
		List<CustomerBO> clients = getDBService().getAllChildrenForParent(
					getSearchId(), getOffice().getOfficeId(),
					CustomerConstants.GROUP_LEVEL_ID);
		if(clients!=null && !clients.isEmpty()){
			for(CustomerBO client : clients){
				amount=amount.add(client.getBalanceForAccountsAtRisk());
			}
		}
		if(getGroupPerformanceHistory().getTotalOutstandingPortfolio().getAmountDoubleValue()!=0.0)
			getGroupPerformanceHistory().setPortfolioAtRisk(amount.divide(getGroupPerformanceHistory().getTotalOutstandingPortfolio()));
		getDBService().update(this);
	}
}
