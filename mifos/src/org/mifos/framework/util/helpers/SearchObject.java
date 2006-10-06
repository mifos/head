/**

 * SearchObject.java    version: 1.0

 

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

import org.mifos.application.customer.util.helpers.CustomerSearchConstants;

public class SearchObject{// extends BasicDynaBean {

	/*private String searchName;
	private String userId;
	private String branchId;
	
	public SearchObject(DynaClass arg0) {
		super(arg0);
	}
	
	public SearchObject(DynaClass arg0, Context context) {
		super(arg0);
		values.put(Constants.USER_ID, context.getUserId());
		values.put(Constants.BRANCH_ID, context.getBranchId());
		//searchName is already in the form. Should always be present
	}*/
	
	private Map<String,String> searchNodeMap;
	
	public SearchObject(){
		searchNodeMap = new HashMap<String,String>();
	}
	
	public void addToSearchNodeMap(String key, String value){
		searchNodeMap.put(key, value);
	}

	public void addSearchTermAndOffice(String searchTerm, String officeId) {
		addToSearchNodeMap(
			"dummy-search-term-key", searchTerm);
		addToSearchNodeMap(
			CustomerSearchConstants.CUSTOMER_SEARCH_OFFICE_ID, 
				officeId);
	}
	
	public String getFromSearchNodeMap(String key){
		return searchNodeMap.get(key);
	}

	public Map<String, String> getSearchNodeMap() {
		return searchNodeMap;
	}

	public void setSearchNodeMap(Map<String, String> searchNodeMap) {
		this.searchNodeMap = searchNodeMap;
	}

}
