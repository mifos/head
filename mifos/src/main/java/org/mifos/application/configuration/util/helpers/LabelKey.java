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
 
package org.mifos.application.configuration.util.helpers;


/**
 * This class encapsulate the key used to index the locale specific string 
 * in labelCache map 
 */

public class LabelKey {
	
	private String key;
	
	private Short localeId;
	
	public LabelKey(String key,Short localeId) {
		this.localeId = localeId;
		this.key = key;
	}
	@Override
	public String toString() {
		
		return "[localeId="+localeId+"]"+"[key="+key+"]";
	}
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof LabelKey))
			return false;
		else {
			LabelKey newObj = (LabelKey) obj;
			if (newObj.localeId.shortValue()==this.localeId.shortValue()
					&& newObj.key.equalsIgnoreCase(this.key))
				return true;
			else
				return false;
		}
	}
	@Override
	public int hashCode() {
		return this.localeId.hashCode() + this.key.hashCode();
	}

}
