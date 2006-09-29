/**
 * MifosServiceSkeleton.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.0 May 05, 2006 (12:31:13 IST)
 */
package org.mifos.api;

import org.mifos.www.services.FindLoanResponse;
import org.mifos.www.services.Loan;
import org.apache.log4j.Logger;

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
