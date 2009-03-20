/*
 * Copyright (c) 2005-2009 Grameen Foundation USA
 * All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 *
 * See also http://www.apache.org/licenses/LICENSE-2.0.html for an
 * explanation of the license and how it is applied.
 */
 
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
import org.mifos.framework.struts.tags.MifosPropertyMessageResourcesTest;
import org.mifos.framework.struts.tags.MifosTextareaTagTest;
import org.mifos.framework.struts.tags.RawButtonTest;
import org.mifos.framework.struts.tags.RawSelectTest;
import org.mifos.framework.util.TestLocalizationConverter;

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
		suite.addTestSuite(TestLocalizationConverter.class);
		suite.addTest(MifosPropertyMessageResourcesTest.testSuite());
		return suite;
	}

}
