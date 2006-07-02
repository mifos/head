/**

 * AccountsSearchBusinessProcessor.java    version: 1.0

 

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
package org.mifos.application.accounts.business.handler;



import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.valueobjects.AccountSearch;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.dao.SearchDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This class business logic related to the accounts
 * @author rajenders
 *
 */
public class AccountsSearchBusinessProcessor extends MifosBusinessProcessor {
	
	public void getAccountsSearch(Context context) throws SystemException,ApplicationException
	{
		String searchString = context.getSearchObject().getFromSearchNodeMap("searchString");
		String from=((AccountSearch)context.getValueObject()).getInput();
		String searchType="AccountSearchResults";
		if(from!=null && from.equals(AccountConstants.SAVINGS))
			searchType=AccountConstants.CUSTOMERS_FOR_SAVINGS_ACCOUNT;
		
		String searchId = HierarchyManager.getInstance().getSearchId(context.getUserContext().getBranchId());
		try{
			//puts the results obtained after the search into the context			
			context.setSearchResult(new SearchDAO().search(searchType,searchString,context.getUserContext().getLevelId(),searchId,context.getUserContext().getId(),context.getUserContext().getBranchId()));		
		}
		catch(SystemException se){
			throw se;
		}
		catch(ApplicationException ae){
			throw ae;
		}
		catch(Exception e){
			//TODO throw proper
			throw new CustomerException(CenterConstants.FATAL_ERROR_EXCEPTION , e);
		}
	}
}
