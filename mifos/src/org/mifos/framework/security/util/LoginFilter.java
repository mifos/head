/**

 * LoginFilter.java    version: 1.0

 

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

package org.mifos.framework.security.util;

import java.io.IOException;
import java.util.Random;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.Globals;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMessage;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.hibernate.helper.HibernateUtil;

/**
 * @author rajenders
 * 
 * LoginFitter class fiter's all the request other then login page if user is
 * not login and forces user to login to mifos
 */
public class LoginFilter implements Filter {

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

	/**
	 * This function implements the login filter it checks if user is not login
	 * it forces the user to login by redirecting him to login page
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain fch) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		String complUri = request.getRequestURI();
		int index=complUri.lastIndexOf("/");
		String uri=complUri.substring(index+1);
/*		if (request.getSession(false) == null) {
			MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).info(
					"Inside Filter session is null");
			ActionMessage error = new ActionMessage(LoginConstants.SESSIONTIMEOUT);
			request.setAttribute(Globals.ERROR_KEY, error);
			if(!uri.equalsIgnoreCase("mifoslogin.do")) {
				((HttpServletResponse)res).sendRedirect(request.getContextPath()+LoginConstants.LOGINPAGEURI);
			}
			return;
		}*/
		try {
			if (uri == null
					|| uri.equalsIgnoreCase(LoginConstants.LOGINPAGEURI)
					|| uri.equalsIgnoreCase( LoginConstants.LOGINACTION)) {
				MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).info(
						"Inside Filter uri is for login page");
				fch.doFilter(req, res);
			} else {
				// First allow him to dsiplay the login page
				// if ( req.)
				//Bug id 27192. Changed the code to test for session null after checking if its login page or not.
					if (request.getSession(false) == null) {
						MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).info(
								"Inside Filter session is null");
						ActionErrors error = new ActionErrors();
						error.add(LoginConstants.SESSIONTIMEOUT,new ActionMessage(LoginConstants.SESSIONTIMEOUT));
						request.setAttribute(Globals.ERROR_KEY, error);
						request.getRequestDispatcher(LoginConstants.LOGINPAGEURI).forward(req,res);
							//((HttpServletResponse)res).sendRedirect(request.getContextPath()+LoginConstants.LOGINPAGEURI);
						return;
					}
				UserContext userContext = (UserContext) ((HttpServletRequest) req)
						.getSession(false).getAttribute(
								LoginConstants.USERCONTEXT);
				if (null == userContext || null == userContext.getId()
						|| "".equals(userContext.getId())) {
					// send back to login page with error message
					((HttpServletResponse) res)
							.sendRedirect(request.getContextPath()+LoginConstants.LOGINPAGEURI);
					return;

				} else {
					((HttpServletRequest) req).getSession(false).setAttribute("randomNUm", new Random().nextLong());
					fch.doFilter(req, res);
				}

			}
		} catch (IllegalStateException ise) {
			MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).error(
					"Inside Filter ISE" + ise.getMessage());
			ActionMessage error = new ActionMessage(LoginConstants.IllEGALSTATE);
			request.setAttribute(Globals.ERROR_KEY, error);
			((HttpServletResponse) res)
					.sendRedirect(request.getContextPath()+LoginConstants.LOGINPAGEURI);
		}
		finally{
			HibernateUtil.closeSession();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig fc) throws ServletException {

	}
}
