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

import static org.mifos.framework.TestUtils.assertWellFormedFragment;
import junit.framework.Assert;
import junit.framework.TestCase;

import org.dom4j.DocumentException;

public class MifosAlphaNumTextTagTest extends TestCase {

    public void testRenderFieldHiddenMandatory() throws DocumentException {
        MifosAlphaNumTextTag mifosAlphaNumTextTag = new MifosAlphaNumTextTag();
        mifosAlphaNumTextTag.setKeyhm("test1");
        mifosAlphaNumTextTag.setPropertyExpr("test2");
        StringBuffer inputsForhidden = new StringBuffer();
        inputsForhidden.append("<input type=\"hidden\" name=\"" + "test1" + "\" value=\"" + "test2" + "\" />");
        assertWellFormedFragment(mifosAlphaNumTextTag.renderFieldHiddenMandatory());
       Assert.assertEquals(inputsForhidden.toString(), mifosAlphaNumTextTag.renderFieldHiddenMandatory());
    }

    public void testRenderDoStartTag() throws DocumentException {
        MifosAlphaNumTextTag mifosAlphaNumTextTag = new MifosAlphaNumTextTag();
        StringBuilder inputsForhidden = new StringBuilder();
        inputsForhidden.append("<script src=" + "\"pages/framework/js/func.js\"" + ">");
        inputsForhidden.append("</script>");
        inputsForhidden.append("<script src=" + "\"pages/framework/js/func_test.js\"" + ">");
        inputsForhidden.append("</script>");
        assertWellFormedFragment(mifosAlphaNumTextTag.renderDoStartTag("test"));
       Assert.assertEquals(inputsForhidden.toString(), mifosAlphaNumTextTag.renderDoStartTag("test"));
    }
}
