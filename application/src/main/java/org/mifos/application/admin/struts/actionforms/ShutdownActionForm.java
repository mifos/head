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

package org.mifos.application.admin.struts.actionforms;

import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.security.util.SecurityConstants;
import org.mifos.application.util.helpers.Methods;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.Globals;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;


public class ShutdownActionForm extends BaseActionForm {

    private String shutdownTimeout;

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String method = request.getParameter("method");
        if (null == request.getAttribute(Constants.CURRENTFLOWKEY))
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        ActionErrors errors;
        try {
            errors = validateFields(method);
        } catch (ApplicationException ae) {
            errors = new ActionErrors();
            errors.add(ae.getKey(), new ActionMessage(ae.getKey(), ae.getValues()));
        }
        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);
        }
        errors.add(super.validate(mapping, request));
        return errors;
    }

    private ActionErrors validateFields(String method) throws ApplicationException {
        ActionErrors errors = new ActionErrors();
        if (method.equals(Methods.shutdown.toString())) {
            validateTimeout(errors);
        }
        return errors;
    }

    public String getShutdownTimeout() {
        return shutdownTimeout;
    }

    public void setShutdownTimeout(String shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }

    protected void validateTimeout(ActionErrors errors) {
        if (StringUtils.isBlank(getShutdownTimeout()) || !isValid(getShutdownTimeout())) {
            errors.add(SecurityConstants.KEY_SHUTDOWN_TIMEOUT_INVALID,
                    new ActionMessage(SecurityConstants.KEY_SHUTDOWN_TIMEOUT_INVALID));
        }
    }

    private boolean isValid(String inputString) {
        try {
            Long.parseLong(inputString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
