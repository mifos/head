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

package org.mifos.reports.business;

import org.mifos.framework.business.BusinessObject;
import org.mifos.security.util.UserContext;

public class ReportsBO extends BusinessObject {

    public ReportsBO() {
        this.reportsCategoryBO = new ReportsCategoryBO();
        this.reportsJasperMap = new ReportsJasperMap();
    }

    public ReportsBO(UserContext userContext) {
        super(userContext);
    }

    private Short reportId;
    private String reportName;
    private String reportIdentifier;
    private ReportsCategoryBO reportsCategoryBO;
    private ReportsJasperMap reportsJasperMap;
    private Short activityId;
    private Short isActive;
    public static final Short ACTIVE = Short.valueOf("1");

    public Short getReportId() {
        return reportId;
    }

    public void setReportId(Short reportId) {
        this.reportId = reportId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getReportIdentifier() {
        return reportIdentifier;
    }

    public void setReportIdentifier(String reportIdentifier) {
        this.reportIdentifier = reportIdentifier;
    }

    public ReportsCategoryBO getReportsCategoryBO() {
        return reportsCategoryBO;
    }

    public void setReportsCategoryBO(ReportsCategoryBO reportsCategoryBO) {
        this.reportsCategoryBO = reportsCategoryBO;
    }

    public Short getEntityID() {
        return null;
    }

    public ReportsJasperMap getReportsJasperMap() {
        return reportsJasperMap;
    }

    public void setReportsJasperMap(ReportsJasperMap reportsJasperMap) {
        this.reportsJasperMap = reportsJasperMap;
    }

    public Short getActivityId() {
        return activityId;
    }

    public void setActivityId(Short activityId) {
        this.activityId = activityId;
    }

    public Short getIsActive() {
        return isActive;
    }

    public void setIsActive(Short isActive) {
        this.isActive = isActive;
    }

}
