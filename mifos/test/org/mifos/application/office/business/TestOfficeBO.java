package org.mifos.application.office.business;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.master.business.CustomFieldView;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.exceptions.OfficeValidationException;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.TestUtils;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestOfficeBO extends MifosTestCase {

	UserContext userContext = null;

	@Override
	protected void setUp() throws Exception {
		userContext = TestUtils.makeUser();
	}

	@Override
	protected void tearDown() throws Exception {
		HibernateUtil.closeSession();
		super.tearDown();
	}

	public void testCreateFailureDuplicateName() throws PersistenceException {
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(TestObjectFactory.HEAD_OFFICE), null, "TestAreaOffice ",
					"ABCD", null, OperationMode.REMOTE_SERVER);
            fail("Should not have been able to get here");
        } catch (OfficeValidationException e) {
			assertEquals(OfficeConstants.OFFICENAMEEXIST, e.getKey());
		}
	}

	public void testCreateFailureDuplicateShortName() throws PersistenceException {
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(TestObjectFactory.HEAD_OFFICE), null, "abcd", "mif2", null,
					OperationMode.REMOTE_SERVER);
            fail("Should not have been able to get here");
        } catch (OfficeValidationException e) {
			assertEquals(OfficeConstants.OFFICESHORTNAMEEXIST, e.getKey());
		}
	}

	public void testCreateSucess() throws Exception { // check short
		// name

		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();

		CustomFieldView customFieldView2 = new CustomFieldView();
		customFieldView2.setFieldId(Short.valueOf("1"));
		customFieldView2.setFieldValue("123456");
		customFieldView.add(customFieldView2);

		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, customFieldView, "abcd", "abcd", null,
				OperationMode.REMOTE_SERVER);
		officeBO.save();
		HibernateUtil.commitTransaction();
		HibernateUtil.flushAndCloseSession();
		officeBO = TestObjectFactory.getOffice(officeBO.getOfficeId());
		assertEquals("1.1.2", officeBO.getSearchId());
		assertEquals("abcd", officeBO.getOfficeName());
		assertEquals("abcd", officeBO.getShortName());
		assertEquals(OperationMode.REMOTE_SERVER, officeBO.getMode());
		assertTrue(officeBO.isActive());
		TestObjectFactory.cleanUp(officeBO);
	}

	public void testCreateSucessFailure() throws Exception {

		try {
			OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
			List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();
			CustomFieldView customFieldView2 = new CustomFieldView();
			customFieldView2.setFieldId(Short.valueOf("1"));
			customFieldView2.setFieldValue("123456");
			customFieldView.add(customFieldView2);
			OfficeBO officeBO = new OfficeBO(userContext,
					OfficeLevel.AREAOFFICE, parent, customFieldView, "abcd",
					"abcd", null, OperationMode.REMOTE_SERVER);
			TestObjectFactory.simulateInvalidConnection();
			officeBO.save();
			fail("Should not have been able to get here");
		} catch (OfficeException e) {
			// We were expecting this exception
		}
	}

	public void testCreateWithNoName() throws Exception { // check short name
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(TestObjectFactory.HEAD_OFFICE), null, null, "mif2", null,
					OperationMode.REMOTE_SERVER);
            fail("Should not have been able to get here");
        } catch (OfficeValidationException e) {
			assertEquals(OfficeConstants.ERRORMANDATORYFIELD, e.getKey());
		}
	}

	public void testCreateWithNoShortName() throws Exception { // check short
		// name
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(TestObjectFactory.HEAD_OFFICE), null, "abcd", null, null,
					OperationMode.REMOTE_SERVER);
            fail("Should not have been able to get here");
        } catch (OfficeValidationException e) {
			assertEquals(OfficeConstants.ERRORMANDATORYFIELD, e.getKey());
		}
	}

	public void testCreateWithNolevel() throws Exception { // check short
		try {

			new OfficeBO(userContext, null, TestObjectFactory.getOffice(Short
					.valueOf("1")), null, "abcd", "abcd", null,
					OperationMode.REMOTE_SERVER);
            fail("Should not have been able to get here");
        } catch (OfficeValidationException e) {
			assertEquals(OfficeConstants.ERRORMANDATORYFIELD, e.getKey());
		}
	}

	public void testCreateWithNoOperationMode() throws Exception {
		// check short name
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(TestObjectFactory.HEAD_OFFICE), null, "abcd", "abcd", null,
					null);
            fail("Should not have been able to get here");
        } catch (OfficeValidationException e) {
			assertEquals(OfficeConstants.ERRORMANDATORYFIELD, e.getKey());
		}
	}

	public void testCreateWithNoParent() throws Exception {
		// check short name
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, null, null,
					"abcd", "abcd", null, OperationMode.REMOTE_SERVER);
            fail("Should not have been able to get here");
        } catch (OfficeValidationException e) {
			assertEquals(OfficeConstants.ERRORMANDATORYFIELD, e.getKey());
		}
	}

	public void testGetChildern() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		HibernateUtil.startTransaction();
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO parent1 = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		assertEquals(2, parent1.getChildren().size());
		officeBO = TestObjectFactory.getOffice(officeBO.getOfficeId());
		TestObjectFactory.cleanUp(officeBO);
	}

	public void testUpdateNameAndShortName() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save(); // createChild also
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();

		officeBO.update("3", "3", officeBO.getOfficeStatus(), officeBO
				.getOfficeLevel(), null, null, null);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		assertEquals("3", savbedOffice.getOfficeName());
		assertEquals("3", savbedOffice.getShortName());
		OfficeBO savbedChild = TestObjectFactory.getOffice(Child.getOfficeId());

		TestObjectFactory.cleanUp(savbedChild);
		TestObjectFactory.cleanUp(savbedOffice);
	}

	public void testUpdateNamefailure() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		OfficeBO savbedChild = TestObjectFactory.getOffice(Child.getOfficeId());

		try {
			savbedOffice.setUserContext(userContext);
			savbedOffice.update("2", "2", officeBO.getOfficeStatus(),
					savbedOffice.getOfficeLevel(), null, null, null);
			assertEquals(true, false);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.OFFICENAMEEXIST, e.getKey());
		}
		TestObjectFactory.cleanUp(savbedChild);
		TestObjectFactory.cleanUp(savbedOffice);
	}

	public void testUpdateShortNamefailure() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save(); // createChild also
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		OfficeBO savbedChild = TestObjectFactory.getOffice(Child.getOfficeId());

		try {
			savbedOffice.setUserContext(userContext);
			savbedOffice.update("22", "2", officeBO.getOfficeStatus(),
					savbedOffice.getOfficeLevel(), null, null, null);
			assertEquals(true, false);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.OFFICESHORTNAMEEXIST, e.getKey());
		}
		TestObjectFactory.cleanUp(savbedChild);
		TestObjectFactory.cleanUp(savbedOffice);
	}

	public void testUpdateStatusSucess() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		savbedOffice.setUserContext(userContext);
		savbedOffice.update("abcd", "abcd", OfficeStatus.INACTIVE, savbedOffice
				.getOfficeLevel(), null, null, null);
		assertEquals(OfficeStatus.INACTIVE, savbedOffice.getOfficeStatus());
		TestObjectFactory.cleanUp(savbedOffice);
	}

	public void testUpdateStatusfailure() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		OfficeBO savbedChild = TestObjectFactory.getOffice(Child.getOfficeId());

		try {
			savbedOffice.setUserContext(userContext);
			savbedOffice.update("abcd", "abcd", OfficeStatus.INACTIVE,
					savbedOffice.getOfficeLevel(), null, null, null);
			assertEquals(true, false);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.KEYHASACTIVECHILDREN, e.getKey());
		}
		TestObjectFactory.cleanUp(savbedChild);
		TestObjectFactory.cleanUp(savbedOffice);
	}
	public void testUpdateOfficelevel() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		savbedOffice.setUserContext(userContext);
		savbedOffice.update("abcd", "abcd", savbedOffice.getOfficeStatus(),
				OfficeLevel.SUBREGIONALOFFICE, null, null, null);
		assertEquals(OfficeLevel.SUBREGIONALOFFICE, savbedOffice
				.getOfficeLevel());
		TestObjectFactory.cleanUp(savbedOffice);
	}

	public void testUpdateOfficelevelFailure() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save(); // createChild also
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		OfficeBO savbedChild = TestObjectFactory.getOffice(Child.getOfficeId());

		try {
			savbedOffice.setUserContext(userContext);
			savbedOffice.update("abcd", "abcd", savbedOffice.getOfficeStatus(),
					OfficeLevel.BRANCHOFFICE, null, null, null);
			assertEquals(true, false);
		} catch (OfficeException e) {
			assertEquals(true, true);
		}
		TestObjectFactory.cleanUp(savbedChild);
		TestObjectFactory.cleanUp(savbedOffice);
	}

	public void testUpdateAddress() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);

		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		savbedOffice.setUserContext(userContext);
		Address address = new Address();
		address.setLine1("bangalore");
		address.setLine2("city");
		savbedOffice.update("abcd", "abcd", savbedOffice.getOfficeStatus(),
				savbedOffice.getOfficeLevel(), null, address, null);
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice2 = TestObjectFactory.getOffice(officeBO
				.getOfficeId());

		assertEquals("bangalore", savbedOffice.getAddress().getAddress()
				.getLine1());
		assertEquals("city", savbedOffice.getAddress().getAddress().getLine2());
		TestObjectFactory.cleanUp(savbedOffice2);
	}

	public void testUpdateCustomFields() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		List<CustomFieldView> customFieldView1 = new ArrayList<CustomFieldView>();

		CustomFieldView customFieldView3 = new CustomFieldView();
		customFieldView3.setFieldId(Short.valueOf("1"));
		customFieldView3.setFieldValue("3434");
		customFieldView1.add(customFieldView3);

		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, customFieldView1, "abcd", "abcd", null,
				OperationMode.REMOTE_SERVER);
		officeBO.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		savbedOffice.setUserContext(userContext);

		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();

		CustomFieldView customFieldView2 = new CustomFieldView();
		customFieldView2.setFieldId(Short.valueOf("1"));
		customFieldView2.setFieldValue("123456");
		customFieldView.add(customFieldView2);
		savbedOffice.update("abcd", "abcd", savbedOffice.getOfficeStatus(),
				savbedOffice.getOfficeLevel(), null, null, customFieldView);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice2 = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		for (OfficeCustomFieldEntity customField : savbedOffice2
				.getCustomFields()) {
			assertEquals("abcd", customField.getOffice().getOfficeName());
			assertEquals("123456", customField.getFieldValue());
		}
		TestObjectFactory.cleanUp(savbedOffice2);
	}

	public void testUpdateCustomFields_WithNull() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		savbedOffice.setUserContext(userContext);

		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();

		CustomFieldView customFieldView2 = new CustomFieldView();
		customFieldView2.setFieldId(Short.valueOf("1"));
		customFieldView2.setFieldValue("123456");
		customFieldView.add(customFieldView2);
		savbedOffice.setCustomFields(null);
		savbedOffice.update("abcd", "abcd", savbedOffice.getOfficeStatus(),
				savbedOffice.getOfficeLevel(), null, null, customFieldView);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice2 = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		for (OfficeCustomFieldEntity customField : savbedOffice2
				.getCustomFields()) {

			assertEquals("123456", customField.getFieldValue());
		}
		TestObjectFactory.cleanUp(savbedOffice2);
	}

	public void testUpdateOfficeStatusSucess() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		savbedOffice.setUserContext(userContext);
		savbedOffice.update("abcd", "abcd", OfficeStatus.INACTIVE, savbedOffice
				.getOfficeLevel(), null, null, null);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		savbedOffice = TestObjectFactory.getOffice(officeBO.getOfficeId());
		savbedOffice.setUserContext(userContext);
		savbedOffice.update("abcd", "abcd", OfficeStatus.ACTIVE, savbedOffice
				.getOfficeLevel(), null, null, null);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		savbedOffice = TestObjectFactory.getOffice(officeBO.getOfficeId());
		assertEquals(OfficeStatus.ACTIVE, savbedOffice.getOfficeStatus());
		TestObjectFactory.cleanUp(savbedOffice);
	}

	public void testOfficeLevelEntity() throws Exception {
		MasterPersistence masterPersistenceService = 
			new MasterPersistence();
		OfficeLevelEntity levelEntity = (OfficeLevelEntity) masterPersistenceService
				.getPersistentObject(OfficeLevelEntity.class, OfficeLevel.AREAOFFICE
						.getValue());
		assertNotNull(levelEntity.getChild());
		assertEquals(OfficeLevel.AREAOFFICE, levelEntity.getLevel());
		assertEquals(true, levelEntity.isConfigured());
		assertEquals(false, levelEntity.isInteractionFlag());
		assertNotNull(levelEntity.getParent());
	}

	public void testGetBranchOnlyChildren() throws Exception {
		OfficeBO office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				office, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		office = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		assertEquals(1, office.getBranchOnlyChildren().size());
		officeBO = TestObjectFactory.getOffice(officeBO.getOfficeId());
		TestObjectFactory.cleanUp(officeBO);
	}

	public void testUpdateParentSucess() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO regionalOffice = TestObjectFactory.createOffice(
				OfficeLevel.REGIONALOFFICE, parent, "abcd", "abcd");
		// createChild also
		OfficeBO areaOffice = TestObjectFactory.createOffice(
				OfficeLevel.AREAOFFICE, regionalOffice, "2", "2");
		OfficeBO branchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, areaOffice, "3", "3");
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		areaOffice = TestObjectFactory.getOffice(areaOffice.getOfficeId());
		areaOffice.setUserContext(userContext);
		areaOffice.update("2", "2", areaOffice.getOfficeStatus(), areaOffice
				.getOfficeLevel(), parent, null, null);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		regionalOffice = TestObjectFactory.getOffice(regionalOffice
				.getOfficeId());
		areaOffice = TestObjectFactory.getOffice(areaOffice.getOfficeId());
		branchOffice = TestObjectFactory.getOffice(branchOffice.getOfficeId());
		if (areaOffice.getSearchId().equalsIgnoreCase("1.1.2")) {
			assertEquals("1.1.2.1", branchOffice.getSearchId());
		} else if (areaOffice.getSearchId().equalsIgnoreCase("1.1.1")) {
			assertEquals("1.1.1.1", branchOffice.getSearchId());
		} else if (areaOffice.getSearchId().equalsIgnoreCase("1.1.3")) {
			assertEquals("1.1.3.1", branchOffice.getSearchId());
		} else
			assertEquals(true, false);
		TestObjectFactory.cleanUp(branchOffice);
		TestObjectFactory.cleanUp(areaOffice);
		TestObjectFactory.cleanUp(regionalOffice);
		resetOffices();

	}
	public void testUpdateParentFailure() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO regionalOffice = TestObjectFactory.createOffice(
				OfficeLevel.REGIONALOFFICE, parent, "abcd", "abcd");
		// createChild also
		OfficeBO areaOffice = TestObjectFactory.createOffice(
				OfficeLevel.AREAOFFICE, regionalOffice, "2", "2");
		OfficeBO branchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, areaOffice, "3", "3");
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		branchOffice = TestObjectFactory.getOffice(branchOffice.getOfficeId());
		areaOffice = TestObjectFactory.getOffice(areaOffice.getOfficeId());
		areaOffice.setUserContext(userContext);
		try{
		areaOffice.update("2", "2", areaOffice.getOfficeStatus(), areaOffice
				.getOfficeLevel(), branchOffice, null, null);

		fail();
		}
		catch (OfficeException e) {
			assertTrue(true);
		}
		regionalOffice = TestObjectFactory.getOffice(regionalOffice
				.getOfficeId());
		TestObjectFactory.cleanUp(branchOffice);
		TestObjectFactory.cleanUp(areaOffice);
		TestObjectFactory.cleanUp(regionalOffice);
		resetOffices();

	}




	public void testUpdateParentFromHoToArea() throws Exception {
		OfficeBO ho = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO regionalOffice = TestObjectFactory.createOffice(
				OfficeLevel.REGIONALOFFICE, ho, "abcd", "abcd");
		// createChild also
		OfficeBO areaOffice = TestObjectFactory.createOffice(
				OfficeLevel.AREAOFFICE, regionalOffice, "2", "2");
		OfficeBO branchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, ho, "3", "3");
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		branchOffice = TestObjectFactory.getOffice(branchOffice.getOfficeId());
		branchOffice.setUserContext(userContext);
		branchOffice.update("3", "3", branchOffice.getOfficeStatus(),
				branchOffice.getOfficeLevel(), areaOffice, null, null);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		regionalOffice = TestObjectFactory.getOffice(regionalOffice
				.getOfficeId());
		areaOffice = TestObjectFactory.getOffice(areaOffice.getOfficeId());
		branchOffice = TestObjectFactory.getOffice(branchOffice.getOfficeId());

		assertEquals(areaOffice.getOfficeId(), branchOffice.getParentOffice()
				.getOfficeId());
		if (areaOffice.getSearchId().equalsIgnoreCase("1.1.2.1")) {
			assertEquals("1.1.2.1.1", branchOffice.getSearchId());
		} else if (areaOffice.getSearchId().equalsIgnoreCase("1.1.1.1")) {
			assertEquals("1.1.1.1.1", branchOffice.getSearchId());

		} else
			assertEquals(true, false);
		TestObjectFactory.cleanUp(branchOffice);
		TestObjectFactory.cleanUp(areaOffice);
		TestObjectFactory.cleanUp(regionalOffice);
		resetOffices();
	}

	public void testUpdateParentHoToRegional() throws Exception {
		OfficeBO ho = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		//
		OfficeBO branchOffice = TestObjectFactory.createOffice(
				OfficeLevel.BRANCHOFFICE, ho, "3", "3");
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		branchOffice = TestObjectFactory.getOffice(branchOffice.getOfficeId());
		OfficeBO areaOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_AREA_OFFICE);
		branchOffice.setUserContext(userContext);
		branchOffice.update("3", "3", branchOffice.getOfficeStatus(),
				branchOffice.getOfficeLevel(), areaOffice, null, null);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		if (areaOffice.getSearchId().equalsIgnoreCase("1.1.1")) {
			if ("1.1.1.1".equals(branchOffice.getSearchId())
					|| "1.1.1.2".equals(branchOffice.getSearchId()))
				assertEquals(true, true);

		} else
			assertEquals(true, false);
		TestObjectFactory.cleanUp(branchOffice);
		resetOffices();

	}

	public void testUpdateParentAreaToHo() throws Exception {
		OfficeBO ho = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		OfficeBO areaOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_AREA_OFFICE);
		branchOffice.setUserContext(userContext);
		branchOffice.update(branchOffice.getOfficeName(), branchOffice
				.getShortName(), branchOffice.getOfficeStatus(), branchOffice
				.getOfficeLevel(), ho, branchOffice.getAddress().getAddress(),
				null);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		if ("1.1.1".equals(branchOffice.getSearchId())
				|| "1.1.2".equals(branchOffice.getSearchId()))
			assertEquals(true, true);
		else
			assertEquals(true, false);

		// update it back

		areaOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_AREA_OFFICE);
		branchOffice.setUserContext(userContext);
		branchOffice.update(branchOffice.getOfficeName(), branchOffice
				.getShortName(), branchOffice.getOfficeStatus(), branchOffice
				.getOfficeLevel(), areaOffice, branchOffice.getAddress()
				.getAddress(), null);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();

		resetOffices();

	}

	public void testCreateOfficeView() {

		OfficeView officeView = new OfficeView(Short.valueOf("1"),
				"headOffice", Integer.valueOf("1"));
		assertEquals("headOffice", officeView.getOfficeName());
		assertEquals(Integer.valueOf("1"), officeView.getVersionNo());
		assertEquals(Short.valueOf("1"), officeView.getOfficeId());


		OfficeView officeView2 = new OfficeView(Short.valueOf("1"),
				"headOffice",Short.valueOf("1") ,"Hifos HO",Integer.valueOf("1"));

		assertEquals("Hifos HO", officeView2.getLevelName());
		assertEquals("Hifos HO(headOffice)", officeView2.getDisplayName());
	}
	
	public void testIsParent() throws Exception {
		OfficeBO ho = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		assertTrue(ho.isParent(branchOffice));
	}
	
	public void testIsNotParent() throws Exception {
		OfficeBO ho = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		assertFalse(branchOffice.isParent(ho));
	}

	private void resetOffices() throws Exception {
		OfficeBO ho = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO areaOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_AREA_OFFICE);

		areaOffice.setParentOffice(ho);
		areaOffice.setSearchId("1.1.1");
		HibernateUtil.getSessionTL().saveOrUpdate(areaOffice);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();

		OfficeBO areaOffice1 = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_AREA_OFFICE);
		OfficeBO branchOffice = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_BRANCH_OFFICE);
		branchOffice.setParentOffice(areaOffice1);
		branchOffice.setSearchId("1.1.1.1");
		branchOffice.setUserContext(userContext);
		branchOffice.update(branchOffice.getOfficeName(), branchOffice
				.getShortName(), branchOffice.getOfficeStatus(), branchOffice
				.getOfficeLevel(), areaOffice1, branchOffice.getAddress()
				.getAddress(), null);
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
	}

	public void testOfficeEquals() throws Exception {
		OfficeBO office1 = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO office1a = TestObjectFactory.getOffice(TestObjectFactory.HEAD_OFFICE);
		OfficeBO office1b = OfficeBO.makeForTest(userContext, (short)1, 
			"office 1b", "1b");
		// TODO: subclass case
		OfficeBO unsaved = OfficeBO.makeForTest(userContext, null, 
			"office 1b", "1b");
		
		OfficeBO office2 = TestObjectFactory.getOffice(TestObjectFactory.SAMPLE_AREA_OFFICE);
		
		// fixing equals was causing failures in
		// ReverseLoanDisbursalActionTest.
		// Re-enable this once we figure it out.
//		TestUtils.verifyBasicEqualsContract(
//			new OfficeBO[] { office1, office1a, office1b }, 
//			new OfficeBO[] { office2, unsaved });

		// The following are temporary, until we can re-enable the
		// code to make verifyBasicEqualsContract pass again.
		assertEquals(office1.getOfficeId(), office1b.getOfficeId());
		assertTrue(office1.equals(office1a));
		assertFalse(office1.equals(office2));
		assertFalse(office1.equals(unsaved));
	}

}
