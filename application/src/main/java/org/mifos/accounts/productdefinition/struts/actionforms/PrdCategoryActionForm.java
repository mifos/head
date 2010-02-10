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

package org.mifos.accounts.productdefinition.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;

public class PrdCategoryActionForm extends BaseActionForm {

    private String productType;
    private String productCategoryName;
    private String productCategoryDesc;
    private String productCategoryStatus;
    private String globalPrdCategoryNum;

    public String getGlobalPrdCategoryNum() {
        return globalPrdCategoryNum;
    }

    public void setGlobalPrdCategoryNum(String globalPrdCategoryNum) {
        this.globalPrdCategoryNum = globalPrdCategoryNum;
    }

    public String getProductCategoryDesc() {
        return productCategoryDesc;
    }

    public void setProductCategoryDesc(String productCategoryDesc) {
        this.productCategoryDesc = productCategoryDesc;
    }

    public String getProductCategoryName() {
        return productCategoryName;
    }

    public void setProductCategoryName(String productCategoryName) {
        this.productCategoryName = productCategoryName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getProductCategoryStatus() {
        return productCategoryStatus;
    }

    public void setProductCategoryStatus(String productCategoryStatus) {
        this.productCategoryStatus = productCategoryStatus;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        ActionErrors errors = new ActionErrors();
        String methodCalled = request.getParameter(Methods.method.toString());
        if (null != methodCalled) {
            if (Methods.createPreview.toString().equals(methodCalled))
                errors.add(super.validate(mapping, request));
            else if (Methods.managePreview.toString().equals(methodCalled))
                errors.add(super.validate(mapping, request));
        }
        if (null != errors && !errors.isEmpty()) {
            request.setAttribute(Globals.ERROR_KEY, errors);
            request.setAttribute("methodCalled", methodCalled);
        }
        return errors;
    }

}
