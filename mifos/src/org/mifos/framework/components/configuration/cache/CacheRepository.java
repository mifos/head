/**

 * CacheRepository.java    version: 1.0



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

package org.mifos.framework.components.configuration.cache;

public class CacheRepository {
	
	private Cache systemCache;
	private OfficeCache officeCache;
	private static CacheRepository cacheRep=new CacheRepository();
	
	public CacheRepository(){
		systemCache = new Cache();
		officeCache = new OfficeCache();
	}

	public OfficeCache getOfficeCache() {
		return officeCache;
	}

	public void setOfficeCache(OfficeCache officeCache) {
		this.officeCache = officeCache;
	}

	public Cache getSystemCache() {
		return systemCache;
	}

	public void setSystemCache(Cache systemCache) {
		this.systemCache = systemCache;
	}

	public Object getValueFromOfficeCache(Key key) {
		return (key!=null)?officeCache.getElement(key):null;
	}

	public Object getValueFromSystemCache(String key) {
		return (key!=null)?systemCache.getElement(key):null;
	}
	
	public static CacheRepository getInstance() {
	    return cacheRep;
	}	
}