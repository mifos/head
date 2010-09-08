package org.mifos.accounts.loan.persistance;

import org.mifos.accounts.business.AccountCustomFieldEntity;
import org.mifos.accounts.loan.business.LoanBO;
import org.mifos.accounts.savings.persistence.GenericDao;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.util.helpers.SurveyDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public final List<CustomFieldDefinitionEntity> retrieveCustomFieldEntitiesForLoan() {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put(MasterConstants.ENTITY_TYPE, EntityType.LOAN.getValue());
        return retrieveCustomFieldDefinitions(queryParameters);
    }

    @Override
    public List<AccountCustomFieldEntity> getCustomFieldResponses(Integer customFieldId) {
        Map<String, Object> queryParameters = new HashMap<String, Object>();
        queryParameters.put("CUSTOM_FIELD_ID", customFieldId);
        return (List<AccountCustomFieldEntity>) genericDao.executeNamedQuery("AccountCustomFieldEntity.getResponses", queryParameters);
    }

    @SuppressWarnings("unchecked")
    private List<CustomFieldDefinitionEntity> retrieveCustomFieldDefinitions(Map<String, Object> queryParameters) {
        List<CustomFieldDefinitionEntity> customFieldsForCenter = (List<CustomFieldDefinitionEntity>) genericDao
                .executeNamedQuery(NamedQueryConstants.RETRIEVE_CUSTOM_FIELDS, queryParameters);
        return customFieldsForCenter;
    }

}