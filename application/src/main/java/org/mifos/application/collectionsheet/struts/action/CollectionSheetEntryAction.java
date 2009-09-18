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

import java.math.BigDecimal;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.accounts.loan.util.helpers.LoanAccountsProductView;
import org.mifos.application.accounts.savings.util.helpers.SavingsAccountView;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryGridDto;
import org.mifos.application.collectionsheet.business.CollectionSheetEntryView;
import org.mifos.application.collectionsheet.struts.actionforms.BulkEntryActionForm;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetDataView;
import org.mifos.application.collectionsheet.util.helpers.CollectionSheetEntryConstants;
import org.mifos.application.customer.util.helpers.CustomerAccountView;
import org.mifos.application.customer.util.helpers.CustomerConstants;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.office.business.OfficeView;
import org.mifos.application.office.util.helpers.OfficeConstants;
import org.mifos.application.servicefacade.CollectionSheetDataViewAssembler;
import org.mifos.application.servicefacade.CollectionSheetEntryDecomposedView;
import org.mifos.application.servicefacade.CollectionSheetEntryFormDto;
import org.mifos.application.servicefacade.CollectionSheetEntryFormDtoDecorator;
import org.mifos.application.servicefacade.CollectionSheetEntryViewTranslator;
import org.mifos.application.servicefacade.CollectionSheetErrorsView;
import org.mifos.application.servicefacade.CollectionSheetFormEnteredDataDto;
import org.mifos.application.servicefacade.CollectionSheetServiceFacade;
import org.mifos.application.servicefacade.DependencyInjectedServiceLocator;
import org.mifos.application.servicefacade.FormEnteredDataAssembler;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.components.configuration.business.Configuration;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.SecurityConstants;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public class CollectionSheetEntryAction extends BaseAction {

    private static final MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.BULKENTRYLOGGER);

    private final CollectionSheetServiceFacade collectionSheetServiceFacade = DependencyInjectedServiceLocator
            .locateCollectionSheetServiceFacade();
    
    public CollectionSheetEntryAction() {
    }

    @Override
    protected BusinessService getService() {
        return new DummyBusinessService();
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
    protected boolean skipActionFormToBusinessObjectConversion(final String method) {
        return method.equals(CollectionSheetEntryConstants.CREATEMETHOD);
    }

    /**
     * This method is called before the load page for center is called It sets
     * this information in session and context.This should be removed after
     * center was successfully created.
     */
    @TransactionDemarcate(saveToken = true)
    public ActionForward load(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        logTrackingInfo("load", request);

        // clean up
        request.getSession().setAttribute(CollectionSheetEntryConstants.BULKENTRYACTIONFORM, null);
        request.getSession().setAttribute(Constants.BUSINESS_KEY, null);
        
        final UserContext userContext = getUserContext(request);
        final CollectionSheetEntryFormDto collectionSheetForm = collectionSheetServiceFacade
                .loadAllActiveBranchesAndSubsequentDataIfApplicable(userContext);
        
        // settings for action
        request.setAttribute(CollectionSheetEntryConstants.REFRESH, collectionSheetForm.getReloadFormAutomatically());
        
        storeOnRequestCollectionSheetEntryFormDto(request, collectionSheetForm);

        return mapping.findForward(CollectionSheetEntryConstants.LOADSUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadLoanOfficers(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        
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
    public ActionForward loadCustomerList(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
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
    public ActionForward getLastMeetingDateForCustomer(final ActionMapping mapping, final ActionForm form,
            final HttpServletRequest request, final HttpServletResponse response) throws Exception {
        
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
    public ActionForward get(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        
        logTrackingInfo("get", request, form);
        
        final BulkEntryActionForm collectionSheetEntryActionForm = (BulkEntryActionForm) form;
        final CollectionSheetEntryFormDto previousCollectionSheetEntryFormDto = retrieveFromRequestCollectionSheetEntryFormDto(request);
        
        final CollectionSheetEntryFormDtoDecorator dtoDecorator = new CollectionSheetEntryFormDtoDecorator(
                previousCollectionSheetEntryFormDto);
        final CollectionSheetFormEnteredDataDto formEnteredDataDto = new FormEnteredDataAssembler(
                collectionSheetEntryActionForm, dtoDecorator).toDto();
        
        final MifosCurrency currency = Configuration.getInstance().getSystemConfig().getCurrency();
        
        final CollectionSheetEntryGridDto collectionSheetEntry = collectionSheetServiceFacade
                .generateCollectionSheetEntryGridView(formEnteredDataDto, currency);

        storeOnRequestCollectionSheetEntryDto(request, collectionSheetEntry);

        return mapping.findForward(CollectionSheetEntryConstants.GETSUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward preview(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        
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
        
        return mapping.findForward(CollectionSheetEntryConstants.PREVIEWSUCCESS);
    }
    
    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward create(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws PageExpiredException {
        
        logTrackingInfo("create", request, form);
        final BulkEntryActionForm collectionSheetActionForm = (BulkEntryActionForm) form;
        
        final CollectionSheetEntryGridDto previousCollectionSheetEntryDto = retrieveFromRequestCollectionSheetEntryDto(request);
        
        final CollectionSheetEntryDecomposedView decomposedViews = new CollectionSheetEntryViewTranslator()
                .toDecomposedView(previousCollectionSheetEntryDto.getBulkEntryParent());

        logBeforeSave(request, collectionSheetActionForm, previousCollectionSheetEntryDto);
        final long beforeSaveData = System.currentTimeMillis();
        
        final CollectionSheetErrorsView collectionSheetErrors = this.collectionSheetServiceFacade.saveCollectionSheet(
                previousCollectionSheetEntryDto, decomposedViews, getUserContext(request).getId());
        
        storeOnRequestErrorAndCollectionSheetData(request, decomposedViews, collectionSheetErrors);
        
        logAfterSave(request, decomposedViews,
                beforeSaveData);
        
        request.setAttribute(CollectionSheetEntryConstants.CENTER, previousCollectionSheetEntryDto.getBulkEntryParent().getCustomerDetail()
                .getDisplayName());
        
        setErrorMessagesIfErrorsExist(request, collectionSheetErrors);
        
        return mapping.findForward(CollectionSheetEntryConstants.CREATESUCCESS);
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward previous(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return mapping.findForward(CollectionSheetEntryConstants.PREVIOUSSUCCESS);
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
        return mapping.findForward(ActionForwards.cancel_success.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward validate(final ActionMapping mapping, final ActionForm form, final HttpServletRequest request,
            final HttpServletResponse response) throws Exception {
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
    
    private void setErrorMessagesIfErrorsExist(final HttpServletRequest request,
            final CollectionSheetErrorsView collectionSheetErrors) {
        final UserContext userContext = getUserContext(request);
        final ResourceBundle resources = ResourceBundle.getBundle(FilePaths.BULKENTRY_RESOURCE, userContext
                .getPreferredLocale());
        final String savingsWithdrawal = resources.getString(CollectionSheetEntryConstants.SAVING_WITHDRAWAL);
        final String savingsDeposit = resources.getString(CollectionSheetEntryConstants.SAVING_DEPOSIT);
        final String acCollection = resources.getString(CollectionSheetEntryConstants.AC_COLLECTION);

        final StringBuilder builder = new StringBuilder();
        final ActionErrors actionErrors = new ActionErrors();
        if (collectionSheetErrors.getSavingsDepNames().size() > 0
                || collectionSheetErrors.getSavingsWithNames().size() > 0
                || collectionSheetErrors.getCustomerAccountNumbers().size() > 0) {
            getErrorString(builder, collectionSheetErrors.getSavingsDepNames(), savingsDeposit);
            getErrorString(builder, collectionSheetErrors.getSavingsWithNames(), savingsWithdrawal);
            getErrorString(builder, collectionSheetErrors.getCustomerAccountNumbers(), acCollection);
            builder.append("<br><br>");
            actionErrors.add(CollectionSheetEntryConstants.ERRORSUPDATE, new ActionMessage(
                    CollectionSheetEntryConstants.ERRORSUPDATE, builder.toString()));
            request.setAttribute(Globals.ERROR_KEY, actionErrors);
        }
    }
    
    private void logAfterSave(final HttpServletRequest request,
            final CollectionSheetEntryDecomposedView decomposedViews,
            final long beforeSaveData) {
        logger.info("after saveData(). session id:" + request.getSession().getId() + ". "
                + getUpdateTotalsString(decomposedViews)
                + ". Saving bulk entry data ran for approximately "
                + (System.currentTimeMillis() - beforeSaveData) / 1000.0 + " seconds."
                + getCustomerAccountViewLogs(decomposedViews)
                + getLoanAccountsProductView(decomposedViews));
        
    }

    private String getCustomerAccountViewLogs(CollectionSheetEntryDecomposedView decomposedViews){
        String logMsg ="\nCustomer Accounts Log";
        Double totalAmountDue = 0.0;
        Double totalCustomerAccountAmountEntered = 0.0;
        for (CustomerAccountView customerAccountView : decomposedViews.getCustomerAccountViews()) {
            logMsg += "\nCustomer ID:"+customerAccountView.getCustomerId();
            logMsg += "\nAccount ID:"+customerAccountView.getAccountId();
            totalAmountDue += customerAccountView.getTotalAmountDue().getAmount().doubleValue();
            logMsg += "\nAmount Due:"+customerAccountView.getTotalAmountDue();
            try {
            totalCustomerAccountAmountEntered += Double.parseDouble(customerAccountView.getCustomerAccountAmountEntered());
            } catch (Exception e) {
                logger.error("Error in parsing customer account amount entered !!!", e);
            }
            logMsg += "\nCustomer Account Amount Entered:"+customerAccountView.getCustomerAccountAmountEntered();
        }
        logMsg += "\nTotal Amount Due:"+totalAmountDue;
        logMsg += "\nTotal Customer Account Amount Entered:"+totalCustomerAccountAmountEntered;
        logMsg += "\n";
        return logMsg;    
    }
    
    private String getLoanAccountsProductView(CollectionSheetEntryDecomposedView decomposedViews){
        String logMsg ="\nLoan Accounts Log";
        Double totalDisBursementAmountEntered = 0.0;
        Double totalEnteredAmount = 0.0;
        Double totalAmountDue = 0.0;
        Double totalDisbursalAmountDue = 0.0;
        Double totalDisbursalAmount = 0.0;
        for (LoanAccountsProductView loanAccountView : decomposedViews.getLoanAccountViews()) {
            logMsg += "\nProduct Offering ID:"+loanAccountView.getPrdOfferingId();
            logMsg += "\nProduct Offering Short Name:"+loanAccountView.getPrdOfferingShortName();
            try{
            totalDisBursementAmountEntered += Double.parseDouble(loanAccountView.getDisBursementAmountEntered());
        } catch (Exception e) {
            logger.error("Error in parsing disbursement amount entered !!!", e);
        }
            logMsg += "\nDisbursement Amount Entered:"+loanAccountView.getDisBursementAmountEntered();
            try {
            totalEnteredAmount += Double.parseDouble(loanAccountView.getEnteredAmount());
        } catch (Exception e) {
            logger.error("Error in parsing disbursement amount entered !!!", e);
        }
            logMsg += "\nEntered Amount:"+loanAccountView.getEnteredAmount();
            totalAmountDue += loanAccountView.getTotalAmountDue();
            logMsg += "\nAmount Due:"+loanAccountView.getTotalAmountDue();
            totalDisbursalAmountDue += loanAccountView.getTotalDisbursalAmountDue();
            logMsg += "\nDisbursal Amount Due:"+loanAccountView.getTotalDisbursalAmountDue();
            totalDisbursalAmount += loanAccountView.getTotalDisburseAmount();
            logMsg += "\nDisburse Amount:"+loanAccountView.getTotalDisburseAmount();
        }
        logMsg += "\nTotal Disbursement Amount Entered:"+totalDisBursementAmountEntered;
        logMsg += "\nTotal Entered Amount:"+totalEnteredAmount;
        logMsg += "\nTotal Amount Due:"+totalAmountDue;
        logMsg += "\nTotal Disbursal Amount Due:"+totalDisbursalAmountDue;
        logMsg += "\nTotal Disbursal Amount:"+totalDisbursalAmount;
        logMsg += "\n";
        return logMsg; 
    }
    
    private void logBeforeSave(final HttpServletRequest request, final BulkEntryActionForm bulkEntryActionForm,
            final CollectionSheetEntryGridDto bulkEntry) {
        String logMsg = "before saveData().";
        logMsg += " session id:" + request.getSession().getId();
        logMsg += ", date:" + bulkEntry.getTransactionDate();
        logMsg += ", office id:" + bulkEntryActionForm.getOfficeId();

        final OfficeView officeView = bulkEntry.getOffice();
        if (null != officeView) {
            logMsg += ", office name:" + officeView.getOfficeName();
        }
        logMsg += ", center id:" + bulkEntryActionForm.getCustomerId();
        logMsg += ".";
        logger.info(logMsg);
    }
    
    private void storeOnRequestErrorAndCollectionSheetData(final HttpServletRequest request,
            final CollectionSheetEntryDecomposedView decomposedViews,
            final CollectionSheetErrorsView collectionSheetErrors) throws PageExpiredException {
        
        // support old way for now.
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY,
                decomposedViews
                .getParentCollectionSheetEntryViews(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.LOANS, decomposedViews.getLoanAccountViews(),
                request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERACCOUNTS,
                decomposedViews
                .getCustomerAccountViews(), request);

        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.ERRORSAVINGSDEPOSIT,
                collectionSheetErrors
                .getSavingsDepNames(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.ERRORSAVINGSWITHDRAW,
                collectionSheetErrors
                .getSavingsWithNames(), request);
    }
    
    private CollectionSheetEntryFormDto retrieveFromRequestCollectionSheetEntryFormDto(final HttpServletRequest request)
            throws PageExpiredException {
        return (CollectionSheetEntryFormDto) SessionUtils.getAttribute(
                CollectionSheetEntryConstants.COLLECTION_SHEET_ENTRY_FORM_DTO, request);
    }
    
    private void storeOnRequestCollectionSheetEntryFormDto(final HttpServletRequest request,
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
    
    private CollectionSheetEntryGridDto retrieveFromRequestCollectionSheetEntryDto(final HttpServletRequest request)
            throws PageExpiredException {
        return (CollectionSheetEntryGridDto) SessionUtils.getAttribute(CollectionSheetEntryConstants.BULKENTRY, request);
    }
    
    private void storeOnRequestCollectionSheetEntryDto(final HttpServletRequest request,
            final CollectionSheetEntryGridDto collectionSheetEntry) throws PageExpiredException {
        
        SessionUtils.setAttribute(CollectionSheetEntryConstants.BULKENTRY, collectionSheetEntry, request);

        // SessionUtils.setMapAttribute(CollectionSheetEntryConstants.CLIENT_ATTENDANCE,
        // collectionSheetEntry
        // .getClientAttendance(), request);
        SessionUtils.setCollectionAttribute(CollectionSheetEntryConstants.CUSTOMERATTENDANCETYPES, collectionSheetEntry
                .getAttendanceTypesList(), request);
    }

    private String getUpdateTotalsString(final CollectionSheetEntryDecomposedView decomposedViews) {
        String totals = "";
        totals += ", loans:" + decomposedViews.getLoanAccountViews().size();
        totals += ", collections:"
                + decomposedViews.getCustomerAccountViews().size();
        return totals;
    }

    private void logTrackingInfo(final String actionMethodName, final HttpServletRequest request, final ActionForm form) {
        BulkEntryActionForm bulkEntryForm = (BulkEntryActionForm) form;
        StringBuilder message = getLogMessage(actionMethodName, request);
        String receiptId = bulkEntryForm.getReceiptId();
        message.append(", receipt Id:" + receiptId);
        String centerId = bulkEntryForm.getCustomerId();
        message.append(", center:" + centerId);
        message.append(", ");
        logger.info(message.toString());
    }

    private void logTrackingInfo(final String actionMethodName, final HttpServletRequest request) {
        StringBuilder message = getLogMessage(actionMethodName, request);
        message.append(", ");
        logger.info(message.toString());
    }

    private StringBuilder getLogMessage(final String actionMethodName, final HttpServletRequest request) {
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

    private void getErrorString(final StringBuilder builder, final List<String> accountNums, final String message) {
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
    protected Locale getUserLocale(final HttpServletRequest request) {
        Locale locale = null;
        UserContext userContext = getUserContext(request);
        if (null != userContext) {
            locale = userContext.getCurrentLocale();
        }
        return locale;
    }
    
    private class DummyBusinessService implements BusinessService {

        @Override
        public BusinessObject getBusinessObject(@SuppressWarnings("unused") final UserContext userContext) {
            return null;
        }

    }
}
