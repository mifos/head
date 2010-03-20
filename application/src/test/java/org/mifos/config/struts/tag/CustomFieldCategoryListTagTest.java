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

package org.mifos.config.struts.tag;

import static org.mifos.framework.TestUtils.assertWellFormedFragment;
import junit.framework.Assert;
import junit.framework.TestCase;
import junitx.framework.StringAssert;

import org.mifos.application.master.business.CustomFieldCategory;
import org.mifos.framework.TestUtils;
import org.mifos.framework.struts.tags.XmlBuilder;
import org.mifos.security.util.UserContext;

public class CustomFieldCategoryListTagTest extends TestCase {

    private UserContext userContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        TestUtils.initializeSpring();
        userContext = TestUtils.makeUser();
    }

    public void testGetCategoryRow() throws Exception {
        CustomFieldCategoryListTag tag = new CustomFieldCategoryListTag("action", "method", "flow");
        String categoryName = "Personnel";
        XmlBuilder link = tag.getCategoryRow(categoryName, categoryName);
       Assert.assertEquals("<tr class=\"fontnormal\"><td width=\"1%\">"
                + "<img src=\"pages/framework/images/bullet_circle.gif\" width=\"9\" height=\"11\" />" + "</td><td>"
                + "<a href=\"action?method=method&amp;" + "category=" + categoryName + "&amp;categoryName="
                + categoryName + "&amp;currentFlowKey=flow\">" + categoryName + "</a></td></tr>", link.getOutput());
    }

    public void testGetCustomFieldCategoryList() throws Exception {
        CustomFieldCategoryListTag tag = new CustomFieldCategoryListTag("action", "method", "flow");
        String html = tag.getCustomFieldCategoryList(userContext);
        assertWellFormedFragment(html);

        for (CustomFieldCategory category : CustomFieldCategory.values()) {
            StringAssert.assertContains(category.toString(), html);
        }
    }

}
