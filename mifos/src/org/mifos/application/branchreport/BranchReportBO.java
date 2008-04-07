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

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.mifos.framework.business.BusinessObject;

public class BranchReportBO extends BusinessObject {

	private Integer branchReportId;
	private Date runDate;
	private Short branchId;
	private Set<BranchReportClientSummaryBO> clientSummaries;
	private Set<BranchReportLoanArrearsAgingBO> loanArrearsAging;
	private Set<BranchReportStaffSummaryBO> staffSummaries;
	private Set<BranchReportStaffingLevelSummaryBO> staffingLevelSummaries;
	private Set<BranchReportLoanDetailsBO> loanDetails;
	private Set<BranchReportLoanArrearsProfileBO> loanArrearsProfile;

	public BranchReportBO() {
		this(null, null);
	}

	public BranchReportBO(Short branchId, Date runDate) {
		this.branchId = branchId;
		this.runDate = runDate;
		clientSummaries = new HashSet<BranchReportClientSummaryBO>();
		loanArrearsAging = new HashSet<BranchReportLoanArrearsAgingBO>();
		staffSummaries = new HashSet<BranchReportStaffSummaryBO>();
		staffingLevelSummaries = new HashSet<BranchReportStaffingLevelSummaryBO>();
		loanDetails = new HashSet<BranchReportLoanDetailsBO>();
		loanArrearsProfile = new HashSet<BranchReportLoanArrearsProfileBO>();
	}

	public void addClientSummary(BranchReportClientSummaryBO clientSummary) {
		clientSummaries.add(clientSummary);
		clientSummary.setBranchReport(this);
	}

	public void addLoanArrearsAging(
			BranchReportLoanArrearsAgingBO loanArrearAgingBO) {
		loanArrearsAging.add(loanArrearAgingBO);
		loanArrearAgingBO.setBranchReport(this);
	}

	public void addStaffSummary(BranchReportStaffSummaryBO staffSummary) {
		staffSummaries.add(staffSummary);
		staffSummary.setBranchReport(this);
	}

	public void addLoanArrearsProfile(
			BranchReportLoanArrearsProfileBO loanArrearProfile) {
		this.loanArrearsProfile.add(loanArrearProfile);
		loanArrearProfile.setBranchReport(this);
	}

	public void addStaffSummaries(
			List<BranchReportStaffSummaryBO> staffSummaries) {
		for (BranchReportStaffSummaryBO staffSummary : staffSummaries) {
			addStaffSummary(staffSummary);
		}
	}

	public void addStaffingLevelSummaries(
			List<BranchReportStaffingLevelSummaryBO> staffingLevelSummaries) {
		for (BranchReportStaffingLevelSummaryBO staffingLevelSummary : staffingLevelSummaries) {
			this.staffingLevelSummaries.add(staffingLevelSummary);
			staffingLevelSummary.setBranchReport(this);
		}
	}

	public void addLoanDetails(List<BranchReportLoanDetailsBO> loanDetailsBO) {
		for (BranchReportLoanDetailsBO loanDetailBO : loanDetailsBO) {
			this.loanDetails.add(loanDetailBO);
			loanDetailBO.setBranchReport(this);
		}
	}

	public Set<BranchReportClientSummaryBO> getClientSummaries() {
		return Collections.unmodifiableSet(clientSummaries);
	}

	public Set<BranchReportLoanArrearsAgingBO> getLoanArrearsAging() {
		return Collections.unmodifiableSet(loanArrearsAging);
	}

	public Set<BranchReportStaffSummaryBO> getStaffSummaries() {
		return Collections.unmodifiableSet(staffSummaries);
	}

	public Set<BranchReportStaffingLevelSummaryBO> getStaffingLevelSummaries() {
		return Collections.unmodifiableSet(staffingLevelSummaries);
	}

	public Set<BranchReportLoanDetailsBO> getLoanDetails() {
		return Collections.unmodifiableSet(loanDetails);
	}

	public Set<BranchReportLoanArrearsProfileBO> getLoanArrearsProfile() {
		return Collections.unmodifiableSet(loanArrearsProfile);
	}

	public Integer getBranchReportId() {
		return branchReportId;
	}

	public static BranchReportBO createInstanceForTest(Integer branchReportId,
			Short branchId, Date runDate) {
		BranchReportBO branchReport = new BranchReportBO(branchId, runDate);
		branchReport.branchReportId = branchReportId;
		return branchReport;
	}

	public BranchReportClientSummaryBO getClientSummary(Predicate predicate) {
		return (BranchReportClientSummaryBO) CollectionUtils.find(
				clientSummaries, predicate);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((branchId == null) ? 0 : branchId.hashCode());
		result = PRIME * result
				+ ((branchReportId == null) ? 0 : branchReportId.hashCode());
		result = PRIME * result
				+ ((clientSummaries == null) ? 0 : clientSummaries.hashCode());
		result = PRIME
				* result
				+ ((loanArrearsAging == null) ? 0 : loanArrearsAging.hashCode());
		result = PRIME * result + ((runDate == null) ? 0 : runDate.hashCode());
		result = PRIME * result
				+ ((staffSummaries == null) ? 0 : staffSummaries.hashCode());
		result = PRIME
				* result
				+ ((staffingLevelSummaries == null) ? 0
						: staffingLevelSummaries.hashCode());
		return result;
	}

	public Short getBranchId() {
		return branchId;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final BranchReportBO other = (BranchReportBO) obj;
		if (branchId == null) {
			if (other.branchId != null)
				return false;
		}
		else if (!branchId.equals(other.branchId))
			return false;
		if (branchReportId == null) {
			if (other.branchReportId != null)
				return false;
		}
		else if (!branchReportId.equals(other.branchReportId))
			return false;
		if (clientSummaries == null) {
			if (other.clientSummaries != null)
				return false;
		}
		else if (!clientSummaries.equals(other.clientSummaries))
			return false;
		if (loanArrearsAging == null) {
			if (other.loanArrearsAging != null)
				return false;
		}
		else if (!loanArrearsAging.equals(other.loanArrearsAging))
			return false;
		if (runDate == null) {
			if (other.runDate != null)
				return false;
		}
		else if (!runDate.equals(other.runDate))
			return false;
		if (staffSummaries == null) {
			if (other.staffSummaries != null)
				return false;
		}
		else if (!staffSummaries.equals(other.staffSummaries))
			return false;
		if (staffingLevelSummaries == null) {
			if (other.staffingLevelSummaries != null)
				return false;
		}
		else if (!staffingLevelSummaries.equals(other.staffingLevelSummaries))
			return false;
		return true;
	}
}
