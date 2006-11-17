package org.mifos.application.master.persistence;

import java.util.List;

import org.mifos.application.master.business.BusinessActivityEntity;
import org.mifos.application.master.business.EntityMaster;
import org.mifos.application.master.business.LookUpMaster;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.TestConstants;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestMasterPersistence extends MifosTestCase {

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	public void tearDown() {
		HibernateUtil.closeSession();

	}

	public void testEntityMasterRetrieval() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		EntityMaster paymentTypes = masterPersistence
				.getLookUpEntity(
						MasterConstants.ATTENDENCETYPES,
						(short)1,
						"org.mifos.application.master.business.CustomerAttendance",
						"attendanceId");
		List<LookUpMaster> paymentValues = paymentTypes.getLookUpMaster();
		assertEquals(4, paymentValues.size());

	}

	public void testEntityMasterRetrievalForInvalidConnection()
			throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterPersistence
					.getLookUpEntity(
							MasterConstants.ATTENDENCETYPES,
							(short)1,
							"org.mifos.application.master.business.CustomerAttendance",
							"attendanceId");
			fail();
		} catch (Exception e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testGetLookUpEntity() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		EntityMaster gender = masterPersistence.getLookUpEntity(
				MasterConstants.GENDER, Short.valueOf("1"));
		List<LookUpMaster> genderValues = gender.getLookUpMaster();
		assertEquals(2, genderValues.size());

	}

	public void testRetrievePaymentTypes() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<PaymentTypeEntity> paymentTypeList = masterPersistence
				.retrievePaymentTypes(Short.valueOf("1"));
		assertEquals(3, paymentTypeList.size());
	}

	public void testRetrievePaymentTypesForInvalidConnection() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterPersistence.retrievePaymentTypes(Short.valueOf("1"));
			fail();
		} catch (Exception e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testGetSupportedPaymentModes() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<PaymentTypeEntity> paymentTypeList = masterPersistence
				.getSupportedPaymentModes(Short.valueOf("1"), Short
						.valueOf("1"));
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER, paymentTypeList.size());
	}
	
	public void testRetrieveMasterEntities() throws NumberFormatException,
			PersistenceException {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<BusinessActivityEntity> masterEntity = masterPersistence
				.retrieveMasterEntities(MasterConstants.LOAN_PURPOSES, Short
						.valueOf("1"));
		assertEquals(129, masterEntity.size());
	}
	
	public void testRetrieveMasterEntitiesForInvalidConnection() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterPersistence
			.retrieveMasterEntities(MasterConstants.LOAN_PURPOSES, Short
					.valueOf("1"));
			fail();
		} catch (Exception e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}
	
	public void retrieveCustomFieldsDefinitionForInvalidConnection() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterPersistence
			.retrieveCustomFieldsDefinition(EntityType.CLIENT);
			fail();
		} catch (Exception e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

	public void testGetMasterEntityName() throws NumberFormatException,
			PersistenceException {
		MasterPersistence masterPersistence = new MasterPersistence();
		assertEquals("Partial Application", masterPersistence
				.retrieveMasterEntities(1, Short.valueOf("1")));
	}

	public void testRetrieveMasterDataEntity() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<MasterDataEntity> masterDataList = masterPersistence
				.retrieveMasterDataEntity("org.mifos.application.accounts.business.AccountStateEntity");
		assertEquals(18, masterDataList.size());
		for (MasterDataEntity masterDataEntity : masterDataList) {
			for (LookUpValueLocaleEntity lookUpValueLocaleEntity : masterDataEntity
					.getLookUpValue().getLookUpValueLocales()) {
				assertEquals(Short.valueOf("1"), lookUpValueLocaleEntity
						.getLocaleId());
			}
		}
	}
	
	public void testRetrieveMasterDataEntityForInvalidConnection() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterPersistence
			.retrieveMasterDataEntity("org.mifos.application.accounts.business.AccountStateEntity");
			fail();
		} catch (Exception e) {
			assertTrue(true);
		} finally {
			HibernateUtil.closeSession();
		}
	}

}
