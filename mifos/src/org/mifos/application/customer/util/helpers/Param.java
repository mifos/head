/**

 * Param  version: 1.0



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
package org.mifos.application.customer.util.helpers;


/**
 * @author imtiyazmb
 *
 */
public class Param {
	private String  paramType ;
	
	private String ParamName;
	private Object value;

	/**
	 * @return Returns the paramName.
	 */
	public String getParamName() {
		return ParamName;
	}
	/**
	 * @param paramName The paramName to set.
	 */
	public void setParamName(String paramName) {
		ParamName = paramName;
	}

	/**
	 * @param paramType
	 * @param paramName
	 * @param value
	 */
	public Param(String paramType, String paramName, Object value) {
		super();
		// TODO Auto-generated constructor stub
		this.paramType = paramType;
		ParamName = paramName;
		this.value = value;
	}
	/**
	 * @return Returns the paramType.
	 */
	public String getParamType() {
		return paramType;
	}
	/**
	 * @param paramType The paramType to set.
	 */
	public void setParamType(String paramType) {
		this.paramType = paramType;
	}
	/**
	 * @return Returns the value.
	 */
	public Object getValue() {
		return value;
	}
	/**
	 * @param value The value to set.
	 */
	public void setValue(Object value) {
		this.value = value;
	}

}
