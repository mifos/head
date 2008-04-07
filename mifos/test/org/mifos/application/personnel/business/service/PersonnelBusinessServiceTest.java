package org.mifos.application.personnel.business.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.master.business.CustomFieldType;
import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.exceptions.ServiceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.hibernate.helper.QueryResult;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class PersonnelBusinessServiceTest extends MifosTestCase {

	private OfficeBO office;

	private PersonnelBO personnel;

	private PersonnelBO personnelBO;

	private PersonnelBusinessService personnelBusinessService;

	@Override
	protected void setUp() throws Exception {
		personnelBusinessService = new PersonnelBusinessService();
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		TestObjectFactory.cleanUp(personnelBO);
		TestObjectFactory.cleanUp(personnel);
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testSuccessfullGetPersonnel() throws Exception {
		personnel = createPersonnel();
		String oldUserName = personnel.getUserName();
		personnel = personnelBusinessService.getPersonnel(personnel.getUserName());
		assertEquals(oldUserName,personnel.getUserName());
	}
	
	public void testFailureGetPersonnel() throws Exception {
		personnel = createPersonnel();
		try{
			personnel = personnelBusinessService.getPersonnel("WRONG_USERNAME");
		}catch(ServiceException pe) {
			assertTrue(true);
			assertEquals(LoginConstants.KEYINVALIDUSER, pe.getKey());
		}

	}
	
	public void testFailureGetOffice() throws Exception {
		TestObjectFactory.simulateInvalidConnection();
		try{
			personnelBusinessService.getOffice(TestObjectFactory.HEAD_OFFICE);
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			HibernateUtil.closeSession();
		}

	}
	public void testSearchFailureWithNoConn() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			personnelBusinessService.getPersonnel("WRONG_USERNAME");
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			HibernateUtil.closeSession();
		}

	}	
	public void testSearch()throws Exception{
		personnel = createPersonnel();
		QueryResult queryResult=personnelBusinessService.search(personnel.getUserName(),Short.valueOf("1"),Short.valueOf("1"));
		
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());
	}
	public void testSearchFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			personnelBusinessService.search("Raj",Short.valueOf("1"),Short.valueOf("1"));
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			HibernateUtil.closeSession();
		}

	}	
	public void  testGetActiveLoanOfficersUnderOffice()throws Exception{
		office = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		personnel = createPersonnel(office, PersonnelLevel.NON_LOAN_OFFICER);
		List<PersonnelBO> loanOfficers = personnelBusinessService.getActiveLoanOfficersUnderOffice(office.getOfficeId());
		assertNotNull(loanOfficers);
		assertEquals(1,loanOfficers.size());
		
	}

	public void testGetActiveLoanOfficersUnderOfficeFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			personnelBusinessService.getActiveLoanOfficersUnderOffice(Short.valueOf("3"));
			fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}
		finally {
			HibernateUtil.closeSession();
		}

	}	
	public void testGetRolesFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			personnelBusinessService.getRoles();
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			HibernateUtil.closeSession();
		}

	}	
	
	public void testGetPersonnelByGlobalPersonnelNumFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			personnelBusinessService.getPersonnelByGlobalPersonnelNum("12345678");
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			HibernateUtil.closeSession();
		}

	}	
	public void testGetPersonnelFailure() throws Exception{
		TestObjectFactory.simulateInvalidConnection();
		try{
			personnelBusinessService.getPersonnel("12345678");
		fail();
		}
		catch (ServiceException e) {
			assertTrue(true);
		}finally {
			HibernateUtil.closeSession();
		}

	}		
	
	public void  testGetActiveLoanOfficersInBranch()
			throws ServiceException {
		try {
			TestObjectFactory.simulateInvalidConnection();
			personnelBusinessService.getActiveLoanOfficersInBranch(	PersonnelLevel.LOAN_OFFICER.getValue(), Short.valueOf("1"),
					Short.valueOf("1"));
			fail();
		} catch (ServiceException e) {
			assertTrue(true);
			
		}
	}
	public void testGetSupportedLocale()throws Exception{
		//asserting only on not null as suppored locales can be added by user 
		assertNotNull(personnelBusinessService.getSupportedLocales());
	}
	private PersonnelBO createPersonnel(OfficeBO office,
			PersonnelLevel personnelLevel) throws Exception {
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				CustomFieldType.NUMERIC));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();
		personnel = new PersonnelBO(personnelLevel, office, Integer
				.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
				"xyz@yahoo.com", null, customFieldView, new Name("XYZ", null, null, null), "111111", date,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, PersonnelConstants.SYSTEM_USER);
		personnel.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		return personnel;
	}

	private PersonnelBO createPersonnel() throws Exception {
		office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		return createPersonnel(office,PersonnelLevel.LOAN_OFFICER);
	}
}
