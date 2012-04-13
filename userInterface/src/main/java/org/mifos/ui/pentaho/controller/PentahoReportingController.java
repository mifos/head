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
package org.mifos.ui.pentaho.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.reports.pentaho.PentahoReport;
import org.mifos.reports.pentaho.PentahoReportsServiceFacade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PentahoReportingController {

    private PentahoReportsServiceFacade pentahoReportsService;

    @Autowired
    public void setPentahoReportsService(PentahoReportsServiceFacade pentahoReportsService) {
        this.pentahoReportsService = pentahoReportsService;
    }

    @RequestMapping(value = "/pentahoreport.ftl", method = RequestMethod.GET)
    public void handlePentahoReportI(final HttpServletRequest request, HttpServletResponse response) throws IOException {

        Integer reportId = Integer.parseInt(request.getParameter("reportId"));
        String outputType = request.getParameter("output-type");

        PentahoReport report = this.pentahoReportsService.getReport(reportId, outputType);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + report.getFilename() + "\"");
        response.setContentType(report.getContentType());
        response.setContentLength(report.getContentSize());

        response.getOutputStream().write(report.getContent());
    }
}