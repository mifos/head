package org.mifos.application.accounts.savings.persistence;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountStateEntity;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.accounts.savings.business.SavingsTrxnDetailEntity;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.customer.business.CustomFieldDefinitionEntity;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.productdefinition.util.helpers.PrdOfferingView;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.helpers.Money;

public class SavingsPersistence extends Persistence {

	private MifosLogger logger = MifosLogManager
			.getLogger(LoggerConstants.ACCOUNTSLOGGER);

	public List<PrdOfferingView> getSavingsProducts(OfficeBO branch,
			CustomerLevelEntity customerLevel, short savingsTypeId)
			throws PersistenceException {
		logger
				.debug("In SavingsPersistence::getSavingsProducts(), customerLevelId: "
						+ customerLevel.getId());
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(AccountConstants.PRDTYPEID, ProductType.SAVINGS
				.getValue());
		queryParameters.put(AccountConstants.PRDSTATUS, PrdStatus.SAVINGS_ACTIVE
				.getValue());
		queryParameters.put(AccountConstants.PRODUCT_APPLICABLE_TO,
				customerLevel.getProductApplicableType());
		return executeNamedQuery(
				NamedQueryConstants.GET_APPLICABLE_SAVINGS_PRODUCT_OFFERINGS,
				queryParameters);
	}

	public List<CustomFieldDefinitionEntity> retrieveCustomFieldsDefinition(
			Short entityType) throws PersistenceException {
		logger
				.debug("In SavingsPersistence::retrieveCustomFieldsDefinition(), entityType: "
						+ entityType);
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(AccountConstants.ENTITY_TYPE, entityType);
		return executeNamedQuery(
				NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS, queryParameters);
	}

	public SavingsBO findById(Integer accountId) throws PersistenceException {
		logger.debug("In SavingsPersistence::findById(), accountId: "
				+ accountId);
		return (SavingsBO) getPersistentObject(SavingsBO.class, accountId);
	}

	public SavingsBO findBySystemId(String globalAccountNumber)
			throws PersistenceException {
		logger
				.debug("In SavingsPersistence::findBySystemId(), globalAccountNumber: "
						+ globalAccountNumber);
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put(AccountConstants.GLOBAL_ACCOUNT_NUMBER,
				globalAccountNumber);
		Object queryResult = execUniqueResultNamedQuery(
				NamedQueryConstants.FIND_ACCOUNT_BY_SYSTEM_ID, queryParameters);
		SavingsBO savings = queryResult == null ? null
				: (SavingsBO) queryResult;
		if (savings != null && savings.getRecommendedAmount() == null) {
			savings.setRecommendedAmount(new Money());
			initialize(savings.getAccountActionDates());
			initialize(savings.getAccountNotes());
			initialize(savings.getAccountFlags());
		}
		return savings;

	}

	public SavingsTrxnDetailEntity retrieveLastTransaction(Integer accountId,
			Date date) throws PersistenceException {

		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountId", accountId);
		queryParameters.put("date", date);
		Object queryResult = execUniqueResultNamedQuery(
				NamedQueryConstants.RETRIEVE_LAST_TRXN, queryParameters);
		return queryResult == null ? null
				: (SavingsTrxnDetailEntity) queryResult;
	}

	public SavingsTrxnDetailEntity retrieveFirstTransaction(Integer accountId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("accountId", accountId);
		Object queryResult = execUniqueResultNamedQuery(
				NamedQueryConstants.RETRIEVE_FIRST_TRXN, queryParameters);
		return queryResult == null ? null
				: (SavingsTrxnDetailEntity) queryResult;
	}

	public AccountStateEntity getAccountStatusObject(Short accountStatusId)
			throws PersistenceException {
		logger
				.debug("In SavingsPersistence::getAccountStatusObject(), accountStatusId: "
						+ accountStatusId);
		return (AccountStateEntity) getPersistentObject(
				AccountStateEntity.class, accountStatusId);
	}

	public List<SavingsBO> getAllClosedAccount(Integer customerId)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("customerId", customerId);
		List queryResult = executeNamedQuery(
				NamedQueryConstants.VIEWALLSAVINGSCLOSEDACCOUNTS,
				queryParameters);
		return queryResult;
	}

	public List<Integer> retreiveAccountsPendingForIntCalc(Date currentDate)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("currentDate", currentDate);
		List<Integer> queryResult = executeNamedQuery(
				NamedQueryConstants.RETRIEVE_ACCCOUNTS_FOR_INT_CALC,
				queryParameters);
		return queryResult;
	}

	public List<Integer> retreiveAccountsPendingForIntPosting(Date currentDate)
			throws PersistenceException {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("currentDate", currentDate);
		List<Integer> queryResult = executeNamedQuery(
				NamedQueryConstants.RETRIEVE_ACCCOUNTS_FOR_INT_POST,
				queryParameters);
		return queryResult;
	}

	public int getMissedDeposits(Integer accountId, Date currentDate)
			throws PersistenceException {
		Integer count = 0;
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ACCOUNT_ID", accountId);
		queryParameters.put("ACCOUNT_TYPE_ID", AccountTypes.SAVINGSACCOUNT
				.getValue());
		queryParameters.put("ACTIVE", AccountStates.SAVINGS_ACC_APPROVED);
		queryParameters.put("CHECKDATE", currentDate);
		queryParameters.put("PAYMENTSTATUS", PaymentStatus.UNPAID.getValue());

		List queryResult = executeNamedQuery(
				NamedQueryConstants.GET_MISSED_DEPOSITS_COUNT, queryParameters);

		if (null != queryResult && queryResult.size() > 0) {
			Object obj = queryResult.get(0);
			if (obj != null)
				count = (Integer) obj;
		}
		return count.intValue();
	}

	public int getMissedDepositsPaidAfterDueDate(Integer accountId)
			throws PersistenceException {
		Integer count = 0;
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ACCOUNT_ID", accountId);
		queryParameters.put("ACCOUNT_TYPE_ID", AccountTypes.SAVINGSACCOUNT
				.getValue());
		queryParameters.put("ACTIVE", AccountStates.SAVINGS_ACC_APPROVED);
		queryParameters.put("PAYMENTSTATUS", PaymentStatus.PAID.getValue());

		List queryResult = executeNamedQuery(
				NamedQueryConstants.GET_MISSED_DEPOSITS_PAID_AFTER_DUEDATE,
				queryParameters);

		if (null != queryResult && queryResult.size() > 0) {
			Object obj = queryResult.get(0);
			if (obj != null)
				count = (Integer) obj;
		}
		return count.intValue();
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
}
