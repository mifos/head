/**

 * Country.java    version: xxx

 

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

package org.mifos.application.master.util.valueobjects;

import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class Country extends ValueObject{

	/**
	 * 
	 */
	public Country() {
		super();
		// TODO Auto-generated constructor stub
	}
	/* {src_lang=Java}*/


	  private Short countryId;
	  /* {transient=false, volatile=false}*/

	  private String countryName;
	  /* {transient=false, volatile=false}*/

	  private String countryShortName;
	  /* {transient=false, volatile=false}*/
	/**
	 * @return Returns the countryId}.
	 */
	public Short getCountryId() {
		return countryId;
	}
	/**
	 * @param countryId The countryId to set.
	 */
	public void setCountryId(Short countryId) {
		this.countryId = countryId;
	}
	/**
	 * @return Returns the countryName}.
	 */
	public String getCountryName() {
		return countryName;
	}
	/**
	 * @param countryName The countryName to set.
	 */
	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	/**
	 * @return Returns the countryShortName}.
	 */
	public String getCountryShortName() {
		return countryShortName;
	}
	/**
	 * @param countryShortName The countryShortName to set.
	 */
	public void setCountryShortName(String countryShortName) {
		this.countryShortName = countryShortName;
	}

}
