/**

 * Product.java    version: xxx

 

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

import java.util.List;

import org.mifos.application.master.util.valueobjects.LookUpValue;
import org.mifos.framework.business.PersistentObject;

/**
 * @author ashishsm
 * 
 */

public class ProductTypeEntity extends PersistentObject {

	private Short productTypeID;

	private LookUpValue lookUpValue;

	private Short latenessDays;

	private Short dormancyDays;

	private List<ProductTypeEntity> productTypeList;

	private String value;

	public ProductTypeEntity() {
		super();
		this.lookUpValue = new LookUpValue();
	}

	public ProductTypeEntity(Short productTypeID, String value,
			Integer versionNo) {
		super();
		this.lookUpValue = new LookUpValue();
		this.productTypeID = productTypeID;
		this.value = value;
		this.versionNo = versionNo;
	}

	public LookUpValue getLookUpValue() {
		return lookUpValue;
	}

	public void setLookUpValue(LookUpValue lookUpValue) {
		this.lookUpValue = lookUpValue;
	}

	public Short getProductTypeID() {
		return productTypeID;
	}

	public void setProductTypeID(Short productTypeID) {
		this.productTypeID = productTypeID;
	}

	public Short getDormancyDays() {
		return dormancyDays;
	}

	public void setDormancyDays(Short dormancyDays) {
		this.dormancyDays = dormancyDays;
	}

	public Short getLatenessDays() {
		return latenessDays;
	}

	public void setLatenessDays(Short latenessDays) {
		this.latenessDays = latenessDays;
	}

	public List<ProductTypeEntity> getProductTypeList() {
		return productTypeList;
	}

	public void setProductTypeList(List<ProductTypeEntity> productTypeList) {
		this.productTypeList = productTypeList;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
