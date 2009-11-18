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

package org.mifos.application.accounts.savings.struts.actionforms;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.util.helpers.LoanConstants;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.struts.actionforms.AccountAppActionForm;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.productdefinition.business.SavingsOfferingBO;
import org.mifos.application.productdefinition.util.helpers.SavingsType;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.FormUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;

public class SavingsActionForm extends AccountAppActionForm {
    private String recommendedAmount;

    public SavingsActionForm() {
        super();
    }

    public String getRecommendedAmount() {
        return recommendedAmount;
    }

    public void setRecommendedAmount(String recommendedAmount) {
        this.recommendedAmount = recommendedAmount;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String method = request.getParameter("method");
        ActionErrors errors = new ActionErrors();
        UserContext userContext = (UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT);
        Locale locale = userContext.getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.SAVING_UI_RESOURCE_PROPERTYFILE, locale);
        String mandatoryAmount = resources.getString("Savings.mandatoryAmountForDeposit");
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));

        if (method.equals("getPrdOfferings") || method.equals("create") || method.equals("edit")
                || method.equals("update") || method.equals("get") || method.equals("validate")) {
        } else {
            errors.add(super.validate(mapping, request));
            if (method.equals("preview") || method.equals("editPreview")) {
                try {
                    SavingsOfferingBO savingsOffering = (SavingsOfferingBO) SessionUtils.getAttribute(
                            SavingsConstants.PRDOFFCERING, request);
                    if (savingsOffering.getSavingsType().getId().equals(SavingsType.MANDATORY.getValue())
                            && getRecommendedAmntValue().equals(new Money())) {
                        // check for mandatory amount
                        errors.add(SavingsConstants.MANDATORY, new ActionMessage(SavingsConstants.MANDATORY,
                                mandatoryAmount));
                    }
                    validateCustomFields(request, errors);
                } catch (PageExpiredException e) {
                    errors.add(SavingsConstants.MANDATORY, new ActionMessage(SavingsConstants.MANDATORY,
                            mandatoryAmount));
                }
            }
        }

        if (!errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", method);
        }
        return errors;
    }

    public double getRecommendedAmntDoubleValue() {
        return getRecommendedAmntValue().getAmountDoubleValue();
    }

    public Money getRecommendedAmntValue() {
        return FormUtils.getMoney(recommendedAmount);
    }

    public void clear() {
        this.setAccountId(null);
        this.setSelectedPrdOfferingId(null);
        this.setAccountCustomFieldSet(new ArrayList<CustomFieldView>());
    }

    private void validateCustomFields(HttpServletRequest request, ActionErrors errors) {
        try {
            List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                    .getAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, request);
            for (CustomFieldView customField : getAccountCustomFieldSet()) {
                boolean isErrorFound = false;
                for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
                    if (customField.getFieldId().equals(customFieldDef.getFieldId()) && customFieldDef.isMandatory()) {
                        if (StringUtils.isBlank(customField.getFieldValue())) {
                            errors.add(LoanConstants.CUSTOM_FIELDS, new ActionMessage(
                                    LoanConstants.ERRORS_SPECIFY_CUSTOM_FIELD_VALUE));
                            isErrorFound = true;
                            break;
                        }
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
}
