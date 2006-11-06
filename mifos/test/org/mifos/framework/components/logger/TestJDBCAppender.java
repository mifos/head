package org.mifos.framework.components.logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.mifos.framework.MifosTestCase;
import org.mifos.framework.hibernate.helper.HibernateUtil;
import org.mifos.framework.util.helpers.FilePaths;

public class TestJDBCAppender extends MifosTestCase {

	private String message = "";

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		MifosLogManager.configure("org/mifos/framework/components/logger/util/helpers/loggerconfiguration.xml");
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	    Connection con = null;
	    Statement stmt = null;

	    try {
			con = HibernateUtil.getSessionTL().connection();
	        stmt = con.createStatement();
	        String sql = "DELETE from logmessages where MESSAGE LIKE \"%" + message + "%\"";
	        int noOfSets = stmt.executeUpdate(sql);
	        assertEquals(1, noOfSets);
	    } catch (SQLException e) {
	       if (stmt != null)
		     stmt.close();
	       throw e;
	    } finally {
		    stmt.close();
		    if (con != null && !con.isClosed()) {
	    	    HibernateUtil.closeSession();
	        }
	    }
	    MifosLogManager.configure(FilePaths.LOGFILE);
	}

	public void testGetConnection() throws Exception {
		MifosLogger logger = MifosLogManager.getLogger("org.mifos.framework.logger");
		message = new Long(System.currentTimeMillis()).toString();
		logger.debug(message);
		checkIfLogged();
	}
	private void checkIfLogged() throws Exception {
	    Connection con = null;
	    Statement stmt = null;

	    try {
			con = HibernateUtil.getSessionTL().connection();
	        stmt = con.createStatement();
	        String sql = "SELECT MESSAGE from logmessages where MESSAGE LIKE \"%" + message + "%\"";
	        ResultSet rs = stmt.executeQuery(sql);
	        int count = 0;
	        while (rs.next()) {
	        	count++;
	        }
	        assertEquals(1, count);
	    } catch (SQLException e) {
	       if (stmt != null)
		     stmt.close();
	       throw e;
	    } finally {
		    stmt.close();
		    if (con != null && !con.isClosed()) {
	    	    HibernateUtil.closeSession();
	        }
	    }
	}
}