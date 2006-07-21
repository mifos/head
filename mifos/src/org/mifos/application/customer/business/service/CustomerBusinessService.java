/**

 * CustomerBusinessService.java    version: 1.0

 

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
package org.mifos.application.customer.business.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.CustomerActivityEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPerformanceHistoryView;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.persistence.service.CustomerPersistenceService;
import org.mifos.application.customer.util.helpers.CustomerConstants;
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
		if(customerActivityEntity.getPersonnel()!=null)
			customerRecentActivityView.setPostedBy(customerActivityEntity.getPersonnel().getDisplayName());
		return customerRecentActivityView;
	}

	
	private Money removeSign(Money amount){
		if(amount!=null && amount.getAmountDoubleValue()<0)
			return amount.negate();
		else
			return amount;
	}
	
	private List<AccountBO> getAccountsForCustomer(String searchId, Short officeId, Short accountTypeId) throws PersistenceException, ServiceException {
		return getDBService().retrieveAccountsUnderCustomer(searchId,officeId,accountTypeId);
	}
	
	private Money getTotalOutstandingLoan(List<AccountBO> accountList) throws PersistenceException, ServiceException {
		Money total = new Money();
		for(AccountBO accountBO : accountList) {
			LoanBO loanBO = (LoanBO) accountBO;
			if(loanBO.isAccountActive()) {
				for(AccountActionDateEntity accountActionDateEntity : loanBO.getAccountActionDates()) {
					total = total.add(accountActionDateEntity.getPrincipal());
				}
			}
		}
		return total;
	}
	
	private Money getPortfolioAtRisk(List<AccountBO> accountList) throws PersistenceException, ServiceException {
		Money amount = new Money();
		for(AccountBO account : accountList) {
			if(account.getAccountType().getAccountTypeId().equals(AccountConstants.LOAN_TYPE)
					&& ((LoanBO)account).isAccountActive()){
				LoanBO loan=(LoanBO)account;
				if(loan.hasPortfolioAtRisk()) {
					amount = getBalanceForPortfolioAtRisk(accountList);
					break;
				}
			}
		}
		return amount;
	}
	
	private Money getBalanceForPortfolioAtRisk(List<AccountBO> accountList) throws PersistenceException, ServiceException {
		Money amount = new Money();
		for(AccountBO account : accountList) {
			if(account.getAccountType().getAccountTypeId().equals(AccountConstants.LOAN_TYPE)
					&& ((LoanBO)account).isAccountActive()){
				LoanBO loan=(LoanBO)account;
				amount=amount.add(loan.getRemainingPrincipalAmount());
			}
		}
		return amount;
	}
	
	private Money getTotalSavings(List<AccountBO> accountList) throws PersistenceException, ServiceException {
		Money total = new Money();
		for(AccountBO accountBO : accountList) {
			SavingsBO savingsBO = (SavingsBO) accountBO;
			total = total.add(savingsBO.getSavingsBalance());
		}
		return total;
	}
	
	private List<CustomerBO> getCustomer(String searchId, Short officeId, Short customerLevelId) throws PersistenceException, ServiceException {
		return getDBService().getAllChildrenForParent(searchId,officeId,customerLevelId);
	}
	
	private List<CustomerBO> getChildList(List<CustomerBO> centerChildren , short childLevelId){
		List<CustomerBO> children = new ArrayList<CustomerBO>();
		for(int i=0;i<centerChildren.size();i++){
			CustomerBO customer = centerChildren.get(i);
			if( customer.getCustomerLevel().getLevelId().shortValue() == childLevelId )
				children.add(customer);
		}
		return children;
	}
	
	public CenterPerformanceHistory getCenterPerformanceHistory(
			String searchId, Short officeId) throws PersistenceException,
			ServiceException {
		List<CustomerBO> centerChildren = getCustomer(searchId, officeId,
				CustomerConstants.CENTER_LEVEL_ID);
		List<CustomerBO> groups = getChildList(centerChildren,
				CustomerConstants.GROUP_LEVEL_ID);
		List<CustomerBO> clients = getChildList(centerChildren,
				CenterConstants.CLIENT_LEVEL_ID);
		List<AccountBO> loanList = getAccountsForCustomer(searchId, officeId,
				Short.valueOf(AccountTypes.LOANACCOUNT));
		List<AccountBO> savingsList = getAccountsForCustomer(searchId,
				officeId, Short.valueOf(AccountTypes.SAVINGSACCOUNT));
		int clientSize = 0;
		int groupSize = 0;
		if (clients != null)
			clientSize = clients.size();
		if (groups != null)
			groupSize = groups.size();
		Money portfolioAtRisk = new Money();
		Money totalOutstandingLoan = getTotalOutstandingLoan(loanList);
		if (totalOutstandingLoan.getAmountDoubleValue() != 0)
			portfolioAtRisk = new Money(String.valueOf(getPortfolioAtRisk(loanList).getAmountDoubleValue()/totalOutstandingLoan.getAmountDoubleValue()));
		Money totalSavings = getTotalSavings(savingsList);

		CenterPerformanceHistory centerPerformanceHistory = new CenterPerformanceHistory();
		centerPerformanceHistory
				.setPerformanceHistoryDetails(groupSize, clientSize,
						totalOutstandingLoan, totalSavings, portfolioAtRisk);
		return centerPerformanceHistory;
	}
	
}
