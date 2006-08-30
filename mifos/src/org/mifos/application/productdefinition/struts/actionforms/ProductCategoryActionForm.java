/**

 * ProductCategoryActionForm.java    version: xxx

 

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

package org.mifos.application.productdefinition.struts.actionforms;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.mifos.application.master.util.valueobjects.PrdCategoryStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.valueobjects.ProductType;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.struts.actionforms.MifosSearchActionForm;
import org.mifos.framework.util.helpers.Constants;

public class ProductCategoryActionForm extends MifosSearchActionForm {

	public ProductCategoryActionForm() {
		this.productType = new ProductType();
		this.prdCategoryStatus=new PrdCategoryStatus();
	}
	/**
	 * Serial Version UID for Serialization
	 */
	private static final long serialVersionUID = 154345076764734632L;
	/**
	 * id of product category
	 */
	private Short productCategoryID;
	/**
	 * product type of product category
	 */
	private ProductType productType;
	/**
	 * name of product category
	 */
	private String productCategoryName;
	/**
	 * created date of product category
	 */
	private String createDate;
	/**
	 * id of created by of product category
	 */
	private Short createdBy;
	/**
	 * date of updation of product category
	 */
	private String updateDate;
	/**
	 * id of updated by of product category
	 */
	private Short updatedBy;
	/**
	 * description of the product category
	 */
	private String productCategoryDesc;
	/**
	 * status of the product category
	 */
	private PrdCategoryStatus prdCategoryStatus;
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
	 * @return Returns the createDate.
	 */
	public String getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate The createDate to set.
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
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
	 * @return Returns the productType.
	 */
	public ProductType getProductType() {
		return productType;
	}
	/**
	 * @param productType The productType to set.
	 */
	public void setProductType(ProductType productType) {
		this.productType = productType;
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
	 * @return Returns the updateDate.
	 */
	public String getUpdateDate() {
		return updateDate;
	}
	/**
	 * @param updateDate The updateDate to set.
	 */
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	/**
	 * @return Returns the description.
	 */
	public String getProductCategoryDesc() {
		return productCategoryDesc;
	}
	/**
	 * @param description The description to set.
	 */
	public void setProductCategoryDesc(String productCategoryDesc) {
		this.productCategoryDesc = productCategoryDesc;
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
	 * This method is used to do any custom validations and also to skip vaildation for any 
	 * particular method
	 * 
	 * @param mapping
	 * @param request
	 * @return
	 */
	public ActionErrors customValidate(ActionMapping mapping,
			HttpServletRequest request) {
		String methodCalled= request.getParameter(ProductDefinitionConstants.METHOD);
		if(null !=methodCalled) {
			if(ProductDefinitionConstants.CANCELMETHOD.equals(methodCalled) || 
					ProductDefinitionConstants.GETMETHOD.equals(methodCalled) || 
					ProductDefinitionConstants.LOADMETHOD.equals(methodCalled) || 
					ProductDefinitionConstants.SEARCHMETHOD.equals(methodCalled) || 
					ProductDefinitionConstants.MANAGEMETHOD.equals(methodCalled)) {
				MifosLogManager.getLogger(LoggerConstants.LOGINLOGGER).info(
						"Skipping validation for "+methodCalled+ " method");
				request.setAttribute(Constants.SKIPVALIDATION,Boolean.valueOf(true));
			}
		}
		return null;
	}
}
