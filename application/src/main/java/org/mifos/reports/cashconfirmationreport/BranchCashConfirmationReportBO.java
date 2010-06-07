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

package org.mifos.reports.cashconfirmationreport;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;
import org.mifos.framework.business.AbstractBusinessObject;

public class BranchCashConfirmationReportBO extends AbstractBusinessObject {

    private Integer branchCashConfirmationReportId;
    private Set<BranchCashConfirmationCenterRecoveryBO> centerRecoveries;
    private Set<BranchCashConfirmationInfoBO> centerIssues;
    private Set<BranchCashConfirmationDisbursementBO> disbursements;
    private Short branchId;
    private Date runDate;

    public BranchCashConfirmationReportBO() {
        this(null, null);
    }

    public BranchCashConfirmationReportBO(Short branchId, Date runDate) {
        this.branchId = branchId;
        this.runDate = runDate;
        centerRecoveries = new HashSet<BranchCashConfirmationCenterRecoveryBO>();
        centerIssues = new HashSet<BranchCashConfirmationInfoBO>();
        disbursements = new HashSet<BranchCashConfirmationDisbursementBO>();
    }

    public void addCenterRecoveries(List<BranchCashConfirmationCenterRecoveryBO> centerRecoveries) {
        for (BranchCashConfirmationCenterRecoveryBO recoveryBO : centerRecoveries) {
            addCenterRecovery(recoveryBO);
        }
    }

    public void addCenterRecovery(BranchCashConfirmationCenterRecoveryBO recoveryBO) {
        centerRecoveries.add(recoveryBO);
        recoveryBO.setBranchCashConfirmationReport(this);
    }

    public void addCenterIssue(BranchCashConfirmationInfoBO issueBO) {
        centerIssues.add(issueBO);
        issueBO.setBranchCashConfirmationReport(this);
    }

    public void addCenterIssues(List<BranchCashConfirmationInfoBO> centerIssues) {
        CollectionUtils.forAllDo(centerIssues, new Closure() {
            public void execute(Object input) {
                addCenterIssue((BranchCashConfirmationInfoBO) input);
            }
        });
    }

    public void addDisbursement(BranchCashConfirmationDisbursementBO disbursementBO) {
        disbursements.add(disbursementBO);
        disbursementBO.setBranchCashConfirmationReport(this);
    }

    public Integer getBranchCashConfirmationReportId() {
        return branchCashConfirmationReportId;
    }

    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result
                + ((branchCashConfirmationReportId == null) ? 0 : branchCashConfirmationReportId.hashCode());
        result = PRIME * result + ((branchId == null) ? 0 : branchId.hashCode());
        result = PRIME * result + ((runDate == null) ? 0 : runDate.hashCode());
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
        final BranchCashConfirmationReportBO other = (BranchCashConfirmationReportBO) obj;
        if (branchCashConfirmationReportId == null) {
            if (other.branchCashConfirmationReportId != null) {
                return false;
            }
        } else if (!branchCashConfirmationReportId.equals(other.branchCashConfirmationReportId)) {
            return false;
        }
        if (branchId == null) {
            if (other.branchId != null) {
                return false;
            }
        } else if (!branchId.equals(other.branchId)) {
            return false;
        }
        if (runDate == null) {
            if (other.runDate != null) {
                return false;
            }
        } else if (!runDate.equals(other.runDate)) {
            return false;
        }
        return true;
    }
}
