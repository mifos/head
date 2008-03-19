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
