package org.mifos.application.accounts.persistence;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.business.AccountStateFlagEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.checklist.business.AccountCheckListBO;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerSearchConstants;
import org.mifos.application.customer.util.helpers.CustomerStatus;
import org.mifos.application.customer.util.helpers.Param;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.framework.exceptions.HibernateSearchException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryInputs;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.hibernate.helper.QueryResultAccountIdSearch;
import org.mifos.framework.persistence.Persistence;

public class AccountPersistence extends Persistence {

	public AccountBO getAccount(Integer accountId) throws PersistenceException {
		return (AccountBO) getPersistentObject(AccountBO.class, accountId);
	}

	public Integer getAccountRunningNumber() throws PersistenceException {
		Object queryResult = execUniqueResultNamedQuery(
				NamedQueryConstants.GET_MAX_ACCOUNT_ID, null);
		Integer accountRunningNumber = queryResult == null ? Integer.valueOf(0)
				: (Integer) queryResult;
		return accountRunningNumber + 1;
	}

	public AccountBO findBySystemId(String accountGlobalNum)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("globalAccountNumber", accountGlobalNum);
		Object queryResult = execUniqueResultNamedQuery(
				NamedQueryConstants.FIND_ACCOUNT_BY_SYSTEM_ID, queryParameters);
		return queryResult == null ? null : (AccountBO) queryResult;
	}

	public AccountFeesEntity getAccountFeeEntity(Integer accountFeesEntityId)
			throws PersistenceException {
		return (AccountFeesEntity) getPersistentObject(AccountFeesEntity.class,
				accountFeesEntityId);
	}

	public List<AccountStateEntity> getAccountStates(Short optionalFlag)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OPTIONAL_FLAG", optionalFlag);
		return executeNamedQuery(NamedQueryConstants.GET_ACCOUNT_STATES,
				queryParameters);
	}

	public List<Integer> getAccountsWithYesterdaysInstallment()
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		Calendar currentDateCalendar = new GregorianCalendar();
		int year = currentDateCalendar.get(Calendar.YEAR);
		int month = currentDateCalendar.get(Calendar.MONTH);
		int day = currentDateCalendar.get(Calendar.DAY_OF_MONTH);
		currentDateCalendar = new GregorianCalendar(year, month, day - 1);
		queryParameters.put("CUSTOMER_TYPE_ID",
				CustomerConstants.CUSTOMER_TYPE_ID);
		queryParameters.put("ACTIVE_CENTER_STATE",
				CustomerStatus.CENTER_ACTIVE.getValue());
		queryParameters.put("ACTIVE_GROUP_STATE",
				CustomerConstants.GROUP_ACTIVE_STATE);
		queryParameters.put("ACTIVE_CLIENT_STATE",
				CustomerConstants.CLIENT_APPROVED);
		queryParameters.put("ONHOLD_CLIENT_STATE",
				CustomerConstants.CLIENT_ONHOLD);
		queryParameters.put("ONHOLD_GROUP_STATE", GroupConstants.HOLD);
		queryParameters.put("CURRENT_DATE", currentDateCalendar.getTime());
		return executeNamedQuery(
				NamedQueryConstants.GET_YESTERDAYS_INSTALLMENT_FOR_ACTIVE_CUSTOMERS,
				queryParameters);
	}

	public QueryResult getAllAccountNotes(Integer accountId)
			throws PersistenceException {
		QueryResult notesResult = null;
		try {
			Session session = null;
			notesResult = QueryFactory.getQueryResult("NotesSearch");
			session = notesResult.getSession();
			Query query = session
					.getNamedQuery(NamedQueryConstants.GETALLACCOUNTNOTES);
			query.setInteger("accountId", accountId);
			notesResult.executeQuery(query);
		} catch (Exception e) {
			throw new PersistenceException(e);
		}
		return notesResult;
	}

	public List<Integer> getActiveCustomerAndSavingsAccounts()
			throws PersistenceException {
		return executeNamedQuery(
				NamedQueryConstants.GET_ACTIVE_CUSTOMER__AND_SAVINGS_ACCOUNTS,
				null);
	}

	public List<AccountStateEntity> retrieveAllAccountStateList(Short prdTypeId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("prdTypeId", prdTypeId);
		List<AccountStateEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.RETRIEVEALLACCOUNTSTATES, queryParameters);
		for (AccountStateEntity accountStateEntity : queryResult) {
			for (AccountStateFlagEntity accountStateFlagEntity : accountStateEntity
					.getFlagSet()) {
				initialize(accountStateFlagEntity);
				initialize(accountStateFlagEntity.getNames());
			}
		}
		return queryResult;
	}

	public List<AccountCheckListBO> getStatusChecklist(Short accountStatusId,
			Short accountTypeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountTypeId", accountTypeId);
		queryParameters.put("accountStatus", accountStatusId);
		queryParameters.put("checklistStatus", 1);
		return executeNamedQuery(NamedQueryConstants.STATUSCHECKLIST,
				queryParameters);
	}

	public List<FeeBO> getAllApplicableFees(Integer accountId,
			FeeCategory categoryType) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountId", accountId);
		queryParameters.put("feeFrequencyTypeId", FeeFrequencyType.PERIODIC
				.getValue());
		queryParameters.put("active", FeeStatus.ACTIVE.getValue());
		if (categoryType.getValue().equals(FeeCategory.LOAN.getValue())) {
			queryParameters.put("category", FeeCategory.LOAN.getValue());
			return executeNamedQuery(
					NamedQueryConstants.GET_ALL_APPLICABLE_LOAN_FEE,
					queryParameters);
		} else {
			queryParameters.put("category1", FeeCategory.ALLCUSTOMERS
					.getValue());
			queryParameters.put("category2", categoryType.getValue());
			return executeNamedQuery(
					NamedQueryConstants.GET_ALL_APPLICABLE_CUSTOMER_FEE,
					queryParameters);
		}
	}

	public QueryResult search(String queryString, Short officeId)
			throws PersistenceException {

		AccountBO accountBO = findBySystemId(queryString);

		if (accountBO == null) return null;
		if (accountBO.getType() == AccountTypes.CUSTOMER_ACCOUNT) {
			return null;
		}
		QueryResult queryResult = QueryFactory
				.getQueryResult(CustomerSearchConstants.LOANACCOUNTIDSEARCH);
		((QueryResultAccountIdSearch)queryResult).setSearchString(queryString);
		String[] namedQuery = new String[2];
		List<Param> paramList = new ArrayList<Param>();
		QueryInputs queryInputs = new QueryInputs();
		String[] aliasNames = { "customerId", "centerName",
				"centerGlobalCustNum", "customerType", "branchGlobalNum",
				"branchName", "loanOfficerName", "loanOffcerGlobalNum",
				"customerStatus", "groupName", "groupGlobalCustNum",
				"clientName", "clientGlobalCustNum", "loanGlobalAccountNumber" };
		queryInputs
				.setPath("org.mifos.application.customer.business.CustomerSearch");
		queryInputs.setAliasNames(aliasNames);
		if (officeId != null) {
			if (officeId.shortValue() == 0) {
				namedQuery[0] = NamedQueryConstants.ACCOUNT_ID_SEARCH_NOOFFICEID_COUNT;
				namedQuery[1] = NamedQueryConstants.ACCOUNT_ID_SEARCH_NOOFFICEID;
			} else {
				namedQuery[0] = NamedQueryConstants.ACCOUNT_ID_SEARCH_COUNT;
				namedQuery[1] = NamedQueryConstants.ACCOUNT_ID_SEARCH;
				paramList.add(typeNameValue("Short", "OFFICEID", officeId));
			}
			paramList
					.add(typeNameValue("String", "SEARCH_STRING", queryString));
		}
		queryInputs.setQueryStrings(namedQuery);
		queryInputs.setParamList(paramList);
		try {
			queryResult.setQueryInputs(queryInputs);
		} catch (HibernateSearchException e) {
			throw new PersistenceException(e);
		}
		return queryResult;

	}

	public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(
			Short entityType) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(AccountConstants.ENTITY_TYPE, entityType);
		return executeNamedQuery(
				NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS, queryParameters);
	}

	public Short getAccountIdFromGlCode(String glCode) {
		Map nameToId = new HashMap<String, Short>();

		nameToId.put("1501", (short) 32);
		nameToId.put("1502", (short) 33);
		nameToId.put("1503", (short) 34);
		nameToId.put("1504", (short) 35);
		nameToId.put("1505", (short) 36);
		nameToId.put("1506", (short) 37);
		nameToId.put("1507", (short) 38);
		nameToId.put("1508", (short) 39);
		nameToId.put("1509", (short) 40);
		nameToId.put("4601", (short) 41);
		nameToId.put("4602", (short) 42);
		nameToId.put("4603", (short) 43);
		nameToId.put("4606", (short) 44);
		nameToId.put("5001", (short) 45);
		nameToId.put("5201", (short) 46);
		nameToId.put("5202", (short) 47);
		nameToId.put("5203", (short) 48);
		nameToId.put("5204", (short) 49);
		nameToId.put("5205", (short) 50);
		nameToId.put("6201", (short) 51);
		nameToId.put("10000", (short) 1);
		nameToId.put("11000", (short) 2);
		nameToId.put("11100", (short) 3);
		nameToId.put("11101", (short) 4);
		nameToId.put("11102", (short) 5);
		nameToId.put("11200", (short) 6);
		nameToId.put("11201", (short) 7);
		nameToId.put("11202", (short) 8);
		nameToId.put("13000", (short) 9);
		nameToId.put("13100", (short) 10);
		nameToId.put("13101", (short) 11);
		nameToId.put("13200", (short) 12);
		nameToId.put("13201", (short) 13);
		nameToId.put("20000", (short) 14);
		nameToId.put("22000", (short) 15);
		nameToId.put("22100", (short) 16);
		nameToId.put("22101", (short) 17);
		nameToId.put("23000", (short) 26);
		nameToId.put("23100", (short) 27);
		nameToId.put("23101", (short) 28);
		nameToId.put("24000", (short) 29);
		nameToId.put("24100", (short) 30);
		nameToId.put("24101", (short) 31);
		nameToId.put("30000", (short) 18);
		nameToId.put("31000", (short) 19);
		nameToId.put("31100", (short) 20);
		nameToId.put("31101", (short) 21);
		nameToId.put("31102", (short) 22);
		nameToId.put("31300", (short) 23);
		nameToId.put("31301", (short) 24);
		nameToId.put("31401", (short) 25);
		nameToId.put("40000", (short) 53);
		nameToId.put("41000", (short) 54);
		nameToId.put("41100", (short) 55);
		nameToId.put("41101", (short) 56);
		nameToId.put("41102", (short) 57);

		return (Short) nameToId.get(glCode);

	}

	public Short getAccountId(String accountName) {
		Map nameToId = new HashMap<String,Short>();
		
		nameToId.put("ASSETS",(short)1);
		nameToId.put("Cash and bank balances",(short)2);
		nameToId.put("Petty Cash Accounts",(short)3);
		nameToId.put("Cash 1",(short)4);
		nameToId.put("Cash 2",(short)5);
		nameToId.put("Bank Balances",(short)6);
		nameToId.put("Bank Account 1",(short)7);
		nameToId.put("Bank Account 2",(short)8);
		nameToId.put("Loan Portfolio",(short)9);
		nameToId.put("Loans and Advances",(short)10);
		nameToId.put("Loans to clients",(short)11);
		nameToId.put("Loan Loss Provisions ",(short)12);
		nameToId.put("Write-offs",(short)13);
		nameToId.put("LIABILITIES",(short)14);
		nameToId.put("Interest Payable",(short)15);
		nameToId.put("Interest payable on clients savings",(short)16);
		nameToId.put("Interest on mandatory savings",(short)17);
		nameToId.put("INCOME",(short)18);
		nameToId.put("Direct Income",(short)19);
		nameToId.put("Interest income from loans",(short)20);
		nameToId.put("Interest on loans",(short)21);
		nameToId.put("Penalty",(short)22);
		nameToId.put("Income from micro credit & lending activities",(short)23);
		nameToId.put("Fees",(short)24);
		nameToId.put("Income from 999 Account",(short)25);
		nameToId.put("Clients Deposits 1",(short)26);
		nameToId.put("Clients Deposits 2",(short)27);
		nameToId.put("Savings accounts ",(short)28);
		nameToId.put("Mandatory Savings 1",(short)29);
		nameToId.put("Mandatory Savings 2",(short)30);
		nameToId.put("Mandatory Savings Accounts",(short)31);
		nameToId.put("IGLoan",(short)32);
		nameToId.put("ManagedICICI-IGLoan",(short)33);
		nameToId.put("SPLoan",(short)34);
		nameToId.put("ManagedICICI-SPLoan",(short)35);
		nameToId.put("WFLoan",(short)36);
		nameToId.put("Managed WFLoan",(short)37);
		nameToId.put("Emergency Loans",(short)38);
		nameToId.put("Special  Loans",(short)39);
		nameToId.put("Micro Enterprises Loans",(short)40);
		nameToId.put("Emergency Fund",(short)41);
		nameToId.put("Margin Money-1",(short)42);
		nameToId.put("Margin Money-2",(short)43);
		nameToId.put("Village Development Fund",(short)44);
		nameToId.put("Interest",(short)45);
		nameToId.put("Processing Fees",(short)46);
		nameToId.put("Annual Subscription Fee",(short)47);
		nameToId.put("Emergency Loan Documentation Fee",(short)48);
		nameToId.put("Sale of Publication",(short)49);
		nameToId.put("Fines & Penalties",(short)50);
		nameToId.put("Miscelleneous Income",(short)51);
		nameToId.put("EXPENDITURE",(short)53);
		nameToId.put("Direct Expenditure",(short)54);
		nameToId.put("Cost of Funds",(short)55);
		nameToId.put("Interest on clients voluntary savings",(short)56);
		nameToId.put("Interest on clients mandatory savings",(short)57);
		
		return (Short)nameToId.get(accountName);
		
	}
}
