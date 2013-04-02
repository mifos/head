/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
 *  All rights reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  permissions and limitations under the License.
 *
 *  See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 *  explanation of the license and how it is applied.
 */

package org.mifos.platform.questionnaire.service;

import java.util.List;

import org.mifos.platform.questionnaire.domain.InformationOrderService;
import org.springframework.beans.factory.annotation.Autowired;

public class InformationOrderServiceFacadeImpl implements InformationOrderServiceFacade {

    @Autowired
    private InformationOrderService informationOrderService;

	@Override
	public List<InformationOrder> getInformationOrder(String page) {
		return informationOrderService.getInformationOrder(page);
	}

	@Override
	public void updateInformationOrder(List<InformationOrder> order) {
		informationOrderService.updateInformationOrder(order);
	}

	@Override
	public void removeAdditionalQuestionIfExists(InformationOrder informationOrder) {
		informationOrderService.removeAdditionalQuestionIfExists(informationOrder);
	}

	@Override
	public void addAdditionalQuestionIfNotExists(
			InformationOrder intformationOrder) {
		informationOrderService.addAdditionalQuestionIfNotExists(intformationOrder);
	}

}
