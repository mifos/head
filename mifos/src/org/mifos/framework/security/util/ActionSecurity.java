package org.mifos.framework.security.util;

import java.util.HashMap;

public class ActionSecurity extends HashMap<String, Short> {
	
	private final String actionName;
	
	public String getActionName() {
		return actionName;
	}
	
	public ActionSecurity(String name) {
		actionName = name;
	}

}
