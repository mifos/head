/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */

package org.mifos.accounts.loan.persistance;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.mifos.application.NamedQueryConstants;
import org.mifos.accounts.business.AccountActionDateEntity;
import org.mifos.accounts.business.AccountFeesEntity;
import org.mifos.accounts.business.AccountPaymentEntity;
import org.mifos.accounts.business.AccountTrxnEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.util.helpers.AccountActionTypes;
import org.mifos.accounts.util.helpers.AccountStates;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.accounts.util.helpers.PaymentStatus;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.productdefinition.business.LoanOfferingBO;
import org.mifos.application.productdefinition.business.LoanOfferingFundEntity;
import org.mifos.core.MifosRuntimeException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.persistence.Persistence;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;

public class LoanPersistence extends Persistence {

    @SuppressWarnings("unchecked")
    public Money getFeeAmountAtDisbursement(final Integer accountId, final MifosCurrency currency) {
        Money amount = new Money(currency);
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ACCOUNT_ID", accountId);

        try {
            List<AccountFeesEntity> queryResult = executeNamedQuery(NamedQueryConstants.GET_FEE_AMOUNT_AT_DISBURSEMENT,
                    queryParameters);
            for (AccountFeesEntity entity : queryResult) {
                if (amount == null) {
                    amount = entity.getAccountFeeAmount();
                } else {
                    amount = amount.add(entity.getAccountFeeAmount());
                }
            }
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }

        return amount;
    }

    public LoanBO findBySystemId(final String accountGlobalNum) throws PersistenceException {
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("globalAccountNumber", accountGlobalNum);
        Object queryResult = execUniqueResultNamedQuery(NamedQueryConstants.FIND_LOAN_ACCOUNT_BY_SYSTEM_ID,
                queryParameters);
        return queryResult == null ? null : (LoanBO) queryResult;
    }

    public LoanBO findByExternalId(final String externalId) throws PersistenceException {
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("externalId", externalId);
        Object queryResult = execUniqueResultNamedQuery(NamedQueryConstants.FIND_LOAN_ACCOUNT_BY_EXTERNAL_ID,
                queryParameters);
        return queryResult == null ? null : (LoanBO) queryResult;
    }
    
    @SuppressWarnings("unchecked")
    public List<LoanBO> findIndividualLoans(final String accountId) throws PersistenceException {
        Map<String, Integer> queryParameters = new HashMap<String, Integer>();
        queryParameters.put(LoanConstants.LOANACCOUNTID, new Integer(accountId));
        List<LoanBO> queryResult = executeNamedQuery(NamedQueryConstants.FIND_INDIVIDUAL_LOANS, queryParameters);
        return queryResult == null ? null : (List<LoanBO>) queryResult;
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getLoanAccountsInArrearsInGoodStanding(final Short latenessDays) throws PersistenceException, InvalidDateException {

        /*
         * TODO: refactor to use Joda Time This code appears to be trying to
         * just get a date that is "latenessDays" before the current date.
         */
        String systemDate = DateUtils.getCurrentDate();
        Date localDate = DateUtils.getLocaleDate(systemDate);
        Calendar currentDate = new GregorianCalendar();
        currentDate.setTime(localDate);
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);
        currentDate = new GregorianCalendar(year, month, day - latenessDays);
        Date date = new Date(currentDate.getTimeInMillis());

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ACCOUNTTYPE_ID", AccountTypes.LOAN_ACCOUNT.getValue());
        queryParameters.put("PAYMENTSTATUS", Short.valueOf(PaymentStatus.UNPAID.getValue()));
        queryParameters.put("LOANACTIVEINGOODSTAND", Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING));
        queryParameters.put("CHECKDATE", date);

