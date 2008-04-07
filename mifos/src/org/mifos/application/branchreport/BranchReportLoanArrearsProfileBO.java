/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005
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

public class BranchReportLoanArrearsProfileBO extends BusinessObject {

	@SuppressWarnings("unused")
	private BranchReportBO branchReport;
	@SuppressWarnings("unused")
	private Integer loanArrearsProfileId;
	private Integer loansInArrears;
	private Integer clientsInArrears;
	private Money overDueBalance;
	private Money unpaidBalance;
	private Integer loansAtRisk;
	private Money outstandingAmountAtRisk;
	private Money overdueAmountAtRisk;
	private Integer clientsAtRisk;

	protected BranchReportLoanArrearsProfileBO() {
		super();
	}

	public BranchReportLoanArrearsProfileBO(Integer loansInArrears,
			Integer clientsInArrears, Money overDueBalanceInArrears,
			Money unpaidBalanceInArrears, Integer loansAtRisk,
			Money outstandingAmountAtRisk, Money overdueAmountAtRisk, Integer clientsAtRisk) {
		this.loansInArrears = loansInArrears;
		this.clientsInArrears = clientsInArrears;
		this.overDueBalance = overDueBalanceInArrears;
		this.unpaidBalance = unpaidBalanceInArrears;
		this.loansAtRisk = loansAtRisk;
		this.outstandingAmountAtRisk = outstandingAmountAtRisk;
		this.overdueAmountAtRisk = overdueAmountAtRisk;
		this.clientsAtRisk = clientsAtRisk;
	}

	public void setBranchReport(BranchReportBO branchReport) {
		this.branchReport = branchReport;
	}

	public Integer getClientsAtRisk() {
		return clientsAtRisk;
	}

	public Integer getClientsInArrears() {
		return clientsInArrears;
	}

	public Integer getLoansAtRisk() {
		return loansAtRisk;
	}

	public Integer getLoansInArrears() {
		return loansInArrears;
	}

	public BigDecimal getOutstandingAmountAtRisk() {
		return getMoneyAmount(outstandingAmountAtRisk);
	}

	public BigDecimal getOverdueAmountAtRisk() {
		return getMoneyAmount(overdueAmountAtRisk);
	}
	
	public BigDecimal getOverDueBalance() {
		return getMoneyAmount(overDueBalance);
	}

	public BigDecimal getUnpaidBalance() {
		return getMoneyAmount(unpaidBalance);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((clientsAtRisk == null) ? 0 : clientsAtRisk.hashCode());
		result = PRIME * result + ((clientsInArrears == null) ? 0 : clientsInArrears.hashCode());
		result = PRIME * result + ((loanArrearsProfileId == null) ? 0 : loanArrearsProfileId.hashCode());
		result = PRIME * result + ((loansAtRisk == null) ? 0 : loansAtRisk.hashCode());
		result = PRIME * result + ((loansInArrears == null) ? 0 : loansInArrears.hashCode());
		result = PRIME * result + ((outstandingAmountAtRisk == null) ? 0 : outstandingAmountAtRisk.hashCode());
		result = PRIME * result + ((overDueBalance == null) ? 0 : overDueBalance.hashCode());
		result = PRIME * result + ((overdueAmountAtRisk == null) ? 0 : overdueAmountAtRisk.hashCode());
		result = PRIME * result + ((unpaidBalance == null) ? 0 : unpaidBalance.hashCode());
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
		final BranchReportLoanArrearsProfileBO other = (BranchReportLoanArrearsProfileBO) obj;
		if (clientsAtRisk == null) {
			if (other.clientsAtRisk != null)
				return false;
		}
		else if (!clientsAtRisk.equals(other.clientsAtRisk))
			return false;
		if (clientsInArrears == null) {
			if (other.clientsInArrears != null)
				return false;
		}
		else if (!clientsInArrears.equals(other.clientsInArrears))
			return false;
		if (loanArrearsProfileId == null) {
			if (other.loanArrearsProfileId != null)
				return false;
		}
		else if (!loanArrearsProfileId.equals(other.loanArrearsProfileId))
			return false;
		if (loansAtRisk == null) {
			if (other.loansAtRisk != null)
				return false;
		}
		else if (!loansAtRisk.equals(other.loansAtRisk))
			return false;
		if (loansInArrears == null) {
			if (other.loansInArrears != null)
				return false;
		}
		else if (!loansInArrears.equals(other.loansInArrears))
			return false;
		if (outstandingAmountAtRisk == null) {
			if (other.outstandingAmountAtRisk != null)
				return false;
		}
		else if (!outstandingAmountAtRisk.equals(other.outstandingAmountAtRisk))
			return false;
		if (overDueBalance == null) {
			if (other.overDueBalance != null)
				return false;
		}
		else if (!overDueBalance.equals(other.overDueBalance))
			return false;
		if (overdueAmountAtRisk == null) {
			if (other.overdueAmountAtRisk != null)
				return false;
		}
		else if (!overdueAmountAtRisk.equals(other.overdueAmountAtRisk))
			return false;
		if (unpaidBalance == null) {
			if (other.unpaidBalance != null)
				return false;
		}
		else if (!unpaidBalance.equals(other.unpaidBalance))
			return false;
		return true;
	}
}
