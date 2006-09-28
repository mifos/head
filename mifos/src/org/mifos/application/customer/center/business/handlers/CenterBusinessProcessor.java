/**

* CenterBusinessProcessor    version: 1.0



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

package org.mifos.application.customer.center.business.handlers;

import java.util.List;

import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.center.util.helpers.CenterSearchResults;
import org.mifos.application.customer.center.util.valueobjects.Center;
import org.mifos.application.customer.dao.SearchDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.util.helpers.CenterSearchInput;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.group.util.helpers.LinkParameters;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

public class CenterBusinessProcessor extends MifosBusinessProcessor {
	/**An insatnce of the logger which is used to log statements */
	private  MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CENTERLOGGER);
	
	/** 
	 * This method returns instance of LinkParameters
	 * @return group 
	 */ 
	LinkParameters getLinkValues(Center center){
		LinkParameters linkParams = new LinkParameters();
		linkParams.setCustomerId(center.getCustomerId());
		linkParams.setCustomerName(center.getDisplayName());
		linkParams.setGlobalCustNum(center.getGlobalCustNum());
		linkParams.setCustomerOfficeId(center.getOffice().getOfficeId());
		linkParams.setCustomerOfficeName(center.getOffice().getOfficeName());
		linkParams.setLevelId(CustomerConstants.CENTER_LEVEL_ID);
		Customer parent = center.getParentCustomer();
		if(parent!=null){
			linkParams.setCustomerParentGCNum(parent.getGlobalCustNum());
			linkParams.setCustomerParentName(parent.getDisplayName());
		}
		return linkParams;
	}
	
	
	
	/**
	 * This method searches for the list of centers under a particualr office or under the child offices of the parent office
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * A search can also be done on the center name
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getSearchCenter(Context context)throws SystemException,ApplicationException {
		CenterSearchResults centerSearchResults = new CenterSearchResults();
		
		//The search string which is entered byt he user 
		String searchString = context.getSearchObject().getFromSearchNodeMap("searchString");
		CenterSearchInput centerSearchInput = (CenterSearchInput)context.getBusinessResults(GroupConstants.CENTER_SEARCH_INPUT);
		//office id under which the list of centers should be displayed
		short branchId = centerSearchInput.getOfficeId();
		//short branchId = Short.valueOf("13");
		logger.debug("--------------branch id obtained in getSearchCenter: "+ branchId);
		SearchDAO searchDAO=new SearchDAO();
		String searchType="centerSearch";
		String searchId = HierarchyManager.getInstance().getSearchId(branchId);
		try{
			//puts the results obtained after the search into the context
			context.setSearchResult(searchDAO.search(searchType, searchString ,context.getUserContext().getLevelId(),searchId,context.getUserContext().getId(),null ));
			logger.debug("Values put into context");
			
		}
		catch(SystemException se){
			logger.error("centerDAO search threw exception ",false, null,se);
		}
		catch(ApplicationException ae){
			throw ae;
		}
		catch(Exception e){
			throw new CustomerException(CenterConstants.FATAL_ERROR_EXCEPTION , e);
		}
		
	}
	/**
	 * This method sets an object as the value of the search results object with a particuar name
	 * @param context This includes all the parameters passed from the action to the business processor layer.
	 * @param resultName The name with which the object will be associated with
	 * @param results The object to be put as the value of the search results
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	
	public void setSearchResults(Context context , String resultName , List results){
		SearchResults masterInfo = new SearchResults();
		masterInfo.setResultName(resultName);
		masterInfo.setValue(results);
		context.addAttribute(masterInfo);
	}
	
	
}



