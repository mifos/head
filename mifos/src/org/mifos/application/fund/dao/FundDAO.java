/**

 * FundDAO  version: 1.0



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

package org.mifos.application.fund.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.customer.util.helpers.CustomerHelper;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.application.master.util.valueobjects.GLCode;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.dao.helpers.MasterDataRetriever;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.valueobjects.Context;
import org.mifos.framework.util.valueobjects.SearchResults;

/**
 * FundDAO contains code for getting master data and methods which make database calls for creating or updating and 
 * retrieving information corresponding to the Fund module 
 */

public class FundDAO extends DAO {

	/**An instance of the logger which is used to log statements */
	private MifosLogger logger =MifosLogManager.getLogger(LoggerConstants.FUNDLOGGER);
	
	/**
	 * This method retrieves the list of glCodes
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getGlCodeList()throws ApplicationException,SystemException{
		MifosLogManager.getLogger(LoggerConstants.FUNDLOGGER).debug("Inside FundDAO getGlCodeList method:");
		List<GLCode> glCodeList = new ArrayList<GLCode>();
		MasterDataRetriever masterDataRetriever = null;
		try{
			masterDataRetriever = getMasterDataRetriever();
		}
		catch(HibernateProcessException hpe){
			throw new ApplicationException(hpe);
		}
		masterDataRetriever.prepare("masterdata.GlCodes",FundConstants.GLCODELIST);
		SearchResults sr = masterDataRetriever.retrieve();
		glCodeList = (List<GLCode>)sr.getValue();
		Collections.sort(glCodeList,new Comparator() {
				        public int compare(Object o1, Object o2) {
					        String s1 = ((GLCode)o1).getGlCodeValue();
					        String s2 = ((GLCode)o2).getGlCodeValue();
					        return s1.compareToIgnoreCase(s2);
					      }
					  });
		sr.setValue(glCodeList);
		return sr;
	}
	
	/**
	 * This method is called to retrieve all the funds currently present in the database. 
	 * @return
	 * @throws ApplicationException
	 * @throws SystemException
	 */
	public SearchResults getAllFunds() throws ApplicationException,SystemException
	{
		
		 Session session = null;
		 List<Fund> allFunds = null;
		   try{
			   session=HibernateUtil.getSession();
			  // Query query=executeNamedQuery()
			   allFunds =  (List<Fund>)executeNamedQuery(NamedQueryConstants.PRDSRCFUNDS,null,session);
			   Collections.sort(allFunds ,new Comparator() {
				        public int compare(Object o1, Object o2) {
					        String s1 = ((Fund)o1).getFundName();
					        String s2 = ((Fund)o2).getFundName();
					        return s1.compareToIgnoreCase(s2);
					      }
					  });
			   if(null!=allFunds){
				   for(int i=0 ;i<allFunds.size();i++){
					   Fund fund = allFunds.get(i);
					   fund.getGlCode().getGlCodeValue();
				   }
			   }
		   }catch(HibernateProcessException hpe){
			   throw hpe;
		   }
		   finally{
			   HibernateUtil.closeSession(session);
		   }
		   SearchResults sr = new CustomerHelper().getResultObject(FundConstants.ALL_FUNDLIST ,allFunds);
		   return sr;
	}
	
	/**
	 * It creates the ValueObject instance passed in the Context object in the database. 
	 * This method gets the hibernate session , starts a transaction , calls create on hibernate session and 
	 * then commits and closes the hibernate session 
	 * @param context
	 * @throws HibernateProcessException
	 */
	public void create(Context context) throws ApplicationException,SystemException
	{
		super.create(context);
		logger.debug("Inside FundDAO update method:");		
	}

	/**
	 * This method is called to check if a particular fund name already exists in the system. If it does it returns a 
	 * true value else it returns false
	 * @param fundName
	 * @return
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public boolean ifFundNameExists(String fundName) throws SystemException,ApplicationException {
		 
		Session session = null;
		  Query query=null;
		  logger.debug("Fund name: "+ fundName);
		  
		  try{
			  HashMap queryParameters = new HashMap();
			  queryParameters.put("fundname",fundName);
			  List queryResult = executeNamedQuery(NamedQueryConstants.DOES_FUND_NAME_EXIST,queryParameters);
			  if(null!=queryResult){
				  if(queryResult.size()==0 || queryResult.get(0)==null)
					  return false;
				  else
					  return true;
			  }
			  return false;  
			 	  
			  
		  }catch(HibernateProcessException hpe){
			  throw new SystemException(hpe);
		  }finally{
			 HibernateUtil.closeSession(session);
		  }
	}
}
