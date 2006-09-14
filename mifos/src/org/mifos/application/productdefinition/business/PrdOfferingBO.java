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

public abstract class PrdOfferingBO extends BusinessObject {

	private final Short prdOfferingId;

	private String prdOfferingName;

	private String prdOfferingShortName;

	private final String globalPrdOfferingNum;

	private final ProductTypeEntity prdType;

	private ProductCategoryBO prdCategory;

	private PrdStatusEntity prdStatus;

	private PrdApplicableMasterEntity prdApplicableMaster;

	private Date startDate;

	private Date endDate;

	private final OfficeBO office;

	private String description;

	private MifosLogger prdLogger = MifosLogManager
			.getLogger(LoggerConstants.PRDDEFINITIONLOGGER);

	protected PrdOfferingBO() {
		prdOfferingId = null;
		globalPrdOfferingNum = null;
		prdType = null;
		office = null;
		prdApplicableMaster = null;
	}

	protected PrdOfferingBO(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate)
			throws ProductDefinitionException {
		this(userContext, prdOfferingName, prdOfferingShortName, prdCategory,
				prdApplicableMaster, startDate, null, null);
	}

	protected PrdOfferingBO(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			Date endDate, String description) throws ProductDefinitionException {
		super(userContext);
		prdLogger.debug("creating product offering");
		vaildate(userContext, prdOfferingName, prdOfferingShortName,
				prdCategory, prdApplicableMaster, startDate, endDate);
		validateDuplicateProductOfferingName(prdOfferingName);
		validateDuplicateProductOfferingShortName(prdOfferingShortName);
		prdOfferingId = null;
		this.prdOfferingName = prdOfferingName;
		this.prdOfferingShortName = prdOfferingShortName;
		this.prdCategory = prdCategory;
		this.prdType = prdCategory.getProductType();
		this.prdApplicableMaster = prdApplicableMaster;
		this.startDate = startDate;
		this.endDate = endDate;
		this.description = description;
		this.globalPrdOfferingNum = generatePrdOfferingGlobalNum();
		this.prdStatus = getPrdStatus(startDate, prdType);
		try {
			this.office = new OfficePersistence().getOffice(userContext
					.getBranchId());
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
		prdLogger.debug("creating product offering done");
	}

	public Short getPrdOfferingId() {
		return prdOfferingId;
	}

	public String getPrdOfferingName() {
		return prdOfferingName;
	}

	public String getPrdOfferingShortName() {
		return prdOfferingShortName;
	}

	public String getGlobalPrdOfferingNum() {
		return globalPrdOfferingNum;
	}

	public ProductTypeEntity getPrdType() {
		return prdType;
	}

	public ProductCategoryBO getPrdCategory() {
		return prdCategory;
	}

	public PrdStatusEntity getPrdStatus() {
		return prdStatus;
	}

	public PrdApplicableMasterEntity getPrdApplicableMaster() {
		return prdApplicableMaster;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public OfficeBO getOffice() {

		return office;
	}

	public String getDescription() {
		return description;
	}

	public void setPrdOfferingName(String prdOfferingName) {
		this.prdOfferingName = prdOfferingName;
	}

	public void setPrdOfferingShortName(String prdOfferingShortName) {
		this.prdOfferingShortName = prdOfferingShortName;
	}

	public void setPrdCategory(ProductCategoryBO prdCategory) {
		this.prdCategory = prdCategory;
	}

	public void setPrdStatus(PrdStatusEntity prdStatus) {
		this.prdStatus = prdStatus;
	}

	public void setPrdApplicableMaster(
			PrdApplicableMasterEntity prdApplicableMaster) {
		this.prdApplicableMaster = prdApplicableMaster;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setDescription(String description) {
		this.description = description;
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
		prdLogger.debug("Generation of new product Offering global number done"
				+ globalPrdOfferingNum);
		return globalPrdOfferingNum.toString();
	}

	private void vaildate(UserContext userContext, String prdOfferingName,
			String prdOfferingShortName, ProductCategoryBO prdCategory,
			PrdApplicableMasterEntity prdApplicableMaster, Date startDate,
			Date endDate) throws ProductDefinitionException {
		prdLogger.debug("Validating the fields in Prd Offering");
		if (userContext == null
				|| prdOfferingName == null
				|| prdOfferingShortName == null
				|| prdCategory == null
				|| prdApplicableMaster == null
				|| startDate == null
				|| (prdOfferingShortName.length() > 4)
				|| DateUtils.getDateWithoutTimeStamp(startDate.getTime())
						.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) < 0
				|| (endDate != null && DateUtils.getDateWithoutTimeStamp(
						startDate.getTime()).compareTo(
						DateUtils.getDateWithoutTimeStamp(endDate.getTime())) >= 0)) {
			throw new ProductDefinitionException("errors.create");
		}
		prdLogger.debug("Validation of the fields in Prd Offering done.");
	}

	private PrdStatusEntity getPrdStatus(Date startDate,
			ProductTypeEntity prdType) throws ProductDefinitionException {
		prdLogger
				.debug("getting the Product status for prdouct offering with start date :"
						+ startDate
						+ " and product Type :"
						+ prdType.getProductTypeID());
		PrdStatus prdStatus = null;
		if (startDate.compareTo(DateUtils.getCurrentDateWithoutTimeStamp()) == 0)
			prdStatus = getActivePrdStatus(prdType);
		else
			prdStatus = getInActivePrdStatus(prdType);
		try {
			prdLogger.debug("getting the Product status for product status :"
					+ prdStatus);
			return new PrdOfferingPersistence().getPrdStatus(prdStatus);
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
	}

	private PrdStatus getActivePrdStatus(ProductTypeEntity prdType) {
		prdLogger.debug("getting the Active Product status for product Type :"
				+ prdType.getProductTypeID());
		if (prdType.getProductTypeID().equals(ProductType.LOAN.getValue()))
			return PrdStatus.LOANACTIVE;
		else
			return PrdStatus.SAVINGSACTIVE;
	}

	private PrdStatus getInActivePrdStatus(ProductTypeEntity prdType) {
		prdLogger
				.debug("getting the In Active Product status for product Type :"
						+ prdType.getProductTypeID());
		if (prdType.getProductTypeID().equals(ProductType.LOAN.getValue()))
			return PrdStatus.LOANINACTIVE;
		else
			return PrdStatus.SAVINGSINACTIVE;
	}

	private void validateDuplicateProductOfferingName(String productOfferingName)
			throws ProductDefinitionException {
		prdLogger.debug("Checking for duplicate product offering name");
		try {
			if (!new PrdOfferingPersistence().getProductOfferingNameCount(
					productOfferingName).equals(Integer.valueOf("0")))
				throw new ProductDefinitionException(
						ProductDefinitionConstants.DUPLPRDINSTNAME);
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
	}

	private void validateDuplicateProductOfferingShortName(
			String productOfferingShortName) throws ProductDefinitionException {
		prdLogger.debug("Checking for duplicate product offering short name");
		try {
			if (!new PrdOfferingPersistence().getProductOfferingShortNameCount(
					productOfferingShortName).equals(Integer.valueOf("0")))
				throw new ProductDefinitionException(
						ProductDefinitionConstants.DUPLPRDINSTSHORTNAME);
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
	}
}
