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

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.persistence.ProductCategoryPersistence;
import org.mifos.application.productdefinition.util.helpers.PrdCategoryStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.StringUtils;

public class ProductCategoryBO extends BusinessObject {

	private final Short productCategoryID;

	private final ProductTypeEntity productType;

	private final OfficeBO office;

	private final String globalPrdCategoryNum;

	private String productCategoryName;

	private String productCategoryDesc;

	private PrdCategoryStatusEntity prdCategoryStatus;
	
	private MifosLogger prdLoanLogger=MifosLogManager.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	protected ProductCategoryBO() {
		productCategoryID = null;
		productType = null;
		office = null;
		globalPrdCategoryNum = null;
	}

	public ProductCategoryBO(UserContext userContext,
			ProductTypeEntity productType, String productCategoryName,
			String productCategoryDesc) throws ProductDefinitionException {
		super(userContext);
		try{
		prdLoanLogger.debug("Creating product category");
		validateDuplicateProductCategoryName(productCategoryName);
		this.productCategoryID = null;
		this.productType = productType;
		this.office = new OfficePersistence().getOffice(userContext
				.getBranchId());
		this.globalPrdCategoryNum = generatePrdCategoryNum();
		this.productCategoryName = productCategoryName;
		this.productCategoryDesc = productCategoryDesc;
		this.prdCategoryStatus = new PrdCategoryStatusEntity(PrdCategoryStatus.ACTIVE);
		setCreateDetails();
		prdLoanLogger.debug("Creation of product category done");
		}
		catch (PersistenceException e) {

			throw new ProductDefinitionException(e);
		}
	}

	public ProductTypeEntity getProductType() {
		return productType;
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

	public String getProductCategoryName() {
		return productCategoryName;
	}

	public void setProductCategoryName(String productCategoryName) {
		this.productCategoryName = productCategoryName;
	}

	public PrdCategoryStatusEntity getPrdCategoryStatus() {
		return prdCategoryStatus;
	}

	public void setPrdCategoryStatus(PrdCategoryStatusEntity prdCategoryStatus) {
		this.prdCategoryStatus = prdCategoryStatus;
	}
	
	public String getGlobalPrdCategoryNum() {
		return globalPrdCategoryNum;
	}

	private String generatePrdCategoryNum() {
		prdLoanLogger.debug("Generating new product category global number");
		StringBuilder globalPrdOfferingNum = new StringBuilder();
		globalPrdOfferingNum.append(userContext.getBranchId());
		globalPrdOfferingNum.append("-");
		Short maxPrdID = new ProductCategoryPersistence().getMaxPrdCategoryId();
		globalPrdOfferingNum.append(StringUtils.lpad(String
				.valueOf(maxPrdID != null ? maxPrdID + 1
						: ProductDefinitionConstants.DEFAULTMAX), '0', 3));
		prdLoanLogger.debug("Generation of new product category global number done");
		return globalPrdOfferingNum.toString();
	}
	
	private void validateDuplicateProductCategoryName(String productCategoryName) throws ProductDefinitionException{
		prdLoanLogger.debug("Checking for duplicate product category name");
		if(!new ProductCategoryPersistence().getProductCategory(productCategoryName).equals(Integer.valueOf("0")))
			throw new ProductDefinitionException(
					ProductDefinitionConstants.DUPLCATEGORYNAME);
	}
	
	private void validateDuplicateProductCategoryName(String productCategoryName,Short productCategoryId) throws ProductDefinitionException{
		prdLoanLogger.debug("Checking for duplicate product category name");
		if(!new ProductCategoryPersistence().getProductCategory(productCategoryName,productCategoryId).equals(Integer.valueOf("0")))
			throw new ProductDefinitionException(
					ProductDefinitionConstants.DUPLCATEGORYNAME);
	}
	
	
	public void updateProductCategory(String productCategoryName,
			String productCategoryDesc,
			PrdCategoryStatusEntity prdCategoryStatus)
			throws ProductDefinitionException {
		prdLoanLogger.debug("Updating product category name");
		validateDuplicateProductCategoryName(productCategoryName,productCategoryID);
		this.productCategoryName = productCategoryName;
		this.productCategoryDesc = productCategoryDesc;
		this.prdCategoryStatus = prdCategoryStatus;
		try {
			new ProductCategoryPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
		prdLoanLogger.debug("Updating product category done");
	}
	
	public void save() throws ProductDefinitionException{
		try {
			new ProductCategoryPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
	}
	
}
