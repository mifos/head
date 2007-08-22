package org.mifos.framework.struts;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.mifos.framework.struts.actionforms.TestBaseActionForm;
import org.mifos.framework.struts.plugin.TestConstPlugin;
import org.mifos.framework.struts.plugin.TestEnumPlugin;
import org.mifos.framework.struts.plugin.TestInitializerPlugin;
import org.mifos.framework.struts.tags.DateTagTest;
import org.mifos.framework.struts.tags.MifosAlphaNumTextTagTest;
import org.mifos.framework.struts.tags.MifosCheckBoxTagTest;
import org.mifos.framework.struts.tags.MifosFileTagTest;
import org.mifos.framework.struts.tags.MifosImageTagTest;
import org.mifos.framework.struts.tags.MifosLabelTagTest;
import org.mifos.framework.struts.tags.MifosNumberTextTagTest;
import org.mifos.framework.struts.tags.MifosTextareaTagTest;
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
		suite.addTestSuite(RawButtonTest.class);
		suite.addTestSuite(RawSelectTest.class);
		suite.addTestSuite(MifosCheckBoxTagTest.class);
		suite.addTestSuite(MifosTextareaTagTest.class);
		suite.addTestSuite(MifosFileTagTest.class);
		suite.addTestSuite(MifosLabelTagTest.class);
		suite.addTestSuite(MifosImageTagTest.class);
		suite.addTestSuite(MifosFileTagTest.class);
		suite.addTestSuite(MifosAlphaNumTextTagTest.class);
		suite.addTestSuite(MifosNumberTextTagTest.class);
		return suite;
	}

}
