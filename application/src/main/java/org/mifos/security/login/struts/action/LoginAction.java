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

package org.mifos.security.login.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.customers.personnel.business.PersonnelBO;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.security.login.struts.actionforms.LoginActionForm;
import org.mifos.security.login.util.helpers.LoginConstants;
import org.mifos.security.util.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * remove {@link LoginActionForm} and LoginFilter, LoginFilterStrutsTest when deleting {@link LoginAction}.
 */
public class LoginAction extends BaseAction {

    private static final Logger logger = LoggerFactory.getLogger(LoginAction.class);

//    public ActionForward load(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
//            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
//        logger.debug("Inside load of LoginAction");
//        SessionUtils.setAttribute(LoginConstants.LOGINACTIONFORM, null, request.getSession());
//        request.getSession(false).setAttribute(Constants.FLOWMANAGER, new FlowManager());
//        return mapping.findForward(ActionForwards.load_success.toString());
//    }

//    @TransactionDemarcate(saveToken = true)
//    public ActionForward login(ActionMapping mapping, ActionForm form, HttpServletRequest request,
//            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
//        logger.debug("Inside login of LoginAction");
//        logger.debug("Using Thread: " + Thread.currentThread().getName());
//        logger.debug("Using hibernate session: " + StaticHibernateUtil.getSessionTL().hashCode());
//
//        ShutdownManager shutdownManager = (ShutdownManager) ServletUtils.getGlobal(request, ShutdownManager.class
//                .getName());
//        if (shutdownManager.isInShutdownCountdownNotificationThreshold()) {
//            request.getSession(false).invalidate();
//            ActionErrors error = new ActionErrors();
//            error.add(LoginConstants.SHUTDOWN, new ActionMessage(LoginConstants.SHUTDOWN));
//            request.setAttribute(Globals.ERROR_KEY, error);
//            return mapping.findForward(ActionForwards.load_main_page.toString());
//        }
//
//        LoginActionForm loginActionForm = (LoginActionForm) form;
//        String userName = loginActionForm.getUserName();
//        String password = loginActionForm.getPassword();
//
//        LoginDto loginDto = loginServiceFacade.login(userName, password);
//
//        PersonnelBO user = this.personnelDao.findPersonnelById(loginDto.getUserId());
//        ActivityContext activityContext = new ActivityContext(Short.valueOf("0"), user.getOffice().getOfficeId(), user.getPersonnelId());
//        request.getSession(false).setAttribute(Constants.ACTIVITYCONTEXT, activityContext);
//
//        Locale preferredLocale = Localization.getInstance().getConfiguredLocale();
//        Short localeId = Localization.getInstance().getLocaleId();
//        UserContext userContext = new UserContext(preferredLocale, localeId);
//        userContext.setId(user.getPersonnelId());
//        userContext.setName(user.getDisplayName());
//        userContext.setLevel(user.getLevelEnum());
//        userContext.setRoles(user.getRoles());
//        userContext.setLastLogin(user.getLastLogin());
//        userContext.setPasswordChanged(user.getPasswordChanged());
//        userContext.setBranchId(user.getOffice().getOfficeId());
//        userContext.setBranchGlobalNum(user.getOffice().getGlobalOfficeNum());
//        userContext.setOfficeLevelId(user.getOffice().getLevel().getId());
//
//        if (loginDto.isPasswordChanged()) {
//            setUserContextInSession(userContext, request);
//        } else {
//            SessionUtils.setAttribute(Constants.TEMPUSERCONTEXT, userContext, request);
//        }
//
//        //  set flow
//        Short passwordChanged = user.getPasswordChanged();
//        if (null != passwordChanged && LoginConstants.PASSWORDCHANGEDFLAG.equals(passwordChanged)) {
//            FlowManager flowManager = (FlowManager) request.getSession().getAttribute(Constants.FLOWMANAGER);
//            flowManager.removeFlow((String) request.getAttribute(Constants.CURRENTFLOWKEY));
//            request.setAttribute(Constants.CURRENTFLOWKEY, null);
//        }
//
//        final String loginForward = getLoginForward(user.getPasswordChanged());
//
//        return mapping.findForward(loginForward);
//    }

//    public ActionForward logout(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
//            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
//
//        logger.debug("Inside logout of LoginAction");
//
//        ResourceBundle resources;
//        UserContext userContext = getUserContext(request);
//        if (null == userContext) {
//            // user might have just been given an empty session, so we
//            // can't assume that their session has a preferred locale
//            resources = ResourceBundle.getBundle(FilePaths.LOGIN_UI_PROPERTY_FILE);
//        } else {
//            // get locale first
//            Locale locale = userContext.getPreferredLocale();
//            resources = ResourceBundle.getBundle(FilePaths.LOGIN_UI_PROPERTY_FILE, locale);
//        }
//
//        request.getSession(false).invalidate();
//        ActionErrors error = new ActionErrors();
//
//        String errorMessage = resources.getString(LoginConstants.LOGOUTOUT);
//
//        // ActionMessage: take errorMessage as literal
//        error.add(LoginConstants.LOGOUTOUT, new ActionMessage(errorMessage, false));
//
//        request.setAttribute(Globals.ERROR_KEY, error);
//        return mapping.findForward(ActionForwards.logout_success.toString());
//    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward updatePassword(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {
        logger.debug("Inside updatePassword of LoginAction");
        LoginActionForm loginActionForm = (LoginActionForm) form;
        UserContext userContext = null;
        String userName = loginActionForm.getUserName();
        if (null == userName || "".equals(userName)) {
            throw new ApplicationException(LoginConstants.SESSIONTIMEOUT);
        }
        String oldPassword = loginActionForm.getOldPassword();
        String newpassword = loginActionForm.getNewPassword();

