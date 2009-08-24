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

package org.mifos.application.accounts.loan.struts.actionforms;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.StringUtils;

public class ReverseLoanDisbursalActionForm extends BaseActionForm {
    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);

    private String searchString;

    private String note;

    public String getSearchString() {
        return searchString;
    }

    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        logger.debug("reset method called");
        super.reset(mapping, request);
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        logger.debug("Inside validate method");
        String method = request.getParameter(Methods.method.toString());
        ActionErrors errors = new ActionErrors();
        if (method.equals(Methods.load.toString())) {
            checkValidationForLoad(errors, getUserContext(request));
        } else if (method.equals(Methods.preview.toString())) {
            checkValidationForPreview(errors, getUserContext(request).getPreferredLocale());
        }
        if (!errors.isEmpty()) {
            request.setAttribute("methodCalled", method);
        }
        logger.debug("outside validate method");
        return errors;
    }

    private void checkValidationForLoad(ActionErrors errors, UserContext userContext) {
        if (StringUtils.isNullOrEmpty(getSearchString())) {
            addError(errors, "SearchString", LoanConstants.ERROR_LOAN_ACCOUNT_ID, getLabel(ConfigurationConstants.LOAN,
                    userContext));
        }
    }

    private void checkValidationForPreview(ActionErrors errors, Locale userLocale) {
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.LOAN_UI_RESOURCE_PROPERTYFILE, userLocale);
        String note = resources.getString("loan.note");
        if (StringUtils.isNullOrEmpty(getNote()))
            addError(errors, LoanConstants.NOTE, LoanConstants.MANDATORY, note);
        else if (getNote().length() > 500)
            addError(errors, LoanConstants.NOTE, LoanConstants.MAX_LENGTH, note, String
                    .valueOf(LoanConstants.COMMENT_LENGTH));
    }

}
