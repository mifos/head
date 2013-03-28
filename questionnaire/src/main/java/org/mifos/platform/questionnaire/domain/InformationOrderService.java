package org.mifos.platform.questionnaire.domain;

import java.util.List;

public interface InformationOrderService {

	List<InformationOrder> getInformationOrder(String page);
	void updateInformationOrder(List<InformationOrder> order);
	void removeAdditionalQuestionIfExists(InformationOrder informationOrder);
	void addAdditionalQuestionIfNotExists(InformationOrder intformationOrder);
	
}
