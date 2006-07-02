/**

 * MifosLoginAction.java    version: 1.0



 * Copyright (c) 2005-2006 Grameen Foundation USA

 * 1029 Vermont Avenue, NW, Suite 400, Washington DC 20005

 * All rights reserved.



 * Apache License 
 * Copyright (c) 2005-2006 Grameen Foundation USA 
 * 

 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 
 *

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the 

 * License. 
 * 
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an explanation of the license 

 * and how it is applied. 

 *

 */

package org.mifos.application.login.struts.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.login.struts.actionforms.MifosLoginActionForm;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.business.util.helpers.MethodNameConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.DoubleSubmitException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.security.util.ActivityContext;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.action.MifosBaseAction;
import org.mifos.framework.util.helpers.Constants;
import org.mifos.framework.util.helpers.ExceptionConstants;
import org.mifos.framework.util.helpers.TransactionDemarcate;
import org.mifos.framework.util.valueobjects.Context;

/**
 * This is the Action associated with Login. The class extends from {<@linkMifosBaseAction}.
 * It overrides method {<@link getPath()} and {<@link appendToMap()} from MifosBaseAction.
 *
 *@author mohammedn 
 */
public class MifosLoginAction extends MifosBaseAction {
	
	private MifosLogger loginLogger=MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER);
	
	/**
	 * The method is called when the user enters Username and password. This method sets the 
	 * {<@link UserContext}in Session and forwards the response to either the 
	 * Home Page or Change Password Page based on the data.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward-- Forwards to change password page or Homepage
	 */
	@TransactionDemarcate(saveToken = true)
	public ActionForward login(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		loginLogger.info("Delegating to the MifosLoginBusinessProcessor to check Login Name"+
				((MifosLoginActionForm)form).getUserName());
		delegateTo(mapping,form,request,response);
		UserContext userContext=setUserContext(mapping,form,request,response);
		loginLogger.info("Login Name " +((MifosLoginActionForm)form).getUserName() +
				"is Authenticated to enter into the Application");
		return mapping.findForward(getLoginForward(userContext));
	}
	
	/**
	 * This method is called to Change the Password of the User. It forwards the 
	 * user to Home Page if he comes from Login Page.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	/*public ActionForward customUpdate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		loginLogger.debug("Inside get method of MifosLoginAction");
		//check if the inputScreen is Login, if it is login screen forward to Home Page
		return mapping.findForward(getUpdateForward(((MifosLoginActionForm) form).getInput()));
	}*/
	
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws ApplicationException, SystemException {
		if(isTokenValid(request)){
			Context context = (Context) request.getAttribute(Constants.CONTEXT);
			if(null==context) {
				throw new ApplicationException(LoginConstants.KEYINVALIDUSER);
			}
			context.setBusinessAction(MethodNameConstants.UPDATE);
			delegate(context, request);
			resetToken(request);
		}else 
			throw new DoubleSubmitException(ExceptionConstants.DOUBLESUBMITEXCEPTION);
		
		return mapping.findForward(getUpdateForward(((MifosLoginActionForm) form).getInput()));
	}
	
	 /**
	  *	Method which is called to decide the pages on which the errors on failure of validation will be displayed 
	  * this method forwards as per the respective input page 
	  * @param mapping indicates action mapping defined in struts-config.xml 
	  * @param form The form bean associated with this action
	  * @param request Contains the request parameters
	  * @param response
	  * @return The mapping to the next page
	  */
	public ActionForward customValidate(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) {
		String forward = null;	
		MifosLoginActionForm actionForm = (MifosLoginActionForm)form;
		String fromPage =actionForm.getInput();
		
		//deciding forward page
		if(null !=fromPage) {
			if(LoginConstants.LOGINPASSCHANGE.equals(fromPage)){
				forward = LoginConstants.LOGIN_PASSWORD_UPDATE_FAILURE;
			}else if(LoginConstants.SETTINGSPASSCHANE.equals(fromPage)){
				forward = LoginConstants.SETTINGS_PASSWORD_UPDATE_FAILURE;
			}
		}
		return mapping.findForward(forward); 
	}

	
	/**
	 * The method adds Login to the getKeyMethodMap of MifosBaseAction
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#appendToMap()
	 */
	public Map<String, String> appendToMap() {
		Map<String, String> loginMap = new HashMap<String, String>();
		loginMap.put(LoginConstants.LOGIN, LoginConstants.LOGIN);
		return loginMap;
	}

	/**
	 * The method provides implementation to the abstract method {@link getPath()}
	 * MifosBaseAction
	 * 
	 * @see org.mifos.framework.struts.action.MifosBaseAction#getPath()
	 */
	protected String getPath() {
		return LoginConstants.GETLOGINPATH;
	}
	
	/**
	 * This method is used to get the Context from the request and delegate to 
	 * the {<@link MifosLoginBusinessProcessor}. 
	 *  
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	protected void delegateTo(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		//get the Context from the request
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		if(null==context) {
			throw new ApplicationException(LoginConstants.KEYINVALIDUSER);
		}
		//set the business action as login
		context.setBusinessAction(LoginConstants.LOGIN);
		//delegate the request to business processor
		delegate(context, request);
	}
	
	/**
	 * This method is used to get the UserContext from the Context. This method also 
	 * creates an Activity Context for checking the Permissions allowed for the User.
	 * The UserContext and ActivityContext are set in the Session.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return UserContext obtained from the Context
	 */
	protected UserContext setUserContext(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		//get the Context from the request after the delegation to business processor
		Context context = (Context) request.getAttribute(Constants.CONTEXT);
		//get the UserContext from the Context
		UserContext userContext = context.getUserContext();
		ActivityContext ac = new ActivityContext((short)0,userContext.getBranchId().shortValue(),userContext.getId().shortValue());
		
		//TODO remove for release build
		request.getSession(false).setAttribute("BypassPermission",new Boolean(false));
		//
		
		request.getSession(false).setAttribute(LoginConstants.USERCONTEXT,userContext);
		request.getSession(false).setAttribute(LoginConstants.ACTIVITYCONTEXT,ac);
		
		return userContext;
	}
	
	/**
	 * This method returns the Forward key for the Login method. If the
	 * user is entering application for the first time he is directed to change password screen.
	 * Otherwise he is directed to Home page of the application.
	 * 
	 * @param userContext
	 * @return
	 */
	protected String getLoginForward(UserContext userContext) {
		Short passwordStatus=userContext.getPasswordChanged();
		
		//check if password changed is required		
		return (null == passwordStatus || LoginConstants.FIRSTTIMEUSER.equals(passwordStatus))?
				LoginConstants.FIRSTLOGIN:LoginConstants.REGULARLOGIN;
	}
	
	/**
	 * This method is used to return the forward key basd on input screen. If the
	 * user is entering application for the first time he is directed to change password screen.
	 * If the User is tring to change password explicitly, then he is forwarded to settings page.
	 * 
	 * @param inputScreen
	 * @return Action Forward Key
	 */
	protected String getUpdateForward(String inputScreen) {
		if(null != inputScreen && LoginConstants.LOGINPASSCHANGE.equalsIgnoreCase(inputScreen)) {
			loginLogger.debug("Forwarding the user to Home page");
			return LoginConstants.REGULARLOGIN;
		}
		loginLogger.debug("Forwarding the user to settings page");
		return LoginConstants.SETTINGSPAGE;
	}
	
	//Bug id 25852.
	/**
	 * This method is called to forward user to the login page. This method just redirects
	 * the user to login page and does nothing.
	 * 
	 * @param mapping
	 * @param form
	 * @param request
	 * @param response
	 * @return ActionForward
	 */
	public ActionForward load(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		loginLogger.debug("Inside get load of MifosLoginAction");
		return mapping.findForward(LoginConstants.LOGINPAGE);
	}
 
}
