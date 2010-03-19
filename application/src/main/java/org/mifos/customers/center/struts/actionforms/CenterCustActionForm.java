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

package org.mifos.customers.center.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.center.business.CenterBO;
import org.mifos.customers.center.util.helpers.ValidateMethods;
import org.mifos.customers.struts.actionforms.CustomerActionForm;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;

public class CenterCustActionForm extends CustomerActionForm {

    private String mfiJoiningDateDD;
    private String mfiJoiningDateMM;
    private String mfiJoiningDateYY;

    @Override
    public String getMfiJoiningDate() {
        if (StringUtils.isBlank(mfiJoiningDateDD) || StringUtils.isBlank(mfiJoiningDateMM)
                || StringUtils.isBlank(mfiJoiningDateYY)) {
            return null;
        }

        String dateSeparator = new LocalizationConverter().getDateSeparatorForCurrentLocale();
        return mfiJoiningDateDD + dateSeparator + mfiJoiningDateMM + dateSeparator + mfiJoiningDateYY;
    }

    public void setMfiJoiningDate(int day, int month, int year) {
        setMfiJoiningDate(Integer.toString(day), Integer.toString(month), Integer.toString(year));
    }

    public void setMfiJoiningDate(String day, String month, String year) {
        setMfiJoiningDateDD(day);
        setMfiJoiningDateMM(month);
        setMfiJoiningDateYY(year);
    }

    public void setMfiJoiningDateYY(String mfiJoiningDateYY) {
        this.mfiJoiningDateYY = mfiJoiningDateYY;
    }

    public String getMfiJoiningDateYY() {
        return mfiJoiningDateYY;
    }

    public void setMfiJoiningDateMM(String mfiJoiningDateMM) {
        this.mfiJoiningDateMM = mfiJoiningDateMM;
    }

    public String getMfiJoiningDateMM() {
        return mfiJoiningDateMM;
    }

    public void setMfiJoiningDateDD(String mfiJoiningDateDD) {
        this.mfiJoiningDateDD = mfiJoiningDateDD;
    }

    public String getMfiJoiningDateDD() {
        return mfiJoiningDateDD;
    }

    /*
     * Validation is done in the order that the fields appear on the UI.
     *
     * @see
     * org.mifos.customers.struts.actionforms.CustomerActionForm#
     * validateFields(javax.servlet.http.HttpServletRequest, java.lang.String)
     */
    @Override
    protected ActionErrors validateFields(HttpServletRequest request, String method) throws ApplicationException {
        ActionErrors errors = new ActionErrors();
        if (method.equals(Methods.preview.toString())) {
            validateName(errors);
            validateLO(errors);
            validateMeeting(request, errors);
        } else if (method.equals(Methods.editPreview.toString())) {
            CenterBO center = (CenterBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
            if (center.isActive()) {
                validateLO(errors);
            }
        }

        if (method.equals(Methods.preview.toString()) || method.equals(Methods.editPreview.toString())) {
            validateMfiJoiningDate(request, errors);
            validateConfigurableMandatoryFields(request, errors, EntityType.CENTER);
            validateCustomFieldsForCustomers(request, errors);
        }
        // fees are only editable in preview and come last
        if (method.equals(Methods.preview.toString())) {
            validateFees(request, errors);
        }

        return errors;
    }

    protected void validateMfiJoiningDate(HttpServletRequest request, ActionErrors errors) {
        if (ValidateMethods.isNullOrBlank(getMfiJoiningDate())) {
            errors.add(CustomerConstants.MFI_JOINING_DATE_MANDATORY, new ActionMessage(
                    CustomerConstants.MFI_JOINING_DATE_MANDATORY));
        }

        else {
            if (!DateUtils.isValidDate(getMfiJoiningDate())) {
                errors.add(CustomerConstants.INVALID_MFI_JOINING_DATE, new ActionMessage(
                        CustomerConstants.INVALID_MFI_JOINING_DATE));
            }
        }
    }

    @Override
    protected MeetingBO getCustomerMeeting(HttpServletRequest request) throws ApplicationException {
        return (MeetingBO) SessionUtils.getAttribute(CustomerConstants.CUSTOMER_MEETING, request);
    }
}
