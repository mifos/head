/*
 * Copyright (c) 2005-2010 Grameen Foundation USA
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

import static org.mifos.framework.TestUtils.assertWellFormedFragment;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.dom4j.DocumentException;

public class MifosFileTagTest extends TestCase {

    public void testrenderInputsForhidden() throws DocumentException {
        MifosFileTag mifosFileTag = new MifosFileTag();
        mifosFileTag.setKeyhm("test1");
        mifosFileTag.setPropertyExpr("test2");
        StringBuffer inputsForhidden = new StringBuffer();
        inputsForhidden.append("<input type=\"hidden\" name=\"" + mifosFileTag.getKeyhm() + "\" value=\""
                + mifosFileTag.getPropertyExpr() + "\" />");
       Assert.assertEquals(inputsForhidden.toString(), mifosFileTag.renderInputsForhidden());
        assertWellFormedFragment(mifosFileTag.renderInputsForhidden());
    }
}
