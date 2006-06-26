/**

 * PrdOfferingMaster.java    version: xxx

 

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

package org.mifos.application.productdefinition.util.helpers;

import org.mifos.framework.business.View;

/**
 * This is a helper class which would be used when we need only certain details
 * of a product offering hence instead of loading the entire product offering
 * object we can load only this object using a query.
 * 
 * @author ashishsm
 * 
 */
public class PrdOfferingView extends View {

	private Short prdOfferingId;

	private String prdOfferingName;

	private String globalPrdOfferingNum;

	public PrdOfferingView() {
		super();

	}

	public PrdOfferingView(Short prdOfferingId, String prdOfferingName,
			String globalPrdOfferingNum) {
		this.globalPrdOfferingNum = globalPrdOfferingNum;
		this.prdOfferingName = prdOfferingName;
		this.prdOfferingId = prdOfferingId;

	}

	public String getGlobalPrdOfferingNum() {
		return globalPrdOfferingNum;
	}

	public void setGlobalPrdOfferingNum(String globalPrdOfferingNum) {
		this.globalPrdOfferingNum = globalPrdOfferingNum;
	}

	public Short getPrdOfferingId() {
		return prdOfferingId;
	}

	public void setPrdOfferingId(Short prdOfferingId) {
		this.prdOfferingId = prdOfferingId;
	}

	public String getPrdOfferingName() {
		return prdOfferingName;
	}

	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

}
