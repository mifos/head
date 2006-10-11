package org.mifos.application;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;
import net.sourceforge.mayfly.Database;
import net.sourceforge.mayfly.JdbcDriver;
import net.sourceforge.mayfly.MayflySqlException;
import net.sourceforge.mayfly.datastore.DataStore;

import org.hibernate.cfg.Configuration;
import org.hibernate.classic.Session;
import org.mifos.application.master.business.MifosCurrency;
import org.mifos.framework.components.logger.MifosLogManager;
import org.mifos.framework.util.helpers.FilePaths;

public class HibernateTest extends TestCase {
	
	public void xtestMysql() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
		
		Configuration hibernateConfiguration = new Configuration();
		hibernateConfiguration.setProperty(
			"hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
		hibernateConfiguration.setProperty(
			"hibernate.connection.url", "jdbc:mysql://localhost:3306/test");
		hibernateConfiguration.setProperty(
			"hibernate.connection.username", "root");
		hibernateConfiguration.setProperty(
			"hibernate.connection.password", "mysql");
		hibernateConfiguration.setProperty(
			"hibernate.dialect", "org.hibernate.dialect.GenericDialect");

		// This is the slow part...
		hibernateConfiguration.configure(
		    new File("src/" + FilePaths.HIBERNATECFGFILE)
//			ResourceLoader.getURI(FilePaths.HIBERNATECFGFILE).toURL()
		);

		Session session = 
			hibernateConfiguration.buildSessionFactory().openSession();
		checkJdbc(session);
		checkHibernateQuery(session);
		session.close();
	}

	private void checkJdbc(Session session) throws SQLException {
		Connection connection = session.connection();
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

    public void testMayfly() throws Exception {
        Database database = new Database(STANDARD_STORE);
        ResultSet results = database.query("select * from logmessages");
        assertFalse(results.next());
    }
    
    public void testMayflyAndHibernate() throws Exception {
		MifosLogManager.configure(FilePaths.LOGFILE);
		
		String url = JdbcDriver.create(STANDARD_STORE);
		Configuration hibernateConfiguration = new Configuration();
		hibernateConfiguration.setProperty(
			"hibernate.connection.driver_class", 
			"net.sourceforge.mayfly.JdbcDriver");
		hibernateConfiguration.setProperty(
			"hibernate.connection.url", url);
		hibernateConfiguration.setProperty(
			"hibernate.dialect", "org.hibernate.dialect.MySQLDialect");

		// This is the slow part...
		hibernateConfiguration.configure(
		    new File("src/" + FilePaths.HIBERNATECFGFILE)
//			ResourceLoader.getURI(FilePaths.HIBERNATECFGFILE).toURL()
		);

		Session session = 
			hibernateConfiguration.buildSessionFactory().openSession();
		checkJdbc(session);
		checkHibernateQuery(session);
		session.close();
	}

	private static DataStore createStandardStore() {
		Database database = new Database();
        executeScript(database, "sql/mifosdbcreationscript.sql");
        executeScript(database, "sql/Iteration13-DBScripts25092006.sql");

        executeScript(database, "sql/mifosmasterdata.sql");
		return database.dataStore();
	}

    public static final DataStore STANDARD_STORE = createStandardStore();

    private static void executeScript(Database database, String name) {
        try {
            Reader sql = new FileReader(
                    "c:/Documents and Settings/jkingdon/" +
                    "workspace/mifos/" +
                    name
                );

            database.executeScript(sql);
        } catch (MayflySqlException e) {
            throw (RuntimeException)(new RuntimeException(
                "error at line " + e.startLineNumber() +
                " column " + e.startColumn()
                ).initCause(e));
        } catch (FileNotFoundException e) {
        	throw new RuntimeException(e);
		}
    }

}
