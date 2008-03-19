package org.mifos.framework.components.batchjobs.helpers;

import org.mifos.application.customer.persistence.CustomerPersistence;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.office.business.service.OfficeBusinessService;
import org.mifos.framework.MifosTestCase;

/** Has hardcoded tests against the Mifos FTP DB of jan21 
 * created using db-20080121.sql. 
 * 1) Make sure to run batch job before running this suite. 
 * 2) Set name of DB properly in HibernateTest.properties
 */
//TODO: Remove this before submitting a patch
public class TestBranchReportAgainstMifosFtpDbValues extends MifosTestCase{

	private static final String OFFICE_ID = "90";

	public void testGetActiveBorrowersCountForOffice() throws Exception {
		OfficeBO office = new OfficeBusinessService().getOffice(Short.valueOf(OFFICE_ID));
		Integer activeBorrowersCount = new CustomerPersistence().getActiveBorrowersCountForOffice(office);
		assertEquals(new Integer(1), activeBorrowersCount);
	}
	
	public void testGetVeryPoorActiveBorrowersCountForOffice() throws Exception {
		OfficeBO office = new OfficeBusinessService().getOffice(Short.valueOf(OFFICE_ID));
		Integer activeBorrowersCount = new CustomerPersistence().getVeryPoorActiveBorrowersCountForOffice(office);
		assertEquals(new Integer(0), activeBorrowersCount);
	}
	
	public void testGetActiveSaversCountForOffice() throws Exception {
		OfficeBO office = new OfficeBusinessService().getOffice(Short.valueOf(OFFICE_ID));
		Integer activeSaversCount = new CustomerPersistence().getActiveSaversCountForOffice(office);
		assertEquals(new Integer(6), activeSaversCount);		
	}
	
	public void testGetVeryPoorActiveSaversCountForOffice() throws Exception {
		OfficeBO office = new OfficeBusinessService().getOffice(Short.valueOf(OFFICE_ID));
		Integer veryPoorActiveSaversCount = new CustomerPersistence().getVeryPoorActiveSaversCountForOffice(office);
		assertEquals(new Integer(1), veryPoorActiveSaversCount);
	}
	
	public void testGetDropoutClientsCountForOffice() throws Exception {
		OfficeBO office = new OfficeBusinessService().getOffice(Short.valueOf(OFFICE_ID));
		Integer dropOutClientsCount = new CustomerPersistence().getDropOutClientsCountForOffice(office);
		assertEquals(new Integer(2), dropOutClientsCount);				
	}
	
	public void testGetVeryPoorDropoutClientsCountForOffice() throws Exception {
		OfficeBO office = new OfficeBusinessService().getOffice(Short.valueOf(OFFICE_ID));
		Integer veryPoorDropOutClientsCount = new CustomerPersistence().getVeryPoorDropOutClientsCountForOffice(office);
		assertEquals(new Integer(1), veryPoorDropOutClientsCount);				
	}	
	

	public void testGetOnHoldClientsCountForOffice() throws Exception {
		OfficeBO office = new OfficeBusinessService().getOffice(Short.valueOf(OFFICE_ID));
		Integer onHoldClientsCount = new CustomerPersistence().getOnHoldClientsCountForOffice(office);
		assertEquals(new Integer(0), onHoldClientsCount);		
	}
	
	public void testGetVeryPoorOnHoldClientsCountForOffice() throws Exception {
		OfficeBO office = new OfficeBusinessService().getOffice(Short.valueOf(OFFICE_ID));
		Integer veryPoorOnHoldClientsCount = new CustomerPersistence().getVeryPoorOnHoldClientsCountForOffice(office);
		assertEquals(new Integer(0), veryPoorOnHoldClientsCount);		
	}	
	
	public void testGetReplacementsCountForOffice() throws Exception {
		OfficeBO office = new OfficeBusinessService().getOffice(Short.valueOf(OFFICE_ID));
		Integer replacementsCount = new CustomerPersistence().getCustomerReplacementsCountForOffice(office, Short.valueOf("3"), "1");
		assertEquals(new Integer(0), replacementsCount);
	}
	
	public void testGetVeryPoorReplacementsCountForOffice() throws Exception {
		OfficeBO office = new OfficeBusinessService().getOffice(Short.valueOf(OFFICE_ID));
		Integer veryPoorReplacementsCount = new CustomerPersistence().getVeryPoorReplacementsCountForOffice(office, Short.valueOf("3"), "1");
		assertEquals(new Integer(0), veryPoorReplacementsCount);
	}	
}
