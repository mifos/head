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

import java.util.Collections;
import java.util.Map;

import org.testng.annotations.Test;

import junit.framework.TestCase;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class MifosSelectTest extends TestCase {

    private static final String INTRODUCTORY_STYLES_AND_SCRIPT = " <STYLE> " + ".ttip {border:1px solid black;"
            + "font-size:12px;" + "layer-background-color:lightyellow;" + "background-color:lightyellow}  "
            + "</STYLE> " + "<script language=\"javascript\" " + "SRC=\"pages/framework/js/Logic.js\" >" + "</script> "
            + "<link rel=\"stylesheet\" type=\"text/css\" " + "href=\"pages/framework/css/tooltip.css\" "
            + "title=\"MyCSS\"/>";

    public void testGettersAndSetters() {
        MifosSelect mifosSelect = new MifosSelect();
        mifosSelect.setId("id");
        mifosSelect.setInput("input");
        mifosSelect.setLabel("label");
        mifosSelect.setMultiple("multiple");
        mifosSelect.setName("name");
        mifosSelect.setOutput("output");
        mifosSelect.setProperty("property");
        mifosSelect.setProperty1("property1");
        mifosSelect.setProperty2("property2");
        mifosSelect.setSelectStyle("selectStyle");
        mifosSelect.setSize("1");
        mifosSelect.setValue("key", "value");
        assertEquals("id", mifosSelect.getId());
        assertEquals("input", mifosSelect.getInput());
        assertEquals("label", mifosSelect.getLabel());
        assertEquals("multiple", mifosSelect.getMultiple());
        assertEquals("name", mifosSelect.getName());
        assertEquals("output", mifosSelect.getOutput());
        assertEquals("property", mifosSelect.getProperty());
        assertEquals("property1", mifosSelect.getProperty1());
        assertEquals("property2", mifosSelect.getProperty2());
        assertEquals("selectStyle", mifosSelect.getSelectStyle());
        assertEquals("1", mifosSelect.getSize());
        mifosSelect = new MifosSelect("newlabel");
        assertEquals("newlabel", mifosSelect.getLabel());
    }

    public void testRenderEmpty() throws Exception {
        String output = new MifosSelect().render(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
        // TestUtils.assertWellFormedFragment(output);
        assertEquals(INTRODUCTORY_STYLES_AND_SCRIPT + "<table >" + "<tr> " + "<td>" + selectTheItem(true) + "</td>"
                + "<td>" + "<table width=\"50%\" border=\"0\" " + "cellspacing=\"0\" cellpadding=\"3\"> " + "<tr>"
                + "<td align=\"center\">" + "<INPUT  name=\"MoveRight\" type=\"button\" value=\"Add >>\" "
                + "style=\"width:65px\" class=\"insidebuttn\" " + "id=\"null.button.add\" "
                + "onclick=\"moveOptions(this.form.LeftSelect,this.form.null)\""
                + "onMouseover=\"showtip(this,event,'Click To move the selected item')\" "
                + "onMouseout=\"hidetip()\" >" + "</INPUT>" + "</td>" + "</tr>" + "<tr>"
                + "<td height=\"26\" align=\"center\">" + "<INPUT  type=\"button\" value=\"<< Remove\" "
                + "style=\"width:65px\" class=\"insidebuttn\" " + "id=\"null.button.remove\" "
                + "onclick=\"moveOptions(this.form.null,this.form.LeftSelect)\""
                + "onMouseover=\"showtip(this,event,'Click To move the selected item')\" "
                + "onMouseout=\"hidetip()\" >" + "</INPUT>" + "</td>" + "</tr>" + "</table>" + "</td>" + "<td>"
                + selectTheItem(false) + "</td>" + "</tr>" + "</table>" + "<div id=\"tooltip\" "
                + "style=\"position:absolute;" + "visibility:hidden;" + "border:1px solid black;" + "font-size:12px;"
                + "layer-background-color:lightyellow;" + "background-color:lightyellow;" + "z-index:1;"
                + "padding:1px\">" + "</div>", output);
    }

    private String selectTheItem(boolean leftSelect) {
        return "<SELECT onMouseover=\"showtip(this,event,'Select the item(s)')\" " + "onMouseOut=\"hidetip()\" "
                + "onchange =\"showtip(this,event,'Select the item(s)')\" " + "style=\"WIDTH: 136px\" "
                + (leftSelect ? "name=\"LeftSelect\" " : "") + "size=\"5\">" + "</SELECT> ";
    }

    public void testHelperEmpty() throws Exception {
        Map<?, ?> map = new MifosSelect().helper(Collections.EMPTY_LIST);
        assertEquals(null, map);
    }

    public void testHelperSameClass() throws Exception {
        MifosSelect select = new MifosSelect();
        select.setProperty1("propertyOne");
        select.setProperty2("propertyTwo");
        Map<?, ?> map = select.helper(Collections.singletonList(new Foo()));
        assertEquals(1, map.size());
        assertEquals(new Integer(5), map.keySet().iterator().next());
        assertEquals("Acorn", map.get(5));
    }

    public void testHelperPrivate() throws Exception {
        MifosSelect select = new MifosSelect();
        select.setProperty1("propertyOne");
        select.setProperty2("privateProperty");
        try {
        select.helper(Collections.singletonList(new Foo()));
        fail("NoSuchMethodException was expected !!!");
        } catch (NoSuchMethodException e){}
        }

    public void testHelperParent() throws Exception {
        MifosSelect select = new MifosSelect();
        select.setProperty1("parentPropertyOne");
        select.setProperty2("parentPropertyTwo");
        Map<?, ?> map = select.helper(Collections.singletonList(new Foo()));
        assertEquals(1, map.size());
        assertEquals(new Integer(55), map.keySet().iterator().next());
        assertEquals("Oak", map.get(55));
    }

    class Foo extends ParentFoo {
        public int getPropertyOne() {
            return 5;
        }

        public String getPropertyTwo() {
            return "Acorn";
        }

        @SuppressWarnings("unused")
        private String getPrivateProperty() {
            return "mine";
        }
    }

    class ParentFoo {
        public int getParentPropertyOne() {
            return 55;
        }

        public String getParentPropertyTwo() {
            return "Oak";
        }
    }
}
