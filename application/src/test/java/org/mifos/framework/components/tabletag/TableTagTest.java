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

package org.mifos.framework.components.tabletag;

import static junitx.framework.StringAssert.assertContains;
import static junitx.framework.StringAssert.assertNotContains;
import static org.mifos.framework.TestUtils.assertWellFormedFragment;

import java.util.Locale;
import java.util.MissingResourceException;

import javax.servlet.jsp.JspException;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.mifos.accounts.loan.util.helpers.LoanConstants;
import org.mifos.accounts.loan.util.helpers.RequestConstants;
import org.mifos.customers.business.CustomerSearch;
import org.mifos.customers.util.helpers.CustomerSearchConstants;
import org.mifos.config.Localization;
import org.mifos.framework.exceptions.TableTagException;
import org.mifos.framework.exceptions.TableTagTypeParserException;
import org.mifos.framework.util.helpers.SearchObject;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class TableTagTest extends TestCase {

    public void testNoResults() throws Exception {
        String html = new TableTag("single").noResults("default-office", TableTag.ALL_BRANCHES, "Rock&Roll")
                .getOutput();
        assertContains("No results found", html);
        assertContains("Rock&amp;Roll", html);
        assertContains("All Branches", html);
        assertNotContains("office-one", html);
        assertNotContains("default-office", html);
        assertWellFormedFragment(html);
    }

    public void testNoResultsMultiple() throws Exception {
        SearchObject searchObject = new SearchObject();
        searchObject.addToSearchNodeMap("dummy-search-term-key", "Rock");
        searchObject.addToSearchNodeMap(CustomerSearchConstants.CUSTOMER_SEARCH_OFFICE_ID, "office-one");
        String html = new TableTag("multiple").noResults("the-office-name", "office-one", "Rock").getOutput();
        assertContains("No results found", html);
        assertContains("Rock", html);
        assertNotContains("All Branches", html);
        assertContains("the-office-name", html);
        assertNotContains("office-one", html);
        assertWellFormedFragment(html);
    }

    public void testNoResultsNotAllBranches() throws Exception {
        SearchObject searchObject = new SearchObject();
        searchObject.addToSearchNodeMap("dummy-search-term-key", "Rock");
        searchObject.addToSearchNodeMap(CustomerSearchConstants.CUSTOMER_SEARCH_OFFICE_ID, "");
        String html = new TableTag("multiple").noResults("the-office-name", "", "Rock").getOutput();
        assertContains("No results found", html);
        assertContains("Rock", html);
        assertNotContains("All Branches", html);
        assertContains("the-office-name", html);
        assertWellFormedFragment(html);
    }

    public void testCreateEndTable() {
        StringBuilder stringBuilder = new StringBuilder();
        new TableTag("single").createEndTable(stringBuilder, true);
        assertContains("<img src=\"pages/framework/images/trans.gif \" width=\"10\" height=\"5\"></td></tr>",
                stringBuilder.toString());
        new TableTag("single").createEndTable(stringBuilder, false);
        assertContains("<img src=\"pages/framework/images/trans.gif \" width=\"5\" height=\"3\"></td></tr>",
                stringBuilder.toString());
    }

    public void testGetSingleFileFailure() throws Exception {
        try {
            new TableTag("single").getSingleFile();
            fail();
        } catch (JspException e) {
            assertTrue(true);
        }
    }

    public void testGetSingleFile() throws Exception {
        Locale locale = Localization.getInstance().getMainLocale();
        TableTag tableTag = new TableTag("single");
        tableTag.setName("viewUsers");
       Assert.assertEquals("org/mifos/framework/util/resources/tabletag/viewUsers.xml", tableTag.getSingleFile());
    }

    public void testParser() throws Exception {

        Files files = TypeParser.getInstance().parser("org/mifos/framework/util/resources/tabletag/type.xml");
        Assert.assertNotNull(files);
        FileName[] file = files.getFileName();
        Assert.assertNotNull(file);
       Assert.assertEquals("1", file[0].getName());
       Assert.assertEquals("org/mifos/framework/util/resources/tabletag/CustomerClient.xml", file[0].getPath());

    }

    public void testGetDisplayText() throws Exception {
       Assert.assertEquals("<span class=\"fontnormalbold\">a</span>,<span class=\"fontnormalbold\">b</span>", Text
                .getDisplayText(new String[] { "a", "b" }, "true"));
       Assert.assertEquals("", Text.getDisplayText(new String[] { "", "" }, "true"));
       Assert.assertEquals("<span class=\"fontnormal\">a</span>,<span class=\"fontnormal\">b</span>", Text.getDisplayText(
                new String[] { "a", "b" }, "false"));

    }

    public void testGetImage() throws Exception {
        Locale locale = Localization.getInstance().getMainLocale();
        CustomerSearch customerSearch = new CustomerSearch();
       Assert.assertEquals(
                "<span class=\"fontnormal\">&nbsp;<img src=pages/framework/images/status_yellow.gif width=\"8\" height=\"9\"></span><span class=\"fontnormal\">&nbsp;PartialApplication</span>",
                Text.getImage(customerSearch, "1", locale));
        customerSearch.setCustomerType(Short.valueOf("4"));
       Assert.assertEquals(
                "<span class=\"fontnormal\">&nbsp;<img src=pages/framework/images/status_yellow.gif width=\"8\" height=\"9\"></span><span class=\"fontnormal\">&nbsp;Pending Approval</span>",
                Text.getImage(customerSearch, "2", locale));
        customerSearch.setCustomerType(Short.valueOf("6"));
       Assert.assertEquals(
                "<span class=\"fontnormal\">&nbsp;<img src=pages/framework/images/status_yellow.gif width=\"8\" height=\"9\"></span><span class=\"fontnormal\">&nbsp;Partial Application</span>",
                Text.getImage(customerSearch, "13", locale));
    }

    public void testTableTagParser() throws Exception {
        Table table = TableTagParser.getInstance().parser("org/mifos/framework/util/resources/tabletag/viewUsers.xml");
        Path path[] = table.getPath();
        for (int i = 0; i < path.length; i++) {
           Assert.assertEquals("PersonAction.do", path[i].getAction());
           Assert.assertEquals("search_success", path[i].getForwardkey());
           Assert.assertEquals("viewUsers", path[i].getKey());

        }
        for (Row row : table.getRow()) {
           Assert.assertEquals("false", row.getTdrequired());

            int i = 0;
            for (Column column : row.getColumn()) {

                if (i++ == 1) {
                   Assert.assertEquals("PersonAction.do", column.getAction());
                   Assert.assertEquals("true", column.getBoldlabel());
                   Assert.assertEquals(null, column.getCheckLinkOptionalRequired());
                   Assert.assertEquals("false", column.getImage());
                   Assert.assertEquals("false", column.getIsLinkOptional());
                   Assert.assertEquals("/", column.getLabel());
                   Assert.assertEquals("string", column.getLabeltype());

                    DisplayName displayName = column.getDisplayname();
                   Assert.assertEquals("true", displayName.getBold());
                    for (Fragment fragment : displayName.getFragment()) {
                       Assert.assertEquals("true", fragment.getBold());
                       Assert.assertEquals("personnelName", fragment.getFragmentName());
                       Assert.assertEquals("method", fragment.getFragmentType());
                       Assert.assertEquals("false", fragment.getItalic());
                    }

                    Parameters parameters = column.getParameters();

                    int j = 0;
                    for (Param param : parameters.getParam()) {
                        if (j++ == 1) {
                           Assert.assertEquals("method", param.getParameterName());
                           Assert.assertEquals("get", param.getParameterValue());
                           Assert.assertEquals("string", param.getParameterValueType());
                        }
                    }

                }
            }

        }

        PageRequirements pageRequirements = table.getPageRequirements();

       Assert.assertEquals("false", pageRequirements.getBlanklinerequired());
       Assert.assertEquals("true", pageRequirements.getBluelineRequired());
       Assert.assertEquals("false", pageRequirements.getBottombluelineRequired());
       Assert.assertEquals("false", pageRequirements.getFlowRequired());
       Assert.assertEquals("false", pageRequirements.getHeadingRequired());
       Assert.assertEquals("true", pageRequirements.getNumbersRequired());
       Assert.assertEquals("true", pageRequirements.getTopbluelineRequired());
       Assert.assertEquals("false", pageRequirements.getValignnumbers());

    }

    public void testHelperCache() throws Exception {

        TableTag tableTag = new TableTag("single");
        tableTag.setName("viewUsers");
        Assert.assertNotNull(tableTag.helperCache("org/mifos/framework/util/resources/tabletag/viewUsers.xml", "viewUsers"));
    }

    public void testPageScroll() {
        Locale locale = Localization.getInstance().getMainLocale();
       Assert.assertEquals("<a href='hRef?method=load&currentFlowKey=1234&current=1'>text</a>", PageScroll.getAnchor("hRef",
                "text", "load", "1234", 1, null));
       Assert.assertEquals(
                "<tr><td width=\"20%\" class=\"fontnormalboldgray\">Previous</td><td width=\"40%\" align=\"center\" class=\"fontnormalbold\">Results 1-10 of 100 </td><td width=\"20%\" class=\"fontnormalbold\"><a href='loaad?method=searchNext&currentFlowKey=1234&current=2'>Next</a></td></tr>",
                PageScroll.getPages(1, 10, 100, "loaad", "1234", locale, null));
       Assert.assertEquals(
                "<tr><td width=\"20%\" class=\"fontnormalbold\"><a href='loaad?method=searchPrev&currentFlowKey=1234&current=4'>Previous</a></td><td width=\"40%\" align=\"center\" class=\"fontnormalbold\">Results 41-50 of 100 </td><td width=\"20%\" class=\"fontnormalbold\"><a href='loaad?method=searchNext&currentFlowKey=1234&current=6'>Next</a></td></tr>",
                PageScroll.getPages(5, 10, 100, "loaad", "1234", locale, null));
       Assert.assertEquals(
                "<tr><td width=\"20%\" class=\"fontnormalboldgray\">Previous</td><td width=\"40%\" align=\"center\" class=\"fontnormalbold\">Results 1-3 of 3 </td><td width=\"20%\" align=\"right\" class=\"fontnormalboldgray\">Next</td></tr>",
                PageScroll.getPages(1, 10, 3, "loaad", "1234", locale, null));
    }

    public void testPageScrollgetAnchor() {
        Locale locale = Localization.getInstance().getMainLocale();
       Assert.assertEquals("<a href='hRef?method=load&currentFlowKey=1234&current=1'>text</a>", PageScroll.getAnchor("hRef",
                "text", "load", "1234", 1, null));
       Assert.assertEquals("<a href='hRef?method=load&currentFlowKey=1234&current=1&" + RequestConstants.PERSPECTIVE + "="
                + LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN + "'>text</a>", PageScroll.getAnchor("hRef", "text",
                "load", "1234", 1, LoanConstants.PERSPECTIVE_VALUE_REDO_LOAN));
    }

    public void testLink() {
       Assert.assertEquals("", Link.createLink(new String[] { "" }, null, null, null, null, null, null));
       Assert.assertEquals(
                "<span class=\"fontnormalbold\"><a href= \"load?X&currentFlowKey=1234&randomNUm=9999\">a</a></span>,<span class=\"fontnormalbold\"><a href= \"load?Y&currentFlowKey=1234&randomNUm=9999\">b</a></span>",
                Link.createLink(new String[] { "a", "b" }, new String[] { "X", "Y" }, "true", "load", "fontnormalbold",
                        "1234", "9999"));
       Assert.assertEquals(
                "<span class=\"headingblue\"><a href= \"load?X\"&currentFlowKey=1234&randomNUm=9999class=\"headingblue\">a</a></span>,<span class=\"headingblue\"><a href= \"load?Y\"&currentFlowKey=1234&randomNUm=9999class=\"headingblue\">b</a></span>",
                Link.createLink(new String[] { "a", "b" }, new String[] { "X", "Y" }, "true", "load", "headingblue",
                        "1234", "9999"));
       Assert.assertEquals(
                "<span><a href= \"load?X&currentFlowKey=1234&randomNUm=9999\">a</a></span>,<span><a href= \"load?Y&currentFlowKey=1234&randomNUm=9999\">b</a></span>",
                Link.createLink(new String[] { "a", "b" }, new String[] { "X", "Y" }, "true", "load", null, "1234",
                        "9999"));
    }

    public void testTableTagTypeParserException() throws Exception {
        try {
            TypeParser.getInstance().parser("org/mifos/framework/components/tabletag/type.xml");
            Assert.fail();
        } catch (TableTagTypeParserException tttpe) {
           Assert.assertEquals("exception.framework.SystemException.TypeParseException", tttpe.getKey());
        }
    }

    public void testTableTagException() throws Exception {
        try {
            Locale locale = Localization.getInstance().getMainLocale();
            Text.getImage(this, "name", locale);
            Assert.fail();
        } catch (TableTagException tte) {
           Assert.assertEquals("exception.framework.TableTagException", tte.getKey());
        }
    }

    public void testTabletag() {

        TableTag tableTag = new TableTag();

        tableTag.setClassName("myclass");
       Assert.assertEquals("myclass", tableTag.getClassName());
        tableTag.setType("mytype");
       Assert.assertEquals("mytype", tableTag.getType());
        tableTag.setName("myname");
       Assert.assertEquals("myname", tableTag.getName());
        tableTag.setBorder("myborder");
       Assert.assertEquals("myborder", tableTag.getBorder());
        tableTag.setCellpadding("mycellpading");
       Assert.assertEquals("mycellpading", tableTag.getCellpadding());
        tableTag.setCellspacing("mycellspacing");
       Assert.assertEquals("mycellspacing", tableTag.getCellspacing());
        tableTag.setWidth("mywidth");
       Assert.assertEquals("mywidth", tableTag.getWidth());
        tableTag.setKey("mykey");
       Assert.assertEquals("mykey", tableTag.getKey());

        tableTag.release();

       Assert.assertEquals(1, tableTag.current);
       Assert.assertEquals(0, tableTag.size);

    }

    public void testSearchObject() throws Exception {
        SearchObject searchObject = new SearchObject();
        searchObject.addSearchTermAndOffice("newSearchTerm", "1");
       Assert.assertEquals("newSearchTerm", searchObject.getFromSearchNodeMap("dummy-search-term-key"));
        searchObject.setSearchNodeMap(null);
        Assert.assertNull(searchObject.getSearchNodeMap());
    }

}
