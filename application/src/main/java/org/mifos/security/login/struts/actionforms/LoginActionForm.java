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

package org.mifos.security.login.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.security.login.util.helpers.LoginConstants;

public class LoginActionForm extends BaseActionForm {

    private String userName;
    private String input;
    private String password;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String method = request.getParameter(Methods.method.toString());
        if (method.equals(Methods.login.toString()) || method.equals(Methods.updatePassword.toString())) {
            errors.add(super.validate(mapping, request));
            if (method.equals(Methods.updatePassword.toString())) {
                if (newPassword.equals(oldPassword)) {
                    errors.add(LoginConstants.SAME_OLD_AND_NEW_PASSWORD, new ActionMessage(
                            LoginConstants.SAME_OLD_AND_NEW_PASSWORD));
                }
            }
        }
        if (!method.equals(Methods.validate.toString())) {
            request.setAttribute(LoginConstants.METHODCALLED, method);
        }
        return errors;
    }
}
