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

package org.mifos.application.productdefinition.struts.actionforms;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.security.util.UserContext;
import org.mifos.application.login.util.helpers.LoginConstants;

public class PrdConfActionForm extends BaseActionForm {

    private String productTypeId;

    private String latenessDays;

    private String dormancyDays;

    public String getDormancyDays() {
        return dormancyDays;
    }

    public void setDormancyDays(String dormancyDays) {
        this.dormancyDays = dormancyDays;
    }

    public String getLatenessDays() {
        return latenessDays;
    }

    public void setLatenessDays(String latenessDays) {
        this.latenessDays = latenessDays;
    }

    public String getProductTypeId() {
        return productTypeId;
    }

    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String method = request.getParameter("method");
        UserContext userContext = (UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT);
        Locale locale = userContext.getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.PRODUCT_DEFINITION_UI_RESOURCE_PROPERTYFILE,
                locale);
        String latenessDays = resources.getString("product.latenessDays");
        String dormancyDays = resources.getString("product.dormancyDays");
        if (method.equals(Methods.update.toString())) {
            if (StringUtils.isBlank(getLatenessDays()))
                addError(errors, "latenessDays", ProductDefinitionConstants.ERROR_MANDATORY, latenessDays);
            else if (getIntegerValue(getLatenessDays()) > 32767)
                addError(errors, "latenessDays", ProductDefinitionConstants.ERROR_MAX_DAYS, latenessDays,
                        ProductDefinitionConstants.MAX_DAYS);
            if (StringUtils.isBlank(getDormancyDays()))
                addError(errors, "dormancyDays", ProductDefinitionConstants.ERROR_MANDATORY, dormancyDays);
            else if (getIntegerValue(getDormancyDays()) > 32767)
                addError(errors, "dormancyDays", ProductDefinitionConstants.ERROR_MAX_DAYS, dormancyDays,
                        ProductDefinitionConstants.MAX_DAYS);
        }
        if (!method.equals(Methods.validate.toString()))
            request.setAttribute("methodCalled", method);
        return errors;
    }
}
