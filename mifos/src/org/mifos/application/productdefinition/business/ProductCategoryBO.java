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

package org.mifos.application.productdefinition.business;

import org.mifos.application.master.util.valueobjects.PrdCategoryStatus;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;

/**
 * @author ashishsm
 * 
 */
public class ProductCategoryBO extends BusinessObject {

	private Short productCategoryID;

	private ProductTypeEntity productType;

	private String productCategoryName;

	private String productCategoryDesc;

	private OfficeBO office;

	private PrdCategoryStatus prdCategoryStatus;

	private String globalPrdOfferingNum;

	public ProductCategoryBO() {
	}	
	public ProductCategoryBO(UserContext userContext) {
		super(userContext);

	}

	public ProductTypeEntity getProductType() {
		return productType;
	}

	public void setProductType(ProductTypeEntity productType) {
		this.productType = productType;
	}

	public String getProductCategoryDesc() {
		return productCategoryDesc;
	}

	public void setProductCategoryDesc(String productCategoryDesc) {
		this.productCategoryDesc = productCategoryDesc;
	}

	public Short getProductCategoryID() {
		return productCategoryID;
	}

	public void setProductCategoryID(Short productCategoryID) {
		this.productCategoryID = productCategoryID;
	}

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}


	private OfficeBO getOffice() {
		return office;
	}

	private void setOffice(OfficeBO office) {
		this.office = office;
	}

	public PrdCategoryStatus getPrdCategoryStatus() {
		return prdCategoryStatus;
	}

	public void setPrdCategoryStatus(PrdCategoryStatus prdCategoryStatus) {
		this.prdCategoryStatus = prdCategoryStatus;
	}

	public String getGlobalPrdOfferingNum() {
		return globalPrdOfferingNum;
	}

	public void setGlobalPrdOfferingNum(String globalPrdOfferingNum) {
		this.globalPrdOfferingNum = globalPrdOfferingNum;
	}

}
