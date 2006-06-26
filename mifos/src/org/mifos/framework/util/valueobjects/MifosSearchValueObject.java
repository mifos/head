/**

 * MifosSearchValueObject.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.framework.util.valueobjects;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ashishsm
 *
 */
public class MifosSearchValueObject extends ValueObject {

	/**
	 * 
	 */
	public MifosSearchValueObject() {
		super();
		// TODO Auto-generated constructor stub
	}
	
/**
	 * This is the map which will hold the search parameters which come from the jsp.
	 */
	private Map<String,String> searchNodeMap = new HashMap<String,String>();

	/**
	 * @return Returns the searchNodeMap}.
	 */
	public Map<String, String> getSearchNodeMap() {
		return searchNodeMap;
	}

	/**
	 * @param searchNodeMap The searchNodeMap to set.
	 */
	public void setSearchNodeMap(Map<String, String> searchNodeMap) {
		this.searchNodeMap = searchNodeMap;
	}
	
	/**
	 * Adds values to the searchNodeMap.
	 * @param key - key with which value is supposed to be added
	 * @param value - value to be put in the map.
	 */
	public void setSearchNode(String key, String value){
		searchNodeMap.put(key, value);
	}
	
	/**
	 * Gets values from the searchNodeMap.
	 * @param key - key with which to find the data in the map.
	 * @return
	 */
	public String getSearchNode(String key){
		return searchNodeMap.get(key);
	}

}
