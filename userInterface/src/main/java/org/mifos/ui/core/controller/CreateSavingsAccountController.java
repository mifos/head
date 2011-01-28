/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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
package org.mifos.ui.core.controller;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CreateSavingsAccountController {

	@Autowired
	private SavingsServiceFacade savingsServiceFacade;

	public CreateSavingsAccountController() {
	}

	public SavingsProductReferenceDto getProduct(Integer productId) {
		SavingsProductReferenceDto product = savingsServiceFacade
				.retrieveSavingsProductReferenceData(productId);
		return product;
	}

	public List<PrdOfferingDto> getProductOfferings(Integer customerId) {
		List<PrdOfferingDto> savingsProducts = savingsServiceFacade
				.retrieveApplicableSavingsProductsForCustomer(customerId);
		return savingsProducts;
	}

	public List<CustomerDto> searchCustomers(CustomerSearchFormBean formBean) {
		// TODO replace stub data
		List<CustomerDto> searchResults = new ArrayList<CustomerDto>();
		for (int i = 0; i < 50; i++) {
			Integer customerId = new Integer(i);
			String displayName = "Customer " + i;
			Integer parentCustomerId = new Integer(1);
			Short levelId = 1;
			CustomerDto customer = new CustomerDto(customerId, displayName,
					parentCustomerId, levelId);
			searchResults.add(customer);
		}
		return searchResults;
	}
}
