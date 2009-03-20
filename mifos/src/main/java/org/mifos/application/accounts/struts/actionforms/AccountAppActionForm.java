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
 
package org.mifos.application.accounts.struts.actionforms;

import java.util.ArrayList;
import java.util.List;

import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.master.business.CustomFieldView;

public class AccountAppActionForm extends ValidatorActionForm {
	private String accountId;

	private String customerId;

	private String selectedPrdOfferingId;

	private String stateSelected;

	private String globalAccountNum;

	private String input;

	private List<CustomFieldView> accountCustomFieldSet;

	// values for moving back
	private String searchInput;

	private String prdOfferingName;

	private String headingInput;

	public String getHeadingInput() {
		return headingInput;
	}

	public void setHeadingInput(String headingInput) {
		this.headingInput = headingInput;
	}

	public String getPrdOfferingName() {
		return prdOfferingName;
	}

	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

	public String getSearchInput() {
		return searchInput;
	}

	public void setSearchInput(String searchInput) {
		this.searchInput = searchInput;
	}

	public AccountAppActionForm() {
		accountCustomFieldSet = new ArrayList<CustomFieldView>();
	}

	public String getStateSelected() {
		return stateSelected;
	}

	public void setStateSelected(String stateSelected) {
		this.stateSelected = stateSelected;
	}

	public String getSelectedPrdOfferingId() {
		return selectedPrdOfferingId;
	}

	public void setSelectedPrdOfferingId(String selectedPrdOfferingId) {
		this.selectedPrdOfferingId = selectedPrdOfferingId;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public List<CustomFieldView> getAccountCustomFieldSet() {
		return this.accountCustomFieldSet;
	}

	public void setAccountCustomFieldSet(
			List<CustomFieldView> accountCustomFieldSet) {
		this.accountCustomFieldSet = accountCustomFieldSet;
	}

	public String getGlobalAccountNum() {
		return globalAccountNum;
	}

	public void setGlobalAccountNum(String globalAccountNumber) {
		this.globalAccountNum = globalAccountNumber;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String getAccountId() {
		return accountId;
	}

	public void setAccountId(String accountId) {
		this.accountId = accountId;
	}

	/**
	 * Return the value of the one of the custom field at index i in
	 * accountCustomFields List.
	 */
	public CustomFieldView getCustomField(int i) {
		while (i >= accountCustomFieldSet.size()) {
			accountCustomFieldSet.add(new CustomFieldView());
		}
		return (accountCustomFieldSet.get(i));
	}

}
