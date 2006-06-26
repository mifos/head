/**

 * FeeType.java    version: xxx

 

 * Copyright © 2005-2006 Grameen Foundation USA

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

package org.mifos.application.fees.util.valueobjects;

import org.mifos.application.master.util.valueobjects.LookUpEntity;
import org.mifos.framework.util.valueobjects.ValueObject;


public class FeeType extends ValueObject {
	
	public FeeType() {
	}

	
	private Short feeTypeId;

	
	private LookUpEntity lookUpEntity;

	
	private java.util.Set feePaymentsCategoriesTypeSet;

	
	private Short flatOrRate;

	
	private String formula;

	
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

	public java.util.Set getFeePaymentsCategoriesTypeSet() {
		return this.feePaymentsCategoriesTypeSet;
	}

	public void setFeePaymentsCategoriesTypeSet(
			java.util.Set feePaymentsCategoriesTypeSet) {
		this.feePaymentsCategoriesTypeSet = feePaymentsCategoriesTypeSet;
	}

}
