/**
 
 * MasterDataRepository.java    version: xxx
 
 
 
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

package org.mifos.framework.dao.helpers;

import java.util.WeakHashMap;

import org.mifos.framework.util.valueobjects.SearchResults;



/**
 * This is a singleton which acts as cache for the master data.
 * @author ashishsm
 *
 */
/**
 * @author ashishsm
 *
 */
public class MasterDataRepository {
	
	private static MasterDataRepository instance = new MasterDataRepository();
	
	
	private WeakHashMap<String,SearchResults> masterDataCache;
	
	
	/**
	 * This returns an instance of the MasterDataRepository.
	 * @return
	 */
	public static MasterDataRepository getInstance() {
		return instance;
	}
	
	/**
	 * Add data to cache .
	 * @param key - Key with which the data would be added to cache.
	 * @param value - Value to be added to the cache.
	 */
	public void addToCache(String key, Object value) {
	}
	
	/**
	 * Determines if there is any data identified by this key in the cache.
	 * @param key - Key identifying an object in the cache.
	 * @return - true if the data is in cache.
	 */
	public boolean isInCache(String key) {
		return false;
	}
	
	public SearchResults getDataFromCache(String key){
		return masterDataCache.get(key);
	}
}
