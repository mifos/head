/**

 * SupportedLocales.java    version: xxx

 

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

public class SupportedLocales extends ValueObject{

	public SupportedLocales() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	/* {src_lang=Java}*/


	  private Short localeId;
	  /* {transient=false, volatile=false}*/

	  private String localeName;
	  /* {transient=false, volatile=false}*/

	  private Short defaultLocale;
	  /* {transient=false, volatile=false}*/

	  private Language language;
	  /* {transient=false, volatile=false}*/

	  private Country country;
	  /* {transient=false, volatile=false}*/
	/**
	 * @return Returns the country}.
	 */
	public Country getCountry() {
		return country;
	}
	/**
	 * @param country The country to set.
	 */
	public void setCountry(Country country) {
		this.country = country;
	}
	/**
	 * @return Returns the defaultLocale}.
	 */
	public Short getDefaultLocale() {
		return defaultLocale;
	}
	/**
	 * @param defaultLocale The defaultLocale to set.
	 */
	public void setDefaultLocale(Short defaultLocale) {
		this.defaultLocale = defaultLocale;
	}
	/**
	 * @return Returns the language}.
	 */
	public Language getLanguage() {
		return language;
	}
	/**
	 * @param language The language to set.
	 */
	public void setLanguage(Language language) {
		this.language = language;
	}
	/**
	 * @return Returns the localeId}.
	 */
	public Short getLocaleId() {
		return localeId;
	}
	/**
	 * @param localeId The localeId to set.
	 */
	public void setLocaleId(Short localeId) {
		this.localeId = localeId;
	}
	/**
	 * @return Returns the localeName}.
	 */
	public String getLocaleName() {
		return localeName;
	}
	/**
	 * @param localeName The localeName to set.
	 */
	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

}
