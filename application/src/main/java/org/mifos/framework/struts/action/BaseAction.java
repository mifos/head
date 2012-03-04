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

package org.mifos.framework.struts.action;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.jstl.core.Config;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.Validator;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;
import org.hibernate.HibernateException;
import org.mifos.accounts.acceptedpaymenttype.persistence.LegacyAcceptedPaymentTypeDao;
import org.mifos.accounts.fees.persistence.FeeDao;
import org.mifos.accounts.fund.persistence.FundDao;
import org.mifos.accounts.fund.servicefacade.FundServiceFacade;
import org.mifos.accounts.loan.persistance.LoanDao;
import org.mifos.accounts.penalties.persistence.PenaltyDao;
import org.mifos.accounts.persistence.LegacyAccountDao;
import org.mifos.accounts.productdefinition.persistence.LoanProductDao;
import org.mifos.accounts.productdefinition.persistence.SavingsProductDao;
import org.mifos.accounts.savings.persistence.SavingsDao;
import org.mifos.accounts.servicefacade.AccountServiceFacade;
import org.mifos.application.admin.servicefacade.CheckListServiceFacade;
import org.mifos.application.admin.servicefacade.FeeServiceFacade;
import org.mifos.application.admin.servicefacade.HolidayServiceFacade;
import org.mifos.application.admin.servicefacade.InvalidDateException;
import org.mifos.application.admin.servicefacade.MonthClosingServiceFacade;
import org.mifos.application.admin.servicefacade.OfficeServiceFacade;
import org.mifos.application.admin.servicefacade.PersonnelServiceFacade;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.application.importexport.servicefacade.ImportTransactionsServiceFacade;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.persistence.LegacyMasterDao;
import org.mifos.application.servicefacade.ApplicationContextProvider;
import org.mifos.application.servicefacade.CenterServiceFacade;
import org.mifos.application.servicefacade.ClientServiceFacade;
import org.mifos.application.servicefacade.CustomerServiceFacade;
import org.mifos.application.servicefacade.GroupServiceFacade;
import org.mifos.application.servicefacade.LoanAccountServiceFacade;
import org.mifos.application.servicefacade.LoanServiceFacade;
import org.mifos.application.servicefacade.MeetingServiceFacade;
import org.mifos.application.servicefacade.NewLoginServiceFacade;
import org.mifos.application.servicefacade.SavingsServiceFacade;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.AccountingRules;
import org.mifos.config.persistence.ApplicationConfigurationDao;
import org.mifos.customers.office.persistence.OfficeDao;
import org.mifos.customers.persistence.CustomerDao;
import org.mifos.customers.personnel.persistence.LegacyPersonnelDao;
import org.mifos.customers.personnel.persistence.PersonnelDao;
import org.mifos.framework.business.AbstractBusinessObject;
import org.mifos.framework.business.LogUtils;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.audit.business.service.AuditBusinessService;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.audit.util.helpers.AuditInterceptor;
import org.mifos.framework.components.batchjobs.MifosBatchJob;
import org.mifos.framework.components.fieldConfiguration.persistence.LegacyFieldConfigurationDao;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.ValueObjectConversionException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ConversionUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.ServletUtils;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.reports.admindocuments.persistence.LegacyAdminDocAccStateMixDao;
import org.mifos.reports.admindocuments.persistence.LegacyAdminDocumentDao;
import org.mifos.security.AuthenticationAuthorizationServiceFacade;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.rolesandpermission.persistence.LegacyRolesPermissionsDao;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseAction extends DispatchAction {

    private static final Logger logger = LoggerFactory.getLogger(BaseAction.class);

    protected BusinessService getService() throws ServiceException {
        return null;
    }

    protected PersonnelDao personnelDao;
    protected OfficeDao officeDao;
    protected CustomerDao customerDao;
    protected SavingsDao savingsDao;
    protected LoanDao loanDao;
    protected LoanProductDao loanProductDao;
    protected SavingsProductDao savingsProductDao;
    protected FeeDao feeDao;
    protected PenaltyDao penaltyDao;
    protected FundDao fundDao;
    protected ApplicationConfigurationDao applicationConfigurationDao;

    protected NewLoginServiceFacade loginServiceFacade;
    protected PersonnelServiceFacade personnelServiceFacade;
    protected CustomerServiceFacade customerServiceFacade;
    protected CenterServiceFacade centerServiceFacade;
    protected GroupServiceFacade groupServiceFacade;
    protected ClientServiceFacade clientServiceFacade;
    protected AccountServiceFacade accountServiceFacade;
    protected MeetingServiceFacade meetingServiceFacade;
    protected LoanAccountServiceFacade loanAccountServiceFacade;
    protected MonthClosingServiceFacade monthClosingServiceFacade;
    protected SavingsServiceFacade savingsServiceFacade;
    protected HolidayServiceFacade holidayServiceFacade;
    protected OfficeServiceFacade officeServiceFacade;
    protected FeeServiceFacade feeServiceFacade;
    protected FundServiceFacade fundServiceFacade;
    protected AuthenticationAuthorizationServiceFacade authenticationAuthorizationServiceFacade;
    protected ImportTransactionsServiceFacade importTransactionsServiceFacade;
    protected CheckListServiceFacade checkListServiceFacade;

    protected LegacyMasterDao legacyMasterDao;
    protected LegacyAdminDocumentDao legacyAdminDocumentDao;
    protected LegacyFieldConfigurationDao legacyFieldConfigurationDao;
    protected LegacyAdminDocAccStateMixDao legacyAdminDocAccStateMixDao;
    protected LegacyRolesPermissionsDao legacyRolesPermissionsDao;
    protected LegacyAccountDao legacyAccountDao;
    protected LegacyAcceptedPaymentTypeDao legacyAcceptedPaymentTypeDao;
    protected LegacyPersonnelDao legacyPersonnelDao;

    // non domain app
    protected LoanServiceFacade loanServiceFacade;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        configureDaoBeans();
        configureServiceFacadeBeans();
        configureLegacyDaoBeans();

        checkLocaleContext(request);

        if (MifosBatchJob.isBatchJobRunningThatRequiresExclusiveAccess()) {
            return logout(mapping, request);
        }
        ShutdownManager shutdownManager = (ShutdownManager) ServletUtils.getGlobal(request, ShutdownManager.class.getName());
        if (shutdownManager.isShutdownDone()) {
            return shutdown(mapping, request);
        }
        if (shutdownManager.isInShutdownCountdownNotificationThreshold()) {
            request.setAttribute("shutdownIsImminent", true);
        }
        TransactionDemarcate annotation = getTransaction(form, request);
        preExecute(form, request, annotation);
        ActionForward forward = super.execute(mapping, form, request, response);
        // TODO: passing 'true' to postExecute guarantees that the session will
        // be closed still working through resolving issues related to enforcing
        // this postExecute(request, annotation, true);
        postExecute(request, annotation, isCloseSessionAnnotationPresent(form, request));
        return forward;
    }

    private void checkLocaleContext(HttpServletRequest request) {
        // Legacy struts actions and UserContext uses this
        // Eg. LoanPrdActionForm.validateInterestGLCode
        UserContext userContext = (UserContext) request.getSession().getAttribute(Constants.USERCONTEXT);
        if (userContext != null) {
            Locale locale = personnelServiceFacade.getUserPreferredLocale();
            userContext.setPreferredLocale(locale);
            HttpSession session = request.getSession(false);
            if(session != null) {
                // Struts tags like html-el
                session.setAttribute(Globals.LOCALE_KEY, locale);
                // struts form validator xml rules
                // see http://struts.apache.org/1.2.4/userGuide/dev_validator.html
                session.setAttribute(Validator.LOCALE_PARAM,locale);
             // reset the JSTL locale
                Config.set(session, Config.FMT_LOCALE, locale);
            }
        }
    }

    private <T extends Object> T getBean(Class<T> clazz) {
        return ApplicationContextProvider.getBean(clazz);
    }

    private void configureLegacyDaoBeans() {
        this.legacyMasterDao = getBean(LegacyMasterDao.class);
        this.legacyAdminDocumentDao = getBean(LegacyAdminDocumentDao.class);
        this.legacyFieldConfigurationDao = getBean(LegacyFieldConfigurationDao.class);
        this.legacyAdminDocAccStateMixDao = getBean(LegacyAdminDocAccStateMixDao.class);
        this.legacyRolesPermissionsDao = getBean(LegacyRolesPermissionsDao.class);
        this.legacyAccountDao = getBean(LegacyAccountDao.class);
        this.legacyAcceptedPaymentTypeDao = getBean(LegacyAcceptedPaymentTypeDao.class);
        this.legacyPersonnelDao = getBean(LegacyPersonnelDao.class);
    }

    private void configureServiceFacadeBeans() {
        this.loginServiceFacade = getBean(NewLoginServiceFacade.class);
        this.personnelServiceFacade = getBean(PersonnelServiceFacade.class);
        this.customerServiceFacade = getBean(CustomerServiceFacade.class);
        this.centerServiceFacade = getBean(CenterServiceFacade.class);
        this.groupServiceFacade = getBean(GroupServiceFacade.class);
        this.clientServiceFacade = getBean(ClientServiceFacade.class);
        this.accountServiceFacade = getBean(AccountServiceFacade.class);
        this.meetingServiceFacade = getBean(MeetingServiceFacade.class);
        this.loanServiceFacade = getBean(LoanServiceFacade.class);
        this.loanAccountServiceFacade = getBean(LoanAccountServiceFacade.class);
        this.monthClosingServiceFacade = getBean(MonthClosingServiceFacade.class);
        this.holidayServiceFacade = getBean(HolidayServiceFacade.class);
        this.officeServiceFacade = getBean(OfficeServiceFacade.class);
        this.feeServiceFacade = getBean(FeeServiceFacade.class);
        this.fundServiceFacade = getBean(FundServiceFacade.class);
        this.savingsServiceFacade = getBean(SavingsServiceFacade.class);
        this.authenticationAuthorizationServiceFacade = getBean(AuthenticationAuthorizationServiceFacade.class);
        this.importTransactionsServiceFacade = getBean(ImportTransactionsServiceFacade.class);
        this.checkListServiceFacade = getBean(CheckListServiceFacade.class);
    }

    private void configureDaoBeans() {
        this.personnelDao = getBean(PersonnelDao.class);
        this.officeDao = getBean(OfficeDao.class);
        this.customerDao = getBean(CustomerDao.class);
        this.savingsDao = getBean(SavingsDao.class);
        this.loanDao = getBean(LoanDao.class);
        this.loanProductDao = getBean(LoanProductDao.class);
        this.savingsProductDao = getBean(SavingsProductDao.class);
        this.feeDao = getBean(FeeDao.class);
        this.penaltyDao = getBean(PenaltyDao.class);
        this.fundDao = getBean(FundDao.class);
        this.applicationConfigurationDao = getBean(ApplicationConfigurationDao.class);
    }

    protected void preExecute(ActionForm actionForm, HttpServletRequest request, TransactionDemarcate annotation)
            throws SystemException, ApplicationException {
        if (null != request.getParameter(Constants.CURRENTFLOWKEY)) {
            request.setAttribute(Constants.CURRENTFLOWKEY, request.getParameter(Constants.CURRENTFLOWKEY));
        }

        preHandleTransaction(request, annotation);
        UserContext userContext = (UserContext) request.getSession().getAttribute(Constants.USER_CONTEXT_KEY);
        Locale locale = getLocale(userContext);
        if (!skipActionFormToBusinessObjectConversion(request.getParameter("method"))) {
            try {
                AbstractBusinessObject businessObject = getBusinessObjectFromSession(request);
                ConversionUtil.populateBusinessObject(actionForm, businessObject, locale);
            } catch (ValueObjectConversionException e) {
                logger.debug("Value object conversion exception while validating BusinessObject: " + new LogUtils().getStackTrace(e));
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected TransactionDemarcate getTransaction(@SuppressWarnings("unused") ActionForm actionForm, HttpServletRequest request) {
        TransactionDemarcate annotation = null;
        try {
            String methodName = request.getParameter(MethodNameConstants.METHOD);
            Method methodToExecute = this.clazz.getMethod(methodName, new Class[] { ActionMapping.class,
                    ActionForm.class, HttpServletRequest.class, HttpServletResponse.class });
            annotation = methodToExecute.getAnnotation(TransactionDemarcate.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return annotation;
    }

    @SuppressWarnings("unchecked")
    protected boolean isCloseSessionAnnotationPresent(@SuppressWarnings("unused") ActionForm actionForm, HttpServletRequest request) {
        boolean isAnnotationPresent = false;
        try {
            String methodName = request.getParameter(MethodNameConstants.METHOD);
            Method methodToExecute = this.clazz.getMethod(methodName, new Class[] { ActionMapping.class,
                    ActionForm.class, HttpServletRequest.class, HttpServletResponse.class });
            isAnnotationPresent = methodToExecute.isAnnotationPresent(CloseSession.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return isAnnotationPresent;
    }

    protected void preHandleTransaction(HttpServletRequest request, TransactionDemarcate annotation)
            throws PageExpiredException {
        if (null != annotation && annotation.saveToken()) {
            createToken(request);
        } else if ((null != annotation && annotation.validateAndResetToken())
                || (null != annotation && annotation.joinToken())) {
            joinToken(request);
        } else if (null != annotation && annotation.conditionToken()) {
            String flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
            if (flowKey == null) {
                createToken(request);
            } else {
                joinToken(request);
            }

        }
    }

    private void createToken(HttpServletRequest request) {
        String flowKey = String.valueOf(new DateTimeService().getCurrentDateTime().getMillis());
        FlowManager flowManager = (FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER);
        if (flowManager == null) {
            flowManager = new FlowManager();
            request.getSession(false).setAttribute(Constants.FLOWMANAGER, flowManager);
        }
        flowManager.addFLow(flowKey, new Flow(), this.clazz.getName());
        request.setAttribute(Constants.CURRENTFLOWKEY, flowKey);

    }

    private void joinToken(HttpServletRequest request) throws PageExpiredException {
        String flowKey = request.getParameter(Constants.CURRENTFLOWKEY);
        if(null == flowKey){
            flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        }
        FlowManager flowManager = (FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER);
        if (flowKey == null || !flowManager.isFlowValid(flowKey)) {
            throw new PageExpiredException("no flow for key " + flowKey);
        }

    }

    protected void postHandleTransaction(HttpServletRequest request, TransactionDemarcate annotation)
            throws SystemException {
        if (null != annotation && annotation.validateAndResetToken()) {
            FlowManager flowManager = (FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER);
            flowManager.removeFlow((String) request.getAttribute(Constants.CURRENTFLOWKEY));
            request.setAttribute(Constants.CURRENTFLOWKEY, null);
        }
    }

    protected void postExecute(HttpServletRequest request, TransactionDemarcate annotation, boolean closeSession)
            throws ApplicationException, SystemException {
        // do cleanup here
        if (startSession()) {
            try {
                StaticHibernateUtil.commitTransaction();
                postHandleTransaction(request, annotation);
            } catch (HibernateException he) {
                he.printStackTrace();
                StaticHibernateUtil.rollbackTransaction();
                throw new ApplicationException(he);
            }
        } else {
            postHandleTransaction(request, annotation);
        }

        if (closeSession) {
            // StaticHibernateUtil.flushAndCloseSession();
            StaticHibernateUtil.closeSession();
        }
    }

    protected boolean isNewBizRequired(HttpServletRequest request) throws ServiceException {
        if (sessionHasBusinessKey(request)) {
            return serviceReturnsBusinessObject() && isTypeOfBusinessKeyDifferentToThatFromService(request);
        }
        return true;
    }

    private boolean serviceReturnsBusinessObject() throws ServiceException {
        return getService().getBusinessObject(null) != null;
    }

    private boolean isTypeOfBusinessKeyDifferentToThatFromService(HttpServletRequest request) throws ServiceException {
        return !getClassNameOfBusinessKeyAttribute(request).equalsIgnoreCase(getClassNameOfBusinessObjectFromService());
    }

    private boolean sessionHasBusinessKey(HttpServletRequest request) {
        return request.getSession().getAttribute(Constants.BUSINESS_KEY) != null;
    }

    private String getClassNameOfBusinessObjectFromService() throws ServiceException {
        return getService().getBusinessObject(null).getClass().getName();
    }

    private String getClassNameOfBusinessKeyAttribute(HttpServletRequest request) {
        return request.getSession().getAttribute(Constants.BUSINESS_KEY).getClass().getName();
    }

    private AbstractBusinessObject getBusinessObjectFromSession(HttpServletRequest request) throws ServiceException {
        AbstractBusinessObject object = null;
        if (isNewBizRequired(request)) {
            UserContext userContext = (UserContext) request.getSession().getAttribute("UserContext");
            object = getService().getBusinessObject(userContext);
            request.getSession().setAttribute(Constants.BUSINESS_KEY, object);
        } else {
            object = (AbstractBusinessObject) request.getSession().getAttribute(Constants.BUSINESS_KEY);
        }
        return object;
    }

    protected Locale getLocale(UserContext userContext) {
        Locale locale = null;
        if (userContext != null) {
            locale = userContext.getCurrentLocale();
        }

        return locale;
    }

    protected boolean startSession() {
        return true;
    }

    /**
     * This should return true if we don't want to the automatic conversion of
     * forms to business objects (for example, if the data from the form ends up in several business objects)
     */
    protected boolean skipActionFormToBusinessObjectConversion(@SuppressWarnings("unused") String method) {
        return true;
    }

    protected UserContext getUserContext(HttpServletRequest request) {
        return (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
    }

    protected Short getShortValue(String str) {
        return StringUtils.isNotBlank(str) ? Short.valueOf(str) : null;
    }

    protected Integer getIntegerValue(String str) {
        return StringUtils.isNotBlank(str) ? Integer.valueOf(str) : null;
    }

    protected Double getDoubleValue(String str) {
        return StringUtils.isNotBlank(str) ? new LocalizationConverter()
                .getDoubleValueForCurrentLocale(str) : null;
    }

    protected String getStringValue(Integer value) {
        return value != null ? String.valueOf(value) : null;
    }

    protected String getStringValue(Short value) {
        return value != null ? String.valueOf(value) : null;
    }

    protected String getStringValue(Money value) {
        return value != null ? String.valueOf(value) : null;
    }

    protected String getStringValue(boolean value) {
        if (value) {
            return "1";
        }
        return "0";
    }

    protected String getDoubleStringForMoney(Double dNumber) {
        return dNumber != null ? new LocalizationConverter().getDoubleStringForMoney(dNumber): null;
    }

    protected String getDoubleStringForInterest(Double dNumber) {
        return dNumber != null ? new LocalizationConverter().getDoubleStringForInterest(dNumber): null;
    }

    protected Date getDateFromString(String strDate, Locale locale) throws InvalidDateException {
        Date date = null;
        if (StringUtils.isNotBlank(strDate)) {
            date = new Date(DateUtils.getLocaleDate(locale, strDate).getTime());
        }
        return date;
    }

    protected void setInitialObjectForAuditLogging(Object object) {
        StaticHibernateUtil.getSessionTL();
        ((AuditInterceptor)StaticHibernateUtil.getInterceptor()).createInitialValueMap(object);
    }

    private ActionForward logout(ActionMapping mapping, HttpServletRequest request) {
        request.getSession(false).invalidate();
        ActionErrors error = new ActionErrors();
        error.add(LoginConstants.BATCH_JOB_RUNNING, new ActionMessage(
                messages.getMessage(LoginConstants.BATCH_JOB_RUNNING,
                        "You have been logged out of the system because batch jobs are running.")
                ));
        request.setAttribute(Globals.ERROR_KEY, error);
        return mapping.findForward(ActionForwards.load_main_page.toString());
    }

    private ActionForward shutdown(ActionMapping mapping, HttpServletRequest request) {
        request.getSession(false).invalidate();
        ActionErrors error = new ActionErrors();
        error.add(LoginConstants.SHUTDOWN, new ActionMessage(
                messages.getMessage(LoginConstants.SHUTDOWN,
                        "You have been logged out of the system because Mifos is shutting down.")
        ));
        request.setAttribute(Globals.ERROR_KEY, error);
        return mapping.findForward(ActionForwards.load_main_page.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadChangeLog(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        Short entityType = EntityType.getEntityValue(request.getParameter(AuditConstants.ENTITY_TYPE).toUpperCase());
        Integer entityId = Integer.valueOf(request.getParameter(AuditConstants.ENTITY_ID));

        // FIXME - keithw - when replacing BaseActions loadChangeLog functionality for given entity when doing spring/ftl
        // see CenterServiceFacade.retrieveChangeLogs for example.
        AuditBusinessService auditBusinessService = new AuditBusinessService();
        request.getSession().setAttribute(AuditConstants.AUDITLOGRECORDS,
                auditBusinessService.getAuditLogRecords(entityType, entityId));

        return mapping.findForward(AuditConstants.VIEW + request.getParameter(AuditConstants.ENTITY_TYPE)
                + AuditConstants.CHANGE_LOG);
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward cancelChangeLog(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        return mapping.findForward(AuditConstants.CANCEL + request.getParameter(AuditConstants.ENTITY_TYPE)
                + AuditConstants.CHANGE_LOG);
    }

    protected void checkVersionMismatch(Integer oldVersionNum, Integer newVersionNum) throws ApplicationException {
        if (!oldVersionNum.equals(newVersionNum)) {
            throw new ApplicationException(Constants.ERROR_VERSION_MISMATCH);
        }
    }

    protected MifosCurrency getCurrency(Short currencyId) {
        MifosCurrency currency;
        if (currencyId == null) {
            // Currency is passed from Form only for Loan (Amount) Fees in multi-currency settings
            currency = Money.getDefaultCurrency();
        } else {
            currency = AccountingRules.getCurrencyByCurrencyId(currencyId);
        }
        return currency;
    }

    public void setLoanServiceFacade(LoanServiceFacade loanServiceFacade) {
        this.loanServiceFacade = loanServiceFacade;
    }

    public void setLoanAccountServiceFacade(LoanAccountServiceFacade loanAccountServiceFacade) {
        this.loanAccountServiceFacade = loanAccountServiceFacade;
    }
}
