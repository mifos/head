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
package org.mifos.application.reports.business.dto;

import static org.mifos.application.reports.util.helpers.ReportUtils.toDisplayDate;

import java.util.Date;

import org.mifos.application.office.business.OfficeBO;

public class BranchReportHeaderDTO {

	private OfficeBO office;
	private String branchManagerName;
	private final Date runDate;

	public BranchReportHeaderDTO(OfficeBO office, String branchManagerName, Date runDate) {
		this.office = office;
		this.branchManagerName = branchManagerName;
		this.runDate = runDate;
	}

	public String getBranchName() {
		return office.getOfficeName();
	}

	public String getCityName() {
		return office.getAddress().getAddress().getCity();
	}

	public String getStateName() {
		return office.getAddress().getAddress().getState();
	}

	public String getReportDate() {
		return toDisplayDate(runDate);
	}

	public String getBranchManagerName() {
		return branchManagerName;
	}
	
	@Override
	public String toString() {
		return " office:" + office.getOfficeId()+" branchManager:"+branchManagerName+" runDate:" + runDate;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = super.hashCode();
		result = PRIME * result + ((branchManagerName == null) ? 0 : branchManagerName.hashCode());
		result = PRIME * result + ((office == null) ? 0 : office.hashCode());
		result = PRIME * result + ((runDate == null) ? 0 : runDate.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (getClass() != obj.getClass())
			return false;
		final BranchReportHeaderDTO other = (BranchReportHeaderDTO) obj;
		if (branchManagerName == null) {
			if (other.branchManagerName != null)
				return false;
		}
		else if (!branchManagerName.equals(other.branchManagerName))
			return false;
		if (office == null) {
			if (other.office != null)
				return false;
		}
		else if (!office.equals(other.office))
			return false;
		if (runDate == null) {
			if (other.runDate != null)
				return false;
		}
		else if (!runDate.equals(other.runDate))
			return false;
		return true;
	}
}
