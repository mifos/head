package org.mifos.application.master.persistence;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mifos.application.master.business.CustomFieldCategory;
import org.mifos.application.master.business.CustomValueList;
import org.mifos.application.master.business.CustomValueListElement;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MasterDataEntity;
import org.mifos.application.master.business.PaymentTypeEntity;
import org.mifos.application.master.business.ValueListElement;
import org.mifos.application.master.util.helpers.MasterConstants;
import org.mifos.application.util.helpers.EntityType;
import org.mifos.config.LocalizedTextLookup;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.PersistenceException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.security.activity.DynamicLookUpValueCreationTypes;
import org.mifos.framework.util.helpers.TestObjectFactory;

public class TestMasterPersistence extends MifosTestCase {
	public TestMasterPersistence() throws SystemException, ApplicationException {
        super();
    }

    final private static short DEFAULT_LOCALE = (short)1;
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	public void tearDown() {
		HibernateUtil.closeSession();
	}

	
	public void testEntityMasterRetrieval() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		CustomValueList paymentTypes = masterPersistence
				.getCustomValueList(
						MasterConstants.ATTENDENCETYPES,
						(short)1,
						"org.mifos.application.master.business.CustomerAttendanceType",
						"attendanceId");
		List<CustomValueListElement> paymentValues = paymentTypes.getCustomValueListElements();
		assertEquals(4, paymentValues.size());

	}

	public void testEntityMasterRetrievalForInvalidConnection()
			throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		TestObjectFactory.simulateInvalidConnection();
		try {
			masterPersistence
					.getCustomValueList(
							MasterConstants.ATTENDENCETYPES,
							(short)1,
							"org.mifos.application.master.business.CustomerAttendanceType",
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
		CustomValueList gender = masterPersistence.getLookUpEntity(
				MasterConstants.GENDER, Short.valueOf("1"));
		List<CustomValueListElement> genderValues = gender.getCustomValueListElements();
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

	/*public void testGetSupportedPaymentModes() throws Exception {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<PaymentTypeEntity> paymentTypeList = masterPersistence
				.getSupportedPaymentModes(Short.valueOf("1"), Short
						.valueOf("1"));
		assertEquals(TestConstants.PAYMENTTYPES_NUMBER, paymentTypeList.size());
	}*/
	
	public void testRetrieveMasterEntities() throws NumberFormatException,
			PersistenceException {
		MasterPersistence masterPersistence = new MasterPersistence();
		List<ValueListElement> masterEntity = masterPersistence
				.retrieveMasterEntities(MasterConstants.LOAN_PURPOSES, Short
						.valueOf("1"));
		//  131 if includes the empty lookup_name for lookup id 259, 263
		assertEquals(131, masterEntity.size());
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

	private boolean foundStringInCustomValueList(MasterPersistence masterPersistence, 
			String CustomValueListName, String searchString, short localId) 
			throws PersistenceException {
		List<ValueListElement> salutations = 
			masterPersistence.retrieveMasterEntities(CustomValueListName, localId);
		boolean foundString = false;
		for (ValueListElement entity : salutations) {
			if (entity.getName().compareTo(searchString)== 0) {
				foundString = true;
			}
		}	
		return foundString;
	}

	private Integer findValueListElementId(MasterPersistence masterPersistence, 
			String CustomValueListName, String searchString, short localId) 
			throws PersistenceException {
		List<ValueListElement> salutations = 
			masterPersistence.retrieveMasterEntities(CustomValueListName, localId);
		Integer elementId = null;
		for (ValueListElement entity : salutations) {
			if (entity.getName().compareTo(searchString)== 0) {
				elementId = entity.getId();
			}
		}	
		return elementId;
	}
	
	public void testAddAndDeleteValueListElement() throws Exception {
		// get the CustomValueList that we want to add to
		MasterPersistence masterPersistence = new MasterPersistence();
		CustomValueList salutationValueList = masterPersistence.getLookUpEntity(
				MasterConstants.SALUTATION, DEFAULT_LOCALE);
		
		// add a CustomValueListElement to the list
		final String NEW_SALUTATION_STRING = "Sir";
		LocalizedTextLookup lookupValueEntity = masterPersistence.addValueListElementForLocale(DynamicLookUpValueCreationTypes.LookUpOption,
				salutationValueList.getEntityId(), 
				NEW_SALUTATION_STRING);
		HibernateUtil.commitTransaction();
		HibernateUtil.flushAndCloseSession();
		
		// verify that the new salutation was created
		Integer newSalutationId = findValueListElementId(masterPersistence,  
				MasterConstants.SALUTATION, NEW_SALUTATION_STRING, DEFAULT_LOCALE);
		assertTrue(newSalutationId != null);
		
		// remove the new salutation
		masterPersistence.deleteValueListElement(newSalutationId);
		HibernateUtil.commitTransaction();
		HibernateUtil.flushAndCloseSession();
		
		// verify that the new salutation was deleted
		assertFalse(foundStringInCustomValueList(masterPersistence, 
				MasterConstants.SALUTATION, NEW_SALUTATION_STRING, DEFAULT_LOCALE));		
	}
	
	public void testUpdateValueListElement() throws Exception {
		// get a CustomValueListElement (as a BusinessActivityEntity)
		MasterPersistence masterPersistence = new MasterPersistence();
		List<ValueListElement> salutations = masterPersistence.retrieveMasterEntities(MasterConstants.SALUTATION, DEFAULT_LOCALE);
		ValueListElement first = salutations.get(0);
		Integer id = first.getId();
		String originalName = first.getName();
		
		// update it
		final String UPDATED_NAME = "Mister"; 
		first.setName(UPDATED_NAME);
		
		// save it
		masterPersistence.updateValueListElementForLocale(id,UPDATED_NAME);
		HibernateUtil.commitTransaction();
		
		// get the element back
		// and verify that it has the new value
		salutations.clear();
		salutations = masterPersistence.retrieveMasterEntities(MasterConstants.SALUTATION, DEFAULT_LOCALE);
		for (ValueListElement entity : salutations) {
			if (entity.getId() == id) {
				assertEquals(entity.getName(), UPDATED_NAME);
			}
		}
		// restore it
		masterPersistence.updateValueListElementForLocale(id,originalName);
		HibernateUtil.commitTransaction();

	}	
	
	
}