        boolean passwordWasPreviouslyChanged = this.loginServiceFacade.updatePassword(userName, oldPassword, newpassword);

        if (passwordWasPreviouslyChanged) {
            userContext = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
        } else {
            userContext = (UserContext) SessionUtils.getAttribute(Constants.TEMPUSERCONTEXT, request);
        }

        setUserContextInSession(userContext, request);
        return mapping.findForward(ActionForwards.updatePassword_success.toString());
    }

    @TransactionDemarcate(validateAndResetToken = true)
    public ActionForward cancel(ActionMapping mapping, ActionForm form, @SuppressWarnings("unused") HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse response) throws Exception {

        String username = ((LoginActionForm) form).getUserName();

        PersonnelBO personnelBO = this.personnelDao.findPersonnelByUsername(username);
        String actionForward = getCancelForward(personnelBO.getPasswordChanged());
        return mapping.findForward(actionForward);
    }

    public ActionForward validate(ActionMapping mapping, @SuppressWarnings("unused") ActionForm form, HttpServletRequest request,
            @SuppressWarnings("unused") HttpServletResponse httpservletresponse) throws Exception {
        String method = (String) request.getAttribute("methodCalled");
        if (method.equalsIgnoreCase(Methods.login.toString())) {
            return mapping.findForward(ActionForwards.login_failure.toString());
        }
        if (method.equalsIgnoreCase(Methods.updatePassword.toString())) {
            return mapping.findForward(ActionForwards.updatePassword_failure.toString());
        }
        return null;
    }

    private void setUserContextInSession(UserContext userContext, HttpServletRequest request) {
        HttpSession hs = request.getSession(false);
        hs.setAttribute(Constants.USERCONTEXT, userContext);
        hs.setAttribute(Globals.LOCALE_KEY, userContext.getCurrentLocale());
    }

//    private String getLoginForward(Short passwordChanged) {
//        return (null == passwordChanged || LoginConstants.FIRSTTIMEUSER.equals(passwordChanged)) ? ActionForwards.loadChangePassword_success
//                .toString()
//                : ActionForwards.login_success.toString();
//    }

    private String getCancelForward(Short passwordChanged) {
        return (null == passwordChanged || LoginConstants.FIRSTTIMEUSER.equals(passwordChanged)) ? ActionForwards.cancel_success
                .toString()
                : ActionForwards.updateSettings_success.toString();
    }
}