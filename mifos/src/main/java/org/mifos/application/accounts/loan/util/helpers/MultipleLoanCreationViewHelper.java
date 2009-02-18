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
package org.mifos.application.accounts.loan.util.helpers;

import static org.mifos.framework.util.helpers.FormUtils.getDoubleValue;
import static org.mifos.framework.util.helpers.NumberUtils.*;

import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.productdefinition.business.LoanAmountOption;
import org.mifos.application.productdefinition.business.LoanOfferingInstallmentRange;
import org.mifos.framework.util.helpers.StringUtils;

public class MultipleLoanCreationViewHelper {

	private String loanAmount;

	private String businessActivity;

	private LoanAmountOption loanAmountOption;
	
	private LoanOfferingInstallmentRange installmentOption;

	private ClientBO client;

	private String selected;

	public MultipleLoanCreationViewHelper(ClientBO client,
			LoanAmountOption loanAmountOption, LoanOfferingInstallmentRange installmentOption) {
		super();
		this.client = client;
		this.loanAmountOption = loanAmountOption;
		this.installmentOption = installmentOption;
		this.loanAmount = getDefaultLoanAmount().toString();
	}

	public MultipleLoanCreationViewHelper() {
		this(null, null, null);
	}

	public String getBusinessActivity() {
		return businessActivity;
	}

	public void setBusinessActivity(String businessActivity) {
		this.businessActivity = businessActivity;
	}

	public Integer getClientId() {
		return client.getCustomerId();
	}

	public String getLoanAmount() {
		return loanAmount;
	}

	public void setLoanAmount(String loanAmount) {
		this.loanAmount = loanAmount;
	}

	public String getClientName() {
		return client.getDisplayName();
	}

	public boolean isLoanAmountInRange() {
		return !StringUtils.isNullOrEmpty(this.loanAmount)
				&& loanAmountOption.isInRange(
						getDoubleValue(this.loanAmount));
	}

	public Double getMinLoanAmount() {
		return loanAmountOption == null ? DOUBLE_ZERO : loanAmountOption
				.getMinLoanAmount();
	}

	public Double getMaxLoanAmount() {
		return loanAmountOption == null ? DOUBLE_ZERO : loanAmountOption
				.getMaxLoanAmount();
	}
	
	public Double getDefaultLoanAmount() {
		return loanAmountOption == null ? DOUBLE_ZERO : loanAmountOption
				.getDefaultLoanAmount();
	}

	public Short getDefaultNoOfInstall() {
		return installmentOption == null ? SHORT_ZERO : installmentOption
				.getDefaultNoOfInstall();
	}

	public Short getMaxNoOfInstall() {
		return installmentOption == null ? SHORT_ZERO : installmentOption
				.getMaxNoOfInstall();
	}

	public Short getMinNoOfInstall() {
		return installmentOption == null ? SHORT_ZERO : installmentOption
				.getMinNoOfInstall();
	}

	public boolean isApplicable() {
		return Boolean.valueOf(selected);
	}

	public String getSelected() {
		return selected;
	}

	public void setSelected(String selected) {
		this.selected = selected;
	}

	public ClientBO getClient() {
		return client;
	}

	public void resetSelected() {
		this.selected = Boolean.FALSE.toString();
	}
}
