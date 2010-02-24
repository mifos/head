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

package org.mifos.reports.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.mifos.reports.business.ReportParameterForm;
import org.mifos.reports.business.validator.Errors;
import org.mifos.reports.business.validator.ReportParameterValidator;
import org.mifos.reports.business.validator.ReportParameterValidatorFactory;
import org.mifos.security.util.UserContext;
import org.mifos.framework.servlet.ModifiableParameterServletRequest;

public class BirtReportValidationAction extends HttpServlet {

    private static final String ERRORS = "reportErrors";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        validateReportParameters(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        doGet(request, response);
    }

    private void validateReportParameters(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ReportParameterValidator<ReportParameterForm> validator = new ReportParameterValidatorFactory()
                .getValidator(request.getParameter("__report"));
        if (validator == null || validator.isAFreshRequest(request)) {
            // go to report parameter page
            request.getRequestDispatcher("/preview").forward(request, response);
            return;
        }

        ReportParameterForm form = validator.buildReportParameterForm(request);
        Errors errors = new Errors(((UserContext) request.getSession().getAttribute("UserContext"))
                .getPreferredLocale());
        validator.validate(form, errors);
        ModifiableParameterServletRequest modifiedRequest = new ModifiableParameterServletRequest(request);
        if (errors.hasErrors()) {
            request.setAttribute(ERRORS, errors);
            validator.removeRequestParameters(modifiedRequest, form, errors);
        }
        request.getRequestDispatcher("/preview").forward(modifiedRequest, response);
    }
}
