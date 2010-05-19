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

import java.sql.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.commons.lang.StringUtils;
import org.mifos.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerHistoricalDataEntity;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.struts.actionforms.CustHistoricalDataActionForm;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class CustHistoricalDataAction extends BaseAction {

    @Override
    protected BusinessService getService() {
        return getCustomerBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

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
            HttpServletResponse response) throws Exception {
        CustomerBO customerBO = new CustomerBusinessService().findBySystemId(request
                .getParameter(CustomerConstants.GLOBAL_CUST_NUM));
        customerBO.setUserContext(getUserContext(request));
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request);
        setTypeForGet(customerBO, form);
        CustomerHistoricalDataEntity customerHistoricalDataEntity = customerBO.getHistoricalData();
        if (customerHistoricalDataEntity == null) {
            customerHistoricalDataEntity = new CustomerHistoricalDataEntity(customerBO);
        }
        String currentDate = DateUtils.getCurrentDate(getUserContext(request).getPreferredLocale());
        SessionUtils.setAttribute(CustomerConstants.MFIJOININGDATE,
                (customerHistoricalDataEntity.getMfiJoiningDate() == null ? DateUtils.getLocaleDate(getUserContext(
                        request).getPreferredLocale(), currentDate) : new Date(customerHistoricalDataEntity
                        .getMfiJoiningDate().getTime())), request);
        SessionUtils.setAttribute(CustomerConstants.CUSTOMER_HISTORICAL_DATA, customerHistoricalDataEntity, request);
        return mapping.findForward(ActionForwards.getHistoricalData_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadHistoricalData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CustHistoricalDataActionForm historicalActionForm = (CustHistoricalDataActionForm) form;
        CustomerHistoricalDataEntity customerHistoricalDataEntity = (CustomerHistoricalDataEntity) SessionUtils
                .getAttribute(CustomerConstants.CUSTOMER_HISTORICAL_DATA, request);
        setFormAttributes(request, historicalActionForm, customerHistoricalDataEntity);
        historicalActionForm
                .setMfiJoiningDate(getMfiJoiningDate(getUserContext(request).getPreferredLocale(), request));
        return mapping.findForward(ActionForwards.loadHistoricalData_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewHistoricalData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previewHistoricalData_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previousHistoricalData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previousHistoricalData_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updateHistoricalData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CustHistoricalDataActionForm historicalActionForm = (CustHistoricalDataActionForm) form;
        CustomerBO customerBOInSession = (CustomerBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        CustomerBO customerBO = getCustomerBusinessService().getCustomer(customerBOInSession.getCustomerId());
        checkVersionMismatch(customerBOInSession.getVersionNo(), customerBO.getVersionNo());
        customerBO.setVersionNo(customerBOInSession.getVersionNo());
        customerBO.setUserContext(getUserContext(request));
        customerBOInSession = null;
        setInitialObjectForAuditLogging(customerBO);
        CustomerHistoricalDataEntity customerHistoricalDataEntity = customerBO.getHistoricalData();
        if (customerBO.getPersonnel() != null) {
            checkPermissionForAddingHistoricalData(customerBO.getLevel(), getUserContext(request), customerBO
                    .getOffice().getOfficeId(), customerBO.getPersonnel().getPersonnelId());
        } else {
            checkPermissionForAddingHistoricalData(customerBO.getLevel(), getUserContext(request), customerBO
                    .getOffice().getOfficeId(), getUserContext(request).getId());
        }
        // Integer oldLoanCycleNo = 0;
        if (customerHistoricalDataEntity == null) {
            customerHistoricalDataEntity = new CustomerHistoricalDataEntity(customerBO);
            customerHistoricalDataEntity.setCreatedBy(customerBO.getUserContext().getId());
            customerHistoricalDataEntity.setCreatedDate(new DateTimeService().getCurrentJavaDateTime());
        } else {
            // oldLoanCycleNo =
            // customerHistoricalDataEntity.getLoanCycleNumber();
            customerHistoricalDataEntity.setUpdatedDate(new DateTimeService().getCurrentJavaDateTime());
            customerHistoricalDataEntity.setUpdatedBy(customerBO.getUserContext().getId());
        }
        setCustomerHistoricalDataEntity(customerBO, historicalActionForm, customerHistoricalDataEntity);
        customerBO.updateHistoricalData(customerHistoricalDataEntity);
        customerBO.update();
        return mapping.findForward(ActionForwards.updateHistoricalData_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward cancelHistoricalData(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(getDetailAccountPage(form));
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String method = (String) request.getAttribute(SavingsConstants.METHODCALLED);
        String forward = null;
        if (method != null) {
            if (method.equals(Methods.previewHistoricalData.toString())) {
                forward = ActionForwards.previewHistoricalData_failure.toString();
            }
        }
        return mapping.findForward(forward);
    }

    private void setTypeForGet(CustomerBO customerBO, ActionForm form) {
        CustHistoricalDataActionForm actionForm = (CustHistoricalDataActionForm) form;
        if (customerBO instanceof ClientBO) {
            actionForm.setType("Client");
        } else if (customerBO instanceof GroupBO) {
            actionForm.setType("Group");
        }
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

    private void setFormAttributes(HttpServletRequest request, CustHistoricalDataActionForm historicalActionForm,
            CustomerHistoricalDataEntity historicalDataEntity) {
        historicalActionForm.setInterestPaid(getStringValue(historicalDataEntity.getInterestPaid()));
        historicalActionForm.setLoanAmount(getStringValue(historicalDataEntity.getLoanAmount()));
        historicalActionForm.setLoanCycleNumber(getStringValue(historicalDataEntity.getLoanCycleNumber()));
        historicalActionForm.setMissedPaymentsCount(getStringValue(historicalDataEntity.getMissedPaymentsCount()));
        historicalActionForm.setCommentNotes(historicalDataEntity.getNotes());
        historicalActionForm.setProductName(historicalDataEntity.getProductName());
        historicalActionForm.setTotalAmountPaid(getStringValue(historicalDataEntity.getTotalAmountPaid()));
        historicalActionForm.setTotalPaymentsCount(getStringValue(historicalDataEntity.getTotalPaymentsCount()));
    }

    private String getMfiJoiningDate(Locale locale, HttpServletRequest request) throws ApplicationException {
        return DateUtils.getUserLocaleDate(locale, SessionUtils.getAttribute(CustomerConstants.MFIJOININGDATE, request)
                .toString());
    }

    private void setCustomerHistoricalDataEntity(CustomerBO customerBO,
            CustHistoricalDataActionForm historicalActionForm, CustomerHistoricalDataEntity historicalDataEntity) throws InvalidDateException {
        historicalDataEntity.setInterestPaid(StringUtils.isBlank(historicalActionForm.getInterestPaid()) ? null : new Money(Money.getDefaultCurrency(), historicalActionForm.getInterestPaid()));
        historicalDataEntity.setLoanAmount(StringUtils.isBlank(historicalActionForm.getLoanAmount()) ? null : new Money(Money.getDefaultCurrency(), historicalActionForm.getLoanAmount()));
        historicalDataEntity.setLoanCycleNumber(historicalActionForm.getLoanCycleNumberValue());
        historicalDataEntity.setMissedPaymentsCount(historicalActionForm.getMissedPaymentsCountValue());
        historicalDataEntity.setNotes(historicalActionForm.getCommentNotes());
        historicalDataEntity.setProductName(historicalActionForm.getProductName());
        historicalDataEntity.setTotalAmountPaid(StringUtils.isBlank(historicalActionForm.getTotalAmountPaid()) ? null : new Money(Money.getDefaultCurrency(), historicalActionForm.getTotalAmountPaid()));
        historicalDataEntity.setTotalPaymentsCount(historicalActionForm.getTotalPaymentsCountValue());
        historicalDataEntity.setMfiJoiningDate(getDateFromString(historicalActionForm.getMfiJoiningDate(), customerBO
                .getUserContext().getPreferredLocale()));
    }

    private CustomerBusinessService getCustomerBusinessService() {
        return (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
    }

    private void checkPermissionForAddingHistoricalData(CustomerLevel customerLevel, UserContext userContext,
            Short recordOfficeId, Short recordLoanOfficerId) throws ApplicationException {
        if (!isPermissionAllowed(customerLevel, userContext, recordOfficeId, recordLoanOfficerId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    private boolean isPermissionAllowed(CustomerLevel customerLevel, UserContext userContext, Short recordOfficeId,
            Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isAddingHistoricaldataPermittedForCustomers(customerLevel, userContext,
                recordOfficeId, recordLoanOfficerId);
    }
}
