
/**
 * MifosServiceTest.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.0 May 05, 2006 (12:31:13 IST)
 */
package org.mifos.api;

import org.mifos.api.MifosServiceStub.FindLoanResponse;

/*
 *  MifosServiceTest Junit test case
 */

public class MifosServiceTest extends junit.framework.TestCase {

	/**
	 * Auto generated test method
	 */
	public void testfindLoan() throws java.lang.Exception {
		org.mifos.api.MifosServiceStub stub = new org.mifos.api.MifosServiceStub("http://localhost:8080/mifos/services/MifosService/findLoan");//the default implementation should point to the right endpoint

		org.mifos.api.MifosServiceStub.FindLoanRequest param6 = (org.mifos.api.MifosServiceStub.FindLoanRequest) getTestObject(org.mifos.api.MifosServiceStub.FindLoanRequest.class);
		param6.setId(new Integer("4"));
		FindLoanResponse flr = stub.findLoan(param6);
		assertNotNull(flr);
		System.out.println(flr.getLoan().getBorrowerName());
		System.out.println(flr.getLoan().getBalance());
		System.out.println(flr.getLoan().getBalanceCurrencyName());

	}

	public void testfindLoan2() throws java.lang.Exception {
		org.mifos.api.MifosServiceStub stub = new org.mifos.api.MifosServiceStub("http://localhost:8080/mifos/services/MifosService/findLoan");//the default implementation should point to the right endpoint

		org.mifos.api.MifosServiceStub.FindLoanRequest param6 = (org.mifos.api.MifosServiceStub.FindLoanRequest) getTestObject(org.mifos.api.MifosServiceStub.FindLoanRequest.class);
		param6.setId(new Integer("5"));
		FindLoanResponse flr = stub.findLoan(param6);
		assertNotNull(flr);
		System.out.println(flr.getLoan().getBorrowerName());
		System.out.println(flr.getLoan().getBalance());
		System.out.println(flr.getLoan().getBalanceCurrencyName());

	}
	//Create an ADBBean and provide it as the test object
	public org.apache.axis2.databinding.ADBBean getTestObject(
			java.lang.Class type) throws Exception {
		return (org.apache.axis2.databinding.ADBBean) type.newInstance();
	}

}
