package org.mifos.framework.util.helpers;

import org.mifos.framework.exceptions.PageExpiredException;

public class FlowManagerHelper {

	public FlowManagerHelper() {
		super();
	}

	public static Object getFromSession(FlowManager flowManager, String flowKey,
			String key) {
		try {
			return flowManager.getFromFlow(flowKey, key);
		} catch (PageExpiredException e) {
			return null;
		}
	}

	public static Object getFlow(FlowManager flowManager, String flowKey) {
		return flowManager.getFlow(flowKey);
	}

}
