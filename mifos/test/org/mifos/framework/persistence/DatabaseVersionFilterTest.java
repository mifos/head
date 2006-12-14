package org.mifos.framework.persistence;

import java.io.PrintWriter;
import java.io.StringWriter;

import junit.framework.TestCase;
import junitx.framework.StringAssert;

public class DatabaseVersionFilterTest extends TestCase {
	
	public void testDatabaseIsReallyOld() throws Exception {
		StringWriter out = new StringWriter();
		new DatabaseVersionFilter().printErrorPage(new PrintWriter(out), -1);
		String output = out.toString();
		
		StringAssert.assertContains("Database is too old to have a version", output);
	}

	public void testUpgradeFailed() throws Exception {
		StringWriter out = new StringWriter();
		new DatabaseVersionFilter().printErrorPage(new PrintWriter(out), 66);
		String output = out.toString();
		
		StringAssert.assertContains("Database Version = 66\n", output);
	}

}
