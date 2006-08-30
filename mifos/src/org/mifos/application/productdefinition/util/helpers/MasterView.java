/**

 * MasterObject.java    version: xxx

 

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
package org.mifos.application.productdefinition.util.helpers;

import org.mifos.framework.business.View;

/**
 * This is a helper class which would be used to retrieve master data when we
 * need only certain details of the object hence instead of loading the entire
 * object we can load only this object using a query. E.g. yes no master we are
 * storing id of yes and locale specifice string represent the yes
 */
public class MasterView extends View {

	private Short id;

	private String name;

	private Integer versionNo;

	public MasterView() {
		super();
	}

	public MasterView(Short id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public MasterView(Short id, String name, Integer versionNo) {
		super();
		this.id = id;
		this.name = name;
		this.versionNo = versionNo;
	}

	public Short getId() {
		return id;
	}

	public void setId(Short id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getVersionNo() {
		return versionNo;
	}

	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}

}
