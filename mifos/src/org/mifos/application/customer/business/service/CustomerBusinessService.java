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
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.business.CustomerActivityEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPerformanceHistoryView;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.customer.client.business.CustomerPictureEntity;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerRecentActivityView;
import org.mifos.application.customer.util.helpers.LoanCycleCounter;
import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.Money;

public class CustomerBusinessService extends BusinessService {

	public CustomerBusinessService() {

	}

	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public CustomerBO getCustomer(Integer customerId) throws ServiceException {
		return new CustomerPersistence().getCustomer(customerId);
	}

	public CustomerBO findBySystemId(String globalCustNum)
			throws PersistenceException, ServiceException {
		return new CustomerPersistence().findBySystemId(globalCustNum);
	}

	public CustomerBO getBySystemId(String globalCustNum, Short levelId)
			throws PersistenceException, ServiceException {
		return new CustomerPersistence().getBySystemId(globalCustNum, levelId);
	}

	public List<LoanCycleCounter> fetchLoanCycleCounter(Integer customerId)
			throws SystemException {
		return new CustomerPersistence().fetchLoanCycleCounter(customerId);

	}

	public CustomerPerformanceHistoryView getLastLoanAmount(Integer customerId)
			throws PersistenceException, ServiceException {
		return new CustomerPersistence().getLastLoanAmount(customerId);
	}

	public CustomerPerformanceHistoryView numberOfMeetings(boolean isPresent,
			Integer customerId) throws HibernateProcessException,
			ServiceException {
		return new CustomerPersistence()
				.numberOfMeetings(isPresent, customerId);
	}

	public double getLastTrxnAmnt(String globalCustNum)
			throws PersistenceException, ServiceException {
		return findBySystemId(globalCustNum).getCustomerAccount()
				.getLastPmntAmnt();
	}

	public List<CustomerRecentActivityView> getRecentActivityView(
			Integer customerId) throws SystemException, ApplicationException {
		CustomerBO customerBO = new CustomerPersistence()
				.getCustomer(customerId);
		Set<CustomerActivityEntity> customerAtivityDetails = customerBO
				.getCustomerAccount().getCustomerActivitDetails();
		List<CustomerRecentActivityView> customerActivityViewList = new ArrayList<CustomerRecentActivityView>();

		int count = 0;
		for (CustomerActivityEntity customerActivityEntity : customerAtivityDetails) {
			customerActivityViewList
					.add(getCustomerActivityView(customerActivityEntity));
			if (++count == 3)
				break;
		}
		return customerActivityViewList;
	}

	public List<CustomerRecentActivityView> getAllActivityView(
			String globalCustNum) throws SystemException, ApplicationException {
		CustomerBO customerBO = findBySystemId(globalCustNum);
		Set<CustomerActivityEntity> customerAtivityDetails = customerBO
				.getCustomerAccount().getCustomerActivitDetails();
		List<CustomerRecentActivityView> customerActivityViewList = new ArrayList<CustomerRecentActivityView>();
		for (CustomerActivityEntity customerActivityEntity : customerAtivityDetails) {
			customerActivityViewList
					.add(getCustomerActivityView(customerActivityEntity));
		}
		return customerActivityViewList;
	}

	private CustomerRecentActivityView getCustomerActivityView(
			CustomerActivityEntity customerActivityEntity) {
		CustomerRecentActivityView customerRecentActivityView = new CustomerRecentActivityView();
		customerRecentActivityView.setActivityDate(customerActivityEntity
				.getCreatedDate());
		customerRecentActivityView.setDescription(customerActivityEntity
				.getDescription());
		customerRecentActivityView.setAmount(removeSign(customerActivityEntity
				.getAmount()));
		if (customerActivityEntity.getPersonnel() != null)
			customerRecentActivityView.setPostedBy(customerActivityEntity
					.getPersonnel().getDisplayName());
		return customerRecentActivityView;
	}

	private Money removeSign(Money amount) {
		if (amount != null && amount.getAmountDoubleValue() < 0)
			return amount.negate();
		else
			return amount;
	}

	private List<AccountBO> getAccountsForCustomer(String searchId,
			Short officeId, Short accountTypeId) throws PersistenceException,
			ServiceException {
		return new CustomerPersistence().retrieveAccountsUnderCustomer(
				searchId, officeId, accountTypeId);
	}

	private Money getTotalOutstandingLoan(List<AccountBO> accountList)
			throws PersistenceException, ServiceException {
		Money total = new Money();
		for (AccountBO accountBO : accountList) {
			LoanBO loanBO = (LoanBO) accountBO;
			if (loanBO.isAccountActive()) {
				for (AccountActionDateEntity accountActionDateEntity : loanBO
						.getAccountActionDates()) {
					total = total
							.add(((LoanScheduleEntity) accountActionDateEntity)
									.getPrincipal());
				}
			}
		}
		return total;
	}

