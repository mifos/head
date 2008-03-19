package org.mifos.application.branchreport;

import org.mifos.framework.business.BusinessObject;

public class BranchReportStaffingLevelSummaryBO extends BusinessObject
		implements Comparable<BranchReportStaffingLevelSummaryBO> {
	public static final Integer TOTAL_STAFF_ROLE_ID = Integer.valueOf(-1);
	public static final String TOTAL_STAFF_ROLE_NAME = "Total Staff";

	@SuppressWarnings("unused")
	private BranchReportBO branchReport;
	@SuppressWarnings("unused")
	private Integer staffingLevelSummaryId;
	private Integer roleId;
	private String roleName;
	private Integer personnelCount;

	protected BranchReportStaffingLevelSummaryBO() {
		super();
	}

	public BranchReportStaffingLevelSummaryBO(Integer roleId, String roleName,
			Integer roleCount) {
		this.roleId = roleId;
		this.roleName = roleName;
		this.personnelCount = roleCount;
	}

	public void setBranchReport(BranchReportBO branchReport) {
		this.branchReport = branchReport;
	}

	public String getRolename() {
		return roleName;
	}

	public Integer getPersonnelCount() {
		return personnelCount;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result
				+ ((personnelCount == null) ? 0 : personnelCount.hashCode());
		result = PRIME * result + ((roleId == null) ? 0 : roleId.hashCode());
		result = PRIME * result
				+ ((roleName == null) ? 0 : roleName.hashCode());
		result = PRIME
				* result
				+ ((staffingLevelSummaryId == null) ? 0
						: staffingLevelSummaryId.hashCode());
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
		final BranchReportStaffingLevelSummaryBO other = (BranchReportStaffingLevelSummaryBO) obj;
		if (personnelCount == null) {
			if (other.personnelCount != null)
				return false;
		}
		else if (!personnelCount.equals(other.personnelCount))
			return false;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		}
		else if (!roleId.equals(other.roleId))
			return false;
		if (roleName == null) {
			if (other.roleName != null)
				return false;
		}
		else if (!roleName.equals(other.roleName))
			return false;
		if (staffingLevelSummaryId == null) {
			if (other.staffingLevelSummaryId != null)
				return false;
		}
		else if (!staffingLevelSummaryId.equals(other.staffingLevelSummaryId))
			return false;
		return true;
	}

	public int compareTo(BranchReportStaffingLevelSummaryBO o) {
		if (roleId.equals(o.roleId))
			return 0;
		if (roleId.equals(TOTAL_STAFF_ROLE_ID))
			return 1;
		if (o.roleId.equals(TOTAL_STAFF_ROLE_ID))
			return -1;
		return roleId - o.roleId;
	}

	public static BranchReportStaffingLevelSummaryBO createInstanceForTest(
			Integer roleId) {
		BranchReportStaffingLevelSummaryBO staffingLevelBO = new BranchReportStaffingLevelSummaryBO();
		staffingLevelBO.roleId = roleId;
		return staffingLevelBO;
	}
}
