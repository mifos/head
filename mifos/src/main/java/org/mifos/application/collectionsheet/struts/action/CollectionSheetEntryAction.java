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
 
package org.mifos.application.collectionsheet.struts.action;

import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.persistance.StandardClientAttendanceDao;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryBO;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.business.service.BulkEntryBusinessService;
import org.mifos.application.collectionsheet.struts.actionforms.BulkEntryActionForm;
import org.mifos.application.collectionsheet.util.helpers.BulkEntrySavingsCache;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.business.CustomerBO;
import org.mifos.application.customer.business.CustomerView;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientAttendanceDto;
import org.mifos.application.customer.client.business.service.ClientService;
import org.mifos.application.customer.client.business.service.StandardClientService;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.customer.util.helpers.CustomerLevel;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.PaymentTypeView;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.application.personnel.business.PersonnelView;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.YesNoFlag;
import org.mifos.config.AccountingRules;
import org.mifos.config.ClientRules;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CollectionSheetEntryAction extends BaseAction {

    private MasterDataService masterService;

    private ClientService clientService;

    private static MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.BULKENTRYLOGGER);

    public CollectionSheetEntryAction() {
        masterService = (MasterDataService) ServiceFactory.getInstance().getBusinessService(
                BusinessServiceName.MasterDataService);
         clientService = new StandardClientService();
        clientService.setClientAttendanceDao(new StandardClientAttendanceDao());
    }

    @Override
    protected BusinessService getService() {
        return new BulkEntryBusinessService();
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("collectionsheetaction");
        security.allow("load", SecurityConstants.CAN_ENTER_COLLECTION_SHEET_DATA);
        security.allow("preview", SecurityConstants.VIEW);
        security.allow("previous", SecurityConstants.VIEW);
        security.allow("get", SecurityConstants.VIEW);
        security.allow("getLastMeetingDateForCustomer", SecurityConstants.VIEW);
        security.allow("create", SecurityConstants.VIEW);
        security.allow("loadLoanOfficers", SecurityConstants.VIEW);
        security.allow("loadCustomerList", SecurityConstants.VIEW);
        security.allow("validate", SecurityConstants.VIEW);
        return security;
    }

    @Override
    protected boolean startSession() {
        return false;
    }

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return method.equals(CollectionSheetEntryConstants.CREATEMETHOD);
    }

    /**
     * This method is called before the load page for center is called It sets
     * this information in session and context.This should be removed after
     * center was successfully created.
     */
    @TransactionDemarcate(saveToken = true)
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        try {
            request.getSession().setAttribute(CollectionSheetEntryConstants.BULKENTRYACTIONFORM, null);
            request.getSession().setAttribute(Constants.BUSINESS_KEY, null);
            UserContext userContext = getUserContext(request);
            List<OfficeView> activeBranches = masterService.getActiveBranches(userContext.getBranchId());
            SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST, activeBranches, request);

            boolean isCenterHeirarchyExists = ClientRules.getCenterHierarchyExists();
            SessionUtils.setAttribute(CollectionSheetEntryConstants.ISCENTERHEIRARCHYEXISTS,
                    isCenterHeirarchyExists ? Constants.YES : Constants.NO, request);
            if (activeBranches.size() == 1) {
                List<PersonnelView> loanOfficers = loadLoanOfficersForBranch(userContext, activeBranches.get(0)
                        .getOfficeId());
                SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, loanOfficers, request);
                if (loanOfficers.size() == 1) {
                    List<CustomerView> parentCustomerList = loadCustomers(loanOfficers.get(0).getPersonnelId(),
                            activeBranches.get(0).getOfficeId());
                    SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERSLIST, parentCustomerList, request);
                    request.setAttribute(CollectionSheetEntryConstants.REFRESH, Constants.NO);
                } else {
                    SessionUtils.setAttribute(CollectionSheetEntryConstants.CUSTOMERSLIST, new ArrayList<CustomerView>(), request);
                    request.setAttribute(CollectionSheetEntryConstants.REFRESH, Constants.YES);
                }
            } else {
                SessionUtils.setAttribute(CustomerConstants.LOAN_OFFICER_LIST, new ArrayList<PersonnelView>(), request);
                SessionUtils.setAttribute(CollectionSheetEntryConstants.CUSTOMERSLIST, new ArrayList<CustomerView>(), request);
                request.setAttribute(CollectionSheetEntryConstants.REFRESH, Constants.YES);
            }
            SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.PAYMENT_TYPES_LIST, masterService
                    .retrieveMasterEntities(PaymentTypeEntity.class, userContext.getLocaleId()), request);
            SessionUtils.setAttribute(CollectionSheetEntryConstants.ISBACKDATEDTRXNALLOWED, Constants.NO, request);

        } catch (Exception e) {
        }
        return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }

    /**
     * This method retrieves the last meeting date for the chosen customer. This
     * meeting date is put as the default date for the tranasaction date in the
     * search criteria
     * 
     */
    @TransactionDemarcate(joinToken = true)
    public ActionForward getLastMeetingDateForCustomer(ActionMapping mapping, ActionForm form,
            HttpServletRequest request, HttpServletResponse response) throws Exception {
        BulkEntryActionForm actionForm = (BulkEntryActionForm) form;

        boolean isBackDatedTrxnAllowed = false;
        if (actionForm.getOfficeId() != null)
            isBackDatedTrxnAllowed = AccountingRules.isBackDatedTxnAllowed();
        Date meetingDate = new BulkEntryBusinessService().getLastMeetingDateForCustomer(Integer.valueOf(actionForm
                .getCustomerId()));
        if (meetingDate != null && isBackDatedTrxnAllowed) {
            actionForm.setTransactionDate(meetingDate);
        } else {
            actionForm.setTransactionDate(DateUtils.getCurrentDateWithoutTimeStamp());
        }
        SessionUtils.setAttribute("LastMeetingDate", meetingDate, request);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISBACKDATEDTRXNALLOWED, isBackDatedTrxnAllowed ? Constants.YES
                : Constants.NO, request);

        return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadLoanOfficers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        UserContext userContext = getUserContext(request);
        BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
        Short officeId = Short.valueOf(bulkEntryActionForm.getOfficeId());
        List<PersonnelView> loanOfficers = loadLoanOfficersForBranch(userContext, officeId);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, loanOfficers, request);
        return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadCustomerList(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
        Short personnelId = Short.valueOf(bulkEntryActionForm.getLoanOfficerId());
        Short officeId = Short.valueOf(bulkEntryActionForm.getOfficeId());
        List<CustomerView> parentCustomerList = loadCustomers(personnelId, officeId);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERSLIST, parentCustomerList, request);
        boolean isCenterHeirarchyExists = ClientRules.getCenterHierarchyExists();
        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISCENTERHEIRARCHYEXISTS, isCenterHeirarchyExists ? Constants.YES
                : Constants.NO, request);
        return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }


    /**
     * This method is called once the search criteria have been entered by the
     * user to generate the bulk entry details for a particular customer It
     * retrieves the loan officer office, and parent customer that was selected
     * and sets them into the bulk entry business object. The list of attendance
     * types and product list associated with the center, and its children are
     * also retrieved
     */
    @TransactionDemarcate(joinToken = true)
    public ActionForward get(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
        CustomerView parentCustomer = getSelectedCustomer(request, form);
        UserContext userContext = getUserContext(request);
        String centerId = bulkEntryActionForm.getCustomerId();
        String userId = userContext.getName();

        Date meetingDate = (Date) SessionUtils.getAttribute("LastMeetingDate", request);
        CollectionSheetEntryBO bulkEntry = (CollectionSheetEntryBO) request.getSession().getAttribute(Constants.BUSINESS_KEY);
        PersonnelView loanOfficer = getSelectedLO(request, form);
        bulkEntry.setLoanOfficer(loanOfficer);
        bulkEntry.setOffice(getSelectedBranchOffice(request, form));
        bulkEntry.setPaymentType(getSelectedPaymentType(request, form));
        bulkEntry.buildBulkEntryView(parentCustomer);
        bulkEntry.setLoanProducts(masterService.getLoanProductsAsOfMeetingDate(meetingDate, parentCustomer
                .getCustomerSearchId(), loanOfficer.getPersonnelId()));
        bulkEntry.setSavingsProducts(masterService.getSavingsProductsAsOfMeetingDate(meetingDate, parentCustomer
                .getCustomerSearchId(), loanOfficer.getPersonnelId()));
        SessionUtils.setAttribute(CollectionSheetEntryConstants.BULKENTRY, bulkEntry, request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERATTENDANCETYPES, masterService.getMasterData(
                MasterConstants.ATTENDENCETYPES, userContext.getLocaleId(),
                "org.mifos.application.master.business.CustomerAttendanceType", "attendanceId")
                .getCustomValueListElements(), request);

        bulkEntry.buildBulkEntryView(parentCustomer);

        List<CustomerBO> customers = bulkEntry.retrieveActiveClientsUnderParent(parentCustomer.getCustomerSearchId());
        HashMap<Integer, ClientAttendanceDto> clientAttendance = getClientAttendance(customers, meetingDate);
        SessionUtils.setMapAttribute(CollectionSheetEntryConstants.CLIENT_ATTENDANCE, clientAttendance, request);

        return mapping.findForward(CollectionSheetEntryConstants.GETSUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;

        CollectionSheetEntryBO bulkEntry = (CollectionSheetEntryBO) SessionUtils.getAttribute(CollectionSheetEntryConstants.BULKENTRY, request);

        List<ClientBO> clients = new ArrayList<ClientBO>();
        List<SavingsBO> savingsAccounts = new ArrayList<SavingsBO>();
        List<String> customerNames = new ArrayList<String>();
        List<String> savingsDepNames = new ArrayList<String>();
        List<String> savingsWithNames = new ArrayList<String>();
        Map<Integer, BulkEntrySavingsCache> savingsCache = new HashMap<Integer, BulkEntrySavingsCache>();
        List<LoanAccountsProductView> loanAccprdViews = new ArrayList<LoanAccountsProductView>();
        List<CustomerAccountView> customerAccViews = new ArrayList<CustomerAccountView>();
        Date meetingDate = Date.valueOf(DateUtils.convertUserToDbFmt(bulkEntryActionForm.getTransactionDate(),
                "dd/MM/yyyy"));
        List<CollectionSheetEntryView> customerViews = new ArrayList<CollectionSheetEntryView>();

        setData(bulkEntry.getBulkEntryParent(), loanAccprdViews, customerAccViews, customerViews);

        /* badness: within this call to setData(), ClientBOs are instantiated
         * and will eventually end up in the HTTP session. Recall that because
         * of thread-local storage in HibernateUtil, each app server worker
         * thread has a different Hibernate session. */
        new BulkEntryBusinessService().setData(customerViews, savingsCache, clients, savingsDepNames, savingsWithNames,
                customerNames, getUserContext(request).getId(), bulkEntry.getReceiptId(), bulkEntry.getPaymentType()
                        .getPaymentTypeId(), bulkEntry.getReceiptDate(), bulkEntry.getTransactionDate(), meetingDate);
        for (Integer accountId : savingsCache.keySet()) {
            BulkEntrySavingsCache bulkEntrySavingsCache = savingsCache.get(accountId);
            if (bulkEntrySavingsCache.getYesNoFlag().equals(YesNoFlag.YES))
                savingsAccounts.add(bulkEntrySavingsCache.getAccount());
        }

        HashMap<Integer, ClientAttendanceDto> clientAttendance = (HashMap<Integer, ClientAttendanceDto>) SessionUtils
                .getAttribute(CollectionSheetEntryConstants.CLIENT_ATTENDANCE, request);
        setClientAttendanceFromForm(clients, meetingDate, clientAttendance, bulkEntryActionForm);

        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CLIENTS, clients, request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.SAVINGS, savingsAccounts, request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.LOANS, loanAccprdViews, request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERACCOUNTS, customerAccViews, request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.ERRORCLIENTS, customerNames, request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.ERRORSAVINGSDEPOSIT, savingsDepNames, request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.ERRORSAVINGSWITHDRAW, savingsWithNames, request);

        return mapping.findForward(CollectionSheetEntryConstants.PREVIEWSUCCESS);
    }

    private void setData(CollectionSheetEntryView parent, List<LoanAccountsProductView> loanAccprdViews,
            List<CustomerAccountView> customerAccViews, List<CollectionSheetEntryView> customerViews) {
        List<CollectionSheetEntryView> children = parent.getCollectionSheetEntryChildren();
        Short levelId = parent.getCustomerDetail().getCustomerLevelId();
        if (null != children) {
            for (CollectionSheetEntryView child : children) {
                setData(child, loanAccprdViews, customerAccViews, customerViews);
            }
        }
        customerViews.add(parent);
        if (!levelId.equals(CustomerLevel.CENTER.getValue())) {
            loanAccprdViews.addAll(parent.getLoanAccountDetails());
        }
        customerAccViews.add(parent.getCustomerAccountDetails());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(CollectionSheetEntryConstants.PREVIUOSSUCCESS);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        String forward = null;
        String methodCalled = request.getParameter(CollectionSheetEntryConstants.METHOD);
        String input = request.getParameter("input");
        if (null != methodCalled) {
            if ("load".equals(input))
                forward = CollectionSheetEntryConstants.LOADSUCCESS;
            else if ("get".equals(input))
                forward = CollectionSheetEntryConstants.GETSUCCESS;
            else if ("preview".equals(input))
                forward = CollectionSheetEntryConstants.PREVIEWSUCCESS;
        }
        if (null != forward)
            return mapping.findForward(forward);
        return null;
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        logger.debug("create ");
        BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
        Date meetingDate = Date.valueOf(DateUtils.convertUserToDbFmt(bulkEntryActionForm.getTransactionDate(),
                "dd/MM/yyyy"));

        UserContext userContext = getUserContext(request);

        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, userContext
                .getPreferredLocale());
        String loan = MessageLookup.getInstance().lookupLabel(ConfigurationConstants.LOAN, userContext);
        String attendance = resources.getString(CollectionSheetEntryConstants.ATTENDANCE);
        String savingsWithdrawal = resources.getString(CollectionSheetEntryConstants.SAVING_WITHDRAWAL);
        String savingsDeposit = resources.getString(CollectionSheetEntryConstants.SAVING_DEPOSITE);
        String acCollection = resources.getString(CollectionSheetEntryConstants.AC_COLLECTION);

        // To enable cache
        BulkEntryBusinessService bulkEntryService = new BulkEntryBusinessService();
        List<String> loanAccountNums = new ArrayList<String>();
        List<String> customerAccountNums = new ArrayList<String>();
        List<String> savingsDepositAccountNums = (List<String>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.ERRORSAVINGSDEPOSIT, request);
        List<String> savingsWithdrawalsAccountNums = (List<String>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.ERRORSAVINGSWITHDRAW, request);
        List<String> customerNames = (List<String>) SessionUtils.getAttribute(CollectionSheetEntryConstants.ERRORCLIENTS, request);
        StringBuilder builder = new StringBuilder();
        ActionErrors actionErrors = new ActionErrors();

        CollectionSheetEntryBO bulkEntry = (CollectionSheetEntryBO) SessionUtils.getAttribute(CollectionSheetEntryConstants.BULKENTRY, request);
        logger.debug("transactionDate " + ((BulkEntryActionForm) form).getTransactionDate());
        Short personnelId = getUserContext(request).getId();
        List<ClientBO> clients = (List<ClientBO>) SessionUtils.getAttribute(CollectionSheetEntryConstants.CLIENTS, request);
        List<SavingsBO> savings = (List<SavingsBO>) SessionUtils.getAttribute(CollectionSheetEntryConstants.SAVINGS, request);
        List<LoanAccountsProductView> loans = (List<LoanAccountsProductView>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.LOANS, request);
        List<CustomerAccountView> customerAccounts = (List<CustomerAccountView>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.CUSTOMERACCOUNTS, request);
        
        try {
            bulkEntryService.saveData(loans, personnelId, bulkEntry.getReceiptId(), bulkEntry.getPaymentType()
                    .getPaymentTypeId(), bulkEntry.getReceiptDate(), bulkEntry.getTransactionDate(), loanAccountNums,
                    savings, savingsDepositAccountNums, clients, customerNames, customerAccounts, customerAccountNums);
//            bulkEntryService.saveDataNonThreaded(loans, personnelId, bulkEntry.getReceiptId(), bulkEntry.getPaymentType()
//                    .getPaymentTypeId(), bulkEntry.getReceiptDate(), bulkEntry.getTransactionDate(), loanAccountNums,
//                    savings, savingsDepositAccountNums, clients, customerNames, customerAccounts, customerAccountNums);
            
        } catch (Exception e) {
            throw e;
        } 

        HashMap<Integer, ClientAttendanceDto> clientAttendance = (HashMap<Integer, ClientAttendanceDto>) SessionUtils
                .getAttribute(CollectionSheetEntryConstants.CLIENT_ATTENDANCE, request);
        clientService.setClientAttendance(setClientAttendanceFromForm(clients, meetingDate, clientAttendance,
                bulkEntryActionForm));

        request.setAttribute(CollectionSheetEntryConstants.CENTER, bulkEntry.getBulkEntryParent().getCustomerDetail()
                .getDisplayName());
        if (loanAccountNums.size() > 0 || savingsDepositAccountNums.size() > 0
                || savingsWithdrawalsAccountNums.size() > 0 || customerAccountNums.size() > 0
                || customerNames.size() > 0) {
            getErrorString(builder, loanAccountNums, loan);
            getErrorString(builder, savingsDepositAccountNums, savingsDeposit);
            getErrorString(builder, savingsWithdrawalsAccountNums, savingsWithdrawal);
            getErrorString(builder, customerAccountNums, acCollection);
            getErrorString(builder, customerNames, attendance);
            builder.append("<br><br>");
            actionErrors.add(CollectionSheetEntryConstants.ERRORSUPDATE, new ActionMessage(CollectionSheetEntryConstants.ERRORSUPDATE,
                    builder.toString()));
            request.setAttribute(Globals.ERROR_KEY, actionErrors);
        }
        // TO clear bulk entry cache in persistence service
        bulkEntryService = null;
        return mapping.findForward(CollectionSheetEntryConstants.CREATESUCCESS);
    }

    private HashMap<Integer, ClientAttendanceDto> getClientAttendance(List<? extends CustomerBO> clients,
            Date meetingDate) {
        ArrayList<ClientAttendanceDto> clientAttendanceDtos = new ArrayList<ClientAttendanceDto>();
        for (CustomerBO client : clients) {
            ClientAttendanceDto clientAttendanceDto = new ClientAttendanceDto(client.getCustomerId(), meetingDate);
            clientAttendanceDtos.add(clientAttendanceDto);
        }
        HashMap<Integer, ClientAttendanceDto> result;
        try {
            result = clientService.getClientAttendance(clientAttendanceDtos);
        } catch (ServiceException e) {
            logger.error("Unexpected error getting Client Attendance.", e);
            result = new HashMap<Integer, ClientAttendanceDto>();
            for (ClientAttendanceDto clientAttendanceDto : clientAttendanceDtos) {
                result.put(clientAttendanceDto.getClientId(), clientAttendanceDto);
            }
        }
        return result;
    }

    private ArrayList<ClientAttendanceDto> setClientAttendanceFromForm(List<? extends CustomerBO> clients,
            Date meetingDate, HashMap<Integer, ClientAttendanceDto> clientAttendance, BulkEntryActionForm form) {
        List<AttendanceType> attendanceFromForm = form.getAttendance();
        ArrayList<ClientAttendanceDto> result = new ArrayList<ClientAttendanceDto>();
        HashMap<Integer, ClientAttendanceDto> clientAttendanceFromForm = clientAttendance;
        if (null == clientAttendance || 0 == clientAttendance.size()) {
            clientAttendanceFromForm = getClientAttendance(clients, meetingDate);
        }
        for (ClientAttendanceDto clientAttendanceDto : clientAttendanceFromForm.values()) {
            AttendanceType attendance = attendanceFromForm.get(clientAttendanceDto.getRow());
            clientAttendanceDto.setAttendance(attendance);
            result.add(clientAttendanceDto);
        }
        return result;
    }

    private void getErrorString(StringBuilder builder, List<String> accountNums, String message) {
        if (accountNums.size() != 0) {
            ListIterator<String> iter = accountNums.listIterator();
            builder.append("<br>");
            builder.append(message + "-	");
            while (iter.hasNext()) {
                builder.append(iter.next());
                if (iter.hasNext())
                    builder.append(", ");
            }
        }
    }

    private List<PersonnelView> loadLoanOfficersForBranch(UserContext userContext, Short branchId) throws Exception {
        return masterService.getListOfActiveLoanOfficers(PersonnelConstants.LOAN_OFFICER, branchId,
                userContext.getId(), userContext.getLevelId());
    }

    /**
     * This method loads either the centers or groups under a particular loan
     * officer as this list of parent customer If the center hierarchy exists,
     * then the list of centers under the loan officer is retrieved as the list
     * of parent customers, else it is the list of groups.
     * 
     */
    private List<CustomerView> loadCustomers(Short personnelId, Short officeId) throws Exception {
        Short customerLevel;
        if (ClientRules.getCenterHierarchyExists())
            customerLevel = Short.valueOf(CustomerLevel.CENTER.getValue());
        else
            customerLevel = Short.valueOf(CustomerLevel.GROUP.getValue());
        List<CustomerView> activeParentUnderLoanOfficer = masterService.getListOfActiveParentsUnderLoanOfficer(
                personnelId, customerLevel, officeId);
        return activeParentUnderLoanOfficer;
    }

    /**
     * This method retrieves the loan officer which was selected from the list
     * of loan officers
     * 
     */
    private PersonnelView getSelectedLO(HttpServletRequest request, ActionForm form) throws Exception {
        BulkEntryActionForm bulkEntryForm = (BulkEntryActionForm) form;
        Short personnelId = Short.valueOf(bulkEntryForm.getLoanOfficerId());
        List<PersonnelView> loanOfficerList = (List<PersonnelView>) SessionUtils.getAttribute(
                CustomerConstants.LOAN_OFFICER_LIST, request);
        for (PersonnelView loanOfficer : loanOfficerList) {
            if (personnelId.shortValue() == loanOfficer.getPersonnelId().shortValue()) {
                return loanOfficer;
            }
        }
        return null;
    }

    /**
     * This method retrieves the branch office which was selected from the list
     * of branch offices
     */
    private OfficeView getSelectedBranchOffice(HttpServletRequest request, ActionForm form) throws Exception {
        BulkEntryActionForm bulkEntryForm = (BulkEntryActionForm) form;
        Short officeId = Short.valueOf(bulkEntryForm.getOfficeId());
        List<OfficeView> branchList = (List<OfficeView>) SessionUtils.getAttribute(
                OfficeConstants.OFFICESBRANCHOFFICESLIST, request);
        for (OfficeView branch : branchList) {
            if (officeId.shortValue() == branch.getOfficeId().shortValue()) {
                return branch;
            }
        }
        return null;
    }

    /**
     * This method retrieves the parent customer which was selected from the
     * list of customers which belong to a particualr branch and have a
     * particular loan officer
     * 
     */
    private CustomerView getSelectedCustomer(HttpServletRequest request, ActionForm form) throws Exception {
        int i = 0;
        BulkEntryActionForm bulkEntryForm = (BulkEntryActionForm) form;
        Integer customerId = Integer.valueOf(bulkEntryForm.getCustomerId());
        List<CustomerView> parentCustomerList = (List<CustomerView>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.CUSTOMERSLIST, request);
        for (i = 0; i < parentCustomerList.size(); i++) {
            if (customerId.intValue() == parentCustomerList.get(i).getCustomerId().intValue()) {
                break;
            }
        }
        return parentCustomerList.get(i);
    }

    /**
     * This method retrieves the payment type which was selected from the list
     * of payement types
     * 
     */
    private PaymentTypeView getSelectedPaymentType(HttpServletRequest request, ActionForm form) throws Exception {
        int i = 0;
        BulkEntryActionForm bulkEntryForm = (BulkEntryActionForm) form;
        Short paymentTypeId = Short.valueOf(bulkEntryForm.getPaymentId());
        List<PaymentTypeEntity> paymentTypeList = (List<PaymentTypeEntity>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.PAYMENT_TYPES_LIST, request);
        for (i = 0; i < paymentTypeList.size(); i++) {
            if (paymentTypeId.shortValue() == paymentTypeList.get(i).getId().shortValue()) {
                break;
            }
        }
        PaymentTypeView paymentType = new PaymentTypeView();
        paymentType.setPaymentTypeId(paymentTypeList.get(i).getId().shortValue());
        paymentType.setPaymentTypeValue(paymentTypeList.get(i).getName());
        return paymentType;
    }

    protected Locale getUserLocale(HttpServletRequest request) {
        Locale locale = null;
        UserContext userContext = getUserContext(request);
        if (null != userContext) {
            locale = userContext.getCurrentLocale();

        }
        return locale;
    }

    private class LockInfo {
        private long lockTime;
        private String userId;

        public LockInfo(long lockTime, String userId) {
            this.lockTime = lockTime;
            this.userId = userId;
        }

        public long getLockTime() {
            return lockTime;
        }

        public void setLockTime(long lockTime) {
            this.lockTime = lockTime;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

    }

}
