/**

 * MifosSessionListener.java    version:1.0

 

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

package org.mifos.application.login.util.helpers;

import java.util.Enumeration;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

/**
 * This class is the implementation class for the HttpSessionListener. This class is used to
 * clean the Session Attributes when Session is made invalid.
 * 
 * @author mohammedn
 *
 */
public class MifosSessionListener implements HttpSessionListener {

	/**
	 * Default Constructor
	 */
	public MifosSessionListener() {
	}

	/**
	 * @see javax.servlet.http.HttpSessionListener#sessionCreated(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		HttpSession session=httpSessionEvent.getSession();
		
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
				"Session Created   "+session.getId());
	}

	/**
	 * @see javax.servlet.http.HttpSessionListener#sessionDestroyed(javax.servlet.http.HttpSessionEvent)
	 */
	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		
		HttpSession session=httpSessionEvent.getSession();
		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
				"Session Removed  "+session.getId());		
		Enumeration sessionObjects=session.getAttributeNames();
		while(sessionObjects.hasMoreElements()) {
			String sessionAtt=(String)sessionObjects.nextElement();
			session.removeAttribute(sessionAtt);
		}
	}

}
