package org.mifos.framework.struts;

import org.mifos.framework.MifosTestSuite;
import org.mifos.framework.struts.actionforms.TestBaseActionForm;
import org.mifos.framework.struts.plugin.TestConstPlugin;
import org.mifos.framework.struts.plugin.TestEnumPlugin;
import org.mifos.framework.struts.plugin.TestInitializerPlugin;

import junit.framework.Test;
import junit.framework.TestSuite;

public class StrutsTestSuite extends MifosTestSuite {

	public StrutsTestSuite() {
		super();
	}

	public static Test suite() throws Exception{
		TestSuite suite = new StrutsTestSuite();
		suite.addTestSuite(TestConstPlugin.class);
		suite.addTestSuite(TestEnumPlugin.class);
		suite.addTestSuite(TestInitializerPlugin.class);
		suite.addTestSuite(TestBaseActionForm.class);
		return suite;
	}

}
