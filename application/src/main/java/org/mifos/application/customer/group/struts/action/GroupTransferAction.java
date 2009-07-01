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

package org.mifos.application.customer.group.struts.action;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.PositionEntity;
import org.mifos.application.customer.business.service.CustomerBusinessService;
import org.mifos.application.customer.center.business.CenterBO;
import org.mifos.application.customer.center.business.service.CenterBusinessService;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientBusinessService;
import org.mifos.application.customer.exceptions.CustomerException;
import org.mifos.application.customer.group.business.GroupBO;
import org.mifos.application.customer.group.business.service.GroupBusinessService;
import org.mifos.application.customer.group.struts.actionforms.GroupTransferActionForm;
import org.mifos.application.customer.group.util.helpers.CenterSearchInput;
import org.mifos.application.customer.group.util.helpers.GroupConstants;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.ClientRules;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.StringUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class GroupTransferAction extends BaseAction {

    private MasterDataService masterService;

    public GroupTransferAction() {
        masterService = (MasterDataService) ServiceFactory.getInstance().getBusinessService(
                BusinessServiceName.MasterDataService);
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
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadBranches(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.loadBranches_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewBranchTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previewBranchTransfer_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward transferToBranch(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        OfficeBO officeToTransfer = getOfficeBusinessService().getOffice(actionForm.getOfficeIdValue());
        GroupBO groupInSession = (GroupBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        GroupBO group = getGroupBusinessService().getGroup(groupInSession.getCustomerId());
        checkVersionMismatch(groupInSession.getVersionNo(), group.getVersionNo());
        group.setVersionNo(groupInSession.getVersionNo());
        group.setUserContext(getUserContext(request));
        setInitialObjectForAuditLogging(group);
        group.transferToBranch(officeToTransfer);
        groupInSession = null;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadParents(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        CenterSearchInput centerSearchInput = new CenterSearchInput();
        centerSearchInput.setOfficeId(getUserContext(request).getBranchId());
        centerSearchInput.setGroupInput(GroupConstants.GROUP_TRANSFER_INPUT);
        SessionUtils.setAttribute(GroupConstants.CENTER_SEARCH_INPUT, centerSearchInput, request.getSession());
        return mapping.findForward(ActionForwards.loadParents_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previewParentTransfer(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.previewParentTransfer_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    @CloseSession
    public ActionForward transferToCenter(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        CenterBO transferToCenter = getCenterBusinessService().findBySystemId(actionForm.getCenterSystemId());
        transferToCenter.setUserContext(getUserContext(request));

        GroupBO groupInSession = (GroupBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        GroupBO group = getGroupBusinessService().getGroup(groupInSession.getCustomerId());
        checkVersionMismatch(groupInSession.getVersionNo(), group.getVersionNo());
        group.setUserContext(getUserContext(request));
        group.setVersionNo(groupInSession.getVersionNo());
        setInitialObjectForAuditLogging(group);
        group.transferToCenter(transferToCenter);
        groupInSession = null;
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    protected CustomerBusinessService getCustomerBusinessService() {
        return (CustomerBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Customer);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadGrpMemberShip(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        doCleanUp(request.getSession(), form);
        UserContext userContext = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());

        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        CustomerBO customerBO = getCustomerBusinessService().getCustomer(
                Integer.valueOf(clientInSession.getCustomerId()));

        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, customerBO, request);

        List<OfficeView> activeBranches = masterService.getActiveBranches(userContext.getBranchId());
        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST, activeBranches, request);

        boolean isCenterHeirarchyExists = ClientRules.getCenterHierarchyExists();
        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISCENTERHEIRARCHYEXISTS,
                isCenterHeirarchyExists ? Constants.YES : Constants.NO, request);

        actionForm.setAssignedLoanOfficerId(clientInSession.getPersonnel().getPersonnelId().toString());

        List<PersonnelView> loanOfficers = loadLoanOfficersForBranch(userContext, customerBO.getOffice().getOfficeId());
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
            HttpServletResponse response) throws Exception {

        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        CustomerBO customerBOInSession = (CustomerBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        CustomerBO customerBO = getCustomerBusinessService().getCustomer(customerBOInSession.getCustomerId());
        checkVersionMismatch(customerBOInSession.getVersionNo(), customerBO.getVersionNo());
        customerBO.setUserContext(getUserContext(request));
        customerBO.getCustomerStatus().setLocaleId(getUserContext(request).getLocaleId());

        checkBeforeRemoving(customerBO, actionForm, request);

        ClientBO client = getClientBusinessService().getClient(customerBO.getCustomerId());

        CustomerPersistence customerPersistence = new CustomerPersistence();
        client.updateClientFlag();
        setInitialObjectForAuditLogging(customerBO);
        PersonnelBO personnel = null;
        if (!StringUtils.isNullOrEmpty(actionForm.getAssignedLoanOfficerId())) {
            personnel = getPersonnelBusinessService()
                    .getPersonnel(Short.valueOf(actionForm.getAssignedLoanOfficerId()));
        }
        customerBO.removeGroupMemberShip(personnel, actionForm.getComment());

        customerBOInSession = null;
        customerBO = null;

        return mapping.findForward(ActionForwards.view_client_details_page.toString());

    }

    private void checkBeforeRemoving(CustomerBO customerBO, GroupTransferActionForm actionForm,
            HttpServletRequest request) throws CustomerException {

        if (customerBO.hasActiveLoanAccounts())
            throw new CustomerException(CustomerConstants.CLIENT_HAS_ACTIVE_ACCOUNTS_EXCEPTION);
        else if (customerBO.getParentCustomer() != null && customerBO.getParentCustomer().hasActiveLoanAccounts())
            throw new CustomerException(CustomerConstants.GROUP_HAS_ACTIVE_ACCOUNTS_EXCEPTION);

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

    private ClientBusinessService getClientBusinessService() throws ServiceException {
        return (ClientBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Client);
    }

    private PersonnelBusinessService getPersonnelBusinessService() throws ServiceException {
        return (PersonnelBusinessService) ServiceFactory.getInstance()
                .getBusinessService(BusinessServiceName.Personnel);
    }

    private List<PersonnelView> loadLoanOfficersForBranch(UserContext userContext, Short branchId) throws Exception {
        return masterService.getListOfActiveLoanOfficers(PersonnelConstants.LOAN_OFFICER, branchId,
                userContext.getId(), userContext.getLevelId());
    }

    protected void loadPositions(HttpServletRequest request) throws ApplicationException {
        SessionUtils.setCollectionAttribute(CustomerConstants.POSITIONS, getMasterEntities(PositionEntity.class,
                getUserContext(request).getLocaleId()), request);
    }

    private OfficeBusinessService getOfficeBusinessService() throws ServiceException {
        return (OfficeBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Office);
    }

    private GroupBusinessService getGroupBusinessService() {
        return (GroupBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Group);
    }

    private CenterBusinessService getCenterBusinessService() {
        return (CenterBusinessService) ServiceFactory.getInstance().getBusinessService(BusinessServiceName.Center);
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
