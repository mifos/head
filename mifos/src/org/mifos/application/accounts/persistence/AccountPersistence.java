package org.mifos.application.accounts.persistence;

import java.sql.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.checklist.util.valueobjects.CheckListMaster;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.fees.business.FeeBO;
import org.mifos.application.fees.util.helpers.FeeCategory;
import org.mifos.application.fees.util.helpers.FeeFrequencyType;
import org.mifos.application.fees.util.helpers.FeeStatus;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.QueryFactory;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.persistence.Persistence;

public class AccountPersistence extends Persistence {

	public AccountBO getAccount(Integer accountId) throws PersistenceException {
		return (AccountBO) getPersistentObject(AccountBO.class, accountId);
	}

	public Integer getAccountRunningNumber() throws PersistenceException {
		Object queryResult = execUniqueResultNamedQuery(
				NamedQueryConstants.GET_MAX_ACCOUNT_ID, null);
		Integer accountRunningNumber = queryResult == null ? new Integer(0)
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
				CustomerConstants.CENTER_ACTIVE_STATE);
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

	public List<Integer> getCustomerAccountsForFee(Short feeId)
			throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("FEEID", feeId);
		return executeNamedQuery(
				NamedQueryConstants.GET_CUSTOMER_ACCOUNTS_FOR_FEE,
				queryParameters);
	}

	public List<AccountBO> getActiveCustomerAndSavingsAccounts()
			throws PersistenceException {
		return executeNamedQuery(
				NamedQueryConstants.GET_ACTIVE_CUSTOMER__AND_SAVINGS_ACCOUNTS,
				null);
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

	public AccountBO getSavingsAccountWithAccountActionsInitialized(
			Integer accountId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountId", accountId);
		List obj = executeNamedQuery(
				"accounts.retrieveSavingsAccountWithAccountActions",
				queryParameters);
		Object[] obj1 = (Object[]) obj.get(0);
		return (AccountBO) obj1[0];
	}

	public AccountBO getLoanAccountWithAccountActionsInitialized(
			Integer accountId) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountId", accountId);
		List obj = executeNamedQuery(
				"accounts.retrieveLoanAccountWithAccountActions",
				queryParameters);
		Object[] obj1 = (Object[]) obj.get(0);
		return (AccountBO) obj1[0];
	}

	public List<AccountStateEntity> retrieveAllAccountStateList(Short prdTypeId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("prdTypeId", prdTypeId);
		List<AccountStateEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.RETRIEVEALLACCOUNTSTATES, queryParameters);
		return queryResult;
	}

	public List<CheckListMaster> getStatusChecklist(Short accountStatusId,
			Short accountTypeId) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountTypeId", accountTypeId);
		queryParameters.put("accountStatus", accountStatusId);
		queryParameters.put("checklistStatus", 1);
		return executeNamedQuery(NamedQueryConstants.STATUSCHECKLIST,
				queryParameters);
	}

	public List<FeeBO> getAllAppllicableFees(Integer accountId,
			Short categoryType) throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountId", accountId);
		queryParameters.put("feeFrequencyTypeId", FeeFrequencyType.PERIODIC
				.getValue());
		queryParameters.put("active", FeeStatus.ACTIVE.getValue());
		if (categoryType.equals(FeeCategory.LOAN.getValue())) {
			queryParameters.put("category", FeeCategory.LOAN.getValue());
			return executeNamedQuery(
					NamedQueryConstants.GET_ALL_APPLICABLE_LOAN_FEE,
					queryParameters);
		} else {
			queryParameters.put("category1", FeeCategory.ALLCUSTOMERS
					.getValue());
			queryParameters.put("category2", categoryType);
			return executeNamedQuery(
					NamedQueryConstants.GET_ALL_APPLICABLE_CUSTOMER_FEE,
					queryParameters);
		}
	}

}
