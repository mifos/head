/**

 * PrdStatus.java    version: xxx

 

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

package org.mifos.application.productdefinition.business;

import org.mifos.framework.business.PersistentObject;

/**
 * This class represents the each prd status
 */
public class PrdStatusEntity extends PersistentObject {

	private Short offeringStatusId;

	private ProductTypeEntity prdType;

	private PrdStateEntity prdStateId;

	private Short status;

	public PrdStatusEntity() {
	}

	public Short getOfferingStatusId() {
		return offeringStatusId;
	}

	public void setOfferingStatusId(Short offeringStatusId) {
		this.offeringStatusId = offeringStatusId;
	}

	public PrdStateEntity getPrdStateId() {
		return prdStateId;
	}

	public void setPrdStateId(PrdStateEntity prdStateId) {
		this.prdStateId = prdStateId;
	}

	public Short getStatus() {
		return this.status;
	}

	public void setStatus(Short status) {
		this.status = status;
	}

	public ProductTypeEntity getPrdType() {
		return prdType;
	}

	public void setPrdType(ProductTypeEntity prdType) {
		this.prdType = prdType;
	}
}
