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

package org.mifos.application.productdefinition.util.valueobjects;

import java.util.List;

import org.mifos.application.master.util.valueobjects.LookUpValue;
import org.mifos.framework.util.valueobjects.ValueObject;

public class ProductType extends ValueObject {
	//-----------------------Constructors----------
	public ProductType() {
		super();
		this.lookUpValue=new LookUpValue();
	}
	
	public ProductType(Short productTypeID,String value,Integer versionNo) {
		super();
		this.lookUpValue=new LookUpValue();
		this.productTypeID=productTypeID;
		this.value=value;
		this.versionNo=versionNo;
	}
	
	//-----------------------Instance Variables----------
	
	/**
	 * Serial Version UID for serialization
	 */
	private static final long serialVersionUID = 1125354354543554432L;
	 /** The value of the simple productTypeID property. */
	private Short productTypeID;
	 /** The value of the LookUpValue association. */
	private LookUpValue lookUpValue;
	 /** The value of the simple latenessDays property. */
	private Short latenessDays;
	 /** The value of the simple dormancyDays property. */
	private Short dormancyDays;
	 /** The value of the simple versionNo property. */
    private Integer versionNo;
    /** The value of the productTypeList association. */
    private List<ProductType> productTypeList;
    /** The value of the simple value property. */
    private String value;
	
	//-----------------------Public methods----------
    /**
	 * @return Returns the lookUpValue.
	 */
	public LookUpValue getLookUpValue() {
		return lookUpValue;
	}

	/**
	 * @param lookUpValue The lookUpValue to set.
	 */
	public void setLookUpValue(LookUpValue lookUpValue) {
		this.lookUpValue = lookUpValue;
	}
	/**
	 * @return Returns the productTypeID.
	 */
	public Short getProductTypeID() {
		return productTypeID;
	}
	/**
	 * @param productTypeID The productTypeID to set.
	 */
	public void setProductTypeID(Short productTypeID) {
		this.productTypeID = productTypeID;
	}
	/**
	 * @return Returns the dormancyDays.
	 */
	public Short getDormancyDays() {
		return dormancyDays;
	}
	/**
	 * @param dormancyDays The dormancyDays to set.
	 */
	public void setDormancyDays(Short dormancyDays) {
		this.dormancyDays = dormancyDays;
	}
	/**
	 * @return Returns the latenessDays.
	 */
	public Short getLatenessDays() {
		return latenessDays;
	}
	/**
	 * @param latenessDays The latenessDays to set.
	 */
	public void setLatenessDays(Short latenessDays) {
		this.latenessDays = latenessDays;
	}
	/**
	 * @return Returns the versionNo.
	 */
	public Integer getVersionNo() {
		return versionNo;
	}
	/**
	 * @param versionNo The versionNo to set.
	 */
	public void setVersionNo(Integer versionNo) {
		this.versionNo = versionNo;
	}
	/**
	 * @return Returns the productTypeList.
	 */
	public List<ProductType> getProductTypeList() {
		return productTypeList;
	}
	/**
	 * @param productTypeList The productTypeList to set.
	 */
	public void setProductTypeList(List<ProductType> productTypeList) {
		this.productTypeList = productTypeList;
	}
	/**
	 * @return Returns the value.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value The value to set.
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	

}
