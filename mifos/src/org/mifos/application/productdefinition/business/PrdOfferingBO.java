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

import org.mifos.application.master.util.valueobjects.PrdApplicableMaster;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.security.util.UserContext;

public class PrdOfferingBO extends BusinessObject {

	private Short prdOfferingId;

	private String prdOfferingName;

	private String prdOfferingShortName;

	private String globalPrdOfferingNum;

	private ProductTypeEntity prdType;

	private ProductCategoryBO prdCategory;

	private PrdStatusEntity prdStatus;

	private PrdApplicableMaster prdApplicableMaster;

	private Date startDate;

	private Date endDate;

	private OfficeBO office;

	private String description;

	protected PrdOfferingBO() {

		office = new OfficeBO();
		prdCategory = new ProductCategoryBO();
		prdStatus = new PrdStatusEntity();
		prdApplicableMaster = new PrdApplicableMaster();
	}

	protected PrdOfferingBO(UserContext userContext) {
		super(userContext);
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

	public PrdApplicableMaster getPrdApplicableMaster() {
		return prdApplicableMaster;
	}

	public void setPrdApplicableMaster(PrdApplicableMaster prdApplicableMaster) {
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
}
