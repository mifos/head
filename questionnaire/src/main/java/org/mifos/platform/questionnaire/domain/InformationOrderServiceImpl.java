package org.mifos.platform.questionnaire.domain;

import java.util.List;

import org.mifos.platform.questionnaire.persistence.InformationOrderDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public class InformationOrderServiceImpl implements InformationOrderService {

	@Autowired
	private InformationOrderDao informationOrderDao;

	@Override
	public List<InformationOrder> getInformationOrder(String page) {
		return informationOrderDao.retrieveByPage(page);
	}

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void updateInformationOrder(List<InformationOrder> informationOrder) {
		informationOrderDao.saveOrUpdateAll(informationOrder);
	}
	
	@Transactional(propagation=Propagation.REQUIRED)
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

	@Transactional(propagation=Propagation.REQUIRED)
	@Override
	public void addAdditionalQuestionIfNotExists(InformationOrder informationOrder) {
		List<InformationOrder> infOrder = 
				informationOrderDao.retrieveByAdditionalQuestionIdAndPage(informationOrder.getAdditionalQuestionId(), informationOrder.getPage());
		if (infOrder == null || infOrder.size() < 1) {
			informationOrderDao.add(informationOrder);
		}
	}
	
}
