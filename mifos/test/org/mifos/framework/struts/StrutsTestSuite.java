package org.mifos.framework.struts;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.MifosTestSuite;
import org.mifos.framework.struts.actionforms.TestBaseActionForm;
import org.mifos.framework.struts.plugin.TestConstPlugin;
import org.mifos.framework.struts.plugin.TestEnumPlugin;
import org.mifos.framework.struts.plugin.TestInitializerPlugin;
import org.mifos.framework.struts.tag.DateTagTest;
import org.mifos.framework.struts.tag.LookUpValueTagTest;
import org.mifos.framework.struts.tag.MifosSelectTest;
import org.mifos.framework.struts.tag.RawButtonTest;
import org.mifos.framework.struts.tag.RawSelectTest;

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
		suite.addTestSuite(DateTagTest.class);
		suite.addTestSuite(LookUpValueTagTest.class);
		suite.addTestSuite(RawButtonTest.class);
		suite.addTestSuite(RawSelectTest.class);
		suite.addTestSuite(MifosSelectTest.class);
		return suite;
	}

}
