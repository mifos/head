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

package org.mifos.framework.components.customTableTag;

import static junitx.framework.StringAssert.assertContains;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import junit.framework.Assert;
import junit.framework.TestCase;
import junitx.framework.ObjectAssert;

import org.mifos.customers.office.business.OfficeView;
import org.mifos.core.ClasspathResource;
import org.mifos.framework.exceptions.TableTagParseException;
import org.testng.annotations.Test;

@Test(groups={"unit", "fastTestsSuite"},  dependsOnGroups={"productMixTestSuite"})
public class TableTagParserTest extends TestCase {

    public void testParserFailure() {
        TableTagParser tableTagParser = new TableTagParser();
        try {
            tableTagParser.parser("WEB-INF/struts-config.xml");
            Assert.fail();
        } catch (TableTagParseException e) {
           Assert.assertEquals("exception.framework.TableTagParseException", e.getKey());
            ObjectAssert.assertInstanceOf(FileNotFoundException.class, e.getCause());
        }
    }

    public void testParser() throws Exception {
        Table table = TableTagParser.getInstance().parser(
                ClasspathResource.getURI("org/mifos/framework/util/resources/customTableTag/example.xml").toString());
        Assert.assertNotNull(table);
        HeaderDetails details = table.getHeaderDetails();
       Assert.assertEquals("drawtablerowbold", details.getHeaderStyle());
        StringBuilder builder = new StringBuilder();
        details.getHeaderInfo(builder);
        assertContains("drawtablerowbold", builder.toString());
        Row row = table.getRow();
       Assert.assertEquals("true", row.getBottomLineRequired());
       Assert.assertEquals("100", row.getTotWidth());
        Column[] columns = row.getColumn();
        for (int i = 0; i < columns.length; i++) {
            if (i == 0) {
               Assert.assertEquals("text", columns[i].getColumnType());
               Assert.assertEquals("Name", columns[i].getLabel());
               Assert.assertEquals("Name", columns[i].getValue());
               Assert.assertEquals("method", columns[i].getValueType());
                StringBuilder builder2 = new StringBuilder();
                Locale locale = new Locale("en", "GB");
                OfficeView officeView = new OfficeView(Short.valueOf("1"), "abcd", Short.valueOf("1"), "branch",
                        Integer.valueOf("1"));

                ColumnDetails columnDetails = new ColumnDetails();
                columnDetails.setRowStyle("drawtablerowbold");
                columnDetails.setAlign("Down");
                columns[i].setColumnDetials(columnDetails);
                columns[i].generateTableColumn(builder2, officeView, locale, locale, locale);
               Assert.assertEquals("<td class=\"drawtablerowbold\"   align=\"Down\" > </td>", builder2.toString());

            }
        }

    }

    public void testActionParam() throws Exception {
        ActionParam actionParam = new ActionParam();
        actionParam.setName("officeName");
        actionParam.setValue("officeName");
        StringBuilder stringBuilder = new StringBuilder();
        actionParam.generateParameter(stringBuilder, createOfficeView());
       Assert.assertEquals("officeName=abcd", stringBuilder.toString());

    }

    public void testLinkDetails() throws Exception {
        LinkDetails linkDetails = new LinkDetails();
        linkDetails.setAction("Load");
        ActionParam actionParam = new ActionParam();
        actionParam.setName("officeName");
        actionParam.setValue("officeName");
        linkDetails.setActionParam(new ActionParam[] { actionParam });
        StringBuilder stringBuilder = new StringBuilder();
        linkDetails.generateLink(stringBuilder, createOfficeView());
        assertContains("Load", stringBuilder.toString());

    }

    public void testRow() throws Exception {

        Row row = new Row();
        Column column = new Column();
        column.setValueType("method");
        column.setColumnType("text");
        column.setLabel("Name");
        column.setValue("Name");
        ColumnDetails columnDetails = new ColumnDetails();
        columnDetails.setRowStyle("drawtablerowbold");
        columnDetails.setAlign("Down");
        column.setColumnDetials(columnDetails);
        row.setColumn(new Column[] { column });
        StringBuilder stringBuilder = new StringBuilder();
        List list = new ArrayList();
        list.add(createOfficeView());
        Locale locale = new Locale("en", "GB");
        row.generateTableRows(stringBuilder, list, locale, locale, locale);
       Assert.assertEquals("<tr><td class=\"drawtablerowbold\"   align=\"Down\" > </td></tr>", stringBuilder.toString());

    }

    private OfficeView createOfficeView() {
        return new OfficeView(Short.valueOf("1"), "abcd", Short.valueOf("1"), "branch", Integer.valueOf("1"));
    }
}
