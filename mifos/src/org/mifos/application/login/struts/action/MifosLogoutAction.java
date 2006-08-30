//Created by MyEclipse Struts
// XSL source (default): platform:/plugin/com.genuitec.eclipse.cross.easystruts.eclipse_4.0.0/xslt/JavaClass.xsl

package org.mifos.application.login.struts.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.MifosBaseAction;

/**
 * This class is the Action for the Logout.
 */
public class MifosLogoutAction extends MifosBaseAction {

	/**
	 * This method is used to logout an user. This method invalidates the Session
	 * of the User.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward logout(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).info(
				"User" +((UserContext)request.getSession().getAttribute(LoginConstants.USERCONTEXT)).getName() +
				" is Logging Out");
		request.getSession(false).invalidate();
		//Bug id 26807 added action errors to display the message.
		ActionErrors error = new ActionErrors();
		error.add(LoginConstants.LOGOUTOUT,new ActionMessage(LoginConstants.LOGOUTOUT));
		request.setAttribute(Globals.ERROR_KEY, error);
		return mapping.findForward(LoginConstants.LOGINPAGE);
	}

	/**
	 * The method adds Logout to the getKeyMethodMap of MifosBaseAction
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#appendToMap()
	 */
	public Map<String, String> appendToMap() {
		Map<String, String> loginMap = new HashMap<String, String>();
		loginMap.put(LoginConstants.LOGOUT, LoginConstants.LOGOUT);
		return loginMap;
	}

	/**
	 * The method provides implementation to the abstract method getPath()
	 * MifosBaseAction
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	protected String getPath() {
		return LoginConstants.GETLOGOUTPATH;
	}

	/* (non-Javadoc)
	 * @see org.mifos.framework.struts.action.MifosBaseAction#isActionFormToValueObjectConversionReq(java.lang.String)
	 */
	protected boolean isActionFormToValueObjectConversionReq(String methodName) {
		return false;
	}
	
	

}
