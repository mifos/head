package org.mifos.application.surveys.persistence;

import java.util.List;

import org.hibernate.Query;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.surveys.business.Question;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.helpers.QuestionState;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.persistence.Persistence;

public class SurveysPersistence extends Persistence {
	
	public List<Survey> retrieveAllSurveys() throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_ALL);
		return query.list();
	}
	
	public List<SurveyResponse> retrieveAllResponses() throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.RESPONSES_RETRIEVE_ALL);
		return query.list();
	}
	
	public List<SurveyResponse> retrieveResponsesByInstance(SurveyInstance instance) throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.RESPONSES_RETRIEVE_BY_INSTANCE);
		query.setParameter("INSTANCE", instance);
		return query.list();
	}
	
	public List<Survey> retrieveSurveysByType(SurveyType type) throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_TYPE);
		query.setParameter("SURVEY_TYPE", type.getValue());
		return query.list();
	}
	
	public List<Survey> retrieveCustomersSurveys() throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_CUSTOMERS_TYPES);
		return query.list();		
	}
	
	public List<Survey> retrieveAccountsSurveys() throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_ACCOUNTS_TYPES);
		return query.list();		
	}	
	
	public List<Survey> retrieveSurveysByName(String name) throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_TYPE);
		query.setParameter("SURVEY_NAME", name);
		return query.list();
	}
	
	public List<Survey> retrieveSurveysByStatus(SurveyState state) throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_STATUS);
		query.setParameter("SURVEY_STATUS", state.getValue());
		return query.list();
	}
	
	public Survey getSurvey(int id) {
		return (Survey) getSession().get(Survey.class, id);
	}
	
	public SurveyInstance getInstance(int id) {
		return (SurveyInstance) getSession().get(SurveyInstance.class, id);
	}
	
	public int getNumQuestions() {
		Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_GET_NUM);
		return Integer.parseInt(query.list().get(0).toString());
	}
	
    public List<Question> retrieveAllQuestions() throws PersistenceException {
        Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_RETRIEVE_ALL);
        return query.list();
    }
    
    public List<Question> retrieveSomeQuestions(int offset, int limit) throws PersistenceException {
    	Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_RETRIEVE_ALL);
    	query.setFirstResult(offset);
    	query.setMaxResults(limit);
    	return query.list();
    }
    
    public List<Question> retrieveQuestionsByState(QuestionState state) throws PersistenceException {
    	Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_RETRIEVE_BY_STATE);
    	query.setParameter("QUESTION_STATE", state.getValue());
    	return query.list();
    }

    
    public List<Question> retrieveQuestionsByAnswerType(AnswerType type) throws PersistenceException {
    	Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_RETRIEVE_BY_TYPE);
    	query.setParameter("ANSWER_TYPE", type.getValue());
    	return query.list();
    }
    
    public Question getQuestion(int id) {
        return (Question) getSession().get(Question.class, id);
    }
    
    public List<Question> retrieveQuestionsByName(String name) {
    	Query query = getSession().getNamedQuery(NamedQueryConstants.QUESTIONS_RETRIEVE_BY_NAME);
    	query.setParameter("SHORT_NAME", name);
    	return query.list();
    }
    
    public List<SurveyInstance> retrieveInstancesByCustomer(CustomerBO customer) throws PersistenceException {
    	Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYINSTANCE_RETRIEVE_BY_CUSTOMER);
    	query.setParameter("INSTANCE_CUSTOMER", customer);
    	return query.list();
    }
    
    public List<SurveyInstance> retrieveInstancesByAccount(AccountBO account) throws PersistenceException {
    	Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYINSTANCE_RETRIEVE_BY_ACCOUNT);
    	query.setParameter("INSTANCE_ACCOUNT", account);
    	return query.list();
    }
    
    public List<SurveyInstance> retrieveInstancesBySurvey(Survey survey) throws PersistenceException {
    	Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYINSTANCE_RETRIEVE_BY_SURVEY);
    	query.setParameter("INSTANCE_SURVEY", survey);
    	return query.list();
    }

}
