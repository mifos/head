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

package org.mifos.customers.struts.action;

import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerHistoricalDataEntity;
import org.mifos.customers.struts.actionforms.CustHistoricalDataActionForm;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.CustomerHistoricalDataUpdateRequest;
import org.mifos.dto.screen.CustomerHistoricalDataDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class CustHistoricalDataAction extends BaseAction {

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("custHistoricalDataAction");
        security.allow("loadHistoricalData", SecurityConstants.VIEW);
        security.allow("getHistoricalData", SecurityConstants.VIEW);
        security.allow("previewHistoricalData", SecurityConstants.VIEW);
        security.allow("previousHistoricalData", SecurityConstants.VIEW);
        security.allow("updateHistoricalData", SecurityConstants.VIEW);
        security.allow("cancelHistoricalData", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward getHistoricalData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        CustHistoricalDataActionForm actionForm = (CustHistoricalDataActionForm) form;
        String globalCustNum = request.getParameter(CustomerConstants.GLOBAL_CUST_NUM);

        CustomerHistoricalDataDto historicalData = this.groupServiceFacade.retrieveCustomerHistoricalData(globalCustNum);

        CustomerBO customer = this.customerDao.findCustomerBySystemId(globalCustNum);
        UserContext userContext = getUserContext(request);
        customer.updateDetails(userContext);

        if (historicalData.isClient()) {
            actionForm.setType("Client");
        } else if (historicalData.isGroup()) {
            actionForm.setType("Group");
        }

        CustomerHistoricalDataEntity customerHistoricalDataEntity = customer.getHistoricalData();
        if (customerHistoricalDataEntity == null) {
            customerHistoricalDataEntity = new CustomerHistoricalDataEntity(customer);
        }

        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customer, request);
        SessionUtils.setAttribute(CustomerConstants.MFIJOININGDATE, historicalData.getMfiJoiningDate(), request);
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_HISTORICAL_DATA, customerHistoricalDataEntity, request);
        return mapping.findForward(ActionForwards.getHistoricalData_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadHistoricalData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustHistoricalDataActionForm historicalActionForm = (CustHistoricalDataActionForm) form;
        UserContext userContext = getUserContext(request);
        CustomerHistoricalDataEntity customerHistoricalDataEntity = (CustomerHistoricalDataEntity) SessionUtils
                .getAttribute(CustomerConstants.CUSTOMER_HISTORICAL_DATA, request);

        historicalActionForm.setInterestPaid(getStringValue(customerHistoricalDataEntity.getInterestPaid()));
        historicalActionForm.setLoanAmount(getStringValue(customerHistoricalDataEntity.getLoanAmount()));
        historicalActionForm.setLoanCycleNumber(getStringValue(customerHistoricalDataEntity.getLoanCycleNumber()));
        historicalActionForm.setMissedPaymentsCount(getStringValue(customerHistoricalDataEntity.getMissedPaymentsCount()));
        historicalActionForm.setCommentNotes(customerHistoricalDataEntity.getNotes());
        historicalActionForm.setProductName(customerHistoricalDataEntity.getProductName());
        historicalActionForm.setTotalAmountPaid(getStringValue(customerHistoricalDataEntity.getTotalAmountPaid()));
        historicalActionForm.setTotalPaymentsCount(getStringValue(customerHistoricalDataEntity.getTotalPaymentsCount()));

        String mfiJoiningDate = SessionUtils.getAttribute(CustomerConstants.MFIJOININGDATE, request).toString();
        historicalActionForm.setMfiJoiningDate(DateUtils.getUserLocaleDate(userContext.getPreferredLocale(), mfiJoiningDate));
        return mapping.findForward(ActionForwards.loadHistoricalData_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewHistoricalData(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previewHistoricalData_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previousHistoricalData(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previousHistoricalData_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updateHistoricalData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CustomerBO customerBOInSession = (CustomerBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        CustomerBO customerBO = this.customerDao.findCustomerById(customerBOInSession.getCustomerId());
        checkVersionMismatch(customerBOInSession.getVersionNo(), customerBO.getVersionNo());
        customerBO.setUserContext(getUserContext(request));


        CustHistoricalDataActionForm historicalActionForm = (CustHistoricalDataActionForm) form;
        CustomerHistoricalDataUpdateRequest historicalData = createHistoricalDataUpdateRequest(historicalActionForm, getUserContext(request));
        this.groupServiceFacade.updateCustomerHistoricalData(customerBO.getGlobalCustNum(), historicalData);

        return mapping.findForward(ActionForwards.updateHistoricalData_success.toString());
    }

    private CustomerHistoricalDataUpdateRequest createHistoricalDataUpdateRequest(CustHistoricalDataActionForm historicalActionForm, UserContext userContext) throws InvalidDateException {
        String interestPaid = historicalActionForm.getInterestPaid();
        String loanAmount = historicalActionForm.getLoanAmount();
        Integer loanCycleNumber = historicalActionForm.getLoanCycleNumberValue();
        Integer missedPaymentsCount = historicalActionForm.getMissedPaymentsCountValue();
        String notes = historicalActionForm.getCommentNotes();
        String productName = historicalActionForm.getProductName();
        String totalAmountPaid = historicalActionForm.getTotalAmountPaid();
        Integer totalPaymentsCount = historicalActionForm.getTotalPaymentsCountValue();
        Date mfiJoiningDate = getDateFromString(historicalActionForm.getMfiJoiningDate(), userContext.getPreferredLocale());

        return new CustomerHistoricalDataUpdateRequest(mfiJoiningDate, interestPaid, loanAmount, loanCycleNumber,
                missedPaymentsCount, notes, productName, totalAmountPaid, totalPaymentsCount);
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward cancelHistoricalData(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(getDetailAccountPage(form));
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
        String forward = null;
        if (method != null) {
            if (method.equals(Methods.previewHistoricalData.toString())) {
                forward = ActionForwards.previewHistoricalData_failure.toString();
            }
        }
        return mapping.findForward(forward);
    }

    private String getDetailAccountPage(ActionForm form) {
        CustHistoricalDataActionForm actionForm = (CustHistoricalDataActionForm) form;
        String type = actionForm.getType();
        String forward = null;
        if (type.equals("Group")) {
            forward = ActionForwards.group_detail_page.toString();
        } else if (type.equals("Client")) {
            forward = ActionForwards.client_detail_page.toString();
        }
        return forward;
    }
}