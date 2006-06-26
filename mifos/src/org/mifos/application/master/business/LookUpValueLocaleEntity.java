/**

 * LookUpValueLocaleEntity.java    version: 1.0



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

package org.mifos.application.master.business;

import org.mifos.framework.util.valueobjects.ValueObject;

public class LookUpValueLocaleEntity extends ValueObject {

	private String lookUpValue;

	private SupportedLocalesEntity locale;

	private Integer lookUpId;

	private Integer lookUpValueId;

	private Short localeId;

	public Integer getLookUpValueId() {
		return lookUpValueId;
	}

	public void setLookUpValueId(Integer lookUpValueId) {
		this.lookUpValueId = lookUpValueId;
	}

	public SupportedLocalesEntity getLocale() {
		return locale;
	}

	public void setLocaleId(Short localeId) {
		this.localeId = localeId;
	}

	public Short getLocaleId() {
		return localeId;
	}

	public void setLocale(SupportedLocalesEntity locale) {
		this.locale = locale;
	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

	public String getLookUpValue() {
		return lookUpValue;
	}

	public void setLookUpValue(String lookUpValue) {
		this.lookUpValue = lookUpValue;
	}

	public String getLocaleName() {
		return locale.getLocaleName();
	}

}
