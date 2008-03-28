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

import static org.mifos.framework.util.helpers.MoneyFactory.ZERO;

import java.math.BigDecimal;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.MoneyUtils;
import org.mifos.framework.util.helpers.NumberUtils;

public class BranchReportLoanArrearsAgingBO extends BusinessObject {

	private Integer arrearsAgingId;
	@SuppressWarnings("unused")
	private BranchReportBO branchReport;
	private LoanArrearsAgingPeriod agingPeriod;
	private Integer clientsAging;
	private Integer loansAging;
	private Money amountAging;
	private Money amountOutstandingAging;

	public BranchReportLoanArrearsAgingBO() {
		super();
	}

	public BranchReportLoanArrearsAgingBO(LoanArrearsAgingPeriod agingPeriod,
			Integer clientsAging, Integer loansAging, Money amountAging,
			Money amountOutstandingAging) {
		super();
		this.agingPeriod = agingPeriod;
		this.clientsAging = clientsAging;
		this.loansAging = loansAging;
		this.amountAging = amountAging;
		this.amountOutstandingAging = amountOutstandingAging;
	}

	public BranchReportLoanArrearsAgingBO(
			LoanArrearsAgingPeriod loanArrearsAgingPeriod) {
		this(loanArrearsAgingPeriod, NumberUtils.ZERO, NumberUtils.ZERO, ZERO,
				ZERO);
	}

	public String getPeriodDescription() {
		return agingPeriod.getDescription();
	}

	public LoanArrearsAgingPeriod getAgingPeriod() {
		return agingPeriod;
	}

	public BigDecimal getAmountAging() {
		return MoneyUtils.getMoneyAmount(amountAging);
	}

	public BigDecimal getAmountOutstandingAging() {
		return MoneyUtils.getMoneyAmount(amountOutstandingAging);
	}

	public Integer getClientsAging() {
		return clientsAging;
	}

	public Integer getLoansAging() {
		return loansAging;
	}

	public void setBranchReport(BranchReportBO branchReport) {
		this.branchReport = branchReport;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((agingPeriod == null) ? 0 : agingPeriod.hashCode());
		result = PRIME * result
				+ ((amountAging == null) ? 0 : amountAging.hashCode());
		result = PRIME
				* result
				+ ((amountOutstandingAging == null) ? 0
						: amountOutstandingAging.hashCode());
		result = PRIME * result
				+ ((arrearsAgingId == null) ? 0 : arrearsAgingId.hashCode());
		result = PRIME * result
				+ ((clientsAging == null) ? 0 : clientsAging.hashCode());
		result = PRIME * result
				+ ((loansAging == null) ? 0 : loansAging.hashCode());
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
		final BranchReportLoanArrearsAgingBO other = (BranchReportLoanArrearsAgingBO) obj;
		if (agingPeriod == null) {
			if (other.agingPeriod != null)
				return false;
		}
		else if (!agingPeriod.equals(other.agingPeriod))
			return false;
		if (amountAging == null) {
			if (other.amountAging != null)
				return false;
		}
		else if (!amountAging.equals(other.amountAging))
			return false;
		if (amountOutstandingAging == null) {
			if (other.amountOutstandingAging != null)
				return false;
		}
		else if (!amountOutstandingAging.equals(other.amountOutstandingAging))
			return false;
		if (arrearsAgingId == null) {
			if (other.arrearsAgingId != null)
				return false;
		}
		else if (!arrearsAgingId.equals(other.arrearsAgingId))
			return false;
		if (clientsAging == null) {
			if (other.clientsAging != null)
				return false;
		}
		else if (!clientsAging.equals(other.clientsAging))
			return false;
		if (loansAging == null) {
			if (other.loansAging != null)
				return false;
		}
		else if (!loansAging.equals(other.loansAging))
			return false;
		return true;
	}

	public static BranchReportLoanArrearsAgingBO createInstanceForTest(
			LoanArrearsAgingPeriod agingPeriod) {
		BranchReportLoanArrearsAgingBO loanArrearsAgingBO = new BranchReportLoanArrearsAgingBO();
		loanArrearsAgingBO.agingPeriod = agingPeriod;
		return loanArrearsAgingBO;
	}

	public Integer getArrearsAgingId() {
		return arrearsAgingId;
	}

	@Override
	public String toString() {
		return "BRLAABO:["
				+ getClass().getName()
				+ " branchReportId:"
				//		+branchReport.getBranchReportId()
				+ " arrearsAgingId: " + arrearsAgingId + "agingPeriod: "
				+ agingPeriod.getDescription() + " clientsAging: "
				+ clientsAging + " loansAging: " + loansAging
				+ " amountAging: " + amountAging + " amountOutstandignAging: "
				+ amountOutstandingAging + "]";
	}
}
