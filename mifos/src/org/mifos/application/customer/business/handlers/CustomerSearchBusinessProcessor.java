/**

 * CustomerSearchBusinessProcessor.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.application.customer.business.handlers;

import java.util.List;

import org.mifos.application.customer.dao.CustomerSearchDAO;
import org.mifos.application.customer.dao.SearchDAO;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.customer.util.valueobjects.Customer;
import org.mifos.application.customer.util.valueobjects.CustomerSearch;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.office.util.valueobjects.Office;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.valueobjects.Personnel;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * This class is used as BusinessProcessor to search for customers.
 * @author ashishsm
 *
 */

public class CustomerSearchBusinessProcessor extends MifosBusinessProcessor {

	/**
	 * default constructor
	 */
	public CustomerSearchBusinessProcessor() {
	}
	
	/**
	 * Gets a list of Customers using getCustomers under the loanofficer method and puts them in the context.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void get(Context context)throws SystemException,ApplicationException{
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside get method of CustomerSearchBusinessProcessor ");
		CustomerSearch customerSearch=(CustomerSearch)context.getValueObject();
		if(null != customerSearch) {
			Short officeId=customerSearch.getOfficeId();
			Short personnelId=customerSearch.getLoanOfficerId();
			List<Customer>  customerList= getCustomers(personnelId,officeId,context.getPath(),context);
			context.addAttribute(new SearchResults(CustomerSearchConstants.CUSTOMERLIST,customerList));
			context.addBusinessResults(CustomerSearchConstants.LOADFORWARD,CustomerSearchConstants.LOADFORWARDNONLOANOFFICER);
		}
	}
	
	/**
	 * Gets a list of LoanOfficers using getLoanOfficers under the branch method and puts them in the context.
	 * 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void preview(Context context)throws SystemException,ApplicationException{
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside preview method of CustomerSearchBusinessProcessor ");
		CustomerSearch customerSearch=(CustomerSearch)context.getValueObject();
		if(null != customerSearch) {
			Short officeId=customerSearch.getOfficeId();
			List<Personnel> personnelList=getLoanOfficers(officeId,context.getPath());
			context.addBusinessResults(CustomerSearchConstants.LOANOFFICERSLIST,personnelList);
			context.addBusinessResults(CustomerSearchConstants.LOADFORWARD,CustomerSearchConstants.LOADFORWARDNONLOANOFFICER);
			context.addBusinessResults(CustomerSearchConstants.OFFICE,customerSearch.getOfficeName());
		}
	}
	
	/**
	 * Gets a list of Branches using getBranchesUnderDataScope method and puts them in the context.
	 * It puts the same list of branches into two searchResults and puts them in the Context twice because
	 * we need them for two combo boxes. 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void loadInitial(Context context)throws SystemException,ApplicationException{
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside loadInitial method of CustomerSearchBusinessProcessor ");
		UserContext userContext=context.getUserContext();
		if(null!=userContext) {
			Short personnelId=userContext.getId();
			Short officeId=userContext.getBranchId();
			MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
				"Inside loadInitial method of CustomerSearchBusinessProcessor personnelId+officeId "+personnelId+"---"+officeId);
			Personnel personnel=((CustomerSearchDAO) getDAO(context.getPath())).getPersonnel(personnelId);
			MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
				"Inside loadInitial method of CustomerSearchBusinessProcessor After Personnel+--"+personnel.getLevel().getLevelId());
			Office office=getOffice(officeId,context.getPath());
			MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
				"Inside loadInitial method of CustomerSearchBusinessProcessor office level id---"+office.getLevel().getLevelId());
			if(personnel.getLevel().getLevelId().equals(PersonnelConstants.LOAN_OFFICER)) {
				List<Customer>  customerList= getCustomers(personnelId,officeId,context.getPath(),context);
				context.addAttribute(new SearchResults(CustomerSearchConstants.CUSTOMERLIST,customerList));
				context.addBusinessResults(CustomerSearchConstants.LOADFORWARD,CustomerSearchConstants.LOADFORWARDLOANOFFICER);
				MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
					"Inside loadInitial method of CustomerSearchBusinessProcessor officeName"+office.getOfficeName());
				context.addBusinessResults(CustomerSearchConstants.OFFICE,office.getOfficeName());
			}
			else {
				if(office.getLevel().getLevelId().equals(OfficeConstants.BRANCHOFFICE)) {
					List<Personnel> personnelList=getLoanOfficers(officeId,context.getPath());
					context.addBusinessResults(CustomerSearchConstants.LOANOFFICERSLIST,personnelList);
					context.addBusinessResults(CustomerSearchConstants.LOADFORWARD,CustomerSearchConstants.LOADFORWARDNONLOANOFFICER);
					MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
						"Inside loadInitial method of CustomerSearchBusinessProcessor officeName"+office.getOfficeName());
					context.addBusinessResults(CustomerSearchConstants.OFFICE,office.getOfficeName());
				}
				else {
					List<Office> officesList=getBranchesUnderDataScope(office.getSearchId(),context.getPath());
					MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
						"Inside loadInitial method of CustomerSearchBusinessProcessor officeSize+---"+
						(officesList!=null?String.valueOf(officesList.size()):"NULL"));
					context.addAttribute(new SearchResults(CustomerSearchConstants.OFFICESLIST,officesList));
					context.addBusinessResults(CustomerSearchConstants.LOADFORWARD,CustomerSearchConstants.LOADFORWARDNONBRANCHOFFICE);
					context.addBusinessResults(CustomerSearchConstants.OFFICE,office.getOfficeName());
				}
			}
			
		}
	}
	
	
	
	/**
	 * Gets the office details for the given office Id
	 * 
	 * @param officeId
	 * @param path
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private Office getOffice(Short officeId,String path) throws  SystemException, ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside getCustomers method of CustomerSearchBusinessProcessor ");
		return ((CustomerSearchDAO) getDAO(path)).getOffice(officeId);
	}
	
	
	/**
	 * Returns SearchResults which contain list of objects of type OfficeMaster based on the datascope of the 
	 * passed office id . The search returns only offices of type BranchOffice.
	 * @param officeId - Office Id of the logged in user
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private List<Office> getBranchesUnderDataScope(String officeSearchId,String path)throws SystemException,ApplicationException{
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside getBranchesUnderDataScope method of CustomerSearchBusinessProcessor ");
	return ((CustomerSearchDAO) getDAO(path)).getOffices(officeSearchId);
	}
	
	/**
	 * Gets a list of LoanOfficers for an OfficeId , this officeId is obtained from valueObject
	 * which has a  searchNodeMap containing the search parameters coming grom the UI , and puts them in the context.
	 * It gets the Loan Officers irrespective of their status. 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private List<Personnel> getLoanOfficers(Short officeId,String path)throws  SystemException, ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside getLoanOfficers method of CustomerSearchBusinessProcessor ");
		return ((CustomerSearchDAO) getDAO(path)).getLoanOfficers(officeId);
	}
	
	/**
	 * Gets a list of Centers for an LoanOfficerId , this LoanOfficerId is obtained from valueObject
	 * which has a  searchNodeMap containing the search parameters coming grom the UI , and puts them in the context.
	 * It gets the centers irrespective of their status. 
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void getCenters(Context context)throws SystemException,ApplicationException{
		
	}
	
	/**
	 * Gets a list of customers for an OfficeId  or for all branches, this officeId is obtained from valueObject
	 * which has a  searchNodeMap containing the search parameters coming grom the UI , and puts them in the context.
	 * It gets the customers irrespective of their status.In case the officeId is not selected in which case it defaults to
	 * all branches then the customer is searched in branches under the datascope of branchid of the logged in user.
	 * @param context
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	private List<Customer> getCustomers(Short personnelId,Short officeId,String path,Context context) throws  SystemException, ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.CUSTOMERSEARCHLOGGER).info(
			"Inside getCustomers method of CustomerSearchBusinessProcessor ");
		boolean isCenterHierarchyExist = Configuration.getInstance().getCustomerConfig(officeId).isCenterHierarchyExists();
		context.addBusinessResults("GrpHierExists",isCenterHierarchyExist);
		if(isCenterHierarchyExist) {			
			return ((CustomerSearchDAO) getDAO(path)).getCustomers(personnelId,officeId,CustomerConstants.CENTER_LEVEL_ID);
		}
		return ((CustomerSearchDAO) getDAO(path)).getCustomers(personnelId,officeId,CustomerConstants.GROUP_LEVEL_ID);
	}
	
	public void getCustomerSearchResults(Context context)  throws  SystemException, ApplicationException {
		CustomerSearch  customerSearch=(CustomerSearch)context.getValueObject();		
		String searchString=context.getSearchObject().getFromSearchNodeMap(CustomerSearchConstants.CUSTOMERSEARCSTRING);
		Short officeId=Short.valueOf(context.getSearchObject().getFromSearchNodeMap(CustomerSearchConstants.CUSTOMERSEARCOFFICEID));	
		String officeSearchId = HierarchyManager.getInstance().getSearchId(context.getUserContext().getBranchId());
		SearchDAO searchDAO=new SearchDAO();
		String searchType=CustomerSearchConstants.CUSTOMERSEARCHRESULTS;
		QueryResult customerSearchResults=(searchDAO.search(searchType,searchString,context.getUserContext().getLevelId(),officeSearchId,context.getUserContext().getId(),officeId));
		context.setSearchResult(customerSearchResults);
		context.getSearchObject().getSearchNodeMap().put(CustomerSearchConstants.CUSTOMERSEARCBRANCH,customerSearch.getOfficeName());
	
	}

}
