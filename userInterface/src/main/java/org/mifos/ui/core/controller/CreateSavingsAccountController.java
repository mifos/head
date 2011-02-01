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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.dto.domain.CustomFieldDto;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.domain.PrdOfferingDto;
import org.mifos.dto.domain.SavingsAccountCreationDto;
import org.mifos.dto.domain.SavingsAccountDetailDto;
import org.mifos.dto.domain.SavingsProductDto;
import org.mifos.dto.screen.CustomerSearchResultsDto;
import org.mifos.dto.screen.SavingsProductReferenceDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class CreateSavingsAccountController {

	@Autowired
	private SavingsServiceFacade savingsServiceFacade;

	public CreateSavingsAccountController() {
	}

	public SavingsAccountDetailDto createAccountInPartialApplicationState(
			CreateSavingsAccountFormBean formBean) {
		Short accountState = 13; // TOOD grab state from constant. NOT from
		return createAccount(formBean, accountState);
	}

	public SavingsAccountDetailDto createAccountInPendingApprovalState(
			CreateSavingsAccountFormBean formBean) {
		Short accountState = 14; // TOOD grab state from constant. NOT from
		return createAccount(formBean, accountState);
	}

	private SavingsAccountDetailDto createAccount(
			CreateSavingsAccountFormBean formBean, Short accountState) {
		SavingsProductReferenceDto productReference = formBean.getProduct();
		SavingsProductDto savingsProduct = productReference
				.getSavingsProductDetails();
		Integer productId = savingsProduct.getProductDetails().getId();
		Integer customerId = formBean.getCustomer().getCustomerId();
		String recommendedOrMandatoryAmount = formBean.getDepositAmount()
				.toString();
		List<CustomFieldDto> customFields = new ArrayList<CustomFieldDto>(); // TODO
		SavingsAccountCreationDto savingsAccountCreation = new SavingsAccountCreationDto(
				productId, customerId, accountState,
				recommendedOrMandatoryAmount, customFields);
		Long savingsId = savingsServiceFacade
				.createSavingsAccount(savingsAccountCreation);
		SavingsAccountDetailDto savingsAccountDetailDto = savingsServiceFacade
				.retrieveSavingsAccountDetails(savingsId);
		return savingsAccountDetailDto;
	}

	public void customerSelected(Integer customerId,
			CreateSavingsAccountFormBean formBean) {
		CustomerDto customer = new CustomerDto(); // TODO use service facade to
													// load customer
		customer.setCustomerId(customerId);
		customer.setDisplayName("FIXME - CreateSavingsAccountController");
		formBean.setCustomer(customer);
	}

	public void loadProduct(Integer productId,
			CreateSavingsAccountFormBean formBean) {

		SavingsProductReferenceDto product = savingsServiceFacade
				.retrieveSavingsProductReferenceData(productId);
		formBean.setProduct(product);
		formBean.setDepositAmount(product.getSavingsProductDetails()
				.getAmountForDeposit());
		formBean.setSavingsTypes(getSavingsTypes());
		formBean.setRecurrenceTypes(getRecurrenceTypes());
		formBean.setRecurrenceFrequencies(getRecurrenceFrequencies());
	}

	public void getProductOfferings(CreateSavingsAccountFormBean formBean) {
		List<PrdOfferingDto> savingsProducts = savingsServiceFacade
				.retrieveApplicableSavingsProductsForCustomer(formBean
						.getCustomer().getCustomerId());
		formBean.setProductOfferings(savingsProducts);
	}

	public CustomerSearchResultsDto searchCustomers(
			CreateSavingsAccountFormBean formBean) {
		// TODO replace stub data
		// CustomerSearchDto searchDto = new
		// CustomerSearchDto(formBean.getSearchString(), 1, 10);
		// someFacade.searchCustomers(searchDto)...
		List<CustomerDto> pagedDetails = new ArrayList<CustomerDto>();
		for (int i = 0; i < 50; i++) {
			Integer customerId = new Integer(i);
			String displayName = formBean.getSearchString() + " - " + i;
			Integer parentCustomerId = new Integer(1);
			Short levelId = 1;
			CustomerDto customer = new CustomerDto(customerId, displayName,
					parentCustomerId, levelId);
			pagedDetails.add(customer);
		}
		CustomerSearchResultsDto resultsDto = new CustomerSearchResultsDto(
				pagedDetails.size(), 1, 100, 100, pagedDetails);
		return resultsDto;
	}

	/**
	 * @see org.mifos.application.meeting.util.helpers.RecurrenceType
	 * @return A map of recurrence type ID to message key which points to a
	 *         localized name.
	 */
	private Map<String, String> getRecurrenceFrequencies() {
		Map<String, String> map = new HashMap<String, String>(3);
		map.put("2", "createSavingsAccount.recurrenceFrequency.month"); // monthly
		map.put("3", "createSavingsAccount.recurrenceFrequency.day"); // daily
		return map;
	}

	/**
	 * @see org.mifos.application.meeting.util.helpers.RecurrenceType
	 * @return A map of recurrence type ID to message key which points to a
	 *         localized name.
	 */
	private Map<String, String> getRecurrenceTypes() {
		Map<String, String> map = new HashMap<String, String>(3);
		map.put("1", "createSavingsAccount.recurrenceType.weekly"); // weekly
		map.put("2", "createSavingsAccount.recurrenceType.monthly"); // monthly
		map.put("3", "createSavingsAccount.recurrenceType.daily"); // daily
		return map;
	}

	/**
	 * @see org.mifos.accounts.productdefinition.util.helpers.SavingsType
	 * @return A map of savings type ID to message key which points to a
	 *         localized name.
	 */
	private Map<String, String> getSavingsTypes() {
		Map<String, String> map = new HashMap<String, String>(2);
		map.put("1", "createSavingsAccount.savingsType.mandatory"); // mandatory
		map.put("2", "createSavingsAccount.savingsType.voluntary"); // voluntary
		return map;
	}
}
