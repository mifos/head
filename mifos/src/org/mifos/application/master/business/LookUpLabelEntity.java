/**

 * LookUpLabelEntity.java    version: 1.0



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

public class LookUpLabelEntity extends PersistentObject {

	private String labelName;

	private Short LookUpLabelId;

	private Short localeId;

	private SupportedLocalesEntity locale;

	private MifosLookUpEntity lookUpEntity;

	public String getLabelName() {
		return labelName;
	}

	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}

	public SupportedLocalesEntity getLocale() {
		return locale;
	}

	public void setLocale(SupportedLocalesEntity locale) {
		this.locale = locale;
	}

	public void setLocaleId(Short localeId) {
		this.localeId = localeId;
	}

	public Short getLocaleId() {
		return localeId;
	}

	public Short getLookUpLabelId() {
		return LookUpLabelId;
	}

	public void setLookUpLabelId(Short lookUpLabelId) {
		LookUpLabelId = lookUpLabelId;
	}

	public MifosLookUpEntity getLookUpEntity() {
		return lookUpEntity;
	}

	public void setLookUpEntity(MifosLookUpEntity lookUpEntity) {
		this.lookUpEntity = lookUpEntity;
	}

}
