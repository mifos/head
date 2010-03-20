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
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.reports.util.helpers.ReportsConstants;

public class ReportsCategoryActionForm extends ValidatorActionForm {

    private String categoryName;
    private short categoryId;

    public ReportsCategoryActionForm() {
        super();
    }

    public void clear() {
        categoryId = 0;
        categoryName = null;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public short getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(short categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        request.getSession().setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        String method = request.getParameter("method");

        validateMethod(errors, Methods.preview.toString(), method);
        validateMethod(errors, Methods.editPreview.toString(), method);

        if (null != errors && !errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);
        }

        return errors;
    }

    private void validateMethod(ActionErrors errors, String verifyMethod, String methodFromRequest) {
        if (methodFromRequest.equals(verifyMethod)) {
            if (StringUtils.isBlank(categoryName)) {
                errors.add(ReportsConstants.ERROR_CATEGORYNAME, new ActionMessage(ReportsConstants.ERROR_CATEGORYNAME));
            }
        }
    }

}
