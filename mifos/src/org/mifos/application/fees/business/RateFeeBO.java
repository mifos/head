/**

 * RateFeeBO.java    version: xxx



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

import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.fees.exceptions.FeeException;
import org.mifos.application.fees.util.helpers.FeeConstants;
import org.mifos.application.fees.util.helpers.RateAmountFlag;
import org.mifos.application.meeting.business.MeetingBO;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.struts.plugin.helper.EntityMasterConstants;

public class RateFeeBO extends FeeBO {

	private Double rate;

	private final FeeFormulaEntity feeFormula;

	/**
	 * Addding a default constructor is hibernate's requiremnt and should not be used to create a valid RateFee object.   
	 */
	protected RateFeeBO() {
		super();
		this.feeFormula = null;
	}

	/**
	 * Constructor to create one time Rate Fee.
	 * Fee Payment tells the time when fee should be charged. (upfront/time of disbursment etc.)
	 */
	public RateFeeBO(UserContext userContext, String feeName, CategoryTypeEntity categoryType, 
			FeeFrequencyTypeEntity feeFrequencyType, GLCodeEntity glCodeEntity,
			Double rate, FeeFormulaEntity feeFormula, boolean isCustomerDefaultFee, 
			FeePaymentEntity feePayment) throws FeeException{
		this(userContext, feeName, categoryType, feeFrequencyType, glCodeEntity, rate, feeFormula, 
				isCustomerDefaultFee, feePayment,null );
	}

	/**
	 * Constructor to create Periodic Rate Fee.
	 * Meeting tells the periodicity of fee. 
	 */
	public RateFeeBO(UserContext userContext, String feeName, CategoryTypeEntity categoryType,
			FeeFrequencyTypeEntity feeFrequencyType,
			GLCodeEntity glCodeEntity,	Double rate, 
			FeeFormulaEntity feeFormula, boolean isCustomerDefaultFee, 
			MeetingBO feeMeeting)throws FeeException {
		
		this(userContext, feeName, categoryType, feeFrequencyType, glCodeEntity, rate, feeFormula, 
				isCustomerDefaultFee, null, feeMeeting);
	}
	
	private RateFeeBO(UserContext userContext, String feeName,
			CategoryTypeEntity categoryType,FeeFrequencyTypeEntity feeFrequencyType,
			GLCodeEntity glCodeEntity, Double rate, FeeFormulaEntity feeFormula, boolean isCustomerDefaultFee,
			FeePaymentEntity feePayment,MeetingBO feeMeeting ) throws FeeException{
		super(userContext, feeName, categoryType, feeFrequencyType, glCodeEntity, isCustomerDefaultFee, 
				feePayment, feeMeeting);
		validateFields(rate, feeFormula);
		this.feeFormula = feeFormula;
		this.rate = rate;
		this.rateOrAmount = rate;
		this.rateAmountFlag = RateAmountFlag.RATE.getValue();
	}
	
	private void validateFields(Double rate, FeeFormulaEntity feeFormula)throws FeeException{
		if(rate==null || rate.doubleValue()<=0.0 || feeFormula==null)
			throw new FeeException(FeeConstants.INVALID_FEE_RATE_OR_FORMULA);
	}
	
	public FeeFormulaEntity getFeeFormula() {
		return feeFormula;
	}

	public Double getRate() {
		return rate;
	}

	public void setRate(Double rate) {
		this.rate = rate;
	}
	
	public RateAmountFlag getFeeType(){
		return RateAmountFlag.RATE;
	}

}
