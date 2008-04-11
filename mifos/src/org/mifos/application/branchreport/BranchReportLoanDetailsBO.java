/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
package org.mifos.application.branchreport;

import static org.mifos.framework.util.helpers.MoneyUtils.getMoneyAmount;

import java.math.BigDecimal;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.util.helpers.Money;

public class BranchReportLoanDetailsBO extends BusinessObject {

	@SuppressWarnings("unused")
	private BranchReportBO branchReport;
	private Integer loanDetailsId;
	private String productName;
	private Integer numberOfLoansIssued;
	private Money loanAmountIssued;
	private Money loanInterestIssued;
	private Integer numberOfLoansOutstanding;
	private Money loanAmountOutstanding;
	private Money loanInterestOutstanding;

	protected BranchReportLoanDetailsBO() {
	}

	public BranchReportLoanDetailsBO(String productName,
			Integer numberOfLoansIssued, Money loanAmountIssued,
			Money loanInterestIssued, Integer numberOfLoansOutstanding,
			Money loanAmountOutstanding, Money loanInterestOutstanding) {
		super();
		this.productName = productName;
		this.numberOfLoansIssued = numberOfLoansIssued;
		this.loanAmountIssued = loanAmountIssued;
		this.loanInterestIssued = loanInterestIssued;
		this.numberOfLoansOutstanding = numberOfLoansOutstanding;
		this.loanAmountOutstanding = loanAmountOutstanding;
		this.loanInterestOutstanding = loanInterestOutstanding;
	}

	public void setBranchReport(BranchReportBO branchReportBO) {
		this.branchReport = branchReportBO;
	}
	
	public BigDecimal getLoanAmountIssued() {
		return getMoneyAmount(loanAmountIssued);
	}

	public BigDecimal getLoanAmountOutstanding() {
		return getMoneyAmount(loanAmountOutstanding);
	}

	public BigDecimal getLoanInterestIssued() {
		return getMoneyAmount(loanInterestIssued);
	}

	public BigDecimal getLoanInterestOutstanding() {
		return getMoneyAmount(loanInterestOutstanding);
	}

	public Integer getNumberOfLoansIssued() {
		return numberOfLoansIssued;
	}

	public Integer getNumberOfLoansOutstanding() {
		return numberOfLoansOutstanding;
	}

	public String getProductName() {
		return productName;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME
				* result
				+ ((loanAmountIssued == null) ? 0 : loanAmountIssued.hashCode());
		result = PRIME
				* result
				+ ((loanAmountOutstanding == null) ? 0 : loanAmountOutstanding
						.hashCode());
		result = PRIME * result
				+ ((loanDetailsId == null) ? 0 : loanDetailsId.hashCode());
		result = PRIME
				* result
				+ ((loanInterestIssued == null) ? 0 : loanInterestIssued
						.hashCode());
		result = PRIME
				* result
				+ ((loanInterestOutstanding == null) ? 0
						: loanInterestOutstanding.hashCode());
		result = PRIME
				* result
				+ ((numberOfLoansIssued == null) ? 0 : numberOfLoansIssued
						.hashCode());
		result = PRIME
				* result
				+ ((numberOfLoansOutstanding == null) ? 0
						: numberOfLoansOutstanding.hashCode());
		result = PRIME * result
				+ ((productName == null) ? 0 : productName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BranchReportLoanDetailsBO other = (BranchReportLoanDetailsBO) obj;
		if (loanAmountIssued == null) {
			if (other.loanAmountIssued != null)
				return false;
		}
		else if (!loanAmountIssued.equals(other.loanAmountIssued))
			return false;
		if (loanAmountOutstanding == null) {
			if (other.loanAmountOutstanding != null)
				return false;
		}
		else if (!loanAmountOutstanding.equals(other.loanAmountOutstanding))
			return false;
		if (loanDetailsId == null) {
			if (other.loanDetailsId != null)
				return false;
		}
		else if (!loanDetailsId.equals(other.loanDetailsId))
			return false;
		if (loanInterestIssued == null) {
			if (other.loanInterestIssued != null)
				return false;
		}
		else if (!loanInterestIssued.equals(other.loanInterestIssued))
			return false;
		if (loanInterestOutstanding == null) {
			if (other.loanInterestOutstanding != null)
				return false;
		}
		else if (!loanInterestOutstanding.equals(other.loanInterestOutstanding))
			return false;
		if (numberOfLoansIssued == null) {
			if (other.numberOfLoansIssued != null)
				return false;
		}
		else if (!numberOfLoansIssued.equals(other.numberOfLoansIssued))
			return false;
		if (numberOfLoansOutstanding == null) {
			if (other.numberOfLoansOutstanding != null)
				return false;
		}
		else if (!numberOfLoansOutstanding
				.equals(other.numberOfLoansOutstanding))
			return false;
		if (productName == null) {
			if (other.productName != null)
				return false;
		}
		else if (!productName.equals(other.productName))
			return false;
		return true;
	}
}
