/**

 * AccountStateFlag.java    version: 1.0



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
package org.mifos.application.accounts.util.valueobjects;

import java.util.Set;

import org.mifos.application.master.util.valueobjects.LookUpValue;
import org.mifos.framework.util.valueobjects.ValueObject;

public class AccountStateFlag extends ValueObject{
	/** The value of the FLAG_ID column. */
	private Short flagId;
	
	/** The value of the STATUS_ID column. */
	private Short statusId;
	
	/** The value of the LOOK_UP_ID column. */
	private Integer lookUpId;
	
	/** The value of the FLAG_DESCRIPTION column. */
	private String flagDescription;

	public AccountStateFlag(){	}
	public String getFlagDescription() {
		return flagDescription;
	}

	public void setFlagDescription(String flagDescription) {
		this.flagDescription = flagDescription;
	}

	public Short getFlagId() {
		return flagId;
	}

	public void setFlagId(Short flagId) {
		this.flagId = flagId;
	}

	public Integer getLookUpId() {
		return lookUpId;
	}

	public void setLookUpId(Integer lookUpId) {
		this.lookUpId = lookUpId;
	}

	public Short getStatusId() {
		return statusId;
	}

	public void setStatusId(Short statusId) {
		this.statusId = statusId;
	}
}
