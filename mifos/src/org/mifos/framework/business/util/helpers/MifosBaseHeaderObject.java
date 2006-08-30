/**

 * MifosBaseHeaderObject.java    version: xxx

 

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

package org.mifos.framework.business.util.helpers;

import java.util.HashMap;

/**
 * This class acts as base header object class which has a hashMap storing 
 * key and value.
 * This is used to display header links on the UI. A new Instance 
 * of this class should be created and 
 * returned from fetchHeader method of the business processor.
 */
public class MifosBaseHeaderObject implements HeaderObject {
	
	/**
	 * This map is used to store values to be displayed in the UI.
	 */
	private HashMap<String,String>headerValues = new HashMap<String,String>();
	/**
	 * 
	 */
	public MifosBaseHeaderObject() {
		super();
		
	}
	
	/**
	 * This method returns the value if any is present against the specified key
	 * else returns null. 
	 * @param key
	 * @return
	 */
	public String getFromHeaderValues(String key){
		if(headerValues.containsKey(key)){
			return headerValues.get(key);
		}
		return null;
	}
	
	/**
	 * This method adds the key , value to the map.
	 * @param key
	 * @param value
	 */
	public void addToHeaderValues(String key,String value){
		headerValues.put(key, value);
	}

	/**
	 * @return Returns the headerValues}.
	 */
	public HashMap<String, String> getHeaderValues() {
		return headerValues;
	}

	/**
	 * @param headerValues The headerValues to set.
	 */
	public void setHeaderValues(HashMap<String, String> headerValues) {
		this.headerValues = headerValues;
	}

}
