package org.mifos.accounts.loan.persistance;

import java.util.HashMap;
import java.util.Map;

import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;

public class LoanDaoHibernate implements LoanDao {

    private final GenericDao genericDao;

    public LoanDaoHibernate(GenericDao genericDao) {
        this.genericDao = genericDao;
    }

    @Override
    public LoanBO findByGlobalAccountNum(String globalAccountNum) {
        Map<String, String> queryParameters = new HashMap<String, String>();
        queryParameters.put("globalAccountNumber", globalAccountNum);
        return (LoanBO) this.genericDao.executeUniqueResultNamedQuery(NamedQueryConstants.FIND_LOAN_ACCOUNT_BY_SYSTEM_ID, queryParameters);
    }

    @Override
    public LoanBO findById(Integer accountId) {
        Map<String, Integer> queryParameters = new HashMap<String, Integer>();
        queryParameters.put("ACCOUNT_ID", accountId);
        return (LoanBO) this.genericDao.executeUniqueResultNamedQuery("loan.findById", queryParameters);
    }
}