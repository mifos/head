/**

 * LookUpValueEntity.java    version: 1.0



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

import java.util.Set;

import org.mifos.application.master.MessageLookup;
import org.mifos.config.LocalizedTextLookup;
import org.mifos.framework.business.PersistentObject;

public class LookUpValueEntity extends PersistentObject implements LocalizedTextLookup {

	private Integer lookUpId;

	/*
	 * The key used for retrieving localized resource bundle message text.
	 */
	private String lookUpName;

	private MifosLookUpEntity lookUpEntity;

	private Set<LookUpValueLocaleEntity> lookUpValueLocales;

	public String getLookUpName() {
		return lookUpName;
	}

	public void setLookUpName(String lookUpName) {
		this.lookUpName = lookUpName;
	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

	public MifosLookUpEntity getLookUpEntity() {
		return lookUpEntity;
	}

	public void setLookUpEntity(MifosLookUpEntity lookUpEntity) {
		this.lookUpEntity = lookUpEntity;
	}

	public Set<LookUpValueLocaleEntity> getLookUpValueLocales() {
		return lookUpValueLocales;
	}

	public void setLookUpValueLocales(
			Set<LookUpValueLocaleEntity> lookUpValueLocales) {
		this.lookUpValueLocales = lookUpValueLocales;
	}

	/*
	 * Get the localized text for this object (or the override
	 * value from the database if present)
	 */
	public String getMessageText() {
		String messageText = null;
		for (LookUpValueLocaleEntity lookUpValueLocale : getLookUpValueLocales()) {
			if (lookUpValueLocale.getLocaleId().equals(MasterDataEntity.CUSTOMIZATION_LOCALE_ID)) {
				messageText = lookUpValueLocale.getLookUpValue();				
			}
		}
	
		if (messageText != null && messageText.length() > 0) {
			return messageText;
		} else {
			return MessageLookup.getInstance().lookup(this);
		}
	}

	/*
	 * The key used to lookup localized properties text.
	 */
	public String getPropertiesKey() {
		return getLookUpName();
	}

}
