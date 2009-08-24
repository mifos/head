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

package org.mifos.application.admindocuments.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.upload.FormFile;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.reports.util.helpers.ReportsConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.StringUtils;

public class BirtAdminDocumentUploadActionForm extends ValidatorActionForm {

    private String accountTypeId;
    private String accountTypeName;
    private String adminiDocumentTitle;
    protected FormFile file;
    private String reportId;
    private String isActive;
    private String id;
    private String name;
    private String[] statusList;

    public BirtAdminDocumentUploadActionForm() {
        super();
    }

    public void clear() {

        this.accountTypeId = null;
        this.accountTypeName = null;
        this.adminiDocumentTitle = null;
        this.file = null;
        this.reportId = null;
        this.isActive = null;
        this.id = null;
        this.name = null;
        statusList = null;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String method = request.getParameter(Methods.method.toString());

        ActionErrors errors = new ActionErrors();
        if (null == request.getAttribute(Constants.CURRENTFLOWKEY))
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        if (method.equals(Methods.preview.toString()))
            validateMethod(errors, Methods.preview.toString(), method);
        else if (method.equals(Methods.editpreview.toString()))
            validateMethod(errors, Methods.editpreview.toString(), method);
        if (null != errors && !errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);
        }

        return errors;
    }

    private void validateMethod(ActionErrors errors, String verifyMethod, String methodFromRequest) {
        if (methodFromRequest.equals(verifyMethod)) {
            if (StringUtils.isNullOrEmpty(adminiDocumentTitle)) {
                errors.add(ReportsConstants.ERROR_TITLE, new ActionMessage(ReportsConstants.ERROR_TITLE));
            }
            if (StringUtils.isNullOrEmpty(accountTypeId) || this.getAccountTypeId().equals("-1")) {
                errors.add(ReportsConstants.ERROR_ACCOUNTTYPE, new ActionMessage(ReportsConstants.ERROR_ACCOUNTTYPE));
            }
            if (StringUtils.isNullOrEmpty(isActive) || this.getIsActive().equals("-1")) {
                errors.add(ReportsConstants.ERROR_STATUS, new ActionMessage(ReportsConstants.ERROR_STATUS));
            }
            if (this.statusList == null || this.statusList.length == 0) {
                errors.add(ReportsConstants.ERROR_STATUSLIST, new ActionMessage(ReportsConstants.ERROR_STATUSLIST));
            }
            if (methodFromRequest.equals(Methods.preview.toString())) {
                if (file == null || !file.getFileName().endsWith(".rptdesign")) {
                    errors.add(ReportsConstants.ERROR_FILE, new ActionMessage(ReportsConstants.ERROR_FILE));
                }

            }
            if (methodFromRequest.equals(Methods.editpreview.toString())) {
                if (file == null || StringUtils.isEmpty(file.getFileName())
                        || !file.getFileName().endsWith(".rptdesign")) {
                    errors.add(ReportsConstants.ERROR_FILE, new ActionMessage(ReportsConstants.ERROR_FILE));
                }
            }
        }
    }

    public String getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(String accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getAdminiDocumentTitle() {
        return adminiDocumentTitle;
    }

    public void setAdminiDocumentTitle(String adminiDocumentTitle) {
        this.adminiDocumentTitle = adminiDocumentTitle;
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

    public String[] getStatusList() {
        return statusList;
    }

    public void setStatusList(String[] statusList) {
        this.statusList = statusList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountTypeName() {
        return accountTypeName;
    }

    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }
}
