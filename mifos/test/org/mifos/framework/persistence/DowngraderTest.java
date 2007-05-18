package org.mifos.framework.persistence;

import static org.mifos.framework.persistence.DatabaseVersionPersistence.APPLICATION_VERSION;
import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class DowngraderTest {
	
	private Downgrader downgrader = new Downgrader();
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	private PrintStream printStream = new PrintStream(out);
	
	@Test public void noArguments() throws Exception {
		String output = run(new String[] { });
		assertEquals("Missing argument for what version to downgrade to.\n",
			output);
	}
	
	@Test public void excessArguments() throws Exception {
		String output = run(new String[] { "43", "53" });
		assertEquals("Excess argument 53.\n",
			output);
	}
	
	@Test public void forgotDashDInAnt() throws Exception {
		String output = run(new String[] { "${downgrade.to}" });
		assertEquals("Argument ${downgrade.to} is not a number.\n",
			output);
	}

	@Test public void nonNumeric() throws Exception {
		String output = run(new String[] { "(-)" });
		assertEquals("Argument (-) is not a number.\n",
			output);
	}
	
	@Test public void tooLow() throws Exception {
		String output = run(new String[] { "99" });
		assertEquals("Attempt to downgrade to 99 which is before 100.\n",
			output);
	}

	@Test public void negative() throws Exception {
		String output = run(new String[] { "-102" });
		assertEquals("Attempt to downgrade to -102 which is before 100.\n",
			output);
	}
	
	@Test public void slightlyTooHigh() throws Exception {
		String output = run(new String[] { "" + APPLICATION_VERSION });
		assertEquals("Attempt to downgrade to " + APPLICATION_VERSION + 
			" which is after " + (APPLICATION_VERSION - 1) + ".\n",
			output);
	}

	@Test public void wayTooHigh() throws Exception {
		String output = run(new String[] { "111888777" });
		assertEquals("Attempt to downgrade to 111888777" + 
			" which is after " + (APPLICATION_VERSION - 1) + ".\n",
			output);
	}

	private String run(String[] arguments) 
	throws UnsupportedEncodingException {
		downgrader.run(arguments, printStream);
		printStream.flush();
		return out.toString("UTF-8");
	}
	
	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(DowngraderTest.class);
	}

}
