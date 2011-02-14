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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.framework.util.helpers.Constants;

/**
 * This class is the action form for Applying adjustments.
 */
public class ApplyAdjustmentActionForm extends ValidatorActionForm {
    private String input;

    private String adjustmentNote;

    private String globalAccountNum;

    private boolean adjustcheckbox;

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getAdjustmentNote() {
        return adjustmentNote;
    }

    public void setAdjustmentNote(String adjustmentNote) {
        this.adjustmentNote = adjustmentNote;
    }

    public String getGlobalAccountNum() {
        return globalAccountNum;
    }

    public void setGlobalAccountNum(String globalAccountNum) {
        this.globalAccountNum = globalAccountNum;
    }

    public boolean getAdjustcheckbox() {
        return adjustcheckbox;
    }

    public void setAdjustcheckbox(boolean adjustcheckbox) {
        this.adjustcheckbox = adjustcheckbox;

    }

    @Override
    public void reset(ActionMapping actionMapping, HttpServletRequest request) {
        this.adjustcheckbox = false;

    }

    @Override
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest request) {
        if (null == request.getAttribute(Constants.CURRENTFLOWKEY)) {
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        }
        ActionErrors actionErrors = new ActionErrors();
        String method = request.getParameter("method");
        if (null != method && method.equals("previewAdjustment")) {
            if (!adjustcheckbox) {
                request.setAttribute("method", "loadAdjustment");
                actionErrors.add("", new ActionMessage("errors.mandatorycheckbox"));
            }
            if (adjustmentNote == null || adjustmentNote.trim() == "") {
                request.setAttribute("method", "loadAdjustment");
                actionErrors.add("", new ActionMessage("errors.mandatorytextarea"));
            } else if (adjustmentNote.length() > 300) {
                request.setAttribute("method", "loadAdjustment");
                actionErrors.add("", new ActionMessage("errors.adjustmentNoteTooBig"));
            }
            if (!actionErrors.isEmpty()) {
                return actionErrors;
            }
        }
        return super.validate(actionMapping, request);
    }
}
