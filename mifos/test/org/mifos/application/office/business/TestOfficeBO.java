package org.mifos.application.office.business;

import java.util.ArrayList;
import java.util.List;

import org.mifos.application.customer.business.CustomFieldView;
import org.mifos.application.master.persistence.service.MasterPersistenceService;
import org.mifos.application.office.exceptions.OfficeException;
import org.mifos.application.office.util.helpers.OfficeLevel;
import org.mifos.application.office.util.helpers.OfficeStatus;
import org.mifos.application.office.util.helpers.OperationMode;
import org.mifos.application.office.util.resources.OfficeConstants;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.business.service.ServiceFactory;
import org.mifos.framework.business.util.Address;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.util.UserContext;
import org.mifos.framework.util.helpers.PersistenceServiceName;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestOfficeBO extends MifosTestCase {

	UserContext userContext = null;

	@Override
	protected void setUp() throws Exception {

		userContext = TestObjectFactory.getUserContext();
	}

	public void testCreateFailureDuplicateName() throws Exception {
		// check officeName
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(Short.valueOf("1")), null, "TestAreaOffice ",
					"ABCD", null, OperationMode.REMOTE_SERVER);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.OFFICENAMEEXIST, e.getKey());

		}

	}

	public void testCreateFailureDuplicateShortName() throws Exception {
		// check short name
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(Short.valueOf("1")), null, "abcd", "mif2", null,
					OperationMode.REMOTE_SERVER);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.OFFICESHORTNAMEEXIST, e.getKey());

		}

	}

	public void testCreateSucess() throws Exception {
		// check short name

		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		List<CustomFieldView> customFieldView = new ArrayList<CustomFieldView>();

		CustomFieldView customFieldView2 = new CustomFieldView();
		customFieldView2.setFieldId(Short.valueOf("1"));
		customFieldView2.setFieldValue("123456");
		customFieldView.add(customFieldView2);

		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, customFieldView, "abcd", "abcd", null,
				OperationMode.REMOTE_SERVER);
		officeBO.save();
		HibernateUtil.closeandFlushSession();
		officeBO = TestObjectFactory.getOffice(officeBO.getOfficeId());
		assertEquals("1.1.2", officeBO.getSearchId());
		assertEquals("abcd", officeBO.getOfficeName());
		assertEquals("abcd", officeBO.getShortName());
		assertEquals(OperationMode.REMOTE_SERVER, officeBO.getMode());
		TestObjectFactory.cleanUp(officeBO);
	}

	public void testCreateWithNoName() throws Exception {
		// check short name
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(Short.valueOf("1")), null, null, "mif2", null,
					OperationMode.REMOTE_SERVER);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.ERRORMANDATORYFIELD, e.getKey());

		}

	}

	public void testCreateWithNoShortName() throws Exception {
		// check short name
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(Short.valueOf("1")), null, "abcd", null, null,
					OperationMode.REMOTE_SERVER);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.ERRORMANDATORYFIELD, e.getKey());

		}

	}

	public void testCreateWithNolevel() throws Exception {
		// check short name
		try {

			new OfficeBO(userContext, null, TestObjectFactory.getOffice(Short
					.valueOf("1")), null, "abcd", "abcd", null,
					OperationMode.REMOTE_SERVER);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.ERRORMANDATORYFIELD, e.getKey());

		}

	}

	public void testCreateWithNoOperationMode() throws Exception {
		// check short name
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, TestObjectFactory
					.getOffice(Short.valueOf("1")), null, "abcd", "abcd", null,
					null);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.ERRORMANDATORYFIELD, e.getKey());

		}

	}

	public void testCreateWithNoParent() throws Exception {
		// check short name
		try {

			new OfficeBO(userContext, OfficeLevel.AREAOFFICE, null, null,
					"abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		} catch (OfficeException e) {
			assertEquals(OfficeConstants.ERRORMANDATORYFIELD, e.getKey());

		}

	}

	public void testGetChildern() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		HibernateUtil.startTransaction();
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		HibernateUtil.commitTransaction();
		TestObjectFactory.flushandCloseSession();
		OfficeBO parent1 = TestObjectFactory.getOffice(Short.valueOf("1"));
		assertEquals(2, parent1.getChildren().size());
		officeBO = TestObjectFactory.getOffice(officeBO.getOfficeId());
		TestObjectFactory.cleanUp(officeBO);
	}

	public void testUpdateNameAndShortName() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();

		officeBO.update("3", "3", officeBO.getOfficeStatus(), officeBO
				.getOfficeLevel(), null, null, null);
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
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();
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
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();
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
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
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
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();
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

	// TODO : this test case is incosistance

	public void testUpdateParentSucess() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO officeBO = new OfficeBO(userContext,
				OfficeLevel.REGIONALOFFICE, parent, null, "abcd", "abcd", null,
				OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();
		OfficeBO ChildChild = new OfficeBO(userContext,
				OfficeLevel.BRANCHOFFICE, Child, null, "3", "3", null,
				OperationMode.REMOTE_SERVER);
		ChildChild.save();
		TestObjectFactory.flushandCloseSession();
		OfficeBO parent1 = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO savbedOffice = TestObjectFactory
				.getOffice(Child.getOfficeId());
		savbedOffice.setUserContext(userContext);
		savbedOffice.update("2", "2", Child.getOfficeStatus(), savbedOffice
				.getOfficeLevel(), parent1, null, null);
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice1 = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		OfficeBO savbedChild = TestObjectFactory.getOffice(Child.getOfficeId());
		OfficeBO savbedChildChild = TestObjectFactory.getOffice(ChildChild
				.getOfficeId());
		if (Child.getSearchId() == "1.1.2") {
			assertEquals("1.1.2", Child.getSearchId());
			assertEquals("1.1.2.1", ChildChild.getSearchId());
		}
		TestObjectFactory.cleanUp(savbedChildChild);
		TestObjectFactory.cleanUp(savbedChild);
		TestObjectFactory.cleanUp(savbedOffice1);

	}

	public void testUpdateOfficelevel() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
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
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		// createChild also
		OfficeBO Child = new OfficeBO(userContext, OfficeLevel.BRANCHOFFICE,
				officeBO, null, "2", "2", null, OperationMode.REMOTE_SERVER);
		Child.save();
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
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));

		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
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
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		List<CustomFieldView> customFieldView1 = new ArrayList<CustomFieldView>();

		CustomFieldView customFieldView3 = new CustomFieldView();
		customFieldView3.setFieldId(Short.valueOf("1"));
		customFieldView3.setFieldValue("3434");
		customFieldView1.add(customFieldView3);

		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, customFieldView1, "abcd", "abcd", null,
				OperationMode.REMOTE_SERVER);
		officeBO.save();
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
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice2 = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		for (OfficeCustomFieldEntity customField : savbedOffice2
				.getCustomFields()) {

			assertEquals("123456", customField.getFieldValue());
		}
		TestObjectFactory.cleanUp(savbedOffice2);
	}

	public void testUpdateCustomFields_WithNull() throws Exception {
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
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
		OfficeBO parent = TestObjectFactory.getOffice(Short.valueOf("1"));
		OfficeBO officeBO = new OfficeBO(userContext, OfficeLevel.AREAOFFICE,
				parent, null, "abcd", "abcd", null, OperationMode.REMOTE_SERVER);
		officeBO.save();
		TestObjectFactory.flushandCloseSession();
		OfficeBO savbedOffice = TestObjectFactory.getOffice(officeBO
				.getOfficeId());
		savbedOffice.setUserContext(userContext);
		savbedOffice.update("abcd", "abcd", OfficeStatus.INACTIVE, savbedOffice
				.getOfficeLevel(), null, null, null);
		TestObjectFactory.flushandCloseSession();
		savbedOffice = TestObjectFactory.getOffice(officeBO.getOfficeId());
		savbedOffice.setUserContext(userContext);
		savbedOffice.update("abcd", "abcd", OfficeStatus.ACTIVE, savbedOffice
				.getOfficeLevel(), null, null, null);
		TestObjectFactory.flushandCloseSession();
		savbedOffice = TestObjectFactory.getOffice(officeBO.getOfficeId());
		assertEquals(OfficeStatus.ACTIVE, savbedOffice.getOfficeStatus());
		TestObjectFactory.cleanUp(savbedOffice);
	}

	public void testOfficeLevelEntity() throws Exception {
		MasterPersistenceService masterPersistenceService = (MasterPersistenceService) ServiceFactory
				.getInstance().getPersistenceService(
						PersistenceServiceName.MasterDataService);
		OfficeLevelEntity levelEntity = (OfficeLevelEntity) masterPersistenceService.findById(
				OfficeLevelEntity.class, OfficeLevel.AREAOFFICE.getValue());
		assertNotNull(levelEntity.getChild());
		assertEquals(OfficeLevel.AREAOFFICE,levelEntity.getLevel());
		assertEquals(true,levelEntity.isConfigured());
		assertEquals(false,levelEntity.isInteractionFlag());
		assertNotNull(levelEntity.getParent());
		
	}

}
