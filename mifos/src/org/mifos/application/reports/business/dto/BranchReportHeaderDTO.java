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
package org.mifos.application.reports.business.dto;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.util.helpers.DateUtils;

public class BranchReportHeaderDTO {

	private OfficeBO office;
	private String branchManagerName;

	public BranchReportHeaderDTO(OfficeBO office, String branchManagerName) {
		this.office = office;
		this.branchManagerName = branchManagerName;
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

	public String getCurrentDate() {
		return DateUtils.getLocalizedDateFormat().format(DateUtils.currentDate());
	}

	public String getBranchManagerName() {
		return branchManagerName;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((office == null) ? 0 : office.hashCode());
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
		final BranchReportHeaderDTO other = (BranchReportHeaderDTO) obj;
		if (office == null) {
			if (other.office != null)
				return false;
		}
		else if (!office.equals(other.office))
			return false;
		return true;
	}
}
