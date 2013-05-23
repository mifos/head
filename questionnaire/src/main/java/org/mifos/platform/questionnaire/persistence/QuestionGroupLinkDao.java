package org.mifos.platform.questionnaire.persistence;

import java.util.List;

import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.platform.persistence.GenericDao;
import org.mifos.platform.questionnaire.domain.QuestionGroupLink;

public interface QuestionGroupLinkDao extends GenericDao<QuestionGroupLink, Integer> {
    List<LookUpValueEntity> retrieveAllConditions(String entityName);

}