        return executeNamedQuery(NamedQueryConstants.GET_LOAN_ACOUNTS_IN_ARREARS_IN_GOOD_STANDING, queryParameters);
    }

    @SuppressWarnings("unchecked")
    public List<Integer> getLoanAccountsInArrears(final Short latenessDays) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();

        Calendar currentDate = new DateTimeService().getCurrentDateTime().toGregorianCalendar();
        currentDate.add(Calendar.DAY_OF_MONTH, -latenessDays);

        currentDate = new GregorianCalendar(currentDate.get(Calendar.YEAR), currentDate.get(Calendar.MONTH),
                currentDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

        queryParameters.put("ACCOUNTTYPE_ID", AccountTypes.LOAN_ACCOUNT.getValue());
        queryParameters.put("PAYMENTSTATUS", Short.valueOf(PaymentStatus.UNPAID.getValue()));
        queryParameters.put("BADSTANDING", Short.valueOf(AccountStates.LOANACC_BADSTANDING));
        queryParameters.put("LOANACTIVEINGOODSTAND", Short.valueOf(AccountStates.LOANACC_ACTIVEINGOODSTANDING));

        queryParameters.put("CHECKDATE", currentDate.getTime());

        return executeNamedQuery(NamedQueryConstants.GET_LOAN_ACOUNTS_IN_ARREARS, queryParameters);
    }

    public LoanBO getAccount(final Integer accountId) throws PersistenceException {
        return (LoanBO) getPersistentObject(LoanBO.class, accountId);
    }

    @SuppressWarnings("unchecked")
    public Short getLastPaymentAction(final Integer accountId) throws PersistenceException {
        Map<String, Integer> queryParameters = new HashMap<String, Integer>();
        queryParameters.put("accountId", accountId);
        List<AccountPaymentEntity> accountPaymentList = executeNamedQuery(NamedQueryConstants.RETRIEVE_MAX_ACCPAYMENT,
                queryParameters);
        if (accountPaymentList != null && accountPaymentList.size() > 0) {
            AccountPaymentEntity accountPayment = accountPaymentList.get(0);
            Set<AccountTrxnEntity> accountTrxnSet = accountPayment.getAccountTrxns();
            for (AccountTrxnEntity accountTrxn : accountTrxnSet) {
                if (accountTrxn.getAccountActionEntity().getId().shortValue() == AccountActionTypes.DISBURSAL
                        .getValue()) {
                    return accountTrxn.getAccountActionEntity().getId();
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<AccountPaymentEntity> retrieveAllAccountPayments(final Integer accountId) throws PersistenceException {
        Map<String, Integer> queryParameters = new HashMap<String, Integer>();
        queryParameters.put("accountId", accountId);
        List<AccountPaymentEntity> accountPaymentList = executeNamedQuery(NamedQueryConstants.RETRIEVE_ALL_ACCPAYMENT,
                queryParameters);
        return accountPaymentList;
    }

    public LoanOfferingBO getLoanOffering(final Short loanOfferingId, final Short localeId) throws PersistenceException {
        LoanOfferingBO loanOffering = (LoanOfferingBO) getPersistentObject(LoanOfferingBO.class, loanOfferingId);
        if (loanOffering.getLoanOfferingFunds() != null && loanOffering.getLoanOfferingFunds().size() > 0) {
            for (LoanOfferingFundEntity loanOfferingFund : loanOffering.getLoanOfferingFunds()) {
                loanOfferingFund.getFund().getFundId();
                loanOfferingFund.getFund().getFundName();
            }
        }
        loanOffering.getInterestTypes().setLocaleId(localeId);
        loanOffering.getGracePeriodType().setLocaleId(localeId);
        return loanOffering;
    }

    @SuppressWarnings("unchecked")
    public List<LoanBO> getSearchResults(final String officeId, final String personnelId, final String currentStatus)
            throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("OFFICE_ID", Short.parseShort(officeId));
        queryParameters.put("PERSONNEL_ID", Short.parseShort(personnelId));
        queryParameters.put("CURRENT_STATUS", Short.parseShort(currentStatus));
        return executeNamedQuery(NamedQueryConstants.GET_SEARCH_RESULTS, queryParameters);
    }

    public void deleteInstallments(final Set<AccountActionDateEntity> accountActionDates) throws PersistenceException {
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            for (AccountActionDateEntity entity : accountActionDates) {
                session.delete(entity);
            }
        } catch (HibernateException he) {
            throw new PersistenceException(he);
        }
    }

    @SuppressWarnings("unchecked")
    public LoanBO getLoanAccountWithAccountActionsInitialized(final Integer accountId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("accountId", accountId);
        List obj = executeNamedQuery("accounts.retrieveLoanAccountWithAccountActions", queryParameters);
        Object[] obj1 = (Object[]) obj.get(0);
        return (LoanBO) obj1[0];
    }

    public Money getLastLoanAmountForCustomer(final Integer customerId, final Integer excludeAccountId) throws PersistenceException {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("customerId", customerId);
        queryParameters.put("excludeAccountId", excludeAccountId);
        Object obj = execUniqueResultNamedQuery(NamedQueryConstants.LAST_LOAN_AMOUNT_CUSTOMER, queryParameters);
        if (null != obj) {
            return (Money) obj;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<LoanBO> getLoanAccountsInActiveBadStanding(final Short branchId, final Short loanOfficerId, final Short loanProductId)
            throws PersistenceException {
        String activeBadAccountIdQuery = "from org.mifos.accounts.loan.business.LoanBO loan where loan.accountState.id = 9";
        StringBuilder queryString = loanQueryString(branchId, loanOfficerId, loanProductId, activeBadAccountIdQuery);
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            Query query = session.createQuery(queryString.toString());
            return query.list();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @SuppressWarnings( { "cast", "unchecked" })
    public List<LoanBO> getLoanAccountsActiveInGoodBadStanding(final Integer customerId) throws PersistenceException {
        try {
            HashMap<String, Object> queryParameters = new HashMap<String, Object>();
            queryParameters.put(LoanConstants.LOANACTIVEINGOODSTAND, AccountStates.LOANACC_ACTIVEINGOODSTANDING);
            queryParameters.put(LoanConstants.CUSTOMER, customerId);
            queryParameters.put(LoanConstants.LOANACTIVEINBADSTAND, AccountStates.LOANACC_BADSTANDING);
            queryParameters.put(LoanConstants.ACCOUNTTYPE_ID, AccountTypes.LOAN_ACCOUNT.getValue());

            return (List<LoanBO>) executeNamedQuery(NamedQueryConstants.ACCOUNT_GETALLLOANBYCUSTOMER, queryParameters);

        } catch (Exception e) {
            throw new PersistenceException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public BigDecimal getTotalOutstandingPrincipalOfLoanAccountsInActiveGoodStanding(final Short branchId,
            final Short loanOfficerId, final Short loanProductId) throws PersistenceException {
        BigDecimal loanBalanceAmount = new BigDecimal(0);
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            Criteria criteria = session.createCriteria(LoanBO.class).setProjection(
                    Projections.sum("loanBalance.amount")).add(Restrictions.eq("accountState.id", (short) 5)).add(
                    Restrictions.eq("office.officeId", branchId));
            if (loanOfficerId != (short) -1) {
                criteria.add(Restrictions.eq("personnel.personnelId", loanOfficerId));
            }
            if (loanProductId != (short) -1) {
                criteria.add(Restrictions.eq("loanOffering.prdOfferingId", loanProductId));
            }

            List list = criteria.list();
            loanBalanceAmount = (BigDecimal) list.get(0);
        } catch (Exception e) {
            throw new PersistenceException(e);
        }
        return loanBalanceAmount;
    }

    @SuppressWarnings("unchecked")
    public List<LoanBO> getActiveLoansBothInGoodAndBadStandingByLoanOfficer(final Short branchId, final Short loanOfficerId,
            final Short loanProductId) throws PersistenceException {

        String activeLoansQuery = "from org.mifos.accounts.loan.business.LoanBO loan where loan.accountState.id in (5,9)";
        StringBuilder queryString = loanQueryString(branchId, loanOfficerId, loanProductId, activeLoansQuery);
        try {
            Session session = StaticHibernateUtil.getSessionTL();
            Query query = session.createQuery(queryString.toString());
            return query.list();
        } catch (Exception e) {
            throw new PersistenceException(e);
        }

    }
    
    public void save(final List<LoanBO> loans) {
        Session session = getHibernateUtil().getSessionTL();
        for (LoanBO loan : loans) {
            session.save(loan);
        }
    }

    @SuppressWarnings("unchecked")
    public List<LoanBO> getAllLoanAccounts() throws PersistenceException {
        HashMap<String, Object> queryParameters = new HashMap<String, Object>();

        List<LoanBO> queryResult = executeNamedQuery(NamedQueryConstants.GET_ALL_LOAN_ACCOUNTS, queryParameters);
        return queryResult;

    }
    
    private StringBuilder loanQueryString(final Short branchId, final Short loanOfficerId, final Short loanProductId,
            final String goodAccountIdQueryString) {

        StringBuilder queryString = new StringBuilder(goodAccountIdQueryString);
        if (loanOfficerId != (short) -1) {
            queryString.append(" and loan.personnel.personnelId = " + loanOfficerId);
        }
        if (loanProductId != (short) -1) {
            queryString.append(" and loan.loanOffering.prdOfferingId = " + loanProductId);
        }
        queryString.append(" and loan.office.officeId = " + branchId);
        return queryString;
    }
}
