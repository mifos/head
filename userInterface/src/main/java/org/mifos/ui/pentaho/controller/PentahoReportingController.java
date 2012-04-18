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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.mifos.reports.pentaho.PentahoReport;
import org.mifos.reports.pentaho.PentahoReportsServiceFacade;
import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.mifos.ui.core.controller.BreadCrumbsLinks;
import org.mifos.ui.core.controller.BreadcrumbBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PentahoReportingController {

    private static final String REPORT_ID_PARAM = "reportId";

    private PentahoReportsServiceFacade pentahoReportsService;

    @Autowired
    public void setPentahoReportsService(PentahoReportsServiceFacade pentahoReportsService) {
        this.pentahoReportsService = pentahoReportsService;
    }

    @RequestMapping(value = "/execPentahoReport.ftl", method = RequestMethod.POST)
    public void executeReport(final HttpServletRequest request, HttpServletResponse response,
            @Valid @ModelAttribute("pentahoReportFormBean") PentahoReportFormBean pentahoReportFormBean)
            throws IOException {
        Integer reportId = pentahoReportFormBean.getReportId();
        Integer outputType = Integer.parseInt(pentahoReportFormBean.getOutputType());
        Map<String, AbstractPentahoParameter> reportParams = pentahoReportFormBean.getAllParameteres();

        PentahoReport report = this.pentahoReportsService.getReport(reportId, outputType, reportParams);

        response.setHeader("Content-Disposition", "attachment; filename=\"" + report.getFilename() + "\"");
        response.setContentType(report.getContentType());
        response.setContentLength(report.getContentSize());

        response.getOutputStream().write(report.getContent());
    }

    @RequestMapping(value = "/viewPentahoReport.ftl", method = RequestMethod.GET)
    public ModelAndView loadReport(@RequestParam(value = REPORT_ID_PARAM, required = true) Integer reportId) {
        ModelAndView mav = new ModelAndView();

        PentahoReportFormBean formBean = createFormBean(reportId);
        List<AbstractPentahoParameter> params = this.pentahoReportsService.getParametersForReport(reportId);
        formBean.setReportParameters(params);

        mav.getModel().put("pentahoReportFormBean", formBean);

        return mav;
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> getBreadCrumbs(HttpServletRequest request) {
        Integer reportId = getReportId(request);
        String reportName = this.pentahoReportsService.getReportName(reportId);
        return new BreadcrumbBuilder().withLink("tab.Reports", "reportsAction.do?method=load")
                .withLink(reportName, "viewPentahoReport.ftl?reportId=" + reportId).build();
    }

    @ModelAttribute("reportName")
    public String getReportName(HttpServletRequest request) {
        return this.pentahoReportsService.getReportName(getReportId(request));
    }

    private PentahoReportFormBean createFormBean(Integer reportId) {
        PentahoReportFormBean form = new PentahoReportFormBean();
        form.setAllowedOutputTypes(this.pentahoReportsService.getReportOutputTypes());
        form.setReportId(reportId);
        form.setOutputType("0");
        return form;
    }

    private Integer getReportId(HttpServletRequest request) {
        return Integer.parseInt(request.getParameter(REPORT_ID_PARAM));
    }
}