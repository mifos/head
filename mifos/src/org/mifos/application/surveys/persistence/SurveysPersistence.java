package org.mifos.application.surveys.persistence;

import java.util.List;

import org.hibernate.Query;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.helpers.SurveyState;
import org.mifos.application.surveys.helpers.SurveyType;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.SessionHolder;
import org.mifos.framework.persistence.SessionPersistence;

public class SurveysPersistence extends SessionPersistence {
	
	public SurveysPersistence(SessionHolder sessionHolder) {
		super(sessionHolder);
	}
	
	public SurveysPersistence() {
		super();
	}
	
	public List<Survey> retrieveAllSurveys() throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_ALL);
		return query.list();
	}
	
	public List<Survey> retrieveByType(SurveyType type) throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_TYPE);
		query.setParameter("SURVEY_TYPE", type.getValue());
		return query.list();
	}
	
	public List<Survey> retrieveByName(String name) throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_TYPE);
		query.setParameter("SURVEY_NAME", name);
		return query.list();
	}
	
	public List<Survey> retrieveByStatus(SurveyState state) throws PersistenceException {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_BY_STATUS);
		query.setParameter("SURVEY_STATUS", state.getValue());
		return query.list();
	}
	
	public Survey getSurvey(int id) {
		return (Survey) getSession().get(Survey.class, id);
	}

}
