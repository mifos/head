package org.mifos.framework.struts;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.struts.actionforms.TestBaseActionForm;
import org.mifos.framework.struts.plugin.TestConstPlugin;
import org.mifos.framework.struts.plugin.TestEnumPlugin;
import org.mifos.framework.struts.plugin.TestInitializerPlugin;
import org.mifos.framework.struts.tags.DateTagTest;
import org.mifos.framework.struts.tags.LookUpValueTagTest;
import org.mifos.framework.struts.tags.RawButtonTest;
import org.mifos.framework.struts.tags.RawSelectTest;

public class StrutsTestSuite extends TestSuite {

	public StrutsTestSuite() {
		super();
	}

	public static Test suite() throws Exception{
		TestSuite suite = new StrutsTestSuite();
		suite.addTestSuite(TestConstPlugin.class);
		suite.addTestSuite(TestEnumPlugin.class);
		suite.addTestSuite(TestInitializerPlugin.class);
		suite.addTestSuite(TestBaseActionForm.class);
		suite.addTestSuite(DateTagTest.class);
		suite.addTestSuite(LookUpValueTagTest.class);
		suite.addTestSuite(RawButtonTest.class);
		suite.addTestSuite(RawSelectTest.class);
		return suite;
	}

}
