/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
package org.mifos.application.productdefinition.business;

import java.util.Set;

import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.productdefinition.exceptions.ProductDefinitionException;
import org.mifos.application.productdefinition.persistence.LoanPrdPersistence;
import org.mifos.application.productdefinition.util.helpers.ProductType;
import org.mifos.framework.business.BusinessObject;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.application.master.MessageLookup;

public class ProductTypeEntity extends BusinessObject {

	private Short productTypeID;

	private LookUpValueEntity lookUpValue;

	private Short latenessDays;

	private Short dormancyDays;

	public ProductTypeEntity(Short prdTypeId) {
		super();
		this.productTypeID = prdTypeId;
	}

	protected ProductTypeEntity() {
		super();
	}

	public LookUpValueEntity getLookUpValue() {
		return lookUpValue;
	}

	public void setLookUpValue(LookUpValueEntity lookUpValue) {
		this.lookUpValue = lookUpValue;
	}

	public Short getProductTypeID() {
		return productTypeID;
	}
	
	public ProductType getType() {
		return ProductType.getProductType(productTypeID);
	}

	public void setProductTypeID(Short productTypeID) {
		this.productTypeID = productTypeID;
	}

	public Short getDormancyDays() {
		return dormancyDays;
	}

	 void setDormancyDays(Short dormancyDays) {
		this.dormancyDays = dormancyDays;
	}

	public Short getLatenessDays() {
		return latenessDays;
	}

	void setLatenessDays(Short latenessDays) {
		this.latenessDays = latenessDays;
	}

	public String getName() {
		String lookupKey = lookUpValue.getLookUpName();
		return MessageLookup.getInstance().lookup(lookupKey);
	}

	public void update(Short latenessDormancy)
			throws ProductDefinitionException {

		if (productTypeID.equals(ProductType.LOAN.getValue()))
			this.latenessDays = latenessDormancy;
		else
			this.dormancyDays = latenessDormancy;
		try {
			new LoanPrdPersistence().createOrUpdate(this);
		} catch (PersistenceException e) {
			throw new ProductDefinitionException(e);
		}
	}
}
