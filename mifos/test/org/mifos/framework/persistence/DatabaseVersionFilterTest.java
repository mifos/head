package org.mifos.framework.persistence;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;

import junit.framework.TestCase;
import junitx.framework.StringAssert;

import org.mifos.framework.ApplicationInitializer;

public class DatabaseVersionFilterTest extends TestCase {
	
	@Override
	protected void setUp() throws Exception {
		ApplicationInitializer.setDatabaseVersionError(null);
	}
	
	@Override
	protected void tearDown() throws Exception {
		ApplicationInitializer.setDatabaseVersionError(null);
	}
	
	public void testDatabaseIsReallyOld() throws Exception {
		String output = printError(-1);
		
		StringAssert.assertContains("Database is too old to have a version", 
			output);
	}

	public void testUpgradeFailed() throws Exception {
		ApplicationInitializer.setDatabaseVersionError(
			new SQLException("bletch ick sputter die"));
		String output = printError(66);
		
		StringAssert.assertContains("Database Version = 66\n", output);
		StringAssert.assertContains(
			"<p>Here are details of what went wrong:</p>", output);
		StringAssert.assertContains("bletch ick sputter die", output);
	}

	public void testInexplicableFailure() throws Exception {
		ApplicationInitializer.setDatabaseVersionError(null);
		String output = printError(66);
		
		StringAssert.assertContains("Database Version = 66\n", output);
		StringAssert.assertContains(
			"<p>I don't have any further details, unfortunately.</p>", output);
		StringAssert.assertNotContains("Exception", output);
	}

	private String printError(int version) {
		StringWriter out = new StringWriter();
		new DatabaseVersionFilter().printErrorPage(new PrintWriter(out), version);
		String output = out.toString();
		return output;
	}
	
}
