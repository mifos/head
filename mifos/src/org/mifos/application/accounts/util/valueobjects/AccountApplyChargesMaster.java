/**

 * AccountApplyChargesMaster.java    version: 1.0

 

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
package org.mifos.application.accounts.util.valueobjects;

import org.mifos.framework.util.helpers.Money;
import org.mifos.framework.util.valueobjects.ValueObject;

/**
 * This class encpsulate the fee related information that we need to show on applycharges 
 * page 
 * @author rajenders
 *
 */
public class AccountApplyChargesMaster extends ValueObject{
	
	private Short feeId;
	private String feeName;

    private Double rateOrAmount;
    

    private Short rateFlatFalg;


    private Short formulaId;
    
    private String periodicity;

    private Short recurrenceTypeId;
    
    private Short paymentType;

	
	public Short getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(Short paymentType) {
		this.paymentType = paymentType;
	}
	public Short getRecurrenceTypeId() {
		return recurrenceTypeId;
	}
	public void setRecurrenceTypeId(Short recurrenceTypeId) {
		this.recurrenceTypeId = recurrenceTypeId;
	}
	public String getPeriodicity() {
		return periodicity;
	}
	public void setPeriodicity(String periodicity) {
		this.periodicity = periodicity;
	}
	
	public AccountApplyChargesMaster(Short feeId, String feeName, Double rateOrAmount, Short rateFlatFalg) {
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.rateFlatFalg = rateFlatFalg;
	}
	public AccountApplyChargesMaster() {
		
	}
	public AccountApplyChargesMaster(Short feeId, String feeName, Double rateOrAmount, Short rateFlatFalg, Short formulaId) {
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.rateFlatFalg = rateFlatFalg;
		this.formulaId = formulaId;
	}
	public Short getFeeId() {
		return feeId;
	}
	public void setFeeId(Short feeId) {
		this.feeId = feeId;
	}
	public String getFeeName() {
		return feeName;
	}
	public void setFeeName(String feeName) {
		this.feeName = feeName;
	}
	public Short getRateFlatFalg() {
		return rateFlatFalg;
	}
	public void setRateFlatFalg(Short rateFlatFalg) {
		this.rateFlatFalg = rateFlatFalg;
	}
	public Double getRateOrAmount() {
		return rateOrAmount;
	}
	public void setRateOrAmount(Double rateOrAmount) {
		this.rateOrAmount = rateOrAmount;
	}
	public Short getFormulaId() {
		return formulaId;
	}
	public void setFormulaId(Short formulaId) {
		this.formulaId = formulaId;
	}
	public AccountApplyChargesMaster(Short feeId, String feeName, Double rateOrAmount, Short rateFlatFalg, Short formulaId, String periodicity) {
		this.feeId = feeId;
		this.feeName = feeName;
		this.rateOrAmount = rateOrAmount;
		this.rateFlatFalg = rateFlatFalg;
		this.formulaId = formulaId;
		this.periodicity = periodicity;
	}

 

}
