/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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
package org.mifos.reports.pentaho;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.springframework.security.access.prepost.PreAuthorize;

public interface PentahoReportsServiceFacade {

    @PreAuthorize("isFullyAuthenticated()")
    PentahoReport getReport(Integer reportId, Integer outputTypeId, Map<String, AbstractPentahoParameter> params);

    @PreAuthorize("isFullyAuthenticated()")
    PentahoReport getAdminReport(Integer adminReportId, Integer outputTypeId, Map<String, AbstractPentahoParameter> params);
    
    @PreAuthorize("isFullyAuthenticated()")
    String getReportName(Integer reportId);
    
    @PreAuthorize("isFullyAuthenticated()")
    String getAdminReportFileName(Integer adminReportId);

    @PreAuthorize("isFullyAuthenticated()")
    Map<String, String> getReportOutputTypes();

    @PreAuthorize("isFullyAuthenticated()")
    List<AbstractPentahoParameter> getParametersForReport(Integer reportId, HttpServletRequest request, Map<String, AbstractPentahoParameter> selectedValues, boolean update);

    @PreAuthorize("isFullyAuthenticated()")
    boolean checkAccessToReport(Integer reportId);
    
    @PreAuthorize("isFullyAuthenticated()")
    Date getEtlLastUpdateDate(HttpServletRequest request);
    
    @PreAuthorize("isFullyAuthenticated()")
    boolean isDW(Integer reportId);
}
