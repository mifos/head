/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.office.business;

import org.mifos.application.master.MessageLookup;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.framework.business.View;

/*
 * Feb 2008 i18n work in progress.
 * looks like we need to make officeName, officeNameKey and
 * levelName, levelNameKey and then go through MessageLookup to resolve them
 */
public class OfficeView extends View {

	private Short officeId;

	private String officeName;

	private Short levelId;

	private String levelNameKey;

	private Integer versionNo;

	public OfficeView(Short officeId, String officeName, Short levelId,
			Integer versionNo) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.levelId = levelId;
		this.versionNo = versionNo;
	}
	
	public OfficeView(Short officeId, String officeName,
			OfficeLevel level, String levelNameKey, Integer versionNo) {
		this(officeId, officeName, level.getValue(), levelNameKey, versionNo);
	}

	public OfficeView(Short officeId, String officeName, Short levelId,
			String levelNameKey, Integer versionNo) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.levelId = levelId;
		this.levelNameKey = levelNameKey;
		this.versionNo = versionNo;
	}

	public OfficeView(Short officeId, String officeName, Integer versionNo) {
		this.officeId = officeId;
		this.officeName = officeName;
		this.versionNo = versionNo;
	}

	public Short getLevelId() {
		return levelId;
	}

	public Short getOfficeId() {
		return officeId;
	}


	public String getOfficeName() {
		return officeName;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public String getLevelName() {
		return MessageLookup.getInstance().lookup(levelNameKey);
	}

	public OfficeView(Short levelId, String levelNameKey) {
		this.levelId = levelId;
		this.levelNameKey = levelNameKey;
	}

	public String getDisplayName() {
		return getLevelName() + "(" + officeName + ")";
	}
}
