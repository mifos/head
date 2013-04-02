package org.mifos.platform.questionnaire.domain;

import java.util.List;

import org.mifos.platform.questionnaire.persistence.InformationOrderDao;
import org.mifos.platform.questionnaire.service.InformationOrder;
import org.springframework.beans.factory.annotation.Autowired;

public class InformationOrderServiceImpl implements InformationOrderService {

	@Autowired
	private InformationOrderDao informationOrderDao;

	@Override
	public List<InformationOrder> getInformationOrder(String page) {
		return informationOrderDao.retrieveByPage(page);
	}

	@Override
	public void updateInformationOrder(List<InformationOrder> informationOrder) {
		informationOrderDao.saveOrUpdateAll(informationOrder);
	}
	
	@Override
	public void removeAdditionalQuestionIfExists(InformationOrder informationOrder) {
		List<InformationOrder> infOrder = 
				informationOrderDao.retrieveByAdditionalQuestionIdAndPage(informationOrder.getAdditionalQuestionId(), informationOrder.getPage());
		if (infOrder != null) {
			for (InformationOrder order: infOrder) {
				informationOrderDao.delete(order);
			}
		}
	}

	@Override
	public void addAdditionalQuestionIfNotExists(InformationOrder informationOrder) {
		List<InformationOrder> infOrder = 
				informationOrderDao.retrieveByAdditionalQuestionIdAndPage(informationOrder.getAdditionalQuestionId(), informationOrder.getPage());
		if (infOrder == null || infOrder.size() < 1) {
			informationOrderDao.add(informationOrder);
		}
	}
	
}
