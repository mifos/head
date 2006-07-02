/**

 * OfficeLevel.java    version: 1.0



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
package org.mifos.application.office.util.helpers;

import org.mifos.framework.business.View;

/**
 * 
 * @author rajenders
 * 
 */
public class ParentOfficeView extends View {

	private Short levelId;

	private String levelName;

	private Short officeId;

	private String officeName;

	public ParentOfficeView() {

	}

	public ParentOfficeView(Short officeId, String officeName, Short levelId) {
		this.levelId = levelId;
		this.officeName = officeName;
		this.officeId = officeId;

	}

	public ParentOfficeView(Short officeId, String officeName, Short levelId,
			String levelName) {
		this.levelId = levelId;
		this.officeName = officeName;
		this.officeId = officeId;
		this.levelName = levelName;
	}

	public Short getLevelId() {
		return levelId;
	}

	public String getOfficeName() {
		return officeName;
	}

	public Short getOfficeId() {
		return officeId;
	}

	public String getLevelName() {
		return levelName;
	}

	public String getDisplayName() {
		// bug 29011 removed white space

		return officeName + " (" + levelName + ")";
	}

}
