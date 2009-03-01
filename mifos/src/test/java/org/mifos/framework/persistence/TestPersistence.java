package org.mifos.framework.persistence;

import net.sourceforge.mayfly.Database;

import org.hibernate.SessionFactory;
import org.hibernate.classic.Session;
import org.mifos.application.accounts.financial.business.GLCodeEntity;
import org.mifos.application.accounts.loan.persistance.LoanPersistence;
import org.mifos.framework.MifosTestCase;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.DatabaseSetup;

public class TestPersistence extends MifosTestCase {

	public TestPersistence() throws SystemException, ApplicationException {
        super();
    }

    public void testConnection() {
		LoanPersistence loanPersistance = new LoanPersistence();
		assertNotNull(loanPersistance.getConnection());
		HibernateUtil.closeSession();
	}

	public void xtestNonUniqueObjectException() throws Exception {
		/* The question is whether this is the kind of hibernate situation
		   which produces a NonUniqueObjectException.  As written, this
		   test doesn't even get that far (having trouble inserting into
		   a table with an auto-increment column). */
		SessionFactory factory = DatabaseSetup.mayflySessionFactory();
		Database database = new Database(DatabaseSetup.getStandardStore());
		Session firstSession = factory.openSession(database.openConnection());

		GLCodeEntity firstEntity = new GLCodeEntity((short)77, "code1");
		firstSession.save(firstEntity);
		
		Session secondSession = factory.openSession(database.openConnection());
		assertEquals("code1", firstEntity.getGlcode());
		
		GLCodeEntity refetched = (GLCodeEntity)
			secondSession.get(GLCodeEntity.class, firstEntity.getGlcodeId());
		assertEquals("code1", refetched.getGlcode());
	}

}
