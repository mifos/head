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

package org.mifos.framework.struts.action;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.actions.DispatchAction;
import org.hibernate.HibernateException;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.business.service.MasterDataService;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.config.AccountingRules;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.business.LogUtils;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.audit.business.service.AuditBusinessService;
import org.mifos.framework.components.audit.util.helpers.AuditConstants;
import org.mifos.framework.components.batchjobs.MifosTask;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.InvalidDateException;
import org.mifos.framework.exceptions.PageExpiredException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.exceptions.ValueObjectConversionException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.DateTimeService;
import org.mifos.framework.util.LocalizationConverter;
import org.mifos.framework.util.helpers.CloseSession;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ConversionUtil;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.Flow;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;

public abstract class BaseAction extends DispatchAction {
    private MifosLogger logger = MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER);

    protected abstract BusinessService getService() throws ServiceException;

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        if (MifosTask.isBatchJobRunning()) {
            return logout(mapping, request);
        }
        if (ShutdownManager.isShutdownDone()) {
            return shutdown(mapping, request);
        }
        if (StaticHibernateUtil.isSessionOpen()) {
            logger.warn("Hibernate session is about to be reused for new action:" + getClass().getName());
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
                BusinessObject businessObject = getBusinessObjectFromSession(request);
                ConversionUtil.populateBusinessObject(actionForm, businessObject, locale);
            } catch (ValueObjectConversionException e) {
                logger.debug("Value object conversion exception while validating BusinessObject: " + new LogUtils().getStackTrace(e));
            }
        }
    }

    protected TransactionDemarcate getTransaction(ActionForm actionForm, HttpServletRequest request) {
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

    protected boolean isCloseSessionAnnotationPresent(ActionForm actionForm, HttpServletRequest request) {
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
            if (flowKey == null)
                createToken(request);
            else
                joinToken(request);

        }
    }

    private void createToken(HttpServletRequest request) throws PageExpiredException {
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
        String flowKey = (String) request.getAttribute(Constants.CURRENTFLOWKEY);
        FlowManager flowManager = (FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER);
        if (flowKey == null || !flowManager.isFlowValid(flowKey)) {
            throw new PageExpiredException("no flow for key " + flowKey);
        }

    }

    protected void postHandleTransaction(HttpServletRequest request, TransactionDemarcate annotation)
            throws SystemException, ApplicationException {
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
            if (StaticHibernateUtil.isSessionOpen()) {
                logger.info("Closing open hibernate session at end of action: " + getClass().getName());
            }
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

    private BusinessObject getBusinessObjectFromSession(HttpServletRequest request) throws ServiceException {
        BusinessObject object = null;
        if (isNewBizRequired(request)) {
            UserContext userContext = (UserContext) request.getSession().getAttribute("UserContext");
            object = getService().getBusinessObject(userContext);
            request.getSession().setAttribute(Constants.BUSINESS_KEY, object);
        } else
            object = (BusinessObject) request.getSession().getAttribute(Constants.BUSINESS_KEY);
        return object;
    }

    protected Locale getLocale(UserContext userContext) {
        Locale locale = null;
        if (userContext != null)
            locale = userContext.getCurrentLocale();

        return locale;
    }

    protected boolean startSession() {
        return true;
    }

    /**
     * This should return true if we don't want to the automatic conversion of
     * forms to business objects (for example, if the data from the form ends up
     * in several business objects)
     */
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return false;
    }

    protected UserContext getUserContext(HttpServletRequest request) {
        return (UserContext) SessionUtils.getAttribute(Constants.USER_CONTEXT_KEY, request.getSession());
    }

    protected List<MasterDataEntity> getMasterEntities(Class clazz, Short localeId) throws ServiceException,
            PersistenceException {
        return new MasterDataService().retrieveMasterEntities(clazz, localeId);
    }

    protected MasterDataEntity getMasterEntities(Short entityId, Class clazz, Short localeId) throws ServiceException,
            PersistenceException {
        return new MasterPersistence().retrieveMasterEntity(entityId, clazz, localeId);
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
        if (value)
            return "1";
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
        if (StringUtils.isNotBlank(strDate))
            date = new Date(DateUtils.getLocaleDate(locale, strDate).getTime());
        return date;
    }

    protected void setInitialObjectForAuditLogging(Object object) {
        StaticHibernateUtil.getSessionTL();
        StaticHibernateUtil.getInterceptor().createInitialValueMap(object);
    }

    private ActionForward logout(ActionMapping mapping, HttpServletRequest request) throws ApplicationException {
        request.getSession(false).invalidate();
        ActionErrors error = new ActionErrors();
        error.add(LoginConstants.BATCH_JOB_RUNNING, new ActionMessage(LoginConstants.BATCH_JOB_RUNNING));
        request.setAttribute(Globals.ERROR_KEY, error);
        return mapping.findForward(ActionForwards.load_main_page.toString());
    }

    private ActionForward shutdown(ActionMapping mapping, HttpServletRequest request) throws ApplicationException {
        request.getSession(false).invalidate();
        ActionErrors error = new ActionErrors();
        error.add(LoginConstants.SHUTDOWN, new ActionMessage(LoginConstants.SHUTDOWN));
        request.setAttribute(Globals.ERROR_KEY, error);
        return mapping.findForward(ActionForwards.load_main_page.toString());
    }

    @TransactionDemarcate(joinToken = true)
    public ActionForward loadChangeLog(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        Short entityType = EntityType.getEntityValue(request.getParameter(AuditConstants.ENTITY_TYPE).toUpperCase());
        Integer entityId = Integer.valueOf(request.getParameter(AuditConstants.ENTITY_ID));
        AuditBusinessService auditBusinessService = new AuditBusinessService();
        request.getSession().setAttribute(AuditConstants.AUDITLOGRECORDS,
                auditBusinessService.getAuditLogRecords(entityType, entityId));
        return mapping.findForward(AuditConstants.VIEW + request.getParameter(AuditConstants.ENTITY_TYPE)
                + AuditConstants.CHANGE_LOG);
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward cancelChangeLog(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        return mapping.findForward(AuditConstants.CANCEL + request.getParameter(AuditConstants.ENTITY_TYPE)
                + AuditConstants.CHANGE_LOG);
    }

    protected void checkVersionMismatch(Integer oldVersionNum, Integer newVersionNum) throws ApplicationException {
        if (!oldVersionNum.equals(newVersionNum))
            throw new ApplicationException(Constants.ERROR_VERSION_MISMATCH);
    }

    /*
     * this method is not intended to be part of the application, but is used by
     * tests to get a copy of the ActionMapping object used in a specific action
     * tests just need to perform this method through the struts machinery, then
     * call
     * "ActionMapping mapping = request.getAttribute(Constants.ACTION_MAPPING)"
     */
    public ActionForward findActionMapping(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        request.setAttribute(Constants.ACTION_MAPPING, mapping);
        // welcome is a global forward, present in all actions
        return mapping.findForward(ActionForwards.welcome.toString());
    }

    protected String localizedMessageLookup(String key) {
        return MessageLookup.getInstance().lookup(key);
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
    
    
}
