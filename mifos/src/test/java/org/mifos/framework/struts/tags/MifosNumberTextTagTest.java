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
 
package org.mifos.framework.struts.tags;

import org.dom4j.DocumentException;
import org.mifos.framework.MifosIntegrationTest;
import org.mifos.framework.exceptions.ApplicationException;
import org.mifos.framework.exceptions.SystemException;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;

public class MifosNumberTextTagTest extends MifosIntegrationTest {
	
	public MifosNumberTextTagTest() throws SystemException, ApplicationException {
        super();
    }

    public void testrenderInputsForhidden() throws DocumentException{
		MifosNumberTextTag mifosNumberTextTag = new MifosNumberTextTag();
		StringBuilder inputsForhidden=new StringBuilder();
		inputsForhidden.append("<script src="+"\"pages/framework/js/func.js\""+">");
		inputsForhidden.append("</script>");
		inputsForhidden.append("<script src="+"\"pages/framework/js/func_test.js\""+">");
		inputsForhidden.append("</script>");
		assertEquals(inputsForhidden.toString(),mifosNumberTextTag.render("test"));
		assertWellFormedFragment(mifosNumberTextTag.render("test"));
	}
}
