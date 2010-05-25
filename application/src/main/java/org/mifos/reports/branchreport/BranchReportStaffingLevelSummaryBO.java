/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

package org.mifos.reports.branchreport;

import org.mifos.framework.business.AbstractBusinessObject;

public class BranchReportStaffingLevelSummaryBO extends AbstractBusinessObject implements
        Comparable<BranchReportStaffingLevelSummaryBO> {
    public static final Integer IS_TOTAL = Integer.valueOf(-1);
    public static final Integer IS_NOT_TOTAL = Integer.valueOf(1);
    public static final String TOTAL_STAFF_ROLE_NAME = "Total Staff";

    @SuppressWarnings("unused")
    private BranchReportBO branchReport;
    @SuppressWarnings("unused")
    private Integer staffingLevelSummaryId;
    // roleId has been repurposed to just be -1 if it is a total and 1 if not a total
    // this is used to make sure the total sorts last (at the bottom of the list)
    private Integer isTotal;
    // roleName has been repurposed to be titleName since we're now using titles
    private String titleName;
    private Integer personnelCount;

    protected BranchReportStaffingLevelSummaryBO() {
        super();
    }

    public BranchReportStaffingLevelSummaryBO(Integer isTotal, String roleName, Integer roleCount) {
        this.isTotal = isTotal;
        this.titleName = roleName;
        this.personnelCount = roleCount;
    }

    public void setBranchReport(BranchReportBO branchReport) {
        this.branchReport = branchReport;
    }

    public String getTitleName() {
        return titleName;
    }

    public Integer getPersonnelCount() {
        return personnelCount;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((personnelCount == null) ? 0 : personnelCount.hashCode());
        result = PRIME * result + ((isTotal == null) ? 0 : isTotal.hashCode());
        result = PRIME * result + ((titleName == null) ? 0 : titleName.hashCode());
        result = PRIME * result + ((staffingLevelSummaryId == null) ? 0 : staffingLevelSummaryId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BranchReportStaffingLevelSummaryBO other = (BranchReportStaffingLevelSummaryBO) obj;
        if (personnelCount == null) {
            if (other.personnelCount != null) {
                return false;
            }
        } else if (!personnelCount.equals(other.personnelCount)) {
            return false;
        }
        if (isTotal == null) {
            if (other.isTotal != null) {
                return false;
            }
        } else if (!isTotal.equals(other.isTotal)) {
            return false;
        }
        if (titleName == null) {
            if (other.titleName != null) {
                return false;
            }
        } else if (!titleName.equals(other.titleName)) {
            return false;
        }
        if (staffingLevelSummaryId == null) {
            if (other.staffingLevelSummaryId != null) {
                return false;
            }
        } else if (!staffingLevelSummaryId.equals(other.staffingLevelSummaryId)) {
            return false;
        }
        return true;
    }

    public int compareTo(BranchReportStaffingLevelSummaryBO o) {
        if (isTotal.equals(IS_TOTAL)) {
            if (o.isTotal.equals(IS_TOTAL)) {
                return 0;
            }
            return 1;
        }
        if (o.isTotal.equals(IS_TOTAL)) {
            return -1;
        }
        return titleName.compareTo(o.titleName);
    }

}
