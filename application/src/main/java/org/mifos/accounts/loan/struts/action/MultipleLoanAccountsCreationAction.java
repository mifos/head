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
import org.mifos.accounts.loan.business.service.LoanDto;
import org.mifos.accounts.loan.business.service.LoanService;
import org.mifos.accounts.loan.persistance.LoanDaoLegacyImpl;
import org.mifos.accounts.loan.struts.actionforms.MultipleLoanAccountsCreationActionForm;
import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.MultipleLoanCreationDto;
import org.mifos.accounts.productdefinition.business.LoanOfferingBO;
import org.mifos.accounts.productdefinition.business.service.LoanPrdBusinessService;
import org.mifos.accounts.productdefinition.business.service.LoanProductService;
import org.mifos.accounts.util.helpers.AccountState;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.config.ClientRules;
import org.mifos.customers.api.CustomerLevel;
import org.mifos.customers.business.CustomerBO;
import org.mifos.customers.business.CustomerLevelEntity;
import org.mifos.customers.client.business.ClientBO;
import org.mifos.customers.util.helpers.CustomerConstants;
import org.mifos.dto.domain.CustomerDto;
import org.mifos.dto.screen.ChangeAccountStatusDto;
import org.mifos.dto.screen.MultipleLoanAccountDetailsDto;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.helpers.Transformer;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;
import org.mifos.service.BusinessRuleException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MultipleLoanAccountsCreationAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(MultipleLoanAccountsCreationAction.class);
    private LoanPrdBusinessService loanPrdBusinessService;
    private LoanProductService loanProductService;
    private LoanService loanService;

    public MultipleLoanAccountsCreationAction() {
        loanPrdBusinessService = new LoanPrdBusinessService();
        loanProductService = new LoanProductService(loanPrdBusinessService);
        loanService = new LoanService(loanProductService, new LoanDaoLegacyImpl());
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
    public ActionForward load(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        ChangeAccountStatusDto accountDetails = this.loanAccountServiceFacade.retrieveAllActiveBranchesAndLoanOfficerDetails();

        SessionUtils.setCollectionAttribute(LoanConstants.MULTIPLE_LOANS_OFFICES_LIST, accountDetails.getActiveBranches(), request);

        Short centerHierarchyExistsValue = accountDetails.isCenterHierarchyExists() ? Constants.YES : Constants.NO;
        SessionUtils.setAttribute(LoanConstants.IS_CENTER_HIERARCHY_EXISTS, centerHierarchyExistsValue, request);

        request.getSession().setAttribute(LoanConstants.MULTIPLE_LOANS_ACTION_FORM, null);
        request.getSession().setAttribute(Constants.BUSINESS_KEY, null);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getLoanOfficers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        Short officeId = getShortValue(((MultipleLoanAccountsCreationActionForm) form).getBranchOfficeId());
        ChangeAccountStatusDto accountDetails = this.loanAccountServiceFacade.retrieveLoanOfficerDetailsForBranch(officeId);

        SessionUtils.setCollectionAttribute(LoanConstants.MULTIPLE_LOANS_LOAN_OFFICERS_LIST, accountDetails.getLoanOfficers(), request);

        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getCenters(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        MultipleLoanAccountsCreationActionForm loanActionForm = (MultipleLoanAccountsCreationActionForm) form;
        Short loanOfficerId = getShortValue(loanActionForm.getLoanOfficerId());
        Short officeId = getShortValue(loanActionForm.getBranchOfficeId());

        List<CustomerDto> topLevelCustomers = this.loanAccountServiceFacade.retrieveActiveGroupingAtTopOfCustomerHierarchyForLoanOfficer(loanOfficerId, officeId);

        boolean isCenterHierarchyExists = ClientRules.getCenterHierarchyExists();

        SessionUtils.setCollectionAttribute(LoanConstants.MULTIPLE_LOANS_CENTERS_LIST, topLevelCustomers, request);
        SessionUtils.setAttribute(LoanConstants.IS_CENTER_HIERARCHY_EXISTS, isCenterHierarchyExists ? Constants.YES: Constants.NO, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward getPrdOfferings(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        MultipleLoanAccountsCreationActionForm loanActionForm = (MultipleLoanAccountsCreationActionForm) form;

        Integer customerId = getIntegerValue(loanActionForm.getCenterId());
        CustomerBO customer = this.customerDao.findCustomerById(customerId);

        loanActionForm.setCenterSearchId(customer.getSearchId());

        List<LoanOfferingBO> loanOfferings = this.loanProductDao.findActiveLoanProductsApplicableToCustomerLevel(new CustomerLevelEntity(CustomerLevel.CLIENT));

        MeetingBO customerMeeting = customer.getCustomerMeetingValue();
        for (Iterator<LoanOfferingBO> iter = loanOfferings.iterator(); iter.hasNext();) {
            LoanOfferingBO loanOffering = iter.next();
            if (!isMeetingMatched(customerMeeting, loanOffering.getLoanOfferingMeeting().getMeeting())) {
                iter.remove();
            }
        }
        SessionUtils.setCollectionAttribute(LoanConstants.LOANPRDOFFERINGS, loanOfferings, request);
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        MultipleLoanAccountsCreationActionForm loanActionForm = (MultipleLoanAccountsCreationActionForm) form;

        String searchId = loanActionForm.getCenterSearchId();
        Short branchId = getShortValue(loanActionForm.getBranchOfficeId());
        Integer productId = Integer.parseInt(loanActionForm.getPrdOfferingId());

        MultipleLoanAccountDetailsDto multipleLoanDetails = this.loanAccountServiceFacade.retrieveMultipleLoanAccountDetails(searchId, branchId, productId);

        List<ClientBO> clients = this.customerDao.findActiveClientsUnderParent(searchId, branchId);
        if (clients.isEmpty()) {
            throw new BusinessRuleException(LoanConstants.NOSEARCHRESULTS);
        }
        LoanOfferingBO loanOffering = this.loanProductDao.findById(productId);
        List<MultipleLoanCreationDto> multipleLoanDetailsXX = buildClientViewHelper(loanOffering, clients);

        loanActionForm.setClientDetails(multipleLoanDetailsXX);

        SessionUtils.setAttribute(LoanConstants.LOANOFFERING, loanOffering, request);
        SessionUtils.setCollectionAttribute(MasterConstants.BUSINESS_ACTIVITIES, multipleLoanDetails.getAllLoanPruposes(), request);
        SessionUtils.setAttribute(CustomerConstants.PENDING_APPROVAL_DEFINED, multipleLoanDetails.isLoanPendingApprovalStateEnabled(), request);

        return mapping.findForward(ActionForwards.get_success.toString());
    }

    private List<MultipleLoanCreationDto> buildClientViewHelper(final LoanOfferingBO loanOffering,
            List<ClientBO> clients) {
        return (List<MultipleLoanCreationDto>) collect(clients,
                new Transformer<ClientBO, MultipleLoanCreationDto>() {
                    public MultipleLoanCreationDto transform(ClientBO client) {
                        return new MultipleLoanCreationDto(client, loanOffering.eligibleLoanAmount(client
                                .getMaxLoanAmount(loanOffering), client.getMaxLoanCycleForProduct(loanOffering)),
                                loanOffering.eligibleNoOfInstall(client.getMaxLoanAmount(loanOffering), client
                                        .getMaxLoanCycleForProduct(loanOffering)), loanOffering.getCurrency());
                    }
                });
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
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
        return mapping.findForward(actionForward.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        MultipleLoanAccountsCreationActionForm loanActionForm = (MultipleLoanAccountsCreationActionForm) form;
        Integer centerId = getIntegerValue(loanActionForm.getCenterId());
        Short loanProductId = getShortValue(loanActionForm.getPrdOfferingId());
        Short accountStateId = getShortValue(loanActionForm.getStateSelected());
        AccountState accountState = AccountState.fromShort(accountStateId);
        UserContext userContext = getUserContext(request);

        List<MultipleLoanCreationDto> applicableClientDetails = loanActionForm.getApplicableClientDetails();

        List<String> accountNumbers = new ArrayList<String>();

        if (applicableClientDetails != null) {
            for (MultipleLoanCreationDto clientDetail : applicableClientDetails) {
                LoanDto loanDto = loanService.createLoan(userContext, centerId, loanProductId, clientDetail
                        .getClientId(), accountState, clientDetail.getLoanAmount(), clientDetail
                        //FIXME: Loan are created using double, the better way to do this would be to
                        // make those double argument as Money or BigDecimal. this workaround is added
                        // to fix MIFOS-2698
                        .getDefaultNoOfInstall(), clientDetail.getMaxLoanAmount().getAmountDoubleValue()
                        , clientDetail.getMinLoanAmount().getAmountDoubleValue(),
                        clientDetail.getMaxNoOfInstall(), clientDetail.getMinNoOfInstall(),
                        getIntegerValue(clientDetail.getBusinessActivity()));
                accountNumbers.add(loanDto.getGlobalAccountNumber());
            }
        }
        request.setAttribute(LoanConstants.ACCOUNTS_LIST, accountNumbers);
        return mapping.findForward(ActionForwards.create_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
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
}