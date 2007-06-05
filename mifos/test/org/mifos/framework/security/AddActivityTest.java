package org.mifos.framework.security;

import static org.junit.Assert.assertEquals;
import static org.mifos.framework.util.helpers.TestObjectFactory.TEST_LOCALE;
import junit.framework.JUnit4TestAdapter;

import org.junit.Test;
import org.mifos.application.rolesandpermission.business.ActivityEntity;
import org.mifos.framework.TestDatabase;
import org.mifos.framework.persistence.DatabaseVersionPersistence;
import org.mifos.framework.security.util.resources.SecurityConstants;

public class AddActivityTest {

	@Test
	public void nothing() {
		
	}

	//@Test // AddActivity isn't implemented yet
	public void startFromStandardStore() throws Exception {
		TestDatabase database = TestDatabase.makeStandard();
		short newId = 17032;
		AddActivity upgrade = new AddActivity(
			DatabaseVersionPersistence.APPLICATION_VERSION + 1,
			newId,
			SecurityConstants.LOAN_MANAGEMENT,
			TEST_LOCALE,
			"Can use the executive washroom");
		upgrade.upgrade(database.openConnection());
		ActivityEntity fetched = (ActivityEntity) 
			database.openSession().get(ActivityEntity.class, newId);
		fetched.setLocaleId(TEST_LOCALE);
		assertEquals("Can use the executive washroom",
			fetched.getActivityName());
		// assert on parent
		// assert that SYSTEM_USER (or role thereof) gets this activity
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(AddActivityTest.class);
	}

}
