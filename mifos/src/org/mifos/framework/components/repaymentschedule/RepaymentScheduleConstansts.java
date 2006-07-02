/**
 * RepaymentScheduleConstansts.java version:1.0
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

 package org.mifos.framework.components.repaymentschedule;


/**
 *
 *  This interface holds the constants required for repayment schedule
 */
public interface RepaymentScheduleConstansts
{

	public static final int FEE_TYPE_ONETIME = 2;
	public static final int FEE_TYPE_PERODIC = 1;

	public static final int GRACE_NONE = 1;
	public static final int GRACE_ALLREPAYMENTS = 2;
	public static final int GRACE_PRINCIPAL = 3;

	public static final int FEE_PAYMENT_FLAT = 0;
	public static final int FEE_PAYMENT_RATE = 1;

	public static final int FEE_FORMULA_LOAN = 1;
	public static final int FEE_FORMULA_LOAN_INTEREST = 2;
	public static final int FEE_FORMULA_INTEREST = 3;


	public static final int MEETING_LOAN = 0;
	public static final int MEETING_CUSTOMER = 1;

	public static final int RECCURENCE_WEEKLY = 1;
	public static final int RECCURENCE_MONTHLY = 2;
	
	public static final String NOT_SUPPORTED_EMI_GENERATION="errors.emitype_not_supported";
	public static final String INTERESTDEDUCTED_INVALIDGRACETYPE="errors.interestdeducted_invalidgrace";
	public static final String PRINCIPALLASTPAYMENT_INVALIDGRACETYPE="errors.principallast_invalidgrace";
	public static final String NOT_SUPPORTED_GRACE_TYPE="errors.not_supported_gracetype";
	public static final String INTERESTDEDUCTED_PRINCIPALLAST="errors.interestdedcuted_principallast";
	
	public static final String NOT_SUPPORTED_FEE_TYPE="errors.not_supported_feetype";
	public static final String NOT_SUPPORTED_FEE_PAYMENT="errors.not_supported_feepayment";
	public static final String NOT_SUPPORTED_FEE_FORMULA="errors.not_supported_feeformula";
	public static final String FEE_SCHEDULE="errors.feeschedule";
	
	public static final String NOT_SUPPORTED_MEETING_TYPE="errors.not_supported_meetingtype";
	
	public static final String REPAYMENTINPUTS_NOTSPECIFIED="errors.repaymentinputs_notspecified";
	
	public static final String NOT_SUPPORTED_FREQUENCY_TYPE="errors.not_supported_frequencytype";
	

	public static final String DATES_MISMATCH = "errors.datemismatch";
	
	public static final String NOT_VALID_DISBURSAL_DATE="errors.not_valid_disbursal_date";



}