	private Money getPortfolioAtRisk(List<AccountBO> accountList)
			throws PersistenceException, ServiceException {
		Money amount = new Money();
		for (AccountBO account : accountList) {
			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue())
					&& ((LoanBO) account).isAccountActive()) {
				LoanBO loan = (LoanBO) account;
				if (loan.hasPortfolioAtRisk()) {
					amount = getBalanceForPortfolioAtRisk(accountList);
					break;
				}
			}
		}
		return amount;
	}

	private Money getBalanceForPortfolioAtRisk(List<AccountBO> accountList)
			throws PersistenceException, ServiceException {
		Money amount = new Money();
		for (AccountBO account : accountList) {
			if (account.getAccountType().getAccountTypeId().equals(
					AccountTypes.LOANACCOUNT.getValue())
					&& ((LoanBO) account).isAccountActive()) {
				LoanBO loan = (LoanBO) account;
				amount = amount.add(loan.getRemainingPrincipalAmount());
			}
		}
		return amount;
	}

	private Money getTotalSavings(List<AccountBO> accountList)
			throws PersistenceException, ServiceException {
		Money total = new Money();
		for (AccountBO accountBO : accountList) {
			SavingsBO savingsBO = (SavingsBO) accountBO;
			total = total.add(savingsBO.getSavingsBalance());
		}
		return total;
	}

	private List<CustomerBO> getCustomer(String searchId, Short officeId,
			Short customerLevelId) throws PersistenceException,
			ServiceException {
		return new CustomerPersistence().getAllChildrenForParent(searchId,
				officeId, customerLevelId);
	}

	private List<CustomerBO> getChildList(List<CustomerBO> centerChildren,
			short childLevelId) {
		List<CustomerBO> children = new ArrayList<CustomerBO>();
		for (int i = 0; i < centerChildren.size(); i++) {
			CustomerBO customer = centerChildren.get(i);
			if (customer.getCustomerLevel().getId().shortValue() == childLevelId)
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
				AccountTypes.LOANACCOUNT.getValue());
		List<AccountBO> savingsList = getAccountsForCustomer(searchId,
				officeId, AccountTypes.SAVINGSACCOUNT.getValue());
		int clientSize = 0;
		int groupSize = 0;
		if (clients != null)
			clientSize = clients.size();
		if (groups != null)
			groupSize = groups.size();
		Money portfolioAtRisk = new Money();
		Money totalOutstandingLoan = getTotalOutstandingLoan(loanList);
		if (totalOutstandingLoan.getAmountDoubleValue() != 0)
			portfolioAtRisk = new Money(String.valueOf(getPortfolioAtRisk(
					loanList).getAmountDoubleValue()
					/ totalOutstandingLoan.getAmountDoubleValue()));
		Money totalSavings = getTotalSavings(savingsList);

		CenterPerformanceHistory centerPerformanceHistory = new CenterPerformanceHistory();
		centerPerformanceHistory
				.setPerformanceHistoryDetails(groupSize, clientSize,
						totalOutstandingLoan, totalSavings, portfolioAtRisk);
		return centerPerformanceHistory;
	}

	public List<CustomerCheckListBO> getStatusChecklist(Short statusId,
			Short customerLevelId) throws PersistenceException,
			ServiceException {
		return new CustomerPersistence().getStatusChecklist(statusId,
				customerLevelId);
	}

	public List<CustomerStatusEntity> retrieveAllCustomerStatusList(
			Short levelId) throws PersistenceException, ServiceException {
		return new CustomerPersistence().retrieveAllCustomerStatusList(levelId);
	}

	public void initializeStateMachine(Short localeId, Short officeId,Short prdTypeId,
			Short levelId) throws StatesInitializationException {
		AccountStateMachines.getInstance().initialize(localeId, officeId,prdTypeId,levelId);
	}

	public String getStatusName(Short localeId, Short statusId, Short levelId)
			throws ApplicationException, SystemException {
		return AccountStateMachines.getInstance().getCustomerStatusName(
				localeId, statusId, levelId);
	}

	public String getFlagName(Short localeId,Short flagId, Short levelId)
			throws ApplicationException, SystemException {
		return AccountStateMachines.getInstance().getCustomerFlagName(localeId,flagId,
				levelId);
	}

	public List<CustomerStatusEntity> getStatusList(
			CustomerStatusEntity customerStatusEntity, Short levelId,
			Short localeId) {
		List<CustomerStatusEntity> statusList = AccountStateMachines
				.getInstance().getStatusList(customerStatusEntity, levelId);
		if (null != statusList) {
			for (CustomerStatusEntity customerStatusObj : statusList) {
				customerStatusObj.setLocaleId(localeId);
			}
		}
		return statusList;
	}

	public QueryResult getAllCustomerNotes(Integer customerId)
			throws ApplicationException, SystemException {
		return new CustomerPersistence().getAllCustomerNotes(customerId);
	}

	public List<BusinessActivityEntity> retrieveMasterEntities(
			String entityName, Short localeId) throws PersistenceException {
		return new MasterPersistence().retrieveMasterEntities(entityName,
				localeId);
	}

	public List<PersonnelView> getFormedByPersonnel(Short levelId,
			Short officeId) throws PersistenceException, ServiceException {
		return new CustomerPersistence()
				.getFormedByPersonnel(levelId, officeId);
	}

	public CustomerPictureEntity retrievePicture(Integer customerId)
			throws PersistenceException {
		return new CustomerPersistence().retrievePicture(customerId);
	}

	public void checkPermissionForStatusChange(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) throws SecurityException {
		if (!isPermissionAllowed(newState, userContext, flagSelected,
				recordOfficeId, recordLoanOfficerId))
			throw new SecurityException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	public boolean isPermissionAllowed(Short newState, UserContext userContext,
			Short flagSelected, Short recordOfficeId, Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isStateChangePermittedForAccount(
				newState.shortValue(),
				null != flagSelected ? flagSelected.shortValue() : 0,
				userContext, recordOfficeId, recordLoanOfficerId);
	}
}
