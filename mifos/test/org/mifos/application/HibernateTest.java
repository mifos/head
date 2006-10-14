package org.mifos.application;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;
import net.sourceforge.mayfly.Database;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.mifos.application.accounts.financial.util.helpers.FinancialInitializer;
import org.mifos.application.configuration.business.MifosConfiguration;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.application.master.persistence.MasterPersistence;
import org.mifos.framework.components.audit.util.helpers.AuditConfigurtion;
import org.mifos.framework.hibernate.factory.HibernateSessionFactory;
import org.mifos.framework.security.authorization.AuthorizationManager;
import org.mifos.framework.security.authorization.HierarchyManager;
import org.mifos.framework.util.helpers.DatabaseSetup;

/**
 * This class might be an interesting demonstration/check of how
 * we have hibernate set up to talk to different test databases.
 * 
 * On this other hand, it is a bit slow for something which doesn't
 * test MIFOS, and things like the statics in {@link DatabaseSetup}
 * probably are problematic in terms of modifying state which other
 * tests will want to deal with.  Therefore, {@link #xtestMayflyAndHibernate()}
 * and {@link #xtestMysql()} are disabled, and the whole test
 * isn't part of the regular suite.
 */
public class HibernateTest extends TestCase {
	
    public void testMayflyNoHibernate() throws Exception {
		Database database = new Database(DatabaseSetup.STANDARD_STORE);
        ResultSet results = database.query("select * from logmessages");
        assertFalse(results.next());
        
        checkJdbc(database.openConnection());
    }
    
    public void xtestStartup() throws Exception {
		DatabaseSetup.configureLogging();
		DatabaseSetup.initializeHibernate();
		squawk("hibernate");
		FinancialInitializer.initialize();
		squawk("financial");
		AuthorizationManager.getInstance().init();
		squawk("authorization");
		HierarchyManager.getInstance().init();
		squawk("hierarchy");
		MifosConfiguration.getInstance().init();
		squawk("mifos");
		AuditConfigurtion.init(); // brings to knees
		squawk("audit");
	}
    
    public void xtestBringToKnees1() throws Exception {
		DatabaseSetup.configureLogging();
		DatabaseSetup.initializeHibernate();
    	new MasterPersistence().retrieveMasterEntities("Loan Purposes", (short)1);
    }
    
    public void xtestBringToKnees2() throws Exception {
		Database database = new Database(DatabaseSetup.STANDARD_STORE);
		System.out.println(database.rowCount("lookup_entity")); // 89
		System.out.println(database.rowCount("lookup_value")); // 555
		System.out.println(database.rowCount("lookup_value_locale")); // 546
        ResultSet results = database.query(
        	"select lookupvalu2_.LOOKUP_ID as col_0_0_, " +
        	"lookupvalu2_.LOOKUP_VALUE as col_1_0_ " +
        	"from " +
        	"LOOKUP_ENTITY mifoslooku0_, " +
        	"LOOKUP_VALUE lookupvalu1_, " +
        	"LOOKUP_VALUE_LOCALE lookupvalu2_ " +
        	"where " +
        	"(mifoslooku0_.ENTITY_ID=lookupvalu1_.ENTITY_ID and " +
        	"lookupvalu2_.LOOKUP_ID=lookupvalu1_.LOOKUP_ID and " +
        	"lookupvalu2_.LOCALE_ID=1 and mifoslooku0_.ENTITY_NAME='Loan Purposes')"
        );
        results.close();
	}

	private void squawk(String whatWeDid) {
		System.out.println(whatWeDid +
			" done " + Runtime.getRuntime().totalMemory() / 1000000.0);
	}
    
    public void xtestMayflyAndHibernate() throws Exception {
		Configuration configuration = DatabaseSetup.mayflyConfiguration();
		SessionFactory sessionFactory = configuration.buildSessionFactory();

		// From here on out is fast; the slow stuff is above this.
		Session session = 
			sessionFactory.openSession();
		checkJdbc(session.connection());
		checkHibernateQuery(session);
		session.close();
	}

	public void xtestMysql() throws Exception {
		DatabaseSetup.setMysql();

		Session session = 
			HibernateSessionFactory.getSessionFactory().openSession();
		checkJdbc(session.connection());
		checkHibernateQuery(session);
		session.close();
	}

	private void checkJdbc(Connection connection) throws SQLException {
		Statement statement = connection.createStatement();
		ResultSet results = statement.executeQuery(
			"select currency_name from currency where currency_id = 3");
		assertTrue(results.next());
		String name = results.getString("currency_name");
		assertEquals("EURO", name);
		assertFalse(results.next());
		results.close();
		statement.close();
	}

	private void checkHibernateQuery(Session session) {
		MifosCurrency currency = (MifosCurrency)
			session.get(MifosCurrency.class, new Short((short) 3));
		assertEquals("EURO", currency.getCurrencyName());
	}

}
