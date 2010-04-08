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

package org.mifos.reports.business;

import java.util.HashSet;
import java.util.Set;

import org.mifos.framework.business.AbstractBusinessObject;

public class ReportsCategoryBO extends AbstractBusinessObject {

    public static short ANALYSIS = 6;

    public ReportsCategoryBO() {
        reportsSet = new HashSet<ReportsBO>();
    }

    private Short reportCategoryId;
    private String reportCategoryName;
    private Set<ReportsBO> reportsSet;
    private Short activityId;

    public Short getReportCategoryId() {
        return reportCategoryId;
    }

    public void setReportCategoryId(Short reportCategoryId) {
        this.reportCategoryId = reportCategoryId;
    }

    public String getReportCategoryName() {
        return reportCategoryName;
    }

    public void setReportCategoryName(String reportCategoryName) {
        this.reportCategoryName = reportCategoryName;
    }

    public Set<ReportsBO> getReportsSet() {
        return reportsSet;
    }

    @SuppressWarnings("unused")
    private void setReportsSet(Set<ReportsBO> reportsSet) {
        this.reportsSet = reportsSet;
    }

    public void addReports(ReportsBO reportsBO) {
        this.reportsSet.add(reportsBO);
    }

    public void setActivityId(Short activityId) {
        this.activityId = activityId;
    }

    public Short getActivityId() {
        return activityId;
    }

}
