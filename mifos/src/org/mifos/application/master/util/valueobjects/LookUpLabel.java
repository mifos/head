/**

 * LookUpLabel.java    version: xxx



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

public class LookUpLabel extends ValueObject{

	private String labelName;
	private SupportedLocales locale;
	private LookUpEntity lookUpEntity;
	private Short entityId;
	private Short localeId;
	public void setEntityId(Short entityId)
	{
		this.entityId = entityId;
	}

	public Short getEntityId()
	{
		return entityId;
	}

	/**
	 * @return Returns the lookUpEntity.
	 */
	public LookUpEntity getLookUpEntity() {
		return lookUpEntity;
	}
	/**
	 * @param lookUpEntity The lookUpEntity to set.
	 */
	public void setLookUpEntity(LookUpEntity lookUpEntity) {
		this.lookUpEntity = lookUpEntity;
	}

	/**
	 * @return Returns the labelName.
	 */
	public String getLabelName() {
		return labelName;
	}
	/**
	 * @param labelName The labelName to set.
	 */
	public void setLabelName(String labelName) {
		this.labelName = labelName;
	}
	/**
	 * @return Returns the locale.
	 */
	public SupportedLocales getLocale() {
		return locale;
	}
	/**
	 * @param locale The locale to set.
	 */
	public void setLocale(SupportedLocales locale) {
		this.locale = locale;
	}
	public void setLocaleId(Short localeId)
	{
		this.localeId = localeId;
	}

	public Short getLocaleId()
	{
			return localeId;
	}

}
