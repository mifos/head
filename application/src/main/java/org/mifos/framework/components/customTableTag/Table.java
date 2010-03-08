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

import java.util.List;
import java.util.Locale;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

import org.mifos.framework.exceptions.TableTagParseException;

public class Table {

    private Row row = null;

    private HeaderDetails headerDetails = null;

    public void setHeaderDetails(HeaderDetails headerDetails) {
        this.headerDetails = headerDetails;
    }

    public HeaderDetails getHeaderDetails() {
        return headerDetails;
    }

    public void setRow(Row row) {
        this.row = row;
    }

    public Row getRow() {
        return row;
    }

    public void getTable(StringBuilder tableInfo, List obj, Locale locale, Locale prefferedLocale, Locale mfiLocale,
            PageContext pageContext, String bundle) throws TableTagParseException, JspException {
        tableInfo.append("<table width=\"" + getRow().getTotWidth()
                + "%\" border=\"0\" cellpadding=\"3\" cellspacing=\"0\" >");

        // Start :: Generating Header
        tableInfo.append("<tr ");
        getHeaderDetails().getHeaderInfo(tableInfo);
        tableInfo.append(" >");
        getRow().getRowHeader(tableInfo, pageContext, bundle);
        tableInfo.append("</tr>");
        // End :: Generationg Header

        // Start :: Generating Rows
        getRow().generateTableRows(tableInfo, obj, locale, prefferedLocale, mfiLocale);
        // End :: Generating Rows

        // Genrate Last Line :: This line will have the same style as listed for
        // columns
        if (getRow().getBottomLineRequired().equalsIgnoreCase("true")) {
            tableInfo.append("<tr>");
            Column[] column = getRow().getColumn();
            for (Column element : column) {
                tableInfo.append("<td class=\"" + element.getColumnDetails().getRowStyle() + "\">&nbsp;</td>");
            }
            tableInfo.append("</tr>");
        }

        tableInfo.append("</table>");
    }

}
