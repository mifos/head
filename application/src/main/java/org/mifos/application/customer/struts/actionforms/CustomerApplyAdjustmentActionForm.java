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

package org.mifos.application.customer.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.validator.ValidatorActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.framework.util.helpers.Constants;

public class CustomerApplyAdjustmentActionForm extends ValidatorActionForm {

    private String input;

    private String adjustmentNote;

    private String globalCustNum;

    private boolean adjustcheckbox;

    public boolean isAdjustcheckbox() {
        return adjustcheckbox;
    }

    public void setAdjustcheckbox(boolean adjustcheckbox) {
        this.adjustcheckbox = adjustcheckbox;
    }

    public String getAdjustmentNote() {
        return adjustmentNote;
    }

    public void setAdjustmentNote(String adjustmentNote) {
        this.adjustmentNote = adjustmentNote;
    }

    public String getGlobalCustNum() {
        return globalCustNum;
    }

    public void setGlobalCustNum(String globalCustNum) {
        this.globalCustNum = globalCustNum;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    @Override
    public void reset(ActionMapping actionMapping, HttpServletRequest request) {
        this.adjustcheckbox = false;
    }

    @Override
    public ActionErrors validate(ActionMapping actionMapping, HttpServletRequest request) {
        if (null == request.getAttribute(Constants.CURRENTFLOWKEY))
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        ActionErrors actionErrors = new ActionErrors();
        if (null != request.getParameter(CustomerConstants.METHOD)
                && request.getParameter(CustomerConstants.METHOD).equals(CustomerConstants.METHOD_PREVIEW_ADJUSTMENT)) {
            if (!adjustcheckbox) {
                request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
                actionErrors.add("", new ActionMessage(CustomerConstants.ERROR_MANDATORY_CHECKBOX));
            }
            if (adjustmentNote == null || adjustmentNote.trim() == "") {
                request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
                actionErrors.add("", new ActionMessage(CustomerConstants.ERROR_MANDATORY_TEXT_AREA));
            } else if (adjustmentNote.length() > 300) {
                request.setAttribute(CustomerConstants.METHOD, CustomerConstants.METHOD_LOAD_ADJUSTMENT);
                actionErrors.add("", new ActionMessage(CustomerConstants.ERROR_ADJUSTMENT_NOTE_TOO_BIG));
            }
            if (!actionErrors.isEmpty()) {
                return actionErrors;
            }
        }
        return super.validate(actionMapping, request);
    }

}
