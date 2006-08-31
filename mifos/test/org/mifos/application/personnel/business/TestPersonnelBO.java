package org.mifos.application.personnel.business;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.office.business.OfficeBO;
import org.mifos.application.personnel.exceptions.PersonnelException;
import org.mifos.application.personnel.util.helpers.PersonnelConstants;
import org.mifos.application.personnel.util.helpers.PersonnelLevel;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.business.util.Name;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestPersonnelBO extends MifosTestCase {

	UserContext userContext;

	OfficeBO office;

	Name name;

	@Override
	protected void setUp() throws Exception {
		userContext = TestObjectFactory.getUserContext();
		office = TestObjectFactory.getOffice(Short.valueOf("1"));
		name = new Name("mifos", null, null, null);
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		userContext = null;
		office = null;
		name = null;
		super.tearDown();
	}

	public void testCreateFailureWithNullName() {
		try {

			new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office,
					Integer.valueOf("1"), Short.valueOf("1"), "ABCD", null,
					null, null, null, name, null, null, null, null, null, null,
					null, userContext.getId());

			assertTrue(false);
		} catch (PersonnelException e) {
			assertEquals(PersonnelConstants.ERRORMANDATORY, e.getKey());
		}
	}

	public void testCreateFailureWithDuplicateName() {
		try {

			new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office,
					Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "mifos",
					null, null, null, name, null, null, null, null, null,
					null, null, userContext.getId());

			assertTrue(false);
		} catch (PersonnelException e) {
			assertEquals(PersonnelConstants.DUPLICATE_USER, e.getKey());
		}
	}

	public void testCreateFailureWithDuplicateGovernMentId() {
		try {
			new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office,
					Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "Raj",
					null, null, null, name, "123", null, null, null, null,
					null, null, userContext.getId());

			assertTrue(false);
		} catch (PersonnelException e) {
			assertEquals(PersonnelConstants.DUPLICATE_GOVT_ID, e.getKey());
		}
	}

	public void testCreateFailureWithDuplicateDisplayNameAndDOB()
			throws Exception {
		try {

			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER, office,
					Integer.valueOf("1"), Short.valueOf("1"), "ABCD", "RAJ",
					null, null, null, name, null, dateFormat
							.parse("1979-12-12"), null, null, null, null, null,userContext.getId());

			assertTrue(false);
		} catch (PersonnelException e) {
			assertEquals(PersonnelConstants.DUPLICATE_USER_NAME_OR_DOB, e
					.getKey());
		}
	}

	public void testCreateSucess() throws Exception {
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
		customFieldView.add(new CustomFieldView(Short.valueOf("1"), "123456",
				Short.valueOf("1")));
		 Address address = new Address("abcd","abcd","abcd","abcd","abcd","abcd","abcd","abcd");
		 Date date =new Date();
		PersonnelBO personnel = new PersonnelBO(PersonnelLevel.NON_LOAN_OFFICER,
				office, Integer.valueOf("1"), Short.valueOf("1"),
				"ABCD", "RAJ", "rajendersaini@yahoo.com", null,
				customFieldView, name, "111111", date, Integer
						.valueOf("1"), Integer.valueOf("1"), date, date, address, userContext.getId());
		personnel.save();
		HibernateUtil.closeandFlushSession();
		PersonnelBO personnelSaved=(PersonnelBO)HibernateUtil.getSessionTL().get(PersonnelBO.class,personnel.getPersonnelId());
		assertEquals("RAJ",personnelSaved.getUserName());
		assertEquals("rajendersaini@yahoo.com",personnelSaved.getEmailId());
		assertEquals("mifos",personnelSaved.getPersonnelDetails().getName().getFirstName());
		assertEquals(generateGlobalPersonnelNum(office.getGlobalOfficeNum(),personnel.getPersonnelId()),personnelSaved.getGlobalPersonnelNum());
		assertEquals(PersonnelLevel.NON_LOAN_OFFICER.getValue(),personnelSaved.getLevel().getId());
		assertEquals(1,personnelSaved.getLevel().getParent().getId().intValue());
		assertFalse(personnelSaved.getLevel().isInteractionFlag());
		assertEquals(null,personnelSaved.getMaxChildCount());
		assertEquals(office.getOfficeId(),personnelSaved.getOffice().getOfficeId());
		assertFalse(personnelSaved.isPasswordChanged());
		assertEquals(1,personnelSaved.getPreferredLocale().getLocaleId().intValue());
		assertEquals(1,personnelSaved.getTitle().intValue());
		assertEquals("mifos",personnelSaved.getDisplayName());
		assertFalse(personnelSaved.isLocked());
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		
		assertEquals(dateFormat.parse(dateFormat.format(date)),personnelSaved.getPersonnelDetails().getDob());
		assertEquals("mifos",personnelSaved.getPersonnelDetails().getDisplayName());
		assertEquals(personnel.getPersonnelId(),personnelSaved.getPersonnelDetails().getPersonnel().getPersonnelId());
		for (PersonnelCustomFieldEntity personnelCustomField : personnelSaved.getCustomFields()) {
			assertEquals("123456",personnelCustomField.getFieldValue());
			assertEquals(1,personnelCustomField.getFieldId().intValue());
		}
		
		HibernateUtil.getSessionTL().delete(personnelSaved);
	}
	private String generateGlobalPersonnelNum(String officeGlobalNum,
			int maxPersonnelId) {
		String userId = "";
		int numberOfZeros = 5 - String.valueOf(maxPersonnelId).length();
		for (int i = 0; i < numberOfZeros; i++) {
			userId = userId + "0";
		}
		userId = userId + maxPersonnelId;
		String userGlobalNum = officeGlobalNum + "-" + userId;
		
		return userGlobalNum;
	}
}
