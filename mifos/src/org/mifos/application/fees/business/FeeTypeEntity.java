/**

 * FeeType.java    version: xxx

 

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

package org.mifos.application.fees.business;

import java.util.Set;

import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.framework.business.PersistentObject;

public class FeeTypeEntity extends PersistentObject {

	private Short feeTypeId;

	private LookUpEntity lookUpEntity;

	private Set<FeePaymentsCategoriesTypeEntity> feePaymentsCategoriesTypes;

	private Short flatOrRate;

	private String formula;

	public FeeTypeEntity() {
	}

	public Short getFeeTypeId() {
		return feeTypeId;
	}

	public void setFeeTypeId(Short feeTypeId) {

		this.feeTypeId = feeTypeId;
	}

	public LookUpEntity getLookUpEntity() {
		return this.lookUpEntity;
	}

	public void setLookUpEntity(LookUpEntity lookUpEntity) {
		this.lookUpEntity = lookUpEntity;
	}

	public Short getFlatOrRate() {
		return this.flatOrRate;
	}

	public void setFlatOrRate(Short flatOrRate) {
		this.flatOrRate = flatOrRate;
	}

	public String getFormula() {
		return this.formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public Set<FeePaymentsCategoriesTypeEntity> getFeePaymentsCategoriesTypes() {
		return feePaymentsCategoriesTypes;
	}

	private void setFeePaymentsCategoriesTypes(
			Set<FeePaymentsCategoriesTypeEntity> feePaymentsCategoriesTypes) {
		this.feePaymentsCategoriesTypes = feePaymentsCategoriesTypes;
	}

	public void addFeePaymentsCategoriesType(
			FeePaymentsCategoriesTypeEntity feePaymentsCategoriesType) {
		feePaymentsCategoriesType.setFeeType(this);
		feePaymentsCategoriesTypes.add(feePaymentsCategoriesType);
	}

}
