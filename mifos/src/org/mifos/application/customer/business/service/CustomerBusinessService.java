/**

 * CustomerBusinessService.java    version: 1.0

 

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
package org.mifos.application.customer.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.CustomerActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityEntity;
import org.mifos.application.accounts.loan.business.LoanActivityView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.util.valueobjects.RecentAccountActivity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPerformanceHistoryView;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.customer.util.helpers.CustomerRecentActivityView;
import org.mifos.application.customer.util.helpers.LoanCycleCounter;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.PersistenceServiceName;

public class CustomerBusinessService extends BusinessService{
	private CustomerPersistenceService dbService;
	
	public CustomerBusinessService()throws ServiceException{
		
	}
	public BusinessObject getBusinessObject(UserContext userContext) {
		return new SavingsBO(userContext);
	}
	
	public CustomerBO getCustomer(Integer customerId)throws ServiceException{
		return getDBService().getCustomer(customerId);
    }
	
	public CustomerBO findBySystemId(String globalCustNum) throws PersistenceException, ServiceException {
		return getDBService().findBySystemId(globalCustNum);
	}
	public CustomerBO getBySystemId(String globalCustNum,Short levelId) throws PersistenceException, ServiceException {
		return getDBService().getBySystemId(globalCustNum,levelId);
	}	
	private CustomerPersistenceService getDBService()throws ServiceException{
		if(dbService==null){
			dbService=(CustomerPersistenceService) ServiceFactory.getInstance().getPersistenceService(
					PersistenceServiceName.Customer);
		}
		return dbService;
	}
	
	public  List<LoanCycleCounter> fetchLoanCycleCounter(Integer customerId)throws SystemException{
		return getDBService().fetchLoanCycleCounter(customerId);
		
	}
	
	public CustomerPerformanceHistoryView getLastLoanAmount(Integer customerId) throws  PersistenceException, ServiceException{
		return getDBService().getLastLoanAmount(customerId);
	}
	public CustomerPerformanceHistoryView numberOfMeetings(boolean isPresent , Integer customerId)throws HibernateProcessException, ServiceException{
		return getDBService().numberOfMeetings(isPresent,customerId);
	}
	
	public double getLastTrxnAmnt(String globalCustNum) throws PersistenceException, ServiceException {
		return findBySystemId(globalCustNum).getCustomerAccount().getLastPmntAmnt();
	}
	
	public List<CustomerRecentActivityView> getRecentActivityView(Integer customerId) throws SystemException, ApplicationException {
		CustomerBO customerBO = getDBService().getCustomer(customerId);
		Set<CustomerActivityEntity> customerAtivityDetails = customerBO.getCustomerAccount().getCustomerActivitDetails();
		List<CustomerRecentActivityView> customerActivityViewList = new ArrayList<CustomerRecentActivityView>();
		
		int count=0;
		for(CustomerActivityEntity customerActivityEntity : customerAtivityDetails) {
			customerActivityViewList.add(getCustomerActivityView(customerActivityEntity));
			if(++count == 3)
				break;
		}
		return customerActivityViewList;
	}
	
	public List<CustomerRecentActivityView> getAllActivityView(String globalCustNum) throws SystemException, ApplicationException {
		CustomerBO customerBO = findBySystemId(globalCustNum);
		Set<CustomerActivityEntity> customerAtivityDetails = customerBO.getCustomerAccount().getCustomerActivitDetails();
		List<CustomerRecentActivityView> customerActivityViewList = new ArrayList<CustomerRecentActivityView>();
		for(CustomerActivityEntity customerActivityEntity : customerAtivityDetails) {
			customerActivityViewList.add(getCustomerActivityView(customerActivityEntity));
		}
		return customerActivityViewList;
	}
	
	private CustomerRecentActivityView getCustomerActivityView(CustomerActivityEntity customerActivityEntity) {
		CustomerRecentActivityView customerRecentActivityView = new CustomerRecentActivityView();
		customerRecentActivityView.setActivityDate(customerActivityEntity.getCreatedDate());
		customerRecentActivityView.setDescription(customerActivityEntity.getDescription());
		customerRecentActivityView.setAmount(removeSign(customerActivityEntity.getAmount()));
		customerRecentActivityView.setPostedBy(customerActivityEntity.getPersonnel().getDisplayName());
		return customerRecentActivityView;
	}

	
	private Money removeSign(Money amount){
		if(amount!=null && amount.getAmountDoubleValue()<0)
			return amount.negate();
		else
			return amount;
	}
	
}
