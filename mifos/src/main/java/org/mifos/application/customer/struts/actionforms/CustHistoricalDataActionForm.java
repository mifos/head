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
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.struts.actionforms.BaseActionForm;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.FilePaths;

public class CustHistoricalDataActionForm extends BaseActionForm {

    private String mfiJoiningDate;

    private String productName;

    private String loanAmount;

    private String totalAmountPaid;

    private String interestPaid;

    private String missedPaymentsCount;

    private String totalPaymentsCount;

    private String commentNotes;

    private String loanCycleNumber;

    private String type;

    public String getCommentNotes() {
        return commentNotes;
    }

    public void setCommentNotes(String commentNotes) {
        this.commentNotes = commentNotes;
    }

    public String getInterestPaid() {
        return interestPaid;
    }

    public Money getInterestPaidValue() {
        return getMoney(getInterestPaid());
    }

    public void setInterestPaid(String interestPaid) {
        this.interestPaid = interestPaid;
    }

    public String getLoanAmount() {
        return loanAmount;
    }

    public Money getLoanAmountValue() {
        return getMoney(getLoanAmount());
    }

    public void setLoanAmount(String loanAmount) {
        this.loanAmount = loanAmount;
    }

    public String getLoanCycleNumber() {
        return loanCycleNumber;
    }

    public Integer getLoanCycleNumberValue() {
        return getIntegerValue(getLoanCycleNumber());
    }

    public void setLoanCycleNumber(String loanCycleNumber) {
        this.loanCycleNumber = loanCycleNumber;
    }

    public String getMfiJoiningDate() {
        return mfiJoiningDate;
    }

    public void setMfiJoiningDate(String mfiJoiningDate) {
        this.mfiJoiningDate = mfiJoiningDate;
    }

    public String getMissedPaymentsCount() {
        return missedPaymentsCount;
    }

    public Integer getMissedPaymentsCountValue() {
        return getIntegerValue(getMissedPaymentsCount());
    }

    public void setMissedPaymentsCount(String missedPaymentsCount) {
        this.missedPaymentsCount = missedPaymentsCount;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getTotalAmountPaid() {
        return totalAmountPaid;
    }

    public Money getTotalAmountPaidValue() {
        return getMoney(getTotalAmountPaid());
    }

    public void setTotalAmountPaid(String totalAmountPaid) {
        this.totalAmountPaid = totalAmountPaid;
    }

    public String getTotalPaymentsCount() {
        return totalPaymentsCount;
    }

    public Integer getTotalPaymentsCountValue() {
        return getIntegerValue(getTotalPaymentsCount());
    }

    public void setTotalPaymentsCount(String totalPaymentsCount) {
        this.totalPaymentsCount = totalPaymentsCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public ActionErrors validate(ActionMapping mapping, HttpServletRequest request) {
        String methodCalled = request.getParameter(Methods.method.toString());
        ActionErrors errors = new ActionErrors();
        if (null != methodCalled) {
            if (Methods.previewHistoricalData.toString().equals(methodCalled)) {
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
        UserContext userContext = (UserContext) request.getSession().getAttribute(LoginConstants.USERCONTEXT);
        Locale locale = userContext.getPreferredLocale();
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.CUSTOMER_UI_RESOURCE_PROPERTYFILE, locale);
        if (StringUtils.isNullAndEmptySafe(getCommentNotes())) {
            if (getCommentNotes().length() > CustomerConstants.HISTORICALDATA_COMMENT_LENGTH) {
                errors.add(CustomerConstants.MAXIMUM_LENGTH, new ActionMessage(CustomerConstants.MAXIMUM_LENGTH,
                        resources.getString("Customer.notes"), CustomerConstants.HISTORICALDATA_COMMENT_LENGTH));
            }
        }
        return errors;
    }
}
