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
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.mifos.reports.pentaho.PentahoReport;
import org.mifos.reports.pentaho.PentahoReportsServiceFacade;
import org.mifos.reports.pentaho.PentahoValidationError;
import org.mifos.reports.pentaho.params.AbstractPentahoParameter;
import org.mifos.ui.core.controller.BreadCrumbsLinks;
import org.mifos.ui.core.controller.BreadcrumbBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class PentahoReportingController {

    private static final String REPORT_ID_PARAM = "reportId";
    private static final String CANCEL_PARAM = "CANCEL";
    private static final String REPORTS_MAIN_URL = "reportsAction.do?method=load";

    private PentahoReportsServiceFacade pentahoReportsService;

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(new PentahoReportFormValidator());
    }

    @Autowired
    public void setPentahoReportsService(PentahoReportsServiceFacade pentahoReportsService) {
        this.pentahoReportsService = pentahoReportsService;
    }

    @RequestMapping(value = "/execPentahoReport.ftl", method = RequestMethod.POST)
    public ModelAndView executeReport(final HttpServletRequest request, HttpServletResponse response,
            @RequestParam(value = CANCEL_PARAM, required = false) String cancel,
            @Valid @ModelAttribute("pentahoReportFormBean") PentahoReportFormBean pentahoReportFormBean,
            BindingResult bindingResult) throws IOException {
        if (!this.pentahoReportsService.checkAccessToReport(pentahoReportFormBean.getReportId())) {
            throw new AccessDeniedException("Access denied");
        }

        ModelAndView mav = null;
        Integer reportId = pentahoReportFormBean.getReportId();
        if (StringUtils.isNotBlank(cancel)) {
            mav = new ModelAndView("redirect:" + REPORTS_MAIN_URL);
        } else if (bindingResult.hasErrors()) {
            mav = new ModelAndView("viewPentahoReport");
            initFormBean(pentahoReportFormBean, reportId, request);
        } else {
            Integer outputType = Integer.parseInt(pentahoReportFormBean.getOutputType());
            Map<String, AbstractPentahoParameter> reportParams = pentahoReportFormBean.getAllParameteres();

            PentahoReport report = this.pentahoReportsService.getReport(reportId, outputType, reportParams);

            if (report.isInError()) {
                for (PentahoValidationError error : report.getErrors()) {
                    addErrorToBindingResult(error, bindingResult);
                }
                mav = new ModelAndView("viewPentahoReport");
                initFormBean(pentahoReportFormBean, reportId, request);
            } else {
                response.setHeader("Content-Disposition", "attachment; filename=\"" + report.getFilename() + "\"");
                response.setContentType(report.getContentType());
                response.setContentLength(report.getContentSize());

                response.getOutputStream().write(report.getContent());
            }
        }
        return mav;
    }

    @RequestMapping(value = "/viewPentahoReport.ftl", method=RequestMethod.GET)
    public void loadReport(@RequestParam(value = REPORT_ID_PARAM, required = true) Integer reportId,
            @ModelAttribute("pentahoReportFormBean") PentahoReportFormBean formBean, HttpServletRequest request) {
        if (!this.pentahoReportsService.checkAccessToReport(reportId)) {
            throw new AccessDeniedException("Access denied");
        }
        initFormBean(formBean, reportId, request);
    }

    @ModelAttribute("breadcrumbs")
    public List<BreadCrumbsLinks> getBreadCrumbs(HttpServletRequest request) {
        Integer reportId = getReportId(request);
        String reportName = this.pentahoReportsService.getReportName(reportId);
        return new BreadcrumbBuilder().withLink("tab.Reports", REPORTS_MAIN_URL)
                .withLink(reportName, "viewPentahoReport.ftl?reportId=" + reportId).build();
    }

    @ModelAttribute("reportName")
    public String getReportName(HttpServletRequest request) {
        return this.pentahoReportsService.getReportName(getReportId(request));
    }

    private void initFormBean(PentahoReportFormBean form, Integer reportId, HttpServletRequest request) {
        form.setAllowedOutputTypes(this.pentahoReportsService.getReportOutputTypes());
        form.setReportId(reportId);
        if (form.getOutputType() == null) {
            form.setOutputType("0");
        }
        Map <String, AbstractPentahoParameter> selectedValues = form.getAllParameteres();
        boolean update=false;
        List<AbstractPentahoParameter> params = this.pentahoReportsService.getParametersForReport(reportId, request, selectedValues, update);
        if (this.pentahoReportsService.isDW(reportId)) {
        	form.setEtlLastUpdate(this.pentahoReportsService.getEtlLastUpdateDate(request));
        	if (form.getEtlLastUpdate().equals(new Date(0))) {
        		request.getSession().setAttribute("dwNotRun", "true");
        	}
        	request.getSession().setAttribute("isDW", "true");
        } else {
        	request.getSession().setAttribute("isDW", "false");
        }
        form.setReportParameters(params);
    }

    private void addErrorToBindingResult(PentahoValidationError validationError, BindingResult bindingResult) {
        ObjectError error;
        if (validationError.isFieldError()) {
            error = new FieldError("pentahoReportFormBean", validationError.getParamName(),
                    validationError.getParamName() + ": " + validationError.getErrorMessage());
        } else {
            error = new ObjectError("pentahoReportFormBean", validationError.getErrorMessage());
        }
        bindingResult.addError(error);
    }

    @ModelAttribute("report_id")
    private Integer getReportId(HttpServletRequest request) {
        return Integer.parseInt(request.getParameter(REPORT_ID_PARAM));
    }
    
	@RequestMapping(value = "/viewPentahoReport.ftl", method = RequestMethod.POST)
	public void updateSelectDropdown(
			@ModelAttribute("pentahoReportFormBean") PentahoReportFormBean formBean,
			HttpServletRequest request) {
		formBean.setAllowedOutputTypes(this.pentahoReportsService.getReportOutputTypes());
		Map<String, AbstractPentahoParameter> selectedValues = formBean
				.getAllParameteres();
		boolean update = true;
		List<AbstractPentahoParameter> params = this.pentahoReportsService
				.getParametersForReport(getReportId(request), request, selectedValues,
						update);

		formBean.setReportParameters(params);

	}
}