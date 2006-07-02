/**

 * Key.java    version: 1.0



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
package org.mifos.framework.components.configuration.cache;


/**
 *  This class defines Key for OfficeCache. 
 *  The key is defined as a composite key of officeid and a string constant.
 */
public class Key {

	private Short officeId;
	private String key;
	
	public Key(Short officeId, String key){
	  this.key = key;
	  this.officeId = officeId;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public Short getOfficeId() {
		return officeId;
	}
	
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}
	
	@Override		  
	public boolean equals(Object obj){
		Key keyObj = (Key)obj;
		return (this.officeId.equals(keyObj.getOfficeId()) && this.key.equalsIgnoreCase(keyObj.getKey()));
	}
	
	@Override
	public int hashCode() {
		return officeId.hashCode()+key.hashCode();
	}
}
