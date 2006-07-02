/**

 * PreviousRequestValues.java    version: xxx

 

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
import java.io.Serializable;

/**
 * This class stores previous request values and is used to dump request values
 * from request processor after successful completion of action processing.
 * This is used to handle scenarios where in case of exception we need to go back o the same page
 * with request having all old values.
 * @author ashishsm
 *
 */
public class PreviousRequestValues implements Serializable {

	/**
	 * 
	 */
	public PreviousRequestValues() {
		super();
		
	}
	
	private HashMap<String,Object> previousRequestValueMap = new HashMap<String,Object>();

	/**
	 * @return Returns the previousRequestValueMap}.
	 */
	public HashMap<String, Object> getPreviousRequestValueMap() {
		return previousRequestValueMap;
	}

	/**
	 * @param previousRequestValueMap The previousRequestValueMap to set.
	 */
	public void setPreviousRequestValueMap(
			HashMap<String, Object> previousRequestValueMap) {
		this.previousRequestValueMap = previousRequestValueMap;
	}

}
