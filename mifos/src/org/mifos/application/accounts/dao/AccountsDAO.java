/**

 * AccountsDAO.java    version: xxx

 

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

package org.mifos.application.accounts.dao;

import java.util.HashMap;
import java.util.List;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.SystemException;

/**
 * @author ashishsm
 *
 */
public class AccountsDAO extends DAO {

	/**
	 * 
	 */
	public AccountsDAO() {
		super();
		
	}
	
	/**
	 * This retrieves the states which are currently in use.If there are no such states it returns null.
	 * @return
	 * @throws SystemException
	 */
	public List getStatesCurrentlyInUse(Short prdTypeId)throws SystemException{
		List statesToReturn = null;
		//preparing parametes to pass.
		HashMap queryParameters = new HashMap();
		queryParameters.put(AccountConstants.PRDTYPEID, prdTypeId);
		 statesToReturn = DAO.executeNamedQuery(NamedQueryConstants.ACCOUNTSTATESINUSE, queryParameters);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Inside getStatesCurrently in use method");
		
		return statesToReturn;
	}
	
	/**
	 * Retrieves all possible states for  a prdType irrespective of whether it is currently in use or not.
	 * @param prdType
	 * @return
	 * @throws SystemException
	 */
	public List getAllPossibleStates(Short prdTypeId)throws SystemException{
		List statesToReturn = null;
		//preparing parametes to pass.
		HashMap queryParameters = new HashMap();
		queryParameters.put(AccountConstants.PRDTYPEID, prdTypeId);
		 statesToReturn = DAO.executeNamedQuery(NamedQueryConstants.ALLACCOUNTSTATES, queryParameters);
		MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER).debug("Inside getStatesCurrently in use method");
		
		return statesToReturn;
		
	}

}
