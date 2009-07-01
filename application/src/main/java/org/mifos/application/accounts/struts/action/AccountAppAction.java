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

package org.mifos.application.accounts.struts.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.accounts.business.AccountBO;
import org.mifos.application.accounts.business.AccountCustomFieldEntity;
import org.mifos.application.accounts.business.service.AccountBusinessService;
import org.mifos.application.accounts.savings.util.helpers.SavingsConstants;
import org.mifos.application.accounts.util.helpers.AccountConstants;
import org.mifos.application.accounts.util.helpers.WaiveEnum;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.util.helpers.CenterConstants;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class AccountAppAction extends BaseAction {
    private AccountBusinessService accountBusinessService;

    public AccountAppAction() {
        this.accountBusinessService = new AccountBusinessService();
    }

    public AccountAppAction(AccountBusinessService accountBusinessService) {
        this.accountBusinessService = accountBusinessService;
    }

    @Override
    protected BusinessService getService() {
        return getAccountBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("accountAppAction");
        security.allow("removeFees", SecurityConstants.VIEW);
        security.allow("getTrxnHistory", SecurityConstants.VIEW);
        return security;
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward removeFees(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Integer accountId = getIntegerValue(request.getParameter("accountId"));
        Short feeId = getShortValue(request.getParameter("feeId"));
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        AccountBO accountBO = getAccountBusinessService().getAccount(accountId);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        if (accountBO.getPersonnel() != null)
            getAccountBusinessService().checkPermissionForRemoveFees(accountBO.getType(),
                    accountBO.getCustomer().getLevel(), uc, accountBO.getOffice().getOfficeId(),
                    accountBO.getPersonnel().getPersonnelId());
        else
            getAccountBusinessService().checkPermissionForRemoveFees(accountBO.getType(),
                    accountBO.getCustomer().getLevel(), uc, accountBO.getOffice().getOfficeId(), uc.getId());
        accountBO.removeFees(feeId, uc.getId());
        String fromPage = request.getParameter(CenterConstants.FROM_PAGE);
        StringBuilder forward = new StringBuilder();
        forward = forward.append(AccountConstants.REMOVE + "_" + fromPage + "_" + AccountConstants.CHARGES);
        if (fromPage != null) {
            return mapping.findForward(forward.toString());
        } else {
            return mapping.findForward(AccountConstants.REMOVE_SUCCESS);
        }
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getTrxnHistory(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String globalAccountNum = request.getParameter("globalAccountNum");
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        AccountBO accountBO = getAccountBusinessService().findBySystemId(globalAccountNum);
        SessionUtils.setCollectionAttribute(SavingsConstants.TRXN_HISTORY_LIST, getAccountBusinessService()
                .getTrxnHistory(accountBO, uc), request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, accountBO, request);
        return mapping.findForward("getTransactionHistory_success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward waiveChargeDue(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        Integer accountId = getIntegerValue(request.getParameter("accountId"));
        AccountBO account = getAccountBusinessService().getAccount(accountId);
        account.setUserContext(uc);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, account, request);
        WaiveEnum waiveEnum = getWaiveType(request.getParameter(AccountConstants.WAIVE_TYPE));
        if (account.getPersonnel() != null)
            getAccountBusinessService().checkPermissionForWaiveDue(waiveEnum, account.getType(),
                    account.getCustomer().getLevel(), uc, account.getOffice().getOfficeId(),
                    account.getPersonnel().getPersonnelId());
        else
            getAccountBusinessService().checkPermissionForWaiveDue(waiveEnum, account.getType(),
                    account.getCustomer().getLevel(), uc, account.getOffice().getOfficeId(), uc.getId());
        account.waiveAmountDue(waiveEnum);
        return mapping.findForward("waiveChargesDue_Success");
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward waiveChargeOverDue(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        UserContext uc = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        Integer accountId = getIntegerValue(request.getParameter("accountId"));
        AccountBO account = getAccountBusinessService().getAccount(accountId);
        account.setUserContext(uc);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, account, request);
        WaiveEnum waiveEnum = getWaiveType(request.getParameter(AccountConstants.WAIVE_TYPE));
        if (account.getPersonnel() != null)
            getAccountBusinessService().checkPermissionForWaiveDue(waiveEnum, account.getType(),
                    account.getCustomer().getLevel(), uc, account.getOffice().getOfficeId(),
                    account.getPersonnel().getPersonnelId());
        else
            getAccountBusinessService().checkPermissionForWaiveDue(waiveEnum, account.getType(),
                    account.getCustomer().getLevel(), uc, account.getOffice().getOfficeId(), uc.getId());
        account.waiveAmountOverDue(waiveEnum);
        return mapping.findForward("waiveChargesOverDue_Success");
    }

    private WaiveEnum getWaiveType(String waiveType) {
        if (waiveType != null) {
            if (waiveType.equalsIgnoreCase(WaiveEnum.PENALTY.toString())) {
                return WaiveEnum.PENALTY;
            }
            if (waiveType.equalsIgnoreCase(WaiveEnum.FEES.toString())) {
                return WaiveEnum.FEES;
            }
        }
        return WaiveEnum.ALL;
    }

    protected CustomerBO getCustomer(Integer customerId) throws ServiceException {
        return getCustomerBusinessService().getCustomer(customerId);
    }

    protected CustomerBO getCustomerBySystemId(String systemId) throws ServiceException {
        return getCustomerBusinessService().findBySystemId(systemId);
    }

    protected CustomerBusinessService getCustomerBusinessService() {
        return (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
    }

    protected AccountBusinessService getAccountBusinessService() {
        return accountBusinessService;
    }

    protected void convertCustomFieldDateToUniformPattern(List<CustomFieldView> customFields, Locale locale) {
        for (CustomFieldView customField : customFields) {
            if (customField.getFieldType().equals(CustomFieldType.DATE.getValue())
                    && StringUtils.isNullAndEmptySafe(customField.getFieldValue()))
                customField.convertDateToUniformPattern(locale);
        }
    }

    protected List<CustomFieldView> createCustomFieldViewsForEdit(Set<AccountCustomFieldEntity> customFieldEntities,
            HttpServletRequest request) throws ApplicationException {
        List<CustomFieldView> customFields = new ArrayList<CustomFieldView>();

        List<CustomFieldDefinitionEntity> customFieldDefs = (List<CustomFieldDefinitionEntity>) SessionUtils
                .getAttribute(SavingsConstants.CUSTOM_FIELDS, request);
        Locale locale = getUserContext(request).getPreferredLocale();
        for (CustomFieldDefinitionEntity customFieldDef : customFieldDefs) {
            boolean customFieldPresent = false;
            for (AccountCustomFieldEntity customFieldEntity : customFieldEntities) {
                customFieldPresent = true;
                if (customFieldDef.getFieldId().equals(customFieldEntity.getFieldId())) {
                    if (customFieldDef.getFieldType().equals(CustomFieldType.DATE.getValue())) {
                        customFields.add(new CustomFieldView(customFieldEntity.getFieldId(), DateUtils
                                .getUserLocaleDate(locale, customFieldEntity.getFieldValue()), customFieldDef
                                .getFieldType()));
                    } else {
                        customFields.add(new CustomFieldView(customFieldEntity.getFieldId(), customFieldEntity
                                .getFieldValue(), customFieldDef.getFieldType()));
                    }
                }
            }
            if (!customFieldPresent)
                customFields.add(new CustomFieldView(customFieldDef.getFieldId(), customFieldDef.getDefaultValue(),
                        customFieldDef.getFieldType()));
        }
        return customFields;
    }
}
