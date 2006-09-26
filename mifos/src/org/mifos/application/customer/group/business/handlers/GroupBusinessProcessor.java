/**

 * GroupBusinessProcessor.java    version: 1.0   



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

package org.mifos.application.customer.group.business.handlers;

import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.dao.SearchDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.customer.util.valueobjects.CustomerSearchInput;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

/** 
 *  This is the business processor for the group module. 
 *  It takes care of handling all the business logic for the group module
 */
public class GroupBusinessProcessor extends MifosBusinessProcessor {
	/**An instance of the customerhelper class */
	CustomerHelper helper = new CustomerHelper();
	
	/**
	 * This method searches for the list of groups under a particualr office and its child offices(as per the data scope) 
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * This search done base on groupName
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getGroupList(Context context)throws SystemException,ApplicationException {
//		The search string which is entered byt he user 
		String searchString = context.getSearchObject().getFromSearchNodeMap("searchString");
		
		CustomerSearchInput customerSearchInput = (CustomerSearchInput)context.getBusinessResults(CustomerConstants.CUSTOMER_SEARCH_INPUT);
		
		//office id under which the list of centers should be displayed
		short officeId = customerSearchInput.getOfficeId();
		String searchId = new CustomerUtilDAO().getOffice(officeId).getSearchId();
		String searchType="GroupList";
		SearchDAO searchDAO=new SearchDAO();
		try{
			//puts the results obtained after the search into the context
			context.setSearchResult(searchDAO.search(searchType,searchString,context.getUserContext().getLevelId(),searchId ,context.getUserContext().getId(),context.getUserContext().getBranchId()));			
		}
		catch(SystemException se){
			throw se;
		}
		catch(ApplicationException ae){
			throw ae;
		}
		catch(Exception e){
			throw new CustomerException(CenterConstants.FATAL_ERROR_EXCEPTION , e);
		}
		
	}
	
}



