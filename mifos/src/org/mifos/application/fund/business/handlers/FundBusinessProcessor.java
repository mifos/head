/**

 * FundBusinessProcessor  version: 1.0



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

package org.mifos.application.fund.business.handlers;

import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.dao.CustomerUtilDAO;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.fund.dao.FundDAO;
import org.mifos.application.fund.exception.FundException;
import org.mifos.application.fund.util.helpers.FundConstants;
import org.mifos.application.fund.util.valueobjects.Fund;
import org.mifos.framework.business.handlers.MifosBusinessProcessor;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.util.valueobjects.Context;

/**
 * FundBusinessProcessor contains the business logic for funds
 */

public class FundBusinessProcessor extends MifosBusinessProcessor {

	/**An instance of the logger which is used to log statements */
	private MifosLogger logger =MifosLogManager.getLogger(LoggerConstants.FUNDLOGGER);
	
	/**
	 * This method is called when the create fund page is loaded. This loads the list of GL codes for the drop 
	 * down box.   
	 *@param context
	 *@throws SystemException
	 *@throws ApplicationException
	 **/
	public void loadInitial(Context context) throws SystemException,
	ApplicationException {
		MifosLogManager.getLogger(LoggerConstants.FUNDLOGGER).debug("Inside FundBusinessProcessor loadInitial method:");
		FundDAO fundDAO= (FundDAO) getDAO(context.getPath());
		context.addAttribute(fundDAO.getGlCodeList());
		
	}
	/**
	 *This method loads all the funds that is available in the system. this is called when their tries to view all funds 
	 *@param context
	 *@throws SystemException
	 *@throws ApplicationException
	 **/
	public void getAllFunds(Context context) throws SystemException,ApplicationException
	{
		MifosLogManager.getLogger(LoggerConstants.FUNDLOGGER).debug("Inside FundBusinessProcessor getAllFunds method:");
		FundDAO fundDAO= (FundDAO) getDAO(context.getPath());
		context.addAttribute(fundDAO.getAllFunds());
	}
	
	/**
	 * This method is called before the actual create. Here a check is made as to whether the fund name that has 
	 * been entered, already exists. If it does a exception is thrown and the error message is shown to the user
	 * param context Parameters passed from the action to the business processor
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void createInitial(Context context)throws SystemException,ApplicationException{
		FundDAO fundDAO = null;
		CustomerUtilDAO customerUtilDAO = new CustomerUtilDAO(); 
		boolean fundNameExists = false;
		
		try{
			fundDAO = (FundDAO)getDAO(context.getPath());	
				//making a check to see if the center name exists. If it exists an exception is thrown
				String name = ((Fund)context.getValueObject()).getFundName();
				fundNameExists = fundDAO.ifFundNameExists(name);
				if(fundNameExists ==true){
					Object[] values = new Object[1];
					values[0] = name;
					throw new FundException(FundConstants.DUPLICATE_FUNDNAME_EXCEPTION,values);
					
				}
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
	
	/**
	 * This method is called before the actual update. Here a check is made as to whether the fund name that has 
	 * been entered during an edit, already exists. If it does a exception is thrown and the error message is shown to the user
	 * param context Parameters passed from the action to the business processor
	 * @throws SystemException
	 * @throws ApplicationException
	 */
	public void updateInitial(Context context)throws SystemException,ApplicationException{
		FundDAO fundDAO = null;
		CustomerUtilDAO customerUtilDAO = new CustomerUtilDAO(); 
		boolean fundNameExists = false;
		
		try{
			fundDAO = (FundDAO)getDAO(context.getPath());	
				//making a check to see if the center name exists. If it exists an exception is thrown
				String name = ((Fund)context.getValueObject()).getFundName();
				String oldName = (String)context.getBusinessResults("OldFundName");
				if(!name.equals(oldName)){
					fundNameExists = fundDAO.ifFundNameExists(name);
					if(fundNameExists ==true){
						Object[] values = new Object[1];
						values[0] = name;
						throw new FundException(FundConstants.DUPLICATE_FUNDNAME_EXCEPTION,values);
						
					}
				}
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
