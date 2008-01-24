package org.mifos.application.login.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.login.struts.actionforms.LoginActionForm;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.business.service.PersonnelBusinessService;
import org.mifos.application.util.helpers.ActionForwards;
import org.mifos.application.util.helpers.Methods;
import org.mifos.framework.business.service.BusinessService;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.ActionSecurity;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.security.util.resources.SecurityConstants;
import org.mifos.framework.struts.action.BaseAction;
import org.mifos.framework.util.helpers.BusinessServiceName;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.FlowManager;
import org.mifos.framework.util.helpers.SessionUtils;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import javax.servlet.http.*;


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

	
	public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		loginLogger.info("Inside load of LoginAction");
		SessionUtils.setAttribute(LoginConstants.LOGINACTIONFORM, null,request.getSession());
		request.getSession(false).setAttribute(Constants.FLOWMANAGER,new FlowManager());
		return mapping.findForward(ActionForwards.load_success.toString());
	}

	@TransactionDemarcate(saveToken = true)
	public ActionForward login(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		loginLogger.info("Inside login of LoginAction");
		loginLogger.info("Using Thread: " + Thread.currentThread().getName());
		loginLogger.info("Is Session Open?: " + HibernateUtil.isSessionOpen());
		loginLogger.info("Using hibernate session: " + HibernateUtil.getSessionTL().hashCode());
		
		LoginActionForm loginActionForm = (LoginActionForm) form;
		String userName = loginActionForm.getUserName();
		String password = loginActionForm.getPassword();
		PersonnelBO personnelBO = getPersonnelBizService().getPersonnel(userName);
		UserContext userContext = personnelBO.login(password);
		setAttributes(userContext, request);
		if(personnelBO.isPasswordChanged())
			setUserContextInSession(userContext, request);
		else
			SessionUtils.setAttribute(Constants.TEMPUSERCONTEXT,userContext,request);
		setFlow(userContext.getPasswordChanged(),request);
		personnelBO = null;
		return mapping.findForward(getLoginForward(userContext.getPasswordChanged()));
	}

	public ActionForward logout(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		loginLogger.info("Inside logout of LoginAction");
		request.getSession(false).invalidate();
		ActionErrors error = new ActionErrors();
		error.add(LoginConstants.LOGOUTOUT,new ActionMessage(LoginConstants.LOGOUTOUT));
		request.setAttribute(Globals.ERROR_KEY, error);
		return mapping.findForward(ActionForwards.logout_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward updatePassword(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		loginLogger.info("Inside updatePassword of LoginAction");
		LoginActionForm loginActionForm = (LoginActionForm) form;
		UserContext userContext = null;
		String userName = loginActionForm.getUserName();
		if (null == userName || "".equals(userName)) {
			throw new ApplicationException(LoginConstants.SESSIONTIMEOUT);
		}
		String oldPassword = loginActionForm.getOldPassword();
		String newpassword = loginActionForm.getNewPassword();
		PersonnelBO personnelBO = getPersonnelBizService().getPersonnel(userName);
		if(personnelBO.isPasswordChanged())
			userContext = (UserContext) SessionUtils.getAttribute(Constants.USERCONTEXT, request.getSession());
		else
			userContext = (UserContext) SessionUtils.getAttribute(Constants.TEMPUSERCONTEXT, request);
		personnelBO.updatePassword(oldPassword,newpassword, userContext.getId());
		setUserContextInSession(userContext, request);
		personnelBO = null;
		return mapping.findForward(ActionForwards.updatePassword_success.toString());
	}

	@TransactionDemarcate(validateAndResetToken = true)
	public ActionForward cancel(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		PersonnelBO personnelBO = getPersonnelBizService().getPersonnel(((LoginActionForm) form).getUserName());
		return mapping.findForward(getCancelForward(personnelBO.getPasswordChanged()));
	}

	public ActionForward validate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse httpservletresponse)
			throws Exception {
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
		return (PersonnelBusinessService)ServiceFactory.getInstance().getBusinessService(
				BusinessServiceName.Personnel);
	}

	private void setAttributes(UserContext userContext, HttpServletRequest request) {
		ActivityContext activityContext = new ActivityContext((short)0,userContext.getBranchId().shortValue(),userContext.getId().shortValue());
		request.getSession(false).setAttribute(Constants.ACTIVITYCONTEXT,activityContext);
	}

	private void setUserContextInSession(UserContext userContext, HttpServletRequest request) {
		HttpSession hs = request.getSession(false);
		hs.setAttribute(Constants.USERCONTEXT,userContext);
		hs.setAttribute("org.apache.struts.action.LOCALE", userContext.getCurrentLocale());

	}

	private String getLoginForward(Short passwordChanged) {
		return (null == passwordChanged || LoginConstants.FIRSTTIMEUSER.equals(passwordChanged))?
				ActionForwards.loadChangePassword_success.toString():ActionForwards.login_success.toString();
	}

	private String getCancelForward(Short passwordChanged) {
		return (null == passwordChanged || LoginConstants.FIRSTTIMEUSER.equals(passwordChanged))?
				ActionForwards.cancel_success.toString():ActionForwards.updateSettings_success.toString();
	}

	private void setFlow(Short passwordChanged, HttpServletRequest request) {
		if (null != passwordChanged && LoginConstants.PASSWORDCHANGEDFLAG.equals(passwordChanged)) {
			FlowManager flowManager = (FlowManager) request.getSession()
					.getAttribute(Constants.FLOWMANAGER);
			flowManager.removeFlow((String) request
					.getAttribute(Constants.CURRENTFLOWKEY));
			request.setAttribute(Constants.CURRENTFLOWKEY, null);
		}
	}
}
