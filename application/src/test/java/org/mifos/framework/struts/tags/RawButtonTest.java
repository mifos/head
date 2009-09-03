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

import junit.framework.Assert;
import junit.framework.TestCase;

public class RawButtonTest extends TestCase {

    public void testRawButton() {
        RawButton rawButton = new RawButton();
        rawButton.setDisabled("disabled");
        rawButton.setId("id");
        rawButton.setName("name");
        rawButton.setOnclick("onClick");
        rawButton.setStyle("style");
        rawButton.setOndblclick("onDblClick");
        rawButton.setTitle("title");
        rawButton.setType("type");
        rawButton.setValue("value");
       Assert.assertEquals("disabled", rawButton.getDisabled());
       Assert.assertEquals("id", rawButton.getId());
       Assert.assertEquals("name", rawButton.getName());
       Assert.assertEquals("onClick", rawButton.getOnclick());
       Assert.assertEquals("onDblClick", rawButton.getOndblclick());
       Assert.assertEquals("title", rawButton.getTitle());
       Assert.assertEquals("type", rawButton.getType());
       Assert.assertEquals("value", rawButton.getValue());
       Assert.assertTrue(rawButton.toString().contains("Click To move the selected item"));
    }
}
