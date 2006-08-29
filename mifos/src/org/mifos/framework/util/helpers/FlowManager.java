/**

 * FlowManager.java    version: 1.0



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
package org.mifos.framework.util.helpers;

import java.util.HashMap;
import java.util.Map;

import org.mifos.framework.exceptions.PageExpiredException;

public class FlowManager {

	private Map<String, Flow> flowData = new HashMap<String, Flow>();

	public FlowManager() {
	}

	public void addFLow(String key, Flow value) {
		flowData.put(key, value);
	}

	public boolean isFlowValid(String key) {
		return flowData.containsKey(key);
	}

	public Flow getFlow(String key) {
		return flowData.get(key);
	}

	public void addToFlow(String flowKey, String key, Object value)
			throws PageExpiredException {
		if (!isFlowValid(flowKey))
			throw new PageExpiredException(
					ExceptionConstants.PAGEEXPIREDEXCEPTION);
		Flow flow = getFlow(flowKey.toString());
		flow.addObjectToSession(key, value);
	}

	public Object getFromFlow(String flowKey, String key)
			throws PageExpiredException {
		if (!isFlowValid(flowKey))
			throw new PageExpiredException(
					ExceptionConstants.PAGEEXPIREDEXCEPTION);
		Flow flow = getFlow(flowKey.toString());
		return flow.getObjectFromSession(key);
	}

	public void removeFlow(String key) {
		flowData.remove(key);
	}
	
	public void removeFromFlow(String flowKey, String key)
			throws PageExpiredException {
		if (!isFlowValid(flowKey))
			throw new PageExpiredException(
					ExceptionConstants.PAGEEXPIREDEXCEPTION);
		Flow flow = getFlow(flowKey.toString());
		flow.removeFromSession(key);
	}
}
