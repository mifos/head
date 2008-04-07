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

import static org.mifos.application.reports.util.helpers.ReportUtils.toDisplayDate;
import static org.mifos.framework.util.helpers.MoneyUtils.getMoneyAmount;

import java.math.BigDecimal;
import java.util.Date;

import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.util.helpers.Money;

public class BranchReportStaffSummaryBO extends BusinessObject {

	@SuppressWarnings("unused")
	private BranchReportBO branchReport;
	@SuppressWarnings("unused")
	private Integer staffSummaryId;
	private Short personnelId;
	private String personnelName;
	private Integer activeBorrowersCount;
	private Integer activeLoansCount;
	private Date joiningDate;
	private Integer centerCount;
	private Integer clientCount;
	private Integer newGroupCount;
	private Money loanAmountOutstanding;
	private Money interestAndFeesAmountOutstanding;
	private BigDecimal portfolioAtRisk;

	public BranchReportStaffSummaryBO(Short personnelId, String personnelName,
			Date joiningDate, Integer borrowersCount, Integer activeLoansCount,
			Integer centerCount, Integer clientCount, Integer newGroupCount,
			Money loanAmountOutstanding,
			Money interestAndFeesAmountOutstanding, BigDecimal portfolioAtRisk) {
		this.personnelId = personnelId;
		this.joiningDate = joiningDate;
		this.activeBorrowersCount = borrowersCount;
		this.activeLoansCount = activeLoansCount;
		this.personnelName = personnelName;
		this.centerCount = centerCount;
		this.clientCount = clientCount;
		this.newGroupCount = newGroupCount;
		this.loanAmountOutstanding = loanAmountOutstanding;
		this.interestAndFeesAmountOutstanding = interestAndFeesAmountOutstanding;
		this.portfolioAtRisk = portfolioAtRisk;
	}

	protected BranchReportStaffSummaryBO() {
	}

	public String getJoiningDateStr() {
		return toDisplayDate(joiningDate);
	}

	public void setBranchReport(BranchReportBO branchReport) {
		this.branchReport = branchReport;
	}

	public Integer getActiveLoansCount() {
		return activeLoansCount;
	}

	public Integer getActiveBorrowersCount() {
		return activeBorrowersCount;
	}

	public String getPersonnelName() {
		return personnelName;
	}

	public Short getPersonnelId() {
		return personnelId;
	}

	public void setCenterCount(Integer centerCount) {
		this.centerCount = centerCount;
	}

	public void setClientCount(Integer clientCount) {
		this.clientCount = clientCount;
	}

	public void setNewGroupCount(Integer newGroupCount) {
		this.newGroupCount = newGroupCount;
	}

	public Integer getCenterCount() {
		return centerCount;
	}

	public Integer getClientCount() {
		return clientCount;
	}

	public Integer getNewGroupCount() {
		return newGroupCount;
	}

	public BigDecimal getLoanAmountOutstanding() {
		return getMoneyAmount(loanAmountOutstanding);
	}

	public BigDecimal getInterestAndFeesAmountOutstanding() {
		return getMoneyAmount(interestAndFeesAmountOutstanding);
	}

	public void setInterestAndFeesAmountOutstanding(
			Money interestAndFeesAmountOutstanding) {
		this.interestAndFeesAmountOutstanding = interestAndFeesAmountOutstanding;
	}

	public void setLoanAmountOutstanding(Money loanAmountOutstanding) {
		this.loanAmountOutstanding = loanAmountOutstanding;
	}

	public void setPortfolioAtRisk(BigDecimal portfolioAtRisk) {
		this.portfolioAtRisk = portfolioAtRisk;
	}

	public BigDecimal getPortfolioAtRisk() {
		return portfolioAtRisk;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME
				* result
				+ ((activeBorrowersCount == null) ? 0 : activeBorrowersCount
						.hashCode());
		result = PRIME
				* result
				+ ((activeLoansCount == null) ? 0 : activeLoansCount.hashCode());
		result = PRIME * result
				+ ((centerCount == null) ? 0 : centerCount.hashCode());
		result = PRIME * result
				+ ((clientCount == null) ? 0 : clientCount.hashCode());
		result = PRIME
				* result
				+ ((interestAndFeesAmountOutstanding == null) ? 0
						: interestAndFeesAmountOutstanding.hashCode());
		result = PRIME * result
				+ ((joiningDate == null) ? 0 : joiningDate.hashCode());
		result = PRIME
				* result
				+ ((loanAmountOutstanding == null) ? 0 : loanAmountOutstanding
						.hashCode());
		result = PRIME * result
				+ ((newGroupCount == null) ? 0 : newGroupCount.hashCode());
		result = PRIME * result
				+ ((personnelId == null) ? 0 : personnelId.hashCode());
		result = PRIME * result
				+ ((personnelName == null) ? 0 : personnelName.hashCode());
		result = PRIME * result
				+ ((portfolioAtRisk == null) ? 0 : portfolioAtRisk.hashCode());
		result = PRIME * result
				+ ((staffSummaryId == null) ? 0 : staffSummaryId.hashCode());
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
		final BranchReportStaffSummaryBO other = (BranchReportStaffSummaryBO) obj;
		if (activeBorrowersCount == null) {
			if (other.activeBorrowersCount != null)
				return false;
		}
		else if (!activeBorrowersCount.equals(other.activeBorrowersCount))
			return false;
		if (activeLoansCount == null) {
			if (other.activeLoansCount != null)
				return false;
		}
		else if (!activeLoansCount.equals(other.activeLoansCount))
			return false;
		if (centerCount == null) {
			if (other.centerCount != null)
				return false;
		}
		else if (!centerCount.equals(other.centerCount))
			return false;
		if (clientCount == null) {
			if (other.clientCount != null)
				return false;
		}
		else if (!clientCount.equals(other.clientCount))
			return false;
		if (interestAndFeesAmountOutstanding == null) {
			if (other.interestAndFeesAmountOutstanding != null)
				return false;
		}
		else if (!interestAndFeesAmountOutstanding
				.equals(other.interestAndFeesAmountOutstanding))
			return false;
		if (joiningDate == null) {
			if (other.joiningDate != null)
				return false;
		}
		else if (!joiningDate.equals(other.joiningDate))
			return false;
		if (loanAmountOutstanding == null) {
			if (other.loanAmountOutstanding != null)
				return false;
		}
		else if (!loanAmountOutstanding.equals(other.loanAmountOutstanding))
			return false;
		if (newGroupCount == null) {
			if (other.newGroupCount != null)
				return false;
		}
		else if (!newGroupCount.equals(other.newGroupCount))
			return false;
		if (personnelId == null) {
			if (other.personnelId != null)
				return false;
		}
		else if (!personnelId.equals(other.personnelId))
			return false;
		if (personnelName == null) {
			if (other.personnelName != null)
				return false;
		}
		else if (!personnelName.equals(other.personnelName))
			return false;
		if (portfolioAtRisk == null) {
			if (other.portfolioAtRisk != null)
				return false;
		}
		else if (!portfolioAtRisk.equals(other.portfolioAtRisk))
			return false;
		if (staffSummaryId == null) {
			if (other.staffSummaryId != null)
				return false;
		}
		else if (!staffSummaryId.equals(other.staffSummaryId))
			return false;
		return true;
	}
}
