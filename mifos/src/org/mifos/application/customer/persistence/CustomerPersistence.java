/**
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
 * All rights reserved.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
package org.mifos.application.customer.persistence;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.persistence.AccountPersistence;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.util.helpers.AccountState;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.checklist.business.CustomerCheckListBO;
import org.mifos.application.checklist.util.resources.CheckListConstants;
import org.mifos.application.configuration.exceptions.ConfigurationException;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerCustomFieldEntity;
import org.mifos.application.customer.business.CustomerPerformanceHistoryView;
import org.mifos.application.customer.business.CustomerStatusEntity;
import org.mifos.application.customer.business.CustomerStatusFlagEntity;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.CustomerPictureEntity;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.util.helpers.ChildrenStateType;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.LoanCycleCounter;
import org.mifos.application.customer.util.helpers.Param;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.ClientRules;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.exceptions.HibernateProcessException;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.helpers.DateUtils;

public class CustomerPersistence extends Persistence {
			
	private static final Predicate CLIENTS_WITH_ACTIVE_LOAN_ACCOUNTS = new Predicate() {
		public boolean evaluate(Object object) {
			Set<AccountBO> accounts = ((ClientBO) object).getAccounts();
			return CollectionUtils.exists(accounts, new Predicate() {
				public boolean evaluate(Object object) {
					AccountStateEntity accountState = ((AccountBO)object).getAccountState();
					return new AccountStateEntity(AccountState.LOAN_ACTIVE_IN_GOOD_STANDING).sameId(accountState);
				}
			});
		}
	};
		
	private static final Predicate CLIENTS_WITH_ACTIVE_SAVINGS_ACCOUNT = new Predicate() {
		public boolean evaluate(Object arg0) {
			Set<AccountBO> accounts = ((ClientBO)arg0).getAccounts();
			return CollectionUtils.exists(accounts, new Predicate() {
				public boolean evaluate(Object arg0) {
					AccountBO account = ((AccountBO)arg0);
					return AccountTypes.SAVINGS_ACCOUNT.getValue().equals(
							account.getAccountType().getAccountTypeId()) 
					&& new AccountStateEntity(AccountState.SAVINGS_ACTIVE).sameId(account.getAccountState());						
				}
			});
		}
	};
	
	public CustomerPersistence() {
	}

	public List<CustomerView> getChildrenForParent(Integer customerId,
			String searchId, Short officeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", searchId + ".%");
		queryParameters.put("OFFICE_ID", officeId);
		List<CustomerView> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_ACTIVE_CHILDREN_FORPARENT,
				queryParameters);
		return queryResult;
	}

	public CustomerBO getCustomerWithSearchId(String searchId, Short officeId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", searchId);
		queryParameters.put("OFFICE_ID", officeId);
		List<CustomerBO> queryResult = executeNamedQuery(
				NamedQueryConstants.ACTIVE_CUSTOMERS_WITH_SEARCH_ID,
				queryParameters);
		return queryResult.get(0);
	}

	public List<CustomerBO> getCustomersUnderParent(String searchId,
			Short officeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", searchId + ".%");
		queryParameters.put("OFFICE_ID", officeId);
		List<CustomerBO> queryResult = executeNamedQuery(
				NamedQueryConstants.ACTIVE_CUSTOMERS_UNDER_PARENT,
				queryParameters);
		queryResult.add(getCustomerWithSearchId(searchId, officeId));
		return queryResult;
	}

	public List<Integer> getChildrenForParent(String searchId, Short officeId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", searchId + ".%");
		queryParameters.put("OFFICE_ID", officeId);
		List<Integer> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_CHILDREN_FOR_PARENT, queryParameters);
		return queryResult;
	}

	public List<CustomerView> getActiveParentList(Short personnelId,
			Short customerLevelId, Short officeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("personnelId", personnelId);
		queryParameters.put("customerLevelId", customerLevelId);
		queryParameters.put("officeId", officeId);

		List<CustomerView> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_PARENTCUSTOMERS_FOR_LOANOFFICER,
				queryParameters);
		return queryResult;

	}

	public List<PrdOfferingBO> getLoanProducts(Date meetingDate,
			String searchId, Short personnelId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("meetingDate", meetingDate);
		queryParameters.put("searchId", searchId + "%");
		queryParameters.put("personnelId", personnelId);
		List<PrdOfferingBO> queryResult = executeNamedQuery(
				NamedQueryConstants.BULKENTRYPRODUCTS, queryParameters);
		return queryResult;

	}

	public List<PrdOfferingBO> getSavingsProducts(Date meetingDate,
			String searchId, Short personnelId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("meetingDate", meetingDate);
		queryParameters.put("searchId", searchId + "%");
		queryParameters.put("personnelId", personnelId);
		List<PrdOfferingBO> queryResult = executeNamedQuery(
				NamedQueryConstants.BULKENTRYSAVINGSPRODUCTS, queryParameters);
		return queryResult;
	}

	public Date getLastMeetingDateForCustomer(Integer customerId)
			throws PersistenceException {
		Date meetingDate = null;
		Date actionDate = new java.sql.Date(Calendar.getInstance().getTime()
				.getTime());
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("CUSTOMER_ID", customerId);
		queryParameters.put("ACTION_DATE", actionDate);
		List<AccountActionDateEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_LAST_MEETINGDATE_FOR_CUSTOMER,
				queryParameters);
		if (queryResult != null && queryResult.size() != 0)
			meetingDate = queryResult.get(0).getActionDate();
		return meetingDate;

	}

	public CustomerBO getCustomer(Integer customerId)
			throws PersistenceException {
		return (CustomerBO) getPersistentObject(CustomerBO.class, customerId);
	}

	public CustomerBO findBySystemId(String globalCustNum)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		CustomerBO customer = null;
		queryParameters.put("globalCustNum", globalCustNum);
		List<CustomerBO> queryResult = executeNamedQuery(
				NamedQueryConstants.CUSTOMER_FIND_ACCOUNT_BY_SYSTEM_ID,
				queryParameters);
		if (null != queryResult && queryResult.size() > 0) {
			customer = queryResult.get(0);
		}
		return customer;
	}

	public CustomerBO findBySystemId(String globalCustNum, Short levelId)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		CustomerBO customer = null;
		queryParameters.put("globalCustNum", globalCustNum);
		if (levelId.shortValue() == CustomerLevel.CENTER.getValue()) {
			List<CenterBO> queryResult = executeNamedQuery(
					NamedQueryConstants.GET_CENTER_BY_SYSTEMID, queryParameters);
			if (null != queryResult && queryResult.size() > 0) {
				customer = queryResult.get(0);
				initializeCustomer(customer);
			}
		}
		else if (levelId.shortValue() == CustomerLevel.GROUP.getValue()) {
			List<GroupBO> queryResult = executeNamedQuery(
					NamedQueryConstants.GET_GROUP_BY_SYSTEMID, queryParameters);
			if (null != queryResult && queryResult.size() > 0) {
				customer = queryResult.get(0);
				initializeCustomer(customer);
			}

		}
		else if (levelId.shortValue() == CustomerLevel.CLIENT.getValue()) {
			List<ClientBO> queryResult = executeNamedQuery(
					NamedQueryConstants.GET_CLIENT_BY_SYSTEMID, queryParameters);
			if (null != queryResult && queryResult.size() > 0) {
				customer = queryResult.get(0);
				initializeCustomer(customer);
			}
		}
		return customer;
	}

	public QueryResult search(String searchString, Short officeId,
			Short userId, Short userOfficeId) throws PersistenceException {

		QueryResult queryResult = null;

		try {

			queryResult = new AccountPersistence().search(searchString,
					officeId);
			if (queryResult == null) {
				queryResult = idSearch(searchString, officeId, userId);
				if (queryResult == null) {
					queryResult = mainSearch(searchString, officeId, userId,
							userOfficeId);
				}
			}

		}
		catch (HibernateSearchException e) {
			throw new PersistenceException(e);
		}
		catch (SystemException e) {
			throw new PersistenceException(e);
		}

		return queryResult;
	}

	public QueryResult searchGroupClient(String searchString, Short userId)
			throws ConfigurationException, PersistenceException {
		String[] namedQuery = new String[2];
		List<Param> paramList = new ArrayList<Param>();
		QueryInputs queryInputs = new QueryInputs();
		QueryResult queryResult = QueryFactory
				.getQueryResult(CustomerSearchConstants.ACCOUNTSEARCHRESULTS);
		PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userId);
		if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
			namedQuery[0] = NamedQueryConstants.SEARCH_GROUP_CLIENT_COUNT_LO;
			namedQuery[1] = NamedQueryConstants.SEARCH_GROUP_CLIENT_LO;
			paramList.add(typeNameValue("Short", "PERSONNEL_ID", userId));
		}
		else {
			namedQuery[0] = NamedQueryConstants.SEARCH_GROUP_CLIENT_COUNT;
			namedQuery[1] = NamedQueryConstants.SEARCH_GROUP_CLIENT;
		}

		paramList.add(typeNameValue("String", "SEARCH_ID", personnel
				.getOffice().getSearchId()
				+ "%"));
		paramList.add(typeNameValue("String", "SEARCH_STRING", searchString
				+ "%"));
		paramList.add(typeNameValue("Boolean", "GROUP_LOAN_ALLOWED",
				ClientRules.getGroupCanApplyLoans() ? Boolean.TRUE
						: Boolean.FALSE));

		String[] aliasNames = { "clientName", "clientId", "groupName",
				"centerName", "officeName", "globelNo" };
		queryInputs.setQueryStrings(namedQuery);
		queryInputs
				.setPath("org.mifos.application.accounts.util.helpers.AccountSearchResults");
		queryInputs.setAliasNames(aliasNames);
		queryInputs.setParamList(paramList);
		try {
			queryResult.setQueryInputs(queryInputs);
		}
		catch (HibernateSearchException e) {
			throw new PersistenceException(e);
		}

		return queryResult;

	}

	public QueryResult searchCustForSavings(String searchString, Short userId)
			throws PersistenceException {
		String[] namedQuery = new String[2];
		List<Param> paramList = new ArrayList<Param>();
		QueryInputs queryInputs = new QueryInputs();
		QueryResult queryResult = QueryFactory
				.getQueryResult(CustomerSearchConstants.CUSTOMERSFORSAVINGSACCOUNT);
		PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userId);
		if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
			namedQuery[0] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS_COUNT;
			namedQuery[1] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS;
			paramList.add(typeNameValue("Short", "PERSONNEL_ID", userId));
		}
		else {
			namedQuery[0] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS_COUNT_NOLO;
			namedQuery[1] = NamedQueryConstants.SEARCH_CUSTOMER_FOR_SAVINGS_NOLO;
		}
		paramList.add(typeNameValue("String", "SEARCH_ID", personnel
				.getOffice().getSearchId()
				+ "%"));
		paramList.add(typeNameValue("String", "SEARCH_STRING", searchString
				+ "%"));

		String[] aliasNames = { "clientName", "clientId", "groupName",
				"centerName", "officeName", "globelNo" };
		queryInputs.setQueryStrings(namedQuery);
		queryInputs
				.setPath("org.mifos.application.accounts.util.helpers.AccountSearchResults");
		queryInputs.setAliasNames(aliasNames);
		queryInputs.setParamList(paramList);
		try {
			queryResult.setQueryInputs(queryInputs);
		}
		catch (HibernateSearchException e) {
			throw new PersistenceException(e);
		}

		return queryResult;

	}

	private QueryResult mainSearch(String searchString, Short officeId,
			Short userId, Short userOfficeId) throws PersistenceException,
			HibernateSearchException {
		String[] namedQuery = new String[2];
		List<Param> paramList = new ArrayList<Param>();
		QueryInputs queryInputs = setQueryInputsValues(namedQuery, paramList);
		QueryResult queryResult = QueryFactory
				.getQueryResult(CustomerSearchConstants.CUSTOMERSEARCHRESULTS);
		if (officeId.shortValue() != 0) {
			namedQuery[0] = NamedQueryConstants.CUSTOMER_SEARCH_COUNT;
			namedQuery[1] = NamedQueryConstants.CUSTOMER_SEARCH;
			paramList.add(typeNameValue("Short", "OFFICEID", officeId));

		}
		else {
			namedQuery[0] = NamedQueryConstants.CUSTOMER_SEARCH_COUNT_NOOFFICEID;
			namedQuery[1] = NamedQueryConstants.CUSTOMER_SEARCH_NOOFFICEID;
			paramList.add(typeNameValue("String", "OFFICE_SEARCH_ID",
					new OfficePersistence().getOffice(userOfficeId)
							.getSearchId()
							+ "%"));
		}
		paramList.add(typeNameValue("String", "SEARCH_STRING", searchString
				+ "%"));
		if (searchString.contains(" ")) {
			paramList.add(typeNameValue("String", "SEARCH_STRING1",
					searchString.substring(0, searchString.indexOf(" "))));
			paramList.add(typeNameValue("String", "SEARCH_STRING2",
					searchString.substring(searchString.indexOf(" ") + 1,
							searchString.length())));
		}
		else {
			paramList.add(typeNameValue("String", "SEARCH_STRING1",
					searchString));
			paramList.add(typeNameValue("String", "SEARCH_STRING2", ""));
		}
		setParams(paramList, userId);
		queryResult.setQueryInputs(queryInputs);
		return queryResult;

	}

	private void setParams(List<Param> paramList, Short userId)
			throws PersistenceException {
		paramList.add(typeNameValue("Short", "USERID", userId));
		paramList.add(typeNameValue("Short", "LOID",
				PersonnelLevel.LOAN_OFFICER.getValue()));
		paramList.add(typeNameValue("Short", "LEVELID", CustomerLevel.CLIENT
				.getValue()));
		paramList.add(typeNameValue("Short", "USERLEVEL_ID",
				new PersonnelPersistence().getPersonnel(userId).getLevelEnum()
						.getValue()));
	}

	private String[] getAliasNames() {
		String[] aliasNames = { "customerId", "centerName",
				"centerGlobalCustNum", "customerType", "branchGlobalNum",
				"branchName", "loanOfficerName", "loanOffcerGlobalNum",
				"customerStatus", "groupName", "groupGlobalCustNum",
				"clientName", "clientGlobalCustNum", "loanGlobalAccountNumber" };

		return aliasNames;

	}

	private QueryInputs setQueryInputsValues(String[] namedQuery,
			List<Param> paramList) {
		QueryInputs queryInputs = new QueryInputs();
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setParamList(paramList);
		queryInputs
				.setPath("org.mifos.application.customer.business.CustomerSearch");
		queryInputs.setAliasNames(getAliasNames());
		return queryInputs;

	}

	private QueryResult idSearch(String searchString, Short officeId,
			Short userId) throws HibernateSearchException, SystemException,
			PersistenceException {
		if (!isCustomerExist(searchString))
			return null;
		String[] namedQuery = new String[2];
		List<Param> paramList = new ArrayList<Param>();
		QueryInputs queryInputs = new QueryInputs();
		String[] Names = { "customerId", "centerName", "centerGlobalCustNum",
				"customerType", "branchGlobalNum", "branchName",
				"loanOfficerName", "loanOffcerGlobalNum", "customerStatus",
				"groupName", "groupGlobalCustNum", "clientName",
				"clientGlobalCustNum", "loanGlobalAccountNumber" };
		QueryResult queryResult = QueryFactory
				.getQueryResult(CustomerSearchConstants.CUSTOMERSEARCHRESULTS);
		queryInputs
				.setPath("org.mifos.application.customer.business.CustomerSearch");
		queryInputs.setAliasNames(Names);
		queryResult.setQueryInputs(queryInputs);
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setParamList(paramList);
		PersonnelBO personnel = new PersonnelPersistence().getPersonnel(userId);

		if (officeId != null && officeId.shortValue() == 0) {
			namedQuery[0] = NamedQueryConstants.CUSTOMER_ID_SEARCH_NOOFFICEID_COUNT;
			namedQuery[1] = NamedQueryConstants.CUSTOMER_ID_SEARCH_NOOFFICEID;
			if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
				paramList.add(typeNameValue("String", "SEARCH_ID", personnel
						.getOffice().getSearchId()));
			}
			else {
				paramList.add(typeNameValue("String", "SEARCH_ID", personnel
						.getOffice().getSearchId()
						+ "%"));
			}
		}
		else {
			paramList.add(typeNameValue("Short", "OFFICEID", officeId));
			if (personnel.getLevelEnum() == PersonnelLevel.LOAN_OFFICER) {
				paramList.add(typeNameValue("String", "ID", personnel
						.getPersonnelId()));
				namedQuery[0] = NamedQueryConstants.CUSTOMER_ID_SEARCH_COUNT;
				namedQuery[1] = NamedQueryConstants.CUSTOMER_ID_SEARCH;
			}
			else {
				paramList.add(typeNameValue("String", "SEARCH_ID", personnel
						.getOffice().getSearchId()
						+ "%"));
				namedQuery[0] = NamedQueryConstants.CUSTOMER_ID_SEARCH_COUNT_NONLO;
				namedQuery[1] = NamedQueryConstants.CUSTOMER_ID_SEARCH_NONLO;
			}

		}

		paramList.add(typeNameValue("String", "SEARCH_STRING", searchString));

		return queryResult;

	}

	private boolean isCustomerExist(String globalCustNum)
			throws PersistenceException {

		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("globalCustNum", globalCustNum);
		Integer count = (Integer) execUniqueResultNamedQuery(
				NamedQueryConstants.CUSTOMER_FIND_COUNT_BY_SYSTEM_ID,
				queryParameters);
		return count != null && count > 0 ? true : false;

	}

	private void initializeCustomer(CustomerBO customer) {
		customer.getGlobalCustNum();
		customer.getOffice().getOfficeId();
		customer.getOffice().getOfficeName();
		customer.getCustomerLevel().getId();
		customer.getDisplayName();
		if (customer.getParentCustomer() != null) {
			customer.getParentCustomer().getGlobalCustNum();
			customer.getParentCustomer().getCustomerId();
			customer.getParentCustomer().getCustomerLevel().getId();
			if (customer.getParentCustomer().getParentCustomer() != null) {
				customer.getParentCustomer().getParentCustomer()
						.getGlobalCustNum();
				customer.getParentCustomer().getParentCustomer()
						.getCustomerId();
				customer.getParentCustomer().getParentCustomer()
						.getCustomerLevel().getId();
			}
		}
	}

	protected List<CustomerBO> getChildrenOtherThanClosed(
			String parentSearchId, Short parentOfficeId,
			CustomerLevel childrenLevel) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
		queryParameters.put("OFFICE_ID", parentOfficeId);
		queryParameters.put("LEVEL_ID", childrenLevel.getValue());
		List<CustomerBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_CHILDREN_OTHER_THAN_CLOSED,
				queryParameters);
		return queryResult;
	}

	protected List<CustomerBO> getChildrenOtherThanClosedAndCancelled(
			String parentSearchId, Short parentOfficeId,
			CustomerLevel childrenLevel) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
		queryParameters.put("OFFICE_ID", parentOfficeId);
		queryParameters.put("LEVEL_ID", childrenLevel.getValue());
		List<CustomerBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_CHILDREN_OTHER_THAN_CLOSED_AND_CANCELLED,
				queryParameters);
		return queryResult;
	}

	protected List<CustomerBO> getAllChildren(String parentSearchId,
			Short parentOfficeId, CustomerLevel childrenLevel)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
		queryParameters.put("OFFICE_ID", parentOfficeId);
		queryParameters.put("LEVEL_ID", childrenLevel.getValue());
		List<CustomerBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_ALL_CHILDREN_FOR_CUSTOMERLEVEL,
				queryParameters);
		return queryResult;
	}

	protected List<CustomerBO> getActiveAndOnHoldChildren(
			String parentSearchId, Short parentOfficeId,
			CustomerLevel childrenLevel) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", parentSearchId + ".%");
		queryParameters.put("OFFICE_ID", parentOfficeId);
		queryParameters.put("LEVEL_ID", childrenLevel.getValue());
		List<CustomerBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_ACTIVE_AND_ONHOLD_CHILDREN,
				queryParameters);
		return queryResult;
	}

	public List<CustomerBO> getChildren(String parentSearchId,
			Short parentOfficeId, CustomerLevel childrenLevel,
			ChildrenStateType childrenStateType) throws PersistenceException {
		if (childrenStateType.equals(ChildrenStateType.OTHER_THAN_CLOSED))
			return getChildrenOtherThanClosed(parentSearchId, parentOfficeId,
					childrenLevel);
		if (childrenStateType
				.equals(ChildrenStateType.OTHER_THAN_CANCELLED_AND_CLOSED))
			return getChildrenOtherThanClosedAndCancelled(parentSearchId,
					parentOfficeId, childrenLevel);
		if (childrenStateType.equals(ChildrenStateType.ALL))
			return getAllChildren(parentSearchId, parentOfficeId, childrenLevel);
		if (childrenStateType.equals(ChildrenStateType.ACTIVE_AND_ONHOLD))
			return getActiveAndOnHoldChildren(parentSearchId, parentOfficeId,
					childrenLevel);
		return null;
	}

	public List<SavingsBO> retrieveSavingsAccountForCustomer(Integer customerId)
			throws PersistenceException {

		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("customerId", customerId);
		return executeNamedQuery(
				NamedQueryConstants.RETRIEVE_SAVINGS_ACCCOUNT_FOR_CUSTOMER,
				queryParameters);
	}

	public CustomerPerformanceHistoryView numberOfMeetings(boolean isPresent,
			Integer customerId) throws PersistenceException {
		Session session = null;
		Query query = null;
		CustomerPerformanceHistoryView customerPerformanceHistoryView = new CustomerPerformanceHistoryView();
		session = HibernateUtil.getSessionTL();
		String systemDate = DateUtils.getCurrentDate();
		Date localDate = DateUtils.getLocaleDate(systemDate);
		Calendar currentDate = new GregorianCalendar();
		currentDate.setTime(localDate);
		currentDate.add(currentDate.YEAR, -1);
		Date dateOneYearBefore = new Date(currentDate.getTimeInMillis());
		if (isPresent) {
			query = session
					.getNamedQuery(NamedQueryConstants.NUMBEROFMEETINGSATTENDED);
			query.setInteger("CUSTOMERID", customerId);
			query.setDate("DATEONEYEARBEFORE", dateOneYearBefore);
			customerPerformanceHistoryView.setMeetingsAttended((Integer) query
					.uniqueResult());
		}
		else {
			query = session
					.getNamedQuery(NamedQueryConstants.NUMBEROFMEETINGSMISSED);
			query.setInteger("CUSTOMERID", customerId);
			query.setDate("DATEONEYEARBEFORE", dateOneYearBefore);
			customerPerformanceHistoryView.setMeetingsMissed((Integer) query
					.uniqueResult());
		}
		return customerPerformanceHistoryView;
	}

	public CustomerPerformanceHistoryView getLastLoanAmount(Integer customerId)
			throws PersistenceException {
		Query query = null;
		Session session = HibernateUtil.getSessionTL();
		CustomerPerformanceHistoryView customerPerformanceHistoryView = null;
		if (null != session) {
			query = session
					.getNamedQuery(NamedQueryConstants.GETLASTLOANAMOUNT);
		}
		query.setInteger("CUSTOMERID", customerId);
		Object obj = query.uniqueResult();
		if (obj != null) {
			customerPerformanceHistoryView = new CustomerPerformanceHistoryView();
			customerPerformanceHistoryView.setLastLoanAmount(query
					.uniqueResult().toString());
		}
		return customerPerformanceHistoryView;
	}

	public List<CustomerStatusEntity> getCustomerStates(Short optionalFlag)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OPTIONAL_FLAG", optionalFlag);
		List<CustomerStatusEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_CUSTOMER_STATES, queryParameters);
		return queryResult;
	}

	public List<Integer> getCustomersWithUpdatedMeetings()
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("updated", YesNoFlag.YES.getValue());
		List<Integer> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_UPDATED_CUSTOMER_MEETINGS,
				queryParameters);
		return queryResult;
	}

	public List<AccountBO> retrieveAccountsUnderCustomer(String searchId,
			Short officeId, Short accountTypeId) throws PersistenceException {

		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING1", searchId);
		queryParameters.put("SEARCH_STRING2", searchId + ".%");
		queryParameters.put("OFFICE_ID", officeId);
		queryParameters.put("ACCOUNT_TYPE_ID", accountTypeId);
		return executeNamedQuery(
				NamedQueryConstants.RETRIEVE_ACCCOUNTS_FOR_CUSTOMER,
				queryParameters);
	}

	public List<CustomerBO> getAllChildrenForParent(String searchId,
			Short officeId, Short customerLevelId) throws PersistenceException {

		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("SEARCH_STRING", searchId + ".%");
		queryParameters.put("OFFICE_ID", officeId);
		queryParameters.put("LEVEL_ID", customerLevelId);
		List<CustomerBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_ALL_CHILDREN, queryParameters);
		return queryResult;
	}

	public List<Integer> getCustomers(Short customerLevelId)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LEVEL_ID", customerLevelId);
		List<Integer> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_ALL_CUSTOMERS, queryParameters);
		return queryResult;
	}

	public List<CustomerCheckListBO> getStatusChecklist(Short statusId,
			Short customerLevelId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("CHECKLIST_STATUS",
				CheckListConstants.STATUS_ACTIVE);
		queryParameters.put("STATUS_ID", statusId);
		queryParameters.put("LEVEL_ID", customerLevelId);
		List<CustomerCheckListBO> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_CUSTOMER_STATE_CHECKLIST,
				queryParameters);
		return queryResult;
	}

	public int getCustomerCountForOffice(CustomerLevel customerLevel,
			Short officeId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LEVEL_ID", customerLevel.getValue());
		queryParameters.put("OFFICE_ID", officeId);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GET_CUSTOMER_COUNT_FOR_OFFICE,
				queryParameters);
		return getCountFromQueryResult(queryResult);
	}

	public List<LoanCycleCounter> fetchLoanCycleCounter(Integer customerId)
			throws PersistenceException {
		HashMap<String, Integer> queryParameters = new HashMap<String, Integer>();
		queryParameters.put("customerId", customerId);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.FETCH_LOANCOUNTERS, queryParameters);
		if (null != queryResult && queryResult.size() > 0) {
			MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER).debug(
					"Fetch loan cycle counter query returned : "
							+ queryResult.size() + " rows");
			List<LoanCycleCounter> loanCycleCounters = new ArrayList<LoanCycleCounter>();
			for (Object obj : queryResult) {
				String prdOfferingName = (String) obj;
				MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER).debug(
						"Prd offering name of the loan account is "
								+ prdOfferingName);
				int counter = 1;
				LoanCycleCounter loanCycleCounter = new LoanCycleCounter(
						prdOfferingName, counter);
				if (!loanCycleCounters.contains(loanCycleCounter)) {
					MifosLogManager
							.getLogger(LoggerConstants.CLIENTLOGGER)
							.debug(
									"Prd offering name "
											+ prdOfferingName
											+ " does not already exist in the list hence adding it to the list");
					loanCycleCounters.add(loanCycleCounter);
				}
				else {
					MifosLogManager
							.getLogger(LoggerConstants.CLIENTLOGGER)
							.debug(
									"Prd offering name "
											+ prdOfferingName
											+ " already exists in the list hence incrementing the counter.");
					for (LoanCycleCounter loanCycle : loanCycleCounters) {
						if (loanCycle.getOfferingName().equals(prdOfferingName)) {
							loanCycle.incrementCounter();
						}
					}
				}
			}
			return loanCycleCounters;
		}
		MifosLogManager.getLogger(LoggerConstants.CLIENTLOGGER).debug(
				"Fetch loan cycle counter query returned : 0 rows");
		return null;
	}

	public List<CustomerStatusEntity> retrieveAllCustomerStatusList(
			Short levelId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("LEVEL_ID", levelId);
		List<CustomerStatusEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_CUSTOMER_STATUS_LIST, queryParameters);
		for (CustomerStatusEntity customerStatus : queryResult) {
			for (CustomerStatusFlagEntity customerStatusFlagEntity : customerStatus
					.getFlagSet()) {
				initialize(customerStatusFlagEntity);
				initialize(customerStatusFlagEntity.getNames());
			}
			initialize(customerStatus.getLookUpValue());
		}
		return queryResult;
	}

	public QueryResult getAllCustomerNotes(Integer customerId)
			throws PersistenceException {
		QueryResult notesResult = null;
		try {
			Session session = null;
			notesResult = QueryFactory.getQueryResult("NotesSearch");
			session = notesResult.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.GETALLCUSTOMERNOTES);
			query.setInteger("CUSTOMER_ID", customerId);
			notesResult.executeQuery(query);
		}
		catch (HibernateProcessException hpe) {
			throw new PersistenceException(hpe);
		}
		catch (HibernateSearchException hse) {
			throw new PersistenceException(hse);
		}
		return notesResult;
	}

	public List<PersonnelView> getFormedByPersonnel(Short levelId,
			Short officeId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("levelId", levelId);
		queryParameters.put("officeId", officeId);
		queryParameters.put("statusId", PersonnelConstants.ACTIVE);
		List<PersonnelView> queryResult = executeNamedQuery(
				NamedQueryConstants.FORMEDBY_LOANOFFICERS_LIST, queryParameters);
		return queryResult;
	}

	public CustomerPictureEntity retrievePicture(Integer customerId)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("customerId", customerId);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GET_CUSTOMER_PICTURE, queryParameters);

		return (CustomerPictureEntity) queryResult.get(0);
	}

	public List<AccountBO> getAllClosedAccount(Integer customerId,
			Short accountTypeId) throws PersistenceException {

		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("customerId", customerId);
		queryParameters.put("accountTypeId", accountTypeId);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.VIEWALLCLOSEDACCOUNTS, queryParameters);
		return queryResult;

	}

	public Short getLoanOfficerForCustomer(Integer customerId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("CUSTOMER_ID", customerId);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.GET_LO_FOR_CUSTOMER, queryParameters);
		return (Short) queryResult.get(0);
	}

	public void updateLOsForAllChildren(Short parentLO, String parentSearchId,
			Short parentOfficeId) {
		String hql = "update CustomerBO customer "
				+ " set customer.personnel = :parentLoanOfficer "
				+

				// This is for Hibernate 3.2.x instead of 3.0beta4 (?)
				//				" set customer.personnel.personnelId = :parentLoanOfficer " +

				" where customer.searchId like :parentSearchId"
				+ " and customer.office.officeId = :parentOfficeId";
		Session session = HibernateUtil.getSessionTL();
		Query update = session.createQuery(hql);
		update.setParameter("parentLoanOfficer", parentLO);
		update.setParameter("parentSearchId", parentSearchId + ".%");
		update.setParameter("parentOfficeId", parentOfficeId);
		update.executeUpate();
	}

	public void deleteCustomerMeeting(CustomerBO customer)
			throws PersistenceException {
		delete(customer.getCustomerMeeting());
	}

	public void deleteMeeting(MeetingBO meeting) throws PersistenceException {
		if (meeting != null) {
			delete(meeting);
		}
	}

	public List<AccountBO> getCustomerAccountsForFee(Short feeId)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("FEEID", feeId);
		return executeNamedQuery(
				NamedQueryConstants.GET_CUSTOMER_ACCOUNTS_FOR_FEE,
				queryParameters);
	}

	public AccountBO getCustomerAccountWithAccountActionsInitialized(
			Integer accountId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountId", accountId);
		List obj = executeNamedQuery(
				"accounts.retrieveCustomerAccountWithAccountActions",
				queryParameters);
		Object[] obj1 = (Object[]) obj.get(0);
		return (AccountBO) obj1[0];
	}

	public List<AccountActionDateEntity> retrieveCustomerAccountActionDetails(
			Integer accountId, Date transactionDate)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ACCOUNT_ID", accountId);
		queryParameters.put("ACTION_DATE", transactionDate);
		queryParameters.put("PAYMENT_STATUS", PaymentStatus.UNPAID.getValue());
		return executeNamedQuery(
				NamedQueryConstants.CUSTOMER_ACCOUNT_ACTIONS_DATE,
				queryParameters);
	}

	public List<CustomerBO> getActiveCentersUnderUser(PersonnelBO personnel)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(CustomerSearchConstants.PERSONNELID, personnel
				.getPersonnelId());
		queryParameters.put(CustomerSearchConstants.OFFICEID, personnel
				.getOffice().getOfficeId());
		queryParameters.put(CustomerSearchConstants.CUSTOMERLEVELID,
				CustomerLevel.CENTER.getValue());
		queryParameters.put(CustomerSearchConstants.CENTER_ACTIVE,
				CustomerStatus.CENTER_ACTIVE.getValue());
		return executeNamedQuery(
				NamedQueryConstants.SEARCH_CENTERS_FOR_LOAN_OFFICER,
				queryParameters);
	}

	public List<CustomerBO> getGroupsUnderUser(PersonnelBO personnel)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(CustomerSearchConstants.PERSONNELID, personnel
				.getPersonnelId());
		queryParameters.put(CustomerSearchConstants.OFFICEID, personnel
				.getOffice().getOfficeId());
		queryParameters.put(CustomerSearchConstants.CUSTOMERLEVELID,
				CustomerLevel.GROUP.getValue());
		return executeNamedQuery(
				NamedQueryConstants.SEARCH_GROUPS_FOR_LOAN_OFFICER,
				queryParameters);
	}

	public Integer getActiveClientCountForOffice(OfficeBO office)
			throws PersistenceException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(CustomerSearchConstants.OFFICE, office);
		return getCountFromQueryResult(executeNamedQuery(
				NamedQueryConstants.GET_ACTIVE_CLIENTS_COUNT_UNDER_OFFICE,
				params));
	}

	public Integer getVeryPoorClientCountForOffice(OfficeBO office)
	throws PersistenceException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(CustomerSearchConstants.OFFICE, office);
		return getCountFromQueryResult(executeNamedQuery(
				NamedQueryConstants.GET_VERY_POOR_CLIENTS_COUNT_UNDER_OFFICE,
				params));
	}
	
	public Integer getActiveOrHoldClientCountForOffice(OfficeBO office)
			throws PersistenceException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(CustomerSearchConstants.OFFICE, office);
		return getCountFromQueryResult(executeNamedQuery(
				NamedQueryConstants.GET_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE,
				params));
	}

	public Integer getVeryPoorActiveOrHoldClientCountForOffice(OfficeBO office)
	throws PersistenceException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(CustomerSearchConstants.OFFICE, office);
		return getCountFromQueryResult(executeNamedQuery(
				NamedQueryConstants.GET_VERY_POOR_ACTIVE_OR_HOLD_CLIENTS_COUNT_UNDER_OFFICE,
				params));
	}

	public Integer getActiveBorrowersCountForOffice(OfficeBO office)
			throws PersistenceException {
		List<ClientBO> clients = runQueryForOffice(NamedQueryConstants.GET_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE, office);
		CollectionUtils.filter(clients, CLIENTS_WITH_ACTIVE_LOAN_ACCOUNTS);
		return clients.size();
	}

	private List<ClientBO> runQueryForOffice(String queryName, OfficeBO office) throws PersistenceException {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(CustomerSearchConstants.OFFICE, office);
		return executeNamedQuery(queryName,params);
	}

	public Integer getVeryPoorActiveBorrowersCountForOffice(OfficeBO office) throws PersistenceException {
		List<ClientBO> veryPoorActiveBorrowers = runQueryForOffice(
				NamedQueryConstants.GET_VERY_POOR_ACTIVE_BORROWERS_COUNT_UNDER_OFFICE,office);
		CollectionUtils.filter(veryPoorActiveBorrowers, CLIENTS_WITH_ACTIVE_LOAN_ACCOUNTS);
		return veryPoorActiveBorrowers.size();
	}

	public Integer getCustomerReplacementsCountForOffice(OfficeBO office, Short fieldId, String fieldValue) throws PersistenceException {
		List<ClientBO> clients = runQueryForOffice(NamedQueryConstants.GET_CUSTOMER_REPLACEMENTS_COUNT_UNDER_OFFICE, office);
		CollectionUtils.filter(clients, new FieldStatePredicate(fieldId, fieldValue));
		return clients.size();
	}

	public Integer getVeryPoorReplacementsCountForOffice(OfficeBO office, Short fieldId, String fieldValue) throws PersistenceException {
		List<ClientBO> veryPoorClients = runQueryForOffice(NamedQueryConstants.GET_VERY_POOR_CLIENTS_UNDER_OFFICE, office);
		CollectionUtils.filter(veryPoorClients,  new FieldStatePredicate(fieldId, fieldValue));
		return veryPoorClients.size();
	}

	
	public Integer getDormantClientsCountByLoanAccountForOffice(
			OfficeBO office, Integer loanCyclePeriod)
			throws PersistenceException {
		return getCountFromQueryResult(executeNamedQuery(
				NamedQueryConstants.GET_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE,
				populateDormantQueryParams(office, loanCyclePeriod)));
	}
	
	public Integer getVeryPoorDormantClientsCountByLoanAccountForOffice(
			OfficeBO office, Integer loanCyclePeriod)
			throws PersistenceException {
		return getCountFromQueryResult(executeNamedQuery(
				NamedQueryConstants.GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_LOAN_ACCOUNT_FOR_OFFICE,
				populateDormantQueryParams(office, loanCyclePeriod)));
	}

	public Integer getDormantClientsCountBySavingAccountForOffice(
			OfficeBO office, Integer loanCyclePeriod)
			throws PersistenceException {
		return getCountFromQueryResult(executeNamedQuery(
				NamedQueryConstants.GET_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE,
				populateDormantQueryParams(office, loanCyclePeriod)));
	}

	public Integer getVeryPoorDormantClientsCountBySavingAccountForOffice(
			OfficeBO office, Integer loanCyclePeriod)
			throws PersistenceException {
		return getCountFromQueryResult(executeNamedQuery(
				NamedQueryConstants.GET_VERY_POOR_DORMANT_CLIENTS_COUNT_BY_SAVING_ACCOUNT_FOR_OFFICE,
				populateDormantQueryParams(office, loanCyclePeriod)));
	}

	private HashMap<String, Object> populateDormantQueryParams(OfficeBO office, Integer loanCyclePeriod) {
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put(CustomerSearchConstants.OFFICEID, office.getOfficeId());
		params.put("loanCyclePeriod", loanCyclePeriod);
		return params;
	}
	
	public Integer getDropOutClientsCountForOffice(OfficeBO office) throws PersistenceException{
		return getCountFromQueryResult(runQueryForOffice(NamedQueryConstants.GET_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE, office));
	}

	public Integer getVeryPoorDropOutClientsCountForOffice(OfficeBO office) throws PersistenceException {
		return getCountFromQueryResult(runQueryForOffice(NamedQueryConstants.GET_VERY_POOR_DROP_OUT_CLIENTS_COUNT_UNDER_OFFICE, office));
	}

	public Integer getOnHoldClientsCountForOffice(OfficeBO office) throws PersistenceException {
		return getCountFromQueryResult(runQueryForOffice(NamedQueryConstants.GET_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE, office));
	}
	
	public Integer getVeryPoorOnHoldClientsCountForOffice(OfficeBO office) throws PersistenceException {
		return getCountFromQueryResult(runQueryForOffice(NamedQueryConstants.GET_VERY_POOR_ON_HOLD_CLIENTS_COUNT_UNDER_OFFICE, office));
	}
	
	public Integer getActiveSaversCountForOffice(OfficeBO office) throws PersistenceException {
		List<ClientBO> clients = runQueryForOffice(NamedQueryConstants.GET_ACTIVE_SAVERS_COUNT_UNDER_OFFICE, office);
		CollectionUtils.filter(clients, CLIENTS_WITH_ACTIVE_SAVINGS_ACCOUNT);
		return clients.size();
	}
	
	public Integer getVeryPoorActiveSaversCountForOffice(OfficeBO office) throws PersistenceException {
		List<ClientBO> clients = runQueryForOffice(NamedQueryConstants.GET_VERY_POOR_ACTIVE_SAVERS_COUNT_UNDER_OFFICE, office);
		CollectionUtils.filter(clients, CLIENTS_WITH_ACTIVE_SAVINGS_ACCOUNT);
		return clients.size();
	}
	
	static private class FieldStatePredicate implements Predicate{
		
		private final Short fieldId;
		private final String fieldValue;

		public FieldStatePredicate(Short fieldId, String fieldValue){
			this.fieldId = fieldId;
			this.fieldValue = fieldValue;
		}
		
		public boolean evaluate(Object object) {
			Set<CustomerCustomFieldEntity> customFields = ((ClientBO)object).getCustomFields();
			return CollectionUtils.exists(customFields, new Predicate(){
				public boolean evaluate(Object object) {
					CustomerCustomFieldEntity field = ((CustomerCustomFieldEntity)object);
					return fieldValue.equals(field.getFieldValue()) && fieldId.equals(field.getFieldId()); 
				}
			});
		}
	};	
}
