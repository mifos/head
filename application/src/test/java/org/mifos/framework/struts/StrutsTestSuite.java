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

import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mifos.framework.struts.actionforms.BaseActionFormIntegrationTest;
import org.mifos.framework.struts.plugin.ConstPluginTest;
import org.mifos.framework.struts.plugin.EnumPluginTest;
import org.mifos.framework.struts.plugin.InitializerPluginTest;
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
import org.mifos.framework.util.LocalizationConverterTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { ConstPluginTest.class, EnumPluginTest.class, InitializerPluginTest.class,
        BaseActionFormIntegrationTest.class, DateTagTest.class, RawButtonTest.class, RawSelectTest.class,
        MifosCheckBoxTagTest.class, MifosTextareaTagTest.class,
        MifosFileTagTest.class, MifosLabelTagTest.class, MifosImageTagTest.class,
        MifosFileTagTest.class, MifosAlphaNumTextTagTest.class,
        MifosNumberTextTagTest.class, LocalizationConverterTest.class,
        MifosPropertyMessageResourcesTest.class })
public class StrutsTestSuite extends TestSuite {
    // placeholder class
}
