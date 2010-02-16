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

package org.mifos.customers.office.struts.actionforms;

import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.customers.office.business.OfficeBO;
import org.mifos.customers.office.exceptions.OfficeException;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;

public class OffActionForm extends BaseActionForm {

    private String globalOfficeNum;

    private String input;

    private ResourceBundle resourceBundle = null;

    private String officeId;

    private String officeName;

    private String shortName;

    private String officeLevel;

    private String parentOfficeId;

    private String officeStatus;

    private Address address;

    private List<CustomFieldView> customFields;

    public OffActionForm() {
        this.customFields = new ArrayList<CustomFieldView>();
        this.address = new Address();

    }

    public List<CustomFieldView> getCustomFields() {
        return customFields;
    }

    public void setCustomFields(List<CustomFieldView> customFields) {
        this.customFields = customFields;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getOfficeId() {
        return officeId;
    }

    public void setOfficeId(String officeId) {
        this.officeId = officeId;
    }

    public String getOfficeLevel() {
        return officeLevel;
    }

    public void setOfficeLevel(String officeLevel) {
        this.officeLevel = officeLevel;
    }

    public String getOfficeName() {
        return officeName;
    }

    public void setOfficeName(String officeName) {
        this.officeName = officeName;
    }

    public String getOfficeStatus() {
        return officeStatus;
    }

    public void setOfficeStatus(String officeStatus) {
        this.officeStatus = officeStatus;
    }

    public String getParentOfficeId() {
        return parentOfficeId;
    }

    public void setParentOfficeId(String parentOfficeId) {
        this.parentOfficeId = parentOfficeId;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public CustomFieldView getCustomField(int i) {
        return getCustomField(customFields, i);
    }

    public void clear() {
        this.officeId = "";
        this.officeLevel = "";
        this.officeName = "";
        this.officeStatus = "";
        this.shortName = "";
        this.parentOfficeId = "";
        this.address = new Address();

    }

    private void verifyFields(ActionErrors actionErrors, UserContext userContext) {
        if (StringUtils.isBlank(officeName))
            actionErrors.add(OfficeConstants.OFFICE_NAME, new ActionMessage(OfficeConstants.ERRORMANDATORYFIELD,
                    getLocaleString(OfficeConstants.OFFICE_NAME, userContext)));
        if (StringUtils.isBlank(shortName))
            actionErrors.add(OfficeConstants.OFFICESHORTNAME, new ActionMessage(OfficeConstants.ERRORMANDATORYFIELD,
                    getLocaleString(OfficeConstants.OFFICESHORTNAME, userContext)));
        if (StringUtils.isBlank(officeLevel))
            actionErrors.add(OfficeConstants.OFFICETYPE, new ActionMessage(OfficeConstants.ERRORMANDATORYFIELD,
                    getLocaleString(OfficeConstants.OFFICETYPE, userContext)));
        if (StringUtils.isNotBlank(officeLevel) && officeLevel.equals("1"))
            ;
        else {

            if (StringUtils.isBlank(parentOfficeId))
                actionErrors.add(OfficeConstants.PARENTOFFICE, new ActionMessage(OfficeConstants.ERRORMANDATORYFIELD,
                        getLocaleString(OfficeConstants.PARENTOFFICE, userContext)));
        }
    }

    private String getLocaleString(String key, UserContext userContext) {

        if (resourceBundle == null)
            try {

                resourceBundle = ResourceBundle.getBundle(FilePaths.OFFICERESOURCEPATH, userContext
                        .getPreferredLocale());
            } catch (MissingResourceException e) {

                resourceBundle = ResourceBundle.getBundle(FilePaths.OFFICERESOURCEPATH, userContext
                        .getPreferredLocale());

            }
        return resourceBundle.getString(key);

    }

    @Override
    protected UserContext getUserContext(HttpServletRequest request) {
        return (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
    }

    protected void validateCustomFields(HttpServletRequest request, ActionErrors errors) {
        try {
            List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                    .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
            for (CustomFieldView customField : customFields) {
                boolean isErrorFound = false;
                for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
                    if (customField.getFieldId().equals(customFieldDef.getFieldId()) && customFieldDef.isMandatory())
                        if (StringUtils.isBlank(customField.getFieldValue())) {
                            errors.add(CustomerConstants.CUSTOM_FIELD, new ActionMessage(
                                    OfficeConstants.ENTERADDTIONALINFO));
                            isErrorFound = true;
                            break;
                        }
                }
                if (isErrorFound)
                    break;
            }
        } catch (PageExpiredException pee) {
            errors.add(ExceptionConstants.PAGEEXPIREDEXCEPTION, new ActionMessage(
                    ExceptionConstants.PAGEEXPIREDEXCEPTION));
        }
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String method = request.getParameter("method");

        if (null != request.getParameter(Constants.CURRENTFLOWKEY)
                && null == request.getAttribute(Constants.CURRENTFLOWKEY))
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));

        if (method.equals(Methods.preview.toString()) || method.equals(Methods.editpreview.toString())) {
            verifyFields(errors, getUserContext(request));
            validateCustomFields(request, errors);
            checkForMandatoryFields(EntityType.OFFICE.getValue(), errors, request);
            errors.add(super.validate(mapping, request));
        }
        if (null != errors && !errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);
        }

        return errors;

    }

    public void populate(OfficeBO officeBO) throws OfficeException {

        this.officeId = officeBO.getOfficeId().toString();
        this.officeLevel = officeBO.getOfficeLevel().getValue().toString();
        this.officeStatus = officeBO.getOfficeStatus().getValue().toString();
        this.officeName = officeBO.getOfficeName();
        this.shortName = officeBO.getShortName();
        if (officeBO.getAddress() != null && officeBO.getAddress().getAddress() != null) {
            this.address.setLine1(officeBO.getAddress().getAddress().getLine1());
            this.address.setLine2(officeBO.getAddress().getAddress().getLine2());
            this.address.setLine3(officeBO.getAddress().getAddress().getLine3());
            this.address.setPhoneNumber(officeBO.getAddress().getAddress().getPhoneNumber());
            this.address.setCity(officeBO.getAddress().getAddress().getCity());
            this.address.setState(officeBO.getAddress().getAddress().getState());
            this.address.setCountry(officeBO.getAddress().getAddress().getCountry());
            this.address.setZip(officeBO.getAddress().getAddress().getZip());
        } else {
            this.address = new Address();
        }
        if (officeBO.getParentOffice() != null)
            this.parentOfficeId = officeBO.getParentOffice().getOfficeId().toString();
        else
            this.parentOfficeId = null;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getGlobalOfficeNum() {
        return globalOfficeNum;
    }

    public void setGlobalOfficeNum(String globalOfficeNum) {
        this.globalOfficeNum = globalOfficeNum;
    }
}
