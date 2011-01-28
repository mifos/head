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

package org.mifos.reports.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.reports.business.ReportsCategoryBO;
import org.mifos.reports.util.helpers.ReportsConstants;

public class BirtReportsUploadActionForm extends ValidatorActionForm {
    private String reportCategoryId;
    private String reportTitle;
    protected FormFile file;
    private String reportId;
    private String isActive;

    public BirtReportsUploadActionForm() {
        super();
    }

    public void clear() {
        this.reportCategoryId = null;
        this.reportTitle = null;
        this.file = null;
        this.reportId = null;
        this.isActive = null;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        request.getSession().setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        String method = request.getParameter("method");

        validateMethod(errors, Methods.preview.toString(), method);
        validateMethod(errors, Methods.editpreview.toString(), method);

        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);
        }

        return errors;
    }

    private void validateMethod(ActionErrors errors, String verifyMethod, String methodFromRequest) {
        if (methodFromRequest.equals(verifyMethod)) {
            if (StringUtils.isBlank(reportTitle)) {
                errors.add(ReportsConstants.ERROR_TITLE, new ActionMessage(ReportsConstants.ERROR_TITLE));
            }
            if (StringUtils.isBlank(reportCategoryId) || this.getReportCategoryId().equals("-1")) {
                errors.add(ReportsConstants.ERROR_CATEGORYID, new ActionMessage(ReportsConstants.ERROR_CATEGORYID));
            }
            if (StringUtils.isBlank(isActive) || this.getIsActive().equals("-1")) {
                errors.add(ReportsConstants.ERROR_STATUS, new ActionMessage(ReportsConstants.ERROR_STATUS));
            }
            if (methodFromRequest.equals(Methods.preview.toString())) {
                if (file == null || !file.getFileName().endsWith(".rptdesign")) {
                    errors.add(ReportsConstants.ERROR_FILE, new ActionMessage(ReportsConstants.ERROR_FILE));
                }
            }
            if (methodFromRequest.equals(Methods.editpreview.toString())) {
                if (file != null && !StringUtils.isEmpty(file.getFileName())
                        && !file.getFileName().endsWith(".rptdesign")) {
                    errors.add(ReportsConstants.ERROR_FILE, new ActionMessage(ReportsConstants.ERROR_FILE));
                }
            }
        }
    }

    public String getReportCategoryId() {
        return reportCategoryId;
    }

    public void setReportCategoryId(String reportCategoryId) {
        this.reportCategoryId = reportCategoryId;
    }

    public void populate(ReportsCategoryBO reportsCategoryBO) throws OfficeException {
        this.reportCategoryId = reportsCategoryBO.getReportCategoryId().toString();
    }

    public String getReportTitle() {
        return reportTitle;
    }

    public void setReportTitle(String reportTitle) {
        this.reportTitle = reportTitle;
    }

    public void setFile(FormFile file) {
        this.file = file;
    }

    public FormFile getFile() {
        return file;
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

}
