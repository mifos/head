package org.mifos.framework.security.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.mifos.framework.security.util.resources.SecurityConstants;

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
