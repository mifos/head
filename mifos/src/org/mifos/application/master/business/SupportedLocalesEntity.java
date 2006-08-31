/**

 * SupportedLocalesEntity.java    version: 1.0

 

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

package org.mifos.application.master.business;

import org.mifos.framework.business.PersistentObject;

public class SupportedLocalesEntity extends PersistentObject {

	private Short localeId;

	private String localeName;

	private Short defaultLocale;

	private LanguageEntity language;

	private CountryEntity country;

	public SupportedLocalesEntity() {
		super();
	}
	public SupportedLocalesEntity(Short localeId) {
		super();
		this.localeId=localeId;
	}

	public CountryEntity getCountry() {
		return country;
	}

	public void setCountry(CountryEntity country) {
		this.country = country;
	}

	public LanguageEntity getLanguage() {
		return language;
	}

	public void setLanguage(LanguageEntity language) {
		this.language = language;
	}

	public Short getDefaultLocale() {
		return defaultLocale;
	}

	public void setDefaultLocale(Short defaultLocale) {
		this.defaultLocale = defaultLocale;
	}

	public Short getLocaleId() {
		return localeId;
	}

	public void setLocaleId(Short localeId) {
		this.localeId = localeId;
	}

	public String getLocaleName() {
		return localeName;
	}

	public void setLocaleName(String localeName) {
		this.localeName = localeName;
	}

}
