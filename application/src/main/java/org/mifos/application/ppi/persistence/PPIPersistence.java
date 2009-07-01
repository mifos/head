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

package org.mifos.application.ppi.persistence;

import java.util.List;

import org.hibernate.Query;
import org.mifos.application.NamedQueryConstants;
import org.mifos.application.ppi.business.PPIChoice;
import org.mifos.application.ppi.business.PPISurvey;
import org.mifos.application.ppi.helpers.Country;
import org.mifos.application.surveys.business.QuestionChoice;
import org.mifos.application.surveys.business.Survey;
import org.mifos.application.surveys.business.SurveyInstance;
import org.mifos.application.surveys.business.SurveyResponse;
import org.mifos.application.surveys.helpers.AnswerType;
import org.mifos.application.surveys.persistence.SurveysPersistence;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;

public class PPIPersistence extends SurveysPersistence {

    public PPISurvey retrieveActivePPISurvey() {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_ACTIVE_PPI);
        List list = query.list();
        return list.size() > 0 ? (PPISurvey) list.get(0) : null;
    }

    public List<PPISurvey> retrieveAllPPISurveys() {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_ALL_PPI);
        return query.list();
    }

    public PPISurvey retrievePPISurveyByCountry(int country) {
        Query query = getSession().getNamedQuery(NamedQueryConstants.SURVEYS_RETRIEVE_PPI_BY_COUNTRY);
        query.setParameter("COUNTRY", country);
        List list = query.list();
        return list.size() > 0 ? (PPISurvey) list.get(0) : null;
    }

    public PPISurvey retrievePPISurveyByCountry(Country country) {
        return retrievePPISurveyByCountry(country.getValue());
    }

    /**
     * Returns the {@link PPISurvey} from persistence with the given id.
     * 
     * If a survey with that id does not exists, or if the survey is not a
     * {@link PPISurvey}, <code>null</code> is returned.
     */
    public PPISurvey getPPISurvey(int id) {
        Survey retrieved = (Survey) getSession().get(PPISurvey.class, id);
        return retrieved instanceof PPISurvey ? (PPISurvey) retrieved : null;
    }

    /**
     * Returns the {@link PPIChoice} from persistence with the given id.
     * 
     * If a QuestionChoice with that id does not exists, or if the
     * QuestionChoice is not a {@link PPIChoice}, <code>null</code> is returned.
     */
    public PPIChoice getPPIChoice(int id) {
        QuestionChoice retrieved = (QuestionChoice) getSession().get(PPIChoice.class, id);
        return retrieved instanceof PPIChoice ? (PPIChoice) retrieved : null;
    }

    @Override
    public List<SurveyResponse> retrieveResponsesByInstance(SurveyInstance instance) throws PersistenceException {
        List<SurveyResponse> list = super.retrieveResponsesByInstance(instance);
        for (SurveyResponse response : list) {
            if (response.getQuestion().getAnswerType() == AnswerType.CHOICE.getValue()) {
                PPIChoice ppiChoice = getPPIChoice(response.getChoiceValue().getChoiceId());
                if (ppiChoice != null)
                    try {
                        response.setChoiceValue(ppiChoice);
                    } catch (ApplicationException e) {
                        throw new PersistenceException(e);
                    }
            }
        }
        return list;
    }

}
