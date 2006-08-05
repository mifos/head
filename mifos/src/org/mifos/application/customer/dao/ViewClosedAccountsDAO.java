/**

 * ViewClosedAccountsDAO.java    version: xxx

 

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

package org.mifos.application.customer.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.framework.dao.DAO;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;

public class ViewClosedAccountsDAO extends DAO {

	public ViewClosedAccountsDAO() {
		super();
		
	}
	
	/**
	 * It returns a list of closed accounts for the the customerId passed.
	 * @param customerId
	 * @return
	 * @throws HibernateProcessException
	 */
	public List getClosedAccounts(Short customerId)throws HibernateProcessException{
		return null;
	}
	
	/**
	 * This method counts active centers whose loan officer is the user, with passed in userId
	 * @param userId  
	 * @return Integer count of active centers under this personnel(Loan Officer). 
	 * @throws SystemException
	 */	
	public Integer getActiveLoanAccountCount(Integer customerId) throws SystemException{
		Integer count ;
		Session session = null;
		try{
			session = HibernateUtil.getSession();
			Query query = session.createQuery("select count(*) from org.mifos.application.accounts.util.valueobjects.Account account where account.customer.customerId=:customerId and ((account.accountTypeId=:accountTypeIdLoan and account.accountStateId!=:status1 and account.accountStateId!=:status2 and account.accountStateId!=:status3 and account.accountStateId!=:status4) or (account.accountTypeId=:accountTypeIdSavings and account.accountStateId!=:status5 and account.accountStateId!=:status6))");
			query.setInteger("customerId", customerId);
			query.setShort("accountTypeIdLoan",
					AccountTypes.LOANACCOUNT.getValue());
			query.setShort("status1",AccountStates.LOANACC_CANCEL);
			query.setShort("status2",AccountStates.LOANACC_BADSTANDING);
			query.setShort("status3",AccountStates.LOANACC_OBLIGATIONSMET);
			query.setShort("status4",AccountStates.LOANACC_WRITTENOFF);
			query.setShort("accountTypeIdSavings",
					AccountTypes.SAVINGSACCOUNT.getValue());
			query.setShort("status5",AccountStates.SAVINGS_ACC_CANCEL);
			query.setShort("status6",AccountStates.SAVINGS_ACC_CLOSED);
			
			Object obj = query.uniqueResult();
			if(obj!=null)
				count = (Integer)obj;
			else
				count=0;
			return count;
			
		}catch(HibernateException he){
			throw he;
		}finally{
			HibernateUtil.closeSession(session);
		}
	}

/*	public Integer getActiveSavingsAccountCount(Integer customerId) throws SystemException{
		Integer count ;
		Session session = null;
		try{
			session = HibernateUtil.getSession();
			Query query = session.createQuery("select count(*) from org.mifos.application.accounts.business.AccountBO account where account.customer.customerId=:customerId and account.accountType.accountTypeId=:accountTypeId and account.accountState.id!=:status1 and account.accountState.id!=:status2 ");
			query.setInteger("customerId", customerId);
			query.setShort("accountTypeId",new Short(AccountTypes.SAVINGSACCOUNT));
			query.setShort("status1",AccountStates.SAVINGS_ACC_CANCEL);
			query.setShort("status2",AccountStates.SAVINGS_ACC_CLOSED);
			Object obj = query.uniqueResult();
			if(obj!=null)
				count = (Integer)obj;
			else
				count=0;
			return count;
			
		}catch(HibernateException he){
			throw he;
		}finally{
			HibernateUtil.closeSession(session);
		}
			
		
	}
	public boolean isCustomerHavingActiveSavingsAccount(int customerId)throws SystemException{
  		if (getActiveSavingsAccountCount(customerId)>0)
  			return true;
  		else
  			return false;
  	} 
	*/
	/** 
	 *  This method is called to check if customer has any active loan or not
	 *  It checks if group has any active loan(loan is considered as active if it is not in closed/cancelled state).
	 *  @param customerId
	 *  @return if yes it returns true, otherwise false 
	 *  @throws SystemException   
	 */	 
  	public boolean isCustomerWithActiveAccounts(int customerId)throws SystemException{
  		if (getActiveLoanAccountCount(customerId).intValue()>0)
  			return true;
  		else
  			return false;
  	} 

  	
}
