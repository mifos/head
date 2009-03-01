package org.mifos.application.accounts.business;

import java.util.Set;

import junit.framework.TestCase;

import org.hibernate.classic.Session;
import org.mifos.application.accounts.util.helpers.AccountActionTypes;
import org.mifos.application.master.MessageLookup;
import org.mifos.application.master.business.LookUpValueEntity;
import org.mifos.application.master.business.LookUpValueLocaleEntity;
import org.mifos.application.master.business.MifosLookUpEntity;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.persistence.TestDatabase;
import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * Also see {@link TestAccountActionEntity}.
 */
public class AccountActionEntityTest extends MifosTestCase {

	public AccountActionEntityTest() throws SystemException, ApplicationException {
        super();
    }

    public void testBasics() throws Exception {
		DatabaseSetup.initializeHibernate();
		TestDatabase database = TestDatabase.makeStandard();
		
		Session session = database.openSession();
		AccountActionEntity action = (AccountActionEntity) 
			session.get(AccountActionEntity.class, 
				AccountActionTypes.PAYMENT.getValue());

		LookUpValueEntity lookUpValue = action.getLookUpValue();
		assertEquals("AccountAction-Payment", lookUpValue.getLookUpName());
		assertEquals(new Integer(191), lookUpValue.getLookUpId());

		MifosLookUpEntity lookUpEntity = lookUpValue.getLookUpEntity();
		assertEquals(MifosLookUpEntity.ACCOUNT_ACTION, 
			lookUpEntity.getEntityId().shortValue());
		assertEquals("AccountAction", lookUpEntity.getEntityType());

		Set<LookUpValueLocaleEntity> valueLocales = 
			lookUpValue.getLookUpValueLocales();
		assertEquals(1, valueLocales.size());
		LookUpValueLocaleEntity valueLocale = valueLocales.iterator().next();
		assertEquals(1, (int)valueLocale.getLocaleId());
		assertEquals("Payment", MessageLookup.getInstance().lookup(lookUpValue));

		assertEquals("Payment", action.getName());
		session.close();
	}
	
	public void testEnum() throws Exception {
		AccountActionTypes myEnum = AccountActionTypes.FEE_REPAYMENT;
		AccountActionEntity entity = new AccountActionEntity(myEnum);
		assertEquals(myEnum.getValue(), entity.getId());
		
		AccountActionTypes out = entity.asEnum();
		assertEquals(myEnum, out);
	}
	
	public void testFromBadInt() throws Exception {
		try {
			AccountActionTypes.fromInt(9999);
			fail();
		}
		catch (RuntimeException e) {
			assertEquals("no account action 9999", e.getMessage());
		}
	}

}
