package org.mifos.accounts.loan.persistance;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.dto.domain.SurveyDto;
import org.springframework.beans.factory.annotation.Autowired;

public class LoanDaoHibernate implements LoanDao {

    private final GenericDao genericDao;

    @Autowired
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

    @SuppressWarnings("unchecked")
    @Override
    public List<SurveyDto> getAccountSurveyDto(final Integer accountId) {

        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("ACCOUNT_ID", accountId);
        List<Object[]> queryResult = (List<Object[]>) this.genericDao.executeNamedQuery(
                "Account.getAccountSurveyDto", queryParameters);

        if (queryResult.size() == 0) {
            return null;
        }

        List<SurveyDto> accountSurveys = new ArrayList<SurveyDto>();
        Integer instanceId;
        String surveyName;
        Date dateConducted;

        for (Object[] accountSurvey : queryResult) {
            instanceId = (Integer) accountSurvey[0];
            surveyName = (String) accountSurvey[1];
            dateConducted = (Date) accountSurvey[2];

            accountSurveys.add(new SurveyDto(instanceId, surveyName, dateConducted));
        }
        return accountSurveys;
    }

    @Override
    public void save(LoanBO loanAccount) {
        this.genericDao.createOrUpdate(loanAccount);
    }

    @Override
	@SuppressWarnings("unchecked")
    public List<LoanBO> findIndividualLoans(final Integer accountId) {

        List<LoanBO> individualLoans = new ArrayList<LoanBO>();

        Map<String, Integer> queryParameters = new HashMap<String, Integer>();
        queryParameters.put(LoanConstants.LOANACCOUNTID, accountId);
        List<LoanBO> queryResult = (List<LoanBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.FIND_INDIVIDUAL_LOANS, queryParameters);
        if (queryResult != null) {
            individualLoans.addAll(queryResult);
        }

        return individualLoans;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LoanBO> findAllLoansWaitingForApproval() {
        List<LoanBO> inapprovedLoansList = new ArrayList<LoanBO>();
        Map<String, Integer> queryParameters = new HashMap<String, Integer>();
        List<LoanBO> queryResult = (List<LoanBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_WAITING_FOR_APPROVAL_LOANS, queryParameters);
        if (queryResult != null) {
            inapprovedLoansList.addAll(queryResult);
        }
        return inapprovedLoansList;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LoanBO> findAllBadStandingLoans() {
        List<LoanBO> badStandingLoans = new ArrayList<LoanBO>();
        Map<String, Integer> queryParameters = new HashMap<String, Integer>();
        List<LoanBO> queryResult = (List<LoanBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_BAD_STANDING_LOANS, queryParameters);
        if (queryResult != null) {
            badStandingLoans.addAll(queryResult);
        }
        return badStandingLoans;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<LoanBO> findLoansToBePaidCurrentWeek() {
        List<LoanBO> loans = new ArrayList<LoanBO>();
        Map<String, String> queryParameters = new HashMap<String, String>();
        LocalDate lDate = new LocalDate();
        queryParameters.put("START_DATE", lDate.toString("yyyy-MM-dd"));
        lDate=lDate.plusWeeks(1);
        queryParameters.put("END_DATE",lDate.toString("yyyy-MM-dd"));
        List<LoanBO> queryResult = (List<LoanBO>) this.genericDao.executeNamedQuery(NamedQueryConstants.GET_ALL_LOANS_TO_BE_PAID_CURRENT_WEEK, queryParameters);
        if (queryResult != null) {
            loans.addAll(queryResult);
        }
        return loans;
    }
}