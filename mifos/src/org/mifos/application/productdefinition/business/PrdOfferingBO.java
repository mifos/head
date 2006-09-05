/**

 * PrdOffering.java    version: 1.0

 

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

import java.util.Date;

import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.persistence.OfficePersistence;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.persistence.PrdOfferingPersistence;
import org.mifos.application.productdefinition.util.helpers.PrdStatus;
import org.mifos.application.productdefinition.util.helpers.ProductDefinitionConstants;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.components.logger.LoggerConstants;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.components.logger.MifosLogger;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.DateUtils;
import org.mifos.framework.util.helpers.StringUtils;

public class PrdOfferingBO extends BusinessObject {

	private Short prdOfferingId;

	private String prdOfferingName;

	private String prdOfferingShortName;

	private String globalPrdOfferingNum;

	private ProductTypeEntity prdType;

	private ProductCategoryBO prdCategory;

	private PrdStatusEntity prdStatus;

	private PrdApplicableMasterEntity prdApplicableMaster;

	private Date startDate;

	private Date endDate;

	private OfficeBO office;

	private String description;

	private MifosLogger prdLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	protected PrdOfferingBO() {
		office = new OfficeBO();
		prdCategory = new ProductCategoryBO();
		prdStatus = new PrdStatusEntity();
		prdApplicableMaster = null;
	}

	// TODO to be removed.
	protected PrdOfferingBO(UserContext userContext) {
		super(userContext);
	}

	protected PrdOfferingBO(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate)
			throws ProductDefinitionException {
		super(userContext);
		try{
		vaildate(userContext, prdOfferingName, prdOfferingShortName,
				prdCategory, prdApplicableMaster, startDate);
		this.prdOfferingName = prdOfferingName;
		this.prdOfferingShortName = prdOfferingShortName;
		this.prdCategory = prdCategory;
		this.prdType = prdCategory.getProductType();
		this.prdApplicableMaster = prdApplicableMaster;
		this.startDate = startDate;
		this.globalPrdOfferingNum = generatePrdOfferingGlobalNum();
		this.prdStatus = setStatus(startDate, prdType);
		this.office = new OfficePersistence().getOffice(userContext
				.getBranchId());
		}
		catch (PersistenceException e) {

			throw new ProductDefinitionException(e);
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getGlobalPrdOfferingNum() {
		return globalPrdOfferingNum;
	}

	public void setGlobalPrdOfferingNum(String globalPrdOfferingNum) {
		this.globalPrdOfferingNum = globalPrdOfferingNum;
	}

	public OfficeBO getOffice() {

		return office;
	}

	public void setOffice(OfficeBO office) {
		this.office = office;
	}

	public PrdApplicableMasterEntity getPrdApplicableMaster() {
		return prdApplicableMaster;
	}

	public void setPrdApplicableMaster(
			PrdApplicableMasterEntity prdApplicableMaster) {
		this.prdApplicableMaster = prdApplicableMaster;
	}

	public ProductCategoryBO getPrdCategory() {
		return prdCategory;
	}

	public void setPrdCategory(ProductCategoryBO prdCategory) {
		this.prdCategory = prdCategory;
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

	public String getPrdOfferingShortName() {
		return prdOfferingShortName;
	}

	public void setPrdOfferingShortName(String prdOfferingShortName) {
		this.prdOfferingShortName = prdOfferingShortName;
	}

	public PrdStatusEntity getPrdStatus() {
		return prdStatus;
	}

	public void setPrdStatus(PrdStatusEntity prdStatus) {
		this.prdStatus = prdStatus;
	}

	public ProductTypeEntity getPrdType() {
		return prdType;
	}

	public void setPrdType(ProductTypeEntity prdType) {
		this.prdType = prdType;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	private String generatePrdOfferingGlobalNum()
			throws ProductDefinitionException {
		prdLogger.debug("Generating new product Offering global number");
		StringBuilder globalPrdOfferingNum = new StringBuilder();
		globalPrdOfferingNum.append(userContext.getBranchId());
		globalPrdOfferingNum.append("-");
		Short maxPrdID = null;
		try {
			maxPrdID = new PrdOfferingPersistence().getMaxPrdOffering();
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
		globalPrdOfferingNum.append(StringUtils.lpad(String
				.valueOf(maxPrdID != null ? maxPrdID + 1
						: ProductDefinitionConstants.DEFAULTMAX), '0', 3));
		prdLogger
				.debug("Generation of new product Offering global number done");
		return globalPrdOfferingNum.toString();
	}

	private void vaildate(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate)
			throws ProductDefinitionException {
		if (userContext == null
				|| prdOfferingName == null
				|| prdOfferingShortName == null
				|| prdCategory == null
				|| prdApplicableMaster == null
				|| startDate == null
				|| (prdOfferingShortName.length() > 4)
				|| startDate.compareTo(DateUtils
						.getCurrentDateWithoutTimeStamp()) < 0) {
			throw new ProductDefinitionException("errors.create");
		}
	}

	private PrdStatusEntity setStatus(Date startDate, ProductTypeEntity prdType)
			throws ProductDefinitionException {
		PrdStatus prdStatus = null;
		if (startDate.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) == 0)
			prdStatus = getActivePrdStatus(prdType);
		else
			prdStatus = getInActivePrdStatus(prdType);
		try {
			return new PrdOfferingPersistence().getPrdStatus(prdStatus);
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
	}

	private PrdStatus getActivePrdStatus(ProductTypeEntity prdType) {
		if (prdType.getProductTypeID().equals(ProductType.LOAN.getValue()))
			return PrdStatus.LOANACTIVE;
		else
			return PrdStatus.SAVINGSACTIVE;

	}

	private PrdStatus getInActivePrdStatus(ProductTypeEntity prdType) {
		if (prdType.getProductTypeID().equals(ProductType.LOAN.getValue()))
			return PrdStatus.LOANINACTIVE;
		else
			return PrdStatus.SAVINGSINACTIVE;

	}
}
