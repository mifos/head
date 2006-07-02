/**

 * MifosSessionAttributeListener.java    version:1.0

 

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

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;

/**
 * This class is the implementation class for the HttpSessionAttributeListener. This class is used to
 * clean the Session Attributes when Session is made invalid.
 * 
 * @author mohammedn
 */
public class MifosSessionAttributeListener implements
		HttpSessionAttributeListener {

	/**
	 * Default Constructor
	 */
	public MifosSessionAttributeListener() {
	}

	/**
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeAdded(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void attributeAdded(HttpSessionBindingEvent httpSessionBindingEvent) {
		HttpSession session=httpSessionBindingEvent.getSession();
		String eventName=httpSessionBindingEvent.getName();

		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
			"Attribute "+eventName+"Added  to Session "+session.getId());
	}

	/**
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeRemoved(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void attributeRemoved(HttpSessionBindingEvent httpSessionBindingEvent) {
		HttpSession session=httpSessionBindingEvent.getSession();
		String eventName=httpSessionBindingEvent.getName();

		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
			"Attribute "+eventName+"removed from Session "+session.getId());
		eventName=null;

	}

	/**
	 * @see javax.servlet.http.HttpSessionAttributeListener#attributeReplaced(javax.servlet.http.HttpSessionBindingEvent)
	 */
	public void attributeReplaced(HttpSessionBindingEvent httpSessionBindingEvent) {
		HttpSession session=httpSessionBindingEvent.getSession();
		String eventName=httpSessionBindingEvent.getName();

		MifosLogManager.getLogger(LoggerConstants.FRAMEWORKLOGGER).debug(
			"Attribute "+eventName+"Changed in Session "+session.getId());

	}

}
