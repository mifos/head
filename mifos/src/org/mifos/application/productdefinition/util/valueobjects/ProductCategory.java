/**

 * ProductCategory.java    version: 1.0

 

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

import java.sql.Date;

import org.mifos.application.master.util.valueobjects.PrdCategoryStatus;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * @author ashishsm
 *
 */
public class ProductCategory extends ValueObject {

	//-----------------------Constructors----------	
	/**
	 *Default Constructor 
	 */
	public ProductCategory() {
		super();
		this.setResultName("ProductCategory");
	}

	//-----------------------Instance Variables----------
	
	/**
	 * Serial Version UID for serialization
	 */
	private static final long serialVersionUID = 15489732897575754L;
	 /** The value of the simple productCategoryID property. */
	private Short productCategoryID;
	 /** The value of the  productType association. */
	private ProductType productType;
	 /** The value of the simple productCategoryName property. */
	private String productCategoryName;
	 /** The value of the simple createdDate property. */
	private Date createdDate;
	 /** The value of the simple createdBy property. */
	private Short createdBy;
	 /** The value of the simple updatedDate property. */
	private Date updatedDate;
	 /** The value of the simple updatedBy property. */
	private Short updatedBy;
	 /** The value of the simple productCategoryDesc property. */
	private String productCategoryDesc;
	 /** The value of the simple officeId property. */
	private Short officeId;
	 /** The value of the PrdCategoryStatus association. */
	private PrdCategoryStatus prdCategoryStatus;
	 /** The value of the simple versionNo property. */
	private Integer versionNo;
	  /** The value of the simple globalPrdOfferingNum property. */
    private java.lang.String globalPrdOfferingNum;

	//-----------------------Public methods----------
	
	/**
	 * @return Returns the createdBy.
	 */
	public Short getCreatedBy() {
		return createdBy;
	}
	/**
	 * @param createdBy The createdBy to set.
	 */
	public void setCreatedBy(Short createdBy) {
		this.createdBy = createdBy;
	}
	/**
	 * @return Returns the createdDate.
	 */
	public Date getCreatedDate() {
		return createdDate;
	}
	/**
	 * @param createdDate The createdDate to set.
	 */

	public void setCreatedDate(Date createdDate) {
		this.createdDate= createdDate;
	}
	
	public void setCreateDate(String createDate) {
		this.createdDate= null; 
	}
	/**
	 * @return Returns the product.
	 */
	public ProductType getProductType() {
		return productType;
	}
	/**
	 * @param product The product to set.
	 */
	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
	/**
	 * @return Returns the productCategoryDesc.
	 */
	public String getProductCategoryDesc() {
		return productCategoryDesc;
	}
	/**
	 * @param productCategoryDesc The productCategoryDesc to set.
	 */
	public void setProductCategoryDesc(String productCategoryDesc) {
		this.productCategoryDesc = productCategoryDesc;
	}
	/**
	 * @return Returns the productCategoryID.
	 */
	public Short getProductCategoryID() {
		return productCategoryID;
	}
	/**
	 * @param productCategoryID The productCategoryID to set.
	 */
	public void setProductCategoryID(Short productCategoryID) {
		this.productCategoryID = productCategoryID;
	}
	/**
	 * @return Returns the productCategoryName.
	 */
	public String getProductCategoryName() {
		return productCategoryName;
	}
	/**
	 * @param productCategoryName The productCategoryName to set.
	 */
	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}
	/**
	 * @return Returns the updatedBy.
	 */
	public Short getUpdatedBy() {
		return updatedBy;
	}
	/**
	 * @param updatedBy The updatedBy to set.
	 */
	public void setUpdatedBy(Short updatedBy) {
		this.updatedBy = updatedBy;
	}
	
	/**
	 * @return Returns the updatedDate.
	 */
	public Date getUpdatedDate() {
		return updatedDate;
	}
	
	/**
	 * @param updatedDate The updatedDate to set.
	 */
	public void setUpdatedDate(Date updatedDate) {
		this.updatedDate = updatedDate;
	}
	
	public void setUpdateDate(String updateDate) {
		this.updatedDate = null; 
	}
	/**
	 * @return Returns the officeId.
	 */
	public Short getOfficeId() {
		return officeId;
	}
	
	/**
	 * @param officeId The officeId to set.
	 */
	public void setOfficeId(Short officeId) {
		this.officeId = officeId;
	}
	/**
	 * @return Returns the prdCategoryStatus.
	 */
	public PrdCategoryStatus getPrdCategoryStatus() {
		return prdCategoryStatus;
	}
	/**
	 * @param prdCategoryStatus The prdCategoryStatus to set.
	 */
	public void setPrdCategoryStatus(PrdCategoryStatus prdCategoryStatus) {
		this.prdCategoryStatus = prdCategoryStatus;
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
	 * @return Returns the globalPrdOfferingNum.
	 */
	public java.lang.String getGlobalPrdOfferingNum() {
		return globalPrdOfferingNum;
	}
	/**
	 * @param globalPrdOfferingNum The globalPrdOfferingNum to set.
	 */
	public void setGlobalPrdOfferingNum(java.lang.String globalPrdOfferingNum) {
		this.globalPrdOfferingNum = globalPrdOfferingNum;
	}
	
}
