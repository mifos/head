/*
 * Copyright (c) 2005-2011 Grameen Foundation USA
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.group.business.GroupBO;
import org.mifos.customers.group.struts.actionforms.GroupTransferActionForm;
import org.mifos.customers.group.util.helpers.GroupConstants;
import org.mifos.customers.office.util.helpers.OfficeConstants;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.CustomerDetailDto;
import org.mifos.dto.screen.CenterSearchInput;
import org.mifos.dto.screen.ClientRemovalFromGroupDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class GroupTransferAction extends BaseAction {

    public GroupTransferAction() {
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
        GroupBO groupInSession = (GroupBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        CustomerDetailDto groupDetail = this.groupServiceFacade.transferGroupToBranch(groupInSession.getGlobalCustNum(), actionForm.getOfficeIdValue(), groupInSession.getVersionNo());

        GroupBO group = this.customerDao.findGroupBySystemId(groupDetail.getGlobalCustNum());
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

        CustomerDetailDto groupDetail = this.groupServiceFacade.transferGroupToCenter(groupInSession.getGlobalCustNum(), actionForm.getCenterSystemId(), groupInSession.getVersionNo());

        GroupBO group = this.customerDao.findGroupBySystemId(groupDetail.getGlobalCustNum());
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, group, request);
        return mapping.findForward(ActionForwards.update_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadGrpMemberShip(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        actionForm.setComment(null);

        ClientBO clientInSession = (ClientBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);

        ClientRemovalFromGroupDto clientRemovalFromGroupDetail = this.clientServiceFacade.retreiveClientDetailsForRemovalFromGroup(clientInSession.getGlobalCustNum());

        actionForm.setAssignedLoanOfficerId(clientRemovalFromGroupDetail.getLoanOfficerId().toString());
        actionForm.setIsActive(clientRemovalFromGroupDetail.isActive() ? Constants.YES: Constants.NO);

        ClientBO client = this.customerDao.findClientBySystemId(clientInSession.getGlobalCustNum());
        SessionUtils.removeAttribute(Constants.BUSINESS_KEY, request);
        SessionUtils.setAttribute(Constants.BUSINESS_KEY, client, request);
        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST, clientRemovalFromGroupDetail.getActiveBranches(), request);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISCENTERHIERARCHYEXISTS, clientRemovalFromGroupDetail.isCenterHierarchyExists() ? Constants.YES : Constants.NO, request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, clientRemovalFromGroupDetail.getLoanOfficers(), request);

        return mapping.findForward(ActionForwards.loadGrpMemberShip_success.toString());
    }

    @CloseSession
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward removeGroupMemberShip(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        GroupTransferActionForm actionForm = (GroupTransferActionForm) form;
        CustomerBO customerBOInSession = (CustomerBO) SessionUtils.getAttribute(Constants.BUSINESS_KEY, request);
        ClientBO client = this.customerDao.findClientBySystemId(customerBOInSession.getGlobalCustNum());

        checkVersionMismatch(customerBOInSession.getVersionNo(), client.getVersionNo());

        Short loanOfficerId = null;
        if (StringUtils.isNotBlank(actionForm.getAssignedLoanOfficerId())) {
            loanOfficerId = Short.valueOf(actionForm.getAssignedLoanOfficerId());
        }

        this.clientServiceFacade.removeGroupMembership(customerBOInSession.getGlobalCustNum(), loanOfficerId, actionForm.getComment());

        return mapping.findForward(ActionForwards.view_client_details_page.toString());
    }

    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
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