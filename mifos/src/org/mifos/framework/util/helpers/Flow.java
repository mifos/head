/**

 * Flow.java    version: 1.0



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

public class Flow {

	private Map<String, Object> sessionData = new HashMap<String, Object>();

	public Flow() {
	}

	public void addObjectToSession(String key, Object value) {
		sessionData.put(key, value);
	}

	public boolean isKeyPresent(String key) {
		return sessionData.containsKey(key);
	}

	public Object getObjectFromSession(String key) {
		return sessionData.get(key);
	}
	
	public void removeFromSession(String key) {
		Object obj = getObjectFromSession(key);
		sessionData.remove(key);
		obj = null;
	}

}
