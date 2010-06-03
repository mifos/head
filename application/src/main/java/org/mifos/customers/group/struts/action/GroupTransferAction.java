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

package org.mifos.customers.group.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.accounts.business.AccountBO;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.ClientRules;
import org.mifos.config.persistence.ConfigurationPersistence;
import org.mifos.core.MifosRuntimeException;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.PositionEntity;
import org.mifos.customers.business.service.CustomerBusinessService;
import org.mifos.customers.center.business.service.CenterBusinessService;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.client.business.service.ClientBusinessService;
import org.mifos.customers.exceptions.CustomerException;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.business.service.GroupBusinessService;
import org.mifos.customers.group.struts.actionforms.GroupTransferActionForm;
import org.mifos.customers.group.util.helpers.CenterSearchInput;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.business.OfficeDetailsDto;
import org.mifos.customers.office.business.service.OfficeBusinessService;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.persistence.CustomerPersistence;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.PersonnelDto;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.customers.personnel.util.helpers.PersonnelConstants;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class GroupTransferAction extends BaseAction {

    private MasterDataService masterService;

    public GroupTransferAction() {
        masterService = new MasterDataService();
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return getGroupBusinessService();
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("groupTransferAction");
        security.allow("loadParents", SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);
        security.allow("loadBranches", SecurityConstants.GROUP_TRANSFER_THE_GROUP);
        security.allow("previewBranchTransfer", SecurityConstants.VIEW);
        security.allow("previewParentTransfer", SecurityConstants.VIEW);
        security.allow("transferToCenter", SecurityConstants.GROUP_CHANGE_CENTER_MEMBERSHIP);
        security.allow("transferToBranch", SecurityConstants.GROUP_TRANSFER_THE_GROUP);
        security.allow("loadGrpMemberShip", SecurityConstants.GROUP_TRANSFER_THE_GROUP);
        security.allow("removeGroupMemberShip", SecurityConstants.CAN_REMOVE_CLIENTS_FROM_GROUPS);
        return security;
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(@SuppressWarnings("unused") String method) {
        return true;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadBranches(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.loadBranches_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewBranchTransfer(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previewBranchTransfer_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward transferToBranch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        UserContext userContext = getUserContext(request);
        GroupBO groupInSession = (GroupBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        GroupBO group = this.customerServiceFacade.transferGroupToBranch(groupInSession.getGlobalCustNum(), actionForm.getOfficeIdValue(), userContext, groupInSession.getVersionNo());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadParents(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        CenterSearchInput centerSearchInput = new CenterSearchInput();
        centerSearchInput.setOfficeId(getUserContext(request).getBranchId());
        centerSearchInput.setGroupInput(GroupConstants.GROUP_TRANSFER_INPUT);
        SessionUtils.setAttribute(GroupConstants.CENTER_SEARCH_INPUT, centerSearchInput, request.getSession());
        return mapping.findForward(ActionForwards.loadParents_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewParentTransfer(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previewParentTransfer_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward transferToCenter(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        GroupBO groupInSession = (GroupBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        UserContext userContext = getUserContext(request);

        GroupBO group = this.customerServiceFacade.transferGroupToCenter(groupInSession.getGlobalCustNum(), actionForm.getCenterSystemId(), userContext, groupInSession.getVersionNo());

        SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    protected CustomerBusinessService getCustomerBusinessService() {
        return new CustomerBusinessService();
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadGrpMemberShip(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        doCleanUp(request.getSession(), form);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());

        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        CustomerBO customerBO = getCustomerBusinessService().getCustomer(Integer.valueOf(clientInSession.getCustomerId()));

        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request);

        List<OfficeDetailsDto> activeBranches = masterService.getActiveBranches(userContext.getBranchId());
        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST, activeBranches, request);

        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();
        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISCENTERHIERARCHYEXISTS,
                isCenterHierarchyExists ? Constants.YES : Constants.NO, request);

        actionForm.setAssignedLoanOfficerId(customerBO.getPersonnel().getPersonnelId().toString());

        List<PersonnelDto> loanOfficers = loadLoanOfficersForBranch(userContext, customerBO.getOffice().getOfficeId());
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, loanOfficers, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, clientInSession, request);

        customerBO.checkIfClientIsATitleHolder();
        actionForm
                .setIsActive(getClientBusinessService().getClient(customerBO.getCustomerId()).isActive() ? Constants.YES
                        : Constants.NO);
        return mapping.findForward(ActionForwards.loadGrpMemberShip_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward removeGroupMemberShip(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        CustomerBO customerBOInSession = (CustomerBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        CustomerBO customerBO = getCustomerBusinessService().getCustomer(customerBOInSession.getCustomerId());
        checkVersionMismatch(customerBOInSession.getVersionNo(), customerBO.getVersionNo());
        customerBO.setUserContext(getUserContext(request));
        customerBO.getCustomerStatus().setLocaleId(getUserContext(request).getLocaleId());

        checkBeforeRemoving(customerBO, actionForm, request);

        ClientBO client = getClientBusinessService().getClient(customerBO.getCustomerId());

        client.updateClientFlag();
        setInitialObjectForAuditLogging(customerBO);
        PersonnelBO personnel = null;
        if (StringUtils.isNotBlank(actionForm.getAssignedLoanOfficerId())) {
            personnel = getPersonnelBusinessService()
                    .getPersonnel(Short.valueOf(actionForm.getAssignedLoanOfficerId()));
        }
        customerBO.removeGroupMemberShip(personnel, actionForm.getComment());

        customerBOInSession = null;
        customerBO = null;

        return mapping.findForward(ActionForwards.view_client_details_page.toString());

    }

    private void checkBeforeRemoving(CustomerBO customerBO, @SuppressWarnings("unused") GroupTransferActionForm actionForm,
            @SuppressWarnings("unused") HttpServletRequest request) throws CustomerException {

        if (customerBO.hasActiveLoanAccounts()) {
            throw new CustomerException(CustomerConstants.CLIENT_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
        }

        try {

            if (customerBO.getParentCustomer() != null) {

                boolean glimEnabled = new ConfigurationPersistence().isGlimEnabled();

                if (glimEnabled) {
                    if (customerIsMemberOfAnyExistingGlimLoanAccount(customerBO, customerBO.getParentCustomer())) {
                        throw new CustomerException(CustomerConstants.GROUP_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
                    }
                } else if (customerBO.getParentCustomer().hasActiveLoanAccounts()) {
                    // not glim - then disallow removing client from group with active account
                    throw new CustomerException(CustomerConstants.GROUP_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
                }
            }
        } catch (PersistenceException e) {
            throw new MifosRuntimeException(e);
        }
    }

    private boolean customerIsMemberOfAnyExistingGlimLoanAccount(CustomerBO customerToRemoveFromGroup, CustomerBO customerWithActiveAccounts) {

        CustomerDao customerDao = DependencyInjectedServiceLocator.locateCustomerDao();
        List<AccountBO> activeLoanAccounts = customerDao.findGLIMLoanAccountsApplicableTo(customerToRemoveFromGroup.getCustomerId(), customerWithActiveAccounts.getCustomerId());

        return !activeLoanAccounts.isEmpty();
    }

    public CustomerBO getCustomer(Integer customerId) throws ServiceException {
        try {
            return new CustomerPersistence().getCustomer(customerId);
        } catch (PersistenceException pe) {
            throw new ServiceException(pe);
        }
    }

    private void doCleanUp(HttpSession session, ActionForm form) {
        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        actionForm.setComment(null);

    }

    private ClientBusinessService getClientBusinessService() {
        return new ClientBusinessService();
    }

    private PersonnelBusinessService getPersonnelBusinessService() {
        return new PersonnelBusinessService();
    }

    private List<PersonnelDto> loadLoanOfficersForBranch(UserContext userContext, Short branchId) throws Exception {
        return masterService.getListOfActiveLoanOfficers(PersonnelConstants.LOAN_OFFICER, branchId,
                userContext.getId(), userContext.getLevelId());
    }

    protected void loadPositions(HttpServletRequest request) throws ApplicationException {
        SessionUtils.setCollectionAttribute(CustomerConstants.POSITIONS, getMasterEntities(PositionEntity.class,
                getUserContext(request).getLocaleId()), request);
    }

    private OfficeBusinessService getOfficeBusinessService() throws ServiceException {
        return new OfficeBusinessService();
    }

    private GroupBusinessService getGroupBusinessService() {
        return new GroupBusinessService();
    }

    private CenterBusinessService getCenterBusinessService() {
        return new CenterBusinessService();
    }

    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        if (method.equalsIgnoreCase(Methods.removeGroupMemberShip.toString())) {
            return mapping.findForward(ActionForwards.loadGrpMemberShip_success.toString());
        }
        if (method.equalsIgnoreCase(Methods.loadGrpMemberShip.toString())) {
            return mapping.findForward(ActionForwards.loadGrpMemberShip_success.toString());
        }
        return null;
    }
}