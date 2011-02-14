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

package org.mifos.accounts.struts.actionforms;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.center.util.helpers.ValidateMethods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;

public class NotesActionForm extends BaseActionForm {

    private String accountId;

    private String accountTypeId;

    private String prdOfferingName;

    private String comment;

    private String globalAccountNum;

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getPrdOfferingName() {
        return prdOfferingName;
    }

    public void setPrdOfferingName(String prdOfferingName) {
        this.prdOfferingName = prdOfferingName;
    }

    public String getAccountTypeId() {
        return accountTypeId;
    }

    public void setAccountTypeId(String accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGlobalAccountNum() {
        return globalAccountNum;
    }

    public void setGlobalAccountNum(String globalAccountNum) {
        this.globalAccountNum = globalAccountNum;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String methodCalled = request.getParameter(Methods.method.toString());
        ActionErrors errors = null;
        if (null != methodCalled) {
            if (Methods.cancel.toString().equals(methodCalled) || Methods.searchNext.toString().equals(methodCalled)
                    || Methods.search.toString().equals(methodCalled)
                    || Methods.searchPrev.toString().equals(methodCalled)
                    || Methods.load.toString().equals(methodCalled) || Methods.create.toString().equals(methodCalled)) {
                request.setAttribute(Constants.SKIPVALIDATION, Boolean.valueOf(true));
            } else if (Methods.preview.toString().equals(methodCalled)) {
                errors = handlePreviewValidations(request, errors);
            }
        }
        if (null != errors && !errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", methodCalled);
        }
        return errors;
    }

    private ActionErrors handlePreviewValidations(HttpServletRequest request, ActionErrors errors) {
        Locale locale = getUserContext(request).getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE, locale);
        String notes = resources.getString("Account.Notes");
        if (ValidateMethods.isNullOrBlank(getComment())) {
            if (null == errors) {
                errors = new ActionErrors();
            }
            errors.add(AccountConstants.ERROR_MANDATORY, new ActionMessage(AccountConstants.ERROR_MANDATORY, notes));
        } else if (getComment().length() > AccountConstants.COMMENT_LENGTH) {
            // status length is more than 500, throw an exception
            if (null == errors) {
                errors = new ActionErrors();
            }
            errors.add(AccountConstants.MAX_LENGTH, new ActionMessage(AccountConstants.MAX_LENGTH, notes,
                    AccountConstants.COMMENT_LENGTH));
        }
        return errors;
    }
}
