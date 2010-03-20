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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountBO;
import org.mifos.accounts.business.AccountFlagMapping;
import org.mifos.accounts.fees.business.FeeBO;
import org.mifos.accounts.fees.business.FeeView;
import org.mifos.accounts.fees.business.service.FeeBusinessService;
import org.mifos.accounts.fees.util.helpers.FeeCategory;
import org.mifos.accounts.util.helpers.AccountConstants;
import org.mifos.accounts.util.helpers.AccountTypes;
import org.mifos.application.master.business.CustomFieldDefinitionEntity;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerPositionEntity;
import org.mifos.customers.business.CustomerPositionView;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.personnel.business.PersonnelView;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.struts.actionforms.CustActionForm;
import org.mifos.customers.struts.actionforms.CustomerActionForm;
import org.mifos.customers.util.helpers.ChildrenStateType;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.customers.util.helpers.CustomerLevel;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.struts.action.SearchAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.ActivityMapper;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class CustAction extends SearchAction {
    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.CUSTOMERLOGGER);

    @Override
    protected BusinessService getService() {
        return getCustomerBusinessService();
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("custAction");
        security.allow("getClosedAccounts", SecurityConstants.VIEW);
        security.allow("getBackToDetailsPage", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getClosedAccounts(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("In CustAction::getClosedAccounts()");
        Integer customerId = getIntegerValue(((CustActionForm) form).getCustomerId());
        CustomerBusinessService customerService = getCustomerBusinessService();
        List<AccountBO> loanAccountsList = customerService.getAllClosedAccount(customerId, AccountTypes.LOAN_ACCOUNT
                .getValue());
        List<AccountBO> savingsAccountList = customerService.getAllClosedAccount(customerId,
                AccountTypes.SAVINGS_ACCOUNT.getValue());
        for (AccountBO savingsBO : savingsAccountList) {
            setLocaleIdForToRetrieveMasterDataName(savingsBO, request);
        }
        for (AccountBO loanBO : loanAccountsList) {
            setLocaleIdForToRetrieveMasterDataName(loanBO, request);
        }
        SessionUtils.setCollectionAttribute(AccountConstants.CLOSEDLOANACCOUNTSLIST, loanAccountsList, request);
        SessionUtils.setCollectionAttribute(AccountConstants.CLOSEDSAVINGSACCOUNTSLIST, savingsAccountList, request);
        return mapping.findForward(ActionForwards.getAllClosedAccounts.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward getBackToDetailsPage(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(getCustomerDetailPage(((CustActionForm) form).getInput()));
    }

    protected void loadCustomFieldDefinitions(EntityType entityType, HttpServletRequest request)
            throws ApplicationException {
        MasterDataService masterDataService = (MasterDataService) ServiceFactory.getInstance().getBusinessService(
                BusinessServiceName.MasterDataService);
        List<CustomFieldDefinitionEntity> customFieldDefs = masterDataService
                .retrieveCustomFieldsDefinition(entityType);
        SessionUtils.setCollectionAttribute(CustomerConstants.CUSTOM_FIELDS_LIST, customFieldDefs, request);
    }

    protected void loadFees(CustomerActionForm actionForm, HttpServletRequest request, FeeCategory feeCategory,
            MeetingBO meeting) throws ApplicationException {
        FeeBusinessService feeService = new FeeBusinessService();
        List<FeeBO> fees = feeService.retrieveCustomerFeesByCategaroyType(feeCategory);
        if (meeting != null) {
            fees = removeMismatchPeriodicFee(fees, meeting);
        }
        List<FeeView> additionalFees = new ArrayList<FeeView>();
        List<FeeView> defaultFees = new ArrayList<FeeView>();
        for (FeeBO fee : fees) {
            if (fee.isCustomerDefaultFee()) {
                defaultFees.add(new FeeView(getUserContext(request), fee));
            } else {
                additionalFees.add(new FeeView(getUserContext(request), fee));
            }
        }
        actionForm.setDefaultFees(defaultFees);
        SessionUtils.setCollectionAttribute(CustomerConstants.ADDITIONAL_FEES_LIST, additionalFees, request);
    }

    private List<FeeBO> removeMismatchPeriodicFee(List<FeeBO> feeList, MeetingBO meeting) {
        List<FeeBO> fees = new ArrayList<FeeBO>();
        for (FeeBO fee : feeList) {
            if (fee.isOneTime() || (fee.isPeriodic() && isFrequencyMatches(fee, meeting))) {
                fees.add(fee);
            }
        }
        return fees;
    }

    private boolean isFrequencyMatches(FeeBO fee, MeetingBO meeting) {
        return (fee.getFeeFrequency().getFeeMeetingFrequency().isMonthly() && meeting.isMonthly())
                || (fee.getFeeFrequency().getFeeMeetingFrequency().isWeekly() && meeting.isWeekly());
    }

    protected void loadLoanOfficers(Short officeId, HttpServletRequest request) throws ApplicationException {
        PersonnelBusinessService personnelService = new PersonnelBusinessService();

        UserContext userContext = getUserContext(request);
        List<PersonnelView> personnelList = personnelService.getActiveLoanOfficersInBranch(officeId, userContext
                .getId(), userContext.getLevelId());
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, personnelList, request);
    }

    protected void convertCustomFieldDateToUniformPattern(List<CustomFieldView> customFields, Locale locale) throws InvalidDateException {
        for (CustomFieldView customField : customFields) {
            if (customField.getFieldType().equals(CustomFieldType.DATE.getValue())
                    && StringUtils.isNotBlank(customField.getFieldValue())) {
                customField.convertDateToUniformPattern(locale);
            }
        }
    }

    protected void checkPermissionForCreate(Short newState, UserContext userContext, Short flagSelected,
            Short recordOfficeId, Short recordLoanOfficerId) throws ApplicationException {
        if (!isPermissionAllowed(newState, userContext, flagSelected, recordOfficeId, recordLoanOfficerId)) {
            throw new CustomerException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
        }
    }

    protected boolean isPermissionAllowed(Short newState, UserContext userContext, Short flagSelected,
            Short recordOfficeId, Short recordLoanOfficerId) {
        return ActivityMapper.getInstance().isSavePermittedForCustomer(newState.shortValue(), userContext,
                recordOfficeId, recordLoanOfficerId);
    }

    protected CustomerBusinessService getCustomerBusinessService() {
        return new CustomerBusinessService();
    }

    private String getCustomerDetailPage(String input) {
        String forward = null;
        if (input.equals("center")) {
            forward = ActionForwards.center_detail_page.toString();
        } else if (input.equals("group")) {
            forward = ActionForwards.group_detail_page.toString();
        } else if (input.equals("client")) {
            forward = ActionForwards.client_detail_page.toString();
        }
        return forward;
    }

    private void setLocaleIdForToRetrieveMasterDataName(AccountBO accountBO, HttpServletRequest request) {
        accountBO.getAccountState().setLocaleId(getUserContext(request).getLocaleId());
        for (AccountFlagMapping accountFlagMapping : accountBO.getAccountFlags()) {
            accountFlagMapping.getFlag().setLocaleId(getUserContext(request).getLocaleId());
        }
    }
}
