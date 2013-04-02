package org.mifos.platform.questionnaire.persistence;

import java.util.List;

import org.mifos.platform.persistence.GenericDao;
import org.mifos.platform.questionnaire.service.InformationOrder;

public interface InformationOrderDao extends GenericDao<InformationOrder, Integer> {
	List<InformationOrder> retrieveByPage(String page);
	List<InformationOrder> retrieveByAdditionalQuestionIdAndPage(Integer additionalQuestionId, String page);
}
