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

import static org.mifos.framework.util.helpers.NumberUtils.getPercentage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateMachines;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.loan.business.LoanScheduleEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.customer.business.CustomerActivityEntity;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerPerformanceHistoryView;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.center.business.CenterPerformanceHistory;
import org.mifos.application.customer.client.business.CustomerPictureEntity;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerRecentActivityView;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.CustomerStatusFlag;
import org.mifos.application.customer.util.helpers.LoanCycleCounter;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.StatesInitializationException;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Money;

public class CustomerBusinessService implements BusinessService {

	private CustomerPersistence customerPersistence;

	public CustomerBusinessService() {
		this(new CustomerPersistence());
	}

	public CustomerBusinessService(CustomerPersistence customerPersistence) {
		super();
		this.customerPersistence = customerPersistence;
	}

	public static CustomerBusinessService getInstance() {
		return (CustomerBusinessService) ServiceFactory.getInstance()
				.getBusinessService(BusinessServiceName.Customer);
	}

	@Override
	public BusinessObject getBusinessObject(UserContext userContext) {
		return null;
	}

	public QueryResult searchGroupClient(String searchString, Short userId)
			throws ServiceException {
		try {
			return new CustomerPersistence().searchGroupClient(searchString,
					userId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
		catch (ConfigurationException ce) {
			throw new ServiceException(ce);
		}

	}

	public QueryResult searchCustForSavings(String searchString, Short userId)
			throws ServiceException {
		try {
			return new CustomerPersistence().searchCustForSavings(searchString,
					userId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}

	}

	public CustomerBO getCustomer(Integer customerId) throws ServiceException {
		try {
			return new CustomerPersistence().getCustomer(customerId);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public CustomerBO findBySystemId(String globalCustNum)
			throws ServiceException {
		try {
			return new CustomerPersistence().findBySystemId(globalCustNum);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public CustomerBO findBySystemId(String globalCustNum, Short levelId)
			throws ServiceException {
		try {
			return new CustomerPersistence().findBySystemId(globalCustNum,
					levelId);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public List<LoanCycleCounter> fetchLoanCycleCounter(CustomerBO customer)
			throws ServiceException {
		try {
			return customerPersistence.fetchLoanCycleCounter(customer);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}

	}

	public CustomerPerformanceHistoryView getLastLoanAmount(Integer customerId)
			throws ServiceException {
		try {
			return new CustomerPersistence().getLastLoanAmount(customerId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public CustomerPerformanceHistoryView numberOfMeetings(boolean isPresent,
			Integer customerId) throws ServiceException {
		try {
			return new CustomerPersistence().numberOfMeetings(isPresent,
					customerId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public double getLastTrxnAmnt(String globalCustNum) throws ServiceException {
		return findBySystemId(globalCustNum).getCustomerAccount()
				.getLastPmntAmnt();
	}

	public List<CustomerRecentActivityView> getRecentActivityView(
			Integer customerId) throws ServiceException {
		CustomerBO customerBO;
		try {
			customerBO = new CustomerPersistence().getCustomer(customerId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
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
			String globalCustNum) throws ServiceException {
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

	public QueryResult search(String searchString, Short officeId,
			Short userId, Short userOfficeId) throws ServiceException {

		try {
			return new CustomerPersistence().search(searchString, officeId,
					userId, userOfficeId);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}

	}

	private CustomerRecentActivityView getCustomerActivityView(
			CustomerActivityEntity customerActivityEntity) {
		CustomerRecentActivityView customerRecentActivityView = new CustomerRecentActivityView();
		customerRecentActivityView.setActivityDate(customerActivityEntity
				.getCreatedDate());
		customerRecentActivityView.setDescription(customerActivityEntity
				.getDescription());
		Money amount = removeSign(customerActivityEntity.getAmount());
		if (amount.getAmountDoubleValue() == 0)
			customerRecentActivityView.setAmount("-");
		else customerRecentActivityView.setAmount(amount.toString());
		if (customerActivityEntity.getPersonnel() != null)
			customerRecentActivityView.setPostedBy(customerActivityEntity
					.getPersonnel().getDisplayName());
		return customerRecentActivityView;
	}

	private Money removeSign(Money amount) {
		if (amount != null && amount.getAmountDoubleValue() < 0)
			return amount.negate();
		else return amount;
	}

	private List<AccountBO> getAccountsForCustomer(String searchId,
			Short officeId, Short accountTypeId) throws ServiceException {
		try {
			return new CustomerPersistence().retrieveAccountsUnderCustomer(
					searchId, officeId, accountTypeId);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	private Money getTotalOutstandingLoan(List<AccountBO> accountList)
			throws ServiceException {
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

	private Money getTotalUnpaidPrincipal(List<AccountBO> accountList)
			throws ServiceException {
		Money total = new Money();
		for (AccountBO accountBO : accountList) {
			LoanBO loanBO = (LoanBO) accountBO;
			if (loanBO.getState().equals(AccountState.LOAN_ACTIVE_IN_BAD_STANDING)) {
				total = total.add(loanBO.getLoanAmount());
			}
		}
		return total;
	}

	private Money getPortfolioAtRisk(List<AccountBO> accountList)
			throws ServiceException {
		Money amount = new Money();
		for (AccountBO account : accountList) {
			if (account.getType() == AccountTypes.LOAN_ACCOUNT
					&& ((LoanBO) account).isAccountActive()
					&& ((LoanBO) account).getDaysInArrears() > 1) {
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
			throws ServiceException {
		Money amount = new Money();
		for (AccountBO account : accountList) {
			if (account.getType() == AccountTypes.LOAN_ACCOUNT
					&& ((LoanBO) account).isAccountActive()) {
				LoanBO loan = (LoanBO) account;
				amount = amount.add(loan.getRemainingPrincipalAmount());
			}
		}
		return amount;
	}

	private Money getTotalSavings(List<AccountBO> accountList)
			throws ServiceException {
		Money total = new Money();
		for (AccountBO accountBO : accountList) {
			SavingsBO savingsBO = (SavingsBO) accountBO;
			total = total.add(savingsBO.getSavingsBalance());
		}
		return total;
	}

	/**
	 * FIXME: THIS METHOD DOES NOT WORK. Specifically, the portfolioAtRisk
	 * calculation. Please see issue 2204.
	 */
	public CenterPerformanceHistory getCenterPerformanceHistory(
			String searchId, Short officeId) throws ServiceException {
		List<CustomerBO> groups = null;
		List<CustomerBO> clients = null;
		try {
			groups = new CustomerPersistence().getChildren(searchId, officeId,
					CustomerLevel.GROUP, ChildrenStateType.ACTIVE_AND_ONHOLD);
			clients = new CustomerPersistence().getChildren(searchId, officeId,
					CustomerLevel.CLIENT, ChildrenStateType.ACTIVE_AND_ONHOLD);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
		List<AccountBO> loanList = getAccountsForCustomer(searchId, officeId,
				AccountTypes.LOAN_ACCOUNT.getValue());
		List<AccountBO> savingsList = getAccountsForCustomer(searchId,
				officeId, AccountTypes.SAVINGS_ACCOUNT.getValue());
		int clientSize = 0;
		int groupSize = 0;
		if (clients != null)
			clientSize = clients.size();
		if (groups != null)
			groupSize = groups.size();
		Money portfolioAtRisk = new Money();
		Money totalLoan = getTotalOutstandingLoan(loanList);
		Money unpaidBadStanding = getTotalUnpaidPrincipal(loanList);
		
		String amountAtRisk = String.valueOf(getPortfolioAtRisk(
				loanList).getAmountDoubleValue());
		Money amountAtRiskMoney = new Money(amountAtRisk);
		
		if (amountAtRiskMoney.getAmountDoubleValue() != 0 &&
				unpaidBadStanding.getAmountDoubleValue() != 0) {
			portfolioAtRisk = new Money(String.valueOf(
					unpaidBadStanding.getAmountDoubleValue()
					/ amountAtRiskMoney.getAmountDoubleValue()
					));
		}
		Money totalSavings = getTotalSavings(savingsList);

		CenterPerformanceHistory centerPerformanceHistory = new CenterPerformanceHistory();
		centerPerformanceHistory
				.setPerformanceHistoryDetails(groupSize, clientSize,
						totalLoan, totalSavings, portfolioAtRisk);
		return centerPerformanceHistory;

	}

	public List<CustomerCheckListBO> getStatusChecklist(Short statusId,
			Short customerLevelId) throws ServiceException {
		try {
			return new CustomerPersistence().getStatusChecklist(statusId,
					customerLevelId);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public List<CustomerStatusEntity> retrieveAllCustomerStatusList(
			Short levelId) throws ServiceException {
		try {
			return new CustomerPersistence()
					.retrieveAllCustomerStatusList(levelId);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public void initializeStateMachine(Short localeId, Short officeId,
			AccountTypes accountTypes, CustomerLevel customerLevel)
			throws ServiceException {
		try {
			AccountStateMachines.getInstance().initialize(localeId, officeId,
					accountTypes, customerLevel);
		}
		catch (StatesInitializationException sie) {
			throw new ServiceException(sie);
		}
	}

	public String getStatusName(Short localeId, CustomerStatus customerStatus,
			CustomerLevel customerLevel) {
		return AccountStateMachines.getInstance().getCustomerStatusName(
				localeId, customerStatus, customerLevel);
	}

	public String getFlagName(Short localeId,
			CustomerStatusFlag customerStatusFlag, CustomerLevel customerLevel) {
		return AccountStateMachines.getInstance().getCustomerFlagName(localeId,
				customerStatusFlag, customerLevel);
	}

	public List<CustomerStatusEntity> getStatusList(
			CustomerStatusEntity customerStatusEntity,
			CustomerLevel customerLevel, Short localeId) {
		List<CustomerStatusEntity> statusList = AccountStateMachines
				.getInstance().getStatusList(customerStatusEntity,
						customerLevel);
		if (null != statusList) {
			for (CustomerStatusEntity customerStatusObj : statusList) {
				customerStatusObj.setLocaleId(localeId);
			}
		}
		return statusList;
	}

	public QueryResult getAllCustomerNotes(Integer customerId)
			throws ServiceException {
		try {
			return new CustomerPersistence().getAllCustomerNotes(customerId);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public List<ValueListElement> retrieveMasterEntities(String entityName,
			Short localeId) throws ServiceException {
		try {
			return new MasterPersistence().retrieveMasterEntities(entityName,
					localeId);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public List<PersonnelView> getFormedByPersonnel(Short levelId,
			Short officeId) throws ServiceException {
		try {
			return new CustomerPersistence().getFormedByPersonnel(levelId,
					officeId);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public CustomerPictureEntity retrievePicture(Integer customerId)
			throws ServiceException {
		try {
			return new CustomerPersistence().retrievePicture(customerId);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public void checkPermissionForStatusChange(Short newState,
			UserContext userContext, Short flagSelected, Short recordOfficeId,
			Short recordLoanOfficerId) throws ServiceException {
		if (!isPermissionAllowed(newState, userContext, flagSelected,
				recordOfficeId, recordLoanOfficerId))
			throw new ServiceException(
					SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
	}

	public boolean isPermissionAllowed(Short newState, UserContext userContext,
			Short flagSelected, Short recordOfficeId, Short recordLoanOfficerId) {
		return ActivityMapper.getInstance().isStateChangePermittedForCustomer(
				newState.shortValue(),
				null != flagSelected ? flagSelected.shortValue() : 0,
				userContext, recordOfficeId, recordLoanOfficerId);
	}

	public List<AccountBO> getAllClosedAccount(Integer customerId,
			Short accountTypeId) throws ServiceException {
		try {
			return new CustomerPersistence().getAllClosedAccount(customerId,
					accountTypeId);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public List<CustomerBO> getActiveCentersUnderUser(PersonnelBO personnel)
			throws ServiceException {
		try {
			return new CustomerPersistence()
					.getActiveCentersUnderUser(personnel);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public Integer getCenterCountForOffice(OfficeBO office)
			throws ServiceException {
		return getCustomerCountForOffice(CustomerLevel.CENTER, office);
	}

	public Integer getClientCountForOffice(OfficeBO office)
			throws ServiceException {
		return getCustomerCountForOffice(CustomerLevel.CLIENT, office);
	}

	public Integer getGroupCountForOffice(OfficeBO office)
			throws ServiceException {
		return getCustomerCountForOffice(CustomerLevel.GROUP, office);
	}

	private Integer getCustomerCountForOffice(CustomerLevel customerLevel,
			OfficeBO office) throws ServiceException {
		try {

			return customerPersistence.getCustomerCountForOffice(customerLevel,
					office.getOfficeId());
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public List<CustomerBO> getGroupsUnderUser(PersonnelBO personnel)
			throws ServiceException {
		try {
			return new CustomerPersistence().getGroupsUnderUser(personnel);
		}
		catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}

	public Integer getActiveClientCountForOffice(OfficeBO office)
			throws ServiceException {
		try {
			return customerPersistence.getActiveClientCountForOffice(office);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getVeryPoorClientCountForOffice(OfficeBO office)
			throws ServiceException {
		try {
			return customerPersistence.getVeryPoorClientCountForOffice(office);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getActiveBorrowersCountForOffice(OfficeBO office)
			throws ServiceException {
		try {
			return customerPersistence.getActiveBorrowersCountForOffice(office);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getVeryPoorActiveBorrowersCountForOffice(OfficeBO office)
			throws ServiceException {
		try {
			return customerPersistence
					.getVeryPoorActiveBorrowersCountForOffice(office);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getCustomerReplacementsCountForOffice(OfficeBO office, Short fieldId, String fieldValue)
			throws ServiceException {
		try {
			return customerPersistence
					.getCustomerReplacementsCountForOffice(office, fieldId, fieldValue);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getCustomerVeryPoorReplacementsCountForOffice(OfficeBO office, Short fieldId, String fieldValue)
			throws ServiceException {
		try {
			return customerPersistence
					.getVeryPoorReplacementsCountForOffice(office, fieldId, fieldValue);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getDormantClientsCountByLoanAccountForOffice(
			OfficeBO office, Integer loanCyclePeriod) throws ServiceException {
		try {
			return customerPersistence
					.getDormantClientsCountByLoanAccountForOffice(office,
							loanCyclePeriod);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getDormantClientsCountBySavingAccountForOffice(
			OfficeBO office, Integer loanCyclePeriod) throws ServiceException {
		try {
			return customerPersistence
			.getDormantClientsCountBySavingAccountForOffice(office,
					loanCyclePeriod);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public Integer getVeryPoorDormantClientsCountByLoanAccountForOffice(
			OfficeBO office, Integer loanCyclePeriod) throws ServiceException {
		try {
			return customerPersistence
					.getVeryPoorDormantClientsCountByLoanAccountForOffice(
							office, loanCyclePeriod);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getVeryPoorDormantClientsCountBySavingAccountForOffice(
			OfficeBO office, Integer loanCyclePeriod) throws ServiceException {
		try {
			return customerPersistence
			.getVeryPoorDormantClientsCountBySavingAccountForOffice(
					office, loanCyclePeriod);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}
	
	public Integer getDropOutClientsCountForOffice(OfficeBO office)
			throws ServiceException {
		try {
			return customerPersistence.getDropOutClientsCountForOffice(office);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public BigDecimal getClientDropOutRateForOffice(OfficeBO office)
			throws ServiceException {
		Integer dropOutClientsCountForOffice = getDropOutClientsCountForOffice(office);
		try {
			Integer activeOrHoldClientCountForOffice = customerPersistence
					.getActiveOrHoldClientCountForOffice(office);
			return getPercentage(dropOutClientsCountForOffice,
					dropOutClientsCountForOffice
							+ activeOrHoldClientCountForOffice);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public BigDecimal getVeryPoorClientDropoutRateForOffice(OfficeBO office)
			throws ServiceException {
		Integer veryPoorDropOutClientsCountForOffice = getVeryPoorDropOutClientsCountForOffice(office);

		try {
			Integer veryPoorActiveOrHoldClientCountForOffice = customerPersistence
					.getVeryPoorActiveOrHoldClientCountForOffice(office);
			return getPercentage(veryPoorDropOutClientsCountForOffice,
					veryPoorDropOutClientsCountForOffice
							+ veryPoorActiveOrHoldClientCountForOffice);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getVeryPoorDropOutClientsCountForOffice(OfficeBO office)
			throws ServiceException {
		try {
			return customerPersistence
					.getVeryPoorDropOutClientsCountForOffice(office);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getOnHoldClientsCountForOffice(OfficeBO office)
			throws ServiceException {
		try {
			return customerPersistence.getOnHoldClientsCountForOffice(office);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getVeryPoorOnHoldClientsCountForOffice(OfficeBO office)
			throws ServiceException {
		try {
			return customerPersistence
					.getVeryPoorOnHoldClientsCountForOffice(office);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getActiveSaversCountForOffice(OfficeBO office)
			throws ServiceException {
		try {
			return customerPersistence.getActiveSaversCountForOffice(office);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public Integer getVeryPoorActiveSaversCountForOffice(OfficeBO office)
			throws ServiceException {
		try {
			return customerPersistence
					.getVeryPoorActiveSaversCountForOffice(office);
		}
		catch (PersistenceException e) {
			throw new ServiceException(e);
		}
	}

	public List<CustomerBO> getCustomersByLevelId(Short customerLevelId)
			throws ServiceException {		
		try {
			return new CustomerPersistence().getCustomersByLevelId(customerLevelId);
		} catch (PersistenceException pe) {
			throw new ServiceException(pe);
		}
	}
}
