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
 
package org.mifos.api;

import org.apache.log4j.Logger;
import org.mifos.www.services.FindLoanResponse;

/**
 * MifosServiceSkeleton java skeleton for the axisService
 */
public class MifosServiceSkeleton implements MifosServiceSkeletonInterface {
	Logger logger = Logger.getLogger(this.getClass());
	private static MifosService msvc = null;

	/**
	 * Auto generated method signature
	 * 
	 * @param param2
	 * 
	 */
	public org.mifos.www.services.FindLoanResponse findLoan(
			org.mifos.www.services.FindLoanRequest param2)

	{
		FindLoanResponse flrsp = new FindLoanResponse();
		
		msvc = new MifosService();
		
		Integer id = param2.getId();
		
		try {
			org.mifos.api.Loan loan = msvc.getLoan(id);
			
			org.mifos.www.services.Loan l = new org.mifos.www.services.Loan();
			
			l.setBalance(loan.getLoanBalance());
			l.setId(loan.getId());
			l.setBorrowerName(loan.getBorrowerName());
			l.setBalanceCurrencyName(loan.getLoanCurrency());
			
			flrsp.setLoan(l);
		} catch (Exception e) {
			logger.error("Exception finding loan: " + e.getStackTrace());
		}
		
		return flrsp;
	}
}
