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

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.apache.commons.lang.ObjectUtils;
import org.mifos.framework.business.BusinessObject;

public class BranchReportClientSummaryBO extends BusinessObject {

	public static final String CENTER_COUNT = "center.count";
	public static final String GROUP_COUNT = "group.count";
	public static final String ACTIVE_CLIENTS_COUNT = "active.members.count";
	public static final String ACTIVE_BORROWERS_COUNT = "active.borrowers.count";
	public static final String ACTIVE_SAVERS_COUNT = "active.savers.count";
	public static final String LOAN_ACCOUNT_DORMANT_COUNT = "loan.account.dormant.count";
	public static final String SAVING_ACCOUNT_DORMANT_COUNT = "saving.account.dormant.count";
	public static final String DROP_OUTS_COUNT = "dropouts.count";
	public static final String REPLACEMENTS_COUNT = "replacements.count";
	public static final String ON_HOLDS_COUNT = "onholds.count";
	public static final String PORTFOLIO_AT_RISK = "portfolio.at.risk";
	public static final String DROP_OUT_RATE = "dropout.rate";
	
	@SuppressWarnings("unused")
	private BranchReportBO branchReport;
	private Integer clientSummaryId;
	private String fieldName;
	private String total;
	private String veryPoorTotal;

	public BranchReportClientSummaryBO(String fieldName, Integer total,
			Integer veryPoorTotal) {
		this.fieldName = fieldName;
		this.total = ObjectUtils.toString(total);
		this.veryPoorTotal = ObjectUtils.toString(veryPoorTotal);
	}

	//TODO TW: Externalize the format of the BigDecimal into a properties file
	public BranchReportClientSummaryBO(String fieldName, BigDecimal total,
			BigDecimal veryPoorTotal) {
		this.fieldName = fieldName;
		this.total = total.setScale(2, RoundingMode.HALF_EVEN).toPlainString();
		if(veryPoorTotal != null)
			this.veryPoorTotal = veryPoorTotal.setScale(2, RoundingMode.HALF_EVEN).toPlainString();
	}
	
	protected BranchReportClientSummaryBO() {
		super();
	}

	public boolean isOfTypeCenterCount() {
		return CENTER_COUNT.equals(fieldName);
	}

	public boolean isOfTypeGroupCount() {
		return GROUP_COUNT.equals(fieldName);
	}

	public boolean isOfTypeActiveMembers() {
		return ACTIVE_CLIENTS_COUNT.equals(fieldName);
	}

	public boolean isOfTypeActiveBorrowers() {
		return ACTIVE_BORROWERS_COUNT.equals(fieldName);
	}

	public boolean isOfTypeReplacements() {
		return REPLACEMENTS_COUNT.equals(fieldName);
	}

	public boolean isOfTypeLoanAccountDormantCount() {
		return LOAN_ACCOUNT_DORMANT_COUNT.equals(fieldName);
	}

	public boolean isOfTypeSavingAccountDormantCount() {
		return SAVING_ACCOUNT_DORMANT_COUNT.equals(fieldName);
	}
	
	public boolean isOfTypeDropOuts() {
		return DROP_OUTS_COUNT.equals(fieldName);
	}

	public boolean isOfTypeOnHolds() {
		return ON_HOLDS_COUNT.equals(fieldName);
	}

	public boolean isOfTypeActiveSavers() {
		return ACTIVE_SAVERS_COUNT.equals(fieldName);
	}

	public boolean isOfTypePortfolioAtRisk() {
		return PORTFOLIO_AT_RISK.equals(fieldName);
	}
	
	public boolean isOfTypeDropoutRate() {
		return DROP_OUT_RATE.equals(fieldName);
	}

	public String getTotal() {
		return total;
	}

	public String getVeryPoorTotal() {
		return veryPoorTotal;
	}

	public void setBranchReport(BranchReportBO branchReport) {
		this.branchReport = branchReport;
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((clientSummaryId == null) ? 0 : clientSummaryId.hashCode());
		result = PRIME * result
				+ ((fieldName == null) ? 0 : fieldName.hashCode());
		result = PRIME * result + ((total == null) ? 0 : total.hashCode());
		result = PRIME * result
				+ ((veryPoorTotal == null) ? 0 : veryPoorTotal.hashCode());
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
		final BranchReportClientSummaryBO other = (BranchReportClientSummaryBO) obj;
		if (clientSummaryId == null) {
			if (other.clientSummaryId != null)
				return false;
		}
		else if (!clientSummaryId.equals(other.clientSummaryId))
			return false;
		if (fieldName == null) {
			if (other.fieldName != null)
				return false;
		}
		else if (!fieldName.equals(other.fieldName))
			return false;
		if (total == null) {
			if (other.total != null)
				return false;
		}
		else if (!total.equals(other.total))
			return false;
		if (veryPoorTotal == null) {
			if (other.veryPoorTotal != null)
				return false;
		}
		else if (!veryPoorTotal.equals(other.veryPoorTotal))
			return false;
		return true;
	}

}
