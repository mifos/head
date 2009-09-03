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

public class MifosImageTagTest extends TestCase {

    public void testRender() throws DocumentException {
        MifosImageTag mifosImageTag = new MifosImageTag();
        String path = "customer";
        mifosImageTag.setModuleName(path);
        mifosImageTag.setId("3");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("<html>");
        stringBuilder.append("<body>");
        String str = "\"pages/framework/images/status_activegreen.gif\"";
        stringBuilder.append("<img src=" + str + " />");
        stringBuilder.append("</body>");
        stringBuilder.append("</html>");
       Assert.assertEquals(stringBuilder.toString(), mifosImageTag.render());
        assertWellFormedFragment(mifosImageTag.render());
    }
}
