package org.mifos.application.ppi.persistence;

import java.util.List;

import org.hibernate.Query;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.surveys.persistence.SurveysPersistence;

//import org.mifos.framework.persistence.Persistence;

public class PPIPersistence extends SurveysPersistence {
	
	public PPISurvey retrieveActivePPISurvey() {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_ACTIVE_PPI);
		if (query.list().size() > 0)
			return (PPISurvey) query.list().get(0);
		return null;
	}
	
	public List<PPISurvey> retrieveAllPPISurveys() {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_ALL_PPI);
		return query.list();
	}
	
	public PPISurvey retrievePPISurveyByCountry(int country) {
		Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_PPI_BY_COUNTRY);
		query.setParameter("COUNTRY", country);
		if (query.list().size() > 0)
			return (PPISurvey) query.list().get(0);
		return null;
	}
	
	public PPISurvey retrievePPISurveyByCountry(Country country) {
		return retrievePPISurveyByCountry(country.getValue());
	}

}
