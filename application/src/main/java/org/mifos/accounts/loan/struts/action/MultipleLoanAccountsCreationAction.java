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

package org.mifos.accounts.loan.struts.action;

import static org.mifos.framework.util.CollectionUtils.collect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.exceptions.AccountException;
import org.mifos.accounts.loan.business.service.LoanBusinessService;
import org.mifos.accounts.loan.business.service.LoanDto;
import org.mifos.accounts.loan.business.service.LoanService;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.loan.struts.actionforms.MultipleLoanAccountsCreationActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.MultipleLoanCreationViewHelper;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerLevelEntity;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.fees.business.service.FeeBusinessService;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.ClientRules;
import org.mifos.config.ProcessFlowRules;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.ActivityMapper;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.helpers.Transformer;

public class MultipleLoanAccountsCreationAction extends BaseAction {

    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.ACCOUNTSLOGGER);
    private LoanPrdBusinessService loanPrdBusinessService;
    private ClientBusinessService clientBusinessService;
    private LoanProductService loanProductService;
    private LoanService loanService;

    public MultipleLoanAccountsCreationAction() {
        loanPrdBusinessService = new LoanPrdBusinessService();
        clientBusinessService = new ClientBusinessService();
        loanProductService = new LoanProductService(loanPrdBusinessService, new FeeBusinessService());
        loanService = new LoanService(loanProductService, new LoanDao());
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    @Override
    protected BusinessService getService() {
        return new LoanBusinessService();
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("multipleloansaction");
        security.allow("load", SecurityConstants.CAN_CREATE_MULTIPLE_LOAN_ACCOUNTS);
        security.allow("getLoanOfficers", SecurityConstants.VIEW);
        security.allow("getCenters", SecurityConstants.VIEW);
        security.allow("getPrdOfferings", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        return security;
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside load method");
        List<OfficeView> activeBranches = new MasterDataService().getActiveBranches(getUserContext(request)
                .getBranchId());
        SessionUtils.setCollectionAttribute(LoanConstants.MULTIPLE_LOANS_OFFICES_LIST, activeBranches, request);

        SessionUtils.setAttribute(LoanConstants.IS_CENTER_HIERARCHY_EXISTS,
                ClientRules.getCenterHierarchyExists() ? Constants.YES : Constants.NO, request);
        request.getSession().setAttribute(LoanConstants.MULTIPLE_LOANS_ACTION_FORM, null);
        request.getSession().setAttribute(Constants.BUSINESS_KEY, null);
        logger.debug("outside load method");
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getLoanOfficers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside getLoanOfficers method");
        UserContext userContext = getUserContext(request);
        List<PersonnelView> loanOfficers = ((MasterDataService) ServiceFactory.getInstance().getBusinessService(
                BusinessServiceName.MasterDataService)).getListOfActiveLoanOfficers(PersonnelLevel.LOAN_OFFICER
                .getValue(), getShortValue(((MultipleLoanAccountsCreationActionForm) form).getBranchOfficeId()),
                userContext.getId(), userContext.getLevelId());
        SessionUtils.setCollectionAttribute(LoanConstants.MULTIPLE_LOANS_LOAN_OFFICERS_LIST, loanOfficers, request);
        logger.debug("outside getLoanOfficers method");
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getCenters(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside getCenters method");
        MultipleLoanAccountsCreationActionForm loanActionForm = (MultipleLoanAccountsCreationActionForm) form;
        Short loanOfficerId = getShortValue(loanActionForm.getLoanOfficerId());
        Short officeId = getShortValue(loanActionForm.getBranchOfficeId());
        List<CustomerView> parentCustomerList = loadCustomers(loanOfficerId, officeId);

        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();

        SessionUtils.setCollectionAttribute(LoanConstants.MULTIPLE_LOANS_CENTERS_LIST, parentCustomerList, request);
        SessionUtils.setAttribute(LoanConstants.IS_CENTER_HIERARCHY_EXISTS, isCenterHierarchyExists ? Constants.YES
                : Constants.NO, request);
        logger.debug("Inside getCenters method");
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getPrdOfferings(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside getPrdOfferings method");

        MultipleLoanAccountsCreationActionForm loanActionForm = (MultipleLoanAccountsCreationActionForm) form;
        CustomerBO customer = new CustomerBusinessService().getCustomer(getIntegerValue(loanActionForm.getCenterId()));

        // FIXME remove next two lines, doesn't make sense to me
        customer.getOffice().getOfficeId();
        customer.getPersonnel().getPersonnelId();

        loanActionForm.setCenterSearchId(customer.getSearchId());
        List<LoanOfferingBO> loanOfferings = loanPrdBusinessService.getApplicablePrdOfferings(new CustomerLevelEntity(
                CustomerLevel.CLIENT));
        removePrdOfferingsNotMachingCustomerMeeting(loanOfferings, customer);
        SessionUtils.setCollectionAttribute(LoanConstants.LOANPRDOFFERINGS, loanOfferings, request);
        logger.debug("outside getPrdOfferings method");
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside get method");
        MultipleLoanAccountsCreationActionForm loanActionForm = (MultipleLoanAccountsCreationActionForm) form;
        List<ClientBO> clients = clientBusinessService.getActiveClientsUnderParent(loanActionForm.getCenterSearchId(),
                getShortValue(loanActionForm.getBranchOfficeId()));
        if (clients == null || clients.isEmpty())
            throw new ApplicationException(LoanConstants.NOSEARCHRESULTS);
        LoanOfferingBO loanOffering = loanPrdBusinessService.getLoanOffering(getShortValue(loanActionForm
                .getPrdOfferingId()), getUserContext(request).getLocaleId());
        loanActionForm.setClientDetails(buildClientViewHelper(loanOffering, clients));
        SessionUtils.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES, new MasterDataService()
                .retrieveMasterEntities(MasterConstants.LOAN_PURPOSES, getUserContext(request).getLocaleId()), request);
        SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering, request);
        SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, ProcessFlowRules
                .isLoanPendingApprovalStateEnabled(), request);
        logger.debug("outside get method");
        return mapping.findForward(ActionForwards.get_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside validate method");
        ActionForwards actionForward = ActionForwards.load_success;
        String method = (String) request.getAttribute("methodCalled");
        if (method != null) {
            if (method.equals(Methods.getPrdOfferings.toString()) || method.equals(Methods.load.toString())
                    || method.equals(Methods.get.toString())) {
                actionForward = ActionForwards.load_success;
            } else if (method.equals(Methods.create.toString())) {
                actionForward = ActionForwards.get_success;
            }
        }
        logger.debug("outside validata method");
        return mapping.findForward(actionForward.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("Inside create method");
        MultipleLoanAccountsCreationActionForm loanActionForm = (MultipleLoanAccountsCreationActionForm) form;
        Integer centerId = getIntegerValue(loanActionForm.getCenterId());
        Short loanProductId = getShortValue(loanActionForm.getPrdOfferingId());
        Short accountStateId = getShortValue(loanActionForm.getStateSelected());
        AccountState accountState = AccountState.fromShort(accountStateId);
        UserContext userContext = getUserContext(request);

        List<MultipleLoanCreationViewHelper> applicableClientDetails = loanActionForm.getApplicableClientDetails();

        List<String> accountNumbers = new ArrayList<String>();

        if (applicableClientDetails != null) {
            for (MultipleLoanCreationViewHelper clientDetail : applicableClientDetails) {
                LoanDto loanDto = loanService.createLoan(userContext, centerId, loanProductId, clientDetail
                        .getClientId(), accountState, clientDetail.getLoanAmount(), clientDetail
                        .getDefaultNoOfInstall(), clientDetail.getMaxLoanAmount(), clientDetail.getMinLoanAmount(),
                        clientDetail.getMaxNoOfInstall(), clientDetail.getMinNoOfInstall(),
                        getIntegerValue(clientDetail.getBusinessActivity()));
                accountNumbers.add(loanDto.getGlobalAccountNumber());
            }
        }
        request.setAttribute(LoanConstants.ACCOUNTS_LIST, accountNumbers);
        logger.debug("outside create method");
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("cancel method called");
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    private List<CustomerView> loadCustomers(Short loanOfficerId, Short officeId) throws Exception {
        logger.debug("Inside loadCustomers method");
        CustomerLevel customerLevel = CustomerLevel.CENTER;
        if (!ClientRules.getCenterHierarchyExists())
            customerLevel = CustomerLevel.GROUP;
        List<CustomerView> activeParentsUnderLoanOfficer = ((MasterDataService) ServiceFactory.getInstance()
                .getBusinessService(BusinessServiceName.MasterDataService)).getListOfActiveParentsUnderLoanOfficer(
                loanOfficerId, customerLevel.getValue(), officeId);
        logger.debug("oouside loadCustomers method");
        return activeParentsUnderLoanOfficer;
    }

    private void removePrdOfferingsNotMachingCustomerMeeting(List<LoanOfferingBO> loanOfferings, CustomerBO customer) {
        logger.debug("Inside removePrdOfferingsNotMachingCustomerMeeting method");
        MeetingBO customerMeeting = customer.getCustomerMeeting().getMeeting();
        for (Iterator<LoanOfferingBO> iter = loanOfferings.iterator(); iter.hasNext();) {
            LoanOfferingBO loanOffering = iter.next();
            if (!isMeetingMatched(customerMeeting, loanOffering.getLoanOfferingMeeting().getMeeting()))
                iter.remove();
        }
        logger.debug("outside removePrdOfferingsNotMachingCustomerMeeting method");
    }

    private boolean isMeetingMatched(MeetingBO meetingToBeMatched, MeetingBO meetingToBeMatchedWith) {
        logger.debug("isMeetingMatched method called");
        return meetingToBeMatched != null
                && meetingToBeMatchedWith != null
                && meetingToBeMatched.getMeetingDetails().getRecurrenceType().getRecurrenceId().equals(
                        meetingToBeMatchedWith.getMeetingDetails().getRecurrenceType().getRecurrenceId())
                && isMultiple(meetingToBeMatchedWith.getMeetingDetails().getRecurAfter(), meetingToBeMatched
                        .getMeetingDetails().getRecurAfter());
    }

    private boolean isMultiple(Short valueToBeChecked, Short valueToBeCheckedWith) {
        return valueToBeChecked % valueToBeCheckedWith == 0;
    }

    private List<MultipleLoanCreationViewHelper> buildClientViewHelper(final LoanOfferingBO loanOffering,
            List<ClientBO> clients) {
        return (List<MultipleLoanCreationViewHelper>) collect(clients,
                new Transformer<ClientBO, MultipleLoanCreationViewHelper>() {
                    public MultipleLoanCreationViewHelper transform(ClientBO client) {
                        return new MultipleLoanCreationViewHelper(client, loanOffering.eligibleLoanAmount(client
                                .getMaxLoanAmount(loanOffering), client.getMaxLoanCycleForProduct(loanOffering)),
                                loanOffering.eligibleNoOfInstall(client.getMaxLoanAmount(loanOffering), client
                                        .getMaxLoanCycleForProduct(loanOffering)));
                    }
                });
    }

    protected void checkPermissionForCreate(Short newState, UserContext userContext, Short flagSelected,
            Short officeId, Short loanOfficerId) throws ApplicationException {
        logger.debug("inside checkPermissionForCreate called");
        if (!isPermissionAllowed(newState, userContext, officeId, loanOfficerId, true))
            throw new AccountException(SecurityConstants.KEY_ACTIVITY_NOT_ALLOWED);
    }

    private boolean isPermissionAllowed(Short newSate, UserContext userContext, Short officeId, Short loanOfficerId,
            boolean saveFlag) {
        logger.debug("inside isPermissionAllowed called");
        return AuthorizationManager.getInstance().isActivityAllowed(
                userContext,
                new ActivityContext(ActivityMapper.getInstance().getActivityIdForState(newSate), officeId,
                        loanOfficerId));
    }
}
