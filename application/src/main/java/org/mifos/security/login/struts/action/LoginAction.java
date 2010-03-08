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

package org.mifos.security.login.struts.action;

import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.admin.system.ShutdownManager;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.customers.personnel.business.service.PersonnelBusinessService;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.StaticHibernateUtil;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.login.struts.actionforms.LoginActionForm;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.ActionSecurity;
import org.mifos.security.util.ActivityContext;
import org.mifos.security.util.SecurityConstants;
import org.mifos.security.util.UserContext;

public class LoginAction extends BaseAction {

    private MifosLogger loginLogger = MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER);

    @Override
    protected boolean skipActionFormToBusinessObjectConversion(String method) {
        return true;
    }

    @Override
    protected BusinessService getService() throws ServiceException {
        return getPersonnelBizService();
    }

    public static ActionSecurity getSecurity() {
        ActionSecurity security = new ActionSecurity("loginAction");
        security.allow("login", SecurityConstants.VIEW);
        security.allow("logout", SecurityConstants.VIEW);
        security.allow("updatePassword", SecurityConstants.VIEW);
        return security;
    }

    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        loginLogger.debug("Inside load of LoginAction");
        SessionUtils.setAttribute(LoginConstants.LOGINACTIONFORM, null, request.getSession());
        request.getSession(false).setAttribute(Constants.FLOWMANAGER, new FlowManager());
        return mapping.findForward(ActionForwards.load_success.toString());
    }

    @TransactionDemarcate(saveToken = true)
    public ActionForward login(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        loginLogger.debug("Inside login of LoginAction");
        loginLogger.debug("Using Thread: " + Thread.currentThread().getName());
        loginLogger.debug("Is Session Open?: " + StaticHibernateUtil.isSessionOpen());
        loginLogger.debug("Using hibernate session: " + StaticHibernateUtil.getSessionTL().hashCode());

        if (ShutdownManager.isInShutdownCountdownNotificationThreshold()) {
            request.getSession(false).invalidate();
            ActionErrors error = new ActionErrors();
            error.add(LoginConstants.SHUTDOWN, new ActionMessage(LoginConstants.SHUTDOWN));
            request.setAttribute(Globals.ERROR_KEY, error);
            return mapping.findForward(ActionForwards.load_main_page.toString());
        }

        LoginActionForm loginActionForm = (LoginActionForm) form;
        String userName = loginActionForm.getUserName();
        String password = loginActionForm.getPassword();
        PersonnelBO personnelBO = getPersonnelBizService().getPersonnel(userName);
        UserContext userContext = personnelBO.login(password);
        setAttributes(userContext, request);
        if (personnelBO.isPasswordChanged()) {
            setUserContextInSession(userContext, request);
        } else {
            SessionUtils.setAttribute(Constants.TEMPUSERCONTEXT, userContext, request);
        }
        setFlow(userContext.getPasswordChanged(), request);
        personnelBO = null;
        return mapping.findForward(getLoginForward(userContext.getPasswordChanged()));
    }

    public ActionForward logout(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        loginLogger.debug("Inside logout of LoginAction");

        ResourceBundle resources;
        UserContext userContext = getUserContext(request);
        if (null == userContext) {
            // user might have just been given an empty session, so we
            // can't assume that their session has a preferred locale
            resources = ResourceBundle.getBundle(FilePaths.LOGIN_UI_PROPERTY_FILE);
        } else {
            // get locale first
            Locale locale = userContext.getPreferredLocale();
            resources = ResourceBundle.getBundle(FilePaths.LOGIN_UI_PROPERTY_FILE, locale);
        }

        request.getSession(false).invalidate();
        ActionErrors error = new ActionErrors();

        String errorMessage = resources.getString(LoginConstants.LOGOUTOUT);

        // ActionMessage: take errorMessage as literal
        error.add(LoginConstants.LOGOUTOUT, new ActionMessage(errorMessage, false));

        request.setAttribute(Globals.ERROR_KEY, error);
        return mapping.findForward(ActionForwards.logout_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updatePassword(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        loginLogger.debug("Inside updatePassword of LoginAction");
        LoginActionForm loginActionForm = (LoginActionForm) form;
        UserContext userContext = null;
        String userName = loginActionForm.getUserName();
        if (null == userName || "".equals(userName)) {
            throw new ApplicationException(LoginConstants.SESSIONTIMEOUT);
        }
        String oldPassword = loginActionForm.getOldPassword();
        String newpassword = loginActionForm.getNewPassword();
        PersonnelBO personnelBO = getPersonnelBizService().getPersonnel(userName);
        if (personnelBO.isPasswordChanged()) {
            userContext = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        } else {
            userContext = (UserContext) SessionUtils.getAttribute(Constants.TEMPUSERCONTEXT, request);
        }
        PersonnelBO personnelInit = ((PersonnelBusinessService) getService()).getPersonnel(Short.valueOf(personnelBO
                .getPersonnelId()));
        checkVersionMismatch(personnelBO.getVersionNo(), personnelInit.getVersionNo());
        personnelInit.setVersionNo(personnelBO.getVersionNo());
        personnelInit.setUserContext(userContext);
        setInitialObjectForAuditLogging(personnelInit);
        personnelInit.updatePassword(oldPassword, newpassword, userContext.getId());
        setUserContextInSession(userContext, request);
        personnelBO = null;
        personnelInit = null;
        return mapping.findForward(ActionForwards.updatePassword_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
        PersonnelBO personnelBO = getPersonnelBizService().getPersonnel(((LoginActionForm) form).getUserName());
        return mapping.findForward(getCancelForward(personnelBO.getPasswordChanged()));
    }

    public ActionForward validate(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse httpservletresponse) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        if (method.equalsIgnoreCase(Methods.login.toString())) {
            return mapping.findForward(ActionForwards.login_failure.toString());
        }
        if (method.equalsIgnoreCase(Methods.updatePassword.toString())) {
            return mapping.findForward(ActionForwards.updatePassword_failure.toString());
        }
        return null;
    }

    private PersonnelBusinessService getPersonnelBizService() {
        return (PersonnelBusinessService) ServiceFactory.getInstance()
                .getBusinessService(BusinessServiceName.Personnel);
    }

    private void setAttributes(UserContext userContext, HttpServletRequest request) {
        ActivityContext activityContext = new ActivityContext((short) 0, userContext.getBranchId().shortValue(),
                userContext.getId().shortValue());
        request.getSession(false).setAttribute(Constants.ACTIVITYCONTEXT, activityContext);
    }

    private void setUserContextInSession(UserContext userContext, HttpServletRequest request) {
        HttpSession hs = request.getSession(false);
        hs.setAttribute(Constants.USERCONTEXT, userContext);
        hs.setAttribute("org.apache.struts.action.LOCALE", userContext.getCurrentLocale());

    }

    private String getLoginForward(Short passwordChanged) {
        return (null == passwordChanged || LoginConstants.FIRSTTIMEUSER.equals(passwordChanged)) ? ActionForwards.loadChangePassword_success
                .toString()
                : ActionForwards.login_success.toString();
    }

    private String getCancelForward(Short passwordChanged) {
        return (null == passwordChanged || LoginConstants.FIRSTTIMEUSER.equals(passwordChanged)) ? ActionForwards.cancel_success
                .toString()
                : ActionForwards.updateSettings_success.toString();
    }

    private void setFlow(Short passwordChanged, HttpServletRequest request) {
        if (null != passwordChanged && LoginConstants.PASSWORDCHANGEDFLAG.equals(passwordChanged)) {
            FlowManager flowManager = (FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER);
            flowManager.removeFlow((String) request.getAttribute(Constants.CURRENTFLOWKEY));
            request.setAttribute(Constants.CURRENTFLOWKEY, null);
        }
    }
}
