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
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.persistance.ClientAttendanceDao;
import org.mifos.application.accounts.loan.persistance.StandardClientAttendanceDao;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.business.SavingsBO;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.business.service.CollectionSheetEntryBusinessService;
import org.mifos.application.collectionsheet.persistence.BulkEntryPersistence;
import org.mifos.application.collectionsheet.struts.actionforms.BulkEntryActionForm;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetDataView;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.application.customer.client.business.AttendanceType;
import org.mifos.application.customer.client.business.ClientAttendanceBO;
import org.mifos.application.customer.client.business.ClientBO;
import org.mifos.application.customer.client.business.service.ClientService;
import org.mifos.application.customer.client.business.service.StandardClientService;
import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.office.util.helpers.OfficeConstants;
import org.mifos.application.personnel.persistence.PersonnelPersistence;
import org.mifos.application.servicefacade.CollectionSheetDataViewAssembler;
import org.mifos.application.servicefacade.CollectionSheetEntryFormDto;
import org.mifos.application.servicefacade.CollectionSheetEntryFormDtoDecorator;
import org.mifos.application.servicefacade.CollectionSheetEntryGridViewAssembler;
import org.mifos.application.servicefacade.CollectionSheetEntryViewAssembler;
import org.mifos.application.servicefacade.CollectionSheetEntryViewTranslator;
import org.mifos.application.servicefacade.CollectionSheetFormEnteredDataDto;
import org.mifos.application.servicefacade.CollectionSheetServiceFacade;
import org.mifos.application.servicefacade.CollectionSheetServiceFacadeWebTier;
import org.mifos.application.servicefacade.ErrorAndCollectionSheetDataDto;
import org.mifos.application.servicefacade.FormEnteredDataAssembler;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CollectionSheetEntryAction extends BaseAction {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.BULKENTRYLOGGER);

    private final CollectionSheetEntryBusinessService collectionSheetEntryService;
    private final CollectionSheetServiceFacade collectionSheetServiceFacade;
    
    // TODO - dependency inject persistence/dao classes into facade using
    // spring.
    private final OfficePersistence officePersistence;
    private final MasterPersistence masterPersistence;
    private final PersonnelPersistence personnelPersistence;
    private final CustomerPersistence customerPersistence;
    private final BulkEntryPersistence bulkEntryPersistence;
    private final ClientAttendanceDao clientAttendanceDao;
    private final ClientService clientService;
    
    public CollectionSheetEntryAction() {
        
        collectionSheetEntryService = new CollectionSheetEntryBusinessService();
        
        // TODO - none of below code is need when DI used with spring
        officePersistence = new OfficePersistence();
        masterPersistence = new MasterPersistence();
        personnelPersistence = new PersonnelPersistence();
        customerPersistence = new CustomerPersistence();
        bulkEntryPersistence = new BulkEntryPersistence();
        clientAttendanceDao = new StandardClientAttendanceDao();
        clientService = new StandardClientService(clientAttendanceDao);
        
        final CollectionSheetEntryViewAssembler collectionSheetEntryViewAssembler = new CollectionSheetEntryViewAssembler(
                bulkEntryPersistence, customerPersistence, clientAttendanceDao);
        
        final CollectionSheetEntryGridViewAssembler collectionSheetEntryGridViewAssembler = new CollectionSheetEntryGridViewAssembler(
                customerPersistence, masterPersistence, clientService);
        
        final CollectionSheetEntryViewTranslator collectionSheetEntryViewTranslator = new CollectionSheetEntryViewTranslator();
        
        collectionSheetServiceFacade = new CollectionSheetServiceFacadeWebTier(officePersistence, masterPersistence,
                personnelPersistence, customerPersistence, collectionSheetEntryViewAssembler,
                collectionSheetEntryGridViewAssembler, collectionSheetEntryService, collectionSheetEntryViewTranslator);
    }

    @Override
    protected BusinessService getService() {
        return collectionSheetEntryService;
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
        logTrackingInfo("load", request);

        // clean up
        request.getSession().setAttribute(CollectionSheetEntryConstants.BULKENTRYACTIONFORM, null);
        request.getSession().setAttribute(Constants.BUSINESS_KEY, null);
        
        final UserContext userContext = getUserContext(request);
        final CollectionSheetEntryFormDto collectionSheetForm = collectionSheetServiceFacade
                .loadAllActiveBranchesAndSubsequentDataIfPossible(userContext);
        
        // settings for action
        request.setAttribute(CollectionSheetEntryConstants.REFRESH, collectionSheetForm.getReloadFormAutomatically());
        
        storeOnRequestCollectionSheetEntryFormDto(request, collectionSheetForm);

        return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadLoanOfficers(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        final BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
        final Short officeId = Short.valueOf(bulkEntryActionForm.getOfficeId());
        final UserContext userContext = getUserContext(request);
        final CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto = retrieveFromRequestCollectionSheetEntryFormDto(request);
        
        final CollectionSheetEntryFormDto latestCollectionSheetEntryFormDto = collectionSheetServiceFacade
                .loadLoanOfficersForBranch(
                officeId, userContext, previousCollectionSheetEntryFormDto);
        
        // add reference data for view
        storeOnRequestCollectionSheetEntryFormDto(request, latestCollectionSheetEntryFormDto);
        
        return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadCustomerList(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        final BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
        final Short personnelId = Short.valueOf(bulkEntryActionForm.getLoanOfficerId());
        final Short officeId = Short.valueOf(bulkEntryActionForm.getOfficeId());
        final CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto = retrieveFromRequestCollectionSheetEntryFormDto(request);
        
        final CollectionSheetEntryFormDto latestCollectionSheetEntryFormDto = collectionSheetServiceFacade
                .loadCustomersForBranchAndLoanOfficer(personnelId, officeId, previousCollectionSheetEntryFormDto);

        // add reference data for view
        storeOnRequestCollectionSheetEntryFormDto(request, latestCollectionSheetEntryFormDto);

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
        
        final BulkEntryActionForm actionForm = (BulkEntryActionForm) form;
        
        final Integer customerId = Integer.valueOf(actionForm.getCustomerId());
        final CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto = retrieveFromRequestCollectionSheetEntryFormDto(request);

        final CollectionSheetEntryFormDto latestCollectionSheetEntryFormDto = collectionSheetServiceFacade
                .loadMeetingDateForCustomer(customerId, previousCollectionSheetEntryFormDto);

        actionForm.setTransactionDate(latestCollectionSheetEntryFormDto.getMeetingDate());
        
        storeOnRequestCollectionSheetEntryFormDto(request, latestCollectionSheetEntryFormDto);

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
        
        logTrackingInfo("get", request, form);
        
        final BulkEntryActionForm collectionSheetEntryActionForm = (BulkEntryActionForm) form;
        final CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto = retrieveFromRequestCollectionSheetEntryFormDto(request);
        
        final CollectionSheetEntryFormDtoDecorator dtoDecorator = new CollectionSheetEntryFormDtoDecorator(
                previousCollectionSheetEntryFormDto);
        final CollectionSheetFormEnteredDataDto formEnteredDataDto = new FormEnteredDataAssembler(
                collectionSheetEntryActionForm, dtoDecorator).toDto();
        
        final CollectionSheetEntryGridDto collectionSheetEntry = collectionSheetServiceFacade.generateCollectionSheetEntryGridView(
                formEnteredDataDto, getUserContext(request));

        storeOnRequestCollectionSheetEntryDto(request, collectionSheetEntry);

        return mapping.findForward(CollectionSheetEntryConstants.GETSUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        
        logTrackingInfo("preview", request, form);
        request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        
        final CollectionSheetEntryGridDto previousCollectionSheetEntryDto = retrieveFromRequestCollectionSheetEntryDto(request);
        final CollectionSheetDataView dataView = new CollectionSheetDataViewAssembler().toDto(request,
                previousCollectionSheetEntryDto);

        final CollectionSheetEntryGridDto collectionSheetEntry = collectionSheetServiceFacade
                .previewCollectionSheetEntry(previousCollectionSheetEntryDto, dataView);

        storeOnRequestCollectionSheetEntryDto(request, collectionSheetEntry);
        
        final ActionErrors errors = new ActionErrors();
        final ActionErrors errorsFromValidation = new CollectionSheetEntryViewPostPreviewValidator().validate(
                collectionSheetEntry.getBulkEntryParent(), errors, getUserContext(request).getPreferredLocale());
        
         if (errorsFromValidation.size() > 0) {
             this.addErrors(request, errorsFromValidation);
        }
        
        final ErrorAndCollectionSheetDataDto errorAndCollectionSheetDataDto = collectionSheetServiceFacade
                .prepareDataForCollectionSheetEntrySave(previousCollectionSheetEntryDto, getUserContext(request)
                        .getId());
        
        storeOnRequestErrorAndCollectionSheetData(request, errorAndCollectionSheetDataDto);

        return mapping.findForward(CollectionSheetEntryConstants.PREVIEWSUCCESS);
    }
    
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws PageExpiredException, InvalidDateException {
        
        logTrackingInfo("create", request, form);
        BulkEntryActionForm bulkEntryActionForm = (BulkEntryActionForm) form;
        Date meetingDate = Date.valueOf(DateUtils.convertUserToDbFmt(bulkEntryActionForm.getTransactionDate(),
                "dd/MM/yyyy"));

        // To enable cache
        CollectionSheetEntryBusinessService bulkEntryService = new CollectionSheetEntryBusinessService();
        List<String> loanAccountNums = new ArrayList<String>();
        List<String> customerAccountNums = new ArrayList<String>();
        List<String> savingsDepositAccountNums = (List<String>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.ERRORSAVINGSDEPOSIT, request);
        List<String> savingsWithdrawalsAccountNums = (List<String>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.ERRORSAVINGSWITHDRAW, request);
        List<String> customerNames = (List<String>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.ERRORCLIENTS, request);
        StringBuilder builder = new StringBuilder();
        ActionErrors actionErrors = new ActionErrors();

        CollectionSheetEntryGridDto bulkEntry = (CollectionSheetEntryGridDto) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.BULKENTRY, request);
        logger.debug("transactionDate " + ((BulkEntryActionForm) form).getTransactionDate());
        Short personnelId = getUserContext(request).getId();
        List<ClientBO> clients = (List<ClientBO>) SessionUtils.getAttribute(CollectionSheetEntryConstants.CLIENTS,
                request);
        List<SavingsBO> savings = (List<SavingsBO>) SessionUtils.getAttribute(CollectionSheetEntryConstants.SAVINGS,
                request);
        List<LoanAccountsProductView> loans = (List<LoanAccountsProductView>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.LOANS, request);
        List<CustomerAccountView> customerAccounts = (List<CustomerAccountView>) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.CUSTOMERACCOUNTS, request);
        List<CollectionSheetEntryView> collectionSheetEntryViews = (List<CollectionSheetEntryView>) SessionUtils
                .getAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY, request);

        String logMsg = "before saveData().";
        logMsg += " session id:" + request.getSession().getId();
        logMsg += ", date:" + bulkEntry.getTransactionDate();
        logMsg += ", office id:" + bulkEntryActionForm.getOfficeId();

        OfficeView officeView = bulkEntry.getOffice();
        if (null != officeView) {
            logMsg += ", office name:" + officeView.getOfficeName();
        }
        logMsg += ", center id:" + bulkEntryActionForm.getCustomerId();
        String attendanceSummary = getAttendanceSummary(clients, bulkEntry.getTransactionDate());
        if (null != attendanceSummary) {
            logMsg += ", attendance:" + attendanceSummary;
        }
        logMsg += ".";
        logger.info(logMsg);

        long beforeSaveData = System.currentTimeMillis();

        bulkEntryService.saveData(loans, personnelId, bulkEntry.getReceiptId(), bulkEntry.getPaymentType()
                .getId(),
                bulkEntry.getReceiptDate(), bulkEntry.getTransactionDate(), loanAccountNums,
                savings, savingsDepositAccountNums, clients, customerNames, customerAccounts, customerAccountNums,
                collectionSheetEntryViews);

        logger.info("after saveData(). session id:" + request.getSession().getId() + ". "
                + getUpdateTotalsString(clients, savings, loans, customerAccounts)
                + ". Saving bulk entry data ran for approximately "
                + ((System.currentTimeMillis() - beforeSaveData) / 1000.0) + " seconds.");

        request.setAttribute(CollectionSheetEntryConstants.CENTER, bulkEntry.getBulkEntryParent().getCustomerDetail()
                .getDisplayName());
        
        
        UserContext userContext = getUserContext(request);
        ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, userContext
                .getPreferredLocale());
        String loan = MessageLookup.getInstance().lookupLabel(ConfigurationConstants.LOAN, userContext);
        String attendance = resources.getString(CollectionSheetEntryConstants.ATTENDANCE);
        String savingsWithdrawal = resources.getString(CollectionSheetEntryConstants.SAVING_WITHDRAWAL);
        String savingsDeposit = resources.getString(CollectionSheetEntryConstants.SAVING_DEPOSITE);
        String acCollection = resources.getString(CollectionSheetEntryConstants.AC_COLLECTION);
        
        if (loanAccountNums.size() > 0 || savingsDepositAccountNums.size() > 0
                || savingsWithdrawalsAccountNums.size() > 0 || customerAccountNums.size() > 0
                || customerNames.size() > 0) {
            getErrorString(builder, loanAccountNums, loan);
            getErrorString(builder, savingsDepositAccountNums, savingsDeposit);
            getErrorString(builder, savingsWithdrawalsAccountNums, savingsWithdrawal);
            getErrorString(builder, customerAccountNums, acCollection);
            getErrorString(builder, customerNames, attendance);
            builder.append("<br><br>");
            actionErrors.add(CollectionSheetEntryConstants.ERRORSUPDATE, new ActionMessage(
                    CollectionSheetEntryConstants.ERRORSUPDATE, builder.toString()));
            request.setAttribute(Globals.ERROR_KEY, actionErrors);
        }
        // TO clear bulk entry cache in persistence service
        bulkEntryService = null;
        return mapping.findForward(CollectionSheetEntryConstants.CREATESUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(CollectionSheetEntryConstants.PREVIOUSSUCCESS);
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
            if ("load".equals(input)) {
                forward = CollectionSheetEntryConstants.LOADSUCCESS;
            } else if ("get".equals(input)) {
                forward = CollectionSheetEntryConstants.GETSUCCESS;
            } else if ("preview".equals(input)) {
                forward = CollectionSheetEntryConstants.PREVIEWSUCCESS;
            }
        }
        if (null != forward) {
            return mapping.findForward(forward);
        }
        return null;
    }

    private void storeOnRequestErrorAndCollectionSheetData(HttpServletRequest request,
            final ErrorAndCollectionSheetDataDto errorAndCollectionSheetDataDto) throws PageExpiredException {
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY,
                errorAndCollectionSheetDataDto.getDecomposedViews().getParentCollectionSheetEntryViews(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.LOANS, errorAndCollectionSheetDataDto
                .getDecomposedViews().getLoanAccountViews(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERACCOUNTS,
                errorAndCollectionSheetDataDto.getDecomposedViews().getCustomerAccountViews(), request);

        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CLIENTS, errorAndCollectionSheetDataDto
                .getClients(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.SAVINGS, errorAndCollectionSheetDataDto
                .getSavingsAccounts(), request);

        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.ERRORCLIENTS, errorAndCollectionSheetDataDto
                .getErrors().getCustomerNames(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.ERRORSAVINGSDEPOSIT,
                errorAndCollectionSheetDataDto.getErrors().getSavingsDepNames(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.ERRORSAVINGSWITHDRAW,
                errorAndCollectionSheetDataDto.getErrors().getSavingsWithNames(), request);
    }
    
    private CollectionSheetEntryFormDto retrieveFromRequestCollectionSheetEntryFormDto(HttpServletRequest request)
            throws PageExpiredException {
        return (CollectionSheetEntryFormDto) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO, request);
    }
    
    private void storeOnRequestCollectionSheetEntryFormDto(HttpServletRequest request,
            final CollectionSheetEntryFormDto latestCollectionSheetEntryFormDto) throws PageExpiredException {
        
        SessionUtils.setAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO,
                latestCollectionSheetEntryFormDto, request);
        
        // support old way of managing reference data for now.
        SessionUtils.setCollectionAttribute(OfficeConstants.OFFICESBRANCHOFFICESLIST, latestCollectionSheetEntryFormDto
                .getActiveBranchesList(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.PAYMENT_TYPES_LIST,
                latestCollectionSheetEntryFormDto
                .getPaymentTypesList(), request);
        SessionUtils.setCollectionAttribute(CustomerConstants.LOAN_OFFICER_LIST, latestCollectionSheetEntryFormDto
                .getLoanOfficerList(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERSLIST,
                latestCollectionSheetEntryFormDto
                .getCustomerList(), request);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISCENTERHIERARCHYEXISTS,
                latestCollectionSheetEntryFormDto
                .getCenterHierarchyExists(), request);
        SessionUtils.setAttribute(CollectionSheetEntryConstants.ISBACKDATEDTRXNALLOWED,
                latestCollectionSheetEntryFormDto
                .getBackDatedTransactionAllowed(), request);
        SessionUtils.setAttribute("LastMeetingDate", latestCollectionSheetEntryFormDto.getMeetingDate(), request);
    }
    
    private CollectionSheetEntryGridDto retrieveFromRequestCollectionSheetEntryDto(HttpServletRequest request)
            throws PageExpiredException {
        return (CollectionSheetEntryGridDto) SessionUtils.getAttribute(CollectionSheetEntryConstants.BULKENTRY, request);
    }
    
    private void storeOnRequestCollectionSheetEntryDto(HttpServletRequest request,
            CollectionSheetEntryGridDto collectionSheetEntry) throws PageExpiredException {
        
        SessionUtils.setAttribute(CollectionSheetEntryConstants.BULKENTRY, collectionSheetEntry, request);
        
        SessionUtils.setMapAttribute(CollectionSheetEntryConstants.CLIENT_ATTENDANCE, collectionSheetEntry
                .getClientAttendance(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERATTENDANCETYPES, collectionSheetEntry
                .getAttendanceTypesList(), request);
    }

    private String getAttendanceSummary(List<ClientBO> clients, Date meetingDate) {
        List<String> attendanceCodes = new ArrayList<String>();
        for (ClientBO client : clients) {
            if (null != client.getClientAttendanceForMeeting(meetingDate)) {
                ClientAttendanceBO clientAttendanceBO = client.getClientAttendanceForMeeting(meetingDate);
                AttendanceType attendanceType = clientAttendanceBO.getAttendanceAsEnum();
                switch (attendanceType) {
                case PRESENT:
                    attendanceCodes.add("P");
                    break;
                case ABSENT:
                    attendanceCodes.add("A");
                    break;
                case APPROVED_LEAVE:
                    attendanceCodes.add("AA");
                    break;
                case LATE:
                    attendanceCodes.add("L");
                    break;
                default:
                    attendanceCodes.add("x");
                    break;
                }
            }
        }
        return StringUtils.join(attendanceCodes, "_");
    }

    private String getUpdateTotalsString(List<ClientBO> clients, List<SavingsBO> savings,
            List<LoanAccountsProductView> loans, List<CustomerAccountView> customerAccounts) {
        String totals = "";
        totals += " clients:" + clients.size();
        totals += ", savings:" + savings.size();
        totals += ", loans:" + loans.size();
        totals += ", collections:" + customerAccounts.size();
        return totals;
    }

    private void logTrackingInfo(String actionMethodName, HttpServletRequest request, ActionForm form) {
        BulkEntryActionForm bulkEntryForm = (BulkEntryActionForm) form;
        StringBuilder message = getLogMessage(actionMethodName, request);
        String receiptId = bulkEntryForm.getReceiptId();
        message.append(", receipt Id:" + receiptId);
        String centerId = bulkEntryForm.getCustomerId();
        message.append(", center:" + centerId);
        message.append(", ");
        logger.info(message.toString());
    }

    private void logTrackingInfo(String actionMethodName, HttpServletRequest request) {
        StringBuilder message = getLogMessage(actionMethodName, request);
        message.append(", ");
        logger.info(message.toString());
    }

    private StringBuilder getLogMessage(String actionMethodName, HttpServletRequest request) {
        UserContext userContext = getUserContext(request);
        StringBuilder message = new StringBuilder();
        message.append(", url:" + request.getRequestURI());
        message.append(", action:" + actionMethodName);
        message.append(", session id:" + request.getSession().getId());
        message.append(", user id:" + userContext.getId());
        message.append(", username:" + userContext.getName());
        message.append(", branch id:" + userContext.getBranchGlobalNum());
        return message;
    }

    private void getErrorString(StringBuilder builder, List<String> accountNums, String message) {
        if (accountNums.size() != 0) {
            ListIterator<String> iter = accountNums.listIterator();
            builder.append("<br>");
            builder.append(message + "-	");
            while (iter.hasNext()) {
                builder.append(iter.next());
                if (iter.hasNext()) {
                    builder.append(", ");
                }
            }
        }
    }
    
    /**
     * used by JSP functions in view.
     */
    protected Locale getUserLocale(HttpServletRequest request) {
        Locale locale = null;
        UserContext userContext = getUserContext(request);
        if (null != userContext) {
            locale = userContext.getCurrentLocale();
        }
        return locale;
    }
}
