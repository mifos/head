/**

 * Language.java    version: xxx

 

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
public class Language extends ValueObject{

	/**
	 * 
	 */
	public Language() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/* {src_lang=Java}*/


	  private Short languageId;
	  /* {transient=false, volatile=false}*/

	  private String languageName;
	  /* {transient=false, volatile=false}*/

	  private String languageShortName;
	  /* {transient=false, volatile=false}*/
	  
	  /**Denotes the lookUpId of the language represented by this object */
		private Integer lookUpId;
	/**
	 * @return Returns the languageId}.
	 */
	public Short getLanguageId() {
		return languageId;
	}
	/**
	 * @param languageId The languageId to set.
	 */
	public void setLanguageId(Short languageId) {
		this.languageId = languageId;
	}
	/**
	 * @return Returns the languageName}.
	 */
	public String getLanguageName() {
		return languageName;
	}
	/**
	 * @param languageName The languageName to set.
	 */
	public void setLanguageName(String languageName) {
		this.languageName = languageName;
	}
	/**
	 * @return Returns the languageShortName}.
	 */
	public String getLanguageShortName() {
		return languageShortName;
	}
	/**
	 * @param languageShortName The languageShortName to set.
	 */
	public void setLanguageShortName(String languageShortName) {
		this.languageShortName = languageShortName;
	}
	/**
	 * @return Returns the lookUpId.
	 */
	public Integer getLookUpId() {
		return lookUpId;
	}
	/**
	 * @param lookUpId The lookUpId to set.
	 */
	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}
}
