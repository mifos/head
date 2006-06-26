/**

 * SearchDAO  version: 1.0



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

package org.mifos.application.customer.dao;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.client.util.helpers.ClientConstants;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.customer.util.helpers.Param;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.QueryResultIdSearch;
import org.mifos.framework.hibernate.helper.QueryResultLoanAccountIdSearch;
/**
 * @author imtiyazmb
 *
 */
public class SearchDAO {
	
	public QueryResult queryResult = null;
	public QueryInputs queryInputs = null;
	public List listOfCustomerAccounts=null;
	public List listOfLoanAccounts=null;
	public String[] namedQuery=new String[2];
	public java.util.List paramList = new java.util.ArrayList();
	private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.COLLECTIONSHEETLOGGER);
	
	public QueryResult search(String searchType,String searchString, Short userLevelId,String officeSearchId ,Short UserId ,Short officeId) throws SystemException, ApplicationException 
	{	
		try{			
			List listOfLoanAccounts = loanAccountIdSearch(searchString.trim() , officeId);				
			if(listOfLoanAccounts==null || listOfLoanAccounts.size()==0)
			{	
				List listOfCustomerAccounts = customerIdSearch(searchString.trim() , officeId);				
				if((listOfCustomerAccounts==null || listOfCustomerAccounts.size()==0) && searchType != null)
				{							 
					customerSearch(searchType,searchString.trim(),userLevelId,officeSearchId,UserId,officeId);								 
				}
			}			
		}		
		catch(HibernateProcessException  hpe) {
				throw new SystemException();
			}
		return queryResult;
	}	
	
	public List loanAccountIdSearch(String searchString,Short officeId) throws HibernateSearchException, SystemException
	{
		
		queryResult=QueryFactory.getQueryResult(CustomerSearchConstants.LOANACCOUNTIDSEARCH);
		queryInputs = new QueryInputs(); 
		String[] aliasNames = {"customerId","centerName" , "centerGlobalCustNum" , "customerType" , "branchGlobalNum",
 				 "branchName" , "loanOfficerName" , "loanOffcerGlobalNum","customerStatus",
 				 "groupName","groupGlobalCustNum","clientName","clientGlobalCustNum","loanGlobalAccountNumber"};						 
		queryInputs.setPath("org.mifos.application.customer.util.valueobjects.CustomerSearch");
		queryInputs.setAliasNames(aliasNames);	
		queryResult.setQueryInputs(queryInputs);
		if(officeId!=null)
		{
			listOfLoanAccounts=((QueryResultLoanAccountIdSearch)queryResult).accountIdSearch(searchString,officeId);
		}
		return listOfLoanAccounts;
	}
	
	public List customerIdSearch(String searchString,Short officeId) throws HibernateSearchException, SystemException
	{
		queryResult=QueryFactory.getQueryResult(CustomerSearchConstants.IDSEARCH);
		queryInputs = new QueryInputs(); 
		String[] Names = {"customerId","centerName" , "centerGlobalCustNum" , "customerType" , "branchGlobalNum",
 				 "branchName" , "loanOfficerName" , "loanOffcerGlobalNum","customerStatus",
 				 "groupName","groupGlobalCustNum","clientName","clientGlobalCustNum","loanGlobalAccountNumber"};						 
		queryInputs.setPath("org.mifos.application.customer.util.valueobjects.CustomerSearch");
		queryInputs.setAliasNames(Names);	
		queryResult.setQueryInputs(queryInputs);	
		if(officeId!=null)
		{
			listOfCustomerAccounts=((QueryResultIdSearch)queryResult).customerIdSearch(searchString,officeId);
		}
		return listOfCustomerAccounts;
	}
	public void customerSearch(String searchType,String str, Short userLevelId,String officeSearchId ,Short UserId ,Short officeId) throws SystemException, ApplicationException
	{	
		String searchString = null;
		if(str.contains("%") && str.length() > 1)
		{
			searchString = str.replace("%","\\%");			
		}
		else
		{
			searchString = str;
		}
		queryResult = QueryFactory.getQueryResult(searchType);
		queryInputs = new QueryInputs(); 
		if(searchType.equals(PersonnelConstants.USER_LIST))
		{
			intiliatizeQueryInputsForPersonnelSearch(searchString,userLevelId,officeSearchId,UserId,officeId);
		}
		else if(searchType.equals(CustomerSearchConstants.CENTERSEARCH))
		{
			intiliatizeQueryInputsForCenterSearch(searchString,userLevelId,officeSearchId,UserId,officeId);
		}
		else if(searchType.equals(CustomerSearchConstants.GROUPLIST))
		{
			intiliatizeQueryInputsForGroupSearch(searchString,userLevelId,officeSearchId,UserId,officeId);
		}
		else if(searchType.equals(CustomerSearchConstants.ACCOUNTSEARCHRESULTS))
		{
			intiliatizeQueryInputsForClientOrGroupSearch(searchString,userLevelId,officeSearchId,UserId,officeId);
		}
		else if(searchType.equals(CustomerSearchConstants.CUSTOMERSEARCHRESULTS))
		{
			intiliatizeQueryInputsForCustomerSearch(searchString,userLevelId,officeSearchId,UserId,officeId);
		}
		else if(searchType.equals(CustomerSearchConstants.CUSTOMERSFORSAVINGSACCOUNT))
		{
			intiliatizeQueryInputsForCustomerSearchForSavings(searchString,userLevelId,officeSearchId,UserId,officeId);
		}
		queryInputs.setParamList(paramList);
		queryResult.setQueryInputs(queryInputs);
	}
	
	public void intiliatizeQueryInputsForPersonnelSearch(String searchString, Short userLevelId,String officeSearchId ,Short UserId ,Short officeId) throws SystemException
	{
		namedQuery[0]=NamedQueryConstants.COUNT_SEARCH_PERSONNEL;
		namedQuery[1]=NamedQueryConstants.SEARCH_PERSONNEL;	
		paramList.add(typeNameValue("String","USER_NAME",searchString+"%"));
		paramList.add(typeNameValue("String","SEARCH_ID",officeSearchId));
		paramList.add(typeNameValue("String","SEARCH_ALL",officeSearchId+".%"));
		paramList.add(typeNameValue("Short","USERID",UserId));
		paramList.add(typeNameValue("Short","LOID",PersonnelConstants.LOAN_OFFICER));
		paramList.add(typeNameValue("Short","USERLEVEL_ID",userLevelId));
		String[] aliasNames = {"officeId" , "officeName" , "personnelId" , "globalPersonnelNum","personnelName"};		
		queryInputs.setQueryStrings(namedQuery);			
		queryInputs.setPath("org.mifos.application.personnel.util.valueobjects.UserSearchResults");	
		queryInputs.setAliasNames(aliasNames);
	}
	public void intiliatizeQueryInputsForCenterSearch(String searchString, Short userLevelId,String officeSearchId ,Short UserId ,Short officeId) throws SystemException
	{		
		namedQuery[0]=NamedQueryConstants.COUNT_SEARCH_CENTERS;
		namedQuery[1]=NamedQueryConstants.SEARCH_CENTERS;			
		paramList.add(typeNameValue("String","SEARCH_ID",officeSearchId+"%"));	
		paramList.add(typeNameValue("String","CENTER_NAME",searchString+"%"));
		paramList.add(typeNameValue("Short","LEVEL_ID",CustomerConstants.CENTER_LEVEL_ID));
		paramList.add(typeNameValue("Short","STATUS_ID",CustomerConstants.CENTER_ACTIVE_STATE));
		paramList.add(typeNameValue("Short","USER_ID",UserId));
		paramList.add(typeNameValue("Short","USER_LEVEL_ID",userLevelId));
		paramList.add(typeNameValue("Short","LO_LEVEL_ID",PersonnelConstants.LOAN_OFFICER));
		String[] aliasNames = {"parentOfficeId" , "parentOfficeName" , "centerSystemId" , "centerName"};
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setPath("org.mifos.application.customer.center.util.valueobjects.CenterSearchResults");
		queryInputs.setAliasNames(aliasNames);	
	}
	public void intiliatizeQueryInputsForGroupSearch(String searchString, Short userLevelId,String officeSearchId ,Short UserId ,Short officeId) throws SystemException
	{	
		if(Configuration.getInstance().getCustomerConfig(officeId).isCenterHierarchyExists()){
			namedQuery[0]=NamedQueryConstants.COUNT_GROUP_SEARCH_WITH_CENTER;
			namedQuery[1]=NamedQueryConstants.GROUP_SEARCH_WITH_CENTER;
			String[] aliasNames = {"officeName" , "groupName" , "centerName","groupId" };
			queryInputs.setAliasNames(aliasNames);
		}
		else
		{
			namedQuery[0]=NamedQueryConstants.COUNT_GROUP_SEARCH_WITHOUT_CENTER;
			namedQuery[1]=NamedQueryConstants.GROUP_SEARCH_WITHOUT_CENTER;
			String[] aliasNames = {"officeName" , "groupName" ,"groupId" };
			queryInputs.setAliasNames(aliasNames);
		}						
		paramList.add(typeNameValue("String","SEARCH_ID",officeSearchId+"%"));
		paramList.add(typeNameValue("String","SEARCH_STRING",searchString+"%"));
		paramList.add(typeNameValue("Short","LEVEL_ID",CustomerConstants.GROUP_LEVEL_ID));
		paramList.add(typeNameValue("Short","STATUS1",GroupConstants.CANCELLED));
		paramList.add(typeNameValue("Short","STATUS2",GroupConstants.CLOSED));
		paramList.add(typeNameValue("Short","USER_ID",UserId));
		paramList.add(typeNameValue("Short","USER_LEVEL_ID",userLevelId));
		paramList.add(typeNameValue("Short","LO_LEVEL_ID",PersonnelConstants.LOAN_OFFICER));
		
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setPath("org.mifos.application.customer.group.util.valueobjects.GroupSearchResults");
		
	}
	public void intiliatizeQueryInputsForClientOrGroupSearch(String searchString, Short userLevelId,String officeSearchId ,Short UserId ,Short officeId) throws SystemException
	{
		logger.debug("\n\nIn intiliatizeQueryInputsForClientOrGroupSearch of searchDAO ::searchString="+searchString+"\tuserLevleId="+userLevelId+"\tofficeSearchId="+officeSearchId+"\tuserid="+UserId+"\tofficeId="+officeId);	
		if (userLevelId.shortValue()==PersonnelConstants.LOAN_OFFICER ){
			if(clientsExists()){
				namedQuery[0]=NamedQueryConstants.LEVEL_COUNT_ACCOUNTSEARCH;
				namedQuery[1]=NamedQueryConstants.LEVEL_ACCOUNTSEARCH;				
				paramList.add(typeNameValue("Short","PERSONNEL_ID",UserId));				
				paramList.add(typeNameValue("Short","CLIENTACTIVE",CustomerConstants.CLIENT_APPROVED));
				paramList.add(typeNameValue("Short","CLIENTNAMETYPE",ClientConstants.CLIENT_NAME_TYPE));
			}
			else{
				namedQuery[0]=NamedQueryConstants.LEVEL_COUNT_ACCOUNTSEARCH_NOCLIENTS;
				namedQuery[1]=NamedQueryConstants.LEVEL_ACCOUNTSEARCH_NOCLIENTS;				
				paramList.add(typeNameValue("Short","PERSONNEL_ID",UserId));				
			}		
		}
		else{
			if(clientsExists()){
				logger.debug("\n\nBefore setting Parameters");
				namedQuery[0]=NamedQueryConstants.COUNT_ACCOUNTSEARCH;
				namedQuery[1]=NamedQueryConstants.ACCOUNTSEARCH;				
				paramList.add(typeNameValue("Short","CLIENTACTIVE",CustomerConstants.CLIENT_APPROVED));
				paramList.add(typeNameValue("Short","CLIENTNAMETYPE",ClientConstants.CLIENT_NAME_TYPE));
			}
			else{
				namedQuery[0]=NamedQueryConstants.COUNT_ACCOUNTSEARCH_NOCLIENTS;
				namedQuery[1]=NamedQueryConstants.ACCOUNTSEARCH_NOCLIENTS;
			}
		}
		
		paramList.add(typeNameValue("String","SEARCH_ID",officeSearchId+"%"));
		paramList.add(typeNameValue("String","SEARCH_STRING",searchString+"%"));
		paramList.add(typeNameValue("Short","LEVELID",CustomerConstants.CLIENT_LEVEL_ID));
		paramList.add(typeNameValue("Short","GROUPLEVELID",CustomerConstants.GROUP_LEVEL_ID));
		paramList.add(typeNameValue("Short","GROUPACTIVE",CustomerConstants.GROUP_ACTIVE_STATE));
		paramList.add(typeNameValue("Boolean","GROUP_LOAN_ALLOWED",Configuration.getInstance().getCustomerConfig(officeId).canGroupApplyForLoan()));
		
		String[] aliasNames = { "clientName", "clientId", "groupName","centerName", "officeName","globelNo" };
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setPath("org.mifos.application.accounts.util.valueobjects.AccountSearchResults");
		queryInputs.setAliasNames(aliasNames);	
	}
	public void intiliatizeQueryInputsForCustomerSearch(String searchString, Short userLevelId,String officeSearchId ,Short UserId ,Short officeId) throws SystemException
	{
		if(officeId.shortValue()!=0)
		{				
			if(clientsExists())
			{
			namedQuery[0]=NamedQueryConstants.COUNT_CUSTOMERSEARCH;
			namedQuery[1]=NamedQueryConstants.CUSTOMERSEARCH;				
			paramList.add(typeNameValue("Short","OFFICEID",officeId));				
			paramList.add(typeNameValue("Short","CLIENTNAMETYPE",ClientConstants.CLIENT_NAME_TYPE));
			}
			else
			{
				namedQuery[0]=NamedQueryConstants.COUNT_CUSTOMERSEARCH_NOCLIENTS;
				namedQuery[1]=NamedQueryConstants.CUSTOMERSEARCH_NOCLIENTS;				
				paramList.add(typeNameValue("Short","OFFICEID",officeId));				
			}
			
		}
		else
		{				
			if(clientsExists())
			{					
				namedQuery[0]=NamedQueryConstants.COUNT_CUSTOMERSEARCH_NOOFFICEID;
				namedQuery[1]=NamedQueryConstants.CUSTOMERSEARCH_NOOFFICEID;					
				paramList.add(typeNameValue("Short","CLIENTNAMETYPE",ClientConstants.CLIENT_NAME_TYPE));
				paramList.add(typeNameValue("String","OFFICE_SEARCH_ID",officeSearchId+"%"));
			}
			else
			{				
				namedQuery[0]=NamedQueryConstants.COUNT_CUSTOMERSEARCH_NOOFFICEID_NOCLIENTS;
				namedQuery[1]=NamedQueryConstants.CUSTOMERSEARCH_NOOFFICEID_NOCLIENTS;										
				paramList.add(typeNameValue("String","OFFICE_SEARCH_ID",officeSearchId+"%"));
			}
		}			
		paramList.add(typeNameValue("Short","USERID",UserId));
		paramList.add(typeNameValue("Short","LOID",PersonnelConstants.LOAN_OFFICER));
		paramList.add(typeNameValue("Short","LEVELID",CustomerConstants.CLIENT_LEVEL_ID));
		paramList.add(typeNameValue("Short","USERLEVEL_ID",userLevelId));
		paramList.add(typeNameValue("String","SEARCH_STRING",searchString+"%"));
		
		String[] aliasNames = {"customerId","centerName" , "centerGlobalCustNum" , "customerType" , "branchGlobalNum",
 				 "branchName" , "loanOfficerName" , "loanOffcerGlobalNum","customerStatus",
 				 "groupName","groupGlobalCustNum","clientName","clientGlobalCustNum","loanGlobalAccountNumber"};
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setPath("org.mifos.application.customer.util.valueobjects.CustomerSearch");
		queryInputs.setAliasNames(aliasNames);
	}
	public void intiliatizeQueryInputsForCustomerSearchForSavings(String searchString, Short userLevelId,String officeSearchId ,Short UserId ,Short officeId) throws SystemException
	{
		if (userLevelId.shortValue()==PersonnelConstants.LOAN_OFFICER )
		{		
			if(clientsExists())
			{			
				namedQuery[0]=NamedQueryConstants.COUNT_CUSTOMERSFORSAVINGSACCOUNT;
				namedQuery[1]=NamedQueryConstants.CUSTOMERSFORSAVINGSACCOUNT;				
				paramList.add(typeNameValue("String","SEARCH_ID",officeSearchId+"%"));
				paramList.add(typeNameValue("String","SEARCH_STRING",searchString+"%"));
				paramList.add(typeNameValue("Short","PERSONNEL_ID",UserId));				
				paramList.add(typeNameValue("Short","LEVELID",CustomerConstants.CLIENT_LEVEL_ID));					
				paramList.add(typeNameValue("Short","GROUPLEVELID",CustomerConstants.GROUP_LEVEL_ID));
				paramList.add(typeNameValue("Short","CENTERLEVELID",CustomerConstants.CENTER_LEVEL_ID));
				paramList.add(typeNameValue("Short","CENTERACTIVE",CustomerConstants.CENTER_ACTIVE_STATE));
				paramList.add(typeNameValue("Short","GROUPACTIVE",CustomerConstants.GROUP_ACTIVE_STATE));
				paramList.add(typeNameValue("Short","CLIENTACTIVE",CustomerConstants.CLIENT_APPROVED));
				paramList.add(typeNameValue("Short","CLIENTNAMETYPE",ClientConstants.CLIENT_NAME_TYPE));
				
			}
			else
			{				
				namedQuery[0]=NamedQueryConstants.COUNT_CUSTOMERSFORSAVINGSACCOUNT_NOCLIENTS;
				namedQuery[1]=NamedQueryConstants.CUSTOMERSFORSAVINGSACCOUNT_NOCLIENTS;				
				paramList.add(typeNameValue("String","SEARCH_ID",officeSearchId+"%"));
				paramList.add(typeNameValue("String","SEARCH_STRING",searchString+"%"));
				paramList.add(typeNameValue("Short","PERSONNEL_ID",UserId));
				paramList.add(typeNameValue("Short","GROUPLEVELID",CustomerConstants.GROUP_LEVEL_ID));
				paramList.add(typeNameValue("Short","CENTERLEVELID",CustomerConstants.CENTER_LEVEL_ID));
				paramList.add(typeNameValue("Short","CENTERACTIVE",CustomerConstants.CENTER_ACTIVE_STATE));
				paramList.add(typeNameValue("Short","GROUPACTIVE",CustomerConstants.GROUP_ACTIVE_STATE));
				
			}
		
		}
		else
		{				
			if(clientsExists())
			{			
				namedQuery[0]=NamedQueryConstants.COUNT_CUSTOMERSFORSAVINGSACCOUNTNONLO;
				namedQuery[1]=NamedQueryConstants.CUSTOMERSFORSAVINGSACCOUNTNONLO;				
				paramList.add(typeNameValue("String","SEARCH_ID",officeSearchId+"%"));
				paramList.add(typeNameValue("String","SEARCH_STRING",searchString+"%"));
				paramList.add(typeNameValue("Short","LEVELID",CustomerConstants.CLIENT_LEVEL_ID));
				paramList.add(typeNameValue("Short","GROUPLEVELID",CustomerConstants.GROUP_LEVEL_ID));
				paramList.add(typeNameValue("Short","CENTERLEVELID",CustomerConstants.CENTER_LEVEL_ID));
				paramList.add(typeNameValue("Short","CENTERACTIVE",CustomerConstants.CENTER_ACTIVE_STATE));
				paramList.add(typeNameValue("Short","GROUPACTIVE",CustomerConstants.GROUP_ACTIVE_STATE));
				paramList.add(typeNameValue("Short","CLIENTACTIVE",CustomerConstants.CLIENT_APPROVED));
				paramList.add(typeNameValue("Short","CLIENTNAMETYPE",ClientConstants.CLIENT_NAME_TYPE));
			}
			else
			{				
				namedQuery[0]=NamedQueryConstants.COUNT_CUSTOMERSFORSAVINGSACCOUNTNONLO_NOCLIENTS;
				namedQuery[1]=NamedQueryConstants.CUSTOMERSFORSAVINGSACCOUNTNONLO_NOCLIENTS;				
				paramList.add(typeNameValue("String","SEARCH_ID",officeSearchId+"%"));
				paramList.add(typeNameValue("String","SEARCH_STRING",searchString+"%"));
				paramList.add(typeNameValue("Short","GROUPLEVELID",CustomerConstants.GROUP_LEVEL_ID));
				paramList.add(typeNameValue("Short","CENTERLEVELID",CustomerConstants.CENTER_LEVEL_ID));
				paramList.add(typeNameValue("Short","CENTERACTIVE",CustomerConstants.CENTER_ACTIVE_STATE));
				paramList.add(typeNameValue("Short","GROUPACTIVE",CustomerConstants.GROUP_ACTIVE_STATE));	
				
			}
		}	
		String[] aliasNames = { "clientName", "clientId", "groupName","centerName", "officeName","globelNo" };
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setPath("org.mifos.application.accounts.util.valueobjects.AccountSearchResults");
		queryInputs.setAliasNames(aliasNames);	
	}
	
	public boolean clientsExists() throws HibernateProcessException	
	{
		Session session = null;
		Query query = null;		
		session = HibernateUtil.getSession();
		query = session.createQuery("select count(*) from org.mifos.application.customer.util.valueobjects.CustomerNameDetail");
		Integer noOfClients =  (Integer) query.uniqueResult();		
		if(noOfClients > 0)
		{
			return true;
		}			
		return false;			
	}
	public Param typeNameValue(String type,String name,Object value)
	{
		return new Param(type,name,value);
	}
	
}
	