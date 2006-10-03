package org.mifos.application.personnel.business.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.login.util.helpers.LoginConstants;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.business.PersonnelBO;
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

	public void testSearch()throws Exception{
		personnel = createPersonnel();
		QueryResult queryResult=personnelBusinessService.search(personnel.getUserName(),Short.valueOf("1"),Short.valueOf("1")				);
		
		assertNotNull(queryResult);
		assertEquals(1,queryResult.getSize());
		assertEquals(1,queryResult.get(0,10).size());
	}
	private PersonnelBO createPersonnel() throws Exception {
		office = TestObjectFactory.getOffice(Short.valueOf("1"));
		Name name = new Name("XYZ", null, null, null);
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("9"), "123456",
				Short.valueOf("1")));
		Address address = new Address("abcd", "abcd", "abcd", "abcd", "abcd",
				"abcd", "abcd", "abcd");
		Date date = new Date();
		personnel = new PersonnelBO(PersonnelLevel.LOAN_OFFICER, office, Integer
				.valueOf("1"), Short.valueOf("1"), "ABCD", "XYZ",
				"xyz@yahoo.com", null, customFieldView, name, "111111", date,
				Integer.valueOf("1"), Integer.valueOf("1"), date, date,
				address, Short.valueOf("1"));
		personnel.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.closeSession();
		personnel = (PersonnelBO) HibernateUtil.getSessionTL().get(
				PersonnelBO.class, personnel.getPersonnelId());
		return personnel;
	}
	
	
}
