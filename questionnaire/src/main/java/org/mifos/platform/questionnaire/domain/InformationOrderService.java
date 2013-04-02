package org.mifos.platform.questionnaire.domain;

import java.util.List;

import org.mifos.platform.questionnaire.service.InformationOrder;

public interface InformationOrderService {

	List<InformationOrder> getInformationOrder(String page);
	void updateInformationOrder(List<InformationOrder> order);
	void removeAdditionalQuestionIfExists(InformationOrder informationOrder);
	void addAdditionalQuestionIfNotExists(InformationOrder intformationOrder);
	
}
