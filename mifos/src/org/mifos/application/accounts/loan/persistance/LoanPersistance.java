package org.mifos.application.accounts.loan.persistance;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.AccountPaymentEntity;
import org.mifos.application.accounts.business.AccountTrxnEntity;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.accounts.util.helpers.PaymentStatus;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;

public class LoanPersistance extends Persistence {

	public Double getFeeAmountAtDisbursement(Integer accountId)
			throws PersistenceException {
		Money amount = new Money();
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ACCOUNT_ID", accountId);
		List<AccountFeesEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_FEE_AMOUNT_AT_DISBURSEMENT,
				queryParameters);
		for (AccountFeesEntity entity : queryResult)
			amount = amount.add(entity.getAccountFeeAmount());
		return amount.getAmountDoubleValue();
	}

	public LoanBO findBySystemId(String accountGlobalNum)
			throws PersistenceException {
		Map<String, String> queryParameters = new HashMap<String, String>();
		queryParameters.put("globalAccountNumber", accountGlobalNum);
		Object queryResult = execUniqueResultNamedQuery(
				NamedQueryConstants.FIND_ACCOUNT_BY_SYSTEM_ID, queryParameters);
		return queryResult == null ? null : (LoanBO) queryResult;
	}

	public List<Integer> getLoanAccountsInArrearsInGoodStanding(Short latenessDays)
			throws PersistenceException {

		String systemDate = DateHelper.getCurrentDate(Configuration
				.getInstance().getSystemConfig().getMFILocale());
		Date localDate = DateHelper.getLocaleDate(Configuration.getInstance()
				.getSystemConfig().getMFILocale(), systemDate);
		Calendar currentDate = new GregorianCalendar();
		currentDate.setTime(localDate);
		int year = currentDate.get(Calendar.YEAR);
		int month = currentDate.get(Calendar.MONTH);
		int day = currentDate.get(Calendar.DAY_OF_MONTH);
		currentDate = new GregorianCalendar(year, month, day - latenessDays);
		Date date = new Date(currentDate.getTimeInMillis());

		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ACCOUNTTYPE_ID", AccountTypes.LOANACCOUNT
				.getValue());
		queryParameters.put("PAYMENTSTATUS", Short.valueOf(PaymentStatus.UNPAID
				.getValue()));
		queryParameters.put("LOANAPPROVED", Short
				.valueOf(AccountStates.LOANACC_APPROVED));
		queryParameters.put("LOANACTIVEINGOODSTAND", Short
				.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING));
		queryParameters.put("CHECKDATE", date);

		return executeNamedQuery(NamedQueryConstants.GET_LOAN_ACOUNTS_IN_ARREARS_IN_GOOD_STANDING,
				queryParameters);
	}

	public List<Integer> getLoanAccountsInArrears(Short latenessDays)
		throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		
		Calendar currentDate = new GregorianCalendar();
		currentDate.add(Calendar.DAY_OF_MONTH,-latenessDays);
		
		currentDate = new GregorianCalendar(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH), currentDate.get(Calendar.DAY_OF_MONTH),0,0,0);

		queryParameters.put("ACCOUNTTYPE_ID", AccountTypes.LOANACCOUNT
				.getValue());
		queryParameters.put("PAYMENTSTATUS", Short.valueOf(PaymentStatus.UNPAID
				.getValue()));
		queryParameters.put("BADSTANDING", Short
				.valueOf(AccountStates.LOANACC_BADSTANDING));
		queryParameters.put("LOANACTIVEINGOODSTAND", Short
				.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING));
		
		queryParameters.put("CHECKDATE", currentDate.getTime());
		
		return executeNamedQuery(NamedQueryConstants.GET_LOAN_ACOUNTS_IN_ARREARS,
				queryParameters);
	}
	
	public LoanBO getAccount(Integer accountId) throws PersistenceException {
		return (LoanBO) getPersistentObject(LoanBO.class, accountId);
	}

	public Short getLastPaymentAction(Integer accountId)
			throws PersistenceException {
		HashMap queryParameters = new HashMap();
		queryParameters.put("accountId", accountId);
		List<AccountPaymentEntity> accountPaymentList = executeNamedQuery(
				NamedQueryConstants.RETRIEVE_MAX_ACCPAYMENT, queryParameters);
		if (accountPaymentList != null && accountPaymentList.size() > 0) {
			AccountPaymentEntity accountPayment = accountPaymentList.get(0);
			Set<AccountTrxnEntity> accountTrxnSet = accountPayment
					.getAccountTrxns();
			for (AccountTrxnEntity accountTrxn : accountTrxnSet) {
				if (accountTrxn.getAccountActionEntity().getId().shortValue() == AccountConstants.ACTION_DISBURSAL) {
					return accountTrxn.getAccountActionEntity().getId();
				}
			}
		}
		return null;
	}

	public LoanOfferingBO getLoanOffering(Short loanOfferingId, Short localeId)
			throws PersistenceException {
		LoanOfferingBO loanOffering = (LoanOfferingBO) getPersistentObject(
				LoanOfferingBO.class, loanOfferingId);
		if (loanOffering.getLoanOfferingFunds() != null
				&& loanOffering.getLoanOfferingFunds().size() > 0)
			for (LoanOfferingFundEntity loanOfferingFund : loanOffering
					.getLoanOfferingFunds()) {
				loanOfferingFund.getFund().getFundId();
				loanOfferingFund.getFund().getFundName();
			}
		loanOffering.getInterestTypes().setLocaleId(localeId);
		loanOffering.getGracePeriodType().setLocaleId(localeId);
		return loanOffering;
	}

	public List<LoanBO> getSearchResults(String officeId, String personnelId,
			String type, String currentStatus) throws PersistenceException {
		Map<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("OFFICE_ID", officeId);
		queryParameters.put("PERSONNEL_ID", personnelId);
		queryParameters.put("CURRENT_STATUS", currentStatus);
		return executeNamedQuery(NamedQueryConstants.GET_SEARCH_RESULTS,
				queryParameters);
	}

	public void deleteInstallments(
			Set<AccountActionDateEntity> accountActionDates)
			throws PersistenceException {
		try {
			Session session = HibernateUtil.getSessionTL();
			for (AccountActionDateEntity entity : accountActionDates) {
				session.delete(entity);
			}
		} catch (HibernateException he) {
			throw new PersistenceException(he);
		}
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
}
