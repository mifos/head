/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
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
 
package org.mifos.framework.util.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * It is returned by the (@link XMLReader} whenever <code>getElement()</code> 
 * method is invoked on it.
 * It wraps the elements read from the xml in hashmap.
 */
public class MifosNode {
	
	/**
	 * It stores the elements obtained by reading the xml files as key value pairs.
	 */
	protected Map nodes = new HashMap(); 
	
	public MifosNode(){
	}
	
	public MifosNode(Map nodes){
		this.nodes = nodes;
	}
	
	@Override
	public String toString(){
		return "org.mifos.framework.util.helpers.Node";
	}

	public Map getNodes() {
		return nodes;
	}

	public void setNodes(Map nodes) {
		this.nodes = nodes;
	}
	
	/**
	 * Puts elements in the existing hashmap of elements as key value pairs.
	 * It puts the element key in lower case, so that retrieval can be case-insensitive .
	 * So if two elements have the same key which differ only in case the first key would be lost as it would be overridden by the last entry.
	 */
	public void putElement(String key,String value){
		nodes.put(key.toLowerCase(), value);
	}
	
	/**
	 * It returns the value for the key specified.
	 * It compares the key using to lower case which 
	 * makes the comparison case-insensitive.
	 */
	public String getElement(String key){
		String value =null;
		if(nodes.containsKey(key.toLowerCase())){
			value = (String)nodes.get(key.toLowerCase());
		}
		return value;
	}

}
