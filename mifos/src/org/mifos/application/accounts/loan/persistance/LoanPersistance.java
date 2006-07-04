package org.mifos.application.accounts.loan.persistance;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountActionDateEntity;
import org.mifos.application.accounts.business.AccountActionEntity;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountFeesEntity;
import org.mifos.application.accounts.business.LoanAccountView;
import org.mifos.application.accounts.loan.business.LoanBO;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.AccountStates;
import org.mifos.application.accounts.util.helpers.AccountTypes;
import org.mifos.application.productdefinition.business.PrdOfferingBO;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.struts.tags.DateHelper;
import org.mifos.framework.util.helpers.Money;

public class LoanPersistance extends Persistence {

	public LoanPersistance() {
		super();
	}

	public AccountActionEntity getAccountActionEntity(Short actionType) {
		Session session = HibernateUtil.getSessionTL();
		AccountActionEntity action = (AccountActionEntity) session.get(AccountActionEntity.class,
				actionType);
		return action;
	}

	public List<LoanAccountView> getLoanAccountsForCustomer(Integer customerId,Date disbursmentDate) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("CUSTOMER_ID", customerId);
		queryParameters.put("DISBURSEMENT_DATE", disbursmentDate);
		List<LoanAccountView> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_LISTOFACCOUNTS_FOR_CUSTOMER,
				queryParameters);
		return queryResult;
	}

	public List<AccountActionDateEntity> getLoanAccountTransactionDetail(
			Integer accountId, Date transactionDate) {
		HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		queryParameters.put("ACCOUNT_ID", accountId);
		queryParameters.put("ACTION_DATE", transactionDate);
		queryParameters.put("PAYMENT_STATUS", AccountConstants.PAYMENT_UNPAID);
		List<AccountActionDateEntity> queryResult = executeNamedQuery(
				NamedQueryConstants.GET_LISTOFACCOUNTSTRXNS_FOR_LOAN,
				queryParameters);
		return queryResult;
	}
	  public List<PrdOfferingBO> getLoanOfferingBOForCustomer(String customerSearchId,Date disbursmentDate){
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
		  
			queryParameters.put("CUSTOMER_SEARCH_ID", customerSearchId);
			queryParameters.put("DISBURSEMENT_DATE", disbursmentDate);
			List<PrdOfferingBO> queryResult = executeNamedQuery(
					NamedQueryConstants.GET_LOANOFFERINGLIST_FOR_LOANS,
					queryParameters);
			return queryResult;

	  }
	  
		public Double getFeeAmountAtDisbursement(Integer accountId, Date transactionDate) {
			HashMap<String, Object> queryParameters = new HashMap<String, Object>();
			queryParameters.put("ACCOUNT_ID", accountId);
			//queryParameters.put("ACTION_DATE", transactionDate);

			List<AccountFeesEntity>queryResult = executeNamedQuery(
					NamedQueryConstants.GET_FEE_AMOUNT_AT_DISBURSEMENT,
					queryParameters);
			Money amount = new Money();
			for (AccountFeesEntity entity : queryResult) {
				amount=amount.add(entity.getAccountFeeAmount());
			}
			
			return amount.getAmountDoubleValue();
		}
		
		public LoanBO findBySystemId(String accountGlobalNum)throws PersistenceException {
			Map<String,String> queryParameters = new HashMap<String,String>();
			LoanBO loanBO = null;
			queryParameters.put("globalAccountNumber", accountGlobalNum);
			try{
				List<AccountBO> queryResult = executeNamedQuery(NamedQueryConstants.FIND_ACCOUNT_BY_SYSTEM_ID, queryParameters);
				if(null != queryResult && queryResult.size() > 0){
					loanBO = (LoanBO)queryResult.get(0);
				}
			}catch(HibernateException he){
				throw new PersistenceException(he);
			}
			
			return loanBO;
		}
		
		/**
		 * 
		 * Add an account Status Change History Object to it with the required parameters.
		 * Set the State of the Account to Active in Bad Standing.
		 * @param account - The AccountBO Object returned from the query
		 */
		public List<LoanBO> getLoanAccountsInArrears(Short latenessDays) throws PersistenceException{
			
			Map<String,Object> queryParameters = new HashMap<String,Object>();
			
			String systemDate = DateHelper.getCurrentDate(Configuration.getInstance().getSystemConfig().getMFILocale()); 
			Date localDate = DateHelper.getLocaleDate(Configuration.getInstance().getSystemConfig().getMFILocale(), systemDate);
			Calendar currentDate = new GregorianCalendar();
			currentDate.setTime(localDate);
			int year = currentDate.get(Calendar.YEAR);
			int month = currentDate.get(Calendar.MONTH);
			int day = currentDate.get(Calendar.DAY_OF_MONTH);
			currentDate = new GregorianCalendar(year, month, day-latenessDays);
			Date date = new Date(currentDate.getTimeInMillis());
								
			queryParameters.put("ACCOUNTTYPE_ID",Short.valueOf(AccountTypes.LOANACCOUNT));
			queryParameters.put("PAYMENTSTATUS",Short.valueOf(AccountConstants.PAYMENT_UNPAID));
			queryParameters.put("LOANAPPROVED",Short.valueOf(AccountStates.LOANACC_APPROVED));
			queryParameters.put("LOANACTIVEINGOODSTAND",Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING));
			queryParameters.put("CHECKDATE",date);
											
			 try{
				 List<LoanBO> loanAccounts =  executeNamedQuery(NamedQueryConstants.GETLOANACOUNTSINARREARS, queryParameters);
				 
					for (LoanBO loanBO : loanAccounts) {
						if(loanBO.accountStatusChangeHistory != null && loanBO.accountStatusChangeHistory.size() > 0) 
							Hibernate.initialize(loanBO.accountStatusChangeHistory);
					}
					return loanAccounts;
					}catch(HibernateException he){
					throw new PersistenceException(he);
				}
					
		}
		
		public LoanBO getAccount(Integer accountId) {
			return (LoanBO)HibernateUtil.getSessionTL().get(LoanBO.class, accountId);
		}
}
