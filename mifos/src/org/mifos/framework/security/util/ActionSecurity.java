/*
 * Copyright (c) 2005-2008 Grameen Foundation USA
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
package org.mifos.framework.security.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mifos.framework.security.util.resources.SecurityConstants;

/**
 * Wrapper for a mapping of methods in an action class to a primary key in the
 * <code>activity</code> table.
 */
public class ActionSecurity {
	
	private Map<String, Short> actions;
	private final String actionName;
	
	public String getActionName() {
		return actionName;
	}
	
	public ActionSecurity(String name) {
		actionName = name;
		actions = new HashMap<String, Short>();
	}

	/**
	 * Allow users with the securityConstant (that's a permission?
	 * not an activityId, I think, but I'm not sure) to run this
	 * method.  Users without that constant cannot run it.
	 * 
	 * If there is no call to this method for a given method,
	 * then no one will be able to run it.
	 * @param securityConstant
	 *     A constant from {@link SecurityConstants} (TODO: enumify)
	 */
	public void allow(String method, short securityConstant) {
		actions.put(method, securityConstant);
	}

	public Short get(String method) {
		return actions.get(method);
	}

	public Set<String> methods() {
		return actions.keySet();
	}

}
