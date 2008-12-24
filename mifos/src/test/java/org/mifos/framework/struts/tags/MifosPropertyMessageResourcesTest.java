package org.mifos.framework.struts.tags;

import static org.junit.Assert.assertEquals;
import junit.framework.JUnit4TestAdapter;

import org.apache.struts.util.MessageResources;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mifos.application.configuration.util.helpers.ConfigurationConstants;
import org.mifos.framework.TestUtils;
import org.mifos.framework.util.helpers.FilePaths;
import org.mifos.framework.util.helpers.TestCaseInitializer;

public class MifosPropertyMessageResourcesTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		TestCaseInitializer initializer = new TestCaseInitializer();
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testGetMessageLocaleString() {
		MifosPropertyMessageResourcesFactory factory = new MifosPropertyMessageResourcesFactory();
		MessageResources resource = factory.createResources(FilePaths.ACCOUNTS_UI_RESOURCE_PROPERTYFILE);
		
		// get a simple resource bundle entry
		assertEquals("Admin", resource.getMessage(TestUtils.ukLocale(), "Account.Admin"));
		// get a LookupValue 
		assertEquals("Branch Office", resource.getMessage(TestUtils.ukLocale(), ConfigurationConstants.BRANCHOFFICE));
	}

	public static junit.framework.Test testSuite() {
		return new JUnit4TestAdapter(MifosPropertyMessageResourcesTest.class);
	}
	
}
