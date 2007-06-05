package org.mifos.framework.security.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import junit.framework.JUnit4TestAdapter;

import org.junit.Before;
import org.junit.Test;
import org.mifos.framework.util.helpers.DatabaseSetup;


public class ActivityMapperTest {
	
	Pattern allowableActionName = Pattern.compile("([a-zA-Z])+");
	
	@Before public void setUp() {
		DatabaseSetup.configureLogging();
	}

	@Test public void namesAcceptable() {
		for (ActionSecurity security 
			: ActivityMapper.getInstance().getAllSecurity()) {
			String name = security.getActionName();
			assertTrue(
				"unacceptable action name " + name,
				acceptableName(name));
		}
	}
	
	@Test public void testMachinery() {
		assertTrue(acceptableName("openSesame"));
		assertFalse(acceptableName("/bin/sh"));
		assertFalse(acceptableName("/openSesame"));
		assertFalse(acceptableName("open,sesame"));
		assertFalse(acceptableName("open sesame"));
		assertFalse(acceptableName("openSesame "));
		assertFalse(acceptableName(""));
		assertFalse(acceptableName(null));
	}

	private boolean acceptableName(String name) {
		if (name == null) {
			return false;
		}
		return allowableActionName.matcher(name).matches();
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ActivityMapperTest.class);
	}

}
